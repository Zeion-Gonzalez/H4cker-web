package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public enum NeverObservableHolder implements Observable.OnSubscribe<Object> {
    INSTANCE;

    static final Observable<Object> NEVER = Observable.create(INSTANCE);

    public static <T> Observable<T> instance() {
        return (Observable<T>) NEVER;
    }

    @Override // p045rx.functions.Action1
    public void call(Subscriber<? super Object> child) {
    }
}
