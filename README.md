Historian
===

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Historian-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5329)
[![codecov](https://codecov.io/gh/yshrsmz/historian/branch/master/graph/badge.svg)](https://codecov.io/gh/yshrsmz/historian)
[![](https://jitpack.io/v/pecet86/historian.svg)](https://jitpack.io/#pecet86/historian)
![License](https://img.shields.io/github/license/pecet86/historian.svg)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-orange.svg)](http://makeapullrequest.com)

Historian is a custom [Timber](https://github.com/JakeWharton/timber).Tree implementation that saves logs to SQLite, so that you can see/download the SQLite file later for debugging.

This library is primarily made to help debugging crash in consumers' devices.

## Installation

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
  def historian_version = '<version>'
  implementation "com.github.pecet86.historian:historian-core:$historian_version"
  implementation "com.github.pecet86.historian:historian-tree:$historian_version" //connect to timber
  implementation "com.github.pecet86.historian:historian-uncaught-handler:$historian_version" //crash activity
  implementation "com.github.pecet86.historian:historian-uncaught-rxjava2:$historian_version" //RaJava2 global error
  implementation 'com.jakewharton.timber:timber:4.7.1'
}

android {
  buildFeatures {
    dataBinding = true
    viewBinding = true
  }
}
```

## Usage

```java
class App extends Application {

    private Historian historian;

      @Override
      public void onCreate() {
        super.onCreate();

        historian = Historian
            .builder(this)
            .debug(true)
            .logLevel(Log.INFO) //minimal log lever
            .notification(true) //show notification
            .retentionPeriod(Period.ONE_WEEK) //when delete old
            .callbacks(new Historian.Callbacks() { //added
              @Override
              public void onSuccess() {
                //is added to datebase
              }

              @Override
              public void onFailure(Throwable throwable) {
                //is error to datebase
              }
            })
            .build();

        //CrashActivity
        HistorianUncaughtExceptionHandler.install(this, historian,
            new CrashConfig()
                .withRestartActivityEnable(true)
                .withCloseActivityEnable(true)
                .withImagePath(R.drawable.historian_cow_error)

                .withRestartActivityClass(MainActivity.class)
        );

        //Global onError
        HistorianRxJavaExceptionHandler.install(
            HistorianUncaughtExceptionHandler.getInstance()
        );

        Timber.plant(new Timber.DebugTree());
        //install to Timber
        Timber.plant(HistorianTree.with(historian));
    }
}
```

## Libraries definition

- use [Android Room](https://developer.android.com/topic/libraries/architecture/room)
- use [ReactiveX](https://github.com/ReactiveX/RxJava/tree/2.x)
- use [Timber](https://github.com/JakeWharton/timber)
- use [Okio](https://github.com/square/okio)

## License

```
Copyright 2017 Shimizu Yasuhiro (yshrsmz)
Copyright 2020 Paweł Cal (pecet86)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
