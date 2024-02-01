package org.jcodec.containers.mp4.boxes;

import java.nio.ByteBuffer;
import org.jcodec.containers.mp4.boxes.channel.Label;

/* loaded from: classes.dex */
public class ChannelBox extends FullBox {
    private int channelBitmap;
    private int channelLayout;
    private ChannelDescription[] descriptions;

    /* loaded from: classes.dex */
    public static class ChannelDescription {
        private int channelFlags;
        private int channelLabel;
        private float[] coordinates;

        public ChannelDescription(int channelLabel, int channelFlags, float[] coordinates) {
            this.coordinates = new float[3];
            this.channelLabel = channelLabel;
            this.channelFlags = channelFlags;
            this.coordinates = coordinates;
        }

        public int getChannelLabel() {
            return this.channelLabel;
        }

        public int getChannelFlags() {
            return this.channelFlags;
        }

        public float[] getCoordinates() {
            return this.coordinates;
        }

        public Label getLabel() {
            return Label.getByVal(this.channelLabel);
        }
    }

    public ChannelBox(Header atom) {
        super(atom);
    }

    public ChannelBox() {
        super(new Header(fourcc()));
    }

    public static String fourcc() {
        return "chan";
    }

    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void parse(ByteBuffer input) {
        super.parse(input);
        this.channelLayout = input.getInt();
        this.channelBitmap = input.getInt();
        int numDescriptions = input.getInt();
        this.descriptions = new ChannelDescription[numDescriptions];
        for (int i = 0; i < numDescriptions; i++) {
            this.descriptions[i] = new ChannelDescription(input.getInt(), input.getInt(), new float[]{Float.intBitsToFloat(input.getInt()), Float.intBitsToFloat(input.getInt()), Float.intBitsToFloat(input.getInt())});
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jcodec.containers.mp4.boxes.FullBox, org.jcodec.containers.mp4.boxes.Box
    public void doWrite(ByteBuffer out) {
        super.doWrite(out);
        out.putInt(this.channelLayout);
        out.putInt(this.channelBitmap);
        out.putInt(this.descriptions.length);
        ChannelDescription[] arr$ = this.descriptions;
        for (ChannelDescription channelDescription : arr$) {
            out.putInt(channelDescription.getChannelLabel());
            out.putInt(channelDescription.getChannelFlags());
            out.putFloat(channelDescription.getCoordinates()[0]);
            out.putFloat(channelDescription.getCoordinates()[1]);
            out.putFloat(channelDescription.getCoordinates()[2]);
        }
    }

    public int getChannelLayout() {
        return this.channelLayout;
    }

    public int getChannelBitmap() {
        return this.channelBitmap;
    }

    public ChannelDescription[] getDescriptions() {
        return this.descriptions;
    }

    public void setChannelLayout(int channelLayout) {
        this.channelLayout = channelLayout;
    }

    public void setDescriptions(ChannelDescription[] descriptions) {
        this.descriptions = descriptions;
    }
}
