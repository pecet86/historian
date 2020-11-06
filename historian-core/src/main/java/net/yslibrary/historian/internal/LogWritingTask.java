package net.yslibrary.historian.internal;

import net.yslibrary.historian.Historian;

import lombok.AllArgsConstructor;

/**
 * Runnable implementation writing logs and executing callbacks
 */

@AllArgsConstructor
public class LogWritingTask implements Runnable {

  private final Historian.Callbacks callbacks;
  private final LogWriter logWriter;
  private final LogEntity log;

  @Override
  public void run() {

    try {
      logWriter.log(log);

      callbacks.onSuccess();
    } catch (Throwable throwable) {
      callbacks.onFailure(throwable);
    }
  }
}
