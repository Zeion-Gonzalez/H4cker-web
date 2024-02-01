package org.jcodec.codecs.h264.decode.aso;

/* loaded from: classes.dex */
public interface Mapper {
    int getAddress(int i);

    int getMbX(int i);

    int getMbY(int i);

    boolean leftAvailable(int i);

    boolean topAvailable(int i);

    boolean topLeftAvailable(int i);

    boolean topRightAvailable(int i);
}
