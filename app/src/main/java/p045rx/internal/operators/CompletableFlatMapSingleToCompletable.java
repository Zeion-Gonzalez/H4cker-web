package p045rx.internal.operators;

import p045rx.Completable;
import p045rx.Single;
import p045rx.SingleSubscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;

/* loaded from: classes.dex */
public final class CompletableFlatMapSingleToCompletable<T> implements Completable.CompletableOnSubscribe {
    final Func1<? super T, ? extends Completable> mapper;
    final Single<T> source;

    public CompletableFlatMapSingleToCompletable(Single<T> source, Func1<? super T, ? extends Completable> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override // p045rx.functions.Action1
    public void call(Completable.CompletableSubscriber t) {
        SourceSubscriber<T> parent = new SourceSubscriber<>(t, this.mapper);
        t.onSubscribe(parent);
        this.source.subscribe(parent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SourceSubscriber<T> extends SingleSubscriber<T> implements Completable.CompletableSubscriber {
        final Completable.CompletableSubscriber actual;
        final Func1<? super T, ? extends Completable> mapper;

        public SourceSubscriber(Completable.CompletableSubscriber actual, Func1<? super T, ? extends Completable> mapper) {
            this.actual = actual;
            this.mapper = mapper;
        }

        @Override // p045rx.SingleSubscriber
        public void onSuccess(T value) {
            try {
                Completable c = this.mapper.call(value);
                if (c == null) {
                    onError(new NullPointerException("The mapper returned a null Completable"));
                } else {
                    c.subscribe(this);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                onError(ex);
            }
        }

        @Override // p045rx.SingleSubscriber
        public void onError(Throwable error) {
            this.actual.onError(error);
        }

        @Override // rx.Completable.CompletableSubscriber
        public void onCompleted() {
            this.actual.onCompleted();
        }

        @Override // rx.Completable.CompletableSubscriber
        public void onSubscribe(Subscription d) {
            add(d);
        }
    }
}
