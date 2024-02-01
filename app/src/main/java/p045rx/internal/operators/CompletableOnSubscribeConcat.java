package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import p045rx.Completable;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.MissingBackpressureException;
import p045rx.internal.util.unsafe.SpscArrayQueue;
import p045rx.plugins.RxJavaHooks;
import p045rx.subscriptions.SerialSubscription;

/* loaded from: classes.dex */
public final class CompletableOnSubscribeConcat implements Completable.CompletableOnSubscribe {
    final int prefetch;
    final Observable<Completable> sources;

    /* JADX WARN: Multi-variable type inference failed */
    public CompletableOnSubscribeConcat(Observable<? extends Completable> sources, int prefetch) {
        this.sources = sources;
        this.prefetch = prefetch;
    }

    @Override // p045rx.functions.Action1
    public void call(Completable.CompletableSubscriber s) {
        CompletableConcatSubscriber parent = new CompletableConcatSubscriber(s, this.prefetch);
        s.onSubscribe(parent);
        this.sources.subscribe((Subscriber<? super Completable>) parent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class CompletableConcatSubscriber extends Subscriber<Completable> {
        final Completable.CompletableSubscriber actual;
        volatile boolean done;
        final int prefetch;
        final SpscArrayQueue<Completable> queue;

        /* renamed from: sr */
        final SerialSubscription f1584sr = new SerialSubscription();
        final ConcatInnerSubscriber inner = new ConcatInnerSubscriber();
        final AtomicInteger wip = new AtomicInteger();
        final AtomicBoolean once = new AtomicBoolean();

        public CompletableConcatSubscriber(Completable.CompletableSubscriber actual, int prefetch) {
            this.actual = actual;
            this.prefetch = prefetch;
            this.queue = new SpscArrayQueue<>(prefetch);
            add(this.f1584sr);
            request(prefetch);
        }

        @Override // p045rx.Observer
        public void onNext(Completable t) {
            if (!this.queue.offer(t)) {
                onError(new MissingBackpressureException());
            } else if (this.wip.getAndIncrement() == 0) {
                next();
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable t) {
            if (this.once.compareAndSet(false, true)) {
                this.actual.onError(t);
            } else {
                RxJavaHooks.onError(t);
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                if (this.wip.getAndIncrement() == 0) {
                    next();
                }
            }
        }

        void innerError(Throwable e) {
            unsubscribe();
            onError(e);
        }

        void innerComplete() {
            if (this.wip.decrementAndGet() != 0) {
                next();
            }
            if (!this.done) {
                request(1L);
            }
        }

        void next() {
            boolean d = this.done;
            Completable c = this.queue.poll();
            if (c == null) {
                if (d) {
                    if (this.once.compareAndSet(false, true)) {
                        this.actual.onCompleted();
                        return;
                    }
                    return;
                }
                RxJavaHooks.onError(new IllegalStateException("Queue is empty?!"));
                return;
            }
            c.unsafeSubscribe(this.inner);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class ConcatInnerSubscriber implements Completable.CompletableSubscriber {
            ConcatInnerSubscriber() {
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
                CompletableConcatSubscriber.this.f1584sr.set(d);
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                CompletableConcatSubscriber.this.innerError(e);
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                CompletableConcatSubscriber.this.innerComplete();
            }
        }
    }
}
