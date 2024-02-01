package p045rx;

import p045rx.annotations.Experimental;

@Experimental
/* loaded from: classes.dex */
public interface AsyncEmitter<T> extends Observer<T> {

    /* loaded from: classes.dex */
    public enum BackpressureMode {
        NONE,
        ERROR,
        BUFFER,
        DROP,
        LATEST
    }

    /* loaded from: classes.dex */
    public interface Cancellable {
        void cancel() throws Exception;
    }

    long requested();

    void setCancellation(Cancellable cancellable);

    void setSubscription(Subscription subscription);
}
