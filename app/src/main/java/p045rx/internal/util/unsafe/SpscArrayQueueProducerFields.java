package p045rx.internal.util.unsafe;

import p045rx.internal.util.SuppressAnimalSniffer;

/* compiled from: SpscArrayQueue.java */
@SuppressAnimalSniffer
/* loaded from: classes.dex */
abstract class SpscArrayQueueProducerFields<E> extends SpscArrayQueueL1Pad<E> {
    protected static final long P_INDEX_OFFSET = UnsafeAccess.addressOf(SpscArrayQueueProducerFields.class, "producerIndex");
    protected long producerIndex;
    protected long producerLookAhead;

    public SpscArrayQueueProducerFields(int capacity) {
        super(capacity);
    }
}
