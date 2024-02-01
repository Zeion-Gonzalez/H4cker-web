package com.instabug.library.invocation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public class Invocation {
    public static final int CHATS = 4;
    public static final int NEW_BUG = 1;
    public static final int NEW_CHAT = 3;
    public static final int NEW_FEEDBACK = 2;
    public static final int PROMPT_OPTIONS = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface InvocationMode {
    }
}
