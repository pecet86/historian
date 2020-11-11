package net.yslibrary.historian.internal.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class FileFactory {
  private static final AtomicLong uniqueIdGenerator = new AtomicLong();

  @Nullable
  public static File create(@NotNull File directory) {
    return create(directory, "historian-" + uniqueIdGenerator.getAndIncrement());
  }

  @Nullable
  public static File create(@NotNull File directory, @NotNull String fileName) {
    File file;
    try {
      file = new File(directory, fileName);
      if (file.exists() && !file.delete()) {
        throw new IOException("Failed to delete file " + file);
      }

      directory.mkdirs();

      if (!file.createNewFile()) {
        throw new IOException("File " + file + " already exists");
      }
    } catch (IOException ex) {
      new IOException("An error occurred while creating a Historian file", ex).printStackTrace();
      file = null;
    }

    return file;
  }
}
