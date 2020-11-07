package net.yslibrary.historian.internal;

import net.yslibrary.historian.Historian;
import net.yslibrary.historian.internal.dao.LogEntityDao;
import net.yslibrary.historian.internal.entities.LogEntity;

import lombok.AllArgsConstructor;

/**
 * Runnable implementation writing logs and executing callbacks
 */

@AllArgsConstructor
public class LogWritingTask implements Runnable {

  private final Historian.Callbacks callbacks;
  private final LogEntityDao dao;
  private final LogEntity log;

  @Override
  public void run() {
    try {
      dao.insertSync(log);

      callbacks.onSuccess();
    } catch (Throwable throwable) {
      callbacks.onFailure(throwable);
    }
  }
}
