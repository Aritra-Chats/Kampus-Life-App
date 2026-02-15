package com.example.module_base_ui.ui.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.module_base_ui.R
import com.example.module_base_ui.data_insertion.RetrofitClient
import com.example.module_base_ui.data_insertion.Routine
import com.example.module_base_ui.ui.theme.GlassBackground_RoutineTab
import com.example.module_base_ui.ui.theme.RoutineItemGlass
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Routine(designation : String, currentSection : String) {
    var routineData by remember { mutableStateOf<List<Routine>>(emptyList()) }
    LaunchedEffect(Unit) {
        try { routineData = loadRoutineData() } catch (e: Exception) { }
    }
    if(designation == "Student") StudentRoutine(routineData, currentSection)
}

suspend fun loadRoutineData(): List<Routine> {
    return RetrofitClient.api.getRoutine()
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentRoutine(routineData : List<Routine>, currentSection: String) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val weekDays = (0..6).map { today.with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 225.dp), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.Start) {
                Text(text = currentSection, color = Color.White, fontSize = 54.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                Text(text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { selectedDate = today })
                Text(text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE")), color = Color.White.copy(alpha = 0.8f), fontSize = 48.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp).clickable { selectedDate = today })
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
                        Box(modifier = Modifier.width(50.dp).height(80.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedDate = date }, contentAlignment = Alignment.Center) {
                            if (isSelected) GlassBackground_RoutineTab(modifier = Modifier.requiredWidth(65.dp).height(70.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = dayName, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f), fontSize = if (isSelected) 16.sp else 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Text(text = date.dayOfMonth.toString(), color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f), fontSize = if (isSelected) 32.sp else 28.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp).weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy((-12).dp)) {
                    val selectedDayName = selectedDate.format(DateTimeFormatter.ofPattern("EEEE"))
                    routineData.forEach { routine ->
                        if (routine.section != currentSection || routine.day != selectedDayName) return@forEach
                        val times = routine.time?.split("-") ?: listOf("", "")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val isActive = try{ LocalTime.now().isBefore(LocalTime.parse(times[1].trim())) && LocalTime.now().isAfter(LocalTime.parse(times[0].trim())) } catch(e : Exception) { false }
                            Image(painter = painterResource(id = if(isActive) R.drawable.active_class else R.drawable.inactive_class), contentDescription = "Class Status", modifier = Modifier.height(84.dp).width(16.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                                RoutineItemGlass(modifier = Modifier.fillMaxSize())
                                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = routine.subject ?: "", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Text(text = routine.teacher ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = routine.time ?: "", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(text = routine.classroom ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(205.dp))
                }
            }
        }
    }
}
