package com.genius.wzy.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.genius.wzy.R;

import genius.com.wzy.linecontrolbar.LineProgressControlBar;


public class WatchVolumeFragment extends Fragment implements
        LineProgressControlBar.OnProgressChangeListener {
    private static final int DEFAULT_MIN_VOLUME_VALUE = 0;
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    private AudioManager mAudioManager;
    private Ringtone mRingtone;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new
            AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    stopRingtone();
                }
            };
    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VOLUME_CHANGED_ACTION)) {
                updateViews();
            }
        }
    };

    private void updateViews() {
        if (mAudioManager != null) {
            int newVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            if (newVolume != mCurVolumeValue) {
                saveVolume(newVolume);
                mLineProgressControlBar.setCurrentProgress(mCurVolumeValue);
            }
        }
    }

    private int mMaxVolumeValue;
    private int mCurVolumeValue;

    private LineProgressControlBar mLineProgressControlBar;

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
        mLineProgressControlBar = (LineProgressControlBar)
                rootView.findViewById(R.id.id_line_progress_control_bar);
        ImageView minusImg = (ImageView) rootView.findViewById(R.id.id_minus_img);
        minusImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineProgressControlBar.minusControl();
            }
        });

        ImageView plusImg = (ImageView) rootView.findViewById(R.id.id_plus_img);
        plusImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLineProgressControlBar.plusControl();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolumeValue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        mCurVolumeValue = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        Uri mAlarmUri = RingtoneManager.
                getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM);
        mRingtone = RingtoneManager.getRingtone(getActivity(), mAlarmUri);
    }

    private void initView() {
        mLineProgressControlBar.setOnProgressChangeListener(this);
        mLineProgressControlBar.setMaxAndMinProgress(mMaxVolumeValue, DEFAULT_MIN_VOLUME_VALUE);
        mLineProgressControlBar.setCurrentProgress(mCurVolumeValue);

    }

    @Override
    public void onResume() {
        registerReceiver();
        super.onResume();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGED_ACTION);
        getActivity().registerReceiver(mVolumeReceiver, filter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mVolumeReceiver);
        stopRingtone();
        mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        super.onPause();
    }

    @Override
    public void progressChange(int progress) {
        Log.e("TAG", "progress=" + progress);
        saveVolume(progress);
        playRingtone();
    }

    @Override
    public void adjustProgress(int progress) {
        if (mAudioManager != null && progress != mCurVolumeValue) {
            saveVolume(progress);
        }
    }

    private void saveVolume(int progress) {
        mCurVolumeValue = progress;
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mCurVolumeValue, 0);
    }

    @SuppressWarnings("deprecation")
    private void playRingtone() {
        stopRingtone();
        if (!mRingtone.isPlaying()) {
            mAudioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN);
            mRingtone.setStreamType(AudioManager.STREAM_ALARM);
            mRingtone.play();
        }
    }

    private void stopRingtone() {
        if (mRingtone != null) {
            mRingtone.stop();
        }
    }
}
