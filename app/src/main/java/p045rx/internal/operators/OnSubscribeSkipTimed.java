package p045rx.internal.operators;

import java.util.concurrent.TimeUnit;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.functions.Action0;

/* loaded from: classes.dex */
public final class OnSubscribeSkipTimed<T> implements Observable.OnSubscribe<T> {
    final Scheduler scheduler;
    final Observable<T> source;
    final long time;
    final TimeUnit unit;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeSkipTimed(Observable<T> source, long time, TimeUnit unit, Scheduler scheduler) {
        this.source = source;
        this.time = time;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    public void call(Subscriber<? super T> child) {
        Scheduler.Worker worker = this.scheduler.createWorker();
        SkipTimedSubscriber<T> subscriber = new SkipTimedSubscriber<>(child);
        subscriber.add(worker);
        child.add(subscriber);
        worker.schedule(subscriber, this.time, this.unit);
        this.source.unsafeSubscribe(subscriber);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SkipTimedSubscriber<T> extends Subscriber<T> implements Action0 {
        final Subscriber<? super T> child;
        volatile boolean gate;

        SkipTimedSubscriber(Subscriber<? super T> child) {
            this.child = child;
        }

        @Override // p045rx.functions.Action0
        public void call() {
            this.gate = true;
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            if (this.gate) {
                this.child.onNext(t);
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            try {
                this.child.onError(e);
            } finally {
                unsubscribe();
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            try {
                this.child.onCompleted();
            } finally {
                unsubscribe();
            }
        }
    }
}
