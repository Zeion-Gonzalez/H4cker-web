package p045rx;

import p045rx.annotations.Beta;
import p045rx.internal.util.SubscriptionList;

@Beta
/* loaded from: classes.dex */
public abstract class SingleSubscriber<T> implements Subscription {

    /* renamed from: cs */
    private final SubscriptionList f1580cs = new SubscriptionList();

    public abstract void onError(Throwable th);

    public abstract void onSuccess(T t);

    public final void add(Subscription s) {
        this.f1580cs.add(s);
    }

    @Override // p045rx.Subscription
    public final void unsubscribe() {
        this.f1580cs.unsubscribe();
    }

    @Override // p045rx.Subscription
    public final boolean isUnsubscribed() {
        return this.f1580cs.isUnsubscribed();
    }
}
