package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;
import p045rx.functions.Func2;
import p045rx.internal.util.UtilityFunctions;

/* loaded from: classes.dex */
public final class OperatorDistinctUntilChanged<T, U> implements Observable.Operator<T, T>, Func2<U, U, Boolean> {
    final Func2<? super U, ? super U, Boolean> comparator;
    final Func1<? super T, ? extends U> keySelector;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Holder {
        static final OperatorDistinctUntilChanged<?, ?> INSTANCE = new OperatorDistinctUntilChanged<>(UtilityFunctions.identity());

        Holder() {
        }
    }

    public static <T> OperatorDistinctUntilChanged<T, T> instance() {
        return (OperatorDistinctUntilChanged<T, T>) Holder.INSTANCE;
    }

    public OperatorDistinctUntilChanged(Func1<? super T, ? extends U> keySelector) {
        this.keySelector = keySelector;
        this.comparator = this;
    }

    public OperatorDistinctUntilChanged(Func2<? super U, ? super U, Boolean> comparator) {
        this.keySelector = UtilityFunctions.identity();
        this.comparator = comparator;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // p045rx.functions.Func2
    public Boolean call(U t1, U t2) {
        return Boolean.valueOf(t1 == t2 || (t1 != null && t1.equals(t2)));
    }

    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        return (Subscriber<T>) new Subscriber<T>(child) { // from class: rx.internal.operators.OperatorDistinctUntilChanged.1
            boolean hasPrevious;
            U previousKey;

            @Override // p045rx.Observer
            public void onNext(T t) {
                try {
                    U key = OperatorDistinctUntilChanged.this.keySelector.call(t);
                    U currentKey = this.previousKey;
                    this.previousKey = key;
                    if (this.hasPrevious) {
                        try {
                            boolean comparison = OperatorDistinctUntilChanged.this.comparator.call(currentKey, key).booleanValue();
                            if (!comparison) {
                                child.onNext(t);
                                return;
                            } else {
                                request(1L);
                                return;
                            }
                        } catch (Throwable e) {
                            Exceptions.throwOrReport(e, child, key);
                            return;
                        }
                    }
                    this.hasPrevious = true;
                    child.onNext(t);
                } catch (Throwable e2) {
                    Exceptions.throwOrReport(e2, child, t);
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                child.onCompleted();
            }
        };
    }
}
