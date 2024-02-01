package p045rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func0;
import p045rx.functions.Func2;
import p045rx.internal.util.atomic.SpscLinkedAtomicQueue;
import p045rx.internal.util.unsafe.SpscLinkedQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;

/* loaded from: classes.dex */
public final class OperatorScan<R, T> implements Observable.Operator<R, T> {
    private static final Object NO_INITIAL_VALUE = new Object();
    final Func2<R, ? super T, R> accumulator;
    private final Func0<R> initialValueFactory;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorScan(final R initialValue, Func2<R, ? super T, R> accumulator) {
        this((Func0) new Func0<R>() { // from class: rx.internal.operators.OperatorScan.1
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            public R call() {
                return (R) initialValue;
            }
        }, (Func2) accumulator);
    }

    public OperatorScan(Func0<R> initialValueFactory, Func2<R, ? super T, R> accumulator) {
        this.initialValueFactory = initialValueFactory;
        this.accumulator = accumulator;
    }

    public OperatorScan(Func2<R, ? super T, R> accumulator) {
        this(NO_INITIAL_VALUE, accumulator);
    }

    public Subscriber<? super T> call(final Subscriber<? super R> child) {
        final R initialValue = this.initialValueFactory.call();
        if (initialValue == NO_INITIAL_VALUE) {
            return (Subscriber<T>) new Subscriber<T>(child) { // from class: rx.internal.operators.OperatorScan.2
                boolean once;
                R value;

                @Override // p045rx.Observer
                public void onNext(T t) {
                    R v;
                    if (!this.once) {
                        this.once = true;
                        v = (R) t;
                    } else {
                        R v2 = this.value;
                        try {
                            v = OperatorScan.this.accumulator.call(v2, t);
                        } catch (Throwable e) {
                            Exceptions.throwOrReport(e, child, t);
                            return;
                        }
                    }
                    this.value = v;
                    child.onNext(v);
                }

                @Override // p045rx.Observer
                public void onError(Throwable e) {
                    child.onError(e);
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    child.onCompleted();
                }
            };
        }
        final InitialProducer<R> ip = new InitialProducer<>(initialValue, child);
        Subscriber subscriber = (Subscriber<T>) new Subscriber<T>() { // from class: rx.internal.operators.OperatorScan.3
            private R value;

            {
                this.value = (R) initialValue;
            }

            @Override // p045rx.Observer
            public void onNext(T currentValue) {
                try {
                    R v = OperatorScan.this.accumulator.call(this.value, currentValue);
                    this.value = v;
                    ip.onNext(v);
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this, currentValue);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ip.onError(e);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                ip.onCompleted();
            }

            @Override // p045rx.Subscriber
            public void setProducer(Producer producer) {
                ip.setProducer(producer);
            }
        };
        child.add(subscriber);
        child.setProducer(ip);
        return subscriber;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class InitialProducer<R> implements Producer, Observer<R> {
        final Subscriber<? super R> child;
        volatile boolean done;
        boolean emitting;
        Throwable error;
        boolean missed;
        long missedRequested;
        volatile Producer producer;
        final Queue<Object> queue;
        final AtomicLong requested;

        public InitialProducer(R initialValue, Subscriber<? super R> child) {
            Queue<Object> q;
            this.child = child;
            if (UnsafeAccess.isUnsafeAvailable()) {
                q = new SpscLinkedQueue<>();
            } else {
                q = new SpscLinkedAtomicQueue<>();
            }
            this.queue = q;
            q.offer(NotificationLite.instance().next(initialValue));
            this.requested = new AtomicLong();
        }

        @Override // p045rx.Observer
        public void onNext(R t) {
            this.queue.offer(NotificationLite.instance().next(t));
            emit();
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super R> child) {
            if (child.isUnsubscribed()) {
                return true;
            }
            if (d) {
                Throwable err = this.error;
                if (err != null) {
                    child.onError(err);
                    return true;
                }
                if (empty) {
                    child.onCompleted();
                    return true;
                }
            }
            return false;
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            emit();
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.done = true;
            emit();
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= required but it was " + n);
            }
            if (n != 0) {
                BackpressureUtils.getAndAddRequest(this.requested, n);
                Producer p = this.producer;
                if (p == null) {
                    synchronized (this.requested) {
                        p = this.producer;
                        if (p == null) {
                            long mr = this.missedRequested;
                            this.missedRequested = BackpressureUtils.addCap(mr, n);
                        }
                    }
                }
                if (p != null) {
                    p.request(n);
                }
                emit();
            }
        }

        public void setProducer(Producer p) {
            long mr;
            if (p == null) {
                throw new NullPointerException();
            }
            synchronized (this.requested) {
                if (this.producer != null) {
                    throw new IllegalStateException("Can't set more than one Producer!");
                }
                mr = this.missedRequested;
                if (mr != Long.MAX_VALUE) {
                    mr--;
                }
                this.missedRequested = 0L;
                this.producer = p;
            }
            if (mr > 0) {
                p.request(mr);
            }
            emit();
        }

        void emit() {
            synchronized (this) {
                if (this.emitting) {
                    this.missed = true;
                } else {
                    this.emitting = true;
                    emitLoop();
                }
            }
        }

        /* JADX WARN: Incorrect condition in loop: B:4:0x0022 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void emitLoop() {
            /*
                r18 = this;
                r0 = r18
                rx.Subscriber<? super R> r2 = r0.child
                r0 = r18
                java.util.Queue<java.lang.Object> r10 = r0.queue
                rx.internal.operators.NotificationLite r8 = p045rx.internal.operators.NotificationLite.instance()
                r0 = r18
                java.util.concurrent.atomic.AtomicLong r11 = r0.requested
                long r12 = r11.get()
            L14:
                r0 = r18
                boolean r3 = r0.done
                boolean r6 = r10.isEmpty()
                r0 = r18
                boolean r15 = r0.checkTerminated(r3, r6, r2)
                if (r15 == 0) goto L25
            L24:
                return
            L25:
                r4 = 0
            L27:
                int r15 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
                if (r15 == 0) goto L40
                r0 = r18
                boolean r3 = r0.done
                java.lang.Object r9 = r10.poll()
                if (r9 != 0) goto L64
                r6 = 1
            L36:
                r0 = r18
                boolean r15 = r0.checkTerminated(r3, r6, r2)
                if (r15 != 0) goto L24
                if (r6 == 0) goto L66
            L40:
                r16 = 0
                int r15 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
                if (r15 == 0) goto L53
                r16 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                int r15 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
                if (r15 == 0) goto L53
                long r12 = p045rx.internal.operators.BackpressureUtils.produced(r11, r4)
            L53:
                monitor-enter(r18)
                r0 = r18
                boolean r15 = r0.missed     // Catch: java.lang.Throwable -> L61
                if (r15 != 0) goto L77
                r15 = 0
                r0 = r18
                r0.emitting = r15     // Catch: java.lang.Throwable -> L61
                monitor-exit(r18)     // Catch: java.lang.Throwable -> L61
                goto L24
            L61:
                r15 = move-exception
                monitor-exit(r18)     // Catch: java.lang.Throwable -> L61
                throw r15
            L64:
                r6 = 0
                goto L36
            L66:
                java.lang.Object r14 = r8.getValue(r9)
                r2.onNext(r14)     // Catch: java.lang.Throwable -> L72
                r16 = 1
                long r4 = r4 + r16
                goto L27
            L72:
                r7 = move-exception
                p045rx.exceptions.Exceptions.throwOrReport(r7, r2, r14)
                goto L24
            L77:
                r15 = 0
                r0 = r18
                r0.missed = r15     // Catch: java.lang.Throwable -> L61
                monitor-exit(r18)     // Catch: java.lang.Throwable -> L61
                goto L14
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorScan.InitialProducer.emitLoop():void");
        }
    }
}
