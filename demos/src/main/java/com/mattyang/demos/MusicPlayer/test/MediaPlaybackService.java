package com.mattyang.demos.MusicPlayer.test;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.mattyang.demos.R;

import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private static final String LOG_TAG = "MediaPlaybackService";
    private static final String channelId = "MediaPlaybackServiceNotification";

    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Context mContext;


    public MediaPlaybackService(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaSessionCompat = new MediaSessionCompat(mContext,LOG_TAG);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY |
                                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSessionCompat.setPlaybackState(mStateBuilder.build());
        mMediaSessionCompat.setCallback(new MysessionCallback());
        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String s, int i, @Nullable Bundle bundle) {
        if(allowBrowsing(s,i)){
            return new BrowserRoot(MY_MEDIA_ROOT_ID,null);
        }else {
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID,null);
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentMediaId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if(TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID,parentMediaId)){
            result.sendResult(null);
            return;
        }
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        if(MY_MEDIA_ROOT_ID.equals(parentMediaId)){
            //Build the MediaItem objects for the top level and put them in the list
        }else{

        }
        result.sendResult(mediaItems);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean allowBrowsing(String clientPackageName, int clientUid){
        if(TextUtils.equals(clientPackageName,"com.demo.carplay.music")){
            return true;
        }
        return false;
    }

    private class MysessionCallback extends MediaSessionCompat.Callback{
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            startService(new Intent(mContext, MediaBrowserService.class));
            mMediaSessionCompat.setActive(true);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.start();
            foregroundNotifications();
        }

        @Override
        public void onStop() {
            super.onStop();

        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }
    }

    private void foregroundNotifications(){
        MediaControllerCompat controller = mMediaSessionCompat.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext,channelId);
        builder.setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark))
                .addAction(new NotificationCompat.Action(R.drawable.ic_launcher_background,"Pause",MediaButtonReceiver
                .buildMediaButtonPendingIntent(mContext,PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mMediaSessionCompat.getSessionToken())
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_STOP)));
        startForeground(1,builder.build());
    }
}
