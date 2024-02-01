package com.instabug.chat.p011ui.p013b;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.instabug.chat.C0507R;
import com.instabug.chat.model.C0527a;
import com.instabug.chat.model.C0528b;
import com.instabug.chat.p011ui.view.CircularImageView;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.media.AudioPlayer;
import com.instabug.library.internal.storage.cache.AssetsCacheManager;
import com.instabug.library.model.AssetEntity;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.Colorizer;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.VideoManipulationUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/* compiled from: MessagesListAdapter.java */
/* renamed from: com.instabug.chat.ui.b.f */
/* loaded from: classes.dex */
public class C0550f extends BaseAdapter {

    /* renamed from: b */
    private List<C0527a> f450b;

    /* renamed from: d */
    private Context f452d;

    /* renamed from: e */
    private ListView f453e;

    /* renamed from: f */
    private a f454f;

    /* renamed from: g */
    private boolean f455g = true;

    /* renamed from: a */
    private final AudioPlayer f449a = new AudioPlayer();

    /* renamed from: c */
    private ColorFilter f451c = new PorterDuffColorFilter(InstabugCore.getPrimaryColor(), PorterDuff.Mode.SRC_IN);

    /* compiled from: MessagesListAdapter.java */
    /* renamed from: com.instabug.chat.ui.b.f$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: b */
        void mo834b(String str);

        /* renamed from: c */
        void mo835c(String str);

        /* renamed from: d */
        void mo836d(String str);
    }

    public C0550f(List<C0527a> list, Context context, ListView listView, a aVar) {
        this.f450b = list;
        this.f453e = listView;
        this.f452d = context;
        this.f454f = aVar;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 8;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.f450b.size();
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        C0527a item = getItem(i);
        switch (item.m664e()) {
            case MESSAGE:
                return item.m666g() ? 0 : 1;
            case IMAGE:
                return item.m666g() ? 2 : 3;
            case AUDIO:
                return item.m666g() ? 4 : 5;
            case VIDEO:
                return item.m666g() ? 6 : 7;
            default:
                return -1;
        }
    }

    @Override // android.widget.Adapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public C0527a getItem(int i) {
        return this.f450b.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        b bVar;
        View inflate;
        int itemViewType = getItemViewType(i);
        if (view == null) {
            switch (itemViewType) {
                case 0:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_me, viewGroup, false);
                    break;
                case 1:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item, viewGroup, false);
                    break;
                case 2:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_img_me, viewGroup, false);
                    break;
                case 3:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_img, viewGroup, false);
                    break;
                case 4:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_voice_me, viewGroup, false);
                    break;
                case 5:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_voice, viewGroup, false);
                    break;
                case 6:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_video_me, viewGroup, false);
                    break;
                case 7:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_video, viewGroup, false);
                    break;
                default:
                    inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0507R.layout.instabug_message_list_item_me, viewGroup, false);
                    break;
            }
            b bVar2 = new b(inflate);
            inflate.setTag(bVar2);
            view = inflate;
            bVar = bVar2;
        } else {
            bVar = (b) view.getTag();
        }
        try {
            m868a(bVar, getItem(i));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    /* renamed from: a */
    private void m868a(b bVar, C0527a c0527a) throws ParseException {
        if (bVar != null) {
            InstabugSDKLogger.m1803v(this, "viewholder: " + (bVar == null) + ", type:" + c0527a.m664e());
            switch (c0527a.m664e()) {
                case MESSAGE:
                    if (c0527a.m666g()) {
                        bVar.f479c.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(bVar.f479c.getBackground()));
                    } else {
                        bVar.f488l.removeAllViews();
                        if (c0527a.m668i()) {
                            m867a(c0527a, bVar);
                        }
                    }
                    bVar.f478b.setText(InstabugDateFormatter.formatMessageDate(c0527a.m660c()));
                    bVar.f479c.setText(c0527a.m655a());
                    if (bVar.f477a != null) {
                        m869a(c0527a.m659b(), bVar.f477a, false);
                        return;
                    }
                    return;
                case IMAGE:
                    if (c0527a.m666g()) {
                        bVar.f480d.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(bVar.f480d.getBackground()));
                    }
                    bVar.f478b.setText(InstabugDateFormatter.formatMessageDate(c0527a.m660c()));
                    m872b(c0527a, bVar);
                    if (bVar.f477a != null && c0527a.m659b() != null) {
                        m869a(c0527a.m659b(), bVar.f477a, false);
                        return;
                    }
                    return;
                case AUDIO:
                    InstabugSDKLogger.m1800e(this, "viewholder: " + (bVar == null) + ", type:" + c0527a.m664e());
                    if (c0527a.m666g()) {
                        bVar.f481e.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(bVar.f481e.getBackground()));
                        bVar.f482f.setColorFilter(this.f451c);
                    }
                    bVar.f478b.setText(InstabugDateFormatter.formatMessageDate(c0527a.m660c()));
                    m873c(c0527a, bVar);
                    if (bVar.f477a != null && c0527a.m659b() != null) {
                        m869a(c0527a.m659b(), bVar.f477a, false);
                        return;
                    }
                    return;
                case VIDEO:
                    if (c0527a.m666g()) {
                        bVar.f485i.setBackgroundDrawable(Colorizer.getPrimaryColorTintedDrawable(bVar.f485i.getBackground()));
                        bVar.f484h.setColorFilter(this.f451c);
                    }
                    bVar.f478b.setText(InstabugDateFormatter.formatMessageDate(c0527a.m660c()));
                    m876d(c0527a, bVar);
                    if (bVar.f477a != null && c0527a.m663d() != null) {
                        m869a(c0527a.m663d(), bVar.f477a, false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: a */
    private void m867a(C0527a c0527a, b bVar) {
        InstabugSDKLogger.m1799d(this, "Binding MessageActions view  FlatMessage = " + c0527a.toString());
        ArrayList<C0528b> m669j = c0527a.m669j();
        if (m669j != null && m669j.size() > 0) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < m669j.size()) {
                    final C0528b c0528b = m669j.get(i2);
                    Button button = new Button(this.f452d);
                    button.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                    button.setText(c0528b.m676b());
                    button.setTextColor(ContextCompat.getColor(this.f452d, 17170443));
                    button.setBackgroundColor(InstabugCore.getPrimaryColor());
                    button.setMaxEms(30);
                    button.setMaxLines(1);
                    button.setId(i2);
                    button.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.ui.b.f.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            C0550f.this.f454f.mo836d(c0528b.m678c());
                        }
                    });
                    bVar.f488l.addView(button);
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: b */
    private void m872b(final C0527a c0527a, b bVar) {
        if (c0527a.m667h() != null) {
            BitmapUtils.loadBitmap(c0527a.m667h(), bVar.f480d);
        } else {
            m869a(c0527a.m663d(), bVar.f480d, true);
        }
        bVar.f480d.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.ui.b.f.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (C0550f.this.f454f != null) {
                    if (c0527a.m667h() != null) {
                        C0550f.this.f454f.mo835c(c0527a.m667h());
                    } else {
                        C0550f.this.f454f.mo835c(c0527a.m663d());
                    }
                }
            }
        });
    }

    /* renamed from: c */
    private void m873c(final C0527a c0527a, final b bVar) {
        final String m667h;
        if (c0527a.m663d() != null) {
            m667h = c0527a.m663d();
        } else {
            m667h = c0527a.m667h();
        }
        if (bVar.f483g != null && bVar.f483g.getVisibility() == 0) {
            bVar.f483g.setVisibility(8);
        }
        if (bVar.f482f.getVisibility() == 8) {
            bVar.f482f.setVisibility(0);
        }
        bVar.f481e.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.ui.b.f.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (c0527a.m665f() == C0527a.a.NONE) {
                    C0550f.this.f449a.start(m667h);
                    c0527a.m651a(C0527a.a.PLAYING);
                    bVar.f482f.setImageResource(C0507R.drawable.instabug_ic_pause);
                } else {
                    C0550f.this.f449a.pause();
                    c0527a.m651a(C0527a.a.NONE);
                    bVar.f482f.setImageResource(C0507R.drawable.instabug_ic_play);
                }
            }
        });
        this.f449a.addOnStopListener(new AudioPlayer.OnStopListener(m667h) { // from class: com.instabug.chat.ui.b.f.4
            @Override // com.instabug.library.internal.media.AudioPlayer.OnStopListener
            public void onStop() {
                c0527a.m651a(C0527a.a.NONE);
                bVar.f482f.setImageResource(C0507R.drawable.instabug_ic_play);
            }
        });
    }

    /* renamed from: d */
    private void m876d(C0527a c0527a, b bVar) {
        if (c0527a.m667h() != null) {
            m877e(c0527a, bVar);
        } else {
            m878f(c0527a, bVar);
        }
    }

    /* renamed from: e */
    private void m877e(final C0527a c0527a, b bVar) {
        InstabugSDKLogger.m1799d(this, "Video path not found but main screenshot found, using it");
        InstabugSDKLogger.m1799d(this, "Video Encoded: " + c0527a.m670k());
        if (c0527a.m670k()) {
            bVar.f487k.setVisibility(8);
            bVar.f484h.setVisibility(0);
            bVar.f486j.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.ui.b.f.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (C0550f.this.f454f != null) {
                        C0550f.this.f454f.mo834b(c0527a.m667h());
                    }
                }
            });
        }
        try {
            Bitmap extractFirstVideoFrame = VideoManipulationUtils.extractFirstVideoFrame(c0527a.m667h());
            if (extractFirstVideoFrame != null) {
                bVar.f485i.setImageBitmap(extractFirstVideoFrame);
            }
        } catch (RuntimeException e) {
            InstabugSDKLogger.m1801e(this, e.getMessage(), e);
        }
    }

    /* renamed from: f */
    private void m878f(C0527a c0527a, final b bVar) {
        AssetsCacheManager.getAssetEntity(this.f452d, AssetsCacheManager.createEmptyEntity(this.f452d, c0527a.m663d(), AssetEntity.AssetType.VIDEO), new AssetsCacheManager.OnDownloadFinished() { // from class: com.instabug.chat.ui.b.f.6
            @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
            public void onSuccess(final AssetEntity assetEntity) {
                InstabugSDKLogger.m1799d(this, "Asset Entity downloaded: " + assetEntity.getFile().getPath());
                bVar.f487k.setVisibility(8);
                bVar.f484h.setVisibility(0);
                bVar.f485i.setImageBitmap(VideoManipulationUtils.extractFirstVideoFrame(assetEntity.getFile().getPath()));
                bVar.f486j.setOnClickListener(new View.OnClickListener() { // from class: com.instabug.chat.ui.b.f.6.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        C0550f.this.f454f.mo834b(assetEntity.getFile().getPath());
                    }
                });
            }

            @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
            public void onFailed(Throwable th) {
                InstabugSDKLogger.m1801e(this, "Asset Entity downloading got error", th);
            }
        });
    }

    /* renamed from: a */
    private void m869a(String str, final ImageView imageView, final boolean z) {
        AssetsCacheManager.getAssetEntity(this.f452d, AssetsCacheManager.createEmptyEntity(this.f452d, str, AssetEntity.AssetType.IMAGE), new AssetsCacheManager.OnDownloadFinished() { // from class: com.instabug.chat.ui.b.f.7
            @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
            public void onSuccess(AssetEntity assetEntity) {
                InstabugSDKLogger.m1799d(this, "Asset Entity downloaded: " + assetEntity.getFile().getPath());
                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(assetEntity.getFile())));
                    if (z && C0550f.this.f455g) {
                        C0550f.this.f453e.setSelection(C0550f.this.getCount() - 1);
                        C0550f.this.f455g = false;
                    }
                } catch (FileNotFoundException e) {
                    InstabugSDKLogger.m1801e(this, "Asset Entity downloading got FileNotFoundException error", e);
                }
            }

            @Override // com.instabug.library.internal.storage.cache.AssetsCacheManager.OnDownloadFinished
            public void onFailed(Throwable th) {
                InstabugSDKLogger.m1801e(this, "Asset Entity downloading got error", th);
            }
        });
    }

    /* renamed from: a */
    public void m880a(List<C0527a> list) {
        this.f450b = list;
    }

    /* compiled from: MessagesListAdapter.java */
    /* renamed from: com.instabug.chat.ui.b.f$b */
    /* loaded from: classes.dex */
    public static class b {

        /* renamed from: a */
        public CircularImageView f477a;

        /* renamed from: b */
        public TextView f478b;

        /* renamed from: c */
        public TextView f479c;

        /* renamed from: d */
        public ImageView f480d;

        /* renamed from: e */
        public FrameLayout f481e;

        /* renamed from: f */
        public ImageView f482f;

        /* renamed from: g */
        public ProgressBar f483g;

        /* renamed from: h */
        public ImageView f484h;

        /* renamed from: i */
        public ImageView f485i;

        /* renamed from: j */
        public FrameLayout f486j;

        /* renamed from: k */
        public ProgressBar f487k;

        /* renamed from: l */
        public LinearLayout f488l;

        public b(View view) {
            this.f477a = (CircularImageView) view.findViewById(C0507R.id.instabug_img_message_sender);
            this.f478b = (TextView) view.findViewById(C0507R.id.instabug_txt_message_time);
            this.f479c = (TextView) view.findViewById(C0507R.id.instabug_txt_message_body);
            this.f480d = (ImageView) view.findViewById(C0507R.id.instabug_img_attachment);
            this.f482f = (ImageView) view.findViewById(C0507R.id.instabug_btn_play_audio);
            this.f481e = (FrameLayout) view.findViewById(C0507R.id.instabug_audio_attachment);
            this.f483g = (ProgressBar) view.findViewById(C0507R.id.instabug_audio_attachment_progress_bar);
            this.f485i = (ImageView) view.findViewById(C0507R.id.instabug_img_video_attachment);
            this.f484h = (ImageView) view.findViewById(C0507R.id.instabug_btn_play_video);
            this.f486j = (FrameLayout) view.findViewById(C0507R.id.instabug_video_attachment);
            this.f487k = (ProgressBar) view.findViewById(C0507R.id.instabug_video_attachment_progress_bar);
            this.f488l = (LinearLayout) view.findViewById(C0507R.id.instabug_message_actions_container);
        }
    }
}
