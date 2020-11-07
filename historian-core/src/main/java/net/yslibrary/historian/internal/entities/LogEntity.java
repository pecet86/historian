package net.yslibrary.historian.internal.entities;

import net.yslibrary.historian.internal.converters.DateConverter;
import net.yslibrary.historian.internal.dao.LogEntityDao;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing log
 */

@Setter
@Getter
@NoArgsConstructor
@Entity(tableName = LogEntityDao.TABLE)
public class LogEntity {

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

  @ColumnInfo(name = "timestamp")
  @TypeConverters(DateConverter.class)
  @NonNull
  private Date timestamp = new Date();

  @Ignore
  public LogEntity(int priority, String tag, @NonNull String message) {
    this.priority = PriorityType.getPriority(priority);
    this.tag = tag;
    this.message = message;
  }

}
