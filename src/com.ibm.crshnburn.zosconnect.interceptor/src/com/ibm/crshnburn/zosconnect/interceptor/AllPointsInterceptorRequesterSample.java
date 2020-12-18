/*
 * Copyright IBM Corporation 2020
 *
 * LICENSE: Apache License
 *          Version 2.0, January 2004
 *          http://www.apache.org/licenses/
 *
 * The following code is sample code created by IBM Corporation.
 * This sample code is not part of any standard IBM product and
 * is provided to you solely for the purpose of assisting you in
 * the development of your applications.  The code is provided
 * 'as is', without warranty or condition of any kind.  IBM shall
 * not be liable for any damages arising out of your use of the
 * sample code, even if IBM has been advised of the possibility
 * of such damages.
 */
package com.ibm.crshnburn.zosconnect.interceptor;

import java.util.Map;

import org.osgi.service.component.ComponentContext;

import com.ibm.zosconnect.spi.Data;
import com.ibm.zosconnect.spi.DataRequester;
import com.ibm.zosconnect.spi.EarlyFailureInterceptorRequester;
import com.ibm.zosconnect.spi.EndpointInterceptor;
import com.ibm.zosconnect.spi.HttpZosConnectRequest;
import com.ibm.zosconnect.spi.InterceptorException;

/**
 * The AllPointsInterceptorRequesterSample class is an example of an Interceptor that implements the Interceptor,
 * InterceptorRequester, EndpointInterceptor, and EarlyFailureInterceptorRequester interfaces. 
 *  
 * Both the EndpointInterceptor and EarlyFailureInterceptorRequester interfaces extend the InterceptorRequester 
 * interface, which in turn extends the Interceptor interface, so all methods of those interfaces are implemented as well.
 *
 * Note, it is possible that an Interceptor can be configured so that it is invoked more than once for the same 
 * request, although this is unlikely to be intended behaviour.
 *
 * The points at which this sample Interceptor methods are invoked in an API requester flow are illustrated here:
 * https://www.ibm.com/support/knowledgecenter/SS4SVW_3.0.0/extending/create_monitoring_interceptor.html and detailed in
 * the steps below.
 *
 * The API requester request flow:
 * 1.  A request is received by z/OS Connect EE from the communication stub program BAQCSTUB.
 * 2.  Authentication and request format checks are made.
 * 3.  If the request fails any checks in step 2 and there is a global Interceptor registered that implements the
 *     EarlyFailureInterceptorRequester interface, its earlyFailureRequester method is called and the request
 *     terminates. 
 * 4.  The request has now passed the early checks, the request can still fail, but it is now not an early failure
 *     and earlyFailureRequester will not now be called.
 * 5.  If there is an Interceptor registered at either the global or API requester level that implements the
 *     InterceptorRequester interface, its preInvokeRequester method is called. 
 * 6.  Request data mapping is performed.
 * 7.  If there is an Interceptor registered at either the global or API requester level that implements the 
 *     EndpointInterceptor interface, its preEndpointInvoke method is called.
 * 8.  The API request is sent to the endpoint.
 * 9.  When the endpoint response is received, the EndpointInterceptor postEndpointInvoke method is called, if the 
 *     corresponding preEndpointInvoke method was called.
 * 10. Response data mapping is performed.
 * 11. The InterceptorRequester postInvokeRequester method is called if the corresponding preInvokeRequester method
 *     was called.
 * 12. z/OS Connect EE returns the response to the BAQCSTUB program.
 *
 * @author IBM
 */
public class AllPointsInterceptorRequesterSample implements EndpointInterceptor, EarlyFailureInterceptorRequester {

    /**
     * The registered sequence number of this Interceptor which determines the order
     * in which the Interceptor is called in relation to other Interceptors.
     */
    private int sequence;

    /**
     * Activates the Interceptor.
     *
     * Trace the activation and retrieve the Interceptor's sequence number from
     * the Interceptor configuration element in server.xml.
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
     * Returns this Interceptor's configured sequence number so z/OS Connect EE can 
     * determine the sequence in which to call Interceptors.
     */
    @Override
    public int getSequence() {
        return sequence;
    }

    /**
     * Returns this Interceptors name.
     */
    @Override
    public String getName() {
        return "zOSConnectAllPointsSampleInterceptorRequester";
    }
       
	/**
     * z/OS Connect EE calls the preInvokeRequester method after initial request checks.
     *
     * The Interceptor is given a request state map that can be used to store data, such as state data, between the 
     * preInvokeRequester and the postInvokeRequester methods.
     *
     * The DataRequester object provides API requester specific data to the Interceptor.
     * See com.ibm.zosconnect.spi.DataRequester javadoc for details.  As the API requester request flows through
     * z/OS Connect EE more data is captured and stored in the DataRequester object.  
     *
     * In this example, the API requester name and version are obtained and logged.
     * 
     * @param requestStateMap
     * @param data
     */
	@Override
	public void preInvokeRequester(Map<Object, Object> requestStateMap, DataRequester data) {
		System.out.println(getName() + " preInvokeRequester");
		
		System.out.println("Invoking the API requester " + data.getData(DataRequester.API_REQUESTER_NAME) + 
				           " verion " + data.getData(DataRequester.API_REQUESTER_VERSION));
		
		System.out.println(getName() + " preInvokeRequester exit");
	}

    /**
    *
    * z/OS Connect EE calls the preEndpointInvoke method just before calling the endpoint.
    *
    * The Interceptor is given a request state map that can be used to store data, such as state data, between the 
    * preEndpointInvoke, postEndpointInvoke and postInvokeRequester methods.
    *
    * The DataRequester object provides the API requester request specific data available to Interceptors.
    * See com.ibm.zosconnect.spi.DataRequester javadoc for details.
    *
    * @param requestStateMap
    * @param data
    */
	public void preEndpointInvoke(Map<Object, Object> requestStateMap, DataRequester data) {
		System.out.println(getName() + " preEndpointInvoke");
		
		System.out.println("API requester target endpoint " + data.getData(DataRequester.ENDPOINT_HOST) +
				           ":" + data.getData(DataRequester.ENDPOINT_PORT));
		
		System.out.println("API requester about to invoke method " + data.getData(DataRequester.ENDPOINT_METHOD) + 
				           " for path " + data.getData(DataRequester.ENDPOINT_FULL_PATH));
			
		System.out.println(getName() + " preEndpointInvoke exit");
	}
	
    /**
     * z/OS Connect EE calls the postEndpointInvoke method once the call to the endpoint returns.
     *
     * The DataRequester object provides API requester specific data to the Interceptor.
     * See com.ibm.zosconnect.spi.DataRequester javadoc for details.  
     * 
     * In this example, the request response code is obtained and logged.
     *
     * @param requestStateMap
     * @param data
     */
	public void postEndpointInvoke(Map<Object, Object> requestStateMap, DataRequester data) {
		System.out.println(getName() + " postEndpointInvoke");
		
		System.out.println("API requester was invoked and the endpoint returned " + data.getData(DataRequester.HTTP_RESPONSE_CODE));
		
		System.out.println(getName() + " postEndpointInvoke exit");
	}
	
    /**
     * z/OS Connect EE calls the postInvokeRequester method.
     *
     * The DataRequester object provides the API requester request specific data available to Interceptors.
     * See com.ibm.zosconnect.spi.DataRequester javadoc for details.  As the API requester request flows through
     * z/OS Connect EE more data elements are captured and stored in the DataRequester object.
     *
     * @param requestStateMap
     * @param data
     */
	@Override
	public void postInvokeRequester(Map<Object, Object> requestStateMap, DataRequester data) {
		System.out.println(getName() + " postInvokeRequester");
		
		System.out.println("API requester returning to calling application with " + data.getData(DataRequester.REQUEST_STATUS_CODE));
		
		System.out.println(getName() + " postInvokeRequester exit");
	}
	
	/** 
	 * z/OS Connect EE calls the earlyFailureRequester method for a failing API requester request.
     *
     * The DataRequester object provides the API requester request specific data available to Interceptors.
     * See com.ibm.zosconnect.spi.DataRequester javadoc for details.
     *
     * @param data
	 */
	@Override
	public void earlyFailureRequester(DataRequester data) {
		System.out.println(getName() + " earlyFailureRequester");

        System.out.println("The request failed early, returning to application with " + data.getData(DataRequester.REQUEST_STATUS_CODE));

        System.out.println(getName() + " earlyFailureRequester exit");	
	}

	/**
     * This method is not called for API requester requests. It is called for API provider and all administration requests.
     */
	@Override
	public void preInvoke(Map<Object, Object> arg0, HttpZosConnectRequest arg1, Data arg2) throws InterceptorException {
		System.out.println(getName() + " preInvoke");
		System.out.println(getName() + " preInvoke exit");
	}
	
	/**
     * This method is not called for API requester requests. It is called for API provider and all administration requests. 
     */
	@Override
	public void postInvoke(Map<Object, Object> arg0, HttpZosConnectRequest arg1, Data arg2)
			throws InterceptorException {
		System.out.println(getName() + " postInvoke");
		System.out.println(getName() + " postInvoke exit");
	}
}
