package com.taipower.west_branch.utility;


import java.util.ArrayList;
import java.util.Calendar;

import com.taipower.west_branch.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ASaBuLuCheck 
{	
		
	public static boolean electricCheckFunction(String electric_number )
	{
		boolean check_boolean = false ;
		
		if( electric_number.length() == 11  )
		{
			int temp = 0;
		
			for(int i = 0 ; i < 5 ; i ++)
			{
				temp = temp + ( ( Integer.valueOf( electric_number.substring( (i * 2) , (i * 2) + 1 ) ) ) * 2) / 10 ;
				temp = temp + ( ( Integer.valueOf( electric_number.substring( (i * 2) , (i * 2) + 1 ) ) ) * 2) % 10 ;
				temp = temp + ( ( Integer.valueOf( electric_number.substring( (i * 2) + 1, (i * 2) + 2 ) ) ) * 1) / 10 ;
				temp = temp + ( ( Integer.valueOf( electric_number.substring( (i * 2) + 1, (i * 2) + 2 ) ) ) * 1) % 10 ;
			}
		
			if( ( temp % 10 ) == Integer.valueOf(electric_number.substring(10)) )  
				check_boolean = true;		
		}
		
		return check_boolean;
	}
	
	
	
	
	public void auto_focus_editview(final TextView now,final TextView after,final int rule, final boolean end)
	{
		now.addTextChangedListener(new TextWatcher() 
        {
        	@Override
			public void onTextChanged(CharSequence s, int start,int before, int count) 
        	{
        		// TODO Auto-generated method stub
        		if( (now.getText().toString().length() >= rule ) && !end )    //size as per your requirement
        		{
        			after.requestFocus();
        		}	
        	}
        	
        	public void beforeTextChanged(CharSequence s, int start, int count, int after) 
        	{
                    //TODO Auto-generated method stub
        	}
        	
        	public void afterTextChanged(Editable s) 
        	{
                    // TODO Auto-generated method stub
        	}
        });
	}
	
	//身分證字號 check
	public static boolean idCheck(String id)
	{
		//char[] head = new char[]{'A','B'};
			
		String head = new String("ABCDEFGHJKLMNPQRSTUVXYWZIO");   
		
		
		int check_value = 0;
		
		try
		{
			
		
		if( id.length() == 10 && head.contains( String.valueOf(id.charAt(0) ) ) )
		{
			Integer.valueOf(id.substring(1));
			
			check_value =  (head.indexOf(id.substring(0,1)) + 10) / 10 
						   + (head.indexOf(id.substring(0,1)) % 10) * 9
						   + Integer.valueOf(id.substring(1 , 2) ) * 8 
						   + Integer.valueOf(id.substring(2 , 3) ) * 7
						   + Integer.valueOf(id.substring(3 , 4) ) * 6
						   + Integer.valueOf(id.substring(4 , 5) ) * 5
						   + Integer.valueOf(id.substring(5 , 6) ) * 4
						   + Integer.valueOf(id.substring(6 , 7) ) * 3
						   + Integer.valueOf(id.substring(7 , 8) ) * 2
						   + Integer.valueOf(id.substring(8 , 9) ) * 1 
						   + Integer.valueOf(id.substring(9 , 10) ) * 1 ;
						   
			if ( (check_value % 10) == 0 )
				return true;
		}
		else if( id.length() == 8 && !head.contains( String.valueOf(id.charAt(0)))  )  
		{
			int[] cross = new int[]{1,2,1,2,1,2,4,1};
			
			for(int i = 0 ; i < cross.length ; i++)
				check_value += (Integer.valueOf( id.substring(i,i+1) ) * cross[i]) / 10 + (Integer.valueOf(  id.substring(i,i+1) ) * cross[i]) % 10 ;
					    	  
			if( check_value % 10 == 0 || ( Integer.valueOf(id.substring(6,7)) == 7  &&  check_value % 10 == 9 ) )
				return true;
		}
		
		}
		catch (NumberFormatException e)
		{
			
		}
		
		return false;
	}
	
	public static boolean isOnline(Activity activity) 
	{
	    ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting())
	    	return true;
	    
	    return false;
	}
	
	
	public boolean NORMAL_STATE = false;
	public boolean WARNING_STATE = true;
	
	public static void setLayoutBackgroundState(View current_view, ArrayList<Integer> error_layout_id, ArrayList<Integer> error_mark_id ,boolean warning_state)
    {
		Integer[] error_layout_id_array = error_layout_id.toArray( new Integer[error_layout_id.size()]);
		
		Integer[] error_mark_id_array = error_mark_id.toArray(new Integer[error_mark_id.size()]);
		
		int background_id = 0;
    	int error_image_id ;
    	
    	if(warning_state)
    	{	
    		background_id = R.drawable.warning_background;
    		error_image_id = Integer.valueOf(R.drawable.error_mark);
    	}
    	else
    	{
    		background_id = R.drawable.edit_view_background;
    		error_image_id = Integer.valueOf(0);
    	}
    	
    	for(int i = 0 ; i < error_layout_id_array.length ; i++)
    	{	
    		((RelativeLayout) current_view.findViewById(error_layout_id_array[i].intValue())).setBackgroundResource( background_id );
    		((ImageView) current_view.findViewById(error_mark_id_array[i].intValue())).setImageResource(error_image_id);	
    	}
    }
	
	
	public final static boolean HALF_TYPE = true;
	public final static boolean FULL_TYPE = false;
	
	public static String convertNumberType(String input,boolean convert_type)
	{
		String[] half_type = {"0","1","2","3","4","5","6","7","8","9"};
		String[] full_type = {"０","１","２","３","４","５","６","７","８","９"};
		
		for(int i = 0 ; i < 10 ; i++)
		{
			if(convert_type)
				input = input.replace(full_type[i], half_type[i]);
			else
				input = input.replace(half_type[i], full_type[i]);
		}
		
		return input;
	}
	
	public static boolean dayOrNight()
	{
		//Return true if current hour is bigger than 6 and smaller than 18 (24hours type) ,otherwise false.
		
		Calendar calendar_data_now = Calendar.getInstance();
        int hour_of_24 = calendar_data_now.get(Calendar.HOUR_OF_DAY);
        
        if( (hour_of_24 >= 18) || (hour_of_24 <= 5) )
        	return false;
        else
        	return true;
	}
	
	
	
	
	
	
	
	
}
