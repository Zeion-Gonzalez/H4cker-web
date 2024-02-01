package p045rx.internal.operators;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import p045rx.Completable;
import p045rx.Scheduler;
import p045rx.Subscription;
import p045rx.functions.Action0;
import p045rx.plugins.RxJavaHooks;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public final class CompletableOnSubscribeTimeout implements Completable.CompletableOnSubscribe {
    final Completable other;
    final Scheduler scheduler;
    final Completable source;
    final long timeout;
    final TimeUnit unit;

    public CompletableOnSubscribeTimeout(Completable source, long timeout, TimeUnit unit, Scheduler scheduler, Completable other) {
        this.source = source;
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
        this.other = other;
    }

    @Override // p045rx.functions.Action1
    public void call(final Completable.CompletableSubscriber s) {
        final CompositeSubscription set = new CompositeSubscription();
        s.onSubscribe(set);
        final AtomicBoolean once = new AtomicBoolean();
        Scheduler.Worker w = this.scheduler.createWorker();
        set.add(w);
        w.schedule(new Action0() { // from class: rx.internal.operators.CompletableOnSubscribeTimeout.1
            @Override // p045rx.functions.Action0
            public void call() {
                if (once.compareAndSet(false, true)) {
                    set.clear();
                    if (CompletableOnSubscribeTimeout.this.other == null) {
                        s.onError(new TimeoutException());
                    } else {
                        CompletableOnSubscribeTimeout.this.other.unsafeSubscribe(new Completable.CompletableSubscriber() { // from class: rx.internal.operators.CompletableOnSubscribeTimeout.1.1
                            @Override // rx.Completable.CompletableSubscriber
                            public void onSubscribe(Subscription d) {
                                set.add(d);
                            }

                            @Override // rx.Completable.CompletableSubscriber
                            public void onError(Throwable e) {
                                set.unsubscribe();
                                s.onError(e);
                            }

                            @Override // rx.Completable.CompletableSubscriber
                            public void onCompleted() {
                                set.unsubscribe();
                                s.onCompleted();
                            }
                        });
                    }
                }
            }
        }, this.timeout, this.unit);
        this.source.unsafeSubscribe(new Completable.CompletableSubscriber() { // from class: rx.internal.operators.CompletableOnSubscribeTimeout.2
            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
                set.add(d);
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                if (once.compareAndSet(false, true)) {
                    set.unsubscribe();
                    s.onError(e);
                } else {
                    RxJavaHooks.onError(e);
                }
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                if (once.compareAndSet(false, true)) {
                    set.unsubscribe();
                    s.onCompleted();
                }
            }
        });
    }
}
