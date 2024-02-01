package org.jcodec.movtool;

import java.io.File;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;
import org.jcodec.containers.mp4.boxes.NodeBox;
import org.jcodec.containers.mp4.boxes.SampleDescriptionBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;

/* loaded from: classes.dex */
public class SetPAR {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Syntax: setpasp <movie> <num:den>");
            System.exit(-1);
        }
        final Rational newPAR = Rational.parse(args[1]);
        new InplaceMP4Editor().modify(new File(args[0]), new MP4Edit() { // from class: org.jcodec.movtool.SetPAR.1
            @Override // org.jcodec.movtool.MP4Edit
            public void apply(MovieBox mov) {
                TrakBox vt = mov.getVideoTrack();
                vt.setPAR(Rational.this);
                Box box = ((SampleDescriptionBox) NodeBox.findFirst(vt, SampleDescriptionBox.class, "mdia", "minf", "stbl", "stsd")).getBoxes().get(0);
                if (box != null && (box instanceof VideoSampleEntry)) {
                    VideoSampleEntry vs = (VideoSampleEntry) box;
                    int codedWidth = vs.getWidth();
                    int codedHeight = vs.getHeight();
                    int displayWidth = (Rational.this.getNum() * codedWidth) / Rational.this.getDen();
                    vt.getTrackHeader().setWidth(displayWidth);
                    Box tapt = Box.findFirst(vt, "tapt");
                    if (tapt != null) {
                        vt.setAperture(new Size(codedWidth, codedHeight), new Size(displayWidth, codedHeight));
                    }
                }
            }

            @Override // org.jcodec.movtool.MP4Edit
            public void apply(MovieBox mov, MovieFragmentBox[] fragmentBox) {
                throw new RuntimeException("Unsupported");
            }
        });
    }
}
