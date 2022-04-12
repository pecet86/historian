package net.yslibrary.historian.uncaught_rxjava2;

import net.yslibrary.historian.uncaught_handler.api.HistorianUncaughtExceptionHandler;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Class HistorianRxJava2ExceptionHandler
 *
 * @author pecet86 - created
 */
@Keep
public class HistorianRxJava2ExceptionHandler implements Consumer<Throwable> {

  private static HistorianRxJava2ExceptionHandler INSTANCE;
  private final HistorianUncaughtExceptionHandler exceptionHandler;

  private HistorianRxJava2ExceptionHandler(HistorianUncaughtExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  private static HistorianRxJava2ExceptionHandler create(@NonNull HistorianUncaughtExceptionHandler handler) {
    return new HistorianRxJava2ExceptionHandler(handler);
  }

  public static void install(@NonNull HistorianUncaughtExceptionHandler handler) {
    if (INSTANCE != null) {
      return;
    }

    HistorianRxJava2ExceptionHandler exceptionHandler = create(handler);
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
