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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.mattyang.demos.R;

import java.util.ArrayList;
import java.util.HashMap;


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
    int mainCount = 0;
    private int mainSelectedItemTouchAreaY = 0;
    private int subSelectedItemTouchAreaY = 0;
    Context mContext;
    Bitmap bitmap;
    Paint mainCirclePaint;
    Paint mainItemPaint;
    Paint mainItemSelectedPaint;
    Paint mainItemTextPaint;
    Paint mainItemSelectedTextPaint;
    Paint subItemPaint;
    Paint subItemTextPaint;
    Paint subItemSelectedTextPaint;
    Paint subItemSelectedCirclePaint;
    Paint subCirclePaint;
    Paint subCircleSubItemPaint;
    Paint subCircleSubItemTextPaint;
    ArrayList<NetworkItem> mainList = new ArrayList<>();
    ArrayList<NetworkItem> subList = new ArrayList<>();
    float[] mainPoints;
    NetworkItem centerItem = new NetworkItem();
    private OnClickListener mOnClickListener;
    HashMap<NetworkItem,ArrayList<NetworkItem>> map = new HashMap<>();
    HashMap<NetworkItem,float[]> thirdLayerPoints = new HashMap<>();


    public NetworkCircleMapView(Context context) {
        super(context);
        this.mContext = context;
        initData();
        initPaints();
    }

    public NetworkCircleMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
         initData();
         initPaints();
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (x >= centerX - mainItemTouchArea && x <= centerX + mainItemTouchArea) {
                    if (y >= centerY - mainItemTouchArea && y <= mainSelectedItemTouchAreaY) {
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(centerItem);
                        }
                    }
                }
                for (int i = 0; i < mainList.size();i++){
                        if(x >= mainPoints[i * 2] - subItemTouchArea && x <= mainPoints[i * 2] + subItemTouchArea){
                            if(y >= mainPoints[i * 2 + 1] - subItemTouchArea && y <= mainPoints[i * 2 + 1] + subItemTouchArea){
                                if(mOnClickListener != null){
                                    mOnClickListener.onClick(mainList.get(i));
                                }
                            }
                        }
                }
                for(NetworkItem item : mainList){
                    if(thirdLayerPoints.get(item)!= null && thirdLayerPoints.get(item).length > 0){
                        for(int m = 0; m < map.get(item).size(); m++){
                            if(x >= thirdLayerPoints.get(item)[m * 2] - subItemTouchArea && x <= thirdLayerPoints.get(item)[m * 2] + subItemTouchArea){
                                if(y >= thirdLayerPoints.get(item)[m * 2 + 1] -subItemTouchArea && y <= thirdLayerPoints.get(item)[m * 2 + 1] + subItemTouchArea){
                                    if(mOnClickListener != null){
                                        mOnClickListener.onClick(map.get(item).get(m));
                                    }
                                }
                            }
                        }
                    }
                }
           return true;
        }
        return super.onTouchEvent(event);
    }


    private void initData(){
        mainCircleRadius = dp2px(110);
        mainItemRadius = dp2px(23);
        mainItemSelectedRadius = dp2px(27);
        mainItemTouchArea = dp2px(35);
        subCircleRadius = dp2px(40);
        subItemRadius = dp2px(5);
        subItemSelectedRadius = dp2px(13);
        subItemTouchArea = dp2px(15);
    }

    /**
     * DataSource of WifiSON list as ArrayList, implement parsing data layers.Currently we just need three layers
     * include master
     * @param dataSource
     */
    public void setDataList(ArrayList<NetworkItem> dataSource){
        for(NetworkItem item : dataSource){
            if(TextUtils.isEmpty(item.getUplinkMac())){
                centerItem = item;
            }else{
                if(item.getUplinkMac().equals(centerItem.mac)){
                    mainList.add(item);
                }else{
                    for(NetworkItem value : mainList) {
                        if (item.getUplinkMac().equals(value.mac)){
                            if(map.get(value) == null){
                                ArrayList<NetworkItem> list = new ArrayList<>();
                                list.add(item);
                                map.put(value,list);
                            }else {
                                map.get(value).add(item);
                            }
                        }
                    }
                }
            }
        }
        int max = 0;
        int subscript = -1;
        for(int i = 0; i < mainList.size(); i ++){
            for(int j = i + 1; j < mainList.size(); j++) {
                if (map.get(mainList.get(i)) != null && map.get(mainList.get(j)) != null) {
                    if (max < map.get(mainList.get(i)).size() || max < map.get(mainList.get(j)).size()) {
                        if (map.get(mainList.get(i)).size() >= map.get(mainList.get(j)).size()) {
                            max = map.get(mainList.get(i)).size();
                            subscript = i;
                        } else {
                            max = map.get(mainList.get(j)).size();
                            subscript = j;
                        }
                    }
                }else{
                    if(map.get(mainList.get(i)) != null){
                        if(max < map.get(mainList.get(i)).size()){
                            max = map.get(mainList.get(i)).size();
                            subscript = i;
                        }
                    }
                    if(map.get(mainList.get(j)) != null){
                        if(max < map.get(mainList.get(j)).size()){
                            max = map.get(mainList.get(j)).size();
                            subscript = j;
                        }
                    }
                }
            }
        }
        NetworkItem item = mainList.get(subscript);
        mainList.remove(item);
        mainList.add(0,item);
        mainCount = mainList.size();
        //Remove exceed item, make sure Max item count is 9
        int subCount = maxPointsCount - 1 - mainCount;
        int total = 0;
        for(int i = 0; i < mainList.size(); i ++){
            if(map.get(mainList.get(i)) != null) {
                total += map.get(mainList.get(i)).size();
            }
        }
        if(total - subCount > 0){
            subCount = total - subCount;
        }else{
            subCount = 0;
        }
        while (subCount != 0) {
            for(int i = mainList.size() - 1; i >= 0; i--) {
                    if(map.get(mainList.get(i)) != null) {
                        if (map.get(mainList.get(i)).size() >= subCount) {
                            while (subCount != 0) {
                                map.get(mainList.get(i)).remove(map.get(mainList.get(i)).size() - subCount);
                                subCount--;
                            }
                        } else {
                            subCount = subCount - map.get(mainList.get(i)).size();
                            map.remove(mainList.get(i));
                        }
                    }
                }
        }
    }

    public void setCenterItemBitmap(int resourceId){
        bitmap = BitmapFactory.decodeResource(getResources(),resourceId);
    }

    public void setCenterItemBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    private void initPaints(){
        mainCirclePaint = new Paint();
        mainCirclePaint.setColor(mContext.getResources().getColor(R.color.light_blue));
        mainCirclePaint.setStrokeWidth(5);
        mainCirclePaint.setStyle(Paint.Style.STROKE);
        mainCirclePaint.setAntiAlias(true);

        mainItemPaint = new Paint();
        mainItemPaint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
        mainItemPaint.setStyle(Paint.Style.FILL);
        mainItemPaint.setStrokeWidth(5);
        mainItemPaint.setAntiAlias(true);

        mainItemSelectedPaint = new Paint();
        mainItemSelectedPaint.setStyle(Paint.Style.STROKE);
        mainItemSelectedPaint.setColor(mContext.getResources().getColor(R.color.light_blue));
        mainItemSelectedPaint.setStrokeWidth(10);
        mainItemSelectedPaint.setAntiAlias(true);

        mainItemTextPaint = new Paint();
        mainItemTextPaint.setColor(mContext.getResources().getColor(R.color.STATIC_COLOR));
        mainItemTextPaint.setStrokeWidth(8);
        mainItemTextPaint.setStyle(Paint.Style.FILL);
        mainItemTextPaint.setTextSize(35);
        mainItemTextPaint.setAntiAlias(true);

        mainItemSelectedTextPaint = new Paint();
        mainItemSelectedTextPaint.setColor(mContext.getResources().getColor(R.color.light_blue));
        mainItemSelectedTextPaint.setStrokeWidth(8);
        mainItemSelectedTextPaint.setStyle(Paint.Style.FILL);
        mainItemSelectedTextPaint.setTextSize(35);
        mainItemSelectedTextPaint.setAntiAlias(true);

        subItemPaint = new Paint();
        subItemPaint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
        subItemPaint.setStrokeWidth(30);
        subItemPaint.setStyle(Paint.Style.FILL);
        subItemPaint.setAntiAlias(true);

        subItemTextPaint = new Paint();
        subItemTextPaint.setColor(mContext.getResources().getColor(R.color.STATIC_COLOR));
        subItemTextPaint.setStrokeWidth(8);
        subItemTextPaint.setStyle(Paint.Style.FILL);
        subItemTextPaint.setTextSize(20);
        subItemTextPaint.setAntiAlias(true);

        subItemSelectedTextPaint = new Paint();
        subItemSelectedTextPaint.setColor(mContext.getResources().getColor(R.color.light_blue));
        subItemSelectedTextPaint.setStrokeWidth(8);
        subItemSelectedTextPaint.setTextSize(20);
        subItemSelectedTextPaint.setStyle(Paint.Style.FILL);
        subItemSelectedTextPaint.setAntiAlias(true);

        subItemSelectedCirclePaint = new Paint();
        subItemSelectedCirclePaint.setColor(mContext.getResources().getColor(R.color.light_blue));
        subItemSelectedCirclePaint.setStyle(Paint.Style.STROKE);
        subItemSelectedCirclePaint.setStrokeWidth(10);
        subItemSelectedCirclePaint.setAntiAlias(true);

        subCirclePaint = new Paint();
        subCirclePaint.setStyle(Paint.Style.STROKE);
        subCirclePaint.setColor(mContext.getResources().getColor(R.color.light_blue));
        subCirclePaint.setStrokeWidth(5);
        subCirclePaint.setAntiAlias(true);

        subCircleSubItemPaint = new Paint();
        subCircleSubItemPaint.setColor(mContext.getResources().getColor(R.color.INPUT_COLOR));
        subCircleSubItemPaint.setStyle(Paint.Style.FILL);
        subCircleSubItemPaint.setStrokeWidth(5);
        subCircleSubItemPaint.setAntiAlias(true);

        subCircleSubItemTextPaint = new Paint();
        subCircleSubItemTextPaint.setColor(mContext.getResources().getColor(R.color.STATIC_COLOR));
        subCircleSubItemTextPaint.setStrokeWidth(8);
        subCircleSubItemTextPaint.setStyle(Paint.Style.FILL);
        subCircleSubItemTextPaint.setTextSize(20);
        subCircleSubItemTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw main circle
        canvas.drawCircle(centerX,centerY,mainCircleRadius,mainCirclePaint);

        //draw main item in the middle
        canvas.drawCircle(centerX,centerY,mainItemRadius,mainItemPaint);
        canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Rect(centerX - bitmap.getWidth() /2,
                                            centerY - bitmap.getHeight() /2,centerX + bitmap.getWidth() /2,centerY + bitmap.getHeight() /2),mainItemPaint);
        //draw main item selected
        canvas.drawCircle(centerX,centerY,mainItemSelectedRadius,mainItemSelectedPaint);

        //draw main item describe info line one
        Rect rect = new Rect();
        if(centerItem.getName() != null) {
            String firstValue = centerItem.getName();
            if (firstValue != null && firstValue.length() > 0) {
                float textWidth = mainItemTextPaint.measureText(firstValue);
                if (textWidth > mainItemTouchArea * 2) {
                    int subIndex = mainItemTextPaint.breakText(firstValue, 0, firstValue.length(), true, mainItemTouchArea * 2, null);
                    firstValue = firstValue.substring(0, subIndex - 3) + "...";
                }
            }
            mainItemTextPaint.getTextBounds(firstValue, 0, firstValue.length(), rect);
            canvas.drawText(firstValue, centerX - ((rect.right - rect.left) / 2), centerY + mainItemSelectedRadius + (rect.bottom - rect.top) + 10, mainItemTextPaint);
        }
        int centerToFirstLine = centerY + mainItemSelectedRadius + (rect.bottom - rect.top) + 20;

        //draw main item describe info line two
        rect = new Rect();
        if(centerItem.getPhoneNickName() != null) {
            String secondValue = centerItem.getPhoneNickName();
            if (secondValue != null && secondValue.length() > 0) {
                float width = mainItemSelectedTextPaint.measureText(secondValue);
                if (width > mainItemTouchArea * 2) {
                    int subIndex = mainItemSelectedTextPaint.breakText(secondValue, 0, secondValue.length(), true, mainItemTouchArea * 2, null);
                    secondValue = secondValue.substring(0, subIndex - 3) + "...";
                }
            }
            mainItemSelectedTextPaint.getTextBounds(secondValue, 0, secondValue.length(), rect);
            canvas.drawText(secondValue, centerX - ((rect.right - rect.left) / 2), centerToFirstLine + (rect.bottom - rect.top) + 5, mainItemSelectedTextPaint);
        }
        mainSelectedItemTouchAreaY = centerToFirstLine + (rect.bottom - rect.top)+5;

//        Path path = new Path();
//        path.moveTo(centerX - mainItemTouchArea,centerY - mainItemTouchArea);
//        path.lineTo(centerX + mainItemTouchArea,centerY - mainItemTouchArea);
//        path.lineTo(centerX + mainItemTouchArea,mainSelectedItemTouchAreaY);
//        path.lineTo(centerX - mainItemTouchArea,mainSelectedItemTouchAreaY);
//        path.lineTo(centerX - mainItemTouchArea,centerY - mainItemTouchArea);
//        Paint pa3 = new Paint();
//        pa3.setColor(Color.RED);
//        pa3.setStrokeWidth(5);
//        pa3.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(path,pa3);

        mainPoints = generatePoints(centerX,centerY,mainCircleRadius,mainCount);
        for(int i = 0; i < mainCount;i++){
            //draw subItem on main circle
            canvas.drawCircle(mainPoints[i * 2],mainPoints[i * 2 + 1],subItemRadius,subItemPaint);

            //draw subItem describe info line one
            Rect subRect = new Rect();
            if(mainList.get(i).getName() != null) {
                String subfirstValue = mainList.get(i).getName();
                if (subfirstValue != null && subfirstValue.length() > 0) {
                    float textWidth = subItemTextPaint.measureText(subfirstValue);
                    if (textWidth > subItemTouchArea * 2) {
                        int subIndex = subItemTextPaint.breakText(subfirstValue, 0, subfirstValue.length(), true, subItemTouchArea * 2, null);
                        subfirstValue = subfirstValue.substring(0, subIndex - 3) + "...";
                    }
                }
                subItemTextPaint.getTextBounds(subfirstValue, 0, subfirstValue.length(), subRect);
                canvas.drawText(subfirstValue, mainPoints[i * 2] - ((subRect.right - subRect.left) / 2), mainPoints[i * 2 + 1] + subItemRadius + (subRect.bottom - subRect.top) + 10, subItemTextPaint);
            }
            int subCenterToFirstLine = (int)(mainPoints[i * 2 + 1] + subItemRadius + (subRect.bottom - subRect.top) + 10);

//                Path path1 = new Path();
//                path1.moveTo(mainPoints[i * 2] - subItemTouchArea, mainPoints[i * 2 + 1] - subItemTouchArea);
//                path1.lineTo(mainPoints[i * 2] + subItemTouchArea, mainPoints[i * 2 + 1] - subItemTouchArea);
//                path1.lineTo(mainPoints[i * 2] + subItemTouchArea, mainPoints[i * 2 + 1] + subItemTouchArea);
//                path1.lineTo(mainPoints[i * 2] - subItemTouchArea, mainPoints[i * 2 + 1] + subItemTouchArea);
//                path1.lineTo(mainPoints[i * 2] - subItemTouchArea, mainPoints[i * 2 + 1] - subItemTouchArea);
//                Paint pa = new Paint();
//                pa.setColor(Color.RED);
//                pa.setStrokeWidth(5);
//                pa.setStyle(Paint.Style.STROKE);
//                canvas.drawPath(path1, pa);


                //draw subItem describe info line two
                Rect secondRect = new Rect();
//                if(mainList.get(i).getPhoneNickName() != null) {
//                    String subsecondValue = mainList.get(i).getPhoneNickName();
//                    if (subsecondValue != null && subsecondValue.length() > 0) {
//                        float width = subItemSelectedTextPaint.measureText(subsecondValue);
//                        if (width > subItemTouchArea * 2) {
//                            int subIndex = subItemSelectedTextPaint.breakText(subsecondValue, 0, subsecondValue.length(), true, subItemTouchArea * 2, null);
//                            subsecondValue = subsecondValue.substring(0, subIndex - 3) + "...";
//                        }
//                    }
//                    subItemSelectedTextPaint.getTextBounds(subsecondValue, 0, subsecondValue.length(), secondRect);
//                    canvas.drawText(subsecondValue, mainPoints[i * 2] - ((secondRect.right - secondRect.left) / 2), subCenterToFirstLine + (secondRect.bottom - secondRect.top) + 5, subItemSelectedTextPaint);
//                }
                subSelectedItemTouchAreaY = subCenterToFirstLine + (secondRect.bottom - secondRect.top)+5;

//                Path path4 = new Path();
//                path4.moveTo(mainPoints[i * 2] - subItemTouchArea,mainPoints[i * 2 + 1] - subItemTouchArea);
//                path4.lineTo(mainPoints[i * 2] + subItemTouchArea,mainPoints[i * 2 + 1] - subItemTouchArea);
//                path4.lineTo(mainPoints[i * 2] + subItemTouchArea,subSelectedItemTouchAreaY);
//                path4.lineTo(mainPoints[i * 2] - subItemTouchArea,subSelectedItemTouchAreaY);
//                path4.lineTo(mainPoints[i * 2] - subItemTouchArea,mainPoints[i * 2 + 1] - subItemTouchArea);
//                Paint pap = new Paint();
//                pap.setColor(Color.RED);
//                pap.setStrokeWidth(5);
//                pap.setStyle(Paint.Style.STROKE);
//                canvas.drawPath(path4,pap);

                //draw subItem is selected circle
//                canvas.drawCircle(mainPoints[i * 2],mainPoints[i * 2 + 1],subItemSelectedRadius,subItemSelectedCirclePaint);

                if(map.get(mainList.get(i)) != null && map.get(mainList.get(i)).size() != 0) {
                    //draw subCircle
                    canvas.drawCircle(mainPoints[i * 2], mainPoints[i * 2 + 1], subCircleRadius, subCirclePaint);
                    float[] subPoints;
                    subPoints = generatePoints((int) mainPoints[i * 2], (int) mainPoints[i * 2 + 1], subCircleRadius, map.get(mainList.get(i)).size());
                    thirdLayerPoints.put(mainList.get(i),subPoints);
                    for(int j = 0; j < map.get(mainList.get(i)).size(); j++){
                        //draw subItem on subCircle
                        canvas.drawCircle(subPoints[j * 2],subPoints[j * 2 + 1],subItemRadius,subCircleSubItemPaint);

//                        Path path2 = new Path();
//                        path2.moveTo(subPoints[j * 2] - subItemTouchArea,subPoints[j * 2 + 1] - subItemTouchArea);
//                        path2.lineTo(subPoints[j * 2] + subItemTouchArea,subPoints[j * 2 + 1] - subItemTouchArea);
//                        path2.lineTo(subPoints[j * 2] + subItemTouchArea,subPoints[j * 2 + 1] + subItemTouchArea);
//                        path2.lineTo(subPoints[j * 2] - subItemTouchArea,subPoints[j * 2 + 1] + subItemTouchArea);
//                        path2.lineTo(subPoints[j * 2] - subItemTouchArea,subPoints[j * 2 + 1] - subItemTouchArea);
//                        Paint pa1 = new Paint();
//                        pa1.setColor(Color.RED);
//                        pa1.setStrokeWidth(5);
//                        pa1.setStyle(Paint.Style.STROKE);
//                        canvas.drawPath(path2,pa1);

                        //draw subCircle subItem describe info
                        Rect thirdRect = new Rect();
                        if(map.get(mainList.get(i)).get(j).getName() != null) {
                            String temp = map.get(mainList.get(i)).get(j).getName();
                            if (temp != null && temp.length() > 0) {
                                float textWidth = subCircleSubItemTextPaint.measureText(temp);
                                if (textWidth > subItemTouchArea * 2) {
                                    int subIndex = subCircleSubItemTextPaint.breakText(temp, 0, temp.length(), true, subItemTouchArea * 2, null);
                                    temp = temp.substring(0, subIndex - 3) + "...";
                                }
                            }
                            subCircleSubItemTextPaint.getTextBounds(temp, 0, temp.length(), thirdRect);
                            canvas.drawText(temp, subPoints[j * 2] - ((thirdRect.right - thirdRect.left) / 2), subPoints[j * 2 + 1] + subItemRadius + (thirdRect.bottom - thirdRect.top) + 10, subCircleSubItemTextPaint);
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

    public interface OnClickListener{
        void onClick(NetworkItem item);
    }

    /**
     * NetworkOnClickListener implement onClick event on NetworkCircleMap
     * @param listener
     */
    public void setNetWorkOnClickListener(OnClickListener listener){
        mOnClickListener = listener;
    }
}
