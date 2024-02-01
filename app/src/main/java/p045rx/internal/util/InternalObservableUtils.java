package p045rx.internal.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import p045rx.Notification;
import p045rx.Observable;
import p045rx.Scheduler;
import p045rx.exceptions.OnErrorNotImplementedException;
import p045rx.functions.Action1;
import p045rx.functions.Action2;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.internal.operators.OperatorAny;
import p045rx.observables.ConnectableObservable;

/* loaded from: classes.dex */
public enum InternalObservableUtils {
    ;

    public static final PlusOneLongFunc2 LONG_COUNTER = new PlusOneLongFunc2();
    public static final ObjectEqualsFunc2 OBJECT_EQUALS = new ObjectEqualsFunc2();
    public static final ToArrayFunc1 TO_ARRAY = new ToArrayFunc1();
    static final ReturnsVoidFunc1 RETURNS_VOID = new ReturnsVoidFunc1();
    public static final PlusOneFunc2 COUNTER = new PlusOneFunc2();
    static final NotificationErrorExtractor ERROR_EXTRACTOR = new NotificationErrorExtractor();
    public static final Action1<Throwable> ERROR_NOT_IMPLEMENTED = new Action1<Throwable>() { // from class: rx.internal.util.InternalObservableUtils.ErrorNotImplementedAction
        @Override // p045rx.functions.Action1
        public void call(Throwable t) {
            throw new OnErrorNotImplementedException(t);
        }
    };
    public static final Observable.Operator<Boolean, Object> IS_EMPTY = new OperatorAny(UtilityFunctions.alwaysTrue(), true);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class PlusOneFunc2 implements Func2<Integer, Object, Integer> {
        PlusOneFunc2() {
        }

        @Override // p045rx.functions.Func2
        public Integer call(Integer count, Object o) {
            return Integer.valueOf(count.intValue() + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class PlusOneLongFunc2 implements Func2<Long, Object, Long> {
        PlusOneLongFunc2() {
        }

        @Override // p045rx.functions.Func2
        public Long call(Long count, Object o) {
            return Long.valueOf(count.longValue() + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ObjectEqualsFunc2 implements Func2<Object, Object, Boolean> {
        ObjectEqualsFunc2() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // p045rx.functions.Func2
        public Boolean call(Object first, Object second) {
            return Boolean.valueOf(first == second || (first != null && first.equals(second)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ToArrayFunc1 implements Func1<List<? extends Observable<?>>, Observable<?>[]> {
        ToArrayFunc1() {
        }

        @Override // p045rx.functions.Func1
        public Observable<?>[] call(List<? extends Observable<?>> o) {
            return (Observable[]) o.toArray(new Observable[o.size()]);
        }
    }

    public static Func1<Object, Boolean> equalsWith(Object other) {
        return new EqualsWithFunc1(other);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EqualsWithFunc1 implements Func1<Object, Boolean> {
        final Object other;

        public EqualsWithFunc1(Object other) {
            this.other = other;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // p045rx.functions.Func1
        public Boolean call(Object t) {
            return Boolean.valueOf(t == this.other || (t != null && t.equals(this.other)));
        }
    }

    public static Func1<Object, Boolean> isInstanceOf(Class<?> clazz) {
        return new IsInstanceOfFunc1(clazz);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class IsInstanceOfFunc1 implements Func1<Object, Boolean> {
        final Class<?> clazz;

        public IsInstanceOfFunc1(Class<?> other) {
            this.clazz = other;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // p045rx.functions.Func1
        public Boolean call(Object t) {
            return Boolean.valueOf(this.clazz.isInstance(t));
        }
    }

    public static final Func1<Observable<? extends Notification<?>>, Observable<?>> createRepeatDematerializer(Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler) {
        return new RepeatNotificationDematerializer(notificationHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class RepeatNotificationDematerializer implements Func1<Observable<? extends Notification<?>>, Observable<?>> {
        final Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler;

        public RepeatNotificationDematerializer(Func1<? super Observable<? extends Void>, ? extends Observable<?>> notificationHandler) {
            this.notificationHandler = notificationHandler;
        }

        @Override // p045rx.functions.Func1
        public Observable<?> call(Observable<? extends Notification<?>> notifications) {
            return this.notificationHandler.call(notifications.map(InternalObservableUtils.RETURNS_VOID));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReturnsVoidFunc1 implements Func1<Object, Void> {
        ReturnsVoidFunc1() {
        }

        @Override // p045rx.functions.Func1
        public Void call(Object t) {
            return null;
        }
    }

    public static <T, R> Func1<Observable<T>, Observable<R>> createReplaySelectorAndObserveOn(Func1<? super Observable<T>, ? extends Observable<R>> selector, Scheduler scheduler) {
        return new SelectorAndObserveOn(selector, scheduler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SelectorAndObserveOn<T, R> implements Func1<Observable<T>, Observable<R>> {
        final Scheduler scheduler;
        final Func1<? super Observable<T>, ? extends Observable<R>> selector;

        @Override // p045rx.functions.Func1
        public /* bridge */ /* synthetic */ Object call(Object x0) {
            return call((Observable) ((Observable) x0));
        }

        public SelectorAndObserveOn(Func1<? super Observable<T>, ? extends Observable<R>> selector, Scheduler scheduler) {
            this.selector = selector;
            this.scheduler = scheduler;
        }

        public Observable<R> call(Observable<T> t) {
            return this.selector.call(t).observeOn(this.scheduler);
        }
    }

    public static final Func1<Observable<? extends Notification<?>>, Observable<?>> createRetryDematerializer(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler) {
        return new RetryNotificationDematerializer(notificationHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class RetryNotificationDematerializer implements Func1<Observable<? extends Notification<?>>, Observable<?>> {
        final Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler;

        public RetryNotificationDematerializer(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler) {
            this.notificationHandler = notificationHandler;
        }

        @Override // p045rx.functions.Func1
        public Observable<?> call(Observable<? extends Notification<?>> notifications) {
            return this.notificationHandler.call(notifications.map(InternalObservableUtils.ERROR_EXTRACTOR));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class NotificationErrorExtractor implements Func1<Notification<?>, Throwable> {
        NotificationErrorExtractor() {
        }

        @Override // p045rx.functions.Func1
        public Throwable call(Notification<?> t) {
            return t.getThrowable();
        }
    }

    public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> source) {
        return new ReplaySupplierNoParams(source);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySupplierNoParams<T> implements Func0<ConnectableObservable<T>> {
        private final Observable<T> source;

        private ReplaySupplierNoParams(Observable<T> source) {
            this.source = source;
        }

        @Override // p045rx.functions.Func0, java.util.concurrent.Callable
        public ConnectableObservable<T> call() {
            return this.source.replay();
        }
    }

    public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> source, int bufferSize) {
        return new ReplaySupplierBuffer(source, bufferSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySupplierBuffer<T> implements Func0<ConnectableObservable<T>> {
        private final int bufferSize;
        private final Observable<T> source;

        private ReplaySupplierBuffer(Observable<T> source, int bufferSize) {
            this.source = source;
            this.bufferSize = bufferSize;
        }

        @Override // p045rx.functions.Func0, java.util.concurrent.Callable
        public ConnectableObservable<T> call() {
            return this.source.replay(this.bufferSize);
        }
    }

    public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> source, long time, TimeUnit unit, Scheduler scheduler) {
        return new ReplaySupplierBufferTime(source, time, unit, scheduler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySupplierBufferTime<T> implements Func0<ConnectableObservable<T>> {
        private final Scheduler scheduler;
        private final Observable<T> source;
        private final long time;
        private final TimeUnit unit;

        private ReplaySupplierBufferTime(Observable<T> source, long time, TimeUnit unit, Scheduler scheduler) {
            this.unit = unit;
            this.source = source;
            this.time = time;
            this.scheduler = scheduler;
        }

        @Override // p045rx.functions.Func0, java.util.concurrent.Callable
        public ConnectableObservable<T> call() {
            return this.source.replay(this.time, this.unit, this.scheduler);
        }
    }

    public static <T> Func0<ConnectableObservable<T>> createReplaySupplier(Observable<T> source, int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
        return new ReplaySupplierTime(source, bufferSize, time, unit, scheduler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySupplierTime<T> implements Func0<ConnectableObservable<T>> {
        private final int bufferSize;
        private final Scheduler scheduler;
        private final Observable<T> source;
        private final long time;
        private final TimeUnit unit;

        private ReplaySupplierTime(Observable<T> source, int bufferSize, long time, TimeUnit unit, Scheduler scheduler) {
            this.time = time;
            this.unit = unit;
            this.scheduler = scheduler;
            this.bufferSize = bufferSize;
            this.source = source;
        }

        @Override // p045rx.functions.Func0, java.util.concurrent.Callable
        public ConnectableObservable<T> call() {
            return this.source.replay(this.bufferSize, this.time, this.unit, this.scheduler);
        }
    }

    public static <T, R> Func2<R, T, R> createCollectorCaller(Action2<R, ? super T> collector) {
        return new CollectorCaller(collector);
    }

    /* loaded from: classes.dex */
    static final class CollectorCaller<T, R> implements Func2<R, T, R> {
        final Action2<R, ? super T> collector;

        public CollectorCaller(Action2<R, ? super T> collector) {
            this.collector = collector;
        }

        @Override // p045rx.functions.Func2
        public R call(R state, T value) {
            this.collector.call(state, value);
            return state;
        }
    }
}
