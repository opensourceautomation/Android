package com.fivehellions.android.osaextension;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivehellions.android.osaextension.RestClient.RequestMethod;


public final class Wizard_Object extends ListFragment {

	private View v;
	private static Context myContext;
	private static Devicelog mydevicelog;

	private String serveraddress;
	private String regid;
	private String osaobjectname;
	private Boolean linking=false;
	int osaselectedobject;
	String osaobjectdescription="";
	String osaobjectcontainer="";
	
	private ArrayList<AndroidDeviceObject> m_objects = null;
	private ArrayList<String> m_names = null;
	
	private TextView txtPageTitle;
	private TextView txtEntryDescription1;   
	private EditText txtEntry1;
	private LinearLayout progressLayout;
	private TextView txtProgress1;
	private ProgressBar progress1; 
	private Button button1;
	private ListView lv;
	private TextView txtListDescription1;

	public static Wizard_Object newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Wizard_Object fragment = new Wizard_Object();

		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.wizobject, container, false);		
		
		
        if (myContext==null){
        	myContext=getActivity();
        }
        if (mydevicelog==null){
        	mydevicelog= new Devicelog(myContext);
        }
		
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		osaobjectname=mypreferences.getString("osaobjectname","");
		serveraddress=mypreferences.getString("serveraddress","");
		
		txtPageTitle = (TextView)v.findViewById(R.id.txtPageTitle);
		txtPageTitle.setText(R.string.txt_object);

		txtEntryDescription1 = (TextView)v.findViewById(R.id.txtEntryDescription1);
		txtEntry1 = (EditText)v.findViewById(R.id.txtEntry1);
		progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
		txtProgress1 = (TextView)v.findViewById(R.id.txtProgress1);
		progress1 = (ProgressBar)v.findViewById(R.id.progress1);
		button1 = (Button)v.findViewById(R.id.button1);
		lv = (ListView) v.findViewById(android.R.id.list);
		txtListDescription1 = (TextView)v.findViewById(R.id.txtListDescription1);
		
		ArrayList<String> myobjects = new ArrayList<String>();
		myobjects.add("Getting Objects...");
		ListAdapter myListAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,myobjects);
		setListAdapter(myListAdapter);
		
		populatelist();
		
		txtEntryDescription1.setVisibility(View.VISIBLE);
		txtEntry1.setVisibility(View.VISIBLE);
		txtProgress1.setVisibility(View.GONE);
		progress1.setVisibility(View.GONE);
		button1.setVisibility(View.VISIBLE);

		button1.setText(R.string.txt_createobject);

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!linking){
					txtProgress1.setText(R.string.txt_creatingobject);
				} else {
					txtProgress1.setText(R.string.txt_linkingobject);
				}
								
				txtProgress1.setVisibility(View.VISIBLE);
				progress1.setVisibility(View.VISIBLE);

				osaobjectname=txtEntry1.getText().toString();

				mydevicelog.log("osaobjectname="+osaobjectname,Log.DEBUG);

				final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
				regid=prefFile.getString("regid","");
				final SharedPreferences.Editor editor = prefFile.edit();  
				editor.putString("osaobjectname", osaobjectname);               
				editor.commit();  

				WizardActivity myactivty = (WizardActivity) myContext;
				myactivty.querefresh();

				//create or update object
				String url;
				
				

				//check if existing object is linked to same device
				if (m_objects!=null){
					for (int i = 0; i < m_objects.size(); i++) {
						
						if (m_objects.get(i).getname().equalsIgnoreCase(osaobjectname)){
							//if we already have the same name make sure we are set to linking so we will update it instead of trying to create it
							linking=true;
						}

						// not needed anymore since we are now using a property instead because address can only be 200 characters and we sometimes have a GCM ID over 200
						//if (m_objects.get(i).getaddress().equalsIgnoreCase(regid)){
							
							//if we already have an object linked to this device then blank out the address on that object since we can't have two objects with same address
							//url="http://"+serveraddress+":8732/api/object/update?oldName="+m_objects.get(i).getname()+"&newName="+m_objects.get(i).getname()+"&desc="+m_objects.get(i).getdescription()+"&type=ANDROID DEVICE&container="+m_objects.get(i).getcontainer()+"&enabled=0";
							//RestClient client = new RestClient(myContext,url);
							//mydevicelog.log("url="+url,Log.DEBUG);
							//new sendRequest().execute(client);			
							
						//}
					}
				}

				if (!linking){
					url="http://"+serveraddress+":8732/api/object/add?name="+osaobjectname+"&desc=Android Device&type=ANDROID DEVICE&container=SYSTEM&enabled=true";

					RestClient client = new RestClient(myContext,url);
					
					mydevicelog.log("url="+url,Log.DEBUG);

					new sendRequest().execute(client);
				} //else { //we will always send a property update now, I'm a little worried about the timing... hopefully the object will be created before we try to update the property... if not I will have to reengineer it to get results of first one back before firing the second
					//changed url because we are now using property instead of url
					//url="http://"+serveraddress+":8732/api/object/update?oldName="+osaobjectname+"&newName="+osaobjectname+"&desc="+osaobjectdescription+"&type=ANDROID DEVICE&address="+regid+"&container="+osaobjectcontainer+"&enabled=1";
					
					url="http://"+serveraddress+":8732/api/property/update?objName="+osaobjectname+"&propName=GCMID&propVal="+regid+"";
					
					RestClient client = new RestClient(myContext,url);

					mydevicelog.log("url="+url,Log.DEBUG);

					new sendRequest().execute(client);
					
				//}


			}
		});

		txtEntryDescription1.setText(R.string.txt_objectname);
		txtEntry1.setText(osaobjectname);

		txtListDescription1.setText(R.string.txt_selectexisting);
		
		txtEntry1.addTextChangedListener(textwatcher);

		if (osaobjectname.equals("")){
			button1.setEnabled(false);
		}


		//this.setListShown(false);
		//this.setEmptyText(getResources().getString(R.string.txt_noobjects));
		
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
			String devicename = getListView().getItemAtPosition(position).toString();
			
			AndroidDeviceObject obj = new AndroidDeviceObject();
			
			if (m_objects.size()>0){
				
				obj = m_objects.get(position);
				
				//check to make sure we got right object
				if (devicename.equals(obj.getname())){
					
					txtEntry1.setText(devicename);
					
					osaobjectname=devicename;
	
					final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
					final SharedPreferences.Editor editor = prefFile.edit();  
					editor.putString("osaobjectname", osaobjectname);               
					editor.commit();  
					
					osaselectedobject=position;
					osaobjectdescription=obj.getdescription();
					osaobjectcontainer=obj.getcontainer();
					
					linking=true;
					button1.setText(R.string.txt_linkobject);
					
				} else {
					
					mydevicelog.log("something is not right name="+devicename+", objectname="+obj.getname(),Log.DEBUG);
				}
			}
		
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

			linking=false;
			
			if (s.length()>0){
				button1.setEnabled(true);
				
				osaobjectname=s.toString();

				final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
				final SharedPreferences.Editor editor = prefFile.edit();  
				editor.putString("osaobjectname", osaobjectname);               
				editor.commit();  
			}
			else
			{
				button1.setEnabled(false);
			}

		} 

	};

	
	private void populatelist(){
		String url;
		
		url="http://"+serveraddress+":8732/api/objects/type/Android%20Device";
		RestClient client = new RestClient(myContext,url);
		mydevicelog.log("url="+url,Log.DEBUG);

		new getObjects().execute(client);	
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
				
				if (!linking){
					txtProgress1.setText(R.string.txt_creationerror);
				} else {
					txtProgress1.setText(R.string.txt_linkerror);
				}
				
				editor.putBoolean("objectlinked", false);

			}else{
				
				if (!linking){
					txtProgress1.setText(R.string.txt_creationsuccessful);
				} else {
					txtProgress1.setText(R.string.txt_linksuccessful);
				}
							
				populatelist();

				editor.putBoolean("objectlinked", true);
				
			}

			editor.commit();

			progress1.setVisibility(View.GONE);

		}

	}

	private class getObjects extends AsyncTask<RestClient, Void, String>{

		@Override
		protected String doInBackground(RestClient... client) {

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
			//progress1.setVisibility(View.VISIBLE);
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
				

			}else{

				ArrayList<String> myobjects = parseresults(result);
				
				ListAdapter myListAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_list_item_1,myobjects);
				setListAdapter(myListAdapter);

			}


			//progress1.setVisibility(View.GONE);

		}

	}
	
	
	private ArrayList<String> parseresults(String result){
		
		m_objects = new ArrayList<AndroidDeviceObject>();
		m_names = new ArrayList<String>();
		
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				AndroidDeviceObject obj = new AndroidDeviceObject();
				
				if (jsonObject.has("Properties")){
					JSONArray jsonProperties = jsonObject.getJSONArray("Properties");
					for (int p = 0; p < jsonProperties.length(); p++) {
						JSONObject jsonProp = jsonProperties.getJSONObject(p);
					
						if (jsonProp.has("Name")){
							if (jsonObject.getString("Name").equalsIgnoreCase("GCMID")){
								if (jsonProp.has("Value")){
									obj.setGCMID(jsonObject.getString("Value"));
								}
							}
						}
					}
				}
				
				if (jsonObject.has("Address")){
					obj.setaddress(jsonObject.getString("Address"));
				}
				if (jsonObject.has("BaseType")){
					obj.setbasetype(jsonObject.getString("BaseType"));
				}
				if (jsonObject.has("Container")){
					obj.setcontainer(jsonObject.getString("Container"));
				}
				if (jsonObject.has("Description")){
					obj.setdescription(jsonObject.getString("Description"));
				}
				if (jsonObject.has("Enabled")){
					obj.setenabled(jsonObject.getString("Enabled"));
				}
				if (jsonObject.has("Name")){
					obj.setname(jsonObject.getString("Name"));
				}
				if (jsonObject.has("Type")){
					obj.settype(jsonObject.getString("Type"));
				}

				m_objects.add(obj);
				m_names.add(obj.getname());
				
			}
						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return m_names;
		
	}
}
