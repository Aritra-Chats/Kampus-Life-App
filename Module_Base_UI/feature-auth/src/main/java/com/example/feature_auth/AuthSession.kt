package com.example.feature_auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Application-wide holder for the currently authenticated user.
 *
 * Other modules depend on [feature-auth] and observe [currentUser]
 * to adapt their behaviour based on the signed-in user's role.
 *
 * Usage from a Composable:
 * ```
 * val user by AuthSession.currentUser.collectAsState()
 * ```
 */
object AuthSession {

    private const val TAG = "AuthSession"

    private val _currentUser = MutableStateFlow<AuthUser?>(null)

    /** Observable stream of the currently signed-in user (null when signed out). */
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    /** Whether we are still checking for a persisted Firebase session. */
    private val _isRestoringSession = MutableStateFlow(true)
    val isRestoringSession: StateFlow<Boolean> = _isRestoringSession.asStateFlow()

    /** Call after a successful sign-in to publish the user to all observers. */
    fun setUser(user: AuthUser) {
        _currentUser.value = user
    }

    /** Call on sign-out to clear the session. */
    fun clear() {
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
    }

    /**
     * Checks if there is an existing Firebase Auth session from a
     * previous app launch. If so, reads the user's profile from
     * Firestore and publishes it.
     *
     * Call this once in `onCreate()` before showing any UI.
     */
    suspend fun tryRestoreSession(authManager: GoogleAuthManager) {
        try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                Log.d(TAG, "Found existing Firebase session: ${firebaseUser.uid}")
                val user = authManager.getUserFromFirestore(firebaseUser.uid)
                if (user != null) {
                    _currentUser.value = user
                    Log.d(TAG, "Session restored for ${user.email}")
                } else {
                    Log.d(TAG, "No Firestore profile found, clearing session")
                    FirebaseAuth.getInstance().signOut()
                }
            } else {
                Log.d(TAG, "No existing Firebase session")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error restoring session", e)
        } finally {
            _isRestoringSession.value = false
        }
    }
}
