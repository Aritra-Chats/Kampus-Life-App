package com.example.kampuslife_modulerotation

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var bg: ImageView
    private lateinit var tvMagnetometer: TextView
    private lateinit var tvGyrometer: TextView
    private lateinit var loadingLayout: LinearLayout

    private var rotationVectorSensor: Sensor? = null
    private var pedometerSensor: Sensor? = null
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var initialStepCount: Float? = null
    private var lastStepValue: Int = 0
    private var stepsSinceMapResume: Int = 0
    private var rotationVal = 0f
    private val smoothingFactor = 0.1f
    private val ACTIVITY_RECOGNITION_REQ = 1001
    private val MAP_MOVEMENT_SCALE = 50f
    private val sensorsToInitialize = mutableSetOf<Int>()
    private val sensorTimeoutHandler = Handler(Looper.getMainLooper())
    private val sensorTimeoutRunnable = Runnable {
        if (::loadingLayout.isInitialized && loadingLayout.isVisible) {
            loadingLayout.visibility = View.GONE
            Toast.makeText(this, "Sensor initialization timed out. Some sensors may not be active.", Toast.LENGTH_LONG).show()
        }
    }

    private fun ensureActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    ACTIVITY_RECOGNITION_REQ
                )
            }
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
        ensureActivityRecognitionPermission()

        bg = findViewById(R.id.bg)
        tvMagnetometer = findViewById(R.id.tvMagnetometer)
        tvGyrometer = findViewById(R.id.tvGyrometer)
        loadingLayout = findViewById(R.id.loading_layout)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor == null)
            Toast.makeText(this, "Rotation Vector Sensor not available", Toast.LENGTH_SHORT).show()
        pedometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (pedometerSensor == null)
            Toast.makeText(this, "Pedometer Sensor not available", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        sensorsToInitialize.clear()
        
        rotationVectorSensor?.also { sensor -> sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME) }
        pedometerSensor?.also { sensor -> sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME) }
        
        if (sensorsToInitialize.isNotEmpty()) {
            loadingLayout.visibility = View.VISIBLE
            sensorTimeoutHandler.postDelayed(sensorTimeoutRunnable, 10000)
        } else {
            loadingLayout.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        initialStepCount = null
        sensorManager.unregisterListener(this)
        sensorTimeoutHandler.removeCallbacks(sensorTimeoutRunnable)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (sensorsToInitialize.contains(event.sensor.type)) {
            sensorsToInitialize.remove(event.sensor.type)
            if (sensorsToInitialize.isEmpty()) {
                loadingLayout.visibility = View.GONE
                sensorTimeoutHandler.removeCallbacks(sensorTimeoutRunnable)
            }
        }
        var azimuthRad = 0f
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            azimuthRad = orientationAngles[0]
            var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
            azimuthDeg = normalizeAngle(azimuthDeg)
            val pitchDeg = Math.toDegrees(orientationAngles[1].toDouble()).toInt()
            if(abs(pitchDeg) < 80)
                rotationVal = azimuthDeg
            updateArrowRotation()
            tvMagnetometer.text = "Compass: ${azimuthDeg.toInt()}°"
        }
        if(event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceBoot = event.values[0]
            if(initialStepCount == null)
                initialStepCount = totalStepsSinceBoot
            stepsSinceMapResume = (totalStepsSinceBoot - (initialStepCount ?: totalStepsSinceBoot)).toInt()
            val stepDiff = (stepsSinceMapResume - lastStepValue)
            if (stepDiff > 0)
                calculateMovementDirection(stepDiff, azimuthRad)
            tvGyrometer.text = "Pedometer: $stepsSinceMapResume"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    private fun calculateMovementDirection(stepDiff : Int, angleRad : Float) {
        val moveX = kotlin.math.sin(angleRad).toFloat() * MAP_MOVEMENT_SCALE * stepDiff * 0.5f
        val moveY = kotlin.math.cos(angleRad).toFloat() * MAP_MOVEMENT_SCALE * stepDiff * 0.5f
        bg.animate().translationXBy(-moveX).translationYBy(moveY).setDuration(200).start()
        lastStepValue = stepsSinceMapResume
    }
    private fun updateArrowRotation() {
        val current = bg.rotation
        val target = normalizeAngle(-rotationVal)
        val diff = shortestAngleDiff(current, target)
        val newRotation =  current + diff * smoothingFactor
        bg.rotation = newRotation
    }
    private fun normalizeAngle(angle: Float): Float {
        return (angle + 360f) % 360f
    }
    private fun shortestAngleDiff(current: Float, target: Float): Float {
        val diff = (target - current + 540f) % 360f - 180f
        return diff
    }
}
