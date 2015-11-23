package com.taipower.west_branch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
*/
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;






import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class online_service_change_name extends Activity 
{
	private Context app_context;
	private Activity app_activity;
	
	
	private Resources res;
	
	private String[] city_array;
	private String[] distr_array;
	private String[] zip_array;
 	
	private int city_selected_index;
	private int distr_selected_index;
	
	private String ApR2;
	
	private String msg = "N";
	
	private boolean msg_check = false;
	private boolean declaration_check = false;
	private boolean radio_check = false ;
	
	
	private String RadioTransfer1;
	private String hTransfer1;
	private String hTransfer2;
	private String hTransfer3;
	private String RadioTransfer11 ;
	private String hTransfer11;
	private String RadioTransfer21 ;
	private String hTransfer21;
	
	
	private Spinner spinner_city;
	private Spinner spinner_distr;
	
	private String result_text = "";
	
	TextView electric_number;
	
	/*
	TextView apply_user ;
	TextView tel_area_number ;
	TextView phone_number ;
	TextView email ;
	TextView ext_phone_number ;
	TextView mobile ;
	*/
	
	
	public online_service_change_name()
	{
		app_context = this;
		app_activity = this;
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.online_service_change_name);
        
        
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  new DmInfor(this,this).menu_linear_height ));
        
        
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        
        back_button.setOnClickListener(
        		new View.OnClickListener() 
        		{
        			public void onClick(View v) 
        			{
        				online_service_change_name.this.finish();
        			}
        		}
        );
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
        
        
        Spinner spinner_action = (Spinner) findViewById(R.id.spinner_choice_commu_addr);
        
        ArrayAdapter adapter_action = new ArrayAdapter(this,android.R.layout.simple_spinner_item,new String[]{"與用電地址相同","與用電地址不相同"});
        adapter_action.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_action.setAdapter(adapter_action);
        spinner_action.setOnItemSelectedListener(on_item_selected_listener);
        
        
        res = this.getResources(); 
        
        city_array = res.getStringArray(R.array.online_service_city);
        
        
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_distr = (Spinner) findViewById(R.id.spinner_distr);
        
        ArrayAdapter adapter_city = new ArrayAdapter(this,android.R.layout.simple_spinner_item, city_array);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(adapter_city);
        
        spinner_city.setOnItemSelectedListener(on_item_selected_listener);
        spinner_distr.setOnItemSelectedListener(on_item_selected_listener);
        
        
        
        RadioGroup  msg_group = (RadioGroup) findViewById(R.id.msg_group);
        msg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				// TODO Auto-generated method stub
				msg_check = true;
				
				if( group.getCheckedRadioButtonId() == R.id.msg0 )
					msg = "Y";
				
			}
		});
        
        
        CheckBox declaration_0 = (CheckBox) findViewById(R.id.check_declaration_0); 
        declaration_0.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
        	@Override
			public void onCheckedChanged(CompoundButton buttonView , boolean isChecked) 
			{
				// TODO Auto-generated method stub
				if(isChecked)
				{	
					RadioTransfer1 = "C1";
					hTransfer1 = "C1";
					hTransfer2 = "B3";
					hTransfer3 = "C3";
					
					declaration_check = isChecked;
				}
			}
        });
        
        RadioGroup  declaration_auto_pay_group = (RadioGroup) findViewById(R.id.declaration_auto_pay_group);
        declaration_auto_pay_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				// TODO Auto-generated method stub
				radio_check = true;
				
				if( checkedId == R.id.radio_declaration_auto_pay )
				{
					RadioTransfer11 = "A11";
					hTransfer11 = "A11";
					RadioTransfer21 = "B11";
					hTransfer21 = "B11";
				}
				if( checkedId == R.id.radio_declaration_cancel_auto_pay )
				{
					RadioTransfer11 = "A11";
					hTransfer11 = "A11";
					RadioTransfer21 = "A11";
					hTransfer21 = "A11";
				}
				if( checkedId == R.id.radio_declaration_non_auto_pay )
				{
					RadioTransfer11 = "A21";
					hTransfer11 = "A21";
					RadioTransfer21 = "B31";
					hTransfer21 = "B31";
				}
			}
		});
        
        
        
        
        ImageButton send_button = (ImageButton) findViewById(R.id.send_button);
        
        send_button.setOnClickListener(new View.OnClickListener() 
        {
        	
        	
        	public void onClick(View v) 
        	{
        		ASaBuLuCheck check = new ASaBuLuCheck();
        		
        		String error_message = "";
        		
        		TextView electric_number = (TextView) findViewById(R.id.electric_number);
        		TextView user_name = (TextView) findViewById(R.id.user_name);
        		TextView new_user_name = (TextView) findViewById(R.id.new_user_name);
        		TextView new_user_id_number = (TextView) findViewById(R.id.new_user_id_number); 
        		
        		//ApR2;
        		//ApCoCity     city_array[city_selected_index]
        		//ApCoCity2    distr_array[distr_selected_index]
        		//ApCoCity3    zip_array[distr_selected_index]
        		
        		//ApCoT3	   
        		TextView new_commu_addr = (TextView) findViewById(R.id.new_commu_addr); 
        		
        		//Aptelarea:
        		TextView used_tel_area_number = (TextView) findViewById(R.id.used_addr_tel_are);
				//Aptelno:  
        		TextView used_addr_tel_phone_number = (TextView) findViewById(R.id.used_addr_tel_phone_number);
        		//Aptelext:	
        		TextView used_addr_extra_tel_number = (TextView) findViewById(R.id.used_addr_extra_tel_number);
        		
        		//ApCoDaytelarea:
        		//ApCoDaytelno:
        		//ApCoDaytelext:
        		//ApCoNighttelarea:
        		//ApCoNighttelno:
        		//ApCoNighttelext:
        		//ApCoFaxtelarea:
        		//ApCoFaxtelno:
        		//ApCoFaxtelext:
        		
        		//Mobile:      mobile.getText().toString();
        		TextView used_addr_mobile = (TextView) findViewById(R.id.used_addr_mobile);
        		//msg:         msg   
        		
        		//Mobile2:
        		
        		//Apemailid:      email.getText().toString()
        		TextView used_addr_email = (TextView) findViewById(R.id.used_addr_email);
        		//usepurpose:     
        		TextView used_addr_kind =  (TextView) findViewById(R.id.used_addr_kind); 
        		//RadioTransfer1  C1
        		//hTransfer3      C3
        		//hTransfer1      C1
        		//hTransfer2      B1
        		//RadioTransfer11:A21
        		//hTransfer11:A21
        		//RadioTransfer21:B21
        		//hTransfer21:B21
        		//R1:V3
        		//IdType:I
        		//authType1:1
        		
        		/*
        		apply_user = (TextView) findViewById(R.id.apply_user);
        		tel_area_number = (TextView) findViewById(R.id.tel_area_number);
        		phone_number = (TextView) findViewById(R.id.phone_number);
        		email = (TextView) findViewById(R.id.email);
        		ext_phone_number = (TextView) findViewById(R.id.ext_phone_number);
        		*/
        		
        		if( !ASaBuLuCheck.electricCheckFunction(electric_number.getText().toString()) )
        			error_message += "電號錯誤\n";
        		
        		if( user_name.getText().toString().isEmpty() )
        			error_message += "用電戶名未填\n";
        		
        		if( new_user_name.getText().toString().isEmpty() )
        			error_message += "新用電戶名未填\n";
        		
        		new ASaBuLuCheck();
				if( new_user_id_number.getText().toString().isEmpty() ||  !ASaBuLuCheck.idCheck(new_user_id_number.getText().toString())  )
        			error_message += "身分證字號有誤\n";
        		
        		if( new_commu_addr.getText().toString().isEmpty() && ApR2.equals("V7") )
        			error_message += "郵寄地址未填\n";
        		
        		if( !used_addr_mobile.getText().toString().isEmpty() && !msg_check)
        			error_message += "手機簡訊提醒未勾選\n";
        		
        		if( used_addr_tel_phone_number.getText().toString().length() < 6  && used_addr_mobile.getText().toString().isEmpty() )
        			error_message += "電話錯誤\n";
        		
        		if( !used_addr_email.getText().toString().contains("@") )
        			error_message += "email格式錯誤\n";
        		
        		
        		if( used_addr_kind.getText().toString().isEmpty() )
        			error_message += "用電用途未填\n";
        		        		
        		if( !declaration_check)
        			error_message += "過戶聲明未勾選\n";
        		
        		if( !radio_check)
        			error_message += "電費代繳未勾選\n";
        		
        		
        		/*
        		if( apply_user.getText().toString().isEmpty() )
        			error_message += "申請人未填\n";
        		*/
        		
        		
        		
        		
        		
        		if( !error_message.equals("") )
        		{
        			AlertDialog.Builder warning_dialog = new AlertDialog.Builder(online_service_change_name.this);
        			warning_dialog.setTitle("資料錯誤");
        			warning_dialog.setMessage(error_message);
        			warning_dialog.setNegativeButton("重新填寫", null);
        			warning_dialog.show();
        			
        			error_message= "";
        		}
        		else
        		{
        			String full_type_address = new_commu_addr.getText().toString().replace("0","０");
        			full_type_address = full_type_address.replace("1","１");
        			full_type_address = full_type_address.replace("2","２");
        			full_type_address = full_type_address.replace("3","３");
        			full_type_address = full_type_address.replace("4","４");
        			full_type_address = full_type_address.replace("5","５");
        			full_type_address = full_type_address.replace("6","６");
        			full_type_address = full_type_address.replace("7","７");
        			full_type_address = full_type_address.replace("8","８");
        			full_type_address = full_type_address.replace("9","９");
        		
        		       		
        			
        		/*
        		
        		String url = "http://wapp.taipower.com.tw/naweb/apfiles/Nawp2J2.asp";
        		
        		try 
        		{
					 "proc_type=" + proc_type +
								 "&custno=" + electric_number.getText().toString() +
								 "&custname=" + URLEncoder.encode( user_name.getText().toString() , "BIG5") + 
								 "&county=" + URLEncoder.encode( city_array[city_selected_index], "BIG5") + 
								 "&area=" + URLEncoder.encode( distr_array[distr_selected_index], "BIG5") +  
								 "&zip_code=" + zip_array[distr_selected_index] + 
								 "&addr_3=" + URLEncoder.encode(full_type_address, "BIG5") + 
								 "&username=" + URLEncoder.encode( apply_user.getText().toString() , "BIG5") +
								 "&emailid=" + urlencode_email +
								 "&telarea=" + tel_area_number.getText().toString() +
								 "&telno=" + phone_number.getText().toString() +
								 "&telext=" + ext_phone_number.getText().toString() + 
								 "&appl_type=02" ;
				
        		} 
        		catch (UnsupportedEncodingException e) 
        		{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		*/
        		
        		/*
        		new LoadingDataAsyncTask().execute(Url , "I"
						   , "05473958173"
						   , "曹峻愷" 
						   , "桃園縣" 
						   , "龜山鄉"
						   , "333"
						   , "萬壽路一段１６１之１號１６樓之４"
						   , "劉承倫" 
						   , "cksleeper@gmail.com"
						   , "02"
						   , "29915511"
						   , "363" );
        		
        		
        		
        		new LoadingDataAsyncTask().execute(Url , electric_number.getText().toString()
						   							   , user_name.getText().toString() 
						   							   , new_user_name.getText().toString()
						   							   , new_user_id_number.getText().toString()
						   							   , ApR2
						   							   , city_array[city_selected_index] 
						   							   , distr_array[distr_selected_index]
						   							   , zip_array[distr_selected_index].toString()
						   							   , full_type_address
						   							   , used_tel_area_number.getText().toString()
						   							   , used_addr_tel_phone_number.getText().toString()
						   							   , used_addr_extra_tel_number.getText().toString()
						   							   , used_addr_mobile.getText().toString()
						   							   , msg
						   							   , used_addr_email.getText().toString()
						   							   , used_addr_kind.getText().toString()
						   							   , RadioTransfer1  
						   							   , hTransfer3      
						   							   , hTransfer1      
						   							   , hTransfer2      
						   							   , RadioTransfer11
						   							   , hTransfer11
						   							   , RadioTransfer21
						   							   , hTransfer21 );
						   							   
						   							   
						String post_data = "custno=" + params[1] + "&" +
								   "username=" + params[2] + "&" +
								   "newusername=" + params[3] + "&" +
								   "IdNumber=" + params[4] + "&" +
								   "ApR2=" + params[5] + "&" +
								   "ApCoCity=" + params[6] + "&" +
								   "ApCoCity2=" + params[7] + "&" +
								   "ApCoCity3=" + params[8] + "&" +
								   "ApCoT3=" + params[9] + "&" +
								   "Aptelarea=" + params[10] + "&" +
								   "Aptelno=" + params[11] + "&" +
								   "Aptelext=" + params[12] + "&" +
								   "ApCoDaytelarea=" + "&" +
								   "ApCoDaytelno=" + "&" +
								   "ApCoDaytelext=" + "&" +
								   "ApCoNighttelarea=" + "&" +
								   "ApCoNighttelno=" + "&" +
								   "ApCoNighttelext=" + "&" +
								   "ApCoFaxtelarea=" + "&" +
								   "ApCoFaxtelno=" + "&" +
								   "ApCoFaxtelext=" + "&" +
								   "Mobile=" + params[13] + "&" +
								   "msg=" + params[14] + "&" +
								   "Mobile2=" + "&" +
								   "Apemailid:=" + params[15] + "&" +
								   "usepurpose=" + params[16] + "&" +
								   "RadioTransfer1=" + params[17] + "&" +
								   "hTransfer3=" + params[18] + "&" +
								   "hTransfer1=" + params[19] + "&" +
								   "hTransfer2=" + params[20] + "&" +
								   "RadioTransfer11=" + params[21] + "&" +
								   "hTransfer11=" + params[22] + "&" +
								   "RadioTransfer21=" + params[23] + "&" +
								   "hTransfer21=" + params[24] + "&" +
								   "R1=V3" 		+ "&" +
								   "T2=" 		+ "&" +
								   "telarea=" 	+ "&" +
								   "telno=" 	+ "&" +
								   "telext=" 	+ "&" +
								   "emailid=" 	+ "&" +
								   "R2=" 		+ "&" +
								   "City="  	+ "&" +
								   "City2=" 	+ "&" +
								   "City3=" 	+ "&" +
								   "T3="  		+ "&" +
								   "IdType=I" 	+ "&" +
								   "authType1=1" ;   							   
						
        		
        		*/
        		
        			String url = "http://wapp.taipower.com.tw/naweb/apfiles/Nawp2J2.asp";
        			String parameters = "";
        			
        			try {
						parameters = "custno=" 		+ electric_number.getText().toString() + "&" +
						    		 "username=" 	+ HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(user_name.getText().toString(),"big5")) + "&" +
								   	 "newusername=" 	+ HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(new_user_name.getText().toString(),"big5")) + "&" +
								   	 "IdNumber=" 	+ new_user_id_number.getText().toString() + "&" +
								   	 "ApR2=" 		+ ApR2 + "&" +
								     "ApCoCity=" 	+ HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(city_array[city_selected_index] ,"big5"))  + "&" +
								   	 "ApCoCity2=" 	+ HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(distr_array[distr_selected_index],"big5")) + "&" +
								   	 "ApCoCity3=" 	+ zip_array[distr_selected_index] + "&" +
								   	 "ApCoT3=" 		+ HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(full_type_address,"big5")) + "&" +
								   	 "Aptelarea=" 	+ used_tel_area_number.getText().toString() + "&" +
								   	 "Aptelno=" 		+ used_addr_tel_phone_number.getText().toString() + "&" +
								   	 "Aptelext=" 	+ used_addr_extra_tel_number.getText().toString() + "&" +
								   			"ApCoDaytelarea=" + "&" +
								   			"ApCoDaytelno=" + "&" +
								   			"ApCoDaytelext=" + "&" +
								   			"ApCoNighttelarea=" + "&" +
								   			"ApCoNighttelno=" + "&" +
								   			"ApCoNighttelext=" + "&" +
								   			"ApCoFaxtelarea=" + "&" +
								   			"ApCoFaxtelno=" + "&" +
								   			"ApCoFaxtelext=" + "&" +
								   			"Mobile=" 		+ used_addr_mobile.getText().toString() + "&" +
								   			"msg=" 			+ msg + "&" +
								   			"Mobile2=" 		+ "&" +
								   			"Apemailid=" 	+ URLEncoder.encode(used_addr_email.getText().toString(),"big5")+ "&" +
								   			"usepurpose=" 	+ HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(used_addr_kind.getText().toString(),"big5")) + "&" +
								   			"RadioTransfer1=" + RadioTransfer1  + "&" +
								   			"hTransfer3=" 	+ hTransfer3 + "&" +
								   			"hTransfer1=" 	+ hTransfer1 + "&" +
								   			"hTransfer2=" 	+ hTransfer2 + "&" +
								   			"RadioTransfer11=" + RadioTransfer11 + "&" +
								   			"hTransfer11=" 	+ hTransfer11 + "&" +
								   			"RadioTransfer21=" + RadioTransfer21 + "&" +
								   			"hTransfer21=" + hTransfer21 + "&" +
								   			"R1=V3" 		+ "&" +
								   			"T2=" 		+ "&" +
								   			"telarea=" 	+ "&" +
								   			"telno=" 	+ "&" +
								   			"telext=" 	+ "&" +
								   			"emailid=" 	+ "&" +
								   			"R2=R6"		+ "&" +
								   			"City="  	+ "&" +
								   			"City2=" 	+ "&" +
								   			"City3=" 	+ "&" +
								   			"T3="  		+ "&" +
								   			"IdType=I" 	+ "&" +
								   			"authType1=1" ;
					} 
        			catch (UnsupportedEncodingException e) 
        			{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}   						
        			
        			new LoadingDataAsyncTask().execute("apply_tag",url,parameters);
        			
        		}
        	}
        		
        });
    
    
        
        
    
        
    }
    
    
    OnItemSelectedListener on_item_selected_listener = new OnItemSelectedListener()
    {
    	@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
		{
			// TODO Auto-generated method stub
			if( parent.getId() == R.id.spinner_choice_commu_addr)
			{
				switch( (int) id)
				{
					case 0:
						ApR2 = "R6";
						spinner_city.setEnabled(false);
						spinner_distr.setEnabled(false);
						findViewById(R.id.new_commu_addr).setEnabled(false);
						break;
					case 1:
						ApR2 = "V7";
						spinner_city.setEnabled(true);
						spinner_distr.setEnabled(true);
						findViewById(R.id.new_commu_addr).setEnabled(true);
						break;
				}
			}
    		
			
    		if( parent.getId() == R.id.spinner_city )
			{
				city_selected_index = (int ) id;
				
				distr_array = res.getStringArray(R.array.online_service_distr00 + city_selected_index);
			    zip_array = res.getStringArray(R.array.online_service_zip00 + city_selected_index);
				
				
				ArrayAdapter adapter_distr = new ArrayAdapter(online_service_change_name.this,android.R.layout.simple_spinner_item, distr_array);
				adapter_distr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        spinner_distr.setAdapter(adapter_distr);
			
			}
			
			if( parent.getId() == R.id.spinner_distr )
			{
				distr_selected_index = (int) id;		
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// TODO Auto-generated method stub
			Toast.makeText(online_service_change_name.this, "沒有選到東西" , Toast.LENGTH_SHORT );
		}
    };
    
    
    
   
    
	class LoadingDataAsyncTask extends AsyncTask<String, Integer, Integer> 
    {
    	String tag_value = "";
		String result_text = "";
		
		protected void onPreExecute ()
    	{
    		super.onPreExecute();
    		//Toast.makeText(rss_reader.this, "更新資料中", Toast.LENGTH_LONG).show();
    		publishProgress(0);
    	}
		
    	byte[] response_data = null;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			
			//set tag
			tag_value = params[0];
			
			try 
			{
				response_data = HttpConnectResponse.onOpenConnection(params[1], "POST", new String[]{params[2]}, HttpConnectResponse.COOKIE_KEEP,HttpConnectResponse.HTTP_NONREDIRECT);
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
			
			return null;
		}
		
		
		//Dialog process_persent = null;
		Dialog process_persent_pointr = null;
		
		
		protected void onProgressUpdate(Integer... progress) 
        {
        	String message = ""; 
			
        	if( progress[0] == 0 )
        	{
        	   	message = "資料傳送中";
        			
        		process_persent_pointr = CreateLoadingDialog.createLoadingDialog(online_service_change_name.this, message , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NONCANCELABLE);
        		
        	}
        	       	
        	super.onProgressUpdate(progress);
        }
		
		@Override
        protected void onPostExecute(Integer result) 
        {
			String temp = "";
			
			try 
			{
				temp = new String(response_data,"big5");
			} 
			catch (UnsupportedEncodingException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Document doc_detail = Jsoup.parse(temp);  
		    
			//data error element 
			Elements detail_center = doc_detail.getElementsByTag("center");
			
			//detail_duplicate
			Elements detail_duplicate_title = doc_detail.select("table[id=TABLE1]");
			Elements detail_duplicate_input = doc_detail.select("input");
			
			Element[] each_input = new Element[detail_duplicate_input.size()] ;
			
			for(int i = 0 ; i < detail_duplicate_input.size() ; i++)
				each_input[i] = detail_duplicate_input.get(i);	
			
			//apply 
			Elements detail = doc_detail.select("table[id=newspaper]");
			
			
			if( !detail_center.isEmpty() )	//data error
			{
				result_text = detail_center.text().toString();
				tag_value = "error_tag";
			}
			else if( !detail_duplicate_title.isEmpty() ) //detail_duplicate_title
			{
				String[] temp_title = detail_duplicate_title.text().toString().split(" ");
				
				for( int i = 0 ; i < temp_title.length ; i++)		
					result_text += temp_title[i] + ":" + each_input[i].val().toString() + "\n";
				
				tag_value = "duplicate_tag";
				
			}
			else
			{	
				result_text = detail.text().toString();
			}

			result_text = result_text.replace("　", "");
			
			
			ArrayList<String> input_value = new ArrayList<String>();
			
			input_value.add(tag_value);
			
			if(tag_value.equals("duplicate_tag"))
			{	
				for(Element qq : each_input)	
					input_value.add(qq.val().toString());
			}
			
			String[] pass_value = input_value.toArray(new String[input_value.size()]);
			
			progress_present_dismiss();
			
			result_dialog(result_text, pass_value );
			
			
        }
		
		//self-define function
		private void progress_present_dismiss()
		{
			CreateLoadingDialog.dialog_dismiss(process_persent_pointr);
			process_persent_pointr = null;
			
		}
		
		
		    	
		                
		           
			    	
		    
		
		
		
    }
    
    
    
    
    
    
    
        
    private void result_dialog(String result_string, final String[] tag_value )
	{
    	/*
    	 * interface override 
    	 */
    	DialogInterface.OnClickListener alertDialog_button_onclick_listnter = new DialogInterface.OnClickListener()
        {
    		@Override
    		public void onClick(DialogInterface dialog, int which)
    		{
    			// TODO Auto-generated method stub
    			
    			try 
				{
    			
    			if( which == DialogInterface.BUTTON_POSITIVE) 
    			{
    				if(tag_value[0].equals("duplicate_tag") )
    				{
    					
    					/* no used
    					String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp530.asp";
    					
    					new LoadingDataAsyncTask().execute("close_tag",url);
    					*/
    				}
    			}
    			
    			if( which == DialogInterface.BUTTON_NEGATIVE)
    			{
    				if( tag_value[0].equals("duplicate_tag") )
    				{
    					String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp825.asp";
    					
    					String paraments = "applid=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[1],"big5"))  + "&" +
    									   "T10=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[2],"big5"))  + "&" +
    									   "T7=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[3],"big5"))  + "&" +
    									   "T8=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[4],"big5")) + "&" +
    									   "T3=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[5],"big5")) + "&" +
    									   "T6=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[6],"big5")) + "&" +
    									   "T9=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(tag_value[7],"big5")) + "&" +
    									   "action1=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode("取消上列申辦資料" ,"big5")) ;
    				
    					new LoadingDataAsyncTask().execute("cancel_tag",url,paraments);
    				}
    			}
    			
    			
    			
    			if( which == DialogInterface.BUTTON_NEUTRAL)
    			{
    				
    				if( tag_value[0].equals("apply_tag") || tag_value[0].equals("cancel_tag"))
    				{
    					String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp2j3.asp";
    					String paraments = "Submit=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode("確定送出","big5")) ;
    					
    				
    					new LoadingDataAsyncTask().execute("check_tag",url,paraments);
    				}
    			}
    			
    			
				} 
    			catch (UnsupportedEncodingException e) 
    			{
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			dialog.dismiss();
    			
    		}
        };
    	
    	
    	
    	AlertDialog.Builder result_dialog_box = new AlertDialog.Builder(app_context);
		result_dialog_box.setTitle("資料確認");
		
		//重複申請
		if( tag_value[0].equals("duplicate_tag") )
		{
			
			result_dialog_box.setPositiveButton("維持申請", alertDialog_button_onclick_listnter);
			result_dialog_box.setNegativeButton("取消申請", alertDialog_button_onclick_listnter);
		}
		
		
		if( tag_value[0].equals("apply_tag") || tag_value[0].equals("cancel_tag") )
		{
			String output = "";
			String temp = "";
			int count = 0;
			
			//"　" full type space
				
			result_string.replace("", "　");
				
			for(int i = 0 ; i < result_string.length() ; i++)
			{	
				temp = result_string.substring(i,i+1);
					
				if( temp.contains(" ")  )
				{
					if( count % 2 == 0 )
						output += ":" ;
					else
						output += "\n\n" ;
					
					count ++;
				}	
				else	
					output = output + String.valueOf(temp) ;
			
			}
			
			
			result_string = output;
			
			result_dialog_box.setNeutralButton("確定", alertDialog_button_onclick_listnter);
			result_dialog_box.setNegativeButton("關閉/取消申請", alertDialog_button_onclick_listnter);
		}
		
		if( tag_value[0].equals("error_tag" ) )
		{	
			
			result_dialog_box.setNegativeButton("關閉", null);
		}
		
    	
		if( tag_value[0].equals("check_tag") )
		{
			
			result_dialog_box.setNegativeButton("確定", null);
		}
		
		
		result_dialog_box.setMessage(result_string);
    	result_dialog_box.show();
	}



    
    
    
   
    
    
    
    
    
    
   
    
    
    
}