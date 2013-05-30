package com.opensourceautomation.android.osaextension.utilities;

import com.opensourceautomation.android.osaextension.BuildConfig;

import android.content.Context;



/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities  {


	
	public static final int RESULT_SAVE = 1;
	public static final int RESULT_DELETE= -1;
	public static final int RESULT_CANCEL = 0;


	/**
	 * Google API project id registered to use GCM.
	 */
	public static final String SENDER_ID = "824659990707";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "OSAExtension";

	/**
	 * Intent used to display a message in the screen.
	 */
	static final String DISPLAY_MESSAGE_ACTION =
			"com.opensourceautomation.android.osaextension.DISPLAY_MESSAGE";

	public static final String DISPLAY_TESTMESSAGE_ACTION =
			"com.opensourceautomation.android.osaextension.DISPLAY_MESSAGE";

	public static final String REFRESH_LOG_ACTION =
			"com.opensourceautomation.android.osaextension.REFRESH_LOG_ACTION";
	
	
	/**
	 * Intent used to show register results in the screen.
	 */
	public static final String REGISTER_RESULTS_ACTION =
			"com.opensourceautomation.android.osaextension.REGISTER_RESULTS";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public static final String EXTRA_MESSAGE = "message";
	static final String EXTRA_CATEGORY = "category";
	static final String EXTRA_LEVEL = "level";
	static final String EXTRA_OSAID = "osaid";
	static final String EXTRA_DATE = "messagedate";


	
	
	/**
	 * Below are constants for the Tasker/Local plugin part of app 
	 * */
	
	 /**
     * Log tag for logcat messages.
     */
    // TODO: Change this to your application's own log tag.
    public static final String LOG_TAG = "OSAExtension"; //$NON-NLS-1$

    /**
     * Flag to enable logcat messages.
     */
    public static final boolean IS_LOGGABLE = BuildConfig.DEBUG;

    /**
     * Flag to enable runtime checking of method parameters.
     */
    public static final boolean IS_PARAMETER_CHECKING_ENABLED = BuildConfig.DEBUG;

    /**
     * Flag to enable runtime checking of whether a method is called on the correct thread.
     */
    public static final boolean IS_CORRECT_THREAD_CHECKING_ENABLED = BuildConfig.DEBUG;

    /**
     * Determines the "versionCode" in the {@code AndroidManifest}.
     *
     * @param context to read the versionCode.
     * @return versionCode of the app.
     */
    public static int getVersionCode(final Context context)
    {
        if (CommonUtilities.IS_PARAMETER_CHECKING_ENABLED)
        {
            if (null == context)
            {
                throw new IllegalArgumentException("context cannot be null"); //$NON-NLS-1$
            }
        }

        try
        {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
        catch (final UnsupportedOperationException e)
        {
            /*
             * This exception is thrown by test contexts
             */

            return 1;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private CommonUtilities()
    {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
	 
	
	
	
	
}


