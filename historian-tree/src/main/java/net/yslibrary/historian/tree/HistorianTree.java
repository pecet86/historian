package net.yslibrary.historian.tree;

import android.util.Log;

import net.yslibrary.historian.api.Historian;
import net.yslibrary.historian.internal.data.entities.PriorityType.LogPriority;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import timber.log.Timber;

/**
 * Class HistorianTree
 *
 * @author yshrsmz - created
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HistorianTree extends Timber.Tree {

  private final Historian historian;

  public static HistorianTree with(Historian historian) {
    return new HistorianTree(historian);
  }

  //<editor-fold desc="functions">

  /**
   * Log a verbose message with optional format args.
   */
  @Override
  public void v(String message, Object... args) {
    prepareLog(Log.VERBOSE, null, message, args);
  }

  /**
   * Log a verbose exception and a message with optional format args.
   */
  @Override
  public void v(Throwable t, String message, Object... args) {
    prepareLog(Log.VERBOSE, t, message, args);
  }

  /**
   * Log a verbose exception.
   */
  @Override
  public void v(Throwable t) {
    prepareLog(Log.VERBOSE, t, null);
  }

  /**
   * Log a debug message with optional format args.
   */
  @Override
  public void d(String message, Object... args) {
    prepareLog(Log.DEBUG, null, message, args);
  }

  /**
   * Log a debug exception and a message with optional format args.
   */
  @Override
  public void d(Throwable t, String message, Object... args) {
    prepareLog(Log.DEBUG, t, message, args);
  }

  /**
   * Log a debug exception.
   */
  @Override
  public void d(Throwable t) {
    prepareLog(Log.DEBUG, t, null);
  }

  /**
   * Log an info message with optional format args.
   */
  @Override
  public void i(String message, Object... args) {
    prepareLog(Log.INFO, null, message, args);
  }

  /**
   * Log an info exception and a message with optional format args.
   */
  @Override
  public void i(Throwable t, String message, Object... args) {
    prepareLog(Log.INFO, t, message, args);
  }

  /**
   * Log an info exception.
   */
  @Override
  public void i(Throwable t) {
    prepareLog(Log.INFO, t, null);
  }

  /**
   * Log a warning message with optional format args.
   */
  @Override
  public void w(String message, Object... args) {
    prepareLog(Log.WARN, null, message, args);
  }

  /**
   * Log a warning exception and a message with optional format args.
   */
  @Override
  public void w(Throwable t, String message, Object... args) {
    prepareLog(Log.WARN, t, message, args);
  }

  /**
   * Log a warning exception.
   */
  @Override
  public void w(Throwable t) {
    prepareLog(Log.WARN, t, null);
  }

  /**
   * Log an error message with optional format args.
   */
  @Override
  public void e(String message, Object... args) {
    prepareLog(Log.ERROR, null, message, args);
  }

  /**
   * Log an error exception and a message with optional format args.
   */
  @Override
  public void e(Throwable t, String message, Object... args) {
    prepareLog(Log.ERROR, t, message, args);
  }

  /**
   * Log an error exception.
   */
  @Override
  public void e(Throwable t) {
    prepareLog(Log.ERROR, t, null);
  }

  /**
   * Log an assert message with optional format args.
   */
  @Override
  public void wtf(String message, Object... args) {
    prepareLog(Log.ASSERT, null, message, args);
  }

  /**
   * Log an assert exception and a message with optional format args.
   */
  @Override
  public void wtf(Throwable t, String message, Object... args) {
    prepareLog(Log.ASSERT, t, message, args);
  }

  /**
   * Log an assert exception.
   */
  @Override
  public void wtf(Throwable t) {
    prepareLog(Log.ASSERT, t, null);
  }

  /**
   * Log at {@code priority} a message with optional format args.
   */
  @Override
  public void log(@LogPriority int priority, String message, Object... args) {
    prepareLog(priority, null, message, args);
  }

  /**
   * Log at {@code priority} an exception and a message with optional format args.
   */
  @Override
  public void log(@LogPriority int priority, Throwable t, String message, Object... args) {
    prepareLog(priority, t, message, args);
  }

  /**
   * Log at {@code priority} an exception.
   */
  @Override
  public void log(@LogPriority int priority, Throwable t) {
    prepareLog(priority, t, null);
  }
  //</editor-fold>

  private Method getMethodFromTree(String name) {
    try {
      Method method = Timber.Tree.class.getDeclaredMethod(name);
      method.setAccessible(true);
      return method;
    } catch (Exception e) {
      return null;
    }
  }

  private String getTagValue() {
    try {
      Method getTag = getMethodFromTree("getTag"); //< 5.0.0
      if (getTag == null) {
        getTag = getMethodFromTree("getTag$timber_release"); // >= 5.0.0
      }

      if (getTag == null) {
        System.err.println("`getTag` or `getTag$timber_release` not exist");
        return null;
      }
      return (String) getTag.invoke(this);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private void prepareLog(@LogPriority int priority, Throwable t, String message, Object... args) {
    // Consume tag even when message is not loggable so that next message is correctly tagged.
    String tag = getTagValue();

    if (!isLoggable(tag, priority)) {
      return;
    }
    if (message != null && message.length() == 0) {
      message = null;
    }
    if (message == null) {
      if (t == null) {
        return; // Swallow message if it's null and there's no throwable.
      }
      message = "";
    } else {
      if (args != null && args.length > 0) {
        message = formatMessage(message, args);
      }
    }

    log(priority, tag, message, t);
  }

  @Override
  protected void log(@LogPriority int priority, String tag, @NonNull String message, Throwable throwable) {
    historian.log(priority, tag, message, throwable);
  }
}
