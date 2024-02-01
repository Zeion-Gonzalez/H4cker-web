package p045rx.internal.operators;

import p045rx.Observable;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.functions.Func1;
import p045rx.internal.producers.ProducerArbiter;
import p045rx.plugins.RxJavaHooks;
import p045rx.subscriptions.SerialSubscription;

/* loaded from: classes.dex */
public final class OperatorOnErrorResumeNextViaFunction<T> implements Observable.Operator<T, T> {
    final Func1<Throwable, ? extends Observable<? extends T>> resumeFunction;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public static <T> OperatorOnErrorResumeNextViaFunction<T> withSingle(final Func1<Throwable, ? extends T> resumeFunction) {
        return new OperatorOnErrorResumeNextViaFunction<>(new Func1<Throwable, Observable<? extends T>>() { // from class: rx.internal.operators.OperatorOnErrorResumeNextViaFunction.1
            @Override // p045rx.functions.Func1
            public Observable<? extends T> call(Throwable t) {
                return Observable.just(Func1.this.call(t));
            }
        });
    }

    public static <T> OperatorOnErrorResumeNextViaFunction<T> withOther(final Observable<? extends T> other) {
        return new OperatorOnErrorResumeNextViaFunction<>(new Func1<Throwable, Observable<? extends T>>() { // from class: rx.internal.operators.OperatorOnErrorResumeNextViaFunction.2
            @Override // p045rx.functions.Func1
            public Observable<? extends T> call(Throwable t) {
                return Observable.this;
            }
        });
    }

    public static <T> OperatorOnErrorResumeNextViaFunction<T> withException(final Observable<? extends T> other) {
        return new OperatorOnErrorResumeNextViaFunction<>(new Func1<Throwable, Observable<? extends T>>() { // from class: rx.internal.operators.OperatorOnErrorResumeNextViaFunction.3
            @Override // p045rx.functions.Func1
            public Observable<? extends T> call(Throwable t) {
                return t instanceof Exception ? Observable.this : Observable.error(t);
            }
        });
    }

    public OperatorOnErrorResumeNextViaFunction(Func1<Throwable, ? extends Observable<? extends T>> f) {
        this.resumeFunction = f;
    }

    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        final ProducerArbiter pa = new ProducerArbiter();
        final SerialSubscription ssub = new SerialSubscription();
        Subscriber subscriber = (Subscriber<T>) new Subscriber<T>() { // from class: rx.internal.operators.OperatorOnErrorResumeNextViaFunction.4
            private boolean done;
            long produced;

            @Override // p045rx.Observer
            public void onCompleted() {
                if (!this.done) {
                    this.done = true;
                    child.onCompleted();
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                if (this.done) {
                    Exceptions.throwIfFatal(e);
                    RxJavaHooks.onError(e);
                    return;
                }
                this.done = true;
                try {
                    unsubscribe();
                    Subscriber<T> next = new Subscriber<T>() { // from class: rx.internal.operators.OperatorOnErrorResumeNextViaFunction.4.1
                        @Override // p045rx.Observer
                        public void onNext(T t) {
                            child.onNext(t);
                        }

                        @Override // p045rx.Observer
                        public void onError(Throwable e2) {
                            child.onError(e2);
                        }

                        @Override // p045rx.Observer
                        public void onCompleted() {
                            child.onCompleted();
                        }

                        @Override // p045rx.Subscriber
                        public void setProducer(Producer producer) {
                            pa.setProducer(producer);
                        }
                    };
                    ssub.set(next);
                    long p = this.produced;
                    if (p != 0) {
                        pa.produced(p);
                    }
                    Observable<? extends T> resume = OperatorOnErrorResumeNextViaFunction.this.resumeFunction.call(e);
                    resume.unsafeSubscribe(next);
                } catch (Throwable e2) {
                    Exceptions.throwOrReport(e2, child);
                }
            }

            @Override // p045rx.Observer
            public void onNext(T t) {
                if (!this.done) {
                    this.produced++;
                    child.onNext(t);
                }
            }

            @Override // p045rx.Subscriber
            public void setProducer(Producer producer) {
                pa.setProducer(producer);
            }
        };
        ssub.set(subscriber);
        child.add(ssub);
        child.setProducer(pa);
        return subscriber;
    }
}
