package com.opensourceautomation.android.osaextension;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.opensourceautomation.android.osaextension.utilities.DBAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity 
{
	public static final String PREFS_NAME = "osaPrefsFile";	
    private final String tag = "OSA";     
    private static final int SPEECH_REQUEST = 1;    
    private static final int PREFERENCE_REQUEST = 2;
    
    String serverIP; 
	String serverPort;
	String serverHttpPort;
	String defaultPage;
	
	private ListView speechList;
	private AlertDialog speechListAlert;
	private WebView mWebView;	 
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	loadPref();
    	   
    	this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
    	LoadWebViewData();
    	getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON); 
    	// Disable button if no recognition service is present
    	CheckForSpeechInput();
    	CheckSettings();
    	initializeDB();
    }
    
    private void LoadWebViewData()
    {
    	setContentView(R.layout.webcontent_layout);
    	mWebView = (WebView) findViewById(R.id.webview);
    	
    	mWebView= (WebView) findViewById(R.id.webview);   	
    	
    	// Don't bother trying to load the page if the server IP hasn't been set
    	if(!serverIP.equals(""))
    	{   			
    		final Activity MyActivity = this;
    		
    		mWebView.setWebViewClient(new WebViewClient() {
    		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    		     Toast.makeText(MyActivity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
    		   }  		   
    		 });
    		
    		mWebView.setWebChromeClient(new WebChromeClient() {  			
   			 public void onProgressChanged(WebView view, int progress) 
   			 {
   				Log.i(tag,"Progress: " + progress);
   				ProgressBar Pbar = (ProgressBar) findViewById(R.id.pB1);
   				if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                    mWebView.setVisibility(WebView.INVISIBLE);
                    
                }
                Pbar.setProgress(progress);
                if(progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                    mWebView.setVisibility(WebView.VISIBLE);
                }

   			 }  			 
   			 });

    		mWebView.getSettings().setJavaScriptEnabled(true);
    		mWebView.loadUrl("http://" + serverIP + ":" + serverHttpPort + "/" + defaultPage);
    		
    		
    		
    	}
    }
        
    private void CheckForSpeechInput()
    {
    	ImageButton speakButton = (ImageButton) findViewById(R.id.speakButton);
    	PackageManager pm = getPackageManager();       
    	List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0); 
    	if (activities.size() == 0)        
    	{            
    		speakButton.setEnabled(false);     
    	}
    }
    
    private void CheckSettings()
    {   		
    	if(serverIP.equals(""))
    	{
    		openWizard();
    	}
    }

	private void initializeDB(){
    	DBAdapter dbAdapter=DBAdapter.getDBAdapterInstance(this);

		try {
			dbAdapter.createDataBase();
		}
		catch (IOException e) {
			Log.e(tag,e.getMessage());
		}
		
		dbAdapter.close();
		
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**     * Handle the action of the button being clicked     */    
	public void speakButtonClicked(View v)   
	{        
		startVoiceRecognitionActivity();   
	}
	
    /**     * Handle the action of the button being clicked     */    
	public void menuButtonClicked(View v)   
	{        
		openOptionsMenu(); 
	}
	
	/**     * Fire an intent to start the voice recognition activity.     */    
    private void startVoiceRecognitionActivity()    
    {        
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);      
    	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,              
    			RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);     
    	intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "OSA - Speech Recognition");    
    	startActivityForResult(intent, SPEECH_REQUEST);    
    }   
    
    /**     * Handle the results from the voice recognition activity.     */   
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)    
    {
    	if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK)        
        {
    		ProcessSpeechResult(data);    		
        }
    	if(requestCode == PREFERENCE_REQUEST)
    	{
    		// Reload the preferences as the user has updated
    		loadPref();
    		
    		// Reload the UI as the changed settings may have an impact
    		LoadWebViewData();
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }  
    
    private void ProcessSpeechResult(Intent data)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Speech Results");
        final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);        
        
        speechList = new ListView(MainActivity.this);
        speechList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));        
        builder.setView(speechList);
        
        speechList.setOnItemClickListener(new OnItemClickListener()
        {
        	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
        	{        		
        		SendSpeechCommand ssc = new SendSpeechCommand(speechList.getItemAtPosition(position).toString());
        		ssc.execute("");
        		speechListAlert.dismiss();        		
       		}
        });        
       
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }});
        
        builder.setCancelable(true);
        speechListAlert = builder.create();
        speechListAlert.show();  		
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_settings:
        	  Intent intent = new Intent();
              intent.setClass(MainActivity.this, Settings.class);
              startActivityForResult(intent, PREFERENCE_REQUEST);
              return true;
	    case R.id.menu_alerts:
	  	  	Intent intent2 = new Intent();
	        intent2.setClass(MainActivity.this, MessagesActivity.class);
	        startActivity(intent2);
	        return true;
	  	}
        
        return false;
        
    }
       
    private void loadPref()
    {
    	SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    	serverIP = mySharedPreferences.getString("serveraddress", "");
    	serverPort = mySharedPreferences.getString("serverRestPort", "8732");
    	serverHttpPort = mySharedPreferences.getString("serverHttpPort", "8081");
    	defaultPage = mySharedPreferences.getString("serverHttpPage", "mobile/index.aspx");

    }   
    
    private void openWizard(){
    	
    	
    	Intent myIntent = new Intent(this,WizardActivity.class); 
    	startActivityForResult(myIntent, PREFERENCE_REQUEST);
    	    	
    }
    
    protected class SendSpeechCommand extends AsyncTask<String, Void, String> 
    {
    	String speechCommand;
    	
		SendSpeechCommand(String commandToSend) 
		{
			speechCommand = commandToSend;
        }

    	@Override
    	protected String doInBackground(String... urls) 
    	{
    		
    		String result = "";
    		final String tag = "OSA"; 
    		
    		try
    		{    			
    			 URL url = new URL("http://" + serverIP + ":" + serverPort + "/api/namedscript/" + URLEncoder.encode(speechCommand, "utf-8"));
    			 HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				   try 
				   {
				     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				     result = readStream(in);
				     Log.i(tag,"Request Result: " + result);
				   }
				   finally 
				   {
				     urlConnection.disconnect();
				   }
    		}
    		catch(Exception e)
    		{
    			Log.e(tag, "Rest Error: " + e.toString());		
    		}
    		return result;            
        }
    	
    	private void MakeToast(String Message)
    	{
    		Toast.makeText(MainActivity.this, Message, Toast.LENGTH_SHORT).show();    	
    	}
    	
    	@Override protected void onPostExecute(String data) 
    	{
    		super.onPostExecute(data);	
    		
    		Log.i(tag,"Server Response - Speech Command: " + data);
    		if(data.equals("true"))
    		{
    			MakeToast("Command Activated");		
    		}
    		else
    		{
    			MakeToast("Command Activation Failed");
    		}
    	}
    	
    	private String readStream(InputStream is) 
    	{ 
    	    try 
    	    { 
    	      ByteArrayOutputStream bo = new ByteArrayOutputStream(); 
    	      int i = is.read(); 
    	      
    	      while(i != -1) 
    	      { 
    	        bo.write(i); 
    	        i = is.read(); 
    	      } 
    	      return bo.toString(); 
    	    } 
    	    catch (IOException e) 
    	    { 
    	      return ""; 
    	    } 
    	}	
    }   
}
