package p045rx.internal.operators;

import java.util.ArrayDeque;
import java.util.Deque;
import p045rx.Observable;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public class OperatorSkipLast<T> implements Observable.Operator<T, T> {
    final int count;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorSkipLast(int count) {
        if (count < 0) {
            throw new IndexOutOfBoundsException("count could not be negative");
        }
        this.count = count;
    }

    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
        return (Subscriber<T>) new Subscriber<T>(subscriber) { // from class: rx.internal.operators.OperatorSkipLast.1

            /* renamed from: on */
            private final NotificationLite<T> f1611on = NotificationLite.instance();
            private final Deque<Object> deque = new ArrayDeque();

            @Override // p045rx.Observer
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override // p045rx.Observer
            public void onNext(T value) {
                if (OperatorSkipLast.this.count == 0) {
                    subscriber.onNext(value);
                    return;
                }
                if (this.deque.size() == OperatorSkipLast.this.count) {
                    subscriber.onNext(this.f1611on.getValue(this.deque.removeFirst()));
                } else {
                    request(1L);
                }
                this.deque.offerLast(this.f1611on.next(value));
            }
        };
    }
}
