package com.taipower.west_branch.utility;

import com.taipower.west_branch.FragmentSecondLayerMenu;
import com.taipower.west_branch.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;



public class PerferenceDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener
{
	
	private Context my_context ;
	private Activity my_activity;
	private PerferenceDialog this_class;
	private String[] my_data;
	private String my_previous_page;
	
	private FragmentTransaction ftagment_transaction;
	
	private SharedPreferences setting ;
	
	public PerferenceDialog(Context context)
	{
		super(context);
	}
	
	
	public PerferenceDialog(Context context,Activity activity , String[] data , boolean show_boolean ,String previous_page) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.my_context = context;
		this.my_activity = activity;
		this.my_data = data;
		this.this_class = this;
		this.my_previous_page = previous_page;
		
		ftagment_transaction = my_activity.getFragmentManager().beginTransaction();
		Fragment fragment = new FragmentSecondLayerMenu();
		Bundle bundle = new Bundle();
		bundle.putString("second_layer_content", previous_page);
		fragment.setArguments(bundle);
		ftagment_transaction.replace(R.id.fragment_content, fragment, "second_layer");
		
		
		setting = my_context.getSharedPreferences("remember", Context.MODE_PRIVATE );
		
		if( show_boolean )
		{
			this_class.setTitle("提醒");
			this_class.setMessage("基本資料是否儲存?\n方便下次使用?");
			this_class.setPositiveButton("是", this_class);
			this_class.setNegativeButton("不要\n下次不要再顯示!!", this_class);
			this_class.setNeutralButton("否", this_class);
			this_class.show();
		}
		else
		{	
			if(previous_page != null)
				ftagment_transaction.commit();
			else
				my_activity.finish();
		}
	}


	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
		// TODO Auto-generated method stub
		Log.i("which","" + which);
		
		switch( which )
		{
			case DialogInterface.BUTTON_NEUTRAL:
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				setting.edit().putBoolean("show_again", false).commit();
				break;
			case DialogInterface.BUTTON_POSITIVE:
				//if( my_data[0].equals("") || my_data[3].equals("") || ( my_data[1].equals("") &&  my_data[2].equals("") &&  my_data[5].equals(""))  )	
					setting.edit().putBoolean("show_again", true).commit();	
				
				setting.edit().putString("apply_user", my_data[0])
							  .putString("tel_area_number", my_data[1])
							  .putString("phone_number", my_data[2] )
							  .putString("email", my_data[3])
							  .putString("ext_phone_number", my_data[4])
							  .putString("mobile", my_data[5]).commit();	
				break;
		}
		
		dialog.dismiss();
		
		if(my_previous_page != null)
			ftagment_transaction.commit();
		else
			my_activity.finish();
	}
	
	


}