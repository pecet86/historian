package net.yslibrary.historian.internal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import lombok.experimental.UtilityClass;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * Class Util
 *
 * @author yshrsmz
 * @author Paweł Cal
 */
@UtilityClass
public class Util {

  public static final String DB_NAME = "historian.db";
  public static final int LOG_LEVEL = Log.INFO;

  public static File getFilesDir(@NonNull Context context) {
    return new File(context.getApplicationContext().getFilesDir().getParent(), "files");
  }

  public static File getDatabasesDir(Context context) {
    return new File(context.getApplicationContext().getFilesDir().getParent(), "databases");
  }

  public static String getStackTraceString(Throwable t) {
    // Don't replace this with Log.getStackTraceString() - it hides
    // UnknownHostException, which is not what we want.
    StringWriter sw = new StringWriter(256);
    PrintWriter pw = new PrintWriter(sw, false);
    t.printStackTrace(pw);
    pw.flush();
    return sw.toString();
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

  @IntDef(value = {
      Log.VERBOSE,
      Log.DEBUG,
      Log.INFO,
      Log.WARN,
      Log.ERROR,
      Log.ASSERT
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface LogPriority {
  }
}
