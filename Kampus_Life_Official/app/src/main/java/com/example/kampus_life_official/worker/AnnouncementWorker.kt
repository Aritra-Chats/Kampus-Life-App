package com.example.kampus_life_official.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kampus_life_official.data_insertion.LocalStorage
import com.example.kampus_life_official.data_insertion.Notification
import com.example.kampus_life_official.data_insertion.RetrofitClient
import com.google.gson.reflect.TypeToken

class AnnouncementWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if(LocalStorage.loadUser(applicationContext) == null) return Result.success()
        return try {
            val remoteNotifications = RetrofitClient.api.getNotifications()
            val localNotifications = LocalStorage.loadData(applicationContext, LocalStorage.KEY_NOTIFICATIONS, object : TypeToken<List<Notification>>() {})

            if (remoteNotifications.size > localNotifications.size) {
                val newOnes = remoteNotifications.filter { remote -> localNotifications.none { it.id == remote.id } }
                newOnes.forEach { announcement ->
                    NotificationHelper.showNotification(
                        applicationContext,
                        announcement.subject ?: "New Announcement",
                        announcement.body ?: "",
                        announcement.hashCode(),
                        notificationType = NotificationType.Announcement
                    )
                }
                LocalStorage.saveData(applicationContext, LocalStorage.KEY_NOTIFICATIONS, remoteNotifications)
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
