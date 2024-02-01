package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OnSubscribeLift<T, R> implements Observable.OnSubscribe<R> {
    final Observable.Operator<? extends R, ? super T> operator;
    final Observable.OnSubscribe<T> parent;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeLift(Observable.OnSubscribe<T> parent, Observable.Operator<? extends R, ? super T> operator) {
        this.parent = parent;
        this.operator = operator;
    }

    public void call(Subscriber<? super R> o) {
        try {
            Subscriber<? super T> st = RxJavaHooks.onObservableLift(this.operator).call(o);
            try {
                st.onStart();
                this.parent.call(st);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                st.onError(e);
            }
        } catch (Throwable e2) {
            Exceptions.throwIfFatal(e2);
            o.onError(e2);
        }
    }
}
