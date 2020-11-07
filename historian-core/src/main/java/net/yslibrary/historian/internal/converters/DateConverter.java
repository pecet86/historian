package net.yslibrary.historian.internal.converters;

import java.util.Date;

import androidx.room.TypeConverter;

/**
 * Class DateConverter
 * Converter `Date` to database and back
 *
 * @author pecet86 - created
 */
public class DateConverter {

  /**
   * Converts a database value to an object
   *
   * @param value datebase value
   * @return object value
   */
  @TypeConverter
  public Date fromTimestamp(Long value) {
    return value == null ? null : new Date(value);
  }

  /**
   * Converts value from object to base
   *
   * @param date object value
   * @return datebase value
   */
  @TypeConverter
  public Long dateToTimestamp(Date date) {
    return date == null ? null : date.getTime();
  }
}
