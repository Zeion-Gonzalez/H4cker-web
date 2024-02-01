package com.instabug.library;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.internal.view.SupportMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import com.instabug.library.internal.media.AudioPlayer;
import com.instabug.library.internal.media.C0664a;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.PermissionsUtils;
import com.instabug.library.util.PlaceHolderUtils;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class InstabugAudioRecordingFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private static final String EXTRA_FILE_PATH = "com.instabug.library.audio_attachment_path";
    private Callbacks listener;
    private PorterDuffColorFilter mColorFilter;
    private int mCurrentTime;
    private boolean mIsRecordingPermissionGranted;
    private ImageView mRecordButtonBackground;
    private ImageView mRecordButtonIcon;
    private C0664a mRecorder;
    private Timer mRecordingTimer;
    private TextView timerTextView;
    private TextView titleTextView;
    private boolean mIsRecordingActive = false;
    private TimerTask mTimerTask = new C0576a();
    private boolean isFirstPermissionRequested = false;

    /* loaded from: classes.dex */
    public interface Callbacks {
        void onAudioRecorded(String str, String str2);
    }

    static /* synthetic */ int access$508(InstabugAudioRecordingFragment instabugAudioRecordingFragment) {
        int i = instabugAudioRecordingFragment.mCurrentTime;
        instabugAudioRecordingFragment.mCurrentTime = i + 1;
        return i;
    }

    public static InstabugAudioRecordingFragment newInstance(String str) {
        InstabugAudioRecordingFragment instabugAudioRecordingFragment = new InstabugAudioRecordingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FILE_PATH, str);
        instabugAudioRecordingFragment.setArguments(bundle);
        return instabugAudioRecordingFragment;
    }

    public static InstabugAudioRecordingFragment newInstance(String str, Callbacks callbacks) {
        InstabugAudioRecordingFragment newInstance = newInstance(str);
        newInstance.listener = callbacks;
        return newInstance;
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mIsRecordingPermissionGranted = false;
        this.mRecordingTimer = new Timer();
        this.mRecorder = new C0664a(getArguments().getString(EXTRA_FILE_PATH));
        View inflate = layoutInflater.inflate(C0577R.layout.instabug_lyt_record_audio, viewGroup, false);
        inflate.findViewById(C0577R.id.instabug_btn_record_audio).setOnTouchListener(this);
        this.mRecordButtonBackground = (ImageView) inflate.findViewById(C0577R.id.instabug_bk_record_audio);
        this.mRecordButtonIcon = (ImageView) inflate.findViewById(C0577R.id.instabug_img_record_audio);
        this.mColorFilter = new PorterDuffColorFilter(SettingsManager.getInstance().getPrimaryColor(), PorterDuff.Mode.SRC_IN);
        this.mRecordButtonBackground.setColorFilter(this.mColorFilter);
        this.mRecordButtonIcon.setColorFilter(this.mColorFilter);
        this.timerTextView = (TextView) inflate.findViewById(C0577R.id.instabug_txt_timer);
        this.timerTextView.setTextColor(Instabug.getPrimaryColor());
        this.timerTextView.setText(String.format("00:%02d", 0));
        this.titleTextView = (TextView) inflate.findViewById(C0577R.id.instabug_txt_recording_title);
        this.titleTextView.setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.VOICE_MESSAGE_PRESS_AND_HOLD_TO_RECORD, getString(C0577R.string.instabug_str_hold_to_record)));
        inflate.findViewById(C0577R.id.instabug_recording_audio_dialog_container).setOnClickListener(this);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        requestAudioRecordingPermission();
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    private void requestAudioRecordingPermission() {
        PermissionsUtils.requestPermission(this, "android.permission.RECORD_AUDIO", 1, new Runnable() { // from class: com.instabug.library.InstabugAudioRecordingFragment.1
            @Override // java.lang.Runnable
            public void run() {
                if (!InstabugAudioRecordingFragment.this.isFirstPermissionRequested) {
                    InstabugAudioRecordingFragment.this.isFirstPermissionRequested = true;
                    return;
                }
                InstabugSDKLogger.m1799d(InstabugAudioRecordingFragment.this, "Shouldn't try to explain why get this permission, either first time or never again selected OR permission not in manifest");
                Toast.makeText(InstabugAudioRecordingFragment.this.getContext(), InstabugAudioRecordingFragment.this.getPermissionDeniedMessage(), 0).show();
                Instabug.setShouldAudioRecordingOptionAppear(false);
            }
        }, new Runnable() { // from class: com.instabug.library.InstabugAudioRecordingFragment.2
            @Override // java.lang.Runnable
            public void run() {
                InstabugSDKLogger.m1799d(InstabugAudioRecordingFragment.this, "Audio recording permission already granted before");
                InstabugAudioRecordingFragment.this.mIsRecordingPermissionGranted = true;
            }
        });
    }

    @Override // android.support.v4.app.Fragment
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        switch (i) {
            case 1:
                if (iArr.length > 0 && iArr[0] == 0) {
                    InstabugSDKLogger.m1799d(this, "Audio recording permission granted by user");
                    this.mIsRecordingPermissionGranted = true;
                    return;
                } else {
                    InstabugSDKLogger.m1799d(this, "Audio recording permission denied by user");
                    this.mIsRecordingPermissionGranted = false;
                    Toast.makeText(getContext(), getPermissionDeniedMessage(), 0).show();
                    return;
                }
            default:
                super.onRequestPermissionsResult(i, strArr, iArr);
                return;
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                if (!this.mIsRecordingActive && this.mIsRecordingPermissionGranted) {
                    this.mCurrentTime = 0;
                    this.mRecordingTimer.scheduleAtFixedRate(this.mTimerTask, 0L, 1000L);
                    this.mRecorder.m1293a();
                    this.mIsRecordingActive = true;
                    setStateActive();
                    this.titleTextView.setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.VOICE_MESSAGE_RELEASE_TO_ATTACH, getString(C0577R.string.instabug_str_release_stop_record)));
                }
                if (!this.mIsRecordingPermissionGranted) {
                    requestAudioRecordingPermission();
                    break;
                }
                break;
            case 1:
            case 3:
                if (this.mIsRecordingActive && this.mIsRecordingPermissionGranted && stopRecording() && motionEvent.getAction() == 1) {
                    finishRecording();
                    break;
                }
                break;
        }
        return true;
    }

    private void finishRecording() {
        if (this.listener != null) {
            final AudioPlayer audioPlayer = new AudioPlayer();
            audioPlayer.getDurationAsync(getArguments().getString(EXTRA_FILE_PATH), new AudioPlayer.InterfaceC0663b() { // from class: com.instabug.library.InstabugAudioRecordingFragment.3
                @Override // com.instabug.library.internal.media.AudioPlayer.InterfaceC0663b
                /* renamed from: a */
                public void mo976a(int i) {
                    audioPlayer.release();
                    InstabugAudioRecordingFragment.this.listener.onAudioRecorded(InstabugAudioRecordingFragment.this.getArguments().getString(InstabugAudioRecordingFragment.EXTRA_FILE_PATH), AudioPlayer.getFormattedDurationText(i));
                    InstabugAudioRecordingFragment.this.removeCurrentFragment();
                }
            });
        }
    }

    private boolean stopRecording() {
        try {
            this.mTimerTask.cancel();
            this.mRecordingTimer.cancel();
            this.mTimerTask = new C0576a();
            this.mRecordingTimer = new Timer();
            this.mIsRecordingActive = false;
            setStateInactive();
            this.timerTextView.setText(String.format("00:%02d", 0));
            this.titleTextView.setText(PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.VOICE_MESSAGE_PRESS_AND_HOLD_TO_RECORD, getString(C0577R.string.instabug_str_hold_to_record)));
            this.mRecorder.m1294b();
            return this.mCurrentTime > 3;
        } catch (RuntimeException e) {
            if (this.mCurrentTime > 1) {
                Toast.makeText(getActivity(), "Unknown error occurred", 0).show();
            }
            InstabugSDKLogger.m1801e(this, "Error capturing audio stream", e);
            return false;
        }
    }

    private void setStateActive() {
        this.mRecordButtonBackground.setImageResource(C0577R.drawable.instabug_bg_active_record);
        Colorizer.applyTint(SupportMenu.CATEGORY_MASK, this.mRecordButtonBackground);
        this.mRecordButtonIcon.setColorFilter((ColorFilter) null);
        this.timerTextView.setTextColor(SupportMenu.CATEGORY_MASK);
    }

    private void setStateInactive() {
        this.mRecordButtonBackground.setImageResource(C0577R.drawable.instabug_bg_default_record);
        this.mRecordButtonBackground.setColorFilter(this.mColorFilter);
        this.mRecordButtonIcon.setColorFilter(this.mColorFilter);
        this.timerTextView.setTextColor(Instabug.getPrimaryColor());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
            getFragmentManager().popBackStack();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.InstabugAudioRecordingFragment$a */
    /* loaded from: classes.dex */
    public class C0576a extends TimerTask {
        C0576a() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            InstabugAudioRecordingFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.instabug.library.InstabugAudioRecordingFragment.a.1
                @Override // java.lang.Runnable
                public void run() {
                    if (InstabugAudioRecordingFragment.this.mCurrentTime >= 50) {
                        InstabugAudioRecordingFragment.this.timerTextView.setTextColor(Instabug.getPrimaryColor());
                    } else {
                        InstabugAudioRecordingFragment.this.timerTextView.setTextColor(SupportMenu.CATEGORY_MASK);
                    }
                    if (InstabugAudioRecordingFragment.this.mCurrentTime == 60) {
                        InstabugAudioRecordingFragment.this.onTimeIsUp();
                    }
                    InstabugAudioRecordingFragment.this.timerTextView.setText(AudioPlayer.getFormattedDurationText(InstabugAudioRecordingFragment.this.mCurrentTime * 1000));
                    InstabugAudioRecordingFragment.access$508(InstabugAudioRecordingFragment.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPermissionDeniedMessage() {
        return PlaceHolderUtils.getPlaceHolder(InstabugCustomTextPlaceHolder.Key.AUDIO_RECORDING_PERMISSION_DENIED, getString(C0577R.string.instabug_audio_recorder_permission_denied));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeIsUp() {
        finishRecording();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeCurrentFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getFragmentManager().popBackStack("Record Audio", 1);
    }
}
