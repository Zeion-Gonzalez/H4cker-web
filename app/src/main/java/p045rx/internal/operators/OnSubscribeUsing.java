package p045rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.CompositeException;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.observers.Subscribers;

/* loaded from: classes.dex */
public final class OnSubscribeUsing<T, Resource> implements Observable.OnSubscribe<T> {
    private final Action1<? super Resource> dispose;
    private final boolean disposeEagerly;
    private final Func1<? super Resource, ? extends Observable<? extends T>> observableFactory;
    private final Func0<Resource> resourceFactory;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeUsing(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Observable<? extends T>> observableFactory, Action1<? super Resource> dispose, boolean disposeEagerly) {
        this.resourceFactory = resourceFactory;
        this.observableFactory = observableFactory;
        this.dispose = dispose;
        this.disposeEagerly = disposeEagerly;
    }

    public void call(Subscriber<? super T> subscriber) {
        Observable<? extends T> observable;
        try {
            Resource resource = this.resourceFactory.call();
            DisposeAction<Resource> disposeOnceOnly = new DisposeAction<>(this.dispose, resource);
            subscriber.add(disposeOnceOnly);
            try {
                Observable<? extends T> source = this.observableFactory.call(resource);
                if (this.disposeEagerly) {
                    observable = source.doOnTerminate(disposeOnceOnly);
                } else {
                    observable = source.doAfterTerminate(disposeOnceOnly);
                }
                try {
                    observable.unsafeSubscribe(Subscribers.wrap(subscriber));
                } catch (Throwable e) {
                    Throwable disposeError = dispose(disposeOnceOnly);
                    Exceptions.throwIfFatal(e);
                    Exceptions.throwIfFatal(disposeError);
                    if (disposeError != null) {
                        subscriber.onError(new CompositeException(e, disposeError));
                    } else {
                        subscriber.onError(e);
                    }
                }
            } catch (Throwable e2) {
                Throwable disposeError2 = dispose(disposeOnceOnly);
                Exceptions.throwIfFatal(e2);
                Exceptions.throwIfFatal(disposeError2);
                if (disposeError2 != null) {
                    subscriber.onError(new CompositeException(e2, disposeError2));
                } else {
                    subscriber.onError(e2);
                }
            }
        } catch (Throwable e3) {
            Exceptions.throwOrReport(e3, subscriber);
        }
    }

    private Throwable dispose(Action0 disposeOnceOnly) {
        try {
            disposeOnceOnly.call();
            return null;
        } catch (Throwable th) {
            return th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DisposeAction<Resource> extends AtomicBoolean implements Action0, Subscription {
        private static final long serialVersionUID = 4262875056400218316L;
        private Action1<? super Resource> dispose;
        private Resource resource;

        DisposeAction(Action1<? super Resource> dispose, Resource resource) {
            this.dispose = dispose;
            this.resource = resource;
            lazySet(false);
        }

        /* JADX WARN: Type inference failed for: r2v0, types: [Resource, rx.functions.Action1<? super Resource>] */
        @Override // p045rx.functions.Action0
        public void call() {
            ?? r2 = (Resource) false;
            if (compareAndSet(false, true)) {
                try {
                    this.dispose.call((Resource) this.resource);
                } finally {
                    this.resource = null;
                    this.dispose = null;
                }
            }
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return get();
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            call();
        }
    }
}
