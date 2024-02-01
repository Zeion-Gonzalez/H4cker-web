package com.instabug.chat.p006a;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import com.instabug.chat.C0507R;
import com.instabug.chat.ChatPlugin;
import com.instabug.chat.InstabugChat;
import com.instabug.chat.model.C0529c;
import com.instabug.chat.model.Message;
import com.instabug.chat.p006a.C0509a;
import com.instabug.chat.p009d.C0521c;
import com.instabug.chat.p011ui.C0540a;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.C0577R;
import com.instabug.library.Feature;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.util.InstabugAppData;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jcodec.codecs.common.biari.MQEncoder;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: NotificationManager.java */
/* renamed from: com.instabug.chat.a.b */
/* loaded from: classes.dex */
public class C0510b {

    /* renamed from: b */
    private static C0510b f297b;

    /* renamed from: a */
    private int f298a;

    /* renamed from: c */
    private final C0509a f299c = new C0509a();

    /* renamed from: d */
    private InstabugAppData f300d;

    /* renamed from: e */
    private List<Message> f301e;

    private C0510b() {
    }

    /* renamed from: a */
    public static C0510b m494a() {
        if (f297b == null) {
            f297b = new C0510b();
        }
        return f297b;
    }

    /* renamed from: a */
    public void m502a(Context context, List<Message> list) {
        String m496a;
        Intent m786a;
        Activity targetActivity;
        this.f300d = new InstabugAppData(context);
        this.f298a = m493a(list);
        this.f301e = list;
        switch (this.f298a) {
            case 0:
                Message message = list.get(list.size() - 1);
                m496a = m496a(context, 0, list);
                m786a = C0540a.m787a(context, message.m633b());
                break;
            case 1:
                m496a = m496a(context, 1, list);
                m786a = C0540a.m786a(context);
                break;
            default:
                m496a = "";
                m786a = null;
                break;
        }
        if (!InstabugCore.isAppOnForeground()) {
            m499a(context, m786a, m496a);
            return;
        }
        if (context instanceof Activity) {
            targetActivity = (Activity) context;
        } else {
            targetActivity = InstabugCore.getTargetActivity();
        }
        if (InstabugCore.isForegroundBusy()) {
            ChatPlugin chatPlugin = (ChatPlugin) InstabugCore.getXPlugin(ChatPlugin.class);
            if (chatPlugin != null && chatPlugin.getState() == 1) {
                m498a(targetActivity, list);
                return;
            } else {
                m499a(context, m786a, m496a);
                return;
            }
        }
        m498a(targetActivity, list);
    }

    /* renamed from: a */
    public void m501a(Context context) {
        final MediaPlayer create = MediaPlayer.create(context, C0577R.raw.new_message);
        if (Build.VERSION.SDK_INT >= 21) {
            create.setAudioAttributes(new AudioAttributes.Builder().setUsage(5).setContentType(4).build());
        } else {
            create.setAudioStreamType(5);
        }
        create.start();
        create.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.instabug.chat.a.b.1
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                create.release();
            }
        });
    }

    /* renamed from: a */
    private int m493a(@NonNull List<Message> list) {
        int i;
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        String m633b = list.get(0).m633b();
        Collections.sort(arrayList, new Message.C0525a(1));
        Iterator it = arrayList.iterator();
        String str = m633b;
        int i2 = 1;
        while (it.hasNext()) {
            String m633b2 = ((Message) it.next()).m633b();
            if (m633b2.equals(str)) {
                m633b2 = str;
                i = i2;
            } else {
                i = i2 + 1;
            }
            i2 = i;
            str = m633b2;
        }
        return i2 == 1 ? 0 : 1;
    }

    /* renamed from: a */
    private String m496a(Context context, int i, List<Message> list) {
        switch (i) {
            case 0:
                return list.get(list.size() - 1).m636c();
            case 1:
                return String.format(context.getResources().getString(C0577R.string.instabug_str_notifications_body), Integer.valueOf(list.size()), list.get(list.size() - 1).m642g().split(" ")[0]);
            default:
                return "";
        }
    }

    /* renamed from: a */
    private String m495a(Activity activity, int i, String str) {
        Resources resources = activity.getResources();
        switch (i) {
            case 0:
                return str + " (" + String.format(resources.getString(C0577R.string.instabug_str_notification_title), this.f300d.getAppName()) + ")";
            case 1:
                return String.format(resources.getString(C0507R.string.instabug_str_notification_title), this.f300d.getAppName());
            default:
                return "";
        }
    }

    /* renamed from: a */
    private void m499a(Context context, Intent intent, CharSequence charSequence) {
        int m744i = C0537a.m744i();
        if (m744i == -1 || m744i == 0) {
            m744i = this.f300d.getAppIcon();
        }
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, MQEncoder.CARRY_MASK);
        Uri defaultUri = RingtoneManager.getDefaultUri(2);
        NotificationCompat.Builder contentIntent = new NotificationCompat.Builder(context, "ibg_channel").setSmallIcon(m744i).setContentTitle(this.f300d.getAppName()).setContentText(charSequence).setAutoCancel(true).setChannelId("ibg_channel").setContentIntent(activity);
        if (Build.VERSION.SDK_INT >= 16) {
            contentIntent.setPriority(1);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            contentIntent.setVibrate(new long[0]);
        }
        if (C0537a.m742g()) {
            contentIntent.setSound(defaultUri);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = new NotificationChannel("ibg_channel", this.f300d.getAppName(), 4);
                notificationChannel.setImportance(4);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(0, contentIntent.build());
        }
    }

    /* renamed from: a */
    public void m503a(Bundle bundle) {
        if (InstabugCore.getFeatureState(Feature.PUSH_NOTIFICATION) == Feature.State.ENABLED && InstabugChat.isInstabugNotification(bundle)) {
            C0521c.m571a().m593b();
        }
    }

    /* renamed from: a */
    public void m504a(Map<String, String> map) {
        if (InstabugCore.getFeatureState(Feature.PUSH_NOTIFICATION) == Feature.State.ENABLED && InstabugChat.isInstabugNotification(map)) {
            C0521c.m571a().m593b();
        }
    }

    /* renamed from: b */
    public boolean m505b(Bundle bundle) {
        try {
            String string = new JSONObject(bundle.getString("message")).getString("IBGHost");
            InstabugSDKLogger.m1799d(this, "IBGHost: " + string);
            if (string != null) {
                if (Boolean.parseBoolean(string)) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            InstabugSDKLogger.m1801e(this, "Something went wrong while showing notification", e);
        } catch (JSONException e2) {
            InstabugSDKLogger.m1801e(this, "Parsing GCM response failed", e2);
        }
        return false;
    }

    /* renamed from: b */
    public boolean m506b(Map<String, String> map) {
        if (map.containsKey("message")) {
            try {
                String string = new JSONObject(map.get("message")).getString("IBGHost");
                if (string != null) {
                    if (Boolean.parseBoolean(string)) {
                        return true;
                    }
                }
            } catch (NullPointerException e) {
                InstabugSDKLogger.m1801e(this, "Something went wrong while showing notification", e);
            } catch (JSONException e2) {
                InstabugSDKLogger.m1801e(this, "Parsing GCM response failed", e2);
            }
        }
        return false;
    }

    /* renamed from: a */
    private void m498a(final Activity activity, List<Message> list) {
        C0529c c0529c;
        Message message = list.get(list.size() - 1);
        switch (this.f298a) {
            case 0:
                C0529c c0529c2 = new C0529c();
                c0529c2.m680a(m496a(activity, 0, list));
                c0529c2.m682b(m495a(activity, 0, message.m642g()));
                c0529c2.m684c(message.m643h());
                c0529c = c0529c2;
                break;
            case 1:
                C0529c c0529c3 = new C0529c();
                c0529c3.m680a(m496a(activity, 1, list));
                c0529c3.m682b(m495a(activity, 1, message.m642g()));
                c0529c3.m684c(message.m643h());
                c0529c = c0529c3;
                break;
            default:
                c0529c = null;
                break;
        }
        this.f299c.m487a(activity, c0529c, new C0509a.b() { // from class: com.instabug.chat.a.b.2
            @Override // com.instabug.chat.p006a.C0509a.b
            /* renamed from: a */
            public void mo491a() {
                C0510b.this.m497a(activity);
            }

            @Override // com.instabug.chat.p006a.C0509a.b
            /* renamed from: b */
            public void mo492b() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m497a(Activity activity) {
        switch (this.f298a) {
            case 0:
                activity.startActivity(C0540a.m787a(activity, this.f301e.get(this.f301e.size() - 1).m633b()));
                return;
            case 1:
                activity.startActivity(C0540a.m786a(activity));
                return;
            default:
                return;
        }
    }
}
