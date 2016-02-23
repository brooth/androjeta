# Androjeta

`Androjeta` - is a Open Source library that brings [Jeta][jeta] on android platform. If your are not familiar with `Jeta` it's recommended to read [this readme][jeta] first.

Examples:
--------
In addition to [Jeta's][jeta] collection of features, `Androjeta` provides  annotations that useful on android:

### @FindView
`FindView` binds a view from the layout.

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/sampleActivity_textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</LinearLayout>
```

Bind `TextView` with Andojeta:
```java
class SampleActivity extends BaseActivity {
    @FindView
    TextView textView;
}
```
instead of:
```java
class SampleActivity extends Activity {
    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        textView = (TextView) activity.findViewById(R.id.sampleActivity_textView);
   }
}
```
Note that in the example above `textView` is bound to a view with id `R.id.sampleActivity_textView`. By the default, Androjeta generates id name as `<activity name started with lowercase>` + `_` + `<field name>`. You can specify the name:
```java
class SampleActivity extends BaseActivity {
    @FindView(R.id.sampleActivity_textView)
    TextView textView;
}
```
or
```java
class SampleActivity extends BaseActivity {
    @FindView(name = "sampleActivity_textView")
    TextView textView;
}
```
See below how to define `BaseActivity`.

### @Retain
Androjeta helps to avoid one of the most annoying boilerplate on android. No need anymore to use `onSaveInstanceState` callback to retain sensitive data:

```java
class SampleActivity extends BaseActivity {
    @Retain
    String text;
}
```

instead of:
```java
class SampleActivity extends Activity {
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            text = bundle.getString("text");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("text", text);
    }
}
```
See below how to define `BaseActivity`.

### @OnClick, @OnLongClick
`@OnClick` binds `View.OnClickListener()` to a method:
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

 <Button
        android:id="@+id/sampleActivity_saveButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Save"/>
</LinearLayout>
```

Bind `Button` with Andojeta:
```java
class SampleActivity extends BaseActivity {
    @OnClick
    void onClickSaveButton() {
        //...
    }
}
```
instead of:
```java
class SampleActivity extends Activity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        findViewById(R.id.sampleActivity_saveButton).setOnClickListener(this);
   }
}
```
Note that `onClickSaveButton` is bound to `R.id.sampleActivity_saveButton`. By default Androjeta generates id name as: `<activity name started with lowercase>` + `_` + `<method name without 'onClick' at the beginning>`. You can specify concrete id:
```java
class SampleActivity extends BaseActivity {
    @OnClick(R.id.sampleActivity_saveButton)
    void onClickSaveButton() {
        //...
    }
}
```
or
```java
class SampleActivity extends BaseActivity {
    @OnClick(name="sampleActivity_saveButton")
    void onClickSaveButton() {
        //...
    }
}
```

##### OnLongClick:
```java
class SampleActivity extends BaseActivity {
    @OnLongClick
    void onLongClickSaveButton() {
        //...
    }
}
```
Note that `onLongClickSaveButton` returns `void`. In this case Androjeta generates `View.OnLongClickListener()` that returns `true` by default. You can change `void` to `boolean` to be able to return `false`.

#### BaseActivity
```java
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            MetaHelper.restoreRetains(this, savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        MetaHelper.findViews(this);
        MetaHelper.applyOnClicks(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MetaHelper.saveRetains(this, outState);
    }
}
```

Note that `Androjeta` doesn't provide `MetaHelper` class. You should create your own helper class depending on your needs. You can use `MetaHelper` class from [androjeta-samples][androjeta-samples] as a prototype.


Installation (gradle):
----------------------

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/brooth/maven"
    }
}

dependencies {
    apt 'org.brooth.androjeta:androjeta-apt:0.1-alpha'
    compile 'org.brooth.androjeta:androjeta:0.1-alpha'
}
```
Note that `Androjeta` is a annotation processing tool so you need an `apt` plugin. It's recommended to use [android-apt by hvisser][android-apt-plugin]

Configuration:
--------------
Please, read configuration section in [this readme][jeta] first.

If you are using [android-apt by hvisser][android-apt-plugin], you can define the path to `jeta.properties` in `arguments` section:
```groovy
apt {
    arguments {
        jetaProperties "$project.projectDir/src/main/java/jeta.properties"
    }
}
```

To be able to use @FindView, @OnClick and @OnLongClick with default names you need to define project's package in `jeta.properties`:
```
application.package=<project's package name>
```

Complete gradle script and samples are available in [androjeta-samples project][androjeta-samples]

License
-------

    Copyright 2015 Oleg Khalidov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[androjeta-samples]: https://github.com/brooth/androjeta-samples
[jeta]: https://github.com/brooth/jeta
[android-apt-plugin]: https://bitbucket.org/hvisser/android-apt