package net.yslibrary.historian.internal.dao;

import androidx.room.Dao;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class SystemDao {

  @RawQuery
  protected abstract int pragma(SupportSQLiteQuery supportSQLiteQuery);

  /**
   * Wykonanie przeniesienia plikó tymczasowych do głownego pliku bazy
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
   * Wykonanie przeniesienia plikó tymczasowych do głownego pliku bazy
   *
   * @return liczba
   */
  public int checkpointeSync() {
    return pragma(new SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"));
  }
}
