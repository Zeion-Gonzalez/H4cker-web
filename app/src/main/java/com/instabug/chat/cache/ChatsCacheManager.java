package com.instabug.chat.cache;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.library.internal.storage.DiskUtils;
import com.instabug.library.internal.storage.cache.AssetsCacheManager;
import com.instabug.library.internal.storage.cache.Cache;
import com.instabug.library.internal.storage.cache.CacheManager;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ChatsCacheManager {
    public static final String CHATS_DISK_CACHE_FILE_NAME = "/chats.cache";
    public static final String CHATS_DISK_CACHE_KEY = "chats_disk_cache";
    public static final String CHATS_MEMORY_CACHE_KEY = "chats_memory_cache";

    public static InMemoryCache<String, Chat> getCache() throws IllegalArgumentException {
        if (!CacheManager.getInstance().cacheExists(CHATS_MEMORY_CACHE_KEY)) {
            InstabugSDKLogger.m1799d(ChatsCacheManager.class, "In-memory cache not found, loading it from disk " + CacheManager.getInstance().getCache(CHATS_MEMORY_CACHE_KEY));
            CacheManager.getInstance().migrateCache(CHATS_DISK_CACHE_KEY, CHATS_MEMORY_CACHE_KEY, new CacheManager.KeyExtractor<String, Chat>() { // from class: com.instabug.chat.cache.ChatsCacheManager.1
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(Chat chat) {
                    return chat.getId();
                }
            });
            Cache cache = CacheManager.getInstance().getCache(CHATS_MEMORY_CACHE_KEY);
            if (cache != null) {
                InstabugSDKLogger.m1799d(ChatsCacheManager.class, "In-memory cache restored from disk, " + cache.getValues().size() + " elements restored");
            }
        }
        InstabugSDKLogger.m1799d(ChatsCacheManager.class, "In-memory cache found");
        return (InMemoryCache) CacheManager.getInstance().getCache(CHATS_MEMORY_CACHE_KEY);
    }

    public static void saveCacheToDisk() throws IllegalArgumentException {
        Cache cache = CacheManager.getInstance().getCache(CHATS_MEMORY_CACHE_KEY);
        Cache cache2 = CacheManager.getInstance().getCache(CHATS_DISK_CACHE_KEY);
        if (cache != null && cache2 != null) {
            CacheManager.getInstance().migrateCache(cache, cache2, new CacheManager.KeyExtractor<String, Chat>() { // from class: com.instabug.chat.cache.ChatsCacheManager.2
                @Override // com.instabug.library.internal.storage.cache.CacheManager.KeyExtractor
                /* renamed from: a  reason: merged with bridge method [inline-methods] */
                public String extractKey(Chat chat) {
                    return chat.getId();
                }
            });
        }
        if (cache2 != null) {
            InstabugSDKLogger.m1799d(ChatsCacheManager.class, "In-memory cache had been persisted on-disk, " + cache2.getValues().size() + " elements saved");
        }
    }

    public static Chat addOfflineChat(Context context) {
        Chat m619a = new Chat.C0524b().m619a(context);
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            cache.put(m619a.getId(), m619a);
        }
        return m619a;
    }

    @Nullable
    public static Chat getChat(String str) {
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            for (Chat chat : cache.getValues()) {
                if (chat.getId().equals(str)) {
                    return chat;
                }
            }
        }
        InstabugSDKLogger.m1800e(ChatsCacheManager.class, "No chat with id: " + str + " found, returning null");
        return null;
    }

    public static List<Chat> getValidChats() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            for (Chat chat : cache.getValues()) {
                if (chat.m609a().size() > 0) {
                    arrayList.add(chat);
                }
            }
        }
        return arrayList;
    }

    public static List<Chat> getOfflineChats() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            for (Chat chat : cache.getValues()) {
                InstabugSDKLogger.m1803v(ChatsCacheManager.class, "chat state: " + chat.m610b() + ", messages count: " + chat.m609a().size());
                if (chat.m610b().equals(Chat.ChatState.READY_TO_BE_SENT) || chat.m610b().equals(Chat.ChatState.LOGS_READY_TO_BE_UPLOADED)) {
                    if (chat.m609a().size() > 0) {
                        arrayList.add(chat);
                    }
                }
            }
        }
        return arrayList;
    }

    public static void updateLocalMessageWithSyncedMessage(@NonNull Context context, @NonNull Message message) throws IOException {
        AssetEntity createEmptyEntity;
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            Chat chat = cache.get(message.m633b());
            ArrayList<Message> m609a = chat.m609a();
            for (int i = 0; i < m609a.size(); i++) {
                if (m609a.get(i).m629a().equals(message.m629a()) && m609a.get(i).m644i().equals(Message.MessageState.READY_TO_BE_SYNCED) && m609a.get(i).m645j().size() == message.m645j().size()) {
                    for (int i2 = 0; i2 < m609a.get(i).m645j().size(); i2++) {
                        String type = message.m645j().get(i2).getType();
                        char c = 65535;
                        switch (type.hashCode()) {
                            case 93166550:
                                if (type.equals(Attachment.TYPE_AUDIO)) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 100313435:
                                if (type.equals(Attachment.TYPE_IMAGE)) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 112202875:
                                if (type.equals(Attachment.TYPE_VIDEO)) {
                                    c = 2;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                createEmptyEntity = AssetsCacheManager.createEmptyEntity(context, message.m645j().get(i2).getUrl(), AssetEntity.AssetType.IMAGE);
                                break;
                            case 1:
                                createEmptyEntity = AssetsCacheManager.createEmptyEntity(context, message.m645j().get(i2).getUrl(), AssetEntity.AssetType.AUDIO);
                                break;
                            case 2:
                                createEmptyEntity = AssetsCacheManager.createEmptyEntity(context, message.m645j().get(i2).getUrl(), AssetEntity.AssetType.VIDEO);
                                break;
                            default:
                                createEmptyEntity = AssetsCacheManager.createEmptyEntity(context, message.m645j().get(i2).getUrl(), AssetEntity.AssetType.IMAGE);
                                break;
                        }
                        AssetEntity assetEntity = createEmptyEntity;
                        File file = new File(m609a.get(i).m645j().get(i2).getLocalPath());
                        DiskUtils.copyFromUriIntoFile(context, Uri.fromFile(file), assetEntity.getFile());
                        AssetsCacheManager.addAssetEntity(assetEntity);
                        InstabugSDKLogger.m1803v(ChatsCacheManager.class, "local attachment file deleted: " + file.delete());
                    }
                    chat.m609a().set(i, message);
                    InstabugSDKLogger.m1803v(ChatsCacheManager.class, "messages number: " + chat.m609a().size());
                    InstabugSDKLogger.m1803v(ChatsCacheManager.class, "messages: " + chat.m609a().get(i));
                    cache.put(chat.getId(), chat);
                    return;
                }
            }
        }
    }

    public static int getTotalMessagesCount() {
        InMemoryCache<String, Chat> cache = getCache();
        if (cache == null) {
            return 0;
        }
        Iterator<Chat> it = cache.getValues().iterator();
        int i = 0;
        while (it.hasNext()) {
            Iterator<Message> it2 = it.next().m609a().iterator();
            while (it2.hasNext()) {
                if (it2.next().m644i().equals(Message.MessageState.SYNCED)) {
                    i++;
                }
            }
        }
        return i;
    }

    public static List<Message> getOfflineMessages() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            for (Chat chat : cache.getValues()) {
                if (chat.m610b().equals(Chat.ChatState.SENT)) {
                    Iterator<Message> it = chat.m609a().iterator();
                    while (it.hasNext()) {
                        Message next = it.next();
                        if (next.m644i().equals(Message.MessageState.READY_TO_BE_SENT) || next.m644i().equals(Message.MessageState.SENT)) {
                            arrayList.add(next);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public static long getLastMessageMessagedAt() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            Iterator<Chat> it = cache.getValues().iterator();
            while (it.hasNext()) {
                Iterator<Message> it2 = it.next().m609a().iterator();
                while (it2.hasNext()) {
                    Message next = it2.next();
                    if (next.m644i() == Message.MessageState.SYNCED) {
                        arrayList.add(next);
                    }
                }
            }
        }
        Collections.sort(arrayList, new Message.C0525a());
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Message message = (Message) arrayList.get(size);
            if (!message.m629a().equals("0")) {
                return message.m641f();
            }
        }
        return 0L;
    }

    public static int getUnreadCount() {
        int i = 0;
        InMemoryCache<String, Chat> cache = getCache();
        if (cache == null) {
            return 0;
        }
        Iterator<Chat> it = cache.getValues().iterator();
        while (true) {
            int i2 = i;
            if (it.hasNext()) {
                i = it.next().m611c() + i2;
            } else {
                return i2;
            }
        }
    }

    public static List<Message> getNotSentMessages() {
        ArrayList arrayList = new ArrayList();
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            Iterator<Chat> it = cache.getValues().iterator();
            while (it.hasNext()) {
                Iterator<Message> it2 = it.next().m609a().iterator();
                while (it2.hasNext()) {
                    Message next = it2.next();
                    if (next.m644i() == Message.MessageState.SENT || next.m644i() == Message.MessageState.READY_TO_BE_SENT) {
                        arrayList.add(next);
                    }
                }
            }
        }
        InstabugSDKLogger.m1803v(ChatsCacheManager.class, "not sent messages count: " + arrayList.size());
        return arrayList;
    }

    public static void cleanupChats() {
        InstabugSDKLogger.m1803v(ChatsCacheManager.class, "cleanupChats");
        InMemoryCache<String, Chat> cache = getCache();
        if (cache != null) {
            List<Chat> values = cache.getValues();
            ArrayList arrayList = new ArrayList();
            for (Chat chat : values) {
                if (chat.m610b() == Chat.ChatState.WAITING_ATTACHMENT_MESSAGE) {
                    arrayList.add(chat);
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                cache.delete(((Chat) it.next()).getId());
            }
        }
        saveCacheToDisk();
    }
}
