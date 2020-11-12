package net.yslibrary.historian.internal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.yslibrary.historian.R;
import net.yslibrary.historian.databinding.HistorianFragmentLogListBinding;
import net.yslibrary.historian.databinding.HistorianListItemLogBinding;
import net.yslibrary.historian.internal.data.datebase.LogsDatabase;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.support.RepositoryProvider;
import net.yslibrary.historian.internal.ui.view_models.LogListItemViewModel;
import net.yslibrary.historian.internal.ui.view_models.LogListViewModel;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static net.yslibrary.historian.internal.Util.askForConfirmationClear;
import static net.yslibrary.historian.internal.Util.askForConfirmationExport;
import static net.yslibrary.historian.internal.ui.fragments.LogListFragmentDirections.actionLogListFragmentToLogFragment;

public class LogListFragment extends BaseFragment implements OnQueryTextListener {

  private HistorianFragmentLogListBinding binding;
  private LogListViewModel viewModel;
  private NavController navController;
  private LogsDatabase database;

  private ElementsAdapter listAdapter;

  //<editor-fold desc="lifecycle">
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    database = RepositoryProvider.getDatabase();
    listAdapter = new ElementsAdapter();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.historian_fragment_log_list, container, false);
    setHasOptionsMenu(true);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    navController = Navigation.findNavController(requireView());

    initViewModels();
    initBinding();
    initObservable();
  }

  @Override
  public void onDestroyView() {
    destroyObservable();
    super.onDestroyView();
  }
  //</editor-fold>

  //<editor-fold desc="init & destroy">
  private void initViewModels() {
    viewModel = new ViewModelProvider(requireActivity()).get(LogListViewModel.class);

    viewModel.init(database.logEntityDao());
  }

  private void initBinding() {
    binding.setViewModel(viewModel);
    binding.setLifecycleOwner(this);

    binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.list.setHasFixedSize(true);
    binding.list.setAdapter(listAdapter);
  }

  private void initObservable() {
    viewModel.logs.observe(getViewLifecycleOwner(), elements -> {
      viewModel.showTutorial.postValue(elements.isEmpty());
      listAdapter.setElements(elements);
    });
  }

  private void destroyObservable() {
    viewModel.logs.removeObservers(getViewLifecycleOwner());
  }
  //</editor-fold>

  //<editor-fold desc="menu">
  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.historian_logs_list, menu);

    MenuItem searchMenuItem = menu.findItem(R.id.search);
    SearchView searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setOnQueryTextListener(this);
    searchView.setIconifiedByDefault(true);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.clear) {
      askForConfirmationClear(requireContext(), this::clearElements);
    } else if (itemId == R.id.export) {
      askForConfirmationExport(requireActivity(), viewModel.logs.getValue());
    } else {
      return super.onOptionsItemSelected(item);
    }
    return true;
  }
  //</editor-fold>

  private void openElement(LogEntity element) {
    navController.navigate(actionLogListFragmentToLogFragment(element.getFormatTimestamp())
        .setId(element.getId()));
  }

  public void clearElements() {
    database
        .logEntityDao()
        .clearAll()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete(viewModel::refresh)
        .onErrorComplete(th -> {
          th.printStackTrace();
          return true;
        })
        .subscribe();
  }

  //<editor-fold desc="OnQueryTextListener">
  @Override
  public boolean onQueryTextSubmit(String query) {
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    viewModel.updateItemsFilter(newText);
    return true;
  }
  //</editor-fold>

  private class ElementsAdapter extends RecyclerView.Adapter<ElementsAdapter.ViewHolder> {

    private List<LogEntity> elements = Collections.emptyList();

    public void setElements(List<LogEntity> elements) {
      this.elements = elements;
      notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(DataBindingUtil.inflate(getLayoutInflater(), R.layout.historian_list_item_log, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      LogEntity element = elements.get(position);

      LogListItemViewModel viewModel = new LogListItemViewModel();
      viewModel.element.setValue(element);

      holder.binding.setViewModel(viewModel);
      holder.binding.setLifecycleOwner(LogListFragment.this);

      holder.binding.getRoot().setOnClickListener(c -> openElement(element));
    }

    @Override
    public int getItemCount() {
      return elements.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

      private final HistorianListItemLogBinding binding;

      public ViewHolder(@NonNull HistorianListItemLogBinding itemView) {
        super(itemView.getRoot());
        binding = itemView;
      }
    }
  }
}
