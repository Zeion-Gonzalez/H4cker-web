package p045rx.internal.operators;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.MissingBackpressureException;
import p045rx.functions.Func1;
import p045rx.internal.operators.OnSubscribeFromIterable;
import p045rx.internal.util.ExceptionsUtils;
import p045rx.internal.util.RxRingBuffer;
import p045rx.internal.util.ScalarSynchronousObservable;
import p045rx.internal.util.atomic.SpscAtomicArrayQueue;
import p045rx.internal.util.atomic.SpscLinkedArrayQueue;
import p045rx.internal.util.unsafe.SpscArrayQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OnSubscribeFlattenIterable<T, R> implements Observable.OnSubscribe<R> {
    final Func1<? super T, ? extends Iterable<? extends R>> mapper;
    final int prefetch;
    final Observable<? extends T> source;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    protected OnSubscribeFlattenIterable(Observable<? extends T> source, Func1<? super T, ? extends Iterable<? extends R>> mapper, int prefetch) {
        this.source = source;
        this.mapper = mapper;
        this.prefetch = prefetch;
    }

    public void call(Subscriber<? super R> t) {
        final FlattenIterableSubscriber<T, R> parent = new FlattenIterableSubscriber<>(t, this.mapper, this.prefetch);
        t.add(parent);
        t.setProducer(new Producer() { // from class: rx.internal.operators.OnSubscribeFlattenIterable.1
            @Override // p045rx.Producer
            public void request(long n) {
                parent.requestMore(n);
            }
        });
        this.source.unsafeSubscribe(parent);
    }

    public static <T, R> Observable<R> createFrom(Observable<? extends T> source, Func1<? super T, ? extends Iterable<? extends R>> mapper, int prefetch) {
        return source instanceof ScalarSynchronousObservable ? Observable.create(new OnSubscribeScalarFlattenIterable(((ScalarSynchronousObservable) source).get(), mapper)) : Observable.create(new OnSubscribeFlattenIterable(source, mapper, prefetch));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FlattenIterableSubscriber<T, R> extends Subscriber<T> {
        Iterator<? extends R> active;
        final Subscriber<? super R> actual;
        volatile boolean done;
        final long limit;
        final Func1<? super T, ? extends Iterable<? extends R>> mapper;
        long produced;
        final Queue<Object> queue;
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final AtomicInteger wip = new AtomicInteger();
        final AtomicLong requested = new AtomicLong();

        /* renamed from: nl */
        final NotificationLite<T> f1590nl = NotificationLite.instance();

        public FlattenIterableSubscriber(Subscriber<? super R> actual, Func1<? super T, ? extends Iterable<? extends R>> mapper, int prefetch) {
            this.actual = actual;
            this.mapper = mapper;
            if (prefetch == Integer.MAX_VALUE) {
                this.limit = Long.MAX_VALUE;
                this.queue = new SpscLinkedArrayQueue(RxRingBuffer.SIZE);
            } else {
                this.limit = prefetch - (prefetch >> 2);
                if (UnsafeAccess.isUnsafeAvailable()) {
                    this.queue = new SpscArrayQueue(prefetch);
                } else {
                    this.queue = new SpscAtomicArrayQueue(prefetch);
                }
            }
            request(prefetch);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            if (!this.queue.offer(this.f1590nl.next(t))) {
                unsubscribe();
                onError(new MissingBackpressureException());
            } else {
                drain();
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (ExceptionsUtils.addThrowable(this.error, e)) {
                this.done = true;
                drain();
            } else {
                RxJavaHooks.onError(e);
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.done = true;
            drain();
        }

        void requestMore(long n) {
            if (n > 0) {
                BackpressureUtils.getAndAddRequest(this.requested, n);
                drain();
            } else if (n < 0) {
                throw new IllegalStateException("n >= 0 required but it was " + n);
            }
        }

        void drain() {
            boolean b;
            if (this.wip.getAndIncrement() == 0) {
                Subscriber<? super R> actual = this.actual;
                Queue<Object> queue = this.queue;
                int missed = 1;
                while (true) {
                    Iterator<? extends R> it = this.active;
                    if (it == null) {
                        boolean d = this.done;
                        Object v = queue.poll();
                        boolean empty = v == null;
                        if (!checkTerminated(d, empty, actual, queue)) {
                            if (!empty) {
                                long p = this.produced + 1;
                                if (p == this.limit) {
                                    this.produced = 0L;
                                    request(p);
                                } else {
                                    this.produced = p;
                                }
                                try {
                                    Iterable<? extends R> iter = this.mapper.call((T) this.f1590nl.getValue(v));
                                    it = iter.iterator();
                                    b = it.hasNext();
                                } catch (Throwable ex) {
                                    Exceptions.throwIfFatal(ex);
                                    onError(ex);
                                }
                                if (b) {
                                    this.active = it;
                                } else {
                                    continue;
                                }
                            }
                        } else {
                            return;
                        }
                    }
                    if (it != null) {
                        long r = this.requested.get();
                        long e = 0;
                        while (true) {
                            if (e == r) {
                                break;
                            }
                            if (!checkTerminated(this.done, false, actual, queue)) {
                                try {
                                    actual.onNext((R) it.next());
                                    if (!checkTerminated(this.done, false, actual, queue)) {
                                        e++;
                                        try {
                                            boolean b2 = it.hasNext();
                                            if (!b2) {
                                                it = null;
                                                this.active = null;
                                                break;
                                            }
                                        } catch (Throwable ex2) {
                                            Exceptions.throwIfFatal(ex2);
                                            it = null;
                                            this.active = null;
                                            onError(ex2);
                                        }
                                    } else {
                                        return;
                                    }
                                } catch (Throwable ex3) {
                                    Exceptions.throwIfFatal(ex3);
                                    it = null;
                                    this.active = null;
                                    onError(ex3);
                                }
                            } else {
                                return;
                            }
                        }
                        if (e == r) {
                            if (checkTerminated(this.done, queue.isEmpty() && it == null, actual, queue)) {
                                return;
                            }
                        }
                        if (e != 0) {
                            BackpressureUtils.produced(this.requested, e);
                        }
                        if (it == null) {
                            continue;
                        }
                    }
                    missed = this.wip.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<?> a, Queue<?> q) {
            if (a.isUnsubscribed()) {
                q.clear();
                this.active = null;
                return true;
            }
            if (d) {
                Throwable ex = this.error.get();
                if (ex != null) {
                    Throwable ex2 = ExceptionsUtils.terminate(this.error);
                    unsubscribe();
                    q.clear();
                    this.active = null;
                    a.onError(ex2);
                    return true;
                }
                if (empty) {
                    a.onCompleted();
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OnSubscribeScalarFlattenIterable<T, R> implements Observable.OnSubscribe<R> {
        final Func1<? super T, ? extends Iterable<? extends R>> mapper;
        final T value;

        @Override // p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            call((Subscriber) ((Subscriber) x0));
        }

        public OnSubscribeScalarFlattenIterable(T value, Func1<? super T, ? extends Iterable<? extends R>> mapper) {
            this.value = value;
            this.mapper = mapper;
        }

        public void call(Subscriber<? super R> t) {
            try {
                Iterable<? extends R> it = this.mapper.call((T) this.value);
                Iterator<? extends R> itor = it.iterator();
                boolean b = itor.hasNext();
                if (!b) {
                    t.onCompleted();
                } else {
                    t.setProducer(new OnSubscribeFromIterable.IterableProducer(t, itor));
                }
            } catch (Throwable ex) {
                Exceptions.throwOrReport(ex, t, this.value);
            }
        }
    }
}
