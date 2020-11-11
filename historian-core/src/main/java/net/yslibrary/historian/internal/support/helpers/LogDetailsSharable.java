package net.yslibrary.historian.internal.support.helpers;

import android.content.Context;

import net.yslibrary.historian.R;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.support.Sharable;

import lombok.RequiredArgsConstructor;
import okio.Buffer;
import okio.Source;

import static java.text.MessageFormat.format;

@RequiredArgsConstructor
public class LogDetailsSharable implements Sharable {

  private final LogEntity throwable;

  @Override
  public Source toSharableContent(Context context) {
    Buffer buffer = new Buffer();

    buffer.writeUtf8(format("{0}: {1}\n", context.getString(R.string.historian_share_priority), throwable.getPriority().name()));
    if (throwable.getTag() != null) {
      buffer.writeUtf8(format("{0}: {1}\n", context.getString(R.string.historian_share_tag), throwable.getTag()));
    }
    if (throwable.getClazz() != null) {
      buffer.writeUtf8(format("{0}: {1}\n", context.getString(R.string.historian_share_class), throwable.getClazz()));
    }

    buffer.writeUtf8(format("---------- {0} ----------\n\n", context.getString(R.string.historian_share_message)));
    buffer.writeUtf8(throwable.getMessage());
    buffer.writeUtf8("\n\n");

    if (throwable.getContent() != null) {
      buffer.writeUtf8(format("---------- {0} ----------\n\n", context.getString(R.string.historian_share_content)));
      buffer.writeUtf8(throwable.getContent());
      buffer.writeUtf8("\n\n");
    }

    return buffer;
  }
}
