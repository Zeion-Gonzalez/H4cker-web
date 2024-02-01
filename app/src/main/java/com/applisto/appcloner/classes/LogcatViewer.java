package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.applisto.appcloner.classes.LogcatViewer;
import com.applisto.appcloner.classes.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;

/* loaded from: classes2.dex */
public class LogcatViewer {
    private static final String ACTION_SWITCH_LOGCAT_LEVEL = "com.applisto.appcloner.action.SWITCH_LOGCAT_LEVEL";
    private static final String EXTRA_LEVEL = "level";
    private static final int MAX_LINES = 1000;
    private static final String NOTIFICATION_CHANNEL_ID = "logcat_viewer";
    private static final String NOTIFICATION_CHANNEL_NAME = "Logcat viewer";
    private Context mContext;
    private Handler mHandler = new Handler();
    private String mLevel = "V";
    private final boolean mLogcatViewer;
    private Process mProcess;
    private static final List<String> sLines = Collections.synchronizedList(new ArrayList());
    private static boolean sAutoScroll = true;

    public LogcatViewer(CloneSettings cloneSettings) {
        this.mLogcatViewer = cloneSettings.getBoolean("logcatViewer", false).booleanValue();
        Log.m15i("ContentValues", "LogcatViewer; mLogcatViewer: " + this.mLogcatViewer);
    }

    /* JADX WARN: Type inference failed for: r7v1, types: [com.applisto.appcloner.classes.LogcatViewer$2] */
    public void install(Context context) {
        Log.m15i("ContentValues", "install; ");
        if (this.mLogcatViewer) {
            this.mContext = context;
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, 2));
                    }
                } catch (Throwable th) {
                    Log.m21w("ContentValues", th);
                }
            }
            startReadLogs();
            context.registerReceiver(new BroadcastReceiver() { // from class: com.applisto.appcloner.classes.LogcatViewer.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    LogcatViewer.this.mLevel = intent.getStringExtra(LogcatViewer.EXTRA_LEVEL);
                    boolean unused = LogcatViewer.sAutoScroll = true;
                    LogcatViewer.this.startReadLogs();
                }
            }, new IntentFilter(ACTION_SWITCH_LOGCAT_LEVEL));
            new Thread() { // from class: com.applisto.appcloner.classes.LogcatViewer.2
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    while (true) {
                        try {
                            synchronized (LogcatViewer.sLines) {
                                try {
                                    LogcatViewer.sLines.wait();
                                    LogcatViewer.this.showLogsDelayed();
                                } catch (Throwable th2) {
                                    throw th2;
                                }
                            }
                        } catch (InterruptedException unused) {
                        }
                    }
                }
            }.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.applisto.appcloner.classes.LogcatViewer$3] */
    public void startReadLogs() {
        Process process = this.mProcess;
        if (process != null) {
            process.destroy();
            this.mProcess = null;
        }
        new Thread() { // from class: com.applisto.appcloner.classes.LogcatViewer.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                LogcatViewer.this.readLogs();
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLogsDelayed() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$LogcatViewer$G1hxUXqz9ZrdlGGcqLP5MzIBnRk
            @Override // java.lang.Runnable
            public final void run() {
                LogcatViewer.this.showLogs();
            }
        }, 250L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLogs() {
        try {
            NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
            String logs = getLogs(5);
            Intent intent = new Intent(this.mContext, LogcatActivity.class);
            intent.setPackage(this.mContext.getPackageName());
            PendingIntent activity = PendingIntent.getActivity(this.mContext, 0, intent, 0);
            Intent intent2 = new Intent(ACTION_SWITCH_LOGCAT_LEVEL);
            intent2.putExtra(EXTRA_LEVEL, "V");
            intent2.setPackage(this.mContext.getPackageName());
            PendingIntent broadcast = PendingIntent.getBroadcast(this.mContext, 1, intent2, 0);
            Intent intent3 = new Intent(ACTION_SWITCH_LOGCAT_LEVEL);
            intent3.putExtra(EXTRA_LEVEL, "D");
            intent3.setPackage(this.mContext.getPackageName());
            PendingIntent broadcast2 = PendingIntent.getBroadcast(this.mContext, 2, intent3, 0);
            Intent intent4 = new Intent(ACTION_SWITCH_LOGCAT_LEVEL);
            intent4.putExtra(EXTRA_LEVEL, "I");
            intent4.setPackage(this.mContext.getPackageName());
            PendingIntent broadcast3 = PendingIntent.getBroadcast(this.mContext, 3, intent4, 0);
            Notification.Builder autoCancel = new Notification.Builder(this.mContext).setContentIntent(activity).setStyle(new Notification.BigTextStyle().bigText(logs)).setPriority(2).setAutoCancel(false);
            Utils.setSmallNotificationIcon(autoCancel);
            if (Build.VERSION.SDK_INT >= 26) {
                autoCancel.setChannelId(NOTIFICATION_CHANNEL_ID);
            }
            autoCancel.addAction(new Notification.Action(0, "VERBOSE", broadcast));
            autoCancel.addAction(new Notification.Action(0, "DEBUG", broadcast2));
            autoCancel.addAction(new Notification.Action(0, "INFO", broadcast3));
            Notification notification = autoCancel.getNotification();
            notification.sound = null;
            notification.defaults &= -2;
            notificationManager.notify(354432, notification);
        } catch (Exception e) {
            Log.m21w("ContentValues", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getLogs(int i) {
        StringBuilder sb = new StringBuilder();
        synchronized (sLines) {
            for (String str : sLines.subList(Math.max(sLines.size() - i, 0), sLines.size())) {
                if (sb.length() > 0) {
                    sb.append(IOUtils.LINE_SEPARATOR_UNIX);
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }

    public void readLogs() {
        try {
            synchronized (sLines) {
                sLines.clear();
                sLines.notifyAll();
            }
            this.mProcess = Runtime.getRuntime().exec("logcat -v time *:" + this.mLevel);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.mProcess.getInputStream()));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        synchronized (sLines) {
                            if (!readLine.startsWith("--------- beginning of") && !readLine.contains("OverScrollerOptimization")) {
                                sLines.add(readLine);
                                if (sLines.size() > 1000) {
                                    sLines.remove(0);
                                }
                                sLines.notifyAll();
                            }
                        }
                    } else {
                        bufferedReader.close();
                        return;
                    }
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (IOException unused) {
                    }
                    throw th;
                }
            }
        } catch (IOException | Exception unused2) {
        }
    }

    /* loaded from: classes2.dex */
    public static class LogcatActivity extends Activity {
        private Handler mHandler = new Handler();
        private boolean mPaused;
        private ScrollView mScrollView;
        private TextView mTextView;
        private Thread mThread;

        @Override // android.app.Activity
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setTitle("Logcat of " + Utils.getAppName(this));
            this.mTextView = new TextView(this);
            this.mTextView.setTypeface(Typeface.create("monospace", 0));
            this.mTextView.setTextSize(12.0f);
            this.mTextView.setTextColor(-657931);
            this.mTextView.setBackgroundColor(-15906911);
            int dp2px = Utils.dp2px(this, 16.0f);
            this.mTextView.setPadding(dp2px, 0, dp2px, 0);
            this.mTextView.setOnTouchListener(new View.OnTouchListener() { // from class: com.applisto.appcloner.classes.LogcatViewer.LogcatActivity.1
                private GestureDetector gestureDetector;

                {
                    this.gestureDetector = new GestureDetector(LogcatActivity.this, new GestureDetector.SimpleOnGestureListener() { // from class: com.applisto.appcloner.classes.LogcatViewer.LogcatActivity.1.1
                        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                        public boolean onDoubleTap(MotionEvent motionEvent) {
                            LogcatActivity.this.mPaused = !LogcatActivity.this.mPaused;
                            if (LogcatActivity.this.mPaused) {
                                Toast.makeText(LogcatActivity.this, "Paused", 0).show();
                            } else {
                                Toast.makeText(LogcatActivity.this, "Resumed", 0).show();
                                LogcatActivity.this.showLogsDelayed();
                            }
                            return super.onDoubleTap(motionEvent);
                        }
                    });
                }

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    this.gestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
            Toast.makeText(this, "Double-tap to pause", 1).show();
            this.mScrollView = new ScrollView(this) { // from class: com.applisto.appcloner.classes.LogcatViewer.LogcatActivity.2
                @Override // android.view.View
                protected void onScrollChanged(int i, int i2, int i3, int i4) {
                    super.onScrollChanged(i, i2, i3, i4);
                    boolean unused = LogcatViewer.sAutoScroll = false;
                }
            };
            this.mScrollView.setScrollbarFadingEnabled(false);
            this.mScrollView.addView(this.mTextView, new FrameLayout.LayoutParams(-1, -2));
            setContentView(this.mScrollView);
            showLogs();
            this.mThread = new Thread() { // from class: com.applisto.appcloner.classes.LogcatViewer.LogcatActivity.3
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    while (LogcatActivity.this.mThread != null) {
                        try {
                            synchronized (LogcatViewer.sLines) {
                                LogcatViewer.sLines.wait();
                                LogcatActivity.this.showLogsDelayed();
                            }
                        } catch (InterruptedException unused) {
                            return;
                        }
                    }
                }
            };
            this.mThread.start();
        }

        @Override // android.app.Activity
        protected void onDestroy() {
            super.onDestroy();
            Thread thread = this.mThread;
            if (thread != null) {
                thread.interrupt();
                this.mThread = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showLogsDelayed() {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$LogcatViewer$LogcatActivity$mk6zMzpU5MiXY_P8Lp3VUDktEtg
                @Override // java.lang.Runnable
                public final void run() {
                    LogcatViewer.LogcatActivity.this.showLogs();
                }
            }, 100L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showLogs() {
            if (this.mPaused) {
                return;
            }
            this.mTextView.setText(LogcatViewer.getLogs(1000));
            this.mTextView.requestLayout();
            if (LogcatViewer.sAutoScroll) {
                this.mScrollView.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$LogcatViewer$LogcatActivity$ve2uQj2bLcgrw9yciAanoOFCrMk
                    @Override // java.lang.Runnable
                    public final void run() {
                        LogcatViewer.LogcatActivity.this.lambda$showLogs$0$LogcatViewer$LogcatActivity();
                    }
                }, 750L);
            }
        }

        public /* synthetic */ void lambda$showLogs$0$LogcatViewer$LogcatActivity() {
            this.mScrollView.fullScroll(130);
            Log.m15i("ContentValues", "run; scrolled");
        }
    }
}
