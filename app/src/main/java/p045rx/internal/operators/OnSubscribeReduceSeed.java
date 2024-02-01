package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func2;

/* loaded from: classes.dex */
public final class OnSubscribeReduceSeed<T, R> implements Observable.OnSubscribe<R> {
    final R initialValue;
    final Func2<R, ? super T, R> reducer;
    final Observable<T> source;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeReduceSeed(Observable<T> source, R initialValue, Func2<R, ? super T, R> reducer) {
        this.source = source;
        this.initialValue = initialValue;
        this.reducer = reducer;
    }

    public void call(Subscriber<? super R> t) {
        new ReduceSeedSubscriber(t, this.initialValue, this.reducer).subscribeTo(this.source);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReduceSeedSubscriber<T, R> extends DeferredScalarSubscriber<T, R> {
        final Func2<R, ? super T, R> reducer;

        public ReduceSeedSubscriber(Subscriber<? super R> actual, R initialValue, Func2<R, ? super T, R> reducer) {
            super(actual);
            this.value = initialValue;
            this.hasValue = true;
            this.reducer = reducer;
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            try {
                this.value = this.reducer.call(this.value, t);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                unsubscribe();
                this.actual.onError(ex);
            }
        }
    }
}
