package net.yslibrary.historian.internal.dao;

import androidx.room.Dao;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Class SystemDao
 * DAO that allows you to perform system operations on the database
 *
 * @author pecet86 - created
 */
@Dao
public abstract class SystemDao {

  @RawQuery
  protected abstract int pragma(SupportSQLiteQuery supportSQLiteQuery);

  /**
   * Performing the transfer of temporary files to the main database file
   *
   * @return liczba
   */
  public Single<Integer> checkpointe() {
    return Single
        .create((SingleEmitter<Integer> emitter) -> emitter.onSuccess(checkpointeSync()))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * Performing the transfer of temporary files to the main database file
   *
   * @return liczba
   */
  public int checkpointeSync() {
    return pragma(new SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"));
  }
}
