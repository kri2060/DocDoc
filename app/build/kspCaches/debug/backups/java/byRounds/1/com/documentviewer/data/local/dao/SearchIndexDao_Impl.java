package com.documentviewer.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.documentviewer.data.local.entity.SearchIndexEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SearchIndexDao_Impl implements SearchIndexDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SearchIndexEntity> __insertionAdapterOfSearchIndexEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByFilePath;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SearchIndexDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSearchIndexEntity = new EntityInsertionAdapter<SearchIndexEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `search_index` (`id`,`filePath`,`fileName`,`content`,`pageNumber`,`indexedAt`,`fileType`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchIndexEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFilePath());
        statement.bindString(3, entity.getFileName());
        statement.bindString(4, entity.getContent());
        if (entity.getPageNumber() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getPageNumber());
        }
        statement.bindLong(6, entity.getIndexedAt());
        statement.bindString(7, entity.getFileType());
      }
    };
    this.__preparedStmtOfDeleteByFilePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_index WHERE filePath = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_index";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SearchIndexEntity index,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSearchIndexEntity.insert(index);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<SearchIndexEntity> indices,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSearchIndexEntity.insert(indices);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByFilePath(final String filePath,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByFilePath.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, filePath);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByFilePath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SearchIndexEntity>> search(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM search_index\n"
            + "        WHERE content LIKE '%' || ? || '%' OR fileName LIKE '%' || ? || '%'\n"
            + "        ORDER BY indexedAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"search_index"}, new Callable<List<SearchIndexEntity>>() {
      @Override
      @NonNull
      public List<SearchIndexEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfIndexedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "indexedAt");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final List<SearchIndexEntity> _result = new ArrayList<SearchIndexEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SearchIndexEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final Integer _tmpPageNumber;
            if (_cursor.isNull(_cursorIndexOfPageNumber)) {
              _tmpPageNumber = null;
            } else {
              _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            }
            final long _tmpIndexedAt;
            _tmpIndexedAt = _cursor.getLong(_cursorIndexOfIndexedAt);
            final String _tmpFileType;
            _tmpFileType = _cursor.getString(_cursorIndexOfFileType);
            _item = new SearchIndexEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpContent,_tmpPageNumber,_tmpIndexedAt,_tmpFileType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<SearchIndexEntity>> getIndexForFile(final String filePath) {
    final String _sql = "SELECT * FROM search_index WHERE filePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, filePath);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"search_index"}, new Callable<List<SearchIndexEntity>>() {
      @Override
      @NonNull
      public List<SearchIndexEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfIndexedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "indexedAt");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final List<SearchIndexEntity> _result = new ArrayList<SearchIndexEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SearchIndexEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final Integer _tmpPageNumber;
            if (_cursor.isNull(_cursorIndexOfPageNumber)) {
              _tmpPageNumber = null;
            } else {
              _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            }
            final long _tmpIndexedAt;
            _tmpIndexedAt = _cursor.getLong(_cursorIndexOfIndexedAt);
            final String _tmpFileType;
            _tmpFileType = _cursor.getString(_cursorIndexOfFileType);
            _item = new SearchIndexEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpContent,_tmpPageNumber,_tmpIndexedAt,_tmpFileType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
