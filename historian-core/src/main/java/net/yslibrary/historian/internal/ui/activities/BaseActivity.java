package net.yslibrary.historian.internal.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.yslibrary.historian.internal.Util;
import net.yslibrary.historian.internal.Util.Duration;
import net.yslibrary.historian.internal.support.RepositoryProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

  protected String TAG;
  public static Boolean isInForeground = false;

  {
    TAG = getClass().getSimpleName();
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RepositoryProvider.initialize(getApplicationContext());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    RepositoryProvider.initialize(getApplicationContext());
  }

  protected void consumeIntent(Intent intent) {
  }

  @Override
  protected void onResume() {
    super.onResume();
    isInForeground = true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    isInForeground = true;
  }

  protected CharSequence getApplicationName() {
    return getApplicationInfo().loadLabel(getPackageManager());
  }

  //<editor-fold desc="logs">
  protected static void log(String TAG, String path, String value) {
    log(TAG, path, value, Log.DEBUG);
  }

  protected static void log(String TAG, String path, String value, int priority) {
    Log.println(priority, String.format("%s.%s", TAG, path), value);
  }

  protected static void log(String TAG, String path, String value, Throwable th) {
    Log.e(String.format("%s.%s", TAG, path), value, th);
  }

  protected void log(String path, String value) {
    log(TAG, path, value, Log.DEBUG);
  }

  protected void log(String path, String value, int priority) {
    log(TAG, path, value, priority);
  }

  protected void log(String path, String value, Throwable th) {
    log(TAG, path, value, th);
  }
  //</editor-fold>

  //<editor-fold desc="toast">
  @SuppressLint({"ShowToast", "AutoDispose"})
  public void toast(@NonNull String message, @Duration int duration) {
    Util.toast(this, message, duration);
  }

  public void toastShort(@NonNull String message) {
    toast(message, Toast.LENGTH_SHORT);
  }

  public void toastLong(@NonNull String message) {
    toast(message, Toast.LENGTH_LONG);
  }

  public void toastShort(@StringRes int id) {
    toast(getString(id), Toast.LENGTH_SHORT);
  }

  public void toastLong(@StringRes int id) {
    toast(getString(id), Toast.LENGTH_LONG);
  }
  //</editor-fold>

}
