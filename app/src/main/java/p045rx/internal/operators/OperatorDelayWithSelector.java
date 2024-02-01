package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;
import p045rx.observers.SerializedSubscriber;
import p045rx.observers.Subscribers;
import p045rx.subjects.PublishSubject;

/* loaded from: classes.dex */
public final class OperatorDelayWithSelector<T, V> implements Observable.Operator<T, T> {
    final Func1<? super T, ? extends Observable<V>> itemDelay;
    final Observable<? extends T> source;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorDelayWithSelector(Observable<? extends T> source, Func1<? super T, ? extends Observable<V>> itemDelay) {
        this.source = source;
        this.itemDelay = itemDelay;
    }

    public Subscriber<? super T> call(Subscriber<? super T> _child) {
        final SerializedSubscriber<T> child = new SerializedSubscriber<>(_child);
        final PublishSubject<Observable<T>> delayedEmissions = PublishSubject.create();
        _child.add(Observable.merge(delayedEmissions).unsafeSubscribe(Subscribers.from(child)));
        return (Subscriber<T>) new Subscriber<T>(_child) { // from class: rx.internal.operators.OperatorDelayWithSelector.1
            @Override // p045rx.Observer
            public void onCompleted() {
                delayedEmissions.onCompleted();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                child.onError(e);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // p045rx.Observer
            public void onNext(final T t) {
                try {
                    delayedEmissions.onNext(OperatorDelayWithSelector.this.itemDelay.call(t).take(1).defaultIfEmpty(null).map((Func1<V, T>) new Func1<V, T>() { // from class: rx.internal.operators.OperatorDelayWithSelector.1.1
                        @Override // p045rx.functions.Func1
                        public T call(V v) {
                            return (T) t;
                        }
                    }));
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this);
                }
            }
        };
    }
}
