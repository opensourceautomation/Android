package com.fivehellions.android.osaextension;



/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities  {


	
	static final int RESULT_SAVE = 1;
	static final int RESULT_DELETE= -1;
	static final int RESULT_CANCEL = 0;


	/**
	 * Google API project id registered to use GCM.
	 */
	static final String SENDER_ID = "824659990707";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "OSAExtension";

	/**
	 * Intent used to display a message in the screen.
	 */
	static final String DISPLAY_MESSAGE_ACTION =
			"com.fivehellions.android.osaextension.DISPLAY_MESSAGE";

	static final String DISPLAY_TESTMESSAGE_ACTION =
			"com.fivehellions.android.osaextension.DISPLAY_MESSAGE";

	static final String REFRESH_LOG_ACTION =
			"com.fivehellions.android.osaextension.REFRESH_LOG_ACTION";
	
	
	/**
	 * Intent used to show register results in the screen.
	 */
	static final String REGISTER_RESULTS_ACTION =
			"com.fivehellions.android.osaextension.REGISTER_RESULTS";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	static final String EXTRA_MESSAGE = "message";
	static final String EXTRA_CATEGORY = "category";
	static final String EXTRA_LEVEL = "level";
	static final String EXTRA_OSAID = "osaid";
	static final String EXTRA_DATE = "messagedate";


}


