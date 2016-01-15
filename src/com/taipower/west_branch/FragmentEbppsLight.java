package com.taipower.west_branch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.SHA1Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;


public class FragmentEbppsLight extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private FragmentEbppsLight this_class;
	
	private float scale;
	
	private View current_view;
	
	private SharedPreferences setting; 
	private long now_time_subtract;
	
	private String save_ebpps_account_hash;
	private String save_ebpps_password_hash;
	
	private boolean auto_login = true;
	private boolean nonauto_login = false;
	
	private boolean auto_login_switch; 
	
	public FragmentEbppsLight()
	{
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		this.this_class = this;
		
		DmInfor dm_infor = new DmInfor(app_activity, app_context);
		scale = dm_infor.scale; 
		
		setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
		
		current_view = inflater.inflate( R.layout.fragment_ebpps_light, container, false);  
	
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_ebpps);
		
		ImageButton menu_button = (ImageButton) findViewById(R.id.title_bar_menu_button);
		menu_button.setVisibility(View.INVISIBLE);
		menu_button.setOnClickListener(null);
		
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(	new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		//
        		//if( ! logout_seeion_id.equals("") )
        		{
        			//String logout_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=logout_action&authsid=";
        			//logout_url += logout_seeion_id;
        			//new LoadingDataAsyncTask().execute("logout_tag",logout_url);
        		}
        		//else
        		//	finish();
        		FragmentManager fm = app_activity.getFragmentManager();
        		FragmentTransaction ft = fm.beginTransaction();
        		Fragment fragment = new MainPageFragment();
        		ft.replace(R.id.fragment_content, fragment, "main_page").commit();
        	}
       	});
        
        auto_login_switch = setting.getBoolean("auto_login_switch", false);
        
        ToggleButton auto_login_button = (ToggleButton) findViewById(R.id.auto_login_switch);
        auto_login_button.setChecked(auto_login_switch);
        auto_login_button.setOnCheckedChangeListener( new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				// TODO Auto-generated method stub
				auto_login_switch = isChecked;
				setting.edit().putBoolean("auto_login_switch", auto_login_switch).commit();
				
				if(!auto_login_switch)
				{
					setting.edit().putString("ebpps_account", "").putString("ebpps_password", "").commit();
				}
			}
        });
        
        Button login_button = (Button) findViewById(R.id.login_ebpps);
        login_button.setOnClickListener(button_on_click_listener);
        
        Button registr_ebpps_button = (Button) findViewById(R.id.registr_ebpps);
        registr_ebpps_button.setOnClickListener(button_on_click_listener);
        
        Button forgot_password_button = (Button) findViewById(R.id.forgot_password);
        forgot_password_button.setOnClickListener(button_on_click_listener);
		
        Button ebpps_privacy_policy_button = (Button) findViewById(R.id.ebpps_privacy_policy);
        ebpps_privacy_policy_button.setOnClickListener(button_on_click_listener);
        
        Button ebpps_security_policy_button = (Button) findViewById(R.id.ebpps_security_policy);
        ebpps_security_policy_button.setOnClickListener(button_on_click_listener);
        
        ((LinearLayout) findViewById(R.id.ebpps_content)).setVisibility(View.INVISIBLE);
        
        return current_view;
	}
	
	@Override
	public void onViewCreated( View view, Bundle savedInstanceState)
	{
		now_time_subtract = (System.currentTimeMillis() / 1000l) - Integer.valueOf(setting.getString("last_login_time", "0")).longValue();
    	
		Log.i("now_time_subtract : ",String.valueOf(now_time_subtract));
		
		String ebpps_account_hash = setting.getString("ebpps_account", "");
		
		if( now_time_subtract < 900l && auto_login_switch && ebpps_account_hash.length() != 0 )
			this_class.loginEbpps(this_class.auto_login);
	}
	
	private View findViewById(int id)
	{
		View view = current_view.findViewById(id);
		
		if( view == null )
			view = app_activity.findViewById(id);
		
		return view;
	}
	
	
	private View.OnClickListener button_on_click_listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if( v.getId() ==  R.id.login_ebpps)
			{
				TextView ebpps_account_textview = (TextView) findViewById(R.id.ebpps_account);
				
				String ebpps_account = ebpps_account_textview.getText().toString();
				ebpps_account = ebpps_account.toUpperCase(Locale.US);
				ebpps_account_textview.setText(ebpps_account);
				
				String ebpps_password = ((TextView) findViewById(R.id.ebpps_password)).getText().toString();
		        
				String error_message = "";
		        
		        if( !ASaBuLuCheck.idCheck(ebpps_account) )
					error_message += "電子帳單帳號填寫錯誤\n";
				
				if(ebpps_password.length() < 7 )
					error_message += "電子帳單密碼填寫錯誤";
		        
		        if(error_message.equals(""))
		        {
		        	this_class.loginEbpps(this_class.nonauto_login);
		        }
		        else
		        {
		        	AlertDialog.Builder warning_dialog = new AlertDialog.Builder(app_context);
		        	warning_dialog.setTitle("歐！歐歐！！");
		        	warning_dialog.setMessage(error_message);
		        	warning_dialog.setNeutralButton("確定", null);
		        	warning_dialog.show();
		        }
			}
			else
			{
				String url = "";
				
				switch(v.getId())
				{
					case R.id.registr_ebpps:
						url = "https://ebpps.taipower.com.tw/EBPPS/action/register.do?myAction=regExplain";
						break;
					case R.id.forgot_password:
						url = "https://ebpps.taipower.com.tw/EBPPS/NAEBJ02_00007.jsp?layout=forgetPWD";
						break;
					case R.id.ebpps_privacy_policy:
						url = "https://ebpps.taipower.com.tw/EBPPS/pri.htm";
						break;
					case R.id.ebpps_security_policy:
						url = "https://ebpps.taipower.com.tw/EBPPS/action/pri.do?myAction=sec";
				}
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url) );
				app_activity.startActivity(intent);
			}
		}
	};
	
	
	private void loginEbpps(boolean auto_login_tag)
	{
		String ebpps_myAction_password_url;
		String ebpps_myAction_check_PWD_url = "";
		
		if( auto_login_tag && auto_login_switch) 	
       	{ 	
       		String ebpps_account_hash = setting.getString("ebpps_account", "");
            String ebpps_password_hash = setting.getString("ebpps_password", "");    
       
            String ebpps_account_unhash = ebpps_account_hash.substring(0, 1) + new BigInteger(ebpps_account_hash.substring(1)).divide(new BigInteger(Build.SERIAL.replaceAll("[^0-9]+", ""))).toString(); 		
            
            ((TextView) findViewById(R.id.ebpps_account)).setText(ebpps_account_unhash);
	        ((TextView) findViewById(R.id.ebpps_password)).setText(ebpps_account_unhash);
            
            ebpps_myAction_password_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=password&account=" + ebpps_account_unhash;        
            
            ebpps_myAction_check_PWD_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=check_PWD&old_hash=" + ebpps_password_hash + "&words2=HYCAPI_INSTEAD&pkcs=&account=&words=********";
           
            save_ebpps_account_hash = ebpps_account_hash;
            save_ebpps_password_hash = ebpps_password_hash;
            //Log.i("ebpps_account :",ebpps_account);
           	//Log.i("Build.SERIAL :",BigInteger.valueOf(Integer.valueOf(Build.SERIAL.replaceAll("[^0-9]+", ""))).toString());
           	//Log.i("ebpps_account_unhash :",ebpps_account_unhash);
       	}		
		else        	
		{        	
			String ebpps_account = ((TextView) findViewById(R.id.ebpps_account)).getText().toString();
	        String ebpps_password_unhash = ((TextView) findViewById(R.id.ebpps_password)).getText().toString();
			
	        String ebpps_password_hash = SHA1Util.b64_sha1(ebpps_password_unhash);
	        
	        String ebpps_account_hash = ebpps_account.substring(0, 1) + new BigInteger(ebpps_account.substring(1).toString()).multiply(new BigInteger(Build.SERIAL.replaceAll("[^0-9]+",""))).toString();   ;
			
	        save_ebpps_account_hash = ebpps_account_hash;
            save_ebpps_password_hash = ebpps_password_hash;
	        
	        //Log.i("ebpps_account", ebpps_account );
	        //Log.i("ebpps_password_hash", ebpps_password_hash );
	        
			ebpps_myAction_password_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=password&account=" + ebpps_account;        		
                		
            ebpps_myAction_check_PWD_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=check_PWD&old_hash=" + ebpps_password_hash + "&words2=HYCAPI_INSTEAD&pkcs=&account=&words=********";
		}       	
        		
		new LoadingDataAsyncTask().execute("login_tag",ebpps_myAction_password_url, ebpps_myAction_check_PWD_url);
	}
	
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{     
		super.onActivityResult(requestCode, resultCode, data); 
		// get subactivity response
		
		/*
		if (resultCode == Activity.RESULT_OK) 
		{ 
			if(requestCode == 0xFF ) 
			{ 
				if( data != null && data.getStringExtra("result").equals("OK"))
				{
					now_time_subtract = (System.currentTimeMillis() / 1000l) - Integer.valueOf(setting.getString("last_login_time", "0")).longValue();
					
					Log.i("now_time_subtract : ",String.valueOf(now_time_subtract));
					
			        if(  now_time_subtract < 900l )
			        {
			        	String url = "https://ebpps.taipower.com.tw/EBPPS/action/bill.do?myAction=queryBill&isAllInfo=yes";
			        	new LoadingDataAsyncTask().execute("on_resume_tag",url);
			        } 
				}
				else
					app_activity.finish();		
			}
		}
		*/
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		/*
		String ebpps_account = setting.getString("ebpps_account", "");
        
        if( !ebpps_account.equals("") )
        {
        	String url = "https://ebpps.taipower.com.tw/EBPPS/action/bill.do?myAction=queryBill";
        	new LoadingDataAsyncTask().execute("on_resume",url);
        }
        */
	}
	
	//CookieStore cookieStore_globe = null;
	
	private String logout_seeion_id = "";
	
	//List<Cookie> cookie_list ;
	
	private ArrayList<String> result_array_list ;
	private ArrayList<String> result_array_electric_list ;
	private ArrayList<String> detail_result_array_list ;
	private ArrayList<String> detail_check_code_array_list;
	
	private HashMap<String,String> history_data_url_map;
	private HashMap<String,String> history_data_detail_map;
	
	private HashMap<String,String> pdf_location_map;
	private HashMap<String,String> pdf_pay_date_map;
	private HashMap<String,String> pdf_payment_map;
	
	private HashMap<String,String> bill_detail_url_map ;
	
	private HttpConnectResponse connection;
	
	private class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> 
    {	
		@Override
		protected void onPreExecute ()
    	{
    		super.onPreExecute();
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    	}
		
		String pdf_name = "";
		String session_tag = "";
		byte[] response_data ;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			int return_value = 0;
			//HttpClient httpClient = new DefaultHttpClient();
			
			//if( cookieStore_globe == null )
			//	cookieStore_globe = new BasicCookieStore();
							
			//HttpContext httpContext = new BasicHttpContext();
			
			//httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore_globe);
						
			session_tag = params[0];
			
			if( connection == null )
				connection = new HttpConnectResponse();
			
			try
			{
				if( session_tag.startsWith("read_detail") || session_tag.startsWith("read_history_tag"))
				{
					publishProgress(3);
								
					//response_data = HttpConnectResponse.apacheConnection(params[1], "get", null, HTTP.ASCII , HttpConnectResponse.COOKIE_KEEP);
					//response_data = HttpConnectResponse.onOpenConnection(params[1], "get", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
        			
					connection.setUrl(params[1]);
					connection.setConnectMethod("GET", null);
					connection.disableCertificate(true);
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data = connection.startConnectAndResponseByteArray();
					
					return_value = 0;
				}
			
				if( session_tag.equals("download_pdf_tag") )
				{
					//download pdf action
					publishProgress(2);
								
					//response_data = HttpConnectResponse.apacheConnection(params[1], "get", null, HTTP.ASCII , HttpConnectResponse.COOKIE_KEEP);
					//response_data = HttpConnectResponse.onOpenConnection(params[1], "get", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
        			
					connection.setUrl(params[1]);
					connection.setConnectMethod("GET", null);
					connection.disableCertificate(true);
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data = connection.startConnectAndResponseByteArray();
					
	                pdf_name = params[2] + ".pdf"; 
					
	                return_value = 0;
	            }
			
				if( session_tag.equals("logout_tag") )
				{
					publishProgress(1);
					//logout action
				
					//HttpConnectResponse.apacheConnection(params[1], "get", null, HTTP.ASCII , HttpConnectResponse.COOKIE_KEEP);
					HttpConnectResponse.onOpenConnection(params[1], "get", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
					
					return_value = 0;
				}
			
				if( session_tag.equals("login_tag") ) 
				{
					publishProgress(0);
				    
					//response_data = HttpConnectResponse.apacheConnection(params[1], "get", null, HTTP.ASCII , HttpConnectResponse.COOKIE_KEEP);
					//response_data = HttpConnectResponse.onOpenConnection(params[1], "get", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
        			
					connection.setUrl(params[1]);
					connection.setConnectMethod("GET", null);
					connection.disableCertificate(true);
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data = connection.startConnectAndResponseByteArray();
					
        			Document lgoin_account_check = Jsoup.parse(new String(response_data,"UTF-8"));
        			
        			Element error_alert_javascript = lgoin_account_check.select("script").last();
        			
        			String error_alert_string = error_alert_javascript.toString().replace("<script ", "");
        			
        			//Log.i("error_alert_string",error_alert_string);
        			
        			if( error_alert_string.contains("alert") )
        				return_value = 8;
        			else
        			{
        				//response_data = HttpConnectResponse.apacheConnection(params[2], "get", null, HTTP.ASCII , HttpConnectResponse.COOKIE_KEEP);
        				//response_data = HttpConnectResponse.onOpenConnection(params[2], "get", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
            			
        				connection.setUrl(params[2]);
    					connection.setConnectMethod("GET", null);
    					connection.disableCertificate(true);
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data = connection.startConnectAndResponseByteArray();
        				
        				String login_content = new String(response_data, "UTF-8");
        				
	            		Document login_layer1 = Jsoup.parse(login_content);
	            			
	            		Element error_alert_javascript_PWD = login_layer1.select("script").last();
	            			
	            		String error_alert_string_1 = error_alert_javascript_PWD.toString().replace("<script ", "");
	            			
	            		if( error_alert_string_1.contains("alert") )
	            			return_value = 9;
	            		else
	            		{
	            			//cookie_list = cookieStore_globe.getCookies();
	            			//logout_seeion_id = cookie_list.get(1).getValue();
	            			logout_seeion_id = HttpConnectResponse.COOKIES_VALUE;
	            			
	            			//login_content = EntityUtils.toString(httpResponse_check_PWD.getEntity());
	            			//login_content = new String(response_data, "UTF-8");	            		
	            		}
	            	}		
				}	
			}
			/*
			catch ( NetworkOnMainThreadException e)
            {
            	//Toast.makeText(webview_content_test.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            	Log.e("err",e.getMessage().toString());
            	e.printStackTrace();
            	return 2;
            }
            */
    		catch(ArrayIndexOutOfBoundsException e)
    		{
    			e.printStackTrace();
    			return 3;
    		}
			/*catch (ClientProtocolException e)
            {
            	Log.e("err",e.getMessage().toString());
            	e.printStackTrace();
            	return 4;
            }*/ 
            catch (IOException e)
            {
            	Log.e("err",e.getMessage().toString());
            	e.printStackTrace();
            	return 5;
            }
            catch (Exception e)
            {
            	//Log.e("err",e.getMessage().toString());
            	e.printStackTrace();
            	return 6;
            }
			
			Log.i("return value :",""+return_value);
			
			return return_value;
		}
		
		//Dialog process_persent = null;
		Dialog process_persent_pointr = null;
		
		@Override
		protected void onProgressUpdate(Integer... progress) 
        {
			super.onProgressUpdate(progress);
        	String message = ""; 
			
        	if( progress[0] == 2 )
            	message = "下載中";
        	if( progress[0] == 0 )
        		message = "登入中";
        	if( progress[0] == 1 )
        		message = "登出中";
        	if( progress[0] == 3 )
        		message = "讀取中";
        		
        	process_persent_pointr = CreateLoadingDialog.createLoadingDialog(app_context, message , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NONCANCELABLE);
        }
		
		ArrayList<String[]> response_result_list;
		

		
		@Override
        protected void onPostExecute(Integer result) 
        {
			if( result ==  0  )
        	{
				String response_content = "";
				Document document = null;
				
				if( session_tag.startsWith("read_detail") )
				{	
					try 
					{
						response_content = new String(response_data, "UTF-8");
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	            	
				
					document = Jsoup.parse(response_content);
					Elements detail_content_basic = document.select("table[summary=排版表格:本期帳單]").select("table[cellpadding=3]").select("td[width=33%]:not(:has(table))");
					
					if( session_tag.startsWith("read_detail") && detail_content_basic != null)
					{	
						detail_result_array_list = new ArrayList<String>(); 
							
						//get detail check code
						Elements detail_check_code = document.select("table[summary=排版表格:本期帳單]").select("table[cellpadding=2]");
							
						if( !detail_check_code.isEmpty() && session_tag.contains("check_code") )
						{
							detail_check_code_array_list = new ArrayList<String>();
								
							detail_check_code_array_list.add("====網路/ATM 繳費資料====");
															
							String detail_check_code_text = detail_check_code.text().toString();
														
							String[] temp_check_code = detail_check_code_text.split(" ");
								
							for(int i = 0 ; i < 4 ; i++ )
								detail_check_code_array_list.add(temp_check_code[i*2+1] + ":" + temp_check_code[i+8]);    
						}
							
						//get detail content basic data
						//Elements detail_content_basic = document.select("table[summary=排版表格:本期帳單]").select("table[cellpadding=3]").select("td[width=33%]:not(:has(table))");
																		
						String detail_content_basic_text = detail_content_basic.text().toString();
						
						String[] temp_detal_basic = detail_content_basic_text.toString().split(" ");
													
						detail_result_array_list.add("======用電資料明細======");
							
						for(int i = 0 ; i <temp_detal_basic.length ; i++ )
						{	
							temp_detal_basic[i] = temp_detal_basic[i].replace("：", ":\n");
							detail_result_array_list.add(temp_detal_basic[i]);
						}
							
						//get detail content bill data
						Elements detail_content_bill = document.select("table[summary=排版表格:本期帳單]").select("table[cellpadding=3]").select("table[width=95%]");
							
						String detail_content_bill_text = detail_content_bill.text().toString();
						
						String[] temp_detal_bill = detail_content_bill_text.toString().split(" ");
													
						detail_result_array_list.add("======計費明細======");
							
						/*
						 * 原始格式 String[] {"123:" , "456"}
						 * 合併
						 * 
						 * 
						 */
						for(int i = 0 ; i <(temp_detal_bill.length) / 2 ; i++ )
							temp_detal_bill[i * 2] = temp_detal_bill[ i * 2 ].replace("：", ":\n");
								
						for(int i = 0 ; i <(temp_detal_bill.length) /2 ; i++ )
							detail_result_array_list.add(temp_detal_bill[i*2]+temp_detal_bill[i*2 + 1]);
						
						ArrayAdapter detail_adapter;
						
						if( session_tag.contains("code") )
							detail_adapter = new ArrayAdapter( app_context , android.R.layout.simple_list_item_1, detail_check_code_array_list);
						else
							detail_adapter = new ArrayAdapter( app_context , android.R.layout.simple_list_item_1, detail_result_array_list);
						
						new AlertDialog.Builder(app_context).setAdapter(detail_adapter, null).setPositiveButton(android.R.string.ok, null).show();
					}
					else
					{
						((LinearLayout) findViewById(R.id.login_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(361 * scale)));
	                    ((LinearLayout) findViewById(R.id.ebpps_content)).setVisibility(View.INVISIBLE);
						new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage("連線逾時，請重新登入").setNeutralButton("確定", null).show();
					}	
				}
				
				if( session_tag.startsWith("read_history") )
				{	
					try 
					{
						response_content = new String(response_data, "UTF-8");
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	            	
					
					
					document = Jsoup.parse(response_content);
					Elements history_content = document.select("input[name=custInfoCSVFormat]");
					
					if( history_content != null)
					{	
						Elements history_data_url_elements = document.select("a[href]");
						
						history_data_detail_map = new HashMap<String,String>();
						history_data_detail_map.clear();
						
						for(Element url : history_data_url_elements)
						{
							String url_string = url.attr("href");
							
							if( url_string.contains("/EBPPS/action/bill.do?myAction=billDetail") )
							{
								int coll_date_index = url_string.indexOf("collDate=");
								String coll_date = url_string.substring(coll_date_index + 9, coll_date_index + 9 + 5);
								
								history_data_detail_map.put(coll_date, "https://ebpps.taipower.com.tw" + url_string);		
							}
						}
						
						final ArrayList<String> history_array_list = new ArrayList<String>();
						
						String[] row = history_content.val().split("\n");
						
						for(int i = row.length - 1; i > -1 ; i--)
						{
							String[] columns = row[i].split(",");
							
							if( i > 0)
								history_array_list.add(columns[2]+","+columns[3]+","+columns[4]);
							
							//Log.i("columns[2],columns[3],columns[4] ", columns[2]+ " " +columns[3]+ " " +columns[4]);
						}
						
						final LinearLayout history_data_layout = (LinearLayout) app_activity.getLayoutInflater().inflate(R.layout.fragment_ebpps_light_history_data_layout, null, false);
						history_data_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
						
						DrawGraphy draw_graphy = new DrawGraphy( app_context, history_array_list, -1);
						
						//DmInfor dm = new DmInfor(app_activity,app_context);
						LinearLayout ebpps_history_data_graphy = (LinearLayout) history_data_layout.findViewById(R.id.ebpps_history_data_graphy);
						//ebpps_history_data_graphy.setLayoutParams(new FrameLayout.LayoutParams(dm.v_width, (int) dm.scale * 180));
						ebpps_history_data_graphy.addView(draw_graphy);
						
						EbppsHistoryDataItemAdapter history_adapter = new EbppsHistoryDataItemAdapter( app_context, android.R.layout.simple_list_item_1, history_array_list.toArray(new String[history_array_list.size()]));						
						ListView history_list_view = (ListView) history_data_layout.findViewById(R.id.ebpps_history_data_list_view);
						history_list_view.setAdapter(history_adapter);
						history_list_view.setOnItemClickListener(new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
							{
								// TODO Auto-generated method stub
								DrawGraphy draw_graphy = new DrawGraphy(app_context,history_array_list, position);
								
								LinearLayout ebpps_history_data_graphy = (LinearLayout) history_data_layout.findViewById(R.id.ebpps_history_data_graphy);
								ebpps_history_data_graphy.removeAllViews();
								ebpps_history_data_graphy.addView(draw_graphy);
							}	
						});
						
						
						final Dialog history_dialog = new Dialog(app_context);
						history_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						history_dialog.setContentView(history_data_layout,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
						history_dialog.setCancelable(false);
						history_dialog.setOnKeyListener(new OnKeyListener()
						{
							@Override
							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
							{
								// TODO Auto-generated method stub
								if( keyCode == KeyEvent.KEYCODE_BACK)
									dialog.dismiss();
									
								return false;
							}
						});
						
						Button ebpps_history_data_close_button = (Button) history_data_layout.findViewById(R.id.ebpps_history_data_close_button);
						ebpps_history_data_close_button.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v) 
							{
								// TODO Auto-generated method stub
								history_dialog.dismiss();
							}
						});
						
						history_dialog.show();
						
						/*
						AlertDialog.Builder read_history = new AlertDialog.Builder(app_context);
						read_history.setView(history_data_layout);
						read_history.setPositiveButton("關閉", null);
						read_history.show();
						*/
					}
					else
					{
						((LinearLayout) findViewById(R.id.login_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(361 * scale)));
	                    ((LinearLayout) findViewById(R.id.ebpps_content)).setVisibility(View.INVISIBLE);
						new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage("連線逾時，請重新登入").setNeutralButton("確定", null).show();
					}	
				}
				
				
				if( session_tag.equals("login_tag") ||  session_tag.equals("on_resume_tag"))
				{
					try 
					{
						response_content = new String(response_data, "UTF-8");
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	            	
				
					document = Jsoup.parse(response_content);
					
					//Element javascript = doc.select("script").first();
	        		Elements title_center = document.select("td[align=center]");
	            		
	            	Elements tr_content_even_td = document.select("tr.even");
	            	Elements tr_content_odd_td = document.select("tr.odd");
	            		
	            	Elements links = document.select("a[href]");
	            		
	            	response_result_list = new ArrayList<String[]>();
	        		
	           		result_array_electric_list = new ArrayList<String>();
	           		
	           		String electric_temp = "";
	            		
	        		for(Element qq : tr_content_odd_td)
	           		{
	           			//Log.i("qq:",qq.text());
	        			
	       				Elements tt = qq.select("td");
	           			String[] temp = tt.text().split(" ");
	           			
	           			electric_temp = temp[0].toString().replace("-" , "");
	           			result_array_electric_list.add(electric_temp);
	           			
	           			response_result_list.add(temp);
	           		}
     			    
	        		for(Element qq : tr_content_even_td)
	            	{
	        			//Log.i("qq:",qq.text());
	       				
	        			Elements tt = qq.select("td");
	           			String[] temp = tt.text().split(" ");
	           			
	           			electric_temp = temp[0].toString().replace("-" , "");
	           			result_array_electric_list.add(electric_temp);
	           			
	           			response_result_list.add(temp);
	           		}
	       			
	       			String title = title_center.text().toString().replace("　", "");
	       			title = title.replace(" ", "");
	       			title = title.replace("！！", "！！\n");
	       			title = title.replace("IP", "\nIP");
        	
	       			int slash_point ;
	        		
	        		if( (slash_point = title.lastIndexOf("/")) != -1  )
	        		{	
	        			String qq = title.substring(slash_point , slash_point + 3);
	       				//Log.i("qq",qq);
	       				title = title.replace(qq, qq+" ");
	       			}
	       			
	       			//Log.i("title_center",title_center.text().toString());
	        		((TextView) findViewById(R.id.ebpps_login_title)).setText(title);
	           		
	            	HashMap<String, String> print_state = ebppsElectricNumberListOnSQLite();
	            	
	            	history_data_url_map = new HashMap<String,String>();      	
	            	
	           		pdf_location_map = new HashMap<String,String>();
	           		pdf_pay_date_map = new HashMap<String,String>();
	           		pdf_payment_map = new HashMap<String,String>();
	           		
	           		String pdf_electric_number = "";
	               	String pdf_pay_date = "";
	               	String pdf_paymnet = "";
	               	String pdf_location = "";
	           	
	           		bill_detail_url_map = new HashMap<String,String>();
	           	
	 
	                //String bill_detail_pay_date = "";
	            	//String bill_detail_location = "";
	            			                
	               	for(Element url : links )
	               	{
	               		//pdf link
	               		String url_string = url.attr("href");
	               		
	               		if(url_string.contains("/EBPPS/modules/bill/NAEBC96_60001.jsp"))
	               		{	
	               			int pdf_electric_number_range = url_string.indexOf("custNo=");
	               			pdf_electric_number = url_string.substring(pdf_electric_number_range + 7, pdf_electric_number_range + 7 + 11);
	               			int pdf_pay_date_range = url_string.indexOf("collDate=");
	               			pdf_pay_date = url_string.substring(pdf_pay_date_range + 9, pdf_pay_date_range + 9 + 7);
	               			int amt_range = url_string.indexOf("amt=");
	               			int collID_range = url_string.indexOf("&collID");
	               			pdf_paymnet = url_string.substring(amt_range + 4, collID_range );
	               			
	                        //Log.i("",pdf_electric_number + " " + pdf_pay_date +" " + pdf_paymnet);
	               			pdf_location = "https://ebpps.taipower.com.tw" + url_string;
	               	
	               			pdf_location_map.put( pdf_electric_number , pdf_location);
	               			pdf_pay_date_map.put( pdf_electric_number , pdf_pay_date);
	               			pdf_payment_map.put( pdf_electric_number, pdf_paymnet);
	               		}
	               		
	               		int electric_number_range;
	               		String electric_number = "";
	               		//detail url and history_data_url
	               		if(url_string.contains("custNo="))
	       				{		
	               			electric_number_range = url_string.indexOf("custNo=");
	               			electric_number = url_string.substring(electric_number_range + 7, electric_number_range + 7 + 11);
	               		}
	               		
	               		if( url_string.contains("/EBPPS/action/bill.do?myAction=billDetail") )
	               		{
	               			//int bill_detail_electric_number_range = url_string.indexOf("custNo=");
	               			//bill_detail_electric_number = url_string.substring(bill_detail_electric_number_range + 7, bill_detail_electric_number_range + 7 + 11);
	                		//bill_detail_electric_number = url.attr("href").substring(49, 60);
	               				
	               			bill_detail_url_map.put(electric_number , "https://ebpps.taipower.com.tw" + url_string);
	               		}
	                	
	                	if( url_string.contains("/EBPPS/action/bill.do?myAction=queryHistoryBill") )
	                		history_data_url_map.put(electric_number, "https://ebpps.taipower.com.tw" + url_string);
	               	}
					
					EbppsItemAdapter ebpps_item_adapter = new EbppsItemAdapter(app_context, android.R.layout.simple_list_item_1 , response_result_list);
					
	           		ListView ebpps_list_view = (ListView) findViewById(R.id.ebpps_list_view);
	           		ebpps_list_view.setAdapter(ebpps_item_adapter);
	           		ebpps_list_view.setOnItemClickListener(item_click_listener);
	            	   		
	               	setting.edit().putString("ebpps_account", save_ebpps_account_hash)
	               	.putString("ebpps_password", save_ebpps_password_hash)
	               	.putString("last_login_time", String.valueOf(System.currentTimeMillis() / 1000l)).commit();
	                 
	               	((LinearLayout) findViewById(R.id.login_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
	               	((LinearLayout) findViewById(R.id.ebpps_content)).setVisibility(View.VISIBLE);
	               	
	               	((MainPage)app_activity).ebppsLoginCount();
				}
					
				
				if( session_tag.equals("download_pdf_tag") )
				{
					boolean check_html = false;
					
					try 
					{
						new String(response_data,"utf-8" );
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						check_html = true;
					}
					
					if( !check_html )
					{
					
					File pdf_file;
                	
	                String external_storage_state = Environment.getExternalStorageState();
	                	
	                if(Environment.MEDIA_MOUNTED.equals(external_storage_state))
	                {
	                	pdf_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdf_name );
	                }
	                else
	                {
	                	pdf_file = new File(Environment.DIRECTORY_DOWNLOADS , pdf_name );
	                }
	                	 
	                String pdf_file_path = pdf_file.getAbsolutePath();
	                	
	                FileOutputStream fos;
					
	                try 
					{
						fos = new FileOutputStream(pdf_file);
						
						fos.write(response_data);
		                
		                fos.close();
					} 
					catch (FileNotFoundException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
					Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse("file://" + pdf_file_path));
                    intent.setDataAndType( Uri.parse("file://" + pdf_file_path) , "application/pdf" );
                    Intent open_pdf = Intent.createChooser( intent, "選擇檔案" );
					app_activity.startActivity(open_pdf);
					
					}
					else
					{
						((LinearLayout) findViewById(R.id.login_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(361 * scale)));
	                    ((LinearLayout) findViewById(R.id.ebpps_content)).setVisibility(View.INVISIBLE);
						new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage("連線逾時，請重新登入").setNeutralButton("確定", null).show();
					}
				}
        	}
			
			CreateLoadingDialog.dialog_dismiss(process_persent_pointr);
			process_persent_pointr = null;
			
			if( result != 0 )
			{	
				String error_message = "";
				Log.i("result", "" + result);
				
				if( result == 8)
					error_message = "帳號錯誤";
				else if( result == 9 )
					error_message = "密碼錯誤";
				else if( result == 5 )
				{	
					error_message = "連線逾時！！請重新登入";
					((LinearLayout) findViewById(R.id.login_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(361 * scale)));
                    ((LinearLayout) findViewById(R.id.ebpps_content)).setVisibility(View.INVISIBLE);
				}
				else
					error_message = "網路錯誤！！請稍候再試";
					
				new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage(error_message).setNeutralButton("確定", null).show();
			}
        }	
    }
	
	private class EbppsItemAdapter extends ArrayAdapter<Object>
	{	
		ArrayList<String[]> result_array_list;
		
		public EbppsItemAdapter(Context context, int layout_resource_id ,ArrayList<String[]> result_list)
		{	
			super(context, layout_resource_id , result_list.toArray(new String[result_list.size()][]));
			
			result_array_list = result_list;
			//Log.i("size:", "" + result_array_list.size());
		}
		
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = app_activity.getLayoutInflater().inflate(R.layout.fragment_ebpps_light_item,  parent , false);
			
			((TextView) convertView.findViewById(R.id.electric_nubmer)).setText(result_array_list.get(position)[0]);
			((TextView) convertView.findViewById(R.id.month)).setText(result_array_list.get(position)[1]);
			
			TextView money = (TextView) convertView.findViewById(R.id.money);
			money.setText(result_array_list.get(position)[2]);
			
			TextView pay_limit = (TextView) convertView.findViewById(R.id.pay_limit);
			pay_limit.setText(result_array_list.get(position)[5]);
			
			String qq = result_array_list.get(position)[3];
			TextView pay_status = (TextView) convertView.findViewById(R.id.pay_status);
			pay_status.setText(qq);
			
			
			if( qq.equals("未繳") )
			{	
				money.setTextColor(Color.RED);
				money.setTypeface(null, Typeface.BOLD);
				money.setTextSize(28.0f);
				pay_limit.setTextColor(Color.RED);
				pay_limit.setTypeface(null, Typeface.BOLD);
				pay_limit.setTextSize(28.0f);
				pay_status.setTextColor(Color.RED);
				pay_status.setTypeface(null, Typeface.BOLD);
				pay_status.setTextSize(28.0f);
			}
			
			//Log.i("position:" , "" + position);
			return convertView;
		}
	}
	
	
	/*
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ( keyCode == KeyEvent.KEYCODE_BACK) 
        {
            // your code
        	//if( ! logout_seeion_id.equals("") )
			//{
			//	String logout_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=logout_action&authsid=";
			//	logout_url += logout_seeion_id;
			//	new LoadingDataAsyncTask().execute("logout_tag",logout_url);
			//}
			//else
			//	finish();
        	
        	return true;
        }

        return super.onKeyDown(keyCode, event);
    }
	*/
	
	public OnItemClickListener item_click_listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id)
		{
			// TODO Auto-generated method stub
			final int item_position = position;
			
			String[] ebpps_item_menu_title = app_context.getResources().getStringArray(R.array.ebpps_item_menu_title);
			
			EbppsItemClickMenuAdapter ebpps_item_menu_adapter = new EbppsItemClickMenuAdapter(app_context, android.R.layout.simple_list_item_1,ebpps_item_menu_title);
			
			AlertDialog.Builder ebpps_item_menu = new AlertDialog.Builder(app_context);
			
			//click_list.setItems(new String[] {"繳費單下載","本期明細","超商條碼(吧扣)繳費","網路/ATM繳費資料"} , new OnClickListener()
			ebpps_item_menu.setAdapter(ebpps_item_menu_adapter , new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					if ( which == 0)
					{
						String electric_number = result_array_electric_list.get(item_position);
						String pdf_location_url = pdf_location_map.get(electric_number);
						
						if(pdf_location_url == null )
						{
							AlertDialog.Builder warning = new AlertDialog.Builder(app_context);
							warning.setMessage("沒有繳費單下載");
							warning.setPositiveButton(android.R.string.ok, null);
							warning.show();
						}
						else
						{	
							String pdf_pay_date = pdf_location_url.substring(95, 102);
							
							new LoadingDataAsyncTask().execute("download_pdf_tag",pdf_location_url, pdf_pay_date + "_" + electric_number);
						}
					}
					
					if ( which == 1 )
					{
						String bill_detail_url = bill_detail_url_map.get(result_array_electric_list.get(item_position));
						
						new LoadingDataAsyncTask().execute("read_detail_tag",bill_detail_url,"");
					}
					
					if ( which == 4 )
					{
						String history_data_url = history_data_url_map.get(result_array_electric_list.get(item_position));
						
						new LoadingDataAsyncTask().execute("read_history_tag",history_data_url,"");
					}
					
					if ( which == 3 )
					{
						if( pdf_pay_date_map.get(result_array_electric_list.get(item_position)) == null )
						{
							AlertDialog.Builder warning = new AlertDialog.Builder(app_context);
							warning.setMessage("沒有網路繳費資料");
							warning.setPositiveButton(android.R.string.ok, null);
							warning.show();
						}
						else
						{
							String bill_detail_url = bill_detail_url_map.get(result_array_electric_list.get(item_position));
						
							new LoadingDataAsyncTask().execute("read_detail_check_code_tag",bill_detail_url,"");
						}
					}	
					
					if ( which == 2)
					{
						AlertDialog.Builder warning = new AlertDialog.Builder(app_context);
						warning.setTitle("本功能暫不開放！！");
						warning.setMessage("敬請期待！！");
						warning.setPositiveButton(android.R.string.ok, null);
						warning.show();
						
						/*
						if( pdf_pay_date_map.get(result_array_electric_list.get(item_position)) == null )
						{
							//Toast.makeText(ebpps_light.this, "沒有條碼可產生" ,Toast.LENGTH_LONG).show();
							AlertDialog.Builder warning = new AlertDialog.Builder(app_context);
							warning.setMessage("沒有條碼可產生");
							warning.setPositiveButton(android.R.string.ok, null);
							warning.show();
						}
						else
						{
							Calendar calendar_data = Calendar.getInstance(); 
							calendar_data.add(Calendar.DATE, 1);
							
							int year = calendar_data.get(Calendar.YEAR) - 2011; //去百位
        					int month = calendar_data.get(Calendar.MONTH) + 1;
        					int date = calendar_data.get(Calendar.DATE) ;
        					
        					String payment_string = pdf_payment_map.get(result_array_electric_list.get(item_position));
        					String payment_money_format = "";
        					
        					for(int i = payment_string.length() ; i < 9 ; i++  )
        						payment_money_format += "0";
        					
        					payment_money_format += payment_string;
        					
        					        					
        					String ROC_date0 = "";
        					        					 						
        						ROC_date0 = (year < 10) ? ( "0" + String.valueOf(year) ) : String.valueOf(year) ;
        						ROC_date0 += (month < 10) ? ( "0" + String.valueOf(month) ) : String.valueOf(month) ;
        						ROC_date0 += (date < 10) ? ( "0" + String.valueOf(date) ) : String.valueOf(date) ;
        					
        					
							Bundle barcode_data = new Bundle();
							barcode_data.putString("electric_number",result_array_electric_list.get(item_position) );
							//收費日
							barcode_data.putString("ROC_date1",pdf_pay_date_map.get(result_array_electric_list.get(item_position)).substring(1) );
							//錢錢
							barcode_data.putString("payment_money_format",payment_money_format );
							//待收期限
							barcode_data.putString("ROC_date0",ROC_date0);
							//origin
							barcode_data.putString("origin_from","ebpps");
							
							
							Intent intent = new Intent();
							intent.putExtras(barcode_data);
							intent.setClass(app_context, barcode_and_ebpps_barcode.class);
							app_context.startActivity(intent);
						}
						*/
					}	
				
					dialog.dismiss();
				}
			});
			
			ebpps_item_menu.show();
		}
	};
	
	
	private class EbppsHistoryDataItemAdapter extends ArrayAdapter<Object>
	{
		String[] items;
		
		public EbppsHistoryDataItemAdapter(Context context, int resource,String[] items_array) 
		{
			super(context, resource, items_array);
			// TODO Auto-generated constructor stub
			items = items_array;
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = app_activity.getLayoutInflater().inflate(R.layout.fragment_ebpps_light_history_data_item, parent, false);
			
			String[] temp = items[position].split(",");
			
			for( int i = 0; i < 3; i ++ )
				((TextView) convertView.findViewById(R.id.ebpps_history_data_list_item0 + i)).setText(temp[i]);
			
			
			Button ebpps_history_data_detail_button = (Button) convertView.findViewById(R.id.ebpps_history_data_detail_button);
			
			if( position >= (items.length - history_data_detail_map.size()) )
			{
				String button_tag = temp[0].replace( "年", "").replace("月", "");
				
				if( button_tag.startsWith("99") )
					button_tag = "0" + button_tag;
				
				ebpps_history_data_detail_button.setFocusable(false);
				ebpps_history_data_detail_button.setFocusableInTouchMode(false);
				
				ebpps_history_data_detail_button.setTag(button_tag);
				ebpps_history_data_detail_button.setOnClickListener(history_data_detail_list_on_click_listener);
			
			}
			else
			{
				ebpps_history_data_detail_button.setVisibility(View.INVISIBLE);
			}
			
			return convertView;
		}
	}
	
	private View.OnClickListener history_data_detail_list_on_click_listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			String history_data_detail_url_string = history_data_detail_map.get(v.getTag());
			
			new LoadingDataAsyncTask().execute("read_detail",history_data_detail_url_string);
		}
		
	};
	
	private class EbppsItemClickMenuAdapter extends ArrayAdapter<Object>
	{
		String[] item_list ;
		
		String[] item_description_list = app_context.getResources().getStringArray(R.array.ebpps_item_menu_description);
		
		public EbppsItemClickMenuAdapter(Context context, int layout_resource_id , Object[] object) 
		{
			super(context, layout_resource_id, object );
			// TODO Auto-generated constructor stub
			item_list = (String[]) object;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = app_activity.getLayoutInflater().inflate(R.layout.fragment_ebpps_light_item_menu, parent, false);
			
			ImageView ebpps_menu_item_image = (ImageView) view.findViewById(R.id.ebpps_menu_item_image);
			ebpps_menu_item_image.setBackgroundResource(R.drawable.ebpps_item_menu0 + position);
			
			AnimationDrawable animation_drawable = (AnimationDrawable) ebpps_menu_item_image.getBackground();  
			animation_drawable.start();
			
			((TextView) view.findViewById(R.id.ebpps_menu_item_text)).setText(item_list[position]);
			((TextView) view.findViewById(R.id.ebpps_menu_item_description)).setText(item_description_list[position]);
			
			return view;
		}
	}
	
	private class DrawGraphy extends View implements Runnable
	{
		float view_width;
		float view_height;
		
		float max_limit_money_point_height;
		float max_limit_kwh_point_height;
		float each_point_width;
		
		int max_kwh;
		int max_money;
		
		int[] kwh_array;
		int[] money_array;
		
		int selected_point;
		
		public DrawGraphy(Context context, ArrayList<String> points, int selected_point)
		{
			super(context);
			
			DmInfor dm = new DmInfor(app_activity,app_context);
			
			float dm_widht_adjust = 60.0f * dm.scale;
			
			this.view_width = (float) dm.v_width - dm_widht_adjust; 
			this.view_height = dm.scale * 180.0f;
			
			//Log.i("view_width view_height", "" + view_width + " " + view_height);
			
			kwh_array = new int[points.size()];
			money_array = new int[points.size()];
			
			for(int i = 0; i < points.size(); i++)
			{
				String[] temp = points.get(i).split(",");
				
				if( Integer.valueOf(temp[1]).intValue() > max_kwh  )
					max_kwh = Integer.valueOf(temp[1]).intValue();
				
				kwh_array[i] = Integer.valueOf(temp[1]).intValue();
				
				if( Integer.valueOf(temp[2].replace("元", "")) > max_money  )
					max_money = Integer.valueOf(temp[2].replace("元", "")).intValue();
				
				money_array[i] = Integer.valueOf(temp[2].replace("元", "")).intValue();
			}
			
			each_point_width = ((float) view_width) / ((float) points.size()) ;
			max_limit_money_point_height = (float) max_money + (max_money / 10.0f);
			max_limit_kwh_point_height  = (float) max_kwh + ( max_kwh / 10.0f);
			
			Log.i("each_point_width max_limit_point_height","" +each_point_width + "  " + max_limit_money_point_height);
			
			this.selected_point = selected_point;
		}
		
		@Override
		public void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			
			float kwh_x0,kwh_y0;
			float money_x0,money_y0,money_x1,money_y1;
			
			Paint money_paint = new Paint();
			money_paint.setColor(Color.BLUE);
			money_paint.setStrokeWidth(2.0f);
		
			Paint kwh_paint = new Paint();
			kwh_paint.setARGB(0x77, 0xFF, 0xCC, 0xCC);
			kwh_paint.setStrokeWidth(each_point_width);	
			
			Paint black_line_paine = new Paint();
			black_line_paine.setColor(Color.BLACK);
			black_line_paine.setStrokeWidth(1.0f);
			
			Paint point_paint = new Paint();
			point_paint.setStrokeWidth(10.0f);
			point_paint.setColor(Color.RED);
			
			for(int i = 0 ; i < money_array.length; i++)
			{		
				kwh_x0 = (each_point_width / 2.0f) + (each_point_width * (float) i);
				kwh_y0 = view_height - (view_height * ( (float) kwh_array[i] / max_limit_kwh_point_height) );
				
				//Log.i("kwh_x0, kwh_y0, kwh_array" , " " + kwh_x0 + " " + kwh_y0 + " " + kwh_array[i] );
				
				canvas.drawLine(kwh_x0 - ((each_point_width / 2.0f) - 1.0f), kwh_y0, kwh_x0 - ((each_point_width / 2.0f) - 1.0f), view_height, black_line_paine);
				canvas.drawLine(kwh_x0 - ((each_point_width / 2.0f) - 1.0f), kwh_y0, kwh_x0 + ((each_point_width / 2.0f) - 1.0f), kwh_y0, black_line_paine);
				canvas.drawLine(kwh_x0, kwh_y0, kwh_x0, view_height, kwh_paint);
				canvas.drawLine(kwh_x0 + ((each_point_width / 2.0f) - 1.0f), kwh_y0, kwh_x0 + ((each_point_width / 2.0f) - 1.0f), view_height, black_line_paine);
				
				if( i < money_array.length - 1)
				{
					money_x0 = (each_point_width / 2.0f) +  ((each_point_width * (float) i));
					money_y0 = view_height - (view_height * ( (float) money_array[i] / max_limit_money_point_height));
				
					money_x1 = (each_point_width / 2.0f) + (each_point_width * (float) (i + 1));
					money_y1 = view_height - (view_height * ( (float) money_array[i + 1] / max_limit_money_point_height));
				
					//Log.i("money_x0, money_y0, money_x1, money_y1 money_array" , " " + money_x0 + " " + money_y0 + " " + money_x1 + " " + money_y1 + " " + money_array[i] );
				
					//canvas.drawLines(new float[]{10.0f,10.0f,30.0f,30.0f}, paint);
					canvas.drawLine(money_x0, money_y0, money_x1, money_y1, money_paint);
				}
				
				if(selected_point != -1)
				{	
					float point_x = (each_point_width / 2.0f) +  ((each_point_width * (float) selected_point));
					float point_y = view_height - (view_height * ( (float) money_array[selected_point] / max_limit_money_point_height));
					canvas.drawPoint( point_x, point_y, point_paint);
				}
			}
		}
		
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			while(!Thread.currentThread().isInterrupted())  
	        {  
	            postInvalidate();   	//update ui 
	        }  
		}
	}
	
	
	private HashMap<String,String> ebppsElectricNumberListOnSQLite()
	{
		DBonSQLite SQLite_for_ebpps_electric_number_list = new DBonSQLite(app_context, "taipower_west_branch_app_data_base.db",null,1);
        SQLiteDatabase db_read  = SQLite_for_ebpps_electric_number_list.getReadableDatabase();
        
        String[] select_column = new String[]{"_ID INTEGER ","electric_number","print_state"};
        
        Cursor cursor = db_read.query("ebpps_electric_number_list_table", select_column , null, null, null, null, null);
        
        int result_rows_num = cursor.getCount();
        
        HashMap<String,String> map_list = new HashMap<String,String>();
        
        if(result_rows_num != 0) 
        {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	
        	for(int i = 0 ; i < result_rows_num ; i++ ) 
        	{	
        		//skip _ID column
        		map_list.put(cursor.getString(1),cursor.getString(2));
        		
        		cursor.moveToNext();//將指標移至下一筆資料
        	}
        }
        	cursor.close(); //關閉Cursor
        	SQLite_for_ebpps_electric_number_list.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
        	
        return map_list; 
		
	}
	
}
