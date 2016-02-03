package com.taipower.west_branch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParserException;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class FragmentTaipowerTV extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	
	private View current_view;
	
	private int view_width;

	//private ImageView left_arrow; 
	//private ImageView right_arrow; 
	//private Animation slight ;
	
	private ArrayList<View> view_list;
	
    private static final int NUMBER_OF_VIDEOS_RETURNED = 10;
    private LinearLayout indicator_layout;
    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;
    private YouTube.Search.List query;
    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param args command line args.
     */
	
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {  
        
		this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		
		current_view = inflater.inflate(R.layout.fragment_taipower_tv , container, false );  
		
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_taipower_tv);
		
		ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setVisibility(View.INVISIBLE);
		title_bar_send_button.setOnClickListener(null);
		
		
		DmInfor screen = new DmInfor(app_activity, app_context);
        int screen_width = screen.v_width;
        int screen_height = screen.v_height;
        view_width = screen_width;
        LayoutParams button_layout_params = new LayoutParams(view_width, (int) (view_width/16)*9);
        
        //LinearLayout scroll_content = (LinearLayout) view.findViewById(R.id.scroll_content);
        //scroll_content.setGravity(Gravity.CENTER_VERTICAL);
        
        /*
        slight = new AlphaAnimation(1.0f , 0.0f );
        slight.setRepeatCount(Animation.INFINITE);
        slight.setRepeatMode(Animation.REVERSE);
        slight.setDuration(500); //You can manage the time of the blink with this parameter
        slight.setStartOffset(0);
        
        
        left_arrow = (ImageView) current_view.findViewById(R.id.left_arrow);
        //left_arrow.setAnimation(slight);
        left_arrow.setVisibility(View.INVISIBLE);
        
        right_arrow = (ImageView) current_view.findViewById(R.id.right_arrow);
        //right_arrow.setAnimation(slight);
        right_arrow.setVisibility(View.INVISIBLE);
        right_arrow.setVisibility(View.VISIBLE);
        */
        
        new DoingBackgroundAsyncTask().execute(null,null);
        
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				FragmentTransaction transaction =  app_activity.getFragmentManager().beginTransaction();  
				Fragment fragment = new MainPageFragment();  
		        transaction.replace(R.id.fragment_content, fragment ,"main_page");  
		        transaction.commit();
			}
        });
        
		return current_view;
	}
	
	private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null)
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
	
	
	private class MyPagerAdapter extends PagerAdapter
	{
		ArrayList<View> adapter_list_view; 
		
		public MyPagerAdapter(ArrayList<View> list_view)
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
			//return adapter_list_view.size();
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
			//Log.i("position", "" + position);
			if( position == (adapter_list_view.size() -1 ))
			{
				//Log.i("destroyItem postion","" + (adapter_list_view.size() -1) + " " + adapter_list_view.get(adapter_list_view.size() -1).toString());
				//((ViewPager)container).removeView(adapter_list_view.get((adapter_list_view.size() -1 )));
			}
			
			{	
				//Log.i("destroyItem postion", position + " " + adapter_list_view.get(position).toString());
				((ViewPager)container).removeView(adapter_list_view.get(position));
			}
        }  
		
		//初始化position位置的界面
		@Override
		public Object instantiateItem(View container, int position) 
		{  
			//Log.i("position", "" + position);
			
			if( position == 0)
			{
				//Log.i("instantiateItem postion",( adapter_list_view.size() - 1) + " " + adapter_list_view.get( adapter_list_view.size() - 1).toString());
				((ViewPager)container).removeView(adapter_list_view.get(adapter_list_view.size() - 1));
				((ViewPager)container).addView(adapter_list_view.get( adapter_list_view.size() - 1));	
			}
			else if( position == (adapter_list_view.size() -1 ))
			{
				//Log.i("instantiateItem postion","1 " +  adapter_list_view.get(1).toString());
				((ViewPager)container).removeView(adapter_list_view.get(1));
				((ViewPager)container).addView(adapter_list_view.get(1));
			}
			
			{	
				//Log.i("instantiateItem postion",position + " " + adapter_list_view.get( position).toString());
				((ViewPager)container).removeView(adapter_list_view.get(position));
				((ViewPager)container).addView(adapter_list_view.get( position));
			}
			
			return   adapter_list_view.get( position);
        }
	}
	
	private OnPageChangeListener page_change_listener = new OnPageChangeListener()
	{
		@Override
		public void onPageScrollStateChanged(int status) 
		{
			// TODO Auto-generated method stub
			if( status == ViewPager.SCROLL_STATE_IDLE )
			{
				//Log.i("current page","" + view_pager.getCurrentItem());
				
				if( view_pager.getCurrentItem() == 0)
					view_pager.setCurrentItem( view_list.size() - 2, false);
				if( view_pager.getCurrentItem() == view_list.size() - 1)
					view_pager.setCurrentItem(1, false);
				
				//Log.i("current page","" + view_pager.getCurrentItem());
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) 
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) 
		{
			// TODO Auto-generated method stub
			/*
			switch(postion)
			{
				case NUMBER_OF_VIDEOS_RETURNED - 1 :
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
			*/
			
			for(int i = 0 ; i <indicator_layout.getChildCount(); i++)
				indicator_layout.getChildAt(i).setBackgroundResource(R.drawable.dot48_0);
			
			//Log.i("position","" + position);
			
			if( position == 0)
				indicator_layout.getChildAt(indicator_layout.getChildCount() -1).setBackgroundResource(R.drawable.dot48_1);
			else if( position == view_list.size() - 1)
				indicator_layout.getChildAt(0).setBackgroundResource(R.drawable.dot48_1);
			else
				indicator_layout.getChildAt(position -1).setBackgroundResource(R.drawable.dot48_1);
		}
	}; 
	
	private ViewPager view_pager;
	
	private class DoingBackgroundAsyncTask extends AsyncTask<String ,Integer ,Integer >
	{
		@Override
		protected void onPreExecute()
		{
			publishProgress(0);
			
			super.onPreExecute();
		}
		
		byte[] response_data;
		
		@SuppressLint("NewApi")
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			
			
			 
    		if ( ASaBuLuCheck.isOnline(app_activity) ) 
    		{
			
			
			YouTube youtube; 
	        YouTube.Search.List query;
	        
	        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() 
	        {            
	            @Override
	            public void initialize(HttpRequest hr) throws IOException 
	            {
	            	
	            }
	        }).setApplicationName(app_context.getString(R.string.app_name)).build();
	         
	        try
	        {
	            /*
	        	query = youtube.search().list("id,snippet");
	            query.setKey("AIzaSyAx09_GTcEyL4Se-daf-Rd7D7dRSUoKgMk");//browser key not android key    
	            query.setType("video");
	            //query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
	            query.setFields("items(id/videoId,snippet/title,snippet/description)");
	            query.setQ("TaipowerTV");
	            query.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	            
	            
	            SearchListResponse searchResponse = query.execute();
	            List<SearchResult> searchResultList = searchResponse.getItems();
	            if (searchResultList != null) 
	            { 
	                
	            	view_list = new ArrayList<View>();
	            	
	                for(SearchResult result:searchResultList)
	                {
	                    //VideoItem item = new VideoItem();
	                    Log.i("title: ",result.getSnippet().getTitle());
	                    Log.i("description: ",result.getSnippet().getDescription());
	                    //Log.i("thumb: ",result.getSnippet().getThumbnails().getDefault().getUrl());
	                    Log.i("id: ",result.getId().getVideoId());
	                    
	                    
	                    TextView title_view = new TextView(app_context);
	                    title_view.setText(result.getSnippet().getTitle());
	                    title_view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
	                    title_view.setGravity(Gravity.CENTER_VERTICAL);
	                    title_view.setTextSize( 24.0f);
	                    title_view.setTextColor(Color.WHITE);
	                    
	                    TextView description = new TextView(app_context);
	                    description.setText(result.getSnippet().getDescription());
	                    description.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
	                    description.setGravity(Gravity.CENTER_VERTICAL);
	                    description.setTextSize( 24.0f);
	                    description.setTextColor(Color.WHITE);
	                    
	                    final String youtube_id = result.getId().getVideoId();
	                    
	                    ImageButton temp_button = new ImageButton(app_context);
	                    temp_button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT ,(int) (view_width/16)*9 ));
	                    
	                    ActivityManager activityManager = (ActivityManager) app_context.getSystemService(Context.ACTIVITY_SERVICE);  
	                    int base_memory = activityManager.getMemoryClass();
	                    int large_memory = activityManager.getLargeMemoryClass();
	                    Log.i("Base Memory :" , "" + base_memory);  
	                    Log.i("Large Memory :" , "" + large_memory);
	                    
	                    String[] youtube_res = new String[]{"/mqdefault.jpg","/hqdefault.jpg","/sddefault.jpg","/maxresdefault.jpg"}; 
	                    
	                    int youtube_res_index = 0;
	                    
	                    if( base_memory < 90 )
	                    	youtube_res_index = activityManager.getMemoryClass() / 30;
	                    else
	                    	youtube_res_index = 3;
	                    
	                    Log.i("youtube_res_index Memory " , "" + youtube_res_index );
	                    
	                    URL newurl = new URL("https://i.ytimg.com/vi/"+ youtube_id + youtube_res[youtube_res_index]  ); 
	                    Bitmap bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
	                    BitmapDrawable drawable = new BitmapDrawable(null, bitmap);
	                    
	                    if( VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN )
	                    	temp_button.setBackground(drawable);
	                    else
	                    	temp_button.setBackgroundDrawable(drawable);
	                    
	                    //button0.startAnimation(button_animation);
	                    temp_button.setOnClickListener(new OnClickListener()
	                    {
	                    	@Override
	                    	public void onClick(View v) 
	                    	{
	                    		// TODO Auto-generated method stub
	                    		Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + youtube_id);
	                    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	                    		app_context.startActivity(intent);
	                    	}	
	                    });
	                	
	                    LinearLayout temp_linear_layout = new LinearLayout(app_context);
	                    temp_linear_layout.setOrientation(LinearLayout.VERTICAL);
	                    temp_linear_layout.setGravity(Gravity.CENTER_VERTICAL);
	                
	                    temp_linear_layout.addView(title_view);
	                    
	                    temp_linear_layout.addView(temp_button);
	                    
	                    
	                    temp_linear_layout.addView(description);
	                    view_list.add(temp_linear_layout);
	                }
	            }
	        	*/
	        	
	        	response_data = HttpConnectResponse.onOpenConnection("http://tv.taipower.com.tw/", "GET", null, false, false);
	        	
	        	Document document = Jsoup.parse(new String(response_data,"utf-8"));
	        	Elements javascript_elements = document.select("script[language=javascript]");
	        	Element element = javascript_elements.last();
	        	
	        	int aURL = element.toString().indexOf("var aURL = ") + 11;
	        	int aIMG = element.toString().indexOf("var aIMG = ") ;
	        	
	        	String id_string = element.toString().substring(aURL, aIMG + 1);
	        	id_string = id_string.replace("[[", "");
	        	id_string = id_string.replace("]]", "");
	        	id_string = id_string.replace("\"", "");
	        	id_string = id_string.replace("http://www.youtube.com/embed/", "");
	        	id_string = id_string.replace("?rel=0", "");
	        	id_string = id_string.replace("\n", "");
	        	id_string = id_string.replace("<br />", "");
	        	id_string = id_string.replace("\t", "");
	        	id_string = id_string.replace(" ", "");
	        	
	        	String[] youtube_id_array = id_string.split(",");

	        	//Log.i(" javascript ", "" +id_string);
	        	
	        	ActivityManager activityManager = (ActivityManager) app_context.getSystemService(Context.ACTIVITY_SERVICE);  
        		int base_memory = activityManager.getMemoryClass();
        		int large_memory = activityManager.getLargeMemoryClass();
        		//Log.i("Base Memory :" , "" + base_memory);  
        		//Log.i("Large Memory :" , "" + large_memory);
	        	
        		String[] youtube_res = new String[]{"/mqdefault.jpg","/hqdefault.jpg","/sddefault.jpg","/maxresdefault.jpg"}; 
        		int youtube_res_index = 0;
        		
        		if( base_memory < 90 )
        			youtube_res_index = activityManager.getMemoryClass() / 30;
        		else
        			youtube_res_index = 3;
        		
        		
        		view_list = new ArrayList<View>();
        		for(int i = 0; i < NUMBER_OF_VIDEOS_RETURNED + 2; i++)
        			view_list.add(new View(app_context)); 
        		
	        	for( int i = 0 ; i < NUMBER_OF_VIDEOS_RETURNED ; i++)
	        	{
	        		ImageButton button = new ImageButton(app_context);
	        		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT ,(int) (view_width/16)*9 ));
	        		
	        		//Log.i("youtube_res_index Memory " , "" + youtube_res_index );
	        		
	        		URL newurl = new URL("https://i.ytimg.com/vi/"+ youtube_id_array[i] + youtube_res[youtube_res_index]  ); 
	        		//Bitmap bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
	        		//BitmapDrawable drawable = new BitmapDrawable(null, bitmap);
	        		Drawable drawable = BitmapDrawable.createFromStream(newurl.openConnection().getInputStream(), youtube_id_array[i]);
	        		
	        		if( VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN )
	        			button.setBackground(drawable);
	        		else
	        			button.setBackgroundDrawable(drawable);
	        		
	        		//button0.startAnimation(button_animation);
	        		button.setTag(youtube_id_array[i]);
	        		button.setOnClickListener(on_click_listener);
	        		
	        		if( i == 0 )
	        		{
	        			ImageButton image_button = new ImageButton(app_context);
	        			image_button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT ,(int) (view_width/16)*9 ));
	        			if( VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN )
	        				image_button.setBackground(drawable);
		        		else
		        			image_button.setBackgroundDrawable(drawable);
	        			image_button.setTag(youtube_id_array[i]);
	        			image_button.setOnClickListener(on_click_listener);
	        			
	        			LinearLayout temp_linear_layout = new LinearLayout(app_context);
	                    temp_linear_layout.setOrientation(LinearLayout.VERTICAL);
	                    temp_linear_layout.setGravity(Gravity.CENTER_VERTICAL);
	                    temp_linear_layout.addView(image_button);
	        			
		        		view_list.set(NUMBER_OF_VIDEOS_RETURNED+1,temp_linear_layout);
	        		}
	        		
	        		if( i == NUMBER_OF_VIDEOS_RETURNED - 1 )
	        		{
	        			ImageButton image_button = new ImageButton(app_context);
	        			image_button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT ,(int) (view_width/16)*9 ));
	        			if( VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN )
	        				image_button.setBackground(drawable);
		        		else
		        			image_button.setBackgroundDrawable(drawable);
	        			image_button.setTag(youtube_id_array[i]);
	        			image_button.setOnClickListener(on_click_listener);
	        			
	        			LinearLayout temp_linear_layout = new LinearLayout(app_context);
	                    temp_linear_layout.setOrientation(LinearLayout.VERTICAL);
	                    temp_linear_layout.setGravity(Gravity.CENTER_VERTICAL);
	                    temp_linear_layout.addView(image_button);
	        			
		        		view_list.set(0,temp_linear_layout);
	        		}
                
                LinearLayout temp_linear_layout = new LinearLayout(app_context);
                temp_linear_layout.setOrientation(LinearLayout.VERTICAL);
                temp_linear_layout.setGravity(Gravity.CENTER_VERTICAL);
            
                //temp_linear_layout.addView(title_view);
                
                temp_linear_layout.addView(button);
                
                
                //temp_linear_layout.addView(description);
                view_list.set(i+1,temp_linear_layout);
                
                
                
	        	}
	        }
	        catch (MalformedURLException e) 
            {
                e.printStackTrace();
                return 0;
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
		
		Dialog process_persent_pointer;
		
		@Override
		protected void onProgressUpdate(Integer... progress) 
        {
        	String message = ""; 
			
        	if( progress[0] == 0 )
        	{
        	   	message = "資料傳送中";
        			
        	   	process_persent_pointer = CreateLoadingDialog.createLoadingDialog(app_context, message , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.CANCELABLE);
        	}
        	       	
        	super.onProgressUpdate(progress);
        }
		
		@Override
		protected void onPostExecute(Integer result) 
        {	
			CreateLoadingDialog.dialog_dismiss(process_persent_pointer);
			process_persent_pointer = null;
			
			if( result != null)
        	{
				
        	if( result == 0 )
            	Toast.makeText(app_context, "連結錯誤" , Toast.LENGTH_LONG).show();
        	if( result == 1 )
            	Toast.makeText(app_context, "錯誤" , Toast.LENGTH_LONG).show();
        	if( result == 2 )
            	Toast.makeText(app_context, "I/O資料錯誤" , Toast.LENGTH_LONG).show();
        	if( result == 3 )
            	Toast.makeText(app_context, "網路位置錯誤" , Toast.LENGTH_LONG).show();
        	if( result == 4 )
            	Toast.makeText(app_context, "網路無法連線 請檢查網際網路是否開啟" , Toast.LENGTH_LONG).show();
        	}
        	else 
        	{
			
        		DmInfor dm = new DmInfor(app_activity,app_context);
        		
        		indicator_layout = (LinearLayout) findViewById(R.id.indicator_layout);
        		
                for(int i = 0 ; i <  view_list.size() - 2; i++ )
        		{
                	View indictor = new View(app_context);
                	indictor.setLayoutParams(new LinearLayout.LayoutParams( (int)(10.0f * dm.scale) , LayoutParams.WRAP_CONTENT));
                	indictor.setClickable(false);
                	indictor.setTag(new Integer(i));
                	indictor.setBackgroundResource(R.drawable.dot48_0);
                	indicator_layout.addView(indictor,i);
        		}
        		
			MyPagerAdapter pager_adapter = new MyPagerAdapter(view_list);
	        
	        view_pager = (ViewPager) current_view.findViewById(R.id.view_pager);
	        view_pager.setAdapter(pager_adapter);
	        view_pager.setOnPageChangeListener(page_change_listener);
	        view_pager.setCurrentItem(1);
	        
	        //auto scroll use other class method
	        //FragmentRssReader other_class = new FragmentRssReader(); 
	        //other_class.viewPagerAutoScroll(view_pager ,view_list.size());
        	}
        }
		
	}
	
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Uri uri = Uri.parse("http://www.youtube.com/embed/" + v.getTag().toString() + "?rel=0" );
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			app_activity.startActivity(intent);
		}
	};
		
	
}
