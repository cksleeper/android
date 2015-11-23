package com.taipower.west_branch;

import static com.taipower.west_branch.CommonUtilities.SENDER_ID;
import static com.taipower.west_branch.CommonUtilities.SERVER_URL;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gcm.GCMRegistrar;
import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.NoTitleBar;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends Activity
{
	private Context app_context;
	private Activity app_activity;
	
	public int hour_of_24;
	public Dialog PRESENT_DIALOG_POINTER;
	
	private SharedPreferences setting;
	
	private FragmentManager fm;
	
	public MainPage()
	{
		this.app_context = this;
		this.app_activity = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
		
		super.onCreate(savedInstanceState);
		app_activity.setContentView(R.layout.main_page);
		
		setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE);
		
		//if( !ASaBuLuCheck.dayOrNight() )
        //	findViewById(R.id.main_page_layout).setBackgroundResource(R.drawable.main_background);
		
		
        ((ImageButton) findViewById(R.id.title_bar_back_button)).setOnClickListener(on_click_listener);
        ((ImageButton) findViewById(R.id.title_bar_menu_button)).setOnClickListener(on_click_listener);
        
		//initialization
		fm = app_activity.getFragmentManager();  
		FragmentTransaction transaction = fm.beginTransaction();  
		MainPageFragment fragment = new MainPageFragment();  
		transaction.replace(R.id.fragment_content, fragment,"main_page");  
        transaction.commit();   
        
        //GCM
        //startGCMRegister();
	}
	
	public void ebppsLoginCount()
	{
		Handler handler = new Handler();
    	Runnable running_waiting = new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
    			setting.edit().putString("last_login_time", "0").commit();
    			Log.i("last_login_time","reset after 900 seconds");
    		}	
    	};
    	
    	handler.postDelayed(running_waiting, 900000);
	}
	
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v.getId() == R.id.title_bar_back_button)
			{
				Log.i("back on main","main");
				app_activity.finish();
			}
			
			if( v.getId() == R.id.title_bar_menu_button )
			{
				Intent intent = new Intent();
				intent.setClass(app_context, SettingDialog.class);
				app_activity.startActivity(intent);
			}
		}
	};
	
	private OnTouchListener on_touch_listener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{	
			//v.startAnimation(button_animation);
			return false;
		}
	}; 
	
	
	private boolean double_touch = false;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//no effective
		boolean indicate = false;
		
		Log.i("keyCode","" + keyCode);
		
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			String fragment_tag = app_activity.getFragmentManager().findFragmentById(R.id.fragment_content).getTag();
		
			Log.i("fragment on activity ",fragment_tag);
        
			Fragment fragment;
        
			if( fragment_tag.equals("rss_reader") || fragment_tag.equals("taipower_tv") || fragment_tag.equals("second_layer") || 
        	fragment_tag.equals("service_map") || fragment_tag.equals("calc_light") || fragment_tag.equals("ebpps_light"))
			{  
				FragmentTransaction transaction = fm.beginTransaction();  
				fragment = new MainPageFragment();  
				transaction.replace(R.id.fragment_content, fragment, "main_page");  
				transaction.commit();
				
				indicate = true;
			}
        
			if( fragment_tag.startsWith("about_meter") || fragment_tag.startsWith("progress_state") || 
				fragment_tag.startsWith("online_service") || fragment_tag.startsWith("no_power") )
			{  
				Bundle bundle = new Bundle();
				
				if(fragment_tag.startsWith("about_meter") )
					bundle.putString("second_layer_content", "about_meter");
				else if(fragment_tag.startsWith("progress_state") )	
					bundle.putString("second_layer_content", "progress_state");
				else if(fragment_tag.startsWith("no_power") )	
					bundle.putString("second_layer_content", "no_power");
				else
					bundle.putString("second_layer_content", "online_service");
				
				FragmentTransaction transaction = fm.beginTransaction();  
				fragment = new FragmentSecondLayerMenu();  
				fragment.setArguments(bundle);
				transaction.replace(R.id.fragment_content, fragment,"second_layer").commit();  
            
				indicate = true;
			}
        
			Toast toast = Toast.makeText(app_context, "要離開嗎？請再按一次 >_* ", Toast.LENGTH_SHORT);

			if(double_touch)
			{	
				double_touch = false;
				toast.cancel();
				indicate = super.onKeyDown(keyCode, event);	
			}
        
			if( fragment_tag.equals("main_page") )
			{	
				if( keyCode == KeyEvent.KEYCODE_BACK  && !double_touch)
				{	
					toast.show();
					double_touch = true;
        		
					Thread wait = new Thread(wait_two_second); 
					wait.start();
				}
			}
			
			//Log.i("indicate",String.valueOf(indicate));
		}
		
		return indicate;
	}
	
	private boolean keyboard_status = false;
	
	private void keyboardIsShow()
	{	
		final View activityRootView = findViewById(R.id.main_page_layout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
		{
		    @Override
		    public void onGlobalLayout() 
		    {
		        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
		        
		        //Log.i("heightDiff",""+heightDiff);
		        
		        if (heightDiff > 75 ) 
		        { // if more than 100 pixels, its probably a keyboard...
		        	keyboard_status = true;
		        }
		        else
		        	keyboard_status = false;
		     }
		});
	}
	
	private Runnable wait_two_second = new Runnable()
	{
		@Override
		public void run()
		{
			try 
			{
				Thread.sleep(2000);
			} catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Log.i("wait time","2 seconds");
			double_touch = false;
		}
	};
	
	@Override
	protected void onDestroy()
	{
		setting.edit().putString("last_login_time", "0").commit();
		
		Log.i("last_login_time","reset");
		
		super.onDestroy();
	}
	
	private void startGCMRegister()
    {
    	checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(app_context);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(app_context);
		
        //registerReceiver(mHandleMessageReceiver , new IntentFilter(DISPLAY_MESSAGE_ACTION));
		final String reg_id = GCMRegistrar.getRegistrationId(app_context);
		
		if (reg_id.equals("")) 
		{
			//Automatically registers application on startup.
			Log.i("GCM", "unregistrate Google GCM, registrating");
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
				
				//new RegisterAsyncTask().execute("register",reg_id);
			} 
			else 
			{
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				
				new RegisterAsyncTask().execute("register",reg_id);	
			}		
		}
    }
    
    private class RegisterAsyncTask extends AsyncTask<String,Void,Void>
	{
		@Override
		protected Void doInBackground(String... params) 
		{
			// TODO Auto-generated method stub                          
			String tag_value = params[0];
			boolean registered = false ;
			
			if( tag_value.equals("register"))
			{
				//registered = ServerUtilities.register(app_context, params[1] , params[2] , params[3] , params[4], params[5]);
				registered = ServerUtilities.register(app_context, params[1]);
				// At this point all attempts to register with the app
				// server failed, so we need to unregister the device
				// from GCM - the app will try to register again when
				// it is restarted. Note that GCM will send an
				// unregistered callback upon completion, but
				// GCMIntentService.onUnregistered() will ignore it.
			
				if(registered)
					Log.i("GCM","registered");
			}
			
			if( tag_value.equals("update"))
			{
				//registered = ServerUtilities.update(app_context, params[1] , params[2] , params[3] , params[4], params[5]);//
				registered = ServerUtilities.register(app_context, params[1]);
				
				if(registered)
					Log.i("GCM","registered");
			}
			
			//if (!registered) 
			//	GCMRegistrar.unregister(app_context);
			
			return null;
		}
	}
    
    private void checkNotNull(Object reference, String name) 
    {
        if (reference == null) 
        	throw new NullPointerException( getString(R.string.error_config, name));
    }
}
