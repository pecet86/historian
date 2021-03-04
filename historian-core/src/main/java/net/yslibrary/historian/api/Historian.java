package net.yslibrary.historian.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.yslibrary.historian.BuildConfig;
import net.yslibrary.historian.R;
import net.yslibrary.historian.internal.data.LogWritingTask;
import net.yslibrary.historian.internal.data.datebase.LogsDatabase;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.data.entities.PriorityType.LogPriority;
import net.yslibrary.historian.internal.support.NotificationHelper;
import net.yslibrary.historian.internal.ui.activities.MainActivity;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;

import static net.yslibrary.historian.api.RetentionManager.Period;
import static net.yslibrary.historian.internal.Constantes.DB_NAME;
import static net.yslibrary.historian.internal.Constantes.LOG_LEVEL;
import static net.yslibrary.historian.internal.Util.askForConfirmationClear;
import static net.yslibrary.historian.internal.Util.askForConfirmationExport;
import static net.yslibrary.historian.internal.Util.toastShort;

/**
 * Class HistorianTree
 *
 * @author yshrsmz - created
 * @author pecet86 - modification
 */
public class Historian {

  private static final String TAG = Historian.class.getSimpleName();
  private static final int MAX_CONTENT_LENGTH = 250000;

  private final Context context;
  @Getter
  private final int logLevel;
  @Getter
  private final boolean debug;
  @Getter
  private final boolean notification;
  @Getter
  private final int maxContentLength;
  private final Callbacks callbacks;

  private final LogsDatabase database;
  private final ExecutorService executorService;
  private final NotificationHelper notificationHelper;
  private final RetentionManager retentionManager;

  private Historian(Builder builder) {
    context = builder.context.getApplicationContext();
    logLevel = builder.logLevel;
    debug = builder.debug;
    notification = builder.notification;
    maxContentLength = builder.maxContentLength;
    callbacks = (builder.callbacks == null) ? new DefaultCallbacks(debug) : builder.callbacks;

    database = LogsDatabase.getInstance(context, DB_NAME);
    notificationHelper = new NotificationHelper(context);
    retentionManager = new RetentionManager(context, builder.retentionPeriod);
    executorService = Executors.newSingleThreadExecutor();

    if (debug) {
      Log.d(TAG, String.format(
          Locale.ENGLISH,
          "backing database file will be created at: %s",
          DB_NAME
      ));
    }
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

  /**
   * saving to the database
   *
   * @param priority  {@link LogPriority}
   * @param tag
   * @param message
   * @param throwable
   */
  public void log(@LogPriority int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable throwable) {
    if (priority < logLevel) {
      return;
    }
    if (message.isEmpty()) {
      return;
    }

    LogEntity logEntity = new LogEntity(
        priority,
        tag,
        message,
        throwable
    )
        .cutContentMax(maxContentLength);

    //save
    executorService.execute(new LogWritingTask(
        callbacks,
        database.logEntityDao(),
        logEntity
    ));

    if (notification) {
      notificationHelper.show(logEntity);
    }
    retentionManager.doMaintenance();
  }

  //<editor-fold desc="functions">

  /**
   * {@link net.yslibrary.historian.internal.data.dao.SystemDao#checkpointe}
   */
  public void checkpointe() {
    database
        .systemDao()
        .checkpointe()
        .subscribeOn(Schedulers.io())
        .subscribe();
  }

  /**
   * Exportuj dane
   */
  public void exportAll(Activity activity) {
    database
        .logEntityDao()
        .getAll()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(entities -> askForConfirmationExport(activity, entities))
        .ignoreElement()
        .onErrorComplete(throwable -> {
          toastShort(activity, R.string.historian_export_empty_text);
          return true;
        })
        .subscribe();
  }

  /**
   * Exportuj dane
   */
  public void exportLast(Activity activity) {
    database
        .logEntityDao()
        .getLast()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(entity -> askForConfirmationExport(activity, entity))
        .onErrorComplete()
        .doOnComplete(() -> toastShort(activity, R.string.historian_export_empty_text))
        .subscribe();
  }

  /**
   * Wyczyść bazę danych
   */
  public void clear() {
    askForConfirmationClear(context, this::clearElements);
  }

  private void clearElements() {
    database
        .logEntityDao()
        .clearAll()
        .subscribeOn(Schedulers.io())
        .subscribe();
  }
  //</editor-fold>

  //<editor-fold desc="global">
  public static final boolean isOp = BuildConfig.isOp;

  /**
   * Get an Intent to launch the Historian UI directly.
   *
   * @param context An Android [Context].
   * @return An Intent for the main Historian Activity that can be started with [Context.startActivity].
   */
  public static Intent getLaunchIntent(Context context) {
    return new Intent(context, MainActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  }

  /**
   * Get an Intent to launch the Historian UI directly.
   *
   * @param context An Android [Context].
   * @return An Intent for the main Historian Activity that can be started with [Context.startActivity].
   */
  public static Intent getClearLaunchIntent(Context context) {
    final int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP |
        Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_TASK_ON_HOME |
        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
        Intent.FLAG_ACTIVITY_NO_HISTORY;

    return new Intent(context, MainActivity.class)
        .setFlags(flags);
  }

  /**
   * Method to dismiss the Chucker notification of Uncaught Errors.
   */
  public static void dismissErrorsNotification(Context context) {
    new NotificationHelper(context).dismissNotification();
  }
  //</editor-fold>

  public interface Callbacks {
    void onSuccess();

    void onFailure(Throwable throwable);
  }

  /**
   * Builder class for {@link Historian}
   */
  public static class Builder {

    private final Context context;
    private int logLevel = LOG_LEVEL;
    private boolean debug = false;
    private int maxContentLength = MAX_CONTENT_LENGTH;
    private boolean notification = BuildConfig.notification;
    private Period retentionPeriod = Period.ONE_WEEK;
    private Callbacks callbacks = null;

    private Builder(Context context) {
      this.context = context.getApplicationContext();
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
    public Builder logLevel(@LogPriority int logLevel) {
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
     * Enable/disable Historian's notification logs.
     * <p>
     * Default is false.
     *
     * @param notification true: show notification logs. false: no notification logs
     * @return Builder
     */
    @CheckResult
    public Builder notification(boolean notification) {
      this.notification = notification;
      return this;
    }

    /**
     * Enable/disable Historian's size entity body .
     * <p>
     * Default is {@link Historian#MAX_CONTENT_LENGTH}
     *
     * @param maxContentLength max body size
     * @return Builder
     */
    @CheckResult
    public Builder maxContentLength(int maxContentLength) {
      this.maxContentLength = maxContentLength;
      return this;
    }

    /**
     * Class responsible of holding the logic for the retention of your throwable.
     * You can customize how long data should be stored here.
     *
     * <p>
     * Default is {@link RetentionManager.Period#ONE_WEEK}
     *
     * @param retentionPeriod to specify the retention of data.
     * @return Builder
     */
    @CheckResult
    public Builder retentionPeriod(Period retentionPeriod) {
      this.retentionPeriod = retentionPeriod;
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
      return new Historian(this);
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
