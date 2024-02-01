package org.jcodec.movtool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.NodeBox;

/* loaded from: classes.dex */
public class MovDump {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Syntax: movdump [options] <filename>");
            System.out.println("Options: \n\t-f <filename> save header to a file\n\t-a <atom name> dump only a specific atom\n");
            return;
        }
        int idx = 0;
        File headerFile = null;
        String atom = null;
        while (idx < args.length) {
            if ("-f".equals(args[idx])) {
                int idx2 = idx + 1;
                headerFile = new File(args[idx2]);
                idx = idx2 + 1;
            } else {
                if (!"-a".equals(args[idx])) {
                    break;
                }
                int idx3 = idx + 1;
                atom = args[idx3];
                idx = idx3 + 1;
            }
        }
        File source = new File(args[idx]);
        if (headerFile != null) {
            dumpHeader(headerFile, source);
        }
        if (atom == null) {
            System.out.println(print(source));
            return;
        }
        String dump = print(source, atom);
        if (dump != null) {
            System.out.println(dump);
        }
    }

    private static void dumpHeader(File headerFile, File source) throws IOException, FileNotFoundException {
        SeekableByteChannel raf = null;
        SeekableByteChannel daos = null;
        try {
            raf = NIOUtils.readableFileChannel(source);
            daos = NIOUtils.writableFileChannel(headerFile);
            for (MP4Util.Atom atom : MP4Util.getRootAtoms(raf)) {
                String fourcc = atom.getHeader().getFourcc();
                if ("moov".equals(fourcc) || "ftyp".equals(fourcc)) {
                    atom.copy(raf, daos);
                }
            }
        } finally {
            raf.close();
            daos.close();
        }
    }

    public static String print(File file) throws IOException {
        return MP4Util.parseMovie(file).toString();
    }

    private static Box findDeep(NodeBox root, String atom) {
        Box res;
        for (Box b : root.getBoxes()) {
            if (!atom.equalsIgnoreCase(b.getFourcc())) {
                if ((b instanceof NodeBox) && (res = findDeep((NodeBox) b, atom)) != null) {
                    return res;
                }
            } else {
                return b;
            }
        }
        return null;
    }

    public static String print(File file, String atom) throws IOException {
        MovieBox mov = MP4Util.parseMovie(file);
        Box found = findDeep(mov, atom);
        if (found != null) {
            return found.toString();
        }
        System.out.println("Atom " + atom + " not found.");
        return null;
    }
}
