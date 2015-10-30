package com.taipower.west_branch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.taipower.west_branch.FragmentOSBusinessId.LoadingDataAsyncTask;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.PerferenceDialog;


public class FragmentOSCancelAutoPay extends Fragment 
{
	private Context app_context ;
	private Activity app_activity;
	private FragmentOSCancelAutoPay this_class;
	
	private View current_view;
	
	private String __EVENTTARGET;
    private String __EVENTARGUMENT;
    private String __VIEWSTATE;
    private String __VIEWSTATEGENERATOR;
    private String __PREVIOUSPAGE;
    private String __EVENTVALIDATION;
	
	
	private String apply_user;
	private String tel_area_number;
	private String phone_number;
	private String email;
	private String ext_phone_number;
	private String mobile;
	
	private TextView electric_number_text_view;
	private TextView bankno_text_view;
	
	private TextView apply_user_text_view ;
	private TextView tel_area_number_text_view ;
	private TextView phone_number_text_view ;
	private TextView email_text_view ;
	private TextView ext_phone_number_text_view ;
	private TextView mobile_text_view ;
	
	private Dialog result_dialog;
	
	public FragmentOSCancelAutoPay()
	{
		
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        
    	this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		this.this_class = this;
    	
    	
		current_view = inflater.inflate(R.layout.fragment_os_cancel_auto_pay, container, false);
        
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_os_cancel_auto_pay);
		
		ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setVisibility(View.VISIBLE);
		title_bar_send_button.setBackgroundResource(R.drawable.title_bar_send_button);
		title_bar_send_button.setOnClickListener(on_click_listener);
        
        
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		String[] data = new String[] { 	apply_user_text_view.getText().toString(),
        										tel_area_number_text_view.getText().toString(),
        										phone_number_text_view.getText().toString(),
        										email_text_view.getText().toString(),
        										ext_phone_number_text_view.getText().toString(),
        										mobile_text_view.getText().toString()};
        		
        		SharedPreferences setting = app_activity.getSharedPreferences("remember", Context.MODE_PRIVATE );
        		new PerferenceDialog( app_context, app_activity, data , setting.getBoolean("show_again", true ), "online_service");
        		//cancel_auto_pay.this.finish();
        	}
        });
        
        
        electric_number_text_view = (TextView) findViewById(R.id.electric_number);
    	bankno_text_view = (TextView) findViewById(R.id.old_trfact);
        
        apply_user_text_view = (TextView) findViewById(R.id.apply_user);
		tel_area_number_text_view = (TextView) findViewById(R.id.tel_area_number);
		phone_number_text_view = (TextView) findViewById(R.id.phone_number);
		email_text_view = (TextView) findViewById(R.id.email);
		ext_phone_number_text_view = (TextView) findViewById(R.id.ext_phone_number);
		mobile_text_view = (TextView) findViewById(R.id.mobile);
        
        
		Button base_data = (Button) findViewById(R.id.base_data);
        base_data.setOnClickListener(on_click_listener);
        
        this_class.refresh();
        
        return current_view;
    }
    
    private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null )
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
    
    
    private ArrayList<Integer> error_layout_id = new ArrayList<Integer>();
    private ArrayList<Integer> error_mark_id = new ArrayList<Integer>();
	
    
    private OnClickListener on_click_listener = new OnClickListener()
    {
    	@Override
    	public void onClick(View v) 
    	{
    		// TODO Auto-generated method stub
    		if( v.getId() == R.id.base_data )
    		{
    			SharedPreferences setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
        		apply_user_text_view.setText(setting.getString("apply_user",""));
        		tel_area_number_text_view.setText(setting.getString("tel_area_number",""));	   
        		phone_number_text_view.setText(setting.getString("phone_number",""));	   
        		email_text_view.setText(setting.getString("email",""));	   
        		ext_phone_number_text_view.setText(setting.getString("ext_phone_number",""));	   
        		mobile_text_view.setText(setting.getString("mobile",""));	   
    		}
    		
    		if( v.getId() == R.id.title_bar_menu_button)
    		{
    			String electric_number = electric_number_text_view.getText().toString();
        		String bankno = bankno_text_view.getText().toString();
        		
        		apply_user = apply_user_text_view.getText().toString();
        		tel_area_number = tel_area_number_text_view.getText().toString();
        		phone_number = phone_number_text_view.getText().toString();
        		email = email_text_view.getText().toString();
        		ext_phone_number = ext_phone_number_text_view.getText().toString();
        		mobile = mobile_text_view.getText().toString();
        		
        		
        		//View this_view = app_activity.findViewById(android.R.id.content).getRootView();
    			
    			if(error_layout_id.size() > 0)
    				ASaBuLuCheck.setLayoutBackgroundState(current_view, error_layout_id, error_mark_id, false);
    			
    			error_layout_id.clear();
    			error_mark_id.clear();
        		
        		String error_message = "";
        		
        		if( !ASaBuLuCheck.electricCheckFunction(electric_number) )
        		{	
        			error_message += "電號錯誤\n";
        			
        			error_layout_id.add(Integer.valueOf(R.id.electric_number_layout));
        			error_mark_id.add(Integer.valueOf(R.id.electric_number_error_mark));
        		}
        		
        		if( bankno.isEmpty() )
        		{	
        			error_message += "代繳帳號未填\n";
        			
        			error_layout_id.add(Integer.valueOf(R.id.old_trfact_layout));
        			error_mark_id.add(Integer.valueOf(R.id.old_trfact_error_mark));
        		}
        		
        		
        		if( apply_user.isEmpty() )
        		{	
        			error_message += "申請人未填\n";
        			
        			error_layout_id.add(Integer.valueOf(R.id.apply_user_layout));
        			error_mark_id.add(Integer.valueOf(R.id.apply_user_error_mark));
        		}
        		
        		if( (tel_area_number.length() < 2 || phone_number.length() < 6) &&  mobile.length() < 8)
		        {	
		        	error_message += "電話或手機號碼錯誤\n" ;
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.tel_area_number_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.tel_area_number_error_mark));
		        	error_layout_id.add(Integer.valueOf(R.id.phone_number_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.phone_number_error_mark));
		        	error_layout_id.add(Integer.valueOf(R.id.mobile_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.mobile_error_mark));
		        }
        		
        		if( !email.contains("@") )
        		{	
        			error_message += "email格式錯誤\n";
        			
        			error_layout_id.add(Integer.valueOf(R.id.email_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.email_error_mark));
        		}
        		
        		
        		if( !error_message.equals("") )
        		{
        			ASaBuLuCheck.setLayoutBackgroundState(current_view, error_layout_id, error_mark_id, true);
        			
        			
        			AlertDialog.Builder warning_dialog = new AlertDialog.Builder(app_context);
        			warning_dialog.setTitle("資料錯誤");
        			warning_dialog.setMessage(error_message);
        			warning_dialog.setNegativeButton("重新填寫", null);
        			warning_dialog.show();
        			
        			error_message = "";
        		}
        		else
        		{
        			        		       		
        			String paraments = "";
					
        			try 
					{
        				paraments = "__EVENTTARGET=" + __EVENTTARGET + "&" +
    								"__EVENTARGUMENT=" + __EVENTARGUMENT + "&" +
    								"__VIEWSTATE=" + __VIEWSTATE + "&" +
    								"__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR + "&" +
    								"__PREVIOUSPAGE=" + __PREVIOUSPAGE + "&" +
    								"__EVENTVALIDATION=" + __EVENTVALIDATION + "&" +
    																
									"custno=" + electric_number + "&" +
									"bankno=" + bankno + "&" +
									"applyname=" + URLEncoder.encode(apply_user,"UTF-8") + "&" +
									"email=" + email + "&" +
									"tel1=" + tel_area_number + "&" +
									"tel2=" + phone_number + "&" +
									"tel3=" + ext_phone_number + "&" +
									"mobile=" + mobile + "&" +
									"Button1=" + URLEncoder.encode("確定送出", "UTF-8");     		
    								
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String url = "http://wapp.taipower.com.tw/newnas/nawp011.aspx";
        			new LoadingDataAsyncTask().execute("check",url,paraments);
        		
        		}
    			
    		}
    		
    	}
    };
    
    
	class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> 
    {
    	protected void onPreExecute ()
    	{
    		super.onPreExecute();
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    		publishProgress(0);
    	}
		
    	String session_tag;
    	byte[] response_data;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			
			//set tag
			session_tag = params[0];
			
			int return_value = 0;
			
			try 
			{
				if( session_tag.equals("refresh") )
					response_data = HttpConnectResponse.onOpenConnection(params[1], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_REDIRECT);
				else
					response_data = HttpConnectResponse.onOpenConnection(params[1], "POST", params[2], HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_REDIRECT);
			
				return_value = 0;
			} 
			catch (URISyntaxException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return_value = 4;
			}
			catch (IOException e)
            {
            	//Log.e("err",e.getMessage().toString());
            	e.printStackTrace();
            	return_value = 5;
            }
            catch (Exception e)
            {
            	//Log.e("err",e.getMessage().toString());
            	e.printStackTrace();
            	return_value = 6;
            }  
			return return_value;
		}
		
		
		//Dialog process_persent = null;
		Dialog process_persent_pointr = null;
		
		@Override
		protected void onProgressUpdate(Integer... progress) 
        {
        	String message = ""; 
			
        	if( progress[0] == 0 )
        	{
        	   	message = "資料傳送中";
        			
        		process_persent_pointr = CreateLoadingDialog.createLoadingDialog(app_context, message , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.CANCELABLE);	
        	}
        	       	
        	super.onProgressUpdate(progress);
        }
		
		@Override
        protected void onPostExecute(Integer result) 
        {
			
			String content = null;
			if( result == 0 )
			{
				
			try 
			{
				content = new String( response_data,"utf-8") ;
			} 
			catch (UnsupportedEncodingException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Document document = Jsoup.parse(content);
		
			__EVENTTARGET = (document.getElementById("__EVENTTARGET") != null)?document.getElementById("__EVENTTARGET").val():"";
			__EVENTARGUMENT = (document.getElementById("__EVENTARGUMENT") != null)?document.getElementById("__EVENTARGUMENT").val():"";
			__VIEWSTATE = (document.getElementById("__VIEWSTATE") != null)?document.getElementById("__VIEWSTATE").val():"";
			__VIEWSTATEGENERATOR = (document.getElementById("__VIEWSTATEGENERATOR") != null)?document.getElementById("__VIEWSTATEGENERATOR").val():"";
			__PREVIOUSPAGE = (document.getElementById("__PREVIOUSPAGE") != null)?document.getElementById("__PREVIOUSPAGE").val():"";
			__EVENTVALIDATION = (document.getElementById("__EVENTVALIDATION") != null)?document.getElementById("__EVENTVALIDATION").val():"";
				
			Elements head_elements = document.select("td[class=head]");
			Elements span_elements = document.select("span");
			Element ok_element = document.getElementById("newspaper");
			
			String[] head_array = new String[head_elements.size()];
			String[] span_array = new String[span_elements.size()];
			
			String result_view_title = "";
			String result_view_content = "";
			
			if( !session_tag.equals("refresh") )
			{
				
			if( ok_element == null  )
			{
				content = content.replace("<br><a href='javascript:history.back()'>按此回上一頁</a>", "");
				
				result_view_title = "資料錯誤";
				result_view_content = content;
				
				session_tag = "error";
			}	
			else if( head_elements.size() > 0 && span_elements.size() > 0 )
			{
				for(int i = 0 ; i < head_elements.size() ; i++)
				{
					String temp_head = head_elements.get(i).text();
					temp_head = temp_head.replace("\t","");
					temp_head = temp_head.replace("\r","");
					temp_head = temp_head.replace("\n","");
					temp_head = temp_head.replace(" ","");
					temp_head = temp_head.replace("　","");
					head_array[i] = temp_head;
					temp_head = null;
	    		
					String temp_span = span_elements.get(i).text();
					temp_span = temp_span.replace("\t","");
					temp_span = temp_span.replace("\r","");
					temp_span = temp_span.replace("\n","");
					temp_span = temp_span.replace(" ","");
					temp_span = temp_span.replace("　","");
					
					temp_span = temp_span.replace(email, " "+ email + " \n");
					temp_span = temp_span.replace(mobile, " "+ mobile);
					
					span_array[i] = temp_span;
					temp_span = null;
				}	
				
				session_tag = "check";
			}	
			else
			{
				result_view_title = "您所申辦完成受理！！";
				result_view_content = ok_element.text();
				
				session_tag = "send";
			}
			
        	}
			
			View dialog_view = null;
			
			if( session_tag.equals("check")  )
			{
				View progress_result_view = (LayoutInflater.from(app_context)).inflate(R.layout.fragment_progress_result , null, false);
				
				((TextView) progress_result_view.findViewById(R.id.progeess_result_title)).setText("申請資料如下");
				
				int[] head_id_array = new int[]{R.id.head0,R.id.head1,R.id.head2,R.id.head3,R.id.head4,R.id.head5,
												R.id.head6,R.id.head7,R.id.head8,R.id.head9,R.id.head10};
					
				int[] colon_id_array = new int[]{R.id.colon0,R.id.colon1,R.id.colon2,R.id.colon3,R.id.colon4,R.id.colon5,
												 R.id.colon6,R.id.colon7,R.id.colon8,R.id.colon9,R.id.colon10};
					
				int[] result_id_array = new int[]{R.id.result0,R.id.result1,R.id.result2,R.id.result3,R.id.result4,R.id.result5,
												  R.id.result6,R.id.result7,R.id.result8,R.id.result9,R.id.result10};
					
				for( int i = 0 ; i < head_array.length ; i++ )
				{
					((TextView) progress_result_view.findViewById(head_id_array[i])).setText(head_array[i]);
					((TextView) progress_result_view.findViewById(colon_id_array[i])).setText("：");
					((TextView) progress_result_view.findViewById(result_id_array[i])).setText(span_array[i]);
				}
					
				DmInfor dm = new DmInfor(app_activity, app_context);
					
				int layout_height = (int) ((( (float) (head_array.length + 1) * 45.0f) + 1.0f) * dm.scale);
					
				((LinearLayout) progress_result_view.findViewById(R.id.progress_result_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, layout_height));
				//((LinearLayout) progress_result_view.findViewById(R.id.progress_result_layout)).setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, layout_height));
				
				
				Button left_button = (Button) progress_result_view.findViewById(R.id.left_button);
				left_button.setText("確認申請");
				left_button.setTag(session_tag);
				left_button.setOnClickListener(send_on_click_listener);
					
				((Button) progress_result_view.findViewById(R.id.center_button)).setVisibility(View.INVISIBLE);
					
				Button right_button = (Button) progress_result_view.findViewById(R.id.right_button);
				right_button.setText("放棄申請");
				right_button.setTag("cancel");
				right_button.setOnClickListener(send_on_click_listener);
			
				dialog_view = progress_result_view;
			}
			
			if( session_tag.equals("send") || session_tag.equals("error") )
			{
				View os_result_view = (LayoutInflater.from(app_context)).inflate(R.layout.fragment_os_result_view , null, false);
				
				((TextView) os_result_view.findViewById(R.id.os_result_view_result_title)).setText(result_view_title);
				((TextView) os_result_view.findViewById(R.id.os_result_view_result_content)).setText(result_view_content);
				
				Button center_button = (Button) os_result_view.findViewById(R.id.center_button);
				center_button.setText("確認");
				center_button.setTag("done");
				center_button.setOnClickListener(send_on_click_listener);
			
				dialog_view = os_result_view;
			}
		
			if( !session_tag.equals("refresh") )
			{
				result_dialog = new Dialog(app_context);
				result_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				result_dialog.setContentView(dialog_view);
				result_dialog.show();
			}
			
			}
			
			CreateLoadingDialog.dialog_dismiss(process_persent_pointr);
			process_persent_pointr = null;
			
			if( result != 0)
			{	
				String error_message = "網路錯誤！！請檢查網際網路是否開啟\n或請稍候再試";
				
				new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage(error_message).setNeutralButton("確定", null).show();
			}
        }
    }
    
		private OnClickListener send_on_click_listener = new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if( v.getTag().equals("check") )
				{
					String paraments = "";
	    			
	    			try 
	    			{		
	    				paraments = "__EVENTTARGET=" + __EVENTTARGET + "&" +
	    							"__EVENTARGUMENT=" + __EVENTARGUMENT + "&" +
	    							"__VIEWSTATE=" + __VIEWSTATE + "&" +
	    							"__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR + "&" +
	    							"__PREVIOUSPAGE=" + __PREVIOUSPAGE + "&" +
	    							"__EVENTVALIDATION=" + __EVENTVALIDATION + "&" +
	    							
	    							"Button1=" + URLEncoder.encode("確定送出", "UTF-8");     				
					} 
	    			catch (UnsupportedEncodingException e) 
	    			{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	   
	    			        	        
	    	        String url = "http://wapp.taipower.com.tw/newnas/nawp012.aspx";
	    	        new LoadingDataAsyncTask().execute( "send", url, paraments);
	    	        
	    	        result_dialog.dismiss();
				}
				else
				{
					this_class.refresh();
				}	
			}
		};
	    
	        
	    private void refresh()
	    {
	    	String url = "http://wapp.taipower.com.tw/newnas/nawp010.aspx";
	    	new LoadingDataAsyncTask().execute("refresh",url);
	    	
	    	electric_number_text_view.setText("");
    		bankno_text_view.setText("");
	    	
	    	//apply_user_text_view.setText("");
	    	//tel_area_number_text_view.setText("");
	    	//phone_number_text_view.setText("");
	    	//email_text_view.setText("");
	    	//ext_phone_number_text_view.setText("");
	    	//mobile_text_view.setText("");
	    	
	    	if( result_dialog != null )
	    		result_dialog.dismiss();
	    }

}