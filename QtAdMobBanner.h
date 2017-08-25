#ifndef QTADMOBBANNER_H
#define QTADMOBBANNER_H

#include "QtAdMobBannerAndroid.h"
#include "QtAdMobBannerIos.h"
#include "QtAdMobBannerDummy.h"
class QTimer;

inline IQtAdMobBanner* CreateQtAdMobBanner()
{
#if (__ANDROID_API__ >= 9)
    return new QtAdMobBannerAndroid();
#elif (TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
    return new QtAdMobBannerIos();
#else
    return new QtAdMobBannerDummy();
#endif
}

class QmlAdMobBanner : public QObject
{
    Q_OBJECT

    Q_PROPERTY(QString unitId READ unitId WRITE setUnitId NOTIFY unitIdChanged)
    Q_PROPERTY(Sizes size READ size WRITE setSize NOTIFY sizeChanged)
    Q_PROPERTY(QSize sizeInPixels READ sizeInPixels NOTIFY sizeChanged)
    Q_PROPERTY(int width READ width NOTIFY sizeChanged)
    Q_PROPERTY(int height READ height NOTIFY sizeChanged)
    Q_PROPERTY(QPoint position READ position WRITE setPosition NOTIFY positionChanged)
    Q_PROPERTY(int x READ x WRITE setX NOTIFY positionChanged)
    Q_PROPERTY(int y READ y WRITE setY NOTIFY positionChanged)
    Q_PROPERTY(bool visible READ visible WRITE setVisible NOTIFY visibleChanged)
    Q_PROPERTY(bool isLoaded READ isLoaded NOTIFY loaded)


    //StartAd
    Q_PROPERTY(bool startAdBannerEnabled READ StartAdBannerEnabled WRITE setStartAdBannerEnabled NOTIFY startAdBannerEnabledChanged)
    Q_PROPERTY(int startAdBannerHeight READ StartAdBannerHeight NOTIFY startAdBannerHeightChanged)
    Q_PROPERTY(int startAdBannerWidth READ StartAdBannerWidth NOTIFY startAdBannerWidthChanged)
    Q_PROPERTY(QPoint startAdBannerPosition READ StartAdBannerPosition WRITE setStartAdBannerPosition)
    Q_PROPERTY(QSize startAdBannerSize READ StartAdBannerSize WRITE setStartAdBannerSize)
    Q_PROPERTY(int startAdBannerRealX READ startAdBannerRealX)
    Q_PROPERTY(int startAdBannerRealY READ startAdBannerRealY)
    Q_PROPERTY(bool startAdBannerVisible READ StartAdBannerVisible WRITE setStartAdBannerVisible NOTIFY startAdBannerVisibleChanged)
    Q_PROPERTY(QString startAdId READ getStartAdId WRITE setStartAdId NOTIFY startAdIdChanged)
    Q_PROPERTY(QStringList testDevices READ getTestDevices WRITE setTestDevices NOTIFY testDevicesChanged)


public:
    enum Sizes
    {
        Banner = 0,
        FullBanner,
        LargeBanner,
        MediumRectangle,
        SmartBanner,
        WideSkyscraper
    };
    Q_ENUMS(Sizes)

    QmlAdMobBanner();
    ~QmlAdMobBanner();

    /*
     * Call it before loading qml
     */
    static void DeclareQML();

    /*
     * Configure banner id
     */
    void setUnitId(const QString& unitId);

    /*
     * Retrive banner id
     */
    const QString& unitId() const;

    /*
     * Setup preconfigured banner size
     */
    void setSize(Sizes size);

    /*
     * Retrieve banner size
     */
    Sizes size() const;

    /*
     * Get real banner size in pixels
     */
    QSize sizeInPixels();

    /*
     * Get banner width in pixels
     */
    int width();

    /*
     * Get banner height in pixels
     */
    int height();

    /*
     * Setup banner position
     */
    void setPosition(const QPoint& position);

    /*
     * Retrieve banner position
     */
    const QPoint& position() const;

    /*
     * Set X position
     */
    void setX(int x);

    /*
     * Get X position
     */
    int x();

    /*
     * Set Y position
     */
    void setY(int y);

    /*
     * Get Y position
     */
    int y();

    /*
     * Show banner
     */
    void setVisible(bool isVisible);

    /*
     * Is banner showed
     */
    bool visible();

    /*
     * Is banner loaded
     */
    bool isLoaded();

    /*
     * Add test device identifier
     */
    Q_INVOKABLE void addTestDevice(const QString& hashedDeviceId);

signals:
    void unitIdChanged();
    void sizeChanged();
    void positionChanged();
    void visibleChanged();
    void loaded();
    void loading();
    void closed();
    void clicked();

    //startAd
    void  startAdBannerShowed();
    void  startAdBannerHeightChanged(int height);
    void  startAdBannerWidthChanged(int width);
    void startAdIdChanged();
    void startAdBannerVisibleChanged();
    void startAdBannerEnabledChanged();
    void testDevicesChanged();

public slots:
    void init();
    void setStartAdBannerEnabled(const bool StartAdBannerEnabled);
    bool StartAdBannerEnabled() const;
    int StartAdBannerHeight() const;
    int StartAdBannerWidth() const;
    void setStartAdBannerPosition(const QPoint StartAdBannerPosition);
    QPoint StartAdBannerPosition() const;
    void setStartAdBannerSize(const QSize StartAdBannerSize);
    QSize StartAdBannerSize() const;
    int startAdBannerRealX();
    int startAdBannerRealY();
    void setStartAdId(const QString &StartAdId);
    QString getStartAdId()const;
    void setStartAdBannerVisible(const bool visible);
    bool StartAdBannerVisible()const;
    QStringList getTestDevices()const;
    void setTestDevices(const QStringList &testDevices);
    void showStartAdBanner();
    void hideStartAdBanner();
    float dp();
    float mm();
    float pt();
        void adctlTimerSlot();
//    IQtAdMobBanner* getInstance();

private:
    IQtAdMobBanner* QML() const;

private:
    IQtAdMobBanner* m_Instance;

protected:
    QTimer *adctlTimer;
    //StartAd
    bool m_StartAdBannerEnabled = false;
    bool m_StartAdBannerVisible = false;
    QString m_StartAdId;
    QStringList m_testDevices;
    bool m_AdInitialized = false;
    int cacheStartAdBannerHeight;
    int cacheStartAdBannerWidth;
    QPoint m_StartAdBannerPosition;
    QSize m_StartAdBannerSize;
    bool m_isStartAdBannerShowed = false;
    bool m_StartAdBannerShowHideTrigger = false;
    bool m_StartAdWidthAlredyGreatThanZero = false;
    float m_dp;
    float m_pt;
    float m_mm;

};

#endif // QTADMOBBANNER_H

