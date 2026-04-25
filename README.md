<div align="center">

# 🎓 Kampus Life

**The official companion app for KIIT University — built for students and faculty alike.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-33.10.0-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Repository Structure](#-repository-structure)
- [Kampus Life Official](#-kampus-life-official)
  - [Features](#features)
  - [Architecture](#architecture)
  - [Authentication Flow](#authentication-flow)
  - [Data Flow](#data-flow)
  - [App Navigation](#app-navigation)
  - [Background Services](#background-services)
  - [Tech Stack](#tech-stack)
- [Module: Navigation & Rotation](#-module-navigation--rotation)
  - [Features](#features-1)
  - [Sensor Pipeline](#sensor-pipeline)
- [Module: Base UI](#-module-base-ui)
- [Getting Started](#-getting-started)
- [Project Roadmap](#-project-roadmap)

---

## 🌟 Overview

**Kampus Life** is a full-featured Android application designed exclusively for [KIIT University](https://kiit.ac.in) staff and students. It centralises everything a student or teacher needs daily — class schedules, faculty directories, campus announcements, and a smart notification system — all behind a single KIIT Google Sign-In.

> 🔒 Access is restricted to verified `@kiit.ac.in` email addresses. The app automatically determines whether the signed-in user is a **Student** or **Teacher** and adapts the entire UI accordingly.

The repository is organised as a multi-module Android project, where each folder is an independent Android Studio project representing a specific stage or sub-system of the app.

---

## 🗂 Repository Structure

```mermaid
graph TD
    ROOT["📁 Kampus-Life-App (Repository Root)"]

    ROOT --> KLO["📱 Kampus_Life_Official\nProduction App"]
    ROOT --> MNR["🧭 Module_Navigation_Rotation\nSensor / Compass Demo"]
    ROOT --> MBU["🎨 Module_Base_UI\nUI Prototype + Auth Module"]
    ROOT --> MDI["💉 Module_Data_Injection\nData Layer Scaffold"]

    KLO --> KLO_APP["app/\nMain application module"]
    KLO_APP --> AUTH["login/\nGoogle Auth + Role resolution"]
    KLO_APP --> DATA["data_insertion/\nRetrofit API + LocalStorage"]
    KLO_APP --> UI["ui/\npages + components + theme"]
    KLO_APP --> WORKER["worker/\nBackground services & alarms"]

    MBU --> MBU_APP["app/\nUI prototype"]
    MBU --> FEAT_AUTH["feature-auth/\nReusable auth library module"]

    style ROOT fill:#1a1a2e,color:#e0e0e0
    style KLO fill:#16213e,color:#4ecca3
    style MNR fill:#16213e,color:#f7b731
    style MBU fill:#16213e,color:#a29bfe
    style MDI fill:#16213e,color:#636e72
```

---

## 📱 Kampus Life Official

> **Path:** `Kampus_Life_Official/`  
> The production-ready Android app. Built entirely in Kotlin with **Jetpack Compose**, using Firebase for auth/storage and a custom REST API for all university data.

### Features

| Tab | Icon | Students | Teachers |
|-----|------|----------|----------|
| **Routine** | 📅 | Weekly timetable for their section. Active class highlighted in real-time. Holiday detection. | Full teaching schedule across all assigned sections. |
| **List** | 📋 | Browse faculty, view mentor–mentee mapping, access administration contacts. | Browse student lists by section, view administration. |
| **Map** | 🗺️ | Campus map view (integrated navigation module) | Campus map view |
| **Notifications** | 🔔 | Announcements from Dean / Director / Registrar. Favourites + category filter. | Same announcement feed. |
| **Profile** | 👤 | Name, roll number, section, department, email. Expandable card with logout. | Name, faculty position, department, email. |

Additional capabilities:
- ✅ **Offline-first** — all data is cached in SharedPreferences (JSON via Gson) and served instantly on app start
- ✅ **Smart class reminders** — foreground service notifies 10–15 minutes before each lecture
- ✅ **Alarm-sound first-class alert** — the first class of the day triggers an alarm-style notification
- ✅ **Announcement push** — WorkManager polls every 15 minutes for new campus announcements
- ✅ **Session persistence** — sessions survive restarts and are silently re-verified against Firebase

---

### Architecture

The app follows a **single-activity, component-based** architecture with Jetpack Compose managing all UI state reactively.

```mermaid
graph TD
    subgraph "Presentation Layer"
        MA["MainActivity\n(entry point, permission handling)"]
        MS["MainScreen\n(Scaffold + NavTab + DetailsTab)"]
        CT["CurrentTab\n(role-aware tab switcher)"]

        P_ROUTINE["RoutinePage\n(StudentRoutine / TeacherRoutine)"]
        P_LIST["StudentTeacherList\n(StudentView / TeacherView)"]
        P_NOTIF["NotificationPage\n(category filter + favourites)"]
        P_PROFILE["ProfilePage\n(expandable card + logout)"]
        P_LOGIN["LoginScreen\n(Google SSO + sync)"]
    end

    subgraph "Auth Layer"
        GAM["GoogleAuthManager\n(Credential Manager + Fallback)"]
        AS["AuthSession\n(StateFlow singleton)"]
        AU["AuthUtils\n(email → role + KIIT info)"]
    end

    subgraph "Data Layer"
        RC["RetrofitClient\n(OkHttp + Gson)"]
        API["ApiService\n(REST endpoints)"]
        LS["LocalStorage\n(SharedPreferences + Gson cache)"]
    end

    subgraph "Background Layer"
        AW["AnnouncementWorker\n(WorkManager, every 15 min)"]
        CCS["ClassCheckService\n(Foreground Service)"]
        AR["AlarmReceiver\n(midnight AlarmManager trigger)"]
        NH["NotificationHelper\n(4 notification channels)"]
    end

    MA --> AS
    MA --> P_LOGIN
    MA --> MS
    MS --> CT
    CT --> P_ROUTINE & P_LIST & P_NOTIF & P_PROFILE
    P_LOGIN --> GAM --> AU --> AS
    MS --> RC --> API
    MS --> LS
    AW --> RC
    AW --> NH
    CCS --> LS
    CCS --> NH
    AR --> CCS
```

---

### Authentication Flow

The app enforces KIIT-only access and resolves each user's role automatically from their institutional email address.

```mermaid
sequenceDiagram
    actor User
    participant App as MainActivity
    participant LS as LocalStorage
    participant FB_AUTH as Firebase Auth
    participant FS as Firestore
    participant GAM as GoogleAuthManager
    participant API as REST API

    App->>LS: Load cached AuthUser
    LS-->>App: cachedUser (or null)
    App->>FB_AUTH: Check current Firebase session
    FB_AUTH-->>App: firebaseUser (or null)

    alt Firebase session exists
        App->>FS: getUserFromFirestore(uid)
        FS-->>App: AuthUser profile
        App->>LS: Update cache
    else No session & cache expired
        App->>App: Show LoginScreen
    end

    User->>App: Tap "Sign in with Google"
    App->>GAM: signIn(activity)
    GAM->>GAM: CredentialManager.getCredential()

    alt CredentialManager succeeds
        GAM-->>GAM: GoogleIdTokenCredential
    else Fallback needed
        GAM->>User: Launch legacy GoogleSignIn intent
        User-->>GAM: Account selected
    end

    GAM->>GAM: Validate email ends with @kiit.ac.in
    GAM->>FB_AUTH: signInWithCredential(GoogleAuthProvider)
    FB_AUTH-->>GAM: FirebaseUser (uid)
    GAM->>GAM: inferRoleFromEmail()\nparseKiitStudentEmail() or parseKiitTeacherEmail()
    GAM->>FS: saveUserToFirestore(uid, AuthUser)
    GAM-->>App: Result.success(AuthUser)

    App->>API: Fetch StudentList + TeacherList
    App->>App: Validate user exists in DB
    alt User not in database
        App->>FB_AUTH: signOut()
        App-->>User: "Account not registered. Contact Support."
    else User verified
        App->>LS: Save all data (routine, mentors, etc.)
        App-->>User: Navigate to MainScreen
    end
```

---

### Data Flow

All data is fetched from a remote REST API and cached locally for offline access. On every app start the cache is served immediately while a background refresh runs silently.

```mermaid
flowchart LR
    subgraph Remote["☁️ Remote (REST API)"]
        direction TB
        EP1["/StudentList"]
        EP2["/TeacherList"]
        EP3["/Routine"]
        EP4["/MentorList"]
        EP5["/AdministrationList"]
        EP6["/Announcement"]
        EP7["/Holiday"]
    end

    subgraph NetworkLayer["🔌 Network Layer"]
        RC["RetrofitClient\n(OkHttp + Gson Converter)"]
        API["ApiService\n(Suspend functions)"]
    end

    subgraph Cache["💾 Local Cache (SharedPreferences)"]
        LS_S["student_data"]
        LS_T["teacher_data"]
        LS_R["routine_data"]
        LS_M["mentor_data"]
        LS_A["admin_data"]
        LS_N["notification_data"]
        LS_H["holiday_data"]
    end

    subgraph UI["📱 Compose UI"]
        SC["MainScreen\n(LaunchedEffect)"]
        TABS["Tab Pages\n(remember + mutableStateOf)"]
    end

    Remote --> RC --> API
    API -->|"Fetched data"| SC
    SC -->|"If changed → save"| Cache
    Cache -->|"Loaded on startup\n(offline-first)"| SC
    SC --> TABS

    style Remote fill:#0f3460,color:#e0e0e0
    style NetworkLayer fill:#16213e,color:#e0e0e0
    style Cache fill:#1a1a2e,color:#e0e0e0
    style UI fill:#0f3460,color:#e0e0e0
```

#### Data Models

| Model | Key Fields | API Endpoint |
|-------|-----------|--------------|
| `StudentList` | `roll`, `name`, `email`, `phone`, `section` | `/StudentList` |
| `TeacherList` | `roll`, `name`, `email`, `phone`, `cabin`, `sections[]` | `/TeacherList` |
| `Routine` | `section`, `subject`, `day`, `time`, `teacher`, `classroom` | `/Routine` |
| `MentorList` | `mentor: TeacherList`, `mentee: List<StudentList>` | `/MentorList` |
| `AdministrationList` | `name`, `designation`, `department`, `cabin` | `/AdministrationList` |
| `Notification` | `sender`, `receiver`, `subject`, `body`, `sendTime` | `/Announcement` |
| `Holiday` | `dateString`, `event` | `/Holiday` |

---

### App Navigation

The app uses a custom bottom navigation bar (`NavTab`) with a context-sensitive top bar (`DetailsTab`) that changes its category chips based on the active tab and user role.

```mermaid
stateDiagram-v2
    [*] --> AppLaunch

    AppLaunch --> RestoringSession : isRestoringSession = true
    RestoringSession --> LoginScreen : no valid session
    RestoringSession --> MainScreen : session restored

    LoginScreen --> MainScreen : Google Sign-In success

    state MainScreen {
        [*] --> RoutineTab

        RoutineTab --> ListTab : select tab 1
        ListTab --> MapTab : select tab 2
        MapTab --> NotificationTab : select tab 3
        NotificationTab --> ProfileTab : select tab 4
        ProfileTab --> RoutineTab : select tab 0

        state RoutineTab {
            [*] --> WeekView
            WeekView --> DaySelected : tap day chip
            DaySelected --> HolidayMessage : date is a holiday
            DaySelected --> ClassList : normal school day
            ClassList --> ActiveClassHighlighted : current time within class window
        }

        state ListTab {
            [*] --> StudentView_or_TeacherView
            StudentView_or_TeacherView --> TeacherDirectory : Student selects "Teachers"
            StudentView_or_TeacherView --> MentorMapping : Student selects "Mentors"
            StudentView_or_TeacherView --> AdminDirectory : selects "Administration"
            StudentView_or_TeacherView --> StudentsBySection : Teacher selects a section
        }

        state NotificationTab {
            [*] --> AllAnnouncements
            AllAnnouncements --> FilteredByDean : category = Dean
            AllAnnouncements --> FilteredByDirector : category = Director
            AllAnnouncements --> FilteredByRegistrar : category = Registrar
            AllAnnouncements --> Favourites : starred items pinned to top
            AllAnnouncements --> ExpandedCard : tap notification card
        }

        state ProfileTab {
            [*] --> CollapsedCard
            CollapsedCard --> ExpandedCardWithLogout : tap card
            ExpandedCardWithLogout --> LoginScreen : tap Logout
        }
    }

    MainScreen --> LoginScreen : session cleared / logout
```

---

### Background Services

The app runs three independent background mechanisms to keep users informed even when the app is closed.

```mermaid
flowchart TD
    subgraph App_Start["🚀 App Start (KampusLifeApplication.onCreate)"]
        NC["NotificationHelper\ncreateNotificationChannel()\n4 channels created"]
        MC["scheduleMidnightCheck()\nAlarmManager.setInexactRepeating\nRTC_WAKEUP, daily at 00:00"]
        AC["scheduleAnnouncementCheck()\nWorkManager.enqueueUniquePeriodicWork\nevery 15 minutes, requires NETWORK"]
    end

    subgraph Midnight_Alarm["⏰ Midnight Alarm Flow"]
        AR["AlarmReceiver\nonReceive()"]
        CCS["ClassCheckService\nstartForeground()"]
        LOOP["Monitoring Loop\nevery 5 min while active"]
        CHECK["checkClasses()\nLoad routine from LocalStorage\nFilter by user role + today's day"]
        NOTIFY_C["NotificationHelper\n.showNotification()\nClass is in 10–15 min"]
        STOP["isFinalClassStarted()?\nStop service 10 min\nafter last class begins"]
    end

    subgraph Announcement_Worker["📢 Announcement Worker (WorkManager)"]
        AW["AnnouncementWorker\ndoWork()"]
        LOAD_U["Check user logged in\n(LocalStorage.loadUser)"]
        FETCH_A["Fetch /Announcement\nfrom REST API"]
        COMPARE["remote.size > local.size?"]
        SHOW_N["showNotification()\nfor each new announcement\ntype = Announcement\ndataId = announcement.id"]
        SAVE_A["LocalStorage.saveData\n(KEY_NOTIFICATIONS)"]
        RETRY["Result.retry()\non network failure"]
    end

    subgraph Notification_Tap["👆 Notification Deep-Link"]
        DEEP["User taps notification"]
        INTENT["PendingIntent → MainActivity\nTARGET_TAB = 3 (Announcements)\nor TARGET_TAB = 0 (Routine)"]
        EXPAND["EXPAND_NOTIFICATION_ID\n→ auto-expands that card"]
    end

    App_Start --> AR
    AR --> CCS --> LOOP --> CHECK --> NOTIFY_C
    LOOP --> STOP
    App_Start --> AW --> LOAD_U --> FETCH_A --> COMPARE
    COMPARE -->|"Yes – new items found"| SHOW_N --> SAVE_A
    COMPARE -->|"No change"| AW
    FETCH_A -->|"Exception"| RETRY
    NOTIFY_C --> DEEP --> INTENT --> EXPAND

    style App_Start fill:#0f3460,color:#e0e0e0
    style Midnight_Alarm fill:#1a1a2e,color:#e0e0e0
    style Announcement_Worker fill:#16213e,color:#e0e0e0
    style Notification_Tap fill:#0d0d0d,color:#e0e0e0
```

#### Notification Channels

| Channel ID | Name | Priority | Sound | Use Case |
|------------|------|----------|-------|----------|
| `routine_first_notifications` | First Class Alert | HIGH | 🔔 Alarm | First lecture of the day |
| `routine_general_notifications` | Class Reminders | HIGH | 🔔 Notification | Subsequent lectures |
| `announcement_notifications` | Announcements | DEFAULT | 🔔 Notification | New campus announcements |
| `kampus_life_service` | Monitoring Service | LOW | Silent | Ongoing foreground service |

---

### Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 2.3.10 |
| UI Framework | Jetpack Compose (BOM) | 2024.09.00 |
| Min SDK | Android 8.0 (Oreo) | API 26 |
| Target / Compile SDK | Android 16 | API 36 |
| Authentication | Firebase Auth + Google Credential Manager | Firebase BOM 33.10.0 |
| Auth Fallback | Google Sign-In (legacy) | play-services-auth 21.5.1 |
| User Profiles | Firebase Firestore | Firebase BOM 33.10.0 |
| Networking | Retrofit 3 + OkHttp 5 + Gson | 3.0.0 / 5.3.2 |
| Offline Cache | SharedPreferences + Gson | 2.13.2 |
| Image Loading | Coil (Compose) | 2.7.0 |
| Background Tasks | WorkManager | 2.10.0 |
| Foreground Service | Android Service + AlarmManager | — |
| Coroutines | kotlinx.coroutines | 1.10.2 |
| Build | Gradle (Kotlin DSL) + Version Catalog | AGP 9.0.1 |

---

## 🧭 Module: Navigation & Rotation

> **Path:** `Module_Navigation_Rotation/`  
> A self-contained Android module that demonstrates real-time **device orientation tracking** and **step-based map navigation** using hardware sensors. Built with traditional Android Views (AppCompat), not Compose.

### Features

- 🧲 **Compass** — reads the device's Rotation Vector Sensor to compute the magnetic azimuth (0°–360°), smoothed using a configurable low-pass filter (`smoothingFactor = 0.1`)
- 🚶 **Pedometer** — uses the hardware Step Counter sensor to count steps since the session began
- 🗺️ **Map simulation** — a background `ImageView` rotates with the compass heading and translates (pans) in the direction of travel with each step
- 🔒 **Permission handling** — requests `ACTIVITY_RECOGNITION` at runtime on Android Q+
- ⏱️ **Sensor timeout** — if sensors fail to initialise within 10 seconds, a loading overlay is hidden and the user is notified

### Sensor Pipeline

```mermaid
flowchart TD
    subgraph Hardware["📡 Device Hardware"]
        RVS["Rotation Vector Sensor\n(TYPE_ROTATION_VECTOR)"]
        SC["Step Counter Sensor\n(TYPE_STEP_COUNTER)"]
    end

    subgraph SensorManager["🔁 SensorManager (SENSOR_DELAY_GAME)"]
        onSC["onSensorChanged(event)"]
    end

    subgraph CompassPipeline["🧭 Compass Pipeline"]
        RM["getRotationMatrixFromVector()\n→ 3×3 rotation matrix"]
        OA["getOrientation()\n→ orientationAngles[3]"]
        AZ["azimuthRad → azimuthDeg\nnormalizeAngle() → 0°–360°"]
        PITCH["Pitch filter\n|pitch| < 80° → accept reading\n(rejects face-up/down)"]
        ROT_VAL["rotationVal updated"]
        SMOOTH["updateArrowRotation()\nshortestAngleDiff() + smoothingFactor\nbg.rotation = current + diff × 0.1"]
        TV_MAG["tvMagnetometer.text\n= \"Compass: ${azimuth}°\""]
    end

    subgraph PedometerPipeline["🚶 Pedometer Pipeline"]
        INIT_STEP["initialStepCount set on\nfirst event (session baseline)"]
        STEP_DIFF["stepDiff = currentSteps − lastStepValue"]
        MOVE["calculateMovementDirection(stepDiff, azimuthRad)\nmoveX = sin(az) × scale × diff × 0.5\nmoveY = cos(az) × scale × diff × 0.5"]
        ANIM["bg.animate()\n.translationXBy(−moveX)\n.translationYBy(moveY)\n.duration(200ms)"]
        TV_GYRO["tvGyrometer.text\n= \"Pedometer: $stepsSinceMapResume\""]
    end

    subgraph Lifecycle["♻️ Activity Lifecycle"]
        RESUME["onResume()\nregisterListener() for both sensors\nstart 10s timeout handler"]
        PAUSE["onPause()\nunregisterListener()\nreset initialStepCount\ncancel timeout"]
    end

    Hardware --> SensorManager
    SensorManager --> onSC
    onSC -->|"TYPE_ROTATION_VECTOR"| RM --> OA --> AZ --> PITCH --> ROT_VAL --> SMOOTH --> TV_MAG
    onSC -->|"TYPE_STEP_COUNTER"| INIT_STEP --> STEP_DIFF --> MOVE --> ANIM --> TV_GYRO
    Lifecycle --> SensorManager

    style Hardware fill:#0f3460,color:#e0e0e0
    style CompassPipeline fill:#16213e,color:#e0e0e0
    style PedometerPipeline fill:#1a1a2e,color:#e0e0e0
    style Lifecycle fill:#0d0d0d,color:#e0e0e0
```

---

## 🎨 Module: Base UI

> **Path:** `Module_Base_UI/`  
> A UI prototype and reusable auth library that served as the development sandbox before `Kampus_Life_Official`. Contains a **`feature-auth`** library module with a thorough [Role-Based Access Guide](Module_Base_UI/feature-auth/ROLE_ACCESS_GUIDE.md).

**`feature-auth` key exports:**

| Export | Type | Description |
|--------|------|-------------|
| `AuthSession` | `object` (singleton) | Holds the current `AuthUser` as a `StateFlow`; cleared on logout |
| `AuthUser` | `data class` | Complete user model (role, email, roll, department, uid, photo) |
| `UserRole` | `enum` | `STUDENT`, `TEACHER`, `UNKNOWN` |
| `AuthUtils` | Functions | `inferRoleFromEmail()`, `parseKiitStudentEmail()`, `parseKiitTeacherEmail()` |

> See [`ROLE_ACCESS_GUIDE.md`](Module_Base_UI/feature-auth/ROLE_ACCESS_GUIDE.md) for complete integration examples, Firestore security rules, and ViewModel patterns.

---

## 🚀 Getting Started

### Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Android Studio | Ladybug (2024.2+) |
| JDK | 11 |
| Android SDK | API 26+ |
| Gradle | Managed via wrapper (`./gradlew`) |

### 1 — Clone the repository

```bash
git clone https://github.com/Aritra-Chats/Kampus-Life-App.git
cd Kampus-Life-App
```

### 2 — Open the target project

Each sub-folder is an independent Android Studio project. Open the one you want to work on:

| Module | Path to open in Android Studio |
|--------|-------------------------------|
| Production app | `Kampus_Life_Official/` |
| Sensor demo | `Module_Navigation_Rotation/` |
| UI prototype | `Module_Base_UI/` |

### 3 — Configure Firebase (Kampus_Life_Official only)

The `google-services.json` files are already present for reference. For your own Firebase project:

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com)
2. Enable **Authentication → Google** sign-in
3. Enable **Firestore Database**
4. Download your `google-services.json` and replace `Kampus_Life_Official/app/google-services.json`
5. Update `webClientId` in `GoogleAuthManager.kt` with your OAuth 2.0 Web Client ID

### 4 — Build & run

```bash
# From the module directory (e.g. Kampus_Life_Official/)
./gradlew assembleDebug
```

Or use the **Run** button in Android Studio with a connected device / emulator running API 26+.

---

## 🗺️ Project Roadmap

```mermaid
timeline
    title Kampus Life — Development Stages
    section Stage 1 · Foundation
        Module_Base_UI        : Base screen layouts
                              : NavTab and DetailsTab components
                              : Routine and List page prototypes
    section Stage 2 · Auth & Data
        feature-auth module   : Google Sign-In integration
                              : Firebase Auth + Firestore
                              : Role-based access (Student / Teacher)
                              : KIIT email validation and parsing
    section Stage 3 · Sensor Exploration
        Module_Navigation_Rotation : Rotation vector compass
                                   : Step-counter map navigation
                                   : Sensor lifecycle management
    section Stage 4 · Production
        Kampus_Life_Official  : Offline-first data layer
                              : Complete 5-tab UI (role-adaptive)
                              : Smart class reminder service
                              : WorkManager announcement polling
                              : Deep-link notification tapping
                              : Session persistence and restore
```

---

<div align="center">

**Built with ❤️ for KIIT University**  
*Contributions, bug reports, and feature requests are welcome via [Issues](https://github.com/Aritra-Chats/Kampus-Life-App/issues) and [Pull Requests](https://github.com/Aritra-Chats/Kampus-Life-App/pulls).*

</div>