package org.jcodec.movtool;

import java.io.File;
import java.nio.ByteBuffer;
import org.jcodec.codecs.prores.ProresFix;
import org.jcodec.containers.mp4.MP4Packet;

/* loaded from: classes.dex */
public class ReExport extends Remux {
    private ByteBuffer outBuf;

    @Override // org.jcodec.movtool.Remux
    protected MP4Packet processFrame(MP4Packet pkt) {
        if (this.outBuf == null) {
            this.outBuf = ByteBuffer.allocate(pkt.getData().remaining() * 2);
        }
        ByteBuffer out = ProresFix.transcode(pkt.getData(), this.outBuf);
        return new MP4Packet(pkt, out);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("reexport <movie> <out>");
            return;
        }
        File tgt = new File(args[0]);
        File src = hidFile(tgt);
        tgt.renameTo(src);
        try {
            new ReExport().remux(tgt, src, null, null);
        } catch (Throwable t) {
            t.printStackTrace();
            tgt.renameTo(new File(tgt.getParentFile(), tgt.getName() + ".error"));
            src.renameTo(tgt);
        }
    }
}
