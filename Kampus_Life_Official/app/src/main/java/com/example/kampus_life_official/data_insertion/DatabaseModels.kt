package com.example.kampus_life_official.data_insertion

import com.google.gson.annotations.SerializedName

data class StudentList(
    @SerializedName("_id")
    val id: String? = null,
    val name: String? = null,
    val roll: Int? = null,
    val email: String? = null,
    val phone: String? = null,
    val section: String? = null
)

data class TeacherList(
    @SerializedName("_id")
    val id: String? = null,
    val name: String? = null,
    val roll: Int? = null,
    val email: String? = null,
    val phone: String? = null,
    val cabin: String? = null,
    val sections: List<String>? = null
)

data class Routine(
    @SerializedName("_id")
    val id: String? = null,
    val section: String? = null,
    val subject: String? = null,
    val day: String? = null,
    val time: String? = null,
    val teacher: String? = null,
    val classroom: String? = null
)

data class AdministrationList(
    @SerializedName("_id")
    val id: String? = null,
    val name: String? = null,
    val designation: String? = null,
    val department: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val cabin: String? = null
)

data class MentorList(
    @SerializedName("_id")
    val id: String? = null,
    val mentor: TeacherList? = null,
    val mentee: List<StudentList>? = null
)

data class Notification(
    @SerializedName("_id")
    val id: String? = null,
    val sender: String? = null,
    val receiver: String? = null,
    val subject: String? = null,
    val body: String? = null,
    val sendTime: String? = null
)

data class Holiday(
    @SerializedName("_id")
    val id: String? = null,
    val dateString: String? = null,
    val event: String? = null
)
