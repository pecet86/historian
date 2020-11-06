package net.yslibrary.historian.internal;

import android.database.sqlite.SQLiteStatement;

/**
 * Class for log writing operation
 */

public class LogWriter {

  private final DbOpenHelper dbOpenHelper;

  private final int size;

  public LogWriter(DbOpenHelper dbOpenHelper, int size) {
    this.dbOpenHelper = dbOpenHelper;
    this.size = size;
  }

  public void log(LogEntity log) {
    dbOpenHelper.executeTransaction(db -> {

      // insert provided log
      SQLiteStatement insertStatement = db.compileStatement(LogTable.INSERT);
      insertStatement.bindString(1, log.getPriority());
      insertStatement.bindString(2, log.getTag() == null ? "" : log.getTag());
      insertStatement.bindString(3, log.getMessage());
      insertStatement.bindLong(4, log.getTimestamp());
      insertStatement.execute();

      // delete if row count exceeds provided size
      SQLiteStatement deleteStatement = db.compileStatement(LogTable.DELETE_OLDER);
      deleteStatement.bindLong(1, (long) size);
      deleteStatement.execute();
    });
  }

  /**
   * Clear logs in SQLite.
   */
  public void delete() {
    dbOpenHelper.executeTransaction(db -> db.delete(LogTable.NAME, null, new String[]{}));
  }
}
