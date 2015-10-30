package com.taipower.west_branch;

import java.util.Calendar;
import java.util.Date;

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
}
