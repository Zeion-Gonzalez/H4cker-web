package p045rx.internal.operators;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import p045rx.Single;
import p045rx.SingleSubscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.functions.FuncN;
import p045rx.plugins.RxJavaHooks;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public final class SingleOperatorZip {
    private SingleOperatorZip() {
        throw new IllegalStateException("No instances!");
    }

    public static <T, R> Single<R> zip(final Single<? extends T>[] singles, final FuncN<? extends R> zipper) {
        return Single.create(new Single.OnSubscribe<R>() { // from class: rx.internal.operators.SingleOperatorZip.1
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(final SingleSubscriber<? super R> subscriber) {
                if (singles.length == 0) {
                    subscriber.onError(new NoSuchElementException("Can't zip 0 Singles."));
                    return;
                }
                final AtomicInteger wip = new AtomicInteger(singles.length);
                final AtomicBoolean once = new AtomicBoolean();
                final Object[] values = new Object[singles.length];
                CompositeSubscription compositeSubscription = new CompositeSubscription();
                subscriber.add(compositeSubscription);
                for (int i = 0; i < singles.length && !compositeSubscription.isUnsubscribed() && !once.get(); i++) {
                    final int j = i;
                    Subscription subscription = new SingleSubscriber<T>() { // from class: rx.internal.operators.SingleOperatorZip.1.1
                        /* JADX WARN: Multi-variable type inference failed */
                        @Override // p045rx.SingleSubscriber
                        public void onSuccess(T value) {
                            values[j] = value;
                            if (wip.decrementAndGet() == 0) {
                                try {
                                    subscriber.onSuccess(zipper.call(values));
                                } catch (Throwable e) {
                                    Exceptions.throwIfFatal(e);
                                    onError(e);
                                }
                            }
                        }

                        @Override // p045rx.SingleSubscriber
                        public void onError(Throwable error) {
                            if (once.compareAndSet(false, true)) {
                                subscriber.onError(error);
                            } else {
                                RxJavaHooks.onError(error);
                            }
                        }
                    };
                    compositeSubscription.add(subscription);
                    if (!compositeSubscription.isUnsubscribed() && !once.get()) {
                        singles[i].subscribe((SingleSubscriber) subscription);
                    } else {
                        return;
                    }
                }
            }
        });
    }
}
