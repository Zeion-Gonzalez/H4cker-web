package org.jcodec.codecs.mpeg12;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/* loaded from: classes.dex */
public class FixHLSTimestamps extends FixTimestamp {
    private long[] lastPts = new long[256];

    public static void main(String[] args) throws IOException {
        String wildCard = args[0];
        int startIdx = Integer.parseInt(args[1]);
        new FixHLSTimestamps().doIt(wildCard, startIdx);
    }

    private void doIt(String wildCard, int startIdx) throws IOException {
        Arrays.fill(this.lastPts, -1L);
        int i = startIdx;
        while (true) {
            File file = new File(String.format(wildCard, Integer.valueOf(i)));
            System.out.println(file.getAbsolutePath());
            if (file.exists()) {
                fix(file);
                i++;
            } else {
                return;
            }
        }
    }

    @Override // org.jcodec.codecs.mpeg12.FixTimestamp
    protected long doWithTimestamp(int streamId, long pts, boolean isPts) {
        if (!isPts) {
            return pts;
        }
        if (this.lastPts[streamId] == -1) {
            this.lastPts[streamId] = pts;
            return pts;
        }
        if (isVideo(streamId)) {
            long[] jArr = this.lastPts;
            jArr[streamId] = jArr[streamId] + 3003;
            return this.lastPts[streamId];
        }
        if (isAudio(streamId)) {
            long[] jArr2 = this.lastPts;
            jArr2[streamId] = jArr2[streamId] + 1920;
            return this.lastPts[streamId];
        }
        throw new RuntimeException("Unexpected!!!");
    }
}
