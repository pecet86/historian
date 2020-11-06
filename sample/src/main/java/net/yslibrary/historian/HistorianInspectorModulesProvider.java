package net.yslibrary.historian;

import android.content.Context;

import com.facebook.stetho.InspectorModulesProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.database.SqliteDatabaseDriver;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yshrsmz on 2017/01/21.
 * Modification by pecet86 on 2020/11/06.
 */
public class HistorianInspectorModulesProvider implements InspectorModulesProvider {

  private final Context context;
  private final Historian historian;

  public HistorianInspectorModulesProvider(Context context, Historian historian) {
    this.context = context;
    this.historian = historian;
  }

  @Override
  public Iterable<ChromeDevtoolsDomain> get() {
    return new Stetho
        .DefaultInspectorModulesBuilder(context)
        .provideDatabaseDriver(new SqliteDatabaseDriver(context,
            () -> {
              List<File> list = new ArrayList<>();
              list.add(new File(historian.dbPath()));
              return list;
            },
            file -> historian.getDatabase())
        )
        .finish();
  }
}
