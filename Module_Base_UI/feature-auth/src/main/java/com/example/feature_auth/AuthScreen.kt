package com.example.feature_auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Basic fallback Auth screen. For the styled login UI,
 * use [com.example.module_base_ui.ui.pages.LoginScreen] in the app module
 * which has access to the full design system.
 */
@Composable
fun AuthScreen(
    activity: Activity,
    authManager: GoogleAuthManager,
    onAuthSuccess: (AuthUser) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Kampus Life Login",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    isLoading = true
                    error = null
                },
                enabled = !isLoading
            ) {
                Text("Sign in with Google")
            }
            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}
