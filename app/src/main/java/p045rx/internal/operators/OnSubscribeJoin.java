package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.observers.SerializedSubscriber;
import p045rx.subscriptions.CompositeSubscription;
import p045rx.subscriptions.SerialSubscription;

/* loaded from: classes.dex */
public final class OnSubscribeJoin<TLeft, TRight, TLeftDuration, TRightDuration, R> implements Observable.OnSubscribe<R> {
    final Observable<TLeft> left;
    final Func1<TLeft, Observable<TLeftDuration>> leftDurationSelector;
    final Func2<TLeft, TRight, R> resultSelector;
    final Observable<TRight> right;
    final Func1<TRight, Observable<TRightDuration>> rightDurationSelector;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeJoin(Observable<TLeft> left, Observable<TRight> right, Func1<TLeft, Observable<TLeftDuration>> leftDurationSelector, Func1<TRight, Observable<TRightDuration>> rightDurationSelector, Func2<TLeft, TRight, R> resultSelector) {
        this.left = left;
        this.right = right;
        this.leftDurationSelector = leftDurationSelector;
        this.rightDurationSelector = rightDurationSelector;
        this.resultSelector = resultSelector;
    }

    public void call(Subscriber<? super R> t1) {
        OnSubscribeJoin<TLeft, TRight, TLeftDuration, TRightDuration, R>.ResultSink result = new ResultSink(new SerializedSubscriber(t1));
        result.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class ResultSink extends HashMap<Integer, TLeft> {
        private static final long serialVersionUID = 3491669543549085380L;
        boolean leftDone;
        int leftId;
        boolean rightDone;
        int rightId;
        final Subscriber<? super R> subscriber;
        final CompositeSubscription group = new CompositeSubscription();
        final Map<Integer, TRight> rightMap = new HashMap();

        public ResultSink(Subscriber<? super R> subscriber) {
            this.subscriber = subscriber;
        }

        HashMap<Integer, TLeft> leftMap() {
            return this;
        }

        public void run() {
            this.subscriber.add(this.group);
            LeftSubscriber leftSubscriber = new LeftSubscriber();
            RightSubscriber rightSubscriber = new RightSubscriber();
            this.group.add(leftSubscriber);
            this.group.add(rightSubscriber);
            OnSubscribeJoin.this.left.unsafeSubscribe(leftSubscriber);
            OnSubscribeJoin.this.right.unsafeSubscribe(rightSubscriber);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class LeftSubscriber extends Subscriber<TLeft> {
            LeftSubscriber() {
            }

            protected void expire(int id, Subscription resource) {
                boolean complete = false;
                synchronized (ResultSink.this) {
                    if (ResultSink.this.leftMap().remove(Integer.valueOf(id)) != null && ResultSink.this.leftMap().isEmpty() && ResultSink.this.leftDone) {
                        complete = true;
                    }
                }
                if (complete) {
                    ResultSink.this.subscriber.onCompleted();
                    ResultSink.this.subscriber.unsubscribe();
                } else {
                    ResultSink.this.group.remove(resource);
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // p045rx.Observer
            public void onNext(TLeft args) {
                int id;
                int highRightId;
                synchronized (ResultSink.this) {
                    ResultSink resultSink = ResultSink.this;
                    id = resultSink.leftId;
                    resultSink.leftId = id + 1;
                    ResultSink.this.leftMap().put(Integer.valueOf(id), args);
                    highRightId = ResultSink.this.rightId;
                }
                try {
                    Observable<TLeftDuration> duration = OnSubscribeJoin.this.leftDurationSelector.call(args);
                    LeftDurationSubscriber leftDurationSubscriber = new LeftDurationSubscriber(id);
                    ResultSink.this.group.add(leftDurationSubscriber);
                    duration.unsafeSubscribe(leftDurationSubscriber);
                    List<TRight> rightValues = new ArrayList<>();
                    synchronized (ResultSink.this) {
                        for (Map.Entry<Integer, TRight> entry : ResultSink.this.rightMap.entrySet()) {
                            if (entry.getKey().intValue() < highRightId) {
                                rightValues.add(entry.getValue());
                            }
                        }
                    }
                    for (TRight r : rightValues) {
                        ResultSink.this.subscriber.onNext(OnSubscribeJoin.this.resultSelector.call(args, r));
                    }
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, this);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ResultSink.this.subscriber.onError(e);
                ResultSink.this.subscriber.unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                boolean complete = false;
                synchronized (ResultSink.this) {
                    ResultSink.this.leftDone = true;
                    if (ResultSink.this.rightDone || ResultSink.this.leftMap().isEmpty()) {
                        complete = true;
                    }
                }
                if (complete) {
                    ResultSink.this.subscriber.onCompleted();
                    ResultSink.this.subscriber.unsubscribe();
                } else {
                    ResultSink.this.group.remove(this);
                }
            }

            /* loaded from: classes.dex */
            final class LeftDurationSubscriber extends Subscriber<TLeftDuration> {

                /* renamed from: id */
                final int f1598id;
                boolean once = true;

                public LeftDurationSubscriber(int id) {
                    this.f1598id = id;
                }

                @Override // p045rx.Observer
                public void onNext(TLeftDuration args) {
                    onCompleted();
                }

                @Override // p045rx.Observer
                public void onError(Throwable e) {
                    LeftSubscriber.this.onError(e);
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    if (this.once) {
                        this.once = false;
                        LeftSubscriber.this.expire(this.f1598id, this);
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class RightSubscriber extends Subscriber<TRight> {
            RightSubscriber() {
            }

            void expire(int id, Subscription resource) {
                boolean complete = false;
                synchronized (ResultSink.this) {
                    if (ResultSink.this.rightMap.remove(Integer.valueOf(id)) != null && ResultSink.this.rightMap.isEmpty() && ResultSink.this.rightDone) {
                        complete = true;
                    }
                }
                if (complete) {
                    ResultSink.this.subscriber.onCompleted();
                    ResultSink.this.subscriber.unsubscribe();
                } else {
                    ResultSink.this.group.remove(resource);
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // p045rx.Observer
            public void onNext(TRight args) {
                int id;
                int highLeftId;
                synchronized (ResultSink.this) {
                    ResultSink resultSink = ResultSink.this;
                    id = resultSink.rightId;
                    resultSink.rightId = id + 1;
                    ResultSink.this.rightMap.put(Integer.valueOf(id), args);
                    highLeftId = ResultSink.this.leftId;
                }
                SerialSubscription md = new SerialSubscription();
                ResultSink.this.group.add(md);
                try {
                    Observable<TRightDuration> duration = OnSubscribeJoin.this.rightDurationSelector.call(args);
                    RightDurationSubscriber rightDurationSubscriber = new RightDurationSubscriber(id);
                    ResultSink.this.group.add(rightDurationSubscriber);
                    duration.unsafeSubscribe(rightDurationSubscriber);
                    List<TLeft> leftValues = new ArrayList<>();
                    synchronized (ResultSink.this) {
                        for (Map.Entry<Integer, TLeft> entry : ResultSink.this.leftMap().entrySet()) {
                            if (entry.getKey().intValue() < highLeftId) {
                                leftValues.add(entry.getValue());
                            }
                        }
                    }
                    for (TLeft lv : leftValues) {
                        ResultSink.this.subscriber.onNext(OnSubscribeJoin.this.resultSelector.call(lv, args));
                    }
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, this);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                ResultSink.this.subscriber.onError(e);
                ResultSink.this.subscriber.unsubscribe();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                boolean complete = false;
                synchronized (ResultSink.this) {
                    ResultSink.this.rightDone = true;
                    if (ResultSink.this.leftDone || ResultSink.this.rightMap.isEmpty()) {
                        complete = true;
                    }
                }
                if (complete) {
                    ResultSink.this.subscriber.onCompleted();
                    ResultSink.this.subscriber.unsubscribe();
                } else {
                    ResultSink.this.group.remove(this);
                }
            }

            /* loaded from: classes.dex */
            final class RightDurationSubscriber extends Subscriber<TRightDuration> {

                /* renamed from: id */
                final int f1599id;
                boolean once = true;

                public RightDurationSubscriber(int id) {
                    this.f1599id = id;
                }

                @Override // p045rx.Observer
                public void onNext(TRightDuration args) {
                    onCompleted();
                }

                @Override // p045rx.Observer
                public void onError(Throwable e) {
                    RightSubscriber.this.onError(e);
                }

                @Override // p045rx.Observer
                public void onCompleted() {
                    if (this.once) {
                        this.once = false;
                        RightSubscriber.this.expire(this.f1599id, this);
                    }
                }
            }
        }
    }
}
