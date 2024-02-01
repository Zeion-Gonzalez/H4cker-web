package p045rx.internal.util;

import p045rx.Scheduler;
import p045rx.Single;
import p045rx.SingleSubscriber;
import p045rx.Subscriber;
import p045rx.functions.Action0;
import p045rx.functions.Func1;
import p045rx.internal.schedulers.EventLoopsScheduler;

/* loaded from: classes.dex */
public final class ScalarSynchronousSingle<T> extends Single<T> {
    final T value;

    public static <T> ScalarSynchronousSingle<T> create(T t) {
        return new ScalarSynchronousSingle<>(t);
    }

    protected ScalarSynchronousSingle(final T t) {
        super(new Single.OnSubscribe<T>() { // from class: rx.internal.util.ScalarSynchronousSingle.1
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(SingleSubscriber<? super T> te) {
                te.onSuccess((Object) t);
            }
        });
        this.value = t;
    }

    public T get() {
        return this.value;
    }

    public Single<T> scalarScheduleOn(Scheduler scheduler) {
        if (!(scheduler instanceof EventLoopsScheduler)) {
            return create((Single.OnSubscribe) new NormalScheduledEmission(scheduler, this.value));
        }
        EventLoopsScheduler es = (EventLoopsScheduler) scheduler;
        return create((Single.OnSubscribe) new DirectScheduledEmission(es, this.value));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DirectScheduledEmission<T> implements Single.OnSubscribe<T> {

        /* renamed from: es */
        private final EventLoopsScheduler f1625es;
        private final T value;

        @Override // p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            call((SingleSubscriber) ((SingleSubscriber) x0));
        }

        DirectScheduledEmission(EventLoopsScheduler es, T value) {
            this.f1625es = es;
            this.value = value;
        }

        public void call(SingleSubscriber<? super T> singleSubscriber) {
            singleSubscriber.add(this.f1625es.scheduleDirect(new ScalarSynchronousSingleAction(singleSubscriber, this.value)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class NormalScheduledEmission<T> implements Single.OnSubscribe<T> {
        private final Scheduler scheduler;
        private final T value;

        @Override // p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            call((SingleSubscriber) ((SingleSubscriber) x0));
        }

        NormalScheduledEmission(Scheduler scheduler, T value) {
            this.scheduler = scheduler;
            this.value = value;
        }

        public void call(SingleSubscriber<? super T> singleSubscriber) {
            Scheduler.Worker worker = this.scheduler.createWorker();
            singleSubscriber.add(worker);
            worker.schedule(new ScalarSynchronousSingleAction(singleSubscriber, this.value));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ScalarSynchronousSingleAction<T> implements Action0 {
        private final SingleSubscriber<? super T> subscriber;
        private final T value;

        ScalarSynchronousSingleAction(SingleSubscriber<? super T> subscriber, T value) {
            this.subscriber = subscriber;
            this.value = value;
        }

        @Override // p045rx.functions.Action0
        public void call() {
            try {
                this.subscriber.onSuccess((T) this.value);
            } catch (Throwable t) {
                this.subscriber.onError(t);
            }
        }
    }

    public <R> Single<R> scalarFlatMap(final Func1<? super T, ? extends Single<? extends R>> func) {
        return create((Single.OnSubscribe) new Single.OnSubscribe<R>() { // from class: rx.internal.util.ScalarSynchronousSingle.2
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(final SingleSubscriber<? super R> child) {
                Single<? extends R> o = (Single) func.call(ScalarSynchronousSingle.this.value);
                if (o instanceof ScalarSynchronousSingle) {
                    child.onSuccess((T) ((ScalarSynchronousSingle) o).value);
                    return;
                }
                Subscriber<R> subscriber = new Subscriber<R>() { // from class: rx.internal.util.ScalarSynchronousSingle.2.1
                    @Override // p045rx.Observer
                    public void onCompleted() {
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override // p045rx.Observer
                    public void onNext(R r) {
                        child.onSuccess(r);
                    }
                };
                child.add(subscriber);
                o.unsafeSubscribe(subscriber);
            }
        });
    }
}
