package p045rx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Single;
import p045rx.annotations.Experimental;
import p045rx.exceptions.CompositeException;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Actions;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.internal.operators.CompletableOnSubscribeConcat;
import p045rx.internal.operators.CompletableOnSubscribeConcatArray;
import p045rx.internal.operators.CompletableOnSubscribeConcatIterable;
import p045rx.internal.operators.CompletableOnSubscribeMerge;
import p045rx.internal.operators.CompletableOnSubscribeMergeArray;
import p045rx.internal.operators.CompletableOnSubscribeMergeDelayErrorArray;
import p045rx.internal.operators.CompletableOnSubscribeMergeDelayErrorIterable;
import p045rx.internal.operators.CompletableOnSubscribeMergeIterable;
import p045rx.internal.operators.CompletableOnSubscribeTimeout;
import p045rx.internal.util.SubscriptionList;
import p045rx.internal.util.UtilityFunctions;
import p045rx.observers.SafeCompletableSubscriber;
import p045rx.observers.SafeSubscriber;
import p045rx.plugins.RxJavaHooks;
import p045rx.schedulers.Schedulers;
import p045rx.subscriptions.BooleanSubscription;
import p045rx.subscriptions.CompositeSubscription;
import p045rx.subscriptions.MultipleAssignmentSubscription;
import p045rx.subscriptions.SerialSubscription;
import p045rx.subscriptions.Subscriptions;

@Experimental
/* loaded from: classes.dex */
public class Completable {
    static final Completable COMPLETE = new Completable(new CompletableOnSubscribe() { // from class: rx.Completable.1
        @Override // p045rx.functions.Action1
        public void call(CompletableSubscriber s) {
            s.onSubscribe(Subscriptions.unsubscribed());
            s.onCompleted();
        }
    }, false);
    static final Completable NEVER = new Completable(new CompletableOnSubscribe() { // from class: rx.Completable.2
        @Override // p045rx.functions.Action1
        public void call(CompletableSubscriber s) {
            s.onSubscribe(Subscriptions.unsubscribed());
        }
    }, false);
    private final CompletableOnSubscribe onSubscribe;

    /* loaded from: classes.dex */
    public interface CompletableOnSubscribe extends Action1<CompletableSubscriber> {
    }

    /* loaded from: classes.dex */
    public interface CompletableOperator extends Func1<CompletableSubscriber, CompletableSubscriber> {
    }

    /* loaded from: classes.dex */
    public interface CompletableSubscriber {
        void onCompleted();

        void onError(Throwable th);

        void onSubscribe(Subscription subscription);
    }

    /* loaded from: classes.dex */
    public interface CompletableTransformer extends Func1<Completable, Completable> {
    }

    public static Completable amb(final Completable... sources) {
        requireNonNull(sources);
        if (sources.length == 0) {
            return complete();
        }
        if (sources.length == 1) {
            return sources[0];
        }
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.3
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                final CompositeSubscription set = new CompositeSubscription();
                s.onSubscribe(set);
                final AtomicBoolean once = new AtomicBoolean();
                CompletableSubscriber inner = new CompletableSubscriber() { // from class: rx.Completable.3.1
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        if (once.compareAndSet(false, true)) {
                            set.unsubscribe();
                            s.onCompleted();
                        }
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
                    public void onSubscribe(Subscription d) {
                        set.add(d);
                    }
                };
                Completable[] arr$ = sources;
                for (Completable c : arr$) {
                    if (!set.isUnsubscribed()) {
                        if (c == null) {
                            NullPointerException npe = new NullPointerException("One of the sources is null");
                            if (once.compareAndSet(false, true)) {
                                set.unsubscribe();
                                s.onError(npe);
                                return;
                            } else {
                                RxJavaHooks.onError(npe);
                                return;
                            }
                        }
                        if (!once.get() && !set.isUnsubscribed()) {
                            c.unsafeSubscribe(inner);
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        });
    }

    public static Completable amb(final Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.4
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                final CompositeSubscription set = new CompositeSubscription();
                s.onSubscribe(set);
                try {
                    Iterator<? extends Completable> it = sources.iterator();
                    if (it == null) {
                        s.onError(new NullPointerException("The iterator returned is null"));
                        return;
                    }
                    boolean empty = true;
                    final AtomicBoolean once = new AtomicBoolean();
                    CompletableSubscriber inner = new CompletableSubscriber() { // from class: rx.Completable.4.1
                        @Override // rx.Completable.CompletableSubscriber
                        public void onCompleted() {
                            if (once.compareAndSet(false, true)) {
                                set.unsubscribe();
                                s.onCompleted();
                            }
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
                        public void onSubscribe(Subscription d) {
                            set.add(d);
                        }
                    };
                    while (!once.get() && !set.isUnsubscribed()) {
                        try {
                            boolean b = it.hasNext();
                            if (!b) {
                                if (empty) {
                                    s.onCompleted();
                                    return;
                                }
                                return;
                            }
                            empty = false;
                            if (!once.get() && !set.isUnsubscribed()) {
                                try {
                                    Completable c = (Completable) it.next();
                                    if (c == null) {
                                        NullPointerException npe = new NullPointerException("One of the sources is null");
                                        if (once.compareAndSet(false, true)) {
                                            set.unsubscribe();
                                            s.onError(npe);
                                            return;
                                        } else {
                                            RxJavaHooks.onError(npe);
                                            return;
                                        }
                                    }
                                    if (!once.get() && !set.isUnsubscribed()) {
                                        c.unsafeSubscribe(inner);
                                    } else {
                                        return;
                                    }
                                } catch (Throwable e) {
                                    if (once.compareAndSet(false, true)) {
                                        set.unsubscribe();
                                        s.onError(e);
                                        return;
                                    } else {
                                        RxJavaHooks.onError(e);
                                        return;
                                    }
                                }
                            } else {
                                return;
                            }
                        } catch (Throwable e2) {
                            if (once.compareAndSet(false, true)) {
                                set.unsubscribe();
                                s.onError(e2);
                                return;
                            } else {
                                RxJavaHooks.onError(e2);
                                return;
                            }
                        }
                    }
                } catch (Throwable e3) {
                    s.onError(e3);
                }
            }
        });
    }

    public static Completable complete() {
        CompletableOnSubscribe cos = RxJavaHooks.onCreate(COMPLETE.onSubscribe);
        return cos == COMPLETE.onSubscribe ? COMPLETE : new Completable(cos, false);
    }

    public static Completable concat(Completable... sources) {
        requireNonNull(sources);
        if (sources.length == 0) {
            return complete();
        }
        if (sources.length == 1) {
            return sources[0];
        }
        return create(new CompletableOnSubscribeConcatArray(sources));
    }

    public static Completable concat(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeConcatIterable(sources));
    }

    public static Completable concat(Observable<? extends Completable> sources) {
        return concat(sources, 2);
    }

    public static Completable concat(Observable<? extends Completable> sources, int prefetch) {
        requireNonNull(sources);
        if (prefetch < 1) {
            throw new IllegalArgumentException("prefetch > 0 required but it was " + prefetch);
        }
        return create(new CompletableOnSubscribeConcat(sources, prefetch));
    }

    public static Completable create(CompletableOnSubscribe onSubscribe) {
        requireNonNull(onSubscribe);
        try {
            return new Completable(onSubscribe);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Throwable ex2) {
            RxJavaHooks.onError(ex2);
            throw toNpe(ex2);
        }
    }

    public static Completable defer(final Func0<? extends Completable> completableFunc0) {
        requireNonNull(completableFunc0);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.5
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                try {
                    Completable c = (Completable) Func0.this.call();
                    if (c == null) {
                        s.onSubscribe(Subscriptions.unsubscribed());
                        s.onError(new NullPointerException("The completable returned is null"));
                    } else {
                        c.unsafeSubscribe(s);
                    }
                } catch (Throwable e) {
                    s.onSubscribe(Subscriptions.unsubscribed());
                    s.onError(e);
                }
            }
        });
    }

    public static Completable error(final Func0<? extends Throwable> errorFunc0) {
        requireNonNull(errorFunc0);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.6
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                Throwable error;
                s.onSubscribe(Subscriptions.unsubscribed());
                try {
                    error = (Throwable) Func0.this.call();
                } catch (Throwable e) {
                    error = e;
                }
                if (error == null) {
                    error = new NullPointerException("The error supplied is null");
                }
                s.onError(error);
            }
        });
    }

    public static Completable error(final Throwable error) {
        requireNonNull(error);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.7
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                s.onSubscribe(Subscriptions.unsubscribed());
                s.onError(error);
            }
        });
    }

    public static Completable fromAction(final Action0 action) {
        requireNonNull(action);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.8
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                BooleanSubscription bs = new BooleanSubscription();
                s.onSubscribe(bs);
                try {
                    Action0.this.call();
                    if (!bs.isUnsubscribed()) {
                        s.onCompleted();
                    }
                } catch (Throwable e) {
                    if (!bs.isUnsubscribed()) {
                        s.onError(e);
                    }
                }
            }
        });
    }

    public static Completable fromCallable(final Callable<?> callable) {
        requireNonNull(callable);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.9
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                BooleanSubscription bs = new BooleanSubscription();
                s.onSubscribe(bs);
                try {
                    callable.call();
                    if (!bs.isUnsubscribed()) {
                        s.onCompleted();
                    }
                } catch (Throwable e) {
                    if (!bs.isUnsubscribed()) {
                        s.onError(e);
                    }
                }
            }
        });
    }

    public static Completable fromFuture(Future<?> future) {
        requireNonNull(future);
        return fromObservable(Observable.from(future));
    }

    public static Completable fromObservable(final Observable<?> flowable) {
        requireNonNull(flowable);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.10
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber cs) {
                Subscriber<Object> subscriber = new Subscriber<Object>() { // from class: rx.Completable.10.1
                    @Override // p045rx.Observer
                    public void onCompleted() {
                        cs.onCompleted();
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable t) {
                        cs.onError(t);
                    }

                    @Override // p045rx.Observer
                    public void onNext(Object t) {
                    }
                };
                cs.onSubscribe(subscriber);
                Observable.this.unsafeSubscribe(subscriber);
            }
        });
    }

    public static Completable fromSingle(final Single<?> single) {
        requireNonNull(single);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.11
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                SingleSubscriber<Object> te = new SingleSubscriber<Object>() { // from class: rx.Completable.11.1
                    @Override // p045rx.SingleSubscriber
                    public void onError(Throwable e) {
                        s.onError(e);
                    }

                    @Override // p045rx.SingleSubscriber
                    public void onSuccess(Object value) {
                        s.onCompleted();
                    }
                };
                s.onSubscribe(te);
                Single.this.subscribe(te);
            }
        });
    }

    public static Completable merge(Completable... sources) {
        requireNonNull(sources);
        if (sources.length == 0) {
            return complete();
        }
        if (sources.length == 1) {
            return sources[0];
        }
        return create(new CompletableOnSubscribeMergeArray(sources));
    }

    public static Completable merge(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeMergeIterable(sources));
    }

    public static Completable merge(Observable<? extends Completable> sources) {
        return merge0(sources, Integer.MAX_VALUE, false);
    }

    public static Completable merge(Observable<? extends Completable> sources, int maxConcurrency) {
        return merge0(sources, maxConcurrency, false);
    }

    protected static Completable merge0(Observable<? extends Completable> sources, int maxConcurrency, boolean delayErrors) {
        requireNonNull(sources);
        if (maxConcurrency < 1) {
            throw new IllegalArgumentException("maxConcurrency > 0 required but it was " + maxConcurrency);
        }
        return create(new CompletableOnSubscribeMerge(sources, maxConcurrency, delayErrors));
    }

    public static Completable mergeDelayError(Completable... sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeMergeDelayErrorArray(sources));
    }

    public static Completable mergeDelayError(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeMergeDelayErrorIterable(sources));
    }

    public static Completable mergeDelayError(Observable<? extends Completable> sources) {
        return merge0(sources, Integer.MAX_VALUE, true);
    }

    public static Completable mergeDelayError(Observable<? extends Completable> sources, int maxConcurrency) {
        return merge0(sources, maxConcurrency, true);
    }

    public static Completable never() {
        CompletableOnSubscribe cos = RxJavaHooks.onCreate(NEVER.onSubscribe);
        return cos == NEVER.onSubscribe ? NEVER : new Completable(cos, false);
    }

    static <T> T requireNonNull(T o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return o;
    }

    public static Completable timer(long delay, TimeUnit unit) {
        return timer(delay, unit, Schedulers.computation());
    }

    public static Completable timer(final long delay, final TimeUnit unit, final Scheduler scheduler) {
        requireNonNull(unit);
        requireNonNull(scheduler);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.12
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
                s.onSubscribe(mad);
                if (!mad.isUnsubscribed()) {
                    final Scheduler.Worker w = Scheduler.this.createWorker();
                    mad.set(w);
                    w.schedule(new Action0() { // from class: rx.Completable.12.1
                        @Override // p045rx.functions.Action0
                        public void call() {
                            try {
                                s.onCompleted();
                            } finally {
                                w.unsubscribe();
                            }
                        }
                    }, delay, unit);
                }
            }
        });
    }

    static NullPointerException toNpe(Throwable ex) {
        NullPointerException npe = new NullPointerException("Actually not, but can't pass out an exception otherwise...");
        npe.initCause(ex);
        return npe;
    }

    public static <R> Completable using(Func0<R> resourceFunc0, Func1<? super R, ? extends Completable> completableFunc1, Action1<? super R> disposer) {
        return using(resourceFunc0, completableFunc1, disposer, true);
    }

    public static <R> Completable using(final Func0<R> resourceFunc0, final Func1<? super R, ? extends Completable> completableFunc1, final Action1<? super R> disposer, final boolean eager) {
        requireNonNull(resourceFunc0);
        requireNonNull(completableFunc1);
        requireNonNull(disposer);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.13
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                try {
                    Object call = Func0.this.call();
                    try {
                        Completable cs = (Completable) completableFunc1.call(call);
                        if (cs == null) {
                            try {
                                disposer.call(call);
                                s.onSubscribe(Subscriptions.unsubscribed());
                                s.onError(new NullPointerException("The completable supplied is null"));
                                return;
                            } catch (Throwable ex) {
                                Exceptions.throwIfFatal(ex);
                                s.onSubscribe(Subscriptions.unsubscribed());
                                s.onError(new CompositeException(Arrays.asList(new NullPointerException("The completable supplied is null"), ex)));
                                return;
                            }
                        }
                        AtomicBoolean once = new AtomicBoolean();
                        cs.unsafeSubscribe(new AnonymousClass1(once, call, s));
                    } catch (Throwable e) {
                        try {
                            disposer.call(call);
                            Exceptions.throwIfFatal(e);
                            s.onSubscribe(Subscriptions.unsubscribed());
                            s.onError(e);
                        } catch (Throwable ex2) {
                            Exceptions.throwIfFatal(e);
                            Exceptions.throwIfFatal(ex2);
                            s.onSubscribe(Subscriptions.unsubscribed());
                            s.onError(new CompositeException(Arrays.asList(e, ex2)));
                        }
                    }
                } catch (Throwable e2) {
                    s.onSubscribe(Subscriptions.unsubscribed());
                    s.onError(e2);
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            /* renamed from: rx.Completable$13$1  reason: invalid class name */
            /* loaded from: classes.dex */
            public class AnonymousClass1 implements CompletableSubscriber {

                /* renamed from: d */
                Subscription f1579d;
                final /* synthetic */ AtomicBoolean val$once;
                final /* synthetic */ Object val$resource;
                final /* synthetic */ CompletableSubscriber val$s;

                AnonymousClass1(AtomicBoolean atomicBoolean, Object obj, CompletableSubscriber completableSubscriber) {
                    this.val$once = atomicBoolean;
                    this.val$resource = obj;
                    this.val$s = completableSubscriber;
                }

                void dispose() {
                    this.f1579d.unsubscribe();
                    if (this.val$once.compareAndSet(false, true)) {
                        try {
                            disposer.call(this.val$resource);
                        } catch (Throwable ex) {
                            RxJavaHooks.onError(ex);
                        }
                    }
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onCompleted() {
                    if (eager && this.val$once.compareAndSet(false, true)) {
                        try {
                            disposer.call(this.val$resource);
                        } catch (Throwable ex) {
                            this.val$s.onError(ex);
                            return;
                        }
                    }
                    this.val$s.onCompleted();
                    if (!eager) {
                        dispose();
                    }
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onError(Throwable e) {
                    if (eager && this.val$once.compareAndSet(false, true)) {
                        try {
                            disposer.call(this.val$resource);
                        } catch (Throwable ex) {
                            e = new CompositeException(Arrays.asList(e, ex));
                        }
                    }
                    this.val$s.onError(e);
                    if (!eager) {
                        dispose();
                    }
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onSubscribe(Subscription d) {
                    this.f1579d = d;
                    this.val$s.onSubscribe(Subscriptions.create(new Action0() { // from class: rx.Completable.13.1.1
                        @Override // p045rx.functions.Action0
                        public void call() {
                            AnonymousClass1.this.dispose();
                        }
                    }));
                }
            }
        });
    }

    protected Completable(CompletableOnSubscribe onSubscribe) {
        this.onSubscribe = RxJavaHooks.onCreate(onSubscribe);
    }

    private Completable(CompletableOnSubscribe onSubscribe, boolean useHook) {
        this.onSubscribe = useHook ? RxJavaHooks.onCreate(onSubscribe) : onSubscribe;
    }

    public final Completable ambWith(Completable other) {
        requireNonNull(other);
        return amb(this, other);
    }

    public final void await() {
        final CountDownLatch cdl = new CountDownLatch(1);
        final Throwable[] err = new Throwable[1];
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.14
            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                err[0] = e;
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
            }
        });
        if (cdl.getCount() == 0) {
            if (err[0] != null) {
                Exceptions.propagate(err[0]);
            }
        } else {
            try {
                cdl.await();
                if (err[0] != null) {
                    Exceptions.propagate(err[0]);
                }
            } catch (InterruptedException ex) {
                throw Exceptions.propagate(ex);
            }
        }
    }

    public final boolean await(long timeout, TimeUnit unit) {
        boolean b = true;
        requireNonNull(unit);
        final CountDownLatch cdl = new CountDownLatch(1);
        final Throwable[] err = new Throwable[1];
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.15
            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                err[0] = e;
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
            }
        });
        if (cdl.getCount() == 0) {
            if (err[0] != null) {
                Exceptions.propagate(err[0]);
            }
        } else {
            try {
                b = cdl.await(timeout, unit);
                if (b && err[0] != null) {
                    Exceptions.propagate(err[0]);
                }
            } catch (InterruptedException ex) {
                throw Exceptions.propagate(ex);
            }
        }
        return b;
    }

    public final Completable compose(CompletableTransformer transformer) {
        return (Completable) m2139to(transformer);
    }

    public final <T> Observable<T> andThen(Observable<T> next) {
        requireNonNull(next);
        return next.delaySubscription(toObservable());
    }

    public final <T> Single<T> andThen(Single<T> next) {
        requireNonNull(next);
        return next.delaySubscription(toObservable());
    }

    public final Completable andThen(Completable next) {
        return concatWith(next);
    }

    public final Completable concatWith(Completable other) {
        requireNonNull(other);
        return concat(this, other);
    }

    public final Completable delay(long delay, TimeUnit unit) {
        return delay(delay, unit, Schedulers.computation(), false);
    }

    public final Completable delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return delay(delay, unit, scheduler, false);
    }

    public final Completable delay(final long delay, final TimeUnit unit, final Scheduler scheduler, final boolean delayError) {
        requireNonNull(unit);
        requireNonNull(scheduler);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.16
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                final CompositeSubscription set = new CompositeSubscription();
                final Scheduler.Worker w = scheduler.createWorker();
                set.add(w);
                Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.16.1
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        set.add(w.schedule(new Action0() { // from class: rx.Completable.16.1.1
                            @Override // p045rx.functions.Action0
                            public void call() {
                                try {
                                    s.onCompleted();
                                } finally {
                                    w.unsubscribe();
                                }
                            }
                        }, delay, unit));
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onError(final Throwable e) {
                        if (delayError) {
                            set.add(w.schedule(new Action0() { // from class: rx.Completable.16.1.2
                                @Override // p045rx.functions.Action0
                                public void call() {
                                    try {
                                        s.onError(e);
                                    } finally {
                                        w.unsubscribe();
                                    }
                                }
                            }, delay, unit));
                        } else {
                            s.onError(e);
                        }
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onSubscribe(Subscription d) {
                        set.add(d);
                        s.onSubscribe(set);
                    }
                });
            }
        });
    }

    @Deprecated
    public final Completable doOnComplete(Action0 onComplete) {
        return doOnCompleted(onComplete);
    }

    public final Completable doOnCompleted(Action0 onCompleted) {
        return doOnLifecycle(Actions.empty(), Actions.empty(), onCompleted, Actions.empty(), Actions.empty());
    }

    public final Completable doOnUnsubscribe(Action0 onUnsubscribe) {
        return doOnLifecycle(Actions.empty(), Actions.empty(), Actions.empty(), Actions.empty(), onUnsubscribe);
    }

    public final Completable doOnError(Action1<? super Throwable> onError) {
        return doOnLifecycle(Actions.empty(), onError, Actions.empty(), Actions.empty(), Actions.empty());
    }

    protected final Completable doOnLifecycle(Action1<? super Subscription> onSubscribe, Action1<? super Throwable> onError, Action0 onComplete, Action0 onAfterComplete, Action0 onUnsubscribe) {
        requireNonNull(onSubscribe);
        requireNonNull(onError);
        requireNonNull(onComplete);
        requireNonNull(onAfterComplete);
        requireNonNull(onUnsubscribe);
        return create(new C091717(onComplete, onAfterComplete, onError, onSubscribe, onUnsubscribe));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: rx.Completable$17 */
    /* loaded from: classes.dex */
    public class C091717 implements CompletableOnSubscribe {
        final /* synthetic */ Action0 val$onAfterComplete;
        final /* synthetic */ Action0 val$onComplete;
        final /* synthetic */ Action1 val$onError;
        final /* synthetic */ Action1 val$onSubscribe;
        final /* synthetic */ Action0 val$onUnsubscribe;

        C091717(Action0 action0, Action0 action02, Action1 action1, Action1 action12, Action0 action03) {
            this.val$onComplete = action0;
            this.val$onAfterComplete = action02;
            this.val$onError = action1;
            this.val$onSubscribe = action12;
            this.val$onUnsubscribe = action03;
        }

        @Override // p045rx.functions.Action1
        public void call(final CompletableSubscriber s) {
            Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.17.1
                @Override // rx.Completable.CompletableSubscriber
                public void onCompleted() {
                    try {
                        C091717.this.val$onComplete.call();
                        s.onCompleted();
                        try {
                            C091717.this.val$onAfterComplete.call();
                        } catch (Throwable e) {
                            RxJavaHooks.onError(e);
                        }
                    } catch (Throwable e2) {
                        s.onError(e2);
                    }
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onError(Throwable e) {
                    try {
                        C091717.this.val$onError.call(e);
                    } catch (Throwable ex) {
                        e = new CompositeException(Arrays.asList(e, ex));
                    }
                    s.onError(e);
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onSubscribe(final Subscription d) {
                    try {
                        C091717.this.val$onSubscribe.call(d);
                        s.onSubscribe(Subscriptions.create(new Action0() { // from class: rx.Completable.17.1.1
                            @Override // p045rx.functions.Action0
                            public void call() {
                                try {
                                    C091717.this.val$onUnsubscribe.call();
                                } catch (Throwable e) {
                                    RxJavaHooks.onError(e);
                                }
                                d.unsubscribe();
                            }
                        }));
                    } catch (Throwable ex) {
                        d.unsubscribe();
                        s.onSubscribe(Subscriptions.unsubscribed());
                        s.onError(ex);
                    }
                }
            });
        }
    }

    public final Completable doOnSubscribe(Action1<? super Subscription> onSubscribe) {
        return doOnLifecycle(onSubscribe, Actions.empty(), Actions.empty(), Actions.empty(), Actions.empty());
    }

    public final Completable doOnTerminate(final Action0 onTerminate) {
        return doOnLifecycle(Actions.empty(), new Action1<Throwable>() { // from class: rx.Completable.18
            @Override // p045rx.functions.Action1
            public void call(Throwable e) {
                onTerminate.call();
            }
        }, onTerminate, Actions.empty(), Actions.empty());
    }

    @Deprecated
    public final Completable endWith(Completable other) {
        return andThen(other);
    }

    @Deprecated
    public final <T> Observable<T> endWith(Observable<T> next) {
        return andThen(next);
    }

    public final Completable doAfterTerminate(Action0 onAfterComplete) {
        return doOnLifecycle(Actions.empty(), Actions.empty(), Actions.empty(), onAfterComplete, Actions.empty());
    }

    public final Throwable get() {
        final CountDownLatch cdl = new CountDownLatch(1);
        final Throwable[] err = new Throwable[1];
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.19
            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                err[0] = e;
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
            }
        });
        if (cdl.getCount() == 0) {
            return err[0];
        }
        try {
            cdl.await();
            return err[0];
        } catch (InterruptedException ex) {
            throw Exceptions.propagate(ex);
        }
    }

    public final Throwable get(long timeout, TimeUnit unit) {
        requireNonNull(unit);
        final CountDownLatch cdl = new CountDownLatch(1);
        final Throwable[] err = new Throwable[1];
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.20
            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                err[0] = e;
                cdl.countDown();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
            }
        });
        if (cdl.getCount() == 0) {
            return err[0];
        }
        try {
            boolean b = cdl.await(timeout, unit);
            if (b) {
                return err[0];
            }
            Exceptions.propagate(new TimeoutException());
            return null;
        } catch (InterruptedException ex) {
            throw Exceptions.propagate(ex);
        }
    }

    public final Completable lift(final CompletableOperator onLift) {
        requireNonNull(onLift);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.21
            @Override // p045rx.functions.Action1
            public void call(CompletableSubscriber s) {
                try {
                    CompletableOperator onLiftDecorated = RxJavaHooks.onCompletableLift(onLift);
                    CompletableSubscriber sw = onLiftDecorated.call(s);
                    Completable.this.unsafeSubscribe(sw);
                } catch (NullPointerException ex) {
                    throw ex;
                } catch (Throwable ex2) {
                    throw Completable.toNpe(ex2);
                }
            }
        });
    }

    public final Completable mergeWith(Completable other) {
        requireNonNull(other);
        return merge(this, other);
    }

    public final Completable observeOn(final Scheduler scheduler) {
        requireNonNull(scheduler);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.22
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                final SubscriptionList ad = new SubscriptionList();
                final Scheduler.Worker w = scheduler.createWorker();
                ad.add(w);
                s.onSubscribe(ad);
                Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.22.1
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        w.schedule(new Action0() { // from class: rx.Completable.22.1.1
                            @Override // p045rx.functions.Action0
                            public void call() {
                                try {
                                    s.onCompleted();
                                } finally {
                                    ad.unsubscribe();
                                }
                            }
                        });
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onError(final Throwable e) {
                        w.schedule(new Action0() { // from class: rx.Completable.22.1.2
                            @Override // p045rx.functions.Action0
                            public void call() {
                                try {
                                    s.onError(e);
                                } finally {
                                    ad.unsubscribe();
                                }
                            }
                        });
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onSubscribe(Subscription d) {
                        ad.add(d);
                    }
                });
            }
        });
    }

    public final Completable onErrorComplete() {
        return onErrorComplete(UtilityFunctions.alwaysTrue());
    }

    public final Completable onErrorComplete(final Func1<? super Throwable, Boolean> predicate) {
        requireNonNull(predicate);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.23
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.23.1
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        s.onCompleted();
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onError(Throwable e) {
                        boolean b;
                        try {
                            b = ((Boolean) predicate.call(e)).booleanValue();
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            b = false;
                            e = new CompositeException(Arrays.asList(e, ex));
                        }
                        if (b) {
                            s.onCompleted();
                        } else {
                            s.onError(e);
                        }
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onSubscribe(Subscription d) {
                        s.onSubscribe(d);
                    }
                });
            }
        });
    }

    public final Completable onErrorResumeNext(final Func1<? super Throwable, ? extends Completable> errorMapper) {
        requireNonNull(errorMapper);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.24
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                final SerialSubscription sd = new SerialSubscription();
                Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.24.1
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        s.onCompleted();
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onError(Throwable e) {
                        try {
                            Completable c = (Completable) errorMapper.call(e);
                            if (c == null) {
                                NullPointerException npe = new NullPointerException("The completable returned is null");
                                s.onError(new CompositeException(Arrays.asList(e, npe)));
                            } else {
                                c.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.24.1.1
                                    @Override // rx.Completable.CompletableSubscriber
                                    public void onCompleted() {
                                        s.onCompleted();
                                    }

                                    @Override // rx.Completable.CompletableSubscriber
                                    public void onError(Throwable e2) {
                                        s.onError(e2);
                                    }

                                    @Override // rx.Completable.CompletableSubscriber
                                    public void onSubscribe(Subscription d) {
                                        sd.set(d);
                                    }
                                });
                            }
                        } catch (Throwable ex) {
                            s.onError(new CompositeException(Arrays.asList(e, ex)));
                        }
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onSubscribe(Subscription d) {
                        sd.set(d);
                    }
                });
            }
        });
    }

    public final Completable repeat() {
        return fromObservable(toObservable().repeat());
    }

    public final Completable repeat(long times) {
        return fromObservable(toObservable().repeat(times));
    }

    public final Completable repeatWhen(Func1<? super Observable<? extends Void>, ? extends Observable<?>> handler) {
        requireNonNull(handler);
        return fromObservable(toObservable().repeatWhen(handler));
    }

    public final Completable retry() {
        return fromObservable(toObservable().retry());
    }

    public final Completable retry(Func2<Integer, Throwable, Boolean> predicate) {
        return fromObservable(toObservable().retry(predicate));
    }

    public final Completable retry(long times) {
        return fromObservable(toObservable().retry(times));
    }

    public final Completable retryWhen(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> handler) {
        return fromObservable(toObservable().retryWhen(handler));
    }

    public final Completable startWith(Completable other) {
        requireNonNull(other);
        return concat(other, this);
    }

    public final <T> Observable<T> startWith(Observable<T> other) {
        requireNonNull(other);
        return toObservable().startWith((Observable) other);
    }

    public final Subscription subscribe() {
        final MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.25
            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                mad.unsubscribe();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                RxJavaHooks.onError(e);
                mad.unsubscribe();
                Completable.deliverUncaughtException(e);
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
                mad.set(d);
            }
        });
        return mad;
    }

    public final Subscription subscribe(final Action0 onComplete) {
        requireNonNull(onComplete);
        final MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.26
            boolean done;

            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                if (!this.done) {
                    this.done = true;
                    try {
                        onComplete.call();
                    } catch (Throwable e) {
                        try {
                            RxJavaHooks.onError(e);
                            Completable.deliverUncaughtException(e);
                        } finally {
                            mad.unsubscribe();
                        }
                    }
                }
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                RxJavaHooks.onError(e);
                mad.unsubscribe();
                Completable.deliverUncaughtException(e);
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
                mad.set(d);
            }
        });
        return mad;
    }

    public final Subscription subscribe(final Action0 onComplete, final Action1<? super Throwable> onError) {
        requireNonNull(onComplete);
        requireNonNull(onError);
        final MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.27
            boolean done;

            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                if (!this.done) {
                    this.done = true;
                    try {
                        onComplete.call();
                        mad.unsubscribe();
                    } catch (Throwable e) {
                        callOnError(e);
                    }
                }
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                if (!this.done) {
                    this.done = true;
                    callOnError(e);
                } else {
                    RxJavaHooks.onError(e);
                    Completable.deliverUncaughtException(e);
                }
            }

            void callOnError(Throwable e) {
                Throwable e2;
                try {
                    onError.call(e);
                    mad.unsubscribe();
                } catch (Throwable ex) {
                    try {
                        e2 = new CompositeException(Arrays.asList(e, ex));
                    } catch (Throwable th) {
                        th = th;
                    }
                    try {
                        RxJavaHooks.onError(e2);
                        Completable.deliverUncaughtException(e2);
                        mad.unsubscribe();
                    } catch (Throwable th2) {
                        th = th2;
                        mad.unsubscribe();
                        throw th;
                    }
                }
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
                mad.set(d);
            }
        });
        return mad;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deliverUncaughtException(Throwable e) {
        Thread thread = Thread.currentThread();
        thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
    }

    public final void unsafeSubscribe(CompletableSubscriber s) {
        requireNonNull(s);
        try {
            CompletableOnSubscribe onSubscribeDecorated = RxJavaHooks.onCompletableStart(this, this.onSubscribe);
            onSubscribeDecorated.call(s);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Throwable ex2) {
            Exceptions.throwIfFatal(ex2);
            Throwable ex3 = RxJavaHooks.onCompletableError(ex2);
            RxJavaHooks.onError(ex3);
            throw toNpe(ex3);
        }
    }

    public final void subscribe(CompletableSubscriber s) {
        if (!(s instanceof SafeCompletableSubscriber)) {
            s = new SafeCompletableSubscriber(s);
        }
        unsafeSubscribe(s);
    }

    public final <T> void unsafeSubscribe(Subscriber<T> s) {
        unsafeSubscribe(s, true);
    }

    private final <T> void unsafeSubscribe(final Subscriber<T> s, boolean callOnStart) {
        requireNonNull(s);
        if (callOnStart) {
            try {
                s.onStart();
            } catch (NullPointerException ex) {
                throw ex;
            } catch (Throwable ex2) {
                Exceptions.throwIfFatal(ex2);
                Throwable ex3 = RxJavaHooks.onObservableError(ex2);
                RxJavaHooks.onError(ex3);
                throw toNpe(ex3);
            }
        }
        unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.28
            @Override // rx.Completable.CompletableSubscriber
            public void onCompleted() {
                s.onCompleted();
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onError(Throwable e) {
                s.onError(e);
            }

            @Override // rx.Completable.CompletableSubscriber
            public void onSubscribe(Subscription d) {
                s.add(d);
            }
        });
        RxJavaHooks.onObservableReturn(s);
    }

    public final <T> void subscribe(Subscriber<T> s) {
        s.onStart();
        if (!(s instanceof SafeSubscriber)) {
            s = new SafeSubscriber<>(s);
        }
        unsafeSubscribe(s, false);
    }

    public final Completable subscribeOn(final Scheduler scheduler) {
        requireNonNull(scheduler);
        return create(new CompletableOnSubscribe() { // from class: rx.Completable.29
            @Override // p045rx.functions.Action1
            public void call(final CompletableSubscriber s) {
                final Scheduler.Worker w = scheduler.createWorker();
                w.schedule(new Action0() { // from class: rx.Completable.29.1
                    @Override // p045rx.functions.Action0
                    public void call() {
                        try {
                            Completable.this.unsafeSubscribe(s);
                        } finally {
                            w.unsubscribe();
                        }
                    }
                });
            }
        });
    }

    public final Completable timeout(long timeout, TimeUnit unit) {
        return timeout0(timeout, unit, Schedulers.computation(), null);
    }

    public final Completable timeout(long timeout, TimeUnit unit, Completable other) {
        requireNonNull(other);
        return timeout0(timeout, unit, Schedulers.computation(), other);
    }

    public final Completable timeout(long timeout, TimeUnit unit, Scheduler scheduler) {
        return timeout0(timeout, unit, scheduler, null);
    }

    public final Completable timeout(long timeout, TimeUnit unit, Scheduler scheduler, Completable other) {
        requireNonNull(other);
        return timeout0(timeout, unit, scheduler, other);
    }

    public final Completable timeout0(long timeout, TimeUnit unit, Scheduler scheduler, Completable other) {
        requireNonNull(unit);
        requireNonNull(scheduler);
        return create(new CompletableOnSubscribeTimeout(this, timeout, unit, scheduler, other));
    }

    /* renamed from: to */
    public final <U> U m2139to(Func1<? super Completable, U> converter) {
        return converter.call(this);
    }

    public final <T> Observable<T> toObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() { // from class: rx.Completable.30
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(Subscriber<? super T> s) {
                Completable.this.unsafeSubscribe(s);
            }
        });
    }

    public final <T> Single<T> toSingle(final Func0<? extends T> completionValueFunc0) {
        requireNonNull(completionValueFunc0);
        return Single.create(new Single.OnSubscribe<T>() { // from class: rx.Completable.31
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(final SingleSubscriber<? super T> s) {
                Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.31.1
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        try {
                            Object call = completionValueFunc0.call();
                            if (call == null) {
                                s.onError(new NullPointerException("The value supplied is null"));
                            } else {
                                s.onSuccess(call);
                            }
                        } catch (Throwable e) {
                            s.onError(e);
                        }
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onError(Throwable e) {
                        s.onError(e);
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onSubscribe(Subscription d) {
                        s.add(d);
                    }
                });
            }
        });
    }

    public final <T> Single<T> toSingleDefault(final T completionValue) {
        requireNonNull(completionValue);
        return toSingle(new Func0<T>() { // from class: rx.Completable.32
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            public T call() {
                return (T) completionValue;
            }
        });
    }

    /* renamed from: rx.Completable$33 */
    /* loaded from: classes.dex */
    class C093533 implements CompletableOnSubscribe {
        final /* synthetic */ Scheduler val$scheduler;

        C093533(Scheduler scheduler) {
            this.val$scheduler = scheduler;
        }

        @Override // p045rx.functions.Action1
        public void call(final CompletableSubscriber s) {
            Completable.this.unsafeSubscribe(new CompletableSubscriber() { // from class: rx.Completable.33.1
                @Override // rx.Completable.CompletableSubscriber
                public void onCompleted() {
                    s.onCompleted();
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onError(Throwable e) {
                    s.onError(e);
                }

                @Override // rx.Completable.CompletableSubscriber
                public void onSubscribe(final Subscription d) {
                    s.onSubscribe(Subscriptions.create(new Action0() { // from class: rx.Completable.33.1.1
                        @Override // p045rx.functions.Action0
                        public void call() {
                            final Scheduler.Worker w = C093533.this.val$scheduler.createWorker();
                            w.schedule(new Action0() { // from class: rx.Completable.33.1.1.1
                                @Override // p045rx.functions.Action0
                                public void call() {
                                    try {
                                        d.unsubscribe();
                                    } finally {
                                        w.unsubscribe();
                                    }
                                }
                            });
                        }
                    }));
                }
            });
        }
    }

    public final Completable unsubscribeOn(Scheduler scheduler) {
        requireNonNull(scheduler);
        return create(new C093533(scheduler));
    }
}
