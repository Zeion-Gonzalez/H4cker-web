package android.support.v4.media.session;

import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.MediaControllerCompatApi23;

@RequiresApi(24)
/* loaded from: classes.dex */
class MediaControllerCompatApi24 {
    MediaControllerCompatApi24() {
    }

    /* loaded from: classes.dex */
    public static class TransportControls extends MediaControllerCompatApi23.TransportControls {
        public static void prepare(Object controlsObj) {
            ((MediaController.TransportControls) controlsObj).prepare();
        }

        public static void prepareFromMediaId(Object controlsObj, String mediaId, Bundle extras) {
            ((MediaController.TransportControls) controlsObj).prepareFromMediaId(mediaId, extras);
        }

        public static void prepareFromSearch(Object controlsObj, String query, Bundle extras) {
            ((MediaController.TransportControls) controlsObj).prepareFromSearch(query, extras);
        }

        public static void prepareFromUri(Object controlsObj, Uri uri, Bundle extras) {
            ((MediaController.TransportControls) controlsObj).prepareFromUri(uri, extras);
        }
    }
}
