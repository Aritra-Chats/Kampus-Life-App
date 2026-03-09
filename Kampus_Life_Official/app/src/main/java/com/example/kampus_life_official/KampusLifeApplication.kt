package com.example.kampus_life_official

import android.app.Application
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.work.*
import com.example.kampus_life_official.worker.AlarmReceiver
import com.example.kampus_life_official.worker.AnnouncementWorker
import com.example.kampus_life_official.worker.NotificationHelper
import java.util.Calendar
import java.util.concurrent.TimeUnit

class KampusLifeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        scheduleMidnightCheck()
        scheduleAnnouncementCheck()
    }

    private fun scheduleMidnightCheck() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply { action = "CHECK_DAILY_CLASSES" }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) { add(Calendar.DAY_OF_YEAR, 1) }
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    private fun scheduleAnnouncementCheck() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val announcementRequest = PeriodicWorkRequestBuilder<AnnouncementWorker>(15, TimeUnit.MINUTES).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("AnnouncementCheck", ExistingPeriodicWorkPolicy.KEEP, announcementRequest)
    }
}
