package p045rx.internal.schedulers;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Subscription;
import p045rx.exceptions.OnErrorNotImplementedException;
import p045rx.functions.Action0;
import p045rx.internal.util.SubscriptionList;
import p045rx.plugins.RxJavaHooks;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public final class ScheduledAction extends AtomicReference<Thread> implements Runnable, Subscription {
    private static final long serialVersionUID = -3962399486978279857L;
    final Action0 action;
    final SubscriptionList cancel;

    public ScheduledAction(Action0 action) {
        this.action = action;
        this.cancel = new SubscriptionList();
    }

    public ScheduledAction(Action0 action, CompositeSubscription parent) {
        this.action = action;
        this.cancel = new SubscriptionList(new Remover(this, parent));
    }

    public ScheduledAction(Action0 action, SubscriptionList parent) {
        this.action = action;
        this.cancel = new SubscriptionList(new Remover2(this, parent));
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                lazySet(Thread.currentThread());
                this.action.call();
                unsubscribe();
            } catch (OnErrorNotImplementedException e) {
                signalError(new IllegalStateException("Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e));
                unsubscribe();
            } catch (Throwable e2) {
                signalError(new IllegalStateException("Fatal Exception thrown on Scheduler.Worker thread.", e2));
                unsubscribe();
            }
        } catch (Throwable th) {
            unsubscribe();
            throw th;
        }
    }

    void signalError(Throwable ie) {
        RxJavaHooks.onError(ie);
        Thread thread = Thread.currentThread();
        thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
    }

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return this.cancel.isUnsubscribed();
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
        if (!this.cancel.isUnsubscribed()) {
            this.cancel.unsubscribe();
        }
    }

    public void add(Subscription s) {
        this.cancel.add(s);
    }

    public void add(Future<?> f) {
        this.cancel.add(new FutureCompleter(f));
    }

    public void addParent(CompositeSubscription parent) {
        this.cancel.add(new Remover(this, parent));
    }

    public void addParent(SubscriptionList parent) {
        this.cancel.add(new Remover2(this, parent));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class FutureCompleter implements Subscription {

        /* renamed from: f */
        private final Future<?> f1620f;

        FutureCompleter(Future<?> f) {
            this.f1620f = f;
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            if (ScheduledAction.this.get() != Thread.currentThread()) {
                this.f1620f.cancel(true);
            } else {
                this.f1620f.cancel(false);
            }
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.f1620f.isCancelled();
        }
    }

    /* loaded from: classes.dex */
    static final class Remover extends AtomicBoolean implements Subscription {
        private static final long serialVersionUID = 247232374289553518L;
        final CompositeSubscription parent;

        /* renamed from: s */
        final ScheduledAction f1621s;

        public Remover(ScheduledAction s, CompositeSubscription parent) {
            this.f1621s = s;
            this.parent = parent;
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.f1621s.isUnsubscribed();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            if (compareAndSet(false, true)) {
                this.parent.remove(this.f1621s);
            }
        }
    }

    /* loaded from: classes.dex */
    static final class Remover2 extends AtomicBoolean implements Subscription {
        private static final long serialVersionUID = 247232374289553518L;
        final SubscriptionList parent;

        /* renamed from: s */
        final ScheduledAction f1622s;

        public Remover2(ScheduledAction s, SubscriptionList parent) {
            this.f1622s = s;
            this.parent = parent;
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.f1622s.isUnsubscribed();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            if (compareAndSet(false, true)) {
                this.parent.remove(this.f1622s);
            }
        }
    }
}
