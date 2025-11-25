# Quick Start Guide

Get the Document Viewer app running in 5 minutes!

## Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Android SDK**: API 34 (Android 14)
- **Minimum Device SDK**: API 26 (Android 8.0)

## Setup Steps

### 1. Open Project in Android Studio

```bash
# Navigate to project directory
cd /home/kri2060/DocumentViewer

# Open with Android Studio
studio .
```

Or use Android Studio:
- File → Open
- Select the `DocumentViewer` folder
- Click OK

### 2. Sync Gradle

Android Studio will automatically prompt to sync. If not:
- Click "Sync Now" banner at the top
- Or: File → Sync Project with Gradle Files

**Wait for sync to complete** (~2-5 minutes first time)

### 3. Resolve Any Issues

#### Common Issues:

**Issue: SDK not found**
```
Solution: Tools → SDK Manager → Install:
- Android SDK 34
- Build Tools 34.0.0
```

**Issue: JDK version mismatch**
```
Solution: File → Project Structure → SDK Location
- Set JDK to version 17
```

**Issue: Gradle version**
```
Solution: Already configured in project
- Gradle 8.2
- Kotlin 1.9.22
```

### 4. Create Empty Resource Files (If Needed)

Some icon resources might be missing. Create placeholder icons or use built-in Material icons (already configured).

### 5. Build the Project

```bash
./gradlew build
```

Or in Android Studio:
- Build → Make Project (Ctrl+F9 / Cmd+F9)

### 6. Run on Device/Emulator

**Option A: Emulator**
```
1. Tools → Device Manager
2. Create Virtual Device
3. Select Pixel 5 or similar
4. System Image: API 34 (Android 14)
5. Click Play button in toolbar
```

**Option B: Physical Device**
```
1. Enable Developer Options on device
2. Enable USB Debugging
3. Connect device via USB
4. Click Run (Shift+F10)
```

## What You'll See

### First Launch

1. **Home Screen** with:
   - Team info at top: "Krishna (366) | Adarsh (166) | Mahesh (165)"
   - Project theme: "Document Management & PDF Toolkit"
   - Categories grid (PDF, Word, Excel, etc.)
   - Places section
   - PDF Operations
   - Tools

2. **Working Features**:
   - ✅ Home screen navigation
   - ✅ Notepad (create/view/delete notes)
   - ✅ Navigation between screens
   - ✅ Theme system (dark mode)

3. **Not Yet Functional** (need implementation):
   - File browsing (will show empty)
   - PDF operations (UI not complete)
   - Most other screens (show placeholder or crash)

## Testing the App

### Test 1: Home Screen
```
✓ Launch app
✓ See team info at top
✓ See all category cards
✓ Click categories (might show empty screens)
```

### Test 2: Notepad
```
1. Click "Notepad" in Tools section
2. Click + button
3. Create a note
4. Save and go back
5. See note in list
6. Click to edit
7. Delete note
```

### Test 3: Navigation
```
✓ Navigate to any section
✓ Use back button
✓ Navigation animations work
```

## Development Workflow

### Making Changes

1. **Edit Kotlin files** in `app/src/main/java/com/documentviewer/`
2. **Compose previews** - Add `@Preview` annotations:
   ```kotlin
   @Preview
   @Composable
   fun PreviewHomeScreen() {
       DocumentViewerTheme {
           HomeScreen(rememberNavController())
       }
   }
   ```
3. **Hot Reload** - Compose supports live editing
4. **Rebuild** - Click Run after changes

### Adding New Screens

Follow templates in [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md):

```kotlin
// 1. Create Screen file
app/src/main/java/com/documentviewer/ui/[feature]/[Name]Screen.kt

// 2. Create ViewModel
class [Name]ViewModel @Inject constructor() : ViewModel()

// 3. Add to Navigation
composable(Screen.[Name].route) {
    [Name]Screen(navController)
}

// 4. Test
Run app → Navigate to screen
```

## Debugging

### Enable Logging

Add to any file:
```kotlin
private const val TAG = "ClassName"

Log.d(TAG, "Message: $variable")
```

View logs:
```bash
# Terminal
adb logcat | grep DocumentViewer

# Or use Android Studio Logcat panel
```

### Database Inspection

```
View → Tool Windows → App Inspection → Database Inspector
- Select running app
- See all tables
- Query data live
```

### Layout Inspector

```
Tools → Layout Inspector
- See Compose hierarchy
- Check dimensions
- Inspect properties
```

## Common Tasks

### Change App Name
```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="app_name">Your Name</string>
```

### Change Theme Colors
```kotlin
// app/src/main/java/com/documentviewer/ui/theme/Color.kt
val PrimaryDark = Color(0xFFYOURCOLOR)
```

### Add New Dependency
```kotlin
// app/build.gradle.kts
dependencies {
    implementation("group:artifact:version")
}
```
Then sync Gradle.

### Clear App Data
```bash
adb shell pm clear com.documentviewer
```

### Uninstall App
```bash
adb uninstall com.documentviewer
```

## Building Release APK

### Debug APK (Testing)
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (Production)
```bash
# 1. Create keystore
keytool -genkey -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-alias

# 2. Configure signing in app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "password"
            keyAlias = "my-alias"
            keyPassword = "password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

# 3. Build
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

## Performance Tips

### 1. Enable R8 (Already configured)
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true  // ✓ Already set
    }
}
```

### 2. Profile App
```
Run → Profile 'app'
- CPU usage
- Memory allocation
- Network activity
```

### 3. Optimize Images
```
- Use vector drawables
- Compress PNG/JPG
- Use WebP format
```

### 4. Lazy Loading
```kotlin
// Use LazyColumn for lists
LazyColumn {
    items(largeList) { item ->
        ItemCard(item)
    }
}
```

## Troubleshooting

### App Crashes on Launch

**Check Hilt setup:**
```kotlin
// DocumentViewerApp.kt - Must have @HiltAndroidApp
@HiltAndroidApp
class DocumentViewerApp : Application()

// MainActivity.kt - Must have @AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

### Compose Preview Not Working

```kotlin
// Add preview dependencies
debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")
```

### Room Database Errors

```bash
# Clear database
adb shell
cd /data/data/com.documentviewer/databases
rm document_viewer_db*
```

### Gradle Sync Fails

```bash
# Clear Gradle cache
./gradlew clean
./gradlew --stop

# In Android Studio:
File → Invalidate Caches → Invalidate and Restart
```

## Next Steps

1. ✅ **App Running** - You have the home screen working
2. 📖 **Read README.md** - Understand the full project
3. 📝 **Read IMPLEMENTATION_GUIDE.md** - See how to add features
4. 💻 **Start Coding** - Implement FileListScreen first
5. 🧪 **Test** - Verify each feature as you build

## Quick Reference

### Project Structure
```
DocumentViewer/
├── app/
│   ├── src/main/
│   │   ├── java/com/documentviewer/
│   │   │   ├── data/         # Database, repositories
│   │   │   ├── ui/           # Screens, ViewModels
│   │   │   ├── core/         # Utilities
│   │   │   └── di/           # Dependency injection
│   │   ├── res/              # Resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── README.md                  # Full documentation
├── IMPLEMENTATION_GUIDE.md    # How to add features
├── PROJECT_SUMMARY.md         # What's done/todo
└── QUICKSTART.md             # This file
```

### Key Commands
```bash
# Build
./gradlew build

# Install debug
./gradlew installDebug

# Run tests
./gradlew test

# Clean
./gradlew clean

# Check dependencies
./gradlew dependencies
```

### Important Files
```
MainActivity.kt          - Entry point
DocumentViewerApp.kt     - Application class
HomeScreen.kt           - Main screen (working)
NotepadScreen.kt        - Example screen (working)
AppModule.kt            - Dependency injection
AppDatabase.kt          - Room database
NavGraph.kt             - Navigation routing
```

## Support Resources

### Android Official
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)

### Libraries Used
- [PDFBox Android](https://github.com/TomRoush/PdfBox-Android)
- [Android PDF Viewer](https://github.com/barteksc/AndroidPdfViewer)
- [Tesseract OCR](https://github.com/rmtheis/tess-two)

### Project Documentation
- `README.md` - Complete overview
- `IMPLEMENTATION_GUIDE.md` - Code templates
- `PROJECT_SUMMARY.md` - Status and architecture

## Team Info

**Developers:**
- Krishna (Roll No: 366)
- Adarsh (Roll No: 166)
- Mahesh (Roll No: 165)

**Project Theme:** Document Management & PDF Toolkit

---

**Ready to code!** 🚀

Start by implementing `FileListScreen` using the template in `IMPLEMENTATION_GUIDE.md`.
