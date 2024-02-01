package p045rx.internal.operators;

import java.util.concurrent.TimeUnit;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.functions.Action0;
import p045rx.internal.operators.OperatorTimeoutBase;

/* loaded from: classes.dex */
public final class OperatorTimeout<T> extends OperatorTimeoutBase<T> {
    @Override // p045rx.internal.operators.OperatorTimeoutBase
    public /* bridge */ /* synthetic */ Subscriber call(Subscriber x0) {
        return super.call(x0);
    }

    public OperatorTimeout(final long timeout, final TimeUnit timeUnit, Observable<? extends T> other, Scheduler scheduler) {
        super(new OperatorTimeoutBase.FirstTimeoutStub<T>() { // from class: rx.internal.operators.OperatorTimeout.1
            @Override // p045rx.functions.Func3
            public /* bridge */ /* synthetic */ Subscription call(Object x0, Long l, Scheduler.Worker worker) {
                return call((OperatorTimeoutBase.TimeoutSubscriber) ((OperatorTimeoutBase.TimeoutSubscriber) x0), l, worker);
            }

            public Subscription call(final OperatorTimeoutBase.TimeoutSubscriber<T> timeoutSubscriber, final Long seqId, Scheduler.Worker inner) {
                return inner.schedule(new Action0() { // from class: rx.internal.operators.OperatorTimeout.1.1
                    @Override // p045rx.functions.Action0
                    public void call() {
                        timeoutSubscriber.onTimeout(seqId.longValue());
                    }
                }, timeout, timeUnit);
            }
        }, new OperatorTimeoutBase.TimeoutStub<T>() { // from class: rx.internal.operators.OperatorTimeout.2
            @Override // p045rx.functions.Func4
            public /* bridge */ /* synthetic */ Subscription call(Object x0, Long l, Object x2, Scheduler.Worker worker) {
                return call((OperatorTimeoutBase.TimeoutSubscriber<Long>) x0, l, (Long) x2, worker);
            }

            public Subscription call(final OperatorTimeoutBase.TimeoutSubscriber<T> timeoutSubscriber, final Long seqId, T value, Scheduler.Worker inner) {
                return inner.schedule(new Action0() { // from class: rx.internal.operators.OperatorTimeout.2.1
                    @Override // p045rx.functions.Action0
                    public void call() {
                        timeoutSubscriber.onTimeout(seqId.longValue());
                    }
                }, timeout, timeUnit);
            }
        }, other, scheduler);
    }
}
