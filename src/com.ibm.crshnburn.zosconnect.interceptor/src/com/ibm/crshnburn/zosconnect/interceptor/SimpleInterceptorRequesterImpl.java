/*
 Copyright IBM Corporation 2020

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

import java.util.Map;

import org.osgi.service.component.ComponentContext;

import com.ibm.zosconnect.spi.Data;
import com.ibm.zosconnect.spi.DataRequester;
import com.ibm.zosconnect.spi.HttpZosConnectRequest;
import com.ibm.zosconnect.spi.InterceptorException;
import com.ibm.zosconnect.spi.InterceptorRequester;

/**
 * The SimpleInterceptorRequesterImpl class is an example of an Interceptor that implements only the 
 * InterceptorRequester interface. As the InterceptorRequester interface extends the Interceptor interface, 
 * you must also implement the methods of the Interceptor interface as well.
 * 
 * Note, it is possible that an Interceptor can be configured so that it is invoked more than once for the same 
 * request, although this is unlikely to be intended behaviour.
 * 
 * This sample Interceptor is invoked at two points in an API requester flow, as illustrated here:
 * https://www.ibm.com/support/knowledgecenter/SS4SVW_3.0.0/extending/requester_interceptors.html and detailed in
 * steps 3 and 7 below.
 *
 * The API requester request flow:
 * 1.  A request is received by z/OS Connect EE from the communication stub program BAQCSTUB.
 * 2.  Authentication and request format checks are made.
 * 3.  If there is an Interceptor registered at either the global or API requester level that implements 
 *     the InterceptorRequester interface, its preInvokeRequester method is called. 
 * 4.  Request data mapping is performed.
 * 5.  The API request is sent to the endpoint.
 * 6.  Response data mapping is performed.
 * 7.  The InterceptorRequester postInvokeRequester method is called if the corresponding preInvokeRequester method
 *     was called.
 * 8. z/OS Connect EE returns the response to the BAQCSTUB program.
 *
 * @author IBM
 */
public class SimpleInterceptorRequesterImpl implements InterceptorRequester {

	/**
     * The registered sequence number of this Interceptor which determines the order
     * in which the Interceptor is called in relation to other Interceptors.
     */
    private int sequence;
    
    /**
     * Activates the Interceptor.
     *
     * Trace the activation and retrieve the Interceptor's sequence number from
     * the Interceptor's configuration element in server.xml.
     *
     * @param context
     * @param properties
     */
    protected void activate(ComponentContext context, Map<String, Object> properties) {

        System.out.println(getName() + " activated");
        if (properties.containsKey(CFG_AD_SEQUENCE_ALIAS)) {
            sequence = (Integer) properties.get(CFG_AD_SEQUENCE_ALIAS);
        }
    }

    /**
     * Deactivates the Interceptor.
     *
     * The Interceptor will no longer receive events.
     *
     * @param context
     */
    protected void deactivate(ComponentContext context) {
        System.out.println(getName() + " deactivated");
    }

    /**
     * Called to signal that the Interceptor's configuration element may have changed in server.xml.
     *
     * Trace the activation and retrieve the Interceptor's sequence number from
     * the Interceptor's configuration element in server.xml.
     *
     * @param context
     * @param properties
     */
    protected void modified(Map<String, Object> properties) {

        System.out.println(getName() + " modified");
        if (properties.containsKey(CFG_AD_SEQUENCE_ALIAS)) {
            sequence = (Integer) properties.get(CFG_AD_SEQUENCE_ALIAS);
        }
    }
    
    /**
     * Returns this Interceptor's name.
     */
	@Override
	public String getName() {
		return "zOSConnectReferenceInterceptorRequester";
	}

	/**
     * Returns this Interceptor's configured sequence number so z/OS Connect EE can 
     * determine the sequence in which to call Interceptors.
     */
	@Override
	public int getSequence() {
		return sequence;
	}

	/**
     * z/OS Connect EE calls the preInvokeRequester method after initial request checks.
     *
     * The Interceptor is given a request state map that can be used to store data, such as state data, between the 
     * preInvokeRequester and the postInvokeRequester methods.
     *
     * The DataRequester object provides API requester specific data to the Interceptor.
     * See com.ibm.zosconnect.spi.DataRequester javadoc for details. As the API requester request flows through
     * z/OS Connect EE more data is captured and stored in the DataRequester object.  
     *
     * In this example, the API requester name and version are obtained and logged.
     * 
     * @param requestStateMap
     * @param data
     */
	@Override
	public void preInvokeRequester(Map<Object, Object> requestStateMap, DataRequester data) throws InterceptorException {
		System.out.println(getName() + " preInvokeRequester entry ");
		
		System.out.println("Invoking the API requester " + data.getData(DataRequester.API_REQUESTER_NAME) + 
		                   " verion " + data.getData(DataRequester.API_REQUESTER_VERSION));
		
		System.out.println(getName() + " preInvokeRequester exit");
	}
	
	/**
	 * z/OS Connect EE calls the postInvokeRequester method.
     *
     * The DataRequester object provides API requester specific data to the Interceptor.
     * See com.ibm.zosconnect.spi.DataRequester javadoc for details.  
     * 
     * In this example, the request response code is obtained and logged.
     *
     * @param requestStateMap
     * @param data
     */
	@Override
	public void postInvokeRequester(Map<Object, Object> requestStateMap, DataRequester data) throws InterceptorException {
		System.out.println(getName() + " postInvokeRequester entry");
		
		System.out.println("API requester returning to calling application with " + data.getData(DataRequester.REQUEST_STATUS_CODE));
		
		System.out.println(getName() + " postInvokeRequester exit");
	}
	
	/**
     * This method is not called for API requester requests. It is called for API provider and all administration requests.
     */
	@Override
	public void preInvoke(Map<Object, Object> requestStateMap, HttpZosConnectRequest request, Data data) throws InterceptorException {
		System.out.println(getName() + " preInvoke");
		System.out.println(getName() + " preInvoke exit");
	}
	
	/**
     * This method is not called for API requester requests. It is called for API provider and all administration requests.
     */
	@Override
	public void postInvoke(Map<Object, Object> requestStateMap, HttpZosConnectRequest request, Data data)
			throws InterceptorException {
		System.out.println(getName() + " postInvoke");
		System.out.println(getName() + " postInvoke exit");
	}
	
}
