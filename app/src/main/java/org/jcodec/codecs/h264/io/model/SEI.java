package org.jcodec.codecs.h264.io.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.codecs.h264.io.write.CAVLCWriter;
import org.jcodec.common.io.BitWriter;

/* loaded from: classes.dex */
public class SEI {
    public SEIMessage[] messages;

    /* loaded from: classes.dex */
    public static class SEIMessage {
        public byte[] payload;
        public int payloadSize;
        public int payloadType;

        public SEIMessage(int payloadType2, int payloadSize2, byte[] payload2) {
            this.payload = payload2;
            this.payloadType = payloadType2;
            this.payloadSize = payloadSize2;
        }
    }

    public SEI(SEIMessage[] messages) {
        this.messages = messages;
    }

    public static SEI read(ByteBuffer is) {
        SEIMessage msg;
        List<SEIMessage> messages = new ArrayList<>();
        do {
            msg = sei_message(is);
            if (msg != null) {
                messages.add(msg);
            }
        } while (msg != null);
        return new SEI((SEIMessage[]) messages.toArray(new SEIMessage[0]));
    }

    private static SEIMessage sei_message(ByteBuffer is) {
        int b;
        int b2;
        int payloadType = 0;
        while (true) {
            b = is.get() & 255;
            if (b != 255) {
                break;
            }
            payloadType += 255;
        }
        if (b == -1) {
            return null;
        }
        int payloadType2 = payloadType + b;
        int payloadSize = 0;
        while (true) {
            b2 = is.get() & 255;
            if (b2 != 255) {
                break;
            }
            payloadSize += 255;
        }
        if (b2 == -1) {
            return null;
        }
        int payloadSize2 = payloadSize + b2;
        byte[] payload = sei_payload(payloadType2, payloadSize2, is);
        if (payload.length == payloadSize2) {
            return new SEIMessage(payloadType2, payloadSize2, payload);
        }
        return null;
    }

    private static byte[] sei_payload(int payloadType, int payloadSize, ByteBuffer is) {
        byte[] res = new byte[payloadSize];
        is.get(res);
        return res;
    }

    public void write(ByteBuffer out) {
        BitWriter writer = new BitWriter(out);
        CAVLCWriter.writeTrailingBits(writer);
    }
}
