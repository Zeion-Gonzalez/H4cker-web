package org.jcodec.testing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.MappedH264ES;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;

/* loaded from: classes.dex */
public class VerifyTool {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Syntax: <error folder location>");
        } else {
            new VerifyTool().doIt(args[0]);
        }
    }

    private void doIt(String location) throws IOException {
        File[] h264 = new File(location).listFiles(new FilenameFilter() { // from class: org.jcodec.testing.VerifyTool.1
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String name) {
                return name.endsWith(".264");
            }
        });
        for (File coded : h264) {
            File ref = new File(coded.getParentFile(), coded.getName().replaceAll(".264$", "_dec.yuv"));
            if (coded.exists() && ref.exists()) {
                try {
                    if (test(coded, ref)) {
                        System.out.println(coded.getAbsolutePath() + " -- FIXED");
                        coded.delete();
                        ref.delete();
                    } else {
                        System.out.println(coded.getAbsolutePath() + " -- NOT FIXED!!!!");
                    }
                } catch (Throwable t) {
                    System.out.println(coded.getAbsolutePath() + " -- ERROR: " + t.getMessage());
                }
            }
        }
    }

    private boolean test(File coded, File ref) throws IOException {
        Picture pic;
        int cbSize;
        ByteBuffer yuv;
        MappedH264ES es = new MappedH264ES(NIOUtils.fetchFrom(coded));
        Picture buf = Picture.create(1920, 1088, ColorSpace.YUV420);
        H264Decoder dec = new H264Decoder();
        ByteBuffer _yuv = NIOUtils.fetchFrom(ref);
        do {
            Packet nextFrame = es.nextFrame();
            if (nextFrame != null) {
                Picture out = dec.decodeFrame(nextFrame.getData(), buf.getData()).cropped();
                pic = out.createCompatible();
                pic.copyFrom(out);
                int lumaSize = pic.getWidth() * pic.getHeight();
                int crSize = lumaSize >> 2;
                cbSize = lumaSize >> 2;
                yuv = NIOUtils.read(_yuv, lumaSize + crSize + cbSize);
                if (!Arrays.equals(JCodecUtil.getAsIntArray(yuv, lumaSize), pic.getPlaneData(0)) || !Arrays.equals(JCodecUtil.getAsIntArray(yuv, crSize), pic.getPlaneData(1))) {
                    return false;
                }
            } else {
                return true;
            }
        } while (Arrays.equals(JCodecUtil.getAsIntArray(yuv, cbSize), pic.getPlaneData(2)));
        return false;
    }
}
