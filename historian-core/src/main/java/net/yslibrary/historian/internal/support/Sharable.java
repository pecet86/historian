package net.yslibrary.historian.internal.support;

import android.content.Context;

import okio.Source;

public interface Sharable {
  Source toSharableContent(Context context);
}
