
package com.opensourceautomation.android.osaextension.tasker;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.R.id;
import com.opensourceautomation.android.osaextension.R.layout;
import com.opensourceautomation.android.osaextension.R.string;
import com.opensourceautomation.android.osaextension.object.OSAObject;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;
import com.opensourceautomation.android.osaextension.utilities.RestClient;
import com.opensourceautomation.android.osaextension.utilities.RestClient.RequestMethod;


public class Tasker_Object extends ListFragment{

    private View v;
    private static Context myContext;
    private static Devicelog mydevicelog;
    private ListView list;
    
    private String serveraddress;
    
    private String mycontainer;
    
    private ArrayList<OSAObject> m_objects = null;
    private objectAdapter m_adapter;
    
	static Tasker_Object newInstance(Context parentcontext, String passedcontainer) {
		Tasker_Object f = new Tasker_Object();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("passedcontainer", passedcontainer);
        f.setArguments(args);

        myContext=parentcontext;
    	
        return f;
    }
	
	@Override
    public void onCreate(Bundle myBundle) {
        super.onCreate(myBundle);

        mycontainer= getArguments().getString("passedcontainer");
    
    }
	

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		serveraddress=mypreferences.getString("serveraddress","");
		
        
        v = inflater.inflate(R.layout.taskercontainer, container, false);
    
        if (myContext==null){
        	myContext=getActivity();
        	
        }
        
        if (myContext != null) {
            
        	if (mydevicelog==null){
            	mydevicelog= new Devicelog(myContext);
            }
        }
        
        list = (ListView)v.findViewById(android.R.id.list);
        TextView txtPageTitle = (TextView)v.findViewById(R.id.txtPageTitle);
        txtPageTitle.setText(R.string.txt_pickobject);
        
        init();
        
        return v;
    }
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
			
			OSAObject obj = (OSAObject)getListView().getItemAtPosition(position);

			if (obj.getName().equals("<-- BACK")){
				goback();
			} else {
				pick_object(obj.getName(),obj.getMethods());
			}
	}
    
    private void init(){

        m_objects = new ArrayList<OSAObject>();
        
        OSAObject obj = new OSAObject();
        obj.setName("Getting Objects...");
        m_objects.add(obj);

        m_adapter = new objectAdapter(myContext, R.layout.object_simple, m_objects);
        setListAdapter(m_adapter);
        
		populatelist();	
    }

	private void populatelist(){
		
		String url;
		
		url="http://"+serveraddress+":8732/api/objects/container/"+mycontainer;
		RestClient client = new RestClient(myContext,url);
		mydevicelog.log("url="+url,Log.DEBUG);

		new getObjects().execute(client);	
	}

	private class getObjects extends AsyncTask<RestClient, Void, String>{

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
			//progress1.setVisibility(View.VISIBLE);
		}


		@Override
		protected void onPostExecute(String result) {

			mydevicelog.log("onPostExecute - result:"+result,Log.DEBUG);	    	 

			if(result==null)
				result="##WASNULL##";

			result = result.replace("\n", "");

			if(result.equals("##NORESULTS##") || result.equals("##WASNULL##")){
				mydevicelog.log(result,Log.DEBUG);
				

			}else{

				m_objects = parseresults(result);
				
				m_adapter = new objectAdapter(myContext, R.layout.object_simple, m_objects);
		        setListAdapter(m_adapter);

			}


		}

	}
	
    
    private class objectAdapter extends ArrayAdapter<OSAObject> {

        public objectAdapter(Context context, int textViewResourceId, ArrayList<OSAObject> objects) {
                super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater vi = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.object_simple, null);

                OSAObject o = m_objects.get(position);
                if (o != null) {
                		TextView objectname = (TextView) v.findViewById(R.id.objectname);
                                                
                        if (objectname !=null){
                        	objectname.setText(o.getName()); 
                        }
                               
                }
                return v;
        }
    }

    private ArrayList<OSAObject> parseresults(String result){
		
    	ArrayList<OSAObject> tmp_objects = new ArrayList<OSAObject>();
		
    	//add <--BACK item in list to back up to containers
    	OSAObject obj = new OSAObject();
		obj.setName("<-- BACK");
		tmp_objects.add(obj);
    	
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				obj = new OSAObject();
				
				obj.parseJSON(jsonObject);
				
				tmp_objects.add(obj);
				
			}
						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmp_objects;
		
	}
    
    private void pick_object(String object, String methods){
    	
    	Activity a = getActivity(); 
    	
		if(a instanceof TaskerConfigureMethod) { 
		    ((TaskerConfigureMethod) a).StartNewFragment("Method",mycontainer,object,methods,null,null); 
		} 
    	    	
    }
    
    private void goback(){
    	
    	Activity a = getActivity(); 
    	
		if(a instanceof TaskerConfigureMethod) { 
		    ((TaskerConfigureMethod) a).StartNewFragment("Container",null,null,null,null,null); 
		} 
    	    	
    }

}
