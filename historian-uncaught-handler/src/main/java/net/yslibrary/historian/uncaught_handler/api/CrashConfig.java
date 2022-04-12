package net.yslibrary.historian.uncaught_handler.api;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import static net.yslibrary.historian.uncaught_handler.R.drawable.historian_cow_error;

/**
 * Class HistorianUncaughtExceptionHandler
 *
 * @author Selim - created
 * @author pecet86 - modification
 */
@SuppressWarnings("FieldMayBeFinal")
@Getter
@With
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrashConfig implements Serializable {

  private static final long serialVersionUID = 65514326969573797L;
  private EventListener eventListener;

  /**
   * @param DestinationActivity will opened after the crash, MUST BE UR STARTING ACTIVITY
   */
  @NonNull
  private Class<? extends AppCompatActivity> restartActivityClass;
  private boolean restartActivityEnable = true;

  /**
   * @param CLOSE application
   */
  private boolean closeActivityEnable = true;

  /**
   * @param DetailsButonText ur error image change what you want. MUST BE "R.drawable.example" or like that
   */
  @DrawableRes
  private Integer imagePath = historian_cow_error;

  /**
   * mapping to unpack the error
   */
  @NonNull
  private transient Function<Throwable, Throwable> mapErrorHandler = throwable -> throwable;

  /**
   * some error can be ignored
   */
  @NonNull
  private transient Predicate<Throwable> testErrorHandler = throwable -> true;

  public interface EventListener extends Serializable {
    void onLaunchErrorActivity();

    void onRestartAppFromErrorActivity();

    void onCloseAppFromErrorActivity();
  }
}
