package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public final class OnSubscribeFromArray<T> implements Observable.OnSubscribe<T> {
    final T[] array;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeFromArray(T[] array) {
        this.array = array;
    }

    public void call(Subscriber<? super T> child) {
        child.setProducer(new FromArrayProducer(child, this.array));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class FromArrayProducer<T> extends AtomicLong implements Producer {
        private static final long serialVersionUID = 3534218984725836979L;
        final T[] array;
        final Subscriber<? super T> child;
        int index;

        public FromArrayProducer(Subscriber<? super T> child, T[] array) {
            this.child = child;
            this.array = array;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required but it was " + n);
            }
            if (n == Long.MAX_VALUE) {
                if (BackpressureUtils.getAndAddRequest(this, n) == 0) {
                    fastPath();
                }
            } else if (n != 0 && BackpressureUtils.getAndAddRequest(this, n) == 0) {
                slowPath(n);
            }
        }

        void fastPath() {
            Subscriber<? super T> child = this.child;
            Object[] arr$ = (T[]) this.array;
            for (Object obj : arr$) {
                Object obj2 = (Object) obj;
                if (!child.isUnsubscribed()) {
                    child.onNext(obj2);
                } else {
                    return;
                }
            }
            if (!child.isUnsubscribed()) {
                child.onCompleted();
            }
        }

        void slowPath(long r) {
            Subscriber<? super T> child = this.child;
            T[] array = this.array;
            int n = array.length;
            long e = 0;
            int i = this.index;
            while (true) {
                if (r != 0 && i != n) {
                    if (!child.isUnsubscribed()) {
                        child.onNext((Object) array[i]);
                        i++;
                        if (i == n) {
                            if (!child.isUnsubscribed()) {
                                child.onCompleted();
                                return;
                            }
                            return;
                        }
                        r--;
                        e--;
                    } else {
                        return;
                    }
                } else {
                    r = get() + e;
                    if (r == 0) {
                        this.index = i;
                        r = addAndGet(e);
                        if (r == 0) {
                            return;
                        } else {
                            e = 0;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
    }
}
