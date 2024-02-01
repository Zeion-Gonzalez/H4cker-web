package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public final class OperatorTake<T> implements Observable.Operator<T, T> {
    final int limit;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorTake(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit >= 0 required but it was " + limit);
        }
        this.limit = limit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: rx.internal.operators.OperatorTake$1 */
    /* loaded from: classes.dex */
    public class C11031 extends Subscriber<T> {
        boolean completed;
        int count;
        final /* synthetic */ Subscriber val$child;

        C11031(Subscriber subscriber) {
            this.val$child = subscriber;
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (!this.completed) {
                this.completed = true;
                this.val$child.onCompleted();
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (!this.completed) {
                this.completed = true;
                try {
                    this.val$child.onError(e);
                } finally {
                    unsubscribe();
                }
            }
        }

        @Override // p045rx.Observer
        public void onNext(T i) {
            if (isUnsubscribed()) {
                return;
            }
            int i2 = this.count;
            this.count = i2 + 1;
            if (i2 < OperatorTake.this.limit) {
                boolean stop = this.count == OperatorTake.this.limit;
                this.val$child.onNext(i);
                if (stop && !this.completed) {
                    this.completed = true;
                    try {
                        this.val$child.onCompleted();
                    } finally {
                        unsubscribe();
                    }
                }
            }
        }

        @Override // p045rx.Subscriber
        public void setProducer(final Producer producer) {
            this.val$child.setProducer(new Producer() { // from class: rx.internal.operators.OperatorTake.1.1
                final AtomicLong requested = new AtomicLong(0);

                @Override // p045rx.Producer
                public void request(long n) {
                    long r;
                    long c;
                    if (n <= 0 || C11031.this.completed) {
                        return;
                    }
                    do {
                        r = this.requested.get();
                        c = Math.min(n, OperatorTake.this.limit - r);
                        if (c == 0) {
                            return;
                        }
                    } while (!this.requested.compareAndSet(r, r + c));
                    producer.request(c);
                }
            });
        }
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        C11031 c11031 = new C11031(child);
        if (this.limit == 0) {
            child.onCompleted();
            c11031.unsubscribe();
        }
        child.add(c11031);
        return c11031;
    }
}
