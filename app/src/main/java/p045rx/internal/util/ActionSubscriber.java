package p045rx.internal.util;

import p045rx.Subscriber;
import p045rx.functions.Action0;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public final class ActionSubscriber<T> extends Subscriber<T> {
    final Action0 onCompleted;
    final Action1<Throwable> onError;
    final Action1<? super T> onNext;

    public ActionSubscriber(Action1<? super T> onNext, Action1<Throwable> onError, Action0 onCompleted) {
        this.onNext = onNext;
        this.onError = onError;
        this.onCompleted = onCompleted;
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.onNext.call(t);
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.onError.call(e);
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.onCompleted.call();
    }
}
