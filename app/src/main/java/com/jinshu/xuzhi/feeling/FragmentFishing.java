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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.jinshu.xuzhi.feeling.Util.CONSTANTS_RES_PREFIX;
import static com.jinshu.xuzhi.feeling.Util.IMAGE_FILE_NAME;
import static com.jinshu.xuzhi.feeling.Util.mScreenHeight;
import static com.jinshu.xuzhi.feeling.Util.mScreenWidth;
import static com.jinshu.xuzhi.feeling.Util.zoomImg;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFishing extends Fragment {
    private static View mRootView;
    private static ImageView fish1,fish2,fish3,fish4,fish5,fish6,fishHook,backArrow;
    static ObjectAnimator bubblePopUp,popUpAfterMove;
    static AnimatorSet set;
    private static  BitmapFactory.Options opt;
    private static ArrayList<Fish> mFishList= new ArrayList<Fish>();;
    private final  String LOG_TAG = this.getClass().getSimpleName();
    private static int mClickX,mClickY;
    private static RelativeLayout mFishingLayout;
    static FragmentFishing mThis;
    static Bitmap mTarget;
    static final MediaPlayer mpBubble  = new MediaPlayer(),mpEnough = new MediaPlayer();
    private class Fish{
        int resourceId = 0;
        Boolean goRight = true;
        ImageView fishView = null;
        boolean beShark = false;

        Fish(ImageView fishView,int resourceId,Boolean goRight){

            this.fishView = fishView;
            this.resourceId = resourceId;
            this.beShark = false;
            this.goRight = goRight;
        }
    }
    /*0.5s repeat timer*/
    static Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            /*if fish meet the bubble ,bite it*/
            int x = (int)(fishHook.getX() + fishHook.getWidth()/2);
            int y =  (int)(fishHook.getY() + fishHook.getHeight()/2);
            for (Fish fish:mFishList)
            {
                biteTheBubble(fish,x,y);
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
        fish6 = (ImageView)mRootView.findViewById(R.id.fish6);
        backArrow = (ImageView)mRootView.findViewById(R.id.arrowback);
        mFishingLayout = (RelativeLayout)mRootView.findViewById(R.id.fishingLayout);
        mFishList.add(new Fish(fish1,R.drawable.fish1,true));
        mFishList.add(new Fish(fish2,R.drawable.fish2,false));
        mFishList.add(new Fish(fish3,R.drawable.fish3,false));
        mFishList.add(new Fish(fish4,R.drawable.fish4,false));
        mFishList.add(new Fish(fish5,R.drawable.fish5,true));
        mFishList.add(new Fish(fish6,R.drawable.fish6,true));

        fishHook = (ImageView)mRootView.findViewById(R.id.fishhook);
        mTarget = combineImages(R.drawable.bubble60,R.drawable.example40);
        fishHook.setImageBitmap(mTarget);
        fishHook.setTag("fishHook");

        opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;

        StartFishAnimation();
        bubblePopUp = ObjectAnimator.ofFloat(fishHook,"Y",mScreenHeight + 40,0).setDuration((mScreenHeight + 40)*10);
        bubblePopUp.start();
        new Thread(new MyThread()).start();
        mpBubble.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mpEnough.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
        return mRootView;
    }

    int getRandomDuration()
    {
        return (int)((new Random().nextInt(10) + 4) * 1000);
    }
    void startFishSwimAnimation(int drawableId,final ImageView fish,Boolean goRight)
    {
        BitmapFactory.decodeResource(getResources(), drawableId, opt);
        /******************define animation ***********************/
        /*from left to right*/
        final ObjectAnimator swim2Right = ObjectAnimator.ofFloat(fish,"X",(float)(-opt.outWidth *4), (float)(mScreenWidth + opt.outWidth *4));
        swim2Right.setInterpolator(new LinearInterpolator());
        /*from right to left*/
        final ObjectAnimator swim2Left = ObjectAnimator.ofFloat(fish,"X", (float)(mScreenWidth + opt.outWidth *4),(float)(-opt.outWidth *4));
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
                swim2Right.setDuration(getRandomDuration());
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
                swim2Left.setDuration(getRandomDuration());
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
        if (goRight) {

            swim2Right.addListener(turn180DegreeListener);
            turn180Degree.addListener(swim2LeftListener);
            swim2Left.addListener(turnBackListener);
            turnBack.addListener(swim2RightListener);
            swim2Right.setDuration(getRandomDuration());
            swim2Right.start();
        }
        else
        {
            swim2Left.addListener(turn180DegreeListener);
            turn180Degree.addListener(swim2RightListener);
            swim2Right.addListener(turnBackListener);
            turnBack.addListener(swim2LeftListener);
            swim2Left.setDuration(getRandomDuration());
            swim2Left.start();

        }
    }
    void StartFishAnimation(){
        for (Fish fish:mFishList)
        {
            startFishSwimAnimation(fish.resourceId,fish.fishView,fish.goRight);
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
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        if (file.exists())
        {

            Bitmap bitmap = null;
            try {
                // 先通过getContentResolver方法获得一个ContentResolver实例，
                // 调用openInputStream(Uri)方法获得uri关联的数据流stream
                // 把上一步获得的数据流解析成为bitmap
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(Uri.fromFile(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            // 设置缩放过的图片为foreground图片
            imageForeground = zoomImg(bitmap,70,70);
        }

        mergeImage = Bitmap.createBitmap(imageBackground.getWidth(), imageBackground.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(mergeImage);

        Paint paint = new Paint();
        paint.setAlpha(100);
        comboImage.drawBitmap(imageBackground, 0f, 0f, null);
        comboImage.drawBitmap(imageForeground, (imageBackground.getWidth()/2 - imageForeground.getWidth()/2), (imageBackground.getHeight() - imageForeground.getHeight() - 20), paint);

        return mergeImage;
    }
     public static void biteTheBubble(Fish fish,int x,int y)
     {
         ImageView fishView = fish.fishView;
         int sharkId = fish.goRight?R.drawable.sharkright:R.drawable.sharkleft;
         if ((abs((fishView.getX()+ fishView.getWidth()/2) - x) < 40) && (abs((fishView.getY() + fishView.getHeight()/2) - y) < 40))
         {
             fishView.setImageResource(sharkId);
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
                 mpEnough.reset();
                 int id = mThis.getActivity().getResources().getIdentifier("yisell", "raw", "com.jinshu.xuzhi.feeling");
                 String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                 //Log.v(LOG_TAG,uriString);
                 mpEnough.setDataSource(mThis.getActivity(), Uri.parse(uriString));
                 mpEnough.prepareAsync();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
    @Override
    public void onDestroy() {
        mpBubble.release();
        mpEnough.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
