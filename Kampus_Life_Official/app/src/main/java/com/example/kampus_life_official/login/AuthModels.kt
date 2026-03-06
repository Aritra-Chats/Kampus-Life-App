package com.example.kampus_life_official.login

enum class UserRole {
    STUDENT,
    TEACHER,
    UNKNOWN
}

data class ParsedKiitInfo(
    val rollNumber: String?,
    val admissionYear: Int?,
    val schoolCode: String?,
    val department: String?,
    val error: String?
)

data class AuthUser(val displayName: String?, val email: String, val role: UserRole, val idToken: String? = null, val uid: String? = null, val photoUrl: String? = null, val rollNumber: String? = null, val admissionYear: Int? = null, val schoolCode: String? = null, val department: String? = null) {
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "email" to email,
        "displayName" to displayName,
        "role" to role.name,
        "photoUrl" to photoUrl,
        "rollNumber" to rollNumber,
        "admissionYear" to admissionYear,
        "schoolCode" to schoolCode,
        "department" to department
    )

    companion object {
        fun fromFirestoreMap(uid: String, map: Map<String, Any?>): AuthUser = AuthUser(
            displayName = map["displayName"] as? String,
            email = (map["email"] as? String) ?: "",
            role = try { UserRole.valueOf((map["role"] as? String) ?: "UNKNOWN") } catch (_: Exception) { UserRole.UNKNOWN },
            uid = uid,
            photoUrl = map["photoUrl"] as? String,
            rollNumber = map["rollNumber"] as? String,
            admissionYear = (map["admissionYear"] as? Long)?.toInt(),
            schoolCode = map["schoolCode"] as? String,
            department = map["department"] as? String
        )
    }
}
