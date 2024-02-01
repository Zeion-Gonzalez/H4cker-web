package p045rx.observables;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Producer;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.annotations.Experimental;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.OnErrorNotImplementedException;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Actions;
import p045rx.functions.Func1;
import p045rx.internal.operators.BlockingOperatorLatest;
import p045rx.internal.operators.BlockingOperatorMostRecent;
import p045rx.internal.operators.BlockingOperatorNext;
import p045rx.internal.operators.BlockingOperatorToFuture;
import p045rx.internal.operators.BlockingOperatorToIterator;
import p045rx.internal.operators.NotificationLite;
import p045rx.internal.util.BlockingUtils;
import p045rx.internal.util.UtilityFunctions;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
public final class BlockingObservable<T> {
    static final Object ON_START = new Object();
    static final Object SET_PRODUCER = new Object();
    static final Object UNSUBSCRIBE = new Object();

    /* renamed from: o */
    private final Observable<? extends T> f1636o;

    private BlockingObservable(Observable<? extends T> o) {
        this.f1636o = o;
    }

    public static <T> BlockingObservable<T> from(Observable<? extends T> o) {
        return new BlockingObservable<>(o);
    }

    public void forEach(final Action1<? super T> onNext) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> exceptionFromOnError = new AtomicReference<>();
        Subscription subscription = this.f1636o.subscribe((Subscriber<? super Object>) new Subscriber<T>() { // from class: rx.observables.BlockingObservable.1
            @Override // p045rx.Observer
            public void onCompleted() {
                latch.countDown();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                exceptionFromOnError.set(e);
                latch.countDown();
            }

            @Override // p045rx.Observer
            public void onNext(T args) {
                onNext.call(args);
            }
        });
        BlockingUtils.awaitForComplete(latch, subscription);
        if (exceptionFromOnError.get() != null) {
            Exceptions.propagate(exceptionFromOnError.get());
        }
    }

    public Iterator<T> getIterator() {
        return BlockingOperatorToIterator.toIterator(this.f1636o);
    }

    public T first() {
        return blockForSingle(this.f1636o.first());
    }

    public T first(Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f1636o.first(predicate));
    }

    public T firstOrDefault(T defaultValue) {
        return blockForSingle(this.f1636o.map(UtilityFunctions.identity()).firstOrDefault(defaultValue));
    }

    public T firstOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f1636o.filter(predicate).map(UtilityFunctions.identity()).firstOrDefault(defaultValue));
    }

    public T last() {
        return blockForSingle(this.f1636o.last());
    }

    public T last(Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f1636o.last(predicate));
    }

    public T lastOrDefault(T defaultValue) {
        return blockForSingle(this.f1636o.map(UtilityFunctions.identity()).lastOrDefault(defaultValue));
    }

    public T lastOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f1636o.filter(predicate).map(UtilityFunctions.identity()).lastOrDefault(defaultValue));
    }

    public Iterable<T> mostRecent(T initialValue) {
        return BlockingOperatorMostRecent.mostRecent(this.f1636o, initialValue);
    }

    public Iterable<T> next() {
        return BlockingOperatorNext.next(this.f1636o);
    }

    public Iterable<T> latest() {
        return BlockingOperatorLatest.latest(this.f1636o);
    }

    public T single() {
        return blockForSingle(this.f1636o.single());
    }

    public T single(Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f1636o.single(predicate));
    }

    public T singleOrDefault(T defaultValue) {
        return blockForSingle(this.f1636o.map(UtilityFunctions.identity()).singleOrDefault(defaultValue));
    }

    public T singleOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f1636o.filter(predicate).map(UtilityFunctions.identity()).singleOrDefault(defaultValue));
    }

    public Future<T> toFuture() {
        return BlockingOperatorToFuture.toFuture(this.f1636o);
    }

    public Iterable<T> toIterable() {
        return new Iterable<T>() { // from class: rx.observables.BlockingObservable.2
            @Override // java.lang.Iterable
            public Iterator<T> iterator() {
                return BlockingObservable.this.getIterator();
            }
        };
    }

    private T blockForSingle(Observable<? extends T> observable) {
        final AtomicReference<T> returnItem = new AtomicReference<>();
        final AtomicReference<Throwable> returnException = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        Subscription subscription = observable.subscribe((Subscriber<? super Object>) new Subscriber<T>() { // from class: rx.observables.BlockingObservable.3
            @Override // p045rx.Observer
            public void onCompleted() {
                latch.countDown();
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                returnException.set(e);
                latch.countDown();
            }

            @Override // p045rx.Observer
            public void onNext(T item) {
                returnItem.set(item);
            }
        });
        BlockingUtils.awaitForComplete(latch, subscription);
        if (returnException.get() != null) {
            Exceptions.propagate(returnException.get());
        }
        return returnItem.get();
    }

    @Experimental
    public void subscribe() {
        final CountDownLatch cdl = new CountDownLatch(1);
        final Throwable[] error = {null};
        Subscription s = this.f1636o.subscribe((Subscriber<? super Object>) new Subscriber<T>() { // from class: rx.observables.BlockingObservable.4
            @Override // p045rx.Observer
            public void onNext(T t) {
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                error[0] = e;
                cdl.countDown();
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                cdl.countDown();
            }
        });
        BlockingUtils.awaitForComplete(cdl, s);
        Throwable e = error[0];
        if (e != null) {
            Exceptions.propagate(e);
        }
    }

    @Experimental
    public void subscribe(Observer<? super T> observer) {
        Object o;
        final NotificationLite<T> nl = NotificationLite.instance();
        final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
        Subscription s = this.f1636o.subscribe((Subscriber<? super Object>) new Subscriber<T>() { // from class: rx.observables.BlockingObservable.5
            @Override // p045rx.Observer
            public void onNext(T t) {
                queue.offer(nl.next(t));
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                queue.offer(nl.error(e));
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                queue.offer(nl.completed());
            }
        });
        do {
            try {
                o = queue.poll();
                if (o == null) {
                    o = queue.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                observer.onError(e);
                return;
            } finally {
                s.unsubscribe();
            }
        } while (!nl.accept(observer, o));
    }

    @Experimental
    public void subscribe(Subscriber<? super T> subscriber) {
        final NotificationLite<T> nl = NotificationLite.instance();
        final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
        final Producer[] theProducer = {null};
        Subscriber<T> s = new Subscriber<T>() { // from class: rx.observables.BlockingObservable.6
            @Override // p045rx.Observer
            public void onNext(T t) {
                queue.offer(nl.next(t));
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                queue.offer(nl.error(e));
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                queue.offer(nl.completed());
            }

            @Override // p045rx.Subscriber
            public void setProducer(Producer p) {
                theProducer[0] = p;
                queue.offer(BlockingObservable.SET_PRODUCER);
            }

            @Override // p045rx.Subscriber
            public void onStart() {
                queue.offer(BlockingObservable.ON_START);
            }
        };
        subscriber.add(s);
        subscriber.add(Subscriptions.create(new Action0() { // from class: rx.observables.BlockingObservable.7
            @Override // p045rx.functions.Action0
            public void call() {
                queue.offer(BlockingObservable.UNSUBSCRIBE);
            }
        }));
        this.f1636o.subscribe((Subscriber<? super Object>) s);
        while (!subscriber.isUnsubscribed()) {
            try {
                Object o = queue.poll();
                if (o == null) {
                    o = queue.take();
                }
                if (subscriber.isUnsubscribed() || o == UNSUBSCRIBE) {
                    break;
                }
                if (o == ON_START) {
                    subscriber.onStart();
                } else if (o == SET_PRODUCER) {
                    subscriber.setProducer(theProducer[0]);
                } else if (nl.accept(subscriber, o)) {
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                subscriber.onError(e);
                return;
            } finally {
                s.unsubscribe();
            }
        }
    }

    @Experimental
    public void subscribe(Action1<? super T> onNext) {
        subscribe(onNext, new Action1<Throwable>() { // from class: rx.observables.BlockingObservable.8
            @Override // p045rx.functions.Action1
            public void call(Throwable t) {
                throw new OnErrorNotImplementedException(t);
            }
        }, Actions.empty());
    }

    @Experimental
    public void subscribe(Action1<? super T> onNext, Action1<? super Throwable> onError) {
        subscribe(onNext, onError, Actions.empty());
    }

    @Experimental
    public void subscribe(final Action1<? super T> onNext, final Action1<? super Throwable> onError, final Action0 onCompleted) {
        subscribe(new Observer<T>() { // from class: rx.observables.BlockingObservable.9
            @Override // p045rx.Observer
            public void onNext(T t) {
                onNext.call(t);
            }

            @Override // p045rx.Observer
            public void onError(Throwable e) {
                onError.call(e);
            }

            @Override // p045rx.Observer
            public void onCompleted() {
                onCompleted.call();
            }
        });
    }
}
