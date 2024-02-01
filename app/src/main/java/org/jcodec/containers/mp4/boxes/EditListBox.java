package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class EditListBox extends FullBox {
    private List<Edit> edits;

    public static String fourcc() {
        return "elst";
    }

    public EditListBox(Header atom) {
        super(atom);
    }

    public EditListBox() {
        this(new Header(fourcc()));
    }

    public EditListBox(List<Edit> edits) {
        this();
        this.edits = edits;
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.edits = new ArrayList();
        long num = input.getInt();
        for (int i = 0; i < num; i++) {
            int duration = input.getInt();
            int mediaTime = input.getInt();
            float rate = input.getInt() / 65536.0f;
            this.edits.add(new Edit(duration, mediaTime, rate));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.edits.size());
        for (Edit edit : this.edits) {
            out.putInt((int) edit.getDuration());
            out.putInt((int) edit.getMediaTime());
            out.putInt((int) (edit.getRate() * 65536.0f));
        }
    }

    public List<Edit> getEdits() {
        return this.edits;
    }
}
