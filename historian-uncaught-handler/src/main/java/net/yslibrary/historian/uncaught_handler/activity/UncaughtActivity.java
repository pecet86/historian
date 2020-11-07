package net.yslibrary.historian.uncaught_handler.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.yslibrary.historian.uncaught_handler.CrashConfig;
import net.yslibrary.historian.uncaught_handler.R;

import androidx.appcompat.app.AppCompatActivity;

import static net.yslibrary.historian.uncaught_handler.Util.closeApplication;
import static net.yslibrary.historian.uncaught_handler.Util.restartApplication;

/**
 * Class HistorianUncaughtExceptionHandler
 *
 * @author Selim - created
 * @author pecet86 - modification
 */
public class UncaughtActivity extends AppCompatActivity {
  private final static String TAG = UncaughtActivity.class.getSimpleName();

  private Button detailsBtn;
  private Button restartBtn;
  private Button closeBtn;
  private TextView textViewCrash;
  private ImageView image;
  private FrameLayout back;

  private CrashConfig crashConfig;

  private void init() {
    textViewCrash = findViewById(R.id.textViewC);
    detailsBtn = findViewById(R.id.btnDetails);
    restartBtn = findViewById(R.id.btnRestart);
    closeBtn = findViewById(R.id.btnClose);
    image = findViewById(R.id.image);
    back = findViewById(R.id.back);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_uncaught);
    init();

    Intent i = getIntent();
    Bundle extraExp = getIntent().getExtras();
    crashConfig = (CrashConfig) i.getSerializableExtra("configuration");

    textViewCrash.setText(crashConfig.getCrashText());
    textViewCrash.setTextColor(crashConfig.getCrashTextColor());
    detailsBtn.setText(crashConfig.getDetailsButtonText());
    restartBtn.setText(crashConfig.getRestartAppButtonText());
    restartBtn.setVisibility(crashConfig.isRestartActivityEnable() ? View.VISIBLE : View.GONE);
    closeBtn.setText(crashConfig.getCloseAppButtonText());
    closeBtn.setVisibility(crashConfig.isCloseActivityEnable() ? View.VISIBLE : View.GONE);
    image.setImageResource(crashConfig.getImagePath());
    back.setBackgroundColor(crashConfig.getBackgorundColor());

    if (extraExp != null) {
      detailsBtn.setOnClickListener(view -> {
        String message = extraExp.getString("stacktrace");
        new AlertDialog.Builder(UncaughtActivity.this)
            .setTitle(crashConfig.getDetailsDialogTitle())
            .setMessage(message)
            .setPositiveButton(crashConfig.getDetailsDialogButtonText(), null)
            .show();
      });
    } else {
      detailsBtn.setVisibility(View.GONE);
    }
    closeBtn.setOnClickListener(view -> closeApplication(this, crashConfig));
    restartBtn.setOnClickListener(view -> restartApplication(this, crashConfig));
  }

  @Override
  public void onBackPressed() {
    System.exit(0);
  }

}
