package com.jinshu.xuzhi.feeling;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

import c.b.BP;
import tyrantgit.explosionfield.ExplosionField;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    final MediaPlayer mp  = new MediaPlayer();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ImageView feelingImage = (ImageView) rootView.findViewById(R.id.feelingImage);
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


        feelingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(feelingImage);
                feelingImage.setClickable(false);
                /*play audio*/
                try {
                    mp.reset();
                    int id = getActivity().getResources().getIdentifier("dabaozha", "raw", "com.jinshu.xuzhi.feeling");
                    String uriString = Util.CONSTANTS_RES_PREFIX + Integer.toString(id);
                    Log.v(LOG_TAG,uriString);
                    mp.setDataSource(getActivity(), Uri.parse(uriString));
                    mp.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Util.getScreenSize(getActivity());
        BP.init("f914c1c8c3c77abf5acf235407c4cd6f");
        return rootView;
    }

    @Override
    public void onDestroy() {
        mp.release();
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }




}
