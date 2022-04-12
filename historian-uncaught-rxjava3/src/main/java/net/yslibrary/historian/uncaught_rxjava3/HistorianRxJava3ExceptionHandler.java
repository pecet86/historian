package net.yslibrary.historian.uncaught_rxjava3;

import net.yslibrary.historian.uncaught_handler.api.HistorianUncaughtExceptionHandler;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * Class HistorianRxJava3ExceptionHandler
 *
 * @author pecet86 - created
 */
@Keep
public class HistorianRxJava3ExceptionHandler implements Consumer<Throwable> {

  private static HistorianRxJava3ExceptionHandler INSTANCE;
  private final HistorianUncaughtExceptionHandler exceptionHandler;

  private HistorianRxJava3ExceptionHandler(HistorianUncaughtExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  private static HistorianRxJava3ExceptionHandler create(@NonNull HistorianUncaughtExceptionHandler handler) {
    return new HistorianRxJava3ExceptionHandler(handler);
  }

  public static void install(@NonNull HistorianUncaughtExceptionHandler handler) {
    if (INSTANCE != null) {
      return;
    }

    HistorianRxJava3ExceptionHandler exceptionHandler = create(handler);
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
