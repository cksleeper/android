package com.taipower.west_branch;


import com.taipower.west_branch.utility.ASaBuLuCheck;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;



public class FragmentSecondLayerMenu extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private FragmentSecondLayerMenu this_class;
	
	private View current_view;
	
	private FragmentTransaction transaction;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {
		this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		this.this_class = this;
		
		transaction = app_activity.getFragmentManager().beginTransaction();
		
		current_view = inflater.inflate(R.layout.fragment_second_layer_menu, container, false);
		
		ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setVisibility(View.INVISIBLE);
		title_bar_send_button.setOnClickListener(null);
		
		ScrollView scroll_view = (ScrollView) findViewById(R.id.second_layer_scroll_view);
		
		Bundle bundle = this.getArguments();
		String second_layer_tag = bundle.getString("second_layer_content");
		
		Resources res = app_context.getResources();
		
		String[] item_title_array = null;
		String[] item_description_array = null;
		
		int title_bar_title = 0;
		
		if( second_layer_tag.equals("about_meter") )
		{
			item_title_array = res.getStringArray(R.array.about_meter_title);
			item_description_array = res.getStringArray(R.array.about_meter_description);
			
			title_bar_title = R.drawable.title_bar_about_meter;
			
			scroll_view.removeAllViews();
		}
		else if(second_layer_tag.equals("progress_state"))
		{
			item_title_array = res.getStringArray(R.array.progress_state_title);
			item_description_array = res.getStringArray(R.array.progress_state_description);
			
			title_bar_title = R.drawable.title_bar_progress_state;
			
			scroll_view.removeAllViews();
			
			//second_layer_list_view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 48 * 5 ));
		}
		else if(second_layer_tag.equals("contact_us"))
		{
			item_title_array = res.getStringArray(R.array.contact_us_title);
			item_description_array = res.getStringArray(R.array.contact_us_description);
			
			title_bar_title = R.drawable.title_bar_contact_us;
			
			 ((ImageButton) findViewById(R.id.second_layer_service_map_button)).setOnClickListener(new View.OnClickListener() 
			 {
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					Fragment fragment = new FragmentServiceMap();
					transaction.replace(R.id.fragment_content, fragment, "service_map").commit();
				}
			 });
		}
		else if(second_layer_tag.equals("online_service"))
		{
			item_title_array = res.getStringArray(R.array.online_service_title);
			item_description_array = res.getStringArray(R.array.online_service_description);
					
			title_bar_title = R.drawable.title_bar_online_service;
			
			scroll_view.removeAllViews();
			
		}
		else if(second_layer_tag.equals("no_power"))
		{
			item_title_array = res.getStringArray(R.array.no_power_title);
			item_description_array = res.getStringArray(R.array.no_power_description);
					
			title_bar_title = R.drawable.title_bar_no_power;
			
			scroll_view.removeAllViews();
			
		}
		
		((LinearLayout) findViewById(R.id.title_bar_layout)).setBackgroundResource(R.drawable.title_bar_second_layer_background);
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(title_bar_title);
		
		ImageButton title_bar_back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
		title_bar_back_button.setBackgroundResource(R.drawable.title_bar_second_layer_back_button);
		title_bar_back_button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Fragment fragment = new MainPageFragment();
				transaction.replace(R.id.fragment_content, fragment, "main_page");
				transaction.commit();
			}
		});
		
		IconTextAdapter coustomer_adapter = new IconTextAdapter(app_context,android.R.layout.simple_list_item_1 ,item_title_array ,item_description_array);
		
		ListView second_layer_list_view  = (ListView) current_view.findViewById(R.id.second_layer_list_view);
		second_layer_list_view.setAdapter(coustomer_adapter);
		second_layer_list_view.setTag(second_layer_tag);
		second_layer_list_view.setOnItemClickListener(on_item_click_listener);
		
		return current_view;
    }
	
	private View findViewById(int id)
	{
		View view = current_view.findViewById(id);
		
		if( view == null)
			view = app_activity.findViewById(id);
		
		return view;
	}
	
	private class IconTextAdapter extends ArrayAdapter<Object>
    {
        String[] item_title;
        String[] item_description;
        
        public IconTextAdapter(Context context, int textViewResourceId, String[] title, String[] description)
        {
        	super(context, textViewResourceId, title);
        	this.item_title = title;
        	this.item_description = description; 
        }
        
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
        {
			//super.getView(position, convertView, parent);
			
			//Log.i("position","" + position);
			
            convertView = app_activity.getLayoutInflater().inflate(R.layout.fragment_second_layer_item, parent, false);
            
            
            TextView item_title_text_view = (TextView) convertView.findViewById(R.id.item_title);
            item_title_text_view.setText(item_title[position].toString());
            
            TextView item_description_textview = (TextView) convertView.findViewById(R.id.item_description);
            item_description_textview.setText(item_description[position].toString());
            
            if( item_title[position].toString().contains("台電"))
            {	
            	ImageView item_indicate_imageview = (ImageView) convertView.findViewById(R.id.item_indicate);
            	item_indicate_imageview.setBackgroundResource(R.drawable.second_layer_menu_contact_us);
            	
            	//if(!ASaBuLuCheck.dayOrNight())
            	//	item_indicate_imageview.setBackgroundResource(R.drawable.second_layer_menu_contact_us_night);
            }
            
            if(!ASaBuLuCheck.dayOrNight())
            {	
            	//item_title_text_view.setTextColor(Color.CYAN);
            	//item_description_textview.setTextColor(Color.WHITE);
            }
            
            
            return convertView;
        }
     }
	
	private OnItemClickListener on_item_click_listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			// TODO Auto-generated method stub
			
			String tag = (String) parent.getTag();
			
			if( tag.equals("contact_us") )
			{
				String[] phone_number = new String[]{"tel:1911","tel:0229915511"};
				
				Intent intent = new Intent(android.content.Intent.ACTION_DIAL,Uri.parse(phone_number[position]));
				app_activity.startActivity(intent);
					
			}	
			else
			{
				String[] about_meter_fragment_tag = new String[]{"about_meter_final_bill","about_meter_no_read"};
					
				String[] progress_state_fragment_tag = new String[]{"progress_state_pay_state","progress_state_progress_state","progress_state_onpower"};
				Fragment[] progress_state_fragment = new Fragment[]{new FragmentPayState(), new FragmentProgress(), new FragmentWebBrowser()};
				
				String[] online_service_fragment_tag = new String[]{"online_service_business_id","online_service_cancel_auto_pay",
																	"online_service_cancel_auto_pay_receipt","online_service_commu_add","online_service_finance",
																	"online_service_meter_check"};
				Fragment[] online_service_fragment = new Fragment[]{new FragmentOSBusinessId(), new FragmentOSCancelAutoPay(), new FragmentOSCancelAutoPayReceipt(),
																	new FragmentOSCommuAdd(), new FragmentOSFinance(), new FragmentOSMeterCheck()};
				
				Fragment third_layer_fragment = new Fragment();
				Bundle bundle = new Bundle();
				
				String fragment_tag = "";
				
				if( tag.equals("online_service") )
				{
					if( position == online_service_fragment_tag.length  )
					{
						AlertDialog.Builder privacy_policy_dialog = new AlertDialog.Builder(app_context);
						privacy_policy_dialog.setTitle("網路櫃檯隱私權政策");
						privacy_policy_dialog.setMessage(R.string.os_privacy_policy);
						privacy_policy_dialog.setNeutralButton("確定", null);
						privacy_policy_dialog.show();
					}
					else
					{
					bundle.putInt("item", position);
					
					third_layer_fragment = online_service_fragment[position];
					third_layer_fragment.setArguments(bundle);
					
					fragment_tag = online_service_fragment_tag[position];
					}
				}
				
				if( tag.equals("about_meter") )
				{
					bundle.putInt("item", position);
					
					third_layer_fragment = new FragmentSendEmailAboutMeter();
					third_layer_fragment.setArguments(bundle);
					
					fragment_tag = about_meter_fragment_tag[position];
				}		
					
				if( tag.equals("progress_state") )
				{
					if(position > 1)
					{
						String[] url_array = new String[]{"http://nds.taipower.com.tw/ndsweb/ndft112m.aspx",
								  						  "http://nds.taipower.com.tw/ndsweb/ndft127m_1.aspx",
			  					  						  "http://nds.taipower.com.tw/ndsweb/ndft137m.aspx"};
		
						bundle.putString("url", url_array[position - 2]);						
					}
					
					if( position > 1)
						position = 2;
						
					
					third_layer_fragment = progress_state_fragment[position];
					third_layer_fragment.setArguments(bundle);
					
					fragment_tag = progress_state_fragment_tag[position];		
					
				}
				
				if( tag.equals("no_power") )
				{
						String[] url_array = new String[]{"http://nds.taipower.com.tw/ndsweb/ndft112m.aspx",
								  						  "http://nds.taipower.com.tw/ndsweb/ndft127m_1.aspx",
			  					  						  "http://nds.taipower.com.tw/ndsweb/ndft137m.aspx"};
		
						bundle.putString("url", url_array[position]);						
					
					
					third_layer_fragment = new FragmentWebBrowser();
					third_layer_fragment.setArguments(bundle);
					
					fragment_tag = "no_power_web_browser";		
				}
				
				
				
				if( position != online_service_fragment_tag.length ) //for privacy policy
					transaction.replace(R.id.fragment_content, third_layer_fragment, fragment_tag).commit();
			}
			
		}
		
	};
	
}
