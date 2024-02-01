package com.applisto.appcloner.classes;

import android.app.PendingIntent;
import android.content.Intent;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.actions.ControlAction;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ToggleTemplate;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import org.jcodec.codecs.common.biari.MQEncoder;

/* loaded from: classes2.dex */
public class MyControlsProviderService extends ControlsProviderService {
    private static final String CONTROL_ID = "button";

    @Override // android.service.controls.ControlsProviderService
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        return new Flow.Publisher() { // from class: com.applisto.appcloner.classes.-$$Lambda$MyControlsProviderService$luIUy2oGcJKQHTsjy3oamdtHsqc
            @Override // java.util.concurrent.Flow.Publisher
            public final void subscribe(Flow.Subscriber subscriber) {
                MyControlsProviderService.this.m10xd04c88f8(subscriber);
            }
        };
    }

    /* renamed from: lambda$createPublisherForAllAvailable$0$MyControlsProviderService */
    public /* synthetic */ void m10xd04c88f8(Flow.Subscriber subscriber) {
        Iterator<Control> it = getControls().iterator();
        while (it.hasNext()) {
            subscriber.onNext(it.next());
        }
        subscriber.onComplete();
    }

    @Override // android.service.controls.ControlsProviderService
    public Flow.Publisher<Control> createPublisherFor(final List<String> list) {
        Log.i("ControlsProviderService", "createPublisherFor; controlIds: " + list);
        return new Flow.Publisher() { // from class: com.applisto.appcloner.classes.-$$Lambda$MyControlsProviderService$faiAKkD_BFjHM0W_5Z6KfJjxteI
            @Override // java.util.concurrent.Flow.Publisher
            public final void subscribe(Flow.Subscriber subscriber) {
                MyControlsProviderService.this.lambda$createPublisherFor$1$MyControlsProviderService(list, subscriber);
            }
        };
    }

    public /* synthetic */ void lambda$createPublisherFor$1$MyControlsProviderService(List list, Flow.Subscriber subscriber) {
        for (Control control : getControls()) {
            if (list.contains(control.getControlId())) {
                subscriber.onSubscribe(new Flow.Subscription() { // from class: com.applisto.appcloner.classes.MyControlsProviderService.1
                    @Override // java.util.concurrent.Flow.Subscription
                    public void request(long j) {
                        Log.i("ControlsProviderService", "request; ");
                    }

                    @Override // java.util.concurrent.Flow.Subscription
                    public void cancel() {
                        Log.i("ControlsProviderService", "cancel; ");
                    }
                });
                subscriber.onNext(control);
            }
        }
    }

    @Override // android.service.controls.ControlsProviderService
    public void performControlAction(String str, ControlAction controlAction, Consumer<Integer> consumer) {
        Log.i("ControlsProviderService", "performControlAction; controlId: " + str);
        if (CONTROL_ID.equals(str)) {
            try {
                Intent launchIntent = Utils.getLaunchIntent(this, getPackageName());
                Log.i("ControlsProviderService", "performControlAction; i: " + launchIntent);
                if (launchIntent != null) {
                    launchIntent.addFlags(268435456);
                    startActivity(launchIntent);
                }
            } catch (Exception e) {
                Log.w("ControlsProviderService", e);
            }
        }
        consumer.accept(1);
    }

    private List<Control> getControls() {
        ArrayList arrayList = new ArrayList();
        Intent launchIntent = Utils.getLaunchIntent(this, getPackageName());
        if (launchIntent != null) {
            Control.StatefulBuilder statefulBuilder = new Control.StatefulBuilder(CONTROL_ID, PendingIntent.getActivity(this, 0, launchIntent, MQEncoder.CARRY_MASK));
            statefulBuilder.setTitle(Utils.getAppName(this));
            statefulBuilder.setDeviceType(52);
            statefulBuilder.setControlId(CONTROL_ID);
            statefulBuilder.setStatus(1);
            statefulBuilder.setControlTemplate(new ToggleTemplate(CONTROL_ID, new ControlButton(true, CONTROL_ID)));
            arrayList.add(statefulBuilder.build());
        }
        return arrayList;
    }
}
