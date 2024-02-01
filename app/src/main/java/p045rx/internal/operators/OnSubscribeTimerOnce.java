package p045rx.internal.operators;

import java.util.concurrent.TimeUnit;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;

/* loaded from: classes.dex */
public final class OnSubscribeTimerOnce implements Observable.OnSubscribe<Long> {
    final Scheduler scheduler;
    final long time;
    final TimeUnit unit;

    public OnSubscribeTimerOnce(long time, TimeUnit unit, Scheduler scheduler) {
        this.time = time;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override // p045rx.functions.Action1
    public void call(final Subscriber<? super Long> child) {
        Scheduler.Worker worker = this.scheduler.createWorker();
        child.add(worker);
        worker.schedule(new Action0() { // from class: rx.internal.operators.OnSubscribeTimerOnce.1
            @Override // p045rx.functions.Action0
            public void call() {
                try {
                    child.onNext(0L);
                    child.onCompleted();
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, child);
                }
            }
        }, this.time, this.unit);
    }
}
