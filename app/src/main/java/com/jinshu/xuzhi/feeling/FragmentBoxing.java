package com.jinshu.xuzhi.feeling;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.jinshu.xuzhi.feeling.MainActivityFragment.getTempUri;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBoxing extends Fragment {

    View mRootView;
    private static  ImageView targetImageView,targetImageView1,dialog,punch;
    private static  BitmapFactory.Options opt;
    private static int mScreenWidth,mScreenHeight;
    private static float targetCenterX,targetCenterY,mTouchX,mTouchY,mPunchX,mPunchY;
    private static int TotalClickTime = 0;
    private final String LOG_TAG = this.getClass().getSimpleName();
    /************3d rotate animation************/
    ImageView mImageView1 = null;
    ImageView mImageView2 = null;
    ImageView mStartAnimView = null;
    View mContainer = null;
    int mDuration = 200;
    float mCenterX = 0.0f;
    float mCenterY = 0.0f;
    float mDepthZ = 300.0f;
    int mIndex = 0;
    String CONSTANTS_RES_PREFIX = "android.resource://com.jinshu.xuzhi.feeling/";
    final MediaPlayer mpBall  = new MediaPlayer();

    final MediaPlayer mpWater = new MediaPlayer();
    private static RelativeLayout mBoxingLayout;
    /****************************/
    public FragmentBoxing() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TotalClickTime= 0;
        mRootView = inflater.inflate(R.layout.fragment_boxing, container, false);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mBoxingLayout = (RelativeLayout)mRootView.findViewById(R.id.colorBallLayout);
        dialog = (ImageView)mRootView.findViewById(R.id.dialog);
        punch = (ImageView)mRootView.findViewById(R.id.punch);

        punch.setOnClickListener(new PunchClickListener());
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        //generate target image
        Bitmap target = combineImages(R.drawable.mouse5,R.drawable.angrypig2);
        targetImageView = (ImageView)mRootView.findViewById(R.id.backGroundImage);
        targetImageView.setImageBitmap(target);
        targetImageView1 = (ImageView)mRootView.findViewById(R.id.backGroundImage1);
        targetImageView1.setImageBitmap(target);

        opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.mouse5, opt);

        /*tumbler for target1*/
        targetImageView.setPivotX(opt.outWidth );//outWidth * 2 = scaleX
        targetImageView.setPivotY(opt.outHeight*2);//outHeight * 2 = scaleY
        final ObjectAnimator imageViewObjectAnimator1 = ObjectAnimator.ofFloat(targetImageView ,
                "rotation", -45f, 45f);
        imageViewObjectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator1.setDuration(1000);

        final ObjectAnimator imageViewObjectAnimator2 = ObjectAnimator.ofFloat(targetImageView ,
                "rotation", 45f, -45f);
        imageViewObjectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator2.setDuration(1000);

        final  AnimatorSet set = new AnimatorSet();
        set.playSequentially(imageViewObjectAnimator1,imageViewObjectAnimator2);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                  set.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
        /*tumbler for target1 finished*/
        /*tumbler for target2*/
        targetImageView1.setPivotX(opt.outWidth );//?
        targetImageView1.setPivotY(opt.outHeight*2);//?
        final ObjectAnimator imageViewObjectAnimator3 = ObjectAnimator.ofFloat(targetImageView1 ,
                "rotation", -45f, 45f);
        imageViewObjectAnimator3.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator3.setDuration(1000);

        final ObjectAnimator imageViewObjectAnimator4 = ObjectAnimator.ofFloat(targetImageView1 ,
                "rotation", 45f, -45f);
        imageViewObjectAnimator4.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator4.setDuration(1000);

        final  AnimatorSet set1 = new AnimatorSet();
        set1.playSequentially(imageViewObjectAnimator3,imageViewObjectAnimator4);
        set1.setInterpolator(new AccelerateDecelerateInterpolator());
        set1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                set1.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set1.start();
        /*tumbler for dialog*/
        dialog.setPivotX(opt.outWidth );//?
        dialog.setPivotY(opt.outHeight*3);//?
        final ObjectAnimator imageViewObjectAnimator5 = ObjectAnimator.ofFloat(dialog ,
                "rotation", -45f, 45f);
        imageViewObjectAnimator5.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator5.setDuration(1000);

        final ObjectAnimator imageViewObjectAnimator6 = ObjectAnimator.ofFloat(dialog ,
                "rotation", 45f, -45f);
        imageViewObjectAnimator6.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator6.setDuration(1000);

        final  AnimatorSet set2 = new AnimatorSet();
        set2.playSequentially(imageViewObjectAnimator5,imageViewObjectAnimator6);
        set2.setInterpolator(new AccelerateDecelerateInterpolator());
        set2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                set2.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        //set2.start();

        /*tumbler animation finished*/
        /*3d rotate animation*/
        mCenterX = opt.outWidth/2;
        mCenterY = opt.outHeight * 2;
        mImageView1 = targetImageView;
        mImageView2 = targetImageView1;
        mContainer = mRootView.findViewById(R.id.colorBallLayout);

        mStartAnimView = mImageView1;
        targetImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    mTouchX = event.getX();
                    mTouchY = event.getY();
                }
                return false;
            }
        });
        mpBall.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mpWater.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        return mRootView;
    }

    public Bitmap combineImages(int backgroundId,int  foregroundId)
    {
        Bitmap mergeImage = null,imageBackground = null,imageForeground = null;
        try {
            BitmapDrawable drawableBackground = (BitmapDrawable)getActivity().getResources().getDrawable(backgroundId);
            BitmapDrawable drawableForeground = (BitmapDrawable)getActivity().getResources().getDrawable(foregroundId);
            imageBackground = drawableBackground.getBitmap();
            imageForeground = drawableForeground.getBitmap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getTempUri() != null)
        {

            Bitmap bitmap = null;
            try {
                // 先通过getContentResolver方法获得一个ContentResolver实例，
                // 调用openInputStream(Uri)方法获得uri关联的数据流stream
                // 把上一步获得的数据流解析成为bitmap
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(getTempUri()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            // 设置缩放过的图片为forground图片
            imageForeground = MainActivityFragment.zoomImg(bitmap,180,180);
        }

        mergeImage = Bitmap.createBitmap(imageBackground.getWidth(), imageBackground.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(mergeImage);

        comboImage.drawBitmap(imageBackground, 0f, 0f, null);
        comboImage.drawBitmap(imageForeground, (imageBackground.getWidth()/2 - imageForeground.getWidth()/2), (imageBackground.getHeight() - imageForeground.getHeight() - 40), null);

        return mergeImage;
    }
    private void applyRotation(View animView, float startAngle, float toAngle)
    {
        float centerX = mCenterX;
        float centerY = mCenterY;
        Rotate3dAnimation rotation = new Rotate3dAnimation(
                startAngle, toAngle, centerX, centerY, mDepthZ, true);
        rotation.setDuration(mDuration);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView());
        animView.startAnimation(rotation);
    }
    /**
     * This class listens for the end of the first half of the animation.
     * It then posts a new action that effectively swaps the views when the container
     * is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        }
        public void onAnimationEnd(Animation animation) {
            mContainer.post(new SwapViews());
            //mContainer.post(new SwapViews1());
        }
        public void onAnimationRepeat(Animation animation) {
        }
    }
    private final class SwapViews implements Runnable
    {
        @Override
        public void run()
        {
            mImageView1.setVisibility(View.GONE);
            mImageView2.setVisibility(View.GONE);
            mIndex++;
            if (0 == mIndex % 2)
            {
                mStartAnimView = mImageView1;
            }
            else
            {
                mStartAnimView = mImageView2;
            }
            mStartAnimView.setVisibility(View.VISIBLE);
            mStartAnimView.requestFocus();

            Rotate3dAnimation rotation = new Rotate3dAnimation(
                    -90,
                    0,
                    mCenterX,
                    mCenterY, mDepthZ, false);
            /*Rotate3dAnimation rotation = new Rotate3dAnimation(
                    -30,
                    0,
                    mCenterX,
                    mCenterY, mDepthZ, false);*/
            rotation.setDuration(mDuration);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mStartAnimView.startAnimation(rotation);
            /*play reverse audio*/
            try {
                mpWater.reset();
                int id = getActivity().getResources().getIdentifier("reverse2", "raw", "com.jinshu.xuzhi.feeling");
                String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                Log.v(LOG_TAG, uriString);
                mpWater.setDataSource(getActivity(), Uri.parse(uriString));
                mpWater.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class PunchClickListener implements View.OnClickListener{
        @Override
        public void onClick(final View view) {

            TotalClickTime++;
            mPunchX = view.getX();
            mPunchY = view.getY();
            int xScale = mStartAnimView.getWidth();
            int yScale = mStartAnimView.getHeight();
            Log.v(LOG_TAG,"xScale = " + xScale + ",yScale = " + yScale);
            Log.v(LOG_TAG,"target x = " + mStartAnimView.getX() + ",target y = " + mStartAnimView.getY());
            /*store ball coordinates*/
            //mColorBallCoordinatesList.add(new FragmentColorBall.ColorBallCoordinates(view.getX(),view.getY()));

            float targetX = (float)(mStartAnimView.getX()+ Math.random() * opt.outWidth);
            //float targetX = (float)(Math.random() * mScreenWidth );
            float targetY = (float)(mStartAnimView.getY()+ Math.random() * opt.outHeight);
            Log.v(LOG_TAG,"targetX = " + targetX + ",targetY = " + targetY);
            ObjectAnimator clickBally = ObjectAnimator.ofFloat(view,"Y",targetY).setDuration(500);
            ObjectAnimator clickBallx = ObjectAnimator.ofFloat(view,"X", targetX).setDuration(500);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(clickBally, clickBallx);
            set.setInterpolator(new AccelerateInterpolator());

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    //play ball audio
                    try {
                        mpBall.reset();
                        int id = getActivity().getResources().getIdentifier("punch2", "raw", "com.jinshu.xuzhi.feeling");
                        String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                        Log.v(LOG_TAG,uriString);
                        mpBall.setDataSource(getActivity(), Uri.parse(uriString));
                        mpBall.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    /* delete the punch view */
                    mBoxingLayout.removeView(view);
                    /*if touch the target, start the animation,else play the failue audio*/

                   /*play tumbler animation*/
                    applyRotation(mStartAnimView, 0, 90);






                    /*ask the question after click 10 times */
                    if (TotalClickTime % 10 == 0) {
                        Log.v(LOG_TAG, "show question view");
                        dialog.setAlpha(1.0f);
                    }

                    /*add a new punch view*/
                    ImageView punch = new ImageView(getContext());
                    punch.setImageResource(R.drawable.punch1);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    punch.setOnClickListener(new FragmentBoxing.PunchClickListener());
                    punch.setTag(R.drawable.punch1);
                    punch.setAlpha(0.0f);
                    params.leftMargin = (int)mPunchX;
                    params.topMargin  = (int)mPunchY;
                    mBoxingLayout.addView(punch,params);
                    punch.animate().alpha(1.0f).setDuration(500).start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            //disappear dialog view
            if (dialog.getAlpha() == 1.0f)
            {
                dialog.setAlpha(0.0f);
            }


            set.start();
        }

    }
    @Override
    public void onDestroy() {
        mpBall.release();
        mpWater.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }
    public Boolean touchTheTarget(View view)
    {
        Log.v(LOG_TAG,"mStartAnimView x = " + mStartAnimView.getX() + ",mStartAnimView width = " + opt.outWidth);
        if ((view.getX() >= mStartAnimView.getX()) && (view.getX() <= (mStartAnimView.getX() + opt.outWidth)))
            return true;
        else
            return false;
    }
}
