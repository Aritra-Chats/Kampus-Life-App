package com.example.kampus_life_official.data_insertion

import retrofit2.http.GET

interface ApiService {
    @GET("StudentList")
    suspend fun getStudentList(): List<StudentList>

    @GET("TeacherList")
    suspend fun getTeacherList(): List<TeacherList>

    @GET("Routine")
    suspend fun getRoutine(): List<Routine>

    @GET("AdministrationList")
    suspend fun getAdministrationList(): List<AdministrationList>

    @GET("MentorList")
    suspend fun getMentorList(): List<MentorList>

    @GET("Announcement")
    suspend fun getNotifications(): List<Notification>

    @GET("Holiday")
    suspend fun getHolidays(): List<Holiday>
}
