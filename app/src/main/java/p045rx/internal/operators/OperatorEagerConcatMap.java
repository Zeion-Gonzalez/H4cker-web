package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;
import p045rx.functions.Func1;
import p045rx.internal.util.atomic.SpscAtomicArrayQueue;
import p045rx.internal.util.unsafe.SpscArrayQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
public final class OperatorEagerConcatMap<T, R> implements Observable.Operator<R, T> {
    final int bufferSize;
    final Func1<? super T, ? extends Observable<? extends R>> mapper;
    private final int maxConcurrent;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorEagerConcatMap(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent) {
        this.mapper = mapper;
        this.bufferSize = bufferSize;
        this.maxConcurrent = maxConcurrent;
    }

    public Subscriber<? super T> call(Subscriber<? super R> t) {
        EagerOuterSubscriber<T, R> outer = new EagerOuterSubscriber<>(this.mapper, this.bufferSize, this.maxConcurrent, t);
        outer.init();
        return outer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EagerOuterProducer extends AtomicLong implements Producer {
        private static final long serialVersionUID = -657299606803478389L;
        final EagerOuterSubscriber<?, ?> parent;

        public EagerOuterProducer(EagerOuterSubscriber<?, ?> parent) {
            this.parent = parent;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n < 0) {
                throw new IllegalStateException("n >= 0 required but it was " + n);
            }
            if (n > 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                this.parent.drain();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EagerOuterSubscriber<T, R> extends Subscriber<T> {
        final Subscriber<? super R> actual;
        final int bufferSize;
        volatile boolean cancelled;
        volatile boolean done;
        Throwable error;
        final Func1<? super T, ? extends Observable<? extends R>> mapper;
        private EagerOuterProducer sharedProducer;
        final Queue<EagerInnerSubscriber<R>> subscribers = new LinkedList();
        final AtomicInteger wip = new AtomicInteger();

        public EagerOuterSubscriber(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent, Subscriber<? super R> actual) {
            this.mapper = mapper;
            this.bufferSize = bufferSize;
            this.actual = actual;
            request(maxConcurrent == Integer.MAX_VALUE ? Long.MAX_VALUE : maxConcurrent);
        }

        void init() {
            this.sharedProducer = new EagerOuterProducer(this);
            add(Subscriptions.create(new Action0() { // from class: rx.internal.operators.OperatorEagerConcatMap.EagerOuterSubscriber.1
                @Override // p045rx.functions.Action0
                public void call() {
                    EagerOuterSubscriber.this.cancelled = true;
                    if (EagerOuterSubscriber.this.wip.getAndIncrement() == 0) {
                        EagerOuterSubscriber.this.cleanup();
                    }
                }
            }));
            this.actual.add(this);
            this.actual.setProducer(this.sharedProducer);
        }

        void cleanup() {
            List<Subscription> list;
            synchronized (this.subscribers) {
                list = new ArrayList<>(this.subscribers);
                this.subscribers.clear();
            }
            for (Subscription s : list) {
                s.unsubscribe();
            }
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            try {
                Observable<? extends R> observable = this.mapper.call(t);
                if (!this.cancelled) {
                    EagerInnerSubscriber<R> inner = new EagerInnerSubscriber<>(this, this.bufferSize);
                    synchronized (this.subscribers) {
                        if (!this.cancelled) {
                            this.subscribers.add(inner);
                            if (!this.cancelled) {
                                observable.unsafeSubscribe(inner);
                                drain();
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, this.actual, t);
            }
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

        /* JADX WARN: Code restructure failed: missing block: B:42:0x00a8, code lost:
        
            if (r6 == 0) goto L48;
         */
        /* JADX WARN: Code restructure failed: missing block: B:44:0x00b1, code lost:
        
            if (r18 == Long.MAX_VALUE) goto L46;
         */
        /* JADX WARN: Code restructure failed: missing block: B:45:0x00b3, code lost:
        
            p045rx.internal.operators.BackpressureUtils.produced(r0, r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:46:0x00b8, code lost:
        
            if (r10 != false) goto L48;
         */
        /* JADX WARN: Code restructure failed: missing block: B:47:0x00ba, code lost:
        
            r13.requestMore(r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:48:0x00bd, code lost:
        
            if (r10 != false) goto L77;
         */
        /* JADX WARN: Code restructure failed: missing block: B:77:0x001c, code lost:
        
            continue;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void drain() {
            /*
                Method dump skipped, instructions count: 245
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorEagerConcatMap.EagerOuterSubscriber.drain():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EagerInnerSubscriber<T> extends Subscriber<T> {
        volatile boolean done;
        Throwable error;

        /* renamed from: nl */
        final NotificationLite<T> f1600nl;
        final EagerOuterSubscriber<?, T> parent;
        final Queue<Object> queue;

        public EagerInnerSubscriber(EagerOuterSubscriber<?, T> parent, int bufferSize) {
            Queue<Object> q;
            this.parent = parent;
            if (UnsafeAccess.isUnsafeAvailable()) {
                q = new SpscArrayQueue<>(bufferSize);
            } else {
                q = new SpscAtomicArrayQueue<>(bufferSize);
            }
            this.queue = q;
            this.f1600nl = NotificationLite.instance();
            request(bufferSize);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            this.queue.offer(this.f1600nl.next(t));
            this.parent.drain();
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            this.parent.drain();
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.done = true;
            this.parent.drain();
        }

        void requestMore(long n) {
            request(n);
        }
    }
}
