
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
import com.opensourceautomation.android.osaextension.object.OSAMethod;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;
import com.opensourceautomation.android.osaextension.utilities.RestClient.RequestMethod;


public class Tasker_Method extends ListFragment{

    private View v;
    private static Context myContext;
    private static Devicelog mydevicelog;
    private ListView list;
        
    private String mycontainer;
    private String myobject;
    private String mymethods;
    
    private ArrayList<OSAMethod> m_methods = null;
    private methodAdapter m_adapter;
    
	static Tasker_Method newInstance(Context parentcontext, String passedcontainer, String passedobject, String passedmethod) {
		Tasker_Method f = new Tasker_Method();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("passedcontainer", passedcontainer);
        args.putString("passedobject", passedobject);
        args.putString("passedmethod", passedmethod);
        
        f.setArguments(args);

        myContext=parentcontext;
    	
        return f;
    }
	
	@Override
    public void onCreate(Bundle myBundle) {
        super.onCreate(myBundle);

        mycontainer= getArguments().getString("passedcontainer");
        myobject= getArguments().getString("passedobject");
        mymethods= getArguments().getString("passedmethod");
    
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    
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
        txtPageTitle.setText(R.string.txt_pickmethod);
        
        init();
        
        return v;
    }
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
			
			OSAMethod m = (OSAMethod)getListView().getItemAtPosition(position);

			if (m.getMethodName().equals("<-- BACK")){
				goback();
			} else {
				pick_method(m.getMethodName(),m.getParameter1(),m.getParameter2());
			}
	}
    
    private void init(){

    	m_methods = new ArrayList<OSAMethod>();
        
        OSAMethod m = new OSAMethod();
        m.setMethodName("Getting Methods...");
        m_methods.add(m);

        m_adapter = new methodAdapter(myContext, R.layout.object_simple, m_methods);
        setListAdapter(m_adapter);
        
		populatelist();	
    }

	private void populatelist(){
		
		m_methods = parseresults(mymethods);
		
		m_adapter = new methodAdapter(myContext, R.layout.object_simple, m_methods);
        setListAdapter(m_adapter);

	}

    
    private class methodAdapter extends ArrayAdapter<OSAMethod> {

        public methodAdapter(Context context, int textViewResourceId, ArrayList<OSAMethod> methods) {
                super(context, textViewResourceId, methods);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater vi = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.object_simple, null);

                OSAMethod m = m_methods.get(position);
                if (m != null) {
                		TextView objectname = (TextView) v.findViewById(R.id.objectname);
                                                
                        if (objectname !=null){
                        	objectname.setText(m.getMethodName()); 
                        }
                               
                }
                return v;
        }
    }

    private ArrayList<OSAMethod> parseresults(String result){
		
    	ArrayList<OSAMethod> tmp_methods = new ArrayList<OSAMethod>();
		
    	//add <--BACK item in list to back up to containers
    	OSAMethod m = new OSAMethod();
		m.setMethodName("<-- BACK");
		tmp_methods.add(m);
    	
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				m = new OSAMethod();
				m.parseJSON(jsonObject);
				tmp_methods.add(m);
				
			}
						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmp_methods;
		
	}
    
    private void pick_method(String method, String p1name, String p2name){
    	
    	Activity a = getActivity(); 
    	
		if(a instanceof TaskerConfigureMethod) { 
		    ((TaskerConfigureMethod) a).StartNewFragment("Parameters",mycontainer,myobject,method,p1name,p2name); 
		} 
    	    	
    }
    
    private void goback(){
    	
    	Activity a = getActivity(); 
    	
		if(a instanceof TaskerConfigureMethod) { 
		    ((TaskerConfigureMethod) a).StartNewFragment("Object",mycontainer,null,null,null,null); 
		} 
    	    	
    }

}
