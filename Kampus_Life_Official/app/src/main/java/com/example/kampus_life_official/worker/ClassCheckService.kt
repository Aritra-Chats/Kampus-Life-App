package com.example.kampus_life_official.worker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.LocalStorage
import com.example.kampus_life_official.data_insertion.Routine
import com.example.kampus_life_official.data_insertion.StudentList
import com.example.kampus_life_official.login.UserRole
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClassCheckService : Service() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "kampus_life_notifications")
            .setContentTitle("Kampus Life Monitoring")
            .setContentText("Checking for classes and announcements...")
            .setSmallIcon(R.drawable.logo)
            .setSilent(true)
            .build()
        
        startForeground(101, notification)
        
        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        serviceScope.launch {
            while (isActive) {
                checkClasses()
                if (isFinalClassStarted()) {
                    stopSelf()
                    break
                }
                delay(5 * 60 * 1000)
            }
        }
    }

    private fun checkClasses() {
        val user = LocalStorage.loadUser(this@ClassCheckService) ?: return
        val routineData = LocalStorage.loadData(this@ClassCheckService, LocalStorage.KEY_ROUTINE, object : TypeToken<List<Routine>>() {})
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE"))

        val filteredRoutines = when (user.role) {
            UserRole.STUDENT -> {
                val students = LocalStorage.loadData(this@ClassCheckService, LocalStorage.KEY_STUDENTS, object : TypeToken<List<StudentList>>() {})
                val roll = user.rollNumber?.toIntOrNull()
                val student = students.find { it.roll == roll }
                routineData.filter { it.section == student?.section && it.day == today }
            }
            UserRole.TEACHER -> {
                routineData.filter { it.teacher == user.displayName && it.day == today }
            }
            else -> emptyList()
        }

        if (filteredRoutines.isEmpty()) return

        val firstClass = filteredRoutines.minByOrNull { routine ->
            val startTimeStr = routine.time?.split("-")?.firstOrNull()?.trim() ?: "23:59"
            try { LocalTime.parse(startTimeStr) } catch (_: Exception) { LocalTime.MAX }
        }

        val now = LocalTime.now()
        filteredRoutines.forEach { routine ->
            val startTimeStr = routine.time?.split("-")?.firstOrNull()?.trim() ?: return@forEach
            val startTime = LocalTime.parse(startTimeStr)

            val minutesUntilClass = java.time.Duration.between(now, startTime).toMinutes()
            if (minutesUntilClass in 11..15) {
                val isFirst = (routine.id == firstClass?.id)
                NotificationHelper.showNotification(
                    this@ClassCheckService,
                    "Upcoming Lecture",
                    "${routine.subject} lecture is in ${routine.classroom} from ${routine.time}, Please join!",
                    routine.hashCode(),
                    notificationType = NotificationType.Routine,
                    isFirstClass = isFirst
                )
            }
        }
    }

    private fun isFinalClassStarted(): Boolean {
        val user = LocalStorage.loadUser(this) ?: return true
        val routineData = LocalStorage.loadData(this, LocalStorage.KEY_ROUTINE, object : TypeToken<List<Routine>>() {})
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE"))
        
        val filteredRoutines = when (user.role) {
            UserRole.STUDENT -> {
                val students = LocalStorage.loadData(this, LocalStorage.KEY_STUDENTS, object : TypeToken<List<StudentList>>() {})
                val student = students.find { it.roll == user.rollNumber?.toIntOrNull() }
                routineData.filter { it.section == student?.section && it.day == today }
            }
            UserRole.TEACHER -> {
                routineData.filter { it.teacher == user.displayName && it.day == today }
            }
            else -> emptyList()
        }
        
        if (filteredRoutines.isEmpty()) return true
        
        val lastClass = filteredRoutines.maxByOrNull { routine ->
            val startTimeStr = routine.time?.split("-")?.firstOrNull()?.trim() ?: "00:00"
            LocalTime.parse(startTimeStr)
        } ?: return true
        
        val lastStartTimeStr = lastClass.time?.split("-")?.firstOrNull()?.trim() ?: return true
        val lastStartTime = LocalTime.parse(lastStartTimeStr)
        
        return LocalTime.now().isAfter(lastStartTime)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
