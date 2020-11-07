package net.yslibrary.historian.uncaught_rxjava2;

import android.app.Application;

import net.yslibrary.historian.Historian;
import net.yslibrary.historian.uncaught_handler.HistorianUncaughtExceptionHandler;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Class HistorianRxJavaExceptionHandler
 *
 * @author pecet86 - created
 */
@Keep
public class HistorianRxJavaExceptionHandler implements Consumer<Throwable> {

  private static HistorianRxJavaExceptionHandler INSTANCE;
  private HistorianUncaughtExceptionHandler exceptionHandler;

  private HistorianRxJavaExceptionHandler(HistorianUncaughtExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  private static HistorianRxJavaExceptionHandler create(
      @NonNull Application app, @NonNull Historian historian) {
    HistorianUncaughtExceptionHandler handler = HistorianUncaughtExceptionHandler.getInstance();
    return new HistorianRxJavaExceptionHandler(handler);
  }

  public static void install(@NonNull Application app, @NonNull Historian historian) {
    if (INSTANCE != null) {
      return;
    }

    HistorianRxJavaExceptionHandler exceptionHandler = create(app, historian);
    RxJavaPlugins.setErrorHandler(exceptionHandler);

    INSTANCE = exceptionHandler;
  }

  public static synchronized HistorianUncaughtExceptionHandler getInstance() {
    return HistorianUncaughtExceptionHandler.getInstance();
  }

  @Override
  public void accept(Throwable throwable) throws Exception {
    exceptionHandler.uncaughtException(Thread.currentThread(), throwable);
  }
}
