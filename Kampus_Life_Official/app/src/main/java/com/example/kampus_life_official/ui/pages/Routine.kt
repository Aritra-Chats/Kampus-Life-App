package com.example.kampus_life_official.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.Holiday
import com.example.kampus_life_official.data_insertion.Routine
import com.example.kampus_life_official.data_insertion.isDateOnHoliday
import com.example.kampus_life_official.ui.theme.GlassBackground_RoutineTab
import com.example.kampus_life_official.ui.theme.RoutineItemGlass
import com.example.kampus_life_official.ui.theme.responsiveDp
import com.example.kampus_life_official.ui.theme.responsiveSp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun Routine(designation : String, studentSection : String?, teacherSections: List<String>?, teacherName: String?, routineData: List<Routine>, holidayData: List<Holiday>) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val weekDays = (0..6).map { today.with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = "Background", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(240.dp)), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.fillMaxSize().padding(responsiveDp(16.dp)), horizontalAlignment = Alignment.Start) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text(text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)), color = Color.White.copy(alpha = 0.8f), fontSize = responsiveSp(12.sp), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { selectedDate = today })
                        Text(text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE")), color = Color.White.copy(alpha = 0.8f), fontSize = responsiveSp(36.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = responsiveDp(12.dp)).clickable { selectedDate = today })
                    }
                    Text(text = if (designation == "Student") studentSection ?: "Section" else "Routine", color = Color.White, fontSize = responsiveSp(38.5.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = responsiveDp(18.dp)))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    weekDays.forEach { date ->
                        val isSelected = date.isEqual(selectedDate)
                        val dayName = when (date.dayOfWeek) {
                            DayOfWeek.MONDAY -> "Mon"
                            DayOfWeek.TUESDAY -> "Tues"
                            DayOfWeek.WEDNESDAY -> "Wed"
                            DayOfWeek.THURSDAY -> "Thurs"
                            DayOfWeek.FRIDAY -> "Fri"
                            DayOfWeek.SATURDAY -> "Sat"
                            DayOfWeek.SUNDAY -> "Sun"
                            else -> ""
                        }
                        Box(modifier = Modifier.width(responsiveDp(50.dp)).height(responsiveDp(80.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedDate = date }, contentAlignment = Alignment.Center) {
                            if (isSelected) GlassBackground_RoutineTab(modifier = Modifier.requiredWidth(responsiveDp((57.5).dp)).height(responsiveDp(70.dp)))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(responsiveDp(4.dp))) {
                                Text(text = dayName, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f), fontSize = if (isSelected) responsiveSp(16.sp) else responsiveSp(14.sp), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Text(text = date.dayOfMonth.toString(), color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f), fontSize = if (isSelected) responsiveSp(32.sp) else responsiveSp(28.sp), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                val currentHoliday = holidayData.find { isDateOnHoliday(selectedDate, it) }
                if (currentHoliday != null) {
                    Box(modifier = Modifier.fillMaxSize().padding(top = 156.dp), contentAlignment = Alignment.TopCenter) {
                        Text("Enjoy your holiday of ${currentHoliday.event}", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                    }
                } else {
                    if (designation == "Student") {
                        if (studentSection != null) {
                            StudentRoutine(routineData, studentSection, selectedDate)
                        } else {
                            Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(156.dp)), contentAlignment = Alignment.TopCenter) {
                                Text("Loading profile data...", color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    } else {
                        if (teacherSections != null && teacherName != null) {
                            TeacherRoutine(routineData, teacherSections, selectedDate, teacherName)
                        } else {
                            Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(156.dp)), contentAlignment = Alignment.TopCenter) {
                                Text("Loading profile data...", color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.StudentRoutine(routineData : List<Routine>, studentSection: String, selectedDate: LocalDate) {
    val selectedDayName = selectedDate.format(DateTimeFormatter.ofPattern("EEEE"))
    val filteredRoutines = remember(routineData, studentSection, selectedDayName) {
        routineData.filter { it.section == studentSection && it.day == selectedDayName }
            .sortedWith(compareBy<Routine> { routine ->
                val times = routine.time?.split("-") ?: listOf("", "")
                try { LocalTime.parse(times[0].trim()) } catch (_: Exception) { LocalTime.MIDNIGHT }
            }.thenBy { routine ->
                val times = routine.time?.split("-") ?: listOf("", "")
                try { LocalTime.parse(times.getOrNull(1)?.trim() ?: "") } catch (_: Exception) { LocalTime.MIDNIGHT }
            })
    }

    if (routineData.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 156.dp), contentAlignment = Alignment.TopCenter) {
            CircularProgressIndicator(color = Color.White.copy(alpha = 0.5f))
        }
        return
    }

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val itemHeight = responsiveDp(90.dp)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
        val viewportHeightPx = constraints.maxHeight
        val itemHeightPx = with(density) { itemHeight.toPx() }

        LaunchedEffect(filteredRoutines) {
            val activeIndex = filteredRoutines.indexOfFirst { routine ->
                val times = routine.time?.split("-") ?: listOf("", "")
                try {
                    selectedDate == LocalDate.now() &&
                            LocalTime.now().isBefore(LocalTime.parse(times[1].trim())) &&
                            LocalTime.now().isAfter(LocalTime.parse(times[0].trim()))
                } catch (_: Exception) { false }
            }
            if (activeIndex != -1) {
                val offset = -(viewportHeightPx / 2) + (itemHeightPx / 2).toInt()
                listState.animateScrollToItem(activeIndex, offset)
            }
        }

        if (filteredRoutines.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(top = 156.dp), contentAlignment = Alignment.TopCenter) {
                Text("No classes today", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(top = responsiveDp(16.dp)),
                verticalArrangement = Arrangement.spacedBy(responsiveDp((-12).dp))
            ) {
                items(filteredRoutines) { routine ->
                    val times = routine.time?.split("-") ?: listOf("", "")
                    val isActive = try{ selectedDate == LocalDate.now() && LocalTime.now().isBefore(LocalTime.parse(times[1].trim())) && LocalTime.now().isAfter(LocalTime.parse(times[0].trim())) } catch(_ : Exception) { false }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp))) {
                        Image(painter = painterResource(id = if(isActive) R.drawable.active_class else R.drawable.inactive_class), contentDescription = "Class Status", modifier = Modifier.height(responsiveDp(84.dp)).width(responsiveDp(16.dp)).scale(if (isActive) 1.3f else 1f, if (isActive) 1.42f else 1f))
                        Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(if(isActive) 95.dp else (91.5).dp)).padding(bottom = responsiveDp( if (isActive) 34.dp else 43.dp))) {
                            if(isActive) RoutineItemGlass(modifier = Modifier.fillMaxSize().requiredHeight(95.dp).padding(top = responsiveDp(15.dp)))
                            Row(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp)).padding(top = responsiveDp(if(isActive) 10.dp else 0.dp)), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = routine.subject ?: "", color = Color.White, fontSize = responsiveSp(if(isActive) 21.sp else 20.sp), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(responsiveDp(if (isActive) 8.dp else 6.dp)))
                                    Text(text = routine.teacher ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(if (isActive) (14.5).sp else 14.sp))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = routine.time ?: "", color = Color.White, fontSize = responsiveSp(if(isActive) 21.sp else 20.sp), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(responsiveDp(if (isActive) 8.dp else 6.dp)))
                                    Text(text = routine.classroom ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(if (isActive) (14.5).sp else 14.sp))
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(responsiveDp(205.dp)))
                }
            }
        }
    }
}

@Composable
fun ColumnScope.TeacherRoutine(routineData : List<Routine>, teacherSections: List<String>, selectedDate: LocalDate, teacherName: String) {
    val selectedDayName = selectedDate.format(DateTimeFormatter.ofPattern("EEEE"))
    val sortedFilteredRoutineData = remember(routineData, teacherSections, selectedDayName, teacherName) {
        routineData.filter { it.section in teacherSections && it.day == selectedDayName && it.teacher == teacherName }
            .sortedWith(compareBy<Routine> { routine ->
                val times = routine.time?.split("-") ?: listOf("", "")
                try { LocalTime.parse(times[0].trim()) } catch (_: Exception) { LocalTime.MIDNIGHT }
            }.thenBy { routine ->
                val times = routine.time?.split("-") ?: listOf("", "")
                try { LocalTime.parse(times.getOrNull(1)?.trim() ?: "") } catch (_: Exception) { LocalTime.MIDNIGHT }
            }.thenBy { it.section ?: "" })
    }

    if (routineData.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White.copy(alpha = 0.5f))
        }
        return
    }

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val itemHeight = responsiveDp(90.dp)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
        val viewportHeightPx = constraints.maxHeight
        val itemHeightPx = with(density) { itemHeight.toPx() }

        LaunchedEffect(sortedFilteredRoutineData) {
            val activeIndex = sortedFilteredRoutineData.indexOfFirst { routine ->
                val times = routine.time?.split("-") ?: listOf("", "")
                try {
                    selectedDate == LocalDate.now() &&
                            LocalTime.now().isBefore(LocalTime.parse(times[1].trim())) &&
                            LocalTime.now().isAfter(LocalTime.parse(times[0].trim()))
                } catch (_: Exception) { false }
            }
            if (activeIndex != -1) {
                val offset = -(viewportHeightPx / 2) + (itemHeightPx / 2).toInt()
                listState.animateScrollToItem(activeIndex, offset)
            }
        }

        if (sortedFilteredRoutineData.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No classes today", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(top = responsiveDp(16.dp)),
                verticalArrangement = Arrangement.spacedBy(responsiveDp((-12).dp))
            ) {
                items(sortedFilteredRoutineData) { routine ->
                    val times = routine.time?.split("-") ?: listOf("", "")
                    val isActive = try{ selectedDate == LocalDate.now() && LocalTime.now().isBefore(LocalTime.parse(times[1].trim())) && LocalTime.now().isAfter(LocalTime.parse(times[0].trim())) } catch(_ : Exception) { false }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp))) {
                        Image(painter = painterResource(id = if(isActive) R.drawable.active_class else R.drawable.inactive_class), contentDescription = "Class Status", modifier = Modifier.height(responsiveDp(84.dp)).width(responsiveDp(16.dp)).scale(if (isActive) 1.12f else 1f, if (isActive) 1.36f else 1f))
                        Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(90.dp)).padding(bottom = responsiveDp(43.dp))) {
                            if(isActive) RoutineItemGlass(modifier = Modifier.fillMaxSize())
                            Row(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp)), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = routine.subject ?: "", color = Color.White, fontSize = responsiveSp(18.sp), fontWeight = FontWeight.Bold)
                                    Text(text = routine.section ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(14.sp))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = routine.time ?: "", color = Color.White, fontSize = responsiveSp(16.sp), fontWeight = FontWeight.Bold)
                                    Text(text = routine.classroom ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(14.sp))
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(responsiveDp(205.dp)))
                }
            }
        }
    }
}
