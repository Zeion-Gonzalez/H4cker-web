package p045rx.observers;

import p045rx.Completable;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.exceptions.CompositeException;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.OnCompletedFailedException;
import p045rx.exceptions.OnErrorFailedException;
import p045rx.plugins.RxJavaHooks;

@Experimental
/* loaded from: classes.dex */
public final class SafeCompletableSubscriber implements Completable.CompletableSubscriber, Subscription {
    final Completable.CompletableSubscriber actual;
    boolean done;

    /* renamed from: s */
    Subscription f1637s;

    public SafeCompletableSubscriber(Completable.CompletableSubscriber actual) {
        this.actual = actual;
    }

    @Override // rx.Completable.CompletableSubscriber
    public void onCompleted() {
        if (!this.done) {
            this.done = true;
            try {
                this.actual.onCompleted();
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                throw new OnCompletedFailedException(ex);
            }
        }
    }

    @Override // rx.Completable.CompletableSubscriber
    public void onError(Throwable e) {
        RxJavaHooks.onError(e);
        if (!this.done) {
            this.done = true;
            try {
                this.actual.onError(e);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                throw new OnErrorFailedException(new CompositeException(e, ex));
            }
        }
    }

    @Override // rx.Completable.CompletableSubscriber
    public void onSubscribe(Subscription d) {
        this.f1637s = d;
        try {
            this.actual.onSubscribe(this);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            d.unsubscribe();
            onError(ex);
        }
    }

    @Override // p045rx.Subscription
    public void unsubscribe() {
        this.f1637s.unsubscribe();
    }

    @Override // p045rx.Subscription
    public boolean isUnsubscribed() {
        return this.done || this.f1637s.isUnsubscribed();
    }
}
