package p045rx.internal.operators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.schedulers.Timestamped;

/* loaded from: classes.dex */
public class OperatorSkipLastTimed<T> implements Observable.Operator<T, T> {
    final Scheduler scheduler;
    final long timeInMillis;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorSkipLastTimed(long time, TimeUnit unit, Scheduler scheduler) {
        this.timeInMillis = unit.toMillis(time);
        this.scheduler = scheduler;
    }

    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
        return (Subscriber<T>) new Subscriber<T>(subscriber) { // from class: rx.internal.operators.OperatorSkipLastTimed.1
            private Deque<Timestamped<T>> buffer = new ArrayDeque();

            private void emitItemsOutOfWindow(long now) {
                long limit = now - OperatorSkipLastTimed.this.timeInMillis;
                while (!this.buffer.isEmpty()) {
                    Timestamped<T> v = this.buffer.getFirst();
                    if (v.getTimestampMillis() < limit) {
                        this.buffer.removeFirst();
                        subscriber.onNext(v.getValue());
                    } else {
                        return;
                    }
                }
            }

            @Override // p045rx.Observer
            public void onNext(T value) {
                long now = OperatorSkipLastTimed.this.scheduler.now();
                emitItemsOutOfWindow(now);
                this.buffer.offerLast(new Timestamped<>(now, value));
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                emitItemsOutOfWindow(OperatorSkipLastTimed.this.scheduler.now());
                subscriber.onCompleted();
            }
        };
    }
}
