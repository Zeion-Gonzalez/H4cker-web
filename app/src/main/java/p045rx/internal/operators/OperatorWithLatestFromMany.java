package p045rx.internal.operators;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.FuncN;
import p045rx.observers.SerializedSubscriber;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OperatorWithLatestFromMany<T, R> implements Observable.OnSubscribe<R> {
    final FuncN<R> combiner;
    final Observable<T> main;
    final Observable<?>[] others;
    final Iterable<Observable<?>> othersIterable;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OperatorWithLatestFromMany(Observable<T> main, Observable<?>[] others, Iterable<Observable<?>> othersIterable, FuncN<R> combiner) {
        this.main = main;
        this.others = others;
        this.othersIterable = othersIterable;
        this.combiner = combiner;
    }

    public void call(Subscriber<? super R> t) {
        Observable<?>[] sources;
        SerializedSubscriber<R> serial = new SerializedSubscriber<>(t);
        int n = 0;
        if (this.others != null) {
            sources = this.others;
            n = sources.length;
        } else {
            sources = new Observable[8];
            for (Observable<?> o : this.othersIterable) {
                if (n == sources.length) {
                    sources = (Observable[]) Arrays.copyOf(sources, (n >> 2) + n);
                }
                sources[n] = o;
                n++;
            }
        }
        WithLatestMainSubscriber<T, R> parent = new WithLatestMainSubscriber<>(t, this.combiner, n);
        serial.add(parent);
        for (int i = 0; i < n; i++) {
            if (!serial.isUnsubscribed()) {
                WithLatestOtherSubscriber inner = new WithLatestOtherSubscriber(parent, i + 1);
                parent.add(inner);
                Observable<?> o2 = sources[i];
                o2.unsafeSubscribe(inner);
            } else {
                return;
            }
        }
        this.main.unsafeSubscribe(parent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WithLatestMainSubscriber<T, R> extends Subscriber<T> {
        static final Object EMPTY = new Object();
        final Subscriber<? super R> actual;
        final FuncN<R> combiner;
        final AtomicReferenceArray<Object> current;
        boolean done;
        final AtomicInteger ready;

        public WithLatestMainSubscriber(Subscriber<? super R> actual, FuncN<R> combiner, int n) {
            this.actual = actual;
            this.combiner = combiner;
            AtomicReferenceArray<Object> array = new AtomicReferenceArray<>(n + 1);
            for (int i = 0; i <= n; i++) {
                array.lazySet(i, EMPTY);
            }
            this.current = array;
            this.ready = new AtomicInteger(n);
            request(0L);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            if (!this.done) {
                if (this.ready.get() == 0) {
                    AtomicReferenceArray<Object> array = this.current;
                    int n = array.length();
                    array.lazySet(0, t);
                    Object[] copy = new Object[array.length()];
                    for (int i = 0; i < n; i++) {
                        copy[i] = array.get(i);
                    }
                    try {
                        R result = this.combiner.call(copy);
                        this.actual.onNext(result);
                        return;
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        onError(ex);
                        return;
                    }
                }
                request(1L);
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (this.done) {
                RxJavaHooks.onError(e);
                return;
            }
            this.done = true;
            unsubscribe();
            this.actual.onError(e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                unsubscribe();
                this.actual.onCompleted();
            }
        }

        @Override // p045rx.Subscriber
        public void setProducer(Producer p) {
            super.setProducer(p);
            this.actual.setProducer(p);
        }

        void innerNext(int index, Object o) {
            Object last = this.current.getAndSet(index, o);
            if (last == EMPTY) {
                this.ready.decrementAndGet();
            }
        }

        void innerError(int index, Throwable e) {
            onError(e);
        }

        void innerComplete(int index) {
            if (this.current.get(index) == EMPTY) {
                onCompleted();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WithLatestOtherSubscriber extends Subscriber<Object> {
        final int index;
        final WithLatestMainSubscriber<?, ?> parent;

        public WithLatestOtherSubscriber(WithLatestMainSubscriber<?, ?> parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        @Override // p045rx.Observer
        public void onNext(Object t) {
            this.parent.innerNext(this.index, t);
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.parent.innerError(this.index, e);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.parent.innerComplete(this.index);
        }
    }
}
