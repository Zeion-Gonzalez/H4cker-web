package p045rx.subjects;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;
import p045rx.internal.operators.BackpressureUtils;
import p045rx.internal.operators.NotificationLite;
import p045rx.internal.util.atomic.SpscLinkedAtomicQueue;
import p045rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue;
import p045rx.internal.util.unsafe.SpscLinkedQueue;
import p045rx.internal.util.unsafe.SpscUnboundedArrayQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;

@Experimental
/* loaded from: classes.dex */
public final class UnicastSubject<T> extends Subject<T, T> {
    final State<T> state;

    public static <T> UnicastSubject<T> create() {
        return create(16);
    }

    public static <T> UnicastSubject<T> create(int capacityHint) {
        State<T> state = new State<>(capacityHint, null);
        return new UnicastSubject<>(state);
    }

    public static <T> UnicastSubject<T> create(int capacityHint, Action0 onTerminated) {
        State<T> state = new State<>(capacityHint, onTerminated);
        return new UnicastSubject<>(state);
    }

    private UnicastSubject(State<T> state) {
        super(state);
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

    @Override // p045rx.subjects.Subject
    public boolean hasObservers() {
        return this.state.subscriber.get() != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class State<T> extends AtomicLong implements Producer, Observer<T>, Observable.OnSubscribe<T>, Subscription {
        private static final long serialVersionUID = -9044104859202255786L;
        volatile boolean caughtUp;
        volatile boolean done;
        boolean emitting;
        Throwable error;
        boolean missed;
        final Queue<Object> queue;
        final AtomicReference<Action0> terminateOnce;

        /* renamed from: nl */
        final NotificationLite<T> f1644nl = NotificationLite.instance();
        final AtomicReference<Subscriber<? super T>> subscriber = new AtomicReference<>();

        @Override // p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            call((Subscriber) ((Subscriber) x0));
        }

        public State(int capacityHint, Action0 onTerminated) {
            Queue<Object> q;
            this.terminateOnce = onTerminated != null ? new AtomicReference<>(onTerminated) : null;
            if (capacityHint > 1) {
                q = UnsafeAccess.isUnsafeAvailable() ? new SpscUnboundedArrayQueue<>(capacityHint) : new SpscUnboundedAtomicArrayQueue<>(capacityHint);
            } else {
                q = UnsafeAccess.isUnsafeAvailable() ? new SpscLinkedQueue<>() : new SpscLinkedAtomicQueue<>();
            }
            this.queue = q;
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            if (!this.done) {
                if (!this.caughtUp) {
                    boolean stillReplay = false;
                    synchronized (this) {
                        if (!this.caughtUp) {
                            this.queue.offer(this.f1644nl.next(t));
                            stillReplay = true;
                        }
                    }
                    if (stillReplay) {
                        replay();
                        return;
                    }
                }
                Subscriber<? super T> s = this.subscriber.get();
                try {
                    s.onNext(t);
                } catch (Throwable ex) {
                    Exceptions.throwOrReport(ex, s, t);
                }
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            boolean stillReplay;
            if (!this.done) {
                doTerminate();
                this.error = e;
                this.done = true;
                if (!this.caughtUp) {
                    synchronized (this) {
                        stillReplay = !this.caughtUp;
                    }
                    if (stillReplay) {
                        replay();
                        return;
                    }
                }
                this.subscriber.get().onError(e);
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            boolean stillReplay;
            if (!this.done) {
                doTerminate();
                this.done = true;
                if (!this.caughtUp) {
                    synchronized (this) {
                        stillReplay = !this.caughtUp;
                    }
                    if (stillReplay) {
                        replay();
                        return;
                    }
                }
                this.subscriber.get().onCompleted();
            }
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required");
            }
            if (n > 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                replay();
            } else if (this.done) {
                replay();
            }
        }

        public void call(Subscriber<? super T> subscriber) {
            if (this.subscriber.compareAndSet(null, subscriber)) {
                subscriber.add(this);
                subscriber.setProducer(this);
            } else {
                subscriber.onError(new IllegalStateException("Only a single subscriber is allowed"));
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:34:0x005f, code lost:
        
            if (r9 == false) goto L38;
         */
        /* JADX WARN: Code restructure failed: missing block: B:36:0x0065, code lost:
        
            if (r5.isEmpty() == false) goto L38;
         */
        /* JADX WARN: Code restructure failed: missing block: B:37:0x0067, code lost:
        
            r14.caughtUp = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:38:0x006a, code lost:
        
            r14.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:?, code lost:
        
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void replay() {
            /*
                r14 = this;
                monitor-enter(r14)
                boolean r12 = r14.emitting     // Catch: java.lang.Throwable -> L72
                if (r12 == 0) goto La
                r12 = 1
                r14.missed = r12     // Catch: java.lang.Throwable -> L72
                monitor-exit(r14)     // Catch: java.lang.Throwable -> L72
            L9:
                return
            La:
                r12 = 1
                r14.emitting = r12     // Catch: java.lang.Throwable -> L72
                monitor-exit(r14)     // Catch: java.lang.Throwable -> L72
                java.util.Queue<java.lang.Object> r5 = r14.queue
            L10:
                java.util.concurrent.atomic.AtomicReference<rx.Subscriber<? super T>> r12 = r14.subscriber
                java.lang.Object r8 = r12.get()
                rx.Subscriber r8 = (p045rx.Subscriber) r8
                r9 = 0
                if (r8 == 0) goto L5a
                boolean r0 = r14.done
                boolean r1 = r5.isEmpty()
                boolean r12 = r14.checkTerminated(r0, r1, r8)
                if (r12 != 0) goto L9
                long r6 = r14.get()
                r12 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                int r12 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r12 != 0) goto L75
                r9 = 1
            L35:
                r2 = 0
            L37:
                r12 = 0
                int r12 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r12 == 0) goto L4e
                boolean r0 = r14.done
                java.lang.Object r10 = r5.poll()
                if (r10 != 0) goto L77
                r1 = 1
            L46:
                boolean r12 = r14.checkTerminated(r0, r1, r8)
                if (r12 != 0) goto L9
                if (r1 == 0) goto L79
            L4e:
                if (r9 != 0) goto L5a
                r12 = 0
                int r12 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                if (r12 == 0) goto L5a
                long r12 = -r2
                r14.addAndGet(r12)
            L5a:
                monitor-enter(r14)
                boolean r12 = r14.missed     // Catch: java.lang.Throwable -> L6f
                if (r12 != 0) goto L99
                if (r9 == 0) goto L6a
                boolean r12 = r5.isEmpty()     // Catch: java.lang.Throwable -> L6f
                if (r12 == 0) goto L6a
                r12 = 1
                r14.caughtUp = r12     // Catch: java.lang.Throwable -> L6f
            L6a:
                r12 = 0
                r14.emitting = r12     // Catch: java.lang.Throwable -> L6f
                monitor-exit(r14)     // Catch: java.lang.Throwable -> L6f
                goto L9
            L6f:
                r12 = move-exception
                monitor-exit(r14)     // Catch: java.lang.Throwable -> L6f
                throw r12
            L72:
                r12 = move-exception
                monitor-exit(r14)     // Catch: java.lang.Throwable -> L72
                throw r12
            L75:
                r9 = 0
                goto L35
            L77:
                r1 = 0
                goto L46
            L79:
                rx.internal.operators.NotificationLite<T> r12 = r14.f1644nl
                java.lang.Object r11 = r12.getValue(r10)
                r8.onNext(r11)     // Catch: java.lang.Throwable -> L89
                r12 = 1
                long r6 = r6 - r12
                r12 = 1
                long r2 = r2 + r12
                goto L37
            L89:
                r4 = move-exception
                r5.clear()
                p045rx.exceptions.Exceptions.throwIfFatal(r4)
                java.lang.Throwable r12 = p045rx.exceptions.OnErrorThrowable.addValueAsLastCause(r4, r11)
                r8.onError(r12)
                goto L9
            L99:
                r12 = 0
                r14.missed = r12     // Catch: java.lang.Throwable -> L6f
                monitor-exit(r14)     // Catch: java.lang.Throwable -> L6f
                goto L10
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.subjects.UnicastSubject.State.replay():void");
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            doTerminate();
            this.done = true;
            synchronized (this) {
                if (!this.emitting) {
                    this.emitting = true;
                    this.queue.clear();
                }
            }
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.done;
        }

        boolean checkTerminated(boolean done, boolean empty, Subscriber<? super T> s) {
            if (s.isUnsubscribed()) {
                this.queue.clear();
                return true;
            }
            if (done) {
                Throwable e = this.error;
                if (e != null) {
                    this.queue.clear();
                    s.onError(e);
                    return true;
                }
                if (empty) {
                    s.onCompleted();
                    return true;
                }
            }
            return false;
        }

        void doTerminate() {
            Action0 a;
            AtomicReference<Action0> ref = this.terminateOnce;
            if (ref != null && (a = ref.get()) != null && ref.compareAndSet(a, null)) {
                a.call();
            }
        }
    }
}
