package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public final class OnSubscribeThrow<T> implements Observable.OnSubscribe<T> {
    private final Throwable exception;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeThrow(Throwable exception) {
        this.exception = exception;
    }

    public void call(Subscriber<? super T> observer) {
        observer.onError(this.exception);
    }
}
