package p045rx.internal.producers;

import java.util.concurrent.atomic.AtomicBoolean;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;

/* loaded from: classes.dex */
public final class SingleProducer<T> extends AtomicBoolean implements Producer {
    private static final long serialVersionUID = -3353584923995471404L;
    final Subscriber<? super T> child;
    final T value;

    public SingleProducer(Subscriber<? super T> child, T value) {
        this.child = child;
        this.value = value;
    }

    @Override // p045rx.Producer
    public void request(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n >= 0 required");
        }
        if (n != 0 && compareAndSet(false, true)) {
            Subscriber<? super T> c = this.child;
            if (!c.isUnsubscribed()) {
                Object obj = (T) this.value;
                try {
                    c.onNext(obj);
                    if (!c.isUnsubscribed()) {
                        c.onCompleted();
                    }
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, c, obj);
                }
            }
        }
    }
}
