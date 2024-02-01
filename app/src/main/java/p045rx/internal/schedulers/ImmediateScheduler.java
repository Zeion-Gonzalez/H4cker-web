package p045rx.internal.schedulers;

import java.util.concurrent.TimeUnit;
import p045rx.Scheduler;
import p045rx.Subscription;
import p045rx.functions.Action0;
import p045rx.subscriptions.BooleanSubscription;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
public final class ImmediateScheduler extends Scheduler {
    public static final ImmediateScheduler INSTANCE = new ImmediateScheduler();

    private ImmediateScheduler() {
    }

    @Override // p045rx.Scheduler
    public Scheduler.Worker createWorker() {
        return new InnerImmediateScheduler();
    }

    /* loaded from: classes.dex */
    private class InnerImmediateScheduler extends Scheduler.Worker implements Subscription {
        final BooleanSubscription innerSubscription;

        private InnerImmediateScheduler() {
            this.innerSubscription = new BooleanSubscription();
        }

        @Override // rx.Scheduler.Worker
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            long execTime = ImmediateScheduler.this.now() + unit.toMillis(delayTime);
            return schedule(new SleepingAction(action, this, execTime));
        }

        @Override // rx.Scheduler.Worker
        public Subscription schedule(Action0 action) {
            action.call();
            return Subscriptions.unsubscribed();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.innerSubscription.unsubscribe();
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.innerSubscription.isUnsubscribed();
        }
    }
}
