package com.iat359.biteme.kotlin

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import com.iat359.biteme.R
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    private lateinit var sensorManager : SensorManager
    private var acelVal = SensorManager.GRAVITY_EARTH
    private var acelLast = SensorManager.GRAVITY_EARTH
    private var shake = 0.00f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        startButton.setOnClickListener{
            startActivity(Intent(this, SwipeActivity::class.java))
        }
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            acelLast = acelVal
            acelVal = Math.sqrt((x * x).toDouble() + y * y + z * z).toFloat()
            val delta: Float = acelVal - acelLast
            shake = shake * 0.9f + delta
            if (shake > 2) {
                startActivity(Intent(this@StartActivity, SwipeActivity::class.java))
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}