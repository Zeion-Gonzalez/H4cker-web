package com.instabug.library.internal.video;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.MotionEvent;
import com.instabug.library.C0634c;
import com.instabug.library.Instabug;
import com.instabug.library.internal.media.C0664a;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.internal.storage.DiskUtils;
import com.instabug.library.invocation.C0704b;
import com.instabug.library.screenshot.C0735a;
import com.instabug.library.tracking.InstabugInternalTrackingDelegate;
import com.instabug.library.util.C0755c;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;

/* compiled from: VideoFrameSnapper.java */
@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
/* renamed from: com.instabug.library.internal.video.c */
/* loaded from: classes.dex */
public class C0688c {

    /* renamed from: a */
    private static C0688c f901a;

    /* renamed from: b */
    private a f902b;

    /* renamed from: c */
    private boolean f903c = false;

    /* renamed from: d */
    private Handler f904d = new Handler();

    /* renamed from: e */
    private Runnable f905e = new Runnable() { // from class: com.instabug.library.internal.video.c.1
        @Override // java.lang.Runnable
        public void run() {
            InstabugSDKLogger.m1802i(this, "Time limit has been reached, stopping recording");
            InternalScreenRecordHelper.getInstance().stop();
        }
    };

    /* renamed from: f */
    private C0664a f906f;

    /* renamed from: g */
    private String f907g;

    /* compiled from: VideoFrameSnapper.java */
    /* renamed from: com.instabug.library.internal.video.c$b */
    /* loaded from: classes.dex */
    public interface b {
        void onFramesCapturingFinished(String str);
    }

    /* renamed from: a */
    public static C0688c m1357a() {
        if (f901a == null) {
            f901a = new C0688c();
        }
        return f901a;
    }

    /* renamed from: a */
    public void m1359a(b bVar) {
        this.f902b = new a(bVar);
        this.f902b.execute(new Void[0]);
        m1360a(true);
        this.f904d.postDelayed(this.f905e, 30000L);
        if (C0755c.m1809a()) {
            InstabugSDKLogger.m1802i(this, "Audio permission granted");
            this.f907g = new File(DiskUtils.getInstabugDirectory(Instabug.getApplicationContext()), "audioMessage_" + String.valueOf(System.currentTimeMillis()) + ".mp4").getAbsolutePath();
            this.f906f = new C0664a(this.f907g);
            C0755c.m1808a(Instabug.getApplicationContext());
            this.f906f.m1293a();
            return;
        }
        InstabugSDKLogger.m1800e(this, "Audio permission is not granted");
    }

    /* renamed from: b */
    public void m1362b() {
        if (this.f902b != null) {
            this.f902b.m1374a();
            m1360a(false);
            this.f904d.removeCallbacks(this.f905e);
            if (this.f906f != null) {
                this.f906f.m1294b();
            }
        }
    }

    /* renamed from: c */
    public void m1363c() {
        InstabugSDKLogger.m1802i(this, "Frame Snapper: " + String.valueOf(this.f902b != null));
        if (this.f902b != null) {
            this.f902b.cancel(true);
            this.f902b.m1374a();
            m1360a(false);
            this.f904d.removeCallbacks(this.f905e);
            if (this.f906f != null) {
                this.f906f.m1294b();
            }
            new File(this.f907g).delete();
        }
    }

    /* renamed from: d */
    public boolean m1364d() {
        return this.f903c;
    }

    /* renamed from: a */
    public void m1360a(boolean z) {
        this.f903c = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: VideoFrameSnapper.java */
    /* renamed from: com.instabug.library.internal.video.c$a */
    /* loaded from: classes.dex */
    public class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: c */
        private b f911c;

        /* renamed from: b */
        private boolean f910b = false;

        /* renamed from: e */
        private C0634c.a[] f913e = null;

        /* renamed from: d */
        private Handler f912d = new Handler();

        public a(b bVar) {
            this.f911c = bVar;
        }

        @Override // android.os.AsyncTask
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public Void doInBackground(Void... voidArr) {
            InstabugSDKLogger.m1802i(this, "Video recording status: " + String.valueOf(isCancelled()));
            if (!isCancelled()) {
                this.f912d.post(new Runnable() { // from class: com.instabug.library.internal.video.c.a.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (a.this.f910b) {
                            a.this.f912d.removeCallbacks(this);
                            if (!a.this.isCancelled()) {
                                a.this.f911c.onFramesCapturingFinished(C0688c.this.f907g);
                                return;
                            } else {
                                a.this.onCancelled();
                                return;
                            }
                        }
                        a.this.f913e = C0634c.m1186a().m1188b();
                        C0688c.this.m1361a(a.this.f913e);
                        a.this.m1370c();
                        if (!a.this.isCancelled()) {
                            a.this.f912d.postDelayed(this, 125L);
                        }
                    }
                });
                return null;
            }
            return null;
        }

        /* renamed from: a */
        public void m1374a() {
            this.f910b = true;
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            super.onCancelled();
            InstabugSDKLogger.m1802i(this, "Cancelling video recording");
            m1368b();
            C0704b.m1513c().m1521a((MotionEvent) null);
            C0704b.m1513c().m1526g();
        }

        /* renamed from: b */
        private void m1368b() {
            File[] listFiles = AttachmentsUtility.getVideoRecordingFramesDirectory(Instabug.getApplicationContext()).listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    file.delete();
                }
            }
            InstabugSDKLogger.m1799d(this, "Video frames are removed");
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: c */
        public void m1370c() {
            this.f913e = null;
        }
    }

    /* renamed from: a */
    public void m1361a(C0634c.a[] aVarArr) {
        C0735a.m1647a(InstabugInternalTrackingDelegate.getInstance().getTargetActivity(), aVarArr);
    }
}
