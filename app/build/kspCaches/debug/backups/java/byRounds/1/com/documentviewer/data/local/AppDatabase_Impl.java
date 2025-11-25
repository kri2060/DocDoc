package com.documentviewer.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.documentviewer.data.local.dao.FavoriteDao;
import com.documentviewer.data.local.dao.FavoriteDao_Impl;
import com.documentviewer.data.local.dao.NoteDao;
import com.documentviewer.data.local.dao.NoteDao_Impl;
import com.documentviewer.data.local.dao.ReadingPositionDao;
import com.documentviewer.data.local.dao.ReadingPositionDao_Impl;
import com.documentviewer.data.local.dao.RecentFileDao;
import com.documentviewer.data.local.dao.RecentFileDao_Impl;
import com.documentviewer.data.local.dao.SearchIndexDao;
import com.documentviewer.data.local.dao.SearchIndexDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RecentFileDao _recentFileDao;

  private volatile FavoriteDao _favoriteDao;

  private volatile NoteDao _noteDao;

  private volatile ReadingPositionDao _readingPositionDao;

  private volatile SearchIndexDao _searchIndexDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `recent_files` (`path` TEXT NOT NULL, `name` TEXT NOT NULL, `size` INTEGER NOT NULL, `lastAccessed` INTEGER NOT NULL, `mimeType` TEXT, `type` TEXT NOT NULL, PRIMARY KEY(`path`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`path` TEXT NOT NULL, `name` TEXT NOT NULL, `size` INTEGER NOT NULL, `addedAt` INTEGER NOT NULL, `mimeType` TEXT, `type` TEXT NOT NULL, PRIMARY KEY(`path`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `modifiedAt` INTEGER NOT NULL, `filePath` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_positions` (`filePath` TEXT NOT NULL, `pageNumber` INTEGER NOT NULL, `scrollOffset` REAL NOT NULL, `lastReadAt` INTEGER NOT NULL, PRIMARY KEY(`filePath`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_index` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `content` TEXT NOT NULL, `pageNumber` INTEGER, `indexedAt` INTEGER NOT NULL, `fileType` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f2bd6033222626551f7c35a3cc5d309c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `recent_files`");
        db.execSQL("DROP TABLE IF EXISTS `favorites`");
        db.execSQL("DROP TABLE IF EXISTS `notes`");
        db.execSQL("DROP TABLE IF EXISTS `reading_positions`");
        db.execSQL("DROP TABLE IF EXISTS `search_index`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRecentFiles = new HashMap<String, TableInfo.Column>(6);
        _columnsRecentFiles.put("path", new TableInfo.Column("path", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("lastAccessed", new TableInfo.Column("lastAccessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("mimeType", new TableInfo.Column("mimeType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentFiles.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecentFiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecentFiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecentFiles = new TableInfo("recent_files", _columnsRecentFiles, _foreignKeysRecentFiles, _indicesRecentFiles);
        final TableInfo _existingRecentFiles = TableInfo.read(db, "recent_files");
        if (!_infoRecentFiles.equals(_existingRecentFiles)) {
          return new RoomOpenHelper.ValidationResult(false, "recent_files(com.documentviewer.data.local.entity.RecentFileEntity).\n"
                  + " Expected:\n" + _infoRecentFiles + "\n"
                  + " Found:\n" + _existingRecentFiles);
        }
        final HashMap<String, TableInfo.Column> _columnsFavorites = new HashMap<String, TableInfo.Column>(6);
        _columnsFavorites.put("path", new TableInfo.Column("path", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("mimeType", new TableInfo.Column("mimeType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFavorites = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFavorites = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFavorites = new TableInfo("favorites", _columnsFavorites, _foreignKeysFavorites, _indicesFavorites);
        final TableInfo _existingFavorites = TableInfo.read(db, "favorites");
        if (!_infoFavorites.equals(_existingFavorites)) {
          return new RoomOpenHelper.ValidationResult(false, "favorites(com.documentviewer.data.local.entity.FavoriteEntity).\n"
                  + " Expected:\n" + _infoFavorites + "\n"
                  + " Found:\n" + _existingFavorites);
        }
        final HashMap<String, TableInfo.Column> _columnsNotes = new HashMap<String, TableInfo.Column>(6);
        _columnsNotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("modifiedAt", new TableInfo.Column("modifiedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("filePath", new TableInfo.Column("filePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotes = new TableInfo("notes", _columnsNotes, _foreignKeysNotes, _indicesNotes);
        final TableInfo _existingNotes = TableInfo.read(db, "notes");
        if (!_infoNotes.equals(_existingNotes)) {
          return new RoomOpenHelper.ValidationResult(false, "notes(com.documentviewer.data.local.entity.NoteEntity).\n"
                  + " Expected:\n" + _infoNotes + "\n"
                  + " Found:\n" + _existingNotes);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingPositions = new HashMap<String, TableInfo.Column>(4);
        _columnsReadingPositions.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingPositions.put("pageNumber", new TableInfo.Column("pageNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingPositions.put("scrollOffset", new TableInfo.Column("scrollOffset", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingPositions.put("lastReadAt", new TableInfo.Column("lastReadAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingPositions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingPositions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReadingPositions = new TableInfo("reading_positions", _columnsReadingPositions, _foreignKeysReadingPositions, _indicesReadingPositions);
        final TableInfo _existingReadingPositions = TableInfo.read(db, "reading_positions");
        if (!_infoReadingPositions.equals(_existingReadingPositions)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_positions(com.documentviewer.data.local.entity.ReadingPositionEntity).\n"
                  + " Expected:\n" + _infoReadingPositions + "\n"
                  + " Found:\n" + _existingReadingPositions);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchIndex = new HashMap<String, TableInfo.Column>(7);
        _columnsSearchIndex.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("pageNumber", new TableInfo.Column("pageNumber", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("indexedAt", new TableInfo.Column("indexedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchIndex.put("fileType", new TableInfo.Column("fileType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchIndex = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchIndex = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchIndex = new TableInfo("search_index", _columnsSearchIndex, _foreignKeysSearchIndex, _indicesSearchIndex);
        final TableInfo _existingSearchIndex = TableInfo.read(db, "search_index");
        if (!_infoSearchIndex.equals(_existingSearchIndex)) {
          return new RoomOpenHelper.ValidationResult(false, "search_index(com.documentviewer.data.local.entity.SearchIndexEntity).\n"
                  + " Expected:\n" + _infoSearchIndex + "\n"
                  + " Found:\n" + _existingSearchIndex);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f2bd6033222626551f7c35a3cc5d309c", "fc0aa07cbdcb34299881353fe6fd686b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "recent_files","favorites","notes","reading_positions","search_index");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `recent_files`");
      _db.execSQL("DELETE FROM `favorites`");
      _db.execSQL("DELETE FROM `notes`");
      _db.execSQL("DELETE FROM `reading_positions`");
      _db.execSQL("DELETE FROM `search_index`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RecentFileDao.class, RecentFileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FavoriteDao.class, FavoriteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NoteDao.class, NoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReadingPositionDao.class, ReadingPositionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchIndexDao.class, SearchIndexDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RecentFileDao recentFileDao() {
    if (_recentFileDao != null) {
      return _recentFileDao;
    } else {
      synchronized(this) {
        if(_recentFileDao == null) {
          _recentFileDao = new RecentFileDao_Impl(this);
        }
        return _recentFileDao;
      }
    }
  }

  @Override
  public FavoriteDao favoriteDao() {
    if (_favoriteDao != null) {
      return _favoriteDao;
    } else {
      synchronized(this) {
        if(_favoriteDao == null) {
          _favoriteDao = new FavoriteDao_Impl(this);
        }
        return _favoriteDao;
      }
    }
  }

  @Override
  public NoteDao noteDao() {
    if (_noteDao != null) {
      return _noteDao;
    } else {
      synchronized(this) {
        if(_noteDao == null) {
          _noteDao = new NoteDao_Impl(this);
        }
        return _noteDao;
      }
    }
  }

  @Override
  public ReadingPositionDao readingPositionDao() {
    if (_readingPositionDao != null) {
      return _readingPositionDao;
    } else {
      synchronized(this) {
        if(_readingPositionDao == null) {
          _readingPositionDao = new ReadingPositionDao_Impl(this);
        }
        return _readingPositionDao;
      }
    }
  }

  @Override
  public SearchIndexDao searchIndexDao() {
    if (_searchIndexDao != null) {
      return _searchIndexDao;
    } else {
      synchronized(this) {
        if(_searchIndexDao == null) {
          _searchIndexDao = new SearchIndexDao_Impl(this);
        }
        return _searchIndexDao;
      }
    }
  }
}
