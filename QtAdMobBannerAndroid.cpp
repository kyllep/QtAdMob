#include "QtAdMobBannerAndroid.h"

#if (__ANDROID_API__ >= 9)

#include <QtAndroid>
#include <QAndroidJniObject>
#include <QAndroidJniEnvironment>
#include <qpa/qplatformnativeinterface.h>
#include <QGuiApplication>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_org_dreamdev_QtAdMob_QtAdMobActivity_onBannerLoaded(JNIEnv *env, jobject thiz)
{
    Q_UNUSED(env)
    Q_UNUSED(thiz)

    const QtAdMobBannerAndroid::TInstances& instances = QtAdMobBannerAndroid::Instances();
    QtAdMobBannerAndroid::TInstances::ConstIterator beg = instances.begin();
    QtAdMobBannerAndroid::TInstances::ConstIterator end = instances.end();
    while(beg != end)
    {
        emit beg.value()->loaded();

        beg++;
    }
}

JNIEXPORT void JNICALL Java_org_dreamdev_QtAdMob_QtAdMobActivity_onBannerLoading(JNIEnv *env, jobject thiz)
{
    Q_UNUSED(env)
    Q_UNUSED(thiz)

    const QtAdMobBannerAndroid::TInstances& instances = QtAdMobBannerAndroid::Instances();
    QtAdMobBannerAndroid::TInstances::ConstIterator beg = instances.begin();
    QtAdMobBannerAndroid::TInstances::ConstIterator end = instances.end();
    while(beg != end)
    {
        emit beg.value()->loading();

        beg++;
    }
}

JNIEXPORT void JNICALL Java_org_dreamdev_QtAdMob_QtAdMobActivity_onBannerClosed(JNIEnv *env, jobject thiz)
{
    Q_UNUSED(env)
    Q_UNUSED(thiz)

    const QtAdMobBannerAndroid::TInstances& instances = QtAdMobBannerAndroid::Instances();
    QtAdMobBannerAndroid::TInstances::ConstIterator beg = instances.begin();
    QtAdMobBannerAndroid::TInstances::ConstIterator end = instances.end();
    while(beg != end)
    {
        emit beg.value()->closed();

        beg++;
    }
}

JNIEXPORT void JNICALL Java_org_dreamdev_QtAdMob_QtAdMobActivity_onBannerClicked(JNIEnv *env, jobject thiz)
{
    Q_UNUSED(env)
    Q_UNUSED(thiz)

    const QtAdMobBannerAndroid::TInstances& instances = QtAdMobBannerAndroid::Instances();
    QtAdMobBannerAndroid::TInstances::ConstIterator beg = instances.begin();
    QtAdMobBannerAndroid::TInstances::ConstIterator end = instances.end();
    while(beg != end)
    {
        emit beg.value()->clicked();

        beg++;
    }
}

#ifdef __cplusplus
}
#endif

int QtAdMobBannerAndroid::s_Index = 0;
QtAdMobBannerAndroid::TInstances QtAdMobBannerAndroid::s_Instances;

QtAdMobBannerAndroid::QtAdMobBannerAndroid()
    : m_BannerSize(IQtAdMobBanner::Banner)
    , m_Activity(0)
    , m_Index(s_Index++)
{
    s_Instances[m_Index] = this;

    QPlatformNativeInterface* interface = QGuiApplication::platformNativeInterface();
    jobject activity = (jobject)interface->nativeResourceForIntegration("QtActivity");
    if (activity)
    {
        m_Activity = new QAndroidJniObject(activity);
    }

    m_Activity->callMethod<void>("InitializeAdBanner");
}

QtAdMobBannerAndroid::~QtAdMobBannerAndroid()
{
    s_Instances.remove(m_Index);

    setVisible(false);
    if (isValid())
    {
        m_Activity->callMethod<void>("ShutdownAdBanner");
    }

    if (m_Activity)
    {
        delete m_Activity;
    }
}

void QtAdMobBannerAndroid::setUnitId(const QString& unitId)
{
    if (!isValid())
    {
        return;
    }

    QAndroidJniObject param1 = QAndroidJniObject::fromString(unitId);
    m_Activity->callMethod<void>("SetAdBannerUnitId", "(Ljava/lang/String;)V", param1.object<jstring>());
    m_UnitId = unitId;
}

const QString& QtAdMobBannerAndroid::unitId() const
{
    return m_UnitId;
}

void QtAdMobBannerAndroid::setSize(IQtAdMobBanner::Sizes size)
{
    if (!isValid())
    {
        return;
    }

    m_Activity->callMethod<void>("SetAdBannerSize", "(I)V", (int)size);
    m_BannerSize = size;
}

IQtAdMobBanner::Sizes QtAdMobBannerAndroid::size() const
{
    return m_BannerSize;
}

QSize QtAdMobBannerAndroid::sizeInPixels()
{
    int width = m_Activity->callMethod<int>("GetAdBannerWidth");
    int height = m_Activity->callMethod<int>("GetAdBannerHeight");

    return QSize(width, height);
}

void QtAdMobBannerAndroid::setPosition(const QPoint& position)
{    
    if (!isValid())
    {
        return;
    }

    m_Activity->callMethod<void>("SetAdBannerPosition", "(II)V", position.x(), position.y());
    m_Position = position;
}

const QPoint& QtAdMobBannerAndroid::position() const
{
    return m_Position;
}

void QtAdMobBannerAndroid::setVisible(bool isVisible)
{
    if (!isValid())
    {
        return;
    }

    if (isVisible)
    {
        m_Activity->callMethod<void>("ShowAdBanner");
    }
    else
    {
        m_Activity->callMethod<void>("HideAdBanner");
    }
}

bool QtAdMobBannerAndroid::visible()
{
    if (!isValid())
    {
        return false;
    }

    bool isVisible = m_Activity->callMethod<jboolean>("IsAdBannerShowed", "()Z");
    return isVisible;
}

bool QtAdMobBannerAndroid::isLoaded()
{
    if (!isValid())
    {
        return false;
    }

    bool isLoaded = m_Activity->callMethod<jboolean>("IsAdBannerLoaded", "()Z");
    return isLoaded;
}

void QtAdMobBannerAndroid::addTestDevice(const QString& hashedDeviceId)
{
    if (!isValid())
    {
        return;
    }

    QAndroidJniObject param1 = QAndroidJniObject::fromString(hashedDeviceId);
    m_Activity->callMethod<void>("AddAdTestDevice", "(Ljava/lang/String;)V", param1.object<jstring>());
}

const QtAdMobBannerAndroid::TInstances& QtAdMobBannerAndroid::Instances()
{
    return s_Instances;
}

bool QtAdMobBannerAndroid::isValid() const
{
    return (m_Activity != 0 && m_Activity->isValid());
}






//-------------------- StartAd -------------------------------------------------


void QtAdMobBannerAndroid::initStartAd(){
    m_Activity->callMethod<void>("InitializeStartAdBanner");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

void QtAdMobBannerAndroid::setStartAdId(const QString& id){
    QAndroidJniObject param1 = QAndroidJniObject::fromString(id);
    m_Activity->callMethod<void>("SetStartAdId", "(Ljava/lang/String;)V", param1.object<jstring>());
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

void QtAdMobBannerAndroid::setStartAdBannerSize(const int width, const int height){
    m_Activity->callMethod<void>("SetStartAdBannerSize", "(II)V", width, height);
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

void QtAdMobBannerAndroid::setStartAdBannerPosition(const int x, const int y){
    m_Activity->callMethod<void>("SetStartAdBannerPosition", "(II)V", x, y);
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

int QtAdMobBannerAndroid::startAdBannerHeight() const{
    return m_Activity->callMethod<int>("GetStartAdBannerHeight");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

int QtAdMobBannerAndroid::startAdBannerWidth() const{
    return m_Activity->callMethod<int>("GetStartAdBannerWidth");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

int QtAdMobBannerAndroid::startAdBannerX() const{
    return m_Activity->callMethod<int>("GetStartAdBannerX");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

int QtAdMobBannerAndroid::startAdBannerY() const{
    return m_Activity->callMethod<int>("GetStartAdBannerY");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

void QtAdMobBannerAndroid::showStartAd(){
    m_Activity->callMethod<void>("ShowStartAdBanner");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

void QtAdMobBannerAndroid::hideStartAd(){
    m_Activity->callMethod<void>("HideStartAdBanner");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
}

float QtAdMobBannerAndroid::getDensity() const{
    QAndroidJniObject qtActivity = QAndroidJniObject::callStaticObjectMethod("org/qtproject/qt5/android/QtNative", "activity", "()Landroid/app/Activity;");
    QAndroidJniObject resources = qtActivity.callObjectMethod("getResources", "()Landroid/content/res/Resources;");
    QAndroidJniObject displayMetrics = resources.callObjectMethod("getDisplayMetrics", "()Landroid/util/DisplayMetrics;");
    int density = displayMetrics.getField<int>("densityDpi");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
    return density;
}

int QtAdMobBannerAndroid::adMobBannerX() const{
    int xval = m_Activity->callMethod<int>("GetAdMobBannerX");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
    return xval;
}

int QtAdMobBannerAndroid::adMobBannerY() const{
    int yval = m_Activity->callMethod<int>("GetAdMobBannerY");
    // Checking exceptions
    QAndroidJniEnvironment env;
    if (env->ExceptionCheck()) {
        // Printing exception message
        env->ExceptionDescribe();

        // Clearing exceptions
        env->ExceptionClear();
    }
    return yval;
}

//------------------------------------------------------------------------------

#endif // __ANDROID_API__
