package org.dreamdev.QtAdMob;

import com.startad.lib.SADView;

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

    protected String m_StartAdId;
    protected SADView m_StartAdBannerView;
    protected boolean m_StartAdBannerShowed = false;
    protected int m_startAdWidth = 0;
    protected int m_startAdHeight = 0;



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

//--------------------- StartAd ------------------------------------------------

   public void SetStartAdId(final String StartAdId)
   {
       runOnUiThread(new Runnable()
       {
           public void run()
           {
               m_StartAdId = StartAdId;
           }
       });
   }

    protected FrameLayout.LayoutParams getLayoutParams()
    {
        Log.d("getLayoutParams", ""+m_startAdWidth);
        FrameLayout.LayoutParams layoutParams;
        if (m_startAdWidth == 0) {
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                        FrameLayout.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new FrameLayout.LayoutParams(m_startAdWidth,
                                                        FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        return layoutParams;
    }

    public void SetStartAdBannerPosition(final int x, final int y)
    {
        Log.d("SetStartAdBannerPosition", ""+m_startAdWidth);
        runOnUiThread(new Runnable()
        {
            public void run()
            {
               FrameLayout.LayoutParams layoutParams = getLayoutParams();

               m_StartAdBannerView.setLayoutParams(layoutParams);
               m_StartAdBannerView.setX(x);
               m_StartAdBannerView.setY(y);
            }
        });
    }

    public void SetStartAdBannerSize(final int width, final int height)
    {
        m_startAdWidth = width;
        m_startAdHeight = height;
        Log.d("SetStartAdBannerSize", ""+m_startAdWidth);
//        runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                m_startAdWidth = width;
//                m_startAdHeight = height;
//                FrameLayout.LayoutParams layoutParams = getLayoutParams();

//                m_StartAdBannerView.setLayoutParams(layoutParams);
//            }
//        });
    }

    public boolean IsStartAdBannerShowed()
    {
        return m_StartAdBannerShowed;
    }

    public int GetStartAdBannerWidth()
    {
        return m_StartAdBannerView.getWidth();
    }

    public int GetStartAdBannerHeight()
    {
        return m_StartAdBannerView.getHeight();
    }

    public void InitializeStartAdBanner()
    {
        final QtAdMobActivity self = this;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (m_StartAdBannerView != null)
                {
                    return;
                }

                m_StatusBarHeight = GetStatusBarHeight();

                m_StartAdBannerView = new SADView(self, m_StartAdId);

                View view = getWindow().getDecorView().getRootView();
                if (view instanceof ViewGroup)
                {
                    m_ViewGroup = (ViewGroup) view;

                    FrameLayout.LayoutParams layoutParams = getLayoutParams();

                    m_StartAdBannerView.setLayoutParams(layoutParams);
                    m_StartAdBannerView.setX(0);
                    m_StartAdBannerView.setY(-300);
                    m_ViewGroup.addView(m_StartAdBannerView);

                    m_StartAdBannerView.setAdListener(new SADView.SADListener() {
                        @Override
                        public void onReceiveAd() {
                            Log.d("SADView", "SADListener onReceiveId");
                        }

                        @Override
                        public void onShowedAd() {
                            Log.d("SADView", "SADListener onShowedAd");
                            m_StartAdBannerShowed = true;
                        }

                        @Override
                        public void onError(SADView.ErrorCode error) {
                            Log.d("SADView", "SADListener onError " + error);
                        }

                        @Override
                        public void onAdClicked() {
                            Log.d("SADView", "SADListener onAdClicked");
                        }

                        @Override
                        public void noAdFound() {
                            Log.d("SADView", "SADListener noAdFound");
                        }
                    });

                    //Load ad for currently active language in app
                    m_StartAdBannerView.loadAd(SADView.LANGUAGE_RU); //or this.sadView.loadAd(SADView.LANGUAGE_RU);
                }
            }
        });
    }

    public int GetStartAdBannerX()
    {
      if(m_StartAdBannerView!=null){
          return Math.round(m_StartAdBannerView.getX());
      }else{
          return 0;
      }
    }

    public int GetStartAdBannerY()
    {
      if(m_StartAdBannerView!=null){
          return Math.round(m_StartAdBannerView.getY());
      }else{
          return 0;
      }
    }


    public void ShowStartAdBanner()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (IsStartAdBannerShowed())
                {
                    return;
                }

                m_StartAdBannerView.setVisibility(View.VISIBLE);
                m_StartAdBannerShowed = true;
            }
        });
    }

    public void HideStartAdBanner()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!IsStartAdBannerShowed())
                {
                    return;
                }

                m_StartAdBannerView.setVisibility(View.GONE);
                m_StartAdBannerShowed = false;
            }
        });
    }


//--------------------------------------------------------------------


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

    public void hideStatusBar()
    {

            View decorView = getWindow().getDecorView();
            // Hide the status bar.
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
//            ActionBar actionBar = getActionBar();
//            actionBar.hide();

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        hideStatusBar();
//        setTransparrent();

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (m_AdBannerView != null)
        {
            m_AdBannerView.pause();
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (m_AdBannerView != null)
        {
            m_AdBannerView.resume();
        }
        hideStatusBar();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (m_AdBannerView != null)
        {
            m_AdBannerView.destroy();
        }
        if(m_StartAdBannerView != null) {
           m_StartAdBannerView.destroy();
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
