/*
 Copyright IBM Corporation 2015, 2019

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

import com.ibm.zosconnect.spi.Data;
import com.ibm.zosconnect.spi.HttpZosConnectRequest;
import com.ibm.zosconnect.spi.Interceptor;
import com.ibm.zosconnect.spi.InterceptorException;

/**
 * The SimpleInterceptorImpl class is an example of an Interceptor that implements just the Interceptor
 * interface.  See the AllPointsInterceptorSample for an example use of the ServiceProviderInterceptor and
 * EarlyFailureInterceptor interfaces.
 *
 * By implementing the Interceptor interface this Interceptor sample is invoked early when being processed by z/OS
 * Connect EE, and again after the SoR response has been returned and z/OS Connect EE is about to return to the
 * caller. For simplification these points are known as P1, and P4.  See the  AllPointsInterceptorSample for
 * details of points P2 and P3.
 *
 * The flow of the API Provider request as it relates to the Interceptor framework is as follows.
 *
 * 1.  A request is received by z/OS Connect EE.
 * 2.  The request is validated, e.g. Bad URL paths, authentication failures, bad JSON, etc.
 * 3.  Next, the requests Service and API information is extracted and matched to registered Services and APIs.
 * 4.  Should the request fail in the above processing the request is said to be an Early Failed request.
 * 5.  If there is an Interceptor registered with the Global Interceptor Manager that implements the
 *     EarlyFailureInterceptor interface its EarlyFail method will be invoked and the request terminates.
 * 6.  The API Provider request has now passed the early checks, the request can still fail, but it is now
 *     not an EarlyFailure and EarlyFail will now not be called.
 * 7.  If there is an Interceptor registered with either the Global, Service or API Interceptor Managers that
 *     implements the Interceptor interface its preInvoke method will be invoked. This is point P1.
 * 8.  Request flow continues through z/OS Connect EE into the Service Provider which will call the
 *     System Of Record, e.g. CICS, IMS, DB2, etc.
 * 9.  If there is an Interceptor registered with either the Global, Service or API Interceptor Managers that
 *     implements the ServiceProviderInterceptor interface its preSorInvoke method will be invoked.
 *     This is point P2.  The SoR is then invoked.
 * 10. Once the SoR returns the ServiceProviderInterceptor is invoked on its postSorInvoke method.
 *     This is point P3.
 * 11. The API Provider response flow continues, any registered Interceptor's with either the Global, Service
 *     or API Interceptor Managers that implements the Interceptor interface is invoked on its postInvoke method.
 *     This is point P4. Finally the response is sent by z/OS Connect EE.
 *
 * This Interceptor will be notified for all requests entering the z/OS Connect EE application on the Interceptor's
 * preInvoke method at P1 and at postInvoke at P4.
 *
 * This Interceptor also shows an example of how the request can be validated at preInvoke and the request terminated
 * if the validation fails.
 *
 * @author IBM
 */
public class SimpleInterceptorImpl implements Interceptor {

    /**
     * Request State Map data element used to accrue data across P1, and P4.
     */
    private static final String CALL_POINTS = "CALL_POINTSP1P4";

    /**
     * The registered sequence number of this Interceptor which determines the order
     * in which the Interceptor is called in relation to other Interceptor's.
     */
    private int sequence;

    /**
     * Date time formatter for logging.
     */
    private DateFormat df = SimpleDateFormat.getDateTimeInstance();

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
     * Returns this Interceptor's configured sequence number so the Interceptor Manager
     * can determine the call sequence of Interceptor's.
     */
    @Override
    public int getSequence() {
        return sequence;
    }

    /**
     * Returns this Interceptor's name.
     */
    @Override
    public String getName() {
        return "zOSConnectReferenceInterceptor";
    }

    /**
     * Interceptor Manager calls preInvoke method at point P1.
     *
     * The Interceptor is given a request state map that can be used to store data entities between the preInvoke
     * at P1 and the postInvoke at P4.
     *
     * The HttpZosConnectRequest object provides access to information associated to a specific HTTP request,
     * URL, HTTP Headers, etc. See com.ibm.zosconnect.spi.HttpZosConnectRequest javadoc for details.
     *
     * The Data object provides the request specific data available to Interceptor's and Service Providers.
     * See com.ibm.zosconnect.spi.Data javadoc for details.  As the API Provider request flows through
     * z/OS Connect EE more data elements are captured and stored in the Data object.  As an example
     * Data.TIME_ZOS_CONNECT_ENTRY is available which contains the z/OS Store Clock Extended time held
     * in a byte array of length 16.
     *
     * In this method the request will be validated and if the validation fails an InterceptorException is thrown
     * and the request is not processed and a failure response is passed to the caller.  Any data elements in the
     * Data object for the HttpZosConnectRequest object can be validated.
     *
     * @param requestStateMap
     * @param httpZosConnectRequest
     * @param data
     *
     * @exception InterceptorException
     */
    @Override
    public void preInvoke(Map<Object, Object> requestStateMap, HttpZosConnectRequest httpZosConnectRequest, Data data)
            throws InterceptorException {

        System.out.println(getName() + " preInvoke - P1");
        String path = httpZosConnectRequest.getRequestURI().trim();

        /*
         * Get the user name for message display
         */
        Principal principal = httpZosConnectRequest.getUserPrincipal();
        String user = "<unknown>";
        if(principal != null){
            user = principal.getName().trim();
        }

        if (data.getData(Data.USER_NAME_MAPPED) != null) {
            user += " (" + data.getData(Data.USER_NAME_MAPPED) + ")";
        }

        /*
         * Validate the user, if the userid starts with "EX" then this is an Example id
         * and is not allowed to run API Provider requests.
         */
        if (user.startsWith("EX")) {
            /*
             * Unable to process this request on behalf of an Operations userid.
             */
            System.out.println(getName() + " preInvoke InterceptorException - P1 - Bad user");
            throw new InterceptorException("Request not processed for user " + user);
        }

        /*
         * Store that this point has been called.
         */
        requestStateMap.put(CALL_POINTS, "P1");

        /*
         * Tell the user
         */
        System.out.println(String.format("User %s called URI %s at %s", user, path, df.format(new Date())));
        System.out.println(getName() + " preInvoke exit - P1");
    }

    /**
     * Interceptor Manager calls postInvoke method at point P4.
     *
     * The Data object provides the request specific data available to Interceptor's and Service Providers.
     * See com.ibm.zosconnect.spi.Data javadoc for details.  As the API Provider request flows through
     * z/OS Connect EE more data elements are captured and stored in the Data object.
     *
     * New Data elements that are available and relevant to point P4 are:
     *
     *  Data.HTTP_RESPONSE_CODE
     *  Data.TIME_ZOS_CONNECT_EXIT
     *  Data.REQUEST_TIMED_OUT
     *
     * @param requestStateMap
     * @param httpZosConnectRequest
     * @param data
     *
     * @exception InterceptorException
     */
    @Override
    public void postInvoke(Map<Object, Object> requestStateMap, HttpZosConnectRequest httpZosConnectRequest, Data data)
            throws InterceptorException {
        System.out.println(getName() + " postInvoke - P4");

        /*
         * Add the call point P4, the final value will be P1P4.
         * This shows how a data element can be passed between the methods.
         */
        String callPoints = requestStateMap.get(CALL_POINTS) + "P4";
        System.out.println(getName() + " Interceptor points called were: " + callPoints);

        System.out.println("The request completed with HTTP Response Code " + data.getData(Data.HTTP_RESPONSE_CODE));

        System.out.println(getName() + " postInvoke exit - P4");
    }
}
