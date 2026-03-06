package com.example.kampus_life_official.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoogleAuthManager(context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Web Client ID (client_type 3) from google-services.json
    private val webClientId = "895797851950-cs0j9n6fei8afj6u93kr713ad1ndtutl.apps.googleusercontent.com"

    companion object {
        private const val TAG = "GoogleAuthManager"
        private const val USERS_COLLECTION = "users"
    }

    suspend fun signIn(activity: Activity): Result<AuthUser> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Using GetGoogleIdOption
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                // Note: setHostedDomainFilter is a hint to the UI, not a strict server-side filter
                .setHostedDomainFilter("kiit.ac.in") 
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result: GetCredentialResponse = credentialManager.getCredential(request = request, context = activity)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

            val email = googleIdTokenCredential.id
            val isAdminEmail = teacherOverrideEmails.contains(email.lowercase().trim())
            val isKiitEmail = email.lowercase().endsWith("@kiit.ac.in")
            
            // Domain Enforcement (Security)
            if (!isAdminEmail && !isKiitEmail) {
                return@withContext Result.failure(Exception("Please use your official KIIT email address."))
            }
            
            val idToken = googleIdTokenCredential.idToken

            // Firebase Authentication
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user ?: return@withContext Result.failure(Exception("Firebase sign-in failed"))

            // Build and Save User Data
            val role = inferRoleFromEmail(email)
            val kiitInfo = if (isKiitEmail) {
                if(role == UserRole.STUDENT) parseKiitStudentEmail(email) else parseKiitTeacherEmail(email)
            } else null
            
            if (kiitInfo?.error != null) return@withContext Result.failure(Exception(kiitInfo.error))
            
            val user = AuthUser(
                displayName = googleIdTokenCredential.displayName,
                email = email,
                role = role,
                uid = firebaseUser.uid,
                photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                rollNumber = kiitInfo?.rollNumber,
                admissionYear = kiitInfo?.admissionYear,
                schoolCode = kiitInfo?.schoolCode,
                department = kiitInfo?.department
            )
            
            val finalUser = applyRoleOverrides(user)
            saveUserToFirestore(firebaseUser.uid, finalUser)

            Result.success(finalUser) 
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential error: ${e.type} - ${e.message}")
            Result.failure(Exception("Sign-in cancelled or failed."))
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error", e)
            Result.failure(e)
        }
    }

    private suspend fun saveUserToFirestore(uid: String, user: AuthUser) {
        try { 
            firestore.collection(USERS_COLLECTION).document(uid).set(user.toFirestoreMap()).await() 
        } catch (e: Exception) {
            Log.e(TAG, "Firestore save error", e)
        }
    }

    suspend fun getUserFromFirestore(uid: String): AuthUser? = withContext(Dispatchers.IO) {
        return@withContext try {
            val doc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            if (doc.exists()) {
                val map = doc.data ?: return@withContext null
                applyRoleOverrides(AuthUser.fromFirestoreMap(uid, map))
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Firestore get error", e)
            null
        }
    }
}
