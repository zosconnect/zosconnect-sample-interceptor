/*
 Copyright IBM Corporation 2015

 LICENSE: Apache License
          Version 2.0, January 2004
          http://www.apache.org/licenses/

 The following code is sample code created by IBM Corporation.
 This sample code is not part of any standard IBM product and
 is provided to you solely for the purpose of assisting you in
 the development of your applications.  The code is provided
 'as is', without warranty or condition of any kind.  IBM shall
 not be liable for any damages arising out of your use of the
 sample code, even if IBM has been advised of the possibility
 of such damages.
*/
package com.ibm.crshnburn.zosconnect.interceptor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The OSGi Bundle Activator that is called by the OSGi runtime
 * when the Bundle is activated and deactivated.
 *
 * @author IBM
 */
public class Activator implements BundleActivator {

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
       System.out.println("BundleActivator start");
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
       System.out.println("BundleActivator stop");
    }
}