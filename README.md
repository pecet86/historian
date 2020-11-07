Historian
===

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Historian-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5329)
[![CircleCI](https://circleci.com/gh/yshrsmz/historian.svg?style=svg)](https://circleci.com/gh/yshrsmz/historian)
[![codecov](https://codecov.io/gh/yshrsmz/historian/branch/master/graph/badge.svg)](https://codecov.io/gh/yshrsmz/historian)

Historian is a custom [Timber](https://github.com/JakeWharton/timber).Tree implementation that saves logs to SQLite, so that you can see/download the SQLite file later for debugging.

This library is primarily made to help debugging crash in consumers' devices.

## Installation

Historian is distributed via jCenter. [![Bintray](https://img.shields.io/bintray/v/yshrsmz/maven/historian-core.svg)](https://bintray.com/yshrsmz/maven/historian-core/view)

```gradle
dependencies {
  implementation 'net.yslibrary.historian:historian-core:LATEST_LIBRARY_VERSION'
  implementation 'net.yslibrary.historian:historian-tree:LATEST_LIBRARY_VERSION' //connect to timber
  implementation 'net.yslibrary.historian:historian-uncaught-handler:LATEST_LIBRARY_VERSION' //crash activity
  implementation 'net.yslibrary.historian:historian-uncaught-rxjava2:LATEST_LIBRARY_VERSION' //RaJava2 global error
  implementation 'com.jakewharton.timber:timber:4.5.1'
}
```

## Usage

```java
class App extends Application {

    Historian historian;

    @Override
    public void onCreate() {
        super.onCreate();

        historian = Historian
            .builder(this)
            .build();

        HistorianUncaughtExceptionHandler.install(this, historian,
            new CrashConfig()
                .withRestartActivityClass(MainActivity.class)
        );
        HistorianRxJavaExceptionHandler.install(this, historian);

        Timber.plant(new Timber.DebugTree());
        Timber.plant(HistorianTree.with(historian));
    }
}
```

## Table definition

use [Android Room](https://developer.android.com/topic/libraries/architecture/room)


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
