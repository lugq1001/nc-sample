package com.nextcont.mobilization.service

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.nextcont.mobilization.MobApp
import timber.log.Timber


object LocationService : AMapLocationListener {

    //声明AMapLocationClient类对象
    private var client: AMapLocationClient? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var address = ""
        private set

    private var protocol: IProtocol? = null

    fun addDelegate(protocol: IProtocol) {
        this.protocol = protocol
    }

    fun removeDelegate() {
        protocol = null
    }

    fun start(purpose: AMapLocationClientOption.AMapLocationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn) {
        if (client == null) {
            //初始化定位
            client = AMapLocationClient(MobApp.Context)
            //设置定位回调监听
            client?.setLocationListener(this)
        } else {
            client?.stopLocation()
        }

        val option = AMapLocationClientOption()
        option.locationPurpose = purpose
        client?.startLocation()
    }

    fun stop() {
        client?.stopLocation()
    }

    fun destroy() {
        client?.onDestroy()
        client = null
    }

    override fun onLocationChanged(loc: AMapLocation?) {
        Timber.i("定位更新: $loc")
        latitude = loc?.latitude ?: 0.0
        longitude = loc?.longitude ?: 0.0
        address = loc?.address ?: ""

        protocol?.onLocationChanged(latitude, longitude)
    }

    interface IProtocol {
        fun onLocationChanged(latitude: Double, longitude: Double)
    }
}