package com.example.kampuslife_modulerotation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.atan2

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var bg: ImageView
    private lateinit var tvMagnetometer: TextView
    private lateinit var tvGyrometer: TextView
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotationVal = 0f
    private var lastGyroTimestamp = 0L
    private var smoothedRotation = 0f
    private var compHistory = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var histCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private val magIntervalMs = 1000L
    private val smoothingFactor = 0.1f

    private val magSampleRunnable = Runnable { requestSingleMagReading() }

    private fun normalizeAngle(angle : Float) : Float {
        var a = angle % 360f
        if(a < 0f) a += 360f
        return a
    }

    private fun shortestAngleDiff(current : Float, target : Float) : Float {
        val diff = (target - current + 540f) % 360f - 180f
        return diff
    }

    private val singleMagListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                var x= event.values[0]
                var y= event.values[1]
                var z= event.values[2]
                val headingRad = if (z < 25f) atan2(y.toDouble(), x.toDouble()) else atan2(y.toDouble(), z.toDouble())
                var headingDeg = Math.toDegrees(headingRad).toFloat()
                headingDeg = normalizeAngle(headingDeg)

                compHistory[histCount] = headingDeg

                if(histCount == compHistory.size-1) {
                    val currentHeading = normalizeAngle(rotationVal)
                    var sum = 0f
                    for (v in compHistory) {
                        sum += v
                    }
                    val avg = sum / compHistory.size
                    val diff = shortestAngleDiff(currentHeading, avg)
                    val correctionStrength = 0.05f
                    val newHeading = normalizeAngle(currentHeading + diff * correctionStrength)
                    rotationVal = newHeading
                    updateArrowRotation()
                }
                histCount = (histCount + 1) % compHistory.size
                tvMagnetometer.text = "X: $x\nY: $y\nZ: $z"
                sensorManager.unregisterListener(this)
                handler.postDelayed(magSampleRunnable, magIntervalMs)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
    }

    private fun requestSingleMagReading() {
        magnetometer?.also { magnetometer ->
            sensorManager.registerListener(singleMagListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bg = findViewById(R.id.bg)
        tvMagnetometer = findViewById(R.id.tvMagnetometer)
        tvGyrometer = findViewById(R.id.tvGyrometer)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if(gyroscope == null)
            Toast.makeText(this, "Gyrometer not available", Toast.LENGTH_SHORT).show()
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if(magnetometer == null)
            Toast.makeText(this, "Magnetometer not available", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.also { gyroscope ->
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        }
        handler.post(magSampleRunnable)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        sensorManager.unregisterListener(singleMagListener)
        handler.removeCallbacks(magSampleRunnable)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            if(lastGyroTimestamp != 0L) {
                val dt = (event.timestamp - lastGyroTimestamp) / 1_000_000_000f
                val omegaZ = event.values[2]
                val deltaDeg = Math.toDegrees(omegaZ * dt.toDouble()).toFloat()

                rotationVal = (rotationVal + deltaDeg + 360f) % 360f
                updateArrowRotation()
                tvGyrometer.text = "X: ${event.values[0]}\nY: ${event.values[1]}\nZ: ${event.values[2]}"
            }
            lastGyroTimestamp = event.timestamp
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    private fun updateArrowRotation() {
        val current = rotationVal
        val target = normalizeAngle(rotationVal)
        val diff = shortestAngleDiff(current, target)
        smoothedRotation = normalizeAngle(current + diff * smoothingFactor)
        bg.rotation = rotationVal
    }
}