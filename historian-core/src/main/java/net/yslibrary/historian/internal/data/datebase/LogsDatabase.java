package net.yslibrary.historian.internal.data.datebase;

import android.content.Context;

import net.yslibrary.historian.internal.data.dao.LogsDao;
import net.yslibrary.historian.internal.data.dao.SystemDao;
import net.yslibrary.historian.internal.data.entities.LogEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Class LogsDatabase
 *
 * @author pecet86 - created
 */
@Database(
    entities = {
        LogEntity.class,
    },
    version = 2,
    exportSchema = false
)
public abstract class LogsDatabase extends RoomDatabase {
  public abstract LogsDao logEntityDao();

  public abstract SystemDao systemDao();

  //<editor-fold desc="create">
  private static LogsDatabase INSTANCE;

  public static synchronized LogsDatabase getInstance(Context context, String name) {
    if (INSTANCE == null) {
      INSTANCE = buildRoomDb(context, name);
    }

    return INSTANCE;
  }

  public static synchronized void closeInstance() {
    if (INSTANCE != null) {
      if (INSTANCE.isOpen()) {
        INSTANCE.close();
      }
    }

    INSTANCE = null;
  }

  public static LogsDatabase buildRoomDb(Context context, String name) {
    return Room.databaseBuilder(context, LogsDatabase.class, name)
        .fallbackToDestructiveMigration()
        .build();
  }
  //</editor-fold>
}
