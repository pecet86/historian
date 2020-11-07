package net.yslibrary.historian.sample;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import net.yslibrary.historian.Historian;
import net.yslibrary.historian.tree.HistorianTree;

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

    historian = Historian.builder(this).build();

    Timber.plant(new Timber.DebugTree());
    Timber.plant(HistorianTree.with(historian));

    Stetho.initializeWithDefaults(this);
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }

  public Historian getHistorian() {
    return historian;
  }
}
