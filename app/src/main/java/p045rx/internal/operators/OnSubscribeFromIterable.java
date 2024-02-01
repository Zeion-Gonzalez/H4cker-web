package p045rx.internal.operators;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;

/* loaded from: classes.dex */
public final class OnSubscribeFromIterable<T> implements Observable.OnSubscribe<T> {

    /* renamed from: is */
    final Iterable<? extends T> f1593is;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeFromIterable(Iterable<? extends T> iterable) {
        if (iterable == null) {
            throw new NullPointerException("iterable must not be null");
        }
        this.f1593is = iterable;
    }

    public void call(Subscriber<? super T> o) {
        try {
            Iterator<? extends T> it = this.f1593is.iterator();
            boolean b = it.hasNext();
            if (!o.isUnsubscribed()) {
                if (!b) {
                    o.onCompleted();
                } else {
                    o.setProducer(new IterableProducer(o, it));
                }
            }
        } catch (Throwable ex) {
            Exceptions.throwOrReport(ex, o);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class IterableProducer<T> extends AtomicLong implements Producer {
        private static final long serialVersionUID = -8730475647105475802L;

        /* renamed from: it */
        private final Iterator<? extends T> f1594it;

        /* renamed from: o */
        private final Subscriber<? super T> f1595o;

        /* JADX INFO: Access modifiers changed from: package-private */
        public IterableProducer(Subscriber<? super T> o, Iterator<? extends T> it) {
            this.f1595o = o;
            this.f1594it = it;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (get() == Long.MAX_VALUE) {
                return;
            }
            if (n == Long.MAX_VALUE && compareAndSet(0L, Long.MAX_VALUE)) {
                fastpath();
            } else if (n > 0 && BackpressureUtils.getAndAddRequest(this, n) == 0) {
                slowpath(n);
            }
        }

        void slowpath(long n) {
            Subscriber<? super T> o = this.f1595o;
            Iterator<? extends T> it = this.f1594it;
            long r = n;
            long e = 0;
            while (true) {
                if (e != r) {
                    if (!o.isUnsubscribed()) {
                        try {
                            o.onNext((T) it.next());
                            if (!o.isUnsubscribed()) {
                                try {
                                    boolean b = it.hasNext();
                                    if (!b) {
                                        if (!o.isUnsubscribed()) {
                                            o.onCompleted();
                                            return;
                                        }
                                        return;
                                    }
                                    e++;
                                } catch (Throwable ex) {
                                    Exceptions.throwOrReport(ex, o);
                                    return;
                                }
                            } else {
                                return;
                            }
                        } catch (Throwable ex2) {
                            Exceptions.throwOrReport(ex2, o);
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    r = get();
                    if (e == r) {
                        r = BackpressureUtils.produced(this, e);
                        if (r != 0) {
                            e = 0;
                        } else {
                            return;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }

        void fastpath() {
            Subscriber<? super T> o = this.f1595o;
            Iterator<? extends T> it = this.f1594it;
            while (!o.isUnsubscribed()) {
                try {
                    o.onNext((T) it.next());
                    if (!o.isUnsubscribed()) {
                        try {
                            boolean b = it.hasNext();
                            if (!b) {
                                if (!o.isUnsubscribed()) {
                                    o.onCompleted();
                                    return;
                                }
                                return;
                            }
                        } catch (Throwable ex) {
                            Exceptions.throwOrReport(ex, o);
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Throwable ex2) {
                    Exceptions.throwOrReport(ex2, o);
                    return;
                }
            }
        }
    }
}
