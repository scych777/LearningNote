package com.mattyang.demos.networkcirclemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;



public class NetworkCircleMapView extends View {
    private static int centerX = 0;
    private static int centerY = 0;
    private static int mainCircleRadius = 340;
    private static int mainItemRadius = 75;
    private static int subCircleRadius = 175;
    private static int subItemRadius = 25;
    private static int count = 8;
    static float[] points = new float[16];
    Context mContext;
    Paint paint;

    private float[] generatePoints(int x, int y, int radius){
        for(int j = 0; j < count; j++){
            double locationX = radius * (Math.cos(Math.PI * 2 / count * j - Math.PI / 2)) + x;
            double locationY = radius * (Math.sin(Math.PI * 2 / count * j - Math.PI / 2)) + y;
            points[j * 2] = (float)locationX;
            points[j * 2 + 1] = (float) locationY;
        }
        return points;
    }





    public NetworkCircleMapView(Context context) {
        super(context);
        this.mContext = context;
    }

    public NetworkCircleMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        Paint secondPaint = new Paint();
        secondPaint.setColor(Color.BLACK);
        secondPaint.setStrokeWidth(30);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX,centerY,mainCircleRadius,paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX,centerY,mainItemRadius,paint);
        float[] mainPoints = generatePoints(centerX,centerY,mainCircleRadius);
        for(int i = 0; i < count;i++){
            canvas.drawPoint(mainPoints[i * 2],mainPoints[i * 2 + 1],secondPaint);
            canvas.save();
            if(i == 0){
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(mainPoints[i * 2],mainPoints[i * 2 + 1],subCircleRadius,paint);
                float[] subPoints = generatePoints((int) mainPoints[i * 2],(int) mainPoints[i * 2 + 1],subCircleRadius);
                canvas.drawPoints(subPoints,secondPaint);
                canvas.save();
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
}
