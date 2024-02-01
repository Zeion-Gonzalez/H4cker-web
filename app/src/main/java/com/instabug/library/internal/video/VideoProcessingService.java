package com.instabug.library.internal.video;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import com.instabug.library.C0577R;
import com.instabug.library.core.eventbus.ScreenRecordingEventBus;
import com.instabug.library.core.eventbus.VideoProcessingServiceEventBus;
import com.instabug.library.internal.storage.AttachmentsUtility;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugSDKLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import p045rx.functions.Action1;

@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
/* loaded from: classes.dex */
public class VideoProcessingService extends IntentService {

    /* renamed from: a */
    private Action f871a;

    /* loaded from: classes.dex */
    public enum Action {
        STOP
    }

    public VideoProcessingService() {
        super("VideoProcessingService");
    }

    /* renamed from: a */
    public static void m1338a(Context context, String str, String str2) {
        Intent intent = new Intent(context, VideoProcessingService.class);
        intent.putExtra("video.file.path", str);
        intent.putExtra("audio.file.path", str2);
        context.startService(intent);
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        VideoProcessingServiceEventBus.getInstance().subscribe(new Action1<Action>() { // from class: com.instabug.library.internal.video.VideoProcessingService.1
            @Override // p045rx.functions.Action1
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void call(Action action) {
                VideoProcessingService.this.f871a = action;
                InstabugSDKLogger.m1799d(VideoProcessingService.this, action.name() + " action is received");
            }
        });
        String stringExtra = intent.getStringExtra("video.file.path");
        String stringExtra2 = intent.getStringExtra("audio.file.path");
        try {
            InstabugSDKLogger.m1799d(this, "making video");
            m1340a(true);
            m1339a(stringExtra, stringExtra2);
        } catch (IOException e) {
            e.printStackTrace();
            InstabugSDKLogger.m1804w(this, "Couldn't encode video");
            m1337a();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            InstabugSDKLogger.m1804w(this, "IllegalAccessException - Couldn't encode video");
            m1337a();
        } catch (OutOfMemoryError e3) {
            e3.printStackTrace();
            InstabugSDKLogger.m1804w(this, "OutOfMemoryError - Couldn't encode video");
            m1337a();
        }
    }

    /* renamed from: a */
    private void m1339a(String str, String str2) throws IOException, IllegalAccessException, NullPointerException {
        List<File> m1336a = m1336a(AttachmentsUtility.getVideoRecordingFramesDirectory(this));
        File file = new File(str);
        C0686a c0686a = new C0686a(file);
        for (File file2 : m1336a) {
            if (this.f871a == Action.STOP) {
                m1341b();
                m1340a(false);
                InstabugSDKLogger.m1799d(this, VideoProcessingService.class.getSimpleName() + " has stopped");
                return;
            }
            c0686a.m1346a(m1342a(BitmapFactory.decodeFile(file2.getPath())));
        }
        c0686a.m1345a();
        m1341b();
        if (Build.VERSION.SDK_INT >= 18 && str2 != null) {
            str = new InstabugVideoUtils().mux(str2, str);
            File file3 = new File(str2);
            file.delete();
            file3.delete();
        }
        InstabugSDKLogger.m1799d(this, "Video encoding is done!");
        ScreenRecordingEventBus.getInstance().post(new ScreenRecordEvent(1, Uri.fromFile(new File(str))));
        m1340a(false);
    }

    /* renamed from: a */
    private void m1337a() {
        Toast.makeText(this, C0577R.string.instabug_str_video_encoding_error, 0).show();
        ScreenRecordingEventBus.getInstance().post(new ScreenRecordEvent(2, null));
        m1340a(false);
    }

    /* renamed from: a */
    private void m1340a(boolean z) {
        SettingsManager.getInstance().setVideoProcessorBusy(z);
    }

    /* renamed from: b */
    private void m1341b() {
        File[] listFiles = AttachmentsUtility.getVideoRecordingFramesDirectory(this).listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                file.delete();
            }
        }
        InstabugSDKLogger.m1799d(this, "Video frames are removed");
    }

    /* renamed from: a */
    private List<File> m1336a(File file) {
        ArrayList arrayList = new ArrayList();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2 != null) {
                    if (file2.isDirectory()) {
                        arrayList.addAll(m1336a(file2));
                    } else if (file2.getName().endsWith(".jpg")) {
                        arrayList.add(file2);
                    }
                }
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    public Picture m1342a(Bitmap bitmap) {
        Picture create = Picture.create(bitmap.getWidth(), bitmap.getHeight(), ColorSpace.RGB);
        m1343a(bitmap, create);
        return create;
    }

    /* renamed from: a */
    public void m1343a(Bitmap bitmap, Picture picture) {
        int[] planeData = picture.getPlaneData(0);
        int[] iArr = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < bitmap.getHeight(); i3++) {
            int i4 = 0;
            while (i4 < bitmap.getWidth()) {
                int i5 = iArr[i2];
                planeData[i] = (i5 >> 16) & 255;
                planeData[i + 1] = (i5 >> 8) & 255;
                planeData[i + 2] = i5 & 255;
                i4++;
                i2++;
                i += 3;
            }
        }
    }
}
