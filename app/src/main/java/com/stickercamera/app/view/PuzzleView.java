package com.stickercamera.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stickercamera.app.entity.Coordinates;
import com.stickercamera.app.entity.ImageBean;
import com.stickercamera.app.entity.ImageItem;
import com.stickercamera.base.util.AppUtil;
import com.stickercamera.base.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼图重要部分
 */
public class PuzzleView extends View {

    private Context context;
    private Path[] path;
    private Bitmap[] bitmaps;
    private boolean[] bitmapsFlag;
    private float[][] pathLT;
    private float[][] pathOffset;
    private int pathNum;
    private int viewWdh, viewHgt;
    private int leftMargin;
    private List<ImageBean> pics;
    private final static int MARGIN_HEIGHT = 100;
    private List<ImageItem> coordinateSetList;


    public PuzzleView(Context context) {
        super(context);
        this.context = context;
    }

    public PuzzleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initPath();
    }

    public PuzzleView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        initPath();
    }

    public void setPathCoordinate(List<ImageItem> pathCoordinate) {
        this.coordinateSetList = pathCoordinate;
        initPath();
    }

    public void setPics(List<ImageBean> imageBeans) {

        leftMargin = (AppUtil.getScreenWidth(context) - dp2px(320)) / 2;
        viewWdh = dp2px(320);
        viewHgt = dp2px(450);
        pics = new ArrayList<>();
        if (imageBeans != null) {
            pics.addAll(imageBeans);
        }
        pathNum = pics.size();
    }

    private void initPath() {
        path = new Path[pathNum];
        for (int i = 0; i < pathNum; i++) {
            path[i] = new Path();
        }
        bitmapsFlag = new boolean[pathNum];

        pathLT = new float[pathNum][2];
        pathOffset = new float[pathNum][2];
        for (int i = 0; i < pathNum; i++) {
            bitmapsFlag[i] = false;
            pathLT[i][0] = 0f;
            pathLT[i][1] = 0f;
            pathOffset[i][0] = 0f;
            pathOffset[i][1] = 0f;
        }

        for (int i = 0; i < pathNum; i++) {
            for (int j = 0; j < coordinateSetList.get(i).getCoordinates().size(); j++) {
                float x = coordinateSetList.get(i).getCoordinates().get(j).getX();
                float y = coordinateSetList.get(i).getCoordinates().get(j).getY();
                if (j == 0) {
                    path[i].moveTo(dp2px(x), dp2px(y));
                } else {
                    path[i].lineTo(dp2px(x), dp2px(y));
                }
            }
            path[i].close();
        }

        // get bitmap
        bitmaps = new Bitmap[pathNum];
        for (int i = 0; i < pathNum; i++) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pics.get(i).path, opt);

            int bmpWdh = opt.outWidth;
            int bmpHgt = opt.outHeight;

            Coordinates coordinate = caculateViewSize(coordinateSetList.get(i).getCoordinates());
            int size = caculateSampleSize(bmpWdh, bmpHgt, dp2px(coordinate.getX()), dp2px(coordinate.getY()));
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = size;

            bitmaps[i] = scaleImage(BitmapFactory.decodeFile(pics.get(i).path, opt), dp2px(coordinate.getX()), dp2px(coordinate.getY()));
        }
    }

    private Coordinates caculateViewSize(List<Coordinates> list) {

        float viewWidth;
        float viewHeight;

        viewWidth = caculateMaxCoordinateX(list) - caculateMinCoordinateX(list);
        viewHeight = caculateMaxCoordinateY(list) - caculateMinCoordinateY(list);

        return new Coordinates(viewWidth, viewHeight);
    }


    private int caculateSampleSize(int picWdh, int picHgt, int showWdh,
                                   int showHgt) {
        // 如果此时显示区域比图片大，直接返回
        if ((showWdh < picWdh) && (showHgt < picHgt)) {
            int wdhSample = picWdh / showWdh;
            int hgtSample = picHgt / showHgt;
            // 利用小的来处理
            int sample = wdhSample > hgtSample ? hgtSample : wdhSample;
            int minSample = 2;
            while (sample > minSample) {
                minSample *= 2;
            }
            return minSample >> 1;
        } else {
            return 0;
        }
    }

    private float caculateMinCoordinateX(List<Coordinates> list) {

        float minX;
        minX = list.get(0).getX();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getX() < minX) {
                minX = list.get(i).getX();
            }
        }
        return minX;
    }

    private float caculateMaxCoordinateX(List<Coordinates> list) {

        float maxX;
        maxX = list.get(0).getX();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getX() > maxX) {
                maxX = list.get(i).getX();
            }
        }
        return maxX;
    }

    private float caculateMinCoordinateY(List<Coordinates> list) {

        float minY;
        minY = list.get(0).getY();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getY() < minY) {
                minY = list.get(i).getY();
            }
        }
        return minY;
    }

    private float caculateMaxCoordinateY(List<Coordinates> list) {

        float maxY;
        maxY = list.get(0).getY();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getY() > maxY) {
                maxY = list.get(i).getY();
            }
        }
        return maxY;
    }

    //图片缩放
    private static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = 1;
        if (scaleWidth >= scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWdh, viewHgt);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);// 显示背景颜色
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
//        canvas.drawPaint(paint);
        // draw1(canvas);
        startDraw(canvas, paint);
    }


    private void startDraw(Canvas canvas, Paint paint) {
        for (int i = 0; i < pathNum; i++) {
            canvas.save();
            drawScene(canvas, paint, i);
            canvas.restore();
        }
    }

    private void drawScene(Canvas canvas, Paint paint, int idx) {
        canvas.clipPath(path[idx]);
        canvas.drawColor(Color.GRAY);
        if (bitmapsFlag[idx]) {
            canvas.drawBitmap(bitmaps[idx], dp2px(caculateMinCoordinateX(coordinateSetList.get(idx).getCoordinates())) + pathOffsetX + pathOffset[idx][0],
                    dp2px(caculateMinCoordinateY(coordinateSetList.get(idx).getCoordinates())) + pathOffsetY + pathOffset[idx][1], paint);
        } else {
            canvas.drawBitmap(bitmaps[idx], dp2px(caculateMinCoordinateX(coordinateSetList.get(idx).getCoordinates())) + pathOffset[idx][0],
                    dp2px(caculateMinCoordinateY(coordinateSetList.get(idx).getCoordinates())) + pathOffset[idx][1], paint);
        }
    }

    private int dp2px(float point) {
        return DensityUtil.dip2px(getContext(), point);
    }

    float ptx, pty;
    float pathOffsetX, pathOffsetY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < pathNum; i++) {
                    bitmapsFlag[i] = false;
                }
                ptx = event.getRawX() - dp2px(leftMargin);
                pty = event.getRawY() - dp2px(MARGIN_HEIGHT);
                pathOffsetX = 0;
                pathOffsetY = 0;
                int cflag = 0;
                for (cflag = 0; cflag < pathNum; cflag++) {
                    if (contains(path[cflag], ptx, pty)) {
                        bitmapsFlag[cflag] = true;
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                pathOffsetX = event.getRawX() - dp2px(leftMargin) - ptx;
                pathOffsetY = event.getRawY() - dp2px(MARGIN_HEIGHT) - pty;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < pathNum; i++) {
                    if (bitmapsFlag[i]) {
                        pathOffset[i][0] += event.getRawX() - dp2px(leftMargin) - ptx;
                        pathOffset[i][1] += event.getRawY() - dp2px(MARGIN_HEIGHT) - pty;

                        if (pathOffset[i][0] > 0) {
                            pathOffset[i][0] = 0;
                        }
                        if (pathOffset[i][0] < -(bitmaps[i].getWidth() - getViewWidth(coordinateSetList.get(i).getCoordinates()))) {
                            pathOffset[i][0] = -(bitmaps[i].getWidth() - getViewWidth(coordinateSetList.get(i).getCoordinates()));
                        }
                        if (pathOffset[i][1] > 0) {
                            pathOffset[i][1] = 0;
                        }
                        if (pathOffset[i][1] < -(bitmaps[i].getHeight() - getViewHeight(coordinateSetList.get(i).getCoordinates()))) {
                            pathOffset[i][1] = -(bitmaps[i].getHeight() - getViewHeight(coordinateSetList.get(i).getCoordinates()));
                        }
                        bitmapsFlag[i] = false;
                        break;
                    }
                }
                invalidate();
                break;
            default:
                break;
        }

        return true;
    }

    private boolean contains(Path parapath, float pointx, float pointy) {
        RectF localRectF = new RectF();
        parapath.computeBounds(localRectF, true);
        Region localRegion = new Region();
        localRegion.setPath(parapath, new Region((int) localRectF.left,
                (int) localRectF.top, (int) localRectF.right,
                (int) localRectF.bottom));
        return localRegion.contains((int) pointx, (int) pointy);
    }

    private float getViewWidth(List<Coordinates> list) {

        return dp2px(caculateMaxCoordinateX(list) - caculateMinCoordinateX(list));
    }

    private float getViewHeight(List<Coordinates> list) {

        return dp2px(caculateMaxCoordinateY(list) - caculateMinCoordinateY(list));
    }
}
