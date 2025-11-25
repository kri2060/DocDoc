# DocumentViewer

> A comprehensive Android document management and PDF toolkit built with Jetpack Compose

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.0-green.svg)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-8.0%2B-brightgreen.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange.svg)](https://developer.android.com/topic/architecture)
[![License](https://img.shields.io/badge/License-Academic-red.svg)](LICENSE)

## Overview

DocumentViewer is a feature-rich Android application designed to streamline document management, viewing, and manipulation. Built entirely with modern Android development tools and following clean architecture principles, this app provides an intuitive interface for handling PDFs, Office documents, images, and more.

**Project Team:**
- Krishna (Roll No: 366)
- Adarsh (Roll No: 166)
- Mahesh (Roll No: 165)

**Project Theme:** Document Management & PDF Toolkit

---

## Features

### Core Functionality
- **Multi-Format Support** - PDF, Word, Excel, PowerPoint, Images, Video, Audio
- **Smart File Browser** - Category-based navigation with recent files and favorites
- **Advanced Search** - Full-text search with OCR support and intelligent indexing
- **PDF Operations** - Merge, split, compress, convert, password protect
- **Document Conversion** - Convert images to PDF, extract PDF pages, Office to PDF
- **Last Page Recall** - Automatically resume reading from where you left off
- **Annotations** - PDF markup with highlighting, drawing, shapes, and signatures
- **Text-to-Speech** - Audio playback of documents with adjustable speed
- **ZIP Creator** - Compress multiple files and folders
- **OCR Scanner** - Extract text from images and PDFs
- **Notepad** - Built-in note-taking with auto-save

### Technical Highlights
- **100% Jetpack Compose** - Modern declarative UI
- **Clean Architecture** - Separation of concerns with MVVM pattern
- **Reactive Programming** - Kotlin Coroutines and Flow
- **Offline-First** - Full functionality without internet
- **Material Design 3** - Beautiful, consistent UI with dynamic theming
- **Background Processing** - WorkManager for long-running operations
- **Type-Safe Navigation** - Navigation Compose with type safety

---

## Screenshots

> *Screenshots to be added*

---

## Technology Stack

### Core Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 1.9.22 | Primary language |
| **Jetpack Compose** | 1.6.0 | UI framework |
| **Material 3** | Latest | Design system |
| **Hilt** | 2.50 | Dependency injection |
| **Room** | 2.6.1 | Local database |
| **Navigation Compose** | 2.7.7 | Navigation |
| **Coroutines** | 1.7.3 | Async operations |
| **WorkManager** | 2.9.0 | Background tasks |

### Key Libraries
| Library | Purpose |
|---------|---------|
| **iText 7** | PDF creation and manipulation |
| **Apache POI** | Office document processing |
| **Zip4j** | ZIP compression |
| **Coil** | Image loading |
| **Accompanist** | Permissions handling |
| **DataStore** | Preferences storage |

---

## Architecture

This project follows **Clean Architecture** principles with three distinct layers:

```
┌─────────────────────────────────────┐
│      Presentation Layer             │
│   (UI, ViewModels, Compose)         │
└──────────────┬──────────────────────┘
               │
               ↓ Use Cases
┌─────────────────────────────────────┐
│       Domain Layer                  │
│   (Business Logic, Use Cases)       │
└──────────────┬──────────────────────┘
               │
               ↓ Repository Interface
┌─────────────────────────────────────┐
│        Data Layer                   │
│  (Repository, Room, File System)    │
└─────────────────────────────────────┘
```

### Project Structure

```
app/src/main/java/com/documentviewer/
├── core/              # Utilities and helpers
│   ├── preferences/   # DataStore preferences
│   └── utils/         # File, PDF, conversion utilities
├── data/              # Data layer
│   ├── local/         # Room database (DAOs, entities)
│   ├── model/         # Data models
│   └── repository/    # Repository implementations
├── domain/            # Business logic
│   └── usecase/       # Use cases
└── ui/                # Presentation layer
    ├── components/    # Reusable UI components
    ├── home/          # Home screen
    ├── navigation/    # Navigation graph
    ├── theme/         # Material 3 theme
    └── viewer/        # Document viewers
```

For a detailed explanation of every file and directory, see [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md).

---

## Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17 or later
- **Android SDK** 34
- **Minimum Android Version** 8.0 (API 26)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/DocumentViewer.git
   cd DocumentViewer
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or use `Shift + F10`

### Build from Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

---

## Configuration

### Permissions

The app requires the following permissions:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

All permissions are requested at runtime following Android best practices.

### Gradle Configuration

Key configuration in `app/build.gradle.kts`:

```kotlin
android {
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```

---

## Database Schema

The app uses Room for local data persistence:

| Entity | Purpose | Key Fields |
|--------|---------|------------|
| **RecentFileEntity** | Track recently opened files | path, name, size, lastAccessed |
| **FavoriteEntity** | Store user favorites | path, name, addedAt |
| **ReadingPositionEntity** | Remember reading positions | filePath, pageNumber, scrollOffset |
| **SearchIndexEntity** | Full-text search index | filePath, content, pageNumber |
| **NoteEntity** | User notes | title, content, createdAt, modifiedAt |

---

## Key Features Explained

### PDF Operations

**Merge PDFs**
```kotlin
// Select multiple PDFs → Reorder → Merge → Save to "My Creation"
```

**Convert Images to PDF**
```kotlin
// Select images → Configure page size, orientation → Generate PDF
```

**Extract Pages**
```kotlin
// Select PDF → Choose page range → Extract as images or new PDF
```

**Password Protection**
```kotlin
// Add or remove password protection from PDFs
```

### Smart Search

- Full-text search across all documents
- OCR-based content extraction
- Filter by type, date, size, location
- Search history and suggestions
- Room database indexing for fast queries

### Text-to-Speech

- Foreground service for background playback
- Adjustable reading speed
- Play/Pause/Stop controls
- Real-time text highlighting
- Continue from last position

### PDF Annotations

- Highlighting and underlining
- Freehand drawing
- Text annotations
- Shapes (rectangles, circles, arrows)
- Digital signatures
- Undo/Redo support
- Save annotated PDFs

---

## Development Guide

### Adding a New Screen

1. Create package in `/ui/`
2. Create `<Feature>Screen.kt` with Composable function
3. Create `<Feature>ViewModel.kt` for state management
4. Define `<Feature>State.kt` data class
5. Add route to `Screen.kt` sealed class
6. Update `NavGraph.kt` with new destination

### Adding a New Feature

1. Define data model in `/data/model/`
2. Create database entity if persistence needed
3. Create DAO for database operations
4. Implement repository in `/data/repository/`
5. Create use case in `/domain/usecase/`
6. Build UI with ViewModel in `/ui/`

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Write KDoc comments for public APIs
- Keep functions small and focused
- Prefer composition over inheritance

---

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Test Structure
```
app/src/
├── test/           # Unit tests
│   └── java/
│       └── com/documentviewer/
│           ├── repository/
│           ├── usecase/
│           └── viewmodel/
└── androidTest/    # Instrumented tests
    └── java/
        └── com/documentviewer/
            └── ui/
```

---

## Performance Optimizations

- **Lazy Loading** - LazyColumn and LazyGrid for efficient lists
- **Background Processing** - WorkManager for long operations
- **Database Indexing** - Indexed queries for fast search
- **Image Caching** - Coil for efficient image loading
- **State Management** - StateFlow for reactive UI updates
- **Parallel Builds** - Gradle parallel execution enabled

---

## Common Issues & Solutions

### Build Errors

**Problem:** `SDK not found`
```bash
# Solution: Set SDK path in local.properties
sdk.dir=/path/to/Android/Sdk
```

**Problem:** `Compose compiler version mismatch`
```kotlin
// Solution: Ensure versions match in build.gradle.kts
kotlinCompilerExtensionVersion = "1.5.10"
kotlin_version = "1.9.22"
```

### Runtime Issues

**Problem:** `FileProvider authority conflict`
```xml
<!-- Solution: Use unique authority in AndroidManifest.xml -->
android:authorities="${applicationId}.fileprovider"
```

**Problem:** `Permission denied`
```kotlin
// Solution: Request runtime permissions before file operations
val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted -> /* handle result */ }
```

---

## Roadmap

### Planned Features
- [ ] Cloud storage integration (Google Drive, Dropbox)
- [ ] Multi-language OCR support
- [ ] Document collaboration features
- [ ] Advanced PDF editing (form filling, page manipulation)
- [ ] Document templates
- [ ] Export to additional formats (EPUB, HTML)
- [ ] Dark mode improvements
- [ ] Tablet optimization
- [ ] Widget support

---

## Contributing

This is an academic project created by students at [Your Institution]. While we're not accepting external contributions, feel free to fork the repository for educational purposes.

### For Educational Use

If you're using this project for learning:
1. Fork the repository
2. Study the architecture and implementation
3. Experiment with new features
4. Share your learnings with the community

---

## License

This project is an academic submission and is not licensed for commercial use.

**Academic Project - For Educational Purposes Only**

---

## Documentation

- **[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)** - Detailed explanation of project structure, files, and architecture
- **[AndroidManifest.xml](app/src/main/AndroidManifest.xml)** - App components and permissions
- **[build.gradle.kts](app/build.gradle.kts)** - Build configuration and dependencies

---

## Acknowledgments

- **Android Team** for Jetpack Compose and modern Android libraries
- **Apache Software Foundation** for POI and Commons libraries
- **iText Software** for PDF processing capabilities
- **Open Source Community** for various libraries used in this project

---

## Contact

**Project Team:**
- Krishna (366)
- Adarsh (166)
- Mahesh (165)

**Project Theme:** Document Management & PDF Toolkit

---

## Project Statistics

- **Lines of Code:** ~15,000+
- **Screens:** 15+
- **Database Tables:** 5
- **Use Cases:** 20+
- **Supported Formats:** PDF, DOCX, XLSX, PPTX, Images, Video, Audio
- **Minimum Android Version:** 8.0 (API 26)
- **Target Android Version:** 14 (API 34)

---

**Built with ❤️ using Jetpack Compose**

*Last Updated: November 2025*
