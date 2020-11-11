package net.yslibrary.historian.internal.ui.fragments;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import net.yslibrary.historian.internal.Util.Duration;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BaseFragment extends Fragment {
  protected String TAG;

  {
    TAG = getClass().getSimpleName();
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
  @SuppressLint("ShowToast")
  protected void toast(String message, @Duration int duration) {
    Single
        .just(Toast.makeText(getActivity(), message, duration))
        .subscribeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(Toast::show)
        .subscribe();
  }

  protected void toastShort(String message) {
    toast(message, Toast.LENGTH_SHORT);
  }

  protected void toastLong(String message) {
    toast(message, Toast.LENGTH_LONG);
  }

  protected void toastShort(@StringRes int id) {
    toast(getString(id), Toast.LENGTH_SHORT);
  }

  protected void toastLong(@StringRes int id) {
    toast(getString(id), Toast.LENGTH_LONG);
  }
  //</editor-fold>

}
