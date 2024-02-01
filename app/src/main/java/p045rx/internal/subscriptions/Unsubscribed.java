package p045rx.internal.subscriptions;

import p045rx.Subscription;

/* loaded from: classes.dex */
public enum Unsubscribed implements Subscription {
    INSTANCE;

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return true;
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
    }
}
