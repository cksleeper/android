package com.taipower.west_branch;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;
import com.taipower.west_branch.utility.PerferenceDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView; 
import android.widget.Toast;



public class FragmentSendEmailAboutMeter extends Fragment 
{
	private Context app_context;
	private Activity app_activity;
	private FragmentSendEmailAboutMeter this_class;
	
	private File photoFile;
	
	private final static int final_bill = 0;
	private final static int no_meter_read = 1;
	private int email_index;
	
	private String capture_picture_location;
	private String browser_picture_location;
	private String capture_picture_location_water;
	private String browser_picture_location_water;
	
	private Resources resource;
	private String[] city_array;
	private String[] distr_array;
	private String[] zip_array;
	private int city_selected_index;
	private int distr_selected_index;
	
	private Spinner spinner_city;
	private Spinner spinner_distr;
    
	private HashMap<String,Integer> street_hash_map;
	
	//private TextView used_address_text_view;
	private AutoCompleteTextView used_address_text_view;
	private TextView meter_read01_text_view;
	private TextView meter_serial01_text_view;
	
	private TextView meter_read01_water_text_view;
	private TextView meter_serial01_water_text_view;
	
	private TextView apply_user_text_view;
	private TextView tel_area_number_text_view;
	private TextView phone_number_text_view;
	//private TextView email_text_view;
	private TextView ext_phone_number_text_view;
	private TextView mobile_text_view;
	
	private String apply_user;
	private String tel_area_number;
	private String phone_number;
	private String email = "";
	private String ext_phone_number;
	private String mobile;
	
	private SharedPreferences setting ;
	
	private View current_view;
	
	public FragmentSendEmailAboutMeter()
	{
		//this.app_context = this;
		//this.app_activity = this;
		//this.this_class = this;
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	this.app_context = this.getActivity();
		this.app_activity = this.getActivity();
		this.this_class = this;
    	
		
		Bundle bundle = this_class.getArguments();
		email_index = bundle.getInt("item");
		
		current_view = inflater.inflate(R.layout.fragment_send_email_about_meter, container, false);
		
		int[] title_resource_id = new int[]{R.drawable.title_bar_about_meter_final_bill,R.drawable.title_bar_about_meter_on_read}; 
		
		((ImageView) findViewById(R.id.title_bar_main_title)).setBackgroundResource(title_resource_id[email_index]);
		
		ImageView title_bar_send_button = (ImageView) findViewById(R.id.title_bar_menu_button);
		title_bar_send_button.setVisibility(View.VISIBLE);
		title_bar_send_button.setBackgroundResource(R.drawable.title_bar_send_button);
		title_bar_send_button.setOnClickListener(button_on_click_listener);
		
        
        ImageButton back_button = (ImageButton) findViewById(R.id.title_bar_back_button);
        back_button.setOnClickListener( new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		apply_user = apply_user_text_view.getText().toString();
        		tel_area_number = tel_area_number_text_view.getText().toString();
        		phone_number = phone_number_text_view.getText().toString();
        		//email = email_text_view.getText().toString();
        		ext_phone_number = ext_phone_number_text_view.getText().toString();
        		mobile = mobile_text_view.getText().toString(); 
        		
        		String[] data = new String[]{ apply_user, tel_area_number, phone_number, email, ext_phone_number, mobile};

    		    SharedPreferences setting = app_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
    		    
    		    new PerferenceDialog( app_context, app_activity, data , setting.getBoolean("show_again", true ), "about_meter" );
        		//finish();
    		    
    		    InputMethodManager imm = (InputMethodManager) app_context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(current_view.getWindowToken(), 0);
        	}
        });
        
        
        resource = app_context.getResources(); 
        city_array = resource.getStringArray(R.array.online_service_city);
        
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_distr = (Spinner) findViewById(R.id.spinner_distr);
        
        ArrayAdapter adapter_city = new ArrayAdapter( app_context, android.R.layout.simple_spinner_item, city_array);
        adapter_city.setDropDownViewResource(R.layout.spinner_dropdown);
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
        
        used_address_text_view = (AutoCompleteTextView) findViewById(R.id.used_address);
        used_address_text_view.setAdapter(select_filter_adapter);
        used_address_text_view.setOnTouchListener(new View.OnTouchListener()
        {
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				// TODO Auto-generated method stub
				if( ((TextView) v).getText().toString().startsWith("請填寫") )
					((TextView) v).setText("");
				
				return false;
			}
        	
        });
        
		meter_read01_text_view = (TextView) findViewById(R.id.meter_read01);
		meter_serial01_text_view = (TextView) findViewById(R.id.meter_serial01);
		
		apply_user_text_view = (TextView) findViewById(R.id.apply_user);
		tel_area_number_text_view = (TextView) findViewById(R.id.tel_area_number);
		phone_number_text_view = (TextView) findViewById(R.id.phone_number);
		ext_phone_number_text_view = (TextView) findViewById(R.id.ext_phone_number);
		//email_text_view = (TextView) findViewById(R.id.email);
		mobile_text_view = (TextView) findViewById(R.id.mobile);
	
		
        ImageButton browser_button = (ImageButton) findViewById(R.id.browser_button);
        browser_button.setOnClickListener( button_on_click_listener );
        
        ImageButton capture_picture_button = (ImageButton) findViewById(R.id.capture_picture_button);
        capture_picture_button.setOnClickListener( button_on_click_listener );
        
        if( email_index == no_meter_read )
        {
        	((LinearLayout) findViewById(R.id.water_layout)).removeAllViews();
        }
        else
        {
        	ImageButton browser_button_water = ( ImageButton ) findViewById(R.id.browser_button_water);
        	browser_button_water.setOnClickListener( button_on_click_listener );
        
        	ImageButton capture_picture_button_water = (ImageButton) findViewById(R.id.capture_picture_button_water);
        	capture_picture_button_water.setOnClickListener( button_on_click_listener );
        
        	meter_read01_water_text_view = (TextView) findViewById(R.id.meter_read01_water);
			meter_serial01_water_text_view = (TextView) findViewById(R.id.meter_serial01_water);
        
        }
        
        Button base_data = (Button) findViewById(R.id.base_data);
        base_data.setOnClickListener( button_on_click_listener );
        		
        Button send_button = (Button) findViewById(R.id.send_button);
        send_button.setOnClickListener(button_on_click_listener);
        
        return current_view;
    
    }
    
    private View findViewById(int id)
    {
    	View view = current_view.findViewById(id);
    	
    	if( view == null)
    		view = app_activity.findViewById(id);
    	
    	return view;
    }
    
    private final static int INTENT_ACTION_PICK = 0;
    private final static int INTENT_ACTION_CAMERA = 1;
    private final static int INTENT_ACTION_PICK_WATER = 2;
    private final static int INTENT_ACTION_CAMERA_WATER = 3;
    
    private ArrayList<Integer> error_layout_id = new ArrayList<Integer>();
    private ArrayList<Integer> error_mark_id = new ArrayList<Integer>();
    
    private View.OnClickListener button_on_click_listener = new View.OnClickListener()
    {
    	String email_content= "";
    	
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			if(v.getId() == R.id.browser_button || v.getId() == R.id.browser_button_water )
			{
				
                Intent intent = new Intent( Intent.ACTION_PICK );
                intent.setType( "image/*" );
                Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                
                if( v.getId() == R.id.browser_button  )
                	this_class.startActivityForResult( destIntent, INTENT_ACTION_PICK );
                else
                	this_class.startActivityForResult( destIntent, INTENT_ACTION_PICK_WATER );
			}
			
			if(v.getId() == R.id.capture_picture_button || v.getId() == R.id.capture_picture_button_water )
			{
				//if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 )
				{
					
				Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    
				//ComponentName qq  = intent_camera.resolveActivity(getPackageManager());
				//Log.i("value is ",qq.getPackageName());
				
				if (intent_camera.resolveActivity(app_activity.getPackageManager()) != null) 
			    {
			        // Create the File where the photo should go
			        File temp_photoFile = null;
					
					//Log.i("after value is ",qq.getPackageName());
					
					try 
			        {
						temp_photoFile = createImageFile();
			            
			        } 
			        catch (IOException e) 
			        {
			            e.getStackTrace();
			        
			        }
			        // Continue only if the File was successfully created
			        if (temp_photoFile != null) 
			        {
			        	photoFile = temp_photoFile;
			        	
			        	intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp_photoFile));
			            
			        	if( v.getId() == R.id.capture_picture_button )
			        		this_class.startActivityForResult(intent_camera, INTENT_ACTION_CAMERA);
			        	else
			        		this_class.startActivityForResult(intent_camera, INTENT_ACTION_CAMERA_WATER);
			        	
			        	temp_photoFile = null;
			        }
			    }
				else
					Toast.makeText(app_context, "沒有照相的應用程式", Toast.LENGTH_SHORT).show();
				}
			}
			
			if(v.getId() == R.id.base_data)
			{
				setting = app_context.getSharedPreferences("remember", app_context.MODE_PRIVATE );
				
				apply_user_text_view.setText(setting.getString("apply_user",""));
				tel_area_number_text_view.setText(setting.getString("tel_area_number",""));
				phone_number_text_view.setText(setting.getString("phone_number",""));
				//email_text_view.setText(setting.getString("email",""));
				ext_phone_number_text_view.setText(setting.getString("ext_phone_number",""));
				mobile_text_view.setText(setting.getString("mobile",""));
			}
			
			//if(v.getId() == R.id.send_button)
			if( v.getId() == R.id.title_bar_menu_button )
			{
				String used_address = used_address_text_view.getText().toString();
				String meter_read01 = meter_read01_text_view.getText().toString();
				String meter_serial01 = meter_serial01_text_view.getText().toString();
				String electric_number = ((TextView) findViewById(R.id.electric_number)).getText().toString();
				
				String meter_read01_water = "";
				String meter_serial01_water = "";
				String water_number= "";
				
				if( email_index != no_meter_read ) 
				{
					meter_read01_water = meter_read01_water_text_view.getText().toString();
					meter_serial01_water = meter_serial01_water_text_view.getText().toString();
					water_number = ((TextView) findViewById(R.id.water_number)).getText().toString();
				}
				
				String note_board= ((TextView) findViewById(R.id.note_board)).getText().toString();
				
				apply_user = apply_user_text_view.getText().toString();
				tel_area_number = tel_area_number_text_view.getText().toString();
				phone_number = phone_number_text_view.getText().toString();
				ext_phone_number = ext_phone_number_text_view.getText().toString();
				//email = email_text_view.getText().toString();
				mobile = mobile_text_view.getText().toString();
		        
				String error_message = "";
		        
				if(error_layout_id.size() > 0)
					ASaBuLuCheck.setLayoutBackgroundState(app_activity.findViewById(android.R.id.content),error_layout_id,error_mark_id, false);
				
				error_layout_id.clear();
				error_mark_id.clear();
				
		        if( used_address.length() < 5 || used_address.startsWith("請填寫")  )
		        {	
		        	error_message += "地址錯誤\n";
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.used_address_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.used_address_error_mark));
		        }
		        
		        
		        if( (meter_read01.equals("") && ( browser_picture_location == null ) && ( capture_picture_location == null )) && 
		        	(meter_read01_water.equals("") && ( browser_picture_location_water == null ) && ( capture_picture_location_water == null )))
		        {	
		        	error_message += "電表指數未輸入或沒有照片\n";
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.meter_read01_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.meter_read01_error_mark));
		        	error_layout_id.add(Integer.valueOf(R.id.meter_serial01_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.meter_serial01_error_mark));
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.meter_read01_water_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.meter_read01_water_error_mark));
		        	error_layout_id.add(Integer.valueOf(R.id.meter_serial01_water_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.meter_serial01_water_error_mark));
		        }
		        
		        
		        //if( !ASaBuLuCheck.electricCheckFunction(electric_number) && electric_number.length() > 0)
		        //	error_message += "電號錯誤\n" ;
		        
		        if( apply_user.equals(""))
		        {	
		        	error_message += "申請人錯誤\n" ;
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.apply_user_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.apply_user_error_mark));
		        }
		        
		        /*
		        if(  email.length() < 5 || !email.contains("@"))
		        {	
		        	error_message += "E-mail格式錯誤\n" ;
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.email_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.email_error_mark));
		        }
				*/
		        
		        if( (tel_area_number.length() < 2 || phone_number.length() < 6) ||  mobile.length() < 8)
		        {	
		        	error_message += "電話或手機號碼錯誤\n" ;
		        	
		        	error_layout_id.add(Integer.valueOf(R.id.tel_area_number_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.tel_area_number_error_mark));
		        	error_layout_id.add(Integer.valueOf(R.id.phone_number_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.phone_number_error_mark));
		        	error_layout_id.add(Integer.valueOf(R.id.mobile_layout));
		        	error_mark_id.add(Integer.valueOf(R.id.mobile_error_mark));
		        }
		        
				
				if( error_message.equals(""))
		        {	
					String electric_content = "電表資料 \n" + " 指數: " + meter_read01 + " 號碼: " + meter_serial01 + "電號: " + electric_number +"\n\n" ;
					String water_content = "水表資料 \n" + " 水表指數: " + meter_read01_water + " 水表號碼: " + meter_serial01_water + "水號: " + water_number + "\n\n";
				     
					ArrayList<String> final_bill_email_array_list = new ArrayList<String>();
					final_bill_email_array_list.add("d1170304@taipower.com.tw");
					final_bill_email_array_list.add("7111@mail.water.gov.tw");
					final_bill_email_array_list.add("d1170406@taipower.com.tw");
					//final_bill_email_array_list.add("u463481@taipower.com.tw");
					
					String[] final_bill_email_array = new String[]{"d1170304@taipower.com.tw","7111@mail.water.gov.tw","d1170406@taipower.com.tw"}; 
					
					if( email_index != no_meter_read )
					{
						if( (meter_read01.length() + meter_serial01.length()) == 0 &&  browser_picture_location == null && capture_picture_location == null)
						{	
							electric_content = "";
							final_bill_email_array[0] = "";
							final_bill_email_array[2] = "";
						
							final_bill_email_array_list.remove("d1170304@taipower.com.tw");
						}
					
						if( (meter_read01_water.length() + meter_serial01_water.length()) == 0 &&  browser_picture_location_water == null && capture_picture_location_water == null)
						{	
							water_content = "";
							final_bill_email_array[1] = "";
							final_bill_email_array[2] = "";
						
							final_bill_email_array_list.remove("7111@mail.water.gov.tw");
						}
						
						final_bill_email_array_list.remove("d1170406@taipower.com.tw");
					}
					else
					{	
						water_content = "";
						final_bill_email_array[0] = "";
						final_bill_email_array[1] = "";
						
						final_bill_email_array_list.remove("d1170304@taipower.com.tw");
						final_bill_email_array_list.remove("7111@mail.water.gov.tw");
					}
					
					email_content = "地址: " + city_array[city_selected_index] + distr_array[distr_selected_index] + used_address + "\n\n" + 
								    electric_content + water_content +
							  	    "申請人: " + apply_user + "\n" +
							  	    "連絡電話: " + tel_area_number + "-" + phone_number + "#" + ext_phone_number + "\n" +
							  	    "行動電話: " + mobile + "\n" +
									"備註: "    + note_board;
					
					final String[] email_address = final_bill_email_array_list.toArray(new String[final_bill_email_array_list.size()]);
					
		        	//final String[] email_address = final_bill_email_array;
		        	/*
		        	TextView additional_message0 = new TextView(app_context);
		        	additional_message0.setText("稍後進入E-mail發送介面\n請選擇想使用的E-mail寄送APP\n並點選送出我們才會收到喔!!");
		        	additional_message0.setTextSize(20.0f);
		        	additional_message0.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		        	
		        	TextView additional_message1 = new TextView(app_context);
		        	additional_message1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		        	additional_message1.setTextSize(20.0f);
		        	additional_message1.setText("\n繳費重要提醒！！\n電費：請至台電各服務處臨櫃繳納\n聯絡電話：(02)29915511#383\n\n水費：請至台水新莊服務所臨櫃繳納\n聯絡電話：(02)29968961#102");
		        	additional_message1.setTextColor(ColorStateList.valueOf(0xFFFF0000));
		        	
		        	LinearLayout additional_layout = new LinearLayout(app_context);
		        	additional_layout.addView(additional_message0);
		        	additional_layout.setOrientation(LinearLayout.VERTICAL);
		        	
		        	
		        	if(email_index != no_meter_read)
		        		additional_layout.addView(additional_message1);
		        	*/
		        	LinearLayout alert_layout = new LinearLayout(app_context);
		        	alert_layout.setBackgroundResource(R.drawable.send_mail_alert_android);
		        	alert_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		        	alert_layout.setOrientation(LinearLayout.VERTICAL);
		        	
		        	
		        	final Dialog alert_dialog = new Dialog(app_context);
		        	
		        	LinearLayout alert_layout_new = new LinearLayout(app_context);
		        	alert_layout_new.setBackgroundResource(R.drawable.send_mail_alert_android);
		        	alert_layout_new.setOrientation(LinearLayout.VERTICAL);
		        	alert_layout_new.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		        	alert_layout_new.setOnClickListener(new View.OnClickListener() 
		        	{	
						@Override
						public void onClick(View v) 
						{
							// TODO Auto-generated method stub
							alert_dialog.dismiss();
	        				//一般寄送E-mail
        		        	Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);   
        		        	
        		        	//email address must be String[];    
        		        	intent.putExtra(Intent.EXTRA_EMAIL, email_address); 
        		        	
        		        	String[] email_subject = {"中抄計算","電表抄表不在補抄"};
        		        	intent.putExtra(Intent.EXTRA_SUBJECT, email_subject[email_index]);   
        		        	//it.setType("message/rfc822");   
        		                		               		        
        		        	//Email內容
        		        	intent.putExtra(Intent.EXTRA_TEXT, email_content); 
        		        	
        		        	String[] attach_file = new String[]{ browser_picture_location, capture_picture_location, browser_picture_location_water, capture_picture_location_water};
        		        	
        		        	//附件  attach file path must used Uri.parse after android version 3.0.0
        		        	//multiple attach must use arrayList Uri 
        		        	ArrayList<Uri> extra_stream_file = new ArrayList<Uri>();
        		        	
        		        	for(String qq : attach_file)
        		        	{
        		        		if(qq != null)
        		        			extra_stream_file.add(Uri.parse(qq));
        		        	}
        		        	
        		        	intent.putExtra(Intent.EXTRA_STREAM, extra_stream_file );
        		        	intent.setType("image/*"); 
        		        	startActivity(Intent.createChooser(intent, "選擇電子郵件客戶端"));
        		        	
        		        	for(String qq : attach_file)	
        		        		qq = null;
						}
					});
		        	
		        	
		        	alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		        	alert_dialog.setContentView(alert_layout_new);
		        	alert_dialog.show();
		        	/*
		        	AlertDialog.Builder send_notic = new AlertDialog.Builder(app_context);
		        	//send_notic.setTitle("小提醒");
		        	//send_notic.setMessage("稍後進入E-mail發送介面\n請選擇想使用的E-mail寄送APP\n並點選送出我們才會收到喔!!");
		        	//send_notic.setView(additional_layout);
		        	send_notic.setView(alert_layout);
		        	send_notic.setNegativeButton("確定", new OnClickListener()
		        	{
		        		@Override	
	        			public void onClick(DialogInterface dialog , int which) 
        	            {
	        				dialog.dismiss();
	        				//一般寄送E-mail
        		        	Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);   
        		        	
        		        	//email address must be String[];    
        		        	intent.putExtra(Intent.EXTRA_EMAIL, email_address); 
        		        	
        		        	String[] email_subject = {"中抄計算","電表抄表不在補抄"};
        		        	intent.putExtra(Intent.EXTRA_SUBJECT, email_subject[email_index]);   
        		        	//it.setType("message/rfc822");   
        		                		               		        
        		        	//Email內容
        		        	intent.putExtra(Intent.EXTRA_TEXT, email_content); 
        		        	
        		        	String[] attach_file = new String[]{ browser_picture_location, capture_picture_location, browser_picture_location_water, capture_picture_location_water};
        		        	
        		        	//附件  attach file path must used Uri.parse after android version 3.0.0
        		        	//multiple attach must use arrayList Uri 
        		        	ArrayList<Uri> extra_stream_file = new ArrayList<Uri>();
        		        	
        		        	for(String qq : attach_file)
        		        	{
        		        		if(qq != null)
        		        			extra_stream_file.add(Uri.parse(qq));
        		        	}
        		        	
        		        	intent.putExtra(Intent.EXTRA_STREAM, extra_stream_file );
        		        	intent.setType("image/*"); 
        		        	startActivity(Intent.createChooser(intent, "選擇電子郵件客戶端"));
        		        	
        		        	for(String qq : attach_file)	
        		        		qq = null;
        		        	 	
        	            }
		        	});
		        	
		        	send_notic.show();
		        	*/
		        	
		        	
		        	}
		        	else
		        	{
		        		ASaBuLuCheck.setLayoutBackgroundState(app_activity.findViewById(android.R.id.content),error_layout_id, error_mark_id ,true);
		        		
		        		AlertDialog.Builder error_dialog = new AlertDialog.Builder(app_context);
		        		error_dialog.setTitle("輸入資料錯誤");
		        		error_dialog.setMessage(error_message);
		        		error_dialog.setNegativeButton("重新輸入",null);
		        		error_dialog.show();
		        	}
			}
		}
    };
    
	
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data)
    {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	 // requestCode 0  上傳檔案
        if(requestCode == INTENT_ACTION_PICK || requestCode == INTENT_ACTION_PICK_WATER )
        { 
        	if ( resultCode == Activity.RESULT_OK )
        	{
        		// 取得檔案的 Uri
        		Uri uri;
        		
        		if( ( uri = data.getData() ) != null )
        		{	
        			if( requestCode == INTENT_ACTION_PICK )
        				browser_picture_location = getRealPathFromURI(uri);
        			else
        				browser_picture_location_water = getRealPathFromURI(uri);
        		}
        		else
        			Toast.makeText(app_context, "您選擇無效的檔案路徑 !!", Toast.LENGTH_LONG).show();
        	}
        	else	
        		Toast.makeText(app_context, "取消選擇檔案 !!", Toast.LENGTH_LONG).show();
        	
        }
        
        // requestCode 1 拍照
        if(requestCode == INTENT_ACTION_CAMERA || requestCode == INTENT_ACTION_CAMERA_WATER )
        { 
        	if (resultCode == Activity.RESULT_OK)
        	{
        		//取出拍照後回傳資料
        	    //Bundle extras = data.getExtras();
        	    //將資料轉換為圖像格式
        	    //Bitmap bmp = (Bitmap) extras.get("data");  //bmp size is 4mb   too large
        		
        	    
        	    // CALL THIS METHOD TO GET THE URI FROM THE thumbnail BITMAP
                //Uri camera_uri_path = getImageUri(app_context.getApplicationContext(), bmp);
                
        		if( requestCode == INTENT_ACTION_CAMERA)
        			capture_picture_location = getRealPathFromURI(Uri.fromFile(photoFile));
        		else
        			capture_picture_location_water = getRealPathFromURI(Uri.fromFile(photoFile));
        	}
        	else	
        		Toast.makeText(app_context, "取消拍照 !!", Toast.LENGTH_LONG).show();
        	
        }    
    
        this.resumeImage();
    }
    
    
    
    
    
    
    // get thumbnail content uri
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    
    
    // get resize content
    public Bitmap getImageResize(File in_image_file) 
    {
    	Bitmap image = null;
    	
    	InputStream is = null;
		
    	try 
		{
			is = new FileInputStream(in_image_file.getAbsolutePath().replace("/file:", ""));
		
			final BitmapFactory.Options options = new BitmapFactory.Options();  
			options.inJustDecodeBounds = true;  
			// Calculate inSampleSize  
			//options.inSampleSize = calculateInSampleSize(options, 30, 50);  
			options.inSampleSize = 6;
			// Decode bitmap with inSampleSize set  
			options.inJustDecodeBounds = false;  
			
			image = BitmapFactory.decodeStream(is , null, options);
    	
			is.close();
			is = null;
        
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    	}
        
        return image;
    }
    
    
    // 
    public String getRealPathFromURI(Uri contentURI) 
    {
        String result;
        Cursor cursor = app_context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) 
        { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        }
        else 
        { 
            cursor.moveToFirst(); 
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
            result = cursor.getString(idx);
            cursor.close();
        }
        
        //if(result.startsWith("/storage"))
        //	result = "/mnt" + result.substring(8);
        return ("file://" + result);
    }
    
    
    
    public String mCurrentPhotoPath = null;

    private File createImageFile() throws IOException 
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.TAIWAN).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        
        File download_file_dir;
        
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
    		download_file_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/taipower/");
    	else
    	{	
    		//download_file_dir = Environment.getDownloadCacheDirectory(); 
        	download_file_dir = new File(Environment.DIRECTORY_DOWNLOADS + "/taipower/" );
    	}
        
        download_file_dir.mkdir();
        
        
        File image = File.createTempFile( imageFileName, ".jpg", download_file_dir);
        Log.i("QQQ","QQQ");
        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
           
        return image;
    }
    
    
    
    private void resumeImage()
    {
    	
    	if( browser_picture_location != null)
    	{
    		ImageView browser_preview = (ImageView) findViewById(R.id.preview_browser_picture);
    		browser_preview.setImageBitmap(this.getImageResize(new File(browser_picture_location)));
    	}
    	
    	
    	if( capture_picture_location != null)
    	{
    		ImageView capture_preview = (ImageView) findViewById(R.id.preview_capture_picture);
    		capture_preview.setImageBitmap(this.getImageResize(new File(capture_picture_location)));
    	}
    	
    	
    	if( browser_picture_location_water != null)
    	{
    		ImageView browser_preview = (ImageView) findViewById(R.id.preview_browser_picture_water);
    		browser_preview.setImageBitmap(this.getImageResize(new File(browser_picture_location_water)));
    	}
    	
    	
    	if( capture_picture_location_water != null)
    	{
    		ImageView capture_preview = (ImageView) findViewById(R.id.preview_capture_picture_water);
    		capture_preview.setImageBitmap(this.getImageResize(new File(capture_picture_location_water)));
    	}
    }
    
    
    private OnItemSelectedListener on_item_selected_listener = new OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
		{
			// TODO Auto-generated method stub
			
    		if( parent.getId() == R.id.spinner_city )
			{
				//city_selected_index = (int ) id;
    			city_selected_index = position;
				
				distr_array = resource.getStringArray(R.array.online_service_distr00 + city_selected_index);
			    zip_array = resource.getStringArray(R.array.online_service_zip00 + city_selected_index);
				
				ArrayAdapter adapter_distr = new ArrayAdapter(app_context,android.R.layout.simple_spinner_item, distr_array);
				adapter_distr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        spinner_distr.setAdapter(adapter_distr);
			
			}
			
			if( parent.getId() == R.id.spinner_distr )
			{
				distr_selected_index = position;		
			
				String[] select_filter = resource.getStringArray(street_hash_map.get(zip_array[distr_selected_index]).intValue());
		        
		        ArrayAdapter<String> select_filter_adapter = new ArrayAdapter<String>(app_context, android.R.layout.simple_dropdown_item_1line,select_filter);
		        used_address_text_view.setAdapter(select_filter_adapter);
			}
		}
        
        public void onNothingSelected(AdapterView arg0) 
        {
            //Toast.makeText(button01.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
        }
    };
    
    

    @Override
	public void onPause()
    {
    	super.onPause();
    	
    	((ImageView) findViewById(R.id.preview_browser_picture)).setImageBitmap(null);;
		((ImageView) findViewById(R.id.preview_capture_picture)).setImageBitmap(null);
		
		if( email_index != 1)
		{
			((ImageView) findViewById(R.id.preview_browser_picture_water)).setImageBitmap(null);
			((ImageView) findViewById(R.id.preview_capture_picture_water)).setImageBitmap(null);
		}
		
		
    	System.gc();
    }
    
    
    
    @Override
	public void onResume()
    {
    	super.onResume();
    	
    	this.resumeImage();
    	
    }
    
    
}