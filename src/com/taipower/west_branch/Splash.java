package com.taipower.west_branch;


import java.io.File;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.NoTitleBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
  



public class Splash extends Activity 
{  
    
	private Context app_context;
	private Activity app_activity;
	
	private SharedPreferences setting ;
	
	private boolean english_service_touch = false;
	
	private Thread auto_skip_thread;
	private boolean auto_skip_check = true; 
	
	public Splash()
	{
		this.app_context = this;
		this.app_activity = this;	
	}
	
	
	//private View dialog_view;
	//private Dialog login_dialog;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) 
	{  
		/*
		 * for ebpps login check
		 */
		
		if( !checkDBexist() )
			new doingAsyncTask().execute("database_tag");
		
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
				
		super.onCreate(savedInstanceState);  
        setContentView(R.layout.splash);
            
        setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
        
        /*
        if(setting.getBoolean("show_title", true))
        {	
        	//new SettingDialog(app_context, app_activity);
        	Intent intent = new Intent();
        	intent.setClass(app_context, SettingDialog.class);
        	startActivity(intent);
        }
        */
        
        
        auto_skip_thread = new Thread(new Runnable()
        {
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub			
				try 
				{
					Thread.sleep(3000);
						
					if(auto_skip_check)
						doFunction(true);	
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("thread","interrupted");
				}
			}
        });
        auto_skip_thread.start();
        
        
        /*
    	Handler handler = new Handler();
    	
    	Runnable running_delay = new Runnable() 
    	{
    		//
    		//Showing splash screen with a timer. This will be useful when you
    		//want to show case your app logo / company
    		//
    		
    		@Override
    		public void run() 
    		{
    			if(!english_service_touch)
    			doFunction(skip_touch);
    		}
    		
    	};
    	
    	handler.postDelayed(running_delay, 5000);
    	*/
    	
    	TextView show_touch_flash = (TextView) findViewById(R.id.show_touch_flash);
    	
    	Animation anim0 = new AlphaAnimation( 0.0f , 1.0f );
    	anim0.setRepeatCount(Animation.INFINITE);
    	anim0.setRepeatMode(Animation.REVERSE);
    	anim0.setDuration(500); //You can manage the time of the blink with this parameter
    	anim0.setStartOffset(0);
    	
    	Animation anim1 = new AlphaAnimation( 0.0f , 1.0f );
    	anim1.setRepeatCount(Animation.INFINITE);
    	anim1.setRepeatMode(Animation.REVERSE);
    	anim1.setDuration(500); //You can manage the time of the blink with this parameter
    	anim1.setStartOffset(500);
    	
    	show_touch_flash.setAnimation(anim0);
    	
    	Button show_english_service = (Button) findViewById(R.id.show_english_service);
    	show_english_service.setAnimation(anim1);
    	show_english_service.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
				/*
				english_service_touch = true;
				
				Intent intent = new Intent();
				intent.setClass(app_context, MainPage.class);
				startActivity(intent);
				finish();
				*/
			}
		});
	} 
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN && !english_service_touch)
		{
			auto_skip_check = false;
			auto_skip_thread.interrupt(); //let thread throw interrupt exception
			doFunction(true);
			
			return true;
		}	
        
        //Log.i("touch", "" + String.valueOf( event.getAction() )  );
        //return super.onTouchEvent(event);
        return false;
	}
	
	private void doFunction(boolean skip_touch)
	{
		if(skip_touch)
		{
			//Intent intent = new Intent(app_context, MainActivity.class);
			Intent intent = new Intent(app_context, MainPage.class);
			startActivity(intent);
			finish();
		}
	}
	
	
	private boolean checkDBexist()
	{
		File db_file = app_context.getDatabasePath("taipower_west_branch_app_data_base.db");
		
		return db_file.exists();
		//boolean is_exist = db_file.exists();
		
		//return is_exist; 
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
				
			if( params[0].equals("database_tag"))
			{
				publishProgress(0);
				//Get fish and wash pants
				//Create DB and save ebpps list
				new DBonSQLite( app_context, "taipower_west_branch_app_data_base.db",null,1);
				
				return_value = 0;
			}
			
			//Log.i("return value :", String.valueOf(return_value));
			return return_value;
		}
		
		//Dialog instialize_dialog ;
		
		@Override
		protected void onProgressUpdate(Integer... values)
		{
			//if(values[0] == 0)
			//	instialize_dialog = Create_loading_dialog.create_loading_dialog(app_context, "資料設定中", Create_loading_dialog.NON_DOWNLOAD_TAG, Create_loading_dialog.NON_DOWNLOAD_TAG);
			
			//if(values[0] == 1)
			//	instialize_dialog = Create_loading_dialog.create_loading_dialog(app_context, "網路連結中", Create_loading_dialog.NON_DOWNLOAD_TAG, Create_loading_dialog.NON_DOWNLOAD_TAG);
			
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			
			
		}
	}
	
	
	
	
	
	
	
	
}  