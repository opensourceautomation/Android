
package com.opensourceautomation.android.osaextension;

import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.DISPLAY_TESTMESSAGE_ACTION;
import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.EXTRA_MESSAGE;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opensourceautomation.android.osaextension.utilities.Devicelog;
import com.opensourceautomation.android.osaextension.utilities.RestClient;
import com.opensourceautomation.android.osaextension.utilities.RestClient.RequestMethod;



public final class Wizard_Test extends Fragment {

	private View v;
	private static Context myContext;
	private static Devicelog mydevicelog;

	private String regid;
	private String serveraddress;
	private String osaobjectname;
	private Boolean senttest=false;
	
	private TextView txtPageTitle;
	private TextView txtDescription1;   
	private LinearLayout progressLayout;
	private TextView txtProgress1;
	private ProgressBar progress1; 
	private Button button1;

	
	public static Wizard_Test newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Wizard_Test fragment = new Wizard_Test();

		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.wiztest, container, false);

        if (myContext==null){
        	myContext=getActivity();
        }
        if (mydevicelog==null){
        	mydevicelog= new Devicelog(myContext);
        }
		
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		regid=mypreferences.getString("regid","");
		serveraddress=mypreferences.getString("serveraddress","");
		osaobjectname=mypreferences.getString("osaobjectname","");
		
		txtPageTitle = (TextView)v.findViewById(R.id.txtPageTitle);
		txtPageTitle.setText(R.string.txt_test);

		txtDescription1 = (TextView)v.findViewById(R.id.txtDescription1);
		progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
		txtProgress1 = (TextView)v.findViewById(R.id.txtProgress1);
		progress1 = (ProgressBar)v.findViewById(R.id.progress1);
		button1 = (Button)v.findViewById(R.id.button1);

		txtDescription1.setVisibility(View.VISIBLE);
		txtProgress1.setVisibility(View.GONE);
		progress1.setVisibility(View.GONE);
		button1.setVisibility(View.VISIBLE);

		button1.setText(R.string.txt_testnotification);

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				txtProgress1.setText(R.string.txt_testing);
				txtProgress1.setVisibility(View.VISIBLE);
				progress1.setVisibility(View.VISIBLE);

				
				//send test
				
				String url;
				url="http://"+serveraddress+":8732/api/object/"+osaobjectname+"/NOTIFY?param1=Hello OSA&param2=default,5";
				
				RestClient client = new RestClient(myContext,url);

				mydevicelog.log("url="+url,Log.DEBUG);

				new sendRequest().execute(client);	
				
			}
		});

		txtDescription1.setText("");

    	getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_TESTMESSAGE_ACTION));

		return v;
	}

  
    @Override
    public void onDestroyView() {
    	mydevicelog.log("wizard_test onDestroyView",Log.DEBUG);
    	
    	super.onDestroyView();
        
        mydevicelog.log("wizard_test onDestroyView about to unregister",Log.DEBUG);
        
        getActivity().unregisterReceiver(mHandleMessageReceiver);
        
        mydevicelog.log("wizard_test onDestroyView done it should now be unregistered",Log.DEBUG);
    }

    
    
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	
	private class sendRequest extends AsyncTask<RestClient, Void, String>{

		@Override
		protected String doInBackground(RestClient... client) {


			try { 
				client[0].Execute(RequestMethod.POST);
			} 
			catch (Exception e) {
				mydevicelog.log("error executing request - "+e.getMessage(),Log.WARN);
			} 
			return client[0].getResponse();
		}


		@Override
		protected void onPreExecute() {
			progress1.setVisibility(View.VISIBLE);
		}


		@Override
		protected void onPostExecute(String result) {
  	 

			if(result==null)
				result="##WASNULL##";

			result = result.replace("\n", "");

			final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
			final SharedPreferences.Editor editor = prefFile.edit();  

			if(result.equals("##NORESULTS##") || result.equals("##WASNULL##")){
				mydevicelog.log(result,Log.DEBUG);
				
				txtProgress1.setText(R.string.txt_testerror);

			}else{
				senttest=true;
				txtProgress1.setText(R.string.txt_testsuccessful);
				
			}

			editor.commit();

			progress1.setVisibility(View.GONE);

		}

	}


    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (senttest){
        		String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
        		txtProgress1.setText(newMessage );
        	}
        }
    };
    
}
