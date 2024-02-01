package p045rx.singles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Single;
import p045rx.SingleSubscriber;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.exceptions.Exceptions;
import p045rx.internal.operators.BlockingOperatorToFuture;
import p045rx.internal.util.BlockingUtils;

@Experimental
/* loaded from: classes.dex */
public final class BlockingSingle<T> {
    private final Single<? extends T> single;

    private BlockingSingle(Single<? extends T> single) {
        this.single = single;
    }

    @Experimental
    public static <T> BlockingSingle<T> from(Single<? extends T> single) {
        return new BlockingSingle<>(single);
    }

    @Experimental
    public T value() {
        final AtomicReference<T> returnItem = new AtomicReference<>();
        final AtomicReference<Throwable> returnException = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        Subscription subscription = this.single.subscribe((SingleSubscriber<? super Object>) new SingleSubscriber<T>() { // from class: rx.singles.BlockingSingle.1
            @Override // p045rx.SingleSubscriber
            public void onSuccess(T value) {
                returnItem.set(value);
                latch.countDown();
            }

            @Override // p045rx.SingleSubscriber
            public void onError(Throwable error) {
                returnException.set(error);
                latch.countDown();
            }
        });
        BlockingUtils.awaitForComplete(latch, subscription);
        Throwable throwable = returnException.get();
        if (throwable != null) {
            throw Exceptions.propagate(throwable);
        }
        return returnItem.get();
    }

    @Experimental
    public Future<T> toFuture() {
        return BlockingOperatorToFuture.toFuture(this.single.toObservable());
    }
}
