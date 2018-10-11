package com.mattyang.qrcodedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mattyang.qrcodedemo.ui.CameraSourcePreview;

public class BarcodeCaptureActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeScanner";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSourcePreview mPreview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_capture);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

    }
}
