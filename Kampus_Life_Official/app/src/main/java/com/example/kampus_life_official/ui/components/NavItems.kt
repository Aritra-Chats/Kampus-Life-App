package com.example.kampus_life_official.ui.components

import androidx.annotation.DrawableRes
import com.example.kampus_life_official.R

class NavItem(
    @DrawableRes val icon: Int,
    val label: String,
    val route: String
)

object NavItems {
    val navItems = listOf(
        NavItem(R.drawable.routine, "Routine", "routine"),
        NavItem(R.drawable.list, "List", "list"),
        NavItem(R.drawable.map, "Map", "map"),
        NavItem(R.drawable.notification, "Notification", "notification"),
        NavItem(R.drawable.profile, "Profile", "profile")
    )
}