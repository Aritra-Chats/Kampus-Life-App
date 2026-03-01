package com.example.feature_auth

enum class UserRole {
    STUDENT,
    TEACHER,
    UNKNOWN
}

/**
 * Parsed information from a KIIT roll-number email.
 */
data class ParsedKiitInfo(
    val rollNumber: String,
    val admissionYear: Int,
    val schoolCode: String,
    val department: String
)

data class AuthUser(
    val displayName: String?,
    val email: String,
    val role: UserRole,
    val idToken: String? = null,
    val uid: String? = null,
    val rollNumber: String? = null,
    val admissionYear: Int? = null,
    val schoolCode: String? = null,
    val department: String? = null
) {
    /** Converts to a Firestore-friendly map. */
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "email" to email,
        "displayName" to displayName,
        "role" to role.name,
        "rollNumber" to rollNumber,
        "admissionYear" to admissionYear,
        "schoolCode" to schoolCode,
        "department" to department
    )

    companion object {
        /** Reconstructs an [AuthUser] from a Firestore document map + uid. */
        fun fromFirestoreMap(uid: String, map: Map<String, Any?>): AuthUser = AuthUser(
            displayName = map["displayName"] as? String,
            email = (map["email"] as? String) ?: "",
            role = try {
                UserRole.valueOf((map["role"] as? String) ?: "UNKNOWN")
            } catch (_: Exception) {
                UserRole.UNKNOWN
            },
            uid = uid,
            rollNumber = map["rollNumber"] as? String,
            admissionYear = (map["admissionYear"] as? Long)?.toInt(),
            schoolCode = map["schoolCode"] as? String,
            department = map["department"] as? String
        )
    }
}
