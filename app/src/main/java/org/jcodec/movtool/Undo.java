package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.BoxFactory;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;

/* loaded from: classes.dex */
public class Undo {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Syntax: qt-undo [-l] <movie>");
            System.err.println("\t-l\t\tList all the previous versions of this movie.");
            System.exit(-1);
        }
        Undo undo = new Undo();
        if ("-l".equals(args[0])) {
            List<MP4Util.Atom> list = undo.list(args[1]);
            System.out.println((list.size() - 1) + " versions.");
        } else {
            undo.undo(args[0]);
        }
    }

    private void undo(String fineName) throws IOException {
        List<MP4Util.Atom> versions = list(fineName);
        if (versions.size() < 2) {
            System.err.println("Nowhere to rollback.");
            return;
        }
        RandomAccessFile raf = null;
        try {
            RandomAccessFile raf2 = new RandomAccessFile(new File(fineName), "rw");
            try {
                raf2.seek(versions.get(versions.size() - 2).getOffset() + 4);
                raf2.write(new byte[]{109, 111, 111, 118});
                raf2.seek(versions.get(versions.size() - 1).getOffset() + 4);
                raf2.write(new byte[]{102, 114, 101, 101});
                raf2.close();
            } catch (Throwable th) {
                th = th;
                raf = raf2;
                raf.close();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x004d, code lost:
    
        r3.add(r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.util.List<org.jcodec.containers.mp4.MP4Util.Atom> list(java.lang.String r8) throws java.io.IOException {
        /*
            r7 = this;
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r2 = 0
            java.io.File r5 = new java.io.File     // Catch: java.lang.Throwable -> L54
            r5.<init>(r8)     // Catch: java.lang.Throwable -> L54
            org.jcodec.common.FileChannelWrapper r2 = org.jcodec.common.NIOUtils.readableFileChannel(r5)     // Catch: java.lang.Throwable -> L54
            r4 = 0
            java.util.List r5 = org.jcodec.containers.mp4.MP4Util.getRootAtoms(r2)     // Catch: java.lang.Throwable -> L54
            java.util.Iterator r1 = r5.iterator()     // Catch: java.lang.Throwable -> L54
        L18:
            boolean r5 = r1.hasNext()     // Catch: java.lang.Throwable -> L54
            if (r5 == 0) goto L50
            java.lang.Object r0 = r1.next()     // Catch: java.lang.Throwable -> L54
            org.jcodec.containers.mp4.MP4Util$Atom r0 = (org.jcodec.containers.mp4.MP4Util.Atom) r0     // Catch: java.lang.Throwable -> L54
            java.lang.String r5 = "free"
            org.jcodec.containers.mp4.boxes.Header r6 = r0.getHeader()     // Catch: java.lang.Throwable -> L54
            java.lang.String r6 = r6.getFourcc()     // Catch: java.lang.Throwable -> L54
            boolean r5 = r5.equals(r6)     // Catch: java.lang.Throwable -> L54
            if (r5 == 0) goto L3d
            boolean r5 = r7.isMoov(r2, r0)     // Catch: java.lang.Throwable -> L54
            if (r5 == 0) goto L3d
            r3.add(r0)     // Catch: java.lang.Throwable -> L54
        L3d:
            java.lang.String r5 = "moov"
            org.jcodec.containers.mp4.boxes.Header r6 = r0.getHeader()     // Catch: java.lang.Throwable -> L54
            java.lang.String r6 = r6.getFourcc()     // Catch: java.lang.Throwable -> L54
            boolean r5 = r5.equals(r6)     // Catch: java.lang.Throwable -> L54
            if (r5 == 0) goto L18
            r3.add(r0)     // Catch: java.lang.Throwable -> L54
        L50:
            r2.close()
            return r3
        L54:
            r5 = move-exception
            r2.close()
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.movtool.Undo.list(java.lang.String):java.util.List");
    }

    private boolean isMoov(SeekableByteChannel is, MP4Util.Atom atom) throws IOException {
        is.position(atom.getOffset() + atom.getHeader().headerSize());
        try {
            Box mov = NodeBox.parseBox(NIOUtils.fetchFrom(is, (int) atom.getHeader().getSize()), new Header("moov", atom.getHeader().getSize()), BoxFactory.getDefault());
            if (mov instanceof MovieBox) {
                if (Box.findFirst((NodeBox) mov, "mvhd") != null) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }
}
