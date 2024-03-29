package p045rx.subjects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.Observable;
import p045rx.Observer;
import p045rx.Producer;
import p045rx.Scheduler;
import p045rx.Subscriber;
import p045rx.Subscription;
import p045rx.annotations.Beta;
import p045rx.exceptions.Exceptions;
import p045rx.internal.operators.BackpressureUtils;
import p045rx.plugins.RxJavaHooks;
import p045rx.schedulers.Schedulers;

/* loaded from: classes.dex */
public final class ReplaySubject<T> extends Subject<T, T> {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    final ReplayState<T> state;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ReplayBuffer<T> {
        void complete();

        void drain(ReplayProducer<T> replayProducer);

        Throwable error();

        void error(Throwable th);

        boolean isComplete();

        boolean isEmpty();

        T last();

        void next(T t);

        int size();

        T[] toArray(T[] tArr);
    }

    public static <T> ReplaySubject<T> create() {
        return create(16);
    }

    public static <T> ReplaySubject<T> create(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity > 0 required but it was " + capacity);
        }
        ReplayBuffer<T> buffer = new ReplayUnboundedBuffer<>(capacity);
        ReplayState<T> state = new ReplayState<>(buffer);
        return new ReplaySubject<>(state);
    }

    static <T> ReplaySubject<T> createUnbounded() {
        ReplayBuffer<T> buffer = new ReplaySizeBoundBuffer<>(Integer.MAX_VALUE);
        ReplayState<T> state = new ReplayState<>(buffer);
        return new ReplaySubject<>(state);
    }

    static <T> ReplaySubject<T> createUnboundedTime() {
        ReplayBuffer<T> buffer = new ReplaySizeAndTimeBoundBuffer<>(Integer.MAX_VALUE, Long.MAX_VALUE, Schedulers.immediate());
        ReplayState<T> state = new ReplayState<>(buffer);
        return new ReplaySubject<>(state);
    }

    public static <T> ReplaySubject<T> createWithSize(int size) {
        ReplayBuffer<T> buffer = new ReplaySizeBoundBuffer<>(size);
        ReplayState<T> state = new ReplayState<>(buffer);
        return new ReplaySubject<>(state);
    }

    public static <T> ReplaySubject<T> createWithTime(long time, TimeUnit unit, Scheduler scheduler) {
        return createWithTimeAndSize(time, unit, Integer.MAX_VALUE, scheduler);
    }

    public static <T> ReplaySubject<T> createWithTimeAndSize(long time, TimeUnit unit, int size, Scheduler scheduler) {
        ReplayBuffer<T> buffer = new ReplaySizeAndTimeBoundBuffer<>(size, unit.toMillis(time), scheduler);
        ReplayState<T> state = new ReplayState<>(buffer);
        return new ReplaySubject<>(state);
    }

    ReplaySubject(ReplayState<T> state) {
        super(state);
        this.state = state;
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.state.onNext(t);
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.state.onError(e);
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.state.onCompleted();
    }

    int subscriberCount() {
        return this.state.get().length;
    }

    @Override // p045rx.subjects.Subject
    public boolean hasObservers() {
        return this.state.get().length != 0;
    }

    @Beta
    public boolean hasThrowable() {
        return this.state.isTerminated() && this.state.buffer.error() != null;
    }

    @Beta
    public boolean hasCompleted() {
        return this.state.isTerminated() && this.state.buffer.error() == null;
    }

    @Beta
    public Throwable getThrowable() {
        if (this.state.isTerminated()) {
            return this.state.buffer.error();
        }
        return null;
    }

    @Beta
    public int size() {
        return this.state.buffer.size();
    }

    @Beta
    public boolean hasAnyValue() {
        return !this.state.buffer.isEmpty();
    }

    @Beta
    public boolean hasValue() {
        return hasAnyValue();
    }

    @Beta
    public T[] getValues(T[] a) {
        return this.state.buffer.toArray(a);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Beta
    public Object[] getValues() {
        T[] r = getValues(EMPTY_ARRAY);
        if (r == EMPTY_ARRAY) {
            return new Object[0];
        }
        return r;
    }

    @Beta
    public T getValue() {
        return this.state.buffer.last();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplayState<T> extends AtomicReference<ReplayProducer<T>[]> implements Observable.OnSubscribe<T>, Observer<T> {
        static final ReplayProducer[] EMPTY = new ReplayProducer[0];
        static final ReplayProducer[] TERMINATED = new ReplayProducer[0];
        private static final long serialVersionUID = 5952362471246910544L;
        final ReplayBuffer<T> buffer;

        @Override // p045rx.functions.Action1
        public /* bridge */ /* synthetic */ void call(Object x0) {
            call((Subscriber) ((Subscriber) x0));
        }

        public ReplayState(ReplayBuffer<T> buffer) {
            this.buffer = buffer;
            lazySet(EMPTY);
        }

        public void call(Subscriber<? super T> t) {
            ReplayProducer<T> rp = new ReplayProducer<>(t, this);
            t.add(rp);
            t.setProducer(rp);
            if (add(rp) && rp.isUnsubscribed()) {
                remove(rp);
            } else {
                this.buffer.drain(rp);
            }
        }

        boolean add(ReplayProducer<T> rp) {
            ReplayProducer<T>[] a;
            ReplayProducer<T>[] b;
            do {
                a = get();
                if (a == TERMINATED) {
                    return false;
                }
                int n = a.length;
                b = new ReplayProducer[n + 1];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = rp;
            } while (!compareAndSet(a, b));
            return true;
        }

        void remove(ReplayProducer<T> rp) {
            ReplayProducer<T>[] a;
            ReplayProducer<T>[] b;
            do {
                a = get();
                if (a != TERMINATED && a != EMPTY) {
                    int n = a.length;
                    int j = -1;
                    int i = 0;
                    while (true) {
                        if (i >= n) {
                            break;
                        }
                        if (a[i] != rp) {
                            i++;
                        } else {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            b = new ReplayProducer[n - 1];
                            System.arraycopy(a, 0, b, 0, j);
                            System.arraycopy(a, j + 1, b, j, (n - j) - 1);
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } while (!compareAndSet(a, b));
        }

        @Override // p045rx.Observer
        public void onNext(T t) {
            ReplayBuffer<T> b = this.buffer;
            b.next(t);
            ReplayProducer<T>[] arr$ = get();
            for (ReplayProducer<T> rp : arr$) {
                b.drain(rp);
            }
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
            ReplayBuffer<T> b = this.buffer;
            b.error(e);
            List<Throwable> errors = null;
            ReplayProducer<T>[] arr$ = getAndSet(TERMINATED);
            for (ReplayProducer<T> rp : arr$) {
                try {
                    b.drain(rp);
                } catch (Throwable ex) {
                    if (errors == null) {
                        errors = new ArrayList<>();
                    }
                    errors.add(ex);
                }
            }
            Exceptions.throwIfAny(errors);
        }

        @Override // p045rx.Observer
        public void onCompleted() {
            ReplayBuffer<T> b = this.buffer;
            b.complete();
            ReplayProducer<T>[] arr$ = getAndSet(TERMINATED);
            for (ReplayProducer<T> rp : arr$) {
                b.drain(rp);
            }
        }

        boolean isTerminated() {
            return get() == TERMINATED;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplayUnboundedBuffer<T> implements ReplayBuffer<T> {
        final int capacity;
        volatile boolean done;
        Throwable error;
        final Object[] head;
        volatile int size;
        Object[] tail;
        int tailIndex;

        public ReplayUnboundedBuffer(int capacity) {
            this.capacity = capacity;
            Object[] objArr = new Object[capacity + 1];
            this.head = objArr;
            this.tail = objArr;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void next(T t) {
            if (!this.done) {
                int i = this.tailIndex;
                Object[] a = this.tail;
                if (i == a.length - 1) {
                    Object[] b = new Object[a.length];
                    b[0] = t;
                    this.tailIndex = 1;
                    a[i] = b;
                    this.tail = b;
                } else {
                    a[i] = t;
                    this.tailIndex = i + 1;
                }
                this.size++;
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void error(Throwable e) {
            if (this.done) {
                RxJavaHooks.onError(e);
            } else {
                this.error = e;
                this.done = true;
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void complete() {
            this.done = true;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void drain(ReplayProducer<T> rp) {
            if (rp.getAndIncrement() == 0) {
                int missed = 1;
                Subscriber<? super T> a = rp.actual;
                int n = this.capacity;
                do {
                    long r = rp.requested.get();
                    long e = 0;
                    Object[] node = (Object[]) rp.node;
                    if (node == null) {
                        node = this.head;
                    }
                    int tailIndex = rp.tailIndex;
                    int index = rp.index;
                    while (e != r) {
                        if (a.isUnsubscribed()) {
                            rp.node = null;
                            return;
                        }
                        boolean d = this.done;
                        boolean empty = index == this.size;
                        if (d && empty) {
                            rp.node = null;
                            Throwable ex = this.error;
                            if (ex != null) {
                                a.onError(ex);
                                return;
                            } else {
                                a.onCompleted();
                                return;
                            }
                        }
                        if (empty) {
                            break;
                        }
                        if (tailIndex == n) {
                            node = (Object[]) node[tailIndex];
                            tailIndex = 0;
                        }
                        a.onNext(node[tailIndex]);
                        e++;
                        tailIndex++;
                        index++;
                    }
                    if (e == r) {
                        if (a.isUnsubscribed()) {
                            rp.node = null;
                            return;
                        }
                        boolean d2 = this.done;
                        boolean empty2 = index == this.size;
                        if (d2 && empty2) {
                            rp.node = null;
                            Throwable ex2 = this.error;
                            if (ex2 != null) {
                                a.onError(ex2);
                                return;
                            } else {
                                a.onCompleted();
                                return;
                            }
                        }
                    }
                    if (e != 0 && r != Long.MAX_VALUE) {
                        BackpressureUtils.produced(rp.requested, e);
                    }
                    rp.index = index;
                    rp.tailIndex = tailIndex;
                    rp.node = node;
                    missed = rp.addAndGet(-missed);
                } while (missed != 0);
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public boolean isComplete() {
            return this.done;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public Throwable error() {
            return this.error;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public T last() {
            int s = this.size;
            if (s == 0) {
                return null;
            }
            Object[] h = this.head;
            int n = this.capacity;
            while (s >= n) {
                h = (Object[]) h[n];
                s -= n;
            }
            return (T) h[s - 1];
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public int size() {
            return this.size;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public boolean isEmpty() {
            return this.size == 0;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public T[] toArray(T[] a) {
            int s = this.size;
            if (a.length < s) {
                a = (T[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), s));
            }
            Object[] h = this.head;
            int n = this.capacity;
            int j = 0;
            while (j + n < s) {
                System.arraycopy(h, 0, a, j, n);
                j += n;
                h = (Object[]) h[n];
            }
            System.arraycopy(h, 0, a, j, s - j);
            if (a.length > s) {
                a[s] = null;
            }
            return a;
        }
    }

    /* loaded from: classes.dex */
    static final class ReplaySizeBoundBuffer<T> implements ReplayBuffer<T> {
        volatile boolean done;
        Throwable error;
        volatile Node<T> head;
        final int limit;
        int size;
        Node<T> tail;

        public ReplaySizeBoundBuffer(int limit) {
            this.limit = limit;
            Node<T> n = new Node<>(null);
            this.tail = n;
            this.head = n;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void next(T value) {
            Node<T> n = new Node<>(value);
            this.tail.set(n);
            this.tail = n;
            int s = this.size;
            if (s == this.limit) {
                this.head = this.head.get();
            } else {
                this.size = s + 1;
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void error(Throwable ex) {
            this.error = ex;
            this.done = true;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void complete() {
            this.done = true;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void drain(ReplayProducer<T> rp) {
            if (rp.getAndIncrement() == 0) {
                Subscriber<? super T> a = rp.actual;
                int missed = 1;
                do {
                    long r = rp.requested.get();
                    long e = 0;
                    Node<T> node = (Node) rp.node;
                    if (node == null) {
                        node = this.head;
                    }
                    while (e != r) {
                        if (a.isUnsubscribed()) {
                            rp.node = null;
                            return;
                        }
                        boolean d = this.done;
                        Node<T> next = node.get();
                        boolean empty = next == null;
                        if (d && empty) {
                            rp.node = null;
                            Throwable ex = this.error;
                            if (ex != null) {
                                a.onError(ex);
                                return;
                            } else {
                                a.onCompleted();
                                return;
                            }
                        }
                        if (empty) {
                            break;
                        }
                        a.onNext((T) next.value);
                        e++;
                        node = next;
                    }
                    if (e == r) {
                        if (a.isUnsubscribed()) {
                            rp.node = null;
                            return;
                        }
                        boolean d2 = this.done;
                        boolean empty2 = node.get() == null;
                        if (d2 && empty2) {
                            rp.node = null;
                            Throwable ex2 = this.error;
                            if (ex2 != null) {
                                a.onError(ex2);
                                return;
                            } else {
                                a.onCompleted();
                                return;
                            }
                        }
                    }
                    if (e != 0 && r != Long.MAX_VALUE) {
                        BackpressureUtils.produced(rp.requested, e);
                    }
                    rp.node = node;
                    missed = rp.addAndGet(-missed);
                } while (missed != 0);
            }
        }

        /* loaded from: classes.dex */
        static final class Node<T> extends AtomicReference<Node<T>> {
            private static final long serialVersionUID = 3713592843205853725L;
            final T value;

            public Node(T value) {
                this.value = value;
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public boolean isComplete() {
            return this.done;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public Throwable error() {
            return this.error;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public T last() {
            Node<T> h = this.head;
            while (true) {
                Node<T> n = h.get();
                if (n != null) {
                    h = n;
                } else {
                    return h.value;
                }
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public int size() {
            int s = 0;
            Node<T> n = this.head.get();
            while (n != null && s != Integer.MAX_VALUE) {
                n = n.get();
                s++;
            }
            return s;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public boolean isEmpty() {
            return this.head.get() == null;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public T[] toArray(T[] a) {
            List<T> list = new ArrayList<>();
            for (Node<T> n = this.head.get(); n != null; n = n.get()) {
                list.add(n.value);
            }
            return (T[]) list.toArray(a);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplaySizeAndTimeBoundBuffer<T> implements ReplayBuffer<T> {
        volatile boolean done;
        Throwable error;
        volatile TimedNode<T> head;
        final int limit;
        final long maxAgeMillis;
        final Scheduler scheduler;
        int size;
        TimedNode<T> tail;

        public ReplaySizeAndTimeBoundBuffer(int limit, long maxAgeMillis, Scheduler scheduler) {
            this.limit = limit;
            TimedNode<T> n = new TimedNode<>(null, 0L);
            this.tail = n;
            this.head = n;
            this.maxAgeMillis = maxAgeMillis;
            this.scheduler = scheduler;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void next(T value) {
            long now = this.scheduler.now();
            TimedNode<T> n = new TimedNode<>(value, now);
            this.tail.set(n);
            this.tail = n;
            long now2 = now - this.maxAgeMillis;
            int s = this.size;
            TimedNode<T> h0 = this.head;
            TimedNode<T> h = h0;
            if (s == this.limit) {
                h = h.get();
            } else {
                s++;
            }
            while (true) {
                TimedNode<T> n2 = h.get();
                if (n2 == null || n2.timestamp > now2) {
                    break;
                }
                h = n2;
                s--;
            }
            this.size = s;
            if (h != h0) {
                this.head = h;
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void error(Throwable ex) {
            evictFinal();
            this.error = ex;
            this.done = true;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void complete() {
            evictFinal();
            this.done = true;
        }

        void evictFinal() {
            long now = this.scheduler.now() - this.maxAgeMillis;
            TimedNode<T> h0 = this.head;
            TimedNode<T> h = h0;
            while (true) {
                TimedNode<T> n = h.get();
                if (n == null || n.timestamp > now) {
                    break;
                } else {
                    h = n;
                }
            }
            if (h0 != h) {
                this.head = h;
            }
        }

        TimedNode<T> latestHead() {
            long now = this.scheduler.now() - this.maxAgeMillis;
            TimedNode<T> h = this.head;
            while (true) {
                TimedNode<T> n = h.get();
                if (n == null || n.timestamp > now) {
                    break;
                }
                h = n;
            }
            return h;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public void drain(ReplayProducer<T> rp) {
            if (rp.getAndIncrement() == 0) {
                Subscriber<? super T> a = rp.actual;
                int missed = 1;
                do {
                    long r = rp.requested.get();
                    long e = 0;
                    TimedNode<T> node = (TimedNode) rp.node;
                    if (node == null) {
                        node = latestHead();
                    }
                    while (e != r) {
                        if (a.isUnsubscribed()) {
                            rp.node = null;
                            return;
                        }
                        boolean d = this.done;
                        TimedNode<T> next = node.get();
                        boolean empty = next == null;
                        if (d && empty) {
                            rp.node = null;
                            Throwable ex = this.error;
                            if (ex != null) {
                                a.onError(ex);
                                return;
                            } else {
                                a.onCompleted();
                                return;
                            }
                        }
                        if (empty) {
                            break;
                        }
                        a.onNext((T) next.value);
                        e++;
                        node = next;
                    }
                    if (e == r) {
                        if (a.isUnsubscribed()) {
                            rp.node = null;
                            return;
                        }
                        boolean d2 = this.done;
                        boolean empty2 = node.get() == null;
                        if (d2 && empty2) {
                            rp.node = null;
                            Throwable ex2 = this.error;
                            if (ex2 != null) {
                                a.onError(ex2);
                                return;
                            } else {
                                a.onCompleted();
                                return;
                            }
                        }
                    }
                    if (e != 0 && r != Long.MAX_VALUE) {
                        BackpressureUtils.produced(rp.requested, e);
                    }
                    rp.node = node;
                    missed = rp.addAndGet(-missed);
                } while (missed != 0);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static final class TimedNode<T> extends AtomicReference<TimedNode<T>> {
            private static final long serialVersionUID = 3713592843205853725L;
            final long timestamp;
            final T value;

            public TimedNode(T value, long timestamp) {
                this.value = value;
                this.timestamp = timestamp;
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public boolean isComplete() {
            return this.done;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public Throwable error() {
            return this.error;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public T last() {
            TimedNode<T> h = latestHead();
            while (true) {
                TimedNode<T> n = h.get();
                if (n != null) {
                    h = n;
                } else {
                    return h.value;
                }
            }
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public int size() {
            int s = 0;
            TimedNode<T> n = latestHead().get();
            while (n != null && s != Integer.MAX_VALUE) {
                n = n.get();
                s++;
            }
            return s;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public boolean isEmpty() {
            return latestHead().get() == null;
        }

        @Override // rx.subjects.ReplaySubject.ReplayBuffer
        public T[] toArray(T[] a) {
            List<T> list = new ArrayList<>();
            for (TimedNode<T> n = latestHead().get(); n != null; n = n.get()) {
                list.add(n.value);
            }
            return (T[]) list.toArray(a);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ReplayProducer<T> extends AtomicInteger implements Producer, Subscription {
        private static final long serialVersionUID = -5006209596735204567L;
        final Subscriber<? super T> actual;
        int index;
        Object node;
        final AtomicLong requested = new AtomicLong();
        final ReplayState<T> state;
        int tailIndex;

        public ReplayProducer(Subscriber<? super T> actual, ReplayState<T> state) {
            this.actual = actual;
            this.state = state;
        }

        @Override // p045rx.Subscription
        public void unsubscribe() {
            this.state.remove(this);
        }

        @Override // p045rx.Subscription
        public boolean isUnsubscribed() {
            return this.actual.isUnsubscribed();
        }

        @Override // p045rx.Producer
        public void request(long n) {
            if (n > 0) {
                BackpressureUtils.getAndAddRequest(this.requested, n);
                this.state.buffer.drain(this);
            } else if (n < 0) {
                throw new IllegalArgumentException("n >= required but it was " + n);
            }
        }
    }
}
