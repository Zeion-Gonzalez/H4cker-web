package org.jcodec.movtool;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jcodec.common.Assert;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.Tuple;
import org.jcodec.containers.mp4.MP4Util;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.BoxFactory;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.MovieFragmentBox;
import org.jcodec.containers.mp4.boxes.NodeBox;

/* loaded from: classes.dex */
public class InplaceMP4Editor {
    public boolean modify(File file, MP4Edit edit) throws IOException {
        SeekableByteChannel fi = null;
        try {
            fi = NIOUtils.rwFileChannel(file);
            List<Tuple.C0871_2<MP4Util.Atom, ByteBuffer>> fragments = doTheFix(fi, edit);
            if (fragments != null) {
                for (Tuple.C0871_2<MP4Util.Atom, ByteBuffer> fragment : fragments) {
                    replaceBox(fi, fragment.f1500v0, fragment.f1501v1);
                }
                return true;
            }
            return false;
        } finally {
            NIOUtils.closeQuietly(fi);
        }
    }

    public boolean copy(File src, File dst, MP4Edit edit) throws IOException {
        SeekableByteChannel fi = null;
        SeekableByteChannel fo = null;
        try {
            fi = NIOUtils.readableFileChannel(src);
            fo = NIOUtils.writableFileChannel(dst);
            List<Tuple.C0871_2<MP4Util.Atom, ByteBuffer>> fragments = doTheFix(fi, edit);
            if (fragments != null) {
                List<Tuple.C0871_2<Long, ByteBuffer>> fragOffsets = Tuple._2map0(fragments, new Tuple.Mapper<MP4Util.Atom, Long>() { // from class: org.jcodec.movtool.InplaceMP4Editor.1
                    @Override // org.jcodec.common.Tuple.Mapper
                    public Long map(MP4Util.Atom t) {
                        return Long.valueOf(t.getOffset());
                    }
                });
                Map<Long, ByteBuffer> rewrite = Tuple.asMap(fragOffsets);
                for (MP4Util.Atom atom : MP4Util.getRootAtoms(fi)) {
                    ByteBuffer byteBuffer = rewrite.get(Long.valueOf(atom.getOffset()));
                    if (byteBuffer != null) {
                        fo.write(byteBuffer);
                    } else {
                        atom.copy(fi, fo);
                    }
                }
                return true;
            }
            return false;
        } finally {
            NIOUtils.closeQuietly(fi);
            NIOUtils.closeQuietly(fo);
        }
    }

    public boolean replace(File src, MP4Edit edit) throws IOException {
        File tmp = new File(src.getParentFile(), "." + src.getName());
        if (!copy(src, tmp, edit)) {
            return false;
        }
        tmp.renameTo(src);
        return true;
    }

    private List<Tuple.C0871_2<MP4Util.Atom, ByteBuffer>> doTheFix(SeekableByteChannel fi, MP4Edit edit) throws IOException {
        MP4Util.Atom moovAtom = getMoov(fi);
        Assert.assertNotNull(moovAtom);
        ByteBuffer moovBuffer = fetchBox(fi, moovAtom);
        MovieBox moovBox = (MovieBox) parseBox(moovBuffer);
        List<Tuple.C0871_2<MP4Util.Atom, ByteBuffer>> fragments = new LinkedList<>();
        if (Box.findFirst(moovBox, "mvex") != null) {
            List<Tuple.C0871_2<ByteBuffer, MovieFragmentBox>> temp = new LinkedList<>();
            for (MP4Util.Atom fragAtom : getFragments(fi)) {
                ByteBuffer fragBuffer = fetchBox(fi, fragAtom);
                fragments.add(Tuple.m2128_2(fragAtom, fragBuffer));
                MovieFragmentBox fragBox = (MovieFragmentBox) parseBox(fragBuffer);
                fragBox.setMovie(moovBox);
                temp.add(Tuple.m2128_2(fragBuffer, fragBox));
            }
            edit.apply(moovBox, (MovieFragmentBox[]) Tuple._2_project1(temp).toArray(new MovieFragmentBox[0]));
            for (Tuple.C0871_2<ByteBuffer, MovieFragmentBox> frag : temp) {
                if (!rewriteBox((ByteBuffer) frag.f1500v0, (Box) frag.f1501v1)) {
                    return null;
                }
            }
        } else {
            edit.apply(moovBox);
        }
        if (!rewriteBox(moovBuffer, moovBox)) {
            return null;
        }
        fragments.add(Tuple.m2128_2(moovAtom, moovBuffer));
        return fragments;
    }

    private void replaceBox(SeekableByteChannel fi, MP4Util.Atom atom, ByteBuffer buffer) throws IOException {
        fi.position(atom.getOffset());
        fi.write(buffer);
    }

    private boolean rewriteBox(ByteBuffer buffer, Box box) {
        try {
            buffer.clear();
            box.write(buffer);
            if (buffer.hasRemaining()) {
                if (buffer.remaining() < 8) {
                    return false;
                }
                buffer.putInt(buffer.remaining());
                buffer.put(new byte[]{102, 114, 101, 101});
            }
            buffer.flip();
            return true;
        } catch (BufferOverflowException e) {
            return false;
        }
    }

    private ByteBuffer fetchBox(SeekableByteChannel fi, MP4Util.Atom moov) throws IOException {
        fi.position(moov.getOffset());
        ByteBuffer oldMov = NIOUtils.fetchFrom(fi, (int) moov.getHeader().getSize());
        return oldMov;
    }

    private Box parseBox(ByteBuffer oldMov) {
        Header header = Header.read(oldMov);
        Box box = NodeBox.parseBox(oldMov, header, BoxFactory.getDefault());
        return box;
    }

    private MP4Util.Atom getMoov(SeekableByteChannel f) throws IOException {
        for (MP4Util.Atom atom : MP4Util.getRootAtoms(f)) {
            if ("moov".equals(atom.getHeader().getFourcc())) {
                return atom;
            }
        }
        return null;
    }

    private List<MP4Util.Atom> getFragments(SeekableByteChannel f) throws IOException {
        List<MP4Util.Atom> result = new LinkedList<>();
        for (MP4Util.Atom atom : MP4Util.getRootAtoms(f)) {
            if ("moof".equals(atom.getHeader().getFourcc())) {
                result.add(atom);
            }
        }
        return result;
    }
}
