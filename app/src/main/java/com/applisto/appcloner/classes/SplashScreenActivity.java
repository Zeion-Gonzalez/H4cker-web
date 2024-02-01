package com.applisto.appcloner.classes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.applisto.appcloner.classes.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/* loaded from: classes2.dex */
public class SplashScreenActivity extends Activity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private String mOriginalActivityName;

    @Override // android.app.Activity
    public void onBackPressed() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Window window;
        ImageView imageView;
        super.onCreate(bundle);
        try {
            CloneSettings cloneSettings = CloneSettings.getInstance(this);
            int intValue = cloneSettings.getInteger("splashScreenDuration", 3).intValue();
            try {
                float floatValue = cloneSettings.getFloat("splashScreenMargin", Float.valueOf(0.3f)).floatValue() / 2.0f;
                Point realScreenSize = Utils.getRealScreenSize(getWindowManager().getDefaultDisplay());
                int round = Math.round(realScreenSize.x * floatValue);
                int round2 = Math.round(realScreenSize.y * floatValue);
                byte[] readFully = Utils.readFully(getAssets().open(".splashScreenFile"), true);
                if (isGif(readFully)) {
                    CustomGifView customGifView = new CustomGifView(this, new ByteArrayInputStream(readFully));
                    FrameLayout frameLayout = new FrameLayout(this);
                    frameLayout.addView(customGifView, new FrameLayout.LayoutParams(-1, -1, 17));
                    imageView = frameLayout;
                } else {
                    Bitmap decodeByteArray = BitmapFactory.decodeByteArray(readFully, 0, readFully.length);
                    ImageView imageView2 = new ImageView(this);
                    imageView2.setImageBitmap(decodeByteArray);
                    imageView = imageView2;
                }
                imageView.setPadding(round, round2, round, round2);
                setContentView(imageView);
            } catch (Exception e) {
                Log.m21w(TAG, e);
            }
            try {
                if (Build.VERSION.SDK_INT >= 21 && (window = getWindow()) != null) {
                    window.addFlags(Integer.MIN_VALUE);
                    window.clearFlags(67108864);
                    int intValue2 = cloneSettings.getInteger("splashScreenBackgroundColor", -1).intValue();
                    window.setStatusBarColor(intValue2);
                    window.setNavigationBarColor(intValue2);
                }
            } catch (Exception e2) {
                Log.m21w(TAG, e2);
            }
            Bundle bundle2 = getPackageManager().getActivityInfo(getComponentName(), 129).metaData;
            if (bundle2 != null) {
                this.mOriginalActivityName = bundle2.getString("com.applisto.appcloner.originalActivityName");
                if (this.mOriginalActivityName != null) {
                    if (this.mOriginalActivityName.startsWith(".")) {
                        this.mOriginalActivityName = getPackageName() + this.mOriginalActivityName;
                    }
                    new Handler().postDelayed(new Runnable() { // from class: com.applisto.appcloner.classes.-$$Lambda$SplashScreenActivity$yXLk1id2mEUWEr-fgdpI8_Zunbs
                        @Override // java.lang.Runnable
                        public final void run() {
                            SplashScreenActivity.this.startApp();
                        }
                    }, intValue * 1000);
                }
            }
        } catch (Exception e3) {
            Log.m21w(TAG, e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startApp() {
        Log.m15i(TAG, "startApp; ");
        try {
            Intent intent = new Intent(getIntent());
            intent.setComponent(new ComponentName(this, Class.forName(this.mOriginalActivityName)));
            intent.setFlags(268435456);
            startActivity(intent);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        finish();
    }

    public static boolean isGif(byte[] bArr) {
        return bArr[0] == 71 && bArr[1] == 73 && bArr[2] == 70;
    }

    /* loaded from: classes2.dex */
    public static class CustomGifView extends View {
        private Movie mGifMovie;
        private float mMovieHeight;
        private long mMovieStart;
        private float mMovieWidth;

        public CustomGifView(Context context, InputStream inputStream) {
            super(context);
            this.mGifMovie = Movie.decodeStream(inputStream);
            this.mMovieWidth = this.mGifMovie.width();
            this.mMovieHeight = this.mGifMovie.height();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            long uptimeMillis = SystemClock.uptimeMillis();
            if (this.mMovieStart == 0) {
                this.mMovieStart = uptimeMillis;
            }
            Movie movie = this.mGifMovie;
            if (movie != null) {
                int duration = movie.duration();
                if (duration == 0) {
                    duration = 1000;
                }
                this.mGifMovie.setTime((int) ((uptimeMillis - this.mMovieStart) % duration));
                float width = getWidth();
                float height = getHeight();
                float min = Math.min(width / this.mMovieWidth, height / this.mMovieHeight);
                float f = this.mMovieWidth * min;
                float f2 = this.mMovieHeight * min;
                canvas.save();
                canvas.translate((width - f) / 2.0f, (height - f2) / 2.0f);
                canvas.scale(min, min);
                this.mGifMovie.draw(canvas, 0.0f, 0.0f);
                canvas.restore();
                invalidate();
            }
        }
    }
}
