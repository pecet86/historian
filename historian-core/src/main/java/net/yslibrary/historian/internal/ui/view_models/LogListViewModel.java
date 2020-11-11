package net.yslibrary.historian.internal.ui.view_models;

import net.yslibrary.historian.internal.data.dao.LogsDao;
import net.yslibrary.historian.internal.data.entities.LogEntity;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static androidx.lifecycle.Transformations.switchMap;

public class LogListViewModel extends ViewModel {

  private static final LiveData<List<LogEntity>> EMPTY = new MutableLiveData<>(Collections.emptyList());

  public final MutableLiveData<String> currentFilter;
  public final MutableLiveData<Boolean> showTutorial;
  public final LiveData<List<LogEntity>> logs;
  private LogsDao dao;

  //<editor-fold desc="init & destroy">
  public LogListViewModel() {
    currentFilter = new MutableLiveData<>("");
    showTutorial = new MutableLiveData<>(true);
    logs = switchMap(currentFilter, this::getLogs);
  }

  @UiThread
  public void init(LogsDao dao) {
    this.dao = dao;
    currentFilter.setValue("");
  }

  @Override
  protected void onCleared() {
    dao = null;
    currentFilter.setValue("");
  }
  //</editor-fold>

  public final void updateItemsFilter(@NonNull String searchQuery) {
    currentFilter.setValue(searchQuery);
  }

  public final void refresh() {
    currentFilter.setValue(currentFilter.getValue());
  }

  //<editor-fold desc="functions">
  private boolean isDao() {
    return dao != null;
  }

  private boolean isEmptyText(String input) {
    return input == null || input.trim().isEmpty();
  }

  private LiveData<List<LogEntity>> getFiltred(String input) {
    return isEmptyText(input) ? dao.getSortedAll() : dao.getFilteredAll(input);
  }

  private LiveData<List<LogEntity>> getLogs(String input) {
    return isDao() ? getFiltred(input) : EMPTY;
  }
  //</editor-fold>
}
