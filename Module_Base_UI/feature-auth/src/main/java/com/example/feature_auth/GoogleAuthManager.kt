package com.example.feature_auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(
    context: Context
) {
    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // TODO: replace with your real Web client ID from Google Cloud Console
    private val webClientId = "895797851950-f30q8aq2ducueugj5hj12g7bak0ll27k.apps.googleusercontent.com"

    companion object {
        private const val TAG = "GoogleAuthManager"
        private const val USERS_COLLECTION = "users"
    }

    /**
     * Full sign-in flow:
     * 1. Google Credential Manager → get Google ID token
     * 2. Firebase Auth → sign in with the Google credential
     * 3. Firestore → create or read user profile
     */
    suspend fun signIn(activity: Activity): Result<AuthUser> {
        return try {
            // Step 1: Google Credential Manager
            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse =
                credentialManager.getCredential(request = request, context = activity)

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)

            val email = googleIdTokenCredential.id
            val displayName = googleIdTokenCredential.displayName
            val idToken = googleIdTokenCredential.idToken

            // Step 2: Firebase Auth
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Firebase sign-in returned null user"))

            val uid = firebaseUser.uid

            // Step 3: Build AuthUser with parsed KIIT data
            val role = inferRoleFromEmail(email)
            val kiitInfo = parseKiitEmail(email)

            val user = AuthUser(
                displayName = displayName,
                email = email,
                role = role,
                idToken = idToken,
                uid = uid,
                rollNumber = kiitInfo?.rollNumber,
                admissionYear = kiitInfo?.admissionYear,
                schoolCode = kiitInfo?.schoolCode,
                department = kiitInfo?.department
            )

            // Step 4: Write to Firestore (create or update)
            saveUserToFirestore(uid, user)

            Result.success(user)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential error", e)
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Token parsing error", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error", e)
            Result.failure(e)
        }
    }

    /**
     * Writes the user profile to Firestore. Uses `set()` so it creates
     * or overwrites the document.
     */
    private suspend fun saveUserToFirestore(uid: String, user: AuthUser) {
        try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .set(user.toFirestoreMap())
                .await()
            Log.d(TAG, "User profile saved to Firestore: $uid")
        } catch (e: Exception) {
            // Non-fatal: the sign-in still succeeded even if Firestore write fails
            Log.w(TAG, "Failed to save user profile to Firestore", e)
        }
    }

    /**
     * Reads a user profile from Firestore by UID.
     * Returns null if the document doesn't exist.
     */
    suspend fun getUserFromFirestore(uid: String): AuthUser? {
        return try {
            val doc = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            if (doc.exists()) {
                val map = doc.data ?: return null
                applyRoleOverrides(AuthUser.fromFirestoreMap(uid, map))
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read user from Firestore", e)
            null
        }
    }
}
