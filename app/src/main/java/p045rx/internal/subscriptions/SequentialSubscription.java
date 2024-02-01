package p045rx.internal.subscriptions;

import java.util.concurrent.atomic.AtomicReference;
import p045rx.Subscription;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
public final class SequentialSubscription extends AtomicReference<Subscription> implements Subscription {
    private static final long serialVersionUID = 995205034283130269L;

    public SequentialSubscription() {
    }

    public SequentialSubscription(Subscription initial) {
        lazySet(initial);
    }

    public Subscription current() {
        Subscription current = (Subscription) super.get();
        if (current == Unsubscribed.INSTANCE) {
            return Subscriptions.unsubscribed();
        }
        return current;
    }

    public boolean update(Subscription next) {
        Subscription current;
        do {
            current = get();
            if (current == Unsubscribed.INSTANCE) {
                if (next != null) {
                    next.unsubscribe();
                }
                return false;
            }
        } while (!compareAndSet(current, next));
        if (current != null) {
            current.unsubscribe();
        }
        return true;
    }

    public boolean replace(Subscription next) {
        Subscription current;
        do {
            current = get();
            if (current == Unsubscribed.INSTANCE) {
                if (next != null) {
                    next.unsubscribe();
                }
                return false;
            }
        } while (!compareAndSet(current, next));
        return true;
    }

    public boolean updateWeak(Subscription next) {
        Subscription current = get();
        if (current == Unsubscribed.INSTANCE) {
            if (next != null) {
                next.unsubscribe();
            }
            return false;
        }
        if (compareAndSet(current, next)) {
            return true;
        }
        Subscription current2 = get();
        if (next != null) {
            next.unsubscribe();
        }
        return current2 == Unsubscribed.INSTANCE;
    }

    public boolean replaceWeak(Subscription next) {
        Subscription current = get();
        if (current == Unsubscribed.INSTANCE) {
            if (next == null) {
                return false;
            }
            next.unsubscribe();
            return false;
        }
        if (!compareAndSet(current, next) && get() == Unsubscribed.INSTANCE) {
            if (next == null) {
                return false;
            }
            next.unsubscribe();
            return false;
        }
        return true;
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
        Subscription current = get();
        if (current != Unsubscribed.INSTANCE) {
            Subscription current2 = getAndSet(Unsubscribed.INSTANCE);
            Subscription current3 = current2;
            if (current3 != null && current3 != Unsubscribed.INSTANCE) {
                current3.unsubscribe();
            }
        }
    }

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return get() == Unsubscribed.INSTANCE;
    }
}
