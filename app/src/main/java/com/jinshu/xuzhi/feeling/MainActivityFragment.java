package com.jinshu.xuzhi.feeling;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
    final String IMAGE_FILE_NAME = "1111.jpg";
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
        final ImageView customPicture = (ImageView) rootView.findViewById(R.id.customPicture);
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
                goAlbums();
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
                clipperBigPic(getContext(), data.getData());
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
    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    /**
     * 临时图片保存路径
     * @return
     */
    private File getTempFile() {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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
}
