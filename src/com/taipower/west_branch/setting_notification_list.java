package com.taipower.west_branch;


import java.util.ArrayList;

import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class setting_notification_list extends Activity
{
	
	
	//DB_on_SQLite_for_barcode SQLite_for_barcode = new DB_on_SQLite_for_barcode(this, "taipower_west_branch_app_data_base.db",null,1);
	//DB_on_SQLite SQLite_for_barcode = new DB_on_SQLite(this, "taipower_west_branch_app_data_base.db",null,1);
	DBonSQLite SQLite_for_notification = new DBonSQLite(this, "taipower_west_branch_app_data_base.db",null,1);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		new NoTitleBar(android.os.Build.VERSION.SDK_INT, this, this);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_noticfication_list);
		
        	
        int linear_height = new DmInfor(this,this).menu_linear_height;
         
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  linear_height ));
        
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        
        back_button.setOnClickListener(
        		new View.OnClickListener() 
        		{
        			public void onClick(View v) 
        			{
        				SQLite_for_notification.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
        				finish();
        			}
        		}
        );
        
        ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(new DmInfor(this,this).menu_linear_height, LayoutParams.MATCH_PARENT));
        
        
              
        SQLiteDatabase db_read  = SQLite_for_notification.getReadableDatabase();
        
        String[] select_column = new String[]{"_ID INTEGER ","title","content","time"};
        
        Cursor cursor = db_read.query("notification_message_table", select_column , null, null, null, null, null);
        
        //取得資料表列數
        int result_rows_num = cursor.getCount();
        //用陣列存資料
        //String[][] db_result = new String[rows_num][8];
        ArrayList<String> notific_result = new ArrayList<String>();
        
        
        if(result_rows_num != 0) 
        {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	
        	for(int i = 0 ; i < result_rows_num ; i++ ) 
        	{
        		String temp_result = "";      
        		
        		//skip _ID column
        		//for(int j = 1 ; j < select_column.length ; j ++)
        		{	
        			temp_result += "通知標題: ";
        			temp_result += cursor.getString(1) + "\n\n";
        			
        			temp_result += "通知內容:\n";
        			temp_result += cursor.getString(2) + "\n\n";
        			
        			temp_result += "收到時間: ";
        			temp_result += cursor.getString(3);
        		
        		}
        		
        		notific_result.add(temp_result);
        		
        		cursor.moveToNext();//將指標移至下一筆資料
        	}
        
        	cursor.close(); //關閉Cursor
        	//SQLite.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
        	
        	
        	
        	ArrayAdapter<String> notification_adapter = new ArrayAdapter<String>( this ,
        														  android.R.layout.simple_list_item_1,
        														  notific_result);
        	
        	
        	
        	ListView noti_list = (ListView) findViewById(R.id.result_list_view);
        	
        	noti_list.setAdapter(notification_adapter);
        	
        	
        	
        	
    
        }
	}
	
	
	
	
	
	
	
} 