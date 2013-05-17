
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
package com.fivehellions.android.osaextension;


import static com.fivehellions.android.osaextension.CommonUtilities.DISPLAY_TESTMESSAGE_ACTION;
import static com.fivehellions.android.osaextension.CommonUtilities.EXTRA_MESSAGE;
import static com.fivehellions.android.osaextension.CommonUtilities.REFRESH_LOG_ACTION;
import static com.fivehellions.android.osaextension.CommonUtilities.REGISTER_RESULTS_ACTION;
import static com.fivehellions.android.osaextension.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService{

	private Context myContext;
	private Devicelog mydevicelog;
	
    private TextToSpeech mTts;
    private String ttsmessage="no message";
    
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
        
        
        myContext=this;
		mydevicelog= new Devicelog(myContext);

        Log.d(TAG, "start GCMIntentService ");
		
    }

    @Override
	public void onDestroy(){
    	mydevicelog.log("on destroy", Log.DEBUG);
    	
        if (mTts != null) {
        	mydevicelog.log("TTS shutdown", Log.DEBUG);
        	
            mTts.stop();
            mTts.shutdown();
        }
        
    	super.onDestroy();
    }
    
    @Override
    protected void onRegistered(Context context, String registrationId) {
        registerResult(context,true,registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        mydevicelog.log("Device unregistered", Log.DEBUG);
        registerResult(context,false,"");
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        mydevicelog.log("Received message", Log.DEBUG);
        
        myContext=context;
        //String message = getString(R.string.gcm_message);
        
        String message="no message sent";
        String category="none";
        String level="0";
        String osaid="-1";
        String messagedate="";
                
        Bundle myBundle = intent.getExtras();
        
        if (myBundle!=null){
            message = myBundle.getString("message");
            category = myBundle.getString("category");
            level = myBundle.getString("level");
            osaid = myBundle.getString("osaid");  
            messagedate = myBundle.getString("messagedate");  
        }
        
        logMessage(context, message, category, level, osaid, messagedate);

    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        mydevicelog.log("Received deleted messages notification", Log.DEBUG);
        
    }

    @Override
    public void onError(Context context, String errorId) {
    	mydevicelog.log("Received error: " + errorId, Log.DEBUG);
        registerResult(context,false,"");
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
    	mydevicelog.log("Received recoverable error: " + errorId, Log.DEBUG);
        return super.onRecoverableError(context, errorId);
    }

    
    static void registerResult(Context context, Boolean results, String regid) {
        Intent intent = new Intent(REGISTER_RESULTS_ACTION);
        intent.putExtra("results", results);
        intent.putExtra("regid", regid);
        context.sendBroadcast(intent);
    }
    


	private void logMessage(Context myContext, String message, String category, String level, String osaid, String messagedate) {

		if (message.equalsIgnoreCase("Hello OSA")){
			Intent intent2 = new Intent(DISPLAY_TESTMESSAGE_ACTION);
			intent2.putExtra(EXTRA_MESSAGE, message);
			myContext.sendBroadcast(intent2);
		}

		int mylevel;

		try {
			mylevel = Integer.parseInt(level);
		} catch (NumberFormatException e1) {
			mylevel=1;
		}

		if (mylevel<1){
			mylevel=1;
		}

		if (mylevel>10){
			mylevel=10;
		}

		category=category.trim();
		
		DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(myContext);
		dbAdapter.openDataBase();

		// look up matching category and level
		// if not found then create one copying default category

		Boolean foundmatch=false;

		// variables for keeping track of range to create if we don't find a level match
		int rangestart=1;
		int rangeend=10;

		//object to store our notification type values
		NotificationType mynotificationtype = new NotificationType();

		//build and run the query
		ArrayList<ArrayList<String>> resultsList=null;
		String query="SELECT _id, category, level_start, level_end, notification, alert, alertsound, speech, toast, popup, vibrate FROM notification_actions where category=? COLLATE NOCASE order by level_start;";
		String[] strArgs= {
				category
		};


		try {
			resultsList = dbAdapter.selectRecordsFromDBList(query, strArgs);
		} catch (SQLiteException e){
			mydevicelog.log(e.getMessage(), Log.ERROR);
		}        	

		//loop results and look for level match
		if (resultsList !=null){
			for (int i = 0; i < resultsList.size(); i++) {

				if (!foundmatch){
					ArrayList<String> results = resultsList.get(i);

					int level_start;
					int level_end;

					try {
						level_start = Integer.parseInt(results.get(2));
					} catch (NumberFormatException e) {
						level_start=11; // if not valid then make sure no message falls in that group
					}

					try {
						level_end = Integer.parseInt(results.get(3));
					} catch (NumberFormatException e) {
						level_end=0; // if not valid then make sure no message falls in that group
					}

					if (mylevel>=level_start && mylevel<=level_end){
						foundmatch=true;
						mynotificationtype.setAll(results);
					} else {
						if (level_end<mylevel){
							rangestart=level_end+1;
						}
						if (level_start>mylevel){
							rangeend=level_start-1;
						}		         		
					}
				}
			}
		}

		if (foundmatch==false){ //create matching entry

			//select default options from database
			//build and run the query
			ArrayList<ArrayList<String>> defaultresult=null;
			String query2="SELECT _id, category, level_start, level_end, notification, alert, alertsound, speech, toast, popup, vibrate FROM notification_actions where category=? and ?>=level_start and ?<=level_end;";
			String[] strArgs2= {
					"default",
					"1"
			};
			try {
				defaultresult = dbAdapter.selectRecordsFromDBList(query2, strArgs2);
			} catch (SQLiteException e){
				mydevicelog.log(e.getMessage(), Log.ERROR);
			}    


			if (defaultresult !=null){
				if (defaultresult.size()>0){
					mynotificationtype.setAll(defaultresult.get(0));
				} else {
					mynotificationtype.setNotification(true);
				}
			} else {
				// if we have no default then we will default to show a notification only
				mynotificationtype.setNotification(true);
			}

			//set our custom values
			mynotificationtype.setCategory(category);
			mynotificationtype.setLevel_start(rangestart);
			mynotificationtype.setLevel_end(rangeend);

			//insert record into database
			ContentValues initialValues = new ContentValues();
			initialValues.put("category", mynotificationtype.getCategory());
			initialValues.put("level_start", mynotificationtype.getLevel_start());
			initialValues.put("level_end", mynotificationtype.getLevel_end());
			initialValues.put("notification", (mynotificationtype.getNotification()) ? 1 : 0 );
			initialValues.put("alert", (mynotificationtype.getAlert()) ? 1 : 0 );
			initialValues.put("alertsound", mynotificationtype.getAlertsound());
			initialValues.put("speech", (mynotificationtype.getSpeech()) ? 1 : 0 );
			initialValues.put("toast", (mynotificationtype.getToast()) ? 1 : 0 );
			initialValues.put("popup", (mynotificationtype.getPopup()) ? 1 : 0 );
			initialValues.put("vibrate", (mynotificationtype.getVibrate()) ? 1 : 0 );
			
			Long n = dbAdapter.insertRecordsInDB("notification_actions", null, initialValues);
			mydevicelog.log("inserted record in notification_actions - "+Long.toString(n)+" total record(s) ", Log.INFO);	

			mynotificationtype.setId(dbAdapter.getLastId());

		}


		// insert message into database log
		ContentValues initialValues = new ContentValues();
		initialValues.put("osaid", osaid);
		initialValues.put("message", message);
		initialValues.put("category", category);
		initialValues.put("level", mylevel);
		initialValues.put("notifydate", messagedate);
		initialValues.put("notification", (mynotificationtype.getNotification()) ? 1 : 0 );
		initialValues.put("alert", (mynotificationtype.getAlert()) ? 1 : 0 );
		initialValues.put("speech", (mynotificationtype.getSpeech()) ? 1 : 0 );
		initialValues.put("toast", (mynotificationtype.getToast()) ? 1 : 0 );
		initialValues.put("popup", (mynotificationtype.getPopup()) ? 1 : 0 );
		initialValues.put("vibrate", (mynotificationtype.getVibrate()) ? 1 : 0 );
		
		Long n = dbAdapter.insertRecordsInDB("notification_log", null, initialValues);
		mydevicelog.log("inserted record in notification_log - "+Long.toString(n)+" total record(s) ", Log.INFO);	

		query="SELECT _id, osaid, message, category, level, deleted, notifydate, notification, alert, speech, toast, popup, vibrate FROM notification_log where _id=last_insert_rowid();";
		ArrayList<ArrayList<String>> stringList = dbAdapter.selectRecordsFromDBList(query, null);

		dbAdapter.close();
		
		NotificationItem mynotification = new NotificationItem();
		if (stringList.size()>0){
			mynotification.setAll(stringList.get(0));
		}

		// take required actions
		generateNotification(mynotification,mynotificationtype.getAlertsound()); //for now we will just show the old notification until we build all the parts
		
        Intent intent = new Intent(REFRESH_LOG_ACTION);
//      intent.putExtra(EXTRA_MESSAGE, message); // don't need to pass these because now all we are doing is refreshing the listview
//      intent.putExtra(EXTRA_CATEGORY, category);
//      intent.putExtra(EXTRA_LEVEL, level);
//      intent.putExtra(EXTRA_OSAID, osaid);
//      intent.putExtra(EXTRA_DATE, messagedate);
        myContext.sendBroadcast(intent);
      
	}

	private void generateNotification(NotificationItem mynotification, String alertsound) {

		if (mynotification.getNotification()){ 


			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(myContext);


			mBuilder.setSmallIcon(R.drawable.ic_stat_gcm);
			mBuilder.setContentTitle(mynotification.getMessage());
			mBuilder.setContentText(mynotification.getCategory()+" ("+Integer.toString(mynotification.getLevel())+")");
			mBuilder.setAutoCancel(true);

			if (mynotification.getAlert()){
				//Define sound URI
				Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				if (alertsound!="" && !alertsound.equalsIgnoreCase("default")){
					soundUri = Uri.parse(alertsound);
				}

				mBuilder.setSound(soundUri); //This sets the sound to play
			}

			if (mynotification.getVibrate()){
				
				mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

			}
			
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(myContext, MainActivity.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(myContext);

			// Adds the back stack for the Intent (but not the Intent itself)
			//stackBuilder.addParentStack(ResultActivity.class);

			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

			// mId allows you to update the notification later on.
			mNotificationManager.notify(mynotification.getId(), mBuilder.build());

		}	  

		if (mynotification.getAlert() && !mynotification.getNotification()){



			//Define sound URI
			Uri soundUri=null;
			
			if (alertsound!=null){
				if (alertsound.equals("") || alertsound.equalsIgnoreCase("default")){
					soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				} else {
					soundUri = Uri.parse(alertsound);
				}
			}
			
			if (soundUri!=null){ // null = silence so we won't play anything
				MediaPlayer mp = new MediaPlayer();
				mp.reset();
				
				mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				
				try {
					mp.setDataSource(myContext, soundUri);
					mp.prepare();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mp.start();
			}

		}
		
		if (mynotification.getVibrate() && !mynotification.getNotification()){
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long [] mypattern= {0,250,200,250};
			v.vibrate(mypattern, -1);
		}
		
		if (mynotification.getSpeech()){
			mydevicelog.log("Speak", Log.DEBUG);
			
			ttsmessage=mynotification.getMessage();

			Intent myIntent = new Intent(myContext, TTSService.class);
        	
        	Bundle myBundle = new Bundle();
        	myBundle.putString("spokenText", mynotification.getMessage());
         	myIntent.putExtras(myBundle);
			
			myContext.startService(myIntent);
		}

//		if (mynotification.getToast()){
//			Context context = getApplicationContext();
//			CharSequence text = mynotification.getMessage();
//			int duration = Toast.LENGTH_LONG;
//			Toast toast = Toast.makeText(context, text, duration);
//			
//			toast.show();
//		}
		

	}


}
