package net.yslibrary.historian.uncaught_handler.internal.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import net.yslibrary.historian.internal.data.entities.LogEntity;
import net.yslibrary.historian.internal.ui.activities.BaseActivity;
import net.yslibrary.historian.uncaught_handler.R;
import net.yslibrary.historian.uncaught_handler.api.CrashConfig;
import net.yslibrary.historian.uncaught_handler.databinding.HistorianActivityUncaughtBinding;
import net.yslibrary.historian.uncaught_handler.internal.ui.view_models.UncaughtViewModel;

import org.parceler.Parcels;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import static net.yslibrary.historian.api.Historian.getClearLaunchIntent;
import static net.yslibrary.historian.internal.Constantes.GET_FILE_FOR_SAVING_REQUEST_CODE;
import static net.yslibrary.historian.internal.Util.createFileToSaveBody;
import static net.yslibrary.historian.internal.Util.exportSingleLog;
import static net.yslibrary.historian.internal.Util.saveToFile;
import static net.yslibrary.historian.uncaught_handler.internal.Constantes.CONFIGURATION_KEY;
import static net.yslibrary.historian.uncaught_handler.internal.Constantes.STACKTRACE_KEY;
import static net.yslibrary.historian.uncaught_handler.internal.Util.closeApplication;
import static net.yslibrary.historian.uncaught_handler.internal.Util.restartApplication;

/**
 * Class HistorianUncaughtExceptionHandler
 *
 * @author Selim - created
 * @author pecet86 - modification
 */
public class UncaughtActivity extends BaseActivity {

  private final static String TAG = UncaughtActivity.class.getSimpleName();

  private HistorianActivityUncaughtBinding binding;
  private UncaughtViewModel viewModel;
  private CrashConfig crashConfig;
  private LogEntity stacktrace;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    crashConfig = (CrashConfig) getIntent().getSerializableExtra(CONFIGURATION_KEY);
    stacktrace = Parcels.unwrap(getIntent().getParcelableExtra(STACKTRACE_KEY));

    initViewModels();
    initBinding();
  }

  @Override
  public void onBackPressed() {
    System.exit(0);
  }

  //<editor-fold desc="init & destroy">
  private void initViewModels() {
    viewModel = new ViewModelProvider(this).get(UncaughtViewModel.class);

    viewModel.restartActivityEnable.setValue(crashConfig.isRestartActivityEnable());
    viewModel.closeActivityEnable.setValue(crashConfig.isCloseActivityEnable());
    viewModel.detailsButtonEnable.setValue(stacktrace != null);

    viewModel.imagePath.setValue(crashConfig.getImagePath());
  }

  private void initBinding() {
    binding = DataBindingUtil.setContentView(this, R.layout.historian_activity_uncaught);
    binding.setViewModel(viewModel);
    binding.setLifecycleOwner(this);

    binding.btnDetails.setOnClickListener(view -> new AlertDialog
        .Builder(UncaughtActivity.this)
        .setTitle(stacktrace.getMessage())
        .setMessage(stacktrace.getContentOrEmpty())
        .setCancelable(true)
        .setNegativeButton(R.string.historian_details_dialog_button_close, null)
        .setNeutralButton(R.string.historian_details_dialog_button_save, (dialog, which) -> {
          createFileToSaveBody(this);
        })
        .setPositiveButton(R.string.historian_details_dialog_button_export, (dialog, which) -> exportSingleLog(
            this,
            stacktrace,
            true))
        .show());
    binding.btnClose.setOnClickListener(view -> closeApplication(this, crashConfig));
    binding.btnRestart.setOnClickListener(view -> restartApplication(this, crashConfig));
    binding.btnHistorian.setOnClickListener(view -> startActivity(getClearLaunchIntent(getApplicationContext())));
  }
  //</editor-fold>

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
    if (requestCode == GET_FILE_FOR_SAVING_REQUEST_CODE && resultCode == -1) {
      Uri uri = resultData != null ? resultData.getData() : null;
      if (uri != null && stacktrace != null) {
        try {
          saveToFile(this, uri, stacktrace);
          toastShort(net.yslibrary.historian.R.string.historian_file_saved);
        } catch (Exception ex) {
          toastShort(net.yslibrary.historian.R.string.historian_file_not_saved);
        }
      } else {
        toastShort(net.yslibrary.historian.R.string.historian_file_not_saved);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, resultData);
    }
  }
}
