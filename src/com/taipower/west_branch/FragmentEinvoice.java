package com.taipower.west_branch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	
	private TextView VNum_1;
	private TextView VNum_2;
	private TextView VNum_3;
	private TextView text_input;
	
	private  String url;
	
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
        	}
        });
		
        /*
        ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setBackgroundResource(R.drawable.title_bar_send_button);
		title_bar_send_button.setVisibility(View.VISIBLE);
		title_bar_send_button.setOnClickListener(on_click_listener);
        */
        
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
		
		//*
		webview = (WebView) findViewById(R.id.webview_browser);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.setWebViewClient(new SSLTolerentWebViewClient());
		webview.loadUrl("https://einvoice.taipower.com.tw/einvoice/search_1");
		//*/
		/*
		final LinearLayout people_einvoice_layout = (LinearLayout) findViewById(R.id.people_einvoice_layout);
		people_einvoice_layout.setVisibility(View.VISIBLE);
		final LinearLayout business_einvoice_layout = (LinearLayout) findViewById(R.id.business_einvoice_layout);
		business_einvoice_layout.setVisibility(View.GONE);
		*/
		url = "https://einvoice.taipower.com.tw/einvoice/search_1";
		
		Spinner spinner_action = (Spinner) current_view.findViewById(R.id.spinner_action);
		spinner_action.setAdapter(new ArrayAdapter(app_context, android.R.layout.simple_list_item_1, new String[]{"發票查詢","營業人查詢"}));
		spinner_action.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) 
			{
				// TODO Auto-generated method stub
				switch(position)
				{
					case 0:
						scan_barcode.setVisibility(View.VISIBLE);
						url = "https://einvoice.taipower.com.tw/einvoice/search_1";
						//people_einvoice_layout.setVisibility(View.VISIBLE);
						//business_einvoice_layout.setVisibility(View.GONE);
						break;
					case 1:
						scan_barcode.setVisibility(View.INVISIBLE);
						url = "https://einvoice.taipower.com.tw/einvoice/search_2";
						//people_einvoice_layout.setVisibility(View.GONE);
						//business_einvoice_layout.setVisibility(View.VISIBLE);
						break;
					default:
						url = "https://einvoice.taipower.com.tw/einvoice/search_1";
						//people_einvoice_layout.setVisibility(View.VISIBLE);
						break;
				}
				
				//*
				webview.getSettings().setJavaScriptEnabled(true);
				webview.loadUrl(url);
				//*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				// TODO Auto-generated method stub
				
			}	
		});
		/*
		VNum_1 = (TextView) findViewById(R.id.VNum_1);
		VNum_2 = (TextView) findViewById(R.id.VNum_2);
		VNum_3 = (TextView) findViewById(R.id.VNum_3);
		text_input = (TextView) findViewById(R.id.text_input);
		
		String code_url = "https://einvoice.taipower.com.tw/einvoice/ValidateNumber.ashx?";
		
		String read_session_code = "https://einvoice.taipower.com.tw/einvoice/readSessionValidateNumber.ashx";
		
		new LoadingDataAsyncTask().execute("check_code",url, code_url + (System.currentTimeMillis() % 100),read_session_code);
		*/
		
		
		return current_view;
    }
	
	private View findViewById(int id)
	{
		View view;
		
		if( (view = current_view.findViewById(id)) == null )
			view = app_activity.findViewById(id);
		
		return view;
	}
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if( v.getId() == R.id.title_bar_menu_button )
			{
				String url = "https://einvoice.taipower.com.tw/einvoice/search_1";
            	String paraments = null;
            	
            	try 
            	{
            		paraments = "__EVENTTARGET=" + URLEncoder.encode("ctl00$MainContent$btn_submit","UTF-8") + "&"+
            					"__EVENTARGUMENT=" + "" + "&" +
            					"__VIEWSTATE=" + URLEncoder.encode( __VIEWSTATE,"UTF-8") + "&" +
            					"__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR + "&"+
            					"__EVENTVALIDATION=" + URLEncoder.encode( __EVENTVALIDATION,"UTF-8") + "&" + 
            					URLEncoder.encode("ctl00$MainContent$VNum_1","UTF-8") + "=" + VNum_1.getText().toString() + "&" +
            					URLEncoder.encode("ctl00$MainContent$VNum_2","UTF-8") + "=" + VNum_2.getText().toString() + "&" +
            					URLEncoder.encode("ctl00$MainContent$VNum_3","UTF-8") + "=" + VNum_3.getText().toString() + "&" +
            					URLEncoder.encode("ctl00$MainContent$txt_input","UTF-8") + "=" + text_input.getText().toString() ;
            									   
            		/*
            		__EVENTTARGET:ctl00%24MainContent%24btn_submit
					__EVENTARGUMENT:
					__VIEWSTATE:lzwc4hBm2rhZSR1tdQ4K7TY%2BEm1mwxyKLf%2BFhnj%2FwgG53zlsrRPfh6BU0RMTWuvS3VaM%2BofRS7xt7eawY1JGbibPy1zAhQO44%2Bt3mLHQcvGf1QrlIWC9G14ORtZIeRv6X6RC2k15Z9d42jeL4n1u7XuXxHIcOTIBPZ3yKvNX51Mi3ErFXPosoOPwESfTsJEoiu07yKEjoKxCc%2Bl69yP6eg%3D%3D
					__VIEWSTATEGENERATOR:50DB3F80
					__EVENTVALIDATION:7nniz7fjwdk82h62JgMHddjJT1gbXenWG58rKIFjTkw8%2FbS3xH%2FYAaN3%2Fz7q9ShUFKQ1ujrpyeZO62TBvmGyZuCW8HUmkKVQ5QjA23VC10dKod08VMkXVTtIsrzN%2Fhm468pF%2FxEcWlztxwjDtFL12Sdvz0dSwzCIxFTxc5LYWrs3dlcTBrHNyCiPCe7zYmMc1fcExVzTb4xlE4REgAGhVg%3D%3D
					ctl00%24MainContent%24VNum_1:10501
					ctl00%24MainContent%24VNum_2:BBX5001821
					ctl00%24MainContent%24VNum_3:050705095277407
					ctl00%24MainContent%24txt_input:96744
            		*/
            	} 
            	catch (UnsupportedEncodingException e) 
            	{
					// TODO Auto-generated catch block
					e.printStackTrace();
            	}
				
            	new LoadingDataAsyncTask().execute("send",url,paraments);
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		//Log.i("requestCode","" + requestCode);
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
				/*
				Log.i("barcode_result",barcode_result.substring(0, 5));
				Log.i("barcode_result",barcode_result.substring(5, 15));
				Log.i("barcode_result",barcode_result.substring(15, barcode_result.length()) );
				*/
				
				if( VERSION.SDK_INT < VERSION_CODES.KITKAT )
				{
					//*
					webview.loadUrl("javascript:document.getElementById('MainContent_VNum_1').value=" + barcode_result.substring(0, 5).toString() + ";");
					webview.loadUrl("javascript:document.getElementById('MainContent_VNum_2').value=" + barcode_result.substring(5, 15).toString() + ";");
					webview.loadUrl("javascript:document.getElementById('MainContent_VNum_3').value=" + barcode_result.substring(15, barcode_result.length()).toString() + ".toString();");
					//*/
				}
				else
				{
				//*
					webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_1').value='" + barcode_result.substring(0, 5).toString() + "';", null);
					webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_2').value='" + barcode_result.substring(5, 15).toString() + "';", null);
					webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_3').value='" + barcode_result.substring(15, barcode_result.length()) + "';", null);
				//*/
				}
				
				/*
				VNum_1.setText(barcode_result.substring(0, 5).toString());
				VNum_2.setText(barcode_result.substring(5, 15).toString());
				VNum_3.setText(barcode_result.substring(15, barcode_result.length()).toString());
				*/
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
			
			Log.i("url",url);
			if(url.contains("search"))
				view.getSettings().setJavaScriptEnabled(true);
			else	
				view.getSettings().setJavaScriptEnabled(false);
			
			/*
			if( VERSION.SDK_INT < VERSION_CODES.KITKAT )
			{
				Log.i("url",url);
				if(url.contains("search"))
					view.getSettings().setJavaScriptEnabled(true);
				else	
					view.getSettings().setJavaScriptEnabled(false);
			}
			else
			{
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
			*/
		}
	}
	
	private String __EVENTTARGET;
    private String __EVENTARGUMENT;
    private String __VIEWSTATE;
    private String __VIEWSTATEGENERATOR;
    private String __EVENTVALIDATION;
	
	private String cookie = null;
	private HttpConnectResponse connection;
	
	private class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer>
	{	
		@Override
		protected void onPreExecute ()
    	{
    		super.onPreExecute();
    	}
		
		byte[] response_data;
		byte[] response_data_content;
		byte[] response_code;
		String tag_value = "";
		int http_response_code = 0;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			this.publishProgress(0);
			Integer return_value = null ;
			
    		if( ASaBuLuCheck.isOnline(app_activity) )
    		{
    			tag_value = params[0];			
			
    			try 
    			{
    				if(connection == null )
    					connection = new HttpConnectResponse();
				
    				if( tag_value.equals("check_code"))
    				{	
    					//Server is had update connection limit at 1 second
    					//wait 5 seconds to connect
    					/*
    					connection.setUrl(params[1]);
    					connection.setConnectMethod("GET", null);
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_CLEAR);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data_content = connection.startConnectAndResponseByteArray();
    					//cookie = connection.getCookie();
    					*/
    					response_data_content = HttpConnectResponse.onOpenConnection(params[1], "GET", null, HttpConnectResponse.COOKIE_CLEAR,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
    					Thread.sleep(1000l);
    					/*
    					connection.setUrl(params[2]);
    					connection.setConnectMethod("GET", null);
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data = connection.startConnectAndResponseByteArray();
    					*/
    					
    					response_data = HttpConnectResponse.onOpenConnection(params[2], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
    					response_code = HttpConnectResponse.onOpenConnection(params[3], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
    					return_value = 1;
    				}
				
    				if( tag_value.equals("send") )
    				{		
    					/*
    					connection.setUrl(params[1]);
    					connection.setConnectMethod("POST", new String[]{params[2]});
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data_content = connection.startConnectAndResponseByteArray();
    					*/
    					response_data_content = HttpConnectResponse.onOpenConnection( params[1], "POST", new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
					
    					return_value = 2;
    				}
    				/*
    				if( connection.HTTP_STATUS >= HttpConnectResponse.HTTP_FORBIDDEN && connection.HTTP_STATUS <= HttpConnectResponse.HTTP_NOT_FOUND )
    					return_value = 404;
    				*/
    				if( HttpConnectResponse.CONNECTION_STATE_CODE >= 400 && HttpConnectResponse.CONNECTION_STATE_CODE <= 404 )
    					return_value = 404;
    			} 
    			catch (InterruptedException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			catch (IOException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    			catch (NumberFormatException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    			catch (URISyntaxException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    		}
    		else
    			return_value = 4;
			
			return return_value;
		}
		
		private Dialog process_persent_pointr = null;
		
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			super.onProgressUpdate(values[0]);
			String message = ""; 
			
			if( values[0] == 0 )
		    {
				message = "資料傳送中";
		        
				if( process_persent_pointr == null )
		        	process_persent_pointr = CreateLoadingDialog.createLoadingDialog(app_context, message , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.CANCELABLE);
		    } 
		}
				
		@Override
		protected void onPostExecute(Integer result) 
		{
			CreateLoadingDialog.dialog_dismiss(process_persent_pointr);
			process_persent_pointr = null;
			
			if( result.intValue() == 1 )
			{				
				Document document = null;
				try 
				{
					document = Jsoup.parse(new String(response_data_content,"UTF-8"));
					
					Log.i("readsession code",new String(response_code,"utf-8"));
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				__EVENTTARGET = document.getElementById("__EVENTTARGET").val();
				__EVENTARGUMENT = document.getElementById("__EVENTARGUMENT").val();
				__VIEWSTATE = document.getElementById("__VIEWSTATE").val();
				__VIEWSTATEGENERATOR = document.getElementById("__VIEWSTATEGENERATOR").val();
				__EVENTVALIDATION = document.getElementById("__EVENTVALIDATION").val();
				
				//Log.i("__EVENTTARGET :",__EVENTTARGET);
				//Log.i("__EVENTARGUMENT", __EVENTARGUMENT);
				//Log.i("__VIEWSTATE",__VIEWSTATE);
				//Log.i("__VIEWSTATEGENERATOR",__VIEWSTATEGENERATOR);
				//Log.i("__EVENTVALIDATION",__EVENTVALIDATION);
				
				if( result.intValue() == 1 )
				{
					Bitmap temp_bitmap = BitmapFactory.decodeByteArray(response_data , 0, response_data.length);
					//((ImageView) findViewById(R.id.check_code_image)).setImageBitmap(temp_bitmap);
					
					
					
				}
				else
				{
					
				}
			}
			else if( result.intValue() == 404 )
			{
				AlertDialog.Builder connection_error = new AlertDialog.Builder(app_context);
				connection_error.setTitle("網路資料錯誤");
				connection_error.setMessage("請檢查網路\n或\n短時間連線次數過多\n請稍候再試");
				connection_error.setNeutralButton("確定",null);
				connection_error.show();
				
			}
        	
			if( result.intValue() == 2 )
			{				
				Document document = null;
				try 
				{
					document = Jsoup.parse(new String(response_data_content,"UTF-8"));
					Log.i("html",new String(response_data_content,"UTF-8"));
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			/*
			CreateLoadingDialog.dialog_dismiss(process_persent_pointr);
			process_persent_pointr = null;
			*/
			
			if( result.intValue() == 4 )
        	{	
        		String error_message = "網路錯誤！！請檢查網際網路是否開啟\n或請稍候再試";
    			
    			new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage(error_message).setNeutralButton("確定", null).show();
        	}
		}	
	}
	
	
}
