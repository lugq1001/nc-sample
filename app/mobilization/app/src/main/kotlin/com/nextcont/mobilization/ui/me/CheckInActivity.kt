package com.nextcont.mobilization.ui.me

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.MyLocationStyle
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.CheckIn
import com.nextcont.mobilization.service.LocationService
import com.nextcont.mobilization.util.DialogUtil

class CheckInActivity : AppCompatActivity() {

    private lateinit var iMapView: MapView
    private lateinit var aMap: AMap

    private lateinit var iCheckInButton: Button
    private lateinit var iCheckInRecordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        title = getString(R.string.title_check_in)

        iCheckInButton = findViewById(R.id.iCheckInButton)
        iCheckInButton.setOnClickListener {
            checkIn()
        }
        iCheckInRecordButton = findViewById(R.id.iCheckInRecordButton)
        iCheckInRecordButton.setOnClickListener {
            checkInRecord()
        }

        iMapView = findViewById(R.id.iMapView)
        iMapView.onCreate(savedInstanceState)

        aMap = iMapView.map
        val locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        aMap.myLocationStyle = locationStyle
        aMap.uiSettings.isScaleControlsEnabled = true
        aMap.uiSettings.isMyLocationButtonEnabled = true // 设置默认定位按钮是否显示
        aMap.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        aMap.moveCamera(CameraUpdateFactory.zoomTo(16f))

        if (CheckIn.hasCheckIn) {
            iCheckInButton.text = getString(R.string.check_in_expired)
            iCheckInButton.isEnabled = false
        }

    }

    override fun onResume() {
        super.onResume()
        iMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        iMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        iMapView.onDestroy()

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        iMapView.onSaveInstanceState(outState)
    }

    private fun checkIn() {
        val checkIn = CheckIn(LocationService.address, System.currentTimeMillis())
        checkIn.save()
        DialogUtil.showAlert(this, getString(R.string.check_in_success))
        iCheckInButton.text = getString(R.string.check_in_expired)
        iCheckInButton.isEnabled = false
    }

    private fun checkInRecord() {
        startActivity(Intent(this, CheckInRecordActivity::class.java))
    }

}
