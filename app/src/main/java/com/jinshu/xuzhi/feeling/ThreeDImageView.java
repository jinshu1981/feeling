package com.jinshu.xuzhi.feeling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by xuzhi on 2017/3/3.
 */

public class ThreeDImageView extends ImageView {
    //摄像机
    private Camera mCamera;
    private final String LOG_TAG = this.getClass().getSimpleName();
    //翻转用的图片
    private Bitmap face;
    private android.graphics.Matrix mMatrix = new android.graphics.Matrix();
    private Paint mPaint = new Paint();

    private int mLastMotionX, mLastMotionY;

    //图片的中心点坐标
    private int centerX, centerY;
    //转动的总距离，跟度数比例1:1
    private int deltaX, deltaY;
    //图片宽度高度
    private int bWidth, bHeight;
    //当前点击位置
    public  int currentX,currentY;
    public ThreeDImageView(Context context){
        super(context);
        setWillNotDraw(false);

        mCamera = new Camera();
        mPaint = new Paint();
        mMatrix = new android.graphics.Matrix();
        mPaint.setAntiAlias(true);
        face = BitmapFactory.decodeResource(getResources(), R.drawable.mouse000);
        bWidth = face.getWidth();
        bHeight = face.getHeight();
        centerX = bWidth>>1;
        centerY = bHeight>>1;
    }

    public ThreeDImageView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setWillNotDraw(false);

        mCamera = new Camera();
        mPaint = new Paint();
        mMatrix = new android.graphics.Matrix();
        mPaint.setAntiAlias(true);
        face = BitmapFactory.decodeResource(getResources(), R.drawable.mouse000);
        bWidth = face.getWidth();
        bHeight = face.getHeight();
        centerX = bWidth>>1;
        centerY = bHeight>>1;
    }

    public ThreeDImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        setWillNotDraw(false);

        mCamera = new Camera();
        mPaint = new Paint();
        mMatrix = new android.graphics.Matrix();
        mPaint.setAntiAlias(true);
        face = BitmapFactory.decodeResource(getResources(), R.drawable.mouse000);
        bWidth = face.getWidth();
        bHeight = face.getHeight();
        centerX = bWidth>>1;
        centerY = bHeight>>1;
    }

    public ThreeDImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr);;
    }
    void rotate( int degreeX, int degreeY) {
        //deltaX += degreeX;
        //deltaY += degreeY;

        mCamera.save();
        mCamera.rotateY(degreeX);
        mCamera.rotateX(-degreeY);
        mCamera.translate(0, 0, -centerX);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        //以图片的中心点为旋转中心,如果不加这两句，就是以（0,0）点为旋转中心
        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postTranslate(centerX, centerY);
        mCamera.save();

        postInvalidate();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = (int) event.getX();
        currentY = (int) event.getY();
        Log.v(LOG_TAG,"currentX = " + currentX + ",currentY = " + currentY);

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.v(LOG_TAG,"ACTION_DOWN");
                //mLastMotionX = x;
                //mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //int dx = x - mLastMotionX;
                //int dy = y - mLastMotionY;
                //rotate(dx, dy);
                //mLastMotionX = x;
                //mLastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
                float deltaX = 0,deltaY = 0;
                //deltaX = mScreenWidth / 2 - currentX;
                Log.v(LOG_TAG,"deltaX = " + deltaX);
                rotate((int)deltaX,0);
                break;
        }
        return true;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawBitmap(face, mMatrix, mPaint);
    }

}