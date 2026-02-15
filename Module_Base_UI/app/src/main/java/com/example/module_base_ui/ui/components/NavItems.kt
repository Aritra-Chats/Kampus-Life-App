package com.example.module_base_ui.ui.components

import androidx.annotation.DrawableRes
import com.example.module_base_ui.R

public class NavItem(
    @DrawableRes val Icon: Int,
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