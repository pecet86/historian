package net.yslibrary.historian.internal.data.dao;

import net.yslibrary.historian.internal.data.entities.LogEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Class LogsDao
 * DAO describing operations on a database table [{@link LogsDao#TABLE}]
 *
 * @author pecet86 - created
 */
@Dao
public abstract class LogsDao {

  public static final String TABLE = "logs";

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public abstract long insertSync(LogEntity value);

  @Query("DELETE FROM " + TABLE)
  public abstract Completable clearAll();

  @Query("SELECT * FROM " + TABLE + " ORDER BY timestamp DESC")
  public abstract Single<List<LogEntity>> getAll();

  @Query("SELECT * FROM " + TABLE + " ORDER BY timestamp DESC LIMIT 1")
  public abstract Maybe<LogEntity> getLast();

  @Delete
  public abstract Completable delete(LogEntity value);

  @Query("SELECT * FROM " + TABLE + " WHERE id = :id")
  public abstract Maybe<LogEntity> getById(int id);

  @Query("SELECT * FROM " + TABLE + " ORDER BY timestamp DESC")
  public abstract LiveData<List<LogEntity>> getSortedAll();

  @Query("SELECT * FROM " + TABLE + " WHERE message LIKE :query ORDER BY timestamp DESC")
  public abstract LiveData<List<LogEntity>> getFilteredAll(String query);

  @Query("DELETE FROM " + TABLE + " WHERE timestamp <= :threshold")
  public abstract Completable deleteBefore(long threshold);
}
