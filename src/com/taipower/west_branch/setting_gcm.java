package com.taipower.west_branch;

import static com.taipower.west_branch.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.taipower.west_branch.CommonUtilities.EXTRA_MESSAGE;
import static com.taipower.west_branch.CommonUtilities.SENDER_ID;
import static com.taipower.west_branch.CommonUtilities.SERVER_URL;

import com.google.android.gcm.GCMRegistrar;
import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;




public class setting_gcm extends Activity
{
	private Context app_context;
	private Activity app_activity;
	
	String electric_number = "";
	String phone_number = "";
	String email = "";
	
	String regId = "";
	
	TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;
	
	protected setting_gcm()
	{
		app_context = this;
		app_activity = this;
	}
    
    @Override
	protected void onCreate(Bundle savedInstanceState)
	{
    	new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
				
		super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_gcm);
        
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
		
        
        this.checkNotNull(SERVER_URL, "SERVER_URL");
        this.checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(app_context);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(app_context);
        
        regId = GCMRegistrar.getRegistrationId(app_context);
        
        
        ToggleButton gcm_service_toggleButton = (ToggleButton) findViewById(R.id.gcm_service_toggleButton);
        
        if (regId.equals("")) 
		{
			//Automatically registers application on startup.
			Log.d("GCM", "尚未註冊 Google GCM, 進行註冊");
			//GCMRegistrar.register(app_context, SENDER_ID );
			gcm_service_toggleButton.setChecked(false);
			gcm_service_toggleButton.setTextOff("未開啟");
		} 
        else
        {
        	gcm_service_toggleButton.setChecked(true);
			gcm_service_toggleButton.setTextOff("已開啟");
        }
        
        
        
        gcm_service_toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
        	@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
        	{
				// TODO Auto-generated method stub
        		if(isChecked)
        		{
        			electric_number = "";
    				phone_number = "";
    				email = "";
    				
    				
    				TextView electric_number_view = (TextView) findViewById(R.id.electric_number);
    				TextView phone_number_view = (TextView) findViewById(R.id.phone_number);
    				TextView email_view = (TextView) findViewById(R.id.email);
    				
    				electric_number = electric_number_view.getText().toString();
    				phone_number = phone_number_view.getText().toString();
    				email = email_view.getText().toString();
    				
    				registerReceiver(mHandleMessageReceiver,   new IntentFilter(DISPLAY_MESSAGE_ACTION));
                    
    				//final String regId = GCMRegistrar.getRegistrationId(app_context);
    	        
    				if (regId.equals("")) 
    				{
    					//Automatically registers application on startup.
    					Log.d("GCM", "尚未註冊 Google GCM, 進行註冊");
    					GCMRegistrar.register(app_context, SENDER_ID );
    				} 
    				else 
    				{
    					// Device is already registered on GCM, check server.
    					if ( GCMRegistrar.isRegisteredOnServer(app_context) ) 
    					{
    						// Skips registration.
    						mDisplay.append(getString(R.string.already_registered) + "\n");
    					} 
    					else 
    					{
    						// Try to register again, but not in the UI thread.
    						// It's also necessary to cancel the thread onDestroy(),
    						// hence the use of AsyncTask instead of a raw thread.
    						final Context context = app_context;
    						mRegisterTask = new AsyncTask<Void, Void, Void>() 
    						{

    							@Override
    							protected Void doInBackground(Void... params) 
    							{
    								boolean registered = ServerUtilities.register(context, regId , electric_number , email , phone_number,"");
    								// At this point all attempts to register with the app
    								// server failed, so we need to unregister the device
    								// from GCM - the app will try to register again when
    								// it is restarted. Note that GCM will send an
    								// unregistered callback upon completion, but
    								// GCMIntentService.onUnregistered() will ignore it.
    								if (!registered) 
    								{
    									GCMRegistrar.unregister(context);
    								}
    								return null;
    							}
    							
    							@Override
    							protected void onPostExecute(Void result)
    							{
    								mRegisterTask = null;
    							}
    							
    						};
    						
    						mRegisterTask.execute(null, null, null);
    					}
    					
    				}
    				
    				
        		}
        		else
        		{
        			// TODO Auto-generated method stub
    				regId = GCMRegistrar.getRegistrationId(app_context);
    				
    				GCMRegistrar.unregister(app_context );
    			
    				//ServerUtilities.unregister(DemoActivity.this , regId);
        		}
			}
        	
        	
        });    
        
        
        
        
        
        mDisplay = (TextView) findViewById(R.id.display);
        
        
        
        
        Button registered = (Button) findViewById(R.id.registered_button);
        registered.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				
			
    
			}
		});		
        
        Button unregistered = (Button) findViewById(R.id.unregistered_button);
        unregistered.setOnClickListener( new View.OnClickListener() 
        {
			@Override
			public void onClick(View v)
			{
				
			}
        });
        
        
        
	
	}
	
	
	@Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }
	
    private void checkNotNull(Object reference, String name) 
    {
        if (reference == null) 
        {
            throw new NullPointerException(getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            mDisplay.append(newMessage + "\n");
        }
    };
	
}