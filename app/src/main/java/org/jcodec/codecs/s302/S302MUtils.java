package org.jcodec.codecs.s302;

import org.jcodec.common.model.ChannelLabel;

/* loaded from: classes.dex */
public class S302MUtils {
    public static String name(int channels) {
        switch (channels) {
            case 1:
                return "Mono";
            case 2:
                return "Stereo 2.0";
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                return null;
            case 4:
                return "Surround 4.0";
            case 8:
                return "Stereo 2.0 + Surround 5.1";
        }
    }

    public static ChannelLabel[] labels(int channels) {
        switch (channels) {
            case 1:
                return new ChannelLabel[]{ChannelLabel.MONO};
            case 2:
                return new ChannelLabel[]{ChannelLabel.STEREO_LEFT, ChannelLabel.STEREO_RIGHT};
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                return null;
            case 4:
                return new ChannelLabel[]{ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT};
            case 8:
                return new ChannelLabel[]{ChannelLabel.STEREO_LEFT, ChannelLabel.STEREO_RIGHT, ChannelLabel.FRONT_LEFT, ChannelLabel.FRONT_RIGHT, ChannelLabel.REAR_LEFT, ChannelLabel.REAR_RIGHT, ChannelLabel.CENTER, ChannelLabel.LFE};
        }
    }
}
