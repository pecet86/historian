package net.yslibrary.historian.internal.support.helpers;

import android.content.Context;

import net.yslibrary.historian.R;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.support.Sharable;

import java.util.List;

import androidx.annotation.NonNull;
import lombok.RequiredArgsConstructor;
import okio.Buffer;
import okio.Source;

@RequiredArgsConstructor
public class LogListDetailsSharable implements Sharable {

  private final List<LogEntity> throwables;

  @Override
  public Source toSharableContent(@NonNull Context context) {
    Buffer buffer = new Buffer();

    String export_prefix = context.getString(R.string.historian_export_prefix);
    String export_postfix = context.getString(R.string.historian_export_postfix);
    String export_separator = context.getString(R.string.historian_export_separator);

    buffer.writeUtf8(export_prefix + "\n");
    throwables
        .stream()
        .map(LogDetailsSharable::new)
        .map(sharable -> sharable.toSharableContent(context) + "\n")
        .forEach(content -> buffer
            .writeUtf8(content)
            .writeUtf8("\n" + export_separator + "\n")
        );
    buffer.writeUtf8("\n" + export_postfix + "\n");

    return buffer;
  }
}
