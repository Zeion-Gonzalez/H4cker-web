package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.observers.SerializedSubscriber;

/* loaded from: classes.dex */
public final class OperatorSerialize<T> implements Observable.Operator<T, T> {
    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Holder {
        static final OperatorSerialize<Object> INSTANCE = new OperatorSerialize<>();

        Holder() {
        }
    }

    public static <T> OperatorSerialize<T> instance() {
        return (OperatorSerialize<T>) Holder.INSTANCE;
    }

    OperatorSerialize() {
    }

    public Subscriber<? super T> call(final Subscriber<? super T> s) {
        return new SerializedSubscriber(new Subscriber<T>(s) { // from class: rx.internal.operators.OperatorSerialize.1
            @Override // p045rx.Observer
            public void onCompleted() {
                s.onCompleted();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                s.onError(e);
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
                s.onNext(t);
            }
        });
    }
}
