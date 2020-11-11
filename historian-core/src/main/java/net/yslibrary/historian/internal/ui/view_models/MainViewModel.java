package net.yslibrary.historian.internal.ui.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

  public final MutableLiveData<String> title;

  //<editor-fold desc="init & destroy">
  public MainViewModel() {
    title = new MutableLiveData<>("");
  }

  @Override
  protected void onCleared() {

  }
  //</editor-fold>

  public void setTitle(@NonNull String title) {
    this.title.postValue(title);
  }
}
