package net.yslibrary.historian.internal.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.yslibrary.historian.R;
import net.yslibrary.historian.databinding.HistorianFragmentLogBinding;
import net.yslibrary.historian.internal.data.datebase.LogsDatabase;
import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.support.RepositoryProvider;
import net.yslibrary.historian.internal.support.helpers.LogDetailsSharable;
import net.yslibrary.historian.internal.support.helpers.SharableUtil;
import net.yslibrary.historian.internal.ui.view_models.LogViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static net.yslibrary.historian.internal.Util.copyTo;
import static net.yslibrary.historian.internal.support.helpers.SharableUtil.PLAIN_MIME_TYPE;
import static net.yslibrary.historian.internal.ui.fragments.LogListFragmentDirections.actionGlobalLogListFragment;

public class LogFragment extends BaseFragment {

  private static final String EXPORT_FILE_NAME = "throwable.txt";
  private static final int GET_FILE_FOR_SAVING_REQUEST_CODE = 43;

  private HistorianFragmentLogBinding binding;
  private LogViewModel viewModel;
  private NavController navController;
  private LogsDatabase database;
  private LogFragmentArgs args;

  private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
    @Override
    public void handleOnBackPressed() {
      popBackStack();
    }
  };

  //<editor-fold desc="lifecycle">
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    database = RepositoryProvider.getDatabase();
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.historian_fragment_log, container, false);
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
    initStartMode();
  }

  @Override
  public void onDestroyView() {
    destroyObservable();
    super.onDestroyView();
  }
  //</editor-fold>

  //<editor-fold desc="init & destroy">
  private void initStartMode() {
    args = LogFragmentArgs.fromBundle(requireArguments());

    int throwableId = args.getId();
    if (throwableId == -1) {
      toastLong(R.string.historian_wrong_walue);
      navController.navigate(actionGlobalLogListFragment());
    } else {
      loadElement(throwableId);
    }
  }

  private void initViewModels() {
    viewModel = new ViewModelProvider(requireActivity()).get(LogViewModel.class);
  }

  private void initBinding() {
    binding.setViewModel(viewModel);
    binding.setLifecycleOwner(this);
  }

  private void initObservable() {
    //viewModel.throwables.observe(getViewLifecycleOwner(), listAdapter::setElements);
  }

  private void destroyObservable() {
    //viewModel.throwables.removeObservers(getViewLifecycleOwner());
  }

  private void popBackStack() {
    if (!navController.popBackStack()) {
      navController.navigate(actionGlobalLogListFragment());
    }
  }
  //</editor-fold>

  //<editor-fold desc="menu">
  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.historian_log, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.clear) {
      askForConfirmationClear();
    } else if (itemId == R.id.share_text) {
      askForConfirmationExport(false);
    } else if (itemId == R.id.share_file) {
      askForConfirmationExport(true);
    } else if (itemId == R.id.save_body) {
      createFileToSaveBody();
    } else {
      return super.onOptionsItemSelected(item);
    }
    return true;
  }
  //</editor-fold>

  private void loadElement(Integer id) {
    database
        .logEntityDao()
        .getById(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(viewModel::setElement)
        .doOnComplete(() -> {
          toastLong(getString(R.string.historian_not_found));
          popBackStack();
        })
        .onErrorComplete(th -> {
          log("loadElement", th.getMessage(), th);
          toastLong(getString(R.string.historian_not_found));
          popBackStack();
          return true;
        })
        .subscribe();
  }

  public void deleteElement() {
    database
        .logEntityDao()
        .delete(viewModel.element.getValue())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        //.doOnComplete(NotificationHelper::clearBuffer)
        .doOnComplete(this::popBackStack)
        .onErrorComplete(th -> {
          th.printStackTrace();
          return true;
        })
        .subscribe();
  }

  //<editor-fold desc="functions">
  private void askForConfirmationClear() {
    new AlertDialog
        .Builder(getContext())
        .setCancelable(false)
        .setTitle(R.string.historian_clear)
        .setMessage(R.string.historian_clear_log_confirmation)
        .setNegativeButton(R.string.historian_cancel, (r, t) -> {
        })
        .setPositiveButton(R.string.historian_delete, (r, t) -> deleteElement())
        .create()
        .show();
  }

  private void askForConfirmationExport(boolean file) {
    new AlertDialog.Builder(getContext())
        .setCancelable(false)
        .setTitle(R.string.historian_export)
        .setMessage(R.string.historian_export_log_confirmation)
        .setNegativeButton(R.string.historian_cancel, (r, t) -> {
        })
        .setPositiveButton(R.string.historian_export, (r, t) -> exportThrowables(file))
        .create()
        .show();
  }

  private void exportThrowables(boolean file) {
    LogEntity throwable = viewModel.element.getValue();
    if (throwable == null) {
      toastShort(R.string.historian_export_empty_text);
      return;
    }

    Intent shareIntent;
    if (file) {
      shareIntent = SharableUtil.shareAsFile(
          new LogDetailsSharable(throwable),
          requireActivity(),
          EXPORT_FILE_NAME,
          R.string.historian_share_log_title,
          R.string.historian_share_log_subject,
          "throwables"
      );
    } else {
      String throwableDetailsText = getString(
          R.string.historian_share_log_content,
          throwable.getFormatTimestamp(),
          throwable.getClazzOrEmpty(),
          throwable.getTagOrEmpty(),
          throwable.getMessage(),
          throwable.getContentOrEmpty()
      );
      shareIntent = ShareCompat
          .IntentBuilder
          .from(requireActivity())
          .setType(PLAIN_MIME_TYPE)
          .setChooserTitle(R.string.historian_share_log_title)
          .setSubject(getString(R.string.historian_share_log_subject))
          .setText(throwableDetailsText)
          .createChooserIntent();
    }

    if (shareIntent != null) {
      startActivity(shareIntent);
    }
  }

  private void createFileToSaveBody() {
    Intent intent = new Intent("android.intent.action.CREATE_DOCUMENT")
        .addCategory("android.intent.category.OPENABLE")
        .putExtra("android.intent.extra.TITLE", "historian-export-" + System.currentTimeMillis())
        .setType("*/*");
    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
      startActivityForResult(intent, GET_FILE_FOR_SAVING_REQUEST_CODE);
    } else {
      toastShort(R.string.historian_save_failed_to_open_document);
    }
  }

  private void saveToFile(@NotNull Uri uri, @NotNull LogEntity throwable) throws IOException {
    copyTo(
        requireContext()
            .getContentResolver()
            .openFileDescriptor(uri, "w")
            .getFileDescriptor(),
        new LogDetailsSharable(throwable).toSharableContent(requireContext())
    );
  }
  //</editor-fold>

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
    if (requestCode == GET_FILE_FOR_SAVING_REQUEST_CODE && resultCode == -1) {
      Uri uri = resultData != null ? resultData.getData() : null;
      LogEntity throwable = viewModel.element.getValue();
      if (uri != null && throwable != null) {
        try {
          saveToFile(uri, throwable);
          toastShort(R.string.historian_file_saved);
        } catch (Exception ex) {
          toastShort(R.string.historian_file_not_saved);
        }
      } else {
        toastShort(R.string.historian_file_not_saved);
      }
    }
  }

}
