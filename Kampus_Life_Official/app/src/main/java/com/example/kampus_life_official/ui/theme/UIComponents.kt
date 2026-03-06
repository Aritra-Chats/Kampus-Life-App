package com.example.kampus_life_official.ui.theme

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kampus_life_official.R

// Helper to calculate responsive sizes based on a baseline (e.g., 400dp width)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun responsiveDp(baseline: Dp): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    return (baseline.value * (screenWidth.value / 400f)).dp
}

// Helper to calculate responsive text sizes based on a baseline (e.g., 400dp width)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun responsiveSp(baseline: TextUnit): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    return (baseline.value * (screenWidth.value / 400f)).sp
}

//Nav Tab
@Composable
fun GlassBackground_NavTab(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .then(
                if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect =
                            RenderEffect.createBlurEffect(22f, 22f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                    }
                } else {
                    Modifier
                })
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(0.3.dp, Color.White.copy(alpha = 0.125f), RoundedCornerShape(50))
    )
}

@Composable
fun GlassOrb(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isPreview = LocalInspectionMode.current
    val orbSize = responsiveDp(78.dp)
    Box(modifier = modifier
        .size(orbSize)
        .clip(CircleShape)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .matchParentSize()
            .then(
                if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect =
                            RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                    }
                } else {
                    Modifier
                })
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
        )
    }
}

// Details Tab
@Composable
fun ShadedPanel(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(10)).background(Color.Black.copy(alpha = 0.3f)))
}

@Composable
fun ShadedPanel_collapsed(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(25)).background(Color.Black.copy(alpha = 0.3f)))
}

@Composable
fun GlassBackground_DetailsTab(modifier: Modifier = Modifier, isSelected: Boolean) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .then(
                if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect =
                            RenderEffect.createBlurEffect(22f, 22f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                    }
                } else {
                    Modifier
                })
            .background(
                if (isSelected) {
                    SolidColor(Color.White.copy(alpha = 0.25f))
                } else {
                    Brush.radialGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f)))
                }
            ).border(1.2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
    )
}

@Composable
fun GlassBackground_RoutineTab(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(modifier = modifier
        .border(width = 1.dp, Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
        .then(
            if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.graphicsLayer {
                    renderEffect = RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                }
            } else {
                Modifier
            })
    )
}

@Composable
fun RoutineItemGlass(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect =
                            RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                    }
                } else {
                    Modifier
                }
            )
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f))
                )
            )
            .border(
                0.5.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                ),
                RoundedCornerShape(20.dp)
            )
    )
}

@Composable
fun ListItemGlass(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect =
                            RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                    }
                } else {
                    Modifier
                }
            )
            .background(
                SolidColor(Color.White.copy(alpha = 0.12f))
            )
            .border(
                0.5.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                ),
                RoundedCornerShape(20.dp)
            )
    )
}

@Composable
fun LoginButton(modifier: Modifier = Modifier, isPreview: Boolean = LocalInspectionMode.current) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.1f))))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
            .then(
                if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP).asComposeRenderEffect()
                    }
                } else Modifier
            )
    )
}

@Composable
fun GoogleLoginButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(modifier = modifier.clip(RoundedCornerShape(50)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) {
        LoginButton(modifier = Modifier.matchParentSize(), isPreview = isPreview)
        Row(modifier = Modifier.padding(horizontal = responsiveDp(20.dp), vertical = responsiveDp(10.dp)), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = null, modifier = Modifier.size(responsiveDp(18.dp)))
            Spacer(Modifier.width(responsiveDp(12.dp)))
            Text(text = "Sign in with Google", color = Color.White.copy(alpha = 0.9f), fontSize = responsiveSp(14.sp), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun RestoreSessionAnimation() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = "Background", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(responsiveDp(16.dp)))
            Text("Loading...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
