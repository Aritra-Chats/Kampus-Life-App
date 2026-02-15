package com.example.module_base_ui.data_insertion

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
fun ComponentActivity.loadDatabases() {
    lifecycleScope.launch {
        try {
            val studentListData = RetrofitClient.api.getStudentList()
            val teacherListData = RetrofitClient.api.getTeacherList()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@loadDatabases, "Error fetching data: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
