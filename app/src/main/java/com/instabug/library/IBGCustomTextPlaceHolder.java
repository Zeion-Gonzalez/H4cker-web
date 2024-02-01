package com.instabug.library;

import android.support.annotation.NonNull;
import com.instabug.library.InstabugCustomTextPlaceHolder;
import java.util.HashMap;
import java.util.Map;

@Deprecated
/* loaded from: classes.dex */
public class IBGCustomTextPlaceHolder {
    private HashMap<Key, String> placeHoldersMap = new HashMap<>();

    @Deprecated
    /* loaded from: classes.dex */
    public enum Key {
        SHAKE_HINT,
        SWIPE_HINT,
        INVALID_EMAIL_MESSAGE,
        INVALID_COMMENT_MESSAGE,
        INVOCATION_HEADER,
        START_CHATS,
        REPORT_BUG,
        REPORT_FEEDBACK,
        EMAIL_FIELD_HINT,
        COMMENT_FIELD_HINT_FOR_BUG_REPORT,
        COMMENT_FIELD_HINT_FOR_FEEDBACK,
        ADD_VOICE_MESSAGE,
        ADD_IMAGE_FROM_GALLERY,
        ADD_EXTRA_SCREENSHOT,
        CONVERSATIONS_LIST_TITLE,
        AUDIO_RECORDING_PERMISSION_DENIED,
        CONVERSATION_TEXT_FIELD_HINT,
        BUG_REPORT_HEADER,
        FEEDBACK_REPORT_HEADER,
        VOICE_MESSAGE_PRESS_AND_HOLD_TO_RECORD,
        VOICE_MESSAGE_RELEASE_TO_ATTACH,
        REPORT_SUCCESSFULLY_SENT,
        SUCCESS_DIALOG_HEADER,
        ADD_VIDEO,
        VIDEO_PLAYER_TITLE
    }

    public void set(Key key, @NonNull String str) {
        this.placeHoldersMap.put(key, str);
    }

    public String get(Key key) {
        if (this.placeHoldersMap.containsKey(key)) {
            return this.placeHoldersMap.get(key);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HashMap<InstabugCustomTextPlaceHolder.Key, String> getConvertedHashMap() {
        HashMap<InstabugCustomTextPlaceHolder.Key, String> hashMap = new HashMap<>();
        for (Map.Entry<Key, String> entry : this.placeHoldersMap.entrySet()) {
            hashMap.put(InstabugCustomTextPlaceHolder.Key.valueOf(entry.getKey().name()), entry.getValue());
        }
        return hashMap;
    }
}
