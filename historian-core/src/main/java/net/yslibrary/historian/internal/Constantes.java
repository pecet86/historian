package net.yslibrary.historian.internal;

import android.util.Log;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constantes {

  //Util
  public static final String DB_NAME = "historian.db";
  public static final int LOG_LEVEL = Log.INFO;

  //LogListFragment
  public static final String LIST_EXPORT_FILE_NAME = "logs.txt";
  public static final String CLIP_DATA_LABEL = "logs";

  //LogFragment
  public static final String SINGLE_EXPORT_FILE_NAME = "throwable.txt";
  public static final int GET_FILE_FOR_SAVING_REQUEST_CODE = 43;
  public static final String INTENT_ACTION_CREATE_DOCUMENT = "android.intent.action.CREATE_DOCUMENT";
  public static final String INTENT_CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
  public static final String INTENT_INTENT_EXTRA_TITLE = "android.intent.extra.TITLE";

  //SharableUtil
  public static final String PLAIN_MIME_TYPE = "text/plain";

  //ClearDatabaseService
  /**
   * Unique job ID for this service.
   */
  public static final int CLEAN_DATABASE_JOB_ID = 1000;

  //NotificationHelper
  public final static String CHANNEL_ID = "historian_logs";
  public final static int NOTIFICATION_ID = 1234;
  public final static int INTENT_REQUEST_CODE = 11;

  public static final String PREFS_NAME = "historian_preferences";
  public static final String KEY_LAST_CLEANUP = "last_cleanup";
}
