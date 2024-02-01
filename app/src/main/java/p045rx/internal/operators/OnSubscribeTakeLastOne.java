package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public final class OnSubscribeTakeLastOne<T> implements Observable.OnSubscribe<T> {
    final Observable<T> source;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeTakeLastOne(Observable<T> source) {
        this.source = source;
    }

    public void call(Subscriber<? super T> t) {
        new TakeLastOneSubscriber(t).subscribeTo(this.source);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class TakeLastOneSubscriber<T> extends DeferredScalarSubscriber<T, T> {
        static final Object EMPTY = new Object();

        /* JADX WARN: Type inference failed for: r0v0, types: [R, java.lang.Object] */
        public TakeLastOneSubscriber(Subscriber<? super T> actual) {
            super(actual);
            this.value = EMPTY;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // p045rx.Observer
        public void onNext(T t) {
            this.value = t;
        }

        @Override // p045rx.internal.operators.DeferredScalarSubscriber, p045rx.Observer
        public void onCompleted() {
            Object o = this.value;
            if (o == EMPTY) {
                complete();
            } else {
                complete(o);
            }
        }
    }
}
