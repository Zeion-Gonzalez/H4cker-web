package p045rx.internal.operators;

import java.util.concurrent.TimeUnit;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;

/* loaded from: classes.dex */
public final class OnSubscribeTimerPeriodically implements Observable.OnSubscribe<Long> {
    final long initialDelay;
    final long period;
    final Scheduler scheduler;
    final TimeUnit unit;

    public OnSubscribeTimerPeriodically(long initialDelay, long period, TimeUnit unit, Scheduler scheduler) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override // p045rx.functions.Action1
    public void call(final Subscriber<? super Long> child) {
        final Scheduler.Worker worker = this.scheduler.createWorker();
        child.add(worker);
        worker.schedulePeriodically(new Action0() { // from class: rx.internal.operators.OnSubscribeTimerPeriodically.1
            long counter;

            @Override // p045rx.functions.Action0
            public void call() {
                try {
                    Subscriber subscriber = child;
                    long j = this.counter;
                    this.counter = 1 + j;
                    subscriber.onNext(Long.valueOf(j));
                } catch (Throwable e) {
                    try {
                        worker.unsubscribe();
                    } finally {
                        Exceptions.throwOrReport(e, child);
                    }
                }
            }
        }, this.initialDelay, this.period, this.unit);
    }
}
