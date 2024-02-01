package p045rx.subjects;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.observers.SerializedObserver;

/* loaded from: classes.dex */
public class SerializedSubject<T, R> extends Subject<T, R> {
    private final Subject<T, R> actual;
    private final SerializedObserver<T> observer;

    public SerializedSubject(final Subject<T, R> actual) {
        super(new Observable.OnSubscribe<R>() { // from class: rx.subjects.SerializedSubject.1
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(Subscriber<? super R> child) {
                Subject.this.unsafeSubscribe(child);
            }
        });
        this.actual = actual;
        this.observer = new SerializedObserver<>(actual);
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.observer.onCompleted();
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.observer.onError(e);
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.observer.onNext(t);
    }

    @Override // p045rx.subjects.Subject
    public boolean hasObservers() {
        return this.actual.hasObservers();
    }
}
