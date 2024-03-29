package p045rx.internal.operators;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.MissingBackpressureException;

/* loaded from: classes.dex */
public final class OperatorBufferWithSize<T> implements Observable.Operator<List<T>, T> {
    final int count;
    final int skip;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorBufferWithSize(int count, int skip) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be greater than 0");
        }
        if (skip <= 0) {
            throw new IllegalArgumentException("skip must be greater than 0");
        }
        this.count = count;
        this.skip = skip;
    }

    public Subscriber<? super T> call(Subscriber<? super List<T>> child) {
        if (this.skip == this.count) {
            BufferExact<T> parent = new BufferExact<>(child, this.count);
            child.add(parent);
            child.setProducer(parent.createProducer());
            return parent;
        }
        if (this.skip > this.count) {
            BufferSkip<T> parent2 = new BufferSkip<>(child, this.count, this.skip);
            child.add(parent2);
            child.setProducer(parent2.createProducer());
            return parent2;
        }
        BufferOverlap<T> parent3 = new BufferOverlap<>(child, this.count, this.skip);
        child.add(parent3);
        child.setProducer(parent3.createProducer());
        return parent3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class BufferExact<T> extends Subscriber<T> {
        final Subscriber<? super List<T>> actual;
        List<T> buffer;
        final int count;

        public BufferExact(Subscriber<? super List<T>> actual, int count) {
            this.actual = actual;
            this.count = count;
            request(0L);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            List<T> b = this.buffer;
            if (b == null) {
                b = new ArrayList<>(this.count);
                this.buffer = b;
            }
            b.add(t);
            if (b.size() == this.count) {
                this.buffer = null;
                this.actual.onNext(b);
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.buffer = null;
            this.actual.onError(e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            List<T> b = this.buffer;
            if (b != null) {
                this.actual.onNext(b);
            }
            this.actual.onCompleted();
        }

        Producer createProducer() {
            return new Producer() { // from class: rx.internal.operators.OperatorBufferWithSize.BufferExact.1
                @Override // p045rx.Producer
                public void request(long n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("n >= required but it was " + n);
                    }
                    if (n != 0) {
                        long u = BackpressureUtils.multiplyCap(n, BufferExact.this.count);
                        BufferExact.this.request(u);
                    }
                }
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class BufferSkip<T> extends Subscriber<T> {
        final Subscriber<? super List<T>> actual;
        List<T> buffer;
        final int count;
        long index;
        final int skip;

        public BufferSkip(Subscriber<? super List<T>> actual, int count, int skip) {
            this.actual = actual;
            this.count = count;
            this.skip = skip;
            request(0L);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            long i = this.index;
            List<T> b = this.buffer;
            if (i == 0) {
                b = new ArrayList<>(this.count);
                this.buffer = b;
            }
            long i2 = i + 1;
            if (i2 == this.skip) {
                this.index = 0L;
            } else {
                this.index = i2;
            }
            if (b != null) {
                b.add(t);
                if (b.size() == this.count) {
                    this.buffer = null;
                    this.actual.onNext(b);
                }
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.buffer = null;
            this.actual.onError(e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            List<T> b = this.buffer;
            if (b != null) {
                this.buffer = null;
                this.actual.onNext(b);
            }
            this.actual.onCompleted();
        }

        Producer createProducer() {
            return new BufferSkipProducer();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class BufferSkipProducer extends AtomicBoolean implements Producer {
            private static final long serialVersionUID = 3428177408082367154L;

            BufferSkipProducer() {
            }

            @Override // p045rx.Producer
            public void request(long n) {
                if (n < 0) {
                    throw new IllegalArgumentException("n >= 0 required but it was " + n);
                }
                if (n != 0) {
                    BufferSkip<T> parent = BufferSkip.this;
                    if (!get() && compareAndSet(false, true)) {
                        long u = BackpressureUtils.multiplyCap(n, parent.count);
                        long v = BackpressureUtils.multiplyCap(parent.skip - parent.count, n - 1);
                        long w = BackpressureUtils.addCap(u, v);
                        parent.request(w);
                        return;
                    }
                    long u2 = BackpressureUtils.multiplyCap(n, parent.skip);
                    parent.request(u2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class BufferOverlap<T> extends Subscriber<T> {
        final Subscriber<? super List<T>> actual;
        final int count;
        long index;
        long produced;
        final ArrayDeque<List<T>> queue = new ArrayDeque<>();
        final AtomicLong requested = new AtomicLong();
        final int skip;

        public BufferOverlap(Subscriber<? super List<T>> actual, int count, int skip) {
            this.actual = actual;
            this.count = count;
            this.skip = skip;
            request(0L);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            long i = this.index;
            if (i == 0) {
                this.queue.offer(new ArrayList<>(this.count));
            }
            long i2 = i + 1;
            if (i2 == this.skip) {
                this.index = 0L;
            } else {
                this.index = i2;
            }
            Iterator i$ = this.queue.iterator();
            while (i$.hasNext()) {
                List<T> list = i$.next();
                list.add(t);
            }
            List<T> b = this.queue.peek();
            if (b != null && b.size() == this.count) {
                this.queue.poll();
                this.produced++;
                this.actual.onNext(b);
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.queue.clear();
            this.actual.onError(e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            long p = this.produced;
            if (p != 0) {
                if (p > this.requested.get()) {
                    this.actual.onError(new MissingBackpressureException("More produced than requested? " + p));
                    return;
                }
                this.requested.addAndGet(-p);
            }
            BackpressureUtils.postCompleteDone(this.requested, this.queue, this.actual);
        }

        Producer createProducer() {
            return new BufferOverlapProducer();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class BufferOverlapProducer extends AtomicBoolean implements Producer {
            private static final long serialVersionUID = -4015894850868853147L;

            BufferOverlapProducer() {
            }

            @Override // p045rx.Producer
            public void request(long n) {
                BufferOverlap<T> parent = BufferOverlap.this;
                if (BackpressureUtils.postCompleteRequest(parent.requested, n, parent.queue, parent.actual) && n != 0) {
                    if (!get() && compareAndSet(false, true)) {
                        long u = BackpressureUtils.multiplyCap(parent.skip, n - 1);
                        long v = BackpressureUtils.addCap(u, parent.count);
                        parent.request(v);
                    } else {
                        long u2 = BackpressureUtils.multiplyCap(parent.skip, n);
                        parent.request(u2);
                    }
                }
            }
        }
    }
}
