package net.yslibrary.historian.uncaught_handler.api;

import net.yslibrary.historian.uncaught_handler.R;

import java.io.Serializable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

/**
 * Class HistorianUncaughtExceptionHandler
 *
 * @author Selim - created
 * @author pecet86 - modification
 */
@Getter
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrashConfig implements Serializable {

  private EventListener eventListener;

  /**
   * @param DestinationActivity will opened after the crash, MUST BE UR STARTING ACTIVITY
   */
  @NonNull
  private Class<? extends AppCompatActivity> restartActivityClass;
  private boolean restartActivityEnable;
  /**
   * @param restartActivityEnable restart your app(be careful starting ur DestinationActivity!). change your button's text what you want
   */
  @StringRes
  private Integer restartAppButtonText;

  private boolean closeActivityEnable;
  /**
   * @param closeAppButtonText close your app(be careful starting ur DestinationActivity!). change your button's text what you want
   */
  @StringRes
  private Integer closeAppButtonText;


  /**
   * @param CrashText your error message "oops its crash" or something.
   */
  @StringRes
  private Integer crashText;
  /**
   * @param CrashTextColor CrashText's color. MUST BE HEX CODE
   */
  @ColorRes
  private Integer crashTextColor;

  /**
   * @param DetailsButonText showing stacktrace with alert dialog. change your button's text what you want
   */
  @StringRes
  private Integer detailsButtonText;
  @StringRes
  private Integer detailsDialogTitle;
  @StringRes
  private Integer detailsDialogButtonText;

  /**
   * @param DetailsButonText ur error image change what you want. MUST BE "R.drawable.example" or like that
   */
  @DrawableRes
  private Integer imagePath;

  /**
   * @param RestartAppButtonText ur crash activity's backgorund color.change your activity's color what you want. MUST BE HEX COLOR
   */
  @ColorRes
  private Integer backgorundColor;

  public CrashConfig() {
    restartAppButtonText = R.string.historian_restart_app_button_text;
    restartActivityEnable = true;
    closeAppButtonText = R.string.historian_close_app_button_text;
    closeActivityEnable = true;

    crashText = R.string.historian_crash_text;
    crashTextColor = R.color.historian_crash_text;
    detailsButtonText = R.string.historian_details_button_text;
    detailsDialogTitle = R.string.historian_details_dialog_title;
    detailsDialogButtonText = R.string.historian_details_dialog_button_text;
    imagePath = R.drawable.historian_cow_error;
    backgorundColor = R.color.historian_backgorund;
  }

  public interface EventListener extends Serializable {
    void onLaunchErrorActivity();

    void onRestartAppFromErrorActivity();

    void onCloseAppFromErrorActivity();
  }
}
