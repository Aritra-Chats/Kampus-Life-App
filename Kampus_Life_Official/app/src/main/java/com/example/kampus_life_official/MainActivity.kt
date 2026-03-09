package com.example.kampus_life_official

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.kampus_life_official.data_insertion.AdministrationList
import com.example.kampus_life_official.data_insertion.Holiday
import com.example.kampus_life_official.data_insertion.LocalStorage
import com.example.kampus_life_official.data_insertion.MentorList
import com.example.kampus_life_official.data_insertion.Notification
import com.example.kampus_life_official.data_insertion.RetrofitClient
import com.example.kampus_life_official.data_insertion.Routine
import com.example.kampus_life_official.data_insertion.StudentList
import com.example.kampus_life_official.data_insertion.TeacherList
import com.example.kampus_life_official.data_insertion.checkHolidayData
import com.example.kampus_life_official.login.AuthSession
import com.example.kampus_life_official.login.AuthUser
import com.example.kampus_life_official.ui.theme.Kampus_Life_OfficialTheme
import com.example.kampus_life_official.ui.components.NavTab
import com.example.kampus_life_official.ui.components.DetailsTab
import com.example.kampus_life_official.ui.pages.Notification as NotificationPage
import com.example.kampus_life_official.ui.pages.Profile
import com.example.kampus_life_official.ui.pages.Routine as RoutinePage
import com.example.kampus_life_official.ui.pages.StudentTeacherList
import com.google.gson.reflect.TypeToken
import com.example.kampus_life_official.login.GoogleAuthManager
import com.example.kampus_life_official.login.UserRole
import com.example.kampus_life_official.ui.pages.LoginScreen
import com.example.kampus_life_official.ui.theme.RestoreSessionAnimation

class MainActivity : ComponentActivity() {
    private val authManager by lazy { GoogleAuthManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kampus_Life_OfficialTheme {
                val context = LocalContext.current
                val currentUser by AuthSession.currentUser.collectAsState()
                val isRestoring by AuthSession.isRestoringSession.collectAsState()

                // Permission handling
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    AuthSession.tryRestoreSession(this@MainActivity, authManager)
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    }
                }

                when {
                    isRestoring -> RestoreSessionAnimation()
                    currentUser == null -> LoginScreen(this, authManager) { user -> AuthSession.setUser(this, user) }
                    else -> {
                        val targetTab = intent.getIntExtra("TARGET_TAB", 0)
                        val expandId = intent.getStringExtra("EXPAND_NOTIFICATION_ID")
                        
                        var selectedIndex by remember { mutableIntStateOf(targetTab) }
                        val tabCategories = remember { mutableStateMapOf<Int, Int>() }
                        val currentCategory = tabCategories[selectedIndex] ?: 0
                        
                        MainScreen(
                            selectedIndex = selectedIndex, 
                            selectedCategory = currentCategory, 
                            currentUser = currentUser!!, 
                            expandNotificationId = expandId,
                            onIndexSelected = { selectedIndex = it }, 
                            onCategorySelected = { category -> tabCategories[selectedIndex] = category }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

private suspend fun loadStudentData(): List<StudentList> = RetrofitClient.api.getStudentList()
private suspend fun loadTeacherData(): List<TeacherList> = RetrofitClient.api.getTeacherList()
private suspend fun loadMentorData(): List<MentorList> = RetrofitClient.api.getMentorList()
private suspend fun loadAdministrationData(): List<AdministrationList> = RetrofitClient.api.getAdministrationList()
private suspend fun loadRoutineData(): List<Routine> = RetrofitClient.api.getRoutine()
private suspend fun loadNotifications(): List<Notification> = RetrofitClient.api.getNotifications()
private suspend fun loadHolidays(): List<Holiday> = RetrofitClient.api.getHolidays()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(selectedIndex: Int, selectedCategory: Int, currentUser: AuthUser, expandNotificationId: String? = null, onIndexSelected: (Int) -> Unit, onCategorySelected: (Int) -> Unit) {
    val context = LocalContext.current
    val designation = when (currentUser.role) {
        UserRole.STUDENT -> "Student"
        UserRole.TEACHER -> "Teacher"
        else -> ""
    }
    var teacherData by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_TEACHERS, object : TypeToken<List<TeacherList>>() {})) }
    var routineData by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_ROUTINE, object : TypeToken<List<Routine>>() {})) }
    var studentData by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_STUDENTS, object : TypeToken<List<StudentList>>() {})) }
    var mentorData by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_MENTORS, object : TypeToken<List<MentorList>>() {})) }
    var administrationData by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_ADMIN, object : TypeToken<List<AdministrationList>>() {})) }
    var notifications by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_NOTIFICATIONS, object : TypeToken<List<Notification>>() {})) }
    var holidays by remember { mutableStateOf(LocalStorage.loadData(context, LocalStorage.KEY_HOLIDAYS, object : TypeToken<List<Holiday>>() {})) }

    LaunchedEffect(Unit) {
        try {
            val fetchedRoutineData = loadRoutineData()
            if (fetchedRoutineData != routineData) {
                routineData = fetchedRoutineData
                LocalStorage.saveData(context, LocalStorage.KEY_ROUTINE, fetchedRoutineData)
            }

            val fetchedTeacherData = loadTeacherData()
            if (fetchedTeacherData != teacherData) {
                teacherData = fetchedTeacherData
                LocalStorage.saveData(context, LocalStorage.KEY_TEACHERS, fetchedTeacherData)
            }

            val fetchedStudentData = loadStudentData()
            if (fetchedStudentData != studentData) {
                studentData = fetchedStudentData
                LocalStorage.saveData(context, LocalStorage.KEY_STUDENTS, fetchedStudentData)
            }

            val fetchedMentorData = loadMentorData()
            if (fetchedMentorData != mentorData) {
                fetchedMentorData.let {
                    mentorData = it
                    LocalStorage.saveData(context, LocalStorage.KEY_MENTORS, it)
                }
            }

            val fetchedAdministrationData = loadAdministrationData()
            if (fetchedAdministrationData != administrationData) {
                administrationData = fetchedAdministrationData
                LocalStorage.saveData(context, LocalStorage.KEY_ADMIN, fetchedAdministrationData)
            }

            val fetchedNotifications = loadNotifications()
            if (fetchedNotifications != notifications) {
                notifications = fetchedNotifications
                LocalStorage.saveData(context, LocalStorage.KEY_NOTIFICATIONS, fetchedNotifications)
            }

            val fetchedHolidays = loadHolidays()
            if (fetchedHolidays != holidays) {
                holidays = fetchedHolidays
                LocalStorage.saveData(context, LocalStorage.KEY_HOLIDAYS, fetchedHolidays)
            }
        } catch (_: Exception) { }
    }
    val studentDetails = remember(studentData, currentUser.rollNumber, designation) {
        if (designation != "Student") return@remember null
        val roll = currentUser.rollNumber?.toIntOrNull()
        studentData.find { it.roll == roll }
    }
    val teacherDetails = remember(teacherData, currentUser.displayName, designation) {
        if (designation != "Teacher") return@remember null
        teacherData.find { it.name == currentUser.displayName }
    }
    val sections = teacherDetails?.sections ?: emptyList()
    checkHolidayData(holidays)
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent,
        topBar = { DetailsTab(selectedIndex = selectedIndex, selectedCategory = selectedCategory, studentSection = studentDetails?.section, teacherSections = sections, designation = designation, routineData = routineData, holidayData = holidays, onCategorySelected = onCategorySelected) },
        bottomBar = { NavTab(selectedIndex = selectedIndex, onSelect = onIndexSelected) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CurrentTab(selectedIndex, selectedCategory, designation, currentUser, studentDetails, teacherDetails, routineData, studentData, teacherData, mentorData, administrationData, notifications, holidays, expandNotificationId)
        }
    }
}

@Composable
fun CurrentTab(selectedIndex: Int, selectedCategory: Int, designation: String, currentUser: AuthUser, studentDetails: StudentList?, teacherDetails: TeacherList?, routineData: List<Routine>, studentData: List<StudentList>, teacherData: List<TeacherList>, mentorData: List<MentorList>, administrationData: List<AdministrationList>, notifications: List<Notification>, holidayData: List<Holiday>, expandNotificationId: String? = null) {
    if (designation == "Student") {
        when (selectedIndex) {
            0 -> RoutinePage(designation, studentDetails?.section, null, null, routineData, holidayData)
            1 -> StudentTeacherList(currentUser.rollNumber?.toIntOrNull(), designation, selectedCategory, studentDetails?.section, null, null, teacherData, mentorData, administrationData, routineData, holidayData)
            3 -> NotificationPage(selectedCategory, notifications, expandNotificationId)
            4 -> Profile(currentUser, designation, studentDetails, null)
        }
    }
    else if (designation == "Teacher") {
        when (selectedIndex) {
            0 -> RoutinePage(designation, null, teacherDetails?.sections, teacherDetails?.name, routineData, holidayData)
            1 -> StudentTeacherList(null, designation, selectedCategory, null, teacherDetails?.sections, studentData, null, null, administrationData, null, null)
            3 -> NotificationPage(selectedCategory, notifications, expandNotificationId)
            4 -> Profile(currentUser, designation, null, teacherDetails)
        }
    }
}
