/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.opensourceautomation.android.osaextension.tasker;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.opensourceautomation.android.osaextension.utilities.CommonUtilities;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;
import com.opensourceautomation.android.osaextension.utilities.RestClient;
import com.opensourceautomation.android.osaextension.utilities.RestClient.RequestMethod;

/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver
{
	private Context myContext;
	private Devicelog mydevicelog;

    /**
     * @param context {@inheritDoc}.
     * @param intent the incoming {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING} Intent. This
     *            should contain the {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was saved by
     *            {@link EditActivity} and later broadcast by Locale.
     */
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
    	myContext=context;
    	mydevicelog= new Devicelog(myContext);
    	
        /*
         * Always be strict on input parameters! A malicious third-party app could send a malformed Intent.
         */

        if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
            }
            return;
        }

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);
        
        if (bundle.containsKey(PluginBundleManager.BUNDLE_EXTRA_STRING_NAMEDSCRIPT)){
	        if (PluginBundleManager.isNamedScriptBundleValid(bundle))
	        {
	            final String namedscript = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_NAMEDSCRIPT);
	            
	    		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
	    		String serveraddress=mypreferences.getString("serveraddress","");
	            
	    		String ecodednamedscript=namedscript;
	    		//encode invalid characters in the parameters
	    		try {
	    			ecodednamedscript = URLEncoder.encode(namedscript, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
				//send call to rest api for namedscript
				String url;
				url="http://"+serveraddress+":8732/api/namedscript/"+ecodednamedscript+"";
				
				RestClient client = new RestClient(myContext,url);
	
				mydevicelog.log("url="+url,Log.DEBUG);
	
				new sendRequest().execute(client);	
				
	        } 
        }
        
        if (bundle.containsKey(PluginBundleManager.BUNDLE_EXTRA_STRING_REPLACE_KEYS)){
        	final String replacekeys = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_REPLACE_KEYS);
            
        	mydevicelog.log("replacekeys="+replacekeys,Log.DEBUG);
        	
        } else {
        	mydevicelog.log("does not have replacekeys",Log.DEBUG);
        }
        
        if (bundle.containsKey(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODNAME)){
	        if (PluginBundleManager.isMethodBundleValid(bundle))
	        {
	            //final String methodcontainer = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODCONTAINER);
	            final String methodobject = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODOBJECT);
	            final String methodname = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODNAME);
	            //final String methodp1name = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER1NAME);
	            //final String methodp2name = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER2NAME);
	            String methodp1value = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE);
	            String methodp2value = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_METHODPARAMETER2VALUE);
	            
	    		SharedPreferences mypreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
	    		String serveraddress=mypreferences.getString("serveraddress","");
	            	    	
	    		
	    		if (methodp1value==null)
	    			methodp1value="";
	    		if (methodp2value==null)
	    			methodp2value="";
	    		
	    		//encode invalid characters in the parameters
	    		try {
					methodp1value = URLEncoder.encode(methodp1value, "UTF-8");
					methodp2value = URLEncoder.encode(methodp2value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
				//send call to rest api for method
				String url;
				url="http://"+serveraddress+":8732/api/object/"+methodobject+"/"+methodname+"?param1="+methodp1value+"&param2="+methodp2value+"";
				RestClient client = new RestClient(myContext,url);
	
				mydevicelog.log("url="+url,Log.DEBUG);
	
				new sendRequest().execute(client);	
				
	        } 
        }        
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
			
		}


		@Override
		protected void onPostExecute(String result) {
  	 

			if(result==null)
				result="##WASNULL##";

			result = result.replace("\n", "");


			if(result.equals("##NORESULTS##") || result.equals("##WASNULL##")){
				mydevicelog.log(result,Log.DEBUG);
				

			}else{
				mydevicelog.log("rest url sent to OSA",Log.DEBUG);
			}


		}

	}
    
    
}