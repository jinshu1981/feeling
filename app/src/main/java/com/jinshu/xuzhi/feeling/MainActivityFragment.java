package com.jinshu.xuzhi.feeling;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import tyrantgit.explosionfield.ExplosionField;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    int alphaNum = 100;
    String CONSTANTS_RES_PREFIX = "android.resource://com.jinshu.xuzhi.feeling/";
    final MediaPlayer mp  = new MediaPlayer();
    final int SELECT_PIC = 1;
    final int SELECT_CLIPPER_PIC = 2;
    static final String IMAGE_FILE_NAME = "1111.jpg";
    static ImageView customPicture;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ImageView feelingImage = (ImageView) rootView.findViewById(R.id.feelingImage);
        final ImageView optionLeft = (ImageView) rootView.findViewById(R.id.optionLeft);
        final ImageView optionCenter = (ImageView) rootView.findViewById(R.id.optionCenter);
        final ImageView optionRight = (ImageView) rootView.findViewById(R.id.optionRight);
        final ImageView circle = (ImageView) rootView.findViewById(R.id.circle);
        customPicture = (ImageView) rootView.findViewById(R.id.customPicture);
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
            // 把解析到的位图显示出来
            customPicture.setImageBitmap(zoomImg(bitmap,200,200));
        }
        optionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof com.jinshu.xuzhi.feeling.FragmentColorBall) {
                    return;
                }
                Fragment fragment = new com.jinshu.xuzhi.feeling.FragmentColorBall();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.commit();
            }
        });
        optionCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof com.jinshu.xuzhi.feeling.FragmentBoxing) {
                    return;
                }
                Fragment fragment = new com.jinshu.xuzhi.feeling.FragmentBoxing();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.commit();
            }
        });
        optionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f instanceof com.jinshu.xuzhi.feeling.FragmentFishing) {
                    return;
                }
                Fragment fragment = new com.jinshu.xuzhi.feeling.FragmentFishing();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.commit();
            }
        });
        customPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用系统相册，进入相册获取图片
                //goAlbums();
                goAlbumsAndCrop();
            }
        });
        /*ExplosionField*/
        final ExplosionField explosionField = ExplosionField.attach2Window(getActivity());
        /*play audio*/
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                optionLeft.setVisibility(View.VISIBLE);
                optionCenter.setVisibility(View.VISIBLE);
                optionRight.setVisibility(View.VISIBLE);
                customPicture.setVisibility(View.VISIBLE);
                circle.setVisibility(View.VISIBLE);

            }
        });


        feelingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(feelingImage);
                feelingImage.setClickable(false);
                /*play audio*/
                try {
                    mp.reset();
                    int id = getActivity().getResources().getIdentifier("dabaozha", "raw", "com.jinshu.xuzhi.feeling");
                    String uriString = CONSTANTS_RES_PREFIX + Integer.toString(id);
                    Log.v(LOG_TAG,uriString);
                    mp.setDataSource(getActivity(), Uri.parse(uriString));
                    mp.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        mp.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }


    /**
     * 调用相册
     */
    private void goAlbums() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PIC);
    }

    /**
     * 调用相册
     */
    private void goAlbumsAndCrop() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // 设置在开启的Intent中设置显示的view可裁剪
        // 这段代码里设置成false也能裁剪啊。。。这是为什么？懂的给我讲讲了
        // 这段注释掉就不会跳转到裁剪的activity
        intent.putExtra("crop", "circle");
        // 设置x,y的比例，截图方框就按照这个比例来截 若设置为0,0，或者不设置 则自由比例截图
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // 裁剪区的宽和高 其实就是裁剪后的显示区域 若裁剪的比例不是显示的比例，则自动压缩图片填满显示区域。若设置为0,0 就不显示。若不设置，则按原始大小显示
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        // 不知道有啥用。。可能会保存一个比例值 需要相关文档啊
        intent.putExtra("scale", true);
        // true的话直接返回bitmap，可能会很占内存 不建议
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT即"output"关联一个Uri
        intent.putExtra("output", getTempUri());
        // 看参数即可知道是输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 面部识别 这里用不上
        intent.putExtra("noFaceDetection", false);
        startActivityForResult(intent, SELECT_PIC);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG,"requestCode = " + requestCode);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case SELECT_PIC:
                //获取图片后裁剪图片
                //clipperBigPic(getContext(), data.getData());
                if (getTempUri() != null) {
                Bitmap bitmap = decodeUriAsBitmap(getTempUri());
                // 把解析到的位图显示出来
                Bitmap bitmap200 = zoomImg(bitmap,200,200);
                customPicture.setImageBitmap(bitmap200);
               //刷新目标图片

                }
                break;
            case SELECT_CLIPPER_PIC:
                //获取图片后保存图片到本地，是否需要保存看情况而定
                saveBitmap(data);
                //显示图片
                //showImage(mGoAlarmIv);
                break;
        }
    }

    /**
     * 裁剪大图
     * @param context
     * @param uri
     */
    private void clipperBigPic(Context context, Uri uri) {
        if (null == uri) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        //Intent it = new Intent(Intent.ACTION_PICK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = PhotoClipperUtil.getPath(context, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }
        //发送裁剪命令
        intent.putExtra("crop", true);
        //X方向上的比例
        intent.putExtra("aspectX", 1);
        //Y方向上的比例
        intent.putExtra("aspectY", 1);
        //裁剪区的宽
        intent.putExtra("outputX", 124);
        //裁剪区的高
        intent.putExtra("outputY", 124);
        //是否保留比例
        intent.putExtra("scale", true);
        //返回数据
        intent.putExtra("return-data", true);
        //输出图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        //裁剪图片保存位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        startActivityForResult(intent, SELECT_CLIPPER_PIC);
    }
    public static Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    /**
     * 临时图片保存路径
     * @return
     */
    private static File getTempFile() {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
            }
        return bitmap;
        }
    /**
     * 保存图片
     * @param data
     */
    private void saveBitmap(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
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
}
