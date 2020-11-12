package net.yslibrary.historian.internal.data.entities;

import net.yslibrary.historian.internal.data.converters.DateConverter;
import net.yslibrary.historian.internal.data.dao.LogsDao;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static android.util.Log.getStackTraceString;

/**
 * Entity class representing log
 *
 * @author yshrsmz - created
 * @author pecet86 - modification
 */
@Setter
@Getter
@NoArgsConstructor
@Entity(tableName = LogsDao.TABLE)
@Parcel
public class LogEntity {

  //<editor-fold desc="fields">
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  private Integer id;

  @ColumnInfo(name = "priority")
  @TypeConverters(PriorityType.class)
  @NonNull
  private PriorityType priority;

  @ColumnInfo(name = "tag")
  private String tag;

  @ColumnInfo(name = "message")
  @NonNull
  private String message;

  @ColumnInfo(name = "clazz")
  private String clazz;

  @ColumnInfo(name = "content")
  private String content;

  @ColumnInfo(name = "timestamp")
  @TypeConverters(DateConverter.class)
  @NonNull
  private Date timestamp = new Date();
  //</editor-fold>

  @Ignore
  public LogEntity(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable throwable) {
    this.priority = PriorityType.getPriority(priority);
    this.tag = tag;
    this.message = message;
    if (throwable != null) {
      clazz = throwable.getClass().getName();
      content = getStackTraceString(throwable);
    }
  }

  //<editor-fold desc="functions">
  public String getTagOrEmpty() {
    return tag == null ? "" : tag;
  }

  public String getClazzOrEmpty() {
    return clazz == null ? "" : clazz;
  }

  public String getContentOrEmpty() {
    return content == null ? "" : content;
  }

  public String getFormatTimestamp() {
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(timestamp);
  }
  //</editor-fold>
}
