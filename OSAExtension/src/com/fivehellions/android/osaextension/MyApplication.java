package com.fivehellions.android.osaextension;


import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import static org.acra.ReportField.*;

import android.app.Application;

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
		
	}
	
}
