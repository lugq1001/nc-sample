package com.nextcont.mobilization.ui.trains

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.PolylineOptions
import com.nextcont.mobilization.R
import com.nextcont.mobilization.model.Training
import com.nextcont.mobilization.service.LocationService
import com.nextcont.mobilization.util.DialogUtil
import com.squareup.moshi.Moshi
import timber.log.Timber


class TrainingActivity : AppCompatActivity(), LocationService.IProtocol {

    companion object {
        const val KEY_TRAIN = "train"
    }

    private lateinit var iMapView: MapView
    private lateinit var aMap: AMap

    private lateinit var iStartButton: Button
    private lateinit var iPauseButton: Button
    private lateinit var iStopButton: Button

    private lateinit var iTimingText: Chronometer

    private var started = false
    private var paused = false

    private var timeDifference: Long = 0 // 记录下来的总时间

    private var tracks = mutableListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        val json = Moshi.Builder().build()
        val jsonAdapter = json.adapter(Training::class.java)

        val train = jsonAdapter.fromJson(intent.getStringExtra(KEY_TRAIN)!!)!!

        title = train.title

        iStartButton = findViewById(R.id.iStartButton)
        iStartButton.setOnClickListener {
            startTraining()
        }
        iPauseButton = findViewById(R.id.iPauseButton)
        iPauseButton.setOnClickListener {
            pauseTraining()
        }

        iStopButton = findViewById(R.id.iStopButton)
        iStopButton.setOnClickListener {
            stopTraining()
        }

        iTimingText = findViewById(R.id.iTimingText)

        iMapView = findViewById(R.id.iMapView)
        iMapView.onCreate(savedInstanceState)

        aMap = iMapView.map
        val locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        aMap.myLocationStyle = locationStyle
        aMap.uiSettings.isScaleControlsEnabled = true
        aMap.uiSettings.isMyLocationButtonEnabled = true // 设置默认定位按钮是否显示
        aMap.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        aMap.moveCamera(CameraUpdateFactory.zoomTo(19f))

        initView()

        LocationService.addDelegate(this)
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
        LocationService.removeDelegate()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        iMapView.onSaveInstanceState(outState)
    }

    private fun initView() {
        iPauseButton.visibility = View.INVISIBLE
        iStartButton.visibility = View.VISIBLE
        iStopButton.visibility = View.INVISIBLE
        iTimingText.visibility = View.INVISIBLE
    }

    private fun startTraining() {
        disableButton(iStartButton, true)
        disableButton(iPauseButton, false)
        disableButton(iStopButton, false)
        iTimingText.visibility = View.VISIBLE
        iStartButton.visibility = View.VISIBLE
        iStopButton.visibility = View.VISIBLE
        iPauseButton.visibility = View.VISIBLE


        if (LocationService.latitude > 0.0 && LocationService.longitude > 0.0) {
            var lat = LocationService.latitude
            var lon = LocationService.longitude
            for (index in 0..10) {
                tracks.add(LatLng(lat, lon))
                lat += 0.00002
                lon += 0.00002
            }
            aMap.addPolyline(
                PolylineOptions().addAll(tracks).setDottedLine(false).width(15f)
                    .color(ContextCompat.getColor(this, R.color.primary))
            )
        }

        iTimingText.base = SystemClock.elapsedRealtime()
        iTimingText.start()

        started = true

    }

    private fun pauseTraining() {
        paused = !paused
        disableButton(iStartButton, true)
        disableButton(iPauseButton, false)
        disableButton(iStopButton, false)
        iTimingText.visibility = View.VISIBLE
        iStartButton.visibility = View.VISIBLE
        iStopButton.visibility = View.VISIBLE
        iPauseButton.visibility = View.VISIBLE
        if (paused) {
            iTimingText.stop()
            timeDifference = iTimingText.base - SystemClock.elapsedRealtime()
            iPauseButton.text = "继续"
        } else {
            iPauseButton.text = "暂停"
            iTimingText.base = SystemClock.elapsedRealtime() + timeDifference
            iTimingText.start()
        }
    }

    private fun stopTraining() {
        DialogUtil.showConfirm(this, "是否完成当前训练？", "已完成", action = {
            iTimingText.stop()
            started = false
            finish()
        })
    }

    private fun disableButton(button: Button, disable: Boolean) {
        if (disable) {
            //button.isEnabled = false
            button.setBackgroundResource(R.drawable.bg_trains_btn_disable)
        } else {
            //button.isEnabled = true
            val bg = when (button.id) {
                R.id.iStartButton -> R.drawable.bg_trains_btn_start
                R.id.iStopButton -> R.drawable.bg_trains_btn_stop
                R.id.iPauseButton -> R.drawable.bg_trains_btn_pause
                else -> R.drawable.bg_trains_btn_disable
            }
            button.setBackgroundResource(bg)
        }

    }


    override fun onLocationChanged(latitude: Double, longitude: Double) {
        Timber.i("=======${latitude} ==========${longitude}")
        if (latitude == 0.0 || longitude == 0.0 || paused || !started) {
            return
        }
        aMap.addPolyline(
            PolylineOptions().add(LatLng(latitude, longitude)).setDottedLine(false).width(15f)
                .color(ContextCompat.getColor(this, R.color.primary))
        )
    }


}
