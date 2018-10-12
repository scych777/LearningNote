package com.mattyang.qrcodedemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.mattyang.qrcodedemo.ui.CameraSourcePreview;
import com.mattyang.qrcodedemo.ui.GraphicOverlay;

import java.io.IOException;

public class BarcodeCaptureActivity extends AppCompatActivity implements BarcodeGraphicTracker.BarcodeUpdateListener{
    private static final String TAG = "BarcodeScanner";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_capture);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash,false);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(rc == PackageManager.PERMISSION_GRANTED){
            createCameraSource(autoFocus,useFlash);
        }else{
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this,new CaptureGestureListener());
    }

    private void requestCameraPermission() {
        Log.w(TAG,"Camera permission is not granted. Requesting permission");
        final String[] permissions = new String []{Manifest.permission.CAMERA};
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(this,permissions,RC_HANDLE_CAMERA_PERM);
            return;
        }
        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity,permissions,RC_HANDLE_CAMERA_PERM);
            }
        };
        findViewById(R.id.topLayout).setOnClickListener(listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean c = gestureDetector.onTouchEvent(event);
        return c || super.onTouchEvent(event);
    }

    private void createCameraSource(boolean autoFocus, boolean useFlash){
        Context context = getApplicationContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeTrackerFactory = new BarcodeTrackerFactory(mGraphicOverlay,this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeTrackerFactory).build());
        if(!barcodeDetector.isOperational()){
            Log.w(TAG,"Dectector dependencies are not yet available");
        }
        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean hasLowStorage = registerReceiver(null,lowstorageFilter) != null;
        if(hasLowStorage){
            Log.w(TAG,"low storage error");
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(),barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600,1024)
                .setRequestedFps(15.0f);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            builder = builder.setAutoFocusEnabled(autoFocus);
        }
        mCameraSource = builder.build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mPreview != null){
            mPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPreview != null){
            mPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode != RC_HANDLE_CAMERA_PERM){
            Log.d(TAG,"Got unexpected permission result:"+ requestCode);
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
            return;
        }
        if(grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"Camera permission granted - initialize the camera source");
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            createCameraSource(autoFocus,false);
            return;
        }
        Log.e(TAG,"Permission not granted: result len = " + grantResults.length );
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok,listener)
                .show();
    }

    private void startCameraSource() throws SecurityException{
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if(code != ConnectionResult.SUCCESS){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,code,RC_HANDLE_GMS);
            dialog.show();
        }

        if(mCameraSource != null){
            try {
                mPreview.start(mCameraSource,mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG,"Unable to start camera source. ",e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private boolean onTap(float rawX, float rawY){
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for(BarcodeGraphic graphic : mGraphicOverlay.getGraphics()){
            Barcode barcode = graphic.getBarcode();
            if(barcode.getBoundingBox().contains((int)x,(int)y)){
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);
            if(distance < bestDistance){
                best = barcode;
                bestDistance = distance;
            }
        }

        if(best != null){
            Intent data = new Intent();
            data.putExtra(BarcodeObject,best);
            setResult(CommonStatusCodes.SUCCESS,data);
            finish();
            return true;
        }
        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(),e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {

    }
}
