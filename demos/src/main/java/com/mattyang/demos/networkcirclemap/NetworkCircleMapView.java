package com.mattyang.demos.networkcirclemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.mattyang.demos.R;


public class NetworkCircleMapView extends View {
    private int centerX = 0;
    private int centerY = 0;
    private int mainCircleRadius = 340;
    private int mainItemRadius = 75;
    private int mainItemSelectedRadius = 88;
    private int mainItemTouchArea = 100;
    private int subCircleRadius = 175;
    private int subItemRadius = 25;
    private int subItemSelectedRadius = 33;
    private int subItemTouchArea = 67;
    private int maxPointsCount = 9;
    private int mainCount = 4;
    private int subCount = maxPointsCount - mainCount - 1;
    private int mainSelectedItemTouchAreaY = 0;
    private int subSelectedItemTouchAreaY = 0;
    Context mContext;
    Bitmap bitmap;


    public NetworkCircleMapView(Context context) {
        super(context);
        this.mContext = context;
        initData();
    }

    public NetworkCircleMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
         bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.router);
         initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centerX = (right - left - getPaddingLeft() - getPaddingRight()) / 2;
        centerY = (bottom - top - getPaddingBottom() - getPaddingTop()) / 2;
    }

    private void initData(){
        mainCircleRadius = dp2px(140);
        mainItemRadius = dp2px(28);
        mainItemSelectedRadius = dp2px(32);
        mainItemTouchArea = dp2px(40);
        subCircleRadius = dp2px(70);
        subItemRadius = dp2px(10);
        subItemSelectedRadius = dp2px(13);
        subItemTouchArea = dp2px(22);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.light_blue));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX,centerY,mainCircleRadius,paint);
        paint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX,centerY,mainItemRadius,paint);
        canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Rect(centerX - bitmap.getWidth() /2,
                                            centerY - bitmap.getHeight() /2,centerX + bitmap.getWidth() /2,centerY + bitmap.getHeight() /2),paint);


        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mContext.getResources().getColor(R.color.light_blue));
        paint.setStrokeWidth(10);
        canvas.drawCircle(centerX,centerY,mainItemSelectedRadius,paint);


        paint.setColor(mContext.getResources().getColor(R.color.STATIC_COLOR));
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.FILL);
        Rect rect = new Rect();
        String firstValue = "2 Floor";
        paint.setTextSize(35);
        if(firstValue != null && firstValue.length() > 0){
            float textWidth = paint.measureText(firstValue);
            if(textWidth > mainItemTouchArea * 2){
                int subIndex = paint.breakText(firstValue,0,firstValue.length(),true,mainItemTouchArea * 2,null);
                firstValue = firstValue.substring(0,subIndex - 3) + "...";
            }
        }
        paint.getTextBounds(firstValue,0,firstValue.length(),rect);
        canvas.drawText(firstValue,centerX - ((rect.right - rect.left)/2),centerY + mainItemSelectedRadius + (rect.bottom - rect.top) + 10,paint);
        int centerToFirstLine = centerY + mainItemSelectedRadius + (rect.bottom - rect.top) + 20;


        paint.setColor(mContext.getResources().getColor(R.color.light_blue));
        rect = new Rect();
        String secondValue = "Chelsea Iphone";
        if(secondValue != null && secondValue.length() > 0){
            float width = paint.measureText(secondValue);
            if(width > mainItemTouchArea * 2){
                int subIndex = paint.breakText(secondValue,0,secondValue.length(),true,mainItemTouchArea * 2,null);
                secondValue = secondValue.substring(0,subIndex - 3) + "...";
            }
        }
        paint.getTextBounds(secondValue,0,secondValue.length(),rect);
        canvas.drawText(secondValue,centerX - ((rect.right - rect.left)/2),centerToFirstLine + (rect.bottom - rect.top)+5,paint);
        mainSelectedItemTouchAreaY = centerToFirstLine + (rect.bottom - rect.top)+5;

        Path path = new Path();
        path.moveTo(centerX - mainItemTouchArea,centerY - mainItemTouchArea);
        path.lineTo(centerX + mainItemTouchArea,centerY - mainItemTouchArea);
        path.lineTo(centerX + mainItemTouchArea,mainSelectedItemTouchAreaY);
        path.lineTo(centerX - mainItemTouchArea,mainSelectedItemTouchAreaY);
        path.lineTo(centerX - mainItemTouchArea,centerY - mainItemTouchArea);
        Paint pa3 = new Paint();
        pa3.setColor(Color.RED);
        pa3.setStrokeWidth(5);
        pa3.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path,pa3);

        float[] mainPoints = generatePoints(centerX,centerY,mainCircleRadius,mainCount);
        Paint secondPaint = new Paint();
        secondPaint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
        secondPaint.setStrokeWidth(30);
        secondPaint.setStyle(Paint.Style.FILL);
        Paint thirdPaint = new Paint();
        Paint fourthPaint = new Paint();
        Paint fifthPaint = new Paint();
        for(int i = 0; i < mainCount;i++){

            canvas.drawCircle(mainPoints[i * 2],mainPoints[i * 2 + 1],subItemRadius,secondPaint);


            thirdPaint.setColor(mContext.getResources().getColor(R.color.STATIC_COLOR));
            thirdPaint.setStrokeWidth(8);
            thirdPaint.setStyle(Paint.Style.FILL);
            Rect subRect = new Rect();
            String subfirstValue = "Lobby";
            thirdPaint.setTextSize(35);
            if(subfirstValue != null && subfirstValue.length() > 0){
                float textWidth = thirdPaint.measureText(subfirstValue);
                if(textWidth > subItemTouchArea * 2){
                    int subIndex = thirdPaint.breakText(subfirstValue,0,subfirstValue.length(),true,subItemTouchArea * 2,null);
                    subfirstValue = subfirstValue.substring(0,subIndex - 3) + "...";
                }
            }
            thirdPaint.getTextBounds(subfirstValue,0,subfirstValue.length(),subRect);
            canvas.drawText(subfirstValue,mainPoints[i * 2] - ((subRect.right - subRect.left)/2),mainPoints[i * 2 + 1] + subItemSelectedRadius + (subRect.bottom - subRect.top) + 10,thirdPaint);
            int subCenterToFirstLine = (int)(mainPoints[i * 2 + 1] + subItemSelectedRadius + (subRect.bottom - subRect.top) + 10);

            if(i != 0) {
                Path path1 = new Path();
                path1.moveTo(mainPoints[i * 2] - subItemTouchArea, mainPoints[i * 2 + 1] - subItemTouchArea);
                path1.lineTo(mainPoints[i * 2] + subItemTouchArea, mainPoints[i * 2 + 1] - subItemTouchArea);
                path1.lineTo(mainPoints[i * 2] + subItemTouchArea, mainPoints[i * 2 + 1] + subItemTouchArea);
                path1.lineTo(mainPoints[i * 2] - subItemTouchArea, mainPoints[i * 2 + 1] + subItemTouchArea);
                path1.lineTo(mainPoints[i * 2] - subItemTouchArea, mainPoints[i * 2 + 1] - subItemTouchArea);
                Paint pa = new Paint();
                pa.setColor(Color.RED);
                pa.setStrokeWidth(5);
                pa.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path1, pa);
            }


            if(i == 0){
                fourthPaint.setColor(mContext.getResources().getColor(R.color.light_blue));
                fourthPaint.setStrokeWidth(8);
                fourthPaint.setTextSize(35);
                fourthPaint.setStyle(Paint.Style.FILL);
                Rect secondRect = new Rect();
                String subsecondValue = "Teri iPhone";
                if(subsecondValue != null && subsecondValue.length() > 0){
                    float width = fourthPaint.measureText(subsecondValue);
                    if(width > subItemTouchArea * 2){
                        int subIndex = fourthPaint.breakText(subsecondValue,0,subsecondValue.length(),true,subItemTouchArea * 2,null);
                        subsecondValue = subsecondValue.substring(0,subIndex - 3) + "...";
                    }
                }
                fourthPaint.getTextBounds(subsecondValue,0,subsecondValue.length(),secondRect);
                canvas.drawText(subsecondValue,mainPoints[i * 2] - ((secondRect.right - secondRect.left)/2),subCenterToFirstLine + (secondRect.bottom - secondRect.top)+5,fourthPaint);
                subSelectedItemTouchAreaY = subCenterToFirstLine + (secondRect.bottom - secondRect.top)+5;

                Path path4 = new Path();
                path4.moveTo(mainPoints[i * 2] - subItemTouchArea,mainPoints[i * 2 + 1] - subItemTouchArea);
                path4.lineTo(mainPoints[i * 2] + subItemTouchArea,mainPoints[i * 2 + 1] - subItemTouchArea);
                path4.lineTo(mainPoints[i * 2] + subItemTouchArea,subSelectedItemTouchAreaY);
                path4.lineTo(mainPoints[i * 2] - subItemTouchArea,subSelectedItemTouchAreaY);
                path4.lineTo(mainPoints[i * 2] - subItemTouchArea,mainPoints[i * 2 + 1] - subItemTouchArea);
                Paint pap = new Paint();
                pap.setColor(Color.RED);
                pap.setStrokeWidth(5);
                pap.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path4,pap);


                secondPaint.setColor(mContext.getResources().getColor(R.color.light_blue));
                secondPaint.setStyle(Paint.Style.STROKE);
                secondPaint.setStrokeWidth(10);
                canvas.drawCircle(mainPoints[i * 2],mainPoints[i * 2 + 1],subItemSelectedRadius,secondPaint);
                secondPaint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
                secondPaint.setStrokeWidth(30);
                secondPaint.setStyle(Paint.Style.FILL);
                if(subCount != 0) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(mContext.getResources().getColor(R.color.light_blue));
                    paint.setStrokeWidth(5);
                    canvas.drawCircle(mainPoints[i * 2], mainPoints[i * 2 + 1], subCircleRadius, paint);
                    float[] subPoints = generatePoints((int) mainPoints[i * 2], (int) mainPoints[i * 2 + 1], subCircleRadius, subCount);
                    paint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
                    paint.setStyle(Paint.Style.FILL);
                    for(int j = 0; j < subCount; j++){
                        canvas.drawCircle(subPoints[j * 2],subPoints[j * 2 + 1],subItemRadius,paint);

                        Path path2 = new Path();
                        path2.moveTo(subPoints[j * 2] - subItemTouchArea,subPoints[j * 2 + 1] - subItemTouchArea);
                        path2.lineTo(subPoints[j * 2] + subItemTouchArea,subPoints[j * 2 + 1] - subItemTouchArea);
                        path2.lineTo(subPoints[j * 2] + subItemTouchArea,subPoints[j * 2 + 1] + subItemTouchArea);
                        path2.lineTo(subPoints[j * 2] - subItemTouchArea,subPoints[j * 2 + 1] + subItemTouchArea);
                        path2.lineTo(subPoints[j * 2] - subItemTouchArea,subPoints[j * 2 + 1] - subItemTouchArea);
                        Paint pa1 = new Paint();
                        pa1.setColor(Color.RED);
                        pa1.setStrokeWidth(5);
                        pa1.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path2,pa1);

                        fifthPaint.setColor(mContext.getResources().getColor(R.color.STATIC_COLOR));
                        fifthPaint.setStrokeWidth(8);
                        fifthPaint.setStyle(Paint.Style.FILL);
                        Rect thirdRect = new Rect();
                        String temp = "Pool";
                        fifthPaint.setTextSize(35);
                        if(temp != null && temp.length() > 0){
                            float textWidth = fifthPaint.measureText(temp);
                            if(textWidth > subItemTouchArea * 2){
                                int subIndex = fifthPaint.breakText(temp,0,temp.length(),true,subItemTouchArea * 2,null);
                                temp = temp.substring(0,subIndex - 3) + "...";
                            }
                        }
                        fifthPaint.getTextBounds(temp,0,temp.length(),thirdRect);
                        canvas.drawText(temp,subPoints[j * 2] - ((thirdRect.right - thirdRect.left)/2),subPoints[j * 2 + 1] + subItemRadius + (thirdRect.bottom - thirdRect.top) + 10,fifthPaint);
                    }
                }
            }
        }
    }

    private DisplayMetrics getDisplayMetrics(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * Remeasure view width, use screen width minus 50 as default
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec){
        int defaultWidth = getDisplayMetrics().widthPixels - 50;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode){
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                defaultWidth = specSize;
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
        }
        return defaultWidth;
    }

    /**
     * Remeasure view height to construct a square view(width equals height)
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec){
        int defaultHeight = Math.min(getDisplayMetrics().heightPixels - 50, getDisplayMetrics().widthPixels - 50);
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode){
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                defaultHeight = specSize;
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
        }
        return defaultHeight;
    }

    /**
     * Generate specific point locations due to point count
     * @param x Circle center X
     * @param y Circle center Y
     * @param radius    Circle radius
     * @param count Number of points
     * @return
     */
    private float[] generatePoints(int x, int y, int radius,int count){
        float[] points = new float[16];
        for(int j = 0; j < count; j++){
            double locationX = radius * (Math.cos(Math.PI * 2 / count * j - Math.PI / 2)) + x;
            double locationY = radius * (Math.sin(Math.PI * 2 / count * j - Math.PI / 2)) + y;
            points[j * 2] = (float)locationX;
            points[j * 2 + 1] = (float) locationY;
        }
        return points;
    }

    private int dp2px(int dp){
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int px = (int)(dp * metrics.density);
        return px;
    }

    private int px2dp(int px){
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int dp = (int)((float) px / metrics.density);
        return dp;
    }
}
