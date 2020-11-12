package net.yslibrary.historian.internal.support;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import net.yslibrary.historian.R;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.support.services.ClearDatabaseService;
import net.yslibrary.historian.internal.ui.activities.BaseActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static net.yslibrary.historian.api.Historian.getLaunchIntent;
import static net.yslibrary.historian.internal.Constantes.CHANNEL_ID;
import static net.yslibrary.historian.internal.Constantes.INTENT_REQUEST_CODE;
import static net.yslibrary.historian.internal.Constantes.NOTIFICATION_ID;

public final class NotificationHelper {

  private final Context context;
  private final NotificationManager notificationManager;
  private final PendingIntent intent;

  public NotificationHelper(Context context) {
    this.context = context;

    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    createOrGetNotificationChannel();

    intent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        getLaunchIntent(context),
        PendingIntent.FLAG_UPDATE_CURRENT
    );
  }

  public void show(LogEntity throwable) {
    if (!BaseActivity.isInForeground) {
      Notification notification = new NotificationCompat
          .Builder(context, CHANNEL_ID)
          .setContentIntent(intent)
          .setLocalOnly(true)
          .setSmallIcon(R.drawable.historian_ic_log_notifications)
          .setColor(ContextCompat.getColor(context, R.color.historian_status_log))
          .setContentTitle(throwable.getClazzOrEmpty())
          .setAutoCancel(true)
          .setContentText(throwable.getMessage())
          .addAction(createClearAction())
          .build();
      notificationManager.notify(NOTIFICATION_ID, notification);
    }
  }

  private NotificationCompat.Action createClearAction() {
    String clearTitle = context.getString(R.string.historian_clear);
    Intent deleteIntent = new Intent(context, ClearDatabaseService.class);

    PendingIntent intent = PendingIntent.getService(
        context,
        INTENT_REQUEST_CODE,
        deleteIntent,
        PendingIntent.FLAG_ONE_SHOT
    );
    return new NotificationCompat.Action(R.drawable.historian_ic_delete_white, clearTitle, intent);
  }

  private void createOrGetNotificationChannel() {
    NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
    if (channel == null) {
      channel = new NotificationChannel(
          CHANNEL_ID,
          context.getString(R.string.historian_throwable_notification_category),
          NotificationManager.IMPORTANCE_LOW
      );
      notificationManager.createNotificationChannel(channel);
    }
  }

  public void dismissNotification() {
    notificationManager.cancel(NOTIFICATION_ID);
  }
}
