package net.yslibrary.historian.uncaught_handler.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.yslibrary.historian.api.Historian;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.uncaught_handler.internal.ui.activity.UncaughtActivity;

import org.parceler.Parcels;

import java.lang.Thread.UncaughtExceptionHandler;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static android.content.Intent.FLAG_ACTIVITY_TASK_ON_HOME;
import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static net.yslibrary.historian.uncaught_handler.internal.Constantes.CONFIGURATION_KEY;
import static net.yslibrary.historian.uncaught_handler.internal.Constantes.STACKTRACE_KEY;

/**
 * Class HistorianUncaughtExceptionHandler
 *
 * @author pecet86 - created
 */
@Keep
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HistorianUncaughtExceptionHandler implements UncaughtExceptionHandler {

  private static HistorianUncaughtExceptionHandler INSTANCE;

  private final Context context;
  private final Historian historian;
  private final UncaughtExceptionHandler handler;
  private final CrashConfig crashConfig;

  private static HistorianUncaughtExceptionHandler create(
      @NonNull Application app, @NonNull Historian historian, @NonNull CrashConfig crashConfig) {
    return new HistorianUncaughtExceptionHandler(
        app.getApplicationContext(),
        historian,
        getDefaultUncaughtExceptionHandler(),
        crashConfig
    );
  }

  public static synchronized void install(@NonNull Application app, @NonNull Historian historian) {
    install(app, historian, new CrashConfig());
  }

  public static synchronized void install(@NonNull Application app, @NonNull Historian historian,
                                          @NonNull CrashConfig crashConfig) {
    if (INSTANCE != null) {
      return;
    }

    HistorianUncaughtExceptionHandler exceptionHandler = create(app, historian, crashConfig);
    Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

    INSTANCE = exceptionHandler;
  }

  public static synchronized HistorianUncaughtExceptionHandler getInstance() {
    return INSTANCE;
  }

  private Intent createIntent(@NonNull Throwable th) {
    final int flags = FLAG_ACTIVITY_CLEAR_TOP |
        FLAG_ACTIVITY_NEW_TASK |
        FLAG_ACTIVITY_TASK_ON_HOME |
        FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
        FLAG_ACTIVITY_NO_HISTORY;

    return new Intent(context, UncaughtActivity.class)
        .setFlags(flags)
        .putExtra(STACKTRACE_KEY, Parcels.wrap(new LogEntity(Log.ERROR, "UncaughtException", th.getMessage(), th)))
        .putExtra(CONFIGURATION_KEY, crashConfig);
  }

  @Override
  public void uncaughtException(@NonNull Thread t, @NonNull Throwable th) {
    try {
      historian.log(Log.ERROR, "UncaughtException", th.getMessage(), th);
      if (crashConfig.getEventListener() != null) {
        crashConfig.getEventListener().onLaunchErrorActivity();
      }
      context.startActivity(createIntent(th));

      if (handler != null) {
        handler.uncaughtException(t, th);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      if (handler != null) {
        handler.uncaughtException(t, th);
      }
    }
  }
}
