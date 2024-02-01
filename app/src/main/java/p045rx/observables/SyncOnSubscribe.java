package p045rx.observables;

import java.util.concurrent.atomic.AtomicLong;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.annotations.Beta;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Action2;
import p045rx.functions.Func0;
import p045rx.functions.Func2;
import p045rx.internal.operators.BackpressureUtils;
import p045rx.plugins.RxJavaHooks;

@Beta
/* loaded from: classes.dex */
public abstract class SyncOnSubscribe<S, T> implements Observable.OnSubscribe<T> {
    protected abstract S generateState();

    protected abstract S next(S s, Observer<? super T> observer);

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public final void call(Subscriber<? super T> subscriber) {
        try {
            S state = generateState();
            SubscriptionProducer<S, T> p = new SubscriptionProducer<>(subscriber, this, state);
            subscriber.add(p);
            subscriber.setProducer(p);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            subscriber.onError(e);
        }
    }

    protected void onUnsubscribe(S state) {
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createSingleState(Func0<? extends S> generator, final Action2<? super S, ? super Observer<? super T>> next) {
        Func2<S, ? super Observer<? super T>, S> nextFunc = new Func2<S, Observer<? super T>, S>() { // from class: rx.observables.SyncOnSubscribe.1
            @Override // p045rx.functions.Func2
            public /* bridge */ /* synthetic */ Object call(Object x0, Object x1) {
                return call((C11881) x0, (Observer) ((Observer) x1));
            }

            public S call(S state, Observer<? super T> subscriber) {
                Action2.this.call(state, subscriber);
                return state;
            }
        };
        return new SyncOnSubscribeImpl(generator, nextFunc);
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createSingleState(Func0<? extends S> generator, final Action2<? super S, ? super Observer<? super T>> next, Action1<? super S> onUnsubscribe) {
        Func2<S, Observer<? super T>, S> nextFunc = new Func2<S, Observer<? super T>, S>() { // from class: rx.observables.SyncOnSubscribe.2
            @Override // p045rx.functions.Func2
            public /* bridge */ /* synthetic */ Object call(Object x0, Object x1) {
                return call((C11892) x0, (Observer) ((Observer) x1));
            }

            public S call(S state, Observer<? super T> subscriber) {
                Action2.this.call(state, subscriber);
                return state;
            }
        };
        return new SyncOnSubscribeImpl(generator, nextFunc, onUnsubscribe);
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createStateful(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next, Action1<? super S> onUnsubscribe) {
        return new SyncOnSubscribeImpl(generator, next, onUnsubscribe);
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createStateful(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next) {
        return new SyncOnSubscribeImpl(generator, next);
    }

    @Beta
    public static <T> SyncOnSubscribe<Void, T> createStateless(final Action1<? super Observer<? super T>> next) {
        Func2<Void, Observer<? super T>, Void> nextFunc = new Func2<Void, Observer<? super T>, Void>() { // from class: rx.observables.SyncOnSubscribe.3
            @Override // p045rx.functions.Func2
            public /* bridge */ /* synthetic */ Void call(Void r2, Object x1) {
                return call(r2, (Observer) ((Observer) x1));
            }

            public Void call(Void state, Observer<? super T> subscriber) {
                Action1.this.call(subscriber);
                return state;
            }
        };
        return new SyncOnSubscribeImpl(nextFunc);
    }

    @Beta
    public static <T> SyncOnSubscribe<Void, T> createStateless(final Action1<? super Observer<? super T>> next, final Action0 onUnsubscribe) {
        Func2<Void, Observer<? super T>, Void> nextFunc = new Func2<Void, Observer<? super T>, Void>() { // from class: rx.observables.SyncOnSubscribe.4
            @Override // p045rx.functions.Func2
            public /* bridge */ /* synthetic */ Void call(Void r2, Object x1) {
                return call(r2, (Observer) ((Observer) x1));
            }

            public Void call(Void state, Observer<? super T> subscriber) {
                Action1.this.call(subscriber);
                return null;
            }
        };
        Action1<? super Void> wrappedOnUnsubscribe = new Action1<Void>() { // from class: rx.observables.SyncOnSubscribe.5
            @Override // p045rx.functions.Action1
            public void call(Void t) {
                Action0.this.call();
            }
        };
        return new SyncOnSubscribeImpl(nextFunc, wrappedOnUnsubscribe);
    }

    /* loaded from: classes.dex */
    static final class SyncOnSubscribeImpl<S, T> extends SyncOnSubscribe<S, T> {
        private final Func0<? extends S> generator;
        private final Func2<? super S, ? super Observer<? super T>, ? extends S> next;
        private final Action1<? super S> onUnsubscribe;

        @Override // p045rx.observables.SyncOnSubscribe, p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            super.call((Subscriber) ((Subscriber) x0));
        }

        SyncOnSubscribeImpl(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next, Action1<? super S> onUnsubscribe) {
            this.generator = generator;
            this.next = next;
            this.onUnsubscribe = onUnsubscribe;
        }

        public SyncOnSubscribeImpl(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next) {
            this(generator, next, null);
        }

        public SyncOnSubscribeImpl(Func2<S, Observer<? super T>, S> next, Action1<? super S> onUnsubscribe) {
            this(null, next, onUnsubscribe);
        }

        public SyncOnSubscribeImpl(Func2<S, Observer<? super T>, S> nextFunc) {
            this(null, nextFunc, null);
        }

        @Override // p045rx.observables.SyncOnSubscribe
        protected S generateState() {
            if (this.generator == null) {
                return null;
            }
            return this.generator.call();
        }

        @Override // p045rx.observables.SyncOnSubscribe
        protected S next(S state, Observer<? super T> observer) {
            return this.next.call(state, observer);
        }

        @Override // p045rx.observables.SyncOnSubscribe
        protected void onUnsubscribe(S state) {
            if (this.onUnsubscribe != null) {
                this.onUnsubscribe.call(state);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SubscriptionProducer<S, T> extends AtomicLong implements Producer, Subscription, Observer<T> {
        private static final long serialVersionUID = -3736864024352728072L;
        private final Subscriber<? super T> actualSubscriber;
        private boolean hasTerminated;
        private boolean onNextCalled;
        private final SyncOnSubscribe<S, T> parent;
        private S state;

        SubscriptionProducer(Subscriber<? super T> subscriber, SyncOnSubscribe<S, T> parent, S state) {
            this.actualSubscriber = subscriber;
            this.parent = parent;
            this.state = state;
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return get() < 0;
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            long requestCount;
            do {
                requestCount = get();
                if (compareAndSet(0L, -1L)) {
                    doUnsubscribe();
                    return;
                }
            } while (!compareAndSet(requestCount, -2L));
        }

        private boolean tryUnsubscribe() {
            if (!this.hasTerminated && get() >= -1) {
                return false;
            }
            set(-1L);
            doUnsubscribe();
            return true;
        }

        private void doUnsubscribe() {
            try {
                this.parent.onUnsubscribe(this.state);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                RxJavaHooks.onError(e);
            }
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n <= 0 || BackpressureUtils.getAndAddRequest(this, n) != 0) {
                return;
            }
            if (n == Long.MAX_VALUE) {
                fastpath();
            } else {
                slowPath(n);
            }
        }

        private void fastpath() {
            SyncOnSubscribe<S, T> p = this.parent;
            Subscriber<? super T> a = this.actualSubscriber;
            do {
                try {
                    this.onNextCalled = false;
                    nextIteration(p);
                } catch (Throwable ex) {
                    handleThrownError(a, ex);
                    return;
                }
            } while (!tryUnsubscribe());
        }

        private void handleThrownError(Subscriber<? super T> a, Throwable ex) {
            if (this.hasTerminated) {
                RxJavaHooks.onError(ex);
                return;
            }
            this.hasTerminated = true;
            a.onError(ex);
            unsubscribe();
        }

        private void slowPath(long n) {
            SyncOnSubscribe<S, T> p = this.parent;
            Subscriber<? super T> a = this.actualSubscriber;
            long numRequested = n;
            do {
                long numRemaining = numRequested;
                do {
                    try {
                        this.onNextCalled = false;
                        nextIteration(p);
                        if (!tryUnsubscribe()) {
                            if (this.onNextCalled) {
                                numRemaining--;
                            }
                        } else {
                            return;
                        }
                    } catch (Throwable ex) {
                        handleThrownError(a, ex);
                        return;
                    }
                } while (numRemaining != 0);
                numRequested = addAndGet(-numRequested);
            } while (numRequested > 0);
            tryUnsubscribe();
        }

        private void nextIteration(SyncOnSubscribe<S, T> parent) {
            this.state = parent.next(this.state, this);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (this.hasTerminated) {
                throw new IllegalStateException("Terminal event already emitted.");
            }
            this.hasTerminated = true;
            if (!this.actualSubscriber.isUnsubscribed()) {
                this.actualSubscriber.onCompleted();
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (this.hasTerminated) {
                throw new IllegalStateException("Terminal event already emitted.");
            }
            this.hasTerminated = true;
            if (!this.actualSubscriber.isUnsubscribed()) {
                this.actualSubscriber.onError(e);
            }
        }

        @Override // p045rx.Observer
        public void onNext(T value) {
            if (this.onNextCalled) {
                throw new IllegalStateException("onNext called multiple times!");
            }
            this.onNextCalled = true;
            this.actualSubscriber.onNext(value);
        }
    }
}
