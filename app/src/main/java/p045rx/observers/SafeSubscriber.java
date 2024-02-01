package p045rx.observers;

import java.util.Arrays;
import p045rx.Subscriber;
import p045rx.exceptions.CompositeException;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.OnCompletedFailedException;
import p045rx.exceptions.OnErrorFailedException;
import p045rx.exceptions.OnErrorNotImplementedException;
import p045rx.exceptions.UnsubscribeFailedException;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public class SafeSubscriber<T> extends Subscriber<T> {
    private final Subscriber<? super T> actual;
    boolean done;

    public SafeSubscriber(Subscriber<? super T> actual) {
        super(actual);
        this.actual = actual;
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        UnsubscribeFailedException unsubscribeFailedException;
        if (!this.done) {
            this.done = true;
            try {
                this.actual.onCompleted();
                try {
                    unsubscribe();
                } finally {
                }
            } catch (Throwable e) {
                try {
                    Exceptions.throwIfFatal(e);
                    RxJavaHooks.onError(e);
                    throw new OnCompletedFailedException(e.getMessage(), e);
                } catch (Throwable th) {
                    try {
                        unsubscribe();
                        throw th;
                    } finally {
                    }
                }
            }
        }
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        Exceptions.throwIfFatal(e);
        if (!this.done) {
            this.done = true;
            _onError(e);
        }
    }

    @Override // p045rx.Observer
    public void onNext(T args) {
        try {
            if (!this.done) {
                this.actual.onNext(args);
            }
        } catch (Throwable e) {
            Exceptions.throwOrReport(e, this);
        }
    }

    protected void _onError(Throwable e) {
        RxJavaHooks.onError(e);
        try {
            this.actual.onError(e);
            try {
                unsubscribe();
            } catch (Throwable unsubscribeException) {
                RxJavaHooks.onError(unsubscribeException);
                throw new OnErrorFailedException(unsubscribeException);
            }
        } catch (OnErrorNotImplementedException e2) {
            try {
                unsubscribe();
                throw e2;
            } catch (Throwable unsubscribeException2) {
                RxJavaHooks.onError(unsubscribeException2);
                throw new OnErrorNotImplementedException("Observer.onError not implemented and error while unsubscribing.", new CompositeException(Arrays.asList(e, unsubscribeException2)));
            }
        } catch (Throwable e22) {
            RxJavaHooks.onError(e22);
            try {
                unsubscribe();
                throw new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError", new CompositeException(Arrays.asList(e, e22)));
            } catch (Throwable unsubscribeException3) {
                RxJavaHooks.onError(unsubscribeException3);
                throw new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError and during unsubscription.", new CompositeException(Arrays.asList(e, e22, unsubscribeException3)));
            }
        }
    }

    public Subscriber<? super T> getActual() {
        return this.actual;
    }
}
