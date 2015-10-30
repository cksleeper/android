package com.taipower.west_branch;

import com.google.android.gcm.GCMRegistrar;
import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class setting_menu extends Activity 
{  
    
	private Context app_context;
	private Activity app_activity;
	
	
	private int[] title_icons;
	private String[] title_mane;
	private String[] state;
	
	private SharedPreferences setting; 
	
	private String apply_user;
	private String tel_area_number;	   
	private String phone_number;	   
	private String email;	   
	private String mobile;	
	private String ebpps_account;
	
	public setting_menu()
	{
		app_context = this;
		app_activity = this;
	}
	
	
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) 
    {  
        
    	new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
    	
    	super.onCreate(savedInstanceState);  
  
        setContentView(R.layout.setting_menu);  
  
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  new DmInfor(this,this).menu_linear_height ));
        
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        
        back_button.setOnClickListener(
        		new View.OnClickListener() 
        		{
        			public void onClick(View v) 
        			{
        				finish();
        			}
        		}
        );
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
        
        
        
        title_mane = new String[]{"個人資料與通知服務設定",
        				 		  "基本資料",
        				 		  "通知服務",
        				 		  "訊息相關",
        				 		  "訊息清單",
        				 		  "問題意見回饋",
        				 		  "電子郵件連絡我們",
        				 		  "電話聯絡我們"}; 
        
        state = new String[]{"","","","","","","",""};
        
        
        title_icons = new int[]{0,
        				  R.drawable.ic_perm_identity_onclick,
        			   	  R.drawable.ic_messenger_onclick,
        			   	  0,
        			   	  R.drawable.ic_message_onclick,
        			   	  0,
        			   	  R.drawable.ic_email_onclick,
        			   	  1};
        
        
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
        	state[1] = "未設定";
        else
        	state[1] = "已設定";
        
        
        String regId = GCMRegistrar.getRegistrationId(app_context);
        
        if( regId.equals(""))
        	state[2] = "未啟用";
        else
        	state[2] = "啟用";
        
        
        IconTextAdapter realtime_adapter = new IconTextAdapter(app_context , android.R.layout.simple_list_item_1 , title_mane , state , title_icons);
        
                
        ListView online_service_menu_list_view = (ListView) findViewById(R.id.setting_menu_list_view);
        online_service_menu_list_view.setAdapter( realtime_adapter);
        online_service_menu_list_view.setOnItemClickListener(on_item_click_listener);
        online_service_menu_list_view.setSelector(R.color.item_list);
        
        
    }  
    		
    
    public class IconTextAdapter extends ArrayAdapter<Object>
    {
        int[] icons;
        String[] textname , textpostion;
        String[] text_state;
        
        public IconTextAdapter(Context context, int textViewResourceId, String[] itemname ,String[] item_state ,  int[] images)
        {
        	super(context, textViewResourceId, itemname);
        	icons = images;
        	textname = itemname;
        	text_state = item_state;
        }
        
        
       
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
        {
			View setting_state_view = convertView;
            View subtitle_view = convertView;
            
            LayoutInflater inflater = app_activity.getLayoutInflater();
            
            //if (row_view == null) 
            {
            	setting_state_view = inflater.inflate(R.layout.setting_menu_setting_state_view, parent, false);
            	
            	ImageView setting_icon = (ImageView) setting_state_view.findViewById(R.id.setting_icon);
            	setting_icon.setImageResource(icons[position]);
            	
            	TextView setting_name = (TextView) setting_state_view.findViewById(R.id.setting_name);
            	setting_name.setText(textname[position]);
            	//Nmae.setTextColor(R.color.item_list);
        	
            	TextView setting_state = (TextView) setting_state_view.findViewById(R.id.setting_state);
            	setting_state.setText(text_state[position]);
            	
            	
            	
            	subtitle_view = inflater.inflate(R.layout.setting_menu_subtitle_view, parent, false);
            	TextView setting_subtitle_name = (TextView) subtitle_view.findViewById(R.id.setting_subtitle_name);
            	setting_subtitle_name.setText(textname[position]);
            	setting_subtitle_name.setBackgroundColor(0xFFD3D3D3);
            	
            	
            	
            }
            
            if(icons[position] == 0)
            	return subtitle_view;
            else
            	return setting_state_view;
        }
     }
    
    
    private OnItemClickListener on_item_click_listener = new OnItemClickListener()
    {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
    	{
			// TODO Auto-generated method stub
			
    		
			//if( ASaBuLu_check.isOnline(app_activity) )
    		{	
    			
				if(position != 0 && position != 3)
				{
				Intent intent = new Intent();
    			//Bundle bundle = new Bundle();
    			
    			switch(position)
    			{
    				case 1:
    					intent.setClass(app_context, setting_apply_user.class);
    					break;
    				case 2:
    					intent.setClass(app_context, setting_gcm.class);
    					break;
    				case 4:
    					intent.setClass(app_context, setting_notification_list.class);
    					break;
    				case 6:
    					intent.setAction(Intent.ACTION_SEND_MULTIPLE);
    					String[] email_address_array = new String[] {"d1170304@taipower.com.tw"} ;
    					intent.putExtra(Intent.EXTRA_EMAIL, email_address_array);
    					intent.putExtra(Intent.EXTRA_SUBJECT, "意見回饋"); 
    					intent.setType("image/*"); 
    					startActivity(Intent.createChooser(intent, "選擇電子郵件客戶端"));
    					break;
    				case 7:
    					intent.setAction(Intent.ACTION_DIAL);
    					intent.setData(Uri.parse("tel:0229915511"));
    			}
    				
    			if(position != 6 && position != 2)
    			startActivity(intent);
				}
    			
    		}
    		/*
    		else
    		{
    			new AlertDialog.Builder(app_context).setTitle("歐歐!!沒有連上網際網路")
				 .setMessage("沒有偵測到網路環境\n請查看網路設定")
				 .setNeutralButton("揪咪", null)
				 .show();
    		}
    		*/
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
			state[1] = "未設定";
		else
			state[1] = "已設定";


		String regId = GCMRegistrar.getRegistrationId(app_context);

		if( regId.equals(""))
			state[2] = "未啟用";
		else
			state[2] = "啟用";

		IconTextAdapter realtime_adapter = new IconTextAdapter(app_context , android.R.layout.simple_list_item_1 , title_mane , state , title_icons);
		
		ListView online_service_menu_list_view = (ListView) findViewById(R.id.setting_menu_list_view);
		online_service_menu_list_view.setAdapter( realtime_adapter);
		online_service_menu_list_view.setOnItemClickListener(on_item_click_listener);
		online_service_menu_list_view.setSelector(R.color.item_list);
    }
       

}  