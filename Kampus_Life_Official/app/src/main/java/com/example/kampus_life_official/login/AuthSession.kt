package com.example.kampus_life_official.login

import android.content.Context
import android.util.Log
import com.example.kampus_life_official.data_insertion.LocalStorage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthSession {
    private const val TAG = "AuthSession"
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()
    private val _isRestoringSession = MutableStateFlow(true)
    val isRestoringSession: StateFlow<Boolean> = _isRestoringSession.asStateFlow()

    fun setUser(context: Context, user: AuthUser) {
        _currentUser.value = user
        LocalStorage.saveUser(context, user)
    }

    fun clear(context: Context) {
        FirebaseAuth.getInstance().signOut()
        LocalStorage.clearUser(context)
        _currentUser.value = null
    }

    suspend fun tryRestoreSession(context: Context, authManager: GoogleAuthManager) {
        try {
            // 1. Try to load from Local Storage first (Offline-first)
            val cachedUser = LocalStorage.loadUser(context)
            if (cachedUser != null && LocalStorage.isUserSessionValid(context)) {
                _currentUser.value = cachedUser
                Log.d(TAG, "Restored session from LocalStorage for ${cachedUser.email}")
            }

            // 2. Re-verify with Firebase if internet is available
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                Log.d(TAG, "Firebase session exists, verifying/updating...")
                val updatedUser = authManager.getUserFromFirestore(firebaseUser.uid)
                if (updatedUser != null) {
                    _currentUser.value = updatedUser
                    LocalStorage.saveUser(context, updatedUser)
                    Log.d(TAG, "Session updated from Firestore for ${updatedUser.email}")
                } else if (cachedUser == null) {
                    // Only sign out if we have no local cache and no firestore profile
                    Log.d(TAG, "No profile found, clearing session")
                    clear(context)
                }
            } else if (cachedUser != null && !LocalStorage.isUserSessionValid(context)) {
                // If local session expired and no firebase session, clear
                Log.d(TAG, "Session expired, clearing")
                clear(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error during session restoration/verification", e)
        } finally {
            _isRestoringSession.value = false
        }
    }
}
