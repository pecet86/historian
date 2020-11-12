package net.yslibrary.historian.internal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import net.yslibrary.historian.R;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.support.helpers.LogDetailsSharable;
import net.yslibrary.historian.internal.support.helpers.LogListDetailsSharable;
import net.yslibrary.historian.internal.support.helpers.SharableUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ShareCompat;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lombok.experimental.UtilityClass;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import static net.yslibrary.historian.internal.Constantes.CLIP_DATA_LABEL;
import static net.yslibrary.historian.internal.Constantes.GET_FILE_FOR_SAVING_REQUEST_CODE;
import static net.yslibrary.historian.internal.Constantes.INTENT_ACTION_CREATE_DOCUMENT;
import static net.yslibrary.historian.internal.Constantes.INTENT_CATEGORY_OPENABLE;
import static net.yslibrary.historian.internal.Constantes.INTENT_INTENT_EXTRA_TITLE;
import static net.yslibrary.historian.internal.Constantes.LIST_EXPORT_FILE_NAME;
import static net.yslibrary.historian.internal.Constantes.PLAIN_MIME_TYPE;
import static net.yslibrary.historian.internal.Constantes.SINGLE_EXPORT_FILE_NAME;

/**
 * Class Util
 *
 * @author yshrsmz
 * @author Paweł Cal
 */
@UtilityClass
public class Util {

  public static File getFilesDir(@NonNull Context context) {
    return new File(context.getApplicationContext().getFilesDir().getParent(), "files");
  }

  public static File getDatabasesDir(Context context) {
    return new File(context.getApplicationContext().getFilesDir().getParent(), "databases");
  }

  public static void copyTo(FileDescriptor fileDescriptor, Source input) throws IOException {
    try (
        BufferedSink output = Okio.buffer(Okio.sink(new FileOutputStream(fileDescriptor)));
        BufferedSource source = Okio.buffer(input)
    ) {
      output.writeAll(source);
      output.flush();
    }
  }

  @IntDef(value = {
      Toast.LENGTH_SHORT,
      Toast.LENGTH_LONG
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Duration {
  }

  //<editor-fold desc="toast">
  @SuppressLint({"ShowToast", "AutoDispose"})
  public static void toast(@NonNull Context context, @NonNull String message, @Duration int duration) {
    Single
        .just(Toast.makeText(context, message, duration))
        .subscribeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(Toast::show)
        .subscribe();
  }

  public static void toastShort(@NonNull Context context, @NonNull String message) {
    toast(context, message, Toast.LENGTH_SHORT);
  }

  public static void toastLong(@NonNull Context context, @NonNull String message) {
    toast(context, message, Toast.LENGTH_LONG);
  }

  public static void toastShort(@NonNull Context context, @StringRes int id) {
    toast(context, context.getString(id), Toast.LENGTH_SHORT);
  }

  public static void toastLong(@NonNull Context context, @StringRes int id) {
    toast(context, context.getString(id), Toast.LENGTH_LONG);
  }
  //</editor-fold>

  //<editor-fold desc="export">
  public static void exportSingleLog(@NonNull Activity activity, LogEntity entity, boolean file) {
    if (entity == null) {
      toastShort(activity, R.string.historian_export_empty_text);
      return;
    }

    Intent shareIntent;
    if (file) {
      shareIntent = SharableUtil.shareAsFile(
          new LogDetailsSharable(entity),
          activity,
          SINGLE_EXPORT_FILE_NAME,
          R.string.historian_share_log_title,
          R.string.historian_share_log_subject,
          "throwables"
      );
    } else {
      String throwableDetailsText = activity.getString(
          R.string.historian_share_log_content,
          entity.getFormatTimestamp(),
          entity.getClazzOrEmpty(),
          entity.getTagOrEmpty(),
          entity.getMessage(),
          entity.getContentOrEmpty()
      );
      shareIntent = ShareCompat
          .IntentBuilder
          .from(activity)
          .setType(PLAIN_MIME_TYPE)
          .setChooserTitle(R.string.historian_share_log_title)
          .setSubject(activity.getString(R.string.historian_share_log_subject))
          .setText(throwableDetailsText)
          .createChooserIntent();
    }

    if (shareIntent != null) {
      activity.startActivity(shareIntent);
    }
  }

  public static void createFileToSaveBody(@NonNull Activity activity) {
    Intent intent = new Intent(INTENT_ACTION_CREATE_DOCUMENT)
        .addCategory(INTENT_CATEGORY_OPENABLE)
        .putExtra(INTENT_INTENT_EXTRA_TITLE, "historian-export-" + System.currentTimeMillis())
        .setType("*/*");
    if (intent.resolveActivity(activity.getPackageManager()) != null) {
      activity.startActivityForResult(intent, GET_FILE_FOR_SAVING_REQUEST_CODE);
    } else {
      toastShort(activity, R.string.historian_save_failed_to_open_document);
    }
  }

  public static void saveToFile(@NonNull Activity activity, @NotNull Uri uri, @NotNull LogEntity entity) throws IOException {
    copyTo(
        activity
            .getContentResolver()
            .openFileDescriptor(uri, "w")
            .getFileDescriptor(),
        new LogDetailsSharable(entity).toSharableContent(activity)
    );
  }

  public static void exportListLog(@NonNull Activity activity, List<LogEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      toastShort(activity, R.string.historian_export_empty_text);
      return;
    }

    Intent shareIntent = SharableUtil.shareAsFile(
        new LogListDetailsSharable(entities),
        activity,
        LIST_EXPORT_FILE_NAME,
        R.string.historian_share_all_logs_title,
        R.string.historian_share_all_logs_subject,
        CLIP_DATA_LABEL
    );
    if (shareIntent != null) {
      activity.startActivity(shareIntent);
    }
  }
  //</editor-fold>
  
  //<editor-fold desc="ask">
  public static void askForConfirmationClear(@NonNull Context context, @NonNull Runnable clearElements) {
    new AlertDialog.Builder(context)
        .setCancelable(false)
        .setTitle(R.string.historian_clear)
        .setMessage(R.string.historian_clear_logs_confirmation)
        .setNegativeButton(R.string.historian_cancel, (r, t) -> {
        })
        .setPositiveButton(R.string.historian_clear, (r, t) -> clearElements.run())
        .create()
        .show();
  }

  public static void askForConfirmationExport(@NonNull Activity activity, List<LogEntity> entities) {
    new AlertDialog.Builder(activity)
        .setCancelable(false)
        .setTitle(R.string.historian_export)
        .setMessage(R.string.historian_export_logs_confirmation)
        .setNegativeButton(R.string.historian_cancel, (r, t) -> {
        })
        .setPositiveButton(R.string.historian_export, (r, t) -> exportListLog(
            activity,
            entities))
        .create()
        .show();
  }
  //</editor-fold>
}
