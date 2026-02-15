package com.example.module_base_ui.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.module_base_ui.R
import com.example.module_base_ui.ui.theme.GlassBackground_DetailsTab
import com.example.module_base_ui.ui.theme.ShadedPanel

@Composable
fun DetailsTab(selectedIndex: Int, designation: String, onSelect: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(320.dp).background(Brush.verticalGradient(0.2f to Color.Black, 0.9f to Color.Transparent)), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 10.dp).padding(horizontal = 5.dp), contentAlignment = Alignment.TopCenter) {
            ShadedPanel(modifier = Modifier.fillMaxWidth().height(192.dp).align(Alignment.TopCenter))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App Logo", modifier = Modifier.height(128.dp).width(400.dp).padding(top = 16.dp), Alignment.TopCenter)
                TabOptions(selectedIndex, designation)
            }
        }
    }
}

@Composable
fun TabOptions(selectedIndex: Int, designation: String) {
    when (selectedIndex) {
        0 -> RoutineTab(designation)
        1 -> ListTab(designation)
        2 -> MapTab()
        3 -> NotificationTab()
        4 -> ProfileTab()
    }
}

@Composable
fun RoutineTab(designation: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (designation == "Student") {
            Spacer(modifier = Modifier.weight(0.25f))
            Box(modifier = Modifier.weight(0.5f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Time Table", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(0.25f))
        } else {
            Spacer(modifier = Modifier.weight(0.25f))
            Box(modifier = Modifier.weight(0.25f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "CSE-24", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(0.25f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "CSE-32", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(0.25f))
        }
    }
}

@Composable
fun ListTab(designation: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (designation == "Student") {
            Spacer(modifier = Modifier.weight(0.05f))
            Box(modifier = Modifier.weight(0.3f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Faculty List", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(0.3f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Mentor", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(0.3f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Administration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(0.05f))
        } else {
            Spacer(modifier = Modifier.weight(0.2f))
            Box(modifier = Modifier.weight(0.2f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Student List (CSE-24)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(0.2f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Student List (CSE-32)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(0.2f).height(42.dp), contentAlignment = Alignment.Center) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
                Text(text = "Administration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

@Composable
fun MapTab() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(0.85f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "UHV", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "8:00am-9:00am", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "A-201", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(modifier = Modifier.weight(0.15f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Image(painter = painterResource(id = R.drawable.search), contentDescription = "Search", Modifier.size(14.dp))
        }
    }
}

@Composable
fun NotificationTab() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(0.225f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Text(text = "All", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(0.225f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Text(text = "Dean", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(0.225f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Text(text = "Director", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(0.225f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Text(text = "Registrar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(0.1f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Image(painter = painterResource(id = R.drawable.options), contentDescription = "Options", Modifier.size(14.dp))
        }
    }
}

@Composable
fun ProfileTab() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.weight(0.3f))
        Box(modifier = Modifier.weight(0.4f).height(42.dp), contentAlignment = Alignment.Center) {
            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize())
            Text(text = "My Profile", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.weight(0.3f))
    }
}
