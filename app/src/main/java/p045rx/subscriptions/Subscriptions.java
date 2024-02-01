package p045rx.subscriptions;

import java.util.concurrent.Future;
import p045rx.Subscription;
import p045rx.functions.Action0;

/* loaded from: classes.dex */
public final class Subscriptions {
    private static final Unsubscribed UNSUBSCRIBED = new Unsubscribed();

    private Subscriptions() {
        throw new IllegalStateException("No instances!");
    }

    public static Subscription empty() {
        return BooleanSubscription.create();
    }

    public static Subscription unsubscribed() {
        return UNSUBSCRIBED;
    }

    public static Subscription create(Action0 unsubscribe) {
        return BooleanSubscription.create(unsubscribe);
    }

    public static Subscription from(Future<?> f) {
        return new FutureSubscription(f);
    }

    /* loaded from: classes.dex */
    static final class FutureSubscription implements Subscription {

        /* renamed from: f */
        final Future<?> f1645f;

        public FutureSubscription(Future<?> f) {
            this.f1645f = f;
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.f1645f.cancel(true);
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.f1645f.isCancelled();
        }
    }

    public static CompositeSubscription from(Subscription... subscriptions) {
        return new CompositeSubscription(subscriptions);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Unsubscribed implements Subscription {
        Unsubscribed() {
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return true;
        }
    }
}
