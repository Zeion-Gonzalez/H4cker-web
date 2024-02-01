package p045rx.subjects;

import java.util.ArrayList;
import java.util.List;
import p045rx.Observable;
import p045rx.annotations.Beta;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action1;
import p045rx.internal.operators.NotificationLite;
import p045rx.internal.producers.SingleProducer;
import p045rx.subjects.SubjectSubscriptionManager;

/* loaded from: classes.dex */
public final class AsyncSubject<T> extends Subject<T, T> {
    volatile Object lastValue;

    /* renamed from: nl */
    private final NotificationLite<T> f1641nl;
    final SubjectSubscriptionManager<T> state;

    public static <T> AsyncSubject<T> create() {
        final SubjectSubscriptionManager<T> state = new SubjectSubscriptionManager<>();
        state.onTerminated = new Action1<SubjectSubscriptionManager.SubjectObserver<T>>() { // from class: rx.subjects.AsyncSubject.1
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((SubjectSubscriptionManager.SubjectObserver) ((SubjectSubscriptionManager.SubjectObserver) x0));
            }

            public void call(SubjectSubscriptionManager.SubjectObserver<T> o) {
                Object v = SubjectSubscriptionManager.this.getLatest();
                NotificationLite<T> nl = SubjectSubscriptionManager.this.f1643nl;
                if (v == null || nl.isCompleted(v)) {
                    o.onCompleted();
                } else if (nl.isError(v)) {
                    o.onError(nl.getError(v));
                } else {
                    o.actual.setProducer(new SingleProducer(o.actual, nl.getValue(v)));
                }
            }
        };
        return new AsyncSubject<>(state, state);
    }

    protected AsyncSubject(Observable.OnSubscribe<T> onSubscribe, SubjectSubscriptionManager<T> state) {
        super(onSubscribe);
        this.f1641nl = NotificationLite.instance();
        this.state = state;
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        if (this.state.active) {
            Object last = this.lastValue;
            if (last == null) {
                last = this.f1641nl.completed();
            }
            SubjectSubscriptionManager.SubjectObserver<T>[] arr$ = this.state.terminate(last);
            for (SubjectSubscriptionManager.SubjectObserver<T> bo : arr$) {
                if (last == this.f1641nl.completed()) {
                    bo.onCompleted();
                } else {
                    bo.actual.setProducer(new SingleProducer(bo.actual, this.f1641nl.getValue(last)));
                }
            }
        }
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        if (this.state.active) {
            Object n = this.f1641nl.error(e);
            List<Throwable> errors = null;
            SubjectSubscriptionManager.SubjectObserver<T>[] arr$ = this.state.terminate(n);
            for (SubjectSubscriptionManager.SubjectObserver<T> bo : arr$) {
                try {
                    bo.onError(e);
                } catch (Throwable e2) {
                    if (errors == null) {
                        errors = new ArrayList<>();
                    }
                    errors.add(e2);
                }
            }
            Exceptions.throwIfAny(errors);
        }
    }

    @Override // p045rx.Observer
    public void onNext(T v) {
        this.lastValue = this.f1641nl.next(v);
    }

    @Override // p045rx.subjects.Subject
    public boolean hasObservers() {
        return this.state.observers().length > 0;
    }

    @Beta
    public boolean hasValue() {
        Object v = this.lastValue;
        Object o = this.state.getLatest();
        return !this.f1641nl.isError(o) && this.f1641nl.isNext(v);
    }

    @Beta
    public boolean hasThrowable() {
        Object o = this.state.getLatest();
        return this.f1641nl.isError(o);
    }

    @Beta
    public boolean hasCompleted() {
        Object o = this.state.getLatest();
        return (o == null || this.f1641nl.isError(o)) ? false : true;
    }

    @Beta
    public T getValue() {
        Object v = this.lastValue;
        Object o = this.state.getLatest();
        if (this.f1641nl.isError(o) || !this.f1641nl.isNext(v)) {
            return null;
        }
        return this.f1641nl.getValue(v);
    }

    @Beta
    public Throwable getThrowable() {
        Object o = this.state.getLatest();
        if (this.f1641nl.isError(o)) {
            return this.f1641nl.getError(o);
        }
        return null;
    }
}
