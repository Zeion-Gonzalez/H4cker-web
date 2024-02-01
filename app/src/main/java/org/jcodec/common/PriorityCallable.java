package org.jcodec.common;

import java.util.concurrent.Callable;

/* loaded from: classes.dex */
public interface PriorityCallable<T> extends Callable<T> {
    int getPriority();
}
