package com.genius.wzy.fragment;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genius.wzy.R;


public class WatchVolumeFragment extends Fragment {
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private AudioManager mAudioManager;

    public WatchVolumeFragment() {
        // Required empty public constructor
    }

    public static WatchVolumeFragment newInstance() {
        return new WatchVolumeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watch_volume, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int maxValue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
    }
}
