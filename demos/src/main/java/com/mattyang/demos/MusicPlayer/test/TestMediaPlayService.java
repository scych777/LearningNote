package com.mattyang.demos.MusicPlayer.test;

import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.mattyang.demos.R;

import java.util.List;

public class TestMediaPlayService extends MediaBrowserServiceCompat {
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mStateBuilder;
    private final String LOG_TAG = "MediaPlayService";
    @Override
    public void onCreate() {
        super.onCreate();
        //create a MediaSessionCompat
        mMediaSessionCompat = new MediaSessionCompat(this,LOG_TAG);
        //Enable callbacks from MediaButton and TransportControls
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //Set an initial PlaybackState with ACTION_PLAY
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY|PlaybackStateCompat.ACTION_PLAY_PAUSE
        );
        mMediaSessionCompat.setPlaybackState(mStateBuilder.build());
        //Hanlde callbacks from MediaControl
        mMediaSessionCompat.setCallback(new testSessionCallback());
        //set token to sessionCompat so that clients can communicate with it
        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    @Override
    public BrowserRoot onGetRoot(String s, int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String s, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    private class testSessionCallback extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            super.onPlay();
            displayMediaNotification();
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

        @Override
        public void onStop() {
            super.onStop();
        }
    }

    private void displayMediaNotification(){
        MediaControllerCompat controller = mMediaSessionCompat.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat mediaDescription = mediaMetadata.getDescription();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"medianotification");
        mBuilder.setContentTitle(mediaDescription.getTitle())
                .setContentText(mediaDescription.getSubtitle())
                .setSubText(mediaDescription.getDescription())
                .setLargeIcon(mediaDescription.getIconBitmap())
                //Allow user click notification to go back to player activity
                .setContentIntent(controller.getSessionActivity())
                //stop service when notification swipe away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP))
                //show transport controls in lock screen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .addAction(new NotificationCompat.Action(R.drawable.ic_launcher_foreground,"pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mMediaSessionCompat.getSessionToken())
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP)));
        startForeground(1,mBuilder.build());
    }

}
