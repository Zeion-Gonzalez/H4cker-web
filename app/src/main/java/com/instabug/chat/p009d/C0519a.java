package com.instabug.chat.p009d;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.C0528b;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.model.State;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: NewMessagesHandler.java */
/* renamed from: com.instabug.chat.d.a */
/* loaded from: classes.dex */
public class C0519a {

    /* renamed from: a */
    private static C0519a f319a;

    /* renamed from: b */
    private List<InterfaceC0520b> f320b = new ArrayList();

    /* renamed from: a */
    public static C0519a m549a() {
        if (f319a == null) {
            f319a = new C0519a();
        }
        return f319a;
    }

    /* renamed from: a */
    public void m567a(Context context, boolean z, JSONObject... jSONObjectArr) {
        List<Message> m553a = m553a(jSONObjectArr);
        List<Message> m551a = m551a(m553a);
        if (z) {
            m559b(context, m553a);
        } else {
            m554a(context, m553a);
        }
        if (m551a.size() > 0) {
            m558b();
        }
        if (this.f320b.size() > 0) {
            m560b(m551a);
            return;
        }
        throw new IllegalStateException("No one is listening for unread messages");
    }

    /* renamed from: a */
    private void m554a(Context context, List<Message> list) {
        InstabugSDKLogger.m1803v(this, "new messages count: " + list.size());
        for (Message message : list) {
            InstabugSDKLogger.m1803v(this, "new message to updating: " + message.toString());
            if (m562d(message)) {
                m563e(message);
            } else if (m565g(message)) {
                InstabugSDKLogger.m1803v(this, "Message:" + message + " is ready to be synced");
                try {
                    ChatsCacheManager.updateLocalMessageWithSyncedMessage(context, message);
                } catch (IOException e) {
                    InstabugSDKLogger.m1801e(this, "Failed to update local message: " + m561c(message) + " with synced message: " + message, e);
                }
            }
        }
    }

    /* renamed from: b */
    private void m559b(Context context, List<Message> list) {
        InstabugSDKLogger.m1803v(this, "START Invalidate Cache");
        List<Message> notSentMessages = ChatsCacheManager.getNotSentMessages();
        InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
        if (cache != null) {
            cache.invalidate();
        }
        InstabugSDKLogger.m1803v(this, "finish Invalidate Cache");
        m554a(context, m552a(list, notSentMessages));
    }

    /* renamed from: a */
    private List<Message> m552a(List<Message> list, List<Message> list2) {
        ArrayList arrayList = new ArrayList(list);
        for (Message message : list2) {
            if (m555a(message, list)) {
                arrayList.add(message);
            }
            if (message.m644i() == Message.MessageState.SENT && m556b(message, list) != null) {
                arrayList.remove(m556b(message, list));
            }
        }
        return arrayList;
    }

    /* renamed from: a */
    private boolean m555a(Message message, List<Message> list) {
        Iterator<Message> it = list.iterator();
        while (it.hasNext()) {
            if (message.m633b().equals(it.next().m633b())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    /* renamed from: b */
    private Message m556b(Message message, List<Message> list) {
        for (Message message2 : list) {
            if (message.m629a().equals(message2.m629a())) {
                return message2;
            }
        }
        return null;
    }

    /* renamed from: a */
    private List<Message> m551a(List<Message> list) {
        ArrayList arrayList = new ArrayList(list);
        for (Message message : list) {
            if (m564f(message) || m565g(message) || m566h(message) || message.m648m() || message.m638d()) {
                InstabugSDKLogger.m1799d(this, "Message " + message.toString() + " removed from list to be notified");
                arrayList.remove(message);
            }
        }
        return arrayList;
    }

    @Nullable
    /* renamed from: a */
    private Chat m550a(Message message) {
        Chat chat;
        InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
        if (cache == null || (chat = cache.get(message.m633b())) == null) {
            InstabugSDKLogger.m1803v(this, "No local chats match messages's chat");
            return null;
        }
        return chat;
    }

    @Nullable
    /* renamed from: b */
    private List<Message> m557b(Message message) {
        Chat m550a = m550a(message);
        if (m550a == null) {
            return null;
        }
        return m550a.m609a();
    }

    @Nullable
    /* renamed from: c */
    private Message m561c(Message message) {
        List<Message> m557b = m557b(message);
        if (m557b != null) {
            for (Message message2 : m557b) {
                if (message2.m629a().equals(message.m629a())) {
                    return message2;
                }
            }
        }
        return null;
    }

    /* renamed from: d */
    private boolean m562d(Message message) {
        return m561c(message) == null;
    }

    /* renamed from: e */
    private void m563e(Message message) {
        Chat m550a = m550a(message);
        if (m550a == null) {
            InstabugSDKLogger.m1803v(this, "Chat with id " + message.m633b() + " doesn't exist, creating new one");
            m550a = new Chat(message.m633b());
        }
        m550a.m609a().add(message);
        InstabugSDKLogger.m1799d(this, "Message " + message + " added to cached chat: " + m550a);
        InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
        if (cache != null) {
            cache.put(m550a.getId(), m550a);
        }
    }

    /* renamed from: f */
    private boolean m564f(Message message) {
        Message m561c = m561c(message);
        return m561c != null && m561c.m629a().equals(message.m629a()) && m561c.m644i().equals(Message.MessageState.SYNCED) && m561c.m645j().size() == message.m645j().size();
    }

    /* renamed from: g */
    private boolean m565g(Message message) {
        Message m561c = m561c(message);
        return m561c != null && m561c.m629a().equals(message.m629a()) && m561c.m644i().equals(Message.MessageState.READY_TO_BE_SYNCED) && m561c.m645j().size() == message.m645j().size();
    }

    /* renamed from: h */
    private boolean m566h(Message message) {
        Message m561c = m561c(message);
        return m561c != null && m561c.m629a().equals(message.m629a()) && m561c.m644i().equals(Message.MessageState.SENT) && m561c.m645j().size() != message.m645j().size();
    }

    /* renamed from: b */
    private void m560b(List<Message> list) {
        if (C0537a.m741f()) {
            InstabugSDKLogger.m1803v(this, "Number of listeners to notify " + this.f320b.size());
            for (int size = this.f320b.size() - 1; size >= 0; size--) {
                InterfaceC0520b interfaceC0520b = this.f320b.get(size);
                InstabugSDKLogger.m1799d(this, "Notifying listener " + interfaceC0520b);
                if (list != null && list.size() > 0) {
                    InstabugSDKLogger.m1799d(this, "Notifying listener with " + list.size() + " message(s)");
                    list = interfaceC0520b.onNewMessagesReceived(list);
                    InstabugSDKLogger.m1799d(this, "Notified listener remained " + (list != null ? Integer.valueOf(list.size()) : null) + " message(s) to be sent to next listener");
                } else {
                    return;
                }
            }
            return;
        }
        InstabugSDKLogger.m1803v(this, "Chat notification disabled, messages that would not be notified " + list);
    }

    /* renamed from: a */
    public void m568a(InterfaceC0520b interfaceC0520b) {
        if (!this.f320b.contains(interfaceC0520b)) {
            this.f320b.add(interfaceC0520b);
        }
    }

    /* renamed from: b */
    public void m569b(InterfaceC0520b interfaceC0520b) {
        this.f320b.remove(interfaceC0520b);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: a */
    private List<Message> m553a(JSONObject[] jSONObjectArr) {
        boolean z;
        char c;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONObjectArr.length; i++) {
            try {
                JSONObject jSONObject = jSONObjectArr[i];
                JSONArray jSONArray = jSONObject.getJSONArray("attachments");
                JSONArray jSONArray2 = jSONObject.getJSONArray("actions");
                Message m624a = new Message(jSONObject.getString("id")).m631b(jSONObject.getString("chat_number")).m634c(jSONObject.getString("body")).m637d(jSONObject.getJSONObject("from").getString("name")).m640e(jSONObject.getString("avatar")).m624a(Message.MessageState.SYNCED);
                if (jSONObject.getString("messaged_at") != null && !jSONObject.getString("messaged_at").equals("") && !jSONObject.getString("messaged_at").equals("null") && InstabugDateFormatter.getDate(jSONObject.getString("messaged_at")) != null) {
                    m624a.m630b(InstabugDateFormatter.getDate(jSONObject.getString("messaged_at")).getTime() / 1000);
                }
                if (jSONObject.getString("read_at") != null && !jSONObject.getString("read_at").equals("") && !jSONObject.getString("read_at").equals("null") && InstabugDateFormatter.getDate(jSONObject.getString("read_at")) != null) {
                    m624a.m622a(InstabugDateFormatter.getDate(jSONObject.getString("read_at")).getTime() / 1000);
                }
                String string = jSONObject.getString("direction");
                switch (string.hashCode()) {
                    case 57076464:
                        if (string.equals("outbound")) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    case 1941740409:
                        if (string.equals("inbound")) {
                            z = false;
                            break;
                        }
                        z = true;
                        break;
                    default:
                        z = true;
                        break;
                }
                switch (z) {
                    case false:
                        m624a.m625a(Message.EnumC0526b.INBOUND);
                        break;
                    case true:
                        m624a.m625a(Message.EnumC0526b.OUTBOUND);
                        break;
                }
                for (int length = jSONArray.length() - 1; length >= 0; length--) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(length);
                    JSONObject jSONObject3 = jSONObject2.getJSONObject("metadata");
                    Attachment attachment = new Attachment();
                    attachment.setUrl(jSONObject2.getString("url"));
                    attachment.setState(Attachment.STATE_SYNCED);
                    String string2 = jSONObject3.getString("file_type");
                    switch (string2.hashCode()) {
                        case 93166550:
                            if (string2.equals(Attachment.TYPE_AUDIO)) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 100313435:
                            if (string2.equals(Attachment.TYPE_IMAGE)) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 112202875:
                            if (string2.equals(Attachment.TYPE_VIDEO)) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 216616442:
                            if (string2.equals("main-screenshot")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            attachment.setType(Attachment.TYPE_AUDIO);
                            attachment.setDuration(jSONObject3.getString(State.KEY_DURATION));
                            break;
                        case 1:
                            attachment.setType(Attachment.TYPE_VIDEO);
                            break;
                        case 2:
                            attachment.setType(Attachment.TYPE_IMAGE);
                            break;
                        case 3:
                            attachment.setType(Attachment.TYPE_IMAGE);
                            break;
                    }
                    m624a.m645j().add(attachment);
                }
                for (int length2 = jSONArray2.length() - 1; length2 >= 0; length2--) {
                    JSONObject jSONObject4 = jSONArray2.getJSONObject(length2);
                    C0528b c0528b = new C0528b();
                    c0528b.fromJson(jSONObject4.toString());
                    m624a.m626a(c0528b);
                }
                arrayList.add(m624a);
            } catch (JSONException e) {
                InstabugSDKLogger.m1801e(this, "Failed to parse message number " + i, e);
            }
        }
        return arrayList;
    }

    /* renamed from: b */
    private void m558b() {
        if (C0537a.m721a() != null) {
            try {
                new Handler(Looper.getMainLooper()).post(C0537a.m721a());
            } catch (Exception e) {
                InstabugSDKLogger.m1801e(this, "new message runnable failed to run.", e);
            }
        }
    }
}
