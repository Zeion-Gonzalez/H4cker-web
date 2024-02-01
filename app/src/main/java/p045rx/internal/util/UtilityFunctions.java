package p045rx.internal.util;

import p045rx.functions.Func1;

/* loaded from: classes.dex */
public final class UtilityFunctions {
    private UtilityFunctions() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Func1<? super T, Boolean> alwaysTrue() {
        return AlwaysTrue.INSTANCE;
    }

    public static <T> Func1<? super T, Boolean> alwaysFalse() {
        return AlwaysFalse.INSTANCE;
    }

    public static <T> Func1<T, T> identity() {
        return new Func1<T, T>() { // from class: rx.internal.util.UtilityFunctions.1
            @Override // p045rx.functions.Func1
            public T call(T o) {
                return o;
            }
        };
    }

    /* loaded from: classes.dex */
    enum AlwaysTrue implements Func1<Object, Boolean> {
        INSTANCE;

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // p045rx.functions.Func1
        public Boolean call(Object o) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    enum AlwaysFalse implements Func1<Object, Boolean> {
        INSTANCE;

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // p045rx.functions.Func1
        public Boolean call(Object o) {
            return false;
        }
    }
}
