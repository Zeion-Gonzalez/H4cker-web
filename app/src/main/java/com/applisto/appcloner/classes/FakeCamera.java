package com.applisto.appcloner.classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;
import com.applisto.appcloner.classes.FakeCamera;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.hooking.Hooking;
import com.instabug.chat.model.Attachment;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import java.io.ByteArrayOutputStrean;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.jcodec.codecs.mpeg12.MPEGConst;

/* loaded from: classes2.dex */
public class FakeCamera {
    private static final String ACTION_FAKE_CAMERA_ROTATE_ANTI_CLOCKWISE = "com.applisto.appcloner.action.FAKE_CAMERA_ROTATE_ANTI_CLOCKWISE";
    private static final String ACTION_FAKE_CAMERA_ROTATE_CLOCKWISE = "com.applisto.appcloner.action.FAKE_CAMERA_ROTATE_CLOCKWISE";
    private static final String ACTION_FAKE_CAMERA_SELECT_CAMERA_PICTURE = "com.applisto.appcloner.action.FAKE_CAMERA_SELECT_CAMERA_PICTURE";
    private static final int NOTIFICATION_ID = 556712456;
    private static Bitmap sBitmap;
    private static Context sContext;
    private static byte[] sJpegBytes;
    private static long sPictureTakenTimestamp;
    private static Properties sStringsProperties;
    private static final String TAG = FakeCamera.class.getSimpleName();
    private static Handler sHandler = new Handler();

    public static void install(Context context, Properties properties) {
        Log.m15i(TAG, "install; ");
        sContext = context;
        sStringsProperties = properties;
        Hooking.initHooking(context);
        Hooking.addHookClass(Hook1.class);
        Hooking.addHookClass(Hook2.class);
        Hooking.addHookClass(Hook3.class);
        Hooking.addHookClass(Hook4.class);
    }

    @HookReflectClass("android.hardware.Camera")
    /* loaded from: classes2.dex */
    public static class Hook1 {
        @HookMethodBackup("release")
        static Method releaseBackup;
        @HookMethodBackup("startPreview")
        static Method startPreviewBackup;
        @HookMethodBackup("takePicture")
        @MethodParams({Camera.ShutterCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class})
        static Method takePictureBackup;

        @HookMethod("startPreview")
        public static void startPreviewHook(Camera camera) throws Throwable {
            Log.m15i(FakeCamera.TAG, "startPreviewHook; ");
            Hooking.callInstanceOrigin(startPreviewBackup, camera, new Object[0]);
            FakeCamera.showNotificationDelayed();
        }

        @HookMethod("release")
        public static void releaseHook(Camera camera) throws Throwable {
            Log.m15i(FakeCamera.TAG, "releaseHook; ");
            FakeCamera.sHandler.removeCallbacksAndMessages(null);
            FakeCamera.hideNotification();
            Hooking.callInstanceOrigin(releaseBackup, camera, new Object[0]);
        }

        @MethodParams({Camera.ShutterCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class})
        @HookMethod("takePicture")
        public static void takePictureHook(final Camera camera, final Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, final Camera.PictureCallback pictureCallback3) throws Throwable {
            if (FakeCamera.sBitmap != null) {
                Log.m15i(FakeCamera.TAG, "takePictureHook; providing selected picture...");
                FakeCamera.sHandler.post(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$FakeCamera$Hook1$slyP2TkhZsI7YCxFK5drsNyS5WQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        FakeCamera.Hook1.lambda$takePictureHook$0(shutterCallback, pictureCallback3, camera);
                    }
                });
            } else {
                Log.m15i(FakeCamera.TAG, "takePictureHook; calling backup...");
                Hooking.callInstanceOrigin(takePictureBackup, camera, shutterCallback, pictureCallback, pictureCallback2, pictureCallback3);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$takePictureHook$0(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera camera) {
            if (shutterCallback != null) {
                try {
                    shutterCallback.onShutter();
                } catch (Exception e) {
                    Log.m21w(FakeCamera.TAG, e);
                }
            }
            if (pictureCallback != null) {
                if (FakeCamera.sJpegBytes != null) {
                    pictureCallback.onPictureTaken(FakeCamera.sJpegBytes, camera);
                } else {
                    ByteArrayOutputStrean byteArrayOutputStrean = new ByteArrayOutputStrean();
                    FakeCamera.sBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStrean);
                    pictureCallback.onPictureTaken(byteArrayOutputStrean.toByteArray(), camera);
                }
            }
            long unused = FakeCamera.sPictureTakenTimestamp = System.currentTimeMillis();
        }
    }

    @HookReflectClass("android.hardware.camera2.CameraManager")
    /* loaded from: classes2.dex */
    public static class Hook2 {
        @HookMethodBackup("openCameraForUid")
        @MethodParams({String.class, CameraDevice.StateCallback.class, Executor.class, int.class})
        static Method openCameraForUidBackup;

        @MethodParams({String.class, CameraDevice.StateCallback.class, Executor.class, int.class})
        @HookMethod("openCameraForUid")
        public static void openCameraForUidHook(CameraManager cameraManager, String str, CameraDevice.StateCallback stateCallback, Executor executor, int i) throws Throwable {
            Log.m15i(FakeCamera.TAG, "openCameraForUidHook; ");
            Hooking.callInstanceOrigin(openCameraForUidBackup, cameraManager, str, stateCallback, executor, Integer.valueOf(i));
            FakeCamera.showNotificationDelayed();
        }
    }

    @HookReflectClass("android.hardware.camera2.impl.CameraDeviceImpl")
    /* loaded from: classes2.dex */
    public static class Hook3 {
        @HookMethodBackup("close")
        static Method closeBackup;

        @HookMethod("close")
        public static void closeHook(Object obj) throws Throwable {
            Log.m15i(FakeCamera.TAG, "closeHook; ");
            FakeCamera.sHandler.removeCallbacksAndMessages(null);
            FakeCamera.hideNotification();
            Hooking.callInstanceOrigin(closeBackup, obj, new Object[0]);
        }
    }

    @HookReflectClass("android.media.ImageReader")
    /* loaded from: classes2.dex */
    public static class Hook4 {
        @HookMethodBackup("acquireLatestImage")
        static Method acquireLatestImageBackup;
        @HookMethodBackup("acquireNextImage")
        static Method acquireNextImageBackup;

        @HookMethod("acquireLatestImage")
        public static Image acquireLatestImageHook(ImageReader imageReader) throws Throwable {
            Log.m15i(FakeCamera.TAG, "acquireLatestImageHook; ");
            if (FakeCamera.sBitmap != null) {
                return getImage();
            }
            return (Image) Hooking.callInstanceOrigin(acquireLatestImageBackup, imageReader, new Object[0]);
        }

        @HookMethod("acquireNextImage")
        public static Image acquireNextImageHook(ImageReader imageReader) throws Throwable {
            Log.m15i(FakeCamera.TAG, "acquireNextImageHook; ");
            if (FakeCamera.sBitmap != null) {
                return getImage();
            }
            return (Image) Hooking.callInstanceOrigin(acquireNextImageBackup, imageReader, new Object[0]);
        }

        private static Image getImage() {
            long unused = FakeCamera.sPictureTakenTimestamp = System.currentTimeMillis();
            return new Image() { // from class: com.applisto.appcloner.classes.FakeCamera.Hook4.1
                @Override // android.media.Image
                public void close() {
                }

                @Override // android.media.Image
                public int getFormat() {
                    return 0;
                }

                @Override // android.media.Image
                public int getScalingMode() {
                    return 0;
                }

                @Override // android.media.Image
                public int getTransform() {
                    return 0;
                }

                @Override // android.media.Image
                public int getWidth() {
                    return FakeCamera.sBitmap.getWidth();
                }

                @Override // android.media.Image
                public int getHeight() {
                    return FakeCamera.sBitmap.getHeight();
                }

                @Override // android.media.Image
                public long getTimestamp() {
                    return FakeCamera.sPictureTakenTimestamp;
                }

                @Override // android.media.Image
                public Image.Plane[] getPlanes() {
                    return new Image.Plane[]{new Image.Plane() { // from class: com.applisto.appcloner.classes.FakeCamera.Hook4.1.1
                        @Override // android.media.Image.Plane
                        public int getPixelStride() {
                            return 0;
                        }

                        @Override // android.media.Image.Plane
                        public int getRowStride() {
                            return 0;
                        }

                        @Override // android.media.Image.Plane
                        public ByteBuffer getBuffer() {
                            Log.m15i(FakeCamera.TAG, "getBuffer; ");
                            if (FakeCamera.sJpegBytes != null) {
                                return ByteBuffer.wrap(FakeCamera.sJpegBytes);
                            }
                            ByteArrayOutputStrean byteArrayOutputStrean = new ByteArrayOutputStrean();
                            FakeCamera.sBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStrean);
                            return ByteBuffer.wrap(byteArrayOutputStrean.toByteArray());
                        }
                    }};
                }
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showNotificationDelayed() {
        sHandler.postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$FakeCamera$SGuMuPS15NjagKFwg9XezCXwjVE
            @Override // java.lang.Runnable
            public final void run() {
                FakeCamera.showNotification();
            }
        }, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showNotification() {
        Log.m15i(TAG, "showNotification; ");
        hideNotification();
        try {
            NotificationManager notificationManager = (NotificationManager) sContext.getSystemService("notification");
            if (notificationManager != null) {
                Intent intent = new Intent(ACTION_FAKE_CAMERA_SELECT_CAMERA_PICTURE);
                intent.setPackage(sContext.getPackageName());
                PendingIntent broadcast = PendingIntent.getBroadcast(sContext, 0, intent, 0);
                String property = sStringsProperties.getProperty("fake_camera_title");
                String property2 = sBitmap != null ? sStringsProperties.getProperty("fake_camera_use_picture_message") : sStringsProperties.getProperty("fake_camera_select_picture_message");
                Notification.Builder when = new Notification.Builder(sContext).setContentTitle(property).setContentText(property2).setContentIntent(broadcast).setAutoCancel(true).setWhen(0L);
                if (sBitmap != null && Build.VERSION.SDK_INT >= 16) {
                    when.setStyle(new Notification.BigPictureStyle().setBigContentTitle(property).setSummaryText(property2).bigPicture(sBitmap));
                    if (Build.VERSION.SDK_INT >= 21) {
                        Intent intent2 = new Intent(ACTION_FAKE_CAMERA_ROTATE_CLOCKWISE);
                        intent2.setPackage(sContext.getPackageName());
                        PendingIntent broadcast2 = PendingIntent.getBroadcast(sContext, 0, intent2, 0);
                        Intent intent3 = new Intent(ACTION_FAKE_CAMERA_ROTATE_ANTI_CLOCKWISE);
                        intent3.setPackage(sContext.getPackageName());
                        PendingIntent broadcast3 = PendingIntent.getBroadcast(sContext, 0, intent3, 0);
                        when.addAction(new Notification.Action(0, sStringsProperties.getProperty("fake_camera_rotate_right_label"), broadcast2));
                        when.addAction(new Notification.Action(0, sStringsProperties.getProperty("fake_camera_rotate_left_label"), broadcast3));
                    }
                }
                Utils.setSmallNotificationIcon(when, System.currentTimeMillis() - sPictureTakenTimestamp > 1000);
                notificationManager.notify(NOTIFICATION_ID, when.getNotification());
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void hideNotification() {
        Log.m15i(TAG, "hideNotification; ");
        try {
            NotificationManager notificationManager = (NotificationManager) sContext.getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
    }

    /* loaded from: classes2.dex */
    public static class FakeCameraReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.m15i(FakeCamera.TAG, "onReceive; intent: " + intent);
            try {
                String action = intent.getAction();
                if (FakeCamera.ACTION_FAKE_CAMERA_SELECT_CAMERA_PICTURE.equals(action)) {
                    Intent intent2 = new Intent(FakeCamera.sContext, FakeCameraActivity.class);
                    intent2.setFlags(268435456);
                    FakeCamera.sContext.startActivity(intent2);
                } else if (FakeCamera.ACTION_FAKE_CAMERA_ROTATE_CLOCKWISE.equals(action)) {
                    rotateBitmap(90);
                } else if (FakeCamera.ACTION_FAKE_CAMERA_ROTATE_ANTI_CLOCKWISE.equals(action)) {
                    rotateBitmap(-90);
                }
            } catch (Exception e) {
                Log.m21w(FakeCamera.TAG, e);
            }
        }

        private void rotateBitmap(int i) {
            Log.m15i(FakeCamera.TAG, "rotateBitmap; degrees: " + i);
            if (FakeCamera.sBitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate(i);
                Bitmap unused = FakeCamera.sBitmap = Bitmap.createBitmap(FakeCamera.sBitmap, 0, 0, FakeCamera.sBitmap.getWidth(), FakeCamera.sBitmap.getHeight(), matrix, true);
                byte[] unused2 = FakeCamera.sJpegBytes = null;
                FakeCamera.showNotification();
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class FakeCameraActivity extends Activity {
        private static final String PERMISSION = "android.permission.READ_EXTERNAL_STORAGE";
        private static final int REQUEST_CODE_SELECT_PICTURE = 1;

        @Override // android.app.Activity
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(PERMISSION) != 0) {
                requestPermissions(new String[]{PERMISSION}, 0);
            } else {
                onSelectPicture();
            }
        }

        private void onSelectPicture() {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }

        @Override // android.app.Activity
        public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
            Log.m15i(FakeCamera.TAG, "onRequestPermissionsResult; permissions: " + Arrays.toString(strArr) + ", grantResults: " + Arrays.toString(iArr));
            if (strArr.length != 1 || iArr.length != 1 || !PERMISSION.equals(strArr[0]) || iArr[0] != 0) {
                Toast.makeText(this, FakeCamera.sStringsProperties.getProperty("permission_not_granted_message"), 1).show();
                finish();
            } else {
                onSelectPicture();
            }
        }

        /* JADX WARN: Type inference failed for: r6v4, types: [com.applisto.appcloner.classes.FakeCamera$FakeCameraActivity$1] */
        @Override // android.app.Activity
        public void onActivityResult(int i, int i2, Intent intent) {
            Log.m15i(FakeCamera.TAG, "onActivityResult; requestCode: " + i + ", resultCode: " + i2);
            if (i == 1) {
                Bitmap unused = FakeCamera.sBitmap = null;
                byte[] unused2 = FakeCamera.sJpegBytes = null;
                if (i2 == -1) {
                    try {
                        final String path = getPath(FakeCamera.sContext, intent.getData());
                        Log.m15i(FakeCamera.TAG, "onActivityResult; path: " + path);
                        new Thread() { // from class: com.applisto.appcloner.classes.FakeCamera.FakeCameraActivity.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                try {
                                    Bitmap unused3 = FakeCamera.sBitmap = BitmapFactory.decodeFile(path);
                                    byte[] readFully = Utils.readFully(new FileInputStream(path), true);
                                    if (FakeCameraActivity.isJpeg(readFully)) {
                                        Log.m15i(FakeCamera.TAG, "onActivityResult; JPEG detected");
                                        byte[] unused4 = FakeCamera.sJpegBytes = readFully;
                                        Bitmap unused5 = FakeCamera.sBitmap = FakeCameraActivity.normalizeImageOrientation(FakeCamera.sBitmap, path);
                                    }
                                    Thread.sleep(1000L);
                                    FakeCamera.showNotification();
                                } catch (Exception e) {
                                    Log.m21w(FakeCamera.TAG, e);
                                }
                            }
                        }.start();
                    } catch (Throwable th) {
                        Log.m21w(FakeCamera.TAG, th);
                        Bitmap unused3 = FakeCamera.sBitmap = null;
                        byte[] unused4 = FakeCamera.sJpegBytes = null;
                    }
                } else {
                    FakeCamera.showNotification();
                }
                finish();
            }
        }

        private static String getPath(Context context, Uri uri) {
            Uri uri2 = null;
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    String[] split = DocumentsContract.getDocumentId(uri).split(":");
                    if ("primary".equalsIgnoreCase(split[0])) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else {
                    if (isDownloadsDocument(uri)) {
                        return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
                    }
                    if (isMediaDocument(uri)) {
                        String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                        String str = split2[0];
                        if (Attachment.TYPE_IMAGE.equals(str)) {
                            uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if (Attachment.TYPE_VIDEO.equals(str)) {
                            uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if (Attachment.TYPE_AUDIO.equals(str)) {
                            uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        return getDataColumn(context, uri2, "_id=?", new String[]{split2[1]});
                    }
                }
            } else {
                if ("content".equalsIgnoreCase(uri.getScheme())) {
                    if (isGooglePhotosUri(uri)) {
                        return uri.getLastPathSegment();
                    }
                    return getDataColumn(context, uri, null, null);
                }
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
            return null;
        }

        private static String getDataColumn(Context context, Uri uri, String str, String[] strArr) {
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, new String[]{"_data"}, str, strArr, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            String string = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                            if (cursor != null) {
                                cursor.close();
                            }
                            return string;
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = null;
            }
        }

        private static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        private static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        private static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        private static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }

        public static boolean isJpeg(byte[] bArr) {
            return bArr[0] == -1 && bArr[1] == -40 && bArr[2] == -1;
        }

        public static Bitmap normalizeImageOrientation(Bitmap bitmap, String str) {
            int imageOrientation = getImageOrientation(str);
            Log.m15i(FakeCamera.TAG, "normalizeImageOrientation; orientation: " + imageOrientation);
            if (imageOrientation == 0) {
                return bitmap;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(imageOrientation);
            try {
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } finally {
                bitmap.recycle();
                System.gc();
            }
        }

        public static int getImageOrientation(String str) {
            try {
                int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", 1);
                if (attributeInt == 3) {
                    return MPEGConst.SEQUENCE_ERROR_CODE;
                }
                if (attributeInt != 6) {
                    return attributeInt != 8 ? 0 : 270;
                }
                return 90;
            } catch (IOException e) {
                Log.m21w(FakeCamera.TAG, e);
                return 0;
            }
        }
    }
}
