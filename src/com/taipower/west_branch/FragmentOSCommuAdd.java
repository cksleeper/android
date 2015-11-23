package com.taipower.west_branch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.PerferenceDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentOSCommuAdd extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private FragmentOSCommuAdd this_class;
	
	private View current_view;
	
	private Resources resource;
	
	private String[] city_array;
	private String[] distr_array;
	private String[] zip_array;
 	
	private int city_selected_index;
	private int distr_selected_index;
	
	private HashMap<String,Integer> street_hash_map;
	
	private String __EVENTTARGET;
    private String __EVENTARGUMENT;
    private String __VIEWSTATE;
    private String __VIEWSTATEGENERATOR;
    private String __PREVIOUSPAGE;
    private String __EVENTVALIDATION;
	
	
	private String rb_type = "RadioButton1";
	private Spinner spinner_distr;
	
	
	private TextView electric_number_text_view ;
	private TextView user_name_text_view;
	private AutoCompleteTextView new_address_text_view;
	
	private TextView apply_user_text_view;
	private TextView tel_area_number_text_view ;
	private TextView phone_number_text_view ;
	private TextView email_text_view ;
	private TextView ext_phone_number_text_view ;
	private TextView mobile_text_view ;
	
	private String email;
	private String mobile;
	
	private Dialog result_dialog;
	
	public FragmentOSCommuAdd()
	{
		
	}
	
	@Override
    public View onCreateView( LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) 
    {
		this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		this.this_class = this;
		
		current_view = inflater.inflate(R.layout.fragment_os_commu_addr, root, false);
        
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_os_commu_addr);
		
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
        		
    	    	SharedPreferences setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
    		    new PerferenceDialog( app_context, app_activity, data , setting.getBoolean("show_again", true ) ,"online_service");
        				//commu_addr.this.finish();
        	}
        });
        
        
        
        Spinner spinner_action = (Spinner) current_view.findViewById(R.id.spinner_action);
        
        ArrayAdapter adapter_action = new ArrayAdapter(app_context,android.R.layout.simple_spinner_item,new String[]{"建立","變更","取消"});
        adapter_action.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinner_action.setAdapter(adapter_action);
        spinner_action.setOnItemSelectedListener(on_item_selected_listener);
        
        resource = app_context.getResources(); 
        
        city_array = resource.getStringArray(R.array.online_service_city);
        
        
        Spinner spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_distr = (Spinner) findViewById(R.id.spinner_distr);
        
        ArrayAdapter adapter_city = new ArrayAdapter(app_context,android.R.layout.simple_spinner_item, city_array);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(adapter_city);
        
        spinner_city.setOnItemSelectedListener(on_item_selected_listener);
        spinner_distr.setOnItemSelectedListener(on_item_selected_listener);
        
        String[] street_map = resource.getStringArray(R.array.street_map);
        
        street_hash_map = new HashMap<String,Integer>();
        
        for(int i = 0 ; i < street_map.length ;i++)
        	street_hash_map.put(street_map[i], Integer.valueOf(R.array.street100 + i));
        
        zip_array = resource.getStringArray(R.array.online_service_zip00 + city_selected_index);
        
        String[] select_filter = resource.getStringArray(street_hash_map.get(zip_array[distr_selected_index]).intValue());
        
        ArrayAdapter<String> select_filter_adapter = new ArrayAdapter<String>(app_context, android.R.layout.simple_dropdown_item_1line,select_filter);
        
        new_address_text_view = (AutoCompleteTextView) current_view.findViewById(R.id.new_address);
        new_address_text_view.setAdapter(select_filter_adapter);
        new_address_text_view.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		if( ((TextView) v).getText().toString().startsWith("請填寫") )
        			((TextView) v).setText("");
        	}
        });
        
        electric_number_text_view = (TextView) findViewById(R.id.electric_number);
		user_name_text_view = (TextView) findViewById(R.id.user_name);
		
        
        apply_user_text_view = (TextView) findViewById(R.id.apply_user);
		tel_area_number_text_view = (TextView) findViewById(R.id.tel_area_number);
		phone_number_text_view = (TextView) findViewById(R.id.phone_number);
		email_text_view = (TextView) findViewById(R.id.email);
		ext_phone_number_text_view = (TextView) findViewById(R.id.ext_phone_number);
		mobile_text_view = (TextView) findViewById(R.id.mobile);
        
        
		Button base_data = (Button) findViewById(R.id.base_data);
        base_data.setOnClickListener( on_click_listener);
        
        this_class.refresh();
        
        return current_view;
    }
    
	private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null)
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
			
			if( v.getId() == R.id.base_data)
			{
				SharedPreferences setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
        		apply_user_text_view.setText(setting.getString("apply_user",""));
        		tel_area_number_text_view.setText(setting.getString("tel_area_number",""));	   
        		phone_number_text_view.setText(setting.getString("phone_number",""));	   
        		email_text_view.setText(setting.getString("email",""));	   
        		ext_phone_number_text_view.setText(setting.getString("ext_phone_number",""));	   
        		mobile_text_view.setText(setting.getString("mobile",""));	   
			}
			
			
			if( v.getId() == R.id.title_bar_menu_button )
			{
				String electric_number = electric_number_text_view.getText().toString();
        		String user_name = user_name_text_view.getText().toString();
        		String new_address = new_address_text_view.getText().toString();
        		
        		String apply_user = apply_user_text_view.getText().toString(); 
        		String tel_area_number = tel_area_number_text_view.getText().toString(); 
        		String phone_number = phone_number_text_view.getText().toString(); 
        		email = email_text_view.getText().toString(); 
        		String ext_phone_number = ext_phone_number_text_view.getText().toString(); 
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
        		
        		if( user_name.isEmpty() )
        		{	
        			error_message += "用電戶名未填\n";
        			
        			error_layout_id.add(Integer.valueOf(R.id.user_name_layout));
        			error_mark_id.add(Integer.valueOf(R.id.user_name_error_mark));
        		}
        		
        		if( (new_address.isEmpty() && !rb_type.equals("RadioButton3")) || new_address.startsWith("請填寫") )
        		{	
        			error_message += "郵寄地址未填\n";
        			
        			error_layout_id.add(Integer.valueOf(R.id.used_address_layout));
        			error_mark_id.add(Integer.valueOf(R.id.used_address_error_mark));
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
        			
        		}
        		else
        		{
        			//String full_type_address = ASaBuLuCheck.convertNumberType(new_address, ASaBuLuCheck.FULL_TYPE);
        		
        			String paraments = "";
        			
        			try 
        			{		
        				paraments = "__EVENTTARGET=" + __EVENTTARGET + "&" +
        							"__EVENTARGUMENT=" + __EVENTARGUMENT + "&" +
        							"__VIEWSTATE=" + __VIEWSTATE + "&" +
        							"__VIEWSTATEGENERATOR=" + __VIEWSTATEGENERATOR + "&" +
        							"__PREVIOUSPAGE=" + __PREVIOUSPAGE + "&" +
        							"__EVENTVALIDATION=" + __EVENTVALIDATION + "&" +
        							
        							"rb_type=" + rb_type + "&" +
        							"custno=" + electric_number + "&" +
        							"custname=" + URLEncoder.encode(user_name,"UTF-8") + "&" +
        							"ddl_addr1=" + URLEncoder.encode(city_array[city_selected_index],"UTF-8") + "&" +
        							"ddl_addr2=" + URLEncoder.encode(distr_array[distr_selected_index],"UTF-8") + "&" +
        							"zip=" + zip_array[distr_selected_index] + "&" +
        							"addr3=" + URLEncoder.encode(new_address,"UTF-8") + "&" +
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
        			        	        
        	        String url = "http://wapp.taipower.com.tw/newnas/nawp021.aspx";
        			new LoadingDataAsyncTask().execute( "check", url, paraments);
        		}	
			}	
		}
	};
	
	
    private OnItemSelectedListener on_item_selected_listener = new OnItemSelectedListener()
    {
    	@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
		{
			// TODO Auto-generated method stub
			if( parent.getId() == R.id.spinner_action)
			{
				switch( (int) id)
				{
					case 0:
						rb_type = "RadioButton1";
						findViewById(R.id.spinner_city).setEnabled(true);
						findViewById(R.id.spinner_distr).setEnabled(true);
						findViewById(R.id.new_address).setEnabled(true);
						break;
					case 1:
						rb_type = "RadioButton2";
						findViewById(R.id.spinner_city).setEnabled(true);
						findViewById(R.id.spinner_distr).setEnabled(true);
						findViewById(R.id.new_address).setEnabled(true);
						break;
					case 2:
						rb_type = "RadioButton3";
						findViewById(R.id.spinner_city).setEnabled(false);
						findViewById(R.id.spinner_distr).setEnabled(false);
						findViewById(R.id.new_address).setEnabled(false);
						break;
				}
			}
    		
			
    		if( parent.getId() == R.id.spinner_city )
			{
				city_selected_index = (int ) id;
				
				distr_array = resource.getStringArray(R.array.online_service_distr00 + city_selected_index);
			    zip_array = resource.getStringArray(R.array.online_service_zip00 + city_selected_index);
				
				ArrayAdapter adapter_distr = new ArrayAdapter(app_context,android.R.layout.simple_spinner_item, distr_array);
				adapter_distr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        spinner_distr.setAdapter(adapter_distr);
			
			}
			
			if( parent.getId() == R.id.spinner_distr )
			{
				distr_selected_index = (int) id;
				
				String[] select_filter = resource.getStringArray(street_hash_map.get(zip_array[distr_selected_index]).intValue());
		        
		        ArrayAdapter<String> select_filter_adapter = new ArrayAdapter<String>(app_context, android.R.layout.simple_dropdown_item_1line,select_filter);
		        
		        new_address_text_view.setAdapter(select_filter_adapter);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// TODO Auto-generated method stub
			Toast.makeText(app_context, "沒有選到東西" , Toast.LENGTH_SHORT );
		}
    };
    
    
	class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> 
    {
    	@Override
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
					response_data = HttpConnectResponse.onOpenConnection(params[1], "POST", new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_REDIRECT);
			
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
		CreateLoadingDialog process_persent = new CreateLoadingDialog();
		
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
    			        	        
    	        String url = "http://wapp.taipower.com.tw/newnas/nawp022.aspx";
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
    	String url = "http://wapp.taipower.com.tw/newnas/nawp020.aspx";
    	new LoadingDataAsyncTask().execute("refresh",url);
    	
    	electric_number_text_view.setText("");
		user_name_text_view.setText("");
		new_address_text_view.setText("");
    	
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