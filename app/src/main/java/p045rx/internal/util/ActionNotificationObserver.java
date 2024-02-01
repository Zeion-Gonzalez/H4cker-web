package p045rx.internal.util;

import p045rx.Notification;
import p045rx.Observer;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public final class ActionNotificationObserver<T> implements Observer<T> {
    final Action1<Notification<? super T>> onNotification;

    public ActionNotificationObserver(Action1<Notification<? super T>> onNotification) {
        this.onNotification = onNotification;
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.onNotification.call(Notification.createOnNext(t));
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.onNotification.call(Notification.createOnError(e));
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.onNotification.call(Notification.createOnCompleted());
    }
}
