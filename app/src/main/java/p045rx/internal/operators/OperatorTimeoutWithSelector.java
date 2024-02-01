package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.internal.operators.OperatorTimeoutBase;
import p045rx.schedulers.Schedulers;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
public class OperatorTimeoutWithSelector<T, U, V> extends OperatorTimeoutBase<T> {
    @Override // p045rx.internal.operators.OperatorTimeoutBase
    public /* bridge */ /* synthetic */ Subscriber call(Subscriber x0) {
        return super.call(x0);
    }

    public OperatorTimeoutWithSelector(final Func0<? extends Observable<U>> firstTimeoutSelector, final Func1<? super T, ? extends Observable<V>> timeoutSelector, Observable<? extends T> other) {
        super(new OperatorTimeoutBase.FirstTimeoutStub<T>() { // from class: rx.internal.operators.OperatorTimeoutWithSelector.1
            @Override // p045rx.functions.Func3
            public /* bridge */ /* synthetic */ Subscription call(Object x0, Long l, Scheduler.Worker worker) {
                return call((OperatorTimeoutBase.TimeoutSubscriber) ((OperatorTimeoutBase.TimeoutSubscriber) x0), l, worker);
            }

            public Subscription call(final OperatorTimeoutBase.TimeoutSubscriber<T> timeoutSubscriber, final Long seqId, Scheduler.Worker inner) {
                if (Func0.this != null) {
                    try {
                        Observable<U> o = (Observable) Func0.this.call();
                        return o.unsafeSubscribe(new Subscriber<U>() { // from class: rx.internal.operators.OperatorTimeoutWithSelector.1.1
                            @Override // p045rx.Observer
                            public void onCompleted() {
                                timeoutSubscriber.onTimeout(seqId.longValue());
                            }

                            @Override // p045rx.Observer
                            public void onError(Throwable e) {
                                timeoutSubscriber.onError(e);
                            }

                            @Override // p045rx.Observer
                            public void onNext(U t) {
                                timeoutSubscriber.onTimeout(seqId.longValue());
                            }
                        });
                    } catch (Throwable t) {
                        Exceptions.throwOrReport(t, timeoutSubscriber);
                        return Subscriptions.unsubscribed();
                    }
                }
                return Subscriptions.unsubscribed();
            }
        }, new OperatorTimeoutBase.TimeoutStub<T>() { // from class: rx.internal.operators.OperatorTimeoutWithSelector.2
            @Override // p045rx.functions.Func4
            public /* bridge */ /* synthetic */ Subscription call(Object x0, Long l, Object x2, Scheduler.Worker worker) {
                return call((OperatorTimeoutBase.TimeoutSubscriber<Long>) x0, l, (Long) x2, worker);
            }

            public Subscription call(final OperatorTimeoutBase.TimeoutSubscriber<T> timeoutSubscriber, final Long seqId, T value, Scheduler.Worker inner) {
                try {
                    Observable<V> o = (Observable) Func1.this.call(value);
                    return o.unsafeSubscribe(new Subscriber<V>() { // from class: rx.internal.operators.OperatorTimeoutWithSelector.2.1
                        @Override // p045rx.Observer
                        public void onCompleted() {
                            timeoutSubscriber.onTimeout(seqId.longValue());
                        }

                        @Override // p045rx.Observer
                        public void onError(Throwable e) {
                            timeoutSubscriber.onError(e);
                        }

                        @Override // p045rx.Observer
                        public void onNext(V t) {
                            timeoutSubscriber.onTimeout(seqId.longValue());
                        }
                    });
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, timeoutSubscriber);
                    return Subscriptions.unsubscribed();
                }
            }
        }, other, Schedulers.immediate());
    }
}
