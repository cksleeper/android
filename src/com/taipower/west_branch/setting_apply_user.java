package com.taipower.west_branch;

import static com.taipower.west_branch.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.taipower.west_branch.CommonUtilities.EXTRA_MESSAGE;
import static com.taipower.west_branch.CommonUtilities.SENDER_ID;
import static com.taipower.west_branch.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.android.gcm.GCMRegistrar;
import com.taipower.west_branch.R;
import com.taipower.west_branch.SettingDialog.RegisterAsyncTask;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;
import com.taipower.west_branch.utility.SHA1Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class setting_apply_user extends Activity
{
	
	private Context app_context;
	private Activity app_activity;
	private setting_apply_user app_class;
	
	private SharedPreferences setting ;
	private long now_time_subtract;
	
	
	private String apply_user ;
	private String tel_area_number ;
	private String phone_number ;
	private String email;
	private String ext_phone_number ;
	private String mobile;
	private String ebpps_account ;
	private String ebpps_password; 
	
	public static String GCM_electric_number_list;
	public static String GCM_email ;
	public static String GCM_phone_number ;
	public static String GCM_mobile ;
	
	private boolean show_notification;
	
	private String regId;
	
	
	private Button prefereance_state_button;
	private Button clear_all_data_button ;
	
	private String prefereance_state ;
  	private boolean clear_all_data ;
	
	private Button GCM_state_button;
	private String GCM_state_text = "";
	
	
	
	public setting_apply_user()
	{
		this.app_context = this;
		this.app_activity = this;
		this.app_class = this;
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
		
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_apply_user);
        
        int linear_height = new DmInfor(app_activity,app_context).menu_linear_height;
         
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  linear_height ));
        
        
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        back_button.setOnClickListener( new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		
      	setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
      	
    	ebpps_account = setting.getString("ebpps_account", "");
    	ebpps_password = setting.getString("ebpps_password", "");
        
   
    	
    	

        if(!ebpps_account.equals(""))
        {
        	
        	String ebpps_account_unhash = ebpps_account.substring(0, 1) + new BigInteger(ebpps_account.substring(1)).divide(new BigInteger(Build.SERIAL.replaceAll("[^0-9]+", ""))).toString(); 		
        	
        	Log.i("ebpps_account :",ebpps_account);
        	Log.i("Build.SERIAL :",BigInteger.valueOf(Integer.valueOf(Build.SERIAL.replaceAll("[^0-9]+", ""))).toString());
        	Log.i("ebpps_account_unhash :",ebpps_account_unhash);
        	
        	String ebpps_myAction_password_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=password&account=";
        	ebpps_myAction_password_url += ebpps_account_unhash;        		
        		
        	String ebpps_myAction_check_PWD_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=check_PWD&old_hash=" + ebpps_password + "&words2=HYCAPI_INSTEAD&pkcs=&account=&words=********";
        	
        	String ebpps_set_electric_list_url = "https://ebpps.taipower.com.tw/EBPPS/action/SetElectric.do?myAction=electric_set";
        	
			String ebpps_myAction_basic_info_list_url = "https://ebpps.taipower.com.tw/EBPPS/action/modBasicInfo.do?myAction=modBasicInfo";
        	
			
			now_time_subtract = (System.currentTimeMillis() / 1000l) - Integer.valueOf(setting.getString("last_login_time", "0")).longValue();
			
			Log.i("now_time_subtract : ",String.valueOf(now_time_subtract));
			
			if( now_time_subtract > 900)
        	{
				new doingAsyncTask().execute("login_tag", ebpps_myAction_password_url, 
        	                                              ebpps_myAction_check_PWD_url, 
        	                                              ebpps_set_electric_list_url, 
        	                                              ebpps_myAction_basic_info_list_url);
        	}
			else
			{
				new doingAsyncTask().execute("on_resume_tag", ebpps_myAction_password_url, 
                        								     ebpps_myAction_check_PWD_url, 
                        								  	 ebpps_set_electric_list_url, 
                        								  	 ebpps_myAction_basic_info_list_url);
			}
        	
        }
        
      	
      	apply_user = setting.getString("apply_user","");
		tel_area_number = setting.getString("tel_area_number","");	   
		phone_number = setting.getString("phone_number","");	   
		email = setting.getString("email","");	   
		mobile = setting.getString("mobile","");	   
      	ebpps_account = setting.getString("ebpps_account","");
      	show_notification = setting.getBoolean("show_notification",true);
		
      	//telephone || mobile
        if( (apply_user.equals("") || email.equals("") || 
        	(tel_area_number.equals("") && phone_number.equals("") && mobile.equals(""))) && 
        	ebpps_account.equals("") )
        {	
        	prefereance_state = "  !!未設定!!  ";
        	clear_all_data = false;
        }
        else
        {
        	if(ebpps_account.equals(""))
        		prefereance_state = "!!自行輸入!!";
        	else
        		prefereance_state = "!!電子帳單系統!!";
        	
        	clear_all_data = true;
        }
        
        prefereance_state_button = (Button) findViewById(R.id.preferance_state_button);
        prefereance_state_button.setText(prefereance_state);
        prefereance_state_button.setOnClickListener(view_on_click_listener);
        
        clear_all_data_button = (Button) findViewById(R.id.clear_all_data_button);
        clear_all_data_button.setOnClickListener(view_on_click_listener);
        clear_all_data_button.setEnabled(clear_all_data);
        
        
        
        
        if(show_notification)
        	GCM_state_text = "!!已開啟!!";
        else
        	GCM_state_text = "!!關閉!!";
        
        regId = GCMRegistrar.getRegistrationId(app_context);
        boolean GCM_state_button_state = false;
        
        if( !regId.equals(""))
        	GCM_state_button_state = true;
        
        GCM_state_button = (Button) findViewById(R.id.GCM_state_button);
        GCM_state_button.setText(GCM_state_text);
        GCM_state_button.setOnClickListener(view_on_click_listener);
        GCM_state_button.setEnabled(GCM_state_button_state);
        
        
        
        
	}
	
	
	private View.OnClickListener view_on_click_listener = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			if( v.getId() == R.id.preferance_state_button )
			{
				//new SettingDialog(app_context,app_activity);
				Intent intent = new Intent();
				intent.setClass(app_context, SettingDialog.class);
				startActivity(intent);
			}
			
			if( v.getId() == R.id.clear_all_data_button )
			{
				AlertDialog.Builder warning_message = new AlertDialog.Builder(app_context);
				warning_message.setTitle("清除基本資料");
				warning_message.setMessage("確定要清除基本資料!!");
				warning_message.setPositiveButton("確定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						
						setting.edit().putString("apply_user", "")
			  			  .putString("tel_area_number", "")
			  			  .putString("phone_number", "" )
			  			  .putString("email", "")
			  			  .putString("ext_phone_number", "")
			  			  .putString("mobile", "")
						  .putString("ebpps_account", "")
						  .putString("ebpps_password", "")
						  .putBoolean("show_notification", true)
			  			  .putBoolean("show_title", false).commit();
						
						prefereance_state_button.setText("  !!未設定!!  ");
			        	clear_all_data_button.setEnabled(false);
						
			        	DBonSQLite SQLite_for_ebpps_electric_number_list = new DBonSQLite(app_context, "taipower_west_branch_app_data_base.db",null,1);
			            SQLiteDatabase db_read  = SQLite_for_ebpps_electric_number_list.getReadableDatabase();
			            db_read.delete("ebpps_electric_number_list_table", null,null);
			            db_read.close();
			            SQLite_for_ebpps_electric_number_list.close();
			            
			            ArrayList<String> electric_number_list_result = new ArrayList<String>();
			            ArrayAdapter<String> ebpps_electric_number_array = new ArrayAdapter<String>(app_context, android.R.layout.simple_list_item_1, electric_number_list_result);
			            
			            ListView ebpps_electric_number_list = (ListView) findViewById(R.id.ebpps_electric_number_list); 
			        	ebpps_electric_number_list.setAdapter(ebpps_electric_number_array);
			        	
			        	
			        	
						GCMRegistrar.unregister(app_context);
					}
					
				});
				warning_message.setNegativeButton("取消", null);
				warning_message.show();
			}
			
			if( v.getId() == R.id.GCM_state_button)
			{
				
				regId = GCMRegistrar.getRegistrationId(app_context);
				if(!regId.equals(""))	 
				{
					if( show_notification )
					{	
						setting.edit().putBoolean("show_notification", false).commit();	
						GCM_state_text = "!!未開啟!!";
					}	
					else
					{
						setting.edit().putBoolean("show_notification", true).commit();	
						GCM_state_text = "!!已開啟!!";
					}
					((Button) v).setText(GCM_state_text);
				}
				else
				{
					AlertDialog.Builder warning_dialog = new AlertDialog.Builder(app_context);
					warning_dialog.setTitle("歐!歐歐!!");
					warning_dialog.setMessage("請先設定電子帳單帳號!!");
					warning_dialog.setNeutralButton("設定", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(app_context, SettingDialog.class);
							startActivity(intent);
							dialog.dismiss();
							
						}
					});
					warning_dialog.show();
				}
			}
			
		}
	};
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
      	
      	apply_user = setting.getString("apply_user","");
		tel_area_number = setting.getString("tel_area_number","");	   
		phone_number = setting.getString("phone_number","");	   
		email = setting.getString("email","");	   
		mobile = setting.getString("mobile","");	  
		ebpps_account = setting.getString("ebpps_account", "");
		
		//telephone || mobile
        if( (apply_user.equals("") || email.equals("") || 
        	(tel_area_number.equals("") && phone_number.equals("") && mobile.equals(""))) && 
        	ebpps_account.equals("") )
        {	
        	prefereance_state = "  !!未設定!!  ";
        	clear_all_data = false;
        }
        else
        {
        	if(ebpps_account.equals(""))
        		prefereance_state = "!!自行輸入!!";
        	else
        		prefereance_state = "!!電子帳單系統!!";
        	
        	clear_all_data = true;
        }
      	
        prefereance_state_button = (Button) findViewById(R.id.preferance_state_button);
        prefereance_state_button.setText(prefereance_state);
        prefereance_state_button.setOnClickListener(view_on_click_listener);
        
        clear_all_data_button = (Button) findViewById(R.id.clear_all_data_button);
        clear_all_data_button.setOnClickListener(view_on_click_listener);
        clear_all_data_button.setEnabled(clear_all_data);
		
        
        
        if(show_notification)
        	GCM_state_text = "!!已開啟!!";
        else
        	GCM_state_text = "!!關閉!!";
        
        regId = GCMRegistrar.getRegistrationId(app_context);
        boolean GCM_state_button_state = false;
        
        if( !regId.equals(""))
        	GCM_state_button_state = true;
        
        GCM_state_button = (Button) findViewById(R.id.GCM_state_button);
        GCM_state_button.setText(GCM_state_text);
        GCM_state_button.setOnClickListener(view_on_click_listener);
        GCM_state_button.setEnabled(GCM_state_button_state);
        
        
		ArrayList<String> electric_number_list_result = ebppsElectricNumberListOnSQLite();
		ArrayAdapter<String> ebpps_electric_number_array = new ArrayAdapter<String>(app_context, android.R.layout.simple_list_item_1, electric_number_list_result);
        ListView ebpps_electric_number_list = (ListView) findViewById(R.id.ebpps_electric_number_list); 
    	ebpps_electric_number_list.setAdapter(ebpps_electric_number_array);
    	
    	
		
	}
	
	private ArrayList<String> ebppsElectricNumberListOnSQLite()
	{
		DBonSQLite SQLite_for_ebpps_electric_number_list = new DBonSQLite(app_context, "taipower_west_branch_app_data_base.db",null,1);
        SQLiteDatabase db_read  = SQLite_for_ebpps_electric_number_list.getReadableDatabase();
        
        String[] select_column = new String[]{"_ID INTEGER ","electric_number","print_state"};
        
        Cursor cursor = db_read.query("ebpps_electric_number_list_table", select_column , null, null, null, null, null);
        
        //取得資料表列數
        int result_rows_num = cursor.getCount();
        //用陣列存資料
        ArrayList<String> electric_number_list_result = new ArrayList<String>();
        
        if(result_rows_num != 0) 
        {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	
        	for(int i = 0 ; i < result_rows_num ; i++ ) 
        	{
        		String temp_result = "";      
        		
        		//skip _ID column
        		for(int j = 1 ; j < select_column.length ; j ++)
        			temp_result += cursor.getString(j) + " ";
        	
        		electric_number_list_result.add(temp_result);
        		
        		cursor.moveToNext();//將指標移至下一筆資料
        	}
        }
        	cursor.close(); //關閉Cursor
        	SQLite_for_ebpps_electric_number_list.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
        	
        return electric_number_list_result;
		
	}
	

	private class doingAsyncTask extends AsyncTask<String, Integer, Integer> 
	{
		@Override
		protected void onPreExecute ()
    	{
    		super.onPreExecute();
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    		publishProgress(0);
    	}
		
		byte[] response_data ;
		byte[] response_data1;
		String tag_value ;
		DBonSQLite SQLite_for_ebpps;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			Integer return_value = null;
			
			tag_value = params[0] ;  
			
			if( tag_value.equals("login_tag"))
			{
				try 
				{	
					//publishProgress(1);
					
					//response_data = HttpConnectResponse.apacheConnection(params[1], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
					
					String login_layer0_content = new String(response_data,"UTF-8");
        			
        			Document lgoin_layer0 = Jsoup.parse(login_layer0_content);
        			
        			Element error_alert_javascript = lgoin_layer0.select("script").last();
        			
        			String error_alert_string = error_alert_javascript.toString().replace("<script ", "");
					
        			//account error
        			if( error_alert_string.contains("alert") ) 
        				return_value = 9;
        			else
        			{
        			
        			//response_data = HttpConnectResponse.apacheConnection(params[2], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
        			
        			login_layer0_content = new String(response_data,"UTF-8");
        			
        			lgoin_layer0 = Jsoup.parse(login_layer0_content);
        			
        			error_alert_javascript = lgoin_layer0.select("script").last();
        			
        			error_alert_string = error_alert_javascript.toString().replace("<script ", "");
					
        			//password error
        			if( error_alert_string.contains("alert") )
        				return_value = 8; 
        			else
        			{
        			
        			//electric number list
        			//response_data = HttpConnectResponse.apacheConnection(params[3], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
					//response_data = HttpConnectResponse.onOpenConnection(params[1], "GET", null , HttpConnectResponse.COOKIE_CLEAR, HttpConnectResponse.HTTP_REDIRECT);
        			
        			//user information
        			//response_data1 = HttpConnectResponse.apacheConnection(params[4], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
        			
        			return_value = 1;
        			
        			}
        			
        			}
        		} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
			
			if(tag_value.equals("on_resume_tag"))
			{
				return_value = 1;
			}
			
			
			Log.i("return value :", String.valueOf(return_value));
			return return_value;
		}
		
		Dialog instialize_dialog ;
		
		protected void onProgressUpdate(Integer... values)
		{
			String message = "";
			
			if(values[0] == 0)
				message = "資料設定中"; 
			else
				message = "網路連結中"; 
				
			instialize_dialog = CreateLoadingDialog.createLoadingDialog(app_context, message, CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NONCANCELABLE);
			
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			
			if( result == 1 )
			{
				Document document = null;
				Document document_infor = null;
				try 
				{
					document = Jsoup.parse(new String(response_data,"UTF-8"));
					document_infor = Jsoup.parse(new String(response_data1,"UTF-8"));
				
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			
				Elements input_tag = document.select("input[checked]");
			
				ArrayList<String> electric_number_list = new ArrayList<String>();
				ArrayList<String> send_list = new ArrayList<String>();
				
				for(Element qq : input_tag)
				{	
					//Log.i( "input value :" , qq.val());
					//Log.i( "input value :" , qq.outerHtml());
				
					Log.i("electric_number", qq.outerHtml().substring(qq.outerHtml().indexOf("name=")+7,38));
					
					electric_number_list.add( qq.outerHtml().substring(qq.outerHtml().indexOf("name=")+7,38));
					String send_tag = "";
					if( qq.val().equals("NoSendBill"))
						send_tag = "N";
					else
						send_tag = "Y";
					Log.i("send tag :", send_tag);
				
					send_list.add(send_tag);
				}

				SQLite_for_ebpps = new DBonSQLite( app_context, "taipower_west_branch_app_data_base.db",null,1);
				SQLiteDatabase db_write = SQLite_for_ebpps.getWritableDatabase();
				
				db_write.delete("ebpps_electric_number_list_table", null, null);
				//db_write.rawQuery("delete from " + "ebpps_electric_number_list_table", null);
			
				ContentValues insert_db_values = new ContentValues();
		    
				for(int i = 0 ; i < electric_number_list.size() ; i ++)
				{	
					insert_db_values.put("electric_number", electric_number_list.get(i) );
					insert_db_values.put("print_state", send_list.get(i) );
					db_write.insert("ebpps_electric_number_list_table", null, insert_db_values);
				}
				
				db_write.close();
				SQLite_for_ebpps.close();	
				
				/*
				ArrayList<String> electric_number_list_result = app_class.ebppsElectricNumberListOnSQLite();
		        ArrayAdapter<String> ebpps_electric_number_array = new ArrayAdapter<String>(app_context, android.R.layout.simple_list_item_1, electric_number_list_result);
		        ListView ebpps_electric_number_list = (ListView) findViewById(R.id.ebpps_electric_number_list); 
		    	ebpps_electric_number_list.setAdapter(ebpps_electric_number_array);
				*/
		    	((ListView) findViewById(R.id.ebpps_electric_number_list)).setAdapter(new ArrayAdapter<String>(app_context, android.R.layout.simple_list_item_1, app_class.ebppsElectricNumberListOnSQLite()));
				
				
				String apply_user = document_infor.select("input[name=con_contact]").val();
			    String tel_area_number = document_infor.select("input[name=con_phone_area]").val();
				String phone_number = document_infor.select("input[name=con_phone]").val();
				String ext_phone_number = document_infor.select("input[name=con_phone_ext]").val();
				String email = document_infor.select("input[name=con_email]").val();
				String mobile = document_infor.select("input[name=con_mobile]").val();
				
				//update infor_data ,not write abpps_account_hash and ebpps_password
				//String ebpps_account_hash = ebpps_account.substring(0, 1) + new BigInteger(ebpps_account.substring(1).toString()).multiply(new BigInteger(Build.SERIAL.replaceAll("[^0-9]+",""))).toString();   ;
				
				//Log.i("ebpps_account :",ebpps_account);
	        	Log.i("Build.SERIAL :",BigInteger.valueOf(Integer.valueOf(Build.SERIAL.replaceAll("[^0-9]+", ""))).toString());
	        	//Log.i("ebpps_account_hash :",ebpps_account_hash);
	        	
	        	Editor infor = setting.edit();
	        	infor.putString("apply_user", apply_user);
	        	infor.putString("tel_area_number", tel_area_number);
				infor.putString("phone_number", phone_number);
				infor.putString("ext_phone_number", ext_phone_number);
				infor.putString("email", email.split(";")[0]);
				infor.putString("mobile", mobile);
				//.putString("ebpps_account", ebpps_account_hash)
				//.putString("ebpps_password", SHA1Util.b64_sha1(ebpps_password))
				//.putBoolean("show_notification", true)
				//.putBoolean("show_again",false ).commit();
				infor.putString("last_login_time", String.valueOf(System.currentTimeMillis() / 1000l) );
				infor.commit();
	        				  
				
				//GCM 
				String GCM_electric_number_list = "";
				for(int i = 0 ; i < electric_number_list.size() ; i++)
					GCM_electric_number_list += electric_number_list.get(i) + ",";
				
				String GCM_email = email.split(";")[0];
				
				String GCM_phone_number = tel_area_number + "-" + phone_number + "#" + ext_phone_number;
				
				String GCM_mobile = mobile.toString();
				
				checkNotNull(SERVER_URL, "SERVER_URL");
		        checkNotNull(SENDER_ID, "SENDER_ID");
		        // Make sure the device has the proper dependencies.
		        GCMRegistrar.checkDevice(app_context);
		        // Make sure the manifest was properly set - comment out this line
		        // while developing the app, then uncomment it when it's ready.
		        GCMRegistrar.checkManifest(app_context);
				
		        //registerReceiver(mHandleMessageReceiver , new IntentFilter(DISPLAY_MESSAGE_ACTION));
	            
				String regId = GCMRegistrar.getRegistrationId(app_context);
				
				if (regId.equals("")) 
				{
					//Automatically registers application on startup.
					Log.i("GCM", "尚未註冊 Google GCM, 進行註冊");
					GCMRegistrar.register(app_context, SENDER_ID );
				} 
				else 
				{
					
					
					// Device is already registered on GCM, check server.
					if( GCMRegistrar.isRegisteredOnServer(app_context) ) 
					{
						// Skips registration ? No ! Update registration.
						Log.i("GCM state :", getString(R.string.already_registered) );
						Log.i("GCM state :", "try to update..." );
						
						new RegisterAsyncTask().execute("update",regId, GCM_electric_number_list, GCM_email, GCM_phone_number, GCM_mobile);
					} 
					else 
					{
						// Try to register again, but not in the UI thread.
						// It's also necessary to cancel the thread onDestroy(),
						// hence the use of AsyncTask instead of a raw thread.
						
						new RegisterAsyncTask().execute("register",regId, GCM_electric_number_list, GCM_email, GCM_phone_number, GCM_mobile);
						
						
					}
					
				}
				
			}			
			
			
			CreateLoadingDialog.dialog_dismiss(instialize_dialog);
			instialize_dialog = null;
			
			if( result == 9)
				Toast.makeText(app_context, "帳號錯誤", Toast.LENGTH_SHORT).show();
			
			
			if( result == 8)
				Toast.makeText(app_context, "密碼錯誤", Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
	class RegisterAsyncTask extends AsyncTask<String,Void,Void>
	{
		String tag_value = "";
		
		@Override
		protected Void doInBackground(String... params) 
		{
			// TODO Auto-generated method stub                          
			tag_value = params[0];
			boolean registered = false ;
			
			if( tag_value.equals("register"))
			{
				registered = ServerUtilities.register(app_context, params[1] , params[2] , params[3] , params[4], params[5]);
				// At this point all attempts to register with the app
				// server failed, so we need to unregister the device
				// from GCM - the app will try to register again when
				// it is restarted. Note that GCM will send an
				// unregistered callback upon completion, but
				// GCMIntentService.onUnregistered() will ignore it.
			
			
			}
			
			if( tag_value.equals("update"))
			{
				registered = ServerUtilities.update(app_context, params[1] , params[2] , params[3] , params[4], params[5]);
				
				
			}
			
			if (!registered) 
				GCMRegistrar.unregister(app_context);
			
			return null;
		
		}
			
		@Override
		protected void onPostExecute(Void params)
		{
			
		}
		
		
	}
	
	private void checkNotNull(Object reference, String name) 
    {
        if (reference == null) 
        {
            throw new NullPointerException( getString(R.string.error_config, name));
        }
    }
	
	private String starText(String text)
  	{
  		String star_result = "";
  		
  		for(int i = 0 ; i < text.length() ; i ++)
  		{
  			if( (i & 0x1) == 0 )
  				star_result += text.substring(i, i+1);
  			else
  				star_result += "*";
  		}
  		
  		return star_result;
  	}
	
	
}