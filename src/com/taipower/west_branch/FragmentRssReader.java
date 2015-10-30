package com.taipower.west_branch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

































import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentRssReader extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private FragmentRssReader this_class;
	
	private ImageView left_arrow; 
	private ImageView right_arrow; 
	
	private Animation slight ;
	
    private ArrayList<ArrayList<String>> headlines_all;
    private ArrayList<ArrayList<String>> links_all;
    
    private int screen_width; 
    
    private ListView rss_list_view;
    
    private View current_view;
    
    private Button rss_tag1;
    private Button rss_tag2;
    private Button rss_tag3;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {
    	this.app_context = this.getActivity();
    	this.app_activity = this.getActivity();
    	this.this_class = this;
    	
    	current_view = inflater.inflate(R.layout.fragment_rss_reader ,container, false );
    	
    	((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_rss_reader);
    	
    	ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setVisibility(View.INVISIBLE);
		title_bar_send_button.setOnClickListener(null);
    	
    	
    	
    	
        DmInfor screen = new DmInfor(app_activity, app_context);
        screen_width = screen.v_width;
        
        /*bad solution
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        */
        
        RelativeLayout view_pager_layout = (RelativeLayout) findViewById(R.id.view_pager_layout);
        view_pager_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) ((screen_width / 16 ) * 9) ));
        
        
        
        		
        rss_tag1 = (Button) findViewById(R.id.rss_tag1);
        rss_tag1.setBackgroundResource(R.drawable.base_data_button_background_pressed);
        rss_tag1.setOnClickListener(on_click_listener);
        
        rss_tag2 = (Button) findViewById(R.id.rss_tag2);
        rss_tag2.setOnClickListener(on_click_listener);
        
        rss_tag3 = (Button) findViewById(R.id.rss_tag3);
        rss_tag3.setOnClickListener(on_click_listener);
        
        
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				FragmentTransaction transaction =  app_activity.getFragmentManager().beginTransaction();  
				Fragment temp_fragment = new MainPageFragment();  
		        transaction.replace(R.id.fragment_content, temp_fragment ,"main_page" );  
		        transaction.commit();
			}
        });
        
        new LoadingDataAsyncTask().execute();
		
        return current_view;
    }
    
    private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null)
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
    
    @Override
    public void onResume()
    {
    	Log.i("fragment on resume","on resume");
    	super.onResume();
    }
    
    
    int rss_tag = 0;
    
    private OnClickListener on_click_listener = new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			
			rss_tag1.setBackgroundResource(R.drawable.base_data_button_background_normal);
			rss_tag2.setBackgroundResource(R.drawable.base_data_button_background_normal);
			rss_tag3.setBackgroundResource(R.drawable.base_data_button_background_normal);
			
			switch( v.getId() )
			{
				case R.id.rss_tag2:
					rss_tag = 1;
					rss_tag2.setBackgroundResource(R.drawable.base_data_button_background_pressed);
					break;
				case R.id.rss_tag3:
					rss_tag = 2;
					rss_tag3.setBackgroundResource(R.drawable.base_data_button_background_pressed);
					break;
				default:
					rss_tag = 0;
					rss_tag1.setBackgroundResource(R.drawable.base_data_button_background_pressed);
					break;
			}
			 
			ArrayAdapter adapter = new ArrayAdapter(app_context ,android.R.layout.simple_list_item_1 ,headlines_all.get(rss_tag));
			
			ArrayList<HashMap<String,Object>> rss_list = new ArrayList<HashMap<String,Object>>();
    		
    		for(int i = 0 ; i < headlines_all.get(rss_tag).size() ; i++ )
    		{
    			HashMap<String,Object> map_map = new HashMap<String,Object>();
    			map_map.put("icon", Integer.valueOf(R.drawable.second_layer_menu_icon));
    			map_map.put("title", headlines_all.get(rss_tag).get(i));
    			
    			rss_list.add(map_map);
    			
    			map_map = null;
    		}
    		
    		SimpleAdapter qq = new SimpleAdapter(app_context, rss_list, R.layout.fragment_rss_reader_list_view, new String[]{"icon","title"}, new int[]{R.id.rss_reader_icon,R.id.rss_reader_title});
    		
			rss_list_view.setAdapter(qq);
			
		}
    };
    
    
    class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> 
    {
    	protected void onPreExecute ()
    	{
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    		
    		publishProgress(9);
    		
    		super.onPreExecute();
    	}
    	
		@SuppressLint("NewApi")
		protected  Integer doInBackground(String... urls) 
        {
        	
    		boolean is_online = ASaBuLuCheck.isOnline(app_activity);
    		if ( is_online ) 
    		{
    		    		
    		try
    		{
    			headlines_all = new ArrayList<ArrayList<String>>();
    	        links_all = new ArrayList<ArrayList<String>>();
    	        
    			for(int i = 0 ; i < 3 ; i++ )
    			{
    				URL url = new URL("http://www.taipower.com.tw/content/news/RSSdata.aspx?rss=" + String.valueOf(i +1) );
    				
    				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    				factory.setNamespaceAware(false);
    				XmlPullParser xpp = factory.newPullParser();
    			
    				xpp.setInput(url.openConnection().getInputStream(), "UTF-8");
    				
    				ArrayList<String> temp_headlines = new ArrayList<String>();
        	        ArrayList<String> temp_links = new ArrayList<String>();
    				
    				boolean insideItem = false;
             
    				// Returns the type of current event: START_TAG, END_TAG, etc..
    				int eventType = xpp.getEventType();
    				
    				while (eventType != XmlPullParser.END_DOCUMENT) 
    				{
    					if (eventType == XmlPullParser.START_TAG) 
    					{
    						if (xpp.getName().equalsIgnoreCase("item")) 
    						{
    							insideItem = true;
    						}
    						else if (xpp.getName().equalsIgnoreCase("title")) 
    						{
    							if (insideItem)
    								temp_headlines.add(xpp.nextText()); //extract the headline
    						} 
    						else if (xpp.getName().equalsIgnoreCase("link")) 
    						{
    							if (insideItem)
    								temp_links.add(xpp.nextText()); //extract the link of article
    						}
    					}
    					else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
    					{
    						insideItem = false;
    					}	
             
    					eventType = xpp.next(); //move to next element
    				}
    				
    				headlines_all.add(temp_headlines);
    				links_all.add(temp_links);
    			}
                
    			byte[] response = HttpConnectResponse.onOpenConnection("http://www.taipower.com.tw", "get", null, HttpConnectResponse.COOKIE_KEEP, HttpConnectResponse.HTTP_REDIRECT);
    				
    			Document document = Jsoup.parse(new String(response,"utf-8"));
    			
    			Element id_pics = document.getElementById("pic");
    			Elements a_href = id_pics.select("a");
    			Elements image = id_pics.select("img");
    			
    			File download_file_dir;
    			File download_file;
    			
    			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            		download_file_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/taipower/");
            	else
            		download_file_dir = new File(Environment.DIRECTORY_DOWNLOADS + "/taipower/" );
            		
    			download_file_dir.mkdirs();
    			
    			view_list = new ArrayList<View>();
    			
    			for(int i = 0 ; i < a_href.size() ; i++ )
    			{
    				//URL url = new URL( HttpConnectResponse.urlencodeLikeBrowser( "http://www.taipower.com.tw" + image.get(i).attr("src") ) );
    				URL url = new URL( "http://www.taipower.com.tw" + HttpConnectResponse.urlencodeChinese( image.get(i).attr("src")) );
    				
        			//Bitmap image_bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        			
        			ImageButton temp_button = new ImageButton(app_context);
                	
        			download_file = new File(download_file_dir, (image.get(i).attr("src")).replace("/UpFile/TaipowerImageFile/", "") );
        			
        			if(download_file.exists() && download_file.length() != 0)
        			{
        				//Log.i("file path :",download_file.getAbsolutePath());
        				
        				temp_button.setBackground( BitmapDrawable.createFromPath(download_file.getAbsolutePath()) );
        				
        				//FileInputStream fis = new FileInputStream(download_file);
        				//temp_button.setBackground( BitmapDrawable.createFromStream(fis,""+i ));
        				
        				Log.i("loading by ","cache");
        			}
        			else
        			{
        				InputStream input_stream =  url.openConnection().getInputStream();
        				//InputStream other_input_stream =  url.openConnection().getInputStream();
        				
        				/*
        				if( VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN ) 
        					temp_button.setBackground( BitmapDrawable.createFromStream(input_stream, "" + i) );
        				else
        					temp_button.setBackgroundDrawable( BitmapDrawable.createFromStream(input_stream, "" + i) );
        				*/
        				Log.i("loading by ","internet");
        				
        				FileOutputStream fos = new FileOutputStream(download_file);
        				//Bitmap temp_bitmap = BitmapFactory.decodeStream(input_stream);
        				//temp_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); 
        				
        				fos.write( HttpConnectResponse.inputStreamToByteArray(input_stream));
        				
        				fos.close();
            			
        				Log.i("write into ","cache");
        				temp_button.setBackground( BitmapDrawable.createFromPath(download_file.getAbsolutePath()) );
        				
        			}
        			
        			
                	temp_button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                	temp_button.setTag(a_href.get(i).attr("href"));
                	//temp_button.startAnimation(button_animation);
                	temp_button.setOnClickListener(page_view_on_click_listener);
                    
        			
                    LinearLayout temp_linearlayout = new LinearLayout(app_context);
                    temp_linearlayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    temp_linearlayout.setGravity(Gravity.CENTER);
                    temp_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
                    temp_linearlayout.addView(temp_button);
                    
                    
                    view_list.add(temp_linearlayout);
        			
    				//Log.i("href:", a_href.get(i).attr("href") );
    				//Log.i("image:", image.get(i).attr("src") );
    				//Log.i("image title:", image.get(i).attr("title") );
    			}
    			
    			
    		}
        	catch (MalformedURLException e) 
            {
                e.printStackTrace();
                return 0;
            }
        	catch (XmlPullParserException e) 
            {
                e.printStackTrace();
                return 1;
            }
        	catch (IOException e) 
            {
                e.printStackTrace();
                return 2;
            }
        	catch ( RuntimeException e) 
            {
                e.printStackTrace();
                return 3;
            } 
    		catch (URISyntaxException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		
        	
    		}
    		else
    			return 4;
    		
        	return null;
        
        }

        
    	Dialog process_persent_pointer = null;
    	
        protected void onProgressUpdate(Integer... progress) 
        {
        	//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
        	
        	String message = "更新線上資料"; 
			
        	process_persent_pointer = CreateLoadingDialog.createLoadingDialog(app_context, message ,CreateLoadingDialog.NON_DOWNLOAD_TAG, 0,CreateLoadingDialog.CANCELABLE);
        	((MainPage)app_activity).PRESENT_DIALOG_POINTER = process_persent_pointer;
        	super.onProgressUpdate(progress);
        }
        
        
        ArrayList<View> view_list;
        
        @Override
        protected void onPostExecute(Integer result) 
        {
        	CreateLoadingDialog.dialog_dismiss(process_persent_pointer);
        	process_persent_pointer = null;
        	
        	if( result != null)
        	{
        	
        	
        	if( result == 0 )
            	Toast.makeText(app_context, "0" , Toast.LENGTH_LONG).show();
        	if( result == 1 )
            	Toast.makeText(app_context, "1" , Toast.LENGTH_LONG).show();
        	if( result == 2 )
            	Toast.makeText(app_context, "I/O資料錯誤" , Toast.LENGTH_LONG).show();
        	if( result == 3 )
            	Toast.makeText(app_context, "網路位置錯誤" , Toast.LENGTH_LONG).show();
        	if( result == 4 )
            	Toast.makeText(app_context, "網路無法連線 請檢查網際網路是否開啟" , Toast.LENGTH_LONG).show();
        	}
        	else 
        	{
        		
        		slight = new AlphaAnimation(1.0f , 0.0f );
                slight.setRepeatCount(Animation.INFINITE);
                slight.setRepeatMode(Animation.REVERSE);
                slight.setDuration(500); //You can manage the time of the blink with this parameter
                slight.setStartOffset(0);
                
                left_arrow = (ImageView) findViewById(R.id.left_arrow);
                //left_arrow.setAnimation(slight);
                left_arrow.setVisibility(View.INVISIBLE);
                
                right_arrow = (ImageView) findViewById(R.id.right_arrow);
                right_arrow.setVisibility(View.INVISIBLE);
                right_arrow.setVisibility(View.VISIBLE);
                //right_arrow.setAnimation(slight);

        		
        		PagerAdapter pager_adapter = new MyPagerAdapter(view_list);
                
                ViewPager view_pager = (ViewPager) findViewById(R.id.view_pager);
        		view_pager.setAdapter(pager_adapter);
        		
                //view_pager.setCurrentItem(0);
                view_pager.setOnPageChangeListener(page_change_listener);
                
                //view pager auto scroll
                //this_class.viewPagerAutoScroll(view_pager, view_list.size() );
                
        		//must run after onCreate(), because scroll is not initialization @ onCreate();
                HorizontalScrollView button_scroll_view = (HorizontalScrollView) findViewById(R.id.button_scroll_view);
                button_scroll_view.scrollTo(300, 0);
                
        		// Binding data
        		ArrayAdapter adapter = new ArrayAdapter(app_context ,android.R.layout.simple_list_item_1 ,headlines_all.get(rss_tag));
        		
        		
        		ArrayList<HashMap<String,Object>> rss_list = new ArrayList<HashMap<String , Object>>();
        		
        		for(int i = 0 ; i < headlines_all.get(rss_tag).size() ; i++ )
        		{
        			HashMap<String,Object> map_map = new HashMap<String,Object>();
        			map_map.put("icon", Integer.valueOf(R.drawable.second_layer_menu_icon));
        			map_map.put("title", headlines_all.get(rss_tag).get(i));
        			
        			rss_list.add(map_map);
        			
        			map_map = null;
        		}
        		
        		SimpleAdapter qq = new SimpleAdapter(app_context, rss_list, R.layout.fragment_rss_reader_list_view, new String[]{"icon","title"}, new int[]{R.id.rss_reader_icon,R.id.rss_reader_title});
        		
        		
        		rss_list_view = (ListView) findViewById(R.id.rss_list_view);
        		//rss_list_view.setAdapter(adapter);
        		rss_list_view.setAdapter(qq);
        		rss_list_view.setOnItemClickListener(on_item_click_listener);
        		
        	}
        }
        
    }
    
    
    private class MyPagerAdapter extends PagerAdapter
	{
		ArrayList<View> adapter_list_view; 
		
		public MyPagerAdapter(ArrayList<View> list_view )
		{
			adapter_list_view = list_view;
			
		}
		
		@Override
	    public CharSequence getPageTitle(int position) 
		{
	        return "title";
	    }
	     
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return adapter_list_view.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) 
		{
			// TODO Auto-generated method stub
			return arg0 == (arg1);
		}
		
		
		
		@Override  
        public void destroyItem(ViewGroup container, int position, Object object) 
		{  
            ((ViewPager)container).removeView(adapter_list_view.get(position));
        }  
		
		//初始化position位置的界面
		@Override
		public Object instantiateItem(View container, int position) 
		{  
			((ViewPager)container).addView(adapter_list_view.get( position), 0);  
            return   adapter_list_view.get( position);
        }
	}
    
    
    private OnPageChangeListener page_change_listener = new OnPageChangeListener()
	{
		@Override
		public void onPageScrollStateChanged(int postion) 
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) 
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int postion) 
		{
			// TODO Auto-generated method stub
			switch(postion)
			{
				case 7:
					//right_arrow.clearAnimation();
					right_arrow.setVisibility(View.INVISIBLE);
					break;
				case 0:
					//left_arrow.clearAnimation();
					left_arrow.setVisibility(View.INVISIBLE);
					break;
				default:
					//right_arrow.startAnimation(slight);
					right_arrow.setVisibility(View.VISIBLE);
					//left_arrow.startAnimation(slight);
					left_arrow.setVisibility(View.VISIBLE);
					break;
			}
		}
	}; 
    
	
	public void viewPagerAutoScroll(final ViewPager view_pager , final int pager_count  )
	{
		final Handler handler = new Handler();
		final Runnable Update = new Runnable()
        {
			boolean return_tag;       	
			
        	public void run() 
            {	
        		int current_page = view_pager.getCurrentItem();
        		
        		if ( current_page == (pager_count - 1) ) 
                	return_tag = true;
                else if( current_page == 0 )
                	return_tag = false;

                if(return_tag)
                	current_page--;
                else
                	current_page++;
                	
                view_pager.setCurrentItem( current_page , true);
            }
        };
        
        
        TimerTask timer_task = new TimerTask()
        {
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				handler.post(Update);
			}	
        };
        
        Timer swipe_timer = new Timer();
        swipe_timer.schedule(timer_task, 2000, 5000);
        
	}
	
	
	
	private OnClickListener page_view_on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) v.getTag()));
			//intent.setAction();
			//intent.setData();
			startActivity(intent);
		}
	};
    
    private OnItemClickListener on_item_click_listener  = new OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
		{
			// TODO Auto-generated method stub
			Uri uri = Uri.parse((links_all.get(rss_tag)).get(position) );
    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    		startActivity(intent);	
		}
    };
    
    
}