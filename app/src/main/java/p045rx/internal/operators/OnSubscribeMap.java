package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.OnErrorThrowable;
import p045rx.functions.Func1;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OnSubscribeMap<T, R> implements Observable.OnSubscribe<R> {
    final Observable<T> source;
    final Func1<? super T, ? extends R> transformer;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeMap(Observable<T> source, Func1<? super T, ? extends R> transformer) {
        this.source = source;
        this.transformer = transformer;
    }

    public void call(Subscriber<? super R> o) {
        MapSubscriber<T, R> parent = new MapSubscriber<>(o, this.transformer);
        o.add(parent);
        this.source.unsafeSubscribe(parent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MapSubscriber<T, R> extends Subscriber<T> {
        final Subscriber<? super R> actual;
        boolean done;
        final Func1<? super T, ? extends R> mapper;

        public MapSubscriber(Subscriber<? super R> actual, Func1<? super T, ? extends R> mapper) {
            this.actual = actual;
            this.mapper = mapper;
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            try {
                R result = this.mapper.call(t);
                this.actual.onNext(result);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                unsubscribe();
                onError(OnErrorThrowable.addValueAsLastCause(ex, t));
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (this.done) {
                RxJavaHooks.onError(e);
            } else {
                this.done = true;
                this.actual.onError(e);
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (!this.done) {
                this.actual.onCompleted();
            }
        }

        @Override // p045rx.Subscriber
        public void setProducer(Producer p) {
            this.actual.setProducer(p);
        }
    }
}
