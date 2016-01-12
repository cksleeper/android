package com.taipower.west_branch;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentEinvoice extends Fragment 
{
	private Activity app_activity;
	private Context app_context;
	private FragmentEinvoice this_class;
	private View current_view;
	
	private WebView webview;
	
	private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN"; 
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		app_activity = this.getActivity();
		app_context = this.getActivity();
		this_class = this;
		
		current_view = inflater.inflate(R.layout.fragment_einvoice, container, false);
		
		ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		InputMethodManager imm = (InputMethodManager) app_context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(current_view.getWindowToken(), 0);
        		
        		Bundle bundle = new Bundle();
        		bundle.putString("second_layer_content", "progress_state");
        		
        		FragmentTransaction ft = this_class.getActivity().getFragmentManager().beginTransaction();
        		Fragment fragment = new FragmentSecondLayerMenu();
        		fragment.setArguments(bundle);
        		ft.replace(R.id.fragment_content, fragment,"second_layer").commit();
        		
        		//finish();
        	}
        });
		
		final Button scan_barcode = (Button) findViewById(R.id.scan_barcode);
		scan_barcode.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				//IntentIntegrator scan_intent = new IntentIntegrator(this_class);
				//scan_intent.initiateScan();
				
				Intent intent = new Intent(ACTION_SCAN);  
				intent.putExtra("SCAN_MODE", "ONE_D_MODE"); //一維條碼模式
				
				this_class.startActivityForResult(intent, 0);  
			}
		});
		
		webview = (WebView) findViewById(R.id.webview_browser);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.setWebViewClient(new SSLTolerentWebViewClient());
		webview.loadUrl("https://einvoice.taipower.com.tw/einvoice/search_1");
		
		
		Spinner spinner_action = (Spinner) current_view.findViewById(R.id.spinner_action);
		spinner_action.setAdapter(new ArrayAdapter(app_context, android.R.layout.simple_list_item_1, new String[]{"發票查詢","營業人查詢"}));
		spinner_action.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) 
			{
				// TODO Auto-generated method stub
				String url = "https://einvoice.taipower.com.tw/einvoice/search_1";
				
				switch(position)
				{
					case 0:
						scan_barcode.setVisibility(View.VISIBLE);
						url = "https://einvoice.taipower.com.tw/einvoice/search_1";
						break;
					case 1:
						scan_barcode.setVisibility(View.INVISIBLE);
						url = "https://einvoice.taipower.com.tw/einvoice/search_2";
						break;
				}
				webview.getSettings().setJavaScriptEnabled(true);
				webview.loadUrl(url);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return current_view;
    }
	
	
	private View findViewById(int id)
	{
		View view;
		
		if( (view = current_view.findViewById(id)) == null )
			view = app_activity.findViewById(id);
		
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		Log.i("requestCode","" + requestCode);
		super.onActivityResult(requestCode, resultCode, intent);
		
		
		
		if( intent != null  )
		{
		//retrieve scan result
		//IntentResult scanning_result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		//Log.i("content",scanning_result.getContents());
		//Log.i("format",scanning_result.getFormatName());
		//we have a result
		//String barcode_result = scanning_result.getContents();
		//String barcode_format = scanning_result.getFormatName();
		
		String barcode_result = intent.getStringExtra("SCAN_RESULT"); //掃描結果  
	    String barcode_format = intent.getStringExtra("SCAN_RESULT_FORMAT");//掃描格式  
		
		
		//if (scanning_result != null)
		if( barcode_result != null )
	    {
			String error_message = "";
			
			if(!barcode_format.startsWith("CODE_128"))
				error_message += "條碼格式錯誤\n";
			
			if( barcode_result.length() != 30 || !barcode_result.substring(5, 7).equals("BB"))
				error_message += "非台電公司電子發票條碼\n";
			else
			{
				String[] side_array = new String[]{"N","C","S","X","Y","Z","E"};
				String side = barcode_result.substring(7,8);
				
				if( !ASaBuLuCheck.electricCheckFunction(barcode_result.substring(19))  )
					error_message += "檢核碼錯誤\n";
					
				boolean check = false;
				
				for(String qq : side_array)
				{
					if( side.equals(qq) )
					{	
						check = true;
						break;
					}
				}
				
				if(!check)
					error_message += "非台電公司電子發票條碼";
			}
			
			if( error_message.equals("")  )
			{
				Log.i("barcode_result",barcode_result.substring(0, 5));
				Log.i("barcode_result",barcode_result.substring(5, 15));
				Log.i("barcode_result",barcode_result.substring(15, barcode_result.length()) );
				
				//webview.loadUrl("javascript:document.getElementById('MainContent_VNum_1').value=" + barcode_result.substring(0, 5).toString() + ";");
				
				webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_1').value='" + barcode_result.substring(0, 5).toString() + "';", null);
				webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_2').value='" + barcode_result.substring(5, 15).toString() + "';", null);
				webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_3').value='" + barcode_result.substring(15, barcode_result.length()) + "';", null);
				
				//webview.loadUrl("javascript:document.getElementById('MainContent_VNum_2').value=" + barcode_result.substring(5, 15).toString() + ";");
				//webview.loadUrl("javascript:document.getElementById('MainContent_VNum_3').value=" + barcode_result.substring(15, barcode_result.length()).toString() + ".toString();");
			}
			else
			{
				AlertDialog.Builder alert_dialog = new AlertDialog.Builder(app_context);
				alert_dialog.setTitle("掃描發生錯誤");
				alert_dialog.setMessage(error_message);
				alert_dialog.setNeutralButton("確定", null);
				alert_dialog.show();
			}
		}
		else
		{
			//Toast toast = Toast.makeText(app_context,"No scan data received!", Toast.LENGTH_SHORT);
			//toast.show();
		}
		
		}
	}
	
	private Dialog dialog = null;
	
	// SSL Error Tolerant Web View Client
	private class SSLTolerentWebViewClient extends WebViewClient 
	{
		@Override
	    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) 
	    {
			handler.proceed(); // Ignore SSL certificate errors
	    }
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			if( dialog == null)
				dialog = CreateLoadingDialog.createLoadingDialog(app_context, "連線中", CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.CANCELABLE);	
		}
		
		@Override
		public void onPageFinished(final WebView view, String url)
		{
			if(dialog != null)
			{	
				dialog.dismiss();
				dialog = null;
			}
			
			view.evaluateJavascript("javascript:document.getElementById('btn2');", new ValueCallback<String>()
			{
				@Override
				public void onReceiveValue(String value) 
				{
					// TODO Auto-generated method stub
					//Log.i("value",value);
					if( value.equals("null")  )
						view.getSettings().setJavaScriptEnabled(true);
					else
						view.getSettings().setJavaScriptEnabled(false);
				}
			});
		}
	}
	
	
}
