@file:Suppress("DEPRECATION")

package com.example.kampus_life_official.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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

    private val webClientId = "895797851950-cs0j9n6fei8afj6u93kr713ad1ndtutl.apps.googleusercontent.com"

    companion object {
        private const val TAG = "GoogleAuthManager"
        private const val USERS_COLLECTION = "users"
    }

    private val fallbackSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .setHostedDomain("kiit.ac.in")
            .build())

    suspend fun signIn(activity: Activity, onFallbackNeeded: (Intent) ->Unit): Result<AuthUser> = withContext(Dispatchers.IO) {
        return@withContext try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setHostedDomainFilter("kiit.ac.in") 
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result: GetCredentialResponse = credentialManager.getCredential(request = request, context = activity)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

            val email = googleIdTokenCredential.id
            if (!email.lowercase().endsWith("@kiit.ac.in")) {
                return@withContext Result.failure(Exception("Please use your official KIIT email address."))
            }

            firebaseAuthAndBuildUser(idToken = googleIdTokenCredential.idToken, email = email, displayName = googleIdTokenCredential.displayName, photoUrl = googleIdTokenCredential.profilePictureUri?.toString())

        } catch (e: GetCredentialException) {
            Log.w(TAG, "CredentialManager failed (${e.type}), triggering fallback: ${e.message}")
            withContext(Dispatchers.Main) {
                fallbackSignInClient.signOut().await()
                onFallbackNeeded(fallbackSignInClient.signInIntent)
            }
            Result.failure(FallbackTriggeredException())
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error", e)
            Result.failure(e)
        }
    }

    class FallbackTriggeredException : Exception("Fallback sign-in UI launched")

    suspend fun handleFallbackResult(data: Intent?): Result<AuthUser> = withContext(Dispatchers.IO) {
        return@withContext try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)

            val email = account.email ?: return@withContext Result.failure(Exception("No email returned from Google."))

            if(!email.lowercase().endsWith("@kiit.ac.in")) {
                fallbackSignInClient.signOut().await()
                return@withContext Result.failure(Exception("Please use your official KIIT email."))
            }

            val idToken = account.idToken?: return@withContext Result.failure(Exception("No ID token returned from Google."))

            firebaseAuthAndBuildUser(idToken, email, account.displayName, account.photoUrl?.toString())
        } catch (e: ApiException) {
            Log.e(TAG, "Fallback sign-in error: ${e.statusCode}")
            Result.failure(Exception("Sign-in cancelled or failed."))
        } catch (e: Exception) {
            Log.e(TAG, "Fallback sign-in error", e)
            Result.failure(e)
        }
    }

    private suspend fun firebaseAuthAndBuildUser(idToken: String, email: String, displayName: String?, photoUrl: String?): Result<AuthUser> {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val firebaseUser = authResult.user ?: return Result.failure(Exception("Firebase sign-in failed."))

        val role = inferRoleFromEmail(email)
        val kiitInfo = if (role == UserRole.STUDENT) parseKiitStudentEmail(email) else parseKiitTeacherEmail(email)

        if (kiitInfo.error != null) return Result.failure(Exception(kiitInfo.error))

        val user = AuthUser(
            displayName = displayName,
            email = email,
            role = role,
            uid = firebaseUser.uid,
            photoUrl = photoUrl,
            rollNumber = kiitInfo.rollNumber,
            admissionYear = kiitInfo.admissionYear,
            schoolCode = kiitInfo.schoolCode,
            department = kiitInfo.department
        )

        saveUserToFirestore(firebaseUser.uid, user)
        return Result.success(user)
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
                AuthUser.fromFirestoreMap(uid, map)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Firestore get error", e)
            null
        }
    }
}
