package com.example.module_base_ui.ui.theme

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp

//Nav Tab
@Composable
fun GlassBackground_NavTab(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier.clip(RoundedCornerShape(50)).then(
            if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.graphicsLayer { renderEffect = RenderEffect.createBlurEffect(22f, 22f, Shader.TileMode.CLAMP).asComposeRenderEffect() }
            } else {
                Modifier
            }).background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.04f)))).border(0.3.dp, Color.White.copy(alpha = 0.125f), RoundedCornerShape(50))
    )
}

@Composable
fun GlassOrb(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isPreview = LocalInspectionMode.current
    Box(modifier = modifier.size(78.dp).clip(CircleShape).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.matchParentSize().then(if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { Modifier.graphicsLayer { renderEffect = RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP).asComposeRenderEffect() }
        } else {
            Modifier
        }).background(Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f)))).border(1.2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
        )
    }
}

// Details Tab
@Composable
fun ShadedPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(10)).background(Color.Black.copy(alpha = 0.3f))
    )
}

@Composable
fun GlassBackground_DetailsTab(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier = modifier.clip(RoundedCornerShape(50)).then(
            if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.graphicsLayer { renderEffect = RenderEffect.createBlurEffect(22f, 22f, Shader.TileMode.CLAMP).asComposeRenderEffect() }
            } else {
                Modifier
            }).background(Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f)))).border(1.2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
    )
}

@Composable
fun GlassBackground_RoutineTab(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    Box(modifier = modifier.border(width = 1.dp, Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)).then(
        if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier.graphicsLayer {
                renderEffect = RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP).asComposeRenderEffect()
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
                        renderEffect = RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP).asComposeRenderEffect()
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
