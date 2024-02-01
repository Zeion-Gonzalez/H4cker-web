package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.AssemblyStackTraceException;

/* loaded from: classes.dex */
public final class OnSubscribeOnAssembly<T> implements Observable.OnSubscribe<T> {
    public static volatile boolean fullStackTrace;
    final Observable.OnSubscribe<T> source;
    final String stacktrace = createStacktrace();

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((Subscriber) ((Subscriber) x0));
    }

    public OnSubscribeOnAssembly(Observable.OnSubscribe<T> source) {
        this.source = source;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String createStacktrace() {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder("Assembly trace:");
        for (StackTraceElement e : stes) {
            String row = e.toString();
            if (fullStackTrace || (e.getLineNumber() > 1 && !row.contains("RxJavaHooks.") && !row.contains("OnSubscribeOnAssembly") && !row.contains(".junit.runner") && !row.contains(".junit4.runner") && !row.contains(".junit.internal") && !row.contains("sun.reflect") && !row.contains("java.lang.Thread.") && !row.contains("ThreadPoolExecutor") && !row.contains("org.apache.catalina.") && !row.contains("org.apache.tomcat."))) {
                sb.append("\n at ").append(row);
            }
        }
        return sb.append("\nOriginal exception:").toString();
    }

    public void call(Subscriber<? super T> t) {
        this.source.call(new OnAssemblySubscriber(t, this.stacktrace));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OnAssemblySubscriber<T> extends Subscriber<T> {
        final Subscriber<? super T> actual;
        final String stacktrace;

        public OnAssemblySubscriber(Subscriber<? super T> actual, String stacktrace) {
            super(actual);
            this.actual = actual;
            this.stacktrace = stacktrace;
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            this.actual.onCompleted();
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            new AssemblyStackTraceException(this.stacktrace).attachTo(e);
            this.actual.onError(e);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            this.actual.onNext(t);
        }
    }
}
