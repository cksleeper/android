package com.taipower.west_branch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("JavascriptInterface")
public class MainPageFragment extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private MainPageFragment this_class;
	
	private View current_view;
	private WebView loading_webview;
	
	private DmInfor dm;
	
	private String fragment_tag;
	
	private Animation button_animation;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {  
		this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		this.this_class = this;
		
		current_view= inflater.inflate(R.layout.main_page_fragment, container, false); 
		
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_main);
		((LinearLayout) findViewById(R.id.title_bar_layout)).setBackgroundResource(0);
		
		ImageButton title_bar_menu_button = (ImageButton) findViewById(R.id.title_bar_menu_button);
		title_bar_menu_button.setBackgroundResource(R.drawable.title_bar_menu_button);
		
		ImageButton title_bar_back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
		title_bar_back_button.setBackgroundResource(R.drawable.title_bar_back_button);
		title_bar_back_button.setOnClickListener(on_click_listener);
		
		
		ImageButton[] main_button_array = new ImageButton[10]; 
		
		for(int i = 0 ; i < main_button_array.length ; i ++)
		{	
			main_button_array[i] = (ImageButton) findViewById(R.id.main_fragment_button0 + i);
			main_button_array[i].setOnClickListener(on_click_listener);
			main_button_array[i].setOnTouchListener(on_touch_listener);
		}
        
		String version_name = "";
		try 
		{
			version_name = (app_context.getPackageManager().getPackageInfo(app_context.getPackageName(), 0)).versionName;
		} catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		((TextView) findViewById(R.id.app_version)).setText(version_name);
		
		dm = new DmInfor(app_activity, app_context);
		
		/*
		loading_webview = (WebView) findViewById(R.id.loading_webview);
		loading_webview.setWebViewClient(new WebViewClient());
		
		int web_view_scale_rate = (int) (100.0f * ((float) dm.scale / 3.0f) * (7.0f/10.0f));
		//Log.i("qq","" + dm.v_width + " "+ web_view_scale_rate );
		
		loading_webview.setInitialScale( web_view_scale_rate );
		
		loading_webview.getSettings().setJavaScriptEnabled(true);
		loading_webview.getSettings().setLoadWithOverviewMode(true);
		loading_webview.getSettings().setUseWideViewPort(true);
		loading_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		loading_webview.getSettings().setSupportMultipleWindows(false);
		loading_webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTML_OUT");
		loading_webview.setWebViewClient(new WebViewClient()
		{
			
		});
		//loading_webview.loadUrl("http://www.taipower.com.tw/Meter/power_today_meter_adv.html");
		loading_webview.loadUrl("file:///android_asset/loading_today.html");
		
		//loading_webview.setOnClickListener(on_click_listener);  //not working
		//loading_webview.setOnTouchListener(on_touch_listener);
		*/
		
		new BackgroundAsyncTask().execute();		
		
		button_animation = new ScaleAnimation(1.0f, 0.9f,1.0f,0.9f,Animation.RELATIVE_TO_PARENT, 0.5f,Animation.RELATIVE_TO_PARENT ,0.5f);
        button_animation.setRepeatCount(Animation.RESTART);
        button_animation.setRepeatMode(Animation.REVERSE);
        button_animation.setDuration(500);
		
        fragment_tag = app_activity.getFragmentManager().findFragmentById(R.id.fragment_content).getTag();
        
        Log.i("fragment class",fragment_tag);
        
        
		return current_view;
	}
	
	
	private View findViewById(int id)
	{
		View view;
		
		if((view = current_view.findViewById(id)) == null)
			view = app_activity.findViewById(id);
		
		return view;
	}
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			
			FragmentTransaction transaction =  app_activity.getFragmentManager().beginTransaction();
			Fragment fragment = null;
			
			Bundle bundle = new Bundle();
			
			switch(v.getId())
			{
				case R.id.main_fragment_button0:
					fragment = new FragmentRssReader();  
			        transaction.replace(R.id.fragment_content, fragment ,"rss_reader");  
			        transaction.commit();
					break;
				case R.id.main_fragment_button1:  
					fragment = new FragmentTaipowerTV();  
			        transaction.replace(R.id.fragment_content, fragment , "taipower_tv");  
			        transaction.commit();
					break;
				case R.id.main_fragment_button2:
					fragment = new FragmentSecondLayerMenu();  
					bundle.putString("second_layer_content", "about_meter");
					fragment.setArguments(bundle);
					transaction.replace(R.id.fragment_content, fragment ,"second_layer");  
			        transaction.commit();
					break;
				case R.id.main_fragment_button3:
					fragment = new FragmentSecondLayerMenu();  
					bundle.putString("second_layer_content", "progress_state");
					fragment.setArguments(bundle);
			        transaction.replace(R.id.fragment_content, fragment , "second_layer");  
			        transaction.commit();
					break;
				case R.id.main_fragment_button4:
					fragment = new FragmentServiceMap();  
			        transaction.replace(R.id.fragment_content, fragment , "service_map" );  
			        transaction.commit();
					break;
				case R.id.main_fragment_button5:
					fragment = new FragmentSecondLayerMenu();  
					bundle.putString("second_layer_content", "contact_us");
					fragment.setArguments(bundle);
			        transaction.replace(R.id.fragment_content, fragment , "second_layer");  
			        transaction.commit();
					break;
				case R.id.main_fragment_button6:
					fragment = new FragmentCalcLight();
					transaction.replace(R.id.fragment_content, fragment, "calc_light").commit();
					break;
				case R.id.main_fragment_button7:
					fragment = new FragmentSecondLayerMenu();  
					bundle.putString("second_layer_content", "online_service");
					fragment.setArguments(bundle);
			        transaction.replace(R.id.fragment_content, fragment , "second_layer");  
			        transaction.commit();
					break;
				case R.id.main_fragment_button8:
					fragment = new FragmentEbppsLight();
					transaction.replace(R.id.fragment_content, fragment, "ebpps_light").commit();
					break;
				case R.id.main_fragment_button9:
					fragment = new FragmentSecondLayerMenu();  
					bundle.putString("second_layer_content", "no_power");
					fragment.setArguments(bundle);
			        transaction.replace(R.id.fragment_content, fragment , "second_layer").commit();  
					break;
				case R.id.title_bar_back_button:
					Log.i("back on fragment","fragment");
					app_activity.finish();
					break;
				default :
					break;
			}
		}
	};
	
	private OnTouchListener on_touch_listener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{	
			//Log.i("id:", "" + v.getId());
			/*
			if(v.getId() == R.id.loading_webview)
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://stpc00601.taipower.com.tw/loadGraph/loadGraph/load_reserve.html"));
				app_activity.startActivity(intent);
			}
			else
			*/	v.startAnimation(button_animation);
			
			return false;
		}
	}; 
	
	@Override
	public void onResume()
	{
		((ImageButton) app_activity.findViewById(R.id.title_bar_menu_button)).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setClass(app_context, SettingDialog.class);
				app_activity.startActivity(intent);
			}
		});
		
		super.onResume();
	}
	
	
	private class BackgroundAsyncTask extends AsyncTask<Integer,Integer,Integer>
	{
		String laoding_data;
		
		@Override
		protected Integer doInBackground(Integer... params) 
		{
			// TODO Auto-generated method stub
			
			Integer return_value = null;
			
			if( ASaBuLuCheck.isOnline(app_activity))
			{
				try 
				{
					URL url = new URL("http://www.taipower.com.tw/");
					InputStream input_stream = ((HttpURLConnection) url.openConnection()).getInputStream();
					
					String qq = new String(HttpConnectResponse.inputStreamToByteArray(input_stream),"UTF-8");
					
					URL loading_url = new URL("http://stpc00601.taipower.com.tw/Meter/js/loadpara.js");
					String laoding_js = new String( HttpConnectResponse.inputStreamToByteArray(loading_url.openConnection().getInputStream()),"UTF-8");
					
					laoding_js = laoding_js.replace("\n", "").replace("\t", "").replace(" ","").replace("\r", "").replace(",","").replace("\"\"", "\"");
					Log.i("laoding_js",laoding_js );
					int load_info_index = laoding_js.indexOf("loadInfo");
					int load_info_end =  laoding_js.indexOf("<!");
					
					laoding_data =  laoding_js.substring(load_info_index + 8 + 2,load_info_end - 1);
					
					Log.i("laoding_data",laoding_data);
					
					if(qq == null) 
						return_value = 1;
				} 
				catch (MalformedURLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return_value = 1;
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				
					return_value = 1;
				}
			}
			else
				return_value = 1;
			
			return return_value;
		}
		
		protected void onPostExecute(Integer result) 
		{
			if(result != null) 
			{
				TextView error_text_view = new TextView(app_context);
				error_text_view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				error_text_view.setGravity(Gravity.CENTER);
				error_text_view.setText("網路發生問題！！\n請稍候再試！！");
				
				//loading_webview.addView(error_text_view);
			
				LinearLayout loading_layout = (LinearLayout) current_view.findViewById(R.id.loading_layout);
				loading_layout.removeAllViews();
				loading_layout.addView(error_text_view);
			}
			else
			{
				
				if( (System.currentTimeMillis() & 2)  == 0x0)
				{
				Thread task = new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						final String[] laoding_data_array = laoding_data.split("\"");
						//final LinearLayout loading_layout = (LinearLayout) current_view.findViewById(R.id.laoding_layout);
						final ImageView loading_image_degree = (ImageView) current_view.findViewById(R.id.loading_degree);
						final TextView loading_current_kw = (TextView) current_view.findViewById(R.id.loading_current_kw);
						final TextView loading_evaluate_kw = (TextView) current_view.findViewById(R.id.loading_evaluate_kw);
						final TextView loading_max_kw = (TextView) current_view.findViewById(R.id.loading_max_kw);
						final TextView loading_rate = (TextView) current_view.findViewById(R.id.loading_rate);
						
						int loading_degree = (int) (Float.valueOf(laoding_data_array[1]) / Float.valueOf(laoding_data_array[3]) * 58.0f) ;
						
						for(int i = 1 ; i < loading_degree ; i++)
						{
							final int k = i;
							
							try 
							{
								Thread.sleep(100);
								
								app_activity.runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										// TODO Auto-generated method stub
										
										//if(loading_graphy != null)
										//{	
											//loading_layout.removeAllViews();
											//loading_graphy = null;
										//}
									
										//String laoding = String.valueOf(Float.valueOf(laoding_data_array[1]) * ((float) k / 100.0f));
										//loading_graphy = new LoadingGraphy( app_context, new String[]{"",laoding,"",laoding_data_array[3]});
										//loading_layout.addView(loading_graphy);
										
										loading_image_degree.setImageResource(R.drawable.loading_degree00 + k);
										loading_current_kw.setText(String.format("%4.1f萬瓩",(Float.valueOf(laoding_data_array[1]) * (float)k  % 10000.0f)));
										loading_evaluate_kw.setText(String.format("%4.1f萬瓩",(Float.valueOf(laoding_data_array[2]) * (float)k  % 10000.0f)));
										loading_max_kw.setText(String.format("%4.1f萬瓩",(Float.valueOf(laoding_data_array[3]) * (float)k  % 10000.0f)));
									}
								});
							} 
							catch (InterruptedException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						app_activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								// TODO Auto-generated method stub
								loading_current_kw.setText(laoding_data_array[1] + "萬瓩");
								loading_evaluate_kw.setText(laoding_data_array[2] + "萬瓩");
								loading_max_kw.setText(laoding_data_array[3] + "萬瓩");
								((TextView) current_view.findViewById(R.id.loading_time)).setText(laoding_data_array[4].replace("更新", ""));
								loading_rate.setText(String.format("負載率%d%%",(int)((Float.valueOf(laoding_data_array[1]) / Float.valueOf(laoding_data_array[3]))*100)));
							}
						});
					}
				});
				task.start();
				}
				else
				{
					//loading_webview = (WebView) findViewById(R.id.loading_webview);
					WebView loading_webview = new WebView(app_context);
					loading_webview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					
					loading_webview.setWebViewClient(new WebViewClient());
					
					int web_view_scale_rate = (int) (100.0f * ((float) dm.scale / 3.0f) * (7.0f/10.0f));
					//Log.i("qq","" + dm.v_width + " "+ web_view_scale_rate );
					
					loading_webview.setInitialScale( web_view_scale_rate );
					
					loading_webview.getSettings().setJavaScriptEnabled(true);
					loading_webview.getSettings().setLoadWithOverviewMode(true);
					loading_webview.getSettings().setUseWideViewPort(true);
					loading_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
					loading_webview.getSettings().setSupportMultipleWindows(false);
					loading_webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTML_OUT");
					loading_webview.setWebViewClient(new WebViewClient()
					{
						
					});
					//loading_webview.loadUrl("http://www.taipower.com.tw/Meter/power_today_meter_adv.html");
					loading_webview.loadUrl("file:///android_asset/loading_today.html");
					
					//loading_webview.setOnClickListener(on_click_listener);  //not working
					//loading_webview.setOnTouchListener(on_touch_listener);
					
					LinearLayout loading_layout = (LinearLayout) current_view.findViewById(R.id.loading_layout);
					loading_layout.removeAllViews();
					loading_layout.addView(loading_webview);
				}
				
			}
		}
	}
	
	LoadingGraphy loading_graphy = null;
	
	private class LoadingGraphy extends View implements Runnable
	{
		float view_height;
		String[] loading_data;
		float loading_limit_height;
		float loading_current_height;
		
		public LoadingGraphy(Context context, String[] loading_data)
		{
			super(context);
			
			this.loading_data = loading_data;
			this.view_height = dm.scale * 190.0f;
			this.loading_limit_height = view_height;
			this.loading_current_height = (Float.valueOf(loading_data[1]) / Float.valueOf(loading_data[3])) * view_height;
		}
		
		@Override
		public void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			
			Paint loading_green = new Paint();
			loading_green.setColor(0xFF04D215);
			loading_green.setStrokeWidth(30.0f);
			
			Paint loading_yellow = new Paint();
			loading_yellow.setColor(0xFFF8FF01);
			loading_yellow.setStrokeWidth(100.0f);
			
			Paint loading_orange = new Paint();
			loading_orange.setColor(0xFF9E01);
			loading_orange.setStrokeWidth(100.0f);
			
			Paint loading_red = new Paint();
			loading_red.setColor(0xFFFF0F00);
			loading_red.setStrokeWidth(100.0f);
			
			Paint loading_black = new Paint();
			loading_black.setColor(0xFF000000);
			loading_black.setStrokeWidth(100.0f);
			
			canvas.drawLine(0.0f, view_height, 0.0f, view_height - loading_current_height , loading_green);
			
		}

		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			while(!Thread.currentThread().isInterrupted())  
	        {  
	            postInvalidate();   	//update ui 
	        }  
		}
	}
	
	
	class MyJavaScriptInterface
	{
		
	}
	
}
