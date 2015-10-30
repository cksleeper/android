package com.taipower.west_branch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;
import com.taipower.west_branch.utility.PerferenceDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


@SuppressLint("JavascriptInterface")
public class FragmentCalcLight extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	
	private View current_view; 
	
	private String online_html_version;
	private String offline_html_version;
	
	private WebView browser;
	
	private String old_year  ;
	private String old_month ;
	private String old_date  ;
	private String new_year  ;
	private String new_month ;
	private String new_date  ;
	 
	private String kind ;
	
	private String power_string;
	
	private Button start_date_button;
	private Button end_date_button;
	private Button kind_button;
	private Button power_button;
	
	public  FragmentCalcLight()
	{
		
	}
	 
	 
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {
		this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		
		online_html_version = "0";
		offline_html_version = "1";
		boolean online_html_check;
		String online_html = null;
		
		try 
		{
			online_html = new String(HttpConnectResponse.inputStreamToByteArray(app_context.openFileInput("mypage.html")),"utf-8");
		
			Document document = Jsoup.parse(online_html);
		
			online_html_version = document.select("version").first().text().toString();
			Log.i("online_html_version",online_html_version);
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] offline_html_byte;
		Document offline_document = null;
		
		try 
		{
			offline_html_byte = HttpConnectResponse.inputStreamToByteArray(app_context.getAssets().open("mypage.html"));
			offline_document = Jsoup.parse(new String(offline_html_byte,"utf-8"));
		
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		offline_html_version = offline_document.select("version").first().text().toString();
		Log.i("offline_html_version",offline_html_version);
		
		String version_date = "";
		
		if( Integer.valueOf(online_html_version)  > Integer.valueOf(offline_html_version) )
		{
			online_html_check = true;
			version_date = ""+(Integer.valueOf(online_html_version.substring(0,4))-1911)+"("+ online_html_version.substring(0,4) +")年"+ online_html_version.substring(4,6)+"月"+online_html_version.substring(6,8)+"日";
		}
		else
		{
			online_html_check = false;
		}
		
		new DoingInBackgroundTask().execute();
		
		current_view = inflater.inflate(R.layout.fragment_calclight, container , false );
		
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_calc_light);
		
		if(online_html_check)
			((TextView) findViewById(R.id.version_date)).setText(version_date);
		
		ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setVisibility(View.INVISIBLE);
		title_bar_send_button.setBackgroundResource(0x00000000);
		title_bar_send_button.setOnClickListener(null);
		
		Bundle bundle = this.getArguments();
		
		ImageButton title_back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
		title_back_button.setOnClickListener(back_on_click_listener);
		
		ImageButton title_menu_button = (ImageButton) findViewById(R.id.title_bar_menu_button);
		title_menu_button.setVisibility(View.INVISIBLE);
		title_menu_button.setOnClickListener(null);
		
	    //Button back_Button = (Button) findViewById(R.id.back_button);
	    //back_Button.setOnTouchListener(on_touch_listener);
	    //back_Button.setOnClickListener(back_on_click_listener);
	    
	    //LinearLayout calc_background = (LinearLayout) findViewById(R.id.calc_background);
	    //calc_background.setLayoutParams(new LinearLayout.LayoutParams(screen_width, screen_width));
	    
	    start_date_button = (Button) findViewById(R.id.start_date_button); 
	    start_date_button.setOnClickListener(on_click_listener);
	    start_date_button.setOnTouchListener(on_touch_listener);
	    
	    end_date_button = (Button) findViewById(R.id.end_date_button); 
	    end_date_button.setOnClickListener(on_click_listener);
	    end_date_button.setOnTouchListener(on_touch_listener);
	    
	    kind_button = (Button) findViewById(R.id.kind_button); 
	    kind_button.setOnClickListener(on_click_listener);
	    kind_button.setOnTouchListener(on_touch_listener);
	    
	    power_button = (Button) findViewById(R.id.power_button);
	    power_button.setOnClickListener(on_click_listener);
	    power_button.setOnTouchListener(on_touch_listener);
	    
	    browser = (WebView) findViewById(R.id.mybrowser);
		browser.addJavascriptInterface(new MyJavaScriptInterface(), "TOTAL_MONEY" );
		browser.getSettings().setJavaScriptEnabled(true); 
		
		if( online_html_check )
			browser.loadDataWithBaseURL("file:///android_asset/mypage.html", online_html, "text/html", "utf-8", "file:///android_asset/mypage.html");
		else
			browser.loadUrl("file:///android_asset/mypage.html");
		
	    Button clear_button = (Button) findViewById(R.id.clear_button);
	    clear_button.setOnClickListener(back_on_click_listener);
	       
	    Button start_calc_button = (Button) findViewById(R.id.start_calc_button);
	    start_calc_button.setOnTouchListener(on_touch_listener);
	    start_calc_button.setOnClickListener(new View.OnClickListener()
	    {
	    	@Override
	    	public void onClick(View arg0) 
	    	{
	    		start_date_button.setTextColor(0xFF000000);
				end_date_button.setTextColor(0xFF000000);
				power_button.setTextColor(0xFF000000);
				kind_button.setTextColor(0xFF000000);
	    		
	    		String error_message = "";
				   
				Calendar old_time = Calendar.getInstance();
				Calendar new_time = Calendar.getInstance();
				   
				int subtract = 0 ;
				   
				if( old_year != null && new_year != null )
				{
					old_time.set(Integer.valueOf(old_year), Integer.valueOf(old_month) - 1, Integer.valueOf(old_date) );
					new_time.set(Integer.valueOf(new_year), Integer.valueOf(new_month) - 1, Integer.valueOf(new_date) );
				   
					subtract = (int) ( (new_time.getTimeInMillis() - old_time.getTimeInMillis()) / (1000*60*60*24) );
					
					if( subtract > 70 || subtract <= 0 )
					{   
					   error_message += "天數過長或過短\n";
					   
					   start_date_button.setTextColor(Color.RED);
					   end_date_button.setTextColor(Color.RED);
					}
				}
				else
				{   
					error_message += "日期錯誤\n";
					   
					if( old_year == null )
						start_date_button.setTextColor(Color.RED);
					
					if( new_year == null )
						end_date_button.setTextColor(Color.RED);
				}
				   
				if( kind == null)
				{
					error_message += "用電種類未選\n";
					kind_button.setTextColor(Color.RED);
				}
				   
				if( power_string == null)
				{
					error_message += "用電度數未填";
					power_button.setTextColor(Color.RED);
				}
				else if( Integer.valueOf(power_string) <= 0  )
				{
					error_message += "用電度數錯誤";
					power_button.setTextColor(Color.RED);
				}
				
				
				if(!error_message.equals(""))
				{
					AlertDialog.Builder warning_dialog = new AlertDialog.Builder(app_context);
					warning_dialog.setTitle("歐!歐歐!!");
					warning_dialog.setMessage(error_message);
					warning_dialog.setNeutralButton("重新填寫", null);
					warning_dialog.show();
				}
				else
				{
	    			browser.loadUrl("javascript:TotalMoney(new Array(" + old_year  + "," + old_month + "," + old_date  + ","
																	   + new_year  + "," + new_month + "," + new_date + "),"
																	   + "'" + kind + "'," + power_string + ")");
				}	 
	    	}
	    });
	   
	    return current_view;
    }
	
	private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null )
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
	   
	public class MyJavaScriptInterface 
	{
		//After Android 4.2 add @JavascriptInterface 
		@JavascriptInterface  
		public void showMoney(final String money)  
		{  
			app_activity.runOnUiThread(new Runnable() 
			{  
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					((TextView) findViewById(R.id.result_text_view)).setText(money + "元");
					
					Vibrator vibrator = (Vibrator) app_context.getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 200 milliseconds
					vibrator.vibrate(200);
						
					start_date_button.setTextColor(0xFF000000);
					end_date_button.setTextColor(0xFF000000);
					power_button.setTextColor(0xFF000000);
					kind_button.setTextColor(0xFF000000);	
				}
			});
		}
		 
		@JavascriptInterface
		public void showFormula(final String formula)
		{
			app_activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Log.i("formula",formula);
					
					((TextView) findViewById(R.id.result_formula_text_view)).setText(formula);	
				}
			});
		}
	}
	
	
	private OnClickListener back_on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if( v.getId() == R.id.title_bar_back_button )
			{
				FragmentTransaction transaction = app_activity.getFragmentManager().beginTransaction();
				Fragment fragment = new MainPageFragment();
				transaction.replace(R.id.fragment_content, fragment, "main_page").commit();
			}
			else
			{
				((TextView) findViewById(R.id.result_text_view)).setText("0元");
				((TextView) findViewById(R.id.start_date_text_view)).setText("");
				((TextView) findViewById(R.id.end_date_text_view)).setText("");
				((TextView) findViewById(R.id.kind_text_view)).setText("");
				((TextView) findViewById(R.id.power_text_view)).setText("");
				((TextView) findViewById(R.id.result_formula_text_view)).setText("");
				
				old_year = null;
				old_month = null;
				old_date = null;
			   
				new_year = null;
				new_month= null;
				new_date = null;
				
				power_string = null;
				kind = null; 
			
				start_date_button.setTextColor(0xFF000000);
				end_date_button.setTextColor(0xFF000000);
				power_button.setTextColor(0xFF000000);
				kind_button.setTextColor(0xFF000000);
			}
		}
	};
	
	   	
	private View date_picker_view;
	private View power_edit_view;
	private String[] kind_string;
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			date_picker_view = app_activity.getLayoutInflater().inflate( R.layout.fragment_calclight_date_picker, null, false);
			power_edit_view = app_activity.getLayoutInflater().inflate( R.layout.fragment_calclight_power_edittext, null, false);
			
			AlertDialog.Builder pinker_dialog = new AlertDialog.Builder(app_context); 
			
			if( v.getId() == R.id.start_date_button )
			{   
				pinker_dialog.setView(date_picker_view);
				pinker_dialog.setPositiveButton("OK" , dialog_on_click_listener );
			}	
			
			if( v.getId() == R.id.end_date_button )
			{
				pinker_dialog.setView(date_picker_view);
				pinker_dialog.setNegativeButton("OK" , dialog_on_click_listener );
			}	
			   
			if( v.getId() == R.id.kind_button)
			{   
				kind_string = new String[]{"住家","營業"};
				pinker_dialog.setItems(kind_string, dialog_on_click_listener);
			}
			   
			if( v.getId() == R.id.power_button)
			{   
				pinker_dialog.setTitle("使用度數");
				pinker_dialog.setView(power_edit_view);
				pinker_dialog.setNeutralButton("OK", dialog_on_click_listener);
			}
			
			pinker_dialog.show();
		}
	};
	
	private OnTouchListener on_touch_listener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			// TODO Auto-generated method stub
			/*
			if( v.getId() != R.id.start_calc_button  && v.getId() != R.id.back_button )
			{
				if( event.getAction() == MotionEvent.ACTION_DOWN)
					v.setBackgroundColor( Color.GRAY);
				
				if( event.getAction() == MotionEvent.ACTION_UP)
					v.setBackgroundColor( 0xFFADD8E6 );
			   
			}
			else if( v.getId() != R.id.back_button )
			{
				if( event.getAction() == MotionEvent.ACTION_DOWN)
					v.setBackgroundColor( 0xFF6495ED);
				
				if( event.getAction() == MotionEvent.ACTION_UP)
					v.setBackgroundColor( 0xFF00BFFF );
			}	
			else
			{
				if( event.getAction() == MotionEvent.ACTION_DOWN)
				{  
					v.setBackgroundColor( Color.BLACK );
					((Button)v).setTextColor(Color.WHITE);
				}	
				   
				if( event.getAction() == MotionEvent.ACTION_UP)
				{   
					v.setBackgroundColor( 0xFF0073BC );
					((Button)v).setTextColor(Color.WHITE);
				}
			}
			*/
			return false;
		}
	};
	
	
	private DialogInterface.OnClickListener dialog_on_click_listener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			// TODO Auto-generated method stub   
			DatePicker date_picker = (DatePicker) date_picker_view.findViewById(R.id.date_picker);
			
			if( which == DialogInterface.BUTTON_POSITIVE)
			{
				old_year = String.valueOf(date_picker.getYear() );
				old_month= String.valueOf(date_picker.getMonth() + 1 );
				old_date = String.valueOf(date_picker.getDayOfMonth() );
				
				//start_date_button.setText(old_year + "年\n" + old_month + "月\n" + old_date + "日" );
				((TextView) findViewById(R.id.start_date_text_view)).setText(String.valueOf(date_picker.getYear() - 1911) + "年" + old_month + "月" + old_date + "日" );
			}
			   
			if( which == DialogInterface.BUTTON_NEGATIVE )
			{
				new_year = String.valueOf(date_picker.getYear() );
				new_month= String.valueOf(date_picker.getMonth() + 1 );
				new_date = String.valueOf(date_picker.getDayOfMonth() );
				
				//end_date_button.setText(new_year + "年\n" + new_month + "月\n" + new_date + "日" );
				((TextView) findViewById(R.id.end_date_text_view)).setText(String.valueOf(date_picker.getYear() - 1911 ) + "年" + new_month + "月" + new_date + "日" );
			}
			   
			if( which == DialogInterface.BUTTON_NEUTRAL)
			{
				power_string = ((EditText) power_edit_view.findViewById(R.id.power)).getText().toString();
				
				if(power_string.length() > 0 )
				{   
					//power_button.setText(power_string + "度");
					((TextView) findViewById(R.id.power_text_view)).setText(power_string + "度");
				}	
				else
					power_string = null;
				   
				//hidden soft keyboard;
				InputMethodManager imm = (InputMethodManager) app_context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(power_edit_view.getWindowToken(), 0);  
			}
			   
			if( which >= 0 )
			{  
				String[] kind_list = new String[]{"home","business"};
				   
				//kind_button.setText(kind_string[which]);
				((TextView) findViewById(R.id.kind_text_view)).setText(kind_string[which]);
				kind = kind_list[which];
			}
	   	}
	};
   
   	private class DoingInBackgroundTask extends AsyncTask<String,Integer,Integer>
   	{	
		byte[] response;
		//Document document;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			Integer result_value = null;
			
			//String map_data_url = "http://cksleeper.dlinkddns.com/mypage.html";
			String map_data_url = "https://tpwb.taipower.com.tw/app/mypage.html";
			
			try 
			{
				response = HttpConnectResponse.onOpenConnection(map_data_url, "GET", null, HttpConnectResponse.COOKIE_CLEAR, HttpConnectResponse.HTTP_NONREDIRECT);
				//document = Jsoup.parse(new URL(map_data_url), 30000);
				
				result_value = 0;
			} 
			catch (SSLHandshakeException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (URISyntaxException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result_value;
		}
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			if( result != null )
			{
				//String map_data_url = "http://cksleeper.dlinkddns.com/map_info_online.xml";
				//Document document = Jsoup.parse(new URL(map_data_url), 30000);
				
				Document document = null;
				
				try 
				{
					String mypage_online = new String(response,"utf-8");
					
					document = Jsoup.parse(mypage_online);
					
					Elements new_html_elements = document.select("version");
					String new_html_version = new_html_elements.first().text();
					Log.i("new_html_version",new_html_version);
				
					if( Double.valueOf(new_html_version) > Double.valueOf(online_html_version) &&  Double.valueOf(new_html_version) > Double.valueOf(offline_html_version) )
					{
						FileOutputStream map_info_online_stream = app_activity.openFileOutput("mypage.html", Context.MODE_PRIVATE);
					
						map_info_online_stream.write(response);
						map_info_online_stream.close();
						
						Toast.makeText(app_context, "電費單價已更新，請離開計算機並再次進入", Toast.LENGTH_LONG).show();
					}
					
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (FileNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		
		
	}
	   
}