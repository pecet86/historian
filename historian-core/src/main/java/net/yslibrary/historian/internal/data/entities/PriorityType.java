package net.yslibrary.historian.internal.data.entities;

import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;
import androidx.room.TypeConverter;

/**
 * Class PriorityType
 *
 * @author pecet86 - created
 */
@Keep
public enum PriorityType {
  UNKNOWN, VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT;

  @TypeConverter
  public static PriorityType to(String name) {
    return name == null ? null : PriorityType.valueOf(name);
  }

  @TypeConverter
  public static String from(PriorityType type) {
    return type == null ? null : type.name();
  }

  public static PriorityType getPriority(@LogPriority int priority) {
    switch (priority) {
      case Log.VERBOSE:
        return PriorityType.VERBOSE;
      case Log.DEBUG:
        return PriorityType.DEBUG;
      case Log.INFO:
        return PriorityType.INFO;
      case Log.WARN:
        return PriorityType.WARN;
      case Log.ERROR:
        return PriorityType.ERROR;
      case Log.ASSERT:
        return PriorityType.ASSERT;
      default:
        return PriorityType.UNKNOWN;
    }
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
