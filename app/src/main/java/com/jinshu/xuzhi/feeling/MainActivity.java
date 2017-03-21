package com.jinshu.xuzhi.feeling;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (f instanceof com.jinshu.xuzhi.feeling.MainActivityFragment) {
            return;
        }
        Fragment fragment = new com.jinshu.xuzhi.feeling.MainActivityFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyOnClickListener implements View.OnClickListener {


        public MyOnClickListener() {

        }

        @Override
        public void onClick(View v) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f instanceof com.jinshu.xuzhi.feeling.FragmentColorBall) {
                return;
            }
            Fragment fragment = new com.jinshu.xuzhi.feeling.FragmentColorBall();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment, fragment);
            transaction.commit();
        }
    }
}