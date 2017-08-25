#include "QtAdMobBanner.h"
#ifdef QTADMOB_QML
#include <QtQml>
#endif

#include <QString>
#include <QDebug>
#include <QTimer>

QmlAdMobBanner::QmlAdMobBanner()
{
    m_Instance = CreateQtAdMobBanner();


    float density = m_Instance->getDensity();
       m_mm = density / 25.4;
       m_pt =  1;

       //engine.rootContext()->setContextProperty("adctlMM",density / 25.4);
       //engine.rootContext()->setContextProperty("adctlPT", 1);

       double scale = density < 180 ? 1 :
                                      density < 270 ? 1.5 :
                                                      density < 360 ? 2 : 3;

       m_dp = scale;

       cacheStartAdBannerHeight = 0;
       cacheStartAdBannerWidth = 0;

    connect(m_Instance, SIGNAL(loaded()), this, SIGNAL(loaded()));
    connect(m_Instance, SIGNAL(loading()), this, SIGNAL(loading()));
    connect(m_Instance, SIGNAL(closed()), this, SIGNAL(closed()));
    connect(m_Instance, SIGNAL(clicked()), this, SIGNAL(clicked()));


}

QmlAdMobBanner::~QmlAdMobBanner()
{
    delete m_Instance;
}

void QmlAdMobBanner::init()
{
    qDebug() << "init";
    if (m_StartAdBannerEnabled) {
        qDebug() << "initStartAd";
        m_Instance->initStartAd();
    }

    //this timer is required!
     adctlTimer = new QTimer(this);
     connect(adctlTimer, SIGNAL(timeout()), this, SLOT(adctlTimerSlot()));
     adctlTimer->start();

    m_AdInitialized = true;
}

void QmlAdMobBanner::DeclareQML()
{
#ifdef QTADMOB_QML
    qmlRegisterType<QmlAdMobBanner>("com.dreamdev.QtAdMobBanner", 1, 0, "AdMobBanner");
#endif
}


//This slot called by timer. Why? Because we don't know,
//when target platform is currently initialize our banners.
//When banners alredy initialized, we stop this timer and reposition our banners to setted by user positions.
void QmlAdMobBanner::adctlTimerSlot()
{

    //signals StartAd
    if (cacheStartAdBannerHeight != StartAdBannerHeight()) {
        cacheStartAdBannerHeight = StartAdBannerHeight();
        emit startAdBannerHeightChanged(StartAdBannerHeight());
    }
    if (cacheStartAdBannerWidth != StartAdBannerWidth()) {
        cacheStartAdBannerWidth = StartAdBannerWidth();
        emit startAdBannerWidthChanged(StartAdBannerWidth());
    }

    //qDebug() << "Banners state" << AdMobBannerWidth() << StartAdBannerWidth()
    //         << adctlTimer->interval() << m_isStartAdBannerShowed << m_isAdMobBannerShowed
    //         << StartAdBannerHeight() << m_AdInitialized << m_StartAdBannerEnabled;

    //signal for firsttime reposition of banners
    if ((StartAdBannerWidth() > 0) && !m_StartAdWidthAlredyGreatThanZero) {
        m_isStartAdBannerShowed = true;
        m_StartAdBannerShowHideTrigger = true;
        m_StartAdWidthAlredyGreatThanZero = true;
        emit startAdBannerShowed();
    }

    if ((m_StartAdWidthAlredyGreatThanZero || !m_StartAdBannerEnabled)) {
        adctlTimer->stop();
        adctlTimer->start(2000);
    }
}

void QmlAdMobBanner::setUnitId(const QString& unitId)
{
    QML()->setUnitId(unitId);

    emit unitIdChanged();
}

const QString& QmlAdMobBanner::unitId() const
{
    return QML()->unitId();
}

void QmlAdMobBanner::setSize(QmlAdMobBanner::Sizes size)
{
    QML()->setSize((IQtAdMobBanner::Sizes)size);

    emit sizeChanged();
}

QmlAdMobBanner::Sizes QmlAdMobBanner::size() const
{
    return (QmlAdMobBanner::Sizes)QML()->size();
}

QSize QmlAdMobBanner::sizeInPixels()
{
    return QML()->sizeInPixels();
}

int QmlAdMobBanner::width()
{
    return sizeInPixels().width();
}

int QmlAdMobBanner::height()
{
    return sizeInPixels().height();
}

void QmlAdMobBanner::setPosition(const QPoint& position)
{
    QML()->setPosition(position);

    emit positionChanged();
}

const QPoint& QmlAdMobBanner::position() const
{
    return QML()->position();
}

void QmlAdMobBanner::setX(int x)
{
    QML()->setPosition(QPoint(x, y()));

    emit positionChanged();
}

int QmlAdMobBanner::x()
{
    return QML()->position().x();
}

void QmlAdMobBanner::setY(int y)
{
    QML()->setPosition(QPoint(x(), y));

    emit positionChanged();
}

int QmlAdMobBanner::y()
{
    return QML()->position().y();
}

void QmlAdMobBanner::setVisible(bool isVisible)
{
    QML()->setVisible(isVisible);

    emit visibleChanged();
}

bool QmlAdMobBanner::visible()
{
    return QML()->visible();
}

bool QmlAdMobBanner::isLoaded()
{
    return QML()->isLoaded();
}

void QmlAdMobBanner::addTestDevice(const QString& hashedDeviceId)
{
    QML()->addTestDevice(hashedDeviceId);
}

IQtAdMobBanner* QmlAdMobBanner::QML() const
{
    return m_Instance;
}



//------------------------- StartAd --------------------------------------------


//StartAd banner enabled
bool QmlAdMobBanner::StartAdBannerEnabled() const
{
    return m_StartAdBannerEnabled;
}

void QmlAdMobBanner::setStartAdBannerEnabled(bool StartAdBannerEnabled)
{
    if(m_StartAdBannerEnabled != StartAdBannerEnabled)
    {
        m_StartAdBannerEnabled = StartAdBannerEnabled;
        emit startAdBannerEnabledChanged();
    }
}



//StartAd width and height
int QmlAdMobBanner::StartAdBannerWidth() const
{
    if (!m_AdInitialized || !m_StartAdBannerEnabled) { return 0; }
    return m_Instance->startAdBannerWidth();
}

int QmlAdMobBanner::StartAdBannerHeight() const
{
    if (!m_AdInitialized || !m_StartAdBannerEnabled) { return 0; }
    return m_Instance->startAdBannerHeight();
}


//StartAd banner position
QPoint QmlAdMobBanner::StartAdBannerPosition() const
{
    return m_StartAdBannerPosition;
}

void QmlAdMobBanner::setStartAdBannerSize(const QSize StartAdBannerSize)
{
    m_StartAdBannerSize = StartAdBannerSize;
    //qDebug() << "StartAdBannerSize C++" << m_StartAdBannerSize;
    //if (!m_AdInitialized || !m_StartAdBannerEnabled) { return; }
    m_Instance->setStartAdBannerSize(StartAdBannerSize.width(),StartAdBannerSize.height());
}

QSize QmlAdMobBanner::StartAdBannerSize() const
{
    return m_StartAdBannerSize;
}


int QmlAdMobBanner::startAdBannerRealX()
{
    return m_Instance->startAdBannerX();
}

int QmlAdMobBanner::startAdBannerRealY()
{
    return m_Instance->startAdBannerY();
}

void QmlAdMobBanner::setStartAdBannerPosition(const QPoint position)
{
    m_StartAdBannerPosition = position;
    if (!m_AdInitialized || !m_StartAdBannerEnabled) { return; }
    m_Instance->setStartAdBannerPosition(position.x(),position.y());
}


void QmlAdMobBanner::setStartAdId(const QString &StartAdId)
{

    if (!m_StartAdId.isEmpty()) { qDebug() << "StartAd ID alredy set!"; return; }
    m_StartAdId = StartAdId;
    m_Instance->setStartAdId(StartAdId);

    m_Instance->initStartAd();
    emit startAdIdChanged();
}


QString QmlAdMobBanner::getStartAdId() const
{
    return m_StartAdId;
}



void QmlAdMobBanner::setStartAdBannerVisible(const bool visible)
{
    qDebug() << "setStartAdBannerVisible";
    if(m_StartAdBannerVisible!=visible) {
        m_StartAdBannerVisible=visible;
        if(visible) {
            showStartAdBanner();
        } else {
            hideStartAdBanner();
        }
        emit startAdBannerVisibleChanged();
    }
}



void QmlAdMobBanner::showStartAdBanner()
{
    if (!m_AdInitialized || !m_StartAdBannerEnabled) { return; }

    if (!m_StartAdBannerShowHideTrigger) {
        m_Instance->showStartAd();
        m_StartAdBannerShowHideTrigger = true;
    }
}

void QmlAdMobBanner::hideStartAdBanner()
{
    if (!m_AdInitialized || !m_StartAdBannerEnabled) { return; }

    if (m_StartAdBannerShowHideTrigger) {
        m_Instance->hideStartAd();
        m_StartAdBannerShowHideTrigger = false;
    }
}

bool QmlAdMobBanner::StartAdBannerVisible() const
{
    return m_StartAdBannerVisible;
}

QStringList QmlAdMobBanner::getTestDevices() const
{
    return m_testDevices;
}



float QmlAdMobBanner::dp()
{
    return m_dp;
}

float QmlAdMobBanner::mm()
{
    return m_mm;
}

float QmlAdMobBanner::pt()
{
    return m_pt;
}

void QmlAdMobBanner::setTestDevices(const QStringList &testDevices)
{
    if(testDevices!=m_testDevices){
        m_testDevices = testDevices;
        emit testDevicesChanged();
    }
}


//------------------------------------------------------------------------------



