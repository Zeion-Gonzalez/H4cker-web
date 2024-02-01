package p045rx.observers;

import p045rx.Observer;
import p045rx.exceptions.Exceptions;
import p045rx.internal.operators.NotificationLite;

/* loaded from: classes.dex */
public class SerializedObserver<T> implements Observer<T> {
    private final Observer<? super T> actual;
    private boolean emitting;

    /* renamed from: nl */
    private final NotificationLite<T> f1638nl = NotificationLite.instance();
    private FastList queue;
    private volatile boolean terminated;

    /* loaded from: classes.dex */
    static final class FastList {
        Object[] array;
        int size;

        FastList() {
        }

        public void add(Object o) {
            int s = this.size;
            Object[] a = this.array;
            if (a == null) {
                a = new Object[16];
                this.array = a;
            } else if (s == a.length) {
                Object[] array2 = new Object[(s >> 2) + s];
                System.arraycopy(a, 0, array2, 0, s);
                a = array2;
                this.array = a;
            }
            a[s] = o;
            this.size = s + 1;
        }
    }

    public SerializedObserver(Observer<? super T> s) {
        this.actual = s;
    }

    /* JADX WARN: Code restructure failed: missing block: B:60:0x0033, code lost:
    
        continue;
     */
    @Override // p045rx.Observer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onNext(T r10) {
        /*
            r9 = this;
            r8 = 1
            boolean r6 = r9.terminated
            if (r6 == 0) goto L6
        L5:
            return
        L6:
            monitor-enter(r9)
            boolean r6 = r9.terminated     // Catch: java.lang.Throwable -> Ld
            if (r6 == 0) goto L10
            monitor-exit(r9)     // Catch: java.lang.Throwable -> Ld
            goto L5
        Ld:
            r6 = move-exception
            monitor-exit(r9)     // Catch: java.lang.Throwable -> Ld
            throw r6
        L10:
            boolean r6 = r9.emitting     // Catch: java.lang.Throwable -> Ld
            if (r6 == 0) goto L2a
            rx.observers.SerializedObserver$FastList r4 = r9.queue     // Catch: java.lang.Throwable -> Ld
            if (r4 != 0) goto L1f
            rx.observers.SerializedObserver$FastList r4 = new rx.observers.SerializedObserver$FastList     // Catch: java.lang.Throwable -> Ld
            r4.<init>()     // Catch: java.lang.Throwable -> Ld
            r9.queue = r4     // Catch: java.lang.Throwable -> Ld
        L1f:
            rx.internal.operators.NotificationLite<T> r6 = r9.f1638nl     // Catch: java.lang.Throwable -> Ld
            java.lang.Object r6 = r6.next(r10)     // Catch: java.lang.Throwable -> Ld
            r4.add(r6)     // Catch: java.lang.Throwable -> Ld
            monitor-exit(r9)     // Catch: java.lang.Throwable -> Ld
            goto L5
        L2a:
            r6 = 1
            r9.emitting = r6     // Catch: java.lang.Throwable -> Ld
            monitor-exit(r9)     // Catch: java.lang.Throwable -> Ld
            rx.Observer<? super T> r6 = r9.actual     // Catch: java.lang.Throwable -> L40
            r6.onNext(r10)     // Catch: java.lang.Throwable -> L40
        L33:
            monitor-enter(r9)
            rx.observers.SerializedObserver$FastList r4 = r9.queue     // Catch: java.lang.Throwable -> L3d
            if (r4 != 0) goto L49
            r6 = 0
            r9.emitting = r6     // Catch: java.lang.Throwable -> L3d
            monitor-exit(r9)     // Catch: java.lang.Throwable -> L3d
            goto L5
        L3d:
            r6 = move-exception
            monitor-exit(r9)     // Catch: java.lang.Throwable -> L3d
            throw r6
        L40:
            r1 = move-exception
            r9.terminated = r8
            rx.Observer<? super T> r6 = r9.actual
            p045rx.exceptions.Exceptions.throwOrReport(r1, r6, r10)
            goto L5
        L49:
            r6 = 0
            r9.queue = r6     // Catch: java.lang.Throwable -> L3d
            monitor-exit(r9)     // Catch: java.lang.Throwable -> L3d
            java.lang.Object[] r0 = r4.array
            int r3 = r0.length
            r2 = 0
        L51:
            if (r2 >= r3) goto L33
            r5 = r0[r2]
            if (r5 == 0) goto L33
            rx.internal.operators.NotificationLite<T> r6 = r9.f1638nl     // Catch: java.lang.Throwable -> L65
            rx.Observer<? super T> r7 = r9.actual     // Catch: java.lang.Throwable -> L65
            boolean r6 = r6.accept(r7, r5)     // Catch: java.lang.Throwable -> L65
            if (r6 == 0) goto L75
            r6 = 1
            r9.terminated = r6     // Catch: java.lang.Throwable -> L65
            goto L5
        L65:
            r1 = move-exception
            r9.terminated = r8
            p045rx.exceptions.Exceptions.throwIfFatal(r1)
            rx.Observer<? super T> r6 = r9.actual
            java.lang.Throwable r7 = p045rx.exceptions.OnErrorThrowable.addValueAsLastCause(r1, r10)
            r6.onError(r7)
            goto L5
        L75:
            int r2 = r2 + 1
            goto L51
        */
        throw new UnsupportedOperationException("Method not decompiled: p045rx.observers.SerializedObserver.onNext(java.lang.Object):void");
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        Exceptions.throwIfFatal(e);
        if (!this.terminated) {
            synchronized (this) {
                if (!this.terminated) {
                    this.terminated = true;
                    if (this.emitting) {
                        FastList list = this.queue;
                        if (list == null) {
                            list = new FastList();
                            this.queue = list;
                        }
                        list.add(this.f1638nl.error(e));
                        return;
                    }
                    this.emitting = true;
                    this.actual.onError(e);
                }
            }
        }
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        if (!this.terminated) {
            synchronized (this) {
                if (!this.terminated) {
                    this.terminated = true;
                    if (this.emitting) {
                        FastList list = this.queue;
                        if (list == null) {
                            list = new FastList();
                            this.queue = list;
                        }
                        list.add(this.f1638nl.completed());
                        return;
                    }
                    this.emitting = true;
                    this.actual.onCompleted();
                }
            }
        }
    }
}
