package net.yslibrary.historian.internal.support;

import android.content.Context;

import net.yslibrary.historian.internal.data.datebase.LogsDatabase;

import java.io.IOException;

import lombok.Getter;

import static net.yslibrary.historian.internal.Constantes.DB_NAME;

public class RepositoryProvider {

  @Getter
  private static LogsDatabase database;

  public static synchronized void close() throws IOException {
    database.close();
    database = null;
  }

  public static synchronized void initialize(Context context) {
    if (database == null || !database.isOpen()) {
      database = LogsDatabase.getInstance(context, DB_NAME);
    }
  }
}
