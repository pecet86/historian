package net.yslibrary.historian.sample;

import android.os.Bundle;
import android.widget.Button;

import net.yslibrary.historian.api.Historian;

import java.util.concurrent.atomic.AtomicLong;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Completable;
import timber.log.Timber;

/**
 * Created by yshrsmz on 2017/01/21.
 * Modification by pecet86 on 2020/11/06.
 */
public class MainActivity extends AppCompatActivity {

  AtomicLong counter = new AtomicLong();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button launchDirectly = findViewById(R.id.launch_directly);
    launchDirectly.setOnClickListener(view -> startActivity(Historian.getLaunchIntent(getApplicationContext())));

    Button testSimple = findViewById(R.id.test_simple);
    testSimple.setOnClickListener(view -> {
      for (int i = 0, l = 100; i < 100; i++) {
        Timber.i("test: %d", counter.getAndIncrement());
      }

      Timber.tag("XXX").e("message %s", "s");
    });

    Button testException = findViewById(R.id.test_exception);
    testException.setOnClickListener(view -> {
      Timber.tag("XXX").e(new RuntimeException("X"));
      Timber.tag("XXX").e(new RuntimeException("X"), "message %s", "s");
    });

    Button testGloablException = findViewById(R.id.test_gloabl_exception);
    testGloablException.setOnClickListener(view -> {
      throw new RuntimeException("X");
    });

    Button testRxjavaException = findViewById(R.id.test_rxjava_exception);
    testRxjavaException.setOnClickListener(view -> {
      Completable.error(new RuntimeException("X")).subscribe();
    });
  }

}
