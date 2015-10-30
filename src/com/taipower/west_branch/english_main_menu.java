package com.taipower.west_branch;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class english_main_menu extends Activity 
{
	private Context app_context;
	private Activity app_activity;
	
	public english_main_menu()
	{
		app_context = this;
		app_activity = this;
	}
	

	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.english_main_menu);
        
        
        int linear_height = new DmInfor(this,this).menu_linear_height;
         
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
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
        
        ImageButton menu_button = (ImageButton ) findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(); 
        		intent.setClass(app_context, setting_menu.class);
        		startActivity(intent);
        	}
        });
        
        Button online_search_service_map_english = (Button) findViewById(R.id.online_search_service_map_english);
        online_search_service_map_english.setOnClickListener(on_click_listener);
        
        Button online_servie_calc_light_english = (Button) findViewById(R.id.online_search_pay_state_english);
        online_servie_calc_light_english.setOnClickListener(on_click_listener);
        
        Button customer_problems_english = (Button) findViewById(R.id.customer_problems_english);
        //customer_problems_english.setOnClickListener(on_click_listener);
        
        Button barcode_and_ebpps_english = (Button) findViewById(R.id.barcode_and_ebpps_english);
        barcode_and_ebpps_english.setOnClickListener(on_click_listener);
        
        Button online_search_calc_light_english = (Button) findViewById(R.id.online_search_calc_light_english);
        online_search_calc_light_english.setOnClickListener(on_click_listener);
    }
	
	
	
	private View.OnClickListener on_click_listener = new View.OnClickListener() 
	{
		public void onClick(View view) 
		{
			/*
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("version", "english");
			intent.putExtras(bundle);
			
			//if( view.getId() == R.id.online_search_service_map_english )
			//    intent.setClass(app_context, OnlineSearchServiceMap.class);
		    		
			//if( view.getId() == R.id.online_search_pay_state_english )
			//	intent.setClass(app_context, online_search_pay_state.class);
			
		    
			//if( view.getId() == R.id.customer_problems_english )
				//intent.setClass(app_context, online_service_menu.class);
			
			//if( view.getId() == R.id.barcode_and_ebpps_english )
			//	intent.setClass(app_context, barcode_and_ebpps_menu.class);
			
			if( view.getId() == R.id.online_search_calc_light_english )
			//	intent.setClass(app_context, OnlineSearchCalcLight.class);
			
			
			startActivity(intent);
		*/
		}
	};
	

}
