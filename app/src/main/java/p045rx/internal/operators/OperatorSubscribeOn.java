package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Producer;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.functions.Action0;

/* loaded from: classes.dex */
public final class OperatorSubscribeOn<T> implements Observable.OnSubscribe<T> {
    final Scheduler scheduler;
    final Observable<T> source;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OperatorSubscribeOn(Observable<T> source, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.source = source;
    }

    public void call(Subscriber<? super T> subscriber) {
        Scheduler.Worker inner = this.scheduler.createWorker();
        subscriber.add(inner);
        inner.schedule(new C11001(subscriber, inner));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: rx.internal.operators.OperatorSubscribeOn$1 */
    /* loaded from: classes.dex */
    public class C11001 implements Action0 {
        final /* synthetic */ Scheduler.Worker val$inner;
        final /* synthetic */ Subscriber val$subscriber;

        C11001(Subscriber subscriber, Scheduler.Worker worker) {
            this.val$subscriber = subscriber;
            this.val$inner = worker;
        }

        @Override // p045rx.functions.Action0
        public void call() {
            final Thread t = Thread.currentThread();
            Subscriber<T> s = new Subscriber<T>(this.val$subscriber) { // from class: rx.internal.operators.OperatorSubscribeOn.1.1
                @Override // p045rx.Observer
                public void onNext(T t2) {
                    C11001.this.val$subscriber.onNext(t2);
                }

                @Override // p045rx.Observer
                public void onError(Throwable e) {
                    try {
                        C11001.this.val$subscriber.onError(e);
                    } finally {
                        C11001.this.val$inner.unsubscribe();
                    }
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    try {
                        C11001.this.val$subscriber.onCompleted();
                    } finally {
                        C11001.this.val$inner.unsubscribe();
                    }
                }

                @Override // p045rx.Subscriber
                public void setProducer(final Producer p) {
                    C11001.this.val$subscriber.setProducer(new Producer() { // from class: rx.internal.operators.OperatorSubscribeOn.1.1.1
                        @Override // p045rx.Producer
                        public void request(final long n) {
                            if (t == Thread.currentThread()) {
                                p.request(n);
                            } else {
                                C11001.this.val$inner.schedule(new Action0() { // from class: rx.internal.operators.OperatorSubscribeOn.1.1.1.1
                                    @Override // p045rx.functions.Action0
                                    public void call() {
                                        p.request(n);
                                    }
                                });
                            }
                        }
                    });
                }
            };
            OperatorSubscribeOn.this.source.unsafeSubscribe(s);
        }
    }
}
