package net.yslibrary.historian.internal.dao;

import net.yslibrary.historian.internal.entities.LogEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Class LogEntityDao
 * DAO describing operations on a database table [{@link LogEntityDao#TABLE}]
 *
 * @author pecet86 - created
 */
@Dao
public abstract class LogEntityDao {

  public static final String TABLE = "logs";

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public abstract long insertSync(LogEntity value);

  @Query("DELETE FROM " + TABLE)
  public abstract Completable clearAll();

  @Query("SELECT * FROM " + TABLE)
  public abstract Single<List<LogEntity>> getAll();

  @Query("SELECT * FROM " + TABLE + " WHERE id = :id")
  public abstract Maybe<LogEntity> getById(int id);
}
