package com.instabug.library.core.eventbus;

import p045rx.Observable;
import p045rx.Subscription;
import p045rx.functions.Action1;
import p045rx.subjects.PublishSubject;
import p045rx.subjects.Subject;

/* loaded from: classes.dex */
public class EventBus<T> {
    private final Subject<T, T> subject;

    /* JADX INFO: Access modifiers changed from: protected */
    public EventBus() {
        this(PublishSubject.create());
    }

    protected EventBus(Subject<T, T> subject) {
        this.subject = subject;
    }

    public <E extends T> void post(E e) {
        this.subject.onNext(e);
    }

    public Subscription subscribe(Action1<? super T> action1) {
        return this.subject.subscribe(action1);
    }

    public <E extends T> Observable<E> observeEvents(Class<E> cls) {
        return (Observable<E>) this.subject.ofType(cls);
    }

    public boolean hasObservers() {
        return this.subject.hasObservers();
    }
}
