package p045rx.internal.operators;

import java.util.Arrays;
import p045rx.Single;
import p045rx.SingleSubscriber;
import p045rx.exceptions.CompositeException;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Action1;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public final class SingleOnSubscribeUsing<T, Resource> implements Single.OnSubscribe<T> {
    final Action1<? super Resource> disposeAction;
    final boolean disposeEagerly;
    final Func0<Resource> resourceFactory;
    final Func1<? super Resource, ? extends Single<? extends T>> singleFactory;

    @Override // p045rx.functions.Action1
    public /* bridge */ /* synthetic */ void call(Object x0) {
        call((SingleSubscriber) ((SingleSubscriber) x0));
    }

    public SingleOnSubscribeUsing(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Single<? extends T>> observableFactory, Action1<? super Resource> disposeAction, boolean disposeEagerly) {
        this.resourceFactory = resourceFactory;
        this.singleFactory = observableFactory;
        this.disposeAction = disposeAction;
        this.disposeEagerly = disposeEagerly;
    }

    public void call(final SingleSubscriber<? super T> child) {
        try {
            final Resource resource = this.resourceFactory.call();
            try {
                Single<? extends T> single = this.singleFactory.call(resource);
                if (single == null) {
                    handleSubscriptionTimeError(child, resource, new NullPointerException("The single"));
                    return;
                }
                SingleSubscriber<T> parent = new SingleSubscriber<T>() { // from class: rx.internal.operators.SingleOnSubscribeUsing.1
                    @Override // p045rx.SingleSubscriber
                    public void onSuccess(T value) {
                        if (SingleOnSubscribeUsing.this.disposeEagerly) {
                            try {
                                SingleOnSubscribeUsing.this.disposeAction.call((Object) resource);
                            } catch (Throwable ex) {
                                Exceptions.throwIfFatal(ex);
                                child.onError(ex);
                                return;
                            }
                        }
                        child.onSuccess(value);
                        if (!SingleOnSubscribeUsing.this.disposeEagerly) {
                            try {
                                SingleOnSubscribeUsing.this.disposeAction.call((Object) resource);
                            } catch (Throwable ex2) {
                                Exceptions.throwIfFatal(ex2);
                                RxJavaHooks.onError(ex2);
                            }
                        }
                    }

                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // p045rx.SingleSubscriber
                    public void onError(Throwable error) {
                        SingleOnSubscribeUsing.this.handleSubscriptionTimeError(child, resource, error);
                    }
                };
                child.add(parent);
                single.subscribe((SingleSubscriber<? super Object>) parent);
            } catch (Throwable ex) {
                handleSubscriptionTimeError(child, resource, ex);
            }
        } catch (Throwable ex2) {
            Exceptions.throwIfFatal(ex2);
            child.onError(ex2);
        }
    }

    void handleSubscriptionTimeError(SingleSubscriber<? super T> t, Resource resource, Throwable ex) {
        Exceptions.throwIfFatal(ex);
        if (this.disposeEagerly) {
            try {
                this.disposeAction.call(resource);
            } catch (Throwable ex2) {
                Exceptions.throwIfFatal(ex2);
                ex = new CompositeException(Arrays.asList(ex, ex2));
            }
        }
        t.onError(ex);
        if (!this.disposeEagerly) {
            try {
                this.disposeAction.call(resource);
            } catch (Throwable ex22) {
                Exceptions.throwIfFatal(ex22);
                RxJavaHooks.onError(ex22);
            }
        }
    }
}
