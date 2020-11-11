package net.yslibrary.historian.internal.ui.view_models;

import net.yslibrary.historian.internal.data.entities.LogEntity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogListItemViewModel extends ViewModel {

  public final MutableLiveData<LogEntity> element;

  //<editor-fold desc="init & destroy">
  public LogListItemViewModel() {
    element = new MutableLiveData<>();
  }

  @Override
  protected void onCleared() {

  }
  //</editor-fold>

}
