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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.CreateLoadingDialog;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.HttpConnectResponse;
import com.taipower.west_branch.utility.NoTitleBar;
import com.taipower.west_branch.utility.PerferenceDialog;


public class online_service_protect_wire extends Activity {
	
	private Context app_context;
	private Activity app_activity;
	
	private Resources res;
	
	private String[] city_array;
	private String[] distr_array;
	private String[] zip_array;
 	
	private int city_selected_index;
	private int distr_selected_index;
	
	private int comm_city_selected_index;
	private int comm_distr_selected_index;
	
	private Spinner spinner_city;
	private Spinner spinner_distr;
	
	private Spinner spinner_comm_city ;
    private Spinner spinner_comm_distr ;
	
	
	private String result_text = "";
	
	TextView apply_user ;
	TextView tel_area_number ;
	TextView phone_number ;
	TextView email ;
	TextView ext_phone_number ;
	TextView mobile ;
	
	public online_service_protect_wire()
	{
		app_context = this;
		app_activity = this;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.online_service_protect_wire);
        
        
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  new DmInfor(app_activity,app_context).menu_linear_height ));
        
        
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        
        back_button.setOnClickListener(
        		new View.OnClickListener() 
        		{
        			public void onClick(View v) 
        			{
        				String[] data = new String[] { 	apply_user.getText().toString(),
							 	tel_area_number.getText().toString(),
							 	phone_number.getText().toString(),
							 	email.getText().toString(),
							 	ext_phone_number.getText().toString(),
							 	mobile.getText().toString()};


    		        	SharedPreferences setting = app_context.getSharedPreferences("remember", MODE_PRIVATE );
    		        	new PerferenceDialog( app_context, app_activity, data , setting.getBoolean("show_again", true ) ,null);
        				
        				//app_activity.finish();
        			}
        		}
        );
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
                
        res = app_context.getResources(); 
        city_array = res.getStringArray(R.array.online_service_city);
        
        
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_distr = (Spinner) findViewById(R.id.spinner_distr);
        
        ArrayAdapter adapter_city = new ArrayAdapter( app_context, android.R.layout.simple_spinner_item, city_array);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(adapter_city);
        
        spinner_city.setOnItemSelectedListener(on_item_selected_listener);
        spinner_distr.setOnItemSelectedListener(on_item_selected_listener);
        
        spinner_comm_city = (Spinner) findViewById(R.id.spinner_comm_city);
        spinner_comm_distr = (Spinner) findViewById(R.id.spinner_comm_distr);
        
        //ArrayAdapter adapter_city = new ArrayAdapter( app_context, android.R.layout.simple_spinner_item, city_array);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_comm_city.setAdapter(adapter_city);
        
        spinner_comm_city.setOnItemSelectedListener(on_item_selected_listener);
        spinner_comm_distr.setOnItemSelectedListener(on_item_selected_listener);
        
        apply_user = (TextView) findViewById(R.id.apply_user);
		tel_area_number = (TextView) findViewById(R.id.tel_area_number);
		phone_number = (TextView) findViewById(R.id.phone_number);
		email = (TextView) findViewById(R.id.email);
		ext_phone_number = (TextView) findViewById(R.id.ext_phone_number);
		mobile = (TextView) findViewById(R.id.mobile);
        
        Button base_data = (Button) findViewById(R.id.base_data);
        base_data.setOnClickListener( new View.OnClickListener() 
        {
        	@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
        		SharedPreferences setting = app_context.getSharedPreferences("remember", MODE_PRIVATE );
        		
        		apply_user.setText(setting.getString("apply_user",""));
        		tel_area_number.setText(setting.getString("tel_area_number",""));	   
        		phone_number.setText(setting.getString("phone_number",""));	   
        		email.setText(setting.getString("email",""));	   
        		ext_phone_number.setText(setting.getString("ext_phone_number",""));	   
        		mobile.setText(setting.getString("mobile",""));	   
			}
		});
        
        ImageButton send_button = (ImageButton) findViewById(R.id.send_button);
        
        send_button.setOnClickListener(new View.OnClickListener() 
        {
        	
        	
        	public void onClick(View v) 
        	{
        		String error_message = "";
        		
        		TextView user_id_number = (TextView) findViewById(R.id.user_id_number); 
        		
        		TextView worked_addr = (TextView) findViewById(R.id.worked_addr); 
        		TextView comm_addr = (TextView) findViewById(R.id.comm_addr); 
        		
        		        		
        		/*
        		apply_user = (TextView) findViewById(R.id.apply_user);
        		tel_area_number = (TextView) findViewById(R.id.tel_area_number);
        		phone_number = (TextView) findViewById(R.id.phone_number);
        		email = (TextView) findViewById(R.id.email);
        		ext_phone_number = (TextView) findViewById(R.id.ext_phone_number);
        		*/
        		
				if( user_id_number.getText().toString().isEmpty() ||  !ASaBuLuCheck.idCheck(user_id_number.getText().toString())  )
        			error_message += "身分證字號有誤\n";
        		
        		if( worked_addr.getText().toString().isEmpty() )
        			error_message += "裝設地址未填\n";
        		
        		if( comm_addr.getText().toString().isEmpty() )
        			error_message += "通訊地址未填\n";
        		
        		if( apply_user.getText().toString().length() < 2 )
        			error_message += "申請人資料錯誤\n";
        		
        		if( tel_area_number.getText().toString().length() < 2 || phone_number.getText().length() < 6 )
        			error_message += "電話錯誤\n";
        		
        		if( !email.getText().toString().contains("@")   )
        			error_message += "email錯誤\n";
        		
        		
        		if( !error_message.equals("") )
        		{
        			AlertDialog.Builder warning_dialog = new AlertDialog.Builder(online_service_protect_wire.this);
        			warning_dialog.setTitle("資料錯誤");
        			warning_dialog.setMessage(error_message);
        			warning_dialog.setNegativeButton("重新填寫", null);
        			warning_dialog.show();
        			
        		}
        		else
        		{
        			String full_type_worked_address = worked_addr.getText().toString().replace("0","０");
        			full_type_worked_address = full_type_worked_address.replace("1","１");
        			full_type_worked_address = full_type_worked_address.replace("2","２");
        			full_type_worked_address = full_type_worked_address.replace("3","３");
        			full_type_worked_address = full_type_worked_address.replace("4","４");
        			full_type_worked_address = full_type_worked_address.replace("5","５");
        			full_type_worked_address = full_type_worked_address.replace("6","６");
        			full_type_worked_address = full_type_worked_address.replace("7","７");
        			full_type_worked_address = full_type_worked_address.replace("8","８");
        			full_type_worked_address = full_type_worked_address.replace("9","９");
        		
        			String full_comm_type_address = comm_addr.getText().toString().replace("0","０");
        			full_comm_type_address = full_comm_type_address.replace("1","１");
        			full_comm_type_address = full_comm_type_address.replace("2","２");
        			full_comm_type_address = full_comm_type_address.replace("3","３");
        			full_comm_type_address = full_comm_type_address.replace("4","４");
        			full_comm_type_address = full_comm_type_address.replace("5","５");
        			full_comm_type_address = full_comm_type_address.replace("6","６");
        			full_comm_type_address = full_comm_type_address.replace("7","７");
        			full_comm_type_address = full_comm_type_address.replace("8","８");
        			full_comm_type_address = full_comm_type_address.replace("9","９");
        			
        			
        			
        			
        		/*
        		 
        		String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp3g2.asp"; 
        		 
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
        		
        			new LoadingDataAsyncTask().execute(Url , apply_user.getText().toString()
        											   , user_id_number.getText().toString()
						   							   , city_array[city_selected_index]
						   							   , distr_array[distr_selected_index]
						   							   , zip_array[distr_selected_index]
						   							   , full_type_worked_address
						   							   , tel_area_number.getText().toString()
						   							   , phone_number.getText().toString()
						   							   , ext_phone_number.getText().toString()
						   							   , email.getText().toString()
						   							   , city_array[comm_city_selected_index]
						   							   , distr_array[comm_distr_selected_index]
						   							   , zip_array[comm_distr_selected_index] 
						   							   , full_comm_type_address);
        		
        		
        			String post_data = "custname="	 + params[1] + "&" +
								   "custid=" 	 + params[2] + "&" +
								   "ApCity=" 	 + params[3] + "&" +
								   "ApCity2=" 	 + params[4] + "&" +
								   "ApCity3="    + params[5] + "&" +	//zip code
								   "ApT3=" 	     + params[6] + "&" +
								   "Aptelarea="  + params[7] + "&" +
								   "Aptelno=" 	 + params[8] + "&" +
								   "Aptelext=" 	 + params[9] + "&" +
								   "Apemailid="  + params[10] + "&" +
								   "ApCoCity=" 	 + params[11] + "&" +
								   "ApCoCity2="  + params[12] + "&" +
								   "ApCoCity3="  + params[13] + "&" +
								   "ApCoT3=" 	 + params[14] + "&" +
								   "ApTypeR2=A";
        		
        		*/
        		
        			String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp3g2.asp";
        			
        			
        			String paraments = "";
					try 
					{
						paraments = "custname="	 + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(apply_user.getText().toString(),"big5")) + "&" +
								   	"custid=" 	 + user_id_number.getText().toString() + "&" +
								   	"ApCity=" 	 + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(city_array[city_selected_index],"big5")) + "&" +
								   	"ApCity2=" 	 + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(distr_array[distr_selected_index],"big5")) + "&" +
								   	"ApCity3="   + zip_array[distr_selected_index] + "&" +	//zip code
								   	"ApT3=" 	 + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(full_type_worked_address ,"big5")) + "&" +
								   	"Aptelarea=" + tel_area_number.getText().toString() + "&" +
								   	"Aptelno=" 	 + phone_number.getText().toString() + "&" +
								   	"Aptelext="  + ext_phone_number.getText().toString() + "&" +
								   	"Apemailid=" + URLEncoder.encode(email.getText().toString() , "big5") + "&" +
								   	"ApCoCity="  + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(city_array[comm_city_selected_index],"big5")) + "&" +
								   	"ApCoCity2=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(distr_array[comm_distr_selected_index],"big5")) + "&" +
								   	"ApCoCity3=" + zip_array[comm_distr_selected_index]  + "&" +
								   	"ApCoT3=" 	 + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(full_comm_type_address,"big5")) + "&" +
								   	"ApTypeR2=A";
					} 
					catch (UnsupportedEncodingException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        			
        			new LoadingDataAsyncTask().execute("apply_tag",url,paraments);
        			
        		
        		
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
			
    		if( (parent.getId() == R.id.spinner_city) || (parent.getId() == R.id.spinner_comm_city) )
			{
				if(parent.getId() == R.id.spinner_city)
				{
					city_selected_index = (int ) id;
					
					distr_array = res.getStringArray(R.array.online_service_distr00 + city_selected_index);
				    zip_array = res.getStringArray(R.array.online_service_zip00 + city_selected_index);
					
				    ArrayAdapter adapter_distr = new ArrayAdapter(online_service_protect_wire.this,android.R.layout.simple_spinner_item, distr_array);
					adapter_distr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner_distr.setAdapter(adapter_distr);
				}
				else
				{
					comm_city_selected_index = (int ) id;
					
					distr_array = res.getStringArray(R.array.online_service_distr00 + comm_city_selected_index);
				    zip_array = res.getStringArray(R.array.online_service_zip00 + comm_city_selected_index);
					
				    ArrayAdapter adapter_distr = new ArrayAdapter(online_service_protect_wire.this,android.R.layout.simple_spinner_item, distr_array);
					adapter_distr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner_comm_distr.setAdapter(adapter_distr);
				}
			
			}
			
			if( (parent.getId() == R.id.spinner_distr) || (parent.getId() == R.id.spinner_comm_distr))
			{
				if( parent.getId() == R.id.spinner_distr)
					distr_selected_index = (int) id;
				else
					comm_distr_selected_index = (int) id;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// TODO Auto-generated method stub
			Toast.makeText(online_service_protect_wire.this, "沒有選到東西" , Toast.LENGTH_SHORT );
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
        			
        		process_persent_pointr = CreateLoadingDialog.createLoadingDialog(online_service_protect_wire.this, message , CreateLoadingDialog.NON_DOWNLOAD_TAG , CreateLoadingDialog.NON_DOWNLOAD_TAG, CreateLoadingDialog.NONCANCELABLE);
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
    
    
    
    
    
    
    
        
    private void result_dialog(String result_string ,final String[] tag_value)
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
    				else if(tag_value[0].equals("check_tag"))
    				{
    					String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp3g4.asp";
    					String paraments = "Submit=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode(" 確定送出  ","big5")) ;
    					
    					new LoadingDataAsyncTask().execute("recheck_tag",url,paraments);
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
    					String url = "http://wapp.taipower.com.tw/naweb/apfiles/nawp3g3.asp";
    					String paraments = "C1=F1&C1=F2&"; //+  
    									    //"sub_btm=" + HttpConnectResponse.urlencodeLikeBrowser(URLEncoder.encode("確定","big5")) ;
    					
    				
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
			
			result_dialog_box.setNegativeButton("取消", null);
			result_dialog_box.setPositiveButton("確定", alertDialog_button_onclick_listnter);
		}
		
		if( tag_value[0].equals("recheck_tag") )
		{
			
			result_dialog_box.setNegativeButton("確定", null);
			
		}
		
		
		result_dialog_box.setMessage(result_string);
    	result_dialog_box.show();
	

	}

    
}