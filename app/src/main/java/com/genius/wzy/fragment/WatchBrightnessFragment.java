package com.genius.wzy.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.genius.wzy.R;

import genius.com.wzy.linecontrolbar.LineProgressControlBar;


public class WatchBrightnessFragment extends Fragment implements
        LineProgressControlBar.OnProgressChangeListener{
    private static final int DEFAULT_MAX_BACKLIGHT = 255;
    private static final int DEFAULT_MIN_BACKLIGHT = 30;
    private static final int DEFAULT_BACKLIGHT = DEFAULT_MIN_BACKLIGHT;

    private int mLastBrightness = 150;

    private ContentResolver mContentResolver;
    private ContentObserver mScreenBrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int brightness = getBrightness();
            if (brightness != mLastBrightness) {
                mLastBrightness = brightness;
                mLineProgressControlBar.setCurrentProgress(mLastBrightness);
            }
        }
    };

    private LineProgressControlBar mLineProgressControlBar;


    public WatchBrightnessFragment() {
    }

    public static WatchBrightnessFragment newInstance() {
        return new WatchBrightnessFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watch_brightness, container, false);
        mLineProgressControlBar =
                (LineProgressControlBar) rootView.findViewById(R.id.id_line_progress_control_bar);
        mLineProgressControlBar.setOnProgressChangeListener(this);
        mLineProgressControlBar.setMaxAndMinProgress(DEFAULT_MAX_BACKLIGHT, DEFAULT_MIN_BACKLIGHT);

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
        mContentResolver = getActivity().getContentResolver();
        setScreenMode();
        mLastBrightness = getBrightness();
    }

    private void initView() {
        mLineProgressControlBar.setCurrentProgress(mLastBrightness);
    }

    @Override
    public void onResume() {
        mContentResolver.registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false,
                mScreenBrightnessObserver);
        super.onResume();
    }

    @Override
    public void onPause() {
        mContentResolver.unregisterContentObserver(mScreenBrightnessObserver);
        super.onPause();
    }

    @Override
    public void progressChange(int progress) {
        mLastBrightness = progress;
        saveBrightness();
    }

    @Override
    public void adjustProgress(int progress) {
        if (progress != mLastBrightness) {
            mLastBrightness = progress;
            saveBrightness();
        }
    }

    private void setScreenMode() {
        try {
            int mode = Settings.System.getInt(mContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == 1) {
                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 获取屏幕亮度. */
    private int getBrightness() {
        return Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS,
                DEFAULT_BACKLIGHT);
    }

    /** 保存屏幕亮度. */
    private void saveBrightness() {
        Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS,
                mLastBrightness);
    }
}
