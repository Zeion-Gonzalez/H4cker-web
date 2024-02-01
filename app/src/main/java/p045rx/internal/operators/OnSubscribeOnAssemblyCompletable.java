package p045rx.internal.operators;

import p045rx.Completable;
import p045rx.Subscription;
import p045rx.exceptions.AssemblyStackTraceException;

/* loaded from: classes.dex */
public final class OnSubscribeOnAssemblyCompletable<T> implements Completable.CompletableOnSubscribe {
    public static volatile boolean fullStackTrace;
    final Completable.CompletableOnSubscribe source;
    final String stacktrace = OnSubscribeOnAssembly.createStacktrace();

    public OnSubscribeOnAssemblyCompletable(Completable.CompletableOnSubscribe source) {
        this.source = source;
    }

    @Override // p045rx.functions.Action1
    public void call(Completable.CompletableSubscriber t) {
        this.source.call(new OnAssemblyCompletableSubscriber(t, this.stacktrace));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OnAssemblyCompletableSubscriber implements Completable.CompletableSubscriber {
        final Completable.CompletableSubscriber actual;
        final String stacktrace;

        public OnAssemblyCompletableSubscriber(Completable.CompletableSubscriber actual, String stacktrace) {
            this.actual = actual;
            this.stacktrace = stacktrace;
        }

        @Override // rx.Completable.CompletableSubscriber
        public void onSubscribe(Subscription d) {
            this.actual.onSubscribe(d);
        }

        @Override // rx.Completable.CompletableSubscriber
        public void onCompleted() {
            this.actual.onCompleted();
        }

        @Override // rx.Completable.CompletableSubscriber
        public void onError(Throwable e) {
            new AssemblyStackTraceException(this.stacktrace).attachTo(e);
            this.actual.onError(e);
        }
    }
}
