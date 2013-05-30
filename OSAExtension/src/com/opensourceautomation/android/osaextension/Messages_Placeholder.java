


package com.opensourceautomation.android.osaextension;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Messages_Placeholder extends Fragment {
    
    private View v;
    private static Context myContext;
    private static Devicelog mydevicelog;
    
    
	public static Messages_Placeholder newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Messages_Placeholder fragment = new Messages_Placeholder();

		return fragment;
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        v = inflater.inflate(R.layout.placeholder, container, false);		
                
        if (myContext==null){
        	myContext=getActivity();
        }
        if (mydevicelog==null){
        	mydevicelog= new Devicelog(myContext);
        }
        
        
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		Boolean objectlinked=mypreferences.getBoolean("objectlinked",false);
		
        if (!objectlinked){
        	//openWizard();
        }
        
        
        return v;
    }


    
    private void openWizard(){
    	
    	Intent myIntent = new Intent(myContext,WizardActivity.class); 
    	myContext.startActivity(myIntent); 
    	    	
    }
    


}