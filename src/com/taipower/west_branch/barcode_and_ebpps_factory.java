package com.taipower.west_branch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.ASaBuLuCheck;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class barcode_and_ebpps_factory extends Activity 
{
	private Context app_context;
	private Activity app_activity;
	
	public barcode_and_ebpps_factory()
	{
		app_context = this;
		app_activity = this;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
		 
		super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_and_ebpps_factory);
        
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  new DmInfor(this,this).menu_linear_height ));
        	
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        
        back_button.setOnClickListener(
        		new View.OnClickListener() 
        		{
        			@Override
        			public void onClick(View v) 
        			{
        				finish();
        			}
        		}
        );
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
        
        Button create_barcode = (Button) findViewById(R.id.create_barcode);
        
        create_barcode.setOnClickListener(new View.OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
		
        
        
        
        TextView electric_number = (TextView) findViewById(R.id.electric_number);
        TextView pay_money = (TextView) findViewById(R.id.pay_money);
        TextView check_code = (TextView) findViewById(R.id.check_code);
        TextView unpay_year = (TextView) findViewById(R.id.unpay_year);
        TextView unpay_month = (TextView) findViewById(R.id.unpay_month);
        TextView unpay_date = (TextView) findViewById(R.id.unpay_date);
        
        String yyymmdd = unpay_year.getText().toString() ;
        yyymmdd += (unpay_month.getText().toString().length() < 2)?  "0" + unpay_month.getText().toString() : unpay_month.getText().toString() ;
        yyymmdd += (unpay_date.getText().toString().length() < 2)? "0" + unpay_date.getText().toString() : unpay_date.getText().toString() ;
        
        String error_message = "";
        
        new ASaBuLuCheck();
		if( electric_number.getText().toString().isEmpty() || !ASaBuLuCheck.electricCheckFunction(electric_number.getText().toString()))
        	error_message += "電號錯誤!!\n";
        
        if( pay_money.getText().toString().isEmpty())
        	error_message += "繳交金額未填!!\n";
        	
        if( check_code.getText().toString().isEmpty() || !check_code_check( electric_number.getText().toString(),
        																    pay_money.getText().toString() ,
        																    check_code.getText().toString().toUpperCase(Locale.ENGLISH), 
        																    yyymmdd ) ) 
        																   
        	error_message += "檢查碼未填或資料錯誤!!\n";
        
        if( ! error_message.equals(""))
        {
        	AlertDialog.Builder error_dialog = new AlertDialog.Builder(app_context);
        	error_dialog.setTitle("資料錯誤");
        	error_dialog.setMessage(error_message);
        	error_dialog.setNegativeButton("重新填寫",null);
        	error_dialog.show();
        }
        else
        {
        	
			
			String payment_string = pay_money.getText().toString();
			String payment_money_format = "";
			
			for(int i = payment_string.length() ; i < 9 ; i++  )
				payment_money_format += "0";
			
			payment_money_format += payment_string;
			
			Calendar calendar_data = Calendar.getInstance(); 
			
			//add 1 date
			calendar_data.add(Calendar.DATE, 1);
			
			
			int year = calendar_data.get(Calendar.YEAR) - 2011; //去百位
			int month = calendar_data.get(Calendar.MONTH) + 1;
			int date = calendar_data.get(Calendar.DATE) ;
			
			String ROC_date0 = (year < 10) ? ( "0" + String.valueOf(year) ) : String.valueOf(year) ;
			ROC_date0 += (month < 10) ? ( "0" + String.valueOf(month) ) : String.valueOf(month) ;
			ROC_date0 += (date < 10) ? ( "0" + String.valueOf(date) ) : String.valueOf(date) ;
			
			
			String ROC_date1 = "";
			String origin_from = "";
			boolean code_base1_boolean = hash_code_base1.contains(check_code.getText().toString().toUpperCase(Locale.ENGLISH).substring(0,1) );
			
			if(code_base1_boolean)
			{
				ROC_date1 = yyymmdd.substring(1,7);
				origin_from = "unpay_by_barcode_factory";
			}
			else
			{
			Calendar calendar_data_now = Calendar.getInstance(); 
			
			int year1 = calendar_data_now.get(Calendar.YEAR) - 2011; //去百位
			int month1 = calendar_data_now.get(Calendar.MONTH) + 1;
			int date1 = calendar_data_now.get(Calendar.DATE) ;
			
			
			ROC_date1 = (year < 10) ? ( "0" + String.valueOf(year1) ) : String.valueOf(year1) ;
			ROC_date1 += (month < 10) ? ( "0" + String.valueOf(month1) ) : String.valueOf(month1) ;
			ROC_date1 += (date1 < 10) ? ( "0" + String.valueOf(date1) ) : String.valueOf(date1) ;
			
				origin_from = "barcode_factory";
        	}
			
			
			
			
			Bundle barcode_data = new Bundle();
			barcode_data.putString("electric_number",electric_number.getText().toString() );
			//收費日
			barcode_data.putString("ROC_date1", ROC_date1);
			//錢錢
			barcode_data.putString("payment_money_format",payment_money_format );
			//代收期限
			barcode_data.putString("ROC_date0",ROC_date0);
			//origin
			barcode_data.putString("origin_from",origin_from);
			
			
			Intent intent = new Intent();
			intent.putExtras(barcode_data);
			intent.setClass(barcode_and_ebpps_factory.this, barcode_and_ebpps_barcode.class);
			startActivity(intent);
        
			app_activity.finish();
        }
    
			}
		});
	
	}  
    
	private static final String hash_code_base0 = "BCADFGHJKILNOMPQ"; 
	private static final String hash_code_base1 = "STRUWXVTE1Z24536"; 
    
	private static boolean check_code_check(String electric_number, String pay_money, String check_code, String yyymmdd)
	{
		if( electric_number.length() < 11)
			return false;
				
		String temp_elect = electric_number.substring(7, 11);
		String temp_pay_money = "";
		String res_pay_ment = "";
		String hash_code = "";
		
		if( hash_code_base0.contains(check_code.substring(0, 1)) )
			hash_code = hash_code_base0;
		else
			hash_code = hash_code_base1;
		
		
		for(int i = 0 ; i < (4-pay_money.length()) ; i++)
			temp_pay_money += "0" ;  
		
		temp_pay_money += pay_money;
		
		for(int i = 4 ; i > 0 ; i-- )
			res_pay_ment += temp_pay_money.substring(i-1,i);
		
		int temp ;
		
		if( hash_code.equals(hash_code_base0) )
			temp =  Integer.valueOf(temp_elect) * Integer.valueOf(res_pay_ment);
		else	
			temp =  Integer.valueOf(temp_elect) * Integer.valueOf(res_pay_ment) + Integer.valueOf(yyymmdd);
		
		String temp_hex = Integer.toHexString(temp).toUpperCase(Locale.ENGLISH);
		
		temp_hex = temp_hex.substring(temp_hex.length() - 4 );
		
		String result_string = "";
		
		for(int i = 0 ; i <temp_hex.length() ; i ++ )
		{
			char temp_char = temp_hex.charAt(i);
			int result_char = 0;			
			
			if( temp_char > '@' )
				result_char = (temp_char - 0x37);
			else
				result_char = (temp_char - 0x30);
			
			result_string += hash_code.substring(result_char,result_char+1);
		}
		
		if(result_string.equals(check_code))
			return true;
		
		return false;
	}
	
    
}