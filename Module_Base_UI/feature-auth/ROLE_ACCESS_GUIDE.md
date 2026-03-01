# Role-Based Access Guide — Kampus Life

## Overview

The `feature-auth` module exposes the signed-in user's **role**, **department**, and **admission year** via the `AuthSession` singleton. Use these to control what data and UI is shown to each user.

---

## Quick Start

### 1. Read the current user in any Composable

```kotlin
import com.example.feature_auth.AuthSession
import com.example.feature_auth.UserRole

@Composable
fun MyScreen() {
    val user by AuthSession.currentUser.collectAsState()

    when (user?.role) {
        UserRole.STUDENT -> { /* student-only UI */ }
        UserRole.TEACHER -> { /* teacher-only UI */ }
        else -> { /* not signed in or unknown */ }
    }
}
```

### 2. Conditionally show/hide UI elements

```kotlin
@Composable
fun AnnouncementCard(announcement: Announcement) {
    val user by AuthSession.currentUser.collectAsState()

    Card {
        Text(announcement.title)
        Text(announcement.body)

        // Only teachers can delete announcements
        if (user?.role == UserRole.TEACHER) {
            IconButton(onClick = { deleteAnnouncement(announcement.id) }) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}
```

### 3. Filter data by department or year

```kotlin
val user = AuthSession.currentUser.value

// Show routines only for the student's department
val filteredRoutines = allRoutines.filter {
    it.department == user?.department   // e.g. "CSE"
}

// Show notices only for the student's batch
val batchNotices = notices.filter {
    it.targetYear == user?.admissionYear  // e.g. 2021
}
```

### 4. Use in non-Composable code (ViewModels, repositories)

```kotlin
class RoutineRepository {
    fun getRoutinesForCurrentUser(): List<Routine> {
        val user = AuthSession.currentUser.value ?: return emptyList()
        return when (user.role) {
            UserRole.STUDENT -> fetchStudentRoutine(user.department, user.admissionYear)
            UserRole.TEACHER -> fetchTeacherSchedule(user.email)
            else -> emptyList()
        }
    }
}
```

### 5. Observe changes reactively (e.g. in a ViewModel)

```kotlin
class ProfileViewModel : ViewModel() {
    val userRole = AuthSession.currentUser
        .map { it?.role }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
```

---

## Available Fields on `AuthUser`

| Field | Type | Example | Available for |
|---|---|---|---|
| `email` | `String` | `2105123@kiit.ac.in` | All |
| `displayName` | `String?` | `"Anshu"` | All |
| `role` | `UserRole` | `STUDENT` / `TEACHER` | All |
| `uid` | `String?` | Firebase UID | All |
| `rollNumber` | `String?` | `"2105123"` | Students only |
| `admissionYear` | `Int?` | `2021` | Students only |
| `schoolCode` | `String?` | `"05"` | Students only |
| `department` | `String?` | `"CSE"` | Students only |

---

## Firestore Security Rules (Recommended)

Replace the default test-mode rules with these to enforce role-based access at the database level:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users can only read/write their own profile
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Announcements: anyone can read, only teachers can write
    match /announcements/{docId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null
        && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == "TEACHER";
    }

    // Routines: anyone can read, only teachers can write
    match /routines/{docId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null
        && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == "TEACHER";
    }
  }
}
```

---

## Sign Out

```kotlin
AuthSession.clear()  // Signs out of Firebase + clears the session
```

This triggers recomposition in any Composable observing `AuthSession.currentUser`, automatically returning the user to the login screen.
