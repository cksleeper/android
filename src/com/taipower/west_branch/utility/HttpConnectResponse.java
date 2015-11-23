package com.taipower.west_branch.utility;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/*
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
*/
import com.google.api.client.util.IOUtils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

@SuppressWarnings("deprecation")
public class HttpConnectResponse 
{
	/*  URLconnect Not run @ Taipowr ebpps why ??
	 *  
	 * 
	 */
	public static String COOKIES_VALUE = null;
	public static int CONNECTION_STATE_CODE ; 
	/*
	private static CookieStore cookieStore_globe = null;
	
	@SuppressWarnings("deprecation")
	public static byte[] apacheConnection(String url, String method, ArrayList<NameValuePair> params,String url_encode_type,boolean cookies) throws ClientProtocolException, IOException 
    {
		if( cookieStore_globe == null )
			cookieStore_globe = new BasicCookieStore();
		
		HttpContext httpContext = new BasicHttpContext();
		
		if(cookies)
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore_globe);
		else
			httpContext = null;
		
		
		byte[] response_data = null;
		
		HttpGet httpRequest_login_get = null ;
		HttpPost httpRequest_login_post = null;
		
		if( method.toUpperCase(Locale.ENGLISH).equals("GET"))
			httpRequest_login_get = new HttpGet(url);
		else
			httpRequest_login_post = new HttpPost(url);	
    	
		HttpClient client_login;
    	
    	if(url.startsWith("https"))
    		client_login = MySSLSocketFactory.createMyHttpClient();
    	else
    		client_login = new DefaultHttpClient();
    	
    	
    	// need ArrayList<NameValuePair>
    	if( method.toUpperCase(Locale.ENGLISH).equals("POST"))
    	{
    		// encode to for POST method
    		UrlEncodedFormEntity encode_params = new UrlEncodedFormEntity(params , url_encode_type );
    		httpRequest_login_post.setEntity( encode_params );
    	}
    	
		//execute HTTP client and get response (without httpContext)
    	HttpResponse httpResponse_login ;
    	
		if( httpContext != null )
		{	
			if( method.toUpperCase(Locale.ENGLISH).equals("POST") )
				httpResponse_login = client_login.execute(httpRequest_login_post,httpContext);
			else
				httpResponse_login = client_login.execute(httpRequest_login_get,httpContext);
		}
		else // non-httpContext
		{	
			if( method.toUpperCase(Locale.ENGLISH).equals("POST") )
				httpResponse_login = client_login.execute(httpRequest_login_post);
			else
				httpResponse_login = client_login.execute(httpRequest_login_get);
		}
		
		Header[] qq = httpResponse_login.getAllHeaders();
		for(Header qqq : qq)
			Log.i("ALL Header :",  qqq.toString());
		
		//save cookie
		Log.i("cookieStore_globe.getCookies() : " , cookieStore_globe.getCookies().toString());
		
		CONNECTION_STATE_CODE = httpResponse_login.getStatusLine().getStatusCode();
		
		Log.i("Apacha http code : " , String.valueOf( CONNECTION_STATE_CODE ) );
		
		
		
		if( CONNECTION_STATE_CODE == 200)
		{
			response_data = EntityUtils.toByteArray(httpResponse_login.getEntity());
			
			if(cookieStore_globe.getCookies().size() > 1)
				COOKIES_VALUE = cookieStore_globe.getCookies().get(1).getValue();
		}
		else 
			return null;
					
		return response_data;
    }
	*/
	
	public final static boolean HTTP_REDIRECT = true;
	public final static boolean HTTP_NONREDIRECT = false;
	
	public final static boolean COOKIE_KEEP = true;
	public final static boolean COOKIE_CLEAR = false;
	
	private static CookieManager cookieManager = null; // ? try later
	private static String cookies = null;
	
	/*
	 *  Create URLConnection class connection to replace apache http class( was be deprecated after API 22)
	 * 
	 */
	
	public static byte[] onOpenConnection( String url_string, String method, String[] parameters, boolean cookie_state, boolean http_redirect) throws IOException, URISyntaxException,SSLHandshakeException
	{
		if( cookieManager == null )
		{	
			cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
		}
		
		URLConnection connection = null;
		boolean https_check = url_string.startsWith("https");
		byte[] http_response = null;
		
		if(!cookie_state)
			cookies = null;
		
		URL url = null;
		
		method =  method.toUpperCase(Locale.US);
		
		if( !method.startsWith("POST") )
		{	
			if(parameters != null )
			{
				if(!parameters.equals("") && !url_string.contains("?"))
					url_string += "?" + parameters;
			}
			//Log.i("Sending 'GET' request to URL : " , url_string);
		}
		
		url = new URL(url_string);
		
		if(https_check)
		{	
			connection = (HttpsURLConnection) url.openConnection(); 
			
			try 
			{
				connection = disableCertificateALLHostName(((HttpsURLConnection) connection));
			} 
			catch (KeyManagementException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (NoSuchAlgorithmException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (NoSuchProviderException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(http_redirect)
				((HttpsURLConnection) connection).setInstanceFollowRedirects(false); 
			else
				((HttpsURLConnection) connection).setInstanceFollowRedirects(true);
			//HttpsURLConnection.setFollowRedirects(true);
			
		}
		else	
		{	
			connection = (HttpURLConnection) url.openConnection(); 
			
			if(http_redirect)
				((HttpURLConnection) connection).setInstanceFollowRedirects(false); 
			else
				((HttpURLConnection) connection).setInstanceFollowRedirects(true);
			
			//HttpURLConnection.setFollowRedirects(true);
		}
		
		
		{
			//((URLConnection) connection).setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			//((URLConnection) connection).setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
			//((URLConnection) connection).setRequestProperty("Accept-Language","zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
			
			((URLConnection) connection).setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");
			
			//((URLConnection) connection).setRequestProperty("Host","ebpps.taipower.com.tw");
			//((URLConnection) connection).setRequestProperty("Origin","http://wapp.taipower.com.tw");
			//((URLConnection) connection).setRequestProperty("Referer","http://wapp.taipower.com.tw/naweb/apfiles/Nawp2J2.asp");
			
			//((URLConnection) connection).setRequestProperty("Cache-Control","max-age=0");
			//((URLConnection) connection).setRequestProperty("Connection","keep-alive");
		}
		
		
		if( cookies != null)
		{
			if(https_check)
				((HttpsURLConnection) connection).setRequestProperty("Cookie",cookies);
			else
				((HttpURLConnection) connection).setRequestProperty("Cookie",cookies);
			
			//Log.i("set Cookie : " , cookies ) ;
		}
		
		//JDK HttpUrlConnection override getInputStream() method，urlConnection.getInputStream() connect();
		
		if( method.startsWith("POST") )
		{
			OutputStream output_stream = null;
			DataOutputStream wr = null;
			
			if( method.contains("FILE") )
			{
				final String BOUNDARY   = "==================================";
				final String HYPHENS    = "--";
				final String CRLF       = "\r\n";
				
				if( https_check)
				{	
					((HttpsURLConnection) connection).setRequestMethod("POST");
					((HttpsURLConnection) connection).setReadTimeout(30000);
					//send post request
					((HttpsURLConnection) connection).setDoOutput(true);
					((HttpsURLConnection) connection).setDoInput(true);
					((HttpsURLConnection) connection).setUseCaches(false);
					((HttpsURLConnection) connection).setRequestProperty("Connection", "keep-alive");
					((HttpsURLConnection) connection).setRequestProperty("Charset", "UTF-8");
					// 把Content Type設為multipart/form-data
					// 以及設定Boundary，Boundary很重要!
					// 當你不只一個參數時，Boundary是用來區隔參數的  
					((HttpsURLConnection) connection).setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
					// 下面是開始寫參數
					
					output_stream = ((HttpsURLConnection) connection).getOutputStream(); // connect
					
					String file_name = parameters[1].split(",")[0];
					String file_path = parameters[1].split(",")[1];
					
					String content_disposition = "Content-Disposition: form-data; name=\"image\"; filename=\"" + file_name + "\"";
					String content_type = "Content-Type: image/jpeg";
					
					wr = new DataOutputStream(output_stream);
					wr.writeBytes(HYPHENS + BOUNDARY + CRLF);       // 寫--==================================
					wr.writeBytes(content_disposition + CRLF);  	// 寫(Disposition)
					wr.writeBytes(content_type + CRLF);				// 寫(Content type)
					wr.writeBytes(CRLF);       
					
					FileInputStream file_input_stream = new FileInputStream(new File(file_path));
					wr.write( inputStreamToByteArray(file_input_stream));
					wr.writeBytes(CRLF);
					
					for(String qq : parameters[0].split("&")  )
					{
						//Log.i("qq ",qq);
						String[] qq_array = qq.split("=");
						
						if(qq_array.length > 1)
						{
							wr.writeBytes(HYPHENS + BOUNDARY + CRLF);       // 寫--==================================
							wr.writeBytes("Content-Disposition: form-data; name=\"" + qq_array[0] + "\"" + CRLF);
							wr.writeBytes(CRLF);
							wr.writeBytes(qq_array[1]);
							wr.writeBytes(CRLF);
						}
					}
					
					wr.writeBytes(HYPHENS + BOUNDARY + HYPHENS);    // (結束)寫--==================================--      
					
					file_input_stream.close();
				}
				else
				{	
					((HttpURLConnection) connection).setRequestMethod("POST");
					((HttpURLConnection) connection).setReadTimeout(30000);
					((HttpURLConnection) connection).setDoOutput(true);
					((HttpURLConnection) connection).setDoInput(true);
					((HttpURLConnection) connection).setUseCaches(false);
					output_stream = ((HttpURLConnection) connection).getOutputStream();
				}
			}
			else	
			{
				//add request header		
				if(https_check)
				{	
					((HttpsURLConnection) connection).setRequestMethod("POST");
					((HttpsURLConnection) connection).setReadTimeout(30000);
					//send post request
					((HttpsURLConnection) connection).setDoOutput(true);
					((HttpsURLConnection) connection).setUseCaches(false);
					output_stream = ((HttpsURLConnection) connection).getOutputStream(); // connect
				}
				else
				{	
					((HttpURLConnection) connection).setRequestMethod("POST");
					((HttpURLConnection) connection).setReadTimeout(30000);
					((HttpURLConnection) connection).setDoOutput(true);
					((HttpURLConnection) connection).setUseCaches(false);
					output_stream = ((HttpURLConnection) connection).getOutputStream();
				}
				
				wr = new DataOutputStream(output_stream);
				wr.writeBytes(parameters[0]);
			}
			
			//connect.setDoInput(true); //for upload data
			//String parameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345"; //example
			//DataOutputStream wr = new DataOutputStream(output_stream);
			//wr.writeBytes(parameters);
			wr.flush();
			wr.close();
			
			//Log.i("Post parameters : " , parameters);
		}
		else
		{
			if(https_check)
			{	
				((HttpsURLConnection) connection).setRequestMethod("GET");
				((HttpsURLConnection) connection).setReadTimeout(120000);
				//send post request
				//((HttpsURLConnection) connection).setDoOutput(true);
				((HttpsURLConnection) connection).connect();
			}
			else
			{	
				((HttpURLConnection) connection).setRequestMethod("GET");
				((HttpURLConnection) connection).setReadTimeout(120000);
				//((HttpURLConnection) connection).setDoOutput(true);
				((HttpURLConnection) connection).connect();
			}
		}
		
		
		Log.i("All fields :" ,((URLConnection) connection).getHeaderFields().toString() );
		
		if( cookies == null )
		{	
			if(https_check)
				cookies = ((HttpsURLConnection) connection).getHeaderField("Set-Cookie");
			else
				cookies = ((HttpURLConnection) connection).getHeaderField("Set-Cookie");
			
			//save cookie
			//cookies_value = cookies;
			
			//if( cookies != null )
			//	Log.i("get Cookies : " , cookies);	
		}
		
		if(https_check)
			CONNECTION_STATE_CODE = ((HttpsURLConnection) connection).getResponseCode();
		else
			CONNECTION_STATE_CODE = ((HttpURLConnection) connection).getResponseCode();
		
		Log.i("Response Code : " , String.valueOf(CONNECTION_STATE_CODE) );
		
		while(CONNECTION_STATE_CODE == 302 || CONNECTION_STATE_CODE == 301 || CONNECTION_STATE_CODE == 304)
		{
			String redirect_url = ((URLConnection) connection).getHeaderField("Location");
			redirect_url = url_string.substring(0,url_string.lastIndexOf("/") + 1 ) + redirect_url;
			
			url = new URL(redirect_url);
			//Log.i("Sending 'POST' request to URL : " , redirect_url);
			
			if(https_check)
			{	
				connection = (HttpsURLConnection) url.openConnection(); 
				((HttpsURLConnection) connection).setInstanceFollowRedirects(false); 
				//HttpsURLConnection.setFollowRedirects(false);
			}
			else	
			{	
				connection = (HttpURLConnection) url.openConnection(); 
				((HttpURLConnection) connection).setInstanceFollowRedirects(false); 
				//HttpURLConnection.setFollowRedirects(false);
			}
			
			if( cookies != null)
			{
				if(https_check)
					((HttpsURLConnection) connection).setRequestProperty("Cookie",cookies);
				else
					((HttpURLConnection) connection).setRequestProperty("Cookie",cookies);
			}
			
			if(https_check)
				CONNECTION_STATE_CODE = ((HttpsURLConnection) connection).getResponseCode();
			else
				CONNECTION_STATE_CODE = ((HttpURLConnection) connection).getResponseCode();
			
			Log.i("Response Code : " ,String.valueOf( CONNECTION_STATE_CODE) );
		}	
		
		if( CONNECTION_STATE_CODE == 200 )
		{	
			InputStream input_stream = ((URLConnection) connection).getInputStream();
			
			http_response = inputStreamToByteArray(input_stream);	
		}
		else
			return null;
		
		return http_response;
	}
	
	
	public static byte[] inputStreamToByteArray( InputStream input_stream) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(input_stream.available());
		
		byte[] buffer = new byte[1024]; // 512 ??  1024 ??
		int count;
	 
		while ((count = input_stream.read(buffer)) != -1 )
		{
			bos.write(buffer, 0, count);
		}
	 
		bos.flush();
		bos.close();
		input_stream.close();
		
		return bos.toByteArray();
	}
	
	@SuppressLint("TrulyRandom")
	private static HttpsURLConnection disableCertificateALLHostName(HttpsURLConnection connection) throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException 
	{
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] 
		{	
			new X509TrustManager() 
			{
				public java.security.cert.X509Certificate[] getAcceptedIssuers() 
				{
					return null;
				}	
            
				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) 
				{
					
				}
            
				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) 
				{
            
				}
			
			}
		};

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		connection.setSSLSocketFactory(sc.getSocketFactory());
		
		return connection;
	}
	
	public static String urlencodeLikeBrowser(String input)
	{
		//big5 low byte 0x41~0x5A convert to A~Z , 0x61 ~ 0x7A convert to a~z
		
		int start_upper_litter = 0x40; 
		int end_lower_litter = 0x7E;
		
		String target = "";
		
		for(int i = start_upper_litter ; i <= end_lower_litter; i ++)
		{	
			target = "%" + String.format("%X", i);
			input = input.replace(target, String.valueOf((char) i));
		}
		
		return input; 
	}
	
	
	public static String urlencodeChinese(String input) throws UnsupportedEncodingException
	{
		String temp = "";
		
		for(int i = 0 ; i < input.length() ; i++ )
		{
			//Log.i("byte:X", "%" + String.format("%X" , (int) input.charAt(i) ));
			//Log.i("byte:C", "%" + String.format("%c" , input.charAt(i) ));
		
			if( (int) input.charAt(i) > 0xFF )
			{
				temp += URLEncoder.encode(input.substring(i, i+1),"utf-8");
				
			}
			else
				temp += input.substring(i, i+1);
		}
		
		Log.i("temp:", temp);
		return temp;
	}
	
	
}