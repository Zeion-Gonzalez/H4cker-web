package p045rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Producer;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.exceptions.Exceptions;
import p045rx.exceptions.OnErrorThrowable;
import p045rx.functions.Action0;
import p045rx.functions.Action1;
import p045rx.functions.Func0;
import p045rx.functions.Func1;
import p045rx.internal.util.OpenHashSet;
import p045rx.observables.ConnectableObservable;
import p045rx.schedulers.Timestamped;
import p045rx.subscriptions.Subscriptions;

/* loaded from: classes.dex */
public final class OperatorReplay<T> extends ConnectableObservable<T> {
    static final Func0 DEFAULT_UNBOUNDED_FACTORY = new Func0() { // from class: rx.internal.operators.OperatorReplay.1
        @Override // p045rx.functions.Func0, java.util.concurrent.Callable
        public Object call() {
            return new UnboundedReplayBuffer(16);
        }
    };
    final Func0<? extends ReplayBuffer<T>> bufferFactory;
    final AtomicReference<ReplaySubscriber<T>> current;
    final Observable<? extends T> source;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        void next(T t);

        void replay(InnerProducer<T> innerProducer);
    }

    public static <T, U, R> Observable<R> multicastSelector(final Func0<? extends ConnectableObservable<U>> connectableFactory, final Func1<? super Observable<U>, ? extends Observable<R>> selector) {
        return Observable.create(new Observable.OnSubscribe<R>() { // from class: rx.internal.operators.OperatorReplay.2
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(final Subscriber<? super R> child) {
                try {
                    ConnectableObservable connectableObservable = (ConnectableObservable) Func0.this.call();
                    Observable<R> observable = (Observable) selector.call(connectableObservable);
                    observable.subscribe(child);
                    connectableObservable.connect(new Action1<Subscription>() { // from class: rx.internal.operators.OperatorReplay.2.1
                        @Override // p045rx.functions.Action1
                        public void call(Subscription t) {
                            child.add(t);
                        }
                    });
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, child);
                }
            }
        });
    }

    public static <T> ConnectableObservable<T> observeOn(final ConnectableObservable<T> co, Scheduler scheduler) {
        final Observable<T> observable = co.observeOn(scheduler);
        Observable.OnSubscribe<T> onSubscribe = new Observable.OnSubscribe<T>() { // from class: rx.internal.operators.OperatorReplay.3
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(final Subscriber<? super T> child) {
                Observable.this.unsafeSubscribe(new Subscriber<T>(child) { // from class: rx.internal.operators.OperatorReplay.3.1
                    @Override // p045rx.Observer
                    public void onNext(T t) {
                        child.onNext(t);
                    }

                    @Override // p045rx.Observer
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override // p045rx.Observer
                    public void onCompleted() {
                        child.onCompleted();
                    }
                });
            }
        };
        return new ConnectableObservable<T>(onSubscribe) { // from class: rx.internal.operators.OperatorReplay.4
            @Override // p045rx.observables.ConnectableObservable
            public void connect(Action1<? super Subscription> connection) {
                co.connect(connection);
            }
        };
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source) {
        return create(source, DEFAULT_UNBOUNDED_FACTORY);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, final int bufferSize) {
        return bufferSize == Integer.MAX_VALUE ? create(source) : create(source, new Func0<ReplayBuffer<T>>() { // from class: rx.internal.operators.OperatorReplay.5
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            public ReplayBuffer<T> call() {
                return new SizeBoundReplayBuffer(bufferSize);
            }
        });
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, long maxAge, TimeUnit unit, Scheduler scheduler) {
        return create(source, maxAge, unit, scheduler, Integer.MAX_VALUE);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, long maxAge, TimeUnit unit, final Scheduler scheduler, final int bufferSize) {
        final long maxAgeInMillis = unit.toMillis(maxAge);
        return create(source, new Func0<ReplayBuffer<T>>() { // from class: rx.internal.operators.OperatorReplay.6
            @Override // p045rx.functions.Func0, java.util.concurrent.Callable
            public ReplayBuffer<T> call() {
                return new SizeAndTimeBoundReplayBuffer(bufferSize, maxAgeInMillis, scheduler);
            }
        });
    }

    static <T> ConnectableObservable<T> create(Observable<? extends T> source, final Func0<? extends ReplayBuffer<T>> bufferFactory) {
        final AtomicReference<ReplaySubscriber<T>> curr = new AtomicReference<>();
        Observable.OnSubscribe<T> onSubscribe = new Observable.OnSubscribe<T>() { // from class: rx.internal.operators.OperatorReplay.7
            @Override // p045rx.functions.Action1
            public /* bridge */ /* synthetic */ void call(Object x0) {
                call((Subscriber) ((Subscriber) x0));
            }

            public void call(Subscriber<? super T> child) {
                ReplaySubscriber<T> r;
                while (true) {
                    r = (ReplaySubscriber) curr.get();
                    if (r != null) {
                        break;
                    }
                    ReplaySubscriber<T> u = new ReplaySubscriber<>((ReplayBuffer) bufferFactory.call());
                    u.init();
                    if (curr.compareAndSet(r, u)) {
                        r = u;
                        break;
                    }
                }
                InnerProducer<T> inner = new InnerProducer<>(r, child);
                r.add((InnerProducer) inner);
                child.add(inner);
                r.buffer.replay(inner);
                child.setProducer(inner);
            }
        };
        return new OperatorReplay(onSubscribe, source, curr, bufferFactory);
    }

    private OperatorReplay(Observable.OnSubscribe<T> onSubscribe, Observable<? extends T> source, AtomicReference<ReplaySubscriber<T>> current, Func0<? extends ReplayBuffer<T>> bufferFactory) {
        super(onSubscribe);
        this.source = source;
        this.current = current;
        this.bufferFactory = bufferFactory;
    }

    @Override // p045rx.observables.ConnectableObservable
    public void connect(Action1<? super Subscription> connection) {
        ReplaySubscriber<T> ps;
        while (true) {
            ps = this.current.get();
            if (ps != null && !ps.isUnsubscribed()) {
                break;
            }
            ReplaySubscriber<T> u = new ReplaySubscriber<>(this.bufferFactory.call());
            u.init();
            if (this.current.compareAndSet(ps, u)) {
                ps = u;
                break;
            }
        }
        boolean doConnect = !ps.shouldConnect.get() && ps.shouldConnect.compareAndSet(false, true);
        connection.call(ps);
        if (doConnect) {
            this.source.unsafeSubscribe(ps);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySubscriber<T> extends Subscriber<T> implements Subscription {
        static final InnerProducer[] EMPTY = new InnerProducer[0];
        static final InnerProducer[] TERMINATED = new InnerProducer[0];
        final ReplayBuffer<T> buffer;
        boolean coordinateAll;
        List<InnerProducer<T>> coordinationQueue;
        boolean done;
        boolean emitting;
        long maxChildRequested;
        long maxUpstreamRequested;
        boolean missed;
        volatile Producer producer;
        long producersCacheVersion;
        volatile long producersVersion;
        volatile boolean terminated;

        /* renamed from: nl */
        final NotificationLite<T> f1608nl = NotificationLite.instance();
        final OpenHashSet<InnerProducer<T>> producers = new OpenHashSet<>();
        InnerProducer<T>[] producersCache = EMPTY;
        final AtomicBoolean shouldConnect = new AtomicBoolean();

        public ReplaySubscriber(ReplayBuffer<T> buffer) {
            this.buffer = buffer;
            request(0L);
        }

        void init() {
            add(Subscriptions.create(new Action0() { // from class: rx.internal.operators.OperatorReplay.ReplaySubscriber.1
                @Override // p045rx.functions.Action0
                public void call() {
                    if (!ReplaySubscriber.this.terminated) {
                        synchronized (ReplaySubscriber.this.producers) {
                            if (!ReplaySubscriber.this.terminated) {
                                ReplaySubscriber.this.producers.terminate();
                                ReplaySubscriber.this.producersVersion++;
                                ReplaySubscriber.this.terminated = true;
                            }
                        }
                    }
                }
            }));
        }

        boolean add(InnerProducer<T> producer) {
            boolean z = false;
            if (producer == null) {
                throw new NullPointerException();
            }
            if (!this.terminated) {
                synchronized (this.producers) {
                    if (!this.terminated) {
                        this.producers.add(producer);
                        this.producersVersion++;
                        z = true;
                    }
                }
            }
            return z;
        }

        void remove(InnerProducer<T> producer) {
            if (!this.terminated) {
                synchronized (this.producers) {
                    if (!this.terminated) {
                        this.producers.remove(producer);
                        if (this.producers.isEmpty()) {
                            this.producersCache = EMPTY;
                        }
                        this.producersVersion++;
                    }
                }
            }
        }

        @Override // p045rx.Subscriber
        public void setProducer(Producer p) {
            Producer p0 = this.producer;
            if (p0 != null) {
                throw new IllegalStateException("Only a single producer can be set on a Subscriber.");
            }
            this.producer = p;
            manageRequests(null);
            replay();
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            if (!this.done) {
                this.buffer.next(t);
                replay();
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            if (!this.done) {
                this.done = true;
                try {
                    this.buffer.error(e);
                    replay();
                } finally {
                    unsubscribe();
                }
            }
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                try {
                    this.buffer.complete();
                    replay();
                } finally {
                    unsubscribe();
                }
            }
        }

        void manageRequests(InnerProducer<T> inner) {
            long maxTotalRequested;
            List<InnerProducer<T>> q;
            boolean all;
            if (!isUnsubscribed()) {
                synchronized (this) {
                    if (this.emitting) {
                        if (inner != null) {
                            List<InnerProducer<T>> q2 = this.coordinationQueue;
                            if (q2 == null) {
                                q2 = new ArrayList<>();
                                this.coordinationQueue = q2;
                            }
                            q2.add(inner);
                        } else {
                            this.coordinateAll = true;
                        }
                        this.missed = true;
                        return;
                    }
                    this.emitting = true;
                    long ri = this.maxChildRequested;
                    if (inner != null) {
                        maxTotalRequested = Math.max(ri, inner.totalRequested.get());
                    } else {
                        maxTotalRequested = ri;
                        InnerProducer<T>[] a = copyProducers();
                        for (InnerProducer<T> rp : a) {
                            if (rp != null) {
                                maxTotalRequested = Math.max(maxTotalRequested, rp.totalRequested.get());
                            }
                        }
                    }
                    makeRequest(maxTotalRequested, ri);
                    while (!isUnsubscribed()) {
                        synchronized (this) {
                            if (!this.missed) {
                                this.emitting = false;
                                return;
                            }
                            this.missed = false;
                            q = this.coordinationQueue;
                            this.coordinationQueue = null;
                            all = this.coordinateAll;
                            this.coordinateAll = false;
                        }
                        long ri2 = this.maxChildRequested;
                        long maxTotalRequested2 = ri2;
                        if (q != null) {
                            Iterator i$ = q.iterator();
                            while (i$.hasNext()) {
                                maxTotalRequested2 = Math.max(maxTotalRequested2, i$.next().totalRequested.get());
                            }
                        }
                        if (all) {
                            InnerProducer<T>[] a2 = copyProducers();
                            for (InnerProducer<T> rp2 : a2) {
                                if (rp2 != null) {
                                    maxTotalRequested2 = Math.max(maxTotalRequested2, rp2.totalRequested.get());
                                }
                            }
                        }
                        makeRequest(maxTotalRequested2, ri2);
                    }
                }
            }
        }

        InnerProducer<T>[] copyProducers() {
            InnerProducer<T>[] result;
            synchronized (this.producers) {
                Object[] a = this.producers.values();
                int n = a.length;
                result = new InnerProducer[n];
                System.arraycopy(a, 0, result, 0, n);
            }
            return result;
        }

        void makeRequest(long maxTotalRequests, long previousTotalRequests) {
            long ur = this.maxUpstreamRequested;
            Producer p = this.producer;
            long diff = maxTotalRequests - previousTotalRequests;
            if (diff == 0) {
                if (ur != 0 && p != null) {
                    this.maxUpstreamRequested = 0L;
                    p.request(ur);
                    return;
                }
                return;
            }
            this.maxChildRequested = maxTotalRequests;
            if (p == null) {
                long u = ur + diff;
                if (u < 0) {
                    u = Long.MAX_VALUE;
                }
                this.maxUpstreamRequested = u;
                return;
            }
            if (ur != 0) {
                this.maxUpstreamRequested = 0L;
                p.request(ur + diff);
            } else {
                p.request(diff);
            }
        }

        void replay() {
            InnerProducer<T>[] pc = this.producersCache;
            if (this.producersCacheVersion != this.producersVersion) {
                synchronized (this.producers) {
                    pc = this.producersCache;
                    Object[] a = this.producers.values();
                    int n = a.length;
                    if (pc.length != n) {
                        pc = new InnerProducer[n];
                        this.producersCache = pc;
                    }
                    System.arraycopy(a, 0, pc, 0, n);
                    this.producersCacheVersion = this.producersVersion;
                }
            }
            ReplayBuffer<T> b = this.buffer;
            InnerProducer<T>[] arr$ = pc;
            for (InnerProducer<T> rp : arr$) {
                if (rp != null) {
                    b.replay(rp);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class InnerProducer<T> extends AtomicLong implements Producer, Subscription {
        static final long UNSUBSCRIBED = Long.MIN_VALUE;
        private static final long serialVersionUID = -4453897557930727610L;
        Subscriber<? super T> child;
        boolean emitting;
        Object index;
        boolean missed;
        final ReplaySubscriber<T> parent;
        final AtomicLong totalRequested = new AtomicLong();

        public InnerProducer(ReplaySubscriber<T> parent, Subscriber<? super T> child) {
            this.parent = parent;
            this.child = child;
        }

        @Override // p045rx.Producer
        public void request(long n) {
            long r;
            long u;
            if (n < 0) {
                return;
            }
            do {
                r = get();
                if (r == UNSUBSCRIBED) {
                    return;
                }
                if (r < 0 || n != 0) {
                    u = r + n;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                } else {
                    return;
                }
            } while (!compareAndSet(r, u));
            addTotalRequested(n);
            this.parent.manageRequests(this);
            this.parent.buffer.replay(this);
        }

        void addTotalRequested(long n) {
            long r;
            long u;
            do {
                r = this.totalRequested.get();
                u = r + n;
                if (u < 0) {
                    u = Long.MAX_VALUE;
                }
            } while (!this.totalRequested.compareAndSet(r, u));
        }

        public long produced(long n) {
            long r;
            long u;
            if (n <= 0) {
                throw new IllegalArgumentException("Cant produce zero or less");
            }
            do {
                r = get();
                if (r == UNSUBSCRIBED) {
                    return UNSUBSCRIBED;
                }
                u = r - n;
                if (u < 0) {
                    throw new IllegalStateException("More produced (" + n + ") than requested (" + r + ")");
                }
            } while (!compareAndSet(r, u));
            return u;
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return get() == UNSUBSCRIBED;
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            long r = get();
            if (r != UNSUBSCRIBED) {
                long r2 = getAndSet(UNSUBSCRIBED);
                if (r2 != UNSUBSCRIBED) {
                    this.parent.remove(this);
                    this.parent.manageRequests(this);
                    this.child = null;
                }
            }
        }

        <U> U index() {
            return (U) this.index;
        }
    }

    /* loaded from: classes.dex */
    static final class UnboundedReplayBuffer<T> extends ArrayList<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 7063189396499112664L;

        /* renamed from: nl */
        final NotificationLite<T> f1609nl;
        volatile int size;

        public UnboundedReplayBuffer(int capacityHint) {
            super(capacityHint);
            this.f1609nl = NotificationLite.instance();
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public void next(T value) {
            add(this.f1609nl.next(value));
            this.size++;
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public void error(Throwable e) {
            add(this.f1609nl.error(e));
            this.size++;
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public void complete() {
            add(this.f1609nl.completed());
            this.size++;
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public void replay(InnerProducer<T> output) {
            synchronized (output) {
                if (output.emitting) {
                    output.missed = true;
                    return;
                }
                output.emitting = true;
                while (!output.isUnsubscribed()) {
                    int sourceIndex = this.size;
                    Integer destIndexObject = (Integer) output.index();
                    int destIndex = destIndexObject != null ? destIndexObject.intValue() : 0;
                    Subscriber<? super T> child = output.child;
                    if (child != null) {
                        long r = output.get();
                        long e = 0;
                        while (e != r && destIndex < sourceIndex) {
                            Object o = get(destIndex);
                            try {
                                if (!this.f1609nl.accept(child, o) && !output.isUnsubscribed()) {
                                    destIndex++;
                                    e++;
                                } else {
                                    return;
                                }
                            } catch (Throwable err) {
                                Exceptions.throwIfFatal(err);
                                output.unsubscribe();
                                if (!this.f1609nl.isError(o) && !this.f1609nl.isCompleted(o)) {
                                    child.onError(OnErrorThrowable.addValueAsLastCause(err, this.f1609nl.getValue(o)));
                                    return;
                                }
                                return;
                            }
                        }
                        if (e != 0) {
                            output.index = Integer.valueOf(destIndex);
                            if (r != Long.MAX_VALUE) {
                                output.produced(e);
                            }
                        }
                        synchronized (output) {
                            if (!output.missed) {
                                output.emitting = false;
                                return;
                            }
                            output.missed = false;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Node extends AtomicReference<Node> {
        private static final long serialVersionUID = 245354315435971818L;
        final long index;
        final Object value;

        public Node(Object value, long index) {
            this.value = value;
            this.index = index;
        }
    }

    /* loaded from: classes.dex */
    static class BoundedReplayBuffer<T> extends AtomicReference<Node> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 2346567790059478686L;
        long index;

        /* renamed from: nl */
        final NotificationLite<T> f1607nl = NotificationLite.instance();
        int size;
        Node tail;

        public BoundedReplayBuffer() {
            Node n = new Node(null, 0L);
            this.tail = n;
            set(n);
        }

        final void addLast(Node n) {
            this.tail.set(n);
            this.tail = n;
            this.size++;
        }

        final void removeFirst() {
            Node head = get();
            Node next = head.get();
            if (next == null) {
                throw new IllegalStateException("Empty list!");
            }
            this.size--;
            setFirst(next);
        }

        final void removeSome(int n) {
            Node head = get();
            while (n > 0) {
                head = head.get();
                n--;
                this.size--;
            }
            setFirst(head);
        }

        final void setFirst(Node n) {
            set(n);
        }

        Node getInitialHead() {
            return get();
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public final void next(T value) {
            Object o = enterTransform(this.f1607nl.next(value));
            long j = this.index + 1;
            this.index = j;
            Node n = new Node(o, j);
            addLast(n);
            truncate();
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public final void error(Throwable e) {
            Object o = enterTransform(this.f1607nl.error(e));
            long j = this.index + 1;
            this.index = j;
            Node n = new Node(o, j);
            addLast(n);
            truncateFinal();
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public final void complete() {
            Object o = enterTransform(this.f1607nl.completed());
            long j = this.index + 1;
            this.index = j;
            Node n = new Node(o, j);
            addLast(n);
            truncateFinal();
        }

        @Override // rx.internal.operators.OperatorReplay.ReplayBuffer
        public final void replay(InnerProducer<T> output) {
            Subscriber<? super T> child;
            Node v;
            synchronized (output) {
                if (output.emitting) {
                    output.missed = true;
                    return;
                }
                output.emitting = true;
                while (!output.isUnsubscribed()) {
                    Node node = (Node) output.index();
                    if (node == null) {
                        node = getInitialHead();
                        output.index = node;
                        output.addTotalRequested(node.index);
                    }
                    if (!output.isUnsubscribed() && (child = output.child) != null) {
                        long r = output.get();
                        long e = 0;
                        while (e != r && (v = node.get()) != null) {
                            Object o = leaveTransform(v.value);
                            try {
                                if (this.f1607nl.accept(child, o)) {
                                    output.index = null;
                                    return;
                                }
                                e++;
                                node = v;
                                if (output.isUnsubscribed()) {
                                    return;
                                }
                            } catch (Throwable err) {
                                output.index = null;
                                Exceptions.throwIfFatal(err);
                                output.unsubscribe();
                                if (!this.f1607nl.isError(o) && !this.f1607nl.isCompleted(o)) {
                                    child.onError(OnErrorThrowable.addValueAsLastCause(err, this.f1607nl.getValue(o)));
                                    return;
                                }
                                return;
                            }
                        }
                        if (e != 0) {
                            output.index = node;
                            if (r != Long.MAX_VALUE) {
                                output.produced(e);
                            }
                        }
                        synchronized (output) {
                            if (!output.missed) {
                                output.emitting = false;
                                return;
                            }
                            output.missed = false;
                        }
                    } else {
                        return;
                    }
                }
            }
        }

        Object enterTransform(Object value) {
            return value;
        }

        Object leaveTransform(Object value) {
            return value;
        }

        void truncate() {
        }

        void truncateFinal() {
        }

        final void collect(Collection<? super T> output) {
            Node n = getInitialHead();
            while (true) {
                Node next = n.get();
                if (next != null) {
                    Object o = next.value;
                    Object v = leaveTransform(o);
                    if (!this.f1607nl.isCompleted(v) && !this.f1607nl.isError(v)) {
                        output.add((T) this.f1607nl.getValue(v));
                        n = next;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        boolean hasError() {
            return this.tail.value != null && this.f1607nl.isError(leaveTransform(this.tail.value));
        }

        boolean hasCompleted() {
            return this.tail.value != null && this.f1607nl.isCompleted(leaveTransform(this.tail.value));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SizeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = -5898283885385201806L;
        final int limit;

        public SizeBoundReplayBuffer(int limit) {
            this.limit = limit;
        }

        @Override // rx.internal.operators.OperatorReplay.BoundedReplayBuffer
        void truncate() {
            if (this.size > this.limit) {
                removeFirst();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class SizeAndTimeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = 3457957419649567404L;
        final int limit;
        final long maxAgeInMillis;
        final Scheduler scheduler;

        public SizeAndTimeBoundReplayBuffer(int limit, long maxAgeInMillis, Scheduler scheduler) {
            this.scheduler = scheduler;
            this.limit = limit;
            this.maxAgeInMillis = maxAgeInMillis;
        }

        @Override // rx.internal.operators.OperatorReplay.BoundedReplayBuffer
        Object enterTransform(Object value) {
            return new Timestamped(this.scheduler.now(), value);
        }

        @Override // rx.internal.operators.OperatorReplay.BoundedReplayBuffer
        Object leaveTransform(Object value) {
            return ((Timestamped) value).getValue();
        }

        @Override // rx.internal.operators.OperatorReplay.BoundedReplayBuffer
        Node getInitialHead() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            for (Node next = prev.get(); next != null && ((Timestamped) next.value).getTimestampMillis() <= timeLimit; next = next.get()) {
                prev = next;
            }
            return prev;
        }

        @Override // rx.internal.operators.OperatorReplay.BoundedReplayBuffer
        void truncate() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            Node next = prev.get();
            int e = 0;
            while (next != null) {
                if (this.size > this.limit) {
                    e++;
                    this.size--;
                    prev = next;
                    next = next.get();
                } else {
                    Timestamped<?> v = (Timestamped) next.value;
                    if (v.getTimestampMillis() > timeLimit) {
                        break;
                    }
                    e++;
                    this.size--;
                    prev = next;
                    next = next.get();
                }
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        @Override // rx.internal.operators.OperatorReplay.BoundedReplayBuffer
        void truncateFinal() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            int e = 0;
            for (Node next = prev.get(); next != null && this.size > 1; next = next.get()) {
                Timestamped<?> v = (Timestamped) next.value;
                if (v.getTimestampMillis() > timeLimit) {
                    break;
                }
                e++;
                this.size--;
                prev = next;
            }
            if (e != 0) {
                setFirst(prev);
            }
        }
    }
}
