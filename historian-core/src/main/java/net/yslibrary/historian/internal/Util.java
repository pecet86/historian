package net.yslibrary.historian.internal;

import android.content.Context;
import android.util.Log;

import java.io.File;

import androidx.annotation.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

  public static final String DB_NAME = "logs.db";
  public static final int LOG_LEVEL = Log.INFO;

  public static File getFilesDir(@NonNull Context context) {
    return new File(context.getApplicationContext().getFilesDir().getParent(), "files");
  }

  public static File getDatabasesDir(Context context) {
    return new File(context.getApplicationContext().getFilesDir().getParent(), "databases");
  }
}
