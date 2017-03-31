package com.jinshu.xuzhi.feeling;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static com.jinshu.xuzhi.feeling.Util.PlayAudio;
import static com.jinshu.xuzhi.feeling.Util.combineImages;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBoxing extends Fragment {

    View mRootView;
    private static  ImageView targetImageView,targetImageView1,/*3d 旋转需要定义两个一样的图片*/dialog,punch,backArrow;
    private static  BitmapFactory.Options opt;
    private static RelativeLayout mBoxingLayout;
    private static float mPunchX,mPunchY/*,mTouchX,mTouchY*/;
    private static int TotalClickTime = 0;
    private final String LOG_TAG = this.getClass().getSimpleName();
    /************3d rotate animation************/
    ImageView mStartAnimView = null;
    View mContainer = null;
    int mDuration = 200;
    float mCenterX = 0.0f;
    float mCenterY = 0.0f;
    float mDepthZ = 300.0f;
    int mIndex = 0;
    final MediaPlayer mpPunch  = new MediaPlayer(),mpTumbler = new MediaPlayer();
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
        mBoxingLayout = (RelativeLayout)mRootView.findViewById(R.id.colorBallLayout);
        mContainer = mRootView.findViewById(R.id.colorBallLayout);
        targetImageView = (ImageView)mRootView.findViewById(R.id.backGroundImage);
        targetImageView1 = (ImageView)mRootView.findViewById(R.id.backGroundImage1);
        dialog = (ImageView)mRootView.findViewById(R.id.dialog);
        punch = (ImageView)mRootView.findViewById(R.id.punch);
        backArrow = (ImageView)mRootView.findViewById(R.id.arrowback);

        //generate target image
        Bitmap target = combineImages(getActivity(),R.drawable.mouse555,R.drawable.example100,180);
        targetImageView.setImageBitmap(target);
        targetImageView1.setImageBitmap(target);

        punch.setOnClickListener(new PunchClickListener());
        targetImageView.setOnClickListener(new TumblerClickListener());

        opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.mouse555, opt);
        StartTumblerAnimation(targetImageView,opt.outWidth,opt.outHeight * 2);
        StartTumblerAnimation(targetImageView1,opt.outWidth,opt.outHeight * 2);

        /*3d rotate animation prepare*/
        mCenterX = opt.outWidth/2;
        mCenterY = opt.outHeight * 2;//?
        mStartAnimView = targetImageView;

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof com.jinshu.xuzhi.feeling.MenuFragment) {
                    return;
                }
                Fragment fragment = new com.jinshu.xuzhi.feeling.MenuFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.commit();
            }
        });

        mpPunch.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mpTumbler.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        return mRootView;
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
        }
        public void onAnimationRepeat(Animation animation) {
        }
    }
    private final class SwapViews implements Runnable
    {
        @Override
        public void run()
        {
            targetImageView.setVisibility(View.GONE);
            targetImageView1.setVisibility(View.GONE);
            mIndex++;
            if (0 == mIndex % 2)
            {
                mStartAnimView = targetImageView;
            }
            else
            {
                mStartAnimView = targetImageView1;
            }
            mStartAnimView.setVisibility(View.VISIBLE);
            mStartAnimView.requestFocus();

            Rotate3dAnimation rotation = new Rotate3dAnimation(
                    -90,
                    0,
                    mCenterX,
                    mCenterY, mDepthZ, false);

            rotation.setDuration(mDuration);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mStartAnimView.startAnimation(rotation);
            PlayAudio(getActivity(),mpTumbler,"reverse2");

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

            float targetX = (float)(mStartAnimView.getX()+ Math.random() * opt.outWidth);
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
                    PlayAudio(getActivity(),mpPunch,"punch2");
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mBoxingLayout.removeView(view);

                    /*hit the tumbler*/
                    applyRotation(mStartAnimView, 0, 90);
                    /*ask the question after click 10 times */
                    if (TotalClickTime % 10 == 0) {
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
        mpPunch.release();
        mpTumbler.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }

    private void StartTumblerAnimation(ImageView target,float pivotX,float pivotY)
    {
        target.setPivotX(pivotX );//outWidth * 2 = scaleX
        target.setPivotY(pivotY);//outHeight * 2 = scaleY 华为p9有问题
        final ObjectAnimator imageViewObjectAnimator1 = ObjectAnimator.ofFloat(target ,
                "rotation", -45f, 45f);
        imageViewObjectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        imageViewObjectAnimator1.setDuration(1000);

        final ObjectAnimator imageViewObjectAnimator2 = ObjectAnimator.ofFloat(target ,
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
    }

    public class TumblerClickListener implements View.OnClickListener{
        @Override
        public void onClick(final View view) {
            //disappear dialog view
            if (dialog.getAlpha() == 1.0f)
            {
                dialog.setAlpha(0.0f);
            }
            TotalClickTime++;
            PlayAudio(getActivity(),mpPunch,"punch3");
            /*hit the tumbler*/
            applyRotation(mStartAnimView, 0, 90);
                    /*ask the question after click 10 times */
            if (TotalClickTime % 10 == 0) {
                dialog.setAlpha(1.0f);
            }


        }

    }
}
