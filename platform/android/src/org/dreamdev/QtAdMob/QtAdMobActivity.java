package org.dreamdev.QtAdMob;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.util.Log;
import android.graphics.Rect;
import android.widget.FrameLayout;
import android.content.Intent;
import android.app.Activity;

import org.qtproject.qt5.android.bindings.QtActivity;
import org.qtproject.qt5.android.bindings.QtApplication;

import java.util.ArrayList;


import android.view.WindowManager;
import android.graphics.Color;
import android.content.Context;




public class QtAdMobActivity extends QtActivity
{
    private ViewGroup m_ViewGroup;
    private AdView m_AdBannerView = null;
    private InterstitialAd m_AdInterstitial = null;
    private boolean m_IsAdBannerShowed = false;
    private boolean m_IsAdBannerLoaded = false;
    private boolean m_IsAdInterstitialLoaded = false;
    private ArrayList<String> m_TestDevices = new ArrayList<String>();
    private int m_AdBannerWidth = 0;
    private int m_AdBannerHeight = 0;
    private int m_StatusBarHeight = 0;
    private int m_ReadyToRequest = 0x00;

    protected int uiOptions = 0;
    protected boolean showNavigation = true;



    public int GetStatusBarHeight()
    {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;
        return titleBarHeight;
    }


    public int statusBarHeight() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            return 0;
//        }

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
    }

    return result;
}


    public void SetAdBannerUnitId(final String adId)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                m_AdBannerView.setAdUnitId(adId);
                m_ReadyToRequest |= 0x01;
                if (m_ReadyToRequest == 0x03 && !IsAdBannerLoaded())
                {
                    RequestBanner();
                }
            }
        });
    }



    public void SetAdBannerSize(final int size)
    {
        final QtAdMobActivity self = this;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                AdSize adSize = AdSize.BANNER;
                switch(size)
                {
                    case 0:
                        adSize = AdSize.BANNER;
                    break;
                    case 1:
                        adSize = AdSize.FULL_BANNER;
                    break;
                    case 2:
                        adSize = AdSize.LARGE_BANNER;
                    break;
                    case 3:
                        adSize = AdSize.MEDIUM_RECTANGLE;
                    break;
                    case 4:
                        adSize = AdSize.SMART_BANNER;
                    break;
                    case 5:
                        adSize = AdSize.WIDE_SKYSCRAPER;
                    break;
                };

                m_AdBannerView.setAdSize(adSize);
                m_AdBannerWidth = adSize.getWidthInPixels(self);
                m_AdBannerHeight = adSize.getHeightInPixels(self);

                m_ReadyToRequest |= 0x02;
                if (m_ReadyToRequest == 0x03 && !IsAdBannerLoaded())
                {
                    RequestBanner();
                }
            }
        });
    }

    public void SetAdBannerPosition(final int x, final int y)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                                     FrameLayout.LayoutParams.WRAP_CONTENT);
                m_AdBannerView.setLayoutParams(layoutParams);
                m_AdBannerView.setX(x);
                m_AdBannerView.setY(y + m_StatusBarHeight);
            }
        });
    }

    public void AddAdTestDevice(final String deviceId)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                m_TestDevices.add(deviceId);
            }
        });
    }

    public boolean IsAdBannerShowed()
    {
        return m_IsAdBannerShowed && m_IsAdBannerLoaded;
    }

    public boolean IsAdBannerLoaded()
    {
        return m_IsAdBannerLoaded;
    }

    public int GetAdBannerWidth()
    {
        return m_AdBannerWidth;
    }

    public int GetAdBannerHeight()
    {
        return m_AdBannerHeight;
    }

    public void ShowAdBanner()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (IsAdBannerShowed())
                {
                    return;
                }

                if (m_ReadyToRequest == 0x03 && !IsAdBannerLoaded())
                {
                    RequestBanner();
                }
                m_AdBannerView.setVisibility(View.VISIBLE);
                m_IsAdBannerShowed = true;
            }
        });
    }

    private void RequestBanner()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onBannerLoading();

                AdRequest.Builder adRequest = new AdRequest.Builder();
                adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                for (String deviceId : m_TestDevices)
                {
                    adRequest.addTestDevice(deviceId);
                }
                m_AdBannerView.loadAd(adRequest.build());
            }
        });
    }

    public void HideAdBanner()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!IsAdBannerShowed())
                {
                    return;
                }

                m_AdBannerView.setVisibility(View.GONE);
                m_IsAdBannerShowed = false;
            }
        });
    }

    public void InitializeAdBanner()
    {
        final QtAdMobActivity self = this;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (m_AdBannerView != null)
                {
                    return;
                }

                m_StatusBarHeight = GetStatusBarHeight();

                m_AdBannerView = new AdView(self);
                m_AdBannerView.setVisibility(View.GONE);

                View view = getWindow().getDecorView().getRootView();
                if (view instanceof ViewGroup)
                {
                    m_ViewGroup = (ViewGroup) view;

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                                         FrameLayout.LayoutParams.WRAP_CONTENT);
                    m_AdBannerView.setLayoutParams(layoutParams);
                    m_AdBannerView.setX(0);
                    m_AdBannerView.setY(m_StatusBarHeight);
                    m_ViewGroup.addView(m_AdBannerView);

                    m_AdBannerView.setAdListener(new AdListener()
                    {
                        public void onAdLoaded()
                        {
                            m_IsAdBannerLoaded = true;
                            onBannerLoaded();
                        }

                        public void onAdClosed()
                        {
                            onBannerClosed();
                        }

                        public void onAdLeftApplication()
                        {
                            onBannerClicked();
                        }
                    });
                }
            }
        });
    }

    public void ShutdownAdBanner()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (m_AdBannerView == null)
                {
                    return;
                }

                m_IsAdBannerShowed = false;
                m_ViewGroup.removeView(m_AdBannerView);
                m_AdBannerView = null;
            }
        });
    }

    public void LoadAdInterstitialWithUnitId(final String adId)
    {
        final QtAdMobActivity self = this;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (m_AdInterstitial == null)
                {
                    m_IsAdInterstitialLoaded = false;

                    m_AdInterstitial = new InterstitialAd(self);
                    m_AdInterstitial.setAdUnitId(adId);
                    m_AdInterstitial.setAdListener(new AdListener()
                    {
                        public void onAdLoaded()
                        {
                            if (m_AdBannerView == null)
                            {
                                return;
                            }
                            m_AdBannerView.setVisibility(View.VISIBLE);
                            m_IsAdBannerShowed = true;

                            m_IsAdInterstitialLoaded = true;
                            onInterstitialLoaded();
                        }

                        public void onAdClosed()
                        {
                            onInterstitialClosed();
                        }

                        public void onAdLeftApplication()
                        {
                            onInterstitialClicked();
                        }
                    });
                }
                RequestNewInterstitial();
            }
        });
    }

    private void RequestNewInterstitial()
    {
        onInterstitialLoading();

        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        for (String deviceId : m_TestDevices)
        {
            adRequest.addTestDevice(deviceId);
        }
        m_AdInterstitial.loadAd(adRequest.build());
    }

    public boolean IsAdInterstitialLoaded()
    {
        return m_IsAdInterstitialLoaded;
    }

    public void ShowAdInterstitial()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (m_IsAdInterstitialLoaded)
                {
                    onInterstitialWillPresent();

                    m_AdInterstitial.show();
                    m_IsAdInterstitialLoaded = false; // Ad might be presented only once, need reload
                }
            }
        });
    }

    public void setTransparrent()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
               getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
               getWindow().setStatusBarColor(Color.TRANSPARENT);
           } else {
               getWindow().setFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                   , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           }
       }
    }


    public void applyNavigationBarState()
    {
        if (!showNavigation)
        {
            if (uiOptions != 0)
            {
                // чтобы спрятать устанавливаем все флаги
                uiOptions |=  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
        }
        else if ((View.SYSTEM_UI_FLAG_HIDE_NAVIGATION & uiOptions) != 0)
        {
            // чтобы показать убираем флаги, если есть
            uiOptions &= ~(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                         | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        restoreStatusBarState();
    }

    public void showNavigationBar()
    {
       showNavigation = true;
       applyNavigationBarState();
    }

    public void hideNavigationBar()
    {
       showNavigation = false;
       applyNavigationBarState();
    }


    public void hideStatusBar()
    {
        uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        applyNavigationBarState();
//        restoreStatusBarState();
    }

    public void showStatusBar()
    {
        uiOptions = 0;
        applyNavigationBarState();
    }

    public void restoreStatusBarState()
    {
        Log.d("MyActivity", "restoreStatusBarState " + uiOptions);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        hideStatusBar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        Log.d("MyActivity", "onWindowFocusChanged " + hasFocus);
        if (hasFocus) {
            restoreStatusBarState();
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("MyActivity", "onPause");
        if (m_AdBannerView != null)
        {
            m_AdBannerView.pause();
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("MyActivity", "onResume");
        if (m_AdBannerView != null)
        {
            m_AdBannerView.resume();
        }
//        showStatusBar();
//        restoreStatusBarState();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (m_AdBannerView != null)
        {
            m_AdBannerView.destroy();
        }
    }

    private static native void onBannerLoaded();
    private static native void onBannerLoading();
    private static native void onBannerClosed();
    private static native void onBannerClicked();

    private static native void onInterstitialLoaded();
    private static native void onInterstitialLoading();
    private static native void onInterstitialWillPresent();
    private static native void onInterstitialClosed();
    private static native void onInterstitialClicked();
}
