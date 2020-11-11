package net.yslibrary.historian.uncaught_handler.internal;

import android.app.Activity;
import android.content.Intent;

import net.yslibrary.historian.uncaught_handler.api.CrashConfig;

import androidx.annotation.NonNull;
import lombok.experimental.UtilityClass;

import static android.os.Process.killProcess;

/**
 * Class Util
 *
 * @author Paweł Cal
 */
@UtilityClass
public class Util {

  private static void killCurrentProcess() {
    killProcess(android.os.Process.myPid());
    System.exit(10);
  }

  private static void restartApplicationWithIntent(@NonNull Activity activity, @NonNull Intent intent, @NonNull CrashConfig config) {
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    if (intent.getComponent() != null) {
      intent.setAction(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
    }
    if (config.getEventListener() != null) {
      config.getEventListener().onRestartAppFromErrorActivity();
    }
    activity.finish();
    activity.startActivity(intent);
    killCurrentProcess();
  }

  public static void restartApplication(@NonNull Activity activity, @NonNull CrashConfig config) {
    Intent intent = new Intent(activity, config.getRestartActivityClass());
    restartApplicationWithIntent(activity, intent, config);
  }

  public static void closeApplication(@NonNull Activity activity, @NonNull CrashConfig config) {
    if (config.getEventListener() != null) {
      config.getEventListener().onCloseAppFromErrorActivity();
    }
    activity.finish();
    killCurrentProcess();
  }
}
