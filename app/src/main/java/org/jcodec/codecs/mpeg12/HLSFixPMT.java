package org.jcodec.codecs.mpeg12;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;
import org.jcodec.containers.mps.MTSUtils;

/* loaded from: classes.dex */
public class HLSFixPMT {
    public void fix(File file) throws IOException {
        RandomAccessFile ra = null;
        try {
            RandomAccessFile ra2 = new RandomAccessFile(file, "rw");
            try {
                byte[] tsPkt = new byte[188];
                while (ra2.read(tsPkt) == 188) {
                    Assert.assertEquals(71, tsPkt[0] & 255);
                    int guidFlags = ((tsPkt[1] & 255) << 8) | (tsPkt[2] & 255);
                    int guid = guidFlags & 8191;
                    int payloadStart = (guidFlags >> 14) & 1;
                    int b0 = tsPkt[3] & 255;
                    int i = b0 & 15;
                    int payloadOff = 0;
                    if ((b0 & 32) != 0) {
                        payloadOff = (tsPkt[4] & 255) + 1;
                    }
                    if (payloadStart == 1) {
                        payloadOff += (tsPkt[payloadOff + 4] & 255) + 1;
                    }
                    if (guid == 0) {
                        if (payloadStart == 0) {
                            throw new RuntimeException("PAT spans multiple TS packets, not supported!!!!!!");
                        }
                        ByteBuffer bb = ByteBuffer.wrap(tsPkt, payloadOff + 4, 184 - payloadOff);
                        fixPAT(bb);
                        ra2.seek(ra2.getFilePointer() - 188);
                        ra2.write(tsPkt);
                    }
                }
                if (ra2 != null) {
                    ra2.close();
                }
            } catch (Throwable th) {
                th = th;
                ra = ra2;
                if (ra != null) {
                    ra.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void fixPAT(ByteBuffer data) {
        ByteBuffer table = data.duplicate();
        MTSUtils.parseSection(data);
        ByteBuffer newPmt = data.duplicate();
        while (data.remaining() > 4) {
            short num = data.getShort();
            short pid = data.getShort();
            if (num != 0) {
                newPmt.putShort(num);
                newPmt.putShort(pid);
            }
        }
        if (newPmt.position() != data.position()) {
            ByteBuffer section = table.duplicate();
            section.get();
            int sectionLen = (newPmt.position() - table.position()) + 1;
            section.putShort((short) ((sectionLen & 4095) | 45056));
            CRC32 crc32 = new CRC32();
            table.limit(newPmt.position());
            crc32.update(NIOUtils.toArray(table));
            newPmt.putInt((int) crc32.getValue());
            while (newPmt.hasRemaining()) {
                newPmt.put((byte) -1);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            exit("Please specify package location");
        }
        File hlsPkg = new File(args[0]);
        if (!hlsPkg.isDirectory()) {
            exit("Not an HLS package, expected a folder");
        }
        File[] listFiles = hlsPkg.listFiles(new FilenameFilter() { // from class: org.jcodec.codecs.mpeg12.HLSFixPMT.1
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String name) {
                return name.endsWith(".ts");
            }
        });
        HLSFixPMT fix = new HLSFixPMT();
        for (File file : listFiles) {
            System.err.println("Processing: " + file.getName());
            fix.fix(file);
        }
    }

    private static void exit(String message) {
        System.err.println("Syntax: hls_fixpmt <hls package location>");
        System.err.println(message);
        System.exit(-1);
    }
}
