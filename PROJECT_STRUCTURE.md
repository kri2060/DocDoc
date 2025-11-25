# DocumentViewer - Project Structure Guide

## Table of Contents
1. [Overview](#overview)
2. [Directory Structure](#directory-structure)
3. [Configuration Files](#configuration-files)
4. [Source Code Organization](#source-code-organization)
5. [Key Files Explained](#key-files-explained)
6. [Build System](#build-system)

---

## Overview

DocumentViewer is an Android application built with **Jetpack Compose** following **Clean Architecture** principles with the **MVVM** pattern. This guide explains every file and directory in the project, helping developers understand the purpose and organization of the codebase.

---

## Directory Structure

```
DocumentViewer/
├── .claude/                      # Claude Code IDE configuration
├── .git/                         # Git version control data
├── .gradle/                      # Gradle build cache (auto-generated)
├── .idea/                        # Android Studio IDE settings
├── .vscode/                      # VS Code IDE settings
├── app/                          # Main application module
│   ├── build/                    # Build outputs (auto-generated)
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/documentviewer/
│   │       │   ├── core/         # Core utilities and helpers
│   │       │   │   ├── preferences/
│   │       │   │   └── utils/
│   │       │   ├── data/         # Data layer
│   │       │   │   ├── local/
│   │       │   │   ├── model/
│   │       │   │   └── repository/
│   │       │   ├── domain/       # Business logic layer
│   │       │   │   └── usecase/
│   │       │   └── ui/           # Presentation layer
│   │       │       ├── components/
│   │       │       ├── home/
│   │       │       ├── navigation/
│   │       │       ├── theme/
│   │       │       └── viewer/
│   │       └── res/              # Android resources
│   │           ├── drawable/
│   │           ├── mipmap/
│   │           ├── values/
│   │           └── xml/
│   └── build.gradle.kts          # App module build configuration
├── gradle/                       # Gradle wrapper files
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build/                        # Project build outputs (auto-generated)
├── build.gradle.kts              # Root project build configuration
├── gradle.properties             # Gradle properties and JVM settings
├── gradlew                       # Gradle wrapper script (Unix/Mac)
├── gradlew.bat                   # Gradle wrapper script (Windows)
├── local.properties              # Local SDK paths (not in version control)
├── settings.gradle.kts           # Gradle settings and module inclusion
├── .gitignore                    # Git ignore rules
├── PROJECT_STRUCTURE.md          # This file
└── README.md                     # Project documentation
```

---

## Configuration Files

### Root Level Configuration

#### `settings.gradle.kts`
**Purpose:** Defines the Gradle project structure and module inclusion.

```kotlin
pluginManagement {
    repositories {
        google()        // Android libraries
        mavenCentral()  // Standard Java/Kotlin libraries
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }  // Third-party libraries
    }
}

rootProject.name = "DocumentViewer"
include(":app")  // Includes the app module
```

**Key Points:**
- Configures repository locations for downloading dependencies
- Defines available modules (currently only `:app`)
- Sets up plugin management for build tools

---

#### `build.gradle.kts` (Root)
**Purpose:** Project-level build configuration that applies to all modules.

```kotlin
buildscript {
    extra.apply {
        set("compose_version", "1.6.0")
        set("kotlin_version", "1.9.22")
        set("hilt_version", "2.50")
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
```

**Key Points:**
- Defines shared version variables for consistency
- Declares plugins used across the project (but not applied at root level)
- Kotlin version: 1.9.22
- Compose version: 1.6.0
- Hilt (Dependency Injection) version: 2.50

---

#### `gradle.properties`
**Purpose:** Global Gradle configuration and JVM settings.

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
org.gradle.parallel=true
org.gradle.caching=true
```

**Key Points:**
- Allocates 2GB RAM to Gradle build process
- Enables AndroidX library support
- Enables parallel builds for faster compilation
- Enables build caching to speed up subsequent builds

---

#### `local.properties`
**Purpose:** Stores local machine-specific paths (not committed to version control).

```properties
sdk.dir=/path/to/Android/Sdk
```

**Key Points:**
- Contains Android SDK location
- Automatically generated by Android Studio
- Listed in `.gitignore` (each developer has their own)

---

#### `gradlew` and `gradlew.bat`
**Purpose:** Gradle wrapper scripts that ensure consistent Gradle version across all machines.

- `gradlew` - Unix/Linux/Mac script
- `gradlew.bat` - Windows script

**Usage:**
```bash
./gradlew assembleDebug    # Build debug APK
./gradlew installDebug     # Install app on connected device
./gradlew clean            # Clean build artifacts
./gradlew test             # Run unit tests
```

---

### App Module Configuration

#### `app/build.gradle.kts`
**Purpose:** Application module build configuration - the most important build file.

**Structure:**

```kotlin
plugins {
    id("com.android.application")              // Android app plugin
    id("org.jetbrains.kotlin.android")         // Kotlin support
    id("com.google.dagger.hilt.android")       // Dependency injection
    id("com.google.devtools.ksp")              // Annotation processing
}

android {
    namespace = "com.documentviewer"           // App package name
    compileSdk = 34                            // SDK version to compile against

    defaultConfig {
        applicationId = "com.documentviewer"    // Unique app identifier
        minSdk = 26                            // Android 8.0+ required
        targetSdk = 34                         // Target Android 14
        versionCode = 1                        // App version number (increment for updates)
        versionName = "1.0"                    // User-facing version string
    }

    buildTypes {
        release {
            isMinifyEnabled = false            // Code shrinking (ProGuard)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17  // Java 17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"                      // Kotlin compiles to Java 17 bytecode
    }

    buildFeatures {
        compose = true                         // Enable Jetpack Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"  // Compose compiler version
    }
}

dependencies {
    // Dependencies listed below
}
```

**Key Dependencies:**

| Category | Library | Purpose |
|----------|---------|---------|
| **Core** | `androidx.core:core-ktx` | Kotlin extensions for Android |
| **UI** | `androidx.compose.ui:ui` | Jetpack Compose UI toolkit |
| **Material** | `androidx.compose.material3:material3` | Material Design 3 components |
| **Navigation** | `androidx.navigation:navigation-compose` | Navigation between screens |
| **ViewModel** | `androidx.lifecycle:lifecycle-viewmodel-compose` | MVVM architecture support |
| **Database** | `androidx.room:room-ktx` | Local SQLite database with coroutines |
| **DI** | `com.google.dagger:hilt-android` | Dependency injection framework |
| **Background** | `androidx.work:work-runtime-ktx` | Background task scheduling |
| **PDF** | `com.itextpdf:itext7-core` | PDF creation and manipulation |
| **Office** | `org.apache.poi:poi-ooxml` | Word/Excel/PowerPoint processing |
| **Compression** | `net.lingala.zip4j:zip4j` | ZIP file operations |
| **Images** | `io.coil-kt:coil-compose` | Image loading and caching |
| **Permissions** | `com.google.accompanist:accompanist-permissions` | Runtime permission handling |

---

#### `app/src/main/AndroidManifest.xml`
**Purpose:** App manifest - declares app components, permissions, and metadata.

**Structure:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- PERMISSIONS -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:name=".DocumentViewerApp"       <!-- Custom Application class -->
        android:allowBackup="true"              <!-- Enable cloud backup -->
        android:icon="@mipmap/ic_launcher"      <!-- App icon -->
        android:label="@string/app_name"        <!-- App name -->
        android:theme="@style/Theme.DocumentViewer">

        <!-- MAIN ACTIVITY -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <data android:mimeType="application/pdf" />
            </intent-filter>
        </activity>

        <!-- FILE PROVIDER -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

        <!-- TEXT-TO-SPEECH SERVICE -->
        <service
            android:name=".ui.viewer.TtsService"
            android:exported="false"
            android:foregroundServiceType="mediaPlayback" />
    </application>
</manifest>
```

**Key Components:**

1. **Permissions:** Required for file access, camera, notifications, background services
2. **MainActivity:** Entry point with intent filters for PDF files
3. **FileProvider:** Secure file sharing between apps
4. **TtsService:** Background service for text-to-speech functionality

---

## Source Code Organization

### `/app/src/main/java/com/documentviewer/`

The source code follows **Clean Architecture** with three main layers:

```
com.documentviewer/
├── core/              # Cross-cutting concerns
├── data/              # Data layer
├── domain/            # Business logic layer
└── ui/                # Presentation layer
```

---

### 1. Core Layer (`/core/`)

**Purpose:** Shared utilities, preferences, and helper classes used across all layers.

#### `/core/preferences/`
- **`PdfPositionPreferences.kt`** - Saves/restores last reading position in PDFs
- **`ThemePreferences.kt`** - Manages dark/light theme settings

#### `/core/utils/`
- **`DocumentCache.kt`** - Caches document metadata for quick access
- **`DocumentConverter.kt`** - Converts between document formats (PDF, Word, Excel)
- **`FileUtils.kt`** - File operations (copy, move, delete, rename)
- **`PdfUtils.kt`** - PDF-specific utilities (merge, split, compress)

---

### 2. Data Layer (`/data/`)

**Purpose:** Manages data sources (database, file system) and provides data to the domain layer.

#### `/data/local/` - Database

**`AppDatabase.kt`** - Room database definition
```kotlin
@Database(
    entities = [
        RecentFileEntity::class,
        FavoriteEntity::class,
        ReadingPositionEntity::class,
        SearchIndexEntity::class,
        NoteEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun readingPositionDao(): ReadingPositionDao
    abstract fun searchIndexDao(): SearchIndexDao
    abstract fun noteDao(): NoteDao
}
```

#### `/data/local/entity/` - Database Tables

| Entity | Purpose | Key Fields |
|--------|---------|------------|
| **RecentFileEntity** | Tracks recently opened files | path, name, size, lastAccessed, type |
| **FavoriteEntity** | Stores user-favorited files | path, name, size, addedAt, type |
| **ReadingPositionEntity** | Remembers last reading position | filePath, pageNumber, scrollOffset |
| **SearchIndexEntity** | Full-text search index | filePath, content, pageNumber |
| **NoteEntity** | User-created notes | title, content, createdAt, modifiedAt |

#### `/data/local/dao/` - Data Access Objects

Each DAO provides database operations for its entity:
- **`RecentFileDao.kt`** - Insert, query, delete recent files
- **`FavoriteDao.kt`** - Manage favorites
- **`ReadingPositionDao.kt`** - Save/retrieve reading positions
- **`SearchIndexDao.kt`** - Full-text search queries
- **`NoteDao.kt`** - CRUD operations for notes

#### `/data/model/` - Data Models

**`DocumentFile.kt`** - Represents a document with metadata
```kotlin
data class DocumentFile(
    val name: String,
    val path: String,
    val size: Long,
    val mimeType: String?,
    val dateModified: Long,
    val type: DocumentType,
    val thumbnailPath: String? = null
)

enum class DocumentType {
    PDF, WORD, EXCEL, POWERPOINT, IMAGE, VIDEO, AUDIO, FOLDER, OTHER
}
```

#### `/data/repository/` - Repository Pattern

Repositories abstract data sources and provide clean APIs:
- **`DocumentRepository.kt`** - File scanning and management
- **`FavoriteRepository.kt`** - Favorite operations
- **`NoteRepository.kt`** - Note management
- **`PdfRepository.kt`** - PDF operations (merge, split, convert)
- **`SearchRepository.kt`** - Document search and indexing

---

### 3. Domain Layer (`/domain/`)

**Purpose:** Contains business logic independent of Android framework.

#### `/domain/usecase/` - Use Cases

Each use case represents a single business operation:
- **`GetRecentFilesUseCase`** - Fetch recent files
- **`ScanDocumentsUseCase`** - Scan device for documents
- **`MergePdfsUseCase`** - Merge multiple PDFs
- **`ConvertImagesToPdfUseCase`** - Create PDF from images
- **`IndexDocumentUseCase`** - Index document for search
- **`ExtractTextFromPdfUseCase`** - OCR text extraction

**Example Structure:**
```kotlin
class GetRecentFilesUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    operator fun invoke(): Flow<List<DocumentFile>> {
        return repository.getRecentFiles()
    }
}
```

---

### 4. UI Layer (`/ui/`)

**Purpose:** Presentation layer with Jetpack Compose UI.

#### `/ui/theme/` - App Theming

- **`Color.kt`** - Color definitions
- **`Theme.kt`** - Material 3 theme configuration
- **`Type.kt`** - Typography definitions

#### `/ui/components/` - Reusable UI Components

- **`DocumentCard.kt`** - Card displaying document info
- **`CategoryCard.kt`** - Category grid item
- **`SearchBar.kt`** - Custom search bar
- **`LoadingIndicator.kt`** - Progress indicators
- **`EmptyState.kt`** - Empty list placeholder

#### `/ui/navigation/` - Navigation

- **`NavGraph.kt`** - Navigation graph with all routes
- **`Screen.kt`** - Sealed class defining all screens

#### Screen Modules

Each feature has its own package with ViewModel and UI:

- **`/ui/home/`** - Home screen with categories and tools
- **`/ui/filelist/`** - File browsing by category
- **`/ui/viewer/`** - Document viewers (PDF, images, office docs)
- **`/ui/pdf/`** - PDF operations (merge, split, convert)
- **`/ui/tools/`** - Utility tools (OCR, ZIP, notepad)
- **`/ui/settings/`** - App settings

**Typical Structure:**
```
/ui/home/
├── HomeScreen.kt       # Composable UI
├── HomeViewModel.kt    # State management
└── HomeState.kt        # UI state data class
```

---

## Key Files Explained

### Application Entry Points

#### `DocumentViewerApp.kt`
**Purpose:** Custom Application class for initialization.

```kotlin
@HiltAndroidApp
class DocumentViewerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize libraries
        // Set up crash reporting
        // Configure logging
    }
}
```

#### `MainActivity.kt`
**Purpose:** Single activity hosting all Compose screens.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocumentViewerTheme {
                NavGraph()
            }
        }
    }
}
```

---

### Resource Files (`/app/src/main/res/`)

#### `/values/strings.xml`
**Purpose:** All user-facing text strings for internationalization.

```xml
<resources>
    <string name="app_name">Document Viewer</string>
    <string name="home_title">Home</string>
    <!-- ... more strings ... -->
</resources>
```

#### `/values/themes.xml`
**Purpose:** Material Design theme definitions.

#### `/xml/file_paths.xml`
**Purpose:** Defines paths for FileProvider to share files securely.

```xml
<paths>
    <external-path name="external_files" path="." />
    <cache-path name="cache" path="." />
</paths>
```

#### `/xml/backup_rules.xml`
**Purpose:** Defines what data is included in cloud backups.

#### `/xml/data_extraction_rules.xml`
**Purpose:** Android 12+ data transfer rules.

---

## Build System

### Gradle Workflow

```
./gradlew assembleDebug
         ↓
1. Read settings.gradle.kts → Identify modules
2. Read build.gradle.kts (root) → Apply plugins
3. Read app/build.gradle.kts → Configure app module
4. Download dependencies from repositories
5. Compile Kotlin → Java bytecode
6. Process Android resources
7. Run Room annotation processors (KSP)
8. Run Hilt annotation processors (KSP)
9. Merge manifests
10. Package APK
11. Sign APK (debug keystore)
12. Output: app/build/outputs/apk/debug/app-debug.apk
```

### Build Variants

- **Debug:** Development build with debugging enabled
- **Release:** Production build (requires signing configuration)

### Generated Directories (Not in Version Control)

- **`.gradle/`** - Gradle build cache
- **`build/`** - Project build outputs
- **`app/build/`** - App module build outputs
- **`.idea/`** - Android Studio settings (partially tracked)

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ Screen   │  │ViewModel │  │  State   │      │
│  │(Compose) │←→│          │←→│ (Flow)   │      │
│  └──────────┘  └──────────┘  └──────────┘      │
└─────────────┬───────────────────────────────────┘
              │
              ↓ Use Cases
┌─────────────────────────────────────────────────┐
│               Domain Layer                       │
│  ┌────────────────────────────────────┐         │
│  │  Use Cases (Business Logic)        │         │
│  │  - GetRecentFilesUseCase           │         │
│  │  - MergePdfsUseCase                │         │
│  └────────────────────────────────────┘         │
└─────────────┬───────────────────────────────────┘
              │
              ↓ Repository Interface
┌─────────────────────────────────────────────────┐
│                Data Layer                        │
│  ┌───────────────┐       ┌──────────────┐      │
│  │ Repository    │       │   Database   │      │
│  │ Implementation│←─────→│   (Room)     │      │
│  └───────────────┘       └──────────────┘      │
│         ↕                                        │
│  ┌───────────────┐                              │
│  │  File System  │                              │
│  │  (MediaStore) │                              │
│  └───────────────┘                              │
└─────────────────────────────────────────────────┘
```

---

## Development Guidelines

### Adding a New Screen

1. Create screen package in `/ui/`
2. Create `<Feature>Screen.kt` with Composable
3. Create `<Feature>ViewModel.kt` with StateFlow
4. Create `<Feature>State.kt` for UI state
5. Add route to `/ui/navigation/Screen.kt`
6. Add navigation in `NavGraph.kt`

### Adding a New Feature

1. Define data model in `/data/model/`
2. Create database entity if needed in `/data/local/entity/`
3. Create DAO in `/data/local/dao/`
4. Create repository in `/data/repository/`
5. Create use case in `/domain/usecase/`
6. Create ViewModel and Screen in `/ui/`

---

## Common Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Check for dependency updates
./gradlew dependencyUpdates

# Generate code coverage report
./gradlew jacocoTestReport

# Lint checks
./gradlew lint
```

---

## Troubleshooting

### Common Issues

1. **Build fails with "SDK not found"**
   - Ensure `local.properties` has correct SDK path

2. **Compose compiler version mismatch**
   - Check `kotlinCompilerExtensionVersion` matches Kotlin version

3. **Room schema export warning**
   - Add schema export directory in `build.gradle.kts`

4. **Hilt/KSP errors**
   - Clean and rebuild project
   - Invalidate caches in Android Studio

---

**Last Updated:** November 2025
**Version:** 1.0
**Team:** Krishna (366), Adarsh (166), Mahesh (165)
