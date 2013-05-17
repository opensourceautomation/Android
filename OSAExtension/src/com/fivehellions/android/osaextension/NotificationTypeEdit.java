package com.fivehellions.android.osaextension;

import static com.fivehellions.android.osaextension.CommonUtilities.RESULT_CANCEL;
import static com.fivehellions.android.osaextension.CommonUtilities.RESULT_DELETE;
import static com.fivehellions.android.osaextension.CommonUtilities.RESULT_SAVE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fivehellions.android.osaextension.RangeSeekBar.OnRangeSeekBarChangeListener;

public class NotificationTypeEdit extends Activity {

    public interface ReadyListener {
        public void ready(NotificationType mynotificationtype,int result);
    }

    private int MY_RESULT=RESULT_CANCEL;
    
    private static int RESULT_RINGTONE_PICKED = 1;
    
    private Context myContext;
    private Devicelog mydevicelog;
	
    
    private NotificationType mynotificationtype;

    private EditText editCategoryName;
    private CheckBox checknotification;
    private CheckBox checkalert;
    private CheckBox checkspeech;
    private CheckBox checkvibrate;
    
    private TextView minTxt;
    private TextView maxTxt;
    private TextView alertsoundTxt;
    	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */ 
        //requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.notificationtypeedit);
    	
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        Intent myIntent = getIntent();
        
        if (myIntent!=null){
	    	if (myIntent.hasExtra("mynotificationtype")) {
	    		mynotificationtype = (NotificationType) myIntent.getSerializableExtra("mynotificationtype");
	        }else{
	        	finish();
	        }
        }else{
        	finish();
        }
        
        myContext=this;
        mydevicelog= new Devicelog(myContext);
        
        ImageButton Btn_Save = (ImageButton) findViewById(R.id.Btn_Save);
        Btn_Save.setOnClickListener(new SaveListener());
        
        ImageButton Btn_Delete = (ImageButton) findViewById(R.id.Btn_Delete);
        Btn_Delete.setOnClickListener(new DeleteListener());
        
        ImageButton Btn_Cancel = (ImageButton) findViewById(R.id.Btn_Cancel);
        Btn_Cancel.setOnClickListener(new CancelListener());
        
        ImageButton Btn_PickAlert = (ImageButton) findViewById(R.id.Btn_PickAlert);
        Btn_PickAlert.setOnClickListener(new RingtonePickListener());
        
        
        editCategoryName = (EditText) findViewById(R.id.editCategoryName);
        checknotification = (CheckBox) findViewById(R.id.checknotification);
        checkalert = (CheckBox) findViewById(R.id.checkalert);
        checkspeech = (CheckBox) findViewById(R.id.checkspeech);
        checkvibrate = (CheckBox) findViewById(R.id.checkvibrate);
        
        //set values
        editCategoryName.setText(mynotificationtype.getCategory());
        checknotification.setChecked(mynotificationtype.getNotification());
        checkalert.setChecked(mynotificationtype.getAlert());
        checkspeech.setChecked(mynotificationtype.getSpeech());
        checkvibrate.setChecked(mynotificationtype.getVibrate());
        
        
        checknotification.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            	mynotificationtype.setNotification(isChecked);
            }
        });
       
        checkalert.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            	mynotificationtype.setAlert(isChecked);
            }
        });
        
        checkspeech.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            	mynotificationtype.setSpeech(isChecked);
            }
        });
        
        checkvibrate.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            	mynotificationtype.setVibrate(isChecked);
            }
        });
        
        
        
     // create RangeSeekBar as Integer range between 20 and 75
        minTxt = (TextView) findViewById(R.id.minTxt);
        maxTxt = (TextView) findViewById(R.id.maxTxt);
        
        minTxt.setText("From "+Integer.toString(mynotificationtype.getLevel_start()));
        maxTxt.setText("To "+Integer.toString(mynotificationtype.getLevel_end()));
                
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(1, 10, myContext);
        
        seekBar.setSelectedMinValue(mynotificationtype.getLevel_start());
        seekBar.setSelectedMaxValue(mynotificationtype.getLevel_end());
        seekBar.setNotifyWhileDragging(true);
        
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                        mynotificationtype.setLevel_start(minValue);
                        mynotificationtype.setLevel_end(maxValue);
                        
                        minTxt.setText("From "+Integer.toString(minValue));
                        maxTxt.setText("To "+Integer.toString(maxValue));
                        
                }
        });

        // add RangeSeekBar to pre-defined layout
        ViewGroup seekbarLayout = (ViewGroup) findViewById(R.id.seekbarLayout);
        seekbarLayout.addView(seekBar);
        
        alertsoundTxt = (TextView) findViewById(R.id.alertsoundTxt);
        
        if (mynotificationtype.getAlertsound()!=null){
	        Uri ringtoneUri = Uri.parse(mynotificationtype.getAlertsound());
	        Ringtone ringtone = RingtoneManager.getRingtone(myContext, ringtoneUri);
	        if (ringtone!=null){
	        	alertsoundTxt.setText(ringtone.getTitle(myContext).trim());
        	}
        } else {
        	alertsoundTxt.setText("Silent");
        }

               
    }

    
    @Override
    public void finish() {
    	// Prepare data intent 
    	Intent data = new Intent();
    	if (MY_RESULT==RESULT_SAVE || MY_RESULT==RESULT_DELETE){
			
			data.putExtra("mynotificationtype", mynotificationtype);
	    	
    	}

		// Activity finished ok, return the data
		setResult(MY_RESULT, data);
		super.finish();
    } 
    
    private class SaveListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
        	
        	mynotificationtype.setCategory(editCategoryName.getText().toString());
        	
        	MY_RESULT=RESULT_SAVE;
        	finish();
        	
        	//readyListener.ready(mynotificationtype,RESULT_SAVE);
        	//NotificationTypeEdit.this.dismiss();

        }
    }
    
    
    private class DeleteListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
        	
        	MY_RESULT=RESULT_DELETE;
        	finish();
        	
        	//readyListener.ready(mynotificationtype,RESULT_DELETE);
        	//NotificationTypeEdit.this.dismiss();
        }
    }   
    
    private class CancelListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
        	
        	mynotificationtype=null;
        	MY_RESULT=RESULT_CANCEL;
        	finish();
        	        	
        	//readyListener.ready(mynotificationtype,RESULT_CANCEL);
        	//NotificationTypeEdit.this.dismiss();
        }
    }   
    
    private class RingtonePickListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
        	String uri = mynotificationtype.getAlertsound();            
        	Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);            
        	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)); 
        	intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");            
        	if( uri != null) {                 
        		intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse( uri));
        	} else {
        		intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,(Uri)null);
        	}
        	startActivityForResult( intent, RESULT_RINGTONE_PICKED);
        }
    }  
 
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode== RESULT_RINGTONE_PICKED){
        	
        	if (resultCode == RESULT_OK) {
        		Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

        		String ringTonePath=null;
        		if (uri != null){
        			ringTonePath = uri.toString();
        		} 
        		
    			mynotificationtype.setAlertsound(ringTonePath);
    			
    	        if (ringTonePath!=null){
    		        Uri ringtoneUri = Uri.parse(ringTonePath);
    		        Ringtone ringtone = RingtoneManager.getRingtone(myContext, ringtoneUri);
    		        
    		        if (ringtone==null){
    		        	mynotificationtype.setAlertsound("default"); 
    		        	alertsoundTxt.setText("default");
    		        } else {
	    		        if (ringtone.getTitle(myContext).trim().startsWith("Default ringtone")){
	    		        	mynotificationtype.setAlertsound("default"); // we won't store the path... that way if they change the default system notificatiion sound we will use the newly selected sound instead of the old default
	    		        	alertsoundTxt.setText("default");
	    		        } else {
	    		        	alertsoundTxt.setText(ringtone.getTitle(myContext).trim());
	    		        }
    		        	
    		        }
    	        } else {
    	        	alertsoundTxt.setText("Silent");
    	        }

    	        
    	        
        	}
        	
        }
     
    }
    
}