package p045rx.subscriptions;

import p045rx.Subscription;
import p045rx.internal.subscriptions.SequentialSubscription;

/* loaded from: classes.dex */
public final class MultipleAssignmentSubscription implements Subscription {
    final SequentialSubscription state = new SequentialSubscription();

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return this.state.isUnsubscribed();
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
        this.state.unsubscribe();
    }

    public void set(Subscription s) {
        if (s == null) {
            throw new IllegalArgumentException("Subscription can not be null");
        }
        this.state.replace(s);
    }

    public Subscription get() {
        return this.state.current();
    }
}
