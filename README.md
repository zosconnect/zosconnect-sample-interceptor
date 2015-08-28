## Sample z/OS Connect Interceptor

A z/OS Connect Interceptor which demonstrates how to build an interceptor and include it as part
of a z/OS Connect configuration.

### Installing

* Clone this repository `git clone git://github.com/crshnburn/zosconnect-sample-interceptor.git`
* Install the feature into your WebSphere Liberty `featureManager install sample-interceptor.esa`

### Configuring

* Add the following to the `featureManager` section
```
<feature>usr:sampleinterceptor-1.0</feature>
```
* Create an interceptor definition
```
<usr_simpleInterceptor id="simpleInterceptor"/>
<zosConnectInterceptors id="interceptorList" interceptorRef="simpleInterceptor"/>
```
* Add the interceptors to the required services.

### Updating

If you want to use this sample as the basis of your own interceptor, or add extra functionality to
this interceptor then the contents of the `src` directory can be imported into an Eclipse installation
which has the WDT tools installed.

### Notice

&copy; Copyright IBM Corporation 2015

### License
```
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
