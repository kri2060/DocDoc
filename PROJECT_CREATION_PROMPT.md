# Document Viewer - Complete Project Creation Prompt

Create a comprehensive document management and PDF toolkit Android application using the specifications below. This is an academic project for Krishna (Roll No: 366), Adarsh (Roll No: 166), and Mahesh (Roll No: 165) with the theme "Document Management & PDF Toolkit".

---

## PROJECT OVERVIEW

Build a fully-featured document management and PDF toolkit Android application that provides powerful capabilities for managing, viewing, editing, and manipulating various document types. The application should be built entirely with modern Android development practices using Jetpack Compose and should work seamlessly on Android Studio with Clean Architecture principles.

---

## TECHNOLOGY STACK

### Core Technologies and Versions
- **Programming Language:** Kotlin 1.9.22
- **Build System:** Gradle with Kotlin DSL
- **Minimum SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34
- **Java Version:** JDK 17
- **Android Studio:** Hedgehog or later
- **Package Name:** com.documentviewer
- **Application ID:** com.documentviewer

### UI Framework
- **UI Toolkit:** Jetpack Compose 1.6.0 (100% Compose, no XML layouts)
- **Compose BOM:** 2024.02.00
- **Material Design:** Material 3 components
- **Icons:** Material Icons Extended set
- **Compose Compiler Extension:** 1.5.10
- **Navigation:** Navigation Compose 2.7.7 for type-safe navigation

### Architecture and Design Patterns
- **Architecture Pattern:** MVVM (Model-View-ViewModel) combined with Clean Architecture
- **Dependency Injection:** Hilt 2.50 for dependency injection throughout the application
- **Reactive Programming:** Kotlin Coroutines 1.7.3 and Kotlin Flow for asynchronous operations
- **State Management:** StateFlow and Flow for UI state management

### Data Persistence and Storage
- **Database:** Room 2.6.1 with Kotlin extensions for local data persistence
- **Key-Value Storage:** DataStore Preferences 1.0.0 for storing user preferences
- **Database Access:** Room DAOs with Flow-based reactive queries
- **Background Tasks:** WorkManager 2.9.0 for long-running operations

### PDF Processing Libraries
- **PDF Viewing:** android-pdf-viewer (barteksc) 3.2.0-beta.1 for displaying PDF documents
- **PDF Manipulation:** pdfbox-android (tom-roush) 2.0.27.0 for creating, merging, splitting, and editing PDFs
- **PDF Annotations:** Custom annotation layer built on top of PDF viewer

### OCR and Text Processing
- **OCR Engine:** tess-two 9.1.0 (Tesseract OCR for Android) for optical character recognition
- **Text-to-Speech:** Built-in Android TextToSpeech API with custom foreground service
- **Language Support:** English and other languages for OCR and TTS

### Office Document Processing
- **Apache POI:** Version 5.2.5 for reading and converting Office documents
- **POI OOXML:** Version 5.2.5 for modern Office formats (DOCX, XLSX, PPTX)
- **XML Beans:** Version 5.1.1 for XML processing
- **Commons Compress:** Version 1.24.0 for compression support
- **Commons IO:** Version 2.15.0 for file utilities

### Additional Libraries
- **Image Loading:** Coil 2.5.0 (Kotlin-first image loading library) with Compose support
- **File Compression:** zip4j 2.11.5 for creating and extracting ZIP archives
- **Permissions:** Accompanist Permissions 0.34.0 for runtime permission handling
- **Lifecycle:** AndroidX Lifecycle 2.7.0 with Compose integration

### Testing Framework
- **Unit Testing:** JUnit 4.13.2
- **Android Testing:** AndroidX Test 1.1.5
- **UI Testing:** Compose UI Test with JUnit4
- **Espresso:** Version 3.5.1 for UI testing

---

## APPLICATION ARCHITECTURE

### Clean Architecture Layer Structure

Implement a three-layer Clean Architecture pattern:

#### 1. Data Layer (app/src/main/java/com/documentviewer/data/)
- **local/dao/**: Room Data Access Objects for database operations
  - RecentFilesDao: Manage recent file access
  - FavoritesDao: Handle favorite files
  - ReadingPositionDao: Store and retrieve document reading positions
  - SearchIndexDao: Manage search index entries
  - NotesDao: CRUD operations for notes
- **local/entity/**: Room Entity classes representing database tables
  - RecentFileEntity: Store recent file metadata
  - FavoriteEntity: Store favorite file information
  - ReadingPositionEntity: Store page numbers and scroll positions
  - SearchIndexEntity: Store indexed document content for search
  - NoteEntity: Store user-created notes
- **local/**: AppDatabase.kt with Room database configuration
- **model/**: Data classes for business objects
  - FileItem: Represents file metadata (name, path, size, type, mime type)
  - PdfOperation: Configuration for PDF operations
  - SearchResult: Search result with highlighted content
  - Annotation: PDF annotation data structures
- **repository/**: Repository implementations providing data to domain layer
  - FileRepository: File system operations
  - DatabaseRepository: Database operations
  - PdfRepository: PDF-specific operations
  - SearchRepository: Search and indexing operations

#### 2. Domain Layer (app/src/main/java/com/documentviewer/domain/)
- **usecase/**: Single-responsibility business logic components
  - File Management: GetFilesUseCase, DeleteFileUseCase, RenameFileUseCase
  - Recent Files: GetRecentFilesUseCase, AddRecentFileUseCase
  - Favorites: AddToFavoritesUseCase, RemoveFromFavoritesUseCase, GetFavoritesUseCase
  - PDF Operations: MergePdfsUseCase, SplitPdfUseCase, ImagesToPdfUseCase, PdfToImagesUseCase
  - Search: SearchFilesUseCase, IndexFileForSearchUseCase
  - Reading Position: SaveReadingPositionUseCase, GetReadingPositionUseCase
  - OCR: ExtractTextFromImageUseCase
  - Notes: CreateNoteUseCase, UpdateNoteUseCase, DeleteNoteUseCase

#### 3. Presentation Layer (UI) (app/src/main/java/com/documentviewer/ui/)
- **theme/**: Material 3 theme configuration
  - Color scheme with primary Purple (#6C63FF) and secondary Teal (#03DAC6)
  - Typography using Roboto font family
  - Dark theme as default with dynamic color support on Android 12+
  - Custom color definitions for file type categories
- **components/**: Reusable Composable UI components
  - FileCard: Display file information in grid/list
  - CategoryCard: Display document categories
  - PlaceCard: Display places (folders, recent, favorites)
  - PdfOperationCard: PDF operation tools
  - ToolCard: Additional tools
  - BottomSheet: File action bottom sheets
  - LoadingIndicator: Progress indicators
  - EmptyState: Empty state screens
- **navigation/**: Navigation graph and routes
  - Define all screen routes
  - Type-safe navigation arguments
  - Deep link support
- **home/**: Home screen with categories, places, PDF operations, and tools
  - HomeScreen.kt: Main composable
  - HomeViewModel.kt: State management with file counts
- **filelist/**: File browsing by category
  - FileListScreen.kt: Display files with sort and search
  - FileListViewModel.kt: File loading and management
- **viewer/**: Document viewers for different file types
  - PdfViewerScreen.kt: PDF viewer with navigation and TTS
  - ImageViewerScreen.kt: Image viewer
  - TextViewerScreen.kt: Text file viewer
  - DocumentViewerViewModel.kt: Viewer state management
- **pdf/**: PDF operation screens
  - ImagesToPdfScreen.kt: Convert images to PDF
  - PdfToImagesScreen.kt: Extract images from PDF
  - MergePdfScreen.kt: Merge multiple PDFs
  - SplitPdfScreen.kt: Split PDF into pages
  - PdfPasswordScreen.kt: Add/remove PDF passwords
  - TextToPdfScreen.kt: Convert text to PDF
  - OfficeToPdfScreen.kt: Convert Office documents to PDF
  - PdfRotateScreen.kt: Rotate PDF pages
  - PdfCompressScreen.kt: Compress PDF files
  - Corresponding ViewModels for each screen
- **tools/**: Additional tool screens
  - ZipCreatorScreen.kt: Create ZIP archives
  - OcrScannerScreen.kt: OCR text extraction
  - NotepadScreen.kt: Notes list
  - NoteEditorScreen.kt: Create/edit notes
  - SearchScreen.kt: Smart search interface
  - PdfMarkupScreen.kt: PDF annotation tools
  - Corresponding ViewModels for each tool
- **settings/**: Settings screen
  - SettingsScreen.kt: App settings and preferences
  - SettingsViewModel.kt: Settings state management

#### 4. Core Layer (app/src/main/java/com/documentviewer/core/)
- **utils/**: Utility classes and extension functions
  - FileUtils: File operations helpers
  - DateUtils: Date formatting
  - SizeUtils: File size formatting
  - MimeTypeUtils: MIME type detection
  - Constants: Application constants
- **permissions/**: Permission handling logic
  - PermissionManager: Runtime permission requests
  - Permission states and callbacks
- **file/**: File system operations
  - MediaStoreScanner: Scan device storage
  - DocumentFileHelper: Storage Access Framework helpers
  - FileTypeDetector: Detect file types

#### 5. Dependency Injection (app/src/main/java/com/documentviewer/di/)
- **AppModule.kt**: Application-level dependencies (Context, Application)
- **DatabaseModule.kt**: Room database and DAO injection
- **RepositoryModule.kt**: Repository implementations
- **UseCaseModule.kt**: Use case dependencies
- **WorkerModule.kt**: WorkManager factory and workers

#### 6. Background Services and Workers
- **service/TtsService.kt**: Foreground service for Text-to-Speech playback
  - Implement MediaSession for playback controls
  - Notification with play/pause/stop actions
  - Continue reading from last position
  - Adjustable speech rate
- **workers/** (in app/src/main/java/com/documentviewer/workers/):
  - PdfMergeWorker: Background PDF merging
  - PdfToImagesWorker: Extract images in background
  - ImagesToPdfWorker: Create PDF in background
  - OcrIndexingWorker: Index documents for search
  - FileIndexingWorker: Scan and index files
  - All workers should show progress notifications

---

## REQUIRED ANDROID PERMISSIONS

Configure the following permissions in AndroidManifest.xml:

- READ_EXTERNAL_STORAGE: Read files from device storage
- WRITE_EXTERNAL_STORAGE: Write files to device storage (for Android 10 and below)
- READ_MEDIA_IMAGES: Read images on Android 13+ (Tiramisu)
- READ_MEDIA_VIDEO: Read videos on Android 13+
- READ_MEDIA_AUDIO: Read audio files on Android 13+
- CAMERA: Capture images for OCR scanning
- FOREGROUND_SERVICE: Run Text-to-Speech service in foreground
- POST_NOTIFICATIONS: Show notifications for completed operations and TTS playback
- MANAGE_EXTERNAL_STORAGE: Full storage access (optional, request when needed)

Implement runtime permission handling using Accompanist Permissions library for all dangerous permissions. Use Storage Access Framework (SAF) with ACTION_OPEN_DOCUMENT and ACTION_CREATE_DOCUMENT for file operations to avoid storage permission issues on Android 11+.

---

## DATABASE SCHEMA

### Room Database Implementation

Create AppDatabase extending RoomDatabase with the following entities:

#### 1. RecentFileEntity Table
```
Table name: recent_files
Columns:
- path: String (Primary Key) - File path
- name: String (Not null) - File display name
- size: Long (Not null) - File size in bytes
- lastAccessed: Long (Not null) - Timestamp of last access
- mimeType: String (Nullable) - MIME type
- type: String (Not null) - File category (PDF, Word, Excel, etc.)
```

#### 2. FavoriteEntity Table
```
Table name: favorites
Columns:
- path: String (Primary Key) - File path
- name: String (Not null) - File display name
- size: Long (Not null) - File size in bytes
- addedAt: Long (Not null) - Timestamp when added to favorites
- mimeType: String (Nullable) - MIME type
- type: String (Not null) - File category
```

#### 3. ReadingPositionEntity Table
```
Table name: reading_positions
Columns:
- filePath: String (Primary Key) - File path
- pageNumber: Int (Not null) - Current page number (0-indexed)
- scrollOffset: Float (Not null) - Vertical scroll position on page
- lastReadAt: Long (Not null) - Timestamp of last read
```

#### 4. SearchIndexEntity Table
```
Table name: search_index
Columns:
- id: Long (Primary Key, Auto-generate) - Unique index ID
- filePath: String (Not null) - File path
- fileName: String (Not null) - File name for quick search
- content: String (Not null) - Extracted text content
- pageNumber: Int (Nullable) - Page number for multi-page documents
- indexedAt: Long (Not null) - Timestamp of indexing
- fileType: String (Not null) - Document type
Indexes:
- Create FTS4 or FTS5 full-text search index on content column
- Create index on fileName for fast filename searches
```

#### 5. NoteEntity Table
```
Table name: notes
Columns:
- id: Long (Primary Key, Auto-generate) - Note ID
- title: String (Not null) - Note title
- content: String (Not null) - Note body text
- createdAt: Long (Not null) - Creation timestamp
- modifiedAt: Long (Not null) - Last modification timestamp
- filePath: String (Nullable) - Saved file path if exported
```

### DAO Interfaces
Create corresponding DAO interfaces with Flow-based reactive queries:
- Insert, update, delete operations
- Query methods returning Flow for reactive UI updates
- Sorting and filtering capabilities
- Full-text search queries for SearchIndexDao

---

## COMPREHENSIVE FEATURE LIST

### CORE FEATURES

#### 1. File Management System
Implement a complete file browsing and management system:

**File Categories:**
- PDF: Browse all PDF documents on device
- Word: Microsoft Word documents (.doc, .docx)
- Excel: Spreadsheets (.xls, .xlsx)
- PowerPoint: Presentations (.ppt, .pptx)
- Images: All image formats (JPG, PNG, GIF, BMP, WebP)
- Videos: Video files (MP4, MKV, AVI, etc.)
- Audio: Audio files (MP3, M4A, WAV, FLAC, etc.)

**File Scanning:**
- Use MediaStore API to scan device storage
- Query ContentResolver with appropriate projections
- Extract: file ID, display name, size, date modified, MIME type, file path
- Support Android 10+ scoped storage
- Implement efficient background scanning with WorkManager

**File Operations:**
- Open: Launch file in appropriate viewer
- Share: Share files using Android Share Sheet
- Delete: Delete files with confirmation dialog
- Rename: Rename files with validation
- Move: Move files to different folders
- Copy: Copy files to clipboard or new location
- File details: Show full metadata (size, date, path, MIME type)
- Multi-select mode: Perform batch operations on multiple files

**View Modes:**
- Grid view: Display files in responsive grid with thumbnails
- List view: Detailed list with file metadata
- Sort options: Name (A-Z, Z-A), Size (ascending/descending), Date (newest/oldest), Type
- Filter: Search within current category

#### 2. Places

**Folders Navigation:**
- Browse device folders using Storage Access Framework
- Use DocumentFile API for folder traversal
- Display folder structure with breadcrumb navigation
- Show file count in each folder
- Support creating new folders
- Remember last visited folders

**Recent Files:**
- Track all file accesses in Room database
- Display 50 most recently accessed files
- Show with timestamps (e.g., "2 hours ago", "Yesterday")
- Quick access to continue work
- Clear recent history option
- Sort by last accessed time

**Favorites:**
- Star/unstar files from any screen
- Dedicated favorites screen
- Sync across app restarts using Room database
- Quick access from home screen
- Remove from favorites option
- Search within favorites

**My Creation Folder:**
- Dedicated folder for all app-generated files
- Store in app-specific external storage directory
- Organized by operation type (merged PDFs, converted images, etc.)
- Auto-naming with timestamps
- Quick share and export options
- Browse with same features as other folders

#### 3. PDF Operations

All PDF operations should:
- Show file picker for input selection
- Display configuration options
- Show progress indicator during processing
- Use WorkManager for background processing
- Show notifications with progress and completion status
- Save output to "My Creation" folder
- Provide "Open" and "Share" actions after completion
- Handle errors gracefully with retry options

**Images to PDF Conversion:**
- Multi-image selection from gallery or camera
- Support formats: JPG, PNG, GIF, BMP, WebP
- Drag-and-drop reordering of images
- Configuration options:
  - Page size: A4, Letter, Legal, Custom
  - Orientation: Portrait, Landscape, Auto
  - Margins: None, Small, Medium, Large (custom values in mm)
  - Image quality: Low, Medium, High, Original
  - Fit to page or original size
- Preview before generation
- Generate PDF using PDFBox Android
- Optimize file size

**PDF to Images Extraction:**
- Select PDF file
- Page range selection:
  - All pages
  - Specific pages (comma-separated: 1,3,5)
  - Page range (e.g., 1-10)
- Output format: PNG (lossless), JPG (compressed)
- Image quality slider (JPG only)
- Resolution options: Original, High (300 DPI), Medium (150 DPI), Low (72 DPI)
- Extract pages using PDFBox Android
- Save images to subfolder named after PDF
- Generate sequential filenames (page_001.png, page_002.png, etc.)

**Merge PDF Files:**
- Select multiple PDF files (minimum 2)
- Drag-and-drop reordering
- Preview list with page counts
- Options:
  - Merge order
  - Add bookmarks for each source PDF
  - Compress output
- Merge using PDFBox Android PDFMergerUtility
- Show progress for large files

**Split PDF:**
- Select PDF file
- Split modes:
  - Single pages: Split each page into separate PDF
  - Custom ranges: Define page ranges to extract
  - Split by size: Split into files of specified page count
  - Split by bookmarks: Split at bookmark locations
- Preview split configuration
- Generate multiple output files with sequential naming

**Add/Remove PDF Password:**
- Add password:
  - Select PDF file
  - Enter password (minimum 6 characters)
  - Confirm password
  - Choose encryption level: 128-bit, 256-bit AES
  - Set permissions: allow printing, copying, editing, commenting
- Remove password:
  - Select encrypted PDF
  - Enter current password
  - Decrypt and save unencrypted version
- Use PDFBox Android encryption features

**Text to PDF:**
- Input methods:
  - Type or paste text
  - Import from text file
  - Use saved note
- Formatting options:
  - Font: Arial, Times New Roman, Courier, Helvetica
  - Font size: 8-72 pt
  - Line spacing: Single, 1.5, Double
  - Alignment: Left, Center, Right, Justify
  - Page size and orientation
  - Margins
- Real-time preview
- Generate PDF using PDFBox

**Office Documents to PDF:**
- Support formats: DOC, DOCX, XLS, XLSX, PPT, PPTX
- Use Apache POI to read Office documents
- Convert to PDF using PDFBox
- Preserve formatting as much as possible
- Handle multi-sheet Excel files (create multi-page PDF)
- Handle slide transitions in PowerPoint
- Show conversion progress

**PDF Rotation:**
- Select PDF file
- Choose pages to rotate (all or specific)
- Rotation angle: 90°, 180°, 270°
- Preview rotation
- Save rotated PDF

**PDF Compression:**
- Select PDF file
- Compression levels:
  - Low: Slight size reduction, high quality
  - Medium: Balanced
  - High: Maximum compression, acceptable quality
  - Custom: Adjust parameters (image quality, resolution)
- Show original vs compressed size
- Preview quality (compare first page)
- Compress images within PDF
- Remove metadata option

### ADVANCED FEATURES

#### 1. Smart Search with OCR

Build a comprehensive search system:

**Search Interface:**
- Prominent search bar on home screen and dedicated search screen
- Real-time suggestions as user types
- Search history with quick access to previous searches
- Voice search support
- Clear search and history options

**Search Capabilities:**
- Filename search: Fast search in database
- Full-text content search: Search within document text
- OCR-based search: Extract and search text from PDFs and images
- Fuzzy search: Find results with typos or similar words
- Boolean operators: AND, OR, NOT for advanced queries
- Phrase search: Exact phrase matching with quotes

**Search Filters:**
- File type: Filter by category (PDF, Word, Images, etc.)
- Date range: Today, This week, This month, Custom range
- Size range: Minimum and maximum file size
- Location: Specific folders
- Modified date: Files modified within timeframe

**Search Indexing:**
- Background indexing using WorkManager
- Extract text from PDFs using PDFBox
- Extract text from images using Tesseract OCR
- Store in SearchIndexEntity with full-text search index
- Incremental indexing: Only index new or modified files
- Re-index option in settings
- Index progress indicator

**Search Results:**
- Display results grouped by file type
- Show snippets with matching text highlighted
- Display file metadata (size, date, path)
- Quick actions: Open, Share, Favorite
- Sort results by relevance, date, name, size
- Infinite scroll with pagination

**Performance Optimization:**
- Use Room FTS (Full-Text Search) for fast queries
- Cache search results
- Lazy loading of results
- Background indexing doesn't block UI

#### 2. PDF Markup and Annotation Tools

Implement a full-featured PDF annotation system:

**Annotation Tools:**

1. **Highlight Tool:**
   - Select text to highlight
   - Color options: Yellow, Green, Blue, Pink, Orange
   - Opacity control
   - Multiple highlights per page
   - Edit/delete existing highlights

2. **Underline Tool:**
   - Select text to underline
   - Color and thickness options
   - Single, double, wavy underline styles

3. **Freehand Drawing Tool:**
   - Draw with finger or stylus
   - Pen colors and thickness
   - Eraser tool
   - Smooth line rendering
   - Pressure sensitivity support (if available)

4. **Text Annotation Tool:**
   - Add text boxes anywhere on page
   - Font customization: family, size, color, style
   - Move and resize text boxes
   - Edit annotation text after creation

5. **Shape Tools:**
   - Rectangle: Outline and filled
   - Circle/Ellipse: Outline and filled
   - Arrow: Point to specific areas
   - Line: Straight lines
   - Color and thickness options
   - Drag to resize and reposition

6. **Digital Signature:**
   - Draw signature with finger/stylus
   - Import signature from image
   - Save signature for reuse
   - Place signature anywhere on document
   - Resize signature
   - Timestamp signature

**Annotation Features:**
- Undo/Redo: Full history stack for all annotations
- Layer system: Annotations on separate layer above PDF
- Lock annotations: Prevent accidental edits
- Delete individual annotations
- Annotation list: View all annotations in document
- Jump to annotation from list
- Export annotations as separate data file (for future editing)

**Saving Annotated PDFs:**
- Flatten annotations: Burn annotations into PDF
- Keep editable: Save annotations as PDF annotations standard
- Save as new file or overwrite
- Compression options

**Export Options:**
- Export individual pages as images (with or without annotations)
- Export range of pages
- Share annotated PDF directly

**UI/UX:**
- Floating toolbar with annotation tools
- Tool palette with quick access
- Color picker with custom colors
- Zoom and pan while annotating
- Auto-save annotations periodically
- Smooth 60 FPS rendering

#### 3. Text-to-Speech Reading Mode

Implement comprehensive TTS functionality:

**TTS Service Implementation:**
- Android foreground service (TtsService) for continuous playback
- Initialize Android TextToSpeech engine
- Handle TTS engine initialization callbacks
- Manage audio focus for proper behavior with other audio apps
- MediaSession integration for media controls

**Playback Controls:**
- Play: Start reading from current position
- Pause: Pause reading, maintain position
- Stop: Stop and reset to beginning
- Previous/Next: Jump to previous/next paragraph or page
- Seek: Jump to specific page
- Replay: Re-read current sentence

**Reading Features:**
- Real-time text highlighting: Highlight current word/sentence being read
- Auto-scroll: Automatically scroll to keep highlighted text visible
- Continue reading: Resume from last stopped position
- Background playback: Read in background while using other apps
- Lock screen controls: Show notification with playback controls

**Speed Control:**
- Adjustable reading speed: 0.5x to 3.0x
- Speed presets: Slow (0.75x), Normal (1.0x), Fast (1.5x), Very Fast (2.0x)
- Fine-tune with slider
- Persist speed preference

**Language and Voice:**
- Detect document language
- Select TTS engine voice
- Download additional language packs if needed
- Voice quality options (if available)

**Reading Position Persistence:**
- Save position when stopping
- Restore position when reopening document
- Sync with Last Page Recall feature
- "Continue Reading" shortcut on home screen

**Notification:**
- Persistent notification while reading
- Show document name and current page
- Playback controls in notification
- Play/Pause button
- Stop button
- Dismiss to stop service

#### 4. Last Page Recall

Implement automatic reading position memory:

**Position Tracking:**
- Automatically save position when closing any document
- Track page number (for multi-page documents)
- Track scroll offset (vertical position on page)
- Save to ReadingPositionEntity in Room database
- Update position periodically while reading (every 5 seconds)

**Position Restoration:**
- Detect when opening previously read document
- Restore to exact page and scroll position
- Show toast: "Resumed from page X"
- Option to start from beginning instead
- Smooth scroll animation to position

**Continue Reading Features:**
- "Continue Reading" section on home screen
- Show last 5-10 documents with reading progress
- Display progress indicator (e.g., "Page 15 of 50, 30%")
- Thumbnails or file icons
- Quick tap to continue
- Clear individual or all reading history

**Sync Across App Restarts:**
- Persist all positions in database
- Load on app startup
- Never lose reading position
- Auto-cleanup very old entries (older than 6 months)

**Progress Visualization:**
- Progress bar showing document completion percentage
- Page indicator: "Page 15/50"
- Estimated time remaining (based on average reading speed)

### ADDITIONAL TOOLS

#### ZIP Creator Tool
- Multi-file and folder selection
- Add files from different locations
- Set compression level: None, Fast, Normal, Maximum
- Password protection option
- Create ZIP in background with WorkManager
- Show compression progress
- Compression ratio display
- Save to "My Creation" folder

#### OCR Scanner Tool
- Capture from camera or select image
- Crop image before OCR
- Image preprocessing: Auto-enhance, grayscale, threshold
- Language selection: English, Spanish, French, German, etc.
- Download language data if not available
- Extract text using Tesseract OCR
- Show progress during extraction
- Display extracted text with confidence scores
- Edit extracted text
- Copy to clipboard
- Share as text
- Save as TXT file
- Convert to PDF
- Batch OCR: Process multiple images

#### Notepad Tool
- Notes list screen showing all saved notes
- Search notes by title or content
- Sort notes: Last modified, Created date, Alphabetical
- Create new note: Title and content fields
- Rich text formatting: Bold, italic, lists (optional)
- Auto-save: Save automatically while typing (every 10 seconds)
- Manual save button
- Delete notes with confirmation
- Export note to TXT file
- Share note as text
- Link note to document (optional)
- Note categories or tags (optional)
- Sync with device storage
- Import existing text files as notes

#### Settings Screen
Comprehensive settings with DataStore Preferences:

**Appearance:**
- Theme: Dark, Light, Auto (follow system)
- Dynamic colors: Enable Material You dynamic colors (Android 12+)
- Grid size: Adjust grid columns for file browsing

**File Management:**
- Default save location: Choose folder for "My Creation"
- Default file sorting: Name, Size, Date, Type
- Show hidden files: Toggle visibility
- Confirm before delete: Require confirmation dialog
- File name format: Date format for auto-generated names

**PDF Settings:**
- Default page size: A4, Letter, Legal
- Default quality: Low, Medium, High
- Keep original files after conversion
- Auto-compress PDFs

**Search:**
- Enable OCR indexing
- Index frequency: Manual, Daily, Weekly
- Storage for index: Show size, clear index

**Text-to-Speech:**
- Default speech rate
- Default voice
- Auto-play on document open
- Continue reading on resume

**Privacy:**
- Clear recent files history
- Clear favorites
- Clear reading positions
- Clear search history
- Reset all settings

**About:**
- App version
- Team information: Krishna (366), Adarsh (166), Mahesh (165)
- Project theme: Document Management & PDF Toolkit
- Libraries used (with licenses)
- Contact/Feedback

---

## USER INTERFACE DESIGN

### Material 3 Design System

**Color Scheme:**
- Primary color: Purple (#6C63FF)
- Secondary color: Teal (#03DAC6)
- Surface colors: Material 3 surface tones
- Category-specific colors:
  - PDF: Red (#EF5350)
  - Word: Blue (#42A5F5)
  - Excel: Green (#66BB6A)
  - PowerPoint: Orange (#FFA726)
  - Images: Purple (#AB47BC)
  - Videos: Deep Purple (#5C6BC0)
  - Audio: Cyan (#26C6DA)
- Error: Material error color
- Background: Proper dark theme backgrounds

**Typography:**
- Font family: Roboto (system default)
- Material 3 typography scale:
  - displayLarge: Large titles
  - headlineLarge: Screen titles
  - titleLarge: Card titles
  - bodyLarge: Primary body text
  - labelLarge: Button text
- Proper text contrast ratios for accessibility

**Component Styling:**
- Cards: Elevated or filled cards with rounded corners (12dp radius)
- Buttons: Filled, outlined, and text buttons
- FABs: Large FAB for primary actions
- Bottom sheets: Modal and standard bottom sheets
- Dialogs: Alert dialogs with Material 3 styling
- TextField: Outlined text fields
- Chips: Filter and action chips
- Navigation: Bottom navigation or navigation drawer
- App bars: Top app bars with proper elevation
- Lists: Lazy columns with dividers
- Grids: Lazy grids with proper spacing

**Spacing and Layout:**
- Consistent 12dp, 16dp, 24dp padding
- Grid spacing: 8dp between items
- Card elevation: 2dp (light theme), 0dp with tonal color (dark theme)
- Icon sizes: 24dp standard, 48dp for category icons

**Animations:**
- Content size animations: animateContentSize()
- Navigation transitions: Fade, slide
- Loading indicators: Circular and linear progress
- Shimmer effects for loading states (optional)
- Smooth scrolling with lazy lists

### Home Screen Layout

**Header Section:**
- Top app bar with app title: "Document Viewer"
- Team info in top right corner (always visible):
  - Line 1: "Krishna (366) | Adarsh (166) | Mahesh (165)"
  - Line 2: "Document Management & PDF Toolkit"
- Search icon leading to search screen
- Settings icon in top right

**Categories Section:**
- Section title: "Categories"
- Lazy grid layout (2 columns on phone, 3-4 on tablet)
- Each category card shows:
  - Category icon (Material Icons)
  - Category name
  - File count (e.g., "25 files")
  - Color-coded background
- Categories: PDF, Word, Excel, PowerPoint, Images, Videos, Audio

**Places Section:**
- Section title: "Places"
- Horizontal scrollable row or grid
- Cards for:
  - Folders: Browse device folders
  - Recent Files: Recently accessed documents
  - Favorites: Starred files
  - My Creation: App-generated files
- Each card with appropriate icon and description

**PDF Operations Section:**
- Section title: "PDF Operations"
- Lazy grid layout (2 columns)
- Operation cards:
  - Images to PDF
  - PDF to Images
  - Merge PDFs
  - Split PDF
  - Add/Remove Password
  - Text to PDF
  - Office to PDF
  - Rotate PDF
  - Compress PDF
- Each card with icon, title, and brief description

**Tools Section:**
- Section title: "Tools"
- Grid or list layout
- Tool cards:
  - ZIP Creator
  - OCR Scanner
  - Notepad
  - Smart Search
- Each card with icon and title

**Continue Reading Section (if applicable):**
- Section title: "Continue Reading"
- Horizontal scrollable list
- Cards showing recently read documents
- Progress indicator on each card
- Quick tap to continue

**Bottom Navigation or FAB:**
- FAB for quick access to most-used feature
- OR Bottom navigation bar with 3-5 primary destinations

### File List Screen

- Top app bar with category name and back button
- Search icon and filter icon in top bar
- Sort option: Dropdown or bottom sheet with sort options
- View mode toggle: Grid/List
- Lazy grid or list of files:
  - File icon or thumbnail
  - File name (max 2 lines, ellipsize)
  - File size and date
  - Favorite icon (toggle)
- Long press or select mode for multi-select
- Action mode toolbar when items selected:
  - Share, Delete, Move, Copy, etc.
- Floating action button for quick actions (optional)
- Empty state when no files found
- Pull-to-refresh to rescan

### PDF Viewer Screen

- Full-screen PDF display
- Top app bar (auto-hide):
  - Back button
  - Document name
  - Share icon
  - More options (Favorite, Properties)
- Bottom bar (auto-hide):
  - Page indicator: "Page 5 of 20"
  - Previous/Next page buttons
  - Thumbnail view button
  - Annotation button (opens markup screen)
  - TTS button (starts reading)
- Zoom controls: Pinch to zoom, double-tap
- Page navigation: Swipe or buttons
- Smooth scrolling between pages
- Loading indicator for large PDFs

### PDF Markup Screen

- Full-screen PDF with annotation overlay
- Top toolbar:
  - Back button
  - Save button
  - Undo/Redo buttons
  - More options
- Side or bottom floating toolbar:
  - Highlight
  - Underline
  - Draw
  - Text
  - Shapes
  - Signature
  - Erase
- Tool-specific options panel:
  - Color picker
  - Thickness slider
  - Style options
- Non-intrusive UI that doesn't block content
- Tool indicator showing current tool
- Save prompt when exiting with unsaved changes

### Settings Screen

- Grouped settings in lazy column
- Section headers for each group
- Settings items:
  - Switch for toggles
  - Dropdown or dialog for selections
  - Slider for numeric values
  - Clickable items for navigation to sub-screens
- "About" section at bottom with team info
- Version number in footer

---

## IMPLEMENTATION DETAILS

### File Scanning with MediaStore

Use MediaStore API to query all files:
- Query: MediaStore.Files.getContentUri("external")
- Projection: _ID, DISPLAY_NAME, SIZE, DATE_MODIFIED, MIME_TYPE, DATA (path)
- Selection: Filter by MIME type for each category
- Sort order: DATE_MODIFIED DESC or DISPLAY_NAME ASC
- Use ContentResolver.query() with proper cursor handling
- Map cursor results to FileItem data class
- Handle Android 10+ scoped storage changes
- Request MANAGE_EXTERNAL_STORAGE if needed for full access

### Storage Access Framework (SAF)

For folder browsing and file operations:
- Use ACTION_OPEN_DOCUMENT_TREE for folder access
- Use ACTION_OPEN_DOCUMENT for file selection
- Use ACTION_CREATE_DOCUMENT for file creation
- Work with DocumentFile API
- Use ContentResolver to open input/output streams
- Handle URI permissions persistently
- Implement breadcrumb navigation using DocumentFile.getParentFile()

### PDF Processing with PDFBox Android

**Viewing PDFs:**
- Use android-pdf-viewer library (barteksc)
- Load PDF from URI or file path
- Configure: swipe horizontal/vertical, page fit, night mode
- Handle page changes with listener
- Implement zoom and scroll

**Creating PDFs:**
- Use PDFBox Android PDDocument
- Add pages with PDPage
- Use PDPageContentStream for content
- Draw images: PDImageXObject.createFromByteArray()
- Draw text with PDFont
- Save to output stream: document.save(outputStream)

**Merging PDFs:**
- Use PDFMergerUtility
- Add sources: merger.addSource(inputStream)
- Set destination: merger.setDestinationStream(outputStream)
- Merge: merger.mergeDocuments()

**Splitting PDFs:**
- Load source PDDocument
- Use Splitter class with split size
- Iterate through split documents and save each

**Encryption:**
- Use AccessPermission for permission settings
- Use StandardProtectionPolicy for password
- Set document encryption with setAllSecurityToBeRemoved(false)

### OCR with Tesseract

**Initialization:**
- Copy traineddata files to app's external files directory
- Path structure: /tessdata/[lang].traineddata
- Initialize TessBaseAPI with data path and language
- Handle initialization errors gracefully
- Download language files on demand if not present

**Text Extraction:**
- Convert image to Bitmap
- Preprocess image: grayscale, contrast, threshold (OpenCV optional)
- Set bitmap to TessBaseAPI: setImage(bitmap)
- Extract text: utF8Text property
- Clean up: end() and recycle bitmap
- Handle extraction in background thread (coroutine)
- Show progress indicator

**Language Management:**
- Support multiple languages: eng, spa, fra, deu, etc.
- Check if language data exists before use
- Provide download mechanism for additional languages
- Store user's language preference

### Text-to-Speech Service

**Service Implementation:**
- Extend Service class
- Initialize TextToSpeech in onCreate()
- Set TTS listener for utterance progress
- Implement MediaSession for media controls
- Create notification channel for foreground service
- Build notification with playback actions
- Start foreground service with notification

**Playback Logic:**
- Split text into manageable chunks (sentences or paragraphs)
- Use TTS.speak() with unique utterance IDs
- Track current utterance for position tracking
- Implement onUtteranceCompleted callback to advance
- Handle pause/resume with TTS.stop() and queue management
- Implement seek by calculating utterance position

**Audio Focus:**
- Request audio focus before playback
- Handle audio focus loss (pause or duck)
- Handle audio focus gain (resume)
- Implement OnAudioFocusChangeListener

**Controls:**
- Broadcast receivers for notification actions
- Update notification state (play/pause icon)
- Send playback state to UI using LiveData or Flow
- Handle stop action: stop service and clear notification

### WorkManager for Background Tasks

**Worker Implementation:**
- Extend CoroutineWorker for all PDF workers
- Receive input data via WorkerParameters
- Perform operation in doWork() suspend function
- Update progress: setProgress(workDataOf(...))
- Show notification with progress
- Return Result.success() or Result.failure()

**Enqueuing Work:**
- Create OneTimeWorkRequest or PeriodicWorkRequest
- Set input data: setInputData(workDataOf("key" to value))
- Set constraints: NetworkType, BatteryNotLow, etc.
- Enqueue: WorkManager.getInstance(context).enqueue(request)
- Observe work state: getWorkInfoByIdLiveData() or Flow

**Notification During Work:**
- Create notification channel in Application class
- Build notification with progress: setProgress(max, current, indeterminate)
- Update notification as work progresses
- Use NotificationManager.notify(id, notification)
- Add action buttons to notification (cancel, open)

### Search Indexing System

**Indexing Workflow:**
1. Scan all documents on device (MediaStore or SAF)
2. For each document:
   - If PDF: Extract text using PDFBox PDFTextStripper
   - If Image: Extract text using Tesseract OCR
   - If Office: Extract text using Apache POI
   - If Text: Read file content directly
3. Store in SearchIndexEntity with file metadata
4. Update indexedAt timestamp
5. Commit to Room database in batches

**Indexing Worker:**
- PeriodicWorkRequest running daily or weekly
- Check for new or modified files since last index
- Incremental indexing: Only index changes
- Show persistent notification during indexing
- Allow manual trigger from settings
- Handle large files: Stream reading, avoid memory issues

**FTS Implementation:**
- Use Room FTS4 or FTS5:
  ```
  @Fts4 or @Fts5
  @Entity(tableName = "search_index_fts")
  data class SearchIndexFts(
      @PrimaryKey val rowid: Long,
      val content: String
  )
  ```
- Create DAO with MATCH queries for full-text search
- Use MATCH operator: "SELECT * FROM search_index_fts WHERE content MATCH :query"
- Implement ranking for relevance

**Search Query Processing:**
- Parse user query for boolean operators
- Escape special characters
- Build FTS MATCH query
- Execute query and map results
- Highlight matching terms in results
- Paginate results for performance

### Reading Position Management

**Saving Position:**
- In PDF viewer, track current page number
- Track scroll offset using scroll state
- Save to database on:
  - Back button press
  - Home button press (onPause)
  - Page change after 2 seconds delay
  - App backgrounded
- Use ViewModel to debounce rapid updates
- Coroutine-based database write: viewModelScope.launch

**Restoring Position:**
- On document open, query ReadingPositionDao by file path
- If position exists:
  - Jump to saved page number
  - Scroll to saved offset with smooth animation
  - Show snackbar: "Resumed from page X"
- If no position, start from page 0
- Provide option to "Start from beginning"

**Continue Reading UI:**
- Query last 10 entries from ReadingPositionDao
- Sort by lastReadAt DESC
- Load file metadata (name, size, thumbnail)
- Calculate reading progress percentage: (currentPage / totalPages) * 100
- Display in horizontal scrollable row
- Quick tap opens document at saved position

### Permissions Handling

**Runtime Permission Requests:**
- Use Accompanist Permissions library
- Request permissions with rememberPermissionState()
- Show rationale dialog before requesting if previously denied
- Handle permission granted: Proceed with operation
- Handle permission denied: Show explanation, offer settings
- For storage: Guide user to use SAF if permissions denied

**Storage Access:**
- Prefer SAF over direct file access
- Only request MANAGE_EXTERNAL_STORAGE if absolutely necessary
- Use app-specific external storage for "My Creation": getExternalFilesDir()
- Handle Android 11+ scoped storage properly

---

## TESTING REQUIREMENTS

### Unit Tests
- Test all use cases with mock repositories
- Test repository implementations with fake data sources
- Test ViewModel state management and logic
- Test utility functions and extensions
- Use JUnit 4 and MockK/Mockito
- Aim for 70%+ code coverage

### Integration Tests
- Test Room database operations with in-memory database
- Test DataStore operations
- Test file operations with test files
- Test WorkManager workers

### UI Tests
- Test Composable screens with Compose Test
- Test navigation between screens
- Test user interactions: clicks, scrolls, text input
- Test state changes and UI updates
- Use ComposeTestRule and semantic matchers
- Test accessibility with semantic properties

---

## PERFORMANCE OPTIMIZATION

**Lazy Loading:**
- Use LazyColumn and LazyGrid for all lists
- Implement paging for large datasets
- Load thumbnails asynchronously with Coil
- Use keys for Lazy list items to optimize recomposition

**Background Processing:**
- Move all heavy operations to background threads/coroutines
- Use Dispatchers.IO for file operations
- Use Dispatchers.Default for CPU-intensive tasks
- Never block main thread

**Memory Management:**
- Recycle bitmaps after use
- Use appropriate image sizes (don't load full resolution if not needed)
- Implement image caching with Coil
- Clear unused resources
- Monitor memory usage with Profiler

**Database Optimization:**
- Use indices on frequently queried columns
- Batch database operations
- Use transactions for multiple writes
- Limit query results with LIMIT
- Use pagination for large result sets

**UI Optimization:**
- Minimize recompositions with remember and derivedStateOf
- Use stable parameters in Composables
- Avoid unnecessary state reads
- Use LaunchedEffect for side effects
- Profile with Layout Inspector and Compose Compiler

---

## BUILD CONFIGURATION

### Gradle Files

**Project-level build.gradle.kts:**
- Configure buildscript with Kotlin and Compose versions
- Add plugins: Android Application, Kotlin Android, Hilt, KSP
- Set plugin versions

**App-level build.gradle.kts:**
- Apply plugins
- Set namespace: com.documentviewer
- Set compileSdk: 34
- Configure defaultConfig:
  - applicationId: com.documentviewer
  - minSdk: 26
  - targetSdk: 34
  - versionCode: 1
  - versionName: "1.0"
- Enable Jetpack Compose in buildFeatures
- Set Compose compiler version
- Configure compileOptions for Java 17
- Set kotlinOptions jvmTarget to "17"
- Add all dependencies as specified in Tech Stack

**settings.gradle.kts:**
- Configure plugin repositories
- Configure dependency repositories: Google, Maven Central
- Include app module

**gradle.properties:**
- Enable Android Jetpack Compose features
- Set Kotlin code style
- Enable non-transitive R classes

### ProGuard Rules (for release builds)

Add rules for:
- Room: Keep entity and DAO classes
- PDFBox: Keep PDF processing classes
- Tesseract: Keep native methods
- Hilt: Generated code rules
- Compose: Compiler rules

---

## ERROR HANDLING AND EDGE CASES

**File Operations:**
- Handle file not found errors
- Handle permission denied errors
- Handle storage full errors
- Validate file formats before operations
- Handle corrupted files gracefully

**PDF Operations:**
- Handle invalid PDF files
- Handle password-protected PDFs (prompt for password)
- Handle extremely large PDFs (warn user, show progress)
- Handle PDFs with no text (OCR might be needed)

**OCR:**
- Handle image quality issues (warn user)
- Handle unsupported image formats
- Handle OCR failures (show error, allow retry)
- Handle missing language data (prompt download)

**Network:**
- Handle no internet when downloading language data
- Retry mechanism for failed downloads

**Database:**
- Handle database migration errors
- Handle corrupted database (reset option)

**TTS:**
- Handle TTS engine not available
- Handle unsupported languages
- Handle audio focus conflicts

**General:**
- Show user-friendly error messages
- Log errors for debugging
- Provide retry options
- Don't crash the app: Use try-catch blocks

---

## ADDITIONAL REQUIREMENTS

### Accessibility
- Provide content descriptions for all images and icons
- Use semantic properties in Compose
- Ensure proper text contrast ratios
- Support TalkBack screen reader
- Make all interactive elements at least 48dp in size

### Localization (Optional but Recommended)
- Extract all strings to strings.xml
- Support RTL layouts
- Provide translations for common languages

### Analytics (Optional)
- Implement Firebase Analytics or similar
- Track feature usage
- Track errors and crashes
- Respect user privacy

### App Icon and Splash Screen
- Design professional app icon
- Implement Material 3 splash screen
- Use adaptive icons for different devices

### Documentation
- Include inline code comments
- Document complex algorithms
- Provide README with setup instructions
- Document architecture and key design decisions

---

## SUCCESS CRITERIA

The completed application must:

1. Compile and run without errors on Android devices with API 26+
2. Work seamlessly in Android Studio without build issues
3. Implement all features listed in the README
4. Display team information (Krishna 366, Adarsh 166, Mahesh 165) prominently on home screen
5. Follow MVVM and Clean Architecture patterns strictly
6. Use 100% Jetpack Compose for UI (no XML layouts)
7. Implement proper dependency injection with Hilt
8. Use Room database for all data persistence
9. Handle all runtime permissions correctly
10. Work offline (except language data downloads)
11. Perform all heavy operations in background (WorkManager)
12. Show appropriate loading states and progress indicators
13. Display user-friendly error messages
14. Follow Material 3 design guidelines
15. Provide smooth animations and transitions
16. Be performant with no ANR errors
17. Handle large files gracefully
18. Save reading positions and restore correctly
19. Index documents for search
20. Support text-to-speech with controls
21. Allow PDF annotations with multiple tools
22. Generate high-quality PDFs from images and text
23. Convert Office documents to PDF
24. Merge, split, and manipulate PDFs
25. Extract text with OCR
26. Create ZIP archives
27. Manage notes with auto-save

---

## FINAL NOTES

This is a comprehensive academic project demonstrating modern Android development skills. The application should showcase:

- Clean Architecture and MVVM design patterns
- Jetpack Compose for modern UI development
- Room database for data persistence
- Hilt for dependency injection
- Coroutines and Flow for asynchronous programming
- WorkManager for background tasks
- Material 3 design system
- Integration of complex libraries (PDFBox, Tesseract, Apache POI)
- Proper permission handling
- Storage Access Framework usage
- Service implementation for TTS
- Advanced features like OCR, TTS, and annotations

Build this application with attention to detail, proper error handling, user experience, and code quality. The project should be production-ready and demonstrable as a portfolio piece for Krishna (Roll No: 366), Adarsh (Roll No: 166), and Mahesh (Roll No: 165).

Good luck building this comprehensive document management and PDF toolkit application!
