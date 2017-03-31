package com.jinshu.xuzhi.feeling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by xuzhi on 2017/3/23.
 */

public class Util {

    private final String LOG_TAG = this.getClass().getSimpleName();
    static final String CONSTANTS_RES_PREFIX = "android.resource://com.jinshu.xuzhi.feeling/";
    static final String IMAGE_FILE_NAME = "1111.jpg";


     static Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    /**
     * 临时图片保存路径
     * @return
     */
     static File getTempFile() {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    static Bitmap decodeUriAsBitmap(Context context,Uri uri) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    // 缩放图片
     static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    //显示自定义图片
    static void ShowCustomPicture(Context context,ImageView target, int scale)
    {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        if (file.exists())
        {
            Bitmap bitmap = null;

            try {
                // 先通过getContentResolver方法获得一个ContentResolver实例，
                // 调用openInputStream(Uri)方法获得uri关联的数据流stream
                // 把上一步获得的数据流解析成为bitmap

                bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.fromFile(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 把解析到的位图显示出来
            if (scale != 0)
            {
                bitmap = zoomImg(bitmap,scale,scale);
            }
            target.setImageBitmap(bitmap);

        }
    }

    /*play audio*/
    static void PlayAudio(Context context, MediaPlayer player, String voice)
    {
        try {
            player.reset();
            int id = context.getResources().getIdentifier(voice, "raw", "com.jinshu.xuzhi.feeling");
            String uriString = Util.CONSTANTS_RES_PREFIX + Integer.toString(id);
            //Log.v(LOG_TAG,uriString);
            player.setDataSource(context, Uri.parse(uriString));
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int mScreenWidth,mScreenHeight;
    static void getScreenSize(Activity activity){
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        Log.v("Util","mScreenWidth = " + mScreenWidth + ",mScreenHeight = " + mScreenHeight);
    }

    public static Bitmap combineImages(Context context,int backgroundId,int  foregroundId,int scale)
    {
        Bitmap mergeImage = null,imageBackground = null,imageForeground = null;
        try {
            BitmapDrawable drawableBackground = (BitmapDrawable)context.getResources().getDrawable(backgroundId);
            BitmapDrawable drawableForeground = (BitmapDrawable)context.getResources().getDrawable(foregroundId);
            imageBackground = drawableBackground.getBitmap();
            imageForeground = drawableForeground.getBitmap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (customFileExist())
        {

            Bitmap bitmap = null;
            try {
                // 先通过getContentResolver方法获得一个ContentResolver实例，
                // 调用openInputStream(Uri)方法获得uri关联的数据流stream
                // 把上一步获得的数据流解析成为bitmap
                File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.fromFile(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            // 设置缩放过的图片为forground图片
            imageForeground = zoomImg(bitmap,scale,scale);
        }

        mergeImage = Bitmap.createBitmap(imageBackground.getWidth(), imageBackground.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(mergeImage);

        comboImage.drawBitmap(imageBackground, 0f, 0f, null);
        comboImage.drawBitmap(imageForeground, (imageBackground.getWidth()/2 - imageForeground.getWidth()/2), (imageBackground.getHeight() - imageForeground.getHeight() - 60), null);

        return mergeImage;
    }

    static boolean customFileExist()
    {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        if (file.exists())
        {
            return true;
        }
        else{
            return false;
        }
    }

}
