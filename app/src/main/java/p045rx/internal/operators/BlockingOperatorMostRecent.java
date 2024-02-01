package p045rx.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;

/* loaded from: classes.dex */
public final class BlockingOperatorMostRecent {
    private BlockingOperatorMostRecent() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Iterable<T> mostRecent(final Observable<? extends T> source, final T initialValue) {
        return new Iterable<T>() { // from class: rx.internal.operators.BlockingOperatorMostRecent.1
            @Override // java.lang.Iterable
            public Iterator<T> iterator() {
                MostRecentObserver<T> mostRecentObserver = new MostRecentObserver<>(initialValue);
                source.subscribe((Subscriber) mostRecentObserver);
                return mostRecentObserver.getIterable();
            }
        };
    }

    /* loaded from: classes.dex */
    static final class MostRecentObserver<T> extends Subscriber<T> {

        /* renamed from: nl */
        final NotificationLite<T> f1581nl = NotificationLite.instance();
        volatile Object value;

        MostRecentObserver(T value) {
            this.value = this.f1581nl.next(value);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.value = this.f1581nl.completed();
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            this.value = this.f1581nl.error(e);
        }

        @Override // p045rx.Observer
        public void onNext(T args) {
            this.value = this.f1581nl.next(args);
        }

        public Iterator<T> getIterable() {
            return new Iterator<T>() { // from class: rx.internal.operators.BlockingOperatorMostRecent.MostRecentObserver.1
                private Object buf;

                @Override // java.util.Iterator
                public boolean hasNext() {
                    this.buf = MostRecentObserver.this.value;
                    return !MostRecentObserver.this.f1581nl.isCompleted(this.buf);
                }

                @Override // java.util.Iterator
                public T next() {
                    try {
                        if (this.buf == null) {
                            this.buf = MostRecentObserver.this.value;
                        }
                        if (MostRecentObserver.this.f1581nl.isCompleted(this.buf)) {
                            throw new NoSuchElementException();
                        }
                        if (MostRecentObserver.this.f1581nl.isError(this.buf)) {
                            throw Exceptions.propagate(MostRecentObserver.this.f1581nl.getError(this.buf));
                        }
                        return MostRecentObserver.this.f1581nl.getValue(this.buf);
                    } finally {
                        this.buf = null;
                    }
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException("Read only iterator");
                }
            };
        }
    }
}
