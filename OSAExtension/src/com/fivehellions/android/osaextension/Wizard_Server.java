

package com.fivehellions.android.osaextension;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivehellions.android.osaextension.RestClient.RequestMethod;


public final class Wizard_Server extends Fragment {

	private View v;
	private static Context myContext;
	private static Devicelog mydevicelog;

	private String serveraddress;

	private TextView txtPageTitle;
	private TextView txtEntryDescription1;   
	private EditText txtEntry1;
	private LinearLayout progressLayout;
	private TextView txtProgress1;
	private ProgressBar progress1; 
	private Button button1;

	public static Wizard_Server newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Wizard_Server fragment = new Wizard_Server();

		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.wizserver, container, false);

        if (myContext==null){
        	myContext=getActivity();
        }
        if (mydevicelog==null){
        	mydevicelog= new Devicelog(myContext);
        }
		
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		serveraddress=mypreferences.getString("serveraddress","");

		txtPageTitle = (TextView)v.findViewById(R.id.txtPageTitle);
		txtPageTitle.setText(R.string.txt_osaserver);

		txtEntryDescription1 = (TextView)v.findViewById(R.id.txtEntryDescription1);
		txtEntry1 = (EditText)v.findViewById(R.id.txtEntry1);
		progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
		txtProgress1 = (TextView)v.findViewById(R.id.txtProgress1);
		progress1 = (ProgressBar)v.findViewById(R.id.progress1);
		button1 = (Button)v.findViewById(R.id.button1);

		txtEntryDescription1.setVisibility(View.VISIBLE);
		txtEntry1.setVisibility(View.VISIBLE);
		txtProgress1.setVisibility(View.GONE);
		progress1.setVisibility(View.GONE);
		button1.setVisibility(View.VISIBLE);

		button1.setText(R.string.txt_testconnection);

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				txtProgress1.setText(R.string.txt_testingconnection);
				txtProgress1.setVisibility(View.VISIBLE);
				progress1.setVisibility(View.VISIBLE);

				serveraddress=txtEntry1.getText().toString();

				mydevicelog.log("serveraddress="+serveraddress,Log.DEBUG);

				final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
				final SharedPreferences.Editor editor = prefFile.edit();  
				editor.putString("serveraddress", serveraddress);               
				editor.commit();  

				WizardActivity myactivty = (WizardActivity) myContext;
				myactivty.querefresh();

				//send test

				String url="http://"+serveraddress+":8732/api/plugins";
				RestClient client = new RestClient(myContext,url);

				mydevicelog.log("url="+url,Log.DEBUG);

				new sendRequest().execute(client);
			}
		});

		txtEntryDescription1.setText(R.string.txt_serveraddress);
		txtEntry1.setText(serveraddress);

		txtEntry1.addTextChangedListener(textwatcher);

		if (serveraddress.equals("")){
			button1.setEnabled(false);
		}


		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}



	TextWatcher textwatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			if (s.length()>0){
				button1.setEnabled(true);
				
				serveraddress=s.toString();

				final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
				final SharedPreferences.Editor editor = prefFile.edit();  
				editor.putString("serveraddress", serveraddress);               
				editor.commit();  
			}
			else
			{
				button1.setEnabled(false);
			}

		} 

	};

	private void parseresults(String result){
		
		final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
		final SharedPreferences.Editor editor = prefFile.edit(); 
		
		editor.putBoolean("androidpluginfound", false);
		editor.putBoolean("restpluginfound", false);
		
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String pluginname="";
				
				if (jsonObject.has("Name")){
					pluginname=jsonObject.getString("Name");
				}
				
				if (pluginname.equalsIgnoreCase("Android")){
 
					editor.putBoolean("androidpluginfound", true);
					
				}
				
				if (pluginname.equalsIgnoreCase("Rest")){
					 
					editor.putBoolean("restpluginfound", true);
					
				}

			}
						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		editor.commit();
		
		
		
	}
	
	private class sendRequest extends AsyncTask<RestClient, Void, String>{

		@Override
		protected String doInBackground(RestClient... client) {

			mydevicelog.log("doInBackground",Log.DEBUG);

			try { 
				client[0].Execute(RequestMethod.GET);
			} 
			catch (Exception e) {
				mydevicelog.log(e.getMessage(),Log.WARN);
			} 
			return client[0].getResponse();
		}


		@Override
		protected void onPreExecute() {
			mydevicelog.log("onPreExecute",Log.DEBUG);
			progress1.setVisibility(View.VISIBLE);
		}


		@Override
		protected void onPostExecute(String result) {

			mydevicelog.log("doInBackground - result:"+result,Log.DEBUG);	    	 

			if(result==null)
				result="##WASNULL##";

			result = result.replace("\n", "");

			final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
			final SharedPreferences.Editor editor = prefFile.edit();  



			if(result.equals("##NORESULTS##") || result.equals("##WASNULL##")){
				mydevicelog.log(result,Log.DEBUG);
				txtProgress1.setText(R.string.txt_connectionerror);

				editor.putBoolean("connectionverified", false);

			}else{
				txtProgress1.setText(R.string.txt_connectionsuccessful);	 

				editor.putBoolean("connectionverified", true);
				
				parseresults(result);
			}

			editor.commit();

			progress1.setVisibility(View.GONE);

		}

	}

}
