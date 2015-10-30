package com.taipower.west_branch;


import java.util.ArrayList;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class barcode_and_ebpps_be_created_list extends Activity
{
	private Context app_context;
	
	//DB_on_SQLite_for_barcode SQLite_for_barcode = new DB_on_SQLite_for_barcode(this, "taipower_west_branch_app_data_base.db",null,1);
	DBonSQLite SQLite_for_barcode = new DBonSQLite(this, "taipower_west_branch_app_data_base.db",null,1);
		
	
	
	public barcode_and_ebpps_be_created_list()
	{
		this.app_context = this;
		
	
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, this, this);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_and_ebpps_be_created_list);
		
        	
        int linear_height = new DmInfor(this,this).menu_linear_height;
         
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  linear_height ));
        
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        
        back_button.setOnClickListener(
        		new View.OnClickListener() 
        		{
        			public void onClick(View v) 
        			{
        				SQLite_for_barcode.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
        				finish();
        			}
        		}
        );
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
        
        
        
        
      			
        SQLiteDatabase db_read  = SQLite_for_barcode.getReadableDatabase();
        
        String[] select_column = {"_ID INTEGER ","electric_number","pay_money","time","picture_uri","origin_from"};
        
        Cursor cursor = db_read.query("barcode_be_created_table", select_column , null, null, null, null, null);
        
        //取得資料表列數
        int result_rows_num = cursor.getCount();
        //用陣列存資料
        
        ArrayList barcode_created_result = new ArrayList();
        
        
        if(result_rows_num != 0) 
        {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	
        	for(int i = 0 ; i < result_rows_num ; i++ ) 
        	{
        		String[] temp_result = new String[5];     
        		
        		//skip _ID column
        		for(int j = 1 ; j < select_column.length ; j ++)
        			temp_result[j-1] = cursor.getString(j);
        		/*	
        		barcode_created_result.add(   temp_result[0] + " " 
        									+ Integer.valueOf(temp_result[1]) + " " 
        									+ temp_result[2]);
        		*/
        		barcode_created_result.add(temp_result);
        		
        		cursor.moveToNext();//將指標移至下一筆資料
        	}
        
        	cursor.close(); //關閉Cursor
        	db_read.close();
        	//SQLite.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
        	
        	
        	
        	//LinearLayout linear = new LinearLayout(app_context);
    		
        	LinearLayout linear = (LinearLayout) findViewById(R.id.barcode_created_list);
        	linear.setOrientation(LinearLayout.VERTICAL);
        	linear.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );
        	
        	
        	if( result_rows_num > 0)
    		{
        		String[] barcode_created_list;
        		
        		
        	for(int i = 0 ; i < result_rows_num ; i++)
        	{
        		LinearLayout all_content = new LinearLayout(app_context); 
        		all_content.setOrientation(LinearLayout.HORIZONTAL);
        		all_content.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );
        		
        		LinearLayout content_part0 = new LinearLayout(app_context);
        		content_part0.setOrientation(LinearLayout.VERTICAL);
        		content_part0.setGravity(Gravity.CENTER);
        		content_part0.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, (float) 1.0 ) );
        		
        		LinearLayout content_part1 = new LinearLayout(app_context);
        		content_part1.setOrientation(LinearLayout.VERTICAL);
        		content_part1.setGravity(Gravity.CENTER);
        		content_part1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, (float) 1.0 ) );
        		
        		LinearLayout content_part2 = new LinearLayout(app_context);
        		content_part2.setOrientation(LinearLayout.VERTICAL);
        		content_part2.setGravity(Gravity.CENTER);
        		content_part2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, (float) 1.0 ) );
        		
        		
        		TextView[] add_text_view = new TextView[4];
        		
        		add_text_view[0] = new TextView(app_context);
        		add_text_view[1] = new TextView(app_context);
        		add_text_view[2] = new TextView(app_context);
        		add_text_view[3] = new TextView(app_context);
        		
        		barcode_created_list = null; 
        		
        		barcode_created_list = (String[]) barcode_created_result.get(i);
        		
        		for( TextView qq : add_text_view)
        		{	
        			qq.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
        			qq.setGravity( Gravity.LEFT & Gravity.CENTER_HORIZONTAL ); 
        		}
        		
        		
        		add_text_view[0].setText( barcode_created_list[0].substring(0,2) + "-" +
        								  barcode_created_list[0].substring(2,4) + "-" +
        								  barcode_created_list[0].substring(4,8) + "-" +
        								  barcode_created_list[0].substring(8,10) + "-" +
        								  barcode_created_list[0].substring(10,11));
        		
        		add_text_view[1].setText(barcode_created_list[2].toString());
        								  
        		add_text_view[0].setTextSize((float) 13.0);
        		add_text_view[1].setTextSize((float) 13.0);
        		
        		content_part0.addView(add_text_view[0]);
        		content_part0.addView(add_text_view[1]);
        		
        		
        		add_text_view[2].setText( "$" + Integer.valueOf(barcode_created_list[1]).toString() );
        		
        		String origin_from = "";
        		
        		if( barcode_created_list[4].equals("ebpps") ) 
        			origin_from = "電子帳單" ;
        		else if(barcode_created_list[4].equals("unpay_by_barcode_factory") )
        			origin_from = "定期抄表";
        		else
        			origin_from = "中抄計算";
        		
        			
        		add_text_view[3].setText( origin_from );
        		
        		content_part1.addView(add_text_view[2]);
        		content_part1.addView(add_text_view[3]);
        		
        		
        		ImageButton picture_button = new ImageButton(app_context);
        		
        		picture_button.setBackgroundResource(R.drawable.ic_receipt_onclick);
        		picture_button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, (float) 1.0 ));
        		picture_button.setTag( barcode_created_list[3] );
        		picture_button.setOnClickListener( on_click_listener );
        		
        		content_part2.addView(picture_button);
        		
        		LinearLayout gap_line = new LinearLayout(app_context);
        		gap_line.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 3 ) ) ;
        		gap_line.setBackgroundColor(0xFFBCBCBC);
        		
        		all_content.addView(content_part0);
        		all_content.addView(content_part1);
        		all_content.addView(content_part2);
        		
        		linear.addView(gap_line);
        		linear.addView(all_content);
        		
        	}
        	
    		}
        	else
        		linear.addView(null);
        	
        	
        	/*
        	ArrayAdapter adapter = new ArrayAdapter<String>( this, R.layout.list_view, barcode_created_result);
            
    		ListView listView = (ListView)findViewById(android.R.id.list);
    		    		
    		listView.setAdapter(adapter);
        	 */
        }
	}
	
	private OnClickListener on_click_listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v.getTag().equals(""))
			{
				AlertDialog.Builder no_picture_dialog = new AlertDialog.Builder(barcode_and_ebpps_be_created_list.this);
				no_picture_dialog.setTitle("沒有照片");
				no_picture_dialog.setMessage("當時沒有拍照喔!!\n下次記得要拍以保障自身權益");
				no_picture_dialog.setPositiveButton(android.R.string.ok , null);
				no_picture_dialog.show();
			}
			else
			{
				Uri uri = Uri.parse( String.valueOf( v.getTag() ) );
						
				Intent new_intent = new Intent(Intent.ACTION_VIEW);
				new_intent.setDataAndType(uri , "image/*");
				Intent process_intent = Intent.createChooser(new_intent,"選擇開啟應用程式");
				startActivity( process_intent);
			}		
		}
	};
	
	
	
	
	
	
	
} 