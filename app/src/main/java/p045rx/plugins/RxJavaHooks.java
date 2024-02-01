package p045rx.plugins;

import java.lang.Thread;
import java.util.concurrent.ScheduledExecutorService;
import p045rx.Completable;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.Single;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.internal.operators.OnSubscribeOnAssembly;
import p045rx.internal.operators.OnSubscribeOnAssemblyCompletable;
import p045rx.internal.operators.OnSubscribeOnAssemblySingle;

@Experimental
/* loaded from: classes.dex */
public final class RxJavaHooks {
    static volatile boolean lockdown;
    static volatile Func1<Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> onCompletableCreate;
    static volatile Func1<Completable.CompletableOperator, Completable.CompletableOperator> onCompletableLift;
    static volatile Func2<Completable, Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> onCompletableStart;
    static volatile Func1<Throwable, Throwable> onCompletableSubscribeError;
    static volatile Func1<Scheduler, Scheduler> onComputationScheduler;
    static volatile Action1<Throwable> onError;
    static volatile Func0<? extends ScheduledExecutorService> onGenericScheduledExecutorService;
    static volatile Func1<Scheduler, Scheduler> onIOScheduler;
    static volatile Func1<Scheduler, Scheduler> onNewThreadScheduler;
    static volatile Func1<Observable.OnSubscribe, Observable.OnSubscribe> onObservableCreate;
    static volatile Func1<Observable.Operator, Observable.Operator> onObservableLift;
    static volatile Func1<Subscription, Subscription> onObservableReturn;
    static volatile Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> onObservableStart;
    static volatile Func1<Throwable, Throwable> onObservableSubscribeError;
    static volatile Func1<Action0, Action0> onScheduleAction;
    static volatile Func1<Single.OnSubscribe, Single.OnSubscribe> onSingleCreate;
    static volatile Func1<Observable.Operator, Observable.Operator> onSingleLift;
    static volatile Func1<Subscription, Subscription> onSingleReturn;
    static volatile Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> onSingleStart;
    static volatile Func1<Throwable, Throwable> onSingleSubscribeError;

    static {
        init();
    }

    private RxJavaHooks() {
        throw new IllegalStateException("No instances!");
    }

    static void init() {
        onError = new Action1<Throwable>() { // from class: rx.plugins.RxJavaHooks.1
            @Override // p045rx.functions.Action1
            public void call(Throwable e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            }
        };
        onObservableStart = new Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe>() { // from class: rx.plugins.RxJavaHooks.2
            @Override // p045rx.functions.Func2
            public Observable.OnSubscribe call(Observable t1, Observable.OnSubscribe t2) {
                return RxJavaPlugins.getInstance().getObservableExecutionHook().onSubscribeStart(t1, t2);
            }
        };
        onObservableReturn = new Func1<Subscription, Subscription>() { // from class: rx.plugins.RxJavaHooks.3
            @Override // p045rx.functions.Func1
            public Subscription call(Subscription f) {
                return RxJavaPlugins.getInstance().getObservableExecutionHook().onSubscribeReturn(f);
            }
        };
        onSingleStart = new Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe>() { // from class: rx.plugins.RxJavaHooks.4
            @Override // p045rx.functions.Func2
            public Observable.OnSubscribe call(Single t1, Observable.OnSubscribe t2) {
                return RxJavaPlugins.getInstance().getSingleExecutionHook().onSubscribeStart(t1, t2);
            }
        };
        onSingleReturn = new Func1<Subscription, Subscription>() { // from class: rx.plugins.RxJavaHooks.5
            @Override // p045rx.functions.Func1
            public Subscription call(Subscription f) {
                return RxJavaPlugins.getInstance().getSingleExecutionHook().onSubscribeReturn(f);
            }
        };
        onCompletableStart = new Func2<Completable, Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe>() { // from class: rx.plugins.RxJavaHooks.6
            @Override // p045rx.functions.Func2
            public Completable.CompletableOnSubscribe call(Completable t1, Completable.CompletableOnSubscribe t2) {
                return RxJavaPlugins.getInstance().getCompletableExecutionHook().onSubscribeStart(t1, t2);
            }
        };
        onScheduleAction = new Func1<Action0, Action0>() { // from class: rx.plugins.RxJavaHooks.7
            @Override // p045rx.functions.Func1
            public Action0 call(Action0 a) {
                return RxJavaPlugins.getInstance().getSchedulersHook().onSchedule(a);
            }
        };
        onObservableSubscribeError = new Func1<Throwable, Throwable>() { // from class: rx.plugins.RxJavaHooks.8
            @Override // p045rx.functions.Func1
            public Throwable call(Throwable t) {
                return RxJavaPlugins.getInstance().getObservableExecutionHook().onSubscribeError(t);
            }
        };
        onObservableLift = new Func1<Observable.Operator, Observable.Operator>() { // from class: rx.plugins.RxJavaHooks.9
            @Override // p045rx.functions.Func1
            public Observable.Operator call(Observable.Operator t) {
                return RxJavaPlugins.getInstance().getObservableExecutionHook().onLift(t);
            }
        };
        onSingleSubscribeError = new Func1<Throwable, Throwable>() { // from class: rx.plugins.RxJavaHooks.10
            @Override // p045rx.functions.Func1
            public Throwable call(Throwable t) {
                return RxJavaPlugins.getInstance().getSingleExecutionHook().onSubscribeError(t);
            }
        };
        onSingleLift = new Func1<Observable.Operator, Observable.Operator>() { // from class: rx.plugins.RxJavaHooks.11
            @Override // p045rx.functions.Func1
            public Observable.Operator call(Observable.Operator t) {
                return RxJavaPlugins.getInstance().getSingleExecutionHook().onLift(t);
            }
        };
        onCompletableSubscribeError = new Func1<Throwable, Throwable>() { // from class: rx.plugins.RxJavaHooks.12
            @Override // p045rx.functions.Func1
            public Throwable call(Throwable t) {
                return RxJavaPlugins.getInstance().getCompletableExecutionHook().onSubscribeError(t);
            }
        };
        onCompletableLift = new Func1<Completable.CompletableOperator, Completable.CompletableOperator>() { // from class: rx.plugins.RxJavaHooks.13
            @Override // p045rx.functions.Func1
            public Completable.CompletableOperator call(Completable.CompletableOperator t) {
                return RxJavaPlugins.getInstance().getCompletableExecutionHook().onLift(t);
            }
        };
        initCreate();
    }

    static void initCreate() {
        onObservableCreate = new Func1<Observable.OnSubscribe, Observable.OnSubscribe>() { // from class: rx.plugins.RxJavaHooks.14
            @Override // p045rx.functions.Func1
            public Observable.OnSubscribe call(Observable.OnSubscribe f) {
                return RxJavaPlugins.getInstance().getObservableExecutionHook().onCreate(f);
            }
        };
        onSingleCreate = new Func1<Single.OnSubscribe, Single.OnSubscribe>() { // from class: rx.plugins.RxJavaHooks.15
            @Override // p045rx.functions.Func1
            public Single.OnSubscribe call(Single.OnSubscribe f) {
                return RxJavaPlugins.getInstance().getSingleExecutionHook().onCreate(f);
            }
        };
        onCompletableCreate = new Func1<Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe>() { // from class: rx.plugins.RxJavaHooks.16
            @Override // p045rx.functions.Func1
            public Completable.CompletableOnSubscribe call(Completable.CompletableOnSubscribe f) {
                return RxJavaPlugins.getInstance().getCompletableExecutionHook().onCreate(f);
            }
        };
    }

    public static void reset() {
        if (!lockdown) {
            init();
            onComputationScheduler = null;
            onIOScheduler = null;
            onNewThreadScheduler = null;
            onGenericScheduledExecutorService = null;
        }
    }

    public static void clear() {
        if (!lockdown) {
            onError = null;
            onObservableCreate = null;
            onObservableStart = null;
            onObservableReturn = null;
            onObservableSubscribeError = null;
            onObservableLift = null;
            onSingleCreate = null;
            onSingleStart = null;
            onSingleReturn = null;
            onSingleSubscribeError = null;
            onSingleLift = null;
            onCompletableCreate = null;
            onCompletableStart = null;
            onCompletableSubscribeError = null;
            onCompletableLift = null;
            onComputationScheduler = null;
            onIOScheduler = null;
            onNewThreadScheduler = null;
            onScheduleAction = null;
            onGenericScheduledExecutorService = null;
        }
    }

    public static void lockdown() {
        lockdown = true;
    }

    public static boolean isLockdown() {
        return lockdown;
    }

    public static void onError(Throwable ex) {
        Action1<Throwable> f = onError;
        if (f != null) {
            try {
                f.call(ex);
                return;
            } catch (Throwable pluginException) {
                System.err.println("The onError handler threw an Exception. It shouldn't. => " + pluginException.getMessage());
                pluginException.printStackTrace();
                signalUncaught(pluginException);
            }
        }
        signalUncaught(ex);
    }

    static void signalUncaught(Throwable ex) {
        Thread current = Thread.currentThread();
        Thread.UncaughtExceptionHandler handler = current.getUncaughtExceptionHandler();
        handler.uncaughtException(current, ex);
    }

    public static <T> Observable.OnSubscribe<T> onCreate(Observable.OnSubscribe<T> onSubscribe) {
        Func1<Observable.OnSubscribe, Observable.OnSubscribe> f = onObservableCreate;
        return f != null ? f.call(onSubscribe) : onSubscribe;
    }

    public static <T> Single.OnSubscribe<T> onCreate(Single.OnSubscribe<T> onSubscribe) {
        Func1<Single.OnSubscribe, Single.OnSubscribe> f = onSingleCreate;
        return f != null ? f.call(onSubscribe) : onSubscribe;
    }

    public static Completable.CompletableOnSubscribe onCreate(Completable.CompletableOnSubscribe onSubscribe) {
        Func1<Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> f = onCompletableCreate;
        return f != null ? f.call(onSubscribe) : onSubscribe;
    }

    public static Scheduler onComputationScheduler(Scheduler scheduler) {
        Func1<Scheduler, Scheduler> f = onComputationScheduler;
        return f != null ? f.call(scheduler) : scheduler;
    }

    public static Scheduler onIOScheduler(Scheduler scheduler) {
        Func1<Scheduler, Scheduler> f = onIOScheduler;
        return f != null ? f.call(scheduler) : scheduler;
    }

    public static Scheduler onNewThreadScheduler(Scheduler scheduler) {
        Func1<Scheduler, Scheduler> f = onNewThreadScheduler;
        return f != null ? f.call(scheduler) : scheduler;
    }

    public static Action0 onScheduledAction(Action0 action) {
        Func1<Action0, Action0> f = onScheduleAction;
        return f != null ? f.call(action) : action;
    }

    public static <T> Observable.OnSubscribe<T> onObservableStart(Observable<T> instance, Observable.OnSubscribe<T> onSubscribe) {
        Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> f = onObservableStart;
        return f != null ? f.call(instance, onSubscribe) : onSubscribe;
    }

    public static Subscription onObservableReturn(Subscription subscription) {
        Func1<Subscription, Subscription> f = onObservableReturn;
        return f != null ? f.call(subscription) : subscription;
    }

    public static Throwable onObservableError(Throwable error) {
        Func1<Throwable, Throwable> f = onObservableSubscribeError;
        return f != null ? f.call(error) : error;
    }

    public static <T, R> Observable.Operator<R, T> onObservableLift(Observable.Operator<R, T> operator) {
        Func1<Observable.Operator, Observable.Operator> f = onObservableLift;
        return f != null ? f.call(operator) : operator;
    }

    public static <T> Observable.OnSubscribe<T> onSingleStart(Single<T> instance, Observable.OnSubscribe<T> onSubscribe) {
        Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> f = onSingleStart;
        return f != null ? f.call(instance, onSubscribe) : onSubscribe;
    }

    public static Subscription onSingleReturn(Subscription subscription) {
        Func1<Subscription, Subscription> f = onSingleReturn;
        return f != null ? f.call(subscription) : subscription;
    }

    public static Throwable onSingleError(Throwable error) {
        Func1<Throwable, Throwable> f = onSingleSubscribeError;
        return f != null ? f.call(error) : error;
    }

    public static <T, R> Observable.Operator<R, T> onSingleLift(Observable.Operator<R, T> operator) {
        Func1<Observable.Operator, Observable.Operator> f = onSingleLift;
        return f != null ? f.call(operator) : operator;
    }

    public static <T> Completable.CompletableOnSubscribe onCompletableStart(Completable instance, Completable.CompletableOnSubscribe onSubscribe) {
        Func2<Completable, Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> f = onCompletableStart;
        return f != null ? f.call(instance, onSubscribe) : onSubscribe;
    }

    public static Throwable onCompletableError(Throwable error) {
        Func1<Throwable, Throwable> f = onCompletableSubscribeError;
        return f != null ? f.call(error) : error;
    }

    public static <T, R> Completable.CompletableOperator onCompletableLift(Completable.CompletableOperator operator) {
        Func1<Completable.CompletableOperator, Completable.CompletableOperator> f = onCompletableLift;
        return f != null ? f.call(operator) : operator;
    }

    public static void setOnError(Action1<Throwable> onError2) {
        if (!lockdown) {
            onError = onError2;
        }
    }

    public static void setOnCompletableCreate(Func1<Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> onCompletableCreate2) {
        if (!lockdown) {
            onCompletableCreate = onCompletableCreate2;
        }
    }

    public static void setOnObservableCreate(Func1<Observable.OnSubscribe, Observable.OnSubscribe> onObservableCreate2) {
        if (!lockdown) {
            onObservableCreate = onObservableCreate2;
        }
    }

    public static void setOnSingleCreate(Func1<Single.OnSubscribe, Single.OnSubscribe> onSingleCreate2) {
        if (!lockdown) {
            onSingleCreate = onSingleCreate2;
        }
    }

    public static void setOnComputationScheduler(Func1<Scheduler, Scheduler> onComputationScheduler2) {
        if (!lockdown) {
            onComputationScheduler = onComputationScheduler2;
        }
    }

    public static void setOnIOScheduler(Func1<Scheduler, Scheduler> onIOScheduler2) {
        if (!lockdown) {
            onIOScheduler = onIOScheduler2;
        }
    }

    public static void setOnNewThreadScheduler(Func1<Scheduler, Scheduler> onNewThreadScheduler2) {
        if (!lockdown) {
            onNewThreadScheduler = onNewThreadScheduler2;
        }
    }

    public static void setOnScheduleAction(Func1<Action0, Action0> onScheduleAction2) {
        if (!lockdown) {
            onScheduleAction = onScheduleAction2;
        }
    }

    public static void setOnCompletableStart(Func2<Completable, Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> onCompletableStart2) {
        if (!lockdown) {
            onCompletableStart = onCompletableStart2;
        }
    }

    public static void setOnObservableStart(Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> onObservableStart2) {
        if (!lockdown) {
            onObservableStart = onObservableStart2;
        }
    }

    public static void setOnSingleStart(Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> onSingleStart2) {
        if (!lockdown) {
            onSingleStart = onSingleStart2;
        }
    }

    public static void setOnObservableReturn(Func1<Subscription, Subscription> onObservableReturn2) {
        if (!lockdown) {
            onObservableReturn = onObservableReturn2;
        }
    }

    public static void setOnSingleReturn(Func1<Subscription, Subscription> onSingleReturn2) {
        if (!lockdown) {
            onSingleReturn = onSingleReturn2;
        }
    }

    public static void setOnSingleSubscribeError(Func1<Throwable, Throwable> onSingleSubscribeError2) {
        if (!lockdown) {
            onSingleSubscribeError = onSingleSubscribeError2;
        }
    }

    public static Func1<Throwable, Throwable> getOnSingleSubscribeError() {
        return onSingleSubscribeError;
    }

    public static void setOnCompletableSubscribeError(Func1<Throwable, Throwable> onCompletableSubscribeError2) {
        if (!lockdown) {
            onCompletableSubscribeError = onCompletableSubscribeError2;
        }
    }

    public static Func1<Throwable, Throwable> getOnCompletableSubscribeError() {
        return onCompletableSubscribeError;
    }

    public static void setOnObservableSubscribeError(Func1<Throwable, Throwable> onObservableSubscribeError2) {
        if (!lockdown) {
            onObservableSubscribeError = onObservableSubscribeError2;
        }
    }

    public static Func1<Throwable, Throwable> getOnObservableSubscribeError() {
        return onObservableSubscribeError;
    }

    public static void setOnObservableLift(Func1<Observable.Operator, Observable.Operator> onObservableLift2) {
        if (!lockdown) {
            onObservableLift = onObservableLift2;
        }
    }

    public static Func1<Observable.Operator, Observable.Operator> getOnObservableLift() {
        return onObservableLift;
    }

    public static void setOnSingleLift(Func1<Observable.Operator, Observable.Operator> onSingleLift2) {
        if (!lockdown) {
            onSingleLift = onSingleLift2;
        }
    }

    public static Func1<Observable.Operator, Observable.Operator> getOnSingleLift() {
        return onSingleLift;
    }

    public static void setOnCompletableLift(Func1<Completable.CompletableOperator, Completable.CompletableOperator> onCompletableLift2) {
        if (!lockdown) {
            onCompletableLift = onCompletableLift2;
        }
    }

    public static Func1<Completable.CompletableOperator, Completable.CompletableOperator> getOnCompletableLift() {
        return onCompletableLift;
    }

    public static Func1<Scheduler, Scheduler> getOnComputationScheduler() {
        return onComputationScheduler;
    }

    public static Action1<Throwable> getOnError() {
        return onError;
    }

    public static Func1<Scheduler, Scheduler> getOnIOScheduler() {
        return onIOScheduler;
    }

    public static Func1<Scheduler, Scheduler> getOnNewThreadScheduler() {
        return onNewThreadScheduler;
    }

    public static Func1<Observable.OnSubscribe, Observable.OnSubscribe> getOnObservableCreate() {
        return onObservableCreate;
    }

    public static Func1<Action0, Action0> getOnScheduleAction() {
        return onScheduleAction;
    }

    public static Func1<Single.OnSubscribe, Single.OnSubscribe> getOnSingleCreate() {
        return onSingleCreate;
    }

    public static Func1<Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> getOnCompletableCreate() {
        return onCompletableCreate;
    }

    public static Func2<Completable, Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe> getOnCompletableStart() {
        return onCompletableStart;
    }

    public static Func2<Observable, Observable.OnSubscribe, Observable.OnSubscribe> getOnObservableStart() {
        return onObservableStart;
    }

    public static Func2<Single, Observable.OnSubscribe, Observable.OnSubscribe> getOnSingleStart() {
        return onSingleStart;
    }

    public static Func1<Subscription, Subscription> getOnObservableReturn() {
        return onObservableReturn;
    }

    public static Func1<Subscription, Subscription> getOnSingleReturn() {
        return onSingleReturn;
    }

    public static void resetAssemblyTracking() {
        if (!lockdown) {
            initCreate();
        }
    }

    public static void clearAssemblyTracking() {
        if (!lockdown) {
            onObservableCreate = null;
            onSingleCreate = null;
            onCompletableCreate = null;
        }
    }

    public static void enableAssemblyTracking() {
        if (!lockdown) {
            onObservableCreate = new Func1<Observable.OnSubscribe, Observable.OnSubscribe>() { // from class: rx.plugins.RxJavaHooks.17
                @Override // p045rx.functions.Func1
                public Observable.OnSubscribe call(Observable.OnSubscribe f) {
                    return new OnSubscribeOnAssembly(f);
                }
            };
            onSingleCreate = new Func1<Single.OnSubscribe, Single.OnSubscribe>() { // from class: rx.plugins.RxJavaHooks.18
                @Override // p045rx.functions.Func1
                public Single.OnSubscribe call(Single.OnSubscribe f) {
                    return new OnSubscribeOnAssemblySingle(f);
                }
            };
            onCompletableCreate = new Func1<Completable.CompletableOnSubscribe, Completable.CompletableOnSubscribe>() { // from class: rx.plugins.RxJavaHooks.19
                @Override // p045rx.functions.Func1
                public Completable.CompletableOnSubscribe call(Completable.CompletableOnSubscribe f) {
                    return new OnSubscribeOnAssemblyCompletable(f);
                }
            };
        }
    }

    public static void setOnGenericScheduledExecutorService(Func0<? extends ScheduledExecutorService> factory) {
        if (!lockdown) {
            onGenericScheduledExecutorService = factory;
        }
    }

    public static Func0<? extends ScheduledExecutorService> getOnGenericScheduledExecutorService() {
        return onGenericScheduledExecutorService;
    }
}
