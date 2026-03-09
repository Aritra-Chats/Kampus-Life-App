package com.example.kampus_life_official.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.kampus_life_official.MainActivity
import com.example.kampus_life_official.R

object NotificationHelper {
    private const val ROUTINE_FIRST_CHANNEL_ID = "routine_first_notifications"
    private const val ROUTINE_GENERAL_CHANNEL_ID = "routine_general_notifications"
    private const val ANNOUNCEMENT_CHANNEL_ID = "announcement_notifications"
    private const val SERVICE_CHANNEL_ID = "kampus_life_service"

    fun createNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val firstChannel = NotificationChannel(ROUTINE_FIRST_CHANNEL_ID, "First Class Alert", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Alarm for the first class of the day"
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            setSound(alarmSound, audioAttributes)
            enableVibration(true)
        }

        val generalChannel = NotificationChannel(ROUTINE_GENERAL_CHANNEL_ID, "Class Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Reminders for classes throughout the day"
            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            setSound(notificationSound, audioAttributes)
            enableVibration(true)
        }

        val announcementChannel = NotificationChannel(ANNOUNCEMENT_CHANNEL_ID, "Announcements", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "New campus announcements"
            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            setSound(notificationSound, audioAttributes)
            enableVibration(true)
        }

        val serviceChannel = NotificationChannel(SERVICE_CHANNEL_ID, "Monitoring Service", NotificationManager.IMPORTANCE_LOW).apply {
            description = "Ongoing service for class monitoring"
        }

        notificationManager.createNotificationChannel(firstChannel)
        notificationManager.createNotificationChannel(generalChannel)
        notificationManager.createNotificationChannel(announcementChannel)
        notificationManager.createNotificationChannel(serviceChannel)
    }

    fun showNotification(context: Context, title: String, message: String, notificationId: Int, notificationType: NotificationType, isFirstClass: Boolean = false, dataId: String? = null) {
        val channelId = when {
            notificationType == NotificationType.Announcement -> ANNOUNCEMENT_CHANNEL_ID
            isFirstClass -> ROUTINE_FIRST_CHANNEL_ID
            else -> ROUTINE_GENERAL_CHANNEL_ID
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TARGET_TAB", if (notificationType == NotificationType.Announcement) 3 else 0)
            if (notificationType == NotificationType.Announcement && dataId != null) {
                putExtra("EXPAND_NOTIFICATION_ID", dataId)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 
            notificationId, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(if (notificationType == NotificationType.Announcement) R.drawable.notification else R.drawable.routine)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (notificationType == NotificationType.Announcement) NotificationCompat.PRIORITY_DEFAULT else NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (notificationType == NotificationType.Routine && isFirstClass) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            builder.setCategory(NotificationCompat.CATEGORY_ALARM)
        } else {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            builder.setCategory(NotificationCompat.CATEGORY_EVENT)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
