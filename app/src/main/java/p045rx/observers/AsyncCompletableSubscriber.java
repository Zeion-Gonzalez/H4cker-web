package p045rx.observers;

import java.util.concurrent.atomic.AtomicReference;
import p045rx.Completable;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.plugins.RxJavaHooks;

@Experimental
/* loaded from: classes.dex */
public abstract class AsyncCompletableSubscriber implements Completable.CompletableSubscriber, Subscription {
    static final Unsubscribed UNSUBSCRIBED = new Unsubscribed();
    private final AtomicReference<Subscription> upstream = new AtomicReference<>();

    @Override // rx.Completable.CompletableSubscriber
    public final void onSubscribe(Subscription d) {
        if (!this.upstream.compareAndSet(null, d)) {
            d.unsubscribe();
            if (this.upstream.get() != UNSUBSCRIBED) {
                RxJavaHooks.onError(new IllegalStateException("Subscription already set!"));
                return;
            }
            return;
        }
        onStart();
    }

    protected void onStart() {
    }

    @Override // p045rx.Subscription
    public final boolean isUnsubscribed() {
        return this.upstream.get() == UNSUBSCRIBED;
    }

    protected final void clear() {
        this.upstream.set(UNSUBSCRIBED);
    }

    @Override // p045rx.Subscription
    public final void unsubscribe() {
        Subscription current = this.upstream.get();
        if (current != UNSUBSCRIBED) {
            Subscription current2 = this.upstream.getAndSet(UNSUBSCRIBED);
            Subscription current3 = current2;
            if (current3 != null && current3 != UNSUBSCRIBED) {
                current3.unsubscribe();
            }
        }
    }

    /* loaded from: classes.dex */
    static final class Unsubscribed implements Subscription {
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
