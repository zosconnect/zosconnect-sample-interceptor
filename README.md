## Sample z/OS Connect Enterprise Edition Interceptors

Sample z/OS Connect Enterprise Edition (EE) Interceptors which demonstrates how to build interceptors and include them as part of a z/OS Connect configuration.

### Building

This example project shows how to build a WebSphere Liberty Profile OSGi extension that can be deployed to a Liberty Profile server. This extension contains four sample z/OS Connect EE Interceptors.  If you wish to create z/OS Connect EE Interceptors of your own, then this project may be used as a base.

This project contains an Eclipse project which can be imported to your Eclipse installation.  The Eclipse installation must have the IBM WebSphere Application Server V9.x Developer Tools installed.

Eclipse can be downloaded from `https://www.eclipse.org/downloads/`. When installed, use the Eclipse Marketplace under the Help menu to locate and install the `IBM WebSphere Application Server V9.x Developer Tools` Eclipse extension.

Clone this repository `git clone git://github.com/zosconnect/zosconnect-sample-interceptor.git`.  Under the `src` directory are two Eclipse projects that can be imported using the Eclipse Import wizard using type `Existing Projects into Workspace`.

You now have the two sample projects, `com.ibm.crshnburn.zosconnect.interceptor`, which contains the Java source code and the OSGi configuration, and `com.ibm.crshnburn.zosconnect.feature`, which contains the WebSphere Liberty Profile feature that produces the Liberty Subsystem Archive (.esa) file that can be installed in the z/OS Connect EE Liberty profile server.

Refer to the Liberty documentation at `https://developer.ibm.com/wasdev/docs/` for full details on implementing OSGi feature bundles.

The important files in the `com.ibm.crshnburn.zosconnect.interceptor` project are:

* src/com/ibm/crshnburn/zosconnect/interceptor/Activator.java - An OSGi Bundle Activator
* src/com/ibm/crshnburn/zosconnect/interceptor/AllPointsInterceptorSample.java - An Interceptor, ServiceProviderInterceptor, and EarlyFailureInterceptor implementation.
* src/com/ibm/crshnburn/zosconnect/interceptor/AllPointsInterceptorRequesterSample.java - An Interceptor, InterceptorRequester, EndpointInterceptor, and EarlyFailureInterceptorRequester implementation.
* src/com/ibm/crshnburn/zosconnect/interceptor/SimpleInterceptorImpl.java - An Interceptor implementation.
* src/com/ibm/crshnburn/zosconnect/interceptor/SimpleInterceptorRequesterImpl.java - An InterceptorRequester implementation.
* BundleContent/Meta-INF/MANIFEST.MF - The `com.ibm.crshnburn.zosconnect.interceptor` Bundle manifest that describes the bundle.
* BundleContent/OSGI-INF/metatype/metatype.xml - Describes the server.xml configuration element detail.
* BundleContent/OSGI-INF/com.ibm.crshnburn.zosconnect.allpointsinterceptor.xml - Describes the implementation class and services of the AllPointsInterceptorSample class
* BundleContent/OSGI-INF/com.ibm.crshnburn.zosconnect.allpointsinterceptor.requester.xml - Describes the implementation class and services of the AllPointsInterceptorRequesterSample class
* BundleContent/OSGI-INF/com.ibm.crshnburn.zosconnect.interceptor.xml - Describes the implementation class and services of the SimpleInterceptorImpl class
* BundleContent/OSGI-INF/com.ibm.crshnburn.zosconnect.interceptor.requester.xml - Describes the implementation class and services of the SimpleInterceptorRequesterImpl class

The important files in the `com.ibm.crshnburn.zosconnect.feature` project are:

* OSGI-INF/SUBSYSTEM.MF - The sampleinterceptor-1.0 OSGi feature description.

The Java code will build automatically and when you are ready to create a Liberty Subsystem Archive (.esa) feature bundle file, right click on the `com.ibm.crshnburn.zosconnect.feature` project and select Export/Liberty Feature (ESA) menu item.  The Liberty Feature Export dialog is displayed. Enter the location and name of the .esa file, e.g. sample-interceptor.esa, then select the `com.ibm.crshnburn.zosconnect.interceptor` bundle to include in the feature. Then click Finish.

### Installing

* Install the feature into your z/OS Connect EE environment `wlp/bin/installUtility install sample-interceptor.esa`.  The `wlp` directory is relative to the z/OS Connect EE installation directory.

### Configuring

* Add the following to the `featureManager` section:
```
<feature>usr:sampleinterceptor-1.0</feature>
```
* Create an interceptor definition for the four sample Interceptors:

```
<usr_simpleInterceptor id="simpleInterceptor" sequence="1"/>
<usr_allPointsInterceptor id="allPointsInterceptor" sequence="2"/>
<usr_simpleInterceptorRequester id="simpleInterceptorRequester" sequence="1"/>
<usr_allPointsInterceptorRequester id="allPointsInterceptorRequester" sequence="2"/>
<zosconnect_zosConnectInterceptors id="interceptorList"
           interceptorRef="simpleInterceptor,allPointsInterceptor,simpleInterceptorRequester,allPointsInterceptorRequester"/>
```
* Add the interceptors at the required global, API, service or API requester level.  This example shows the interceptor configured at the global level: 

```
<zosconnect_zosConnectManager globalInterceptorsRef="interceptorList"/>
```

* See the z/OS Connect EE configuration documentation for further details.

### Notice

&copy; Copyright IBM Corporation 2015, 2021

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
