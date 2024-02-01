package com.instabug.chat.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.chat.model.Message;
import com.instabug.library.C0577R;
import com.instabug.library.internal.storage.cache.Cacheable;
import com.instabug.library.model.BaseReport;
import com.instabug.library.model.State;
import com.instabug.library.util.InstabugAppData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Chat extends BaseReport implements Cacheable {

    /* renamed from: a */
    private String f339a;
    @Nullable

    /* renamed from: b */
    private State f340b;

    /* renamed from: c */
    private ArrayList<Message> f341c;

    /* renamed from: d */
    private ChatState f342d;

    /* loaded from: classes.dex */
    public enum ChatState {
        WAITING_ATTACHMENT_MESSAGE,
        READY_TO_BE_SENT,
        LOGS_READY_TO_BE_UPLOADED,
        SENT,
        NOT_AVAILABLE
    }

    public Chat(@NonNull String str, @NonNull State state, @NonNull ChatState chatState) {
        this.f339a = str;
        this.f340b = state;
        this.f342d = chatState;
        this.f341c = new ArrayList<>();
    }

    public Chat(@NonNull String str) {
        this.f339a = str;
        this.f341c = new ArrayList<>();
        m604a(ChatState.SENT);
    }

    public Chat() {
        this.f342d = ChatState.NOT_AVAILABLE;
        this.f341c = new ArrayList<>();
    }

    @Override // com.instabug.library.model.BaseReport
    public String getId() {
        return this.f339a;
    }

    @Override // com.instabug.library.model.BaseReport
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Chat setId(String str) {
        this.f339a = str;
        m603k();
        return this;
    }

    /* renamed from: a */
    public ArrayList<Message> m609a() {
        return this.f341c;
    }

    /* renamed from: a */
    public Chat m607a(ArrayList<Message> arrayList) {
        this.f341c = arrayList;
        m603k();
        return this;
    }

    @Override // com.instabug.library.model.BaseReport
    @Nullable
    public State getState() {
        return this.f340b;
    }

    @Override // com.instabug.library.model.BaseReport
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Chat setState(@Nullable State state) {
        this.f340b = state;
        return this;
    }

    /* renamed from: b */
    public ChatState m610b() {
        return this.f342d;
    }

    /* renamed from: a */
    public Chat m604a(ChatState chatState) {
        this.f342d = chatState;
        return this;
    }

    /* renamed from: c */
    public int m611c() {
        int i = 0;
        Iterator<Message> it = this.f341c.iterator();
        while (true) {
            int i2 = i;
            if (it.hasNext()) {
                i = !it.next().m638d() ? i2 + 1 : i2;
            } else {
                return i2;
            }
        }
    }

    /* renamed from: d */
    public void m612d() {
        for (int size = this.f341c.size() - 1; size >= 0; size--) {
            this.f341c.get(size).m628a(true);
        }
    }

    @Nullable
    /* renamed from: e */
    public String m613e() {
        Message m602j = m602j();
        if (m602j != null) {
            return m602j.m643h();
        }
        return null;
    }

    /* renamed from: f */
    public String m614f() {
        Message m602j = m602j();
        if (m602j != null) {
            return m602j.m642g();
        }
        if (this.f341c.size() != 0) {
            return this.f341c.get(this.f341c.size() - 1).m642g();
        }
        return "";
    }

    @Nullable
    /* renamed from: j */
    private Message m602j() {
        Message m617i = m617i();
        if (m617i != null && m617i.m648m()) {
            Iterator<Message> it = this.f341c.iterator();
            while (it.hasNext()) {
                Message next = it.next();
                if (!next.m648m()) {
                    return next;
                }
            }
            return null;
        }
        return m617i;
    }

    /* renamed from: g */
    public long m615g() {
        if (m616h() != null) {
            return m616h().m641f();
        }
        return 0L;
    }

    @Nullable
    /* renamed from: h */
    public Message m616h() {
        if (this.f341c.size() == 0) {
            return null;
        }
        Collections.sort(this.f341c, new Message.C0525a(2));
        return this.f341c.get(this.f341c.size() - 1);
    }

    @Nullable
    /* renamed from: i */
    public Message m617i() {
        for (int size = this.f341c.size() - 1; size >= 0; size--) {
            if (this.f341c.get(size).m644i() == Message.MessageState.SYNCED) {
                return this.f341c.get(size);
            }
        }
        return null;
    }

    /* renamed from: k */
    private void m603k() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < m609a().size()) {
                m609a().get(i2).m631b(this.f339a);
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    /* renamed from: a */
    public String m608a(Context context) {
        String m614f = m614f();
        return (m614f == null || m614f.equals("") || m614f.equals(" ") || m614f.equals("null") || m616h() == null || m616h().m648m()) ? String.format(context.getString(C0577R.string.instabug_str_notification_title), new InstabugAppData(context).getAppName()) : m614f;
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public String toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", getId()).put("messages", Message.m621a(m609a())).put("chat_state", m610b().toString());
        if (getState() != null) {
            jSONObject.put("state", getState().toJson());
        }
        return jSONObject.toString();
    }

    @Override // com.instabug.library.internal.storage.cache.Cacheable
    public void fromJson(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("id")) {
            setId(jSONObject.getString("id"));
        }
        if (jSONObject.has("messages")) {
            m607a(Message.m620a(jSONObject.getJSONArray("messages")));
        }
        if (jSONObject.has("chat_state")) {
            m604a(ChatState.valueOf(jSONObject.getString("chat_state")));
        }
        if (jSONObject.has("state")) {
            State state = new State();
            state.fromJson(jSONObject.getString("state"));
            setState(state);
        }
    }

    public String toString() {
        return "Chat:[" + this.f339a + " chatState: " + m610b() + ", " + this.f341c + "]";
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Chat)) {
            return false;
        }
        Chat chat = (Chat) obj;
        if (!String.valueOf(chat.getId()).equals(getId()) || chat.m610b() != m610b()) {
            return false;
        }
        if ((chat.getState() != null || getState() != null) && !chat.getState().equals(getState())) {
            return false;
        }
        for (int i = 0; i < chat.m609a().size(); i++) {
            if (!chat.m609a().get(i).equals(m609a().get(i))) {
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

    /* renamed from: com.instabug.chat.model.Chat$a */
    /* loaded from: classes.dex */
    public static class C0523a implements Serializable, Comparator<Chat> {
        @Override // java.util.Comparator
        /* renamed from: a  reason: merged with bridge method [inline-methods] */
        public int compare(Chat chat, Chat chat2) {
            return new Date(chat.m615g()).compareTo(new Date(chat2.m615g()));
        }
    }

    /* renamed from: com.instabug.chat.model.Chat$b */
    /* loaded from: classes.dex */
    public static class C0524b {
        /* renamed from: a */
        public Chat m619a(Context context) {
            return new Chat(System.currentTimeMillis() + "", new State.Builder(context).build(true), ChatState.READY_TO_BE_SENT);
        }
    }
}
