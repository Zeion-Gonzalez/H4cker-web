package org.jcodec.containers.mp4.boxes;

/* loaded from: classes.dex */
public class PartialSyncSamplesBox extends SyncSamplesBox {
    public static String fourcc() {
        return "stps";
    }

    public PartialSyncSamplesBox() {
        super(new Header(fourcc()));
    }

    public PartialSyncSamplesBox(int[] array) {
        super(array);
    }
}
