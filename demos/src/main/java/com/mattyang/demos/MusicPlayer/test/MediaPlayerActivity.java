package com.mattyang.demos.MusicPlayer.test;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;

public class MediaPlayerActivity extends AppCompatActivity {
    private MediaBrowserCompat mMediaBrowser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaBrowser = new MediaBrowserCompat(this,new ComponentName(this,MediaPlaybackService.class)
                                                ,mConnectionCallbacks,null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(MediaControllerCompat.getMediaController(MediaPlayerActivity.this) != null){
            MediaControllerCompat.getMediaController(MediaPlayerActivity.this).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback(){
                @Override
                public void onConnected() {
                    super.onConnected();
                    MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();
                    try {
                        MediaControllerCompat mediaController =  new MediaControllerCompat(MediaPlayerActivity.this,
                                token);
                        MediaControllerCompat.setMediaController(MediaPlayerActivity.this,
                                mediaController);
                        buildTransportControls();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionSuspended() {
                    super.onConnectionSuspended();
                }

                @Override
                public void onConnectionFailed() {
                    super.onConnectionFailed();
                }
            };

    //Bind UI
    void buildTransportControls(){
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(MediaPlayerActivity.this);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();
        mediaController.registerCallback(controllerCallback);
    }

    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }
    };
}
