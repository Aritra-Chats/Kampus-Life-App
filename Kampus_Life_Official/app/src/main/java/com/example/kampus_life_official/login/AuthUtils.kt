package com.example.kampus_life_official.login

import com.example.kampus_life_official.loadTeacherData

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

val SCHOOL_ABBR_MAP = mapOf(
    "01" to "Biotechnology",
    "fce" to "Civil",
    "03" to "ETC",
    "04" to "Electrical",
    "fcs" to "CSE",
    "06" to "Mechanical",
    "07" to "IT",
    "51" to "CSSE",
    "fet" to "Electronics",
    "53" to "CSCE"
)

fun inferRoleFromEmail(email: String): UserRole {
    return if (email.substringBefore("@").all { it.isDigit() }) UserRole.STUDENT else UserRole.TEACHER
}

fun parseKiitStudentEmail(email: String): ParsedKiitInfo {
    val lower = email.lowercase().trim()
    val userPart = lower.substringBefore("@")
    if (userPart.length < 5) return ParsedKiitInfo(
        rollNumber = null,
        admissionYear = null,
        schoolCode = null,
        department = null,
        error = "Invalid KIIT email format"
    )
    val yearDigits = userPart.substring(0, 2)
    val schoolCode = userPart.substring(2, 4)
    val admissionYear = 2000 + (yearDigits.toIntOrNull() ?: return ParsedKiitInfo(
        rollNumber = null,
        admissionYear = null,
        schoolCode = null,
        department = null,
        error = "Could not parse admission year from email"
    ))
    val department = SCHOOL_CODE_MAP[schoolCode] ?: "Unknown"

    return  ParsedKiitInfo(
        rollNumber = userPart,
        admissionYear = admissionYear,
        schoolCode = schoolCode,
        department = department,
        error = null
    )
}

suspend fun parseKiitTeacherEmail(email: String): ParsedKiitInfo {
    val lower = email.lowercase().trim()
    val userPart = lower.substringBefore("@")
    if (userPart.length < 5) return ParsedKiitInfo(
        rollNumber = null,
        admissionYear = null,
        schoolCode = null,
        department = null,
        error = "Invalid KIIT email format"
    )
    val schoolCode = userPart.substring(userPart.length-4, userPart.length)
    val department = SCHOOL_ABBR_MAP[userPart.substring(userPart.length-4, userPart.length)] ?: "Unknown"
    val teacherData = loadTeacherData()
    val roll = teacherData.find {it.email == email}?.roll

    return  ParsedKiitInfo(
        rollNumber = roll.toString(),
        admissionYear = null,
        schoolCode = schoolCode,
        department = department,
        error = null
    )
}
