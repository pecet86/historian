package net.yslibrary.historian.sample;

import android.app.Application;
import android.content.Context;

import net.yslibrary.historian.Historian;
import net.yslibrary.historian.tree.HistorianTree;
import net.yslibrary.historian.uncaught_handler.CrashConfig;
import net.yslibrary.historian.uncaught_handler.HistorianUncaughtExceptionHandler;
import net.yslibrary.historian.uncaught_rxjava2.HistorianRxJavaExceptionHandler;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * Created by yshrsmz on 17/01/20.
 * Modification by pecet86 on 2020/11/06.
 */

public class App extends Application {

  private Historian historian;

  public static App get(@NonNull Context context) {
    return (App) context.getApplicationContext();
  }

  public static Historian getHistorian(@NonNull Context context) {
    return get(context).getHistorian();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    historian = Historian
        .builder(this)
        .build();

    HistorianUncaughtExceptionHandler.install(this, historian,
        new CrashConfig()
            .withRestartActivityClass(MainActivity.class)
    );
    HistorianRxJavaExceptionHandler.install(this, historian);

    Timber.plant(new Timber.DebugTree());
    Timber.plant(HistorianTree.with(historian));
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }

  public Historian getHistorian() {
    return historian;
  }
}
