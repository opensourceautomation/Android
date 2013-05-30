


package com.opensourceautomation.android.osaextension;


import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.REGISTER_RESULTS_ACTION;
import static com.opensourceautomation.android.osaextension.utilities.CommonUtilities.SENDER_ID;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opensourceautomation.android.osaextension.R;
import com.google.android.gcm.GCMRegistrar;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;



public final class Wizard_Register extends Fragment {

	private View v;
	private static Context myContext;
	private static Devicelog mydevicelog;

	private String regid;

	private TextView txtPageTitle;
	private TextView txtDescription1;   
	private LinearLayout progressLayout;
	private TextView txtProgress1;
	private ProgressBar progress1; 
	private Button button1;

	
	public static Wizard_Register newInstance(Context parentcontext) {

		myContext=parentcontext;
		mydevicelog= new Devicelog(myContext);

		Wizard_Register fragment = new Wizard_Register();

		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (myContext==null){
        	myContext=getActivity();
        }
		
		myContext.registerReceiver(mHandleRegisterReceiver,
                new IntentFilter(REGISTER_RESULTS_ACTION));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.wizregister, container, false);

        if (myContext==null){
        	myContext=getActivity();
        }
        if (mydevicelog==null){
        	mydevicelog= new Devicelog(myContext);
        }
		
		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
		regid=mypreferences.getString("regid","");

		txtPageTitle = (TextView)v.findViewById(R.id.txtPageTitle);
		txtPageTitle.setText(R.string.txt_register);

		txtDescription1 = (TextView)v.findViewById(R.id.txtDescription1);
		progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
		txtProgress1 = (TextView)v.findViewById(R.id.txtProgress1);
		progress1 = (ProgressBar)v.findViewById(R.id.progress1);
		button1 = (Button)v.findViewById(R.id.button1);

		txtDescription1.setVisibility(View.VISIBLE);
		txtProgress1.setVisibility(View.GONE);
		progress1.setVisibility(View.GONE);
		button1.setVisibility(View.VISIBLE);

		button1.setText(R.string.txt_registerdevice);

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				txtProgress1.setText(R.string.txt_registering);
				txtProgress1.setVisibility(View.VISIBLE);
				progress1.setVisibility(View.VISIBLE);

				
				//send register

				GCMRegistrar.register(myContext, SENDER_ID);
			}
		});

		txtDescription1.setText(R.string.txt_registerwithgcm);


		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	
    private final BroadcastReceiver mHandleRegisterReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Boolean mresults = intent.getExtras().getBoolean("results");
        	String mregid = intent.getExtras().getString("regid");

        	final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
			final SharedPreferences.Editor editor = prefFile.edit();  

			regid=mregid;
			editor.putString("regid", regid);  
			
			editor.commit();
			
        	if (mresults){
        		txtProgress1.setText(R.string.txt_registersuccessful);	 
        	}else{
        		txtProgress1.setText(R.string.txt_registererror);	
        	}
        	
        	progress1.setVisibility(View.GONE);
        	
        }
    };


}
