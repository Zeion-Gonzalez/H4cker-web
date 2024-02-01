package com.instabug.chat.model;

import android.support.annotation.NonNull;
import com.instabug.library.internal.storage.cache.Cacheable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Message implements Cacheable, Serializable {

    /* renamed from: a */
    private String f343a;

    /* renamed from: b */
    private String f344b;

    /* renamed from: c */
    private String f345c;

    /* renamed from: d */
    private String f346d;

    /* renamed from: e */
    private String f347e;

    /* renamed from: f */
    private long f348f;

    /* renamed from: g */
    private boolean f349g;

    /* renamed from: h */
    private long f350h;

    /* renamed from: i */
    private ArrayList<Attachment> f351i;

    /* renamed from: j */
    private ArrayList<C0528b> f352j;

    /* renamed from: k */
    private EnumC0526b f353k;

    /* renamed from: l */
    private MessageState f354l;

    /* loaded from: classes.dex */
    public enum MessageState {
        STAY_OFFLINE,
        READY_TO_BE_SENT,
        SENT,
        READY_TO_BE_SYNCED,
        SYNCED,
        NOT_AVAILABLE
    }

    public Message() {
        this(String.valueOf(System.currentTimeMillis()));
    }

    public Message(String str) {
        this.f343a = str;
        this.f351i = new ArrayList<>();
        this.f352j = new ArrayList<>();
        this.f353k = EnumC0526b.NOT_AVAILABLE;
        this.f354l = MessageState.NOT_AVAILABLE;
    }

    /* renamed from: a */
    public static JSONArray m621a(ArrayList<Message> arrayList) throws JSONException {
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

    /* renamed from: a */
    public static ArrayList<Message> m620a(JSONArray jSONArray) throws JSONException {
        ArrayList<Message> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            Message message = new Message();
            message.fromJson(jSONArray.getJSONObject(i).toString());
            arrayList.add(message);
        }
        return arrayList;
    }

    /* renamed from: a */
    public String m629a() {
        return this.f343a;
    }

    /* renamed from: a */
    public Message m627a(String str) {
        this.f343a = str;
        return this;
    }

    /* renamed from: b */
    public String m633b() {
        return this.f344b;
    }

    /* renamed from: b */
    public Message m631b(String str) {
        this.f344b = str;
        return this;
    }

    /* renamed from: c */
    public String m636c() {
        return this.f345c;
    }

    /* renamed from: c */
    public Message m634c(String str) {
        this.f345c = str;
        return this;
    }

    /* renamed from: d */
    public boolean m638d() {
        return this.f349g;
    }

    /* renamed from: a */
    public Message m628a(boolean z) {
        this.f349g = z;
        return this;
    }

    /* renamed from: e */
    public long m639e() {
        return this.f350h;
    }

    /* renamed from: a */
    public Message m622a(long j) {
        this.f350h = j;
        if (j != 0) {
            this.f349g = true;
        }
        return this;
    }

    /* renamed from: f */
    public long m641f() {
        return this.f348f;
    }

    /* renamed from: b */
    public Message m630b(long j) {
        this.f348f = j;
        return this;
    }

    /* renamed from: g */
    public String m642g() {
        return this.f346d;
    }

    /* renamed from: d */
    public Message m637d(String str) {
        this.f346d = str;
        return this;
    }

    /* renamed from: h */
    public String m643h() {
        return this.f347e;
    }

    /* renamed from: e */
    public Message m640e(String str) {
        this.f347e = str;
        return this;
    }

    /* renamed from: i */
    public MessageState m644i() {
        return this.f354l;
    }

    /* renamed from: a */
    public Message m624a(MessageState messageState) {
        this.f354l = messageState;
        return this;
    }

    /* renamed from: j */
    public ArrayList<Attachment> m645j() {
        return this.f351i;
    }

    /* renamed from: b */
    public Message m632b(@NonNull ArrayList<Attachment> arrayList) {
        this.f351i = arrayList;
        return this;
    }

    /* renamed from: a */
    public Message m623a(Attachment attachment) {
        this.f351i.add(attachment);
        return this;
    }

    /* renamed from: k */
    public ArrayList<C0528b> m646k() {
        return this.f352j;
    }

    /* renamed from: c */
    public Message m635c(ArrayList<C0528b> arrayList) {
        this.f352j = arrayList;
        return this;
    }

    /* renamed from: a */
    public Message m626a(C0528b c0528b) {
        this.f352j.add(c0528b);
        return this;
    }

    /* renamed from: l */
    public EnumC0526b m647l() {
        return this.f353k;
    }

    /* renamed from: a */
    public Message m625a(EnumC0526b enumC0526b) {
        this.f353k = enumC0526b;
        if (enumC0526b == EnumC0526b.INBOUND) {
            this.f349g = true;
        }
        return this;
    }

    /* renamed from: m */
    public boolean m648m() {
        return this.f353k != null && this.f353k == EnumC0526b.INBOUND;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", m629a()).put("chat_id", m633b()).put("body", m636c()).put("sender_name", m642g()).put("sender_avatar_url", m643h()).put("messaged_at", m641f()).put("read", m638d()).put("read_at", m639e()).put("messages_state", m644i().toString()).put("direction", m647l().toString()).put("attachments", Attachment.toJson(m645j())).put("actions", C0528b.m672a(m646k()));
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        EnumC0526b enumC0526b;
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            m627a(jSONObject.getString("id"));
        }
        if (jSONObject.has("chat_id")) {
            m631b(jSONObject.getString("chat_id"));
        }
        if (jSONObject.has("body")) {
            m634c(jSONObject.getString("body"));
        }
        if (jSONObject.has("sender_name")) {
            m637d(jSONObject.getString("sender_name"));
        }
        if (jSONObject.has("sender_avatar_url")) {
            m640e(jSONObject.getString("sender_avatar_url"));
        }
        if (jSONObject.has("messaged_at")) {
            m630b(jSONObject.getLong("messaged_at"));
        }
        if (jSONObject.has("read")) {
            m628a(jSONObject.getBoolean("read"));
        }
        if (jSONObject.has("read_at")) {
            m622a(jSONObject.getLong("read_at"));
        }
        if (jSONObject.has("attachments")) {
            m632b(Attachment.fromJson(jSONObject.getJSONArray("attachments")));
        }
        if (jSONObject.has("actions")) {
            m635c(C0528b.m671a(jSONObject.getJSONArray("actions")));
        }
        if (jSONObject.has("direction")) {
            String string = jSONObject.getString("direction");
            char c = 65535;
            switch (string.hashCode()) {
                case 57076464:
                    if (string.equals("outbound")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1941740409:
                    if (string.equals("inbound")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    enumC0526b = EnumC0526b.INBOUND;
                    break;
                case 1:
                    enumC0526b = EnumC0526b.OUTBOUND;
                    break;
                default:
                    enumC0526b = EnumC0526b.NOT_AVAILABLE;
                    break;
            }
            m625a(enumC0526b);
        }
        if (jSONObject.has("messages_state")) {
            m624a(MessageState.valueOf(jSONObject.getString("messages_state")));
        }
    }

    public String toString() {
        return "Message:[" + this.f343a + ", " + this.f344b + ", " + this.f345c + ", " + this.f348f + ", " + this.f350h + ", " + this.f346d + ", " + this.f347e + ", " + this.f354l + ", " + this.f353k + ", " + this.f349g + ", " + this.f351i + "]";
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Message)) {
            return false;
        }
        Message message = (Message) obj;
        if (!String.valueOf(message.m629a()).equals(String.valueOf(m629a())) || !String.valueOf(message.m633b()).equals(String.valueOf(m633b())) || !String.valueOf(message.m642g()).equals(String.valueOf(m642g())) || !String.valueOf(message.m643h()).equals(String.valueOf(m643h())) || !String.valueOf(message.m636c()).equals(String.valueOf(m636c())) || message.m641f() != m641f() || message.m644i() != m644i() || message.m647l() != m647l() || message.m648m() != m648m() || message.m638d() != m638d() || message.m639e() != m639e() || message.m645j() == null || message.m645j().size() != m645j().size() || message.m646k() == null || message.m646k().size() != m646k().size()) {
            return false;
        }
        for (int i = 0; i < message.m645j().size(); i++) {
            if (!message.m645j().get(i).equals(m645j().get(i))) {
                return false;
            }
        }
        for (int i2 = 0; i2 < message.m646k().size(); i2++) {
            if (!message.m646k().get(i2).equals(m646k().get(i2))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (m629a() != null) {
            return m629a().hashCode();
        }
        return -1;
    }

    /* renamed from: com.instabug.chat.model.Message$b */
    /* loaded from: classes.dex */
    public enum EnumC0526b {
        INBOUND("inbound"),
        OUTBOUND("outbound"),
        NOT_AVAILABLE("not-available");


        /* renamed from: d */
        private final String f360d;

        EnumC0526b(String str) {
            this.f360d = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.f360d;
        }
    }

    /* renamed from: com.instabug.chat.model.Message$a */
    /* loaded from: classes.dex */
    public static class C0525a implements Serializable, Comparator<Message> {

        /* renamed from: a */
        private int f355a;

        public C0525a() {
            this.f355a = 2;
        }

        public C0525a(int i) {
            this.f355a = 2;
            this.f355a = i;
        }

        @Override // java.util.Comparator
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public int compare(Message message, Message message2) {
            switch (this.f355a) {
                case 1:
                    return message.m633b().compareTo(message2.m633b());
                case 2:
                    return new Date(message.m641f()).compareTo(new Date(message2.m641f()));
                default:
                    throw new IllegalStateException("Message comparator wasn't provided comparison messageIssueType");
            }
        }
    }
}
