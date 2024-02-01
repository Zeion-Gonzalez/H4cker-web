package com.instabug.library.internal.video;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;
import com.instabug.library.C0577R;
import com.instabug.library.C0629b;
import com.instabug.library.Feature;
import com.instabug.library.InstabugBaseFragment;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PlaceHolderUtils;

/* loaded from: classes.dex */
public class VideoPlayerFragment extends InstabugBaseFragment {
    public static final String TAG = "video_player_fragment";
    public static final String VIDEO_PATH = "video.uri";
    private MediaController mediaControls;
    private int position = 0;
    private ProgressDialog progressDialog;
    private String videoUri;
    private VideoView videoView;

    public static VideoPlayerFragment newInstance(String str) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VIDEO_PATH, str);
        videoPlayerFragment.setArguments(bundle);
        return videoPlayerFragment;
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected int getLayout() {
        return C0577R.layout.instabug_lyt_video_view;
    }

    @Override // com.instabug.library.InstabugBaseFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.videoView = (VideoView) view.findViewById(C0577R.id.video_view);
        if (C0629b.m1160a().m1170b(Feature.WHITE_LABELING) == Feature.State.DISABLED) {
            getActivity().findViewById(C0577R.id.instabug_pbi_footer).setVisibility(8);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(@Nullable Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mediaControls == null) {
            this.mediaControls = new MediaController(getActivity());
        }
        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage("Loading...");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
        try {
            this.videoView.setMediaController(this.mediaControls);
            this.videoView.setVideoURI(Uri.parse(this.videoUri));
        } catch (Exception e) {
            InstabugSDKLogger.m1801e(this, e.getMessage(), e);
        }
        this.videoView.requestFocus();
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.instabug.library.internal.video.VideoPlayerFragment.1
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                VideoPlayerFragment.this.progressDialog.dismiss();
                VideoPlayerFragment.this.videoView.seekTo(VideoPlayerFragment.this.position);
                if (VideoPlayerFragment.this.position == 0) {
                    VideoPlayerFragment.this.videoView.start();
                } else {
                    VideoPlayerFragment.this.videoView.pause();
                }
            }
        });
        this.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.instabug.library.internal.video.VideoPlayerFragment.2
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VideoPlayerFragment.this.progressDialog.dismiss();
                return false;
            }
        });
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected String getTitle() {
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.VIDEO_PLAYER_TITLE, getString(C0577R.string.instabug_str_video_player));
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected void consumeNewInstanceSavedArguments() {
        this.videoUri = getArguments().getString(VIDEO_PATH);
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected void restoreState(Bundle bundle) {
        this.position = bundle.getInt("Position");
        this.videoView.seekTo(this.position);
    }

    @Override // com.instabug.library.InstabugBaseFragment
    protected void saveState(Bundle bundle) {
        bundle.putInt("Position", this.videoView.getCurrentPosition());
        this.videoView.pause();
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (C0629b.m1160a().m1170b(Feature.WHITE_LABELING) == Feature.State.DISABLED) {
            getActivity().findViewById(C0577R.id.instabug_pbi_footer).setVisibility(0);
        }
    }
}
