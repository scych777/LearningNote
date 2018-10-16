package com.mattyang.qrcodedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.barcode.Barcode;
import com.mattyang.qrcodedemo.ui.GraphicOverlay;

public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN

    };

    private static int mCurrentColorIndex = 0;
    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;
    Bitmap bitmap;

    BarcodeGraphic(GraphicOverlay overlay){
        super(overlay);
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(10.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
        bitmap = BitmapFactory.decodeResource(MainActivity.mContext.getResources(),R.drawable.check_circle);
    }

    public int getId(){
        return mId;
    }

    public void setId(int id){
        this.mId = id;
    }

    public Barcode getBarcode(){
        return mBarcode;
    }

    void updateItem(Barcode barcode){
        mBarcode = barcode;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if(barcode == null){
            return;
        }

        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect,mRectPaint);
//        canvas.drawText(barcode.rawValue,rect.left,rect.bottom,mTextPaint);
        canvas.drawBitmap(bitmap,barcode.getBoundingBox().centerX(),barcode.getBoundingBox().centerY(),mTextPaint);
    }
}
