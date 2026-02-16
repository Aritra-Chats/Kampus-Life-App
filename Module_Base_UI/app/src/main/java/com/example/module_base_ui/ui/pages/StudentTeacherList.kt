package com.example.module_base_ui.ui.pages

import android.content.Intent
import android.net.Uri
import android.text.Layout
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.module_base_ui.R
import com.example.module_base_ui.data_insertion.RetrofitClient
import com.example.module_base_ui.data_insertion.StudentList
import com.example.module_base_ui.data_insertion.TeacherList
import com.example.module_base_ui.ui.theme.RoutineItemGlass

@Composable
fun StudentTeacherList(designation: String, selectedCategory: Int) {
    var studentData by remember { mutableStateOf<List<StudentList>>(emptyList()) }
    var teacherData by remember { mutableStateOf<List<TeacherList>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            studentData = loadStudentData()
            teacherData = loadTeacherData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (designation == "Teacher") {
        displayStudentList(studentData, selectedCategory)
    } else {
        displayTeacherList(teacherData, selectedCategory)
    }
}

suspend fun loadStudentData(): List<StudentList> {
    return RetrofitClient.api.getStudentList()
}

suspend fun loadTeacherData(): List<TeacherList> {
    return RetrofitClient.api.getTeacherList()
}

@Composable
fun displayStudentList(studentData: List<StudentList>, selectedCategory: Int) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 230.dp), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                studentData.forEach { student ->
                    Box(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                        RoutineItemGlass(modifier = Modifier.fillMaxSize())
                        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp), horizontalAlignment = Alignment.Start) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = student.name ?: "Unknown", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Image(painter = painterResource(id = R.drawable.phone), contentDescription = student.phone,
                                    modifier = Modifier.size(24.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        student.phone?.let { phone ->
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = student.roll?.toString() ?: "Not Available", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Light)
                                Image(painter = painterResource(id = R.drawable.email), contentDescription = student.email,
                                    modifier = Modifier.size(24.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        student.email?.let { email ->
                                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun displayTeacherList(teacherData: List<TeacherList>, selectedCategory: Int) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 230.dp), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                teacherData.forEach { teacher ->
                    Box(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                        RoutineItemGlass(modifier = Modifier.fillMaxSize())
                        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp), horizontalAlignment = Alignment.Start) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = teacher.name ?: "Unknown", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Image(painter = painterResource(id = R.drawable.phone), contentDescription = teacher.phone,
                                    modifier = Modifier.size(24.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        teacher.phone?.let { phone ->
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = teacher.cabin ?: "No Cabin", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Light)
                                Image(painter = painterResource(id = R.drawable.email), contentDescription = teacher.email,
                                    modifier = Modifier.size(24.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        teacher.email?.let { email ->
                                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
