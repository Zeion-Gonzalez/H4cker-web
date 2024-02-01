package com.instabug.chat.cache;

import android.content.Context;
import com.instabug.chat.model.C0530d;
import com.instabug.chat.model.Chat;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.OnDiskCache;

/* compiled from: CacheUtility.java */
/* renamed from: com.instabug.chat.cache.a */
/* loaded from: classes.dex */
public class C0518a {
    /* renamed from: a */
    public static void m547a(Context context) {
        CacheManager.getInstance().addCache(new OnDiskCache(context, ChatsCacheManager.CHATS_DISK_CACHE_KEY, ChatsCacheManager.CHATS_DISK_CACHE_FILE_NAME, Chat.class));
        CacheManager.getInstance().addCache(new OnDiskCache(context, ReadQueueCacheManager.READ_QUEUE_DISK_CACHE_KEY, ReadQueueCacheManager.READ_QUEUE_DISK_CACHE_FILE_NAME, C0530d.class));
    }

    /* renamed from: a */
    public static void m546a() {
        ChatsCacheManager.cleanupChats();
    }

    /* renamed from: b */
    public static void m548b() {
        ChatsCacheManager.saveCacheToDisk();
        ReadQueueCacheManager.saveCacheToDisk();
    }
}
