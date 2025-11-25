package com.documentviewer.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.documentviewer.data.local.entity.ReadingPositionEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class ReadingPositionDao_Impl implements ReadingPositionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReadingPositionEntity> __insertionAdapterOfReadingPositionEntity;

  private final EntityDeletionOrUpdateAdapter<ReadingPositionEntity> __deletionAdapterOfReadingPositionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPath;

  public ReadingPositionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReadingPositionEntity = new EntityInsertionAdapter<ReadingPositionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_positions` (`filePath`,`pageNumber`,`scrollOffset`,`lastReadAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingPositionEntity entity) {
        statement.bindString(1, entity.getFilePath());
        statement.bindLong(2, entity.getPageNumber());
        statement.bindDouble(3, entity.getScrollOffset());
        statement.bindLong(4, entity.getLastReadAt());
      }
    };
    this.__deletionAdapterOfReadingPositionEntity = new EntityDeletionOrUpdateAdapter<ReadingPositionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reading_positions` WHERE `filePath` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingPositionEntity entity) {
        statement.bindString(1, entity.getFilePath());
      }
    };
    this.__preparedStmtOfDeleteByPath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_positions WHERE filePath = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ReadingPositionEntity position,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingPositionEntity.insert(position);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ReadingPositionEntity position,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReadingPositionEntity.handle(position);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByPath(final String filePath, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPath.acquire();
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
          __preparedStmtOfDeleteByPath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ReadingPositionEntity> getPosition(final String filePath) {
    final String _sql = "SELECT * FROM reading_positions WHERE filePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, filePath);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_positions"}, new Callable<ReadingPositionEntity>() {
      @Override
      @Nullable
      public ReadingPositionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfScrollOffset = CursorUtil.getColumnIndexOrThrow(_cursor, "scrollOffset");
          final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
          final ReadingPositionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final float _tmpScrollOffset;
            _tmpScrollOffset = _cursor.getFloat(_cursorIndexOfScrollOffset);
            final long _tmpLastReadAt;
            _tmpLastReadAt = _cursor.getLong(_cursorIndexOfLastReadAt);
            _result = new ReadingPositionEntity(_tmpFilePath,_tmpPageNumber,_tmpScrollOffset,_tmpLastReadAt);
          } else {
            _result = null;
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
  public Flow<List<ReadingPositionEntity>> getRecentPositions() {
    final String _sql = "SELECT * FROM reading_positions ORDER BY lastReadAt DESC LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_positions"}, new Callable<List<ReadingPositionEntity>>() {
      @Override
      @NonNull
      public List<ReadingPositionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfScrollOffset = CursorUtil.getColumnIndexOrThrow(_cursor, "scrollOffset");
          final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
          final List<ReadingPositionEntity> _result = new ArrayList<ReadingPositionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingPositionEntity _item;
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final float _tmpScrollOffset;
            _tmpScrollOffset = _cursor.getFloat(_cursorIndexOfScrollOffset);
            final long _tmpLastReadAt;
            _tmpLastReadAt = _cursor.getLong(_cursorIndexOfLastReadAt);
            _item = new ReadingPositionEntity(_tmpFilePath,_tmpPageNumber,_tmpScrollOffset,_tmpLastReadAt);
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
