package com.example.module_base_ui.ui.pages

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_auth.AuthUser
import com.example.feature_auth.GoogleAuthManager
import com.example.feature_auth.UserRole
import com.example.feature_auth.applyRoleOverrides
import com.example.module_base_ui.R
import com.example.module_base_ui.ui.theme.ShadedPanel

// ---------- Google login pill button ----------
@Composable
private fun GoogleLoginButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glass background (blurred, separate layer)
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.graphicsLayer {
                            renderEffect = RenderEffect
                                .createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    } else Modifier
                )
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
        )

        // Sharp text (above blur)
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "G",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Login",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ---------- Login Screen ----------
@Composable
fun LoginScreen(
    activity: Activity,
    authManager: GoogleAuthManager,
    onAuthSuccess: (AuthUser) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // ---- Sign-in side-effect ----
    LaunchedEffect(isLoading) {
        if (!isLoading) return@LaunchedEffect

        val result = authManager.signIn(activity)
        isLoading = false

        result.onSuccess { user ->
            val adjustedUser = applyRoleOverrides(user)
            if (adjustedUser.role == UserRole.UNKNOWN) {
                error = "Unauthorised User: University mail not detected"
                return@onSuccess
            }
            onAuthSuccess(adjustedUser)
        }

        result.onFailure { throwable ->
            error = throwable.localizedMessage ?: "Sign-in failed"
        }
    }

    // ---- UI (static, matches Figma layer-by-layer) ----
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Layer 1: Background orbs image (full screen)
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Layer 2: Shaded center panel / frame
        ShadedPanel(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.65f)
        )

        // Layer 3: Content (logo + button)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Kampus Life Logo",
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .aspectRatio(1.6f),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(48.dp))

            // Login button / loading
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White.copy(alpha = 0.7f),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                GoogleLoginButton(
                    onClick = {
                        isLoading = true
                        error = null
                    },
                    enabled = true
                )
            }

            // Error message
            error?.let {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = it,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp,
                    modifier = Modifier.alpha(0.85f)
                )
            }
        }
    }
}
