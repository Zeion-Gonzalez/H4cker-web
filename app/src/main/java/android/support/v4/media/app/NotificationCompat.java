package android.support.v4.media.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.mediacompat.C0081R;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

/* loaded from: classes.dex */
public class NotificationCompat {
    private NotificationCompat() {
    }

    /* loaded from: classes.dex */
    public static class MediaStyle extends NotificationCompat.Style {
        private static final int MAX_MEDIA_BUTTONS = 5;
        private static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
        int[] mActionsToShowInCompact = null;
        PendingIntent mCancelButtonIntent;
        boolean mShowCancelButton;
        MediaSessionCompat.Token mToken;

        public static MediaSessionCompat.Token getMediaSession(Notification notification) {
            Bundle extras = android.support.v4.app.NotificationCompat.getExtras(notification);
            if (extras != null) {
                if (Build.VERSION.SDK_INT >= 21) {
                    Parcelable tokenInner = extras.getParcelable(android.support.v4.app.NotificationCompat.EXTRA_MEDIA_SESSION);
                    if (tokenInner != null) {
                        return MediaSessionCompat.Token.fromToken(tokenInner);
                    }
                } else {
                    IBinder tokenInner2 = BundleCompat.getBinder(extras, android.support.v4.app.NotificationCompat.EXTRA_MEDIA_SESSION);
                    if (tokenInner2 != null) {
                        Parcel p = Parcel.obtain();
                        p.writeStrongBinder(tokenInner2);
                        p.setDataPosition(0);
                        MediaSessionCompat.Token createFromParcel = MediaSessionCompat.Token.CREATOR.createFromParcel(p);
                        p.recycle();
                        return createFromParcel;
                    }
                }
            }
            return null;
        }

        public MediaStyle() {
        }

        public MediaStyle(NotificationCompat.Builder builder) {
            setBuilder(builder);
        }

        public MediaStyle setShowActionsInCompactView(int... actions) {
            this.mActionsToShowInCompact = actions;
            return this;
        }

        public MediaStyle setMediaSession(MediaSessionCompat.Token token) {
            this.mToken = token;
            return this;
        }

        public MediaStyle setShowCancelButton(boolean show) {
            if (Build.VERSION.SDK_INT < 21) {
                this.mShowCancelButton = show;
            }
            return this;
        }

        public MediaStyle setCancelButtonIntent(PendingIntent pendingIntent) {
            this.mCancelButtonIntent = pendingIntent;
            return this;
        }

        @Override // android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public void apply(NotificationBuilderWithBuilderAccessor builder) {
            if (Build.VERSION.SDK_INT >= 21) {
                builder.getBuilder().setStyle(fillInMediaStyle(new Notification.MediaStyle()));
            } else if (this.mShowCancelButton) {
                builder.getBuilder().setOngoing(true);
            }
        }

        @RequiresApi(21)
        Notification.MediaStyle fillInMediaStyle(Notification.MediaStyle style) {
            if (this.mActionsToShowInCompact != null) {
                style.setShowActionsInCompactView(this.mActionsToShowInCompact);
            }
            if (this.mToken != null) {
                style.setMediaSession((MediaSession.Token) this.mToken.getToken());
            }
            return style;
        }

        @Override // android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor builder) {
            if (Build.VERSION.SDK_INT >= 21) {
                return null;
            }
            return generateContentView();
        }

        RemoteViews generateContentView() {
            RemoteViews view = applyStandardTemplate(false, getContentViewLayoutResource(), true);
            int numActions = this.mBuilder.mActions.size();
            int numActionsInCompact = this.mActionsToShowInCompact == null ? 0 : Math.min(this.mActionsToShowInCompact.length, 3);
            view.removeAllViews(C0081R.id.media_actions);
            if (numActionsInCompact > 0) {
                for (int i = 0; i < numActionsInCompact; i++) {
                    if (i >= numActions) {
                        throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", Integer.valueOf(i), Integer.valueOf(numActions - 1)));
                    }
                    NotificationCompat.Action action = this.mBuilder.mActions.get(this.mActionsToShowInCompact[i]);
                    RemoteViews button = generateMediaActionButton(action);
                    view.addView(C0081R.id.media_actions, button);
                }
            }
            if (this.mShowCancelButton) {
                view.setViewVisibility(C0081R.id.end_padder, 8);
                view.setViewVisibility(C0081R.id.cancel_action, 0);
                view.setOnClickPendingIntent(C0081R.id.cancel_action, this.mCancelButtonIntent);
                view.setInt(C0081R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(C0081R.integer.cancel_button_image_alpha));
            } else {
                view.setViewVisibility(C0081R.id.end_padder, 0);
                view.setViewVisibility(C0081R.id.cancel_action, 8);
            }
            return view;
        }

        private RemoteViews generateMediaActionButton(NotificationCompat.Action action) {
            boolean tombstone = action.getActionIntent() == null;
            RemoteViews button = new RemoteViews(this.mBuilder.mContext.getPackageName(), C0081R.layout.notification_media_action);
            button.setImageViewResource(C0081R.id.action0, action.getIcon());
            if (!tombstone) {
                button.setOnClickPendingIntent(C0081R.id.action0, action.getActionIntent());
            }
            if (Build.VERSION.SDK_INT >= 15) {
                button.setContentDescription(C0081R.id.action0, action.getTitle());
            }
            return button;
        }

        int getContentViewLayoutResource() {
            return C0081R.layout.notification_template_media;
        }

        @Override // android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor builder) {
            if (Build.VERSION.SDK_INT >= 21) {
                return null;
            }
            return generateBigContentView();
        }

        RemoteViews generateBigContentView() {
            int actionCount = Math.min(this.mBuilder.mActions.size(), 5);
            RemoteViews big = applyStandardTemplate(false, getBigContentViewLayoutResource(actionCount), false);
            big.removeAllViews(C0081R.id.media_actions);
            if (actionCount > 0) {
                for (int i = 0; i < actionCount; i++) {
                    RemoteViews button = generateMediaActionButton(this.mBuilder.mActions.get(i));
                    big.addView(C0081R.id.media_actions, button);
                }
            }
            if (this.mShowCancelButton) {
                big.setViewVisibility(C0081R.id.cancel_action, 0);
                big.setInt(C0081R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(C0081R.integer.cancel_button_image_alpha));
                big.setOnClickPendingIntent(C0081R.id.cancel_action, this.mCancelButtonIntent);
            } else {
                big.setViewVisibility(C0081R.id.cancel_action, 8);
            }
            return big;
        }

        int getBigContentViewLayoutResource(int actionCount) {
            return actionCount <= 3 ? C0081R.layout.notification_template_big_media_narrow : C0081R.layout.notification_template_big_media;
        }
    }

    /* loaded from: classes.dex */
    public static class DecoratedMediaCustomViewStyle extends MediaStyle {
        @Override // android.support.v4.media.app.NotificationCompat.MediaStyle, android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public void apply(NotificationBuilderWithBuilderAccessor builder) {
            if (Build.VERSION.SDK_INT >= 24) {
                builder.getBuilder().setStyle(fillInMediaStyle(new Notification.DecoratedMediaCustomViewStyle()));
            } else {
                super.apply(builder);
            }
        }

        @Override // android.support.v4.media.app.NotificationCompat.MediaStyle, android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor builder) {
            if (Build.VERSION.SDK_INT >= 24) {
                return null;
            }
            boolean hasContentView = this.mBuilder.getContentView() != null;
            if (Build.VERSION.SDK_INT >= 21) {
                boolean createCustomContent = hasContentView || this.mBuilder.getBigContentView() != null;
                if (createCustomContent) {
                    RemoteViews contentView = generateContentView();
                    if (hasContentView) {
                        buildIntoRemoteViews(contentView, this.mBuilder.getContentView());
                    }
                    setBackgroundColor(contentView);
                    return contentView;
                }
            } else {
                RemoteViews contentView2 = generateContentView();
                if (hasContentView) {
                    buildIntoRemoteViews(contentView2, this.mBuilder.getContentView());
                    return contentView2;
                }
            }
            return null;
        }

        @Override // android.support.v4.media.app.NotificationCompat.MediaStyle
        int getContentViewLayoutResource() {
            return this.mBuilder.getContentView() != null ? C0081R.layout.notification_template_media_custom : super.getContentViewLayoutResource();
        }

        @Override // android.support.v4.media.app.NotificationCompat.MediaStyle, android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor builder) {
            RemoteViews innerView;
            RemoteViews bigContentView = null;
            if (Build.VERSION.SDK_INT < 24) {
                if (this.mBuilder.getBigContentView() != null) {
                    innerView = this.mBuilder.getBigContentView();
                } else {
                    innerView = this.mBuilder.getContentView();
                }
                if (innerView != null) {
                    bigContentView = generateBigContentView();
                    buildIntoRemoteViews(bigContentView, innerView);
                    if (Build.VERSION.SDK_INT >= 21) {
                        setBackgroundColor(bigContentView);
                    }
                }
            }
            return bigContentView;
        }

        @Override // android.support.v4.media.app.NotificationCompat.MediaStyle
        int getBigContentViewLayoutResource(int actionCount) {
            return actionCount <= 3 ? C0081R.layout.notification_template_big_media_narrow_custom : C0081R.layout.notification_template_big_media_custom;
        }

        @Override // android.support.v4.app.NotificationCompat.Style
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor builder) {
            RemoteViews innerView;
            RemoteViews headsUpContentView = null;
            if (Build.VERSION.SDK_INT < 24) {
                if (this.mBuilder.getHeadsUpContentView() != null) {
                    innerView = this.mBuilder.getHeadsUpContentView();
                } else {
                    innerView = this.mBuilder.getContentView();
                }
                if (innerView != null) {
                    headsUpContentView = generateBigContentView();
                    buildIntoRemoteViews(headsUpContentView, innerView);
                    if (Build.VERSION.SDK_INT >= 21) {
                        setBackgroundColor(headsUpContentView);
                    }
                }
            }
            return headsUpContentView;
        }

        private void setBackgroundColor(RemoteViews views) {
            int color;
            if (this.mBuilder.getColor() != 0) {
                color = this.mBuilder.getColor();
            } else {
                color = this.mBuilder.mContext.getResources().getColor(C0081R.color.notification_material_background_media_default_color);
            }
            views.setInt(C0081R.id.status_bar_latest_event_content, "setBackgroundColor", color);
        }
    }
}
