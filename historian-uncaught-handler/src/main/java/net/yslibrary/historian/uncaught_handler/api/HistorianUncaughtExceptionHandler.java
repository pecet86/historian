package net.yslibrary.historian.uncaught_handler.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.yslibrary.historian.api.Historian;
import net.yslibrary.historian.uncaught_handler.internal.ui.activity.UncaughtActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static net.yslibrary.historian.uncaught_handler.internal.ui.activity.UncaughtActivity.CONFIGURATION_KEY;
import static net.yslibrary.historian.uncaught_handler.internal.ui.activity.UncaughtActivity.STACKTRACE_KEY;

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
    Writer result = new StringWriter();
    PrintWriter printWriter = new PrintWriter(result);
    th.printStackTrace(printWriter);
    String stacktrace = result.toString();
    printWriter.close();

    final int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP |
        Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_TASK_ON_HOME |
        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
        Intent.FLAG_ACTIVITY_NO_HISTORY;

    return new Intent(context, UncaughtActivity.class)
        .setFlags(flags)
        .putExtra(STACKTRACE_KEY, stacktrace)
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

  private static String getStackTraceString(@NonNull Throwable th) {
    StringWriter sw = new StringWriter(256);
    PrintWriter pw = new PrintWriter(sw, false);
    th.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }
}
