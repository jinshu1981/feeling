package com.jinshu.xuzhi.feeling;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import static com.jinshu.xuzhi.feeling.Util.IMAGE_FILE_NAME;
import static com.jinshu.xuzhi.feeling.Util.ShowCustomPicture;
import static com.jinshu.xuzhi.feeling.Util.customFileExist;
import static com.jinshu.xuzhi.feeling.Util.decodeUriAsBitmap;
import static com.jinshu.xuzhi.feeling.Util.getTempUri;
import static com.jinshu.xuzhi.feeling.Util.zoomImg;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();
    final MediaPlayer mp  = new MediaPlayer();
    final int SELECT_PIC = 1;
    static ImageView customPicture,photoFrame,delete;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        final ImageView optionLeft = (ImageView) rootView.findViewById(R.id.optionLeft);
        final ImageView optionCenter = (ImageView) rootView.findViewById(R.id.optionCenter);
        final ImageView optionRight = (ImageView) rootView.findViewById(R.id.optionRight);
        photoFrame = (ImageView) rootView.findViewById(R.id.photoFrame);
        customPicture = (ImageView) rootView.findViewById(R.id.customPicture);
        delete = (ImageView) rootView.findViewById(R.id.delete);
        if (customFileExist())
        {
            delete.setVisibility(View.VISIBLE);
        }
        else
        {
            delete.setVisibility(View.GONE);
        }

        if (customFileExist()) {
           ShowCustomPicture(getActivity(), customPicture, 180);
            delete.setVisibility(View.VISIBLE);
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
                goAlbumsAndCrop();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                    Boolean res = file.delete();
                    Log.v(LOG_TAG,res.toString());
                    if (res)
                    {
                        Log.v(LOG_TAG,"custom picture deleted" );
                        BitmapDrawable drawableBackground = (BitmapDrawable)getActivity().getResources().getDrawable(R.drawable.example100jpg);
                        customPicture.setImageBitmap(drawableBackground.getBitmap());
                        delete.setVisibility(View.GONE);

                    }
                }

        });

        return rootView;
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
        intent.putExtra("crop", "true");
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
                //获取图片后缩放图片并显示
                File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                if (file.exists()) {
                    Bitmap bitmap = decodeUriAsBitmap(getActivity(), Uri.fromFile(file));
                    // 把解析到的位图显示出来
                    Bitmap bitmap200 = zoomImg(bitmap,200,200);
                    customPicture.setImageBitmap(bitmap200);
                    delete.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

}
