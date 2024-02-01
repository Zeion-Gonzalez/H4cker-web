package p045rx.internal.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import p045rx.internal.schedulers.GenericScheduledExecutorService;
import p045rx.internal.schedulers.SchedulerLifecycle;
import p045rx.internal.util.unsafe.MpmcArrayQueue;
import p045rx.internal.util.unsafe.UnsafeAccess;
import p045rx.plugins.RxJavaHooks;

/* loaded from: classes.dex */
public abstract class ObjectPool<T> implements SchedulerLifecycle {
    final int maxSize;
    final int minSize;
    private final AtomicReference<Future<?>> periodicTask;
    Queue<T> pool;
    private final long validationInterval;

    protected abstract T createObject();

    public ObjectPool() {
        this(0, 0, 67L);
    }

    private ObjectPool(int min, int max, long validationInterval) {
        this.minSize = min;
        this.maxSize = max;
        this.validationInterval = validationInterval;
        this.periodicTask = new AtomicReference<>();
        initialize(min);
        start();
    }

    public T borrowObject() {
        T object = this.pool.poll();
        if (object == null) {
            return createObject();
        }
        return object;
    }

    public void returnObject(T object) {
        if (object != null) {
            this.pool.offer(object);
        }
    }

    @Override // p045rx.internal.schedulers.SchedulerLifecycle
    public void shutdown() {
        Future<?> f = this.periodicTask.getAndSet(null);
        if (f != null) {
            f.cancel(false);
        }
    }

    @Override // p045rx.internal.schedulers.SchedulerLifecycle
    public void start() {
        while (this.periodicTask.get() == null) {
            ScheduledExecutorService w = GenericScheduledExecutorService.getInstance();
            try {
                Future<?> f = w.scheduleAtFixedRate(new Runnable() { // from class: rx.internal.util.ObjectPool.1
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.lang.Runnable
                    public void run() {
                        int size = ObjectPool.this.pool.size();
                        if (size < ObjectPool.this.minSize) {
                            int sizeToBeAdded = ObjectPool.this.maxSize - size;
                            for (int i = 0; i < sizeToBeAdded; i++) {
                                ObjectPool.this.pool.add(ObjectPool.this.createObject());
                            }
                            return;
                        }
                        if (size > ObjectPool.this.maxSize) {
                            int sizeToBeRemoved = size - ObjectPool.this.maxSize;
                            for (int i2 = 0; i2 < sizeToBeRemoved; i2++) {
                                ObjectPool.this.pool.poll();
                            }
                        }
                    }
                }, this.validationInterval, this.validationInterval, TimeUnit.SECONDS);
                if (!this.periodicTask.compareAndSet(null, f)) {
                    f.cancel(false);
                } else {
                    return;
                }
            } catch (RejectedExecutionException ex) {
                RxJavaHooks.onError(ex);
                return;
            }
        }
    }

    private void initialize(int min) {
        if (UnsafeAccess.isUnsafeAvailable()) {
            this.pool = new MpmcArrayQueue(Math.max(this.maxSize, 1024));
        } else {
            this.pool = new ConcurrentLinkedQueue();
        }
        for (int i = 0; i < min; i++) {
            this.pool.add(createObject());
        }
    }
}
