package p045rx.internal.schedulers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Completable;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Scheduler;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.functions.Action0;
import p045rx.functions.Func1;
import p045rx.internal.operators.BufferUntilSubscriber;
import p045rx.observers.SerializedObserver;
import p045rx.subjects.PublishSubject;
import p045rx.subscriptions.Subscriptions;

@Experimental
/* loaded from: classes.dex */
public class SchedulerWhen extends Scheduler implements Subscription {
    private static final Subscription SUBSCRIBED = new Subscription() { // from class: rx.internal.schedulers.SchedulerWhen.3
        @Override // p045rx.Subscription
        public void unsubscribe() {
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return false;
        }
    };
    private static final Subscription UNSUBSCRIBED = Subscriptions.unsubscribed();
    private final Scheduler actualScheduler;
    private final Subscription subscription;
    private final Observer<Observable<Completable>> workerObserver;

    public SchedulerWhen(Func1<Observable<Observable<Completable>>, Completable> combine, Scheduler actualScheduler) {
        this.actualScheduler = actualScheduler;
        PublishSubject<Observable<Completable>> workerSubject = PublishSubject.create();
        this.workerObserver = new SerializedObserver(workerSubject);
        this.subscription = combine.call(workerSubject.onBackpressureBuffer()).subscribe();
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
        this.subscription.unsubscribe();
    }

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return this.subscription.isUnsubscribed();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // p045rx.Scheduler
    public Scheduler.Worker createWorker() {
        final Scheduler.Worker actualWorker = this.actualScheduler.createWorker();
        BufferUntilSubscriber<ScheduledAction> actionSubject = BufferUntilSubscriber.create();
        final Observer<ScheduledAction> actionObserver = new SerializedObserver<>(actionSubject);
        Object map = actionSubject.map(new Func1<ScheduledAction, Completable>() { // from class: rx.internal.schedulers.SchedulerWhen.1
            @Override // p045rx.functions.Func1
            public Completable call(final ScheduledAction action) {
                return Completable.create(new Completable.CompletableOnSubscribe() { // from class: rx.internal.schedulers.SchedulerWhen.1.1
                    @Override // p045rx.functions.Action1
                    public void call(Completable.CompletableSubscriber actionCompletable) {
                        actionCompletable.onSubscribe(action);
                        action.call(actualWorker);
                        actionCompletable.onCompleted();
                    }
                });
            }
        });
        Scheduler.Worker worker = new Scheduler.Worker() { // from class: rx.internal.schedulers.SchedulerWhen.2
            private final AtomicBoolean unsubscribed = new AtomicBoolean();

            @Override // p045rx.Subscription
            public void unsubscribe() {
                if (this.unsubscribed.compareAndSet(false, true)) {
                    actualWorker.unsubscribe();
                    actionObserver.onCompleted();
                }
            }

            @Override // p045rx.Subscription
            public boolean isUnsubscribed() {
                return this.unsubscribed.get();
            }

            @Override // rx.Scheduler.Worker
            public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
                DelayedAction delayedAction = new DelayedAction(action, delayTime, unit);
                actionObserver.onNext(delayedAction);
                return delayedAction;
            }

            @Override // rx.Scheduler.Worker
            public Subscription schedule(Action0 action) {
                ImmediateAction immediateAction = new ImmediateAction(action);
                actionObserver.onNext(immediateAction);
                return immediateAction;
            }
        };
        this.workerObserver.onNext(map);
        return worker;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class ScheduledAction extends AtomicReference<Subscription> implements Subscription {
        protected abstract Subscription callActual(Scheduler.Worker worker);

        public ScheduledAction() {
            super(SchedulerWhen.SUBSCRIBED);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void call(Scheduler.Worker actualWorker) {
            Subscription oldState = get();
            if (oldState != SchedulerWhen.UNSUBSCRIBED && oldState == SchedulerWhen.SUBSCRIBED) {
                Subscription newState = callActual(actualWorker);
                if (!compareAndSet(SchedulerWhen.SUBSCRIBED, newState)) {
                    newState.unsubscribe();
                }
            }
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return get().isUnsubscribed();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            Subscription oldState;
            Subscription newState = SchedulerWhen.UNSUBSCRIBED;
            do {
                oldState = get();
                if (oldState == SchedulerWhen.UNSUBSCRIBED) {
                    return;
                }
            } while (!compareAndSet(oldState, newState));
            if (oldState != SchedulerWhen.SUBSCRIBED) {
                oldState.unsubscribe();
            }
        }
    }

    /* loaded from: classes.dex */
    private static class ImmediateAction extends ScheduledAction {
        private final Action0 action;

        public ImmediateAction(Action0 action) {
            this.action = action;
        }

        @Override // rx.internal.schedulers.SchedulerWhen.ScheduledAction
        protected Subscription callActual(Scheduler.Worker actualWorker) {
            return actualWorker.schedule(this.action);
        }
    }

    /* loaded from: classes.dex */
    private static class DelayedAction extends ScheduledAction {
        private final Action0 action;
        private final long delayTime;
        private final TimeUnit unit;

        public DelayedAction(Action0 action, long delayTime, TimeUnit unit) {
            this.action = action;
            this.delayTime = delayTime;
            this.unit = unit;
        }

        @Override // rx.internal.schedulers.SchedulerWhen.ScheduledAction
        protected Subscription callActual(Scheduler.Worker actualWorker) {
            return actualWorker.schedule(this.action, this.delayTime, this.unit);
        }
    }
}
