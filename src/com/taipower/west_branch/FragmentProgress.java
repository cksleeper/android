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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class FragmentProgress extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private FragmentProgress this_class;
	
	private View current_view;
	
	private String __EVENTTARGET;
    private String __EVENTARGUMENT;
    private String __VIEWSTATE;
    private String __VIEWSTATEGENERATOR;
    private String __PREVIOUSPAGE;
    private String __EVENTVALIDATION;
	
	private String rb_select; 
	
	private TextView apply_number0_text_view;
	private TextView apply_number1_text_view;
	private TextView user_name_text_view;
	
	private Dialog result_dialog;
	
	private View progress_cancel_view;
	
	public FragmentProgress()
    {
    	
    }
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
		this.app_context = this.getActivity();
    	this.app_activity = this.getActivity();
    	this.this_class = this;
    	
    	current_view = inflater.inflate(R.layout.fragment_progress, container ,false);  
	
    	ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
    	title_bar_send_button.setVisibility(View.VISIBLE);
    	title_bar_send_button.setBackgroundResource(R.drawable.title_bar_send_button);
    	title_bar_send_button.setOnClickListener(on_click_listener);
    	
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		InputMethodManager imm = (InputMethodManager) app_context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(current_view.getWindowToken(), 0);
        		
        		Bundle bundle = new Bundle();
        		bundle.putString("second_layer_content", "progress_state");
        		
        		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        		Fragment fragment = new FragmentSecondLayerMenu();
        		fragment.setArguments(bundle);
        		ft.replace(R.id.fragment_content, fragment,"second_layer").commit();
        		
        		//finish();
        	}
        });
        
        
        Spinner spinner_action = (Spinner) findViewById(R.id.spinner_action);
        
        ArrayAdapter adapter_action = new ArrayAdapter(app_context,android.R.layout.simple_spinner_item,new String[]{"一般申請案件","網路申請案件"});
        adapter_action.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_action.setAdapter(adapter_action);
        spinner_action.setOnItemSelectedListener(on_item_selected_listener);
        rb_select = "rb_cps";     
        
        apply_number0_text_view = (TextView) findViewById(R.id.apply_number0);
        apply_number1_text_view = (TextView) findViewById(R.id.apply_number1);
        user_name_text_view = (TextView) findViewById(R.id.user_name);
        
        
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
			String error_message = "";
    		
    		String user_name = user_name_text_view.getText().toString();
    		String apply_number0 = apply_number0_text_view.getText().toString();
    		String apply_number1 = apply_number1_text_view.getText().toString();
    		
    		//View current_view = app_activity.findViewById(android.R.id.content);
    		
    		if( error_layout_id.size() > 0)
    			ASaBuLuCheck.setLayoutBackgroundState(current_view, error_layout_id, error_mark_id, false);
    		
    		error_layout_id.clear();
    		error_mark_id.clear();
    		        		
    		if( user_name.isEmpty() )
    		{	
    			error_message += "用電戶名未填\n";
    			
    			error_layout_id.add(Integer.valueOf(R.id.user_name_layout));
    			error_mark_id.add(Integer.valueOf(R.id.user_name_error_mark));
    		}
    		
    		if( apply_number0.length() < 2 && rb_select.equals("C") )
    		{	
    			error_message += "受理號碼前二碼錯誤\n";
    			
    			error_layout_id.add(Integer.valueOf(R.id.apply_number0_layout));
    			error_mark_id.add(Integer.valueOf(R.id.apply_number0_error_mark));
    		}
    		
    		if( apply_number1.length() < 8 )
    		{	
    			error_message += "受理號碼末八碼錯誤\n";
    			
    			error_layout_id.add(Integer.valueOf(R.id.apply_number1_layout));
    			error_mark_id.add(Integer.valueOf(R.id.apply_number1_error_mark));
    		}
    		
    		user_name = ASaBuLuCheck.convertNumberType(user_name, ASaBuLuCheck.FULL_TYPE);
    		
    		
    		if( error_message.isEmpty() )
    		{
    			String dist = "";
    			String cpsno = "";
    			String nas = "";
    			String nasno = "";

    	        if( rb_select.equals("rb_cps") )
    	        {
    	            dist = apply_number0;
    	            cpsno = apply_number1;
    	        }
    	        else
    	        {
    	            nas = apply_number0;
    	            nasno = apply_number1;
    	        }
    			
    			//String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawpdc1.asp";  //This page character is big5
    			String url = "http://wapp.taipower.com.tw/newnas/nawp091.aspx";
    			
    			String paraments = null;
    			
    			try
    			{
    			paraments = "__EVENTTARGET=Button2&" +
    						"__EVENTARGUMENT=" + URLEncoder.encode(__EVENTARGUMENT,"UTF-8") + "&" +
    						"__VIEWSTATE=" + URLEncoder.encode(__VIEWSTATE,"UTF-8") + "&" +
    						"__VIEWSTATEGENERATOR=" +URLEncoder.encode( __VIEWSTATEGENERATOR,"UTF-8") + "&" +
    						"__EVENTVALIDATION=" + URLEncoder.encode(__EVENTVALIDATION,"UTF-8") + "&" +
    						"custname=" + URLEncoder.encode(user_name, "UTF-8")  + "&" +
    						"dist=" + dist + "&" +
    						"cpsno=" + cpsno + "&" + 
    						"rb_select=" + rb_select + "&" +
    						"nas=" + nas + "&" +
    						"nasno=" + nasno;
    			
    			//Log.i("paraments",paraments);
    			
    			}
    			catch (UnsupportedEncodingException e)
    			{
    				e.printStackTrace();
    			}
    			
    			new LoadingDataAsyncTask().execute("send", url, paraments);
    			
    		}
    		else
    		{
    			ASaBuLuCheck.setLayoutBackgroundState(current_view, error_layout_id, error_mark_id, true);
    			
    			AlertDialog.Builder error_warning = new AlertDialog.Builder(app_context);
    			error_warning.setTitle("錯誤");
    			error_warning.setMessage(error_message);
    			error_warning.setNeutralButton("重新填寫", null);
    			error_warning.show();
    		}
		}
	};
	
	private HttpConnectResponse connection;
	
	private class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> 
	{
		protected void onPreExecute ()
    	{
    		super.onPreExecute();
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    		publishProgress(0);
    	}
		
		private String session_tag ;
		
		private String[] result_array ;
		private String result_text ;
		private byte[] response_data; 
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
    		if ( ASaBuLuCheck.isOnline(app_activity))			
    		{
    			session_tag = params[0];
    			
    			if( connection == null )
    				connection = new HttpConnectResponse();
    				
    			try 
				{
    				if( session_tag.equals("refresh") )
    				{	
    					connection.setUrl(params[1]);
    					connection.setConnectMethod("GET", null);
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data = connection.startConnectAndResponseByteArray();
    					
    					//response_data = HttpConnectResponse.onOpenConnection(params[1], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT);
    				}
    				else
    				{	
    					connection.setUrl(params[1]);
    					connection.setConnectMethod("POST", new String[]{params[2]});
    					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
    					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
    					response_data = connection.startConnectAndResponseByteArray();
    					
    					//response_data = HttpConnectResponse.onOpenConnection(params[1], "POST", new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT);
    				}
    				
					if( response_data == null)
						return 9;
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
    		}
    		else
    			return 4;
    		
			return null;
		}
		
		//Dialog process_persent = null;
		Dialog process_persent_pointr = null;
		
		@Override
		protected void onProgressUpdate(Integer... progress) 
		{
			super.onProgressUpdate(progress);
			
			if( progress[0] == 0 )
				process_persent_pointr = CreateLoadingDialog.createLoadingDialog(app_context, "資料傳送中" , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.CANCELABLE);
		}
				
		@Override
		protected void onPostExecute(Integer result) 
		{
			if( result == null )
			{
				String content = null;
				
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
			    
				Elements button1_element = document.select("input[name=Button1]");
				
				if( session_tag.equals("send")  )
				{
					if(ok_element != null)
					{
						String[] head_array = new String[head_elements.size()];
						String[] span_array = new String[span_elements.size()];
				
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
							span_array[i] = temp_span;
							temp_span = null;
						}	
						
						View progress_result_view = (LayoutInflater.from(app_context)).inflate(R.layout.fragment_progress_result , null, false);
						
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
						
						int layout_height = (int) ((((float)(head_array.length + 1 ) * 45.0f) + 1.0f) * dm.scale);
						
						//((LinearLayout) progress_result_view.findViewById(R.id.progress_result_layout)).setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, layout_height));
						((LinearLayout) progress_result_view.findViewById(R.id.progress_result_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, layout_height));
						
						
						if (button1_element.size() > 0)
						{
							Button left_button = (Button) progress_result_view.findViewById(R.id.left_button);
							left_button.setText("取消申請案件");
							left_button.setTag("cancel");
							left_button.setOnClickListener(on_clicker_listener);
							
							((Button) progress_result_view.findViewById(R.id.center_button)).setVisibility(View.INVISIBLE);
							
							Button right_button = (Button) progress_result_view.findViewById(R.id.right_button);
							right_button.setText("維持申請案件");
							right_button.setTag("done");
							right_button.setOnClickListener(on_clicker_listener);
						}
						else
						{
							((Button) progress_result_view.findViewById(R.id.left_button)).setVisibility(View.INVISIBLE);
							
							Button center_button = (Button) progress_result_view.findViewById(R.id.center_button);
							center_button.setText("確定");
							center_button.setTag("done");
							center_button.setOnClickListener(on_clicker_listener);
							
							((Button) progress_result_view.findViewById(R.id.right_button)).setVisibility(View.INVISIBLE);
						}
						
						result_dialog = new Dialog(app_context);
						result_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						result_dialog.setContentView(progress_result_view);
						result_dialog.show();
					}
					else
					{	
						content = content.replace("<br><a href='javascript:history.back()'>按此回上一頁</a>", "");
						
						AlertDialog.Builder error_dialog = new AlertDialog.Builder(app_context);
						error_dialog.setTitle("歐！歐歐！！");
						error_dialog.setMessage(content);
						error_dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								// TODO Auto-generated method stub
								this_class.refresh();
								dialog.dismiss();
							}
						});
						error_dialog.show();
					}	    		
			    }
				
				if(session_tag.equals("cancel"))
				{	
					progress_cancel_view = (LayoutInflater.from(app_context)).inflate(R.layout.fragment_progress_cancel , null, false);
					
					String temp_head = head_elements.get(0).text();
					temp_head = temp_head.replace("\t","");
					temp_head = temp_head.replace("\r","");
					temp_head = temp_head.replace("\n","");
					temp_head = temp_head.replace(" ","");
					temp_head = temp_head.replace("　","");
					
					String temp_span = span_elements.get(0).text();
					temp_span = temp_span.replace("\t","");
					temp_span = temp_span.replace("\r","");
					temp_span = temp_span.replace("\n","");
					temp_span = temp_span.replace(" ","");
					temp_span = temp_span.replace("　","");
					
					((TextView) progress_cancel_view.findViewById(R.id.head0)).setText(temp_head);
					((TextView) progress_cancel_view.findViewById(R.id.result0)).setText(temp_span);
					
					Button left_button = (Button) progress_cancel_view.findViewById(R.id.left_button);
					left_button.setText("確定取消");
					left_button.setTag("cancel_action");
					left_button.setOnClickListener(on_clicker_listener);
					
					Button right_button = (Button) progress_cancel_view.findViewById(R.id.right_button);
					right_button.setText("離開");
					right_button.setTag("done");
					right_button.setOnClickListener(on_clicker_listener);
					
					result_dialog = new Dialog(app_context);
					result_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					result_dialog.setContentView(progress_cancel_view);
					result_dialog.show();
					
				}
				
				if(session_tag.equals("cancel_done"))
				{
					String temp_span = span_elements.get(0).text();
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(app_context);
					dialog.setTitle("歐！歐歐！！");
					dialog.setMessage(temp_span);
					dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub
							this_class.refresh();
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
			
			
			CreateLoadingDialog.dialog_dismiss(process_persent_pointr);
			process_persent_pointr = null;
			
			if( result != null)
			{
				String error_message = "";
				
				//if( result == 0 )
				//	Toast.makeText(app_context, "0" , Toast.LENGTH_LONG).show();
				
				//if( result == 1 )
				//	Toast.makeText(app_context, "1" , Toast.LENGTH_LONG).show();
				
				if( result == 2 )
					error_message = "I/O資料錯誤";
				if( result == 3 )
					error_message = "網路位置錯誤";
				if( result == 4 )
					error_message = "網路無法連線\n請檢查網際網路是否開啟";
				if( result == 9 )
					error_message = "網路錯誤。";
			
				error_message += "\n或請稍候再試";
	        	
	        	new AlertDialog.Builder(app_context).setTitle("喔！喔喔！！").setMessage(error_message).setNeutralButton("確定", null).show();
	        	
			}
		 }
	}
	
	private OnClickListener on_clicker_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			if( v.getTag().equals("done") )
			{
				this_class.refresh();
			}
			
			if( v.getTag().equals("cancel") )
			{
    			String paraments = null;
    			
    			try
    			{
    				paraments = "__EVENTTARGET=&" + 
    							"__EVENTARGUMENT=&" +
    							"__VIEWSTATE=&" +
    							"__VIEWSTATEGENERATOR=" + URLEncoder.encode(__VIEWSTATEGENERATOR,"UTF-8") + "&" +
    							"__PREVIOUSPAGE=" + URLEncoder.encode(__PREVIOUSPAGE,"UTF-8") + "&" +
    							"__EVENTVALIDATION=" + URLEncoder.encode(__EVENTVALIDATION,"UTF-8") + "&" +
    							"Button1=" + URLEncoder.encode("取消此筆申辦資料","UTF-8");
    			}
    			catch (UnsupportedEncodingException e)
    			{
    				e.printStackTrace();
    			}
    			
    			String url = "http://wapp.taipower.com.tw/newnas/nawp093.aspx";
    			new LoadingDataAsyncTask().execute("cancel", url, paraments);
    			
    			result_dialog.dismiss();
			}
			
			if( v.getTag().equals("cancel_action") )
			{
				String apply_user = ((TextView) progress_cancel_view.findViewById(R.id.apply_user)).getText().toString();
				String email = ((TextView) progress_cancel_view.findViewById(R.id.email)).getText().toString();
				String tel1 = ((TextView) progress_cancel_view.findViewById(R.id.tel1)).getText().toString();
				String tel2 = ((TextView) progress_cancel_view.findViewById(R.id.tel2)).getText().toString();
				String tel3 = ((TextView) progress_cancel_view.findViewById(R.id.tel3)).getText().toString();
				
				String error_message = "";
				
				if( apply_user.isEmpty() )
					error_message = "姓名未填\n";
				
				if( email.isEmpty() )
					error_message = "email未填\n";
				
				if( tel1.length() < 2 && tel2.length() < 6 )
					error_message = "電話未填或錯誤";
				
				if( error_message.equals(""))
				{
					String paraments = "";
					
					try 
					{
						paraments = "__EVENTARGUMENT=&" + 
							   "__VIEWSTATE=" + URLEncoder.encode(__VIEWSTATE,"UTF-8") + "&" +
							   "__VIEWSTATEGENERATOR=" + URLEncoder.encode(__VIEWSTATEGENERATOR,"UTF-8") + "&" +
							   "__PREVIOUSPAGE=" + URLEncoder.encode(__PREVIOUSPAGE,"UTF-8") + "&" + 
							   "__EVENTVALIDATION=" + URLEncoder.encode(__EVENTVALIDATION,"UTF-8") + "&" +
							   "TB_name=" + URLEncoder.encode(apply_user,"UTF-8") + "&" + 
							   "TB_email=" + email + "&" +
							   "TB_area=" + tel1 + "&" +
							   "TB_phone=" + tel2 + "&" +
							   "TB_ext=" + tel3 + "&" +
							   "Button1=" +  URLEncoder.encode("確定取消","UTF-8");
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String url = "http://wapp.taipower.com.tw/newnas/nawp094.aspx";
					new LoadingDataAsyncTask().execute("cancel_done", url, paraments);
				}
				else
				{
					AlertDialog.Builder error_dialog = new AlertDialog.Builder(app_context);
					error_dialog.setTitle("歐！歐歐！！");
					error_dialog.setMessage(error_message);
					error_dialog.setNeutralButton("確定", null);
					error_dialog.show();
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
						rb_select = "rb_cps";
						apply_number0_text_view.setEnabled(true);
						apply_number0_text_view.setText("");
						break;
					case 1:
						rb_select = "rb_nas";
						apply_number0_text_view.setText("N");
						apply_number0_text_view.setEnabled(false);
						break;
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// TODO Auto-generated method stub
		}
    };
    
    private void refresh()
    {
    	String url = "http://wapp.taipower.com.tw/newnas/nawp090.aspx";
        new LoadingDataAsyncTask().execute("refresh",url);
        
        apply_number1_text_view.setText("");
		user_name_text_view.setText("");
		
		if(result_dialog != null)
			result_dialog.dismiss();
    }
}
