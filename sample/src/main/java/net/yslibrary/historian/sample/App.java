package net.yslibrary.historian.sample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import net.yslibrary.historian.api.Historian;
import net.yslibrary.historian.tree.HistorianTree;
import net.yslibrary.historian.uncaught_handler.api.CrashConfig;
import net.yslibrary.historian.uncaught_handler.api.HistorianUncaughtExceptionHandler;
import net.yslibrary.historian.uncaught_rxjava2.HistorianRxJavaExceptionHandler;

import androidx.annotation.NonNull;
import timber.log.Timber;

import static net.yslibrary.historian.api.RetentionManager.Period;

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
        .debug(true)
        .logLevel(Log.ERROR) //minimal log lever
        .notification(true) //show notification
        .retentionPeriod(Period.ONE_WEEK) //when delete old
        .maxContentLength(200) //max size save body
        .callbacks(new Historian.Callbacks() { //added
          @Override
          public void onSuccess() {
            //is added to datebase
          }

          @Override
          public void onFailure(Throwable throwable) {
            //is error to datebase
          }
        })
        .build();

    //CrashActivity
    HistorianUncaughtExceptionHandler.install(this, historian,
        new CrashConfig()
            /*.withRestartActivityEnable(true)
            .withCloseActivityEnable(true)
            .withImagePath(R.drawable.historian_cow_error)*/
            .withRestartActivityClass(MainActivity.class)
    );

    //Global onError
    HistorianRxJavaExceptionHandler.install(
        HistorianUncaughtExceptionHandler.getInstance()
    );

    Timber.plant(new Timber.DebugTree());
    //install to Timber
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
