package p045rx.internal.util;

import p045rx.Observer;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public final class ObserverSubscriber<T> extends Subscriber<T> {
    final Observer<? super T> observer;

    public ObserverSubscriber(Observer<? super T> observer) {
        this.observer = observer;
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.observer.onNext(t);
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.observer.onError(e);
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.observer.onCompleted();
    }
}
