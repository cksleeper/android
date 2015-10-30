package com.taipower.west_branch;

import java.util.ArrayList;
import java.util.List;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.DmInfor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



public class FragmentBarcodeAndEbppsScrollMenu extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	
	private int view_width;
	
	private ImageView left_arrow; 
	private ImageView right_arrow; 
	
	private ArrayList<ImageButton> button_list;
	
	private Animation button_animation;
	private Animation slight ;
	
	//fragment class can't use constructor
	public FragmentBarcodeAndEbppsScrollMenu()
	{
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  
    {  
		View view = inflater.inflate(R.layout.fragment_barcode_and_ebpps_scroll_menu, container, false);  
		
		
		app_context = this.getActivity();
		app_activity = this.getActivity();
		
		DmInfor screen = new DmInfor(app_activity, app_context);
        int screen_width = screen.v_width;
        int screen_height = screen.v_height;
        view_width = screen_width - 50;
        
        MarginLayoutParams button_layout_params = new MarginLayoutParams(view_width, view_width);
        button_layout_params.setMargins(25, 0, 25, 0);
        
        //Animation button_animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF, -0.5f,Animation.RELATIVE_TO_SELF ,1.0f);
        button_animation = new ScaleAnimation(1.05f, 0.95f,1.05f,0.95f,Animation.RELATIVE_TO_PARENT, 0.5f,Animation.RELATIVE_TO_PARENT ,0.5f);
        button_animation.setRepeatCount(Animation.RESTART);
        button_animation.setRepeatMode(Animation.REVERSE);
        button_animation.setDuration(750);
        
        
        int[] background_resource = new int[]{0,
  							 0,
        									  0}; 
        
        int[] id_resource = new int[]{0,0,0};
        /*
        ArrayList<View> view_list = new ArrayList<View>();
        button_list = new ArrayList<ImageButton>();
        
        
        for(int i = 0 ; i < 3;i ++)
        {
        	ImageButton temp_button = new ImageButton(app_context);
        	temp_button.setBackgroundResource(background_resource[i]);
        	temp_button.setLayoutParams(button_layout_params);
        	temp_button.setId(id_resource[i]);
        	//temp_button.startAnimation(button_animation);
        	temp_button.setOnClickListener(on_click_listener);
            
            LinearLayout temp_linearlayout = new LinearLayout(app_context);
            temp_linearlayout.setLayoutParams(new LinearLayout.LayoutParams(screen_width, screen_width));
            temp_linearlayout.setGravity(Gravity.CENTER);
            temp_linearlayout.setOrientation(LinearLayout.HORIZONTAL);
            temp_linearlayout.addView(temp_button);
            
            button_list.add(temp_button);
            view_list.add(temp_linearlayout);
            
        }
        */
        
        /*
        ImageButton button0 = new ImageButton(app_context);
        button0.setBackgroundResource(R.drawable.need_art_design);
        button0.setLayoutParams(button_layout_params);
        button0.setId(R.id.barcode_factory);
        button0.startAnimation(button_animation);
        button0.setOnClickListener(on_click_listener);
        
        LinearLayout linear_layout0 = new LinearLayout(app_context);
        linear_layout0.setGravity(Gravity.CENTER_VERTICAL);
        linear_layout0.addView(button0);
        
        ImageButton button1 = new ImageButton(app_context);
        button1.setBackgroundResource(R.drawable.ebpps_light_button_onclick);
        button1.setLayoutParams(button_layout_params);
        button1.setId(R.id.ebpps_light);
        button1.startAnimation(button_animation);
        button1.setOnClickListener(on_click_listener);
        
        LinearLayout linear_layout1 = new LinearLayout(app_context);
        linear_layout1.setGravity(Gravity.CENTER_VERTICAL);
        linear_layout1.addView(button1);
        
        ImageButton button2 = new ImageButton(app_context);
        button2.setBackgroundResource(R.drawable.barcode_created_list_button_onclick);
        button2.setLayoutParams(button_layout_params);
        button2.setId(R.id.barcode_be_created_list);
        button2.startAnimation(button_animation);
        button2.setOnClickListener(on_click_listener);
        
        LinearLayout linear_layout2 = new LinearLayout(app_context);
        linear_layout2.setGravity(Gravity.CENTER_VERTICAL);
        linear_layout2.addView(button2);
        
        
        
        
        //scroll_content.addView(button0);
        //scroll_content.addView(button1);
        //scroll_content.addView(button2);
        */
        slight = new AlphaAnimation(1.0f , 0.0f );
        slight.setRepeatCount(Animation.INFINITE);
        slight.setRepeatMode(Animation.REVERSE);
        slight.setDuration(500); //You can manage the time of the blink with this parameter
        slight.setStartOffset(0);
        
        
        left_arrow = (ImageView) view.findViewById(R.id.left_arrow);
        //left_arrow.setAnimation(slight);
        left_arrow.setVisibility(View.INVISIBLE);
        
        right_arrow = (ImageView) view.findViewById(R.id.right_arrow);
        right_arrow.setAnimation(slight);
        /*
        ArrayList<View> view_list = new ArrayList<View>();
        view_list.add(linear_layout0);
        view_list.add(linear_layout1);
        view_list.add(linear_layout2);
        */
        
        /*
        PagerAdapter pager_adapter = new MyPagerAdapter(view_list);
        
        ViewPager view_pager = (ViewPager) view.findViewById(R.id.view_pager);
        view_pager.setAdapter(pager_adapter);
        view_pager.setCurrentItem(0); //设置默认当前页
        view_pager.setOnPageChangeListener(page_change_listener);
        */
        
		return view;
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
				case 2:
					right_arrow.clearAnimation();
					right_arrow.setVisibility(View.INVISIBLE);
					break;
				case 0:
					left_arrow.clearAnimation();
					left_arrow.setVisibility(View.INVISIBLE);
					break;
				default:
					right_arrow.startAnimation(slight);
					right_arrow.setVisibility(View.VISIBLE);
					left_arrow.startAnimation(slight);
					left_arrow.setVisibility(View.VISIBLE);
					break;
			}
			
			button_list.get(postion).startAnimation(button_animation);
		}
	}; 
	
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{	
			// TODO Auto-generated method stub
			/*
			Intent intent = new Intent();
			
			boolean online_check = ASaBuLuCheck.isOnline(app_activity);
			
			//if(  v.getId() == R.id.ebpps_light && online_check )
			{
				//intent.setClass(app_context, barcode_and_ebpps_ebpps_light.class);
				//startActivity(intent);
			}
			//else if( v.getId() ==  R.id.ebpps_light )
			{
				new AlertDialog.Builder(app_context).setTitle("歐歐!!沒有連上網際網路")
				.setMessage("沒有偵測到網路環境\n請查看網路設定")
				.setNeutralButton("揪咪", null)
				.show();
			}
			
			//if(v.getId() ==  R.id.barcode_be_created_list)
			{
				intent.setClass(app_context, barcode_and_ebpps_be_created_list.class);
				startActivity(intent);
			}
		
			//if(v.getId() ==  R.id.barcode_factory)
			{
				intent.setClass(app_context, barcode_and_ebpps_factory.class);
				startActivity(intent);
			}
			
			*/
		}
		
	};
	
}
