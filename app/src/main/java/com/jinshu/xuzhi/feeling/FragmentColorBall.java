package com.jinshu.xuzhi.feeling;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentColorBall extends Fragment {

    private static int mScreenWidth,mScreenHeight;
    private static LinkedList<ColorBallCoordinates> mColorBallCoordinatesList;
    private static ArrayList<ColorDirtyPair> mColorDirtyPairList;
    private static View mRootView;
    private static ImageView target, dialog;
    private static RelativeLayout mColorBallLayout;
    private static  BitmapFactory.Options opt;
    private static int TotalClickTime = 0;
    private static int[] ballImageArray = {R.drawable.ballblack,R.drawable.ballblue,
                                       R.drawable.ballbrown,R.drawable.ballgreen,
                                       R.drawable.balllightblue,R.drawable.ballpurple,
                                       R.drawable.ballred,R.drawable.ballyellow,
                                       R.drawable.ballwhite
    };
    private static float targetX,targetY;
    private final String LOG_TAG = this.getClass().getSimpleName();
    final MediaPlayer mpBall  = new MediaPlayer();
    final MediaPlayer mpWater = new MediaPlayer();
    final String CONSTANTS_RES_PREFIX = "android.resource://com.jinshu.xuzhi.feeling/";
    public FragmentColorBall() {
        // Required empty public constructor
        mColorBallCoordinatesList = new LinkedList<ColorBallCoordinates>();

        mColorDirtyPairList = new ArrayList<ColorDirtyPair>();
        ArrayList dirty1 = new ArrayList();
        dirty1.addAll((Arrays.asList(R.drawable.dirtyblack1, R.drawable.dirtyblack2, R.drawable.dirtyblack3)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballblack,dirty1));

        ArrayList dirty2 = new ArrayList();
        dirty2.addAll((Arrays.asList(R.drawable.dirtyblue1, R.drawable.dirtyblue2)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballblue,dirty2));



        ArrayList dirty3 = new ArrayList();
        dirty3.addAll((Arrays.asList(R.drawable.dirtygreen1,R.drawable.dirtygreen2)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballgreen,dirty3));

        ArrayList dirty4 = new ArrayList();
        dirty4.addAll((Arrays.asList(R.drawable.dirtylightblue1)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.balllightblue,dirty4));

        ArrayList dirty5 = new ArrayList();
        dirty5.addAll((Arrays.asList(R.drawable.dirtypurple1,R.drawable.dirtypurple2)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballpurple,dirty5));

        ArrayList dirty6 = new ArrayList();
        dirty6.addAll((Arrays.asList(R.drawable.dirtyred1,R.drawable.dirtyred2)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballred,dirty6));

        ArrayList dirty7 = new ArrayList();
        dirty7.addAll((Arrays.asList(R.drawable.dirtyyellow1,R.drawable.dirtyyellow2)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballyellow,dirty7));

        ArrayList dirty8 = new ArrayList();
        dirty8.addAll((Arrays.asList(R.drawable.dirtywhite1,R.drawable.dirtywhite2)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballwhite,dirty8));

        ArrayList dirty9 = new ArrayList();
        dirty9.addAll((Arrays.asList(R.drawable.dirtybrown)));
        mColorDirtyPairList.add(new ColorDirtyPair(R.drawable.ballbrown,dirty9));

        TotalClickTime = 0;


    }
    private class ColorBallCoordinates{
        float x;
        float y;
        ColorBallCoordinates(float x,float y){
            this.x = x;
            this.y = y;
        }
    }
    private class ColorDirtyPair{
        int colorTag;
        ArrayList dirtyResourceIdList;
        ColorDirtyPair(int colorTag,ArrayList dirtyResourceIdList){
            this.colorTag = colorTag;
            this.dirtyResourceIdList = dirtyResourceIdList;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_color_ball, container, false);
        mColorBallLayout =  (RelativeLayout) mRootView.findViewById(R.id.colorBallLayout);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        opt = new BitmapFactory.Options();
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        Log.v(LOG_TAG,"mScreenWidth = " + mScreenWidth + ",mScreenHeight = " + mScreenHeight);
        final ImageView colorBallLeft = (ImageView)mRootView.findViewById(R.id.colorballLeft);
        final ImageView colorBallCenter = (ImageView)mRootView.findViewById(R.id.colorballCenter);
        final ImageView colorBallRight = (ImageView)mRootView.findViewById(R.id.colorballRight);
         dialog = (ImageView)mRootView.findViewById(R.id.dialog);
        colorBallLeft.setTag(R.drawable.ballpurple);
        colorBallCenter.setTag(R.drawable.ballgreen);
        colorBallRight.setTag(R.drawable.ballyellow);

        target = (ImageView)mRootView.findViewById(R.id.feelingImage);

        colorBallLeft.setOnClickListener(new ColorBallClickListener());
        colorBallCenter.setOnClickListener(new ColorBallClickListener());
        colorBallRight.setOnClickListener(new ColorBallClickListener());

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
     public class ColorBallClickListener implements View.OnClickListener{
        @Override
        public void onClick(final View view) {

            TotalClickTime ++;
            int xScale = target.getWidth();
            int yScale = target.getHeight();
            Log.v(LOG_TAG,"xScale = " + xScale + ",yScale = " + yScale);
            Log.v(LOG_TAG,"target x = " + target.getX() + ",target y = " + target.getY());
            /*store ball coordinates*/
            mColorBallCoordinatesList.add(new ColorBallCoordinates(view.getX(),view.getY()));

            targetX = (float)(target.getX()+ Math.random() * xScale);
            targetY = (float)(target.getY()+ Math.random() * yScale);
            Log.v(LOG_TAG,"targetX = " + targetX + ",targetY = " + targetY);
            ObjectAnimator clickBally = ObjectAnimator.ofFloat(view,"Y",targetY).setDuration(500);
            ObjectAnimator clickBallx = ObjectAnimator.ofFloat(view,"X", targetX).setDuration(500);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(clickBally, clickBallx);
            set.setInterpolator(new BounceInterpolator());

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    //play ball audio
                    try {
                        mpBall.reset();
                        int id = getActivity().getResources().getIdentifier("ball1", "raw", "com.jinshu.xuzhi.feeling");
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
                    /* delete the ball view */
                    int viewTag = (int)view.getTag();
                   // Log.v(LOG_TAG,"viewTag = " + viewTag);
                    mColorBallLayout.removeView(view);

                    /*add paint view to current position*/
                    Log.v(LOG_TAG,"add paint view to current position");
                    ImageView paintDirty = new ImageView(getContext());
                    int dirtyResourceId = generateDirtyByTag(viewTag);
                    paintDirty.setImageResource(dirtyResourceId);
                    paintDirty.setAlpha(0.5f);
                    /*get image size by drawable resource id*/
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), dirtyResourceId, opt);

                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params1.leftMargin = (int)(targetX - opt.outWidth/2);
                    params1.topMargin  = (int)(targetY - opt.outHeight/2);
                    //Log.v(LOG_TAG,"paintDirty.getWidth() = " + opt.outWidth + ",paintDirty.getHeight() " + opt.outHeight);
                    //play water audio
                    try {
                        mpWater.reset();
                        int id = getActivity().getResources().getIdentifier("water1", "raw", "com.jinshu.xuzhi.feeling");
                        String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                        Log.v(LOG_TAG,uriString);
                        mpWater.setDataSource(getActivity(), Uri.parse(uriString));
                        mpWater.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mColorBallLayout.addView(paintDirty,params1);



                    /*ask the question after click 10 times */
                    if (TotalClickTime % 10 == 0) {
                        Log.v(LOG_TAG, "show question view");
                        dialog.setAlpha(1.0f);
                    }

                    /*add a new ball view*/
                    ColorBallCoordinates coordinates = mColorBallCoordinatesList.poll();
                    ImageView colorBall = new ImageView(getContext());
                    int index = new Random().nextInt(ballImageArray.length);
                    //Log.v(LOG_TAG,"ballImageArray index = " + index);
                    colorBall.setImageResource(ballImageArray[index]);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    colorBall.setOnClickListener(new ColorBallClickListener());
                    colorBall.setTag(ballImageArray[index]);
                    colorBall.setAlpha(0.0f);
                    params.leftMargin = (int)coordinates.x;
                    params.topMargin  = (int)coordinates.y;
                    mColorBallLayout.addView(colorBall,params);
                    colorBall.animate().alpha(1.0f).setDuration(500).start();
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
    int generateDirtyByTag(int tag)
    {
        //Log.v(LOG_TAG,"tag  = " + tag);
        for (ColorDirtyPair colorDirtyPair : mColorDirtyPairList) {
           // Log.v(LOG_TAG,"colorTag = " + colorDirtyPair.colorTag);
            if (colorDirtyPair.colorTag == tag)
            {
                return (int)colorDirtyPair.dirtyResourceIdList.get(new Random().nextInt(colorDirtyPair.dirtyResourceIdList.size()));
            }

        }
       return R.drawable.dirtyblack3;
    }
    @Override
    public void onDestroy() {
        mpBall.release();
        mpWater.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }

}
