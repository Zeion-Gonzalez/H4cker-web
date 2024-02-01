package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.schedulers.Timestamped;

/* loaded from: classes.dex */
public final class OperatorTimestamp<T> implements Observable.Operator<Timestamped<T>, T> {
    final Scheduler scheduler;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorTimestamp(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Subscriber<? super T> call(final Subscriber<? super Timestamped<T>> o) {
        return (Subscriber<T>) new Subscriber<T>(o) { // from class: rx.internal.operators.OperatorTimestamp.1
            @Override // p045rx.Observer
            public void onCompleted() {
                o.onCompleted();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
                o.onNext(new Timestamped(OperatorTimestamp.this.scheduler.now(), t));
            }
        };
    }
}
