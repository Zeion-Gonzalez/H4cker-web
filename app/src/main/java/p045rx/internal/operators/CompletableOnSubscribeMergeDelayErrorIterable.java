package p045rx.internal.operators;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import p045rx.Completable;
import p045rx.Subscription;
import p045rx.internal.util.unsafe.MpscLinkedQueue;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public final class CompletableOnSubscribeMergeDelayErrorIterable implements Completable.CompletableOnSubscribe {
    final Iterable<? extends Completable> sources;

    public CompletableOnSubscribeMergeDelayErrorIterable(Iterable<? extends Completable> sources) {
        this.sources = sources;
    }

    @Override // p045rx.functions.Action1
    public void call(final Completable.CompletableSubscriber s) {
        final CompositeSubscription set = new CompositeSubscription();
        s.onSubscribe(set);
        try {
            Iterator<? extends Completable> iterator = this.sources.iterator();
            if (iterator == null) {
                s.onError(new NullPointerException("The source iterator returned is null"));
                return;
            }
            final AtomicInteger wip = new AtomicInteger(1);
            final Queue<Throwable> queue = new MpscLinkedQueue<>();
            while (!set.isUnsubscribed()) {
                try {
                    boolean b = iterator.hasNext();
                    if (b) {
                        if (!set.isUnsubscribed()) {
                            try {
                                Completable c = iterator.next();
                                if (!set.isUnsubscribed()) {
                                    if (c == null) {
                                        NullPointerException e = new NullPointerException("A completable source is null");
                                        queue.offer(e);
                                        if (wip.decrementAndGet() == 0) {
                                            if (queue.isEmpty()) {
                                                s.onCompleted();
                                                return;
                                            } else {
                                                s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                                                return;
                                            }
                                        }
                                        return;
                                    }
                                    wip.getAndIncrement();
                                    c.unsafeSubscribe(new Completable.CompletableSubscriber() { // from class: rx.internal.operators.CompletableOnSubscribeMergeDelayErrorIterable.1
                                        @Override // rx.Completable.CompletableSubscriber
                                        public void onSubscribe(Subscription d) {
                                            set.add(d);
                                        }

                                        @Override // rx.Completable.CompletableSubscriber
                                        public void onError(Throwable e2) {
                                            queue.offer(e2);
                                            tryTerminate();
                                        }

                                        @Override // rx.Completable.CompletableSubscriber
                                        public void onCompleted() {
                                            tryTerminate();
                                        }

                                        void tryTerminate() {
                                            if (wip.decrementAndGet() == 0) {
                                                if (queue.isEmpty()) {
                                                    s.onCompleted();
                                                } else {
                                                    s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    return;
                                }
                            } catch (Throwable e2) {
                                queue.offer(e2);
                                if (wip.decrementAndGet() == 0) {
                                    if (queue.isEmpty()) {
                                        s.onCompleted();
                                        return;
                                    } else {
                                        s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                                        return;
                                    }
                                }
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        if (wip.decrementAndGet() == 0) {
                            if (queue.isEmpty()) {
                                s.onCompleted();
                                return;
                            } else {
                                s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                                return;
                            }
                        }
                        return;
                    }
                } catch (Throwable e3) {
                    queue.offer(e3);
                    if (wip.decrementAndGet() == 0) {
                        if (queue.isEmpty()) {
                            s.onCompleted();
                            return;
                        } else {
                            s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                            return;
                        }
                    }
                    return;
                }
            }
        } catch (Throwable e4) {
            s.onError(e4);
        }
    }
}
