package com.example.module_base_ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.module_base_ui.ui.theme.Module_Base_UITheme
import com.example.module_base_ui.ui.components.NavTab
import com.example.module_base_ui.ui.components.DetailsTab
import com.example.module_base_ui.ui.pages.Routine
import com.example.module_base_ui.ui.pages.StudentTeacherList

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module_Base_UITheme {
                var selectedIndex by remember { mutableIntStateOf(2) }
                var selectedCategory by remember { mutableStateOf(0) }
                MainScreen(selectedIndex = selectedIndex, selectedCategory = selectedCategory, onIndexSelected = { selectedIndex = it }, onCategorySelected = { selectedCategory = it })
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(selectedIndex: Int, selectedCategory: Int, onIndexSelected: (Int) -> Unit, onCategorySelected: (Int) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent,
        topBar = { DetailsTab(selectedIndex = selectedIndex, selectedCategory = selectedCategory, designation = "Student", onCategorySelected = onCategorySelected) },
        bottomBar = { NavTab(selectedIndex = selectedIndex, onSelect = onIndexSelected) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CurrentTab(selectedIndex, selectedCategory)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentTab(selectedIndex: Int, selectedCategory: Int) {
    when (selectedIndex) {
        0 -> Routine("Student", "CSE-26")
        1 -> StudentTeacherList("Student", selectedCategory)
        // 2 -> Map()
        // 3 -> Notification()
        // 4 -> Profile()
    }
}
