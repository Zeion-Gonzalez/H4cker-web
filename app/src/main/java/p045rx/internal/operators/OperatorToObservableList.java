package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import p045rx.Observable;
import p045rx.Subscriber;
import p045rx.exceptions.Exceptions;
import p045rx.internal.producers.SingleDelayedProducer;

/* loaded from: classes.dex */
public final class OperatorToObservableList<T> implements Observable.Operator<List<T>, T> {
    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Holder {
        static final OperatorToObservableList<Object> INSTANCE = new OperatorToObservableList<>();

        Holder() {
        }
    }

    public static <T> OperatorToObservableList<T> instance() {
        return (OperatorToObservableList<T>) Holder.INSTANCE;
    }

    OperatorToObservableList() {
    }

    public Subscriber<? super T> call(final Subscriber<? super List<T>> o) {
        final SingleDelayedProducer<List<T>> producer = new SingleDelayedProducer<>(o);
        Subscriber subscriber = (Subscriber<T>) new Subscriber<T>() { // from class: rx.internal.operators.OperatorToObservableList.1
            boolean completed;
            List<T> list = new LinkedList();

            @Override // p045rx.Subscriber
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                if (!this.completed) {
                    this.completed = true;
                    try {
                        ArrayList arrayList = new ArrayList(this.list);
                        this.list = null;
                        producer.setValue(arrayList);
                    } catch (Throwable t) {
                        Exceptions.throwOrReport(t, this);
                    }
                }
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override // p045rx.Observer
            public void onNext(T value) {
                if (!this.completed) {
                    this.list.add(value);
                }
            }
        };
        o.add(subscriber);
        o.setProducer(producer);
        return subscriber;
    }
}
