package com.taipower.west_branch;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.taipower.west_branch.R;
import com.taipower.west_branch.utility.DBonSQLite;
import com.taipower.west_branch.utility.DmInfor;
import com.taipower.west_branch.utility.NoTitleBar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class barcode_and_ebpps_barcode extends Activity {
	
	private Context app_context ;
	private Activity app_activity ;
	
	File photoFile = null;
	
	String electric_number ;
    String payment_money_format; 
	String origin_from;
    
	ImageView barcode0 ;
	ImageView barcode1 ;
	ImageView barcode2 ;
	
	String ROC_date0 ;
    String ROC_date1 ;
    String x;
    
	Bitmap bitmap0 = null;
    Bitmap bitmap1 = null;
    Bitmap bitmap2 = null;
    
    private static final int[] desired_width0 = new int[]{ 520, 605, 690, 775, 860, 945,1030,1115,1200,1285,1370,1455,1540,1625,
    													  1710,
    													  1795,1880,1965,2050,2135,2220,2305,2390,2475,2560,2645,2730,2815,2900};
    private static final int[] desired_width1 = new int[]{1040,1210,1380,1550,1720,1890,2060,2230,2400,2570,2740,2910,3080,3250,
    													  3420,
    													  3590,3760,3930,4100,4270,4440,4610,4780,4950,5120,5290,5460,5630,5800}; 
    private static final int[] desired_width2 = new int[]{ 890,1030,1170,1310,1450,1590,1730,1870,2010,2150,2290,2430,2570,2710,
    													  2850,
    													  2990,3130,3270,3410,3550,3690,3830,3970,4110,4250,4390,4530,4670,4810}; 
    
    private int zoom_count = 14;
    
    public barcode_and_ebpps_barcode()
    {
    	app_context = this;
    	app_activity = this;
    }
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    
    	new NoTitleBar(android.os.Build.VERSION.SDK_INT, app_context, app_activity);
		
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.barcode_and_ebpps_barcode);
    		
        int linear_height = new DmInfor(this,this).menu_linear_height;
         
        LinearLayout menu_bar = (LinearLayout) findViewById(R.id.menu_bar);
        menu_bar.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,  linear_height ));
    	
       
        
    //LinearLayout l = new LinearLayout(this);
    //l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    //l.setOrientation(LinearLayout.VERTICAL);

    //setContentView(l);
    
    
    ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
	
	back_button.setOnClickListener(new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			finish_warning();				
		}
	});		
    
	ImageView ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
    ic_launcher.setLayoutParams(new LinearLayout.LayoutParams(linear_height, LayoutParams.MATCH_PARENT));
	
    
    Bundle bundle = getIntent().getExtras();
    electric_number = (String) bundle.getString("electric_number");
    payment_money_format = (String) bundle.getString("payment_money_format");
    ROC_date0 = (String) bundle.getString("ROC_date0");
    ROC_date1 = (String) bundle.getString("ROC_date1");
    origin_from = bundle.getString("origin_from");
    
    /*// barcode data
    String barcode_data0 = "010717112";
    String barcode_data1 = "07003849100010522";
    String barcode_data2 = "000000936";
    */
    
    //String x = (String) check_number_calc((barcode_data0 + barcode_data1 + barcode_data2)); 
    x = check_number_calc((ROC_date0 + "112" + electric_number + ROC_date1 + payment_money_format)); 
    
    
    // barcode image
    //Bitmap bitmap0 = null;
    //Bitmap bitmap1 = null;
    //Bitmap bitmap2 = null;
    //ImageView iv = new ImageView(this);
    
    bitmap0 = encodeAsBitmap( (ROC_date0 + "112") , BarcodeFormat.CODE_39, desired_width0[zoom_count], 300);
    barcode0 = (ImageView) findViewById(R.id.barcode00);
    barcode0.setImageBitmap(bitmap0);
	
    TextView barcode0_text = (TextView) findViewById(R.id.barcode00_text);
    barcode0_text.setText(ROC_date0 + "112");
    
    bitmap1 = encodeAsBitmap( (electric_number + ROC_date1), BarcodeFormat.CODE_39, desired_width1[zoom_count], 300);
	barcode1 = (ImageView) findViewById(R.id.barcode01);
	barcode1.setImageBitmap(bitmap1);
    
	TextView barcode1_text = (TextView) findViewById(R.id.barcode01_text);
    barcode1_text.setText(electric_number + ROC_date1);
	
	bitmap2 = encodeAsBitmap(("00000"+ x + payment_money_format), BarcodeFormat.CODE_39, desired_width2[zoom_count], 300);
	barcode2 = (ImageView) findViewById(R.id.barcode02);
	barcode2.setImageBitmap(bitmap2);
    
	TextView barcode2_text = (TextView) findViewById(R.id.barcode02_text);
    barcode2_text.setText("00000"+ x + payment_money_format);
	
    //TextView zoom_level = (TextView) findViewById(R.id.zoom_level);
	//zoom_level.setText(String.valueOf(zoom_count + 1));
	
	
    //l.addView(iv);

    //barcode text
    //TextView tv = new TextView(this);
    //tv.setGravity(Gravity.CENTER_HORIZONTAL);
    //tv.setText(barcode_data);

    //l.addView(tv);
	
	TextView warning_text = (TextView) findViewById(R.id.warning_text);
	
	Animation anim = new AlphaAnimation( 0.0f , 1.0f );
	anim.setRepeatCount(Animation.INFINITE);
	anim.setRepeatMode(Animation.REVERSE);
	anim.setDuration(500); //You can manage the time of the blink with this parameter
	anim.setStartOffset(0);
	
	warning_text.setAnimation(anim);
	
	
	ImageButton capture_picture_button_barcode = (ImageButton) findViewById(R.id.capture_picture_button_barcode); 
	
	capture_picture_button_barcode.setOnClickListener(new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			//Intent intent_camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    //startActivityForResult(intent_camera, 1);
			
			Intent intent_camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    // Ensure that there's a camera activity to handle the intent
		    if (intent_camera.resolveActivity(getPackageManager()) != null) 
		    {
		        // Create the File where the photo should go
		        //File photoFile = null;
		        try 
		        {
		            photoFile = createImageFile();
		        } 
		        catch (IOException ex) 
		        {
		            // Error occurred while creating the File
		        	Log.e("error", ex.getMessage());
		        }
		        // Continue only if the File was successfully created
		        if (photoFile != null) 
		        {
		        	intent_camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		            startActivityForResult(intent_camera, 1);
		        }
		    }
		}
	});
				
	
	String[] zoom_level_array= {"30%","35%","40%","45%","50%","55%","60%","65%","70%","75%","80%","85%","90%","95%",
								"100%",
								"105%","110%","115%","120%","125%","130%","135%","140%","145%","150%","155%","160%","165%","170%"};
	
	ArrayAdapter zoom_level_adapter =  new ArrayAdapter(app_context,android.R.layout.simple_dropdown_item_1line,zoom_level_array);
	
	Spinner zoom_level = (Spinner) findViewById(R.id.zoom_level); 
	zoom_level.setAdapter(zoom_level_adapter);
	zoom_level.setSelection(zoom_count);
	zoom_level.setOnItemSelectedListener(zoom_level_on_click_listener);
	
	
	
	
	//ImageButton zoom_in = (ImageButton) findViewById(R.id.zoom_in);
	//zoom_in.setOnClickListener(zoom_on_click_listener);
	
	//ImageButton zoom_out = (ImageButton) findViewById(R.id.zoom_out);
	//zoom_out.setOnClickListener(zoom_on_click_listener);		
		
	
    }
    
    
    private OnItemSelectedListener zoom_level_on_click_listener = new OnItemSelectedListener()
    {
    	@Override
		public void onItemSelected(AdapterView<?> parent, View view,int position, long id) 
    	{
			// TODO Auto-generated method stub
			
    		zoom_count = position;
    		
    		bitmap0 = null;
		    bitmap1 = null;
		    bitmap2 = null;
			
			bitmap0 = encodeAsBitmap( (ROC_date0 + "112") , BarcodeFormat.CODE_39, desired_width0[zoom_count], 300);
			//barcode0 = (ImageView) findViewById(R.id.barcode00);
			barcode0.setImageBitmap(bitmap0);
			
			bitmap1 = encodeAsBitmap( (electric_number + ROC_date1), BarcodeFormat.CODE_39, desired_width1[zoom_count], 300);
			//barcode1 = (ImageView) findViewById(R.id.barcode01);
			barcode1.setImageBitmap(bitmap1);
			
			bitmap2 = encodeAsBitmap(("00000"+ x + payment_money_format), BarcodeFormat.CODE_39, desired_width2[zoom_count], 300);
			//barcode2 = (ImageView) findViewById(R.id.barcode02);
			barcode2.setImageBitmap(bitmap2);
    		
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    /*
    private View.OnClickListener zoom_on_click_listener = new View.OnClickListener()
    {
    	@Override
		public void onClick(View v) 
    	{
			// TODO Auto-generated method stub
			if( v.getId() == R.id.zoom_in)
			{
				if( zoom_count >= 10 )
				{	
					zoom_count = 10;
					Toast.makeText(app_context, "已放到最大了,不要再按了", Toast.LENGTH_SHORT).show();
				}
				else 
					zoom_count += 1;
			}
			else
			{	
				if( zoom_count <= 0 )
				{
					zoom_count = 0; 
					Toast.makeText(app_context, "已放到最小了,不要再按了", Toast.LENGTH_SHORT).show();
				}
				else
					 zoom_count -= 1;
			}
				
			bitmap0 = null;
		    bitmap1 = null;
		    bitmap2 = null;
			
			
			bitmap0 = encodeAsBitmap( (ROC_date0 + "112") , BarcodeFormat.CODE_39, desired_width0[zoom_count], 300);
			barcode0 = (ImageView) findViewById(R.id.barcode00);
			barcode0.setImageBitmap(bitmap0);
			
			bitmap1 = encodeAsBitmap( (electric_number + ROC_date1), BarcodeFormat.CODE_39, desired_width1[zoom_count], 300);
			barcode1 = (ImageView) findViewById(R.id.barcode01);
			barcode1.setImageBitmap(bitmap1);
			
			bitmap2 = encodeAsBitmap(("00000"+ x + payment_money_format), BarcodeFormat.CODE_39, desired_width2[zoom_count], 300);
			barcode2 = (ImageView) findViewById(R.id.barcode02);
			barcode2.setImageBitmap(bitmap2);
			
			//TextView zoom_level = (TextView) findViewById(R.id.zoom_level);
			//zoom_level.setText(String.valueOf(zoom_count + 1));
			
		}
    	
    };
    
    */
    
    private  String check_number_calc(String String_data)
    {
    	String[] result_array = {"0","1","2","3","4","5","6","7","8","9",
				 				 "A","B","C","D","E","F","G","H","I","J",
				 				 "K","L","M","N","O","P","Q","R","S","T",
				 				 "U","V","W","X","Y","Z","-","."," ","$",						
				 				 "/","+","%"};
    	
    	int bill_result =  0;
		int length = 0;
		length = String_data.length();
		
		
		for(int x = 0; x < length ; x++ )
			bill_result += Integer.parseInt(String_data.substring(x, x+1) ) ;
		
		int mod_bill_result = bill_result % 43 ; 
		
		
				
    	return result_array[mod_bill_result];
    }
    
    
    
    
    
    protected  static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight)
    {  
            final int WHITE = 0xFFFFFFFF;   
            final int BLACK = 0xFF000000;  
              
            MultiFormatWriter writer = new MultiFormatWriter();  
            BitMatrix result = null;  
            try {  
                result = writer.encode(contents, format, desiredWidth,  
                        desiredHeight, null);  
            } catch (WriterException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
              
            int width = result.getWidth();  
            int height = result.getHeight();  
            int[] pixels = new int[width * height];  
            // All are 0, or black, by default  
            for (int y = 0; y < height; y++) {  
                int offset = y * width;  
                for (int x = 0; x < width; x++) {  
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;  
                }  
            }  

            Bitmap bitmap = Bitmap.createBitmap(width, height,  
                    Bitmap.Config.ARGB_8888);  
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
            return bitmap;  
        }
    
    
    
    
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data)
    {
    	// TODO Auto-generated method stub
    	 super.onActivityResult(requestCode, resultCode, data);
        
    	        
        // requestCode 1 拍照
        if(requestCode == 1)
        { 
        	if (resultCode == RESULT_OK)
        	{
        		//ImageView preview = (ImageView) findViewById(R.id.preview_capture_picture);
        		//取出拍照後回傳資料
        	    //Bundle extras = data.getExtras();
        	    //將資料轉換為圖像格式
        	    //Bitmap bmp = (Bitmap) extras.get("data");
        	    
        	    //preview.setImageBitmap(bmp);
        	    //preview.getLayoutParams().width = 50;
        	    
        	    // CALL THIS METHOD TO GET THE URI FROM THE thumbnail BITMAP
                //Uri camera_uri_path = getImageUri(getApplicationContext(), bmp);
                //TextView camera_uri_locate = (TextView) findViewById(R.id.capture_picture_uri_locate);
                //camera_uri_locate.setText(getRealPathFromURI(camera_uri_path));
                
        	    //TextView camera_uri_locate = (TextView) findViewById(R.id.capture_picture_barcode_location);
        	    
        	    //camera_uri_locate.setText(getRealPathFromURI(Uri.fromFile(photoFile)));
        	}
        }    
    }
    
    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ( keyCode == KeyEvent.KEYCODE_BACK) 
        {
            // your code
        	finish_warning();        	
        	
        	return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    
    private void finish_warning()
    {
    	AlertDialog.Builder conform_dialog = new AlertDialog.Builder(app_context);
		conform_dialog.setTitle("超商繳費小白單拍照了嗎!!");
		
		
		conform_dialog.setNegativeButton("好險!回去拍照", new OnClickListener()
		{
			@Override	
			public void onClick(DialogInterface dialog , int which) 
        	{
				dialog.dismiss();
        	}
		});			
		
		conform_dialog.setNeutralButton("已經拍了!", new OnClickListener()
		{
			@Override	
			public void onClick(DialogInterface dialog , int which) 
        	{
				Calendar calendar_data = Calendar.getInstance(); 
				int year = calendar_data.get(Calendar.YEAR) - 1911;
				int month = calendar_data.get(Calendar.MONTH) + 1;
				int date = calendar_data.get(Calendar.DATE) ;
				int hour = calendar_data.get(Calendar.HOUR);
				int minute = calendar_data.get(Calendar.MINUTE);
				
				Date date_object = new Date();
				String now = date_object.toString();
				
				DBonSQLite SQLite_for_barcode = new DBonSQLite( barcode_and_ebpps_barcode.this, "taipower_west_branch_app_data_base.db",null,1);
							
					
				SQLiteDatabase db_write = SQLite_for_barcode.getWritableDatabase();
			    ContentValues insert_db_values = new ContentValues();
			    insert_db_values.put("electric_number", electric_number );
			    insert_db_values.put("pay_money", payment_money_format );
			    insert_db_values.put("time", String.valueOf(year) + "/" + month + "/" + date + " " + hour + ":" + minute );
			    
			    if( photoFile != null)
			    	insert_db_values.put("picture_uri", getRealPathFromURI(Uri.fromFile(photoFile)) );
			    else
			    	insert_db_values.put("picture_uri", "" );
			    
			    if( !origin_from.equals("") )
			    	insert_db_values.put("origin_from", origin_from);
			    
			    	
			    db_write.insert("barcode_be_created_table", null, insert_db_values);
			    db_write.close();
			    SQLite_for_barcode.close();	
				
				dialog.dismiss();
				
				finish();
        	}
		});			
		
		conform_dialog.show();					
    }
    
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     * use onResume() when app wake up 
     * 
     */
    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	if(!bitmap0.isRecycled())
    		bitmap0.recycle();
    	if(!bitmap1.isRecycled())
    		bitmap1.recycle();
    	if(!bitmap2.isRecycled())
    		bitmap2.recycle();
    	
    	
    	bitmap0 = null;
	    bitmap1 = null;
	    bitmap2 = null;
	    
	    System.gc();
    }
    
    
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	bitmap0 = null;
	    bitmap1 = null;
	    bitmap2 = null;
    	
    	bitmap0 = encodeAsBitmap( (ROC_date0 + "112") , BarcodeFormat.CODE_39, desired_width0[zoom_count], 300);
		//barcode0 = (ImageView) findViewById(R.id.barcode00);
		barcode0.setImageBitmap(bitmap0);
		
		bitmap1 = encodeAsBitmap( (electric_number + ROC_date1), BarcodeFormat.CODE_39, desired_width1[zoom_count], 300);
		//barcode1 = (ImageView) findViewById(R.id.barcode01);
		barcode1.setImageBitmap(bitmap1);
		
		bitmap2 = encodeAsBitmap(("00000"+ x + payment_money_format), BarcodeFormat.CODE_39, desired_width2[zoom_count], 300);
		//barcode2 = (ImageView) findViewById(R.id.barcode02);
		barcode2.setImageBitmap(bitmap2);
    	
    }
    
}