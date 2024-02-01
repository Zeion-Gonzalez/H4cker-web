package org.jcodec.containers.mp4.boxes.channel;

/* loaded from: classes.dex */
public enum ChannelLayout {
    kCAFChannelLayoutTag_UseChannelDescriptions(0, new Label[0]),
    kCAFChannelLayoutTag_UseChannelBitmap(65536, new Label[0]),
    kCAFChannelLayoutTag_Mono(6553601, Label.Mono),
    kCAFChannelLayoutTag_Stereo(6619138, Label.Left, Label.Right),
    kCAFChannelLayoutTag_StereoHeadphones(6684674, Label.HeadphonesLeft, Label.HeadphonesRight),
    kCAFChannelLayoutTag_MatrixStereo(6750210, Label.LeftTotal, Label.RightTotal),
    kCAFChannelLayoutTag_MidSide(6815746, Label.MS_Mid, Label.MS_Side),
    kCAFChannelLayoutTag_XY(6881282, Label.XY_X, Label.XY_Y),
    kCAFChannelLayoutTag_Binaural(6946818, Label.HeadphonesLeft, Label.HeadphonesRight),
    kCAFChannelLayoutTag_Ambisonic_B_Format(7012356, Label.Ambisonic_W, Label.Ambisonic_X, Label.Ambisonic_Y, Label.Ambisonic_Z),
    kCAFChannelLayoutTag_Quadraphonic(7077892, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_Pentagonal(7143429, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center),
    kCAFChannelLayoutTag_Hexagonal(7208966, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center, Label.CenterSurround),
    kCAFChannelLayoutTag_Octagonal(7274504, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center, Label.CenterSurround, Label.LeftCenter, Label.RightCenter),
    kCAFChannelLayoutTag_Cube(7340040, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.TopBackLeft, Label.TopBackRight, Label.TopBackCenter, Label.TopCenterSurround),
    kCAFChannelLayoutTag_MPEG_3_0_A(7405571, Label.Left, Label.Right, Label.Center),
    kCAFChannelLayoutTag_MPEG_3_0_B(7471107, Label.Center, Label.Left, Label.Right),
    kCAFChannelLayoutTag_MPEG_4_0_A(7536644, Label.Left, Label.Right, Label.Center, Label.CenterSurround),
    kCAFChannelLayoutTag_MPEG_4_0_B(7602180, Label.Center, Label.Left, Label.Right, Label.CenterSurround),
    kCAFChannelLayoutTag_MPEG_5_0_A(7667717, Label.Left, Label.Right, Label.Center, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_MPEG_5_0_B(7733253, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center),
    kCAFChannelLayoutTag_MPEG_5_0_C(7798789, Label.Left, Label.Center, Label.Right, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_MPEG_5_0_D(7864325, Label.Center, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_MPEG_5_1_A(7929862, Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_MPEG_5_1_B(7995398, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center, Label.LFEScreen),
    kCAFChannelLayoutTag_MPEG_5_1_C(8060934, Label.Left, Label.Center, Label.Right, Label.LeftSurround, Label.RightSurround, Label.LFEScreen),
    kCAFChannelLayoutTag_MPEG_5_1_D(8126470, Label.Center, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.LFEScreen),
    kCAFChannelLayoutTag_MPEG_6_1_A(8192007, Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.LeftSurround, Label.RightSurround, Label.Right),
    kCAFChannelLayoutTag_MPEG_7_1_A(8257544, Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.LeftSurround, Label.RightSurround, Label.LeftCenter, Label.RightCenter),
    kCAFChannelLayoutTag_MPEG_7_1_B(8323080, Label.Center, Label.LeftCenter, Label.RightCenter, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.LFEScreen),
    kCAFChannelLayoutTag_MPEG_7_1_C(8388616, Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.LeftSurround, Label.RightSurround, Label.RearSurroundLeft, Label.RearSurroundRight),
    kCAFChannelLayoutTag_Emagic_Default_7_1(8454152, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center, Label.LFEScreen, Label.LeftCenter, Label.RightCenter),
    kCAFChannelLayoutTag_SMPTE_DTV(8519688, Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.LeftSurround, Label.RightSurround, Label.LeftTotal, Label.RightTotal),
    kCAFChannelLayoutTag_ITU_2_1(8585219, Label.Left, Label.Right, Label.CenterSurround),
    kCAFChannelLayoutTag_ITU_2_2(8650756, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_DVD_4(8716291, Label.Left, Label.Right, Label.LFEScreen),
    kCAFChannelLayoutTag_DVD_5(8781828, Label.Left, Label.Right, Label.LFEScreen, Label.CenterSurround),
    kCAFChannelLayoutTag_DVD_6(8847365, Label.Left, Label.Right, Label.LFEScreen, Label.LeftSurround, Label.RightSurround),
    kCAFChannelLayoutTag_DVD_10(8912900, Label.Left, Label.Right, Label.Center, Label.LFEScreen),
    kCAFChannelLayoutTag_DVD_11(8978437, Label.Left, Label.Right, Label.Center, Label.LFEScreen, Label.CenterSurround),
    kCAFChannelLayoutTag_DVD_18(9043973, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.LFEScreen),
    kCAFChannelLayoutTag_AudioUnit_6_0(9109510, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center, Label.CenterSurround),
    kCAFChannelLayoutTag_AudioUnit_7_0(9175047, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.Center, Label.RearSurroundLeft, Label.RearSurroundRight),
    kCAFChannelLayoutTag_AAC_6_0(9240582, Label.Center, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.CenterSurround),
    kCAFChannelLayoutTag_AAC_6_1(9306119, Label.Center, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.CenterSurround, Label.LFEScreen),
    kCAFChannelLayoutTag_AAC_7_0(9371655, Label.Center, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.RearSurroundLeft, Label.RearSurroundRight),
    kCAFChannelLayoutTag_AAC_Octagonal(9437192, Label.Center, Label.Left, Label.Right, Label.LeftSurround, Label.RightSurround, Label.RearSurroundLeft, Label.RearSurroundRight, Label.CenterSurround),
    kCAFChannelLayoutTag_TMH_10_2_std(9502736, Label.Left, Label.Right, Label.Center, Label.Mono, Label.Mono, Label.Mono, Label.LeftSurround, Label.RightSurround, Label.Mono, Label.Mono, Label.Mono, Label.Mono, Label.Mono, Label.CenterSurround, Label.LFEScreen, Label.LFE2),
    kCAFChannelLayoutTag_TMH_10_2_full(9568277, Label.LeftCenter, Label.RightCenter, Label.Mono, Label.Mono, Label.Mono),
    kCAFChannelLayoutTag_RESERVED_DO_NOT_USE(9633792, new Label[0]);

    private int code;
    private Label[] labels;

    ChannelLayout(int code, Label... labels) {
        this.code = code;
        this.labels = labels;
    }

    public int getCode() {
        return this.code;
    }

    public Label[] getLabels() {
        return this.labels;
    }
}
