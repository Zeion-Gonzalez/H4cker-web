package com.instabug.crash.models;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.model.Attachment;
import com.instabug.library.model.State;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.visualusersteps.VisualUserStepsHelper;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.functions.Action1;

/* loaded from: classes.dex */
public class Crash implements Cacheable {

    /* renamed from: a */
    private String f539a;

    /* renamed from: b */
    private String f540b;

    /* renamed from: c */
    private String f541c;

    /* renamed from: d */
    private ArrayList<Attachment> f542d;

    /* renamed from: e */
    private State f543e;

    /* renamed from: f */
    private CrashState f544f;

    /* renamed from: g */
    private boolean f545g;

    /* loaded from: classes.dex */
    public enum CrashState {
        READY_TO_BE_SENT,
        LOGS_READY_TO_BE_UPLOADED,
        ATTACHMENTS_READY_TO_BE_UPLOADED,
        NOT_AVAILABLE,
        WAITING_FOR_SCREEN_RECORDING_TO_BE_TRIMMED
    }

    public Crash() {
        this.f544f = CrashState.NOT_AVAILABLE;
    }

    public Crash(@NonNull String str, @NonNull State state) {
        this();
        this.f539a = str;
        this.f543e = state;
        this.f542d = new ArrayList<>(6);
    }

    /* renamed from: a */
    public String m944a() {
        return this.f539a;
    }

    /* renamed from: a */
    public Crash m941a(String str) {
        this.f539a = str;
        return this;
    }

    /* renamed from: b */
    public String m946b() {
        return this.f540b;
    }

    /* renamed from: b */
    public Crash m945b(String str) {
        this.f540b = str;
        return this;
    }

    /* renamed from: c */
    public String m948c() {
        return this.f541c;
    }

    /* renamed from: c */
    public Crash m947c(String str) {
        this.f541c = str;
        return this;
    }

    /* renamed from: a */
    public Crash m937a(Uri uri) {
        return m938a(uri, Attachment.Type.ATTACHMENT_FILE);
    }

    /* renamed from: a */
    public Crash m938a(Uri uri, Attachment.Type type) {
        if (uri == null) {
            InstabugSDKLogger.m1804w(this, "Adding attachment with a null Uri, ignored.");
        } else {
            Attachment attachment = new Attachment();
            attachment.setName(uri.getLastPathSegment());
            attachment.setLocalPath(uri.getPath());
            attachment.setType(type);
            this.f542d.add(attachment);
        }
        return this;
    }

    /* renamed from: d */
    public ArrayList<Attachment> m949d() {
        return this.f542d;
    }

    /* renamed from: a */
    public Crash m942a(@NonNull ArrayList<Attachment> arrayList) {
        this.f542d = arrayList;
        return this;
    }

    /* renamed from: e */
    public State m950e() {
        return this.f543e;
    }

    /* renamed from: a */
    public Crash m940a(State state) {
        this.f543e = state;
        return this;
    }

    /* renamed from: f */
    public CrashState m951f() {
        return this.f544f;
    }

    /* renamed from: a */
    public Crash m939a(CrashState crashState) {
        this.f544f = crashState;
        return this;
    }

    /* renamed from: g */
    public boolean m952g() {
        return this.f545g;
    }

    /* renamed from: a */
    public Crash m943a(boolean z) {
        this.f545g = z;
        return this;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", m944a()).put("temporary_server_token", m946b()).put("crash_message", m948c()).put("crash_state", m951f().toString()).put("state", m950e().toJson()).put("attachments", Attachment.toJson(m949d())).put("handled", m952g());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            m941a(jSONObject.getString("id"));
        }
        if (jSONObject.has("temporary_server_token")) {
            m945b(jSONObject.getString("temporary_server_token"));
        }
        if (jSONObject.has("crash_message")) {
            m947c(jSONObject.getString("crash_message"));
        }
        if (jSONObject.has("crash_state")) {
            m939a(CrashState.valueOf(jSONObject.getString("crash_state")));
        }
        if (jSONObject.has("state")) {
            State state = new State();
            state.fromJson(jSONObject.getString("state"));
            m940a(state);
        }
        if (jSONObject.has("attachments")) {
            m942a(Attachment.fromJson(jSONObject.getJSONArray("attachments")));
        }
        if (jSONObject.has("handled")) {
            m943a(jSONObject.getBoolean("handled"));
        }
    }

    public String toString() {
        return "Internal Id: " + this.f539a + ", TemporaryServerToken:" + this.f540b + ", crashMessage:" + this.f541c + ", handled:" + this.f545g;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Crash)) {
            return false;
        }
        Crash crash = (Crash) obj;
        if (!String.valueOf(crash.m944a()).equals(String.valueOf(m944a())) || !String.valueOf(crash.m948c()).equals(String.valueOf(m948c())) || !String.valueOf(crash.m946b()).equals(String.valueOf(m946b())) || crash.m951f() != m951f() || !crash.m950e().equals(m950e()) || crash.m952g() != m952g() || crash.m949d() == null || crash.m949d().size() != m949d().size()) {
            return false;
        }
        for (int i = 0; i < crash.m949d().size(); i++) {
            if (!crash.m949d().get(i).equals(m949d().get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (m944a() != null) {
            return m944a().hashCode();
        }
        return -1;
    }

    /* renamed from: com.instabug.crash.models.Crash$a */
    /* loaded from: classes.dex */
    public static class C0567a {
        /* renamed from: a */
        public Crash m953a(Context context) {
            final Crash crash = new Crash(System.currentTimeMillis() + "", new State.Builder(context).build(true));
            if (InstabugCore.getFeatureState(Feature.REPRO_STEPS) == Feature.State.ENABLED && InstabugCore.isReproStepsScreenshotEnabled()) {
                VisualUserStepsHelper.getVisualUserStepsFileObservable(context, crash.m944a()).subscribe(new Action1<Uri>() { // from class: com.instabug.crash.models.Crash.a.1
                    @Override // p045rx.functions.Action1
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void call(Uri uri) {
                        crash.m938a(uri, Attachment.Type.VISUAL_USER_STEPS);
                    }
                });
            }
            return crash;
        }
    }
}
