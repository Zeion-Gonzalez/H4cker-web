package com.instabug.chat.network;

import com.instabug.chat.cache.ChatsCacheManager;
import com.instabug.chat.eventbus.C0522a;
import com.instabug.chat.eventbus.ChatTimeUpdatedEventBus;
import com.instabug.chat.eventbus.ChatTriggeringEventBus;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.chat.network.p010a.C0536a;
import com.instabug.chat.settings.C0537a;
import com.instabug.library.internal.storage.cache.InMemoryCache;
import com.instabug.library.network.InstabugNetworkBasedBackgroundService;
import com.instabug.library.network.Request;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;

/* loaded from: classes.dex */
public class InstabugMessageUploaderService extends InstabugNetworkBasedBackgroundService {
    @Override // com.instabug.library.network.AbstractIntentServiceC0722a
    protected void runBackgroundTask() throws Exception {
        m691a();
        m696a(ChatsCacheManager.getOfflineMessages());
    }

    /* renamed from: a */
    private void m691a() throws IOException, JSONException {
        InstabugSDKLogger.m1803v(this, "Found " + ChatsCacheManager.getOfflineChats().size() + " offline chats in cache");
        for (final Chat chat : ChatsCacheManager.getOfflineChats()) {
            if (chat.m610b().equals(Chat.ChatState.READY_TO_BE_SENT) && chat.m609a().size() > 0) {
                InstabugSDKLogger.m1803v(this, "Uploading offline Chat: " + chat);
                C0536a.m708a().m712a(this, chat.getState(), new Request.Callbacks<String, Throwable>() { // from class: com.instabug.chat.network.InstabugMessageUploaderService.1
                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onSucceeded(String str) {
                        InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "triggering chat " + chat.toString() + " triggeredChatId: " + str);
                        String id = chat.getId();
                        ChatTriggeringEventBus.getInstance().post(new C0522a(id, str));
                        InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "Updating local chat with id: " + id + ", with synced chat with id: " + str);
                        chat.setId(str);
                        chat.m604a(Chat.ChatState.LOGS_READY_TO_BE_UPLOADED);
                        InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
                        if (cache != null) {
                            cache.delete(id);
                            cache.put(chat.getId(), chat);
                        }
                        ChatsCacheManager.saveCacheToDisk();
                        InstabugMessageUploaderService.this.m692a(chat);
                    }

                    @Override // com.instabug.library.network.Request.Callbacks
                    /* renamed from: a  reason: merged with bridge method [inline-methods] */
                    public void onFailed(Throwable th) {
                        InstabugSDKLogger.m1801e(InstabugMessageUploaderService.this, "Something went wrong while triggering offline chat with id: " + chat.getId(), th);
                    }
                });
            } else if (chat.m610b().equals(Chat.ChatState.LOGS_READY_TO_BE_UPLOADED)) {
                InstabugSDKLogger.m1799d(this, "chat: " + chat.toString() + " already uploaded but has unsent logs, uploading now");
                m692a(chat);
            }
        }
    }

    /* renamed from: a */
    private void m696a(List<Message> list) throws IOException, JSONException {
        InstabugSDKLogger.m1803v(this, "Found " + list.size() + " offline messages in cache");
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                final Message message = list.get(i2);
                if (message.m644i() == Message.MessageState.READY_TO_BE_SENT) {
                    InstabugSDKLogger.m1803v(this, "Uploading message: " + list.get(i2));
                    C0536a.m708a().m711a(this, message, new Request.Callbacks<String, Throwable>() { // from class: com.instabug.chat.network.InstabugMessageUploaderService.2
                        @Override // com.instabug.library.network.Request.Callbacks
                        /* renamed from: a  reason: merged with bridge method [inline-methods] */
                        public void onSucceeded(String str) {
                            InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "Send message response: " + str);
                            if (str != null && !str.equals("") && !str.equals("null")) {
                                Chat chat = ChatsCacheManager.getChat(message.m633b());
                                if (chat != null) {
                                    chat.m609a().remove(message);
                                    message.m627a(str);
                                    if (message.m645j().size() == 0) {
                                        message.m624a(Message.MessageState.READY_TO_BE_SYNCED);
                                    } else {
                                        message.m624a(Message.MessageState.SENT);
                                    }
                                    InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "Caching sent message:" + message.toString());
                                    chat.m609a().add(message);
                                    InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
                                    if (cache != null) {
                                        cache.put(chat.getId(), chat);
                                    }
                                    ChatsCacheManager.saveCacheToDisk();
                                    if (message.m645j().size() != 0) {
                                        try {
                                            InstabugMessageUploaderService.this.m693a(message);
                                            return;
                                        } catch (FileNotFoundException | JSONException e) {
                                            InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "Something went wrong while uploading messageattach attachments " + e.getMessage());
                                            return;
                                        }
                                    }
                                    C0537a.m723a(Calendar.getInstance(Locale.ENGLISH).getTime().getTime());
                                    ChatTimeUpdatedEventBus.getInstance().post(Long.valueOf(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds()));
                                    return;
                                }
                                InstabugSDKLogger.m1800e(this, "Chat is null so can't remove message from it");
                            }
                        }

                        @Override // com.instabug.library.network.Request.Callbacks
                        /* renamed from: a  reason: merged with bridge method [inline-methods] */
                        public void onFailed(Throwable th) {
                            InstabugSDKLogger.m1801e(InstabugMessageUploaderService.this, "Something went wrong while uploading cached message", th);
                        }
                    });
                } else if (message.m644i() == Message.MessageState.SENT) {
                    InstabugSDKLogger.m1803v(this, "Uploading message's attachments : " + list.get(i2));
                    try {
                        m693a(message);
                    } catch (FileNotFoundException | JSONException e) {
                        InstabugSDKLogger.m1803v(this, "Something went wrong while uploading message attachments " + e.getMessage());
                    }
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m693a(final Message message) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.m1803v(this, "Found " + message.m645j().size() + " attachments related to message: " + message.m636c());
        C0536a.m708a().m714b(this, message, new Request.Callbacks<Boolean, Message>() { // from class: com.instabug.chat.network.InstabugMessageUploaderService.3
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(Boolean bool) {
                InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "Message attachments uploaded successfully");
                Chat chat = ChatsCacheManager.getChat(message.m633b());
                if (chat != null) {
                    chat.m609a().remove(message);
                    message.m624a(Message.MessageState.READY_TO_BE_SYNCED);
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 >= message.m645j().size()) {
                            break;
                        }
                        message.m645j().get(i2).setState(Attachment.STATE_SYNCED);
                        i = i2 + 1;
                    }
                    InstabugSDKLogger.m1803v(InstabugMessageUploaderService.this, "Caching sent message:" + message.toString());
                    chat.m609a().add(message);
                    InMemoryCache<String, Chat> cache = ChatsCacheManager.getCache();
                    if (cache != null) {
                        cache.put(chat.getId(), chat);
                    }
                    ChatsCacheManager.saveCacheToDisk();
                    C0537a.m723a(Calendar.getInstance(Locale.ENGLISH).getTime().getTime());
                    ChatTimeUpdatedEventBus.getInstance().post(Long.valueOf(InstabugDateFormatter.getCurrentUTCTimeStampInMiliSeconds()));
                    return;
                }
                InstabugSDKLogger.m1800e(this, "Chat is null so can't remove message from it");
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Message message2) {
                InstabugSDKLogger.m1800e(InstabugMessageUploaderService.this, "Something went wrong while uploading message attachments, Message: " + message);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: a */
    public void m692a(final Chat chat) {
        InstabugSDKLogger.m1799d(this, "START uploading all logs related to this chat id = " + chat.getId());
        C0536a.m708a().m710a(this, chat, new Request.Callbacks<Boolean, Chat>() { // from class: com.instabug.chat.network.InstabugMessageUploaderService.4
            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onSucceeded(Boolean bool) {
                InstabugSDKLogger.m1799d(InstabugMessageUploaderService.this, "chat logs uploaded successfully, change its state");
                chat.m604a(Chat.ChatState.SENT);
                ChatsCacheManager.saveCacheToDisk();
            }

            @Override // com.instabug.library.network.Request.Callbacks
            /* renamed from: a  reason: merged with bridge method [inline-methods] */
            public void onFailed(Chat chat2) {
                InstabugSDKLogger.m1799d(InstabugMessageUploaderService.this, "Something went wrong while uploading chat logs");
            }
        });
    }
}
