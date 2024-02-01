package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OnSubscribeDetach<T> implements Observable.OnSubscribe<T> {
    final Observable<T> source;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeDetach(Observable<T> source) {
        this.source = source;
    }

    public void call(Subscriber<? super T> t) {
        DetachSubscriber<T> parent = new DetachSubscriber<>(t);
        DetachProducer<T> producer = new DetachProducer<>(parent);
        t.add(producer);
        t.setProducer(producer);
        this.source.unsafeSubscribe(parent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DetachSubscriber<T> extends Subscriber<T> {
        final AtomicReference<Subscriber<? super T>> actual;
        final AtomicReference<Producer> producer = new AtomicReference<>();
        final AtomicLong requested = new AtomicLong();

        public DetachSubscriber(Subscriber<? super T> actual) {
            this.actual = new AtomicReference<>(actual);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            Subscriber<? super T> a = this.actual.get();
            if (a != null) {
                a.onNext(t);
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.producer.lazySet(TerminatedProducer.INSTANCE);
            Subscriber<? super T> a = this.actual.getAndSet(null);
            if (a != null) {
                a.onError(e);
            } else {
                RxJavaHooks.onError(e);
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.producer.lazySet(TerminatedProducer.INSTANCE);
            Subscriber<? super T> a = this.actual.getAndSet(null);
            if (a != null) {
                a.onCompleted();
            }
        }

        void innerRequest(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required but it was " + n);
            }
            Producer p = this.producer.get();
            if (p != null) {
                p.request(n);
                return;
            }
            BackpressureUtils.getAndAddRequest(this.requested, n);
            Producer p2 = this.producer.get();
            if (p2 != null && p2 != TerminatedProducer.INSTANCE) {
                long r = this.requested.getAndSet(0L);
                p2.request(r);
            }
        }

        @Override // p045rx.Subscriber
        public void setProducer(Producer p) {
            if (this.producer.compareAndSet(null, p)) {
                long r = this.requested.getAndSet(0L);
                p.request(r);
            } else if (this.producer.get() != TerminatedProducer.INSTANCE) {
                throw new IllegalStateException("Producer already set!");
            }
        }

        void innerUnsubscribe() {
            this.producer.lazySet(TerminatedProducer.INSTANCE);
            this.actual.lazySet(null);
            unsubscribe();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DetachProducer<T> implements Producer, Subscription {
        final DetachSubscriber<T> parent;

        public DetachProducer(DetachSubscriber<T> parent) {
            this.parent = parent;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            this.parent.innerRequest(n);
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.parent.isUnsubscribed();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.parent.innerUnsubscribe();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum TerminatedProducer implements Producer {
        INSTANCE;

        @Override // p045rx.Producer
        public void request(long n) {
        }
    }
}
