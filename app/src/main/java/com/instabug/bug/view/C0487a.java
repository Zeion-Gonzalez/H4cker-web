package com.instabug.bug.view;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.instabug.bug.C0458R;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.model.Attachment;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.VideoManipulationUtils;
import com.instabug.library.view.IconView;
import java.util.ArrayList;
import java.util.List;

/* compiled from: AttachmentsAdapter.java */
/* renamed from: com.instabug.bug.view.a */
/* loaded from: classes.dex */
public class C0487a extends BaseAdapter {

    /* renamed from: a */
    private final LayoutInflater f172a;

    /* renamed from: b */
    private List<Attachment> f173b = new ArrayList();

    /* renamed from: c */
    private ColorFilter f174c;

    /* renamed from: d */
    private a f175d;

    /* renamed from: e */
    private ProgressBar f176e;

    /* renamed from: f */
    private ImageView f177f;

    /* renamed from: g */
    private int f178g;

    /* renamed from: h */
    private final int f179h;

    /* compiled from: AttachmentsAdapter.java */
    /* renamed from: com.instabug.bug.view.a$a */
    /* loaded from: classes.dex */
    public interface a {
        /* renamed from: a */
        void mo337a(View view, Attachment attachment);
    }

    public C0487a(Activity activity, ColorFilter colorFilter, a aVar) {
        this.f172a = LayoutInflater.from(activity);
        this.f174c = colorFilter;
        this.f175d = aVar;
        this.f178g = activity.getWindowManager().getDefaultDisplay().getHeight();
        if (activity.getResources().getConfiguration().orientation == 1) {
            this.f179h = 5;
        } else {
            this.f179h = 3;
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (this.f173b != null) {
            return this.f173b.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    public Attachment getItem(int i) {
        return this.f173b.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 3;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        if (this.f173b == null || this.f173b.size() == 0) {
            return super.getItemViewType(i);
        }
        switch (this.f173b.get(i).getType()) {
            case AUDIO:
                return 2;
            case VIDEO:
                return 1;
            default:
                return 0;
        }
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        d dVar;
        b bVar;
        c cVar;
        switch (getItemViewType(i)) {
            case 0:
                if (view == null) {
                    c cVar2 = new c();
                    view = this.f172a.inflate(C0458R.layout.instabug_lyt_attachment_image, (ViewGroup) null);
                    view.setLayoutParams(new AbsListView.LayoutParams(-1, this.f178g / this.f179h));
                    cVar2.f195b = (ImageView) view.findViewById(C0458R.id.instabug_img_attachment);
                    cVar2.f194a = (RelativeLayout) view.findViewById(C0458R.id.instabug_grid_img_item);
                    cVar2.f196c = (IconView) view.findViewById(C0458R.id.instabug_btn_remove_attachment);
                    view.setTag(cVar2);
                    cVar = cVar2;
                } else {
                    cVar = (c) view.getTag();
                }
                m328a(cVar, getItem(i));
                return view;
            case 1:
                if (view == null) {
                    d dVar2 = new d();
                    view = this.f172a.inflate(C0458R.layout.instabug_lyt_attachment_video, (ViewGroup) null);
                    view.setLayoutParams(new AbsListView.LayoutParams(-1, this.f178g / this.f179h));
                    dVar2.f197a = (RelativeLayout) view.findViewById(C0458R.id.instabug_grid_video_item);
                    dVar2.f201e = (ImageView) view.findViewById(C0458R.id.instabug_img_video_attachment);
                    dVar2.f199c = (IconView) view.findViewById(C0458R.id.instabug_btn_remove_attachment);
                    dVar2.f198b = (ProgressBar) view.findViewById(C0458R.id.instabug_attachment_progress_bar);
                    dVar2.f200d = (ImageView) view.findViewById(C0458R.id.instabug_btn_video_play_attachment);
                    view.setTag(dVar2);
                    dVar = dVar2;
                } else {
                    dVar = (d) view.getTag();
                }
                m329a(dVar, getItem(i));
                return view;
            case 2:
                if (view == null) {
                    b bVar2 = new b();
                    view = this.f172a.inflate(C0458R.layout.instabug_lyt_attachment_audio, (ViewGroup) null);
                    view.setLayoutParams(new AbsListView.LayoutParams(-1, this.f178g / this.f179h));
                    bVar2.f183a = (RelativeLayout) view.findViewById(C0458R.id.instabug_grid_audio_item);
                    bVar2.f184b = (ImageView) view.findViewById(C0458R.id.instabug_img_audio_attachment);
                    bVar2.f185c = (IconView) view.findViewById(C0458R.id.instabug_btn_remove_attachment);
                    bVar2.f187e = (TextView) view.findViewById(C0458R.id.instabug_txt_attachment_length);
                    bVar2.f186d = (ImageView) view.findViewById(C0458R.id.instabug_btn_audio_play_attachment);
                    view.setTag(bVar2);
                    bVar = bVar2;
                } else {
                    bVar = (b) view.getTag();
                }
                m327a(bVar, getItem(i));
                return view;
            default:
                return null;
        }
    }

    /* renamed from: a */
    private void m328a(c cVar, Attachment attachment) {
        BitmapUtils.loadBitmap(attachment.getLocalPath(), cVar.f195b);
        cVar.f195b.setTag(attachment);
        cVar.f194a.setOnClickListener(m330c(attachment));
        cVar.f196c.setTag(attachment);
        cVar.f196c.setOnClickListener(m330c(attachment));
    }

    /* renamed from: a */
    private void m329a(d dVar, Attachment attachment) {
        dVar.f199c.findViewById(C0458R.id.instabug_btn_remove_attachment).setTag(attachment);
        dVar.f199c.findViewById(C0458R.id.instabug_btn_remove_attachment).setOnClickListener(m330c(attachment));
        dVar.f200d.setColorFilter(this.f174c);
        dVar.f201e.setTag(attachment);
        dVar.f197a.setOnClickListener(m330c(attachment));
        this.f177f = dVar.f200d;
        this.f176e = dVar.f198b;
        InstabugSDKLogger.m1799d(this, "encoded: " + attachment.isVideoEncoded());
        if (attachment.getLocalPath() != null && attachment.isVideoEncoded()) {
            try {
                InstabugSDKLogger.m1799d(this, "Video path found, extracting it's first frame " + attachment.getLocalPath());
                dVar.f201e.setImageBitmap(VideoManipulationUtils.extractFirstVideoFrame(attachment.getLocalPath()));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        InstabugSDKLogger.m1799d(this, "Neither video path nor main screenshot found, using white background");
        dVar.f201e.setImageResource(C0458R.drawable.instabug_bg_card);
        if (this.f176e != null && this.f176e.getVisibility() == 8) {
            this.f176e.setVisibility(0);
        }
        if (this.f177f != null && this.f177f.getVisibility() == 0) {
            this.f177f.setVisibility(8);
        }
    }

    /* renamed from: a */
    private void m327a(b bVar, Attachment attachment) {
        bVar.f184b.setTag(attachment);
        bVar.f183a.setOnClickListener(m330c(attachment));
        bVar.f185c.setTag(attachment);
        bVar.f185c.setOnClickListener(m330c(attachment));
        bVar.f186d.setColorFilter(this.f174c);
        bVar.f187e.setTextColor(InstabugCore.getPrimaryColor());
        InstabugSDKLogger.m1799d(this, "Audio length is " + attachment.getDuration());
        bVar.f187e.setText(attachment.getDuration());
    }

    /* renamed from: c */
    private View.OnClickListener m330c(final Attachment attachment) {
        return new View.OnClickListener() { // from class: com.instabug.bug.view.a.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                C0487a.this.f175d.mo337a(view, attachment);
            }
        };
    }

    /* renamed from: a */
    public ProgressBar m331a() {
        return this.f176e;
    }

    /* renamed from: b */
    public ImageView m334b() {
        return this.f177f;
    }

    /* renamed from: a */
    public void m333a(Attachment attachment) {
        this.f173b.add(attachment);
    }

    /* renamed from: c */
    public void m336c() {
        this.f173b.clear();
    }

    /* renamed from: b */
    public void m335b(Attachment attachment) {
        this.f173b.remove(attachment);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: AttachmentsAdapter.java */
    /* renamed from: com.instabug.bug.view.a$c */
    /* loaded from: classes.dex */
    public static class c {

        /* renamed from: a */
        RelativeLayout f194a;

        /* renamed from: b */
        ImageView f195b;

        /* renamed from: c */
        IconView f196c;

        private c() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: AttachmentsAdapter.java */
    /* renamed from: com.instabug.bug.view.a$b */
    /* loaded from: classes.dex */
    public static class b {

        /* renamed from: a */
        RelativeLayout f183a;

        /* renamed from: b */
        ImageView f184b;

        /* renamed from: c */
        IconView f185c;

        /* renamed from: d */
        ImageView f186d;

        /* renamed from: e */
        TextView f187e;

        private b() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: AttachmentsAdapter.java */
    /* renamed from: com.instabug.bug.view.a$d */
    /* loaded from: classes.dex */
    public static class d {

        /* renamed from: a */
        RelativeLayout f197a;

        /* renamed from: b */
        ProgressBar f198b;

        /* renamed from: c */
        IconView f199c;

        /* renamed from: d */
        ImageView f200d;

        /* renamed from: e */
        ImageView f201e;

        private d() {
        }
    }
}
