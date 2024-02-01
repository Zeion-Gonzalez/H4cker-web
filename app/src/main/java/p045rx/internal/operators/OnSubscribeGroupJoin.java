package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.observers.SerializedObserver;
import p045rx.observers.SerializedSubscriber;
import p045rx.subjects.PublishSubject;
import p045rx.subjects.Subject;
import p045rx.subscriptions.CompositeSubscription;
import p045rx.subscriptions.RefCountSubscription;

/* loaded from: classes.dex */
public final class OnSubscribeGroupJoin<T1, T2, D1, D2, R> implements Observable.OnSubscribe<R> {
    final Observable<T1> left;
    final Func1<? super T1, ? extends Observable<D1>> leftDuration;
    final Func2<? super T1, ? super Observable<T2>, ? extends R> resultSelector;
    final Observable<T2> right;
    final Func1<? super T2, ? extends Observable<D2>> rightDuration;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeGroupJoin(Observable<T1> left, Observable<T2> right, Func1<? super T1, ? extends Observable<D1>> leftDuration, Func1<? super T2, ? extends Observable<D2>> rightDuration, Func2<? super T1, ? super Observable<T2>, ? extends R> resultSelector) {
        this.left = left;
        this.right = right;
        this.leftDuration = leftDuration;
        this.rightDuration = rightDuration;
        this.resultSelector = resultSelector;
    }

    public void call(Subscriber<? super R> child) {
        OnSubscribeGroupJoin<T1, T2, D1, D2, R>.ResultManager ro = new ResultManager(new SerializedSubscriber(child));
        child.add(ro);
        ro.init();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class ResultManager extends HashMap<Integer, Observer<T2>> implements Subscription {
        private static final long serialVersionUID = -3035156013812425335L;
        boolean leftDone;
        int leftIds;
        boolean rightDone;
        int rightIds;
        final Subscriber<? super R> subscriber;
        final Map<Integer, T2> rightMap = new HashMap();
        final CompositeSubscription group = new CompositeSubscription();
        final RefCountSubscription cancel = new RefCountSubscription(this.group);

        public ResultManager(Subscriber<? super R> subscriber) {
            this.subscriber = subscriber;
        }

        public void init() {
            LeftObserver leftObserver = new LeftObserver();
            RightObserver rightObserver = new RightObserver();
            this.group.add(leftObserver);
            this.group.add(rightObserver);
            OnSubscribeGroupJoin.this.left.unsafeSubscribe(leftObserver);
            OnSubscribeGroupJoin.this.right.unsafeSubscribe(rightObserver);
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.cancel.unsubscribe();
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.cancel.isUnsubscribed();
        }

        Map<Integer, Observer<T2>> leftMap() {
            return this;
        }

        void errorAll(Throwable e) {
            List<Observer<T2>> list;
            synchronized (this) {
                list = new ArrayList<>(leftMap().values());
                leftMap().clear();
                this.rightMap.clear();
            }
            for (Observer<T2> o : list) {
                o.onError(e);
            }
            this.subscriber.onError(e);
            this.cancel.unsubscribe();
        }

        void errorMain(Throwable e) {
            synchronized (this) {
                leftMap().clear();
                this.rightMap.clear();
            }
            this.subscriber.onError(e);
            this.cancel.unsubscribe();
        }

        void complete(List<Observer<T2>> list) {
            if (list != null) {
                for (Observer<T2> o : list) {
                    o.onCompleted();
                }
                this.subscriber.onCompleted();
                this.cancel.unsubscribe();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class LeftObserver extends Subscriber<T1> {
            LeftObserver() {
            }

            @Override // p045rx.Observer
            public void onNext(T1 args) {
                int id;
                List<T2> rightMapValues;
                try {
                    Subject<T2, T2> subj = PublishSubject.create();
                    SerializedObserver serializedObserver = new SerializedObserver(subj);
                    synchronized (ResultManager.this) {
                        ResultManager resultManager = ResultManager.this;
                        id = resultManager.leftIds;
                        resultManager.leftIds = id + 1;
                        ResultManager.this.leftMap().put(Integer.valueOf(id), serializedObserver);
                    }
                    Observable<T2> window = Observable.create(new WindowObservableFunc(subj, ResultManager.this.cancel));
                    Observable<D1> duration = OnSubscribeGroupJoin.this.leftDuration.call(args);
                    LeftDurationObserver leftDurationObserver = new LeftDurationObserver(id);
                    ResultManager.this.group.add(leftDurationObserver);
                    duration.unsafeSubscribe(leftDurationObserver);
                    R result = OnSubscribeGroupJoin.this.resultSelector.call(args, window);
                    synchronized (ResultManager.this) {
                        rightMapValues = new ArrayList<>((Collection<? extends T2>) ResultManager.this.rightMap.values());
                    }
                    ResultManager.this.subscriber.onNext(result);
                    for (T2 t2 : rightMapValues) {
                        serializedObserver.onNext(t2);
                    }
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, this);
                }
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                ArrayList arrayList = null;
                synchronized (ResultManager.this) {
                    try {
                        ResultManager.this.leftDone = true;
                        if (ResultManager.this.rightDone) {
                            ArrayList arrayList2 = new ArrayList(ResultManager.this.leftMap().values());
                            try {
                                ResultManager.this.leftMap().clear();
                                ResultManager.this.rightMap.clear();
                                arrayList = arrayList2;
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        }
                        ResultManager.this.complete(arrayList);
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ResultManager.this.errorAll(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class RightObserver extends Subscriber<T2> {
            RightObserver() {
            }

            @Override // p045rx.Observer
            public void onNext(T2 args) {
                int id;
                List<Observer<T2>> list;
                try {
                    synchronized (ResultManager.this) {
                        ResultManager resultManager = ResultManager.this;
                        id = resultManager.rightIds;
                        resultManager.rightIds = id + 1;
                        ResultManager.this.rightMap.put(Integer.valueOf(id), args);
                    }
                    Observable<D2> duration = OnSubscribeGroupJoin.this.rightDuration.call(args);
                    RightDurationObserver rightDurationObserver = new RightDurationObserver(id);
                    ResultManager.this.group.add(rightDurationObserver);
                    duration.unsafeSubscribe(rightDurationObserver);
                    synchronized (ResultManager.this) {
                        list = new ArrayList<>(ResultManager.this.leftMap().values());
                    }
                    for (Observer<T2> o : list) {
                        o.onNext(args);
                    }
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, this);
                }
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                ArrayList arrayList = null;
                synchronized (ResultManager.this) {
                    try {
                        ResultManager.this.rightDone = true;
                        if (ResultManager.this.leftDone) {
                            ArrayList arrayList2 = new ArrayList(ResultManager.this.leftMap().values());
                            try {
                                ResultManager.this.leftMap().clear();
                                ResultManager.this.rightMap.clear();
                                arrayList = arrayList2;
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        }
                        ResultManager.this.complete(arrayList);
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ResultManager.this.errorAll(e);
            }
        }

        /* loaded from: classes.dex */
        final class LeftDurationObserver extends Subscriber<D1> {

            /* renamed from: id */
            final int f1596id;
            boolean once = true;

            public LeftDurationObserver(int id) {
                this.f1596id = id;
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                Observer<T2> gr;
                if (this.once) {
                    this.once = false;
                    synchronized (ResultManager.this) {
                        gr = ResultManager.this.leftMap().remove(Integer.valueOf(this.f1596id));
                    }
                    if (gr != null) {
                        gr.onCompleted();
                    }
                    ResultManager.this.group.remove(this);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ResultManager.this.errorMain(e);
            }

            @Override // p045rx.Observer
            public void onNext(D1 args) {
                onCompleted();
            }
        }

        /* loaded from: classes.dex */
        final class RightDurationObserver extends Subscriber<D2> {

            /* renamed from: id */
            final int f1597id;
            boolean once = true;

            public RightDurationObserver(int id) {
                this.f1597id = id;
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                if (this.once) {
                    this.once = false;
                    synchronized (ResultManager.this) {
                        ResultManager.this.rightMap.remove(Integer.valueOf(this.f1597id));
                    }
                    ResultManager.this.group.remove(this);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ResultManager.this.errorMain(e);
            }

            @Override // p045rx.Observer
            public void onNext(D2 args) {
                onCompleted();
            }
        }
    }

    /* loaded from: classes.dex */
    static final class WindowObservableFunc<T> implements Observable.OnSubscribe<T> {
        final RefCountSubscription refCount;
        final Observable<T> underlying;

        @Override // p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            call((Subscriber) ((Subscriber) x0));
        }

        public WindowObservableFunc(Observable<T> underlying, RefCountSubscription refCount) {
            this.refCount = refCount;
            this.underlying = underlying;
        }

        public void call(Subscriber<? super T> t1) {
            Subscription ref = this.refCount.get();
            WindowObservableFunc<T>.WindowSubscriber wo = new WindowSubscriber(t1, ref);
            wo.add(ref);
            this.underlying.unsafeSubscribe(wo);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class WindowSubscriber extends Subscriber<T> {
            private final Subscription ref;
            final Subscriber<? super T> subscriber;

            public WindowSubscriber(Subscriber<? super T> subscriber, Subscription ref) {
                super(subscriber);
                this.subscriber = subscriber;
                this.ref = ref;
            }

            @Override // p045rx.Observer
            public void onNext(T args) {
                this.subscriber.onNext(args);
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                this.subscriber.onError(e);
                this.ref.unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                this.subscriber.onCompleted();
                this.ref.unsubscribe();
            }
        }
    }
}
