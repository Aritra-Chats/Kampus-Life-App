package com.example.module_base_ui.data_insertion

import retrofit2.http.GET

interface ApiService {
    @GET("StudentList")
    suspend fun getStudentList(): List<StudentList>

    @GET("TeacherList")
    suspend fun getTeacherList(): List<TeacherList>

    @GET("Routine")
    suspend fun getRoutine(): List<Routine>
}