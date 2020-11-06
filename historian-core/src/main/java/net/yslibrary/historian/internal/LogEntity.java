package net.yslibrary.historian.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing log
 */

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LogEntity {
  private final String priority;
  private final String tag;
  private final String message;
  private final long timestamp;

  @SuppressWarnings("WeakerAccess")
  public static LogEntity create(int priority, String tag, String message, long timestamp) {
    return new LogEntity(Util.priorityString(priority), tag, message, timestamp);
  }
}
