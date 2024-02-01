package p045rx.observables;

import p045rx.Observable;
import p045rx.Subscription;
import p045rx.annotations.Beta;
import p045rx.functions.Action1;
import p045rx.functions.Actions;
import p045rx.internal.operators.OnSubscribeAutoConnect;
import p045rx.internal.operators.OnSubscribeRefCount;

/* loaded from: classes.dex */
public abstract class ConnectableObservable<T> extends Observable<T> {
    public abstract void connect(Action1<? super Subscription> action1);

    /* JADX INFO: Access modifiers changed from: protected */
    public ConnectableObservable(Observable.OnSubscribe<T> onSubscribe) {
        super(onSubscribe);
    }

    public final Subscription connect() {
        final Subscription[] out = new Subscription[1];
        connect(new Action1<Subscription>() { // from class: rx.observables.ConnectableObservable.1
            @Override // p045rx.functions.Action1
            public void call(Subscription t1) {
                out[0] = t1;
            }
        });
        return out[0];
    }

    public Observable<T> refCount() {
        return create(new OnSubscribeRefCount(this));
    }

    @Beta
    public Observable<T> autoConnect() {
        return autoConnect(1);
    }

    @Beta
    public Observable<T> autoConnect(int numberOfSubscribers) {
        return autoConnect(numberOfSubscribers, Actions.empty());
    }

    @Beta
    public Observable<T> autoConnect(int numberOfSubscribers, Action1<? super Subscription> connection) {
        if (numberOfSubscribers > 0) {
            return create(new OnSubscribeAutoConnect(this, numberOfSubscribers, connection));
        }
        connect(connection);
        return this;
    }
}
