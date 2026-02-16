package com.example.module_base_ui.data_insertion

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
