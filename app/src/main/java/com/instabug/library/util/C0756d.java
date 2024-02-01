package com.instabug.library.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/* compiled from: ShakeDetector.java */
/* renamed from: com.instabug.library.util.d */
/* loaded from: classes.dex */
public class C0756d implements SensorEventListener {

    /* renamed from: a */
    private SensorManager f1256a;

    /* renamed from: b */
    private Sensor f1257b;

    /* renamed from: d */
    private float f1259d;

    /* renamed from: e */
    private float f1260e;

    /* renamed from: f */
    private float f1261f;

    /* renamed from: g */
    private a f1262g;

    /* renamed from: c */
    private long f1258c = 0;

    /* renamed from: h */
    private int f1263h = 350;

    /* compiled from: ShakeDetector.java */
    /* renamed from: com.instabug.library.util.d$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: c */
        void mo1499c();
    }

    public C0756d(Context context, a aVar) {
        this.f1256a = (SensorManager) context.getSystemService("sensor");
        this.f1257b = this.f1256a.getDefaultSensor(1);
        m1813a(aVar);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1) {
            float f = sensorEvent.values[0];
            float f2 = sensorEvent.values[1];
            float f3 = sensorEvent.values[2];
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.f1258c > 400) {
                if ((Math.abs(((((f + f2) + f3) - this.f1259d) - this.f1260e) - this.f1261f) / ((float) (currentTimeMillis - this.f1258c))) * 10000.0f > this.f1263h) {
                    this.f1262g.mo1499c();
                }
                this.f1258c = currentTimeMillis;
                this.f1259d = f;
                this.f1260e = f2;
                this.f1261f = f3;
            }
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /* renamed from: a */
    public void m1813a(a aVar) {
        this.f1262g = aVar;
    }

    /* renamed from: a */
    public void m1811a() {
        this.f1256a.registerListener(this, this.f1257b, 3);
    }

    /* renamed from: b */
    public void m1814b() {
        this.f1256a.unregisterListener(this);
    }

    /* renamed from: a */
    public void m1812a(int i) {
        this.f1263h = i;
    }
}
