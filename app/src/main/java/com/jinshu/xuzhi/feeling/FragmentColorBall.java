package com.jinshu.xuzhi.feeling;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static com.jinshu.xuzhi.feeling.Util.PlayAudio;
import static com.jinshu.xuzhi.feeling.Util.ShowCustomPicture;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentColorBall extends Fragment {

    private static LinkedList<ColorBallCoordinates> mColorBallCoordinatesList;
    private static ArrayList<ColorDirtyPair> mColorDirtyPairList;
    private static View mRootView;
    private static ImageView target, dialog,colorBallLeft,colorBallCenter,colorBallRight,backArrow;
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
    final MediaPlayer mpBall  = new MediaPlayer(),mpWater = new MediaPlayer();
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
        colorBallLeft = (ImageView)mRootView.findViewById(R.id.colorballLeft);
        colorBallCenter = (ImageView)mRootView.findViewById(R.id.colorballCenter);
        colorBallRight = (ImageView)mRootView.findViewById(R.id.colorballRight);
        dialog = (ImageView)mRootView.findViewById(R.id.dialog);
        target = (ImageView)mRootView.findViewById(R.id.feelingImage);
        backArrow = (ImageView)mRootView.findViewById(R.id.arrowback);

        ShowCustomPicture(getActivity(),target,0);
        colorBallLeft.setTag(R.drawable.ballpurple);
        colorBallCenter.setTag(R.drawable.ballgreen);
        colorBallRight.setTag(R.drawable.ballyellow);


        opt = new BitmapFactory.Options();

        colorBallLeft.setOnClickListener(new ColorBallClickListener());
        colorBallCenter.setOnClickListener(new ColorBallClickListener());
        colorBallRight.setOnClickListener(new ColorBallClickListener());

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
            /*store ball coordinates for future add new ball*/
            mColorBallCoordinatesList.add(new ColorBallCoordinates(view.getX(),view.getY()));

            /*generate ball destination coordinate*/
            targetX = (float)(target.getX()+ Math.random() * (xScale-20));
            targetY = (float)(target.getY()+ Math.random() * (yScale-20));//-20 避免球总打到下面
            Log.v(LOG_TAG,"targetX = " + targetX + ",targetY = " + targetY);
            ObjectAnimator clickBally = ObjectAnimator.ofFloat(view,"Y",targetY).setDuration(500);
            ObjectAnimator clickBallx = ObjectAnimator.ofFloat(view,"X", targetX).setDuration(500);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(clickBally, clickBallx);
            set.setInterpolator(new BounceInterpolator());

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    PlayAudio(getActivity(),mpBall,"ball1");
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    /* delete the ball view */
                    int viewTag = (int)view.getTag();
                    mColorBallLayout.removeView(view);

                    /*add paint view to current position*/
                   //Log.v(LOG_TAG,"add paint view to current position");
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

                    PlayAudio(getActivity(),mpWater,"water1");
                    mColorBallLayout.addView(paintDirty,params1);



                    /*show dialog after 10 times */
                    if (TotalClickTime % 10 == 0) {
                        //Log.v(LOG_TAG, "show question view");
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
    private int generateDirtyByTag(int tag)
    {
        for (ColorDirtyPair colorDirtyPair : mColorDirtyPairList) {
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
