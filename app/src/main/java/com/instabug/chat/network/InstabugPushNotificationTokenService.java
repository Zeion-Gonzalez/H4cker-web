package com.instabug.chat.network;

import com.instabug.chat.network.p010a.C0536a;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugPushNotificationTokenService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        m705a();
    }

    /* renamed from: a */
    private void m705a() {
        try {
            C0536a.m708a().m713a(this, C0537a.m747l(), new Request.Callbacks<String, Throwable>() { // from class: com.instabug.chat.network.InstabugPushNotificationTokenService.1
                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onSucceeded(String str) {
                    if (str.equalsIgnoreCase("ok")) {
                        C0537a.m740f(true);
                    } else {
                        C0537a.m740f(false);
                    }
                }

                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onFailed(Throwable th) {
                    C0537a.m740f(false);
                }
            });
        } catch (IOException e) {
            C0537a.m740f(false);
            InstabugSDKLogger.m1799d(this, "sending push notification token got error: " + e.getMessage());
        } catch (JSONException e2) {
            C0537a.m740f(false);
            InstabugSDKLogger.m1799d(this, "sending push notification token got error: " + e2.getMessage());
        }
    }
}
