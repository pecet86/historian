package net.yslibrary.historian.internal.support.services;

import android.content.Context;
import android.content.Intent;

import net.yslibrary.historian.internal.support.NotificationHelper;
import net.yslibrary.historian.internal.support.RepositoryProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static net.yslibrary.historian.internal.Constantes.CLEAN_DATABASE_JOB_ID;

public class ClearDatabaseService extends JobIntentService {

  /**
   * Convenience method for enqueuing work in to this service.
   */
  static void enqueueWork(Context context, Intent work) {
    enqueueWork(context, ClearDatabaseService.class, CLEAN_DATABASE_JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    onHandleIntent(intent);
  }

  protected void onHandleIntent(@Nullable Intent intent) {
    if (intent == null) {
      return;
    }

    RepositoryProvider.initialize(getApplicationContext());

    RepositoryProvider
        .getDatabase()
        .logEntityDao()
        .clearAll()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorComplete(throwable -> true)
        .subscribe();
    new NotificationHelper(this)
        .dismissNotification();
  }
}
