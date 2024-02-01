package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.functions.Func2;
import p045rx.internal.util.UtilityFunctions;

/* loaded from: classes.dex */
public final class OperatorSequenceEqual {
    static final Object LOCAL_ONCOMPLETED = new Object();

    private OperatorSequenceEqual() {
        throw new IllegalStateException("No instances!");
    }

    static <T> Observable<Object> materializeLite(Observable<T> source) {
        return Observable.concat(source, Observable.just(LOCAL_ONCOMPLETED));
    }

    public static <T> Observable<Boolean> sequenceEqual(Observable<? extends T> first, Observable<? extends T> second, final Func2<? super T, ? super T, Boolean> equality) {
        Observable<Object> firstObservable = materializeLite(first);
        Observable<Object> secondObservable = materializeLite(second);
        return Observable.zip(firstObservable, secondObservable, new Func2<Object, Object, Boolean>() { // from class: rx.internal.operators.OperatorSequenceEqual.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // p045rx.functions.Func2
            public Boolean call(Object t1, Object t2) {
                boolean c1 = t1 == OperatorSequenceEqual.LOCAL_ONCOMPLETED;
                boolean c2 = t2 == OperatorSequenceEqual.LOCAL_ONCOMPLETED;
                if (c1 && c2) {
                    return true;
                }
                if (c1 || c2) {
                    return false;
                }
                return (Boolean) Func2.this.call(t1, t2);
            }
        }).all(UtilityFunctions.identity());
    }
}
