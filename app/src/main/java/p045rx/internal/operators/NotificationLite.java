package p045rx.internal.operators;

import java.io.Serializable;
import p045rx.Notification;
import p045rx.Observer;

/* loaded from: classes.dex */
public final class NotificationLite<T> {
    private static final NotificationLite INSTANCE = new NotificationLite();
    private static final Object ON_COMPLETED_SENTINEL = new Serializable() { // from class: rx.internal.operators.NotificationLite.1
        private static final long serialVersionUID = 1;

        public String toString() {
            return "Notification=>Completed";
        }
    };
    private static final Object ON_NEXT_NULL_SENTINEL = new Serializable() { // from class: rx.internal.operators.NotificationLite.2
        private static final long serialVersionUID = 2;

        public String toString() {
            return "Notification=>NULL";
        }
    };

    private NotificationLite() {
    }

    public static <T> NotificationLite<T> instance() {
        return INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OnErrorSentinel implements Serializable {
        private static final long serialVersionUID = 3;

        /* renamed from: e */
        final Throwable f1588e;

        public OnErrorSentinel(Throwable e) {
            this.f1588e = e;
        }

        public String toString() {
            return "Notification=>Error:" + this.f1588e;
        }
    }

    public Object next(T t) {
        if (t == null) {
            return ON_NEXT_NULL_SENTINEL;
        }
        return t;
    }

    public Object completed() {
        return ON_COMPLETED_SENTINEL;
    }

    public Object error(Throwable e) {
        return new OnErrorSentinel(e);
    }

    public boolean accept(Observer<? super T> o, Object n) {
        if (n == ON_COMPLETED_SENTINEL) {
            o.onCompleted();
            return true;
        }
        if (n == ON_NEXT_NULL_SENTINEL) {
            o.onNext(null);
            return false;
        }
        if (n != null) {
            if (n.getClass() == OnErrorSentinel.class) {
                o.onError(((OnErrorSentinel) n).f1588e);
                return true;
            }
            o.onNext(n);
            return false;
        }
        throw new IllegalArgumentException("The lite notification can not be null");
    }

    public boolean isCompleted(Object n) {
        return n == ON_COMPLETED_SENTINEL;
    }

    public boolean isError(Object n) {
        return n instanceof OnErrorSentinel;
    }

    public boolean isNull(Object n) {
        return n == ON_NEXT_NULL_SENTINEL;
    }

    public boolean isNext(Object n) {
        return (n == null || isError(n) || isCompleted(n)) ? false : true;
    }

    public Notification.Kind kind(Object n) {
        if (n == null) {
            throw new IllegalArgumentException("The lite notification can not be null");
        }
        if (n == ON_COMPLETED_SENTINEL) {
            return Notification.Kind.OnCompleted;
        }
        if (n instanceof OnErrorSentinel) {
            return Notification.Kind.OnError;
        }
        return Notification.Kind.OnNext;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T getValue(Object n) {
        if (n == ON_NEXT_NULL_SENTINEL) {
            return null;
        }
        return n;
    }

    public Throwable getError(Object n) {
        return ((OnErrorSentinel) n).f1588e;
    }
}
