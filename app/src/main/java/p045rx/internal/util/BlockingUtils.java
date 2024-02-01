package p045rx.internal.util;

import java.util.concurrent.CountDownLatch;
import p045rx.Subscription;
import p045rx.annotations.Experimental;

@Experimental
/* loaded from: classes.dex */
public final class BlockingUtils {
    private BlockingUtils() {
    }

    @Experimental
    public static void awaitForComplete(CountDownLatch latch, Subscription subscription) {
        if (latch.getCount() != 0) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                subscription.unsubscribe();
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for subscription to complete.", e);
            }
        }
    }
}