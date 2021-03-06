/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taipower.west_branch;

import static com.taipower.west_branch.CommonUtilities.SERVER_URL;
import static com.taipower.west_branch.CommonUtilities.TAG;
import static com.taipower.west_branch.CommonUtilities.displayMessage;

import com.google.android.gcm.GCMRegistrar;
import com.taipower.west_branch.R;
import com.taipower.west_branch.R.string;
import com.taipower.west_branch.utility.HttpConnectResponse;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.net.ssl.SSLHandshakeException;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities extends Object
{
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String regId , final String electric_number_list , final String email , final String phone_number, final String mobile) 
    {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        
        String serverUrl = SERVER_URL + "/register.php";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("electric_number", electric_number_list);
        params.put("email", email);
        params.put("phone_number",phone_number);
        params.put("mobile", mobile);
        params.put("regId", regId);
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            Log.d(TAG, "Attempt #" + i + " to register");
            try 
            {
               	displayMessage(context, context.getString( R.string.server_registering, i, MAX_ATTEMPTS));
               	post(serverUrl, params);
               	GCMRegistrar.setRegisteredOnServer(context, true);
               	String message = context.getString(com.taipower.west_branch.R.string.server_registered);
               	CommonUtilities.displayMessage(context, message);
                return true;
            } 
            catch (IOException e) 
            {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) 
                {
                    break;
                }
                try 
                {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } 
                catch (InterruptedException e1) 
                {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(com.taipower.west_branch.R.string.server_register_error , MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
        return false;
    }
    
    public static boolean register(Context context, String reg_id)
    {
    	String url = "https://cksleeper.dlinkddns.com/android.php";
    	String[] parameters = new String[]{"reg_id=" + reg_id};
    	
    	
    	try 
    	{
    		byte[] response_data = HttpConnectResponse.onOpenConnection(url, "POST", parameters, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
		
			GCMRegistrar.setRegisteredOnServer(context, true);
			
			String response_string;
			
			if( response_data != null )
			{	
				response_string = new String(response_data);
				Log.i("GCM register status",response_string);
			
				if( response_string.equalsIgnoreCase("GCM OK") )
					return true;
			}
			else
				return false;
    	} 
    	catch (SSLHandshakeException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	catch (URISyntaxException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return false;
    }
    
    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String regId) 
    {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/unregister.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try 
        {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(com.taipower.west_branch.R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } 
        catch (IOException e) 
        {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(com.taipower.west_branch.R.string.server_unregister_error,
                    e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }
    }

    
    public static boolean update(final Context context, final String regId , final String electric_number_list , final String email , final String phone_number, final String mobile) 
    {
    	Log.i(TAG, "update device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/update.php";
        Map<String, String> params = new HashMap<String, String>();
        
        params.put("electric_number", electric_number_list);
        params.put("email", email);
        params.put("phone_number",phone_number);
        params.put("mobile", mobile);
        params.put("regId", regId);
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            Log.d(TAG, "Attempt #" + i + " to update");
            try 
            {
               	displayMessage(context, context.getString( R.string.server_registering, i, MAX_ATTEMPTS));
               	post(serverUrl, params);
               	GCMRegistrar.setRegisteredOnServer(context, true);
               	String message = context.getString(com.taipower.west_branch.R.string.server_updated);
               	CommonUtilities.displayMessage(context, message);
                return true;
            } 
            catch (IOException e) 
            {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to update on attempt " + i, e);
                if (i == MAX_ATTEMPTS) 
                {
                    break;
                }
                try 
                {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } 
                catch (InterruptedException e1) 
                {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(com.taipower.west_branch.R.string.server_update_error , MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
        return false;
    }
    
    
    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params) throws IOException 
    {
        URL url;
        try 
        {
            url = new URL(endpoint);
        } 
        catch (MalformedURLException e) 
        {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) 
        {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) 
            {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        
        try 
        {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) 
            {
            	Log.i(TAG, "post data to server error");
            	
            	throw new IOException("Post failed with error code " + status);
              
              
            }
        } 
        finally 
        {
            if (conn != null) 
            {
            	Log.i(TAG, "connection disconnect");
            	int status = conn.getResponseCode();
            	Log.i(TAG, String.valueOf(status) );
            	
            	InputStream input = conn.getInputStream();
            	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    			
    			byte[] data = new byte[input.available()];
    			int count;
    		 
    			while ((count = input.read(data)) != -1)
    			{
    				bos.write(data, 0, count);
    			}
    		 
    			bos.flush();
    			bos.close();
    			input.close();
    			
    			Log.i("Response dta :",new String(bos.toByteArray(),"UTF-8"));
            	conn.disconnect();
            }
        }
      }
}
