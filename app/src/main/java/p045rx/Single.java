package p045rx;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import p045rx.Completable;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.annotations.Beta;
import p045rx.annotations.Experimental;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.OnErrorNotImplementedException;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.functions.Func3;
import p045rx.functions.Func4;
import p045rx.functions.Func5;
import p045rx.functions.Func6;
import p045rx.functions.Func7;
import p045rx.functions.Func8;
import p045rx.functions.Func9;
import p045rx.functions.FuncN;
import p045rx.internal.operators.CompletableFlatMapSingleToCompletable;
import p045rx.internal.operators.OnSubscribeDoOnEach;
import p045rx.internal.operators.OnSubscribeToObservableFuture;
import p045rx.internal.operators.OperatorDelay;
import p045rx.internal.operators.OperatorDoOnSubscribe;
import p045rx.internal.operators.OperatorDoOnUnsubscribe;
import p045rx.internal.operators.OperatorObserveOn;
import p045rx.internal.operators.OperatorOnErrorResumeNextViaFunction;
import p045rx.internal.operators.OperatorTimeout;
import p045rx.internal.operators.SingleDoAfterTerminate;
import p045rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther;
import p045rx.internal.operators.SingleOnSubscribeMap;
import p045rx.internal.operators.SingleOnSubscribeUsing;
import p045rx.internal.operators.SingleOperatorOnErrorResumeNext;
import p045rx.internal.operators.SingleOperatorZip;
import p045rx.internal.producers.SingleDelayedProducer;
import p045rx.internal.util.ScalarSynchronousSingle;
import p045rx.internal.util.UtilityFunctions;
import p045rx.observers.SafeSubscriber;
import p045rx.observers.SerializedSubscriber;
import p045rx.plugins.RxJavaHooks;
import p045rx.schedulers.Schedulers;
import p045rx.singles.BlockingSingle;
import p045rx.subscriptions.Subscriptions;

@Beta
/* loaded from: classes.dex */
public class Single<T> {
    final Observable.OnSubscribe<T> onSubscribe;

    /* loaded from: classes.dex */
    public interface OnSubscribe<T> extends Action1<SingleSubscriber<? super T>> {
    }

    /* loaded from: classes.dex */
    public interface Transformer<T, R> extends Func1<Single<T>, Single<R>> {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Single(OnSubscribe<T> f) {
        final OnSubscribe<T> g = RxJavaHooks.onCreate(f);
        this.onSubscribe = new Observable.OnSubscribe<T>() { // from class: rx.Single.1
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(final Subscriber<? super T> child) {
                final SingleDelayedProducer<T> producer = new SingleDelayedProducer<>(child);
                child.setProducer(producer);
                SingleSubscriber<T> ss = new SingleSubscriber<T>() { // from class: rx.Single.1.1
                    @Override // p045rx.SingleSubscriber
                    public void onSuccess(T value) {
                        producer.setValue(value);
                    }

                    @Override // p045rx.SingleSubscriber
                    public void onError(Throwable error) {
                        child.onError(error);
                    }
                };
                child.add(ss);
                g.call(ss);
            }
        };
    }

    private Single(Observable.OnSubscribe<T> f) {
        this.onSubscribe = RxJavaHooks.onCreate(f);
    }

    public static <T> Single<T> create(OnSubscribe<T> f) {
        return new Single<>(f);
    }

    @Experimental
    public final <R> Single<R> lift(final Observable.Operator<? extends R, ? super T> lift) {
        return new Single<>(new Observable.OnSubscribe<R>() { // from class: rx.Single.2
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(Subscriber<? super R> o) {
                try {
                    Subscriber<? super T> st = RxJavaHooks.onSingleLift(lift).call(o);
                    try {
                        st.onStart();
                        Single.this.onSubscribe.call(st);
                    } catch (Throwable e) {
                        Exceptions.throwOrReport(e, st);
                    }
                } catch (Throwable e2) {
                    Exceptions.throwOrReport(e2, o);
                }
            }
        });
    }

    public <R> Single<R> compose(Transformer<? super T, ? extends R> transformer) {
        return (Single) transformer.call(this);
    }

    private static <T> Observable<T> asObservable(Single<T> t) {
        return Observable.create(t.onSubscribe);
    }

    private Single<Observable<T>> nest() {
        return just(asObservable(this));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2) {
        return Observable.concat(asObservable(t1), asObservable(t2));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8, Single<? extends T> t9) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8), asObservable(t9));
    }

    public static <T> Single<T> error(final Throwable exception) {
        return create(new OnSubscribe<T>() { // from class: rx.Single.3
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(SingleSubscriber<? super T> te) {
                te.onError(exception);
            }
        });
    }

    public static <T> Single<T> from(Future<? extends T> future) {
        return new Single<>(OnSubscribeToObservableFuture.toObservableFuture(future));
    }

    public static <T> Single<T> from(Future<? extends T> future, long timeout, TimeUnit unit) {
        return new Single<>(OnSubscribeToObservableFuture.toObservableFuture(future, timeout, unit));
    }

    public static <T> Single<T> from(Future<? extends T> future, Scheduler scheduler) {
        return new Single(OnSubscribeToObservableFuture.toObservableFuture(future)).subscribeOn(scheduler);
    }

    @Beta
    public static <T> Single<T> fromCallable(final Callable<? extends T> func) {
        return create(new OnSubscribe<T>() { // from class: rx.Single.4
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(SingleSubscriber<? super T> singleSubscriber) {
                try {
                    singleSubscriber.onSuccess((Object) func.call());
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    singleSubscriber.onError(t);
                }
            }
        });
    }

    public static <T> Single<T> just(T value) {
        return ScalarSynchronousSingle.create(value);
    }

    public static <T> Single<T> merge(Single<? extends Single<? extends T>> source) {
        return source instanceof ScalarSynchronousSingle ? ((ScalarSynchronousSingle) source).scalarFlatMap(UtilityFunctions.identity()) : create(new OnSubscribe<T>() { // from class: rx.Single.5
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(final SingleSubscriber<? super T> child) {
                SingleSubscriber<Single<? extends T>> parent = new SingleSubscriber<Single<? extends T>>() { // from class: rx.Single.5.1
                    @Override // p045rx.SingleSubscriber
                    public /* bridge */ /* synthetic */ void onSuccess(Object x0) {
                        onSuccess((Single) ((Single) x0));
                    }

                    public void onSuccess(Single<? extends T> innerSingle) {
                        innerSingle.subscribe(child);
                    }

                    @Override // p045rx.SingleSubscriber
                    public void onError(Throwable error) {
                        child.onError(error);
                    }
                };
                child.add(parent);
                Single.this.subscribe(parent);
            }
        });
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2) {
        return Observable.merge(asObservable(t1), asObservable(t2));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8, Single<? extends T> t9) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8), asObservable(t9));
    }

    public static <T1, T2, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, final Func2<? super T1, ? super T2, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2}, new FuncN<R>() { // from class: rx.Single.6
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func2.this.call(args[0], args[1]);
            }
        });
    }

    public static <T1, T2, T3, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, final Func3<? super T1, ? super T2, ? super T3, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3}, new FuncN<R>() { // from class: rx.Single.7
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func3.this.call(args[0], args[1], args[2]);
            }
        });
    }

    public static <T1, T2, T3, T4, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, final Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4}, new FuncN<R>() { // from class: rx.Single.8
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func4.this.call(args[0], args[1], args[2], args[3]);
            }
        });
    }

    public static <T1, T2, T3, T4, T5, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, final Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5}, new FuncN<R>() { // from class: rx.Single.9
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func5.this.call(args[0], args[1], args[2], args[3], args[4]);
            }
        });
    }

    public static <T1, T2, T3, T4, T5, T6, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, final Func6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6}, new FuncN<R>() { // from class: rx.Single.10
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func6.this.call(args[0], args[1], args[2], args[3], args[4], args[5]);
            }
        });
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Single<? extends T7> s7, final Func7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6, s7}, new FuncN<R>() { // from class: rx.Single.11
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func7.this.call(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            }
        });
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Single<? extends T7> s7, Single<? extends T8> s8, final Func8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6, s7, s8}, new FuncN<R>() { // from class: rx.Single.12
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func8.this.call(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            }
        });
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Single<? extends T7> s7, Single<? extends T8> s8, Single<? extends T9> s9, final Func9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6, s7, s8, s9}, new FuncN<R>() { // from class: rx.Single.13
            @Override // p045rx.functions.FuncN
            public R call(Object... args) {
                return (R) Func9.this.call(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            }
        });
    }

    public static <R> Single<R> zip(Iterable<? extends Single<?>> singles, FuncN<? extends R> zipFunction) {
        Single[] iterableToArray = iterableToArray(singles);
        return SingleOperatorZip.zip(iterableToArray, zipFunction);
    }

    public final Observable<T> concatWith(Single<? extends T> t1) {
        return concat(this, t1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <R> Single<R> flatMap(Func1<? super T, ? extends Single<? extends R>> func) {
        return this instanceof ScalarSynchronousSingle ? ((ScalarSynchronousSingle) this).scalarFlatMap(func) : merge(map(func));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <R> Observable<R> flatMapObservable(Func1<? super T, ? extends Observable<? extends R>> func) {
        return Observable.merge(asObservable(map(func)));
    }

    @Experimental
    public final Completable flatMapCompletable(Func1<? super T, ? extends Completable> func) {
        return Completable.create(new CompletableFlatMapSingleToCompletable(this, func));
    }

    public final <R> Single<R> map(Func1<? super T, ? extends R> func) {
        return create(new SingleOnSubscribeMap(this, func));
    }

    public final Observable<T> mergeWith(Single<? extends T> t1) {
        return merge(this, t1);
    }

    public final Single<T> observeOn(Scheduler scheduler) {
        return this instanceof ScalarSynchronousSingle ? ((ScalarSynchronousSingle) this).scalarScheduleOn(scheduler) : (Single<T>) lift(new OperatorObserveOn(scheduler, false));
    }

    public final Single<T> onErrorReturn(Func1<Throwable, ? extends T> resumeFunction) {
        return (Single<T>) lift(OperatorOnErrorResumeNextViaFunction.withSingle(resumeFunction));
    }

    @Experimental
    public final Single<T> onErrorResumeNext(Single<? extends T> resumeSingleInCaseOfError) {
        return new Single<>(SingleOperatorOnErrorResumeNext.withOther(this, resumeSingleInCaseOfError));
    }

    @Experimental
    public final Single<T> onErrorResumeNext(Func1<Throwable, ? extends Single<? extends T>> resumeFunctionInCaseOfError) {
        return new Single<>(SingleOperatorOnErrorResumeNext.withFunction(this, resumeFunctionInCaseOfError));
    }

    public final Subscription subscribe() {
        return subscribe((Subscriber) new Subscriber<T>() { // from class: rx.Single.14
            @Override // p045rx.Observer
            public final void onCompleted() {
            }

            @Override // p045rx.Observer
            public final void onError(Throwable e) {
                throw new OnErrorNotImplementedException(e);
            }

            @Override // p045rx.Observer
            public final void onNext(T args) {
            }
        });
    }

    public final Subscription subscribe(final Action1<? super T> onSuccess) {
        if (onSuccess == null) {
            throw new IllegalArgumentException("onSuccess can not be null");
        }
        return subscribe((Subscriber) new Subscriber<T>() { // from class: rx.Single.15
            @Override // p045rx.Observer
            public final void onCompleted() {
            }

            @Override // p045rx.Observer
            public final void onError(Throwable e) {
                throw new OnErrorNotImplementedException(e);
            }

            @Override // p045rx.Observer
            public final void onNext(T args) {
                onSuccess.call(args);
            }
        });
    }

    public final Subscription subscribe(final Action1<? super T> onSuccess, final Action1<Throwable> onError) {
        if (onSuccess == null) {
            throw new IllegalArgumentException("onSuccess can not be null");
        }
        if (onError == null) {
            throw new IllegalArgumentException("onError can not be null");
        }
        return subscribe((Subscriber) new Subscriber<T>() { // from class: rx.Single.16
            @Override // p045rx.Observer
            public final void onCompleted() {
            }

            @Override // p045rx.Observer
            public final void onError(Throwable e) {
                onError.call(e);
            }

            @Override // p045rx.Observer
            public final void onNext(T args) {
                onSuccess.call(args);
            }
        });
    }

    public final Subscription unsafeSubscribe(Subscriber<? super T> subscriber) {
        try {
            subscriber.onStart();
            RxJavaHooks.onSingleStart(this, this.onSubscribe).call(subscriber);
            return RxJavaHooks.onSingleReturn(subscriber);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            try {
                subscriber.onError(RxJavaHooks.onSingleError(e));
                return Subscriptions.unsubscribed();
            } catch (Throwable e2) {
                Exceptions.throwIfFatal(e2);
                RuntimeException r = new RuntimeException("Error occurred attempting to subscribe [" + e.getMessage() + "] and then again while trying to pass to onError.", e2);
                RxJavaHooks.onSingleError(r);
                throw r;
            }
        }
    }

    public final Subscription subscribe(final Observer<? super T> observer) {
        if (observer == null) {
            throw new NullPointerException("observer is null");
        }
        return subscribe(new SingleSubscriber<T>() { // from class: rx.Single.17
            @Override // p045rx.SingleSubscriber
            public void onSuccess(T value) {
                observer.onNext(value);
                observer.onCompleted();
            }

            @Override // p045rx.SingleSubscriber
            public void onError(Throwable error) {
                observer.onError(error);
            }
        });
    }

    public final Subscription subscribe(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("observer can not be null");
        }
        if (this.onSubscribe == null) {
            throw new IllegalStateException("onSubscribe function can not be null.");
        }
        subscriber.onStart();
        if (!(subscriber instanceof SafeSubscriber)) {
            subscriber = new SafeSubscriber<>(subscriber);
        }
        try {
            RxJavaHooks.onSingleStart(this, this.onSubscribe).call(subscriber);
            return RxJavaHooks.onSingleReturn(subscriber);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            try {
                subscriber.onError(RxJavaHooks.onSingleError(e));
                return Subscriptions.empty();
            } catch (Throwable e2) {
                Exceptions.throwIfFatal(e2);
                RuntimeException r = new RuntimeException("Error occurred attempting to subscribe [" + e.getMessage() + "] and then again while trying to pass to onError.", e2);
                RxJavaHooks.onSingleError(r);
                throw r;
            }
        }
    }

    public final Subscription subscribe(final SingleSubscriber<? super T> te) {
        Subscriber<T> s = new Subscriber<T>() { // from class: rx.Single.18
            @Override // p045rx.Observer
            public void onCompleted() {
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                te.onError(e);
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
                te.onSuccess(t);
            }
        };
        te.add(s);
        subscribe((Subscriber) s);
        return s;
    }

    public final Single<T> subscribeOn(final Scheduler scheduler) {
        return this instanceof ScalarSynchronousSingle ? ((ScalarSynchronousSingle) this).scalarScheduleOn(scheduler) : create(new OnSubscribe<T>() { // from class: rx.Single.19
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(final SingleSubscriber<? super T> t) {
                final Scheduler.Worker w = scheduler.createWorker();
                t.add(w);
                w.schedule(new Action0() { // from class: rx.Single.19.1
                    @Override // p045rx.functions.Action0
                    public void call() {
                        SingleSubscriber<T> ssub = new SingleSubscriber<T>() { // from class: rx.Single.19.1.1
                            @Override // p045rx.SingleSubscriber
                            public void onSuccess(T value) {
                                try {
                                    t.onSuccess(value);
                                } finally {
                                    w.unsubscribe();
                                }
                            }

                            @Override // p045rx.SingleSubscriber
                            public void onError(Throwable error) {
                                try {
                                    t.onError(error);
                                } finally {
                                    w.unsubscribe();
                                }
                            }
                        };
                        t.add(ssub);
                        Single.this.subscribe(ssub);
                    }
                });
            }
        });
    }

    public final Single<T> takeUntil(final Completable other) {
        return (Single<T>) lift((Observable.Operator<T, T>) new Observable.Operator<T, T>() { // from class: rx.Single.20
            @Override // p045rx.functions.Func1
            public /* bridge */ /* synthetic */ Object call(Object x0) {
                return call((Subscriber) ((Subscriber) x0));
            }

            public Subscriber<? super T> call(Subscriber<? super T> child) {
                final Subscriber<T> serial = new SerializedSubscriber<>(child, false);
                final Subscriber subscriber = (Subscriber<T>) new Subscriber<T>(serial, false) { // from class: rx.Single.20.1
                    @Override // p045rx.Observer
                    public void onNext(T t) {
                        serial.onNext(t);
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable e) {
                        try {
                            serial.onError(e);
                        } finally {
                            serial.unsubscribe();
                        }
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        try {
                            serial.onCompleted();
                        } finally {
                            serial.unsubscribe();
                        }
                    }
                };
                Completable.CompletableSubscriber so = new Completable.CompletableSubscriber() { // from class: rx.Single.20.2
                    @Override // rx.Completable.CompletableSubscriber
                    public void onCompleted() {
                        onError(new CancellationException("Stream was canceled before emitting a terminal event."));
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override // rx.Completable.CompletableSubscriber
                    public void onSubscribe(Subscription d) {
                        serial.add(d);
                    }
                };
                serial.add(subscriber);
                child.add(serial);
                other.unsafeSubscribe(so);
                return subscriber;
            }
        });
    }

    public final <E> Single<T> takeUntil(final Observable<? extends E> other) {
        return (Single<T>) lift((Observable.Operator<T, T>) new Observable.Operator<T, T>() { // from class: rx.Single.21
            @Override // p045rx.functions.Func1
            public /* bridge */ /* synthetic */ Object call(Object x0) {
                return call((Subscriber) ((Subscriber) x0));
            }

            /* JADX WARN: Multi-variable type inference failed */
            public Subscriber<? super T> call(Subscriber<? super T> child) {
                final Subscriber<T> serial = new SerializedSubscriber<>(child, false);
                final Subscriber subscriber = (Subscriber<T>) new Subscriber<T>(serial, false) { // from class: rx.Single.21.1
                    @Override // p045rx.Observer
                    public void onNext(T t) {
                        serial.onNext(t);
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable e) {
                        try {
                            serial.onError(e);
                        } finally {
                            serial.unsubscribe();
                        }
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        try {
                            serial.onCompleted();
                        } finally {
                            serial.unsubscribe();
                        }
                    }
                };
                Subscription subscription = new Subscriber<E>() { // from class: rx.Single.21.2
                    @Override // p045rx.Observer
                    public void onCompleted() {
                        onError(new CancellationException("Stream was canceled before emitting a terminal event."));
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override // p045rx.Observer
                    public void onNext(E e) {
                        onError(new CancellationException("Stream was canceled before emitting a terminal event."));
                    }
                };
                serial.add(subscriber);
                serial.add(subscription);
                child.add(serial);
                other.unsafeSubscribe(subscription);
                return subscriber;
            }
        });
    }

    public final <E> Single<T> takeUntil(final Single<? extends E> other) {
        return (Single<T>) lift((Observable.Operator<T, T>) new Observable.Operator<T, T>() { // from class: rx.Single.22
            @Override // p045rx.functions.Func1
            public /* bridge */ /* synthetic */ Object call(Object x0) {
                return call((Subscriber) ((Subscriber) x0));
            }

            /* JADX WARN: Multi-variable type inference failed */
            public Subscriber<? super T> call(Subscriber<? super T> child) {
                final Subscriber<T> serial = new SerializedSubscriber<>(child, false);
                final Subscriber subscriber = (Subscriber<T>) new Subscriber<T>(serial, false) { // from class: rx.Single.22.1
                    @Override // p045rx.Observer
                    public void onNext(T t) {
                        serial.onNext(t);
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable e) {
                        try {
                            serial.onError(e);
                        } finally {
                            serial.unsubscribe();
                        }
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        try {
                            serial.onCompleted();
                        } finally {
                            serial.unsubscribe();
                        }
                    }
                };
                Subscription subscription = new SingleSubscriber<E>() { // from class: rx.Single.22.2
                    @Override // p045rx.SingleSubscriber
                    public void onSuccess(E value) {
                        onError(new CancellationException("Stream was canceled before emitting a terminal event."));
                    }

                    @Override // p045rx.SingleSubscriber
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }
                };
                serial.add(subscriber);
                serial.add(subscription);
                child.add(serial);
                other.subscribe((SingleSubscriber) subscription);
                return subscriber;
            }
        });
    }

    public final Observable<T> toObservable() {
        return asObservable(this);
    }

    @Experimental
    public final Completable toCompletable() {
        return Completable.fromSingle(this);
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit) {
        return timeout(timeout, timeUnit, null, Schedulers.computation());
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler) {
        return timeout(timeout, timeUnit, null, scheduler);
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit, Single<? extends T> other) {
        return timeout(timeout, timeUnit, other, Schedulers.computation());
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit, Single<? extends T> other, Scheduler scheduler) {
        if (other == null) {
            other = error(new TimeoutException());
        }
        return (Single<T>) lift(new OperatorTimeout(timeout, timeUnit, asObservable(other), scheduler));
    }

    @Experimental
    public final BlockingSingle<T> toBlocking() {
        return BlockingSingle.from(this);
    }

    public final <T2, R> Single<R> zipWith(Single<? extends T2> other, Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return zip(this, other, zipFunction);
    }

    @Experimental
    public final Single<T> doOnError(final Action1<Throwable> onError) {
        Observer<T> observer = new Observer<T>() { // from class: rx.Single.23
            @Override // p045rx.Observer
            public void onCompleted() {
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                onError.call(e);
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
            }
        };
        return Observable.create(new OnSubscribeDoOnEach(toObservable(), observer)).toSingle();
    }

    @Experimental
    public final Single<T> doOnSuccess(final Action1<? super T> onSuccess) {
        Observer<T> observer = new Observer<T>() { // from class: rx.Single.24
            @Override // p045rx.Observer
            public void onCompleted() {
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
                onSuccess.call(t);
            }
        };
        return Observable.create(new OnSubscribeDoOnEach(toObservable(), observer)).toSingle();
    }

    @Experimental
    public final Single<T> doOnSubscribe(Action0 subscribe) {
        return (Single<T>) lift(new OperatorDoOnSubscribe(subscribe));
    }

    @Experimental
    public final Single<T> delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return (Single<T>) lift(new OperatorDelay(delay, unit, scheduler));
    }

    @Experimental
    public final Single<T> delay(long delay, TimeUnit unit) {
        return delay(delay, unit, Schedulers.computation());
    }

    @Experimental
    public static <T> Single<T> defer(final Callable<Single<T>> singleFactory) {
        return create(new OnSubscribe<T>() { // from class: rx.Single.25
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SingleSubscriber) ((SingleSubscriber) x0));
            }

            public void call(SingleSubscriber<? super T> singleSubscriber) {
                try {
                    Single<? extends T> single = (Single) singleFactory.call();
                    single.subscribe((SingleSubscriber<? super Object>) singleSubscriber);
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    singleSubscriber.onError(t);
                }
            }
        });
    }

    @Experimental
    public final Single<T> doOnUnsubscribe(Action0 action) {
        return (Single<T>) lift(new OperatorDoOnUnsubscribe(action));
    }

    @Experimental
    public final Single<T> doAfterTerminate(Action0 action) {
        return create(new SingleDoAfterTerminate(this, action));
    }

    static <T> Single<? extends T>[] iterableToArray(Iterable<? extends Single<? extends T>> singlesIterable) {
        if (singlesIterable instanceof Collection) {
            Collection<? extends Single<? extends T>> list = (Collection) singlesIterable;
            int count = list.size();
            return (Single[]) list.toArray(new Single[count]);
        }
        Single<? extends T>[] tempArray = new Single[8];
        int count2 = 0;
        for (Single<? extends T> s : singlesIterable) {
            if (count2 == tempArray.length) {
                Single<? extends T>[] sb = new Single[(count2 >> 2) + count2];
                System.arraycopy(tempArray, 0, sb, 0, count2);
                tempArray = sb;
            }
            tempArray[count2] = s;
            count2++;
        }
        if (tempArray.length == count2) {
            return tempArray;
        }
        Single<? extends T>[] singlesArray = new Single[count2];
        System.arraycopy(tempArray, 0, singlesArray, 0, count2);
        return singlesArray;
    }

    public final Single<T> retry() {
        return toObservable().retry().toSingle();
    }

    public final Single<T> retry(long count) {
        return toObservable().retry(count).toSingle();
    }

    public final Single<T> retry(Func2<Integer, Throwable, Boolean> predicate) {
        return toObservable().retry(predicate).toSingle();
    }

    public final Single<T> retryWhen(Func1<Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler) {
        return toObservable().retryWhen(notificationHandler).toSingle();
    }

    @Experimental
    public static <T, Resource> Single<T> using(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Single<? extends T>> singleFactory, Action1<? super Resource> disposeAction) {
        return using(resourceFactory, singleFactory, disposeAction, false);
    }

    @Experimental
    public static <T, Resource> Single<T> using(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Single<? extends T>> singleFactory, Action1<? super Resource> disposeAction, boolean disposeEagerly) {
        if (resourceFactory == null) {
            throw new NullPointerException("resourceFactory is null");
        }
        if (singleFactory == null) {
            throw new NullPointerException("singleFactory is null");
        }
        if (disposeAction == null) {
            throw new NullPointerException("disposeAction is null");
        }
        return create(new SingleOnSubscribeUsing(resourceFactory, singleFactory, disposeAction, disposeEagerly));
    }

    @Experimental
    public final Single<T> delaySubscription(Observable<?> other) {
        if (other == null) {
            throw new NullPointerException();
        }
        return create(new SingleOnSubscribeDelaySubscriptionOther(this, other));
    }
}
