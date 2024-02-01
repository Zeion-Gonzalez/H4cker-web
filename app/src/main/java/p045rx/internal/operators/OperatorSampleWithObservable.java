package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.observers.SerializedSubscriber;

/* loaded from: classes.dex */
public final class OperatorSampleWithObservable<T, U> implements Observable.Operator<T, T> {
    static final Object EMPTY_TOKEN = new Object();
    final Observable<U> sampler;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorSampleWithObservable(Observable<U> sampler) {
        this.sampler = sampler;
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        final SerializedSubscriber<T> s = new SerializedSubscriber<>(child);
        final AtomicReference<Object> value = new AtomicReference<>(EMPTY_TOKEN);
        final AtomicReference<Subscription> main = new AtomicReference<>();
        final Subscriber<U> samplerSub = new Subscriber<U>() { // from class: rx.internal.operators.OperatorSampleWithObservable.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // p045rx.Observer
            public void onNext(U t) {
                Object localValue = value.getAndSet(OperatorSampleWithObservable.EMPTY_TOKEN);
                if (localValue != OperatorSampleWithObservable.EMPTY_TOKEN) {
                    s.onNext(localValue);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                s.onError(e);
                ((Subscription) main.get()).unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                onNext(null);
                s.onCompleted();
                ((Subscription) main.get()).unsubscribe();
            }
        };
        Subscriber subscriber = (Subscriber<T>) new Subscriber<T>() { // from class: rx.internal.operators.OperatorSampleWithObservable.2
            @Override // p045rx.Observer
            public void onNext(T t) {
                value.set(t);
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                s.onError(e);
                samplerSub.unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                samplerSub.onNext(null);
                s.onCompleted();
                samplerSub.unsubscribe();
            }
        };
        main.lazySet(subscriber);
        child.add(subscriber);
        child.add(samplerSub);
        this.sampler.unsafeSubscribe(samplerSub);
        return subscriber;
    }
}
