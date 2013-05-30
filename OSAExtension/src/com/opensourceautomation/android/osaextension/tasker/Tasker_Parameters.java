package com.opensourceautomation.android.osaextension.tasker;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;


public class Tasker_Parameters extends Fragment{

    private View v;
    private static Context myContext;
    private static Devicelog mydevicelog;
    
    private String mycontainer;
    private String myobject;
    private String mymethod;
    private String myp1name;
    private String myp2name;
    private String myp1value;
    private String myp2value;
    
    private EditText textp1;
    private EditText textp2;
    
	static Tasker_Parameters newInstance(Context parentcontext, String passedcontainer, String passedobject, String passedmethod, String passedp1name, String passedp2name, String passedp1value, String passedp2value) {
		Tasker_Parameters f = new Tasker_Parameters();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("passedcontainer", passedcontainer);
        args.putString("passedobject", passedobject);
        args.putString("passedmethod", passedmethod);
        args.putString("passedp1name", passedp1name);
        args.putString("passedp2name", passedp2name);
        args.putString("passedp1value", passedp1value);
        args.putString("passedp2value", passedp2value);
        f.setArguments(args);

        myContext=parentcontext;
    	
        return f;
    }
	
	@Override
    public void onCreate(Bundle myBundle) {
        super.onCreate(myBundle);

        mycontainer= getArguments().getString("passedcontainer");
        myobject= getArguments().getString("passedobject");
        mymethod= getArguments().getString("passedmethod");
        myp1name= getArguments().getString("passedp1name");
        myp2name= getArguments().getString("passedp2name");
        myp1value= getArguments().getString("passedp1value");
        myp2value= getArguments().getString("passedp2value");
        
        if (myp1name==null){
        	myp1name="";
        }
        if (myp2name==null){
        	myp2name="";
        }
    }
	

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        v = inflater.inflate(R.layout.taskerparameters, container, false);
    
        if (myContext==null){
        	myContext=getActivity();
        	
        }
        
        if (myContext != null) {
            
        	if (mydevicelog==null){
            	mydevicelog= new Devicelog(myContext);
            }
        }
        
        TextView textBack = (TextView)v.findViewById(R.id.textBack);
        textBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goback();
			}
		});
        
        TextView textViewp1 = (TextView)v.findViewById(R.id.textViewp1);
        if (myp1name.equals("")) {
        	textViewp1.setText(R.string.txt_p1);
        } else {
        	textViewp1.setText(myp1name);
        }
        
        TextView textViewp2 = (TextView)v.findViewById(R.id.textViewp2);
        if (myp2name.equals("")) {
        	textViewp2.setText(R.string.txt_p2);
        } else {
        	textViewp2.setText(myp2name);
        }
        
        textp1 = (EditText)v.findViewById(R.id.textp1);
        textp1.setText(myp1value);
        textp1.addTextChangedListener(p1textwatcher);
        
        textp2 = (EditText)v.findViewById(R.id.textp2);
        textp2.setText(myp2value);
        textp2.addTextChangedListener(p2textwatcher);
        
        return v;
    }
    
  
    TextWatcher p1textwatcher = new TextWatcher() {
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
			myp1value=s.toString();
			savevalues();
		} 
	};
	TextWatcher p2textwatcher = new TextWatcher() {
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
				myp2value=s.toString();
				savevalues();
			} 
		};	
    
	private void savevalues(){
		Activity a = getActivity(); 
    	
		if(a instanceof TaskerConfigureMethod) { 
		    ((TaskerConfigureMethod) a).SaveValues(myp1value,myp2value);; 
		} 
	}
	
    private void goback(){
    	
    	Activity a = getActivity(); 
    	
		if(a instanceof TaskerConfigureMethod) { 
		    ((TaskerConfigureMethod) a).StartNewFragment("Object",mycontainer,myobject,null,null,null); 
		} 
    	    	
    }

}
