package com.example.module_base_ui.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.module_base_ui.ui.theme.GlassBackground_NavTab
import com.example.module_base_ui.ui.theme.GlassOrb

@Composable
fun NavTab(items: List<NavItem> = NavItems.navItems, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(320.dp).background(Brush.verticalGradient(0.1f to Color.Transparent, 0.8f to Color.Black)), contentAlignment = Alignment.BottomCenter) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).padding(horizontal = 5.dp), contentAlignment = Alignment.BottomStart) {
            val orbSize = 78.dp
            val slotWidth = (maxWidth - 32.dp) / items.size
            val orbOffsetX by animateDpAsState(
                targetValue = (slotWidth * selectedIndex) + (slotWidth / 2) - ((orbSize - 32.dp) / 2),
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
                label = "OrbOffset"
            )
            GlassBackground_NavTab(
                modifier = Modifier.fillMaxWidth().height(66.dp).align(Alignment.BottomCenter)
            )
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(66.dp).align(Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.3f else 1.1f,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                        label = "IconScale"
                    )
                    Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { onSelect(index) }, interactionSource = remember { MutableInteractionSource() }) {
                            Icon(painter = painterResource(item.Icon), contentDescription = item.label, tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp).graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                })
                        }
                    }
                }
            }
            GlassOrb(
                modifier = Modifier.offset(x = orbOffsetX, y = 6.dp).align(Alignment.BottomStart),
                onClick = { onSelect(selectedIndex) })
        }
    }
}
