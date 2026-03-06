package com.example.kampus_life_official.ui.pages

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kampus_life_official.login.AuthUser
import com.example.kampus_life_official.login.GoogleAuthManager
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.LocalStorage
import com.example.kampus_life_official.loadAdministrationData
import com.example.kampus_life_official.loadHolidays
import com.example.kampus_life_official.loadMentorData
import com.example.kampus_life_official.loadNotifications
import com.example.kampus_life_official.loadRoutineData
import com.example.kampus_life_official.loadStudentData
import com.example.kampus_life_official.loadTeacherData
import com.example.kampus_life_official.login.UserRole
import com.example.kampus_life_official.ui.theme.ShadedPanel
import com.example.kampus_life_official.ui.theme.GoogleLoginButton
import com.example.kampus_life_official.ui.theme.responsiveDp
import com.example.kampus_life_official.ui.theme.responsiveSp

@Composable
fun LoginScreen(activity: Activity, authManager: GoogleAuthManager, onAuthSuccess: (AuthUser) -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(isLoading) {
        if (!isLoading) return@LaunchedEffect
        
        val result = authManager.signIn(activity)
        
        result.onSuccess { user ->
            try {
                // 1. Fetch Student/Teacher Data first to verify registration
                val fetchedStudentData = loadStudentData()
                val fetchedTeacherData = loadTeacherData()
                
                val userEmail = user.email.lowercase().trim()
                val rollInt = user.rollNumber?.toIntOrNull()

                // Robust matching: Try matching by email first, then by roll number
                val userInDb = when (user.role) {
                    UserRole.STUDENT -> {
                        fetchedStudentData.find {
                            it.email?.lowercase()?.trim() == userEmail || (rollInt != null && it.roll == rollInt)
                        }
                    }
                    UserRole.TEACHER -> {
                        fetchedTeacherData.find {
                            it.email?.lowercase()?.trim() == userEmail
                        }
                    }
                    else -> null
                }

                if (userInDb == null) {
                    throw Exception("Account ($userEmail) not registered in database. Contact Support.")
                }

                // 2. Save Basic Data
                LocalStorage.saveData(context, LocalStorage.KEY_STUDENTS, fetchedStudentData)
                LocalStorage.saveData(context, LocalStorage.KEY_TEACHERS, fetchedTeacherData)

                // 3. Fetch all other required data sequentially
                val fetchedRoutineData = loadRoutineData()
                LocalStorage.saveData(context, LocalStorage.KEY_ROUTINE, fetchedRoutineData)

                val fetchedMentorData = loadMentorData()
                LocalStorage.saveData(context, LocalStorage.KEY_MENTORS, fetchedMentorData)

                val fetchedAdministrationData = loadAdministrationData()
                LocalStorage.saveData(context, LocalStorage.KEY_ADMIN, fetchedAdministrationData)

                val fetchedNotifications = loadNotifications()
                LocalStorage.saveData(context, LocalStorage.KEY_NOTIFICATIONS, fetchedNotifications)

                val fetchedHolidays = loadHolidays()
                LocalStorage.saveData(context, LocalStorage.KEY_HOLIDAYS, fetchedHolidays)

                // 4. Data synced successfully, move to Main
                isLoading = false
                onAuthSuccess(user)
                
            } catch (e: Exception) {
                isLoading = false
                error = e.localizedMessage ?: "Sync failed"
            }
        }
        
        result.onFailure { throwable ->
            isLoading = false
            error = throwable.localizedMessage ?: "Sign-in failed" 
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        ShadedPanel(modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.88f).fillMaxHeight(0.65f))
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(40.dp)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Kampus Life Logo", modifier = Modifier.widthIn(max = responsiveDp(260.dp)), contentScale = ContentScale.Fit)
            Spacer(Modifier.height(responsiveDp(48.dp)))
            
            if (isLoading) { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White.copy(alpha = 0.7f), strokeWidth = responsiveDp(2.dp), modifier = Modifier.size(responsiveDp(32.dp)))
                    Spacer(Modifier.height(16.dp))
                    Text("Syncing data...", color = Color.White.copy(alpha = 0.7f), fontSize = responsiveSp(14.sp))
                }
            } else { 
                GoogleLoginButton(onClick = {
                    isLoading = true
                    error = null 
                }, enabled = true)
            }
            
            error?.let {
                Spacer(Modifier.height(responsiveDp(20.dp)))
                Text(text = it, color = Color(0xFFFF6B6B), fontSize = responsiveSp(13.sp), modifier = Modifier.alpha(0.85f), textAlign = TextAlign.Center)
            }
        }
    }
}
