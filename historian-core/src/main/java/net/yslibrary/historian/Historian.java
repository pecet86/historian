package net.yslibrary.historian;

import android.content.Context;
import android.util.Log;

import net.yslibrary.historian.internal.LogWritingTask;
import net.yslibrary.historian.internal.Util;
import net.yslibrary.historian.internal.datebase.LogsDatabase;
import net.yslibrary.historian.internal.entities.LogEntity;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.CheckResult;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;

import static net.yslibrary.historian.internal.Util.DB_NAME;
import static net.yslibrary.historian.internal.Util.LOG_LEVEL;
import static net.yslibrary.historian.internal.Util.getDatabasesDir;

/**
 * Historian
 */

public class Historian {

  private static final String TAG = Historian.class.getSimpleName();

  private final Context context;
  @Getter
  private final String dbName;
  @Getter
  private final int logLevel;
  @Getter
  private final boolean debug;
  private final Callbacks callbacks;

  private final LogsDatabase database;
  private final ExecutorService executorService;

  private Historian(Context context, String dbName, int logLevel, boolean debug, Callbacks callbacks) {
    this.context = context;
    this.dbName = dbName;
    this.logLevel = logLevel;
    this.debug = debug;
    this.callbacks = (callbacks == null) ? new DefaultCallbacks(debug) : callbacks;

    database = LogsDatabase.getInstance(context, dbName);

    if (debug) {
      Log.d(TAG, String.format(
          Locale.ENGLISH,
          "backing database file will be created at: %s",
          dbName
      ));
    }

    executorService = Executors.newSingleThreadExecutor();
  }

  /**
   * Get Builder
   *
   * @param context Context
   * @return {@link Builder}
   */
  @CheckResult
  public static Builder builder(Context context) {
    return new Builder(context);
  }

  public void log(int priority, String tag, String message) {
    if (priority < logLevel) {
      return;
    }
    if (message == null || message.length() == 0) {
      return;
    }

    executorService.execute(new LogWritingTask(
        callbacks,
        database.logEntityDao(),
        new LogEntity(priority, tag, message)
    ));
  }

  public Completable checkpointe() {
    return database
        .systemDao()
        .checkpointe()
        .subscribeOn(Schedulers.io())
        .ignoreElement();
  }

  /**
   * delete cache
   */
  public Completable clear() {
    return database
        .logEntityDao()
        .clearAll()
        .subscribeOn(Schedulers.io());
  }

  public File getDbFile() {
    File databases = getDatabasesDir(context);
    return new File(databases, dbName);
  }

  public interface Callbacks {
    void onSuccess();

    void onFailure(Throwable throwable);
  }

  /**
   * Builder class for {@link net.yslibrary.historian.Historian}
   */
  public static class Builder {

    private final Context context;
    private String name = DB_NAME;
    private int logLevel = LOG_LEVEL;
    private boolean debug = false;
    private Callbacks callbacks = null;

    private Builder(Context context) {
      this.context = context.getApplicationContext();
    }

    /**
     * Specify a name of the Historian's Database file
     * <p>
     * Default is {@link Util#DB_NAME}
     *
     * @param name file name of the backing SQLite database file
     * @return Builder
     */
    @CheckResult
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Specify minimum log level to save. The value should be any one of
     * {@link android.util.Log#VERBOSE},
     * {@link android.util.Log#DEBUG},
     * {@link android.util.Log#INFO},
     * {@link android.util.Log#WARN},
     * {@link android.util.Log#ERROR} or
     * {@link android.util.Log#ASSERT}.
     * <p>
     * Default is {@link android.util.Log#INFO}
     *
     * @param logLevel log level
     * @return Builder
     */
    @CheckResult
    public Builder logLevel(int logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    /**
     * Enable/disable Historian's debug logs(not saved to SQLite).
     * <p>
     * Default is false.
     *
     * @param debug true: output logs. false: no debug logs
     * @return Builder
     */
    @CheckResult
    public Builder debug(boolean debug) {
      this.debug = debug;
      return this;
    }

    /**
     * Specify callbacks. This callbacks are called each time Historian save a log.
     * This callbacks are called on background thread.
     * <p>
     * Default is {@link DefaultCallbacks}
     *
     * @param callbacks callbacks to execute.
     * @return Builder
     */
    @CheckResult
    public Builder callbacks(Callbacks callbacks) {
      this.callbacks = callbacks;
      return this;
    }

    /**
     * Build Historian. You need to call this method to use {@link Historian}
     *
     * @return {@link Historian}
     */
    @CheckResult
    public Historian build() {
      return new Historian(context, name, logLevel, debug, callbacks);
    }
  }

  public static class DefaultCallbacks implements Callbacks {
    private final boolean debug;

    DefaultCallbacks(boolean debug) {
      this.debug = debug;
    }

    @Override
    public void onSuccess() {
      // no-op
    }

    @Override
    public void onFailure(Throwable throwable) {
      if (debug) {
        Log.e(TAG, "Something happened while trying to save a log", throwable);
      }
    }
  }
}
