package com.jinshu.xuzhi.feeling;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFishing extends Fragment {
    private static View mRootView;
    private static ImageView fish1,fish2,fish3,fish4,fish5,fishHook;
    static ObjectAnimator bubblePopUp,popUpAfterMove;
    static AnimatorSet set;
    private static int mScreenWidth,mScreenHeight;
    private static  BitmapFactory.Options opt;
    private static ArrayList<Fish> mFishList= new ArrayList<Fish>();;
    private final  String LOG_TAG = this.getClass().getSimpleName();
    private static int mClickX,mClickY;
    private static RelativeLayout mFishingLayout;
    static FragmentFishing mThis;
    static Bitmap mTarget;
    static final MediaPlayer mpBubble  = new MediaPlayer();
    static final MediaPlayer mpYisell = new MediaPlayer();
    static String CONSTANTS_RES_PREFIX = "android.resource://com.jinshu.xuzhi.feeling/";
    private class Fish{
        int resourceId = 0;
        int sharkId = 0;
        ImageView fishView = null;
        boolean beShark = false;

        Fish(ImageView fishView,int resourceId,int sharkId){

            this.fishView = fishView;
            this.resourceId = resourceId;
            this.sharkId = sharkId;
            this.beShark = false;
        }
    }
    static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            /*if fish touch the fishhook ,bite it*/
            int x = (int)(fishHook.getX() + fishHook.getWidth()/2);
            int y =  (int)(fishHook.getY() + fishHook.getHeight()/2);
            for (Fish fish:mFishList)
            {
                biteTheHook(fish,x,y);
            }

            super.handleMessage(msg);
        }
    };
    public FragmentFishing() {
        // Required empty public constructor
        mThis = this;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_fishing, container, false);
        fish1 = (ImageView)mRootView.findViewById(R.id.fish1);
        fish2 = (ImageView)mRootView.findViewById(R.id.fish2);
        fish3 = (ImageView)mRootView.findViewById(R.id.fish3);
        fish4 = (ImageView)mRootView.findViewById(R.id.fish4);
        fish5 = (ImageView)mRootView.findViewById(R.id.fish5);
        mFishingLayout = (RelativeLayout)mRootView.findViewById(R.id.fishingLayout);
        mFishList.add(new Fish(fish1,R.drawable.fishicon,R.drawable.sharkright));
        mFishList.add(new Fish(fish5,R.drawable.fishpink,R.drawable.sharkright));
        mFishList.add(new Fish(fish2,R.drawable.fishicon1,R.drawable.sharkleft));
        mFishList.add(new Fish(fish3,R.drawable.fishicon2,R.drawable.sharkleft));
        mFishList.add(new Fish(fish4,R.drawable.fishblue,R.drawable.sharkleft));

        fishHook = (ImageView)mRootView.findViewById(R.id.fishhook);
        mTarget = combineImages(R.drawable.bubble60,R.drawable.angrypig3);
        fishHook.setImageBitmap(mTarget);
        fishHook.setTag("fishHook");


        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        opt = new BitmapFactory.Options();
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        //Log.v(LOG_TAG,"mScreenWidth = " + mScreenWidth + ",mScreenHeight = " + mScreenHeight);
        opt.inJustDecodeBounds = true;
        startFishSwimAnimation(R.drawable.fishicon,fish1,"L");
        startFishSwimAnimation(R.drawable.fishpink,fish5,"L");
        startFishSwimAnimation(R.drawable.fishicon1,fish2,"R");
        startFishSwimAnimation(R.drawable.fishicon2,fish3,"R");
        startFishSwimAnimation(R.drawable.fishblue,fish4,"R");
        bubblePopUp = ObjectAnimator.ofFloat(fishHook,"Y",mScreenHeight + 40,0).setDuration((mScreenHeight + 40)*10);
        //Log.v(LOG_TAG,"bubblePopUp duration" + (mScreenHeight + 40));
        bubblePopUp.start();
        new Thread(new MyThread()).start();
        mpBubble.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mpYisell.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mClickX = min((int) motionEvent.getX(),mScreenWidth - fishHook.getWidth()) ;
                        mClickY = min((int) motionEvent.getY(),mScreenHeight - fishHook.getHeight());

                        /*if any animation not finished stop it */
                        if ((set != null) &&(set.isRunning())) set.cancel();
                        if ((bubblePopUp != null) &&(bubblePopUp.isRunning())) bubblePopUp.cancel();
                        if ((popUpAfterMove != null) &&(popUpAfterMove.isRunning())) popUpAfterMove.cancel();

                        /*Move fishhook to destination*/
                        long yDistance = (long)abs(fishHook.getY()-mClickY);
                        long xDistance = (long)abs(fishHook.getX()-mClickX);
                        Log.v(LOG_TAG,"fishHook x = " + fishHook.getX() + ",fishHook y = "+ fishHook.getY() );
                        Log.v(LOG_TAG,"mClickX = " + mClickX + ",mClickY = "+ mClickY );
                        long speed = yDistance > xDistance? yDistance:xDistance;
                        ObjectAnimator clickY = ObjectAnimator.ofFloat(fishHook,"Y",mClickY).setDuration(speed *5);
                        ObjectAnimator clickX = ObjectAnimator.ofFloat(fishHook,"X", mClickX).setDuration(speed *5);
                        //Log.v(LOG_TAG,"move duration" + speed);

                        set = new AnimatorSet();
                        set.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                popUpAfterMove = ObjectAnimator.ofFloat(fishHook,"Y",0).setDuration((long)abs(fishHook.getY()) * 10);

                                popUpAfterMove.start();

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        set.playTogether(clickY, clickX);
                        set.setInterpolator(new LinearInterpolator());
                        set.start();
                        break;
                }
                return true;
            }
        });

        return mRootView;
    }


    void startFishSwimAnimation(int drawableId,final ImageView fish,String startPoint)
    {
        BitmapFactory.decodeResource(getResources(), drawableId, opt);
        int duration = (int)((new Random().nextInt(5) + 5) * 1000);
        /******************define animation ***********************/
        /*from left to right*/
        final ObjectAnimator swim2Right = ObjectAnimator.ofFloat(fish,"X",(float)(-opt.outWidth *2), (float)(mScreenWidth + opt.outWidth *2)).setDuration(duration);
        swim2Right.setInterpolator(new LinearInterpolator());
        /*from right to left*/
        final ObjectAnimator swim2Left = ObjectAnimator.ofFloat(fish,"X", (float)(mScreenWidth + opt.outWidth *2),(float)(-opt.outWidth *2)).setDuration(duration);
        swim2Left.setInterpolator(new LinearInterpolator());
        /*turn*/
        final ObjectAnimator turn180Degree = ObjectAnimator.ofFloat(fish, "rotationY", 0.0F, 180.0F);
        final ObjectAnimator turnBack = ObjectAnimator.ofFloat(fish, "rotationY",180.0F, 360.0F);
        /************define listener *************/
        Animator.AnimatorListener turn180DegreeListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fish.setY((int)(Math.random() * (mScreenHeight - opt.outHeight *2)));
                turn180Degree.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        Animator.AnimatorListener turnBackListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fish.setY((int)(Math.random() * (mScreenHeight - opt.outHeight *2)));
                turnBack.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        Animator.AnimatorListener swim2RightListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                swim2Right.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        Animator.AnimatorListener swim2LeftListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                swim2Left.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };

        /*setup listeners and start animation*/
        if (startPoint.equals("L")) {

            swim2Right.addListener(turn180DegreeListener);
            turn180Degree.addListener(swim2LeftListener);
            swim2Left.addListener(turnBackListener);
            turnBack.addListener(swim2RightListener);
            swim2Right.start();
        }
        else
        {
            swim2Left.addListener(turn180DegreeListener);
            turn180Degree.addListener(swim2RightListener);
            swim2Right.addListener(turnBackListener);
            turnBack.addListener(swim2LeftListener);
            swim2Left.start();

        }
    }
    private Bitmap combineImages(int backgroundId, int  foregroundId)
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


        mergeImage = Bitmap.createBitmap(imageBackground.getWidth(), imageBackground.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(mergeImage);

        Paint paint = new Paint();
        paint.setAlpha(200);
        comboImage.drawBitmap(imageBackground, 0f, 0f, null);
        comboImage.drawBitmap(imageForeground, (imageBackground.getWidth()/2 - imageForeground.getWidth()/2), (imageBackground.getHeight() - imageForeground.getHeight() - 20), paint);

        return mergeImage;
    }
     public static void biteTheHook(Fish fish,int x,int y)
     {
         ImageView fishView = fish.fishView;
         if ((abs((fishView.getX()+ fishView.getWidth()/2) - x) < 40) && (abs((fishView.getY() + fishView.getHeight()/2) - y) < 40))
         {
             fishView.setImageResource(fish.sharkId);
             fish.beShark = true;
             /*delete fishhook view ,play audio,popup a new fishhook*/
             ImageView fishhook = (ImageView) mRootView.findViewWithTag("fishHook");

             try {
                 mpBubble.reset();
                 int id = mThis.getActivity().getResources().getIdentifier("bite", "raw", "com.jinshu.xuzhi.feeling");
                 String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                 //Log.v(LOG_TAG,uriString);
                 mpBubble.setDataSource(mThis.getActivity(), Uri.parse(uriString));
                 mpBubble.prepareAsync();
             } catch (IOException e) {
                 e.printStackTrace();
             }

             mFishingLayout.removeView(fishhook);

             ImageView newFishhook = new ImageView(mThis.getActivity());
             newFishhook.setImageBitmap(mTarget);
             newFishhook.setTag("fishHook");
             RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
             params1.leftMargin = (int)(random() * (mScreenWidth - mTarget.getWidth()));
             params1.topMargin  = mScreenHeight/2;
             mFishingLayout.addView(newFishhook,params1);
             fishHook = newFishhook;
             bubblePopUp = ObjectAnimator.ofFloat(fishHook,"Y",mScreenHeight + 40,0).setDuration((mScreenHeight + 40)*10);
             bubblePopUp.start();

         }
         else if (fish.beShark)
         {
             fishView.setImageResource(fish.resourceId);
             fish.beShark = false;
             try {
                 mpYisell.reset();
                 int id = mThis.getActivity().getResources().getIdentifier("yisell", "raw", "com.jinshu.xuzhi.feeling");
                 String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                 //Log.v(LOG_TAG,uriString);
                 mpYisell.setDataSource(mThis.getActivity(), Uri.parse(uriString));
                 mpYisell.prepareAsync();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
    @Override
    public void onDestroy() {
        mpBubble.release();
        mpYisell.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }
}
