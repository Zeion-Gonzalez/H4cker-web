package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class AnimationUtilsCompat {
    public static Interpolator loadInterpolator(Context context, int id) throws Resources.NotFoundException {
        Interpolator createInterpolatorFromXml;
        if (Build.VERSION.SDK_INT >= 21) {
            return AnimationUtils.loadInterpolator(context, id);
        }
        XmlResourceParser parser = null;
        try {
            try {
                try {
                    if (id == 17563663) {
                        createInterpolatorFromXml = new FastOutLinearInInterpolator();
                    } else if (id == 17563661) {
                        createInterpolatorFromXml = new FastOutSlowInInterpolator();
                        if (0 != 0) {
                            parser.close();
                        }
                    } else if (id == 17563662) {
                        createInterpolatorFromXml = new LinearOutSlowInInterpolator();
                        if (0 != 0) {
                            parser.close();
                        }
                    } else {
                        parser = context.getResources().getAnimation(id);
                        createInterpolatorFromXml = createInterpolatorFromXml(context, context.getResources(), context.getTheme(), parser);
                        if (parser != null) {
                            parser.close();
                        }
                    }
                    return createInterpolatorFromXml;
                } catch (IOException ex) {
                    Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                    rnf.initCause(ex);
                    throw rnf;
                }
            } catch (XmlPullParserException ex2) {
                Resources.NotFoundException rnf2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                rnf2.initCause(ex2);
                throw rnf2;
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:43:0x00cc, code lost:
    
        return r2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static android.view.animation.Interpolator createInterpolatorFromXml(android.content.Context r8, android.content.res.Resources r9, android.content.res.Resources.Theme r10, org.xmlpull.v1.XmlPullParser r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r2 = 0
            int r1 = r11.getDepth()
        L5:
            int r4 = r11.next()
            r5 = 3
            if (r4 != r5) goto L12
            int r5 = r11.getDepth()
            if (r5 <= r1) goto Lcc
        L12:
            r5 = 1
            if (r4 == r5) goto Lcc
            r5 = 2
            if (r4 != r5) goto L5
            android.util.AttributeSet r0 = android.util.Xml.asAttributeSet(r11)
            java.lang.String r3 = r11.getName()
            java.lang.String r5 = "linearInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L2e
            android.view.animation.LinearInterpolator r2 = new android.view.animation.LinearInterpolator
            r2.<init>()
            goto L5
        L2e:
            java.lang.String r5 = "accelerateInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L3c
            android.view.animation.AccelerateInterpolator r2 = new android.view.animation.AccelerateInterpolator
            r2.<init>(r8, r0)
            goto L5
        L3c:
            java.lang.String r5 = "decelerateInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L4a
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>(r8, r0)
            goto L5
        L4a:
            java.lang.String r5 = "accelerateDecelerateInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L58
            android.view.animation.AccelerateDecelerateInterpolator r2 = new android.view.animation.AccelerateDecelerateInterpolator
            r2.<init>()
            goto L5
        L58:
            java.lang.String r5 = "cycleInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L66
            android.view.animation.CycleInterpolator r2 = new android.view.animation.CycleInterpolator
            r2.<init>(r8, r0)
            goto L5
        L66:
            java.lang.String r5 = "anticipateInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L74
            android.view.animation.AnticipateInterpolator r2 = new android.view.animation.AnticipateInterpolator
            r2.<init>(r8, r0)
            goto L5
        L74:
            java.lang.String r5 = "overshootInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L82
            android.view.animation.OvershootInterpolator r2 = new android.view.animation.OvershootInterpolator
            r2.<init>(r8, r0)
            goto L5
        L82:
            java.lang.String r5 = "anticipateOvershootInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L91
            android.view.animation.AnticipateOvershootInterpolator r2 = new android.view.animation.AnticipateOvershootInterpolator
            r2.<init>(r8, r0)
            goto L5
        L91:
            java.lang.String r5 = "bounceInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto La0
            android.view.animation.BounceInterpolator r2 = new android.view.animation.BounceInterpolator
            r2.<init>()
            goto L5
        La0:
            java.lang.String r5 = "pathInterpolator"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto Laf
            android.support.graphics.drawable.PathInterpolatorCompat r2 = new android.support.graphics.drawable.PathInterpolatorCompat
            r2.<init>(r8, r0, r11)
            goto L5
        Laf:
            java.lang.RuntimeException r5 = new java.lang.RuntimeException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Unknown interpolator name: "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r11.getName()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        Lcc:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.AnimationUtilsCompat.createInterpolatorFromXml(android.content.Context, android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser):android.view.animation.Interpolator");
    }
}
