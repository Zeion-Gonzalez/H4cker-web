package org.jcodec.codecs.mpeg12;

import com.instabug.chat.model.Attachment;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class SetBaseTimestamp extends FixTimestamp {
    private int baseTs;
    private long firstPts = -1;
    private boolean video;

    public SetBaseTimestamp(boolean video, int baseTs) {
        this.video = video;
        this.baseTs = baseTs;
    }

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        new SetBaseTimestamp(Attachment.TYPE_VIDEO.equalsIgnoreCase(args[1]), Integer.parseInt(args[2])).fix(file);
    }

    @Override // org.jcodec.codecs.mpeg12.FixTimestamp
    protected long doWithTimestamp(int streamId, long pts, boolean isPts) {
        if ((this.video && isVideo(streamId)) || (!this.video && isAudio(streamId))) {
            if (this.firstPts == -1) {
                this.firstPts = pts;
            }
            return (pts - this.firstPts) + this.baseTs;
        }
        return pts;
    }
}
