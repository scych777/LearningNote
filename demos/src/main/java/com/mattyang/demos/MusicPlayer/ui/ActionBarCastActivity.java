package com.mattyang.demos.MusicPlayer.ui;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mattyang.demos.MusicPlayer.utils.LogHelper;
import com.mattyang.demos.R;

public class ActionBarCastActivity extends AppCompatActivity {
    private static final String TAG = LogHelper.makeLogTag(ActionBarCastActivity.class);
    private boolean mToolbarInitialized;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private int mItemToOpenWhenDrawerCloses = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.d(TAG,"Activity onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mToolbarInitialized){
            throw new IllegalStateException("You must run super.initializeToolbar at "+
            "the end of your onCreate method");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null){
            mDrawerToggle.syncState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null){
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        getFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        if(item != null && item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mToolbar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mToolbar.setTitle(titleId);
    }

    protected void initializeToolbar(){
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolbar == null){
            throw new IllegalStateException("Layout is required to include a Toolbar with id "+
            "'toolbar'");
        }
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(mDrawerLayout != null){
//            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//            if(navigationView == null){
//                throw new IllegalStateException("Layout requires a NavigationView "+
//                "with id 'nav_view'");
//            }
//            mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open_content_drawer,
//                    R.string.close_content_drawer);
//            mDrawerLayout.setDrawerListener(mDrawerListener);
//            populateDrawerItems(navigationView);
//            setSupportActionBar(mToolbar);
//            updateDrawerToggle();
        }else{
            setSupportActionBar(mToolbar);
        }
        mToolbarInitialized = true;
    }

    private void populateDrawerItems(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mItemToOpenWhenDrawerCloses = menuItem.getItemId();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
//        if(MusicPlayerActivity.class.isAssignableFrom(getClass())){
//            navigationView.setCheckedItem(R.id.navigation_allmusic);
//        }else if(PlaceholerActivity.class.isAssignableFrom(getClass())){
//            navigationView.setCheckedItem(R.id.navigation_playlists);
//        }
    }

    private void updateDrawerToggle(){
        if(mDrawerToggle == null){
            return;
        }
        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }
        if(isRoot){
            mDrawerToggle.syncState();
        }
    }

    private final DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {
            if(mDrawerToggle != null){mDrawerToggle.onDrawerSlide(view,v);}
        }

        @Override
        public void onDrawerOpened(@NonNull View view) {
            if(mDrawerToggle != null)
                mDrawerToggle.onDrawerOpened(view);
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(R.string.app_name);
        }

        @Override
        public void onDrawerClosed(@NonNull View view) {
            if(mDrawerToggle != null)
                mDrawerToggle.onDrawerClosed(view);
            if(mItemToOpenWhenDrawerCloses >= 0){
                Bundle extras = ActivityOptions.makeCustomAnimation(ActionBarCastActivity.this,
                        R.anim.fade_in,R.anim.fade_out).toBundle();
            Class activityClass = null;
            switch (mItemToOpenWhenDrawerCloses){
//                case R.id.navigation_allmusic:
//                    activityClass = MusicPlayerActivity.class;
//                case R.id.navigation_playlists:
//                    activityClass = PlaceholderActivity.class;
            }
            if(activityClass != null){
                startActivity(new Intent(ActionBarCastActivity.this,activityClass),extras);
            }
            }
        }

        @Override
        public void onDrawerStateChanged(int i) {
            if(mDrawerToggle != null)
                mDrawerToggle.onDrawerStateChanged(i);
        }
    };

    private final android.support.v4.app.FragmentManager.OnBackStackChangedListener mBackStackChangedListener =
            new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    updateDrawerToggle();
                }
            };
}
