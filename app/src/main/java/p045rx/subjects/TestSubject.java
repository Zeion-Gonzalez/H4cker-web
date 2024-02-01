package p045rx.subjects;

import java.util.concurrent.TimeUnit;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Scheduler;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.internal.operators.NotificationLite;
import p045rx.schedulers.TestScheduler;
import p045rx.subjects.SubjectSubscriptionManager;

/* loaded from: classes.dex */
public final class TestSubject<T> extends Subject<T, T> {
    private final Scheduler.Worker innerScheduler;
    private final SubjectSubscriptionManager<T> state;

    public static <T> TestSubject<T> create(TestScheduler scheduler) {
        final SubjectSubscriptionManager<T> state = new SubjectSubscriptionManager<>();
        state.onAdded = new Action1<SubjectSubscriptionManager.SubjectObserver<T>>() { // from class: rx.subjects.TestSubject.1
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SubjectSubscriptionManager.SubjectObserver) ((SubjectSubscriptionManager.SubjectObserver) x0));
            }

            public void call(SubjectSubscriptionManager.SubjectObserver<T> o) {
                o.emitFirst(SubjectSubscriptionManager.this.getLatest(), SubjectSubscriptionManager.this.f1643nl);
            }
        };
        state.onTerminated = state.onAdded;
        return new TestSubject<>(state, state, scheduler);
    }

    protected TestSubject(Observable.OnSubscribe<T> onSubscribe, SubjectSubscriptionManager<T> state, TestScheduler scheduler) {
        super(onSubscribe);
        this.state = state;
        this.innerScheduler = scheduler.createWorker();
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        onCompleted(0L);
    }

    void internalOnCompleted() {
        if (this.state.active) {
            SubjectSubscriptionManager.SubjectObserver<T>[] arr$ = this.state.terminate(NotificationLite.instance().completed());
            for (SubjectSubscriptionManager.SubjectObserver<T> bo : arr$) {
                bo.onCompleted();
            }
        }
    }

    public void onCompleted(long delayTime) {
        this.innerScheduler.schedule(new Action0() { // from class: rx.subjects.TestSubject.2
            @Override // p045rx.functions.Action0
            public void call() {
                TestSubject.this.internalOnCompleted();
            }
        }, delayTime, TimeUnit.MILLISECONDS);
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        onError(e, 0L);
    }

    void internalOnError(Throwable e) {
        if (this.state.active) {
            SubjectSubscriptionManager.SubjectObserver<T>[] arr$ = this.state.terminate(NotificationLite.instance().error(e));
            for (SubjectSubscriptionManager.SubjectObserver<T> bo : arr$) {
                bo.onError(e);
            }
        }
    }

    public void onError(final Throwable e, long delayTime) {
        this.innerScheduler.schedule(new Action0() { // from class: rx.subjects.TestSubject.3
            @Override // p045rx.functions.Action0
            public void call() {
                TestSubject.this.internalOnError(e);
            }
        }, delayTime, TimeUnit.MILLISECONDS);
    }

    @Override // p045rx.Observer
    public void onNext(T v) {
        onNext(v, 0L);
    }

    void internalOnNext(T v) {
        Observer<? super T>[] arr$ = this.state.observers();
        for (Observer<? super T> o : arr$) {
            o.onNext(v);
        }
    }

    public void onNext(final T v, long delayTime) {
        this.innerScheduler.schedule(new Action0() { // from class: rx.subjects.TestSubject.4
            /* JADX WARN: Multi-variable type inference failed */
            @Override // p045rx.functions.Action0
            public void call() {
                TestSubject.this.internalOnNext(v);
            }
        }, delayTime, TimeUnit.MILLISECONDS);
    }

    @Override // p045rx.subjects.Subject
    public boolean hasObservers() {
        return this.state.observers().length > 0;
    }
}
