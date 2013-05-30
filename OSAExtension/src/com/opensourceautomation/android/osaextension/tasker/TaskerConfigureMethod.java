package com.opensourceautomation.android.osaextension.tasker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;


public class TaskerConfigureMethod extends AbstractPluginFragmentActivity {
   
    private String myCurrentFragment;
    
	private static Devicelog mydevicelog;
	private Context myContext;

    String mycontainer;
    String myobject;
    String mymethod;
    String myp1name;
    String myp2name;
    String myp1value;
    String myp2value;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.plugin_method));
        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        myContext=this;
        mydevicelog= new Devicelog(myContext);

        setContentView(R.layout.taskerconfigmethod);
        
      
        // Do first time initialization -- add initial fragment.
        if (myCurrentFragment==null){
        	
            if (PluginBundleManager.isMethodBundleValid(localeBundle))
            {
            	// parse the bundle and somehow preset these values???
            	
            	mycontainer = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODCONTAINER);
            	myobject = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODOBJECT);
            	mymethod = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODNAME);
            	myp1name = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER1NAME);
            	myp2name = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER2NAME);
            	myp1value = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE);
            	myp2value = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER2VALUE);
            	
            	setFinalized(true); // this will enable the save button since we have all our values entered
            	
            	Fragment newFragment = Tasker_Parameters.newInstance(myContext,mycontainer,myobject,mymethod,myp1name,myp2name,myp1value,myp2value);
    	        myCurrentFragment="Container";
    	        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	        ft.add(R.id.simple_fragment, newFragment).commit();
    	        
            } else {
            	
		        Fragment newFragment = Tasker_Container.newInstance(myContext);
		        myCurrentFragment="Container";
		        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		        ft.add(R.id.simple_fragment, newFragment).commit();

            }
        }
        
    }
	
    public void StartNewFragment(String fragmenttype, String passed_container, String passed_object, String passed_method,String passed_p1name, String passed_p2name) {
		
    	mycontainer=passed_container;
    	myobject=passed_object;
    	mymethod=passed_method;
    	myp1name=passed_p1name;
    	myp2name=passed_p2name;
    	
		// Instantiate a new fragment.
		Fragment newFragment=null;
		myCurrentFragment=fragmenttype;
		
		if (fragmenttype.equals("Container") || fragmenttype.equals("") || fragmenttype==null){
			newFragment = Tasker_Container.newInstance(myContext);
		}
	
		if (fragmenttype.equals("Object")){
			newFragment = Tasker_Object.newInstance(myContext,passed_container);
		}
		
		if (fragmenttype.equals("Method")){
			newFragment = Tasker_Method.newInstance(myContext,passed_container,passed_object,passed_method);
		}
		
		if (fragmenttype.equals("Parameters")){
			setFinalized(true); // this will enable the save button since we have all our values entered
			newFragment = Tasker_Parameters.newInstance(myContext,passed_container,passed_object,passed_method,passed_p1name,passed_p2name,myp1value,myp2value);
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.simple_fragment, newFragment);
		ft.commit();
	}
    
    public void SaveValues(String passed_p1value, String passed_p2value) {
    	myp1value=passed_p1value;
    	myp2value=passed_p2value;
    	
    	setFinalized(true); // this will enable the save button since we have all our values entered
    }
    
    @Override
    public void finish()
    {
        if (!isCanceled())
        {
        	if (mymethod!=null){
	            if (mymethod.length() > 0)
	            {
	                final Intent resultIntent = new Intent();
	
	                /*
	                 * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note
	                 * that anything placed in this Bundle must be available to Locale's class loader. So storing
	                 * String, int, and other standard objects will work just fine. Parcelable objects are not
	                 * acceptable, unless they also implement Serializable. Serializable objects must be standard
	                 * Android platform objects (A Serializable class private to this plug-in's APK cannot be
	                 * stored in the Bundle, as Locale's classloader will not recognize it).
	                 */
	                final Bundle resultBundle =
	                        PluginBundleManager.generateMethodBundle(getApplicationContext(), mycontainer, myobject, mymethod, myp1name, myp2name, myp1value, myp2value);
	                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
	
	                /*
	                 * The blurb is concise status text to be displayed in the host's UI.
	                 */
	                final String blurb = generateBlurb(getApplicationContext(), myobject+"."+mymethod+"("+myp1value+","+myp2value+")");
	                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
	
	                setResult(RESULT_OK, resultIntent);
	            }
        	}
        }

        super.finish();
    }

    /**
     * @param context Application context.
     * @param namedscript The namedscript to be called by the plug-in. Cannot be null.
     * @return A blurb for the plug-in.
     */
    /* package */static String generateBlurb(final Context context, final String namedscript)
    {
        final int maxBlurbLength =
                context.getResources().getInteger(R.integer.twofortyfouram_locale_maximum_blurb_length);

        if (namedscript.length() > maxBlurbLength)
        {
            return namedscript.substring(0, maxBlurbLength);
        }

        return namedscript;
    }
    
}