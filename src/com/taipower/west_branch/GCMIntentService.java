/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taipower.west_branch;

import static com.taipower.west_branch.CommonUtilities.SENDER_ID;
import static com.taipower.west_branch.CommonUtilities.displayMessage;

import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.taipower.west_branch.R;
import com.taipower.west_branch.R.drawable;
import com.taipower.west_branch.R.string;
import com.taipower.west_branch.utility.DBonSQLite;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService 
{

    
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() 
    {
        super(SENDER_ID);
    }
    
    @Override
    protected void onRegistered(Context context, String registrationId )
    {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(com.taipower_west_branch_project.R.string.gcm_registered));
        //ServerUtilities.register(context, registrationId , gcm_service.electric_number , gcm_service.email , gcm_service.phone_number);
        ServerUtilities.register(context, registrationId , SettingDialog.GCM_electric_number_list , SettingDialog.GCM_email , SettingDialog.GCM_phone_number,SettingDialog.GCM_mobile);
    
    }
	
    @Override
    protected void onUnregistered(Context context, String registrationId) 
    {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(com.taipower_west_branch_project.R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) 
        {
            ServerUtilities.unregister(context, registrationId);
        } 
        else 
        {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }
	
    
    @Override
    protected void onMessage(Context context, Intent intent) 
    {
        Log.i(TAG, "Received message");
        //String message = getString(com.taipower_west_branch_project.R.string.gcm_message);
        //displayMessage(context, message);
        // notifies user
        //generateNotification(context, message);
        
        Bundle bData = intent.getExtras();
        
        // 處理 bData 內含的訊息
        
        //String message = bData.getString("electric_number");
        //String title = bData.getString("title");
        //String description = bData.getString("description");
        //...
        //...
        // 通知 user
        generateNotification_new(context, bData);
    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(com.taipower.west_branch.R.string.gcm_deleted, total);
        displayMessage(context, message);
        
        // notifies user
        //generateNotification(context, message);
        Bundle bd_data = new Bundle();
        bd_data.putString("title", "255");
        
        generateNotification_new(context, bd_data);
    }

    @Override
    public void onError(Context context, String errorId)
    {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(com.taipower.west_branch.R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(com.taipower.west_branch.R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    
    /*
    private static void generateNotification(Context context, String message)
    {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(com.taipower_west_branch_project.R.string.app_name);
        
        Intent notificationIntent = new Intent(context, com.taipower_west_branch_project.gcm_service.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
    */
    
    
    
	

	@SuppressLint("NewApi")
	private static void generateNotification_new(Context context, Bundle data)
    {
        
		SharedPreferences setting = context.getSharedPreferences("remember", Context.MODE_PRIVATE );
		boolean show_notification = setting.getBoolean("show_notification", true);
		
		
		String title = data.getString("title"); 
        String electric_number = data.getString("electric_number","");
        String message = data.getString("message","");
        		
        HashMap<String,String> title_map = new HashMap<String, String>();
    	
    	title_map.put("0", "啟動通知服務");
    	title_map.put("1", "工作停電停電通知");
    	title_map.put("2", "事故停電停電通知");
    	title_map.put("3", "分區輪流限電通知");
    	title_map.put("4", "報竣補件線補費繳交通知");
    	title_map.put("5", "案件完成通知");
    	title_map.put("6", "電費發行通知");
    	title_map.put("7", "電費已繳通知");
    	title_map.put("8", "電費未繳通知");
    	title_map.put("255", "取消通知服務");
        
    	
    	if(	title != null )
        {
    		//int icon = R.drawable.ic_launcher;
    		long when = System.currentTimeMillis();
    		
    		Intent intent = new Intent(context, setting_notification_list.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		
    		PendingIntent appIntent = PendingIntent.getActivity(context, (int)(when % 256) , intent , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT );
    		
    		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		
    		if( !electric_number.equals("") )
    			electric_number = "電號:" + electric_number;
    		
    		message = electric_number + " " + message;
        
    		Calendar calendar_data = Calendar.getInstance(); 
    		int year = calendar_data.get(Calendar.YEAR) - 1911;
    		int month = calendar_data.get(Calendar.MONTH) + 1;
    		int date = calendar_data.get(Calendar.DATE) ;
    		int hour = calendar_data.get(Calendar.HOUR);
    		int minute = calendar_data.get(Calendar.MINUTE);
    		
    		DBonSQLite SQLite_for_notification = new DBonSQLite( context , "taipower_west_branch_app_data_base.db",null,1);
    		
    		SQLiteDatabase db_write = SQLite_for_notification.getWritableDatabase();
    		ContentValues insert_db_values = new ContentValues();
    		insert_db_values.put("title", title_map.get(title) );
    		insert_db_values.put("content", message );
    		insert_db_values.put("time", String.valueOf(year) + "/" + month + "/" + date + " " + hour + ":" + minute );
    		
    		db_write.insert("notification_message_table", null, insert_db_values);
    		db_write.close();
    		SQLite_for_notification.close();	
        
    		//NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
    		if( show_notification)
    		{
    			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
    			{
    				Notification.Builder builder = new Notification.Builder(context);
    				builder.setSmallIcon(R.drawable.solgan144, 0);
    				builder.setTicker(title_map.get(title));
    				builder.setWhen(when);
    				builder.setContentTitle(title_map.get(title));
    				builder.setContentText(message);
    				builder.setAutoCancel(true);
    				builder.setContentIntent(appIntent);
    				
    				Notification notification = builder.build();
    				
    				notification.flags |= Notification.FLAG_AUTO_CANCEL;
    				notificationManager.notify( (int)(when % 256) , notification);
    			}	
    			else 
    			{
    				Notification noti = new NotificationCompat.Builder(context).setContentTitle(title_map.get(title))
            										  						   .setContentText(message)
            										  						   .setContentIntent(appIntent)
            										  						   .setDefaults(Notification.DEFAULT_ALL)
            										  						   .setAutoCancel(true)
            										  						   .setSmallIcon(R.drawable.solgan144)
            										  						   .setWhen(when)
            										  						   .build();
    				notificationManager.notify( (int)(when % 256) , noti);
    			}
    		}	
    	}
    }
    
}
