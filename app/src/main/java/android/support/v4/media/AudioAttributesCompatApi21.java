package android.support.v4.media;

import android.media.AudioAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
/* loaded from: classes.dex */
class AudioAttributesCompatApi21 {
    private static final String TAG = "AudioAttributesCompat";
    private static Method sAudioAttributesToLegacyStreamType;

    AudioAttributesCompatApi21() {
    }

    public static int toLegacyStreamType(Wrapper aaWrap) {
        AudioAttributes aaObject = aaWrap.unwrap();
        try {
            if (sAudioAttributesToLegacyStreamType == null) {
                sAudioAttributesToLegacyStreamType = AudioAttributes.class.getMethod("toLegacyStreamType", AudioAttributes.class);
            }
            Object result = sAudioAttributesToLegacyStreamType.invoke(null, aaObject);
            return ((Integer) result).intValue();
        } catch (ClassCastException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.w(TAG, "getLegacyStreamType() failed on API21+", e);
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Wrapper {
        private AudioAttributes mWrapped;

        private Wrapper(AudioAttributes obj) {
            this.mWrapped = obj;
        }

        public static Wrapper wrap(@NonNull AudioAttributes obj) {
            if (obj == null) {
                throw new IllegalArgumentException("AudioAttributesApi21.Wrapper cannot wrap null");
            }
            return new Wrapper(obj);
        }

        public AudioAttributes unwrap() {
            return this.mWrapped;
        }
    }
}
