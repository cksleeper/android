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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
	private static ArrayList<String> cookie_string_list;
	/*
	 *  Create URLConnection class connection to replace apache http class( was be deprecated after API 22)
	 * 
	 */
	
	private CookieManager cookie_manager;
	private String cookie_string;
	private String url_string;
	private String method;
	private String[] parameters;
	private boolean disable_ssl_status = true;;
	private boolean cookie_status;
	private boolean follow_redirect;
	private HashMap<String,String> headers_map;
	
	public static final int HTTP_OK = HttpURLConnection.HTTP_OK;
	public static final int HTTP_FORBIDDEN = HttpURLConnection.HTTP_FORBIDDEN;
	public static final int HTTP_NOT_FOUND = HttpURLConnection.HTTP_NOT_FOUND;
	public int HTTP_STATUS;
	
	public HttpConnectResponse()
	{
		/*
		if( cookie_manager == null )
		{	
			cookie_manager = new CookieManager();
			CookieHandler.setDefault(cookie_manager);
			Log.i("cookie_manager","set");
		}
		*/
	}
	
	public void setUrl(String url_string)
	{
		this.url_string = url_string;
	}
	
	public void setConnectMethod(String method, String[] parameters)
	{
		this.method = method.toUpperCase(Locale.US);
		
		if( this.method.equals("GET")  )
			this.parameters = null;
		else
			this.parameters = parameters;
	}
	
	public void setConnectMethod(String method)
	{
		this.setConnectMethod(method, null);
	}
	
	public void disableCertificate(boolean disable_ssl_status)
	{
		this.disable_ssl_status = disable_ssl_status;
	}
	
	public void setCookieStatus(boolean cookie_status)
	{
		this.cookie_status = cookie_status;
	}
	
	public void setRedirectStatus(boolean follow_redirect)
	{
		this.follow_redirect = follow_redirect;
	}
	
	public void setConnectionRequestProperty(String headers, String value)
	{
		if( headers_map != null )
			headers_map = new HashMap<String,String>();
		
		this.headers_map.put(headers, value);
	}
	
	public void setConnectionRequestProperty(HashMap<String ,String> headers_map)
	{
		this.headers_map = headers_map;
	}
	
	public CookieManager getCookieManager()
	{
		return cookie_manager;
	}
	
	public void setCookieManager(CookieManager cookie_manager)
	{
		this.cookie_manager = cookie_manager;
	}
	
	public String getCookie()
	{
		return cookie_string;
	}
	
	public void setCookie(String cookie)
	{
		if( cookie != null && cookie.length() > 0)
		{
			this.cookie_string = cookie;
			this.cookie_status = true;
		}
		else
		{
			this.cookie_string = null;
			this.cookie_status = false;
		}
	}
	
	public InputStream startConnectAndResponseInputStream() throws SSLHandshakeException, IOException, URISyntaxException
	{	
		if(!cookie_status)
			cookie_string = null;
		
		URL url = new URL(url_string);
		URLConnection connection;
		
		boolean https_check = url_string.startsWith("https");
		
		if( https_check )
		{	
			if(disable_ssl_status)
				connection = disableCertificateALLHostName(((HttpsURLConnection) url.openConnection()));
			else
				connection = (HttpsURLConnection) url.openConnection();
			
			((HttpsURLConnection) connection).setInstanceFollowRedirects(follow_redirect); 
		}
		else	
		{	
			connection = (HttpURLConnection) url.openConnection();
			((HttpURLConnection) connection).setInstanceFollowRedirects(follow_redirect); 
		}
		
		if( headers_map != null)
		{	
			Set<String> headers_set = headers_map.keySet();
			for(String key : headers_set )
			connection.setRequestProperty(key, headers_map.get(key));	
		}
		//((URLConnection) connection).setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		//((URLConnection) connection).setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
		//((URLConnection) connection).setRequestProperty("Accept-Language","zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");	
		//((URLConnection) connection).setRequestProperty("Host","ebpps.taipower.com.tw");
		//((URLConnection) connection).setRequestProperty("Origin","http://wapp.taipower.com.tw");
		//((URLConnection) connection).setRequestProperty("Referer","http://wapp.taipower.com.tw/naweb/apfiles/Nawp2J2.asp");
		//((URLConnection) connection).setRequestProperty("Cache-Control","max-age=0");
		//((URLConnection) connection).setRequestProperty("Connection","keep-alive");
		connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36 CKSHttpConnect");
		
		if( cookie_string != null)
		{	
			connection.setRequestProperty("Cookie",cookie_string);
			//Log.i("set cookie",cookie_string);
		}
		
		//JDK HttpUrlConnection override getInputStream() method，urlConnection.getInputStream() connect();
		
		if( method.startsWith("POST") )
		{
			OutputStream output_stream = null;
			DataOutputStream wr = null;
					
			//add request header		
			if(https_check)
				((HttpsURLConnection) connection).setRequestMethod("POST");
			else
				((HttpURLConnection) connection).setRequestMethod("POST");
			
			connection.setReadTimeout(30000);
			//send post request
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			output_stream = connection.getOutputStream(); // connect
			
			wr = new DataOutputStream(output_stream);
			wr.writeBytes(parameters[0]);
					
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
				((HttpsURLConnection) connection).setRequestMethod("GET");
			else
				((HttpURLConnection) connection).setRequestMethod("GET");
		
			connection.setReadTimeout(30000);
			//send post request
			connection.connect();
		}
		
		if( cookie_string == null )
		{	
			cookie_string = connection.getHeaderField("Set-Cookie");
			//save cookie
			//cookies_value = cookies;
			//if( cookie_string != null )
			//	Log.i("get Cookies : " , cookie_string);	
		}
		
		if( cookie_string != null )
			Log.i("get Cookies : " , cookie_string);	
		
		if(https_check)
			HTTP_STATUS = ((HttpsURLConnection) connection).getResponseCode();
		else
			HTTP_STATUS = ((HttpURLConnection) connection).getResponseCode();
		
		Log.i("Response Code : " , String.valueOf(HTTP_STATUS) );
		
		while( (HTTP_STATUS >= HttpURLConnection.HTTP_MOVED_PERM) && (HTTP_STATUS <= HttpURLConnection.HTTP_NOT_MODIFIED) )
		{
			String redirect_url = connection.getHeaderField("Location");
			redirect_url = url_string.substring(0,url_string.indexOf("/",8) ) + redirect_url;
			
			url = new URL(redirect_url);
			//Log.i("Sending 'POST' request to URL : " , redirect_url);
			
			if(disable_ssl_status)
				connection = disableCertificateALLHostName(((HttpsURLConnection) url.openConnection()));
			else
				connection = (HttpsURLConnection) url.openConnection();
			
			
			if(https_check)	
				((HttpsURLConnection) connection).setInstanceFollowRedirects(false); 
			else	
				((HttpURLConnection) connection).setInstanceFollowRedirects(false); 
			
			if( cookie_string != null)
				connection.setRequestProperty("Cookie",cookie_string);
			
			if(https_check)
				HTTP_STATUS = ((HttpsURLConnection) connection).getResponseCode();
			else
				HTTP_STATUS = ((HttpURLConnection) connection).getResponseCode();
			
			Log.i("Response Code : " ,String.valueOf( HTTP_STATUS) );
		}	
		
		if( HTTP_STATUS == HttpURLConnection.HTTP_OK )
		{	
			return connection.getInputStream();
		}
		else
			return null;
	}
	
	public byte[] startConnectAndResponseByteArray() throws SSLHandshakeException, IOException, URISyntaxException
	{
		return inputStreamToByteArray(this.startConnectAndResponseInputStream());
	}
	
	
	
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
		
		
		if(cookie_state == COOKIE_CLEAR )
		{	
			cookies = null;
			cookie_string_list = null;	
			Log.i("cookie","clear cookie");
		}
		
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
			
			connection = disableCertificateALLHostName(((HttpsURLConnection) connection));
			
			if(http_redirect)
				((HttpsURLConnection) connection).setInstanceFollowRedirects(true); 
			else
				((HttpsURLConnection) connection).setInstanceFollowRedirects(false);
			//HttpsURLConnection.setFollowRedirects(true);
		}
		else	
		{	
			connection = (HttpURLConnection) url.openConnection(); 
			
			if(http_redirect)
				((HttpURLConnection) connection).setInstanceFollowRedirects(true); 
			else
				((HttpURLConnection) connection).setInstanceFollowRedirects(false);
			
			//HttpURLConnection.setFollowRedirects(true);
		}
			
		//Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
		//Accept-Encoding:gzip, deflate
		//Accept-Language:zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2
		//Cache-Control:max-age=0	
		//Connection:keep-alive
		//Content-Length:745
		//Content-Type:application/x-www-form-urlencoded
		//Cookie:__utma=4361142.1055650384.1421299797.1434792678.1434938224.71; __AntiXsrfToken=45e2caf2e8ed43d19d3a057ccab73258; ASP.NET_SessionId=cnmkki1zvn2cnrw5gphnkmia; style=Small Text; _ga=GA1.3.1055650384.1421299797
		//Host:einvoice.taipower.com.tw
		//Origin:https://einvoice.taipower.com.tw
		//Referer:https://einvoice.taipower.com.tw/einvoice/search_1
		//Upgrade-Insecure-Requests:1
		//User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36
		 
		connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36 CKSHttpConnect");
		//connection.setRequestProperty("Origin","https://einvoice.taipower.com.tw");
		//connection.setRequestProperty("Referer","https://einvoice.taipower.com.tw/einvoice/search_1");
		//connection.setRequestProperty("Host","einvoice.taipower.com.tw");
		//connection.setRequestProperty("Upgrade-Insecure-Requests","1");
		
		/*
		if( cookies != null)
		{
			connection.setRequestProperty("Cookie",cookies);
			Log.i("set Cookie : " , cookies ) ;
		}
		*/
		
		if( cookie_string_list != null )
		{
			String cookie = "";
			
			for(String qq : cookie_string_list)
				cookie += qq + "; ";
			
			cookie += "path=/; HttpOnly";
			
			connection.setRequestProperty("Cookie",cookie);
			Log.i("set Cookie : ", cookie ) ;
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
					wr.writeBytes(HYPHENS + BOUNDARY + CRLF);       // write down--==================================
					wr.writeBytes(content_disposition + CRLF);  	// write down(Disposition)
					wr.writeBytes(content_type + CRLF);				// write down(Content type)
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
							wr.writeBytes(HYPHENS + BOUNDARY + CRLF);       // write down--==================================
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
		
		//for(int i = 0;(connection.getHeaderFieldKey(i) != null) ;i++)
			//Log.i("All fields :" ,connection.getHeaderFields().toString() );
		/*
		if( cookies == null )
		{	
			cookies = connection.getHeaderField("Set-Cookie");
			
			//save cookie
			//cookies_value = cookies;
			
			if( cookies != null )
				Log.i("get Cookies : " , cookies);
		}
		*/
		
		
		if( cookie_string_list == null )
		{
			cookie_string_list = new ArrayList<String>();
			if( connection.getHeaderField("Set-Cookie") != null && connection.getHeaderField("Set-Cookie").contains(";") )
				cookie_string_list.add(connection.getHeaderField("Set-Cookie").split(";")[0]);
			
			Log.i("add new cookie : ", cookie_string_list.toString());
		}	
		
		String cookie_temp = connection.getHeaderField("Set-Cookie");
			
		if( cookie_temp != null && !cookie_string_list.contains(cookie_temp.split(";")[0]))
		{
			Log.i("temp cookie",cookie_temp);
			cookie_string_list.add(cookie_temp.split(";")[0]);
			
			//cookie_string_list.add("__utma=4361142.1055650384.1421299797.1434792678.1434938224.71");
			//cookie_string_list.add("style=Small Text");
			//cookie_string_list.add("_ga=GA1.3.1055650384.1421299797");
			Log.i("add new cookie : " , cookie_string_list.toString());
		}
		else
			Log.i("add new cookie : " , "cookie are the same ,not be to add");
		
		if(https_check)
			CONNECTION_STATE_CODE = ((HttpsURLConnection) connection).getResponseCode();
		else
			CONNECTION_STATE_CODE = ((HttpURLConnection) connection).getResponseCode();
		
		Log.i("Response Code : " , String.valueOf(CONNECTION_STATE_CODE) );
		
		while(CONNECTION_STATE_CODE == 302 || CONNECTION_STATE_CODE == 301 || CONNECTION_STATE_CODE == 304)
		{
			String redirect_url = connection.getHeaderField("Location");
			redirect_url = url_string.substring(0,url_string.indexOf("/",8) ) + redirect_url;
			
			url = new URL(redirect_url);
			Log.i("Sending 'GET' request to URL : " , redirect_url);
			
			if(https_check)
			{	
				connection = disableCertificateALLHostName(((HttpsURLConnection) url.openConnection()));
				
				((HttpsURLConnection) connection).setInstanceFollowRedirects(false); 
				//HttpsURLConnection.setFollowRedirects(false);
			}
			else	
			{	
				connection = url.openConnection(); 
				((HttpURLConnection) connection).setInstanceFollowRedirects(false); 
				//HttpURLConnection.setFollowRedirects(false);
			}
			/*
			if( cookies != null)
			{
				connection.setRequestProperty("Cookie",cookies);
				Log.i("set Cookie : " , cookies ) ;
			}
			*/
			
			if( cookie_string_list != null )
			{
				String cookie = "";
				
				for(String qq : cookie_string_list)
					cookie += qq + "; ";
				
				cookie += "path=/; HttpOnly";
				
				connection.setRequestProperty("Cookie",cookie);
				Log.i("set Cookie : " , cookie ) ;
			}
			
			
			if(https_check)
				CONNECTION_STATE_CODE = ((HttpsURLConnection) connection).getResponseCode();
			else
				CONNECTION_STATE_CODE = ((HttpURLConnection) connection).getResponseCode();
			
			Log.i("Response Code : " ,String.valueOf( CONNECTION_STATE_CODE) );
		}	
		
		if( CONNECTION_STATE_CODE == 200 )
		{	
			InputStream input_stream = connection.getInputStream();
			
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
	private static HttpsURLConnection disableCertificateALLHostName(HttpsURLConnection connection)
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
		try
		{
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			connection.setSSLSocketFactory(sc.getSocketFactory());
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