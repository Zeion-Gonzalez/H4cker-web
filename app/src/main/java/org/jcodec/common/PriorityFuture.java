package org.jcodec.common;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class PriorityFuture<T> implements RunnableFuture<T> {
    public static Comparator<Runnable> COMP = new Comparator<Runnable>() { // from class: org.jcodec.common.PriorityFuture.1
        @Override // java.util.Comparator
        public int compare(Runnable o1, Runnable o2) {
            int p1;
            int p2;
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null || (p1 = ((PriorityFuture) o1).getPriority()) > (p2 = ((PriorityFuture) o2).getPriority())) {
                return 1;
            }
            return p1 == p2 ? 0 : -1;
        }
    };
    private int priority;
    private RunnableFuture<T> src;

    public PriorityFuture(RunnableFuture<T> other, int priority) {
        this.src = other;
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.src.cancel(mayInterruptIfRunning);
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.src.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.src.isDone();
    }

    @Override // java.util.concurrent.Future
    public T get() throws InterruptedException, ExecutionException {
        return this.src.get();
    }

    @Override // java.util.concurrent.Future
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.src.get();
    }

    @Override // java.util.concurrent.RunnableFuture, java.lang.Runnable
    public void run() {
        this.src.run();
    }
}
