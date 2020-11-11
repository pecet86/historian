package net.yslibrary.historian.internal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import net.yslibrary.historian.R;
import net.yslibrary.historian.databinding.HistorianActivityMainBinding;
import net.yslibrary.historian.internal.ui.fragments.LogFragmentArgs;
import net.yslibrary.historian.internal.ui.view_models.MainViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends BaseActivity {

  private HistorianActivityMainBinding binding;
  private MainViewModel viewModel;
  private NavController navController;

  //<editor-fold desc="lifecycle">
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initViewModels();
    initBinding();
    configBinding();
    initNavigation();

    consumeIntent(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    consumeIntent(intent);
  }

  @Override
  protected void consumeIntent(Intent intent) {

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
  //</editor-fold>

  //<editor-fold desc="init & destroy">
  private void initViewModels() {
    viewModel = new ViewModelProvider(this).get(MainViewModel.class);
  }

  private void initBinding() {
    binding = DataBindingUtil.setContentView(this, R.layout.historian_activity_main);
    binding.setViewModel(viewModel);
    binding.setLifecycleOwner(this);
  }

  private void configBinding() {
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setSubtitle(getApplicationName());
  }

  private void initNavigation() {
    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
    assert navHostFragment != null;
    navController = navHostFragment.getNavController();

    //navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);

    AppBarConfiguration appBarConfiguration = new AppBarConfiguration
        .Builder(navController.getGraph())
        .build();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
      if (destination.getId() == R.id.logFragment) {
        viewModel.setTitle(LogFragmentArgs.fromBundle(arguments).getTitle());
      } else {
        viewModel.setTitle("");
      }
    });
  }
  //</editor-fold>

  //<editor-fold desc="menu">
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      onBackPressed();
    } else {
      return super.onOptionsItemSelected(item);
    }
    return true;
  }
  //</editor-fold>
}
