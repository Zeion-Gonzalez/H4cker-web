package p045rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import p045rx.Subscriber;
import p045rx.functions.Func1;
import p045rx.internal.util.UtilityFunctions;

/* loaded from: classes.dex */
public final class BackpressureUtils {
    static final long COMPLETED_MASK = Long.MIN_VALUE;
    static final long REQUESTED_MASK = Long.MAX_VALUE;

    private BackpressureUtils() {
        throw new IllegalStateException("No instances!");
    }

    @Deprecated
    public static <T> long getAndAddRequest(AtomicLongFieldUpdater<T> requested, T object, long n) {
        long current;
        long next;
        do {
            current = requested.get(object);
            next = addCap(current, n);
        } while (!requested.compareAndSet(object, current, next));
        return current;
    }

    public static long getAndAddRequest(AtomicLong requested, long n) {
        long current;
        long next;
        do {
            current = requested.get();
            next = addCap(current, n);
        } while (!requested.compareAndSet(current, next));
        return current;
    }

    public static long multiplyCap(long a, long b) {
        long u = a * b;
        if (((a | b) >>> 31) != 0 && b != 0 && u / b != a) {
            return REQUESTED_MASK;
        }
        return u;
    }

    public static long addCap(long a, long b) {
        long u = a + b;
        if (u < 0) {
            return REQUESTED_MASK;
        }
        return u;
    }

    public static <T> void postCompleteDone(AtomicLong requested, Queue<T> queue, Subscriber<? super T> actual) {
        postCompleteDone(requested, queue, actual, UtilityFunctions.identity());
    }

    public static <T> boolean postCompleteRequest(AtomicLong requested, long n, Queue<T> queue, Subscriber<? super T> actual) {
        return postCompleteRequest(requested, n, queue, actual, UtilityFunctions.identity());
    }

    public static <T, R> void postCompleteDone(AtomicLong requested, Queue<T> queue, Subscriber<? super R> actual, Func1<? super T, ? extends R> exitTransform) {
        long r;
        long u;
        do {
            r = requested.get();
            if ((r & COMPLETED_MASK) == 0) {
                u = r | COMPLETED_MASK;
            } else {
                return;
            }
        } while (!requested.compareAndSet(r, u));
        if (r != 0) {
            postCompleteDrain(requested, queue, actual, exitTransform);
        }
    }

    public static <T, R> boolean postCompleteRequest(AtomicLong requested, long n, Queue<T> queue, Subscriber<? super R> actual, Func1<? super T, ? extends R> exitTransform) {
        long r;
        long c;
        long v;
        if (n < 0) {
            throw new IllegalArgumentException("n >= 0 required but it was " + n);
        }
        if (n == 0) {
            return (requested.get() & COMPLETED_MASK) == 0;
        }
        do {
            r = requested.get();
            c = r & COMPLETED_MASK;
            long u = r & REQUESTED_MASK;
            v = addCap(u, n);
        } while (!requested.compareAndSet(r, v | c));
        if (r != COMPLETED_MASK) {
            return c == 0;
        }
        postCompleteDrain(requested, queue, actual, exitTransform);
        return false;
    }

    static <T, R> void postCompleteDrain(AtomicLong requested, Queue<T> queue, Subscriber<? super R> subscriber, Func1<? super T, ? extends R> exitTransform) {
        long r = requested.get();
        if (r == REQUESTED_MASK) {
            while (!subscriber.isUnsubscribed()) {
                Object poll = queue.poll();
                if (poll == null) {
                    subscriber.onCompleted();
                    return;
                }
                subscriber.onNext((R) exitTransform.call(poll));
            }
            return;
        }
        long e = COMPLETED_MASK;
        while (true) {
            if (e != r) {
                if (!subscriber.isUnsubscribed()) {
                    Object poll2 = queue.poll();
                    if (poll2 == null) {
                        subscriber.onCompleted();
                        return;
                    } else {
                        subscriber.onNext((R) exitTransform.call(poll2));
                        e++;
                    }
                } else {
                    return;
                }
            } else {
                if (e == r) {
                    if (!subscriber.isUnsubscribed()) {
                        if (queue.isEmpty()) {
                            subscriber.onCompleted();
                            return;
                        }
                    } else {
                        return;
                    }
                }
                r = requested.get();
                if (r == e) {
                    r = requested.addAndGet(-(e & REQUESTED_MASK));
                    if (r == COMPLETED_MASK) {
                        return;
                    } else {
                        e = COMPLETED_MASK;
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public static long produced(AtomicLong requested, long n) {
        long current;
        long next;
        do {
            current = requested.get();
            if (current == REQUESTED_MASK) {
                return REQUESTED_MASK;
            }
            next = current - n;
            if (next < 0) {
                throw new IllegalStateException("More produced than requested: " + next);
            }
        } while (!requested.compareAndSet(current, next));
        return next;
    }

    public static boolean validate(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n >= 0 required but it was " + n);
        }
        return n != 0;
    }
}
