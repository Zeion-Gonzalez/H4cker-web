package com.instabug.survey.network.service;

import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.survey.cache.SurveysCacheManager;
import com.instabug.survey.p032a.C0769c;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugSurveysSubmitterService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        InstabugSDKLogger.m1799d(this, "runBackgroundTask started");
        m2031a();
    }

    /* renamed from: a */
    private void m2031a() throws IOException, JSONException {
        InstabugSDKLogger.m1799d(this, "submitSurveys started");
        List<C0769c> answeredAndNotSubmittedSurveys = SurveysCacheManager.getAnsweredAndNotSubmittedSurveys();
        InstabugSDKLogger.m1799d(this, "answeredSurveys size: " + answeredAndNotSubmittedSurveys.size());
        for (final C0769c c0769c : answeredAndNotSubmittedSurveys) {
            C0784a.m2034a().m2036a(this, c0769c, new Request.Callbacks<Boolean, Throwable>() { // from class: com.instabug.survey.network.service.InstabugSurveysSubmitterService.1
                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onSucceeded(Boolean bool) {
                    c0769c.m1951b(true);
                    SurveysCacheManager.saveCacheToDisk();
                }

                @Override // com.instabug.library.network.Request.Callbacks
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public void onFailed(Throwable th) {
                    InstabugSDKLogger.m1801e(this, th.getMessage(), th);
                }
            });
        }
    }
}
