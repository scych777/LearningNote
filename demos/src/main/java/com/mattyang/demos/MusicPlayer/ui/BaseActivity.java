package com.mattyang.demos.MusicPlayer.ui;

import android.support.v4.media.MediaBrowserCompat;

import com.mattyang.demos.MusicPlayer.utils.LogHelper;

public class BaseActivity extends ActionBarCastActivity implements MediaBrowserProvider {
    private static final String TAG = LogHelper.makeLogTag(BaseActivity.class);
    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return null;
    }
}
