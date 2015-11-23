package com.taipower.west_branch;

import static com.taipower.west_branch.CommonUtilities.SENDER_ID;
import static com.taipower.west_branch.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.android.gcm.GCMRegistrar;
import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;
import com.taipower.west_branch.utility.SHA1Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SettingDialog extends Activity
{
	private Context app_context;
	//private Dialog  app_dialog;
	private Activity app_activity;
	
	private View current_view;
	
	private SharedPreferences setting ;
	
	private Button input_self_button;	
	private Button input_ebpps_button;
	
	private LinearLayout setting_dialog_content_layout;
	
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
	
	
	//public SettingDialog(Context context, Activity activity)
	public SettingDialog()
	{
		this.app_context = this;
		this.app_activity = this;
	}
	
	
	public void onCreate(Bundle savedInstanceState)
	{
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_dialog);
		
		
		setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
		
		apply_user = setting.getString("apply_user","");
		tel_area_number = setting.getString("tel_area_number","");	   
		phone_number = setting.getString("phone_number","");	   
		email = setting.getString("email","");	   
		mobile = setting.getString("mobile","");	   
      	ebpps_account = setting.getString("ebpps_account","");
      	boolean show_notification = setting.getBoolean("show_notification",true);
		
      	String prefereance_state = "";
      	
      	//telephone || mobile
        if( (apply_user.equals("") || email.equals("") || (tel_area_number.equals("") && phone_number.equals("") && mobile.equals(""))) && ebpps_account.equals("") )
        {	
        	prefereance_state = "  !!未設定!!  ";
        }
        else
        {
        	if(ebpps_account.equals(""))
        		prefereance_state = "!!自行輸入!!";
        	else
        		prefereance_state = "!!電子帳單系統!!";
        }
		
        ((TextView) findViewById(R.id.user_info_state)).setText(prefereance_state);
        
		
		//current_view = app_activity.getWindow().getDecorView();
		current_view = app_activity.findViewById(android.R.id.content);
        
		
		input_self_button = (Button) findViewById(R.id.input_self_button);
		input_self_button.setOnClickListener(layout_content_on_click_listener );
		
		input_ebpps_button = (Button) findViewById(R.id.input_ebpps_button);
		input_ebpps_button.setOnClickListener(layout_content_on_click_listener );
		
		
		input_self_button.setBackgroundResource(R.drawable.send_button_background_pressed);
		
		LayoutInflater inflater = LayoutInflater.from(app_context);
		View layout_view =  inflater.inflate(R.layout.setting_dialog_self, null ,false);
		
		setting_dialog_content_layout = (LinearLayout) current_view.findViewById(R.id.setting_dialog_content_layout); 
		setting_dialog_content_layout.removeAllViews();
		setting_dialog_content_layout.addView(layout_view);
		setting_dialog_content_layout.setTag(R.id.input_self_button);
		
		
		Button login_button = (Button) findViewById(R.id.save_button);
        login_button.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
				apply_user = "";
				tel_area_number = "";
				phone_number = "";
				email = "";
				ext_phone_number = "";
				mobile = "";
				
				ebpps_account = "";
				ebpps_password = "";
				
				int setting_dialog_button_id = ((Integer) setting_dialog_content_layout.getTag()).intValue();
				
				if( setting_dialog_button_id == R.id.input_self_button )
				{
					apply_user = ((TextView) findViewById(R.id.apply_user)).getText().toString();
					tel_area_number = ((TextView) findViewById(R.id.tel_area_number)).getText().toString();
					phone_number = ((TextView) findViewById(R.id.phone_number)).getText().toString();
					email = ((TextView) findViewById(R.id.email)).getText().toString();
					ext_phone_number = ((TextView) findViewById(R.id.ext_phone_number)).getText().toString();
					mobile = ((TextView) findViewById(R.id.mobile)).getText().toString();
				}
				else
				{
					ebpps_account = ((TextView) findViewById(R.id.ebpps_account)).getText().toString();
					ebpps_password = ((TextView) findViewById(R.id.ebpps_password)).getText().toString();
				}
				
				setting.edit().putString("apply_user", apply_user)
				  			  .putString("tel_area_number", tel_area_number)
				  			  .putString("phone_number", phone_number )
				  			  .putString("email", email)
				  			  .putString("ext_phone_number", ext_phone_number)
				  			  .putString("mobile", mobile)
							  .putBoolean("show_title", false).commit();
				
				
				if( apply_user.equals("") || 
					email.equals("") || 
					(tel_area_number.equals("") && phone_number.equals("") && mobile.equals(""))  )
					setting.edit().putBoolean("show_again", true).commit();
				
				
				String error_message = "";
				
				if(ebpps_account.length() < 8 && ebpps_account.length() > 0  )
					error_message += "電子帳單帳號填寫錯誤\n";
				
				if(ebpps_password.length() < 8 && ebpps_password.length() > 0 )
					error_message += "電子帳單密碼填寫錯誤";
				
				if(error_message.equals("") && ebpps_account.length() != 0)
				{
					String ebpps_myAction_password_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=password&account=";
					ebpps_myAction_password_url += ebpps_account;
					
					String ebpps_myAction_check_PWD_url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do?myAction=check_PWD&old_hash=";
					ebpps_myAction_check_PWD_url += SHA1Util.b64_sha1(ebpps_password) + "&words2=HYCAPI_INSTEAD&pkcs=&account=&words=********";
					
					String ebpps_set_electric_list_url = "https://ebpps.taipower.com.tw/EBPPS/action/SetElectric.do";
					
					String ebpps_myAction_basic_info_list_url = "https://ebpps.taipower.com.tw/EBPPS/action/modBasicInfo.do?myAction=modBasicInfo";
					
					new doingAsyncTask().execute("login_tag", ebpps_myAction_password_url, 
															  ebpps_myAction_check_PWD_url, 
															  ebpps_set_electric_list_url, 
															  ebpps_myAction_basic_info_list_url);
					
					
				}
				else if( ebpps_account.length() != 0 )
				{
					AlertDialog.Builder error_dialog = new AlertDialog.Builder(app_context);
					error_dialog.setTitle("歐！歐！！");
					error_dialog.setMessage(error_message);
					error_dialog.setNegativeButton("重新填寫", null);
					error_dialog.show();
				}
				else
				{
					//app_dialog.dismiss();
					app_activity.finish();
				}
			}
		});
        
        
        
        Button pass_button = (Button) findViewById(R.id.pass_ebpps_Button);
        pass_button.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				setting.edit().putBoolean("show_title", false).commit();
				//app_dialog.dismiss();
				
				Intent intent = new Intent();
				intent.putExtra("result", "fail");
				app_activity.setResult(Activity.RESULT_OK,intent);
				app_activity.finish();
				
				//Log.i("class name : ",app_activity.getClass().getName());
				
				//if( app_activity.getClass().getName().equals("com.taipower_west_branch_project.barcode_and_ebpps_ebpps_light"))
				//	app_activity.finish();
			}
		});
        
        
        Button signup_ebpps = (Button) current_view.findViewById(R.id.signup_ebpps);
        signup_ebpps.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://ebpps.taipower.com.tw/EBPPS/action/register.do?myAction=regExplain"));
				app_context.startActivity(intent);
			}
		});
	}
	
	
	private OnClickListener layout_content_on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			input_self_button.setBackgroundResource(R.drawable.send_button_background_normal);
			input_ebpps_button.setBackgroundResource(R.drawable.send_button_background_normal);
			
			int layout_id ;
			
			if(v.getId() == R.id.input_self_button)			
			{	
				layout_id = R.layout.setting_dialog_self;
				input_self_button.setBackgroundResource(R.drawable.send_button_background_pressed);
			}
			else
			{
				layout_id = R.layout.setting_dialog_ebpps;
				input_ebpps_button.setBackgroundResource(R.drawable.send_button_background_pressed);
			}
			
			LayoutInflater inflater = LayoutInflater.from(app_context);
			View layout_view =  inflater.inflate(layout_id, null ,false);
			
			setting_dialog_content_layout = (LinearLayout) current_view.findViewById(R.id.setting_dialog_content_layout); 
			setting_dialog_content_layout.removeAllViews();
			setting_dialog_content_layout.addView(layout_view);
			setting_dialog_content_layout.setTag(Integer.valueOf(v.getId()));
		}
		
	};
	
	
	// dialog method override
	@Override
	public boolean onKeyDown(int key_code ,KeyEvent event)
	{
		if(key_code == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent();
			intent.putExtra("result", "fail");
			app_activity.setResult(Activity.RESULT_OK,intent);
			app_activity.finish();
		}
		//if(key_code == KeyEvent.KEYCODE_BACK && app_activity.getClass().getName().equals("com.taipower_west_branch_project.barcode_and_ebpps_ebpps_light"))
		//	app_activity.finish();
		return super.onKeyDown(key_code, event);
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
		
		
		@SuppressWarnings("deprecation")
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			Integer return_value = null;
			
			tag_value = params[0] ;  
			
			if( params[0].equals("login_tag"))
			{
				try 
				{	
					publishProgress(1);
					
					//response_data = HttpConnectResponse.apacheConnection(params[1], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
					response_data = HttpConnectResponse.onOpenConnection(params[1], "GET", null , HttpConnectResponse.COOKIE_CLEAR, HttpConnectResponse.HTTP_REDIRECT);
					
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
        			response_data = HttpConnectResponse.onOpenConnection(params[2], "GET", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_NONREDIRECT);
        			
        			login_layer0_content = new String(response_data,"UTF-8");
        			
        			lgoin_layer0 = Jsoup.parse(login_layer0_content);
        			
        			error_alert_javascript = lgoin_layer0.select("script").last();
        			
        			error_alert_string = error_alert_javascript.toString().replace("<script ", "");
					
        			//password error
        			if( error_alert_string.contains("alert") )
        				return_value = 8; 
        			else
        			{
        			
        			//response_data = HttpConnectResponse.apacheConnection(params[3], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
					response_data = HttpConnectResponse.onOpenConnection(params[3], "GET", null , HttpConnectResponse.COOKIE_CLEAR, HttpConnectResponse.HTTP_REDIRECT);
        			
        			//response_data1 = HttpConnectResponse.apacheConnection(params[4], "GET", null, HTTP.UTF_8 , HttpConnectResponse.COOKIE_KEEP);
					response_data1 = HttpConnectResponse.onOpenConnection(params[4], "GET", null , HttpConnectResponse.COOKIE_CLEAR, HttpConnectResponse.HTTP_REDIRECT);
					
        			return_value = 1;
        			
        			}
        			
        			}
        		} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
				//catch (URISyntaxException e) 
				{
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} 
			
				
			}
			
			
			
			Log.i("return value :", String.valueOf(return_value));
			return return_value;
		}
		
		Dialog present_dialog ;
		
		protected void onProgressUpdate(Integer... values)
		{
			
			if(values[0] == 1)
				present_dialog = CreateLoadingDialog.createLoadingDialog(app_context, "網路連結中", CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NONCANCELABLE);
			
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
			
		    apply_user = document_infor.select("input[name=con_contact]").val();
		    tel_area_number = document_infor.select("input[name=con_phone_area]").val();
			phone_number = document_infor.select("input[name=con_phone]").val();
			ext_phone_number = document_infor.select("input[name=con_phone_ext]").val();
			email = document_infor.select("input[name=con_email]").val();
			mobile = document_infor.select("input[name=con_mobile]").val();
		    
			String ebpps_account_hash = ebpps_account.substring(0, 1) + new BigInteger(ebpps_account.substring(1).toString()).multiply(new BigInteger(Build.SERIAL.replaceAll("[^0-9]+",""))).toString();   ;
			
			//Log.i("ebpps_account :",ebpps_account);
        	Log.i("Build.SERIAL :",BigInteger.valueOf(Integer.valueOf(Build.SERIAL.replaceAll("[^0-9]+", ""))).toString());
        	//Log.i("ebpps_account_hash :",ebpps_account_hash);
			
			setting.edit().putString("apply_user", apply_user)
						  .putString("tel_area_number", tel_area_number)
						  .putString("phone_number", phone_number)
						  .putString("ext_phone_number", ext_phone_number)
						  .putString("email", email.split(";")[0])
						  .putString("mobile", mobile)
						  .putString("ebpps_account", ebpps_account_hash)
						  .putString("ebpps_password", SHA1Util.b64_sha1(ebpps_password))
						  .putString("last_login_time", String.valueOf(System.currentTimeMillis() / 1000l))
						  .putBoolean("show_notification", true)
						  .putBoolean("show_again",false ).commit();
			
			//GCM 
			GCM_electric_number_list = "";
			for(int i =0 ; i < electric_number_list.size() ; i++)
				GCM_electric_number_list += electric_number_list.get(i) + ",";
			
			GCM_email = email.split(";")[0];
			GCM_phone_number = tel_area_number + "-" + phone_number + "#" + ext_phone_number;
			GCM_mobile = mobile.toString();
			
			checkNotNull(SERVER_URL, "SERVER_URL");
	        checkNotNull(SENDER_ID, "SENDER_ID");
	        // Make sure the device has the proper dependencies.
	        GCMRegistrar.checkDevice(app_context);
	        // Make sure the manifest was properly set - comment out this line
	        // while developing the app, then uncomment it when it's ready.
	        GCMRegistrar.checkManifest(app_context);
			
	        //registerReceiver(mHandleMessageReceiver , new IntentFilter(DISPLAY_MESSAGE_ACTION));
            
			final String regId = GCMRegistrar.getRegistrationId(app_context);
			
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
			
				//app_dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("result","OK");
				app_activity.setResult(Activity.RESULT_OK, intent);//pass result to 
				app_activity.finish();
			
			
			
			}
			
			CreateLoadingDialog.dialog_dismiss(present_dialog);
			present_dialog = null;
			
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
	
	
}