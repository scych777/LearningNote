package com.mattyang.qrcodedemo;

import android.content.Context;
import android.support.annotation.UiThread;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.mattyang.qrcodedemo.ui.GraphicOverlay;

public class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<BarcodeGraphic> mOverlay;
    private BarcodeGraphic mGraphic;
    private BarcodeUpdateListener mBarcodeUpdateListener;

    public interface BarcodeUpdateListener{
        @UiThread
        void onBarcodeDetected(Barcode barcode);
    }

    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> mOverlay, BarcodeGraphic mGraphic, Context context){
        this.mOverlay = mOverlay;
        this.mGraphic = mGraphic;
        if(context instanceof BarcodeUpdateListener){
            this.mBarcodeUpdateListener = (BarcodeUpdateListener) context;
        }else{
            throw new RuntimeException("Hosting activity must implement BarcodeUpdateListener");
        }
    }

    @Override
    public void onNewItem(int i, Barcode barcode) {
        mGraphic.setId(i);
        mBarcodeUpdateListener.onBarcodeDetected(barcode);
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detections, Barcode barcode) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(barcode);
    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detections) {
        mOverlay.remove(mGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }
}
