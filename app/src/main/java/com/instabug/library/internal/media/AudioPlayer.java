package com.instabug.library.internal.media;

import android.media.MediaPlayer;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class AudioPlayer {
    private String filePath;
    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener onCompletionListener;
    private InterfaceC0663b onGetDurationListener;
    private Map<String, OnStopListener> onStopListeners = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.instabug.library.internal.media.AudioPlayer$a */
    /* loaded from: classes.dex */
    public enum EnumC0662a {
        START,
        PAUSE,
        GET_DURATION
    }

    /* renamed from: com.instabug.library.internal.media.AudioPlayer$b */
    /* loaded from: classes.dex */
    public interface InterfaceC0663b {
        /* renamed from: a */
        void mo976a(int i);
    }

    private void prepare(final EnumC0662a enumC0662a) {
        try {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setDataSource(this.filePath);
            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.instabug.library.internal.media.AudioPlayer.1
                @Override // android.media.MediaPlayer.OnPreparedListener
                public void onPrepared(MediaPlayer mediaPlayer) {
                    AudioPlayer.this.doAction(enumC0662a);
                }
            });
            this.mediaPlayer.prepareAsync();
            if (this.onCompletionListener != null) {
                this.mediaPlayer.setOnCompletionListener(this.onCompletionListener);
            }
        } catch (IOException e) {
            InstabugSDKLogger.m1801e(this, "Playing audio file failed", e);
        }
    }

    public void addOnStopListener(OnStopListener onStopListener) {
        this.onStopListeners.put(onStopListener.getFilePath(), onStopListener);
        if (this.onCompletionListener == null) {
            this.onCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.instabug.library.internal.media.AudioPlayer.2
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer) {
                    AudioPlayer.this.notifyOnStopListeners();
                }
            };
            if (this.mediaPlayer != null) {
                this.mediaPlayer.setOnCompletionListener(this.onCompletionListener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOnStopListeners() {
        Iterator<OnStopListener> it = this.onStopListeners.values().iterator();
        while (it.hasNext()) {
            it.next().onStop();
        }
    }

    private void stream(String str, EnumC0662a enumC0662a) {
        if (str == null) {
            InstabugSDKLogger.m1800e(this, "Audio file path can not be null");
        } else if (str.equals(this.filePath)) {
            doAction(enumC0662a);
        } else {
            this.filePath = str;
            prepare(enumC0662a);
        }
    }

    public void getDurationAsync(String str, InterfaceC0663b interfaceC0663b) {
        stream(str, EnumC0662a.GET_DURATION);
        this.onGetDurationListener = interfaceC0663b;
    }

    public void start(String str) {
        pause();
        stream(str, EnumC0662a.START);
    }

    public void pause() {
        notifyOnStopListeners();
        doAction(EnumC0662a.PAUSE);
    }

    public void release() {
        this.filePath = null;
        if (this.mediaPlayer != null) {
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
            }
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAction(EnumC0662a enumC0662a) {
        switch (enumC0662a) {
            case START:
                if (!this.mediaPlayer.isPlaying()) {
                    this.mediaPlayer.start();
                    return;
                }
                return;
            case PAUSE:
                if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
                    this.mediaPlayer.pause();
                    return;
                }
                return;
            case GET_DURATION:
                this.onGetDurationListener.mo976a(this.mediaPlayer.getDuration());
                return;
            default:
                return;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class OnStopListener {
        private String filePath;

        public abstract void onStop();

        public String getFilePath() {
            return this.filePath;
        }

        public OnStopListener(String str) {
            this.filePath = str;
        }
    }

    public static String getFormattedDurationText(long j) {
        int i = (int) (j / 3600000);
        int i2 = (int) ((j % 3600000) / 60000);
        int i3 = (int) ((j % 60000) / 1000);
        return i > 0 ? String.format("%02d:%02d:%02d", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)) : String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3));
    }
}
