package com.example.kampus_life_official.ui.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kampus_life_official.R
import com.example.kampus_life_official.data_insertion.Notification
import com.example.kampus_life_official.ui.theme.RoutineItemGlass
import com.example.kampus_life_official.ui.theme.responsiveDp
import com.example.kampus_life_official.ui.theme.responsiveSp
import androidx.core.content.edit

@Composable
fun Notification(selectedCategory: Int, notifications: List<Notification>) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE) }
    var favoriteIds by remember { mutableStateOf(prefs.getStringSet("favorites", emptySet()) ?: emptySet()) }

    LaunchedEffect(Unit) {
        try {
            // Validate favorites: Remove IDs that no longer exist in the fetched list
            val currentIds = notifications.mapNotNull { it.id }.toSet()
            val validFavorites = favoriteIds.intersect(currentIds)
            if (validFavorites.size != favoriteIds.size) {
                favoriteIds = validFavorites
                prefs.edit { putStringSet("favorites", validFavorites) }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    DisplayNotifications(notifications = notifications, selectedCategory = selectedCategory, favoriteIds = favoriteIds, onToggleFavorite = { id ->
            val newFavorites = if (favoriteIds.contains(id)) favoriteIds - id else favoriteIds + id
            favoriteIds = newFavorites
            prefs.edit { putStringSet("favorites", newFavorites) }
        }
    )
}

@Composable
fun DisplayNotifications(notifications: List<Notification>, selectedCategory: Int, favoriteIds: Set<String>, onToggleFavorite: (String) -> Unit) {
    var selectedNotification by remember { mutableStateOf<Notification?>(null) }

    val filteredAndSortedNotifications = remember(notifications, selectedCategory, favoriteIds) {
        val filtered = if (selectedCategory == 0) {
            notifications
        } else {
            val filterSender = when (selectedCategory) {
                1 -> "Dean"
                2 -> "Director"
                3 -> "Registrar"
                else -> ""
            }
            notifications.filter { it.sender?.contains(filterSender, ignoreCase = true) == true }
        }
        filtered.sortedWith(compareByDescending<Notification> { favoriteIds.contains(it.id) }.thenByDescending { it.sendTime }.thenByDescending { it.id })
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().padding(top = responsiveDp(250.dp)), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = responsiveDp(16.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(responsiveDp(12.dp))) {
                filteredAndSortedNotifications.forEach { notification -> 
                    DisplayNotificationEntry(notification = notification, isFavorite = favoriteIds.contains(notification.id), onClick = { selectedNotification = notification }, onToggleFavorite = { notification.id?.let { onToggleFavorite(it) } })
                }
                Spacer(modifier = Modifier.height(responsiveDp(100.dp)))
            }
        }

        selectedNotification?.let { notification -> NotificationOverlay(notification = notification, onDismiss = { selectedNotification = null }) }
    }
}

@Composable
fun DisplayNotificationEntry(notification: Notification, isFavorite: Boolean, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(responsiveDp(120.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }) {
        RoutineItemGlass(modifier = Modifier.fillMaxSize())
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = responsiveDp(20.dp), vertical = responsiveDp(12.dp)), verticalArrangement = Arrangement.spacedBy(responsiveDp(4.dp))) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = notification.sender ?: "Unknown", color = Color.White, fontSize = responsiveSp(18.sp), fontWeight = FontWeight.Bold)
                Text(text = notification.sendTime ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(12.sp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                    Text(text = notification.subject ?: "No Subject", color = Color.White, fontSize = responsiveSp(16.sp), fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = notification.body ?: "", color = Color.Gray.copy(alpha = 0.8f), fontSize = responsiveSp(14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Image(painter = painterResource(id = if (isFavorite) R.drawable.filled_star else R.drawable.star), contentDescription = "Favorite", modifier = Modifier.size(responsiveDp(20.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggleFavorite() }
                )
            }
        }
    }
}

@Composable
fun NotificationOverlay(notification: Notification, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() }, contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxWidth(0.85f).fillMaxHeight(0.6f).clickable(enabled = false) {}) {
            RoutineItemGlass(modifier = Modifier.fillMaxSize())
            Column(modifier = Modifier.fillMaxSize().padding(responsiveDp(24.dp)).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.Start) {
                Text(text = notification.sender ?: "Unknown", color = Color.White, fontSize = responsiveSp(22.sp), fontWeight = FontWeight.Bold)
                Text(text = notification.sendTime ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = responsiveSp(12.sp), modifier = Modifier.padding(bottom = responsiveDp(16.dp)))
                Text(text = notification.subject ?: "No Subject", color = Color.White, fontSize = responsiveSp(18.sp), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = responsiveDp(8.dp)))
                Text(text = notification.body ?: "", color = Color.White.copy(alpha = 0.9f), fontSize = responsiveSp(16.sp), lineHeight = responsiveSp(22.sp))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Close", color = Color.Cyan, fontSize = responsiveSp(16.sp), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End).padding(top = responsiveDp(16.dp)).clickable { onDismiss() })
            }
        }
    }
}
