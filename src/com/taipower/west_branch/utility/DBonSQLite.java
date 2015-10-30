package com.taipower.west_branch.utility;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBonSQLite extends SQLiteOpenHelper  {
	
	private static String data_base_name = "taipower_west_branch_app_data_base.db";
	private static int data_base_version = 1;
	
	private static String[] table_name = {"remind_table",
										  "barcode_be_created_table",
										  "notification_message_table",
										  "ebpps_electric_number_list_table"};
	
		
	private static String table0_columns = "create table if not exists " + table_name[0] + " (" 
			 									   + "_ID INTEGER PRIMARY KEY,"
			 									   + "contract_id TEXT,"
			 									   + "bill_cycle TEXT,"
			 									   + "bill_kind TEXT,"
			 									   + "electric_number0 TEXT,"
			 									   + "electric_number1 TEXT,"
			 									   + "electric_number2 TEXT," 
			 									   + "electric_number3 TEXT)";
			 									   //+ "hour TEXT,"
												   //+ "minute TEXT)";
	
	private static String table1_columns = "create table if not exists " + table_name[1] + " (" 
			 									   + "_ID INTEGER PRIMARY KEY,"
			 									   + "electric_number TEXT,"
			 									   + "pay_money TEXT,"
			 									   + "time TEXT,"
			 									   + "picture_uri TEXT,"
												   + "origin_from TEXT,"
			 									   + "reserved0 TEXT,"
			 									   + "reserved1 TEXT,"
			 									   + "reserved2 TEXT,"
												   + "reserved3 TEXT)";
												   //+ "hour TEXT,"
												   //+ "minute TEXT)";
	
	private static String table2_columns = "create table if not exists " + table_name[2] + " (" 
			   									   + "_ID INTEGER PRIMARY KEY,"
			   									   + "title TEXT,"
			   									   + "content TEXT,"
			   									   + "time TEXT,"
			   									   + "reserved0 TEXT,"
			   									   + "reserved1 TEXT,"
			   									   + "reserved2 TEXT,"
			   									   + "reserved3 TEXT)";
												   //+ "hour TEXT,"
												   //+ "minute TEXT)";
	
	
	private static String table3_columns = "create table if not exists " + table_name[3] + " (" 
			   										+ "_ID INTEGER PRIMARY KEY,"
			   										+ "electric_number TEXT,"
			   										+ "print_state TEXT,"
			   										+ "reserved0 TEXT,"
			   										+ "reserved1 TEXT,"
			   										+ "reserved2 TEXT,"
			   										+ "reserved3 TEXT)";
			 
	
	//constructor
	public DBonSQLite(Context context, String name, CursorFactory factory, int version) 
	{
		super(context, data_base_name, factory, data_base_version);
		// TODO Auto-generated constructor stub
	}
	
	public DBonSQLite(Context context, String name, int version) 
	{
		this(context, data_base_name, null, data_base_version);
	}
	
	public DBonSQLite(Context context, String name ) 
	{
		this(context, data_base_name, null, data_base_version);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase data_base) 
	{
		//TODO Auto-generated method stub
		String[] SQL_command = {table0_columns,table1_columns,table2_columns,table3_columns}; 
						
		//SQL command
		for(String qq : SQL_command)
			data_base.execSQL(qq);
		
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase data_base, int oldVersion, int newVersion) 
	{
		
		String SQL_command = "DROP TABLE IF EXISTS " + table_name[0];
		
		data_base.execSQL(SQL_command); 
		onCreate(data_base);
	
	}
	
	//customize_function
	public void onUpgradeCustom(SQLiteDatabase data_base, int oldVersion, int newVersion , int upgrade_table) 
	{
		String SQL_command = "";
		
		//data_base.query(table, values, whereClause, whereArgs);
	}
	
	
	@Override   
	public void onOpen(SQLiteDatabase data_base) 
	{    
		super.onOpen(data_base);      
	    // TODO 每次成功打開數據庫後首先被執行   
		this.onCreate(data_base);
		
		
	}
	 
	@Override
	public synchronized void close() 
	{
		super.close();
		
	}

	
	

}
