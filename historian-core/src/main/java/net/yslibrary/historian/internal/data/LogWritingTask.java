package net.yslibrary.historian.internal.data;

import net.yslibrary.historian.api.Historian.Callbacks;
import net.yslibrary.historian.internal.data.dao.LogsDao;
import net.yslibrary.historian.internal.data.entities.LogEntity;

import lombok.AllArgsConstructor;

/**
 * Class ThrowableWritingTask
 * Runnable implementation writing logs and executing callbacks
 *
 * @author yshrsmz - created
 * @author pecet86 - modification
 */
@AllArgsConstructor
public class LogWritingTask implements Runnable {

  private final Callbacks callbacks;
  private final LogsDao dao;
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
