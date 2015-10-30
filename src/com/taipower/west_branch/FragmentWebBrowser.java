package com.taipower.west_branch;

import com.taipower.west_branch.utility.CreateLoadingDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentWebBrowser extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	
	private View current_view; 
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {
		
		this.app_activity = this.getActivity();
		this.app_context = this.getActivity();
		
		Bundle bundle = this.getArguments();
		String url_string = bundle.getString("url");
		
		current_view = inflater.inflate(R.layout.fragment_web_browser, container, false);
		
		ImageView title_bar_main_title = (ImageView) findViewById(R.id.title_bar_main_title);
		
		if(  url_string.contains("ndft112m.aspx") )
			title_bar_main_title.setBackgroundResource(R.drawable.title_bar_no_power_search);
		else if(  url_string.contains("ndft137m.aspx") )
			title_bar_main_title.setBackgroundResource(R.drawable.title_bar_no_power_case_search);
			
		
		ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
    	title_bar_send_button.setVisibility(View.INVISIBLE);
    	title_bar_send_button.setBackgroundResource(0x00000000);
    	title_bar_send_button.setOnClickListener(null);
    	
		ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
		back_button.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				FragmentTransaction fragment_transaction = app_activity.getFragmentManager().beginTransaction();
				Fragment fragment = new FragmentSecondLayerMenu();
				Bundle bundle = new Bundle();
				//bundle.putString("second_layer_content", "progress_state");
				bundle.putString("second_layer_content", "no_power");
				fragment.setArguments(bundle);
				fragment_transaction.replace(R.id.fragment_content, fragment,"second_layer").commit();
			}
		});
		
		
		
		
		WebView web_view = (WebView) findViewById(R.id.web_browser);
		web_view.getSettings().setDisplayZoomControls(true);
		web_view.getSettings().setJavaScriptEnabled(true);
		web_view.getSettings().setSupportZoom(true);
		web_view.setWebViewClient(new MyWebViewClient());
		
		web_view.loadUrl(url_string);
		
		return current_view;
    }
	
	private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null)
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
	
	private class MyWebViewClient extends WebViewClient
	{
		Dialog dialog;
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) 
		{
			dialog = CreateLoadingDialog.createLoadingDialog(app_context, "連線中", CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.CANCELABLE);	
		}
		
		@Override
		public void onPageFinished(WebView view, String url) 
		{
			dialog.dismiss();
		}
		
	}
	
}
