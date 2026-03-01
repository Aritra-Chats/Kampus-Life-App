package com.example.module_base_ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.module_base_ui.ui.theme.Module_Base_UITheme
import com.example.module_base_ui.ui.components.NavTab
import com.example.module_base_ui.ui.components.DetailsTab
import com.example.module_base_ui.ui.pages.Routine
import com.example.module_base_ui.ui.pages.StudentTeacherList
import com.example.module_base_ui.ui.pages.LoginScreen
import com.example.feature_auth.AuthSession
import com.example.feature_auth.GoogleAuthManager
import com.example.feature_auth.UserRole

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val authManager by lazy { GoogleAuthManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module_Base_UITheme {
                val currentUser by AuthSession.currentUser.collectAsState()
                val isRestoring by AuthSession.isRestoringSession.collectAsState()

                // Restore session on first launch
                LaunchedEffect(Unit) {
                    AuthSession.tryRestoreSession(authManager)
                }

                when {
                    isRestoring -> {
                        // ---------- Loading splash ----------
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Loading...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    currentUser == null -> {
                        // ---------- Auth gate ----------
                        LoginScreen(
                            activity = this@MainActivity,
                            authManager = authManager,
                            onAuthSuccess = { user ->
                                AuthSession.setUser(user)
                            }
                        )
                    }
                    else -> {
                        // ---------- Main app ----------
                        var selectedIndex by remember { mutableIntStateOf(2) }
                        var selectedCategory by remember { mutableStateOf(0) }
                        val designation = when (currentUser?.role) {
                            UserRole.TEACHER -> "Teacher"
                            UserRole.STUDENT -> "Student"
                            else -> "Unknown"
                        }
                        val department = currentUser?.department ?: "CSE-26"
                        MainScreen(
                            selectedIndex = selectedIndex,
                            selectedCategory = selectedCategory,
                            designation = designation,
                            department = department,
                            onIndexSelected = { selectedIndex = it },
                            onCategorySelected = { selectedCategory = it }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    selectedIndex: Int,
    selectedCategory: Int,
    designation: String,
    department: String,
    onIndexSelected: (Int) -> Unit,
    onCategorySelected: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            DetailsTab(
                selectedIndex = selectedIndex,
                selectedCategory = selectedCategory,
                designation = designation,
                onCategorySelected = onCategorySelected
            )
        },
        bottomBar = { NavTab(selectedIndex = selectedIndex, onSelect = onIndexSelected) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CurrentTab(selectedIndex, selectedCategory, designation, department)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentTab(selectedIndex: Int, selectedCategory: Int, designation: String, department: String) {
    when (selectedIndex) {
        0 -> Routine(designation, department)
        1 -> StudentTeacherList(designation, selectedCategory)
        // 2 -> Map()
        // 3 -> Notification()
        // 4 -> Profile()
    }
}
