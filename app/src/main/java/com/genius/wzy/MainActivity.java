package com.genius.wzy;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.genius.wzy.fragment.WatchBrightnessFragment;
import com.genius.wzy.fragment.WatchVolumeFragment;

public class MainActivity extends Activity {
    private WatchBrightnessFragment mWatchBrightnessFragment;
    private Fragment mWatchVolumeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_main_layout);

        mWatchBrightnessFragment = WatchBrightnessFragment.newInstance();
        mWatchVolumeFragment = WatchVolumeFragment.newInstance();
    }

    public void onChangeBrightnessFragment(View view) {
        getFragmentManager().beginTransaction().replace(R.id.id_container, mWatchBrightnessFragment)
                .addToBackStack(null).commit();
    }

    public void onChangeVolumeFragment(View view) {
        getFragmentManager().beginTransaction().replace(R.id.id_container, mWatchVolumeFragment)
                .addToBackStack(null).commit();
    }
}
