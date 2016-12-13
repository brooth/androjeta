
# Androjeta

`Androjeta` - is a Open Source library that brings [Jeta][jeta] on android platform.

Tutorials:
--------------
Read the docs on [jeta.brooth.org](http://jeta.brooth.org/guide/androjeta/overview.html)

Samples:
--------
[androjeta-samples](https://github.com/brooth/androjeta-samples)


Installation (gradle):
----------------------

```groovy
repositories {
    jcenter()
}

dependencies {
    apt 'org.brooth.androjeta:androjeta-apt:2.3'
    compile 'org.brooth.androjeta:androjeta:2.3'
}
```
[Here](https://plugins.gradle.org/search?term=apt) to can find available `apt` plugins.

License
-------

    Copyright 2016 Oleg Khalidov

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
[jeta-configuration]: https://github.com/brooth/jeta#configuration
