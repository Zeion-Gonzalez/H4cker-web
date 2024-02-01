package p045rx.internal.util.atomic;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;
import p045rx.internal.util.unsafe.Pow2;

/* loaded from: classes.dex */
abstract class AtomicReferenceArrayQueue<E> extends AbstractQueue<E> {
    protected final AtomicReferenceArray<E> buffer;
    protected final int mask;

    public AtomicReferenceArrayQueue(int capacity) {
        int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
        this.mask = actualCapacity - 1;
        this.buffer = new AtomicReferenceArray<>(actualCapacity);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        while (true) {
            if (poll() == null && isEmpty()) {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int calcElementOffset(long index, int mask) {
        return ((int) index) & mask;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int calcElementOffset(long index) {
        return ((int) index) & this.mask;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final E lvElement(AtomicReferenceArray<E> buffer, int offset) {
        return buffer.get(offset);
    }

    protected final E lpElement(AtomicReferenceArray<E> buffer, int offset) {
        return buffer.get(offset);
    }

    protected final E lpElement(int offset) {
        return this.buffer.get(offset);
    }

    protected final void spElement(AtomicReferenceArray<E> buffer, int offset, E value) {
        buffer.lazySet(offset, value);
    }

    protected final void spElement(int offset, E value) {
        this.buffer.lazySet(offset, value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void soElement(AtomicReferenceArray<E> buffer, int offset, E value) {
        buffer.lazySet(offset, value);
    }

    protected final void soElement(int offset, E value) {
        this.buffer.lazySet(offset, value);
    }

    protected final void svElement(AtomicReferenceArray<E> buffer, int offset, E value) {
        buffer.set(offset, value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final E lvElement(int offset) {
        return lvElement(this.buffer, offset);
    }
}
