package net.yslibrary.historian.api;

import android.content.Context;
import android.util.Log;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import net.yslibrary.historian.internal.support.RepositoryProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RetentionManager {

  protected static final String TAG = RetentionManager.class.getSimpleName();

  private static final String PREFS_NAME = "historian_preferences";
  private static final String KEY_LAST_CLEANUP = "last_cleanup";

  private static long lastCleanup = 0;

  @NonNull
  private final Context context;
  private final long period;
  private final long cleanupFrequency;
  private final RxSharedPreferences preferences;

  public RetentionManager(@NonNull Context context, @NonNull Period retentionPeriod) {
    this.context = context;

    period = retentionPeriod.toMillis();
    preferences = RxSharedPreferences.create(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
    cleanupFrequency = retentionPeriod == Period.ONE_HOUR
        ? TimeUnit.MINUTES.toMillis(30L)
        : TimeUnit.HOURS.toMillis(2L);
  }

  public void doMaintenance() {
    if (period > 0) {
      long now = System.currentTimeMillis();
      if (isCleanupDue(now)) {
        Log.i(TAG, "Performing data retention maintenance...");
        deleteSince(getThreshold(now));
        updateLastCleanup(now);
      }
    }
  }

  private boolean isCleanupDue(long now) {
    return now - getLastCleanup(now) > cleanupFrequency;
  }

  private long getLastCleanup(long fallback) {
    if (lastCleanup == 0L) {
      lastCleanup = preferences.getLong(KEY_LAST_CLEANUP, fallback).get();
    }
    return lastCleanup;
  }

  private void updateLastCleanup(long time) {
    lastCleanup = time;
    preferences.getLong(KEY_LAST_CLEANUP).set(time);
  }

  private long getThreshold(long now) {
    return period == 0L ? now : now - period;
  }

  private void deleteSince(long threshold) {
    RepositoryProvider
        .getDatabase()
        .logEntityDao()
        .deleteBefore(threshold)
        .subscribeOn(Schedulers.io())
        .subscribe();
  }
  
  @RequiredArgsConstructor
  @Getter
  public enum Period {
    /**
     * Retain data for the last hour.
     */
    ONE_HOUR(1, TimeUnit.HOURS),
    /**
     * Retain data for the last day.
     */
    ONE_DAY(1, TimeUnit.DAYS),
    /**
     * Retain data for the last week.
     */
    ONE_WEEK(7, TimeUnit.DAYS),
    /**
     * Retain data forever.
     */
    FOREVER(0, null);

    private final long duration;
    private final TimeUnit unit;

    public long toMillis() {
      if (unit == null) {
        return duration;
      } else {
        return unit.toMillis(duration);
      }
    }
  }
}
