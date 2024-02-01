package com.instabug.chat.model;

import android.webkit.MimeTypeMap;
import com.instabug.library.internal.storage.cache.Cacheable;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.jcodec.common.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Attachment implements Cacheable, Serializable {
    static final String KEY_ATTACHMENT_STATE = "attachment_state";
    static final String KEY_DURATION = "duration";
    static final String KEY_LOCALE_PATH = "local_path";
    static final String KEY_NAME = "name";
    static final String KEY_TYPE = "type";
    static final String KEY_URL = "url";
    static final String KEY_VIDEO_ENCODED = "video_encoded";
    public static final String STATE_NOT_AVAILABLE = "not_available";
    public static final String STATE_OFFLINE = "offline";
    public static final String STATE_SYNCED = "synced";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_NOT_AVAILABLE = "not_available";
    public static final String TYPE_VIDEO = "video";
    private String duration;
    private boolean isVideoEncoded = false;
    private String localPath;
    private String name;
    private String state;
    private String type;
    private String url;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AttachmentState {
    }

    public Attachment() {
        setType("not_available");
        setState("not_available");
    }

    public static JSONArray toJson(ArrayList<Attachment> arrayList) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < arrayList.size()) {
                jSONArray.put(new JSONObject(arrayList.get(i2).toJson()));
                i = i2 + 1;
            } else {
                return jSONArray;
            }
        }
    }

    public static ArrayList<Attachment> fromJson(JSONArray jSONArray) throws JSONException {
        ArrayList<Attachment> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            Attachment attachment = new Attachment();
            attachment.fromJson(jSONArray.getJSONObject(i).toString());
            arrayList.add(attachment);
        }
        return arrayList;
    }

    public String getName() {
        return this.name;
    }

    public Attachment setName(String str) {
        this.name = str;
        return this;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public Attachment setLocalPath(String str) {
        this.localPath = str;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public Attachment setUrl(String str) {
        this.url = str;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public Attachment setType(String str) {
        this.type = str;
        return this;
    }

    public boolean isVideoEncoded() {
        return this.isVideoEncoded;
    }

    public Attachment setVideoEncoded(boolean z) {
        this.isVideoEncoded = z;
        return this;
    }

    public String getFileType() {
        String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(getName());
        if (fileExtensionFromUrl != null && !StringUtils.isEmpty(fileExtensionFromUrl)) {
            String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
            if (mimeTypeFromExtension == null || mimeTypeFromExtension.equals("")) {
                return getType();
            }
            return mimeTypeFromExtension;
        }
        return getType();
    }

    public String getState() {
        return this.state;
    }

    public Attachment setState(String str) {
        this.state = str;
        return this;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String str) {
        this.duration = str;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(KEY_NAME, getName()).put(KEY_LOCALE_PATH, getLocalPath()).put(KEY_URL, getUrl()).put(KEY_TYPE, getType()).put(KEY_ATTACHMENT_STATE, getState().toString()).put(KEY_VIDEO_ENCODED, isVideoEncoded()).put("duration", getDuration());
        return jSONObject.toString();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00d0, code lost:
    
        if (r3.equals(com.instabug.chat.model.Attachment.STATE_OFFLINE) != false) goto L23;
     */
    @Override // com.instabug.library.internal.storage.cache.Cacheable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void fromJson(java.lang.String r7) throws org.json.JSONException {
        /*
            r6 = this;
            r2 = 1
            r0 = 0
            r1 = -1
            org.json.JSONObject r4 = new org.json.JSONObject
            r4.<init>(r7)
            java.lang.String r3 = "name"
            boolean r3 = r4.has(r3)
            if (r3 == 0) goto L19
            java.lang.String r3 = "name"
            java.lang.String r3 = r4.getString(r3)
            r6.setName(r3)
        L19:
            java.lang.String r3 = "local_path"
            boolean r3 = r4.has(r3)
            if (r3 == 0) goto L2a
            java.lang.String r3 = "local_path"
            java.lang.String r3 = r4.getString(r3)
            r6.setLocalPath(r3)
        L2a:
            java.lang.String r3 = "url"
            boolean r3 = r4.has(r3)
            if (r3 == 0) goto L3b
            java.lang.String r3 = "url"
            java.lang.String r3 = r4.getString(r3)
            r6.setUrl(r3)
        L3b:
            java.lang.String r3 = "type"
            boolean r3 = r4.has(r3)
            if (r3 == 0) goto L59
            java.lang.String r3 = "type"
            java.lang.String r3 = r4.getString(r3)
            int r5 = r3.hashCode()
            switch(r5) {
                case 93166550: goto La4;
                case 100313435: goto L9a;
                case 112202875: goto Lae;
                default: goto L50;
            }
        L50:
            r3 = r1
        L51:
            switch(r3) {
                case 0: goto Lb8;
                case 1: goto Lbe;
                case 2: goto Lc4;
                default: goto L54;
            }
        L54:
            java.lang.String r3 = "not_available"
            r6.setType(r3)
        L59:
            java.lang.String r3 = "attachment_state"
            boolean r3 = r4.has(r3)
            if (r3 == 0) goto L77
            java.lang.String r3 = "attachment_state"
            java.lang.String r3 = r4.getString(r3)
            int r5 = r3.hashCode()
            switch(r5) {
                case -1548612125: goto Lca;
                case -887493510: goto Ld3;
                default: goto L6e;
            }
        L6e:
            r0 = r1
        L6f:
            switch(r0) {
                case 0: goto Ldd;
                case 1: goto Le3;
                default: goto L72;
            }
        L72:
            java.lang.String r0 = "not_available"
            r6.setState(r0)
        L77:
            java.lang.String r0 = "video_encoded"
            boolean r0 = r4.has(r0)
            if (r0 == 0) goto L88
            java.lang.String r0 = "video_encoded"
            boolean r0 = r4.getBoolean(r0)
            r6.setVideoEncoded(r0)
        L88:
            java.lang.String r0 = "duration"
            boolean r0 = r4.has(r0)
            if (r0 == 0) goto L99
            java.lang.String r0 = "duration"
            java.lang.String r0 = r4.getString(r0)
            r6.setDuration(r0)
        L99:
            return
        L9a:
            java.lang.String r5 = "image"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L50
            r3 = r0
            goto L51
        La4:
            java.lang.String r5 = "audio"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L50
            r3 = r2
            goto L51
        Lae:
            java.lang.String r5 = "video"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L50
            r3 = 2
            goto L51
        Lb8:
            java.lang.String r3 = "image"
            r6.setType(r3)
            goto L59
        Lbe:
            java.lang.String r3 = "audio"
            r6.setType(r3)
            goto L59
        Lc4:
            java.lang.String r3 = "video"
            r6.setType(r3)
            goto L59
        Lca:
            java.lang.String r2 = "offline"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L6e
            goto L6f
        Ld3:
            java.lang.String r0 = "synced"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L6e
            r0 = r2
            goto L6f
        Ldd:
            java.lang.String r0 = "offline"
            r6.setState(r0)
            goto L77
        Le3:
            java.lang.String r0 = "synced"
            r6.setState(r0)
            goto L77
        */
        throw new UnsupportedOperationException("Method not decompiled: com.instabug.chat.model.Attachment.fromJson(java.lang.String):void");
    }

    public String toString() {
        return "Name: " + getName() + ", Local Path: " + getLocalPath() + ", Type: " + getType() + ", Url: " + getUrl() + ", Attachment State: " + getState();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Attachment)) {
            return false;
        }
        Attachment attachment = (Attachment) obj;
        return String.valueOf(attachment.getName()).equals(String.valueOf(getName())) && String.valueOf(attachment.getLocalPath()).equals(String.valueOf(getLocalPath())) && String.valueOf(attachment.getUrl()).equals(String.valueOf(getUrl())) && attachment.getType().equals(getType()) && attachment.getState().equals(getState()) && attachment.isVideoEncoded() == isVideoEncoded() && String.valueOf(attachment.getDuration()).equals(String.valueOf(getDuration()));
    }

    public int hashCode() {
        if (getName() != null) {
            return getName().hashCode();
        }
        return -1;
    }
}
