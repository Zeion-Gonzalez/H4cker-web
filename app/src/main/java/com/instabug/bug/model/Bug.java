package com.instabug.bug.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.model.Attachment;
import com.instabug.library.model.BaseReport;
import com.instabug.library.model.State;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.StringUtility;
import com.instabug.library.visualusersteps.VisualUserStepsHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p045rx.functions.Action1;
import p045rx.schedulers.Schedulers;

/* loaded from: classes.dex */
public class Bug extends BaseReport implements Cacheable, Serializable {

    /* renamed from: a */
    private String f76a;

    /* renamed from: b */
    private String f77b;

    /* renamed from: c */
    private String f78c;

    /* renamed from: d */
    private EnumC0472b f79d;

    /* renamed from: e */
    private String f80e;

    /* renamed from: f */
    private ArrayList<Attachment> f81f;

    /* renamed from: g */
    private State f82g;

    /* renamed from: h */
    private BugState f83h;

    /* renamed from: i */
    private String f84i;

    /* renamed from: j */
    private boolean f85j;

    /* renamed from: k */
    private ViewHierarchyInspectionState f86k;

    /* renamed from: l */
    private transient List<C0471a> f87l;

    /* renamed from: m */
    private ArrayList<String> f88m;

    /* loaded from: classes.dex */
    public enum BugState {
        IN_PROGRESS,
        WAITING_VIDEO,
        READY_TO_BE_SENT,
        LOGS_READY_TO_BE_UPLOADED,
        ATTACHMENTS_READY_TO_BE_UPLOADED,
        NOT_AVAILABLE
    }

    /* loaded from: classes.dex */
    public enum ViewHierarchyInspectionState {
        IN_PROGRESS,
        FAILED,
        DONE
    }

    public Bug() {
        this.f83h = BugState.NOT_AVAILABLE;
        this.f79d = EnumC0472b.NOT_AVAILABLE;
    }

    public Bug(@NonNull String str, @NonNull State state, @NonNull BugState bugState) {
        this.f77b = str;
        this.f82g = state;
        this.f83h = bugState;
        this.f79d = EnumC0472b.NOT_AVAILABLE;
        this.f81f = new ArrayList<>(6);
        this.f88m = new ArrayList<>();
    }

    @Override // com.instabug.library.model.BaseReport
    public String getId() {
        return this.f77b;
    }

    @Override // com.instabug.library.model.BaseReport
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Bug setId(String str) {
        this.f77b = str;
        return this;
    }

    /* renamed from: a */
    public String m120a() {
        return this.f78c;
    }

    /* renamed from: b */
    public Bug m123b(String str) {
        this.f78c = str;
        return this;
    }

    /* renamed from: c */
    public void m127c(String str) {
        this.f76a = str;
    }

    /* renamed from: b */
    public String m124b() {
        return this.f76a;
    }

    /* renamed from: c */
    public EnumC0472b m126c() {
        return this.f79d;
    }

    /* renamed from: a */
    public Bug m115a(EnumC0472b enumC0472b) {
        this.f79d = enumC0472b;
        return this;
    }

    /* renamed from: d */
    public String m129d() {
        return this.f80e;
    }

    /* renamed from: d */
    public Bug m128d(String str) {
        this.f80e = str;
        return this;
    }

    /* renamed from: a */
    public Bug m112a(Uri uri, Attachment.Type type) {
        if (uri == null) {
            InstabugSDKLogger.m1804w(this, "Adding attachment with a null Uri, ignored.");
        } else if (type == null) {
            InstabugSDKLogger.m1804w(this, "Adding attachment with a null Attachment.Type, ignored.");
        } else {
            Attachment attachment = new Attachment();
            attachment.setName(uri.getLastPathSegment());
            attachment.setLocalPath(uri.getPath());
            attachment.setType(type);
            this.f81f.add(attachment);
        }
        return this;
    }

    /* renamed from: e */
    public ArrayList<Attachment> m131e() {
        return this.f81f;
    }

    /* renamed from: a */
    public Bug m118a(ArrayList<Attachment> arrayList) {
        this.f81f = arrayList;
        return this;
    }

    @Override // com.instabug.library.model.BaseReport
    public State getState() {
        return this.f82g;
    }

    @Override // com.instabug.library.model.BaseReport
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Bug setState(State state) {
        this.f82g = state;
        return this;
    }

    /* renamed from: f */
    public BugState m132f() {
        return this.f83h;
    }

    /* renamed from: a */
    public Bug m113a(BugState bugState) {
        this.f83h = bugState;
        return this;
    }

    /* renamed from: g */
    public String m134g() {
        return this.f84i;
    }

    /* renamed from: e */
    public Bug m130e(String str) {
        this.f84i = str;
        return this;
    }

    /* renamed from: b */
    public void m125b(ArrayList<String> arrayList) {
        this.f88m = arrayList;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", getId()).put("temporary_server_token", m120a()).put("type", m126c().toString()).put("message", m129d()).put("bug_state", m132f().toString()).put("state", getState().toJson()).put("attachments", Attachment.toJson(m131e())).put("view_hierarchy", m134g()).put("categories_list", m135h());
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        EnumC0472b enumC0472b;
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            setId(jSONObject.getString("id"));
        }
        if (jSONObject.has("temporary_server_token")) {
            m123b(jSONObject.getString("temporary_server_token"));
        }
        if (jSONObject.has("type")) {
            String string = jSONObject.getString("type");
            char c = 65535;
            switch (string.hashCode()) {
                case -191501435:
                    if (string.equals("feedback")) {
                        c = 1;
                        break;
                    }
                    break;
                case 97908:
                    if (string.equals("bug")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    enumC0472b = EnumC0472b.BUG;
                    break;
                case 1:
                    enumC0472b = EnumC0472b.FEEDBACK;
                    break;
                default:
                    enumC0472b = EnumC0472b.NOT_AVAILABLE;
                    break;
            }
            m115a(enumC0472b);
        }
        if (jSONObject.has("message")) {
            m128d(jSONObject.getString("message"));
        }
        if (jSONObject.has("bug_state")) {
            m113a(BugState.valueOf(jSONObject.getString("bug_state")));
        }
        if (jSONObject.has("state")) {
            State state = new State();
            state.fromJson(jSONObject.getString("state"));
            setState(state);
        }
        if (jSONObject.has("attachments")) {
            m118a(Attachment.fromJson(jSONObject.getJSONArray("attachments")));
        }
        if (jSONObject.has("view_hierarchy")) {
            m130e(jSONObject.getString("view_hierarchy"));
        }
        if (jSONObject.has("categories_list")) {
            m122a(jSONObject.getJSONArray("categories_list"));
        }
    }

    /* renamed from: h */
    public JSONArray m135h() {
        JSONArray jSONArray = new JSONArray();
        Iterator<String> it = this.f88m.iterator();
        while (it.hasNext()) {
            jSONArray.put(it.next());
        }
        return jSONArray;
    }

    /* renamed from: a */
    public void m122a(JSONArray jSONArray) throws JSONException {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(jSONArray.getString(i));
        }
        m125b(arrayList);
    }

    /* renamed from: i */
    public int m136i() {
        int i = 0;
        Iterator<Attachment> it = m131e().iterator();
        while (true) {
            int i2 = i;
            if (it.hasNext()) {
                Attachment next = it.next();
                i = (next.getType() == Attachment.Type.MAIN_SCREENSHOT || next.getType() == Attachment.Type.IMAGE || next.getType() == Attachment.Type.VIDEO || next.getType() == Attachment.Type.AUDIO) ? i2 + 1 : i2;
            } else {
                return i2;
            }
        }
    }

    /* renamed from: j */
    public boolean m137j() {
        Iterator<Attachment> it = m131e().iterator();
        while (it.hasNext()) {
            if (it.next().getType() == Attachment.Type.MAIN_SCREENSHOT) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: k */
    public boolean m138k() {
        return this.f85j;
    }

    /* renamed from: a */
    public Bug m119a(boolean z) {
        this.f85j = z;
        return this;
    }

    /* renamed from: l */
    public ViewHierarchyInspectionState m139l() {
        return this.f86k;
    }

    /* renamed from: a */
    public Bug m114a(ViewHierarchyInspectionState viewHierarchyInspectionState) {
        this.f86k = viewHierarchyInspectionState;
        return this;
    }

    /* renamed from: m */
    public List<C0471a> m140m() {
        return this.f87l;
    }

    /* renamed from: a */
    public void m121a(List<C0471a> list) {
        this.f87l = list;
    }

    /* renamed from: f */
    public void m133f(String str) {
        this.f88m.add(str);
    }

    /* renamed from: n */
    public String m141n() {
        return StringUtility.toCommaSeparated(this.f88m);
    }

    public String toString() {
        return "Internal Id: " + this.f77b + ", TemporaryServerToken:" + this.f78c + ", Message:" + this.f80e + ", Type:" + this.f79d;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Bug)) {
            return false;
        }
        Bug bug = (Bug) obj;
        if (!String.valueOf(bug.getId()).equals(String.valueOf(getId())) || !String.valueOf(bug.m129d()).equals(String.valueOf(m129d())) || !String.valueOf(bug.m120a()).equals(String.valueOf(m120a())) || bug.m132f() != m132f() || !bug.getState().equals(getState()) || bug.m126c() != m126c() || bug.m131e() == null || bug.m131e().size() != m131e().size()) {
            return false;
        }
        for (int i = 0; i < bug.m131e().size(); i++) {
            if (!bug.m131e().get(i).equals(m131e().get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return -1;
    }

    /* renamed from: com.instabug.bug.model.Bug$a */
    /* loaded from: classes.dex */
    public static class C0470a {
        /* renamed from: a */
        public Bug m142a(Context context) {
            final Bug bug = new Bug(System.currentTimeMillis() + "", new State.Builder(context).build(false), BugState.IN_PROGRESS);
            bug.m119a(InstabugCore.getFeatureState(Feature.VIEW_HIERARCHY) == Feature.State.ENABLED);
            if (InstabugCore.getFeatureState(Feature.REPRO_STEPS) == Feature.State.ENABLED && InstabugCore.isReproStepsScreenshotEnabled()) {
                VisualUserStepsHelper.getVisualUserStepsFileObservable(context, bug.getId()).subscribeOn(Schedulers.m2140io()).subscribe(new Action1<Uri>() { // from class: com.instabug.bug.model.Bug.a.1
                    @Override // p045rx.functions.Action1
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void call(Uri uri) {
                        bug.m112a(uri, Attachment.Type.VISUAL_USER_STEPS);
                    }
                });
            }
            return bug;
        }
    }
}
