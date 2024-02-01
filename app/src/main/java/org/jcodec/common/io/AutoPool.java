package org.jcodec.common.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class AutoPool {
    private static AutoPool instance = new AutoPool();
    private List<AutoResource> resources = Collections.synchronizedList(new ArrayList());
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private AutoPool() {
        this.scheduler.scheduleAtFixedRate(new Runnable() { // from class: org.jcodec.common.io.AutoPool.1
            @Override // java.lang.Runnable
            public void run() {
                long curTime = System.currentTimeMillis();
                List<AutoResource> res = AutoPool.this.resources;
                for (AutoResource autoResource : res) {
                    autoResource.setCurTime(curTime);
                }
            }
        }, 0L, 100L, TimeUnit.MILLISECONDS);
    }

    public static AutoPool getInstance() {
        return instance;
    }

    public void add(AutoResource res) {
        this.resources.add(res);
    }
}
