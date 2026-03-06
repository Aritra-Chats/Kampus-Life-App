package com.example.kampus_life_official.ui.pages

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.AdministrationList
import com.example.kampus_life_official.data_insertion.Holiday
import com.example.kampus_life_official.data_insertion.MentorList
import com.example.kampus_life_official.data_insertion.Routine
import com.example.kampus_life_official.data_insertion.StudentList
import com.example.kampus_life_official.data_insertion.TeacherList
import com.example.kampus_life_official.data_insertion.isDateOnHoliday
import com.example.kampus_life_official.ui.theme.GlassBackground_DetailsTab
import com.example.kampus_life_official.ui.theme.GlassBackground_RoutineTab
import com.example.kampus_life_official.ui.theme.ListItemGlass
import com.example.kampus_life_official.ui.theme.RoutineItemGlass
import com.example.kampus_life_official.ui.theme.responsiveDp
import com.example.kampus_life_official.ui.theme.responsiveSp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun StudentTeacherList(studentRoll: Int?, designation: String, selectedCategory: Int, section: String?, sections: List<String>?, studentData: List<StudentList>?, teacherData: List<TeacherList>?, mentorData: List<MentorList>?, administrationData: List<AdministrationList>, routineData: List<Routine>?, holidayData: List<Holiday>?) {
    if (designation == "Student") {
        StudentView(teacherData!!, studentRoll!!, section!!,  selectedCategory, mentorData!!, administrationData, routineData!!, holidayData!!)
    } else {
        TeacherView(studentData!!, selectedCategory, sections!!, administrationData)
    }
}

@Composable
fun TeacherView(studentData: List<StudentList>, selectedCategory: Int, sections: List<String>, administrationData: List<AdministrationList>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = "Background", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(255.dp)), contentAlignment = Alignment.TopCenter) {
            when {
                selectedCategory < sections.size -> DisplayStudentList(studentData, sections[selectedCategory])
                selectedCategory == sections.size -> DisplayAdministrationDetails(administrationData)
            }
        }
    }
}

@Composable
fun StudentView(teacherData: List<TeacherList>, studentRoll: Int, section: String, selectedCategory: Int, mentorData: List<MentorList>, administrationData: List<AdministrationList>, routineData: List<Routine>, holidayData: List<Holiday>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = "Background", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(255.dp)), contentAlignment = Alignment.TopCenter) {
            when (selectedCategory) {
                0 -> DisplayTeacherList(teacherData, section)
                1 -> DisplayMentorDetails(mentorData, studentRoll, routineData, holidayData)
                2 -> DisplayAdministrationDetails(administrationData)
            }
        }
    }
}

@Composable
fun DisplayStudentList(studentData: List<StudentList>, section: String) {
    val context = LocalContext.current
    val filteredStudents = remember(studentData, section) { studentData.filter { it.section == section } }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(16.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(responsiveDp(12.dp))) {
        filteredStudents.forEach { student ->
            Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(90.dp))) {
                ListItemGlass(modifier = Modifier.fillMaxSize())
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp), vertical = responsiveDp(12.dp)), horizontalAlignment = Alignment.Start) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = student.name ?: "Unknown", color = Color.White, fontSize = responsiveSp(24.sp), fontWeight = FontWeight.Bold)
                        Image(painter = painterResource(id = R.drawable.phone), contentDescription = "Call", modifier = Modifier.size(responsiveDp(24.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { student.phone?.let { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:$it".toUri())) } })
                    }
                    Spacer(modifier = Modifier.height(responsiveDp(8.dp)))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = student.roll?.toString() ?: "Not Available", color = Color.White, fontSize = responsiveSp(16.sp), fontWeight = FontWeight.Light)
                        Image(painter = painterResource(id = R.drawable.email), contentDescription = "Email", modifier = Modifier.size(responsiveDp(24.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { student.email?.let { context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$it".toUri())) } })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(responsiveDp(100.dp)))
    }
}

@Composable
fun DisplayTeacherList(teacherData: List<TeacherList>, section: String) {
    val context = LocalContext.current
    val filteredTeachers = remember(teacherData, section) { teacherData.filter { it.sections!!.contains(section) } }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(16.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(responsiveDp(12.dp))) {
        filteredTeachers.forEach { teacher ->
            Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(90.dp))) {
                ListItemGlass(modifier = Modifier.fillMaxSize())
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp), vertical = responsiveDp(12.dp)), horizontalAlignment = Alignment.Start) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = teacher.name ?: "Unknown", color = Color.White, fontSize = responsiveSp(24.sp), fontWeight = FontWeight.Bold)
                        Image(painter = painterResource(id = R.drawable.phone), contentDescription = "Call", modifier = Modifier.size(responsiveDp(24.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { teacher.phone?.let { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:$it".toUri())) } })
                    }
                    Spacer(modifier = Modifier.height(responsiveDp(8.dp)))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = teacher.cabin ?: "No Cabin", color = Color.White, fontSize = responsiveSp(16.sp), fontWeight = FontWeight.Light)
                        Image(painter = painterResource(id = R.drawable.email), contentDescription = "Email", modifier = Modifier.size(responsiveDp(24.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { teacher.email?.let { context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$it".toUri())) } })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(responsiveDp(100.dp)))
    }
}

@Composable
fun DisplayMentorDetails(mentorData: List<MentorList>, studentRoll: Int, routineData: List<Routine>, holidayData: List<Holiday>) {

    var mentorTeacher by remember { mutableStateOf<TeacherList?>(null) }
    LaunchedEffect(mentorData, studentRoll) {
        mentorTeacher = mentorData.find { mentor ->
            mentor.mentee?.any { it.roll == studentRoll } == true
        }?.mentor
    }

    if (mentorTeacher == null) return

    val context = LocalContext.current
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val weekDays = (0..6).map { today.with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(16.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(responsiveDp(12.dp))) {
        Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(220.dp))) {
            RoutineItemGlass(modifier = Modifier.fillMaxSize())
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp), vertical = responsiveDp(16.dp)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(text = mentorTeacher?.name ?: "Unknown", color = Color.White, fontSize = responsiveSp(24.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = responsiveDp(16.dp)))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = responsiveDp(8.dp))) {
                    Image(painter = painterResource(id = R.drawable.location), contentDescription = "Location", modifier = Modifier.size(responsiveDp(16.dp)))
                    Spacer(modifier = Modifier.width(responsiveDp(8.dp)))
                    Text(text = mentorTeacher?.cabin ?: "Unknown", color = Color.White, fontSize = responsiveSp(14.sp))
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = responsiveDp(12.dp)), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.height(responsiveDp(80.dp)).width(responsiveDp(160.dp)).clickable {
                        mentorTeacher?.phone?.let { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:$it".toUri())) } }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), isSelected = false)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.phone), contentDescription = null, modifier = Modifier.size(responsiveDp(24.dp)))
                            Spacer(modifier = Modifier.width(responsiveDp(10.dp)))
                            Text(text = "Contact", color = Color.White, fontSize = responsiveSp(16.sp))
                        }
                    }
                    Box(modifier = Modifier.height(responsiveDp(80.dp)).width(responsiveDp(160.dp)).clickable { mentorTeacher?.email?.let { context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$it".toUri())) } }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), isSelected = false)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.email), contentDescription = null, modifier = Modifier.size(responsiveDp(24.dp)))
                            Spacer(modifier = Modifier.width(responsiveDp(10.dp)))
                            Text(text = "E-mail", color = Color.White, fontSize = responsiveSp(16.sp))
                        }
                    }
                }
            }
        }
                
        Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(500.dp))) {
            RoutineItemGlass(modifier = Modifier.fillMaxSize())
            Column(modifier = Modifier.fillMaxWidth().padding(responsiveDp(16.dp)), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Mentor Time Table", color = Color.White, fontSize = responsiveSp(32.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = responsiveDp(12.dp)))
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No classes today", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = responsiveDp(16.dp)).weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(responsiveDp((-12).dp))) {
                        val selectedDayName = selectedDate.format(DateTimeFormatter.ofPattern("EEEE"))
                        val filteredRoutines = remember(routineData, mentorTeacher, selectedDayName) {
                            routineData.filter { it.teacher == mentorTeacher?.name && it.day == selectedDayName && mentorTeacher?.sections?.contains(it.section) == true }
                                .sortedWith(compareBy<Routine> { routine ->
                                    val times = routine.time?.split("-") ?: listOf("", "")
                                    try { LocalTime.parse(times[0].trim()) } catch (_: Exception) { LocalTime.MIDNIGHT }
                                }.thenBy { routine ->
                                    val times = routine.time?.split("-") ?: listOf("", "")
                                    try { LocalTime.parse(times.getOrNull(1)?.trim() ?: "") } catch (_: Exception) { LocalTime.MIDNIGHT }
                                }.thenBy { it.section ?: "" })
                        }
                        if (filteredRoutines.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No classes today", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                            }
                        } else {
                            filteredRoutines.forEach { routine ->
                                val times = routine.time?.split("-") ?: listOf("", "")
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(responsiveDp(8.dp))) {
                                    val isActive = try { selectedDate == LocalDate.now() && LocalTime.now().isBefore(LocalTime.parse(times[1].trim())) && LocalTime.now().isAfter(LocalTime.parse(times[0].trim())) } catch (_: Exception) { false }
                                    Image(painter = painterResource(id = if (isActive) R.drawable.active_class else R.drawable.inactive_class), contentDescription = "Class Status", modifier = Modifier.height(responsiveDp(84.dp)).width(responsiveDp(16.dp)).scale(if (isActive) 1.12f else 1f, if (isActive) 1.36f else 1f))
                                    Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(90.dp)).padding(bottom = responsiveDp(43.dp))) {
                                        if (isActive) RoutineItemGlass(modifier = Modifier.fillMaxSize())
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
                        }
                        Spacer(modifier = Modifier.height(responsiveDp(205.dp)))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(responsiveDp(100.dp)))
    }
}

@Composable
fun DisplayAdministrationDetails(administrationData: List<AdministrationList>) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(16.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(responsiveDp(12.dp))) {
        administrationData.forEach { administration ->
            Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(200.dp))) {
                RoutineItemGlass(modifier = Modifier.fillMaxSize())
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp), vertical = responsiveDp(16.dp)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(text = administration.name ?: "Unknown", color = Color.White, fontSize = responsiveSp(24.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = responsiveDp(16.dp)))
                    Text(text = "${administration.designation ?: "Unknown"}\n${administration.department ?: "Unknown"}", color = Color.Gray, fontSize = responsiveSp(12.sp), fontWeight = FontWeight.Light, textAlign = TextAlign.Center, lineHeight = responsiveSp(13.sp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = responsiveDp(8.dp))) {
                        Image(painter = painterResource(id = R.drawable.location), contentDescription = "Location", modifier = Modifier.size(responsiveDp(16.dp)))
                        Spacer(modifier = Modifier.width(responsiveDp(8.dp)))
                        Text(text = administration.cabin ?: "Unknown", color = Color.White, fontSize = responsiveSp(14.sp))
                    }
                    Box(modifier = Modifier.height(responsiveDp(40.dp)).width(responsiveDp(100.dp)).clickable {
                        administration.email?.let { context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$it".toUri())) } }, contentAlignment = Alignment.Center) {
                        GlassBackground_DetailsTab(modifier = Modifier.fillMaxSize(), isSelected = false)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.email), contentDescription = null, modifier = Modifier.size(responsiveDp(16.dp)))
                            Spacer(modifier = Modifier.width(responsiveDp(5.dp)))
                            Text(text = "E-mail", color = Color.White, fontSize = responsiveSp(12.sp))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}
