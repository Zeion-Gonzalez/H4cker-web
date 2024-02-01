package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public final class OperatorSkip<T> implements Observable.Operator<T, T> {
    final int toSkip;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorSkip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n >= 0 required but it was " + n);
        }
        this.toSkip = n;
    }

    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        return (Subscriber<T>) new Subscriber<T>(child) { // from class: rx.internal.operators.OperatorSkip.1
            int skipped;

            @Override // p045rx.Observer
            public void onCompleted() {
                child.onCompleted();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
                if (this.skipped >= OperatorSkip.this.toSkip) {
                    child.onNext(t);
                } else {
                    this.skipped++;
                }
            }

            @Override // p045rx.Subscriber
            public void setProducer(Producer producer) {
                child.setProducer(producer);
                producer.request(OperatorSkip.this.toSkip);
            }
        };
    }
}
