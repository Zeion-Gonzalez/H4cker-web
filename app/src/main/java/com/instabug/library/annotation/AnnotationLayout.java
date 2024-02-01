package com.instabug.library.annotation;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.instabug.library.C0577R;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.annotation.AnnotationView;
import com.instabug.library.annotation.ColorPickerPopUpView;
import com.instabug.library.annotation.ShapeSuggestionsLayout;
import com.instabug.library.util.AsyncTaskC0753a;
import com.instabug.library.util.AttrResolver;
import com.instabug.library.util.BitmapUtils;
import com.instabug.library.view.IconView;
import com.instabug.library.view.ViewUtils;

/* loaded from: classes.dex */
public class AnnotationLayout extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "AnnotationLayout";
    private LinearLayout annotationActionsContainer;
    private AnnotationView annotationView;
    private View border;
    private View brushIndicator;
    private ColorPickerPopUpView colorPicker;
    private IconView iconBlur;
    private IconView iconBrush;
    private RelativeLayout iconBrushLayout;
    private IconView iconMagnify;
    private IconView iconUndo;
    private ShapeSuggestionsLayout shapeSuggestionsLayout;
    private int tintingColor;

    public AnnotationLayout(Context context) {
        this(context, null);
    }

    public AnnotationLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    @TargetApi(11)
    public AnnotationLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews();
    }

    @TargetApi(21)
    public AnnotationLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initViews();
    }

    private void initViews() {
        inflate(getContext(), C0577R.layout.instabug_annotation_view, this);
        this.annotationActionsContainer = (LinearLayout) findViewById(C0577R.id.instabug_annotation_actions_container);
        this.shapeSuggestionsLayout = (ShapeSuggestionsLayout) findViewById(C0577R.id.shapeSuggestionsLayout);
        this.shapeSuggestionsLayout.setOnShapeSelectedListener(new ShapeSuggestionsLayout.InterfaceC0606a() { // from class: com.instabug.library.annotation.AnnotationLayout.1
            @Override // com.instabug.library.annotation.ShapeSuggestionsLayout.InterfaceC0606a
            /* renamed from: a */
            public void mo1022a(int i) {
                if (i == 1) {
                    AnnotationLayout.this.annotationView.m1047a();
                }
            }
        });
        this.iconBrushLayout = (RelativeLayout) findViewById(C0577R.id.icon_brush_layout);
        this.iconBrush = (IconView) findViewById(C0577R.id.icon_brush);
        this.iconMagnify = (IconView) findViewById(C0577R.id.icon_magnify);
        this.iconBlur = (IconView) findViewById(C0577R.id.icon_blur);
        this.iconUndo = (IconView) findViewById(C0577R.id.icon_undo);
        this.iconBrush.setEnabled(false);
        this.iconMagnify.setEnabled(false);
        this.iconBlur.setEnabled(false);
        this.iconUndo.setEnabled(false);
        this.border = findViewById(C0577R.id.instabug_annotation_image_border);
        this.annotationView = (AnnotationView) findViewById(C0577R.id.instabug_annotation_image);
        this.colorPicker = (ColorPickerPopUpView) findViewById(C0577R.id.instabug_color_picker);
        this.brushIndicator = findViewById(C0577R.id.brush_indicator);
        this.annotationView.setDrawingMode(AnnotationView.EnumC0594b.DRAW_PATH);
        this.iconBrush.setTextColor(Instabug.getPrimaryColor());
        this.annotationView.setDrawingColor(this.colorPicker.getSelectedColor());
        this.annotationView.setOnActionDownListener(new AnnotationView.InterfaceC0597e() { // from class: com.instabug.library.annotation.AnnotationLayout.2
            @Override // com.instabug.library.annotation.AnnotationView.InterfaceC0597e
            /* renamed from: a */
            public void mo1023a() {
                if (AnnotationLayout.this.colorPicker.getVisibility() == 0) {
                    AnnotationLayout.this.colorPicker.setVisibility(8);
                }
                AnnotationLayout.this.shapeSuggestionsLayout.m1064b();
            }
        });
        this.annotationView.setOnPathRecognizedListener(new AnnotationView.InterfaceC0599g() { // from class: com.instabug.library.annotation.AnnotationLayout.3
            @Override // com.instabug.library.annotation.AnnotationView.InterfaceC0599g
            /* renamed from: a */
            public void mo1024a(Path path, Path path2) {
                AnnotationLayout.this.showShapeSuggestions(path, path2);
            }
        });
        this.annotationView.m2149setOnNewMagnifierAddingAbilityChangedListener(new AnnotationView.InterfaceC0598f() { // from class: com.instabug.library.annotation.AnnotationLayout.4
            @Override // com.instabug.library.annotation.AnnotationView.InterfaceC0598f
            /* renamed from: a */
            public void mo1025a(boolean z) {
                AnnotationLayout.this.iconMagnify.setEnabled(z);
            }
        });
        this.colorPicker.setOnColorSelectionListener(new ColorPickerPopUpView.InterfaceC0601b() { // from class: com.instabug.library.annotation.AnnotationLayout.5
            @Override // com.instabug.library.annotation.ColorPickerPopUpView.InterfaceC0601b
            /* renamed from: a */
            public void mo1026a(@ColorInt int i, int i2) {
                AnnotationLayout.this.annotationView.setDrawingColor(i);
                AnnotationLayout.this.colorPicker.setVisibility(8);
                AnnotationLayout.this.brushIndicator.setBackgroundColor(i);
            }
        });
        this.colorPicker.setPopUpBackgroundColor(AttrResolver.getBackgroundColor(getContext()));
        this.colorPicker.setPopUpBorderColor(AttrResolver.getDividerColor(getContext()));
        this.iconBrushLayout.setOnClickListener(this);
        this.iconMagnify.setOnClickListener(this);
        this.iconBlur.setOnClickListener(this);
        this.iconUndo.setOnClickListener(this);
        setViewSelector(this.iconMagnify);
        setViewSelector(this.iconUndo);
        if (Instabug.getTheme() == InstabugColorTheme.InstabugColorThemeLight) {
            this.tintingColor = ContextCompat.getColor(getContext(), C0577R.color.instabug_theme_tinting_color_light);
        } else {
            this.tintingColor = ContextCompat.getColor(getContext(), C0577R.color.instabug_theme_tinting_color_dark);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showShapeSuggestions(Path... pathArr) {
        for (Path path : pathArr) {
            this.shapeSuggestionsLayout.m1063a(path);
        }
        this.shapeSuggestionsLayout.m1062a();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBorder() {
        int convertDpToPx = ViewUtils.convertDpToPx(getContext(), 4.0f);
        int convertDpToPx2 = ViewUtils.convertDpToPx(getContext(), 2.0f);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(new RectShape());
        shapeDrawable.getPaint().setColor(Instabug.getPrimaryColor());
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        shapeDrawable.getPaint().setStrokeWidth(convertDpToPx);
        this.border.setPadding(convertDpToPx2, convertDpToPx2, convertDpToPx2, convertDpToPx2);
        if (Build.VERSION.SDK_INT >= 16) {
            this.border.setBackground(shapeDrawable);
        } else {
            this.border.setBackgroundDrawable(shapeDrawable);
        }
    }

    private void setViewSelector(final IconView iconView) {
        iconView.setOnTouchListener(new View.OnTouchListener() { // from class: com.instabug.library.annotation.AnnotationLayout.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    iconView.setTextColor(Instabug.getPrimaryColor());
                    return false;
                }
                if (motionEvent.getAction() == 1) {
                    iconView.setTextColor(AnnotationLayout.this.tintingColor);
                    return false;
                }
                return false;
            }
        });
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.shapeSuggestionsLayout.m1064b();
        int id = view.getId();
        if (id == C0577R.id.icon_brush_layout) {
            if (this.iconBrush.getCurrentTextColor() == Instabug.getPrimaryColor()) {
                switchColorPickerVisibility();
            }
            this.annotationView.setDrawingMode(AnnotationView.EnumC0594b.DRAW_PATH);
            resetColorSelection();
            this.iconBrush.setTextColor(Instabug.getPrimaryColor());
            return;
        }
        if (id == C0577R.id.icon_magnify) {
            this.annotationView.m1049b();
            hideColorPicker();
        } else {
            if (id == C0577R.id.icon_blur) {
                this.annotationView.setDrawingMode(AnnotationView.EnumC0594b.DRAW_BLUR);
                resetColorSelection();
                this.iconBlur.setTextColor(Instabug.getPrimaryColor());
                hideColorPicker();
                return;
            }
            if (id == C0577R.id.icon_undo) {
                this.annotationView.m1051d();
                hideColorPicker();
            }
        }
    }

    private void switchColorPickerVisibility() {
        this.colorPicker.setVisibility(this.colorPicker.getVisibility() == 0 ? 8 : 0);
    }

    private void hideColorPicker() {
        if (this.colorPicker.getVisibility() == 0) {
            this.colorPicker.setVisibility(8);
        }
    }

    private void resetColorSelection() {
        int childCount = this.annotationActionsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.annotationActionsContainer.getChildAt(i) instanceof IconView) {
                ((TextView) this.annotationActionsContainer.getChildAt(i)).setTextColor(this.tintingColor);
            }
        }
        this.iconBrush.setTextColor(this.tintingColor);
    }

    public void setBaseImage(@NonNull Uri uri, @Nullable final Runnable runnable) {
        BitmapUtils.loadBitmap(uri.getPath(), this.annotationView, new AsyncTaskC0753a.a() { // from class: com.instabug.library.annotation.AnnotationLayout.7
            @Override // com.instabug.library.util.AsyncTaskC0753a.a
            /* renamed from: a */
            public void mo1027a() {
                AnnotationLayout.this.enableButtons();
                AnnotationLayout.this.setBorder();
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableButtons() {
        this.iconBrush.setEnabled(true);
        this.iconMagnify.setEnabled(true);
        this.iconBlur.setEnabled(true);
        this.iconUndo.setEnabled(true);
    }

    @Nullable
    public Bitmap getAnnotatedBitmap() {
        return this.annotationView.m1050c();
    }
}
