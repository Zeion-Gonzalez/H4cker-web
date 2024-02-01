package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;
import p045rx.internal.producers.SingleDelayedProducer;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OperatorAll<T> implements Observable.Operator<Boolean, T> {
    final Func1<? super T, Boolean> predicate;

    public OperatorAll(Func1<? super T, Boolean> predicate) {
        this.predicate = predicate;
    }

    @Override // p045rx.functions.Func1
    public Subscriber<? super T> call(final Subscriber<? super Boolean> child) {
        final SingleDelayedProducer<Boolean> producer = new SingleDelayedProducer<>(child);
        Subscriber subscriber = (Subscriber<T>) new Subscriber<T>() { // from class: rx.internal.operators.OperatorAll.1
            boolean done;

            @Override // p045rx.Observer
            public void onNext(T t) {
                if (!this.done) {
                    try {
                        Boolean result = OperatorAll.this.predicate.call(t);
                        if (!result.booleanValue()) {
                            this.done = true;
                            producer.setValue(false);
                            unsubscribe();
                        }
                    } catch (Throwable e) {
                        Exceptions.throwOrReport(e, this, t);
                    }
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                if (!this.done) {
                    this.done = true;
                    child.onError(e);
                } else {
                    RxJavaHooks.onError(e);
                }
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                if (!this.done) {
                    this.done = true;
                    producer.setValue(true);
                }
            }
        };
        child.add(subscriber);
        child.setProducer(producer);
        return subscriber;
    }
}
