package p045rx.internal.util;

import java.util.concurrent.atomic.AtomicLong;
import p045rx.Producer;
import p045rx.annotations.Experimental;

@Experimental
/* loaded from: classes.dex */
public final class BackpressureDrainManager extends AtomicLong implements Producer {
    private static final long serialVersionUID = 2826241102729529449L;
    final BackpressureQueueCallback actual;
    boolean emitting;
    Throwable exception;
    volatile boolean terminated;

    /* loaded from: classes.dex */
    public interface BackpressureQueueCallback {
        boolean accept(Object obj);

        void complete(Throwable th);

        Object peek();

        Object poll();
    }

    public BackpressureDrainManager(BackpressureQueueCallback actual) {
        this.actual = actual;
    }

    public boolean isTerminated() {
        return this.terminated;
    }

    public void terminate() {
        this.terminated = true;
    }

    public void terminate(Throwable error) {
        if (!this.terminated) {
            this.exception = error;
            this.terminated = true;
        }
    }

    public void terminateAndDrain() {
        this.terminated = true;
        drain();
    }

    public void terminateAndDrain(Throwable error) {
        if (!this.terminated) {
            this.exception = error;
            this.terminated = true;
            drain();
        }
    }

    @Override // p045rx.Producer
    public void request(long n) {
        long r;
        boolean mayDrain;
        long u;
        if (n == 0) {
            return;
        }
        do {
            r = get();
            mayDrain = r == 0;
            if (r == Long.MAX_VALUE) {
                break;
            }
            if (n == Long.MAX_VALUE) {
                u = n;
                mayDrain = true;
            } else if (r > Long.MAX_VALUE - n) {
                u = Long.MAX_VALUE;
            } else {
                u = r + n;
            }
        } while (!compareAndSet(r, u));
        if (mayDrain) {
            drain();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:124:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:125:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x005f, code lost:
    
        if (1 != 0) goto L124;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0061, code lost:
    
        monitor-enter(r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0063, code lost:
    
        r14.emitting = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0065, code lost:
    
        monitor-exit(r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x00af, code lost:
    
        r14.emitting = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void drain() {
        /*
            Method dump skipped, instructions count: 195
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: p045rx.internal.util.BackpressureDrainManager.drain():void");
    }
}
