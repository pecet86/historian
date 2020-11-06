package net.yslibrary.historian.internal;

import android.util.Log;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

  public static String priorityString(int priority) {
    switch (priority) {
      case Log.VERBOSE:
        return "VERBOSE";
      case Log.DEBUG:
        return "DEBUG";
      case Log.INFO:
        return "INFO";
      case Log.WARN:
        return "WARN";
      case Log.ERROR:
        return "ERROR";
      case Log.ASSERT:
        return "ASSERT";
      default:
        return "UNKNOWN";
    }
  }
}
