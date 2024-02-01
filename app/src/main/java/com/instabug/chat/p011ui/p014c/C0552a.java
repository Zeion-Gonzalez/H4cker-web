package com.instabug.chat.p011ui.p014c;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.instabug.chat.C0507R;
import com.instabug.chat.model.Attachment;
import com.instabug.chat.model.Chat;
import com.instabug.chat.model.Message;
import com.instabug.chat.p011ui.view.CircularImageView;
import com.instabug.library.C0577R;
import com.instabug.library.internal.storage.cache.AssetsCacheManager;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugAppData;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/* compiled from: ChatsAdapter.java */
/* renamed from: com.instabug.chat.ui.c.a */
/* loaded from: classes.dex */
class C0552a extends BaseAdapter {

    /* renamed from: a */
    private List<Chat> f489a;

    /* renamed from: b */
    private InstabugAppData f490b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0552a(Context context, List<Chat> list) {
        this.f489a = list;
        this.f490b = new InstabugAppData(context);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.f489a.size();
    }

    @Override // android.widget.Adapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Chat getItem(int i) {
        return this.f489a.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return getItem(i).getId().hashCode();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_conversation_list_item, viewGroup, false);
            aVar = new a(view);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        m886a(view.getContext(), aVar, getItem(i));
        return view;
    }

    /* renamed from: a */
    private void m886a(Context context, final a aVar, Chat chat) {
        InstabugSDKLogger.m1803v(this, "Binding chat " + chat + " to view");
        Collections.sort(chat.m609a(), new Message.C0525a());
        Message m616h = chat.m616h();
        if (m616h == null || m616h.m636c() == null || TextUtils.isEmpty(m616h.m636c().trim()) || m616h.m636c().equals("null")) {
            if (m616h != null && m616h.m645j().size() > 0) {
                String type = m616h.m645j().get(m616h.m645j().size() - 1).getType();
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
                        aVar.f496d.setText(C0577R.string.instabug_str_image);
                        break;
                    case 1:
                        aVar.f496d.setText(C0577R.string.instabug_str_audio);
                        break;
                    case 2:
                        aVar.f496d.setText(C0577R.string.instabug_str_video);
                        break;
                }
            }
        } else {
            aVar.f496d.setText(m616h.m636c());
        }
        String m614f = chat.m614f();
        if (m614f != null && !m614f.equals("") && !m614f.equals("null") && !m616h.m648m()) {
            InstabugSDKLogger.m1803v(this, "chat SenderName: " + m614f);
            aVar.f493a.setText(m614f);
        } else {
            aVar.f493a.setText(String.format(context.getString(C0577R.string.instabug_str_notification_title), this.f490b.getAppName()));
        }
        aVar.f495c.setText(InstabugDateFormatter.formatConversationLastMessageDate(chat.m615g()));
        if (chat.m611c() != 0) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(C0577R.attr.instabug_unread_message_background_color, typedValue, true);
            aVar.f498f.setBackgroundColor(typedValue.data);
            aVar.f497e.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(ContextCompat.getDrawable(context, C0577R.drawable.instabug_bg_white_oval)));
            aVar.f497e.setText(String.valueOf(chat.m611c()));
            aVar.f497e.setVisibility(0);
        } else {
            aVar.f498f.setBackgroundColor(0);
            aVar.f497e.setVisibility(8);
        }
        if (chat.m613e() != null) {
            AssetsCacheManager.getAssetEntity(context, AssetsCacheManager.createEmptyEntity(context, chat.m613e(), AssetEntity.AssetType.IMAGE), new AssetsCacheManager.OnDownloadFinished() { // from class: com.instabug.chat.ui.c.a.1
                @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
                public void onSuccess(AssetEntity assetEntity) {
                    InstabugSDKLogger.m1799d(this, "Asset Entity downloaded: " + assetEntity.getFile().getPath());
                    try {
                        aVar.f494b.setImageDrawable(null);
                        aVar.f494b.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(assetEntity.getFile())));
                    } catch (FileNotFoundException e) {
                        InstabugSDKLogger.m1801e(this, "Asset Entity downloading got FileNotFoundException error", e);
                    }
                }

                @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
                public void onFailed(Throwable th) {
                    InstabugSDKLogger.m1801e(this, "Asset Entity downloading got error", th);
                }
            });
        } else {
            aVar.f494b.setImageResource(C0507R.drawable.instabug_ic_avatar);
        }
    }

    /* renamed from: a */
    public void m888a(List<Chat> list) {
        this.f489a = list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ChatsAdapter.java */
    /* renamed from: com.instabug.chat.ui.c.a$a */
    /* loaded from: classes.dex */
    public static class a {

        /* renamed from: a */
        private final TextView f493a;

        /* renamed from: b */
        private final CircularImageView f494b;

        /* renamed from: c */
        private final TextView f495c;

        /* renamed from: d */
        private final TextView f496d;

        /* renamed from: e */
        private final TextView f497e;

        /* renamed from: f */
        private final LinearLayout f498f;

        a(View view) {
            this.f498f = (LinearLayout) view.findViewById(C0507R.id.conversation_list_item_container);
            this.f493a = (TextView) view.findViewById(C0507R.id.instabug_txt_message_sender);
            this.f494b = (CircularImageView) view.findViewById(C0507R.id.instabug_message_sender_avatar);
            this.f495c = (TextView) view.findViewById(C0507R.id.instabug_txt_message_time);
            this.f497e = (TextView) view.findViewById(C0507R.id.instabug_unread_messages_count);
            this.f496d = (TextView) view.findViewById(C0507R.id.instabug_txt_message_snippet);
        }
    }
}
