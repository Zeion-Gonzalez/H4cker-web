package p045rx.internal.operators;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.functions.Func1;

/* loaded from: classes.dex */
public final class OperatorTakeLastTimed<T> implements Observable.Operator<T, T> {
    final long ageMillis;
    final int count;
    final Scheduler scheduler;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorTakeLastTimed(long time, TimeUnit unit, Scheduler scheduler) {
        this.ageMillis = unit.toMillis(time);
        this.scheduler = scheduler;
        this.count = -1;
    }

    public OperatorTakeLastTimed(int count, long time, TimeUnit unit, Scheduler scheduler) {
        if (count < 0) {
            throw new IndexOutOfBoundsException("count could not be negative");
        }
        this.ageMillis = unit.toMillis(time);
        this.scheduler = scheduler;
        this.count = count;
    }

    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        final TakeLastTimedSubscriber<T> parent = new TakeLastTimedSubscriber<>(subscriber, this.count, this.ageMillis, this.scheduler);
        subscriber.add(parent);
        subscriber.setProducer(new Producer() { // from class: rx.internal.operators.OperatorTakeLastTimed.1
            @Override // p045rx.Producer
            public void request(long n) {
                parent.requestMore(n);
            }
        });
        return parent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class TakeLastTimedSubscriber<T> extends Subscriber<T> implements Func1<Object, T> {
        final Subscriber<? super T> actual;
        final long ageMillis;
        final int count;
        final Scheduler scheduler;
        final AtomicLong requested = new AtomicLong();
        final ArrayDeque<Object> queue = new ArrayDeque<>();
        final ArrayDeque<Long> queueTimes = new ArrayDeque<>();

        /* renamed from: nl */
        final NotificationLite<T> f1615nl = NotificationLite.instance();

        public TakeLastTimedSubscriber(Subscriber<? super T> actual, int count, long ageMillis, Scheduler scheduler) {
            this.actual = actual;
            this.count = count;
            this.ageMillis = ageMillis;
            this.scheduler = scheduler;
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            if (this.count != 0) {
                long now = this.scheduler.now();
                if (this.queue.size() == this.count) {
                    this.queue.poll();
                    this.queueTimes.poll();
                }
                evictOld(now);
                this.queue.offer(this.f1615nl.next(t));
                this.queueTimes.offer(Long.valueOf(now));
            }
        }

        protected void evictOld(long now) {
            long minTime = now - this.ageMillis;
            while (true) {
                Long time = this.queueTimes.peek();
                if (time != null && time.longValue() < minTime) {
                    this.queue.poll();
                    this.queueTimes.poll();
                } else {
                    return;
                }
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.queue.clear();
            this.queueTimes.clear();
            this.actual.onError(e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            evictOld(this.scheduler.now());
            this.queueTimes.clear();
            BackpressureUtils.postCompleteDone(this.requested, this.queue, this.actual, this);
        }

        @Override // p045rx.functions.Func1
        public T call(Object t) {
            return this.f1615nl.getValue(t);
        }

        void requestMore(long n) {
            BackpressureUtils.postCompleteRequest(this.requested, n, this.queue, this.actual, this);
        }
    }
}
