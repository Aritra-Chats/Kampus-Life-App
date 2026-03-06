package com.example.kampus_life_official.data_insertion

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import com.example.kampus_life_official.login.AuthUser

object LocalStorage {
    private const val PREFS_NAME = "app_data_prefs"
    private val gson = Gson()

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun <T> saveData(context: Context, key: String, data: List<T>) {
        val json = gson.toJson(data)
        getPrefs(context).edit { putString(key, json) }
    }

    fun <T> loadData(context: Context, key: String, typeToken: TypeToken<List<T>>): List<T> {
        val json = getPrefs(context).getString(key, null)
        return if (json != null) { gson.fromJson(json, typeToken.type) } else { emptyList() }
    }

    fun saveUser(context: Context, user: AuthUser) {
        val json = gson.toJson(user)
        getPrefs(context).edit {
            putString(KEY_USER, json)
            putLong(KEY_USER_TIMESTAMP, System.currentTimeMillis())
        }
    }

    fun loadUser(context: Context): AuthUser? {
        val json = getPrefs(context).getString(KEY_USER, null)
        return if (json != null) {
            gson.fromJson(json, AuthUser::class.java)
        } else null
    }

    fun isUserSessionValid(context: Context): Boolean {
        val timestamp = getPrefs(context).getLong(KEY_USER_TIMESTAMP, 0L)
        val oneDayMillis = 24 * 60 * 60 * 1000L
        return (System.currentTimeMillis() - timestamp) < oneDayMillis
    }

    fun clearUser(context: Context) {
        getPrefs(context).edit {
            remove(KEY_USER)
            remove(KEY_USER_TIMESTAMP)
        }
    }

    const val KEY_ROUTINE = "routine_data"
    const val KEY_STUDENTS = "student_data"
    const val KEY_TEACHERS = "teacher_data"
    const val KEY_MENTORS = "mentor_data"
    const val KEY_ADMIN = "admin_data"
    const val KEY_NOTIFICATIONS = "notification_data"
    const val KEY_HOLIDAYS = "holiday_data"
    private const val KEY_USER = "auth_user"
    private const val KEY_USER_TIMESTAMP = "auth_user_timestamp"
}
