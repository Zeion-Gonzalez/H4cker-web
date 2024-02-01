package p045rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.MissingBackpressureException;
import p045rx.internal.util.atomic.SpscAtomicArrayQueue;
import p045rx.internal.util.unsafe.SpscArrayQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;

/* loaded from: classes.dex */
public final class OnSubscribePublishMulticast<T> extends AtomicInteger implements Observable.OnSubscribe<T>, Observer<T>, Subscription {
    static final PublishProducer<?>[] EMPTY = new PublishProducer[0];
    static final PublishProducer<?>[] TERMINATED = new PublishProducer[0];
    private static final long serialVersionUID = -3741892510772238743L;
    final boolean delayError;
    volatile boolean done;
    Throwable error;
    final ParentSubscriber<T> parent;
    final int prefetch;
    volatile Producer producer;
    final Queue<T> queue;
    volatile PublishProducer<T>[] subscribers;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public OnSubscribePublishMulticast(int prefetch, boolean delayError) {
        if (prefetch <= 0) {
            throw new IllegalArgumentException("prefetch > 0 required but it was " + prefetch);
        }
        this.prefetch = prefetch;
        this.delayError = delayError;
        if (UnsafeAccess.isUnsafeAvailable()) {
            this.queue = new SpscArrayQueue(prefetch);
        } else {
            this.queue = new SpscAtomicArrayQueue(prefetch);
        }
        this.subscribers = EMPTY;
        this.parent = new ParentSubscriber<>(this);
    }

    public void call(Subscriber<? super T> t) {
        PublishProducer<T> pp = new PublishProducer<>(t, this);
        t.add(pp);
        t.setProducer(pp);
        if (add(pp)) {
            if (pp.isUnsubscribed()) {
                remove(pp);
                return;
            } else {
                drain();
                return;
            }
        }
        Throwable e = this.error;
        if (e != null) {
            t.onError(e);
        } else {
            t.onCompleted();
        }
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        if (!this.queue.offer(t)) {
            this.parent.unsubscribe();
            this.error = new MissingBackpressureException("Queue full?!");
            this.done = true;
        }
        drain();
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.error = e;
        this.done = true;
        drain();
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.done = true;
        drain();
    }

    void setProducer(Producer p) {
        this.producer = p;
        p.request(this.prefetch);
    }

    void drain() {
        if (getAndIncrement() == 0) {
            Queue<T> q = this.queue;
            int missed = 0;
            do {
                long r = Long.MAX_VALUE;
                PublishProducer<T>[] a = this.subscribers;
                int n = a.length;
                for (PublishProducer<T> inner : a) {
                    r = Math.min(r, inner.get());
                }
                if (n != 0) {
                    long e = 0;
                    while (e != r) {
                        boolean d = this.done;
                        T v = q.poll();
                        boolean empty = v == null;
                        if (!checkTerminated(d, empty)) {
                            if (empty) {
                                break;
                            }
                            for (PublishProducer<T> inner2 : a) {
                                inner2.actual.onNext(v);
                            }
                            e++;
                        } else {
                            return;
                        }
                    }
                    if (e == r && checkTerminated(this.done, q.isEmpty())) {
                        return;
                    }
                    if (e != 0) {
                        Producer p = this.producer;
                        if (p != null) {
                            p.request(e);
                        }
                        for (PublishProducer<T> inner3 : a) {
                            BackpressureUtils.produced(inner3, e);
                        }
                    }
                }
                missed = addAndGet(-missed);
            } while (missed != 0);
        }
    }

    boolean checkTerminated(boolean d, boolean empty) {
        if (d) {
            if (this.delayError) {
                if (empty) {
                    PublishProducer<T>[] a = terminate();
                    Throwable ex = this.error;
                    if (ex != null) {
                        for (PublishProducer<T> inner : a) {
                            inner.actual.onError(ex);
                        }
                        return true;
                    }
                    for (PublishProducer<T> inner2 : a) {
                        inner2.actual.onCompleted();
                    }
                    return true;
                }
            } else {
                Throwable ex2 = this.error;
                if (ex2 != null) {
                    this.queue.clear();
                    PublishProducer<T>[] a2 = terminate();
                    for (PublishProducer<T> inner3 : a2) {
                        inner3.actual.onError(ex2);
                    }
                    return true;
                }
                if (empty) {
                    PublishProducer<T>[] a3 = terminate();
                    for (PublishProducer<T> inner4 : a3) {
                        inner4.actual.onCompleted();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    PublishProducer<T>[] terminate() {
        PublishProducer<T>[] a = this.subscribers;
        if (a != TERMINATED) {
            synchronized (this) {
                a = this.subscribers;
                if (a != TERMINATED) {
                    this.subscribers = TERMINATED;
                }
            }
        }
        return a;
    }

    boolean add(PublishProducer<T> inner) {
        boolean z = false;
        if (this.subscribers != TERMINATED) {
            synchronized (this) {
                PublishProducer<T>[] a = this.subscribers;
                if (a != TERMINATED) {
                    int n = a.length;
                    PublishProducer<T>[] b = new PublishProducer[n + 1];
                    System.arraycopy(a, 0, b, 0, n);
                    b[n] = inner;
                    this.subscribers = b;
                    z = true;
                }
            }
        }
        return z;
    }

    /* JADX WARN: Multi-variable type inference failed */
    void remove(PublishProducer<T> inner) {
        PublishProducer<?>[] b;
        PublishProducer<T>[] a = this.subscribers;
        if (a != TERMINATED && a != EMPTY) {
            synchronized (this) {
                PublishProducer<T>[] a2 = this.subscribers;
                if (a2 != TERMINATED && a2 != EMPTY) {
                    int j = -1;
                    int n = a2.length;
                    int i = 0;
                    while (true) {
                        if (i >= n) {
                            break;
                        }
                        if (a2[i] != inner) {
                            i++;
                        } else {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            b = new PublishProducer[n - 1];
                            System.arraycopy(a2, 0, b, 0, j);
                            System.arraycopy(a2, j + 1, b, j, (n - j) - 1);
                        }
                        this.subscribers = b;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ParentSubscriber<T> extends Subscriber<T> {
        final OnSubscribePublishMulticast<T> state;

        public ParentSubscriber(OnSubscribePublishMulticast<T> state) {
            this.state = state;
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            this.state.onNext(t);
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.state.onError(e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.state.onCompleted();
        }

        @Override // p045rx.Subscriber
        public void setProducer(Producer p) {
            this.state.setProducer(p);
        }
    }

    public Subscriber<T> subscriber() {
        return this.parent;
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
        this.parent.unsubscribe();
    }

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return this.parent.isUnsubscribed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class PublishProducer<T> extends AtomicLong implements Producer, Subscription {
        private static final long serialVersionUID = 960704844171597367L;
        final Subscriber<? super T> actual;
        final AtomicBoolean once = new AtomicBoolean();
        final OnSubscribePublishMulticast<T> parent;

        public PublishProducer(Subscriber<? super T> actual, OnSubscribePublishMulticast<T> parent) {
            this.actual = actual;
            this.parent = parent;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required but it was " + n);
            }
            if (n != 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                this.parent.drain();
            }
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.once.get();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            if (this.once.compareAndSet(false, true)) {
                this.parent.remove(this);
            }
        }
    }
}
