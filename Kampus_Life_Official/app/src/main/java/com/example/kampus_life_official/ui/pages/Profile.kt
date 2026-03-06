package com.example.kampus_life_official.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.StudentList
import com.example.kampus_life_official.data_insertion.TeacherList
import com.example.kampus_life_official.login.AuthSession
import com.example.kampus_life_official.login.AuthUser
import com.example.kampus_life_official.ui.theme.ListItemGlass
import com.example.kampus_life_official.ui.theme.LoginButton
import com.example.kampus_life_official.ui.theme.responsiveDp
import com.example.kampus_life_official.ui.theme.responsiveSp

@Composable
fun Profile(user: AuthUser, designation: String, studentDetails: StudentList?, teacherDetails: TeacherList?) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = "Background", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(260.dp)).padding(horizontal = responsiveDp(16.dp)), contentAlignment = Alignment.TopCenter) {
            val name = if (designation == "Student") studentDetails?.name ?: user.displayName ?: "Unknown" else teacherDetails?.name ?: user.displayName ?: "Unknown"
            ProfileCard(name = name, userLogo = user.photoUrl, roll = user.rollNumber ?: "N/A", email = user.email, designation = designation, position = if (designation == "Teacher") "Faculty" else "Student", department = user.department ?: studentDetails?.section ?: "N/A", isExpanded = isExpanded, onClick = { isExpanded = !isExpanded }, onLogout = { AuthSession.clear(context) })
        }
    }
}

@Composable
fun ProfileCard(name: String, userLogo: String?, roll: String, email: String, designation: String, position: String, department: String, isExpanded: Boolean, onClick: () -> Unit, onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }) {
        Box(modifier = Modifier.padding(top = responsiveDp(40.dp)).fillMaxWidth().wrapContentHeight()) {
            ListItemGlass(modifier = Modifier.matchParentSize())
            Column(modifier = Modifier.padding(top = responsiveDp(50.dp)).padding(horizontal = responsiveDp(24.dp))) {
                Text(text = name, color = Color.White, fontSize = responsiveSp(24.sp), fontWeight = FontWeight.Bold)
                if (designation == "Student") {
                    Text(text = "Roll: $roll", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                } else {
                    Text(text = position, color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                }
                Text(text = "Dept: $department", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                Text(text = email, color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(16.sp))
                AnimatedVisibility(visible = isExpanded) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(responsiveDp(20.dp)))
                        Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(45.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onLogout() }, contentAlignment = Alignment.Center) {
                            LoginButton(modifier = Modifier.matchParentSize(), isPreview = LocalInspectionMode.current)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(id = R.drawable.logout), contentDescription = "Logout Logo", modifier = Modifier.size(responsiveDp(16.dp)))
                                Spacer(Modifier.width(responsiveDp(10.dp)))
                                Text(text = "Logout", color = Color.Red, fontSize = responsiveSp(16.sp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(responsiveDp(20.dp)))
            }
        }

        AsyncImage(model = userLogo, contentDescription = "Profile picture", placeholder = painterResource(id = R.drawable.image_placeholder), error = painterResource(id = R.drawable.image_placeholder), modifier = Modifier.padding(start = responsiveDp(24.dp), top = responsiveDp(10.dp)).size(responsiveDp(70.dp)).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
    }
}
