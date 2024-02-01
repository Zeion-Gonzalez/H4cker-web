package p045rx.internal.operators;

import java.util.NoSuchElementException;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.internal.producers.SingleProducer;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class OperatorSingle<T> implements Observable.Operator<T, T> {
    private final T defaultValue;
    private final boolean hasDefaultValue;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Holder {
        static final OperatorSingle<?> INSTANCE = new OperatorSingle<>();

        Holder() {
        }
    }

    public static <T> OperatorSingle<T> instance() {
        return (OperatorSingle<T>) Holder.INSTANCE;
    }

    OperatorSingle() {
        this(false, null);
    }

    public OperatorSingle(T defaultValue) {
        this(true, defaultValue);
    }

    private OperatorSingle(boolean hasDefaultValue, T defaultValue) {
        this.hasDefaultValue = hasDefaultValue;
        this.defaultValue = defaultValue;
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        ParentSubscriber<T> parent = new ParentSubscriber<>(child, this.hasDefaultValue, this.defaultValue);
        child.add(parent);
        return parent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ParentSubscriber<T> extends Subscriber<T> {
        private final Subscriber<? super T> child;
        private final T defaultValue;
        private final boolean hasDefaultValue;
        private boolean hasTooManyElements;
        private boolean isNonEmpty;
        private T value;

        ParentSubscriber(Subscriber<? super T> child, boolean hasDefaultValue, T defaultValue) {
            this.child = child;
            this.hasDefaultValue = hasDefaultValue;
            this.defaultValue = defaultValue;
            request(2L);
        }

        @Override // p045rx.Observer
        public void onNext(T value) {
            if (!this.hasTooManyElements) {
                if (this.isNonEmpty) {
                    this.hasTooManyElements = true;
                    this.child.onError(new IllegalArgumentException("Sequence contains too many elements"));
                    unsubscribe();
                } else {
                    this.value = value;
                    this.isNonEmpty = true;
                }
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (!this.hasTooManyElements) {
                if (this.isNonEmpty) {
                    this.child.setProducer(new SingleProducer(this.child, this.value));
                } else if (this.hasDefaultValue) {
                    this.child.setProducer(new SingleProducer(this.child, this.defaultValue));
                } else {
                    this.child.onError(new NoSuchElementException("Sequence contains no elements"));
                }
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (this.hasTooManyElements) {
                RxJavaHooks.onError(e);
            } else {
                this.child.onError(e);
            }
        }
    }
}
