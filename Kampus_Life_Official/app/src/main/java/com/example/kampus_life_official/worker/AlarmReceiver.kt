package com.example.kampus_life_official.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.kampus_life_official.data_insertion.LocalStorage
import com.example.kampus_life_official.data_insertion.Routine
import com.example.kampus_life_official.data_insertion.StudentList
import com.example.kampus_life_official.login.UserRole
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, "CHECK_DAILY_CLASSES" -> {
                checkAndScheduleFirstClassAlarm(context)
            }
            "START_CLASS_MONITOR" -> {
                val serviceIntent = Intent(context, ClassCheckService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }

    private fun checkAndScheduleFirstClassAlarm(context: Context) {
        val user = LocalStorage.loadUser(context) ?: return
        val routineData = LocalStorage.loadData(context, LocalStorage.KEY_ROUTINE, object : TypeToken<List<Routine>>() {})
        
        // If routine data is empty, it might be a network issue or first run. Retry in 30 mins.
        if (routineData.isEmpty()) {
            scheduleRetry(context)
            return
        }

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE"))
        
        val filteredRoutines = when (user.role) {
            UserRole.STUDENT -> {
                val students = LocalStorage.loadData(context, LocalStorage.KEY_STUDENTS, object : TypeToken<List<StudentList>>() {})
                val roll = user.rollNumber?.toIntOrNull()
                val student = students.find { it.roll == roll }
                routineData.filter { it.section == student?.section && it.day == today }
            }
            UserRole.TEACHER -> routineData.filter { it.teacher == user.displayName && it.day == today }
            else -> emptyList()
        }

        if (filteredRoutines.isEmpty()) return

        val firstClass = filteredRoutines.minByOrNull { routine ->
            val startTimeStr = routine.time?.split("-")?.firstOrNull()?.trim() ?: "23:59"
            try { LocalTime.parse(startTimeStr) } catch (_: Exception) { LocalTime.MAX }
        } ?: return

        val startTimeStr = firstClass.time?.split("-")?.firstOrNull()?.trim() ?: return
        val startTime = try { LocalTime.parse(startTimeStr) } catch (_: Exception) { return }
        val alarmTime = startTime.minusMinutes(30)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmTime.hour)
            set(Calendar.MINUTE, alarmTime.minute)
            set(Calendar.SECOND, 0)
        }

        // If alarm time for today has already passed, we don't schedule it for today.
        // However, if the first class is currently or soon, we might want to start monitor immediately.
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            val now = LocalTime.now()
            if (now.isBefore(startTime)) {
                // Class hasn't started yet but it's within 30 mins. Start service.
                val serviceIntent = Intent(context, ClassCheckService::class.java)
                context.startForegroundService(serviceIntent)
            }
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val startIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "START_CLASS_MONITOR"
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 1, startIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun scheduleRetry(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val retryIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "CHECK_DAILY_CLASSES"
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        val retryTime = System.currentTimeMillis() + (30 * 60 * 1000) // 30 mins
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, retryTime, pendingIntent)
    }
}
