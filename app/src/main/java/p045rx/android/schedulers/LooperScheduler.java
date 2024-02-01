package p045rx.android.schedulers;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.TimeUnit;
import p045rx.Scheduler;
import p045rx.Subscription;
import p045rx.android.plugins.RxAndroidPlugins;
import p045rx.android.plugins.RxAndroidSchedulersHook;
import p045rx.exceptions.OnErrorNotImplementedException;
import p045rx.functions.Action0;
import p045rx.plugins.RxJavaPlugins;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
class LooperScheduler extends Scheduler {
    private final Handler handler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LooperScheduler(Looper looper) {
        this.handler = new Handler(looper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LooperScheduler(Handler handler) {
        this.handler = handler;
    }

    @Override // p045rx.Scheduler
    public Scheduler.Worker createWorker() {
        return new HandlerWorker(this.handler);
    }

    /* loaded from: classes.dex */
    static class HandlerWorker extends Scheduler.Worker {
        private final Handler handler;
        private final RxAndroidSchedulersHook hook = RxAndroidPlugins.getInstance().getSchedulersHook();
        private volatile boolean unsubscribed;

        HandlerWorker(Handler handler) {
            this.handler = handler;
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.unsubscribed = true;
            this.handler.removeCallbacksAndMessages(this);
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.unsubscribed;
        }

        @Override // rx.Scheduler.Worker
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (this.unsubscribed) {
                return Subscriptions.unsubscribed();
            }
            ScheduledAction scheduledAction = new ScheduledAction(this.hook.onSchedule(action), this.handler);
            Message message = Message.obtain(this.handler, scheduledAction);
            message.obj = this;
            this.handler.sendMessageDelayed(message, unit.toMillis(delayTime));
            if (this.unsubscribed) {
                this.handler.removeCallbacks(scheduledAction);
                return Subscriptions.unsubscribed();
            }
            return scheduledAction;
        }

        @Override // rx.Scheduler.Worker
        public Subscription schedule(Action0 action) {
            return schedule(action, 0L, TimeUnit.MILLISECONDS);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ScheduledAction implements Runnable, Subscription {
        private final Action0 action;
        private final Handler handler;
        private volatile boolean unsubscribed;

        ScheduledAction(Action0 action, Handler handler) {
            this.action = action;
            this.handler = handler;
        }

        @Override // java.lang.Runnable
        public void run() {
            IllegalStateException ie;
            try {
                this.action.call();
            } catch (Throwable e) {
                if (e instanceof OnErrorNotImplementedException) {
                    ie = new IllegalStateException("Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e);
                } else {
                    ie = new IllegalStateException("Fatal Exception thrown on Scheduler.Worker thread.", e);
                }
                RxJavaPlugins.getInstance().getErrorHandler().handleError(ie);
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
            }
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.unsubscribed = true;
            this.handler.removeCallbacks(this);
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.unsubscribed;
        }
    }
}
