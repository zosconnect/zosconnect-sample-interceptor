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

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.osgi.service.component.ComponentContext;

import com.ibm.zosconnect.spi.*;

public class SimpleInterceptorImpl implements Interceptor {
	private int sequence;

	private DateFormat df = SimpleDateFormat.getDateTimeInstance();

	protected void activate(ComponentContext context,
			Map<String, Object> properties) {
		System.out.println("Sample interceptor activated");
		sequence = (Integer) properties.get(CFG_AD_SEQUENCE_ALIAS);
	}

	protected void deactivate(ComponentContext context) {
		System.out.println("Sample interceptor deactivated");
	}

	protected void modified(Map<String, Object> properties) {
		System.out.println("Sample interceptor modified");
		sequence = (Integer) properties.get(CFG_AD_SEQUENCE_ALIAS);
	}

	@Override
	public String getName() {
		return "zOSConnectReferenceInterceptor";
	}

	@Override
	public void preInvoke(Map<Object, Object> requestStateMap,
			HttpZosConnectRequest httpZosConnectRequest, Data data)
			throws InterceptorException {
		System.out.println("ReferenceInterceptor preInvoke");
		Principal principal = httpZosConnectRequest.getUserPrincipal();
		String user = "<unknown>";
		if(principal != null){
			user = principal.getName().trim();
		}
		String path = httpZosConnectRequest.getRequestURI().trim();
		System.out.println(String.format("User %s called URI %s at %s", user, path,
				df.format(new Date())));
	}

	@Override
	public void postInvoke(Map<Object, Object> requestStateMap,
			HttpZosConnectRequest httpZosConnectRequest, Data data)
			throws InterceptorException {
		System.out.println("ReferenceInterceptor postInvoke");
		Principal principal = httpZosConnectRequest.getUserPrincipal();
		String user = "<unknown>";
		if(principal != null){
			user = principal.getName().trim();
		}
		String path = httpZosConnectRequest.getRequestURI().trim();
		System.out.println(String.format("User %s finished calling URI %s at %n", user,
				path, df.format(new Date())));
	}

	@Override
	public int getSequence() {
		return sequence;
	}

}
