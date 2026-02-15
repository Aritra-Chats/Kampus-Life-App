package com.example.module_base_ui

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

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module_Base_UITheme {
                var selectedIndex by remember { mutableIntStateOf(2) }
                MainScreen(selectedIndex = selectedIndex, onIndexSelected = { selectedIndex = it })
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(selectedIndex: Int, onIndexSelected: (Int) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent,
        topBar = { DetailsTab(selectedIndex = selectedIndex, designation = "Student", onSelect = onIndexSelected) },
        bottomBar = { NavTab(selectedIndex = selectedIndex, onSelect = onIndexSelected) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CurrentTab(selectedIndex)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentTab(selectedIndex: Int) {
    when (selectedIndex) {
        0 -> Routine("Student", "CSE-26")
    }
}
