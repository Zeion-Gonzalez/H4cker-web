package org.jcodec.codecs.mpeg4.es;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/* loaded from: classes.dex */
public class NodeDescriptor extends Descriptor {
    private Collection<Descriptor> children;

    public NodeDescriptor(int tag, int size) {
        super(tag, size);
        this.children = new ArrayList();
    }

    public NodeDescriptor(int tag) {
        super(tag);
        this.children = new ArrayList();
    }

    public NodeDescriptor(int tag, Descriptor[] children) {
        super(tag);
        this.children = new ArrayList();
        this.children.addAll(Arrays.asList(children));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg4.es.Descriptor
    public void doWrite(ByteBuffer out) {
        for (Descriptor descr : this.children) {
            descr.write(out);
        }
    }

    public Collection<Descriptor> getChildren() {
        return this.children;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.codecs.mpeg4.es.Descriptor
    public void parse(ByteBuffer input) {
        Descriptor d;
        do {
            d = Descriptor.read(input);
            if (d != null) {
                this.children.add(d);
            }
        } while (d != null);
    }
}
