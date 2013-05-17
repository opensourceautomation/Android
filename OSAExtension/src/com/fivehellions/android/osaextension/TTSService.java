package com.fivehellions.android.osaextension;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TTSService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech mTts;
    private String spokenText="test";

    private static final String TAG = "OSA TTSService";
    
    private Boolean isinitialized=false;
    
    private int numberofspeaks=0;
    
    @Override
    public void onCreate() {
        Log.d(TAG, "TTSService oncreate ");
        
        
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent, flags, startId);
        
        Log.d(TAG, "TTSService onstartcommand ");
        
        Bundle myBundle = intent.getExtras();
        spokenText = myBundle.getString("spokenText");
 
        if (mTts != null) {
        	
        	int checkloop=0;
        	while (!isinitialized && checkloop<30){
        		checkloop++;
        		try {
        			Log.d(TAG, "TTSService waiting for initialize ");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	speak();
        } else {
        	mTts = new TextToSpeech(this, this);
        }
        
        return START_STICKY;
    }

    @Override
    public void onInit(int status) {
    	
    	Log.d(TAG, "TTSService onInit ");
    	
        if (status == TextToSpeech.SUCCESS) {
            //int result = mTts.setLanguage(Locale.US);
            //if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
        
        	isinitialized=true;
        	
        	speak();
           // }
        }
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
    	numberofspeaks--;
    	
    	Log.d(TAG, "TTSService onUtteranceCompleted, numberofspeaks- "+Integer.toString(numberofspeaks));
    	
    	if (mTts != null) {
        	//if (!mTts.isSpeaking()){
    		if (numberofspeaks<=0){
        		Log.d(TAG, "TTSService shutting down mTts ");
                mTts.stop();
                mTts.shutdown();    		
                mTts=null;
        	}
        }

    }

    @Override
    public void onDestroy() {
    	Log.d(TAG, "TTSService ondestroy ");
    	
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    private void speak(){
    	numberofspeaks++;
    	
    	Log.d(TAG, "TTSService speak(), numberofspeaks- "+Integer.toString(numberofspeaks));
    	
    	mTts.setOnUtteranceCompletedListener(this);

    	HashMap<String, String> sTtsParams = new HashMap<String, String>();
		sTtsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_NOTIFICATION+"");
		sTtsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,Integer.toString(numberofspeaks));
		
		mTts.speak(spokenText, TextToSpeech.QUEUE_ADD, sTtsParams);
    }
	
}