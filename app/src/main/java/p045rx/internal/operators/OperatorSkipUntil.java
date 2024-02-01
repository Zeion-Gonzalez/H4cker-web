package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.observers.SerializedSubscriber;

/* loaded from: classes.dex */
public final class OperatorSkipUntil<T, U> implements Observable.Operator<T, T> {
    final Observable<U> other;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorSkipUntil(Observable<U> other) {
        this.other = other;
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        final SerializedSubscriber<T> s = new SerializedSubscriber<>(child);
        final AtomicBoolean gate = new AtomicBoolean();
        Subscriber<U> u = new Subscriber<U>() { // from class: rx.internal.operators.OperatorSkipUntil.1
            @Override // p045rx.Observer
            public void onNext(U t) {
                gate.set(true);
                unsubscribe();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                s.onError(e);
                s.unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                unsubscribe();
            }
        };
        child.add(u);
        this.other.unsafeSubscribe(u);
        return (Subscriber<T>) new Subscriber<T>(child) { // from class: rx.internal.operators.OperatorSkipUntil.2
            @Override // p045rx.Observer
            public void onNext(T t) {
                if (gate.get()) {
                    s.onNext(t);
                } else {
                    request(1L);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                s.onError(e);
                unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                s.onCompleted();
                unsubscribe();
            }
        };
    }
}
