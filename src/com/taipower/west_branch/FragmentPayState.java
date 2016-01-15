package com.taipower.west_branch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;




public class FragmentPayState extends Fragment 
{
	
	private Context app_context;
	private Activity app_activity;
	
	private View current_view;
	
	private String version;
	
	private String txtCaptcha_data;
	//String strResult;
	private String custNoBox11;
	
	private TextView electric_number_text_view;
    private TextView txtCaptcha_data_text_view;
    private TextView user_name_text_view;
    
    private View result_view;
    
    private TextView date0_text_view;
    private TextView state0_text_view;
    private TextView date1_text_view;
    private TextView state1_text_view;
    private TextView date2_text_view;
    private TextView state2_text_view;
   	
    private TextView[] text_view_array ;
    
    private LinearLayout contact_Layout;
    
    private Button done_button;
    private Dialog result_dialog;
    
    private String url_content;
    private String url_code;
    
    private String __VIEWSTATE ;
	private String __EVENTVALIDATION;
	private String __VIEWSTATEGENERATOR;
    
    ImageView check_code_image;
		
    public FragmentPayState()
    {
    	
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
		
		this.app_context = this.getActivity();
    	this.app_activity = this.getActivity();
    	
    	//Bundle bundle = getArguments();
		
    	//if(bundle != null)
		//	version = bundle.getString("version");
    	//else 
    		version = "";
    	
		//if(version.equals("english"))
		//	current_view = inflater.inflate(R.layout.online_search_pay_state_english, container ,false);
		//else
    		current_view = inflater.inflate(R.layout.fragment_pay_state, container ,false);
			
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		InputMethodManager imm = (InputMethodManager) app_context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(current_view.getWindowToken(), 0);
        		
        		Bundle bundle = new Bundle();
        		bundle.putString("second_layer_content", "progress_state");
        		
        		FragmentTransaction ft = app_activity.getFragmentManager().beginTransaction();
        		Fragment fragment = new FragmentSecondLayerMenu();
        		fragment.setArguments(bundle);
        		ft.replace(R.id.fragment_content, fragment,"second_layer").commit();		
        		//finish();
        	}
        });
        
        ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setBackgroundResource(R.drawable.title_bar_send_button);
		title_bar_send_button.setVisibility(View.VISIBLE);
		title_bar_send_button.setOnClickListener(on_click_listener);
        
        //contact_Layout = (LinearLayout) findViewById(R.id.pay_state_result_contact_layout);
        //contact_Layout.setVisibility(View.INVISIBLE);
        
        electric_number_text_view = (TextView) findViewById(R.id.electric_number);
        txtCaptcha_data_text_view = (TextView) findViewById(R.id.check_number);
        user_name_text_view = (TextView) findViewById(R.id.user_name);
        /*
        date0_text_view = (TextView) findViewById(R.id.date0);
        state0_text_view = (TextView) findViewById(R.id.state0);
        date1_text_view = (TextView) findViewById(R.id.date1);
        state1_text_view = (TextView) findViewById(R.id.state1);
        date2_text_view = (TextView) findViewById(R.id.date2);
        state2_text_view = (TextView) findViewById(R.id.state2);
        */
        check_code_image = (ImageView) findViewById(R.id.check_code);
        
        //url_content = "http://wapp10.taipower.com.tw/eq2/nawp300_mobile.aspx";
        url_content = "http://wapp10.taipower.com.tw/eq2/nawp300.aspx";
        
        /*
        check code send 
        function refreshCAPTCHA()
        {
        	var d = new Date().getMilliseconds();
        	$("#captcha").attr("src","code.aspx?d=" + d);
		//}
        */
        
        url_code = "http://wapp10.taipower.com.tw/eq2/code.aspx?d=" ;
        
        new LoadingDataAsyncTask().execute("check_code",url_content,url_code + String.valueOf( System.currentTimeMillis() ) );
        
        Button clear_data = (Button) findViewById(R.id.clear_button);
        clear_data.setOnClickListener( on_click_listener );
        
        Button send_data = (Button) findViewById(R.id.send_button);
        send_data.setOnClickListener( on_click_listener );
        
        return current_view;
    }
	
    
    private View findViewById(int id)
    {
    	View view;
    	
    	view = current_view.findViewById(id);
    	
    	if( view == null)
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if( v.getId() == R.id.clear_button )
			{
				electric_number_text_view.setText("");
				txtCaptcha_data_text_view.setText("");
				user_name_text_view.setText("");
				
                new LoadingDataAsyncTask().execute("check_code",url_content,url_code + String.valueOf( System.currentTimeMillis() ));
                //new LoadingDataAsyncTask().execute("check_code",url_content,url_code);
			}
			
			if( v.getId() == R.id.title_bar_menu_button )
			//if( v.getId() == R.id.send_button )
			{
				custNoBox11 = electric_number_text_view.getText().toString();
                txtCaptcha_data = txtCaptcha_data_text_view.getText().toString();
        		
                String error_message = "";
        		
        		if(custNoBox11.isEmpty() || !ASaBuLuCheck.electricCheckFunction(custNoBox11))
        			error_message += "電號未填或錯誤\n";
                
                if(txtCaptcha_data.isEmpty())
                	error_message += "驗證碼未填";
               
                if(!error_message.equals(""))
                {
                   	AlertDialog.Builder error_warning = new AlertDialog.Builder(app_context);
                   	error_warning.setTitle("歐!歐!!");
                    error_warning.setMessage(error_message);
                    error_warning.setNeutralButton("重新填寫", null);
                    error_warning.show();
                }
                else
                {
                	//String url = "http://wapp10.taipower.com.tw/eq2/nawp300_mobile.aspx";
                	String url = "http://wapp10.taipower.com.tw/eq2/nawp300.aspx";
                	
                	//__VIEWSTATE = "/wEPDwUKLTkzMDcwMzYxOGRkTnBEsslyrlqwVYnWPZnhVBWecNBA5Wmz9zdL+LE7uBs=";
                    //__EVENTVALIDATION = "/wEWBALZsY7aCgK977LvDAK976ruDALcu4S2BLDIirFBM6MdlbtyA9BhhXFQKlduJA1rv/F/GQdybAED";
                	//__VIEWSTATEGENERATOR = "4838404B"; 
                	//Log.i("__VIEWSTATE :",__VIEWSTATE);
    				//Log.i("__EVENTVALIDATION", __EVENTVALIDATION);
    				//Log.i("__VIEWSTATEGENERATOR",__VIEWSTATEGENERATOR);
    				
                	String paraments = null;
                	
                	try 
                	{
                		paraments = "__VIEWSTATE=" + URLEncoder.encode( __VIEWSTATE,"UTF-8") + "&" +
                					"__EVENTVALIDATION=" +URLEncoder.encode( __EVENTVALIDATION,"UTF-8") + "&" + 
                					"__VIEWSTATEGENERATOR=" + URLEncoder.encode(__VIEWSTATEGENERATOR, "UTF-8" ) + "&"+
                					"cn="+ custNoBox11   + "&" +  
                					"c=" + txtCaptcha_data + "&" +
                					"submit=" + URLEncoder.encode("查詢","UTF-8");
                	} 
                	catch (UnsupportedEncodingException e) 
                	{
						// TODO Auto-generated catch block
						e.printStackTrace();
                	}
					
                	new LoadingDataAsyncTask().execute("send",url,paraments);
                }		
			}
		}
	};
	
	private String cookie = null;
	private HttpConnectResponse connection;
	
	private class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer>
	{	
		@Override
		protected void onPreExecute ()
    	{
    		super.onPreExecute();
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    	}
		
		byte[] response_data;
		byte[] response_data_content;
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
					
					connection.setUrl(params[1]);
					connection.setConnectMethod("GET", null);
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data_content = connection.startConnectAndResponseByteArray();
					//cookie = connection.getCookie();
					
					//response_data_content = HttpConnectResponse.onOpenConnection(params[1], "GET", null, HttpConnectResponse.COOKIE_CLEAR,HttpConnectResponse.HTTP_NONREDIRECT ) ;
					
					Thread.sleep(1500l);
					
					connection.setUrl(params[2]);
					connection.setConnectMethod("GET", null);
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data = connection.startConnectAndResponseByteArray();
					
					//response_data = HttpConnectResponse.onOpenConnection(params[2], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
					
					return_value = 1;
				}
				
				if( tag_value.equals("send") )
				{	
					connection.setUrl(params[1]);
					connection.setConnectMethod("POST", new String[]{params[2]});
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data = connection.startConnectAndResponseByteArray();
					
					//response_data = HttpConnectResponse.onOpenConnection( params[1], "POST", new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
					
					return_value = 2;
				}
				
				if( tag_value.equals("show_money0") )
				{	
					connection.setUrl(params[1]);
					connection.setConnectMethod("GET", null);
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data_content = connection.startConnectAndResponseByteArray();
					
					//response_data_content = HttpConnectResponse.onOpenConnection( params[1], "GET", null, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
					
					return_value = 5;
				}
				
				if( tag_value.equals("show_money1") )
				{	
					connection.setUrl(params[1]);
					connection.setConnectMethod("POST", new String[]{params[2]});
					connection.setCookieStatus(HttpConnectResponse.COOKIE_KEEP);
					connection.setRedirectStatus(HttpConnectResponse.HTTP_NONREDIRECT);
					response_data = connection.startConnectAndResponseByteArray();
					
					//response_data = HttpConnectResponse.onOpenConnection( params[1], "POST",new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT ) ;
					
					return_value = 6;
				}
				
				if( connection.HTTP_STATUS >= HttpConnectResponse.HTTP_FORBIDDEN && connection.HTTP_STATUS <= HttpConnectResponse.HTTP_NOT_FOUND )
					return_value = 404;
				//if( HttpConnectResponse.CONNECTION_STATE_CODE >= 400 && HttpConnectResponse.CONNECTION_STATE_CODE <= 404 )
				//	return_value = 404;
				
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
		
		Dialog process_persent_pointr = null;
		
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
			
			if( result.intValue() == 1 || result.intValue() == 5)
			{				
				Document document = null;
				try 
				{
					document = Jsoup.parse(new String(response_data_content,"UTF-8"));
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				__VIEWSTATE = document.getElementById("__VIEWSTATE").val();
				__EVENTVALIDATION = document.getElementById("__EVENTVALIDATION").val();
				__VIEWSTATEGENERATOR = document.getElementById("__VIEWSTATEGENERATOR").val();
				
				//Log.i("__VIEWSTATE :",__VIEWSTATE);
				//Log.i("__EVENTVALIDATION", __EVENTVALIDATION);
				//Log.i("__VIEWSTATEGENERATOR",__VIEWSTATEGENERATOR);
				if( result.intValue() == 1 )
				{
					Bitmap temp_bitmap = BitmapFactory.decodeByteArray(response_data , 0, response_data.length);
					check_code_image.setImageBitmap(temp_bitmap);
				}
				else
				{
					String user_name = ((TextView) findViewById(R.id.user_name)).getText().toString();
					
					String url = "http://wapp10.taipower.com.tw/eq2/nawps00.aspx";;
					
					String paraments = null;
					try 
					{
						/*
						 * @ pay state , "=" must change to urlencode "%3D" ?
						 * I don't know
						 */
						
						paraments = "__VIEWSTATE=" + URLEncoder.encode( __VIEWSTATE,"UTF-8") + "&" +
									"__EVENTVALIDATION=" +URLEncoder.encode( __EVENTVALIDATION,"UTF-8") + "&" + 
									"__VIEWSTATEGENERATOR=" + URLEncoder.encode(__VIEWSTATEGENERATOR, "UTF-8" ) + "&"+
									"n=" + URLEncoder.encode(user_name,"UTF-8") + "&" +  
									"submit=" + URLEncoder.encode("查詢明細","UTF-8");
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					new LoadingDataAsyncTask().execute("show_money1",url,paraments);
				}
			}
			
			if( result.intValue() == 6 )
			{
				Document document = null;
				
				try 
				{
					document = Jsoup.parse( new String(response_data,"UTF-8" ) );
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Element id_c = document.getElementById("c");
				Elements pure_u_8_24 = document.select("div[class=pure-u-8-24]");
				Elements tpcError =  document.select("span[class=tpcError]");
				//
				if( id_c != null )
				{
					String money0 = id_c.text();
					
					Log.i("money0",money0);
					
					text_view_array[1].setText(money0);
				}
				else if( pure_u_8_24.size() >= 5  )
				{	
					//for(Element qq :pure_u_8_24 )
					//	Log.i("pure_u_8_24",qq.text().toString());
					text_view_array[1].setText(pure_u_8_24.get(2).text());
					text_view_array[2].setText(pure_u_8_24.get(3).text());
					
					
					/*
					if( pure_u_8_24.size() == 5 )
					{	
						text_view_array[1].setText(pure_u_8_24.get(2).text());
						text_view_array[2].setText(pure_u_8_24.get(3).text());
					}
					*/
					if( pure_u_8_24.size() == 6 )
					{
						//text_view_array[1].setText(pure_u_8_24.get(2).text());
						//text_view_array[2].setText(pure_u_8_24.get(3).text());
						text_view_array[3].setText(pure_u_8_24.get(4).text());
					}
				}
				else
				{
					for(TextView qq : text_view_array)
						qq.setVisibility(View.GONE);	
				}
				
				if( tpcError.size() == 0)
					((TextView) result_dialog.findViewById(R.id.alert)).setVisibility(View.GONE);
				else
				{	
					((TextView) result_dialog.findViewById(R.id.alert)).setText("(輸入姓名不符，不顯示電費金額)");
					((TextView) result_dialog.findViewById(R.id.alert)).setTextColor(0xFFFF0000);
					((TextView) result_dialog.findViewById(R.id.alert)).setTextSize(14.0f);
				}
				
				result_dialog.show();
			}
			
			if( result.intValue() == 2)
			{
				Document document = null;
				
				try 
				{
					document = Jsoup.parse( new String(response_data,"UTF-8" ) );
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				
				Element rd1 = document.getElementById("rd1");
				Element r1 = document.getElementById("r1");
				
				Elements pure_u_1_3 = document.select("div[class=pure-u-1-3]");
				Elements pure_u_2_3 = document.select("div[class=pure-u-2-3]");
				
				Element msg = document.getElementById("msg");
					
				String date_string = "";
				String state_string = ""; 
				
				if( msg == null)
				{			
					DmInfor dm = new DmInfor(app_activity, app_context);
					
					int layout_height = (int) ((float) (151 + (40 * (pure_u_1_3.size() - 3) ) ) * dm.scale);
					
					Log.i("layout_height","" +layout_height);
					
					result_view =  (LayoutInflater.from(app_context)).inflate(R.layout.fragment_pay_state_result, null, false);
					
					date0_text_view = (TextView) result_view.findViewById(R.id.date0);
			        state0_text_view = (TextView) result_view.findViewById(R.id.state0);
			        date1_text_view = (TextView) result_view.findViewById(R.id.date1);
			        state1_text_view = (TextView) result_view.findViewById(R.id.state1);
			        date2_text_view = (TextView) result_view.findViewById(R.id.date2);
			        state2_text_view = (TextView) result_view.findViewById(R.id.state2);
					contact_Layout = (LinearLayout) result_view.findViewById(R.id.pay_state_result_contact_layout);
			        done_button = (Button) result_view.findViewById(R.id.pay_state_result_done_button);
					
					((LinearLayout) result_view.findViewById(R.id.pay_state_result_layout)).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, layout_height));
					
					String service_point_distr_number = electric_number_text_view.getText().toString().substring(0, 2);
					
			    	String[] service_distr_map = app_activity.getResources().getStringArray(R.array.service_distr_map);
					
			    	String[] contact_us = service_distr_map[Integer.valueOf(service_point_distr_number)].split(":");
			    	
					((TextView) result_view.findViewById(R.id.pay_state_result_service_point)).setText(contact_us[0].substring(3));
					((TextView) result_view.findViewById(R.id.pay_state_result_service_tel)).setText("聯絡電話：" + contact_us[1]);  
					//contact_Layout.setVisibility(View.VISIBLE);
					
					date_string = rd1.text().toString(); 
					state_string = r1.text().toString();
					
					if( pure_u_1_3.size() == 4 )
					{
						date1_text_view.setText(pure_u_1_3.last().text().toString());    
					    state1_text_view.setText(pure_u_2_3.last().text().toString());
					}
					
					if( pure_u_1_3.size() > 4 )
					{
						date1_text_view.setText(pure_u_1_3.get(pure_u_1_3.size()-2).text().toString());    
					    state1_text_view.setText(pure_u_2_3.get(pure_u_2_3.size()-2).text().toString());
						
						date2_text_view.setText(pure_u_1_3.last().text().toString());    
					    state2_text_view.setText(pure_u_2_3.last().text().toString());
					}
					
					date0_text_view.setText(date_string);    
				    state0_text_view.setText(state_string);
				    
				    //server connection limit so disable connect 
				    //new LoadingDataAsyncTask().execute("check_code",url_content,url_code + String.valueOf( System.currentTimeMillis() ));
				    //check_code_image.setImageBitmap(null);
				    
				    done_button.setOnClickListener(new OnClickListener()
				    {
						@Override
						public void onClick(View v) 
						{
							// TODO Auto-generated method stub
							electric_number_text_view.setText("");
							txtCaptcha_data_text_view.setText("");
							user_name_text_view.setText("");
							
							new LoadingDataAsyncTask().execute("check_code",url_content,url_code + String.valueOf( System.currentTimeMillis() ));
							result_dialog.dismiss();
						}
				    });
				    
				    result_dialog = new Dialog(app_context);
				    result_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				    result_dialog.setContentView(result_view);
				    
				    TextView bill_title = (TextView) result_view.findViewById(R.id.bill_title);
					TextView bill0 = (TextView) result_view.findViewById(R.id.bill0);
					TextView bill1 = (TextView) result_view.findViewById(R.id.bill1);
					TextView bill2 = (TextView) result_view.findViewById(R.id.bill2);
					
					text_view_array = new TextView[]{bill_title,bill0,bill1,bill2};
				    
				    String user_name = user_name_text_view.getText().toString(); 
				    
				    if( user_name.length() == 0  )
				    {	
				    	for(TextView qq : text_view_array)
							qq.setVisibility(View.GONE);
				    	
				    	result_dialog.show();
				    }
				    else
				    {
				    	String url = "http://wapp10.taipower.com.tw/eq2/nawps00.aspx";
				    	
				    	new LoadingDataAsyncTask().execute("show_money0",url);
				    }
				}  	
				else
				{	
					AlertDialog.Builder error_dialog = new AlertDialog.Builder(app_context);
					error_dialog.setTitle("歐！歐歐！！");
					error_dialog.setMessage(msg.text());
					error_dialog.setNeutralButton("重新輸入",new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub
							new LoadingDataAsyncTask().execute("check_code",url_content,url_code + String.valueOf( System.currentTimeMillis() ));
							dialog.dismiss();
						}						
					});
					error_dialog.show();
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




