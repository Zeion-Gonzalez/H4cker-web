package org.jcodec.containers.mp4.boxes.channel;

import android.support.v4.internal.view.SupportMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import org.jcodec.containers.mp4.boxes.AudioSampleEntry;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.containers.mp4.boxes.ChannelBox;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.boxes.TrakBox;

/* loaded from: classes.dex */
public class ChannelUtils {
    private static final List<Label> MONO = Arrays.asList(Label.Mono);
    private static final List<Label> STEREO = Arrays.asList(Label.Left, Label.Right);
    private static final List<Label> MATRIX_STEREO = Arrays.asList(Label.LeftTotal, Label.RightTotal);
    private static final Label[] EMPTY = new Label[0];

    public static Label[] getLabels(AudioSampleEntry se) {
        ChannelBox channel = (ChannelBox) Box.findFirst(se, ChannelBox.class, "chan");
        if (channel != null) {
            return getLabels(channel);
        }
        int channelCount = se.getChannelCount();
        switch (channelCount) {
            case 1:
                return new Label[]{Label.Mono};
            case 2:
                return new Label[]{Label.Left, Label.Right};
            case 3:
                return new Label[]{Label.Left, Label.Right, Label.Center};
            case 4:
                return new Label[]{Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround};
            case 5:
                return new Label[]{Label.Left, Label.Right, Label.Center, Label.LeftSurround, Label.RightSurround};
            case 6:
                return new Label[]{Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.LeftSurround, Label.RightSurround};
            default:
                Label[] res = new Label[channelCount];
                Arrays.fill(res, Label.Mono);
                return res;
        }
    }

    public static Label[] getLabels(TrakBox trakBox) {
        return getLabels((AudioSampleEntry) trakBox.getSampleEntries()[0]);
    }

    public static void setLabel(TrakBox trakBox, int channel, Label label) {
        Label[] labels = getLabels(trakBox);
        labels[channel] = label;
        setLabels(trakBox, labels);
    }

    private static void setLabels(TrakBox trakBox, Label[] labels) {
        ChannelBox channel = (ChannelBox) Box.findFirst(trakBox, ChannelBox.class, "mdia", "minf", "stbl", "stsd", null, "chan");
        if (channel == null) {
            channel = new ChannelBox();
            ((SampleEntry) Box.findFirst(trakBox, SampleEntry.class, "mdia", "minf", "stbl", "stsd", null)).add(channel);
        }
        setLabels(labels, channel);
    }

    public static void setLabels(Label[] labels, ChannelBox channel) {
        channel.setChannelLayout(ChannelLayout.kCAFChannelLayoutTag_UseChannelDescriptions.getCode());
        ChannelBox.ChannelDescription[] list = new ChannelBox.ChannelDescription[labels.length];
        for (int i = 0; i < labels.length; i++) {
            list[i] = new ChannelBox.ChannelDescription(labels[i].getVal(), 0, new float[]{0.0f, 0.0f, 0.0f});
        }
        channel.setDescriptions(list);
    }

    public static Label[] getLabels(ChannelBox box) {
        long tag = box.getChannelLayout();
        if ((tag >> 16) == 147) {
            int n = ((int) tag) & SupportMenu.USER_MASK;
            Label[] res = new Label[n];
            for (int i = 0; i < n; i++) {
                res[i] = Label.getByVal(65536 | i);
            }
            return res;
        }
        Iterator i$ = EnumSet.allOf(ChannelLayout.class).iterator();
        while (i$.hasNext()) {
            ChannelLayout layout = (ChannelLayout) i$.next();
            if (layout.getCode() == tag) {
                switch (layout) {
                    case kCAFChannelLayoutTag_UseChannelDescriptions:
                        return extractLabels(box.getDescriptions());
                    case kCAFChannelLayoutTag_UseChannelBitmap:
                        return getLabelsByBitmap(box.getChannelBitmap());
                    default:
                        return layout.getLabels();
                }
            }
        }
        return EMPTY;
    }

    private static Label[] extractLabels(ChannelBox.ChannelDescription[] descriptions) {
        Label[] result = new Label[descriptions.length];
        for (int i = 0; i < descriptions.length; i++) {
            result[i] = descriptions[i].getLabel();
        }
        return result;
    }

    public static Label[] getLabelsByBitmap(long channelBitmap) {
        List<Label> result = new ArrayList<>();
        Label[] arr$ = Label.values();
        for (Label label : arr$) {
            if ((label.bitmapVal & channelBitmap) != 0) {
                result.add(label);
            }
        }
        return (Label[]) result.toArray(new Label[0]);
    }
}
