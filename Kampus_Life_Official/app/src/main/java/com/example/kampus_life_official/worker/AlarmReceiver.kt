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
            "CHECK_DAILY_CLASSES" -> checkAndScheduleFirstClassAlarm(context)
            "START_CLASS_MONITOR" -> {
                val serviceIntent = Intent(context, ClassCheckService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }

    private fun checkAndScheduleFirstClassAlarm(context: Context) {
        val user = LocalStorage.loadUser(context) ?: return
        val routineData = LocalStorage.loadData(context, LocalStorage.KEY_ROUTINE, object : TypeToken<List<Routine>>() {})
        
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

        if (calendar.timeInMillis <= System.currentTimeMillis()) return

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
}
