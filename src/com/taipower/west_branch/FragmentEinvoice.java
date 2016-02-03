package com.taipower.west_branch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Locale;

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
import android.app.AlertDialog.Builder;
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
import android.view.Gravity;
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
	
	private TextView Inv_PNUM;
	private TextView Inv_Num;
	private TextView Inv_YYYMM;
	
	private TextView txt_input;
	
	private ImageView check_code_image;
	
	private String url;
	private static final String code_url = "https://einvoice.taipower.com.tw/einvoice/ValidateNumber.ashx?";
	private static final String read_session_code = "https://einvoice.taipower.com.tw/einvoice/readSessionValidateNumber.ashx";
	
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
		
        ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setBackgroundResource(R.drawable.title_bar_send_button);
		title_bar_send_button.setVisibility(View.VISIBLE);
		title_bar_send_button.setOnClickListener(on_click_listener);
        
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
		
		/*
		webview = (WebView) findViewById(R.id.webview_browser);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.setWebViewClient(new SSLTolerentWebViewClient());
		webview.loadUrl("https://einvoice.taipower.com.tw/einvoice/search_1");
		/*/
		//*
		final LinearLayout people_einvoice_layout = (LinearLayout) findViewById(R.id.people_einvoice_layout);
		people_einvoice_layout.setVisibility(View.VISIBLE);
		final LinearLayout business_einvoice_layout = (LinearLayout) findViewById(R.id.business_einvoice_layout);
		business_einvoice_layout.setVisibility(View.GONE);
		//*/
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
						people_einvoice_layout.setVisibility(View.VISIBLE);
						business_einvoice_layout.setVisibility(View.GONE);
						break;
					case 1:
						scan_barcode.setVisibility(View.INVISIBLE);
						url = "https://einvoice.taipower.com.tw/einvoice/search_2";
						people_einvoice_layout.setVisibility(View.GONE);
						business_einvoice_layout.setVisibility(View.VISIBLE);
						break;
					default:
						url = "https://einvoice.taipower.com.tw/einvoice/search_1";
						people_einvoice_layout.setVisibility(View.VISIBLE);
						break;
				}
				new LoadingDataAsyncTask().execute("check_code_refresh",url, code_url + (System.currentTimeMillis() % 100),read_session_code);
				/*
				webview.getSettings().setJavaScriptEnabled(true);
				webview.loadUrl(url);
				/*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				// TODO Auto-generated method stub
				
			}	
		});
		//*
		VNum_1 = (TextView) findViewById(R.id.VNum_1);
		VNum_2 = (TextView) findViewById(R.id.VNum_2);
		VNum_3 = (TextView) findViewById(R.id.VNum_3);
		
		Inv_PNUM = (TextView) findViewById(R.id.Inv_PNUM);
		Inv_Num = (TextView) findViewById(R.id.Inv_Num);
		Inv_YYYMM = (TextView) findViewById(R.id.Inv_YYYMM);
		
		txt_input = (TextView) findViewById(R.id.text_input);
		
		check_code_image = (ImageView) findViewById(R.id.check_code_image);
		check_code_image.setOnClickListener(on_click_listener);
		
		//new LoadingDataAsyncTask().execute("check_code",url, code_url + (System.currentTimeMillis() % 100),read_session_code);
		//*/
		
		return current_view;
    }
	
	private View findViewById(int id)
	{
		View view;
		
		if( (view = current_view.findViewById(id)) == null )
			view = app_activity.findViewById(id);
		
		return view;
	}
	
	private View.OnClickListener on_click_listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if( v.getId() == R.id.check_code_image )
			{
				new LoadingDataAsyncTask().execute("check_code_refresh",url, code_url + (System.currentTimeMillis() % 100),read_session_code);
			}
			
			if( v.getId() == R.id.title_bar_menu_button )
			{
				VNum_2.setText(VNum_2.getText().toString().toUpperCase(Locale.US));
				
				String VNum_1_string = VNum_1.getText().toString();
				String VNum_2_string = VNum_2.getText().toString();
				String VNum_3_string = VNum_3.getText().toString();
				
				String Inv_PNUM_string = Inv_PNUM.getText().toString();
				String Inv_Num_string = Inv_Num.getText().toString();
				String Inv_YYYMM_string = Inv_YYYMM.getText().toString();
				
				String error_message = "";
				
				if( url.contains("search_1")  )
				{	
					if( VNum_1_string.length() < 5 || Integer.valueOf(VNum_1_string.substring(0, 3)) < 105 || Integer.valueOf(VNum_1_string.substring(3)) > 12 )
						error_message += "年月錯誤\n";
					
					if( VNum_2_string.length() < 10 || !VNum_2_string.substring(0, 2).equals("BB") )
						error_message += "流水號錯誤\n";

					String[] side_array = new String[]{"N","C","S","X","Y","Z","E"};
					String side = VNum_2_string.substring(2,3);
					
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
					
					if( VNum_3_string.length() < 15 || !ASaBuLuCheck.electricCheckFunction(VNum_3_string.substring(4)) )
						error_message += "檢核碼錯誤";
				}
				else
				{
					if( !ASaBuLuCheck.electricCheckFunction(Inv_PNUM_string) )
						error_message += "電號錯誤\n";
					
					if( !ASaBuLuCheck.idCheck(Inv_Num_string) )
						error_message += "統一編號錯誤\n";
					
					if( Inv_YYYMM_string.length() < 5 || Integer.valueOf(Inv_YYYMM_string.substring(0, 3)) < 105 || Integer.valueOf(Inv_YYYMM_string.substring(3)) > 12 )
						error_message += "年月錯誤";
				}
				
				if(error_message.equals(""))	
				{	
            	String paraments = null;
            	
            	try 
            	{
            		paraments = "__EVENTTARGET=" + URLEncoder.encode("ctl00$MainContent$btn_submit","UTF-8") + "&"+
        						"__EVENTARGUMENT=" + "" + "&" +
        						"__VIEWSTATE=" + URLEncoder.encode( __VIEWSTATE,"UTF-8") + "&" +
        						"__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR + "&"+
        						"__EVENTVALIDATION=" + URLEncoder.encode( __EVENTVALIDATION,"UTF-8") + "&" ; 
            		
            		if( url.contains("search_1") )
            		{
            			paraments += URLEncoder.encode("ctl00$MainContent$VNum_1","UTF-8") + "=" + VNum_1.getText().toString() + "&" +
            						 URLEncoder.encode("ctl00$MainContent$VNum_2","UTF-8") + "=" + VNum_2.getText().toString() + "&" +
            						 URLEncoder.encode("ctl00$MainContent$VNum_3","UTF-8") + "=" + VNum_3.getText().toString() + "&" ;
            		}
            		else
            		{
            			paraments += URLEncoder.encode("ctl00$MainContent$Inv_PNUM","UTF-8") + "=" + Inv_PNUM.getText().toString() + "&" +
       						 		 URLEncoder.encode("ctl00$MainContent$Inv_Num","UTF-8") + "=" + Inv_Num.getText().toString() + "&" +
       						 		 URLEncoder.encode("ctl00$MainContent$Inv_YYYMM","UTF-8") + "=" + Inv_YYYMM.getText().toString() + "&" ;
            		}
            		
            		paraments += URLEncoder.encode("ctl00$MainContent$txt_input","UTF-8") + "=" + txt_input.getText().toString() ;
            		/*
            		__EVENTTARGET:ctl00%24MainContent%24btn_submit
					__EVENTARGUMENT:
					__VIEWSTATE:lzwc4hBm2rhZSR1tdQ4K7TY%2BEm1mwxyKLf%2BFhnj%2FwgG53zlsrRPfh6BU0RMTWuvS3VaM%2BofRS7xt7eawY1JGbibPy1zAhQO44%2Bt3mLHQcvGf1QrlIWC9G14ORtZIeRv6X6RC2k15Z9d42jeL4n1u7XuXxHIcOTIBPZ3yKvNX51Mi3ErFXPosoOPwESfTsJEoiu07yKEjoKxCc%2Bl69yP6eg%3D%3D
					__VIEWSTATEGENERATOR:50DB3F80
					__EVENTVALIDATION:7nniz7fjwdk82h62JgMHddjJT1gbXenWG58rKIFjTkw8%2FbS3xH%2FYAaN3%2Fz7q9ShUFKQ1ujrpyeZO62TBvmGyZuCW8HUmkKVQ5QjA23VC10dKod08VMkXVTtIsrzN%2Fhm468pF%2FxEcWlztxwjDtFL12Sdvz0dSwzCIxFTxc5LYWrs3dlcTBrHNyCiPCe7zYmMc1fcExVzTb4xlE4REgAGhVg%3D%3D
					
					search_1
					ctl00%24MainContent%24VNum_1:10501
					ctl00%24MainContent%24VNum_2:BBX5001821
					ctl00%24MainContent%24VNum_3:050705095277407
					
					search_2
					ctl00$MainContent$Inv_PNUM:05012222100
					ctl00$MainContent$Inv_Num:23060248
					ctl00$MainContent$Inv_YYYMM:10501
					
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
				else
				{
					AlertDialog.Builder alert_dialog = new AlertDialog.Builder(app_context);
					alert_dialog.setTitle("錯誤");
					alert_dialog.setMessage(error_message);
					alert_dialog.setNeutralButton("確定", null);
					alert_dialog.show();
				}
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
			StringBuilder error_message = new StringBuilder("");
			
			if(!barcode_format.startsWith("CODE_128"))
				error_message.append("條碼格式錯誤\n");
			
			if( barcode_result.length() != 30 || !barcode_result.substring(5, 7).equals("BB"))
				error_message.append("非台電公司電子發票條碼\n");
			else
			{
				String[] side_array = new String[]{"N","C","S","X","Y","Z","E"};
				String side = barcode_result.substring(7,8);
				
				if( !ASaBuLuCheck.electricCheckFunction(barcode_result.substring(19))  )
					error_message.append("檢核碼錯誤\n");
					
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
					error_message.append("非台電公司電子發票條碼");
			}
			
			if( error_message.toString().equals("")  )
			{
				/*
				Log.i("barcode_result",barcode_result.substring(0, 5));
				Log.i("barcode_result",barcode_result.substring(5, 15));
				Log.i("barcode_result",barcode_result.substring(15, barcode_result.length()) );
				*/
				
				/*
				if( VERSION.SDK_INT < VERSION_CODES.KITKAT )
				{
					webview.loadUrl("javascript:document.getElementById('MainContent_VNum_1').value=" + barcode_result.substring(0, 5).toString() + ";");
					webview.loadUrl("javascript:document.getElementById('MainContent_VNum_2').value=" + barcode_result.substring(5, 15).toString() + ";");
					webview.loadUrl("javascript:document.getElementById('MainContent_VNum_3').value=" + barcode_result.substring(15, barcode_result.length()).toString() + ".toString();");
				}
				else
				{
					webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_1').value='" + barcode_result.substring(0, 5).toString() + "';", null);
					webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_2').value='" + barcode_result.substring(5, 15).toString() + "';", null);
					webview.evaluateJavascript("javascript:document.getElementById('MainContent_VNum_3').value='" + barcode_result.substring(15, barcode_result.length()) + "';", null);
				}
				/*/
				
				
				VNum_1.setText(barcode_result.substring(0, 5).toString());
				VNum_2.setText(barcode_result.substring(5, 15).toString());
				VNum_3.setText(barcode_result.substring(15, barcode_result.length()).toString());
			}
			else
			{
				AlertDialog.Builder alert_dialog = new AlertDialog.Builder(app_context);
				alert_dialog.setTitle("掃描發生錯誤");
				alert_dialog.setMessage(error_message.toString());
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
			
			//Log.i("url",url);
			if(url.contains("search"))
				view.getSettings().setJavaScriptEnabled(true);
			else	
				view.getSettings().setJavaScriptEnabled(false);
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
				
    				if( tag_value.contains("check_code"))
    				{	
    					//Server is had update connection limit at 1 second
    					//wait 5 seconds to connect
    					
    					connection.setUrl(params[1]);
    					connection.setConnectMethod("GET", null);
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data_content = connection.startConnectAndResponseByteArray();
    					
    					/*
    					if( tag_value.contains("refresh"))
    						response_data_content = HttpConnectResponse.onOpenConnection(params[1], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					else
    						response_data_content = HttpConnectResponse.onOpenConnection(params[1], "GET", null, HttpConnectResponse.COOKIE_CLEAR,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					*/
    					
    					Thread.sleep(1000l);
    					
    					connection.setUrl(params[2]);
    					connection.setConnectMethod("GET", null);
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data = connection.startConnectAndResponseByteArray();
    					
    					//response_data = HttpConnectResponse.onOpenConnection(params[2], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
    					//session code
    					//response_code = HttpConnectResponse.onOpenConnection(params[3], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
    					return_value = 1;
    				}
				
    				if( tag_value.equals("send") )
    				{		
    					connection.setUrl(params[1]);
    					connection.setConnectMethod("POST", new String[]{params[2]});
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data_content = connection.startConnectAndResponseByteArray();
    					
    					//response_data_content = HttpConnectResponse.onOpenConnection( params[1], "POST", new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
    					
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
    				return_value = 9;
    			}
    			catch (IOException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				return_value = 9;
    			} 
    			catch (NumberFormatException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				return_value = 9;
    			} 
    			catch (URISyntaxException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				return_value = 9;
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
			
			if( result != null )
			{
				if( result.intValue() == 1 )
				{				
					Document document = null;
					try 
					{
						document = Jsoup.parse(new String(response_data_content,"UTF-8"));
						
						//Log.i("readsession code",new String(response_code,"utf-8"));
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
				
					
					Bitmap temp_bitmap = BitmapFactory.decodeByteArray(response_data , 0, response_data.length);
					((ImageView) findViewById(R.id.check_code_image)).setImageBitmap(temp_bitmap);
					
				}
				else if( result.intValue() == 404 )
				{
					AlertDialog.Builder connection_error = new AlertDialog.Builder(app_context);
					connection_error.setTitle("網路資料錯誤");
					connection_error.setMessage("請檢查網路\n或\n短時間連線次數過多\n請稍候再試");
					connection_error.setNeutralButton("確定",dialog_on_click_listener);
					connection_error.show();
				}
        	
				if( result.intValue() == 2 )
				{				
					AlertDialog.Builder show_dialog = new AlertDialog.Builder(app_context);
					
					Document document = null;
					
					try 
					{
						document = Jsoup.parse(new String(response_data_content,"UTF-8"));
						//Log.i("html",new String(response_data_content,"UTF-8"));
						
						if( url.contains("search_1") )
						{
							Element main_content_label2 = document.getElementById("MainContent_Label2");
							
							if( main_content_label2 != null && main_content_label2.text().toString().contains("查詢結果") ) 
							{
								/*
								MainContent_TableCell1 發票號碼
								MainContent_INV_NUM FH68964561
								
								MainContent_TableCell33 發票期別
								MainContent_INV_YM 10502
								MainContent_TableCell11 買方統一編號
								MainContent_BUY_ID -
								
								MainContent_TableCell29 總計金額
								MainContent_TOT_AMT 29
								MainContent_TableCell57 對獎結果
								MainContent_PRIZE_TYPE 尚未開獎
								
														稅額
								MainContent_TAX     -
														課稅別 
								MainContent_TAX_TYPE 應稅
						
								MainContent_TableCell9 發票日期
								MainContent_INV_DT 20160105
								MainContent_TableCell5 隨機碼
								MainContent_INV_RAN_NUM 3145
								
								MainContent_CUST_NO 05095277407
								 */
								
								/*
								String INV_NUM = document.getElementById("MainContent_INV_NUM").text().toString();
								String INV_YM = document.getElementById("MainContent_INV_YM").text().toString();
								String BUY_ID = document.getElementById("MainContent_BUY_ID").text().toString();
								String TOT_AMT = document.getElementById("MainContent_TOT_AMT").text().toString();
								String PRIZE_TYPE = document.getElementById("MainContent_PRIZE_TYPE").text().toString();
								String TAX = document.getElementById("MainContent_TAX").text().toString();
								String TAX_TYPE = document.getElementById("MainContent_TAX_TYPE").text().toString();
								String INV_DT = document.getElementById("MainContent_INV_DT").text().toString();
								String INV_RAN_NUM = document.getElementById("MainContent_INV_RAN_NUM").text().toString();
								String CUST_NO = document.getElementById("MainContent_CUST_NO").text().toString();
							
								String[] result_array = new String[]{"發票號碼：" + INV_NUM,
															 	 	 "發票期別：" + INV_YM,
															 	     "買方統一編號：" + BUY_ID,
															  	 	 "總計金額：" + TOT_AMT,
															  	 	 "對獎結果：" + PRIZE_TYPE,
															 	  	 "稅額：" + TAX,
															  	 "課稅別：" + TAX_TYPE,
															 	 "發票日期：" + INV_DT,
															 	 "隨機碼：" + INV_RAN_NUM,
															 	 "電號：" + CUST_NO};
								 */
								LinearLayout result_layout = (LinearLayout) LayoutInflater.from(app_context).inflate(R.layout.fragment_einvoice_result, null, false); 
								((TextView) result_layout.findViewById(R.id.einvoice_INV_NUM)).setText(document.getElementById("MainContent_INV_NUM").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_INV_YM)).setText(document.getElementById("MainContent_INV_YM").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_BUY_ID)).setText(document.getElementById("MainContent_BUY_ID").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_TOT_AMT)).setText(document.getElementById("MainContent_TOT_AMT").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_PRIZE_TYPE)).setText(document.getElementById("MainContent_PRIZE_TYPE").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_TAX)).setText(document.getElementById("MainContent_TAX").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_TAX_TYPE)).setText(document.getElementById("MainContent_TAX_TYPE").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_INV_DT)).setText(document.getElementById("MainContent_INV_DT").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_INV_RAN_NUM)).setText(document.getElementById("MainContent_INV_RAN_NUM").text().toString());
								((TextView) result_layout.findViewById(R.id.einvoice_CUST_NO)).setText(document.getElementById("MainContent_CUST_NO").text().toString());
						
								show_dialog.setTitle("查詢結果");
								show_dialog.setView(result_layout);
								//show_dialog.setItems(result_array, null);
							}
							else
							{
								String error_message = document.getElementById("MainContent_Label1").text().toString();
						
								show_dialog.setTitle("喔喔！！");
								show_dialog.setMessage(error_message);
							}
						}
						else
						{
							if( document.getElementById("MainContent_INV_0") != null  )
							{
								Elements inv_elements = document.getElementsByTag("span");
								
								LinearLayout result_layout = new LinearLayout(app_context); 
								result_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
								
								for(Element qq : inv_elements)
								{
									if( qq.text().contains(":")  )
									{	
										TextView result_textview = new TextView(app_context);
										result_textview.setText(qq.text().replace(" ","").replace("總", " 總"));
										result_textview.setTextSize(14.0f);
										result_textview.setGravity(Gravity.CENTER);
										result_textview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
										result_layout.addView(result_textview);
									}
								}
								
								show_dialog.setTitle("查詢結果");
								show_dialog.setView(result_layout);
							}
							else
							{
								String error_message = document.getElementById("MainContent_Label1").text().toString();
								
								show_dialog.setTitle("喔喔！！");
								show_dialog.setMessage(error_message);
							}
						}
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						show_dialog.setTitle("喔喔！！");
						show_dialog.setMessage("發生未預期的錯誤！！");
					}	
					
					show_dialog.setNeutralButton("確定", dialog_on_click_listener);
					show_dialog.show();	
				}
				
				if( result.intValue() == 4 || result.intValue() == 9)
				{	
					String error_message = "網路錯誤！！請檢查網際網路是否開啟\n或請稍候再試";
					
					new AlertDialog.Builder(app_context).setTitle("喔喔！！").setMessage(error_message).setNeutralButton("確定", null).show();
				}	
			}
			else
			{
				String error_message = "網路錯誤！！請檢查網際網路是否開啟\n或請稍候再試";
				
				new AlertDialog.Builder(app_context).setTitle("喔喔！！").setMessage(error_message).setNeutralButton("確定", null).show();
			}
			
			
		}	
	}
	
	private DialogInterface.OnClickListener dialog_on_click_listener = new DialogInterface.OnClickListener() 
	{	
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			// TODO Auto-generated method stub
			new LoadingDataAsyncTask().execute("check_code_refresh",url, code_url + (System.currentTimeMillis() % 100),read_session_code);
			
			VNum_1.setText("");
			VNum_2.setText("");
			VNum_3.setText("");
			
			Inv_PNUM.setText("");
			Inv_Num.setText("");
			Inv_YYYMM.setText("");
			
			txt_input.setText("");
			
			dialog.dismiss();
		}
	};
	
	
}
