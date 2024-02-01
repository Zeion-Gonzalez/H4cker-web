package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Subscriber;
import p045rx.functions.Func1;
import p045rx.observers.SerializedObserver;
import p045rx.observers.SerializedSubscriber;
import p045rx.subjects.UnicastSubject;
import p045rx.subscriptions.CompositeSubscription;

/* loaded from: classes.dex */
public final class OperatorWindowWithStartEndObservable<T, U, V> implements Observable.Operator<Observable<T>, T> {
    final Func1<? super U, ? extends Observable<? extends V>> windowClosingSelector;
    final Observable<? extends U> windowOpenings;

    @Override // p045rx.functions.Func1
    public /* bridge */ /* synthetic */ Object call(Object x0) {
        return call((Subscriber) ((Subscriber) x0));
    }

    public OperatorWindowWithStartEndObservable(Observable<? extends U> windowOpenings, Func1<? super U, ? extends Observable<? extends V>> windowClosingSelector) {
        this.windowOpenings = windowOpenings;
        this.windowClosingSelector = windowClosingSelector;
    }

    public Subscriber<? super T> call(Subscriber<? super Observable<T>> child) {
        CompositeSubscription csub = new CompositeSubscription();
        child.add(csub);
        final OperatorWindowWithStartEndObservable<T, U, V>.SourceSubscriber sub = new SourceSubscriber(child, csub);
        Subscriber<U> open = new Subscriber<U>() { // from class: rx.internal.operators.OperatorWindowWithStartEndObservable.1
            @Override // p045rx.Subscriber
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override // p045rx.Observer
            public void onNext(U t) {
                sub.beginWindow(t);
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                sub.onError(e);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                sub.onCompleted();
            }
        };
        csub.add(sub);
        csub.add(open);
        this.windowOpenings.unsafeSubscribe(open);
        return sub;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SerializedSubject<T> {
        final Observer<T> consumer;
        final Observable<T> producer;

        public SerializedSubject(Observer<T> consumer, Observable<T> producer) {
            this.consumer = new SerializedObserver(consumer);
            this.producer = producer;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class SourceSubscriber extends Subscriber<T> {
        final Subscriber<? super Observable<T>> child;
        final CompositeSubscription csub;
        boolean done;
        final Object guard = new Object();
        final List<SerializedSubject<T>> chunks = new LinkedList();

        public SourceSubscriber(Subscriber<? super Observable<T>> child, CompositeSubscription csub) {
            this.child = new SerializedSubscriber(child);
            this.csub = csub;
        }

        @Override // p045rx.Subscriber
        public void onStart() {
            request(Long.MAX_VALUE);
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            synchronized (this.guard) {
                if (!this.done) {
                    List<SerializedSubject<T>> list = new ArrayList<>(this.chunks);
                    for (SerializedSubject<T> cs : list) {
                        cs.consumer.onNext(t);
                    }
                }
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            try {
                synchronized (this.guard) {
                    if (!this.done) {
                        this.done = true;
                        List<SerializedSubject<T>> list = new ArrayList<>(this.chunks);
                        this.chunks.clear();
                        for (SerializedSubject<T> cs : list) {
                            cs.consumer.onError(e);
                        }
                        this.child.onError(e);
                    }
                }
            } finally {
                this.csub.unsubscribe();
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            try {
                synchronized (this.guard) {
                    if (!this.done) {
                        this.done = true;
                        List<SerializedSubject<T>> list = new ArrayList<>(this.chunks);
                        this.chunks.clear();
                        for (SerializedSubject<T> cs : list) {
                            cs.consumer.onCompleted();
                        }
                        this.child.onCompleted();
                    }
                }
            } finally {
                this.csub.unsubscribe();
            }
        }

        void beginWindow(U token) {
            final SerializedSubject<T> s = createSerializedSubject();
            synchronized (this.guard) {
                if (!this.done) {
                    this.chunks.add(s);
                    this.child.onNext(s.producer);
                    try {
                        Observable<? extends V> end = OperatorWindowWithStartEndObservable.this.windowClosingSelector.call(token);
                        Subscriber<V> v = new Subscriber<V>() { // from class: rx.internal.operators.OperatorWindowWithStartEndObservable.SourceSubscriber.1
                            boolean once = true;

                            @Override // p045rx.Observer
                            public void onNext(V t) {
                                onCompleted();
                            }

                            @Override // p045rx.Observer
                            public void onError(Throwable e) {
                                SourceSubscriber.this.onError(e);
                            }

                            @Override // p045rx.Observer
                            public void onCompleted() {
                                if (this.once) {
                                    this.once = false;
                                    SourceSubscriber.this.endWindow(s);
                                    SourceSubscriber.this.csub.remove(this);
                                }
                            }
                        };
                        this.csub.add(v);
                        end.unsafeSubscribe(v);
                    } catch (Throwable e) {
                        onError(e);
                    }
                }
            }
        }

        void endWindow(SerializedSubject<T> window) {
            boolean terminate = false;
            synchronized (this.guard) {
                if (!this.done) {
                    Iterator<SerializedSubject<T>> it = this.chunks.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        SerializedSubject<T> s = it.next();
                        if (s == window) {
                            terminate = true;
                            it.remove();
                            break;
                        }
                    }
                    if (terminate) {
                        window.consumer.onCompleted();
                    }
                }
            }
        }

        SerializedSubject<T> createSerializedSubject() {
            UnicastSubject<T> bus = UnicastSubject.create();
            return new SerializedSubject<>(bus, bus);
        }
    }
}
