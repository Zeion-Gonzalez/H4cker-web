package p045rx.internal.operators;

import java.util.HashSet;
import java.util.Set;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.functions.Func1;
import p045rx.internal.util.UtilityFunctions;

/* loaded from: classes.dex */
public final class OperatorDistinct<T, U> implements Observable.Operator<T, T> {
    final Func1<? super T, ? extends U> keySelector;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Holder {
        static final OperatorDistinct<?, ?> INSTANCE = new OperatorDistinct<>(UtilityFunctions.identity());

        Holder() {
        }
    }

    public static <T> OperatorDistinct<T, T> instance() {
        return (OperatorDistinct<T, T>) Holder.INSTANCE;
    }

    public OperatorDistinct(Func1<? super T, ? extends U> keySelector) {
        this.keySelector = keySelector;
    }

    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        return (Subscriber<T>) new Subscriber<T>(child) { // from class: rx.internal.operators.OperatorDistinct.1
            Set<U> keyMemory = new HashSet();

            @Override // p045rx.Observer
            public void onNext(T t) {
                U key = OperatorDistinct.this.keySelector.call(t);
                if (this.keyMemory.add(key)) {
                    child.onNext(t);
                } else {
                    request(1L);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                this.keyMemory = null;
                child.onError(e);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                this.keyMemory = null;
                child.onCompleted();
            }
        };
    }
}
