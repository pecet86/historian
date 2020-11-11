package net.yslibrary.historian.uncaught_handler.internal.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;

import net.yslibrary.historian.uncaught_handler.R;
import net.yslibrary.historian.uncaught_handler.api.CrashConfig;
import net.yslibrary.historian.uncaught_handler.databinding.HistorianActivityUncaughtBinding;
import net.yslibrary.historian.uncaught_handler.internal.ui.view_models.UncaughtViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import static net.yslibrary.historian.api.Historian.getClearLaunchIntent;
import static net.yslibrary.historian.uncaught_handler.internal.Util.closeApplication;
import static net.yslibrary.historian.uncaught_handler.internal.Util.restartApplication;

/**
 * Class HistorianUncaughtExceptionHandler
 *
 * @author Selim - created
 * @author pecet86 - modification
 */
public class UncaughtActivity extends AppCompatActivity {

  private final static String TAG = UncaughtActivity.class.getSimpleName();
  public final static String CONFIGURATION_KEY = "configuration";
  public final static String STACKTRACE_KEY = "stacktrace";


  private HistorianActivityUncaughtBinding binding;
  private UncaughtViewModel viewModel;
  private CrashConfig crashConfig;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    crashConfig = (CrashConfig) getIntent().getSerializableExtra(CONFIGURATION_KEY);
    String stacktrace = getIntent().getStringExtra(STACKTRACE_KEY);

    initViewModels(stacktrace != null);
    initBinding(stacktrace);
  }

  @Override
  public void onBackPressed() {
    System.exit(0);
  }

  //<editor-fold desc="init & destroy">
  private void initViewModels(boolean detalis) {
    viewModel = new ViewModelProvider(this).get(UncaughtViewModel.class);

    viewModel.restartActivityEnable.setValue(crashConfig.isRestartActivityEnable());
    viewModel.restartAppButtonText.setValue(crashConfig.getRestartAppButtonText());

    viewModel.closeActivityEnable.setValue(crashConfig.isCloseActivityEnable());
    viewModel.closeAppButtonText.setValue(crashConfig.getCloseAppButtonText());

    viewModel.crashText.setValue(crashConfig.getCrashText());
    viewModel.crashTextColor.setValue(crashConfig.getCrashTextColor());

    viewModel.detailsButtonEnable.setValue(detalis);
    viewModel.detailsButtonText.setValue(crashConfig.getDetailsButtonText());

    viewModel.imagePath.setValue(crashConfig.getImagePath());
    viewModel.backgorundColor.setValue(crashConfig.getBackgorundColor());
  }

  private void initBinding(String message) {
    binding = DataBindingUtil.setContentView(this, R.layout.historian_activity_uncaught);
    binding.setViewModel(viewModel);
    binding.setLifecycleOwner(this);

    binding.btnDetails.setOnClickListener(view -> new AlertDialog
        .Builder(UncaughtActivity.this)
        .setTitle(crashConfig.getDetailsDialogTitle())
        .setMessage(message)
        .setPositiveButton(crashConfig.getDetailsDialogButtonText(), null)
        .show());
    binding.btnClose.setOnClickListener(view -> closeApplication(this, crashConfig));
    binding.btnRestart.setOnClickListener(view -> restartApplication(this, crashConfig));
    binding.btnHistorian.setOnClickListener(view -> startActivity(getClearLaunchIntent(getApplicationContext())));
  }
  //</editor-fold>

}
