package net.yslibrary.historian.uncaught_handler.internal.ui.view_models;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UncaughtViewModel extends ViewModel {

  public final MutableLiveData<Boolean> restartActivityEnable;
  public final MutableLiveData<Boolean> closeActivityEnable;
  public final MutableLiveData<Boolean> detailsButtonEnable;
  public final MutableLiveData<Integer> imagePath;

  //<editor-fold desc="init & destroy">
  public UncaughtViewModel() {
    restartActivityEnable = new MutableLiveData<>(false);
    closeActivityEnable = new MutableLiveData<>(false);
    detailsButtonEnable = new MutableLiveData<>(false);
    imagePath = new MutableLiveData<>(-1);
  }

  @Override
  protected void onCleared() {

  }
  //</editor-fold>

  //<editor-fold desc="static">
  @BindingAdapter("android:src")
  public static void setImageResource(ImageView view, int resource) {
    if (resource == -1) {
      return;
    }
    view.setImageResource(resource);
  }

  @BindingAdapter("android:text")
  public static void setTextResource(TextView view, int resource) {
    if (resource == -1) {
      return;
    }
    view.setText(resource);
  }

  @BindingAdapter("android:text")
  public static void setButtonResource(Button view, int resource) {
    if (resource == -1) {
      return;
    }
    view.setText(resource);
  }

  @BindingAdapter("android:textColor")
  public static void setTextColorResource(TextView view, int resource) {
    if (resource == -1) {
      return;
    }
    view.setTextColor(resource);
  }

  @BindingAdapter("android:background")
  public static void setFrameLayoutBackgroundResource(FrameLayout view, int resource) {
    if (resource == -1) {
      return;
    }
    view.setBackgroundColor(view.getResources().getColor(resource, null));
  }
  //</editor-fold>
}
