package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.CompositeException;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.MissingBackpressureException;
import p045rx.exceptions.OnErrorThrowable;
import p045rx.internal.util.RxRingBuffer;
import p045rx.internal.util.ScalarSynchronousObservable;
import p045rx.internal.util.atomic.SpscAtomicArrayQueue;
import p045rx.internal.util.atomic.SpscExactAtomicArrayQueue;
import p045rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue;
import p045rx.internal.util.unsafe.Pow2;
import p045rx.internal.util.unsafe.SpscArrayQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public final class OperatorMerge<T> implements Observable.Operator<T, Observable<? extends T>> {
    final boolean delayErrors;
    final int maxConcurrent;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class HolderNoDelay {
        static final OperatorMerge<Object> INSTANCE = new OperatorMerge<>(false, Integer.MAX_VALUE);

        HolderNoDelay() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class HolderDelayErrors {
        static final OperatorMerge<Object> INSTANCE = new OperatorMerge<>(true, Integer.MAX_VALUE);

        HolderDelayErrors() {
        }
    }

    public static <T> OperatorMerge<T> instance(boolean delayErrors) {
        return delayErrors ? (OperatorMerge<T>) HolderDelayErrors.INSTANCE : (OperatorMerge<T>) HolderNoDelay.INSTANCE;
    }

    public static <T> OperatorMerge<T> instance(boolean delayErrors, int maxConcurrent) {
        if (maxConcurrent <= 0) {
            throw new IllegalArgumentException("maxConcurrent > 0 required but it was " + maxConcurrent);
        }
        return maxConcurrent == Integer.MAX_VALUE ? instance(delayErrors) : new OperatorMerge<>(delayErrors, maxConcurrent);
    }

    OperatorMerge(boolean delayErrors, int maxConcurrent) {
        this.delayErrors = delayErrors;
        this.maxConcurrent = maxConcurrent;
    }

    public Subscriber<Observable<? extends T>> call(Subscriber<? super T> child) {
        MergeSubscriber<T> subscriber = new MergeSubscriber<>(child, this.delayErrors, this.maxConcurrent);
        MergeProducer<T> producer = new MergeProducer<>(subscriber);
        subscriber.producer = producer;
        child.add(subscriber);
        child.setProducer(producer);
        return subscriber;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MergeProducer<T> extends AtomicLong implements Producer {
        private static final long serialVersionUID = -1214379189873595503L;
        final MergeSubscriber<T> subscriber;

        public MergeProducer(MergeSubscriber<T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n <= 0) {
                if (n < 0) {
                    throw new IllegalArgumentException("n >= 0 required");
                }
            } else if (get() != Long.MAX_VALUE) {
                BackpressureUtils.getAndAddRequest(this, n);
                this.subscriber.emit();
            }
        }

        public long produced(int n) {
            return addAndGet(-n);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MergeSubscriber<T> extends Subscriber<Observable<? extends T>> {
        static final InnerSubscriber<?>[] EMPTY = new InnerSubscriber[0];
        final Subscriber<? super T> child;
        final boolean delayErrors;
        volatile boolean done;
        boolean emitting;
        volatile ConcurrentLinkedQueue<Throwable> errors;
        long lastId;
        int lastIndex;
        final int maxConcurrent;
        boolean missed;
        MergeProducer<T> producer;
        volatile Queue<Object> queue;
        int scalarEmissionCount;
        final int scalarEmissionLimit;
        volatile CompositeSubscription subscriptions;
        long uniqueId;

        /* renamed from: nl */
        final NotificationLite<T> f1603nl = NotificationLite.instance();
        final Object innerGuard = new Object();
        volatile InnerSubscriber<?>[] innerSubscribers = EMPTY;

        @Override // p045rx.Observer
        public /* bridge */ /* synthetic */ void onNext(Object x0) {
            onNext((Observable) ((Observable) x0));
        }

        public MergeSubscriber(Subscriber<? super T> child, boolean delayErrors, int maxConcurrent) {
            this.child = child;
            this.delayErrors = delayErrors;
            this.maxConcurrent = maxConcurrent;
            if (maxConcurrent == Integer.MAX_VALUE) {
                this.scalarEmissionLimit = Integer.MAX_VALUE;
                request(Long.MAX_VALUE);
            } else {
                this.scalarEmissionLimit = Math.max(1, maxConcurrent >> 1);
                request(maxConcurrent);
            }
        }

        Queue<Throwable> getOrCreateErrorQueue() {
            ConcurrentLinkedQueue<Throwable> q = this.errors;
            if (q == null) {
                synchronized (this) {
                    try {
                        q = this.errors;
                        if (q == null) {
                            ConcurrentLinkedQueue<Throwable> q2 = new ConcurrentLinkedQueue<>();
                            try {
                                this.errors = q2;
                                q = q2;
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }
            return q;
        }

        CompositeSubscription getOrCreateComposite() {
            CompositeSubscription c = this.subscriptions;
            if (c == null) {
                boolean shouldAdd = false;
                synchronized (this) {
                    try {
                        c = this.subscriptions;
                        if (c == null) {
                            CompositeSubscription c2 = new CompositeSubscription();
                            try {
                                this.subscriptions = c2;
                                shouldAdd = true;
                                c = c2;
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        }
                        if (shouldAdd) {
                            add(c);
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }
            return c;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onNext(Observable<? extends T> t) {
            if (t != null) {
                if (t == Observable.empty()) {
                    emitEmpty();
                    return;
                }
                if (t instanceof ScalarSynchronousObservable) {
                    tryEmit(((ScalarSynchronousObservable) t).get());
                    return;
                }
                long j = this.uniqueId;
                this.uniqueId = 1 + j;
                InnerSubscriber<T> inner = new InnerSubscriber<>(this, j);
                addInner(inner);
                t.unsafeSubscribe(inner);
                emit();
            }
        }

        void emitEmpty() {
            int produced = this.scalarEmissionCount + 1;
            if (produced == this.scalarEmissionLimit) {
                this.scalarEmissionCount = 0;
                requestMore(produced);
            } else {
                this.scalarEmissionCount = produced;
            }
        }

        private void reportError() {
            List<Throwable> list = new ArrayList<>(this.errors);
            if (list.size() == 1) {
                this.child.onError(list.get(0));
            } else {
                this.child.onError(new CompositeException(list));
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            getOrCreateErrorQueue().offer(e);
            this.done = true;
            emit();
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.done = true;
            emit();
        }

        /* JADX WARN: Multi-variable type inference failed */
        void addInner(InnerSubscriber<T> inner) {
            getOrCreateComposite().add(inner);
            synchronized (this.innerGuard) {
                InnerSubscriber<?>[] a = this.innerSubscribers;
                int n = a.length;
                InnerSubscriber<?>[] b = new InnerSubscriber[n + 1];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = inner;
                this.innerSubscribers = b;
            }
        }

        void removeInner(InnerSubscriber<T> inner) {
            RxRingBuffer q = inner.queue;
            if (q != null) {
                q.release();
            }
            this.subscriptions.remove(inner);
            synchronized (this.innerGuard) {
                InnerSubscriber<?>[] a = this.innerSubscribers;
                int n = a.length;
                int j = -1;
                int i = 0;
                while (true) {
                    if (i >= n) {
                        break;
                    }
                    if (!inner.equals(a[i])) {
                        i++;
                    } else {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    if (n == 1) {
                        this.innerSubscribers = EMPTY;
                        return;
                    }
                    InnerSubscriber<?>[] b = new InnerSubscriber[n - 1];
                    System.arraycopy(a, 0, b, 0, j);
                    System.arraycopy(a, j + 1, b, j, (n - j) - 1);
                    this.innerSubscribers = b;
                }
            }
        }

        void tryEmit(InnerSubscriber<T> subscriber, T value) {
            boolean success = false;
            long r = this.producer.get();
            if (r != 0) {
                synchronized (this) {
                    r = this.producer.get();
                    if (!this.emitting && r != 0) {
                        this.emitting = true;
                        success = true;
                    }
                }
            }
            if (success) {
                RxRingBuffer subscriberQueue = subscriber.queue;
                if (subscriberQueue == null || subscriberQueue.isEmpty()) {
                    emitScalar(subscriber, value, r);
                    return;
                } else {
                    queueScalar(subscriber, value);
                    emitLoop();
                    return;
                }
            }
            queueScalar(subscriber, value);
            emit();
        }

        protected void queueScalar(InnerSubscriber<T> subscriber, T value) {
            RxRingBuffer q = subscriber.queue;
            if (q == null) {
                q = RxRingBuffer.getSpscInstance();
                subscriber.add(q);
                subscriber.queue = q;
            }
            try {
                q.onNext(this.f1603nl.next(value));
            } catch (IllegalStateException ex) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.unsubscribe();
                    subscriber.onError(ex);
                }
            } catch (MissingBackpressureException ex2) {
                subscriber.unsubscribe();
                subscriber.onError(ex2);
            }
        }

        protected void emitScalar(InnerSubscriber<T> subscriber, T value, long r) {
            boolean skipFinal = false;
            try {
                try {
                    this.child.onNext(value);
                } catch (Throwable t) {
                    if (!this.delayErrors) {
                        Exceptions.throwIfFatal(t);
                        subscriber.unsubscribe();
                        subscriber.onError(t);
                        if (1 == 0) {
                            synchronized (this) {
                                this.emitting = false;
                                return;
                            }
                        }
                        return;
                    }
                    getOrCreateErrorQueue().offer(t);
                }
                if (r != Long.MAX_VALUE) {
                    this.producer.produced(1);
                }
                subscriber.requestMore(1L);
                synchronized (this) {
                    skipFinal = true;
                    if (this.missed) {
                        this.missed = false;
                        if (1 == 0) {
                            synchronized (this) {
                                this.emitting = false;
                            }
                        }
                        emitLoop();
                    } else {
                        this.emitting = false;
                        if (1 == 0) {
                            synchronized (this) {
                                this.emitting = false;
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                if (!skipFinal) {
                    synchronized (this) {
                        this.emitting = false;
                    }
                }
                throw th;
            }
        }

        public void requestMore(long n) {
            request(n);
        }

        void tryEmit(T value) {
            boolean success = false;
            long r = this.producer.get();
            if (r != 0) {
                synchronized (this) {
                    r = this.producer.get();
                    if (!this.emitting && r != 0) {
                        this.emitting = true;
                        success = true;
                    }
                }
            }
            if (success) {
                Queue<Object> mainQueue = this.queue;
                if (mainQueue == null || mainQueue.isEmpty()) {
                    emitScalar(value, r);
                    return;
                } else {
                    queueScalar(value);
                    emitLoop();
                    return;
                }
            }
            queueScalar(value);
            emit();
        }

        protected void queueScalar(T value) {
            Queue<Object> q = this.queue;
            if (q == null) {
                int mc = this.maxConcurrent;
                if (mc == Integer.MAX_VALUE) {
                    q = new SpscUnboundedAtomicArrayQueue<>(RxRingBuffer.SIZE);
                } else if (Pow2.isPowerOfTwo(mc)) {
                    if (UnsafeAccess.isUnsafeAvailable()) {
                        q = new SpscArrayQueue<>(mc);
                    } else {
                        q = new SpscAtomicArrayQueue<>(mc);
                    }
                } else {
                    q = new SpscExactAtomicArrayQueue<>(mc);
                }
                this.queue = q;
            }
            if (!q.offer(this.f1603nl.next(value))) {
                unsubscribe();
                onError(OnErrorThrowable.addValueAsLastCause(new MissingBackpressureException(), value));
            }
        }

        protected void emitScalar(T value, long r) {
            boolean skipFinal = false;
            try {
                try {
                    this.child.onNext(value);
                } catch (Throwable t) {
                    if (!this.delayErrors) {
                        Exceptions.throwIfFatal(t);
                        unsubscribe();
                        onError(t);
                        if (1 == 0) {
                            synchronized (this) {
                                this.emitting = false;
                                return;
                            }
                        }
                        return;
                    }
                    getOrCreateErrorQueue().offer(t);
                }
                if (r != Long.MAX_VALUE) {
                    this.producer.produced(1);
                }
                int produced = this.scalarEmissionCount + 1;
                if (produced == this.scalarEmissionLimit) {
                    this.scalarEmissionCount = 0;
                    requestMore(produced);
                } else {
                    this.scalarEmissionCount = produced;
                }
                synchronized (this) {
                    skipFinal = true;
                    if (this.missed) {
                        this.missed = false;
                        if (1 == 0) {
                            synchronized (this) {
                                this.emitting = false;
                            }
                        }
                        emitLoop();
                    } else {
                        this.emitting = false;
                        if (1 == 0) {
                            synchronized (this) {
                                this.emitting = false;
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                if (!skipFinal) {
                    synchronized (this) {
                        this.emitting = false;
                    }
                }
                throw th;
            }
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

        /* JADX WARN: Code restructure failed: missing block: B:100:0x0147, code lost:
        
            r14 = r8;
            r7 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:101:0x0149, code lost:
        
            if (r7 >= r15) goto L282;
         */
        /* JADX WARN: Code restructure failed: missing block: B:103:0x0155, code lost:
        
            if (r9[r14].f1602id != r0) goto L119;
         */
        /* JADX WARN: Code restructure failed: missing block: B:104:0x0157, code lost:
        
            r8 = r14;
            r32.lastIndex = r14;
            r32.lastId = r9[r14].f1602id;
         */
        /* JADX WARN: Code restructure failed: missing block: B:105:0x016a, code lost:
        
            r14 = r8;
            r7 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:106:0x016c, code lost:
        
            if (r7 >= r15) goto L272;
         */
        /* JADX WARN: Code restructure failed: missing block: B:108:0x0172, code lost:
        
            if (checkTerminate() == false) goto L123;
         */
        /* JADX WARN: Code restructure failed: missing block: B:110:0x0176, code lost:
        
            if (1 != 0) goto L292;
         */
        /* JADX WARN: Code restructure failed: missing block: B:111:0x0178, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:113:0x017b, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:114:0x0181, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:119:0x0187, code lost:
        
            r14 = r14 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:120:0x0189, code lost:
        
            if (r14 != r15) goto L284;
         */
        /* JADX WARN: Code restructure failed: missing block: B:121:0x018b, code lost:
        
            r14 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:122:0x018c, code lost:
        
            r7 = r7 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:123:0x018f, code lost:
        
            r13 = r9[r14];
            r16 = null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:124:0x0193, code lost:
        
            r17 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:126:0x0199, code lost:
        
            if (r20 <= 0) goto L279;
         */
        /* JADX WARN: Code restructure failed: missing block: B:128:0x019f, code lost:
        
            if (checkTerminate() == false) goto L139;
         */
        /* JADX WARN: Code restructure failed: missing block: B:130:0x01a3, code lost:
        
            if (1 != 0) goto L294;
         */
        /* JADX WARN: Code restructure failed: missing block: B:131:0x01a5, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:133:0x01a8, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:134:0x01ae, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:139:0x01b4, code lost:
        
            r0 = r13.queue;
         */
        /* JADX WARN: Code restructure failed: missing block: B:140:0x01b8, code lost:
        
            if (r0 != null) goto L165;
         */
        /* JADX WARN: Code restructure failed: missing block: B:141:0x01ba, code lost:
        
            if (r17 <= 0) goto L145;
         */
        /* JADX WARN: Code restructure failed: missing block: B:142:0x01bc, code lost:
        
            if (r28 != false) goto L187;
         */
        /* JADX WARN: Code restructure failed: missing block: B:143:0x01be, code lost:
        
            r20 = r32.producer.produced(r17);
         */
        /* JADX WARN: Code restructure failed: missing block: B:144:0x01cc, code lost:
        
            r13.requestMore(r17);
         */
        /* JADX WARN: Code restructure failed: missing block: B:146:0x01da, code lost:
        
            if (r20 == 0) goto L275;
         */
        /* JADX WARN: Code restructure failed: missing block: B:147:0x01dc, code lost:
        
            if (r16 != null) goto L277;
         */
        /* JADX WARN: Code restructure failed: missing block: B:148:0x01de, code lost:
        
            r11 = r13.done;
            r12 = r13.queue;
         */
        /* JADX WARN: Code restructure failed: missing block: B:149:0x01e2, code lost:
        
            if (r11 == false) goto L189;
         */
        /* JADX WARN: Code restructure failed: missing block: B:150:0x01e4, code lost:
        
            if (r12 == null) goto L153;
         */
        /* JADX WARN: Code restructure failed: missing block: B:152:0x01ea, code lost:
        
            if (r12.isEmpty() == false) goto L189;
         */
        /* JADX WARN: Code restructure failed: missing block: B:153:0x01ec, code lost:
        
            removeInner(r13);
         */
        /* JADX WARN: Code restructure failed: missing block: B:154:0x01f5, code lost:
        
            if (checkTerminate() == false) goto L188;
         */
        /* JADX WARN: Code restructure failed: missing block: B:156:0x01f9, code lost:
        
            if (1 != 0) goto L296;
         */
        /* JADX WARN: Code restructure failed: missing block: B:157:0x01fb, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:159:0x01fe, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:160:0x0204, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:165:0x020a, code lost:
        
            r16 = r0.poll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:166:0x020e, code lost:
        
            if (r16 == null) goto L280;
         */
        /* JADX WARN: Code restructure failed: missing block: B:168:0x021e, code lost:
        
            r4.onNext((T) r32.f1603nl.getValue(r16));
         */
        /* JADX WARN: Code restructure failed: missing block: B:169:0x0223, code lost:
        
            r20 = r20 - 1;
            r17 = r17 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:170:0x022b, code lost:
        
            r27 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:171:0x022c, code lost:
        
            r23 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:172:0x022e, code lost:
        
            p045rx.exceptions.Exceptions.throwIfFatal(r27);
         */
        /* JADX WARN: Code restructure failed: missing block: B:173:0x0231, code lost:
        
            r4.onError(r27);
         */
        /* JADX WARN: Code restructure failed: missing block: B:174:0x0236, code lost:
        
            unsubscribe();
         */
        /* JADX WARN: Code restructure failed: missing block: B:175:0x0239, code lost:
        
            if (1 == 0) goto L176;
         */
        /* JADX WARN: Code restructure failed: missing block: B:176:0x023b, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:178:0x023e, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:184:0x024a, code lost:
        
            r30 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:185:0x024b, code lost:
        
            unsubscribe();
         */
        /* JADX WARN: Code restructure failed: missing block: B:186:0x024e, code lost:
        
            throw r30;
         */
        /* JADX WARN: Code restructure failed: missing block: B:187:0x024f, code lost:
        
            r20 = Long.MAX_VALUE;
         */
        /* JADX WARN: Code restructure failed: missing block: B:188:0x0256, code lost:
        
            r19 = r19 + 1;
            r10 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:190:0x025d, code lost:
        
            if (r20 != 0) goto L209;
         */
        /* JADX WARN: Code restructure failed: missing block: B:191:0x025f, code lost:
        
            r32.lastIndex = r14;
            r32.lastId = r9[r14].f1602id;
         */
        /* JADX WARN: Code restructure failed: missing block: B:192:0x0271, code lost:
        
            if (r19 <= 0) goto L194;
         */
        /* JADX WARN: Code restructure failed: missing block: B:193:0x0273, code lost:
        
            request(r19);
         */
        /* JADX WARN: Code restructure failed: missing block: B:194:0x027f, code lost:
        
            if (r10 != false) goto L262;
         */
        /* JADX WARN: Code restructure failed: missing block: B:195:0x0281, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:197:0x0288, code lost:
        
            if (r32.missed != false) goto L213;
         */
        /* JADX WARN: Code restructure failed: missing block: B:198:0x028a, code lost:
        
            r23 = true;
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:199:0x0294, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:200:0x0295, code lost:
        
            if (1 != 0) goto L300;
         */
        /* JADX WARN: Code restructure failed: missing block: B:201:0x0297, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:203:0x029a, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:204:0x02a0, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:209:0x02a6, code lost:
        
            r14 = r14 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:20:0x003b, code lost:
        
            if (r0 != null) goto L21;
         */
        /* JADX WARN: Code restructure failed: missing block: B:210:0x02a8, code lost:
        
            if (r14 != r15) goto L274;
         */
        /* JADX WARN: Code restructure failed: missing block: B:211:0x02aa, code lost:
        
            r14 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:212:0x02ab, code lost:
        
            r7 = r7 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:214:0x02b1, code lost:
        
            r32.missed = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:215:0x02b7, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:21:0x003d, code lost:
        
            r22 = 0;
            r16 = null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x0045, code lost:
        
            if (r20 <= 0) goto L267;
         */
        /* JADX WARN: Code restructure failed: missing block: B:24:0x0047, code lost:
        
            r16 = r0.poll();
         */
        /* JADX WARN: Code restructure failed: missing block: B:25:0x004f, code lost:
        
            if (checkTerminate() == false) goto L37;
         */
        /* JADX WARN: Code restructure failed: missing block: B:27:0x0053, code lost:
        
            if (1 != 0) goto L286;
         */
        /* JADX WARN: Code restructure failed: missing block: B:286:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:287:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:288:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:289:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x0055, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:290:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:291:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:292:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:293:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:294:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:295:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:296:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:297:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:298:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:299:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:300:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:301:?, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x0058, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x005e, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:37:0x0066, code lost:
        
            if (r16 != null) goto L65;
         */
        /* JADX WARN: Code restructure failed: missing block: B:38:0x0068, code lost:
        
            if (r22 <= 0) goto L41;
         */
        /* JADX WARN: Code restructure failed: missing block: B:39:0x006a, code lost:
        
            if (r28 == false) goto L90;
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x006c, code lost:
        
            r20 = Long.MAX_VALUE;
         */
        /* JADX WARN: Code restructure failed: missing block: B:42:0x0075, code lost:
        
            if (r20 == 0) goto L264;
         */
        /* JADX WARN: Code restructure failed: missing block: B:43:0x0077, code lost:
        
            if (r16 != null) goto L266;
         */
        /* JADX WARN: Code restructure failed: missing block: B:44:0x0079, code lost:
        
            r5 = r32.done;
            r0 = r32.queue;
            r9 = r32.innerSubscribers;
            r15 = r9.length;
         */
        /* JADX WARN: Code restructure failed: missing block: B:45:0x0088, code lost:
        
            if (r5 == false) goto L92;
         */
        /* JADX WARN: Code restructure failed: missing block: B:46:0x008a, code lost:
        
            if (r0 == null) goto L49;
         */
        /* JADX WARN: Code restructure failed: missing block: B:48:0x0090, code lost:
        
            if (r0.isEmpty() == false) goto L92;
         */
        /* JADX WARN: Code restructure failed: missing block: B:49:0x0092, code lost:
        
            if (r15 != 0) goto L92;
         */
        /* JADX WARN: Code restructure failed: missing block: B:50:0x0094, code lost:
        
            r6 = r32.errors;
         */
        /* JADX WARN: Code restructure failed: missing block: B:51:0x0098, code lost:
        
            if (r6 == null) goto L54;
         */
        /* JADX WARN: Code restructure failed: missing block: B:53:0x009e, code lost:
        
            if (r6.isEmpty() == false) goto L91;
         */
        /* JADX WARN: Code restructure failed: missing block: B:54:0x00a0, code lost:
        
            r4.onCompleted();
         */
        /* JADX WARN: Code restructure failed: missing block: B:56:0x00a5, code lost:
        
            if (1 != 0) goto L288;
         */
        /* JADX WARN: Code restructure failed: missing block: B:57:0x00a7, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:59:0x00aa, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:60:0x00b0, code lost:
        
            monitor-exit(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:66:0x00c4, code lost:
        
            r4.onNext((T) r32.f1603nl.getValue(r16));
         */
        /* JADX WARN: Code restructure failed: missing block: B:67:0x00c9, code lost:
        
            r19 = r19 + 1;
            r22 = r22 + 1;
            r20 = r20 - 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x00d3, code lost:
        
            r27 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:70:0x00da, code lost:
        
            if (r32.delayErrors == false) goto L258;
         */
        /* JADX WARN: Code restructure failed: missing block: B:71:0x00dc, code lost:
        
            p045rx.exceptions.Exceptions.throwIfFatal(r27);
            unsubscribe();
            r4.onError(r27);
         */
        /* JADX WARN: Code restructure failed: missing block: B:72:0x00e9, code lost:
        
            if (1 == 0) goto L73;
         */
        /* JADX WARN: Code restructure failed: missing block: B:73:0x00eb, code lost:
        
            monitor-enter(r32);
         */
        /* JADX WARN: Code restructure failed: missing block: B:75:0x00ee, code lost:
        
            r32.emitting = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:81:0x00fa, code lost:
        
            getOrCreateErrorQueue().offer(r27);
         */
        /* JADX WARN: Code restructure failed: missing block: B:90:0x0114, code lost:
        
            r20 = r32.producer.produced(r22);
         */
        /* JADX WARN: Code restructure failed: missing block: B:91:0x0124, code lost:
        
            reportError();
         */
        /* JADX WARN: Code restructure failed: missing block: B:92:0x0129, code lost:
        
            r10 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:93:0x012a, code lost:
        
            if (r15 <= 0) goto L192;
         */
        /* JADX WARN: Code restructure failed: missing block: B:94:0x012c, code lost:
        
            r0 = r32.lastId;
            r8 = r32.lastIndex;
         */
        /* JADX WARN: Code restructure failed: missing block: B:95:0x0136, code lost:
        
            if (r15 <= r8) goto L98;
         */
        /* JADX WARN: Code restructure failed: missing block: B:97:0x0142, code lost:
        
            if (r9[r8].f1602id == r0) goto L105;
         */
        /* JADX WARN: Code restructure failed: missing block: B:98:0x0144, code lost:
        
            if (r15 > r8) goto L100;
         */
        /* JADX WARN: Code restructure failed: missing block: B:99:0x0146, code lost:
        
            r8 = 0;
         */
        /* JADX WARN: Finally extract failed */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void emitLoop() {
            /*
                Method dump skipped, instructions count: 704
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorMerge.MergeSubscriber.emitLoop():void");
        }

        boolean checkTerminate() {
            if (this.child.isUnsubscribed()) {
                return true;
            }
            Queue<Throwable> e = this.errors;
            if (!this.delayErrors && e != null && !e.isEmpty()) {
                try {
                    reportError();
                    return true;
                } finally {
                    unsubscribe();
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class InnerSubscriber<T> extends Subscriber<T> {
        static final int LIMIT = RxRingBuffer.SIZE / 4;
        volatile boolean done;

        /* renamed from: id */
        final long f1602id;
        int outstanding;
        final MergeSubscriber<T> parent;
        volatile RxRingBuffer queue;

        public InnerSubscriber(MergeSubscriber<T> parent, long id) {
            this.parent = parent;
            this.f1602id = id;
        }

        @Override // p045rx.Subscriber
        public void onStart() {
            this.outstanding = RxRingBuffer.SIZE;
            request(RxRingBuffer.SIZE);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            this.parent.tryEmit(this, t);
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.done = true;
            this.parent.getOrCreateErrorQueue().offer(e);
            this.parent.emit();
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.done = true;
            this.parent.emit();
        }

        public void requestMore(long n) {
            int r = this.outstanding - ((int) n);
            if (r > LIMIT) {
                this.outstanding = r;
                return;
            }
            this.outstanding = RxRingBuffer.SIZE;
            int k = RxRingBuffer.SIZE - r;
            if (k > 0) {
                request(k);
            }
        }
    }
}
