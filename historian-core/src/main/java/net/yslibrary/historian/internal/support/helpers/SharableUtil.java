package net.yslibrary.historian.internal.support.helpers;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import net.yslibrary.historian.BuildConfig;
import net.yslibrary.historian.R;
import net.yslibrary.historian.internal.support.FileFactory;
import net.yslibrary.historian.internal.support.Sharable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import androidx.annotation.StringRes;
import androidx.annotation.WorkerThread;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import lombok.experimental.UtilityClass;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import static net.yslibrary.historian.internal.Constantes.PLAIN_MIME_TYPE;

@UtilityClass
public class SharableUtil {

  private static final String TAG = SharableUtil.class.getSimpleName();


  @WorkerThread
  @Nullable
  public static String toSharableUtf8Content(@NotNull Sharable content, @NotNull Context context) {
    try (BufferedSource source = Okio.buffer(content.toSharableContent(context))) {
      return source.readUtf8();
    } catch (Exception ex) {
      new IOException("An error occurred while creating a Historian file", ex).printStackTrace();
      return null;
    }
  }

  @WorkerThread
  public static Intent shareAsUtf8Text(@NotNull Sharable content, @NotNull Activity activity,
                                       @NotNull String intentTitle, @NotNull String intentSubject) {
    return ShareCompat
        .IntentBuilder
        .from(activity)
        .setType(PLAIN_MIME_TYPE)
        .setChooserTitle(intentTitle)
        .setSubject(intentSubject)
        .setText(toSharableUtf8Content(content, activity))
        .createChooserIntent();
  }

  @WorkerThread
  @Nullable
  public static Intent shareAsFile(@NotNull Sharable content, @NotNull Activity activity,
                                   @NotNull String fileName, @NotNull String intentTitle,
                                   @NotNull String intentSubject, @NotNull String clipDataLabel) {
    File cache = activity.getCacheDir();
    if (cache == null || !cache.exists()) {
      Log.w(TAG, "Failed to obtain a valid cache directory for Historian file export");
      Toast.makeText(activity, R.string.historian_export_no_file, Toast.LENGTH_SHORT).show();
      return null;
    }

    File file = FileFactory.create(cache, fileName);
    if (file == null) {
      Log.w(TAG, "Failed to create an export file for Historian");
      Toast.makeText(activity, R.string.historian_export_no_file, Toast.LENGTH_SHORT).show();
      return null;
    }

    Source fileContent = content.toSharableContent(activity);

    try (BufferedSink source = Okio.buffer(Okio.sink(file))) {
      source.writeAll(fileContent);
      source.flush();
    } catch (Exception ex) {
      new IOException("An error occurred while creating a Historian file", ex).printStackTrace();
      return null;
    }

    Uri uri = FileProvider.getUriForFile(
        activity,
        BuildConfig.FILES_AUTHORITY,
        file
    );
    Intent shareIntent = ShareCompat.IntentBuilder.from(activity)
        .setType(activity.getContentResolver().getType(uri))
        .setChooserTitle(intentTitle)
        .setSubject(intentSubject)
        .setStream(uri)
        .getIntent();

    shareIntent.setClipData(ClipData.newRawUri(clipDataLabel, uri));
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    return Intent.createChooser(shareIntent, intentTitle);
  }

  @WorkerThread
  @Nullable
  public static Intent shareAsFile(@NotNull Sharable content, @NotNull Activity activity,
                                   @NotNull String fileName, @StringRes int intentTitle,
                                   @StringRes int intentSubject, @NotNull String clipDataLabel) {
    return shareAsFile(
        content,
        activity,
        fileName,
        activity.getString(intentTitle),
        activity.getString(intentSubject),
        clipDataLabel
    );
  }
}
