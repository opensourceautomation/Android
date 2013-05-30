package com.opensourceautomation.android.osaextension;


import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APPLICATION_LOG;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.AVAILABLE_MEM_SIZE;
import static org.acra.ReportField.BRAND;
import static org.acra.ReportField.BUILD;
import static org.acra.ReportField.CRASH_CONFIGURATION;
import static org.acra.ReportField.DEVICE_FEATURES;
import static org.acra.ReportField.DISPLAY;
import static org.acra.ReportField.ENVIRONMENT;
import static org.acra.ReportField.FILE_PATH;
import static org.acra.ReportField.INITIAL_CONFIGURATION;
import static org.acra.ReportField.INSTALLATION_ID;
import static org.acra.ReportField.IS_SILENT;
import static org.acra.ReportField.PACKAGE_NAME;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.REPORT_ID;
import static org.acra.ReportField.SETTINGS_SECURE;
import static org.acra.ReportField.SETTINGS_SYSTEM;
import static org.acra.ReportField.SHARED_PREFERENCES;
import static org.acra.ReportField.STACK_TRACE;
import static org.acra.ReportField.TOTAL_MEM_SIZE;
import static org.acra.ReportField.USER_APP_START_DATE;
import static org.acra.ReportField.USER_CRASH_DATE;
import static org.acra.ReportField.USER_EMAIL;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.opensourceautomation.android.osaextension.BuildConfig;
import com.opensourceautomation.android.osaextension.utilities.CommonUtilities;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

//@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=72044ad1", formKey = "", applicationLogFile = "applog.log", customReportContent = {ReportField.REPORT_ID, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME, ReportField.FILE_PATH, ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.PRODUCT, ReportField.ANDROID_VERSION, ReportField.BUILD, ReportField.TOTAL_MEM_SIZE, ReportField.AVAILABLE_MEM_SIZE, ReportField.STACK_TRACE, ReportField.INITIAL_CONFIGURATION, ReportField.CRASH_CONFIGURATION, ReportField.DISPLAY, ReportField.USER_APP_START_DATE, ReportField.USER_CRASH_DATE, ReportField.IS_SILENT, ReportField.INSTALLATION_ID, ReportField.USER_EMAIL, ReportField.DEVICE_FEATURES, ReportField.ENVIRONMENT, ReportField.SHARED_PREFERENCES, ReportField.SETTINGS_SYSTEM, ReportField.SETTINGS_SECURE, ReportField.APPLICATION_LOG})
@ReportsCrashes(formKey = "dGI3QUxrZEJaRmUwb2Zjck05a3JVTEE6MA", applicationLogFile = "applog.log", customReportContent = {REPORT_ID, APP_VERSION_CODE, APP_VERSION_NAME, PACKAGE_NAME, FILE_PATH, PHONE_MODEL, BRAND, PRODUCT, ANDROID_VERSION, BUILD, TOTAL_MEM_SIZE, AVAILABLE_MEM_SIZE, STACK_TRACE, INITIAL_CONFIGURATION, CRASH_CONFIGURATION, DISPLAY, USER_APP_START_DATE, USER_CRASH_DATE, IS_SILENT, INSTALLATION_ID, USER_EMAIL, DEVICE_FEATURES, ENVIRONMENT, SHARED_PREFERENCES, SETTINGS_SYSTEM, SETTINGS_SECURE, APPLICATION_LOG})

//@ReportsCrashes(
//	      formKey = "" // This is required for backward compatibility but not used
//	      , formUri = "http://www.backendofyourchoice.com/reportpath"
//	      , applicationLogFile = "applog.log"
//	  )


public class MyApplication extends Application {

	@Override
	public void onCreate(){
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		super.onCreate();
		
        if (BuildConfig.DEBUG)
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.v(CommonUtilities.LOG_TAG, "Application is debuggable.  Enabling additional debug logging"); //$NON-NLS-1$
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            {
                enableApiLevel9Debugging();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                enableApiLevel11Debugging();
            }

            /*
             * If using the Fragment compatibility library, enable debug logging here
             */
            // android.support.v4.app.FragmentManager.enableDebugLogging(true);
            // android.support.v4.app.LoaderManager.enableDebugLogging(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static void enableApiLevel9Debugging()
    {
        android.os.StrictMode.enableDefaults();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void enableApiLevel11Debugging()
    {
        android.app.LoaderManager.enableDebugLogging(true);
        android.app.FragmentManager.enableDebugLogging(true);
    }

	
}
