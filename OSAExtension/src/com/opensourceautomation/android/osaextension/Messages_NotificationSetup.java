package com.opensourceautomation.android.osaextension;


import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.RESULT_DELETE;
import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.RESULT_SAVE;

import java.io.IOException;
import java.util.ArrayList;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.object.NotificationType;
import com.opensourceautomation.android.osaextension.utilities.DBAdapter;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Messages_NotificationSetup extends ListFragment {
    
    private View v;
    private static Context myContext;
    private static Devicelog mydevicelog;

    private ArrayList<NotificationType> m_notificationtypes = null;
    private notificationTypeAdapter m_adapter;
        
    private static int RESULT_NOTIFICATION_EDIT = 1;
    
	public static Messages_NotificationSetup newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Messages_NotificationSetup fragment = new Messages_NotificationSetup();

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
        if (mydevicelog==null){
        	mydevicelog= new Devicelog(myContext);
        }
        
        initializeDB();
        
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		Boolean objectlinked=mypreferences.getBoolean("objectlinked",false);
		
        if (!objectlinked){
        	//openWizard();
        }
        
        ListView list = (ListView)v.findViewById(android.R.id.list);
        registerForContextMenu(list);
        
        loadData();
        
        LinearLayout bottombar = (LinearLayout)v.findViewById(R.id.bottombar);
        bottombar.setVisibility(View.VISIBLE);
        
        ImageButton Btn_Add = (ImageButton)v.findViewById(R.id.Btn_Add);
        Btn_Add.setOnClickListener(new AddListener());
        return v;
    }

    private class AddListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
        	//pass -99 to readyListener to indicate we canceled
        	NotificationType obj = new NotificationType();
        	opennotificationtypeedit(obj);
        }
    }   
    

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
			
			NotificationType obj = new NotificationType();
			
			//obj = m_notificationtypes.get(position);
			obj = (NotificationType)getListView().getItemAtPosition(position);
			opennotificationtypeedit(obj);

			//start NotificationTypeEdit for results
		
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(m_notificationtypes.get(info.position).getCategory());
			String[] menuItems = getResources().getStringArray(R.array.menu);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.menu);
		String menuItemName = menuItems[menuItemIndex];
		String listItemName = m_notificationtypes.get(info.position).getCategory();

		if (menuItemIndex==0){
			opennotificationtypeedit(m_notificationtypes.get(info.position));	
		}
		if (menuItemIndex==1){
			deleteNotificationType(m_notificationtypes.get(info.position));
			loadData();
		}		
		if (menuItemIndex==2){
			//clone
		}			
		
		
		mydevicelog.log(String.format("Selected %s for item %s", menuItemName, listItemName),Log.DEBUG);
		return true;
	}
	
    private void loadData(){
                
        m_notificationtypes = new ArrayList<NotificationType>();
        getNotificationtypes();
        
        m_adapter = new notificationTypeAdapter(myContext, R.layout.log_row, m_notificationtypes);
        setListAdapter(m_adapter);	
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
    
    private void getNotificationtypes(){
    	
    	DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(myContext);
        dbAdapter.openDataBase();
    	
        String query="SELECT _id, category, level_start, level_end, notification, alert, alertsound, speech, toast, popup, vibrate FROM notification_actions;";
        ArrayList<ArrayList<String>> stringList = dbAdapter.selectRecordsFromDBList(query, null);
        dbAdapter.close();

        //m_notifications = new ArrayList<notification>();
        
        for (int i = 0; i < stringList.size(); i++) {
        	ArrayList<String> list = stringList.get(i);
        	NotificationType a1 = new NotificationType();
            try {
            	a1.setAll(list);
            	
            } catch (Exception e) {
            	mydevicelog.log(e.getMessage(),Log.ERROR);
            }
            m_notificationtypes.add(a1);
        }
        
      }
    
    
    private void openWizard(){
    	
    	Intent myIntent = new Intent(myContext,WizardActivity.class); 
    	myContext.startActivity(myIntent); 
    	    	
    }
    
    private void opennotificationtypeedit(NotificationType mynotificationtype){
    	
    	Intent i = new Intent(myContext, NotificationTypeEdit.class);
    	i.putExtra("mynotificationtype", mynotificationtype);
    	startActivityForResult(i, RESULT_NOTIFICATION_EDIT);

    }       
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		mydevicelog.log("requestCode:"+requestCode+" resultCode:"+resultCode, Log.DEBUG);

		NotificationType returnnotificationtype;
		
		if (resultCode == RESULT_SAVE && requestCode == RESULT_NOTIFICATION_EDIT) {
	    	if (data.hasExtra("mynotificationtype")) {
	    		returnnotificationtype = (NotificationType) data.getSerializableExtra("mynotificationtype");
	    		
	    		saveNotificationType(returnnotificationtype);
	        }
		}
		
		if (resultCode == RESULT_DELETE && requestCode == RESULT_NOTIFICATION_EDIT) {
			if (data.hasExtra("mynotificationtype")) {
				returnnotificationtype = (NotificationType) data.getSerializableExtra("mynotificationtype");
	    		
	    		deleteNotificationType(returnnotificationtype);
	        }
		}	
		
		loadData();
    } 
	
    private void deleteNotificationType(NotificationType mynotificationtype){
    	
    	DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(myContext);
        dbAdapter.openDataBase();
        
        String [] strArray = {Integer.toString(mynotificationtype.getId())};
		long n = dbAdapter.deleteRecordInDB("notification_actions", "_id=?", strArray);
        
		mydevicelog.log(n+" notification_actions deleted",Log.DEBUG);
		
        dbAdapter.close();
    }
    
    private void saveNotificationType(NotificationType mynotificationtype){
    
        DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(myContext);
        dbAdapter.openDataBase();

        ContentValues initialValues = new ContentValues();
		initialValues.put("category", mynotificationtype.getCategory());
		initialValues.put("level_start", mynotificationtype.getLevel_start());
		initialValues.put("level_end", mynotificationtype.getLevel_end());
		initialValues.put("notification", (mynotificationtype.getNotification()) == true ? 1 : 0);
		initialValues.put("alert", (mynotificationtype.getAlert()) == true ? 1 : 0);
		initialValues.put("alertsound", mynotificationtype.getAlertsound());
		initialValues.put("speech", (mynotificationtype.getSpeech()) == true ? 1 : 0);
		initialValues.put("toast", (mynotificationtype.getToast()) == true ? 1 : 0);
		initialValues.put("popup", (mynotificationtype.getPopup()) == true ? 1 : 0);
		initialValues.put("vibrate", (mynotificationtype.getVibrate()) == true ? 1 : 0);
					
        if (mynotificationtype.getId()<=0){
			long n = dbAdapter.insertRecordsInDB("notification_actions", null, initialValues);
			
			mydevicelog.log(n+" new cheat created",Log.DEBUG);
        }
        else{
			String [] strArray = {Integer.toString(mynotificationtype.getId())};
			long n = dbAdapter.updateRecordsInDB("notification_actions", initialValues, "_id=?", strArray);
			
			mydevicelog.log(n+" notification_actions updated",Log.DEBUG);
        }
        
        dbAdapter.close();
        
    }
    
    private class notificationTypeAdapter extends ArrayAdapter<NotificationType> {

        public notificationTypeAdapter(Context context, int textViewResourceId, ArrayList<NotificationType> messages) {
                super(context, textViewResourceId, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.notificationtype_row, null);
                }
                NotificationType n = m_notificationtypes.get(position);
                if (n != null) {
                        TextView categorytext = (TextView) v.findViewById(R.id.categorytext);
                        TextView levelrange = (TextView) v.findViewById(R.id.levelrange);
                        
                        ImageView imageNotification = (ImageView) v.findViewById(R.id.imageNotification);
                        ImageView imageAlert = (ImageView) v.findViewById(R.id.imageAlert);
                        ImageView imageSpeech = (ImageView) v.findViewById(R.id.imageSpeech);
                        ImageView imageVibrate = (ImageView) v.findViewById(R.id.imageVibrate);

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
                        
                        if (categorytext != null) {
                        	categorytext.setText(n.getCategory()); 
                        }
                        if (levelrange != null) {
                        	
                        	String formattedLevelString = n.getLevel_start()+" - "+n.getLevel_end();
                        	levelrange.setText(formattedLevelString); 
                        }
                       
                        
                }
                return v;
        }
    }


}