package com.example.kampus_life_official.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.Holiday
import com.example.kampus_life_official.data_insertion.Routine
import com.example.kampus_life_official.data_insertion.isDateOnHoliday
import com.example.kampus_life_official.ui.theme.GlassBackground_DetailsTab
import com.example.kampus_life_official.ui.theme.ShadedPanel
import com.example.kampus_life_official.ui.theme.ShadedPanel_collapsed
import com.example.kampus_life_official.ui.theme.responsiveDp
import com.example.kampus_life_official.ui.theme.responsiveSp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DetailsTab(selectedIndex: Int, selectedCategory: Int, routineData: List<Routine>, studentSection: String?, teacherSections: List<String>?, holidayData: List<Holiday>, designation: String, onCategorySelected: (Int) -> Unit) {
    var targetLocation by remember { mutableStateOf("") }
    LaunchedEffect(selectedIndex) { if (selectedIndex != 2) targetLocation = "" }
    val isCollapsed = selectedIndex == 2 && targetLocation.isNotEmpty()
    val heightAnimSpec = if (isCollapsed) { tween(durationMillis = 500, delayMillis = 500) } else { tween<Dp>(durationMillis = 500) }
    val height by animateDpAsState(
        targetValue = if (isCollapsed) 125.dp else 320.dp,
        animationSpec = heightAnimSpec,
        label = "heightTransition"
    )
    Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(height)).background(Brush.verticalGradient(0.2f to Color.Black, 0.9f to Color.Transparent)).statusBarsPadding(), contentAlignment = Alignment.TopCenter) {
        TabOptions(selectedIndex, selectedCategory, studentSection, teacherSections, designation, routineData, holidayData, onCategorySelected, targetLocation) { targetLocation = it }
    }
}

@Composable
fun TabOptions(selectedIndex: Int, selectedCategory: Int, studentSection: String?, teacherSections: List<String>?, designation: String, routineData: List<Routine>, holidayData: List<Holiday>, onCategorySelected: (Int) -> Unit, targetLocation: String, onTargetLocationChange: (String) -> Unit) {
    when (selectedIndex) {
        0 -> RoutineTab()
        1 -> ListTab(teacherSections, designation, selectedCategory, onCategorySelected)
        2 -> MapTab(targetLocation, designation, studentSection, teacherSections, onTargetLocationChange, routineData, holidayData)
        3 -> NotificationTab(selectedCategory, onCategorySelected)
        4 -> ProfileTab()
    }
}

@Composable
fun RoutineTab() {
    Box(modifier = Modifier.fillMaxWidth().padding(top = responsiveDp(20.dp)).padding(horizontal = responsiveDp(5.dp)), contentAlignment = Alignment.TopCenter) {
        ShadedPanel(modifier = Modifier.fillMaxWidth().height(responsiveDp(192.dp)).align(Alignment.TopCenter))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App Logo", modifier = Modifier.height(responsiveDp(128.dp)).width(responsiveDp(400.dp)).padding(top = responsiveDp(16.dp)), Alignment.TopCenter)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(12.dp)).padding(top = responsiveDp(8.dp)), horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp)), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(0.25f))
                Box(modifier = Modifier.weight(0.5f).height(responsiveDp(42.dp)), contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), false)
                    Text(text = "Time Table", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(0.25f))
            }
        }
    }
}

@Composable
fun ListTab(teacherSections: List<String>?, designation: String, selectedCategory: Int, onCategorySelected: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = responsiveDp(20.dp)).padding(horizontal = responsiveDp(5.dp)), contentAlignment = Alignment.TopCenter) {
        ShadedPanel(modifier = Modifier.fillMaxWidth().height(responsiveDp(192.dp)).align(Alignment.TopCenter))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App Logo", modifier = Modifier.height(responsiveDp(128.dp)).width(responsiveDp(400.dp)).padding(top = responsiveDp(16.dp)), Alignment.TopCenter)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(12.dp)).padding(top = responsiveDp(8.dp)), horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp)), verticalAlignment = Alignment.CenterVertically) {
                if (designation == "Student") {
                    Spacer(modifier = Modifier.weight(0.05f))
                    Box(modifier = Modifier.weight(0.3f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(0) }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 0)
                        Text(text = "Faculty List", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.weight(0.3f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(1) }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 1)
                        Text(text = "Mentor", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.weight(0.3f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(2) }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 2)
                        Text(text = "Administration", color = Color.White, fontSize = responsiveSp(12.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = responsiveDp(2.dp)))
                    }
                    Spacer(modifier = Modifier.weight(0.05f))
                } else {
                    val size = teacherSections!!.size + 3
                    Spacer(modifier = Modifier.weight(1f/size))
                    teacherSections.forEachIndexed { index, section ->
                        Box(modifier = Modifier.weight(1f/size).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(index) }, contentAlignment = Alignment.Center) {
                            GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == index)
                            Text(text = "Student List ($section)", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(modifier = Modifier.weight(1f/size).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(2) }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 2)
                        Text(text = "Administration", color = Color.White, fontSize = responsiveSp(12.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = responsiveDp(2.dp)))
                    }
                    Spacer(modifier = Modifier.weight(1f/size))
                }
            }
        }
    }
}

@Composable
fun MapTab(targetLocation: String, designation: String, studentSection: String?, teacherSections: List<String>?, onTargetLocationChange: (String) -> Unit, routineData: List<Routine>, holidayData: List<Holiday>) {
    val currentRoutine = remember(routineData) {
        val now = LocalTime.now()
        val currentHoliday = holidayData.find { isDateOnHoliday(LocalDate.now(), it) }
        if (currentHoliday == null) {
            val todayName = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE"))
            routineData.find { routine ->
                if (designation == "Student" && routine.section != studentSection) return@find false
                else if (designation == "Teacher" && teacherSections?.contains(routine.section) != true) return@find false
                if (routine.day != todayName) return@find false
                val times = routine.time?.split("-") ?: return@find false
                if (times.size < 2) return@find false
                try {
                    val start = LocalTime.parse(times[0].trim())
                    val end = LocalTime.parse(times[1].trim())
                    (now == start || now.isAfter(start)) && now.isBefore(end)
                } catch (_: Exception) {
                    false
                }
            }
        } else return@remember null
    }

    val isCollapsed = targetLocation.isNotEmpty()
    val componentSpec = tween<Dp>(500, delayMillis = -75)
    val panelSpec = if (isCollapsed) tween(500, delayMillis = 250) else tween<Dp>(500)
    
    val panelHeight by animateDpAsState(if (isCollapsed) 72.dp else 192.dp, panelSpec, label = "panelHeight")
    val logoWidth by animateDpAsState(if (isCollapsed) 75.dp else 400.dp, componentSpec, label = "logoWidth")
    val logoHeight by animateDpAsState(if (isCollapsed) 32.dp else 112.dp, componentSpec, label = "logoHeight")
    val containerPaddingTop by animateDpAsState(if (isCollapsed) 12.dp else 20.dp, panelSpec, label = "paddingTop")
    val containerPaddingHorizontal by animateDpAsState(if (isCollapsed) 8.dp else 5.dp, panelSpec, label = "paddingHorizontal")

    var visualCollapsed by remember { mutableStateOf(isCollapsed) }
    LaunchedEffect(isCollapsed) { if (isCollapsed) {
            delay(500)
            visualCollapsed = true
        } else { visualCollapsed = false }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(top = responsiveDp(containerPaddingTop)).padding(horizontal = responsiveDp(containerPaddingHorizontal))) {
        val w = maxWidth
        
        Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(panelHeight))) {
            Crossfade(targetState = visualCollapsed, animationSpec = tween(500), label = "PanelFade") { collapsed ->
                if (collapsed) {
                    ShadedPanel_collapsed(modifier = Modifier.fillMaxSize())
                } else {
                    ShadedPanel(modifier = Modifier.fillMaxSize())
                }
            }
        }

        val logoX by animateDpAsState(if (isCollapsed) 4.dp else 0.dp, componentSpec, label = "logoX")
        val logoY by animateDpAsState(if (isCollapsed) 18.dp else 16.dp, componentSpec, label = "logoY")
        
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .offset(x = responsiveDp(logoX), y = responsiveDp(logoY))
                .size(width = responsiveDp(logoWidth), height = responsiveDp(logoHeight))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { if(isCollapsed) onTargetLocationChange("") }
        )

        val infoRowHeight = 42.dp
        val rowX by animateDpAsState(if (isCollapsed) 77.dp else (3.5).dp, componentSpec, label = "rowX")
        val rowY by animateDpAsState(if (isCollapsed) (68.dp - infoRowHeight) / 2 else 136.dp, componentSpec, label = "rowY")
        val rowWidth by animateDpAsState(if (isCollapsed) w - 85.dp - 12.dp else w - 24.dp, componentSpec, label = "rowWidth")

        Row(
            modifier = Modifier
                .offset(x = responsiveDp(rowX), y = responsiveDp(rowY))
                .width(responsiveDp(rowWidth))
                .height(responsiveDp(infoRowHeight)),
            horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(if (isCollapsed) 1f else 0.85f)
                    .height(responsiveDp(infoRowHeight))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                        if (!isCollapsed) onTargetLocationChange(currentRoutine?.classroom ?: "---")
                    },
                contentAlignment = Alignment.Center
            ) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), false)
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(if(isCollapsed) 16.dp else 24.dp)), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = currentRoutine?.subject ?: "---", color = Color.White, fontSize = responsiveSp(if(isCollapsed) 12.sp else 14.sp), fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = currentRoutine?.time ?: "---", color = Color.White, fontSize = responsiveSp(if(isCollapsed) 12.sp else 14.sp), fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = currentRoutine?.classroom ?: "---", color = Color.White, fontSize = responsiveSp(if(isCollapsed) 12.sp else 14.sp), fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(0.15f)
                    .size(responsiveDp(infoRowHeight))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                        if (isCollapsed) onTargetLocationChange("") 
                    },
                contentAlignment = Alignment.Center
            ) {
                GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), false)
                Image(painter = painterResource(id = R.drawable.search), contentDescription = "Search", modifier = Modifier.size(responsiveDp(if(isCollapsed) 18.dp else 14.dp)))
            }
        }
    }
}

@Composable
fun NotificationTab(selectedCategory: Int, onCategorySelected: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = responsiveDp(20.dp)).padding(horizontal = responsiveDp(5.dp)), contentAlignment = Alignment.TopCenter) {
        ShadedPanel(modifier = Modifier.fillMaxWidth().height(responsiveDp(192.dp)).align(Alignment.TopCenter))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App Logo", modifier = Modifier.height(responsiveDp(128.dp)).width(responsiveDp(400.dp)).padding(top = responsiveDp(16.dp)), Alignment.TopCenter)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(12.dp)).padding(top = responsiveDp(8.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(0) }, horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp)), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(0.225f).height(responsiveDp(42.dp)), contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 0)
                    Text(text = "All", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.225f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(1) }, contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 1)
                    Text(text = "Dean", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.225f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(2) }, contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 2)
                    Text(text = "Director", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.225f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(3) }, contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 3)
                    Text(text = "Registrar", color = Color.White, fontSize = responsiveSp(12.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = responsiveDp(4.dp)))
                }
                Box(modifier = Modifier.weight(0.1f).height(responsiveDp(42.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCategorySelected(4) }, contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), selectedCategory == 4)
                    Image(painter = painterResource(id = R.drawable.options), contentDescription = "Options", Modifier.size(responsiveDp(14.dp)))
                }
            }
        }
    }
}

@Composable
fun ProfileTab() {
    Box(modifier = Modifier.fillMaxWidth().padding(top = responsiveDp(20.dp)).padding(horizontal = responsiveDp(5.dp)), contentAlignment = Alignment.TopCenter) {
        ShadedPanel(modifier = Modifier.fillMaxWidth().height(responsiveDp(192.dp)).align(Alignment.TopCenter))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App Logo", modifier = Modifier.height(responsiveDp(128.dp)).width(responsiveDp(400.dp)).padding(top = responsiveDp(16.dp)), Alignment.TopCenter)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(12.dp)).padding(top = responsiveDp(8.dp)), horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp)), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(0.3f))
                Box(modifier = Modifier.weight(0.4f).height(responsiveDp(42.dp)), contentAlignment = Alignment.Center) {
                    GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), false)
                    Text(text = "My Profile", color = Color.White, fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(0.3f))
            }
        }
    }
}
