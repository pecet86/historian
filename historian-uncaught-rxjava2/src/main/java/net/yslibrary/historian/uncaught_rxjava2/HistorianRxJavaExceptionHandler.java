package net.yslibrary.historian.uncaught_rxjava2;

import net.yslibrary.historian.uncaught_handler.api.HistorianUncaughtExceptionHandler;

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
  private final HistorianUncaughtExceptionHandler exceptionHandler;

  private HistorianRxJavaExceptionHandler(HistorianUncaughtExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  private static HistorianRxJavaExceptionHandler create(@NonNull HistorianUncaughtExceptionHandler handler) {
    return new HistorianRxJavaExceptionHandler(handler);
  }

  public static void install(@NonNull HistorianUncaughtExceptionHandler handler) {
    if (INSTANCE != null) {
      return;
    }

    HistorianRxJavaExceptionHandler exceptionHandler = create(handler);
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
