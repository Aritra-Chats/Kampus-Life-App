package com.example.feature_auth

/**
 * Maps KIIT school codes to department short names.
 * Expand this as needed.
 */
val SCHOOL_CODE_MAP = mapOf(
    "01" to "Biotechnology",
    "02" to "Civil",
    "03" to "ETC",
    "04" to "Electrical",
    "05" to "CSE",
    "06" to "Mechanical",
    "07" to "IT",
    "51" to "CSSE",
    "52" to "Electronics",
    "53" to "CSCE"
)

fun inferRoleFromEmail(email: String): UserRole {
    val lower = email.lowercase().trim()
    if (!lower.endsWith("@kiit.ac.in")) return UserRole.UNKNOWN

    val userPart = lower.substringBefore("@")
    val first = userPart.firstOrNull() ?: return UserRole.UNKNOWN

    return if (first.isDigit()) {
        UserRole.STUDENT
    } else {
        UserRole.TEACHER
    }
}

/**
 * Parses a KIIT student email (e.g. `2105123@kiit.ac.in`) into structured data.
 *
 * Roll number format: `YYSSXXX`
 *  - YY  = admission year (e.g. 21 → 2021)
 *  - SS  = school/branch code (e.g. 05 → CSE)
 *  - XXX = serial number (variable length)
 *
 * Returns `null` if the email is not a parseable KIIT student email.
 */
fun parseKiitEmail(email: String): ParsedKiitInfo? {
    val lower = email.lowercase().trim()
    if (!lower.endsWith("@kiit.ac.in")) return null

    val userPart = lower.substringBefore("@")
    if (userPart.length < 5 || !userPart.first().isDigit()) return null

    val yearDigits = userPart.substring(0, 2)
    val schoolCode = userPart.substring(2, 4)
    val admissionYear = 2000 + (yearDigits.toIntOrNull() ?: return null)
    val department = SCHOOL_CODE_MAP[schoolCode] ?: "Unknown"

    return ParsedKiitInfo(
        rollNumber = userPart,
        admissionYear = admissionYear,
        schoolCode = schoolCode,
        department = department
    )
}

/**
 * Email addresses that should always be promoted to TEACHER,
 * regardless of the domain-based inference result.
 */
private val teacherOverrideEmails = setOf(
    "yt.nbt.2812@gmail.com"
)

/**
 * Applies role overrides (e.g. admin / whitelist) on top of
 * the domain-inferred role.
 */
fun applyRoleOverrides(user: AuthUser): AuthUser {
    if (user.email.lowercase().trim() in teacherOverrideEmails) {
        return user.copy(role = UserRole.TEACHER)
    }
    return user
}
