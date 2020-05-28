package com.nextcont.mobilization

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.nextcont.mobilization.service.LocationService
import timber.log.Timber

class MobApp: Application() {

    companion object {

        lateinit var Shared: MobApp private set
        lateinit var Context: Context private set

        val sp: SharedPreferences
            get() = Context.getSharedPreferences("mob_data_3", MODE_PRIVATE)

    }


    override fun onCreate() {
        super.onCreate()
        Shared = this
        Context = applicationContext
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        LocationService.init(this)


        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(this, "mobilization-face-android", "idl-license.face-android")

        val config = FaceSDKManager.getInstance().faceConfig
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
//        config.setLivenessTypeList(ExampleApplication.livenessList)
//        config.setLivenessRandom(ExampleApplication.isLivenessRandom)
        config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS)
        config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS)
        config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE)
        config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH)
        config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL)
        config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW)
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE)
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD)
        config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION)
        config.setCheckFaceQuality(true)
        config.setFaceDecodeNumberOfThreads(2)

        FaceSDKManager.getInstance().faceConfig = config
    }

//    不透明 100% FF
//    95% F2
//    90% E6
//    85% D9
//    80% CC
//    75% BF
//    70% B3
//    65% A6
//    60% 99
//    55% 8C
//    半透明 50% 80
//    45% 73
//    40% 66
//    35% 59
//    30% 4D
//    25% 40
//    20% 33
//    15% 26
//    10% 1A
//    5% 0D
//    全透明 0% 00
}