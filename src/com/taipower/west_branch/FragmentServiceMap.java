package com.taipower.west_branch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taipower.west_branch.utility.HttpConnectResponse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


enum MAP_TAG{TAIPOWER,TAIWAN_WATER,GOV,GOV_HH,GOV_LR,CHT,GAS,TAIPEI_WATER}


public class FragmentServiceMap extends Fragment
{
	private Context app_context;
	private Activity app_activity;
	private FragmentServiceMap this_class;
	
	private static View current_view;
	
	private Resources res;
	
	private GoogleMap mMap;
	
	private Marker[] transport_marker;
	
	String[][] transport_array;
	
	private boolean english_version; 
	
	private String online_map_version;
	private String local_map_version;
	private Document online_document;
	private boolean map_info_online ;
	
	private String[][] nickname_id_string_array;
	private int[] nickname_id = new int[]{R.array.taipower_nickname,
										  R.array.taiwan_water_nickname,
										  R.array.gov_nickname,
										  R.array.gov_hh_nickname,
										  R.array.gov_lr_nickname,
										  R.array.cht_nickname,
										  R.array.gas_nickname,
										  R.array.taipei_water};
	
	private String[][] resource_array_id_string_array;
	private int[] resource_array_id = new int[]{R.array.taipower,
												R.array.taiwan_water,
												R.array.taipei_water,
												R.array.taipei_water,
												R.array.taipei_water,
												R.array.cht,
												R.array.taipei_water,
												R.array.taipei_water};
	//緯度	
    double my_lat;
    //經度
    double my_lng;
    
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		this.app_context = this.getActivity();
    	this.app_activity = this.getActivity();
		this.this_class = this;
		
    	current_view = inflater.inflate(R.layout.fragment_service_map, container, false);
    	
    	res = app_activity.getResources();  //return getActivity().getResources()
    	
    	
    	local_map_version = res.getString(R.string.map_version);
    	online_map_version = "0.0";
    	
    	Document document = null;
    	try 
		{
			byte[] map_info_online_byte_array = HttpConnectResponse.inputStreamToByteArray(app_context.openFileInput("online_map_info.xml"));
				
			document = Jsoup.parse(new String(map_info_online_byte_array,"utf-8"));
			
			online_map_version =  document.select("string[name=map_version]").first().text().toString();
			Log.i("online_map_version",online_map_version);
			online_document = document;
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	new DoingInBackgroundTask().execute();
		
    	nickname_id_string_array = new String[nickname_id.length][];
    	resource_array_id_string_array = new String[resource_array_id.length][];
    	
    	if( Double.valueOf(online_map_version) > Double.valueOf(local_map_version) )
		{
    		String[] name_nickname = new String[]{"taipower_nickname",
    											  "taiwan_water_nickname",
    											  "gov_nickname",
    											  "gov_hh_nickname",
    											  "gov_lr_nickname",
    											  "cht_nickname",
    											  "gas_nickname",
    											  "taipei_water"};
    		
    		for(int i = 0; i <  nickname_id_string_array.length; i++ )
    		{
    			Elements nickname_items = document.select("string-array[name="+ name_nickname[i] +"]").first().select("item");
        		
    			ArrayList<String> temp_array = new ArrayList<String>();
    			
    			for(Element item : nickname_items )
    				temp_array.add(item.text().toString());
    			
    			nickname_id_string_array[i] = temp_array.toArray(new String[temp_array.size()]);	
    		}
    		
    		String[] name_resource_array = new String[]{"taipower",
														"taiwan_water",
														"taipei_water",
														"taipei_water",
														"taipei_water",
														"cht",
														"taipei_water",
														"taipei_water"};	
    		
    		for(int i = 0; i <  resource_array_id_string_array.length; i++ )
    		{
    			Elements nickname_items = document.select("string-array[name="+ name_resource_array[i] +"]").first().select("item");
    			
    			ArrayList<String> temp_array = new ArrayList<String>();
    			
    			for(Element item : nickname_items )
    				temp_array.add(item.text().toString());
    			
    			resource_array_id_string_array[i] = temp_array.toArray(new String[temp_array.size()]);		
    		}
    		
    		map_info_online = true;
    		Log.i("map_info","map_info_online");
		}
		else 
		{
			for(int i = 0; i < nickname_id.length; i++)
				nickname_id_string_array[i] = res.getStringArray(nickname_id[i]);
			
			for(int i = 0; i < resource_array_id.length; i++)
				resource_array_id_string_array[i] = res.getStringArray(resource_array_id[i]); 	
			
			map_info_online = false;
			Log.i("map_info","map_info_offline");
		}
    	
    	
    	
    	
    	((LinearLayout) app_activity.findViewById(R.id.title_bar_layout)).setBackgroundResource(R.drawable.title_bar_second_layer_background);
    	((ImageView) app_activity.findViewById(R.id.title_bar_main_title)).setBackgroundResource(R.drawable.title_bar_service_map);
    	
		//Bundle bundle = getIntent().getExtras();
    	Bundle bundle = this.getArguments();
    	
		if( bundle != null)
		{	
			if( bundle.getString("version").equals("english")  )
				english_version = true;
		}
		else
			english_version = false;
		
		ImageButton title_bar_back_button = (ImageButton) app_activity.findViewById(R.id.title_bar_back_button);
		title_bar_back_button.setBackgroundResource(R.drawable.title_bar_second_layer_back_button);
		//if(english_version)
		//	title_bar_back_button.setBackgroundResource(R.drawable.ic_keyboard_arrow_left_onclick);
        
		title_bar_back_button.setOnClickListener(new View.OnClickListener() 
        {
        	@Override
			public void onClick(View v) 
        	{
        		FragmentTransaction fragment_trans = app_activity.getFragmentManager().beginTransaction();
        		Fragment fragment = new MainPageFragment();
        		fragment_trans.replace(R.id.fragment_content, fragment,"main_page");
        		fragment_trans.commit();
        	}
        });
		
        
	    ImageButton title_bar_menu_button = (ImageButton) app_activity.findViewById(R.id.title_bar_menu_button);
	    title_bar_menu_button.setVisibility(View.VISIBLE);
	    title_bar_menu_button.setBackgroundResource(R.drawable.title_bar_second_layer_menu_button);
	    title_bar_menu_button.setOnClickListener(new View.OnClickListener()
	   	{
	    	@Override
	   		public void onClick(View v)
	    	{
	   			// TODO Auto-generated method stub
	   			String[] item_name = res.getStringArray(R.array.service_map_menu);
	   					
	    		ItemAdapter item_adapter = new ItemAdapter(app_context, android.R.layout.simple_list_item_1, item_name , null);
	    			
	    		AlertDialog.Builder menu = new AlertDialog.Builder(app_context);
	    		menu.setAdapter(item_adapter, menu_on_click_listener);
	    		menu.show();	
	    	}
	   	});
	    
        
	    Fragment fragment ;
		
		if( getFragmentManager().findFragmentById(R.id.map) != null)
		{	
			Log.i("getFragmentManager findFragmentById", app_activity.getFragmentManager().findFragmentById(R.id.map).toString());
			fragment = app_activity.getFragmentManager().findFragmentById(R.id.map);
		}
		else
		{	
			Log.i("getChildFragmentManager findFragmentById", getChildFragmentManager().findFragmentById(R.id.map).toString());
			fragment = getChildFragmentManager().findFragmentById(R.id.map);
		}
			
	    MapFragment mapFragment = (MapFragment) fragment;
	    
	    /*
	    mapFragment.getMapAsync(new OnMapReadyCallback()
	    {
			@Override
			public void onMapReady(GoogleMap googleMap) 
			{
				// TODO Auto-generated method stub
				
			}
	    });
	    */
	    
	    mMap = mapFragment.getMap();
	    
	    if( mMap == null)
	    {
	    	String[] warning_message; 
	    	
	    	if( !english_version )
	    		warning_message = new String[]{"歐!歐歐~~ !!","尚未安裝GooglePlay服務\n請至Play商店下載安裝","確定"};
	    	else
	    		warning_message = new String[]{"OH!~OH~OH~~ !!","Don't have GooglePlay Service!!\nPlease download from PlayStore","OK"};
	    	
	    	AlertDialog.Builder warning = new  AlertDialog.Builder(app_context);
	    	warning.setTitle(warning_message[0]);
	    	warning.setMessage(warning_message[1]);
	    	warning.setNeutralButton(warning_message[2], null);
	    	warning.show();
	    }
	    else
	    {
	    	
	    	// Getting LocationManager object from System Service LOCATION_SERVICE
	    	LocationManager locationManager = (LocationManager) app_context.getSystemService(Context.LOCATION_SERVICE);
	        // Creating a criteria object to retrieve provider
	        //Criteria criteria = new Criteria();
	        // Getting the name of the best provider
	        //String provider = locationManager.getBestProvider(criteria, true);
	        // Getting Current Location
	        //Location location = locationManager.getLastKnownLocation(provider);
	        
	        Location location = this_class.getLastKnownLocation(locationManager);
		    //test 
	        
	    	/*
	    	GoogleApiClient.Builder mGoogleApiClient = new GoogleApiClient.Builder(app_context);
	    	mGoogleApiClient.addConnectionCallbacks(new ConnectionCallbacks()
	        {
				@Override
				public void onConnected(Bundle connectionHint)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onConnectionSuspended(int cause) 
				{
					// TODO Auto-generated method stub
					
				}	
	        });
	    	
	    	mGoogleApiClient.addOnConnectionFailedListener(new OnConnectionFailedListener()
	    	{
				@Override
				public void onConnectionFailed(ConnectionResult result) 
				{
					// TODO Auto-generated method stub
					
				}
	    	});
	    	mGoogleApiClient.addApi(LocationServices.API);
	    	GoogleApiClient googleApiClient = mGoogleApiClient.build();
	    	
	    	Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
	        */
	    	
	        
	    	if( location != null)
	    	{
	        	my_lat = location.getLatitude();
	    	    my_lng = location.getLongitude();
	    		
	    	    //CircleOptions circleOptions = new CircleOptions().center(new LatLng(my_lat, my_lng)).radius(500); // In meters
	    	    //circleOptions.strokeWidth(2);
	    	    //circleOptions.strokeColor(Color.BLUE);
	    	    //circleOptions.fillColor(Color.parseColor("#500084d3"));
	    	    //circleOptions.visible(true);
	    	    //mMap.addCircle(circleOptions);
	    	    	
	        }	
	        else
	        {
	        	String[] now_location_warning_string;
	        	
	        	if( english_version )
	        		now_location_warning_string = new String[]{"OH!OOOH~ ","Location Service Not Set On,Please Turn It On It","OK"};
	        	else
	        		now_location_warning_string = new String[]{"歐歐!!定位服務未開啟!!","您的行動裝置定位服務未開啟\n系統將以新北大橋為定位中心","關閉"};
	        	
	        	AlertDialog.Builder now_location_warning = new AlertDialog.Builder(app_context);
	        	now_location_warning.setTitle(now_location_warning_string[0]);
	        	now_location_warning.setMessage(now_location_warning_string[1]);
	        	now_location_warning.setNegativeButton(now_location_warning_string[2],null);
	        	
	        	now_location_warning.show();
	        	
	        	//新北大橋 location
	        	my_lat = 25.0469761;
	        	my_lng = 121.4843166;
	        }
	        
	        
	        this_class.loadActivity(MAP_TAG.TAIPOWER, null, NEAREST_LOCATION);
	    }
	    
	    return current_view;
	}
	
	
	private Location getLastKnownLocation(LocationManager locationManager) 
	{
	    List<String> providers = locationManager.getProviders(true);
	    Location bestLocation = null;
	    
	    for (String provider : providers) 
	    {
	        Location l = locationManager.getLastKnownLocation(provider);
	        Log.i("last known location", "provider: "+ provider  + "location: " +l);

	        if (l == null) 
	        {
	            continue;
	        }
	        
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) 
	        {
	            Log.i("found best last known location:" , "" + l);
	            bestLocation = l;
	        }
	    }
	    
	    if ( bestLocation == null) 
	    {
	        return null;
	    }
	    
	    return bestLocation;
	}
	
	@Override
	public void onDestroyView ()
	{
		//This map fragment do not be removed automatically, use manual method to remove this.
		
		Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
		
		if(fragment != null)
		{	
			Log.i("onDestroyView","onDestroyView");
			
			getFragmentManager().beginTransaction().remove(fragment).commit();
		}
		
		((ImageButton) app_activity.findViewById(R.id.title_bar_menu_button)).setOnClickListener(null);
		
		super.onDestroyView();
	}
	
	
	private class ItemAdapter extends ArrayAdapter<Object>
	{
		String[] temp_list_data;
		int[] image_resource_id;
		MAP_TAG item_map_tag;
		
		public ItemAdapter(Context context, int resource_layout_id, Object[] list_data, MAP_TAG map_tag)
		{
			super(context, resource_layout_id, list_data );
			
			int[]image_id = new int[]{R.drawable.taipower_logo144,
									  R.drawable.taiwan_water144,
									  R.drawable.distr_exec144,
									  R.drawable.household144,
					 				  R.drawable.landregistered144,
					 				  R.drawable.cht144,
					 				  R.drawable.gas144,
					 				  R.drawable.taiwan_water144,};
			
			temp_list_data =  (String[]) list_data;
			
			image_resource_id = new int[temp_list_data.length];
			
			item_map_tag = map_tag;
			
			if( map_tag == null )
			{
				image_resource_id = image_id;
			}
			else
			{						
				for(int i = 0 ; i < temp_list_data.length ; i++)
				{	
					if( temp_list_data[i].contains("北西") && map_tag == MAP_TAG.TAIPOWER )
						image_resource_id[i]  = R.drawable.icon144;
					else
						image_resource_id[i] = image_id[map_tag.ordinal()];
				}
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = app_activity.getLayoutInflater().inflate(R.layout.fragment_service_map_menu_item, parent, false);
			
			ImageView service_map_item_image = (ImageView) convertView.findViewById(R.id.service_map_item_image);
			service_map_item_image.setBackgroundResource(image_resource_id[position]);
			
			TextView service_map_item_text = (TextView) convertView.findViewById(R.id.service_map_item_text);
			service_map_item_text.setText(temp_list_data[position]);
			
			return convertView;
		}
		
		public MAP_TAG getMAPTAG()
		{
			return item_map_tag;
		}
	}
	
	private ItemAdapter sub_menu_adapter;
	
	
	
	private DialogInterface.OnClickListener menu_on_click_listener = new DialogInterface.OnClickListener()
	{	
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			// TODO Auto-generated method stub
			//String[] list_array = res.getStringArray(nickname_id[which]);
			String[] list_array = nickname_id_string_array[which];
			
			
			ArrayList<String> temp_list_array = new ArrayList<String>(); 
			
			if( which == MAP_TAG.TAIPOWER.ordinal() || which == MAP_TAG.TAIWAN_WATER.ordinal() || which == MAP_TAG.CHT.ordinal() || which == MAP_TAG.TAIPEI_WATER.ordinal())
				temp_list_array.add("最近的服務據點");
			
			for( int i = 0 ; i<list_array.length; i++)
			{	
				if( which != MAP_TAG.TAIPEI_WATER.ordinal() )
					temp_list_array.add(list_array[i]);
				else
					temp_list_array.add(list_array[i].split(",")[2]);
			}
			
			list_array = temp_list_array.toArray(new String[temp_list_array.size()]);
			
			sub_menu_adapter = new ItemAdapter(app_context, android.R.layout.simple_list_item_1, list_array, MAP_TAG.values()[which]);
			
			AlertDialog.Builder sub_menu = new AlertDialog.Builder(app_context);
			sub_menu.setCustomTitle(null);
			sub_menu.setAdapter(sub_menu_adapter, sub_menu_on_click_listener);
			sub_menu.show();
			
			dialog.dismiss();
		}
	};
	
	private DialogInterface.OnClickListener sub_menu_on_click_listener = new DialogInterface.OnClickListener()
	{
		ArrayList<String> third_menu_list;
		//int resource_id;
		String[] resource_id_string_array;
		
		//String[] resource_array_list;
		int sub_menu_resource_which;
		
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			// TODO Auto-generated method stub
			MAP_TAG map_tag = sub_menu_adapter.getMAPTAG(); 
			
			if( (map_tag == MAP_TAG.TAIPOWER || map_tag == MAP_TAG.TAIWAN_WATER || map_tag == MAP_TAG.CHT || map_tag == MAP_TAG.TAIPEI_WATER) && which == 0  )
				loadActivity(map_tag, null, this_class.NEAREST_LOCATION);
			else if( map_tag == MAP_TAG.TAIPEI_WATER )
				loadActivity(map_tag, null, which - 1);
			else
			{
				sub_menu_resource_which = map_tag.ordinal();
			
				//String[] nick_name = res.getStringArray(nickname_id[sub_menu_resource_which]);
				String[] nick_name = nickname_id_string_array[sub_menu_resource_which];
			
				third_menu_list = new ArrayList<String>();
			
				if( sub_menu_adapter.getMAPTAG() == MAP_TAG.GOV || sub_menu_adapter.getMAPTAG() == MAP_TAG.GOV_HH || 
					sub_menu_adapter.getMAPTAG() == MAP_TAG.GOV_LR || sub_menu_adapter.getMAPTAG() == MAP_TAG.GAS)
				{
					String map_tag_name = "";
					int map_tag_array_id = 0;
					
					switch( sub_menu_adapter.getMAPTAG() )
					{
						case GOV:
							map_tag_name = "gov";
							map_tag_array_id = R.array.gov_00;
							break;
						case GOV_HH:
							map_tag_name = "gov_hh";
							map_tag_array_id = R.array.gov_hh_00;
							break;
						case GOV_LR:
							map_tag_name = "gov_lr";
							map_tag_array_id = R.array.gov_lr_00;
							break;
						case GAS:
							map_tag_name = "gas";
							map_tag_array_id = R.array.gas_00;
							break;
						default:
							break;
					}
					
					if(  map_info_online )
					{
						String string_array_name = ""; 
						
						if( which < 10)
							string_array_name = String.format("%s_0%d",map_tag_name , which);
						else
							string_array_name = String.format("%s_%d",map_tag_name , which);
						
						Elements string_array_elements = online_document.select("string-array[name="+ string_array_name +"]").select("item");
						
						ArrayList<String> temp_array = new ArrayList<String>();
		    			
		    			for(Element item : string_array_elements )
		    				temp_array.add(item.text().toString());
						
						resource_id_string_array = temp_array.toArray(new String[temp_array.size()]);
						
					}
					else
						resource_id_string_array = res.getStringArray(map_tag_array_id + which);
					
					third_menu_list.add("最近的服務據點");
					
				}
				else
				{
					//resource_id = resource_array_id[sub_menu_resource_which];
					resource_id_string_array = resource_array_id_string_array[sub_menu_resource_which];
					which -= 1; // remove nearest item
				}
				
				
				
				/*
				switch( sub_menu_adapter.getMAPTAG() )
				{
					case GOV:
					{	
						if(  map_info_online )
						{
							String string_array_name; 
							if( which < 10)
								string_array_name = String.format("gov_0%d", which);
							else
								string_array_name = String.format("gov_%d", which);
							
							Elements string_array_elements = online_document.select("string-array[name="+ string_array_name +"]").select("item");
							
							resource_id_string_array = string_array_elements.toArray(new String[string_array_elements.size()]);
							
						}
						else
							resource_id_string_array = res.getStringArray(R.array.gov_00 + which);
						
						third_menu_list.add("最近的服務據點");
						break;
					}
					case GOV_HH:
					{
						if(  map_info_online )
						{
							
						}
						else
							resource_id_string_array = res.getStringArray(R.array.gov_hh_00 + which);
						
						third_menu_list.add("最近的服務據點");
						break;
					}
					case GOV_LR:
					{
						if(  map_info_online )
						{
							
						}
						else
							resource_id_string_array = res.getStringArray(R.array.gov_lr_00 + which);
					
						third_menu_list.add("最近的服務據點");
						break;
					}
					case GAS:
					{
						if(  map_info_online )
						{
							
						}
						else
							resource_id_string_array = res.getStringArray(R.array.gas_00 + which);
					
						third_menu_list.add("最近的服務據點");
						break;
					}
					default :
					{	
						//resource_id = resource_array_id[sub_menu_resource_which];
						resource_id_string_array = resource_array_id_string_array[sub_menu_resource_which];
						which -= 1; // remove nearest item
						break;
					}
				}
				*/
				
				
				//resource_array_list = res.getStringArray(resource_id);
				//resource_array_list = resource_id_string_array;
			
				for(int i = 0 ; i < resource_id_string_array.length ; i++)
				{
					String[] temp = resource_id_string_array[i].split(",");
				
					if( map_tag == MAP_TAG.TAIWAN_WATER  )
					{	
						if( temp[2].contains( nick_name[which].substring(0, 5) ) )
							third_menu_list.add(temp[2]);
					}
					else 
					{		
						if( temp[2].contains( nick_name[which].substring(0, 2) ) )
							third_menu_list.add(temp[2]);
					}
				}
			
				ItemAdapter third_menu_adapter = new ItemAdapter(app_context, android.R.layout.simple_list_item_1,
																third_menu_list.toArray(new String[third_menu_list.size()]), sub_menu_adapter.getMAPTAG() );
			
				AlertDialog.Builder third_menu = new AlertDialog.Builder(app_context);
				third_menu.setTitle(null);
				third_menu.setAdapter(third_menu_adapter, new OnClickListener()
				//third_menu.setItems(third_menu_list.toArray(new String[third_menu_list.size()]), new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// TODO Auto-generated method stub
						if( sub_menu_resource_which == MAP_TAG.TAIPOWER.ordinal() || sub_menu_resource_which == MAP_TAG.TAIWAN_WATER.ordinal() || sub_menu_resource_which == MAP_TAG.CHT.ordinal())
						{
							for(int i = 0 ; i <  resource_id_string_array.length ; i++ )
							{
								String[] temp = resource_id_string_array[i].split(",");
							
								if( temp[2].contains(third_menu_list.get(which)) )
								{
									loadActivity(MAP_TAG.values()[sub_menu_resource_which], resource_id_string_array, i);
									break;
								}
							}
						}
						else
							this_class.loadActivity(MAP_TAG.values()[sub_menu_resource_which], resource_id_string_array, which - 1);
					}
				});
				third_menu.setCancelable(true);
				third_menu.show();
			}
		}
	};
	
	private DialogInterface.OnClickListener taipei_water_sub_menu_on_click_listener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			// TODO Auto-generated method stub
			//loadActivity(sub_menu_adapter.getMAPTAG(), 0, which - 1);
			loadActivity(sub_menu_adapter.getMAPTAG(), null, which - 1);
			
			dialog.dismiss();
		}
	};
	
	
	private int NEAREST_LOCATION = -1;
	
	@SuppressLint("NewApi")
	//private void loadActivity(MAP_TAG map_tag, int resource_id,int which)
	private void loadActivity(MAP_TAG map_tag, String[] resource_id_string_array,int which)
	{ 	
		ArrayList<String> temp_service_point_array_list;
		
		if( resource_id_string_array == null )
	   	{	
			//temp_service_point_array_list = new ArrayList<String>(Arrays.asList(res.getStringArray(resource_array_id[map_tag.ordinal()])));
			temp_service_point_array_list = new ArrayList<String>(Arrays.asList(resource_array_id_string_array[map_tag.ordinal()]));
	   	}
	   	else
	   	{	
	   		//temp_service_point_array_list = new ArrayList<String>(Arrays.asList(res.getStringArray(resource_id)));
	   		temp_service_point_array_list = new ArrayList<String>(Arrays.asList(resource_id_string_array));
	   	}
	   		
	   	if( mMap != null )
    		mMap.clear();
	   	
	   	mMap.setMyLocationEnabled(true);
	   	mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener()
	   	{
			@Override
			public void onMyLocationChange(Location location) 
			{
				// TODO Auto-generated method stub
				my_lat = location.getLatitude();
			    my_lng = location.getLongitude();
			}
	   	});
	   	
	   	double nearest_distance = 1000000.0;
	   	Marker camera_marker = null ;
	   	Marker selected_marker = null;
	   	   	
	   	for(int i = 0; i < temp_service_point_array_list.size() ;i++)
	    {
	    	Marker temp_mark = mMap.addMarker(markerIconOptions( map_tag, temp_service_point_array_list.get(i)));
	    	
	    	if( i == which )
	    		selected_marker = temp_mark;
	    		
	   		double temp_distance = Math.floor( distanceCalc( my_lat, my_lng, temp_mark.getPosition().latitude, temp_mark.getPosition().longitude))/1000.0f;
	   		
	    	if(temp_distance < nearest_distance )
	    	{		
	    		nearest_distance = temp_distance ;
		    	camera_marker = temp_mark;
		   	}
	   	}
	   	
	   	if( selected_marker != null )
	   		camera_marker = selected_marker; 
	   	
	   	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera_marker.getPosition(), 12));
	   	camera_marker.showInfoWindow();
	    
	   	mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener()
    	{
	    	@Override
	    	public boolean onMyLocationButtonClick() 
	    	{
	    		// TODO Auto-generated method stub
	    		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(my_lat, my_lng), 12));
	    			
	    		return false;
	    	}
	    });
	    
	    mMap.setInfoWindowAdapter(map_inofr_adapter);
	    
	    mMap.setOnInfoWindowClickListener(info_window_click_listener);
	}
	
	private OnInfoWindowClickListener info_window_click_listener = new OnInfoWindowClickListener()
	{
		@Override
		public void onInfoWindowClick(Marker marker) 
		{
			// TODO Auto-generated method stub
			final String[] temp_data = marker.getSnippet().split(","); 
			
			String message = "";
			
			message += temp_data[0] + "\n";
			message += "地址：" + temp_data[1] + "\n";
			message += "電話：" + temp_data[2] + "\n";
			message += "距離：" + String.valueOf(distanceCalc( my_lat, my_lng, marker.getPosition().latitude, marker.getPosition().longitude)/1000.0f) + "公里";
			
			AlertDialog.Builder info_dialog = new AlertDialog.Builder(app_context);
			info_dialog.setTitle("詳細資料");
			info_dialog.setMessage(message);
			info_dialog.setPositiveButton("撥打電話", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TODO Auto-generated method stub
					String temp_tel = temp_data[2].replace("(","");
					temp_tel = temp_tel.replace(")", "");
					temp_tel = temp_tel.replace("-", "");
					
					
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + temp_tel));
					app_activity.startActivity(intent);
					
					dialog.dismiss();
				}
			});
			info_dialog.setNegativeButton("關閉", null);
			info_dialog.show();
		}
	};
	
	private InfoWindowAdapter map_inofr_adapter = new InfoWindowAdapter()
	{
		@Override
        public View getInfoWindow(Marker marker) 
    	{
			View mark_view = app_activity.getLayoutInflater().inflate(R.layout.fragment_service_map_mark_info_adapter, null);
        	
			ImageView info_adapter_icon = (ImageView) mark_view.findViewById(R.id.info_adapter_icon);
			
			info_adapter_icon.setImageResource(imageResource(marker));
			
			String[] temp_data = marker.getSnippet().split(",");
			
			((TextView) mark_view.findViewById(R.id.info_adapter_title)).setText(temp_data[0]);
			((TextView) mark_view.findViewById(R.id.info_adapter_subtitle)).setText(temp_data[1]);
			
        	return mark_view;
        }

        @Override
        public View getInfoContents(Marker marker) 
        {
        	return null;
        }
	};
	
	public double distanceCalc (double lat_a, double lng_a, double lat_b, double lng_b ) 
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
        Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }
	
	private int imageResource(Marker marker)
	{
		MAP_TAG map_tag =  MAP_TAG.values()[Integer.valueOf(marker.getTitle())];
		
		int image_resource = 0;
		
		switch(map_tag)
		{
			case TAIPOWER:
				image_resource = R.drawable.icon144;
				break;
			case TAIWAN_WATER:
				image_resource = R.drawable.taiwan_water144;
				break;
			case GOV:
				image_resource = R.drawable.distr_exec144;
				break;
			case GOV_HH:
				image_resource = R.drawable.household144;
				break;
			case GOV_LR:
				image_resource = R.drawable.landregistered144;
				break;
			case CHT:
				image_resource = R.drawable.cht144;
				break;
			case GAS:
				image_resource = R.drawable.gas144;
				break;
			case TAIPEI_WATER:
				image_resource = R.drawable.taiwan_water144;
				break;
		}
		
		return image_resource;
	}
	
	
	private MarkerOptions markerIconOptions (MAP_TAG map_tag,String point_data)
	{
		MarkerOptions marker_data = new MarkerOptions();
		
		String[] temp_point_data = point_data.split(",");
		
		double distance = distanceCalc( my_lat, my_lng, Double.parseDouble(temp_point_data[0]), Double.parseDouble(temp_point_data[1])) / 1000.0f;
		
		marker_data.position(new LatLng(Double.parseDouble(temp_point_data[0]),Double.parseDouble(temp_point_data[1])));
		marker_data.title(String.valueOf(map_tag.ordinal()));
		marker_data.snippet(temp_point_data[2] + "," + temp_point_data[3] + "," + temp_point_data[4] + "," + String.valueOf(distance));
		marker_data.draggable(false);
		
		return marker_data;
	}
	
	private void transportMarker()
	{
		for(int i = 0 ;i < transport_array.length ; i ++)
		{
			if(transport_marker[i] != null)
				transport_marker[i].remove();
			
			//if(transport_marker[i] == null)
			{
			MarkerOptions marker_data = new MarkerOptions();
		
			marker_data.position(new LatLng(Double.valueOf(transport_array[i][0]),Double.valueOf(transport_array[i][1])));
			marker_data.title(String.valueOf(i));
			
			if(transport_array[i][2].startsWith("捷運") || transport_array[i][2].startsWith("MRT"))
				marker_data.icon(BitmapDescriptorFactory.fromResource(R.drawable.mrt));
			else
				marker_data.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
			
			marker_data.snippet("transport");
			
			transport_marker[i] = mMap.addMarker(marker_data);
			}
		}
	}
	
	
	private class DoingInBackgroundTask extends AsyncTask<String,Integer,Integer>
	{	
		byte[] response;
		//Document document;
		
		@Override
		protected Integer doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
			Integer result_value = null;
			
			//String map_data_url = "http://cksleeper.dlinkddns.com/online_map_info.xml";
			String map_data_url = "https://tpwb.taipower.com.tw/app/online_map_info.xml";
			
			try 
			{
				response = HttpConnectResponse.onOpenConnection(map_data_url, "GET", null, HttpConnectResponse.COOKIE_CLEAR, HttpConnectResponse.HTTP_NONREDIRECT);
				//document = Jsoup.parse(new URL(map_data_url), 30000);
				
				result_value = 0;
			} 
			catch (SSLHandshakeException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			
			return result_value;
		}
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			if( result != null )
			{
				//String map_data_url = "http://cksleeper.dlinkddns.com/map_info_online.xml";
				//Document document = Jsoup.parse(new URL(map_data_url), 30000);
				
				Document document = null;
				
				try 
				{
					String map_info_online = new String(response,"utf-8");
					
					document = Jsoup.parse(map_info_online);
					
					Elements new_map_version_elements = document.select("string[name=map_version]");
					String new_map_version = new_map_version_elements.first().text();
					Log.i("new_map_version",new_map_version);
				
					if( Double.valueOf(new_map_version) > Double.valueOf(online_map_version) && Double.valueOf(new_map_version) > Double.valueOf(local_map_version) )
					{
						FileOutputStream map_info_online_stream = app_activity.openFileOutput("online_map_info.xml", Context.MODE_PRIVATE);
					
						map_info_online_stream.write(response);
						map_info_online_stream.close();
						
						Toast.makeText(app_context, "地圖資料已更新，請離開地圖並再次進入", Toast.LENGTH_LONG).show();
					}
					
				} 
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (FileNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		
		
	}
	
}
