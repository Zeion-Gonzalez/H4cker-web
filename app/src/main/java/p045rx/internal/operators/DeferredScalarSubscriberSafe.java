package p045rx.internal.operators;

import p045rx.Subscriber;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public abstract class DeferredScalarSubscriberSafe<T, R> extends DeferredScalarSubscriber<T, R> {
    protected boolean done;

    public DeferredScalarSubscriberSafe(Subscriber<? super R> actual) {
        super(actual);
    }

    @Override // p045rx.internal.operators.DeferredScalarSubscriber, p045rx.Observer
    public void onError(Throwable ex) {
        if (!this.done) {
            this.done = true;
            super.onError(ex);
        } else {
            RxJavaHooks.onError(ex);
        }
    }

    @Override // p045rx.internal.operators.DeferredScalarSubscriber, p045rx.Observer
    public void onCompleted() {
        if (!this.done) {
            this.done = true;
            super.onCompleted();
        }
    }
}
