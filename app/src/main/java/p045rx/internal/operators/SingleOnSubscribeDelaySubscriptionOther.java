package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Single;
import p045rx.SingleSubscriber;
import p045rx.Subscriber;
import p045rx.plugins.RxJavaHooks;
import p045rx.subscriptions.SerialSubscription;

/* loaded from: classes.dex */
public final class SingleOnSubscribeDelaySubscriptionOther<T> implements Single.OnSubscribe<T> {
    final Single<? extends T> main;
    final Observable<?> other;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((SingleSubscriber) ((SingleSubscriber) x0));
    }

    public SingleOnSubscribeDelaySubscriptionOther(Single<? extends T> main, Observable<?> other) {
        this.main = main;
        this.other = other;
    }

    public void call(final SingleSubscriber<? super T> subscriber) {
        final SingleSubscriber<T> child = new SingleSubscriber<T>() { // from class: rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther.1
            @Override // p045rx.SingleSubscriber
            public void onSuccess(T value) {
                subscriber.onSuccess(value);
            }

            @Override // p045rx.SingleSubscriber
            public void onError(Throwable error) {
                subscriber.onError(error);
            }
        };
        final SerialSubscription serial = new SerialSubscription();
        subscriber.add(serial);
        Subscriber<Object> otherSubscriber = new Subscriber<Object>() { // from class: rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther.2
            boolean done;

            @Override // p045rx.Observer
            public void onNext(Object t) {
                onCompleted();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                if (this.done) {
                    RxJavaHooks.onError(e);
                } else {
                    this.done = true;
                    child.onError(e);
                }
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                if (!this.done) {
                    this.done = true;
                    serial.set(child);
                    SingleOnSubscribeDelaySubscriptionOther.this.main.subscribe(child);
                }
            }
        };
        serial.set(otherSubscriber);
        this.other.subscribe((Subscriber<? super Object>) otherSubscriber);
    }
}
