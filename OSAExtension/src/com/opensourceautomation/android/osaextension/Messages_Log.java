

package com.opensourceautomation.android.osaextension;

import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.REFRESH_LOG_ACTION;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.opensourceautomation.android.osaextension.object.NotificationItem;
import com.opensourceautomation.android.osaextension.utilities.DBAdapter;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;

public class Messages_Log extends ListFragment {
    
    private View v;
    private static Context myContext;
    private static Devicelog mydevicelog;
    private ListView list;
    
    private ArrayList<NotificationItem> tempList = new ArrayList<NotificationItem>();
    private ArrayList<NotificationItem> m_notifications = null;
    
    static int LIST_SIZE; 
    private Date mLastDate;     
    static final int BATCH_SIZE = 10; 
    
	public static Messages_Log newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Messages_Log fragment = new Messages_Log();

		return fragment;
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        v = inflater.inflate(R.layout.messageslist, container, false);		
                
        if (myContext==null){
        	myContext=getActivity();
        	
        }
        
        if (myContext != null) {
            
        	if (mydevicelog==null){
            	mydevicelog= new Devicelog(myContext);
            }
            
        	initializeDB();

        }
        
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		Boolean objectlinked=mypreferences.getBoolean("objectlinked",false);
		
        if (!objectlinked){
        	openWizard();
        }
        
        list = (ListView)v.findViewById(android.R.id.list);
        
        init();

    	getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(REFRESH_LOG_ACTION));

        return v;
    }

    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        mydevicelog.log("main_log onDestroyView",Log.DEBUG);
        
        getActivity().unregisterReceiver(mHandleMessageReceiver);
    }

    
    
    private void init(){

    	//start with date 10 years from current, each time we call to get records we will get log records less than this date and then update it to last record we pulled
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.YEAR, 10);
    	mLastDate = new Date(c.getTimeInMillis());

    	tempList.clear(); 
        getNotifications(mLastDate);
        m_notifications = (ArrayList<NotificationItem>)tempList.clone();
                
        //m_adapter = new notificationAdapter(myContext, R.layout.log_row, m_notifications);
        setListAdapter(new endlessAdapter());	
    }
    
	private void initializeDB(){
    	DBAdapter dbAdapter=DBAdapter.getDBAdapterInstance(myContext);

		try {
			dbAdapter.createDataBase();
		}
		catch (IOException e) {
			mydevicelog.log(e.getMessage(),Log.ERROR);
		}
		
		dbAdapter.close();
		
    }
    
    private Boolean getNotifications(Date lastDate){
    	
    	Boolean moredata=true;
    	
    	DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(myContext);
        dbAdapter.openDataBase();
    	
        //only select records where notifydate<lastDate limit BATCH_SIZE
        SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
        
        String[] strArgs= {
        		sdf.format(lastDate),
        		"#taskertask#",
     			Integer.toString(BATCH_SIZE)
        };
        
        String query="SELECT _id, osaid, message, category, level, deleted, notifydate, notification, alert, speech, toast, popup, vibrate FROM notification_log where notifydate<=? and category<>? order by notifydate desc LIMIT ?;";
        ArrayList<ArrayList<String>> stringList = dbAdapter.selectRecordsFromDBList(query, strArgs);
        dbAdapter.close();

        //m_notifications = new ArrayList<notification>();
        
        for (int i = 0; i < stringList.size(); i++) {
        	ArrayList<String> list = stringList.get(i);
        	NotificationItem a1 = new NotificationItem();
            try {
            	a1.setAll(list);
            	
            } catch (Exception e) {
            	mydevicelog.log(e.getMessage(),Log.ERROR);
            }
            tempList.add(a1);
        }
        
        if (tempList.size()>0){
        	mLastDate = tempList.get(tempList.size()-1).getNotifydate();
        }
        

        if (stringList.size()<BATCH_SIZE){
        	moredata=false;
        }
        
        return moredata;
        
      }
    
    
    private void openWizard(){
    	
    	Intent myIntent = new Intent(myContext,WizardActivity.class); 
    	myContext.startActivity(myIntent); 
    	    	
    }
    
    
    class endlessAdapter extends EndlessAdapter { 
    	private RotateAnimation rotate=null; 
    	
    	endlessAdapter() { 
    		super(new notificationAdapter(myContext,  
    				 R.layout.log_row, m_notifications )); 

    		rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 
    				0.5f, Animation.RELATIVE_TO_SELF, 
    				0.5f); 
    		rotate.setDuration(600); 
    		rotate.setRepeatMode(Animation.RESTART); 
    		rotate.setRepeatCount(Animation.INFINITE); 
    	} 

    	@Override
    	protected View getPendingView(ViewGroup parent) {
    		LayoutInflater vi = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View row=vi.inflate(R.layout.log_row, null); 

    		View child=row.findViewById(R.id.mainlayout); 
    		child.setVisibility(View.GONE); 
    		child=row.findViewById(R.id.throbber); 
    		child.setVisibility(View.VISIBLE); 
    		child.startAnimation(rotate); 

    		return(row); 
    	} 

    	@Override
    	protected boolean cacheInBackground() { 
    		tempList.clear(); 
   			return getNotifications(mLastDate);
    	} 


    	@Override
    	protected void appendCachedData() { 

    		@SuppressWarnings("unchecked") 
    		ArrayAdapter<NotificationItem> arrAdapterNew = (ArrayAdapter<NotificationItem>)getWrappedAdapter(); 

    		int listLen = tempList.size(); 
    		for(int i=0; i<listLen; i++){ 
    			arrAdapterNew.add(tempList.get(i)); 
    		} 
    	} 
    } 
    
    private class notificationAdapter extends ArrayAdapter<NotificationItem> {

        public notificationAdapter(Context context, int textViewResourceId, ArrayList<NotificationItem> messages) {
                super(context, textViewResourceId, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater vi = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.log_row, null);

                NotificationItem n = m_notifications.get(position);
                if (n != null) {
                		TextView messagecategory = (TextView) v.findViewById(R.id.messagecategory);
                        TextView messagetext = (TextView) v.findViewById(R.id.messagetext);
                        TextView messagetime = (TextView) v.findViewById(R.id.messagetime);
                        
                        ImageView imageNotification = (ImageView) v.findViewById(R.id.imageNotification);
                        ImageView imageAlert = (ImageView) v.findViewById(R.id.imageAlert);
                        ImageView imageSpeech = (ImageView) v.findViewById(R.id.imageSpeech);
                        ImageView imageVibrate = (ImageView) v.findViewById(R.id.imageVibrate);

                        
                        if (messagecategory !=null){
                        	messagecategory.setText(n.getCategory()+" ("+n.getLevel()+")"); 
                        }
                        
                        if (messagetext != null) {
                        	messagetext.setText(n.getMessage()); 
                        }
                        if (messagetime != null) {
                        	
                        	SimpleDateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss.SSS a");  
                        	String formattedDateString = formatter.format(n.getNotifydate()); 
                        	messagetime.setText(formattedDateString); 
                        }
                       
                        if (n.getNotification()){
                        	imageNotification.setImageResource(R.drawable.type_notification_on);
                        }
                        if (n.getAlert()){
                        	imageAlert.setImageResource(R.drawable.type_alert_on);
                        }
                        if (n.getSpeech()){
                        	imageSpeech.setImageResource(R.drawable.type_speak_on);
                        }
                        if (n.getVibrate()){
                        	imageVibrate.setImageResource(R.drawable.type_vibrate_on);
                        }                        
                        
                }
                return v;
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	init();
        	

        }
    };

}