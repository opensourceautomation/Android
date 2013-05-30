package com.opensourceautomation.android.osaextension;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;


public class Settings extends PreferenceActivity{

	Context myContext;
	private Devicelog mydevicelog;

	private PreferenceCategory sharerate;
	
	private String regid;
	private String serveraddress;
	private String serverRestPort;
	private String serverHttpPort;
	private String serverHttpPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myContext=this;
		mydevicelog= new Devicelog(myContext);
		
		
		addPreferencesFromResource(R.xml.settings);

		final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(myContext);   
		regid=prefFile.getString("regid","");
		serveraddress=prefFile.getString("serveraddress", "");
		serverRestPort = prefFile.getString("serverRestPort", "8732");
    	serverHttpPort = prefFile.getString("serverHttpPort", "8081");
    	serverHttpPage = prefFile.getString("serverHttpPage", "mobile/index.aspx");
    	
		
		// Get the wizard preference
		Preference wizard = (Preference) findPreference("wizard");
		wizard
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				openWizard();
				return true;
			}

		}); 
		
		// Get the serveraddress preference
		Preference serveraddresspref = (Preference) findPreference("serveraddress");
		serveraddresspref.setSummary(serveraddress);
		
		serveraddresspref
		.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference,Object newValue) {
				
				serveraddress=newValue.toString();
				
				Preference serveraddresspref = (Preference) findPreference("serveraddress");
				serveraddresspref.setSummary(serveraddress);
				
				saveString("serveraddress",serveraddress);
				
				return true;
			}
		}); 
		
		// Get the serverrestport preference
		Preference serverrestportpref = (Preference) findPreference("serverRestPort");
		serverrestportpref.setSummary(serverRestPort);
		
		serverrestportpref
		.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference,Object newValue) {
				
				serverRestPort=newValue.toString();
				
				Preference serverrestportpref = (Preference) findPreference("serverRestPort");
				serverrestportpref.setSummary(serverRestPort);
				
				saveString("serverRestPort",serverRestPort);
				
				return true;
			}
		}); 
		
		// Get the serverhttpport preference
		Preference serverhttpportpref = (Preference) findPreference("serverHttpPort");
		serverhttpportpref.setSummary(serverHttpPort);
		
		serverhttpportpref
		.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference,Object newValue) {
				
				serverHttpPort=newValue.toString();
				
				Preference serverhttpportpref = (Preference) findPreference("serverHttpPort");
				serverhttpportpref.setSummary(serverHttpPort);
				
				saveString("serverHttpPort",serverHttpPort);
				
				return true;
			}
		}); 
		
		// Get the serverhttppage preference
		Preference serverhttppagepref = (Preference) findPreference("serverHttpPage");
		serverhttppagepref.setSummary(serverHttpPage);
		
		serverhttppagepref
		.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference,Object newValue) {
				
				serverHttpPage=newValue.toString();
				
				Preference serverhttppagepref = (Preference) findPreference("serverHttpPage");
				serverhttppagepref.setSummary(serverHttpPage);
				
				saveString("serverHttpPage",serverHttpPage);
				
				return true;
			}
		}); 
		
		// Get the regid preference
		Preference regidpref = (Preference) findPreference("regid");
		regidpref.setSummary(regid);
		
		regidpref
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				shareRegid();
				return true;
			}

		}); 
		
		sharerate = (PreferenceCategory) findPreference("sharerate");

		// Get the rate preference
		Preference rateapp = (Preference) findPreference("rateapp");
		rateapp
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				rate();
				return true;
			}

		}); 
		sharerate.removePreference(rateapp);

		// Get the share preference
		Preference shareapp = (Preference) findPreference("shareapp");
		shareapp
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				share();
				return true;
			}

		});   
		
		sharerate.removePreference(shareapp);

		getPreferenceScreen().removePreference(sharerate);

	}
	
	private void saveBoolean(final String field, final boolean value) {                  
		final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(this); 
		final SharedPreferences.Editor editor = prefFile.edit();   
		editor.putBoolean(field, value); 
		editor.commit();          
	}     

	private void saveInt(final String field, final int data) { 
		final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(this);   
		final SharedPreferences.Editor editor = prefFile.edit();  
		editor.putInt(field, data);               
		editor.commit();          
	}

	private void saveString(final String field, final String data) { 
		final SharedPreferences prefFile = PreferenceManager.getDefaultSharedPreferences(this);   
		final SharedPreferences.Editor editor = prefFile.edit();  
		editor.putString(field, data);               
		editor.commit();          
	}  

    private void openWizard(){
    	
    	Intent myIntent = new Intent(this,com.opensourceautomation.android.osaextension.WizardActivity.class); 
    	
    	this.startActivity(myIntent); 
    	    	
    }
	
	private void shareRegid(){

		
		Intent shareIntent=new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT,regid);
		startActivity(Intent.createChooser(shareIntent, "Share GCM Registration ID"));
	}
	
	private void share(){

		String shareurl = "https://play.google.com/store/apps/details?id=com.opensourceautomation.android.osaextension&referrer=utm_source%3Dinapp%26utm_medium%3Dshare%26utm_content%3Dshare";
		Intent shareIntent=new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT,myContext.getResources().getString(R.string.txt_sharetext) +" "+ shareurl);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, myContext.getResources().getString(R.string.txt_sharesubjext));
		startActivity(Intent.createChooser(shareIntent, myContext.getResources().getString(R.string.txt_sharetitle)));
	}

	private void rate(){
		Intent rateIntent = new Intent(Intent.ACTION_VIEW);
		rateIntent.setData(Uri.parse("market://details?id=com.opensourceautomation.android.osaextension"));
		startActivity(rateIntent);
	}


	
	
	
}

