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

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.opensourceautomation.android.osaextension.utilities.CommonUtilities;

/**
 * Class for managing the {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} for this plug-in.
 */
public final class PluginBundleManager
{
    /**
     * Type: {@code String}.
     * <p>
     * String message to display in a Toast message.
     */
    public static final String BUNDLE_EXTRA_STRING_NAMEDSCRIPT = "com.opensourceautomation.android.osaextension.extra.STRING_NAMEDSCRIPT"; //$NON-NLS-1$

    public static final String BUNDLE_EXTRA_STRING_METHODCONTAINER = "com.opensourceautomation.android.osaextension.extra.CONTAINER"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_METHODNAME = "com.opensourceautomation.android.osaextension.extra.METHOD"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_METHODOBJECT = "com.opensourceautomation.android.osaextension.extra.OBJECT"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_METHODPARAMETER1NAME = "com.opensourceautomation.android.osaextension.extra.PONENAME"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_METHODPARAMETER2NAME = "com.opensourceautomation.android.osaextension.extra.PTWONAME"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE = "com.opensourceautomation.android.osaextension.extra.PONEVALUE"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_METHODPARAMETER2VALUE = "com.opensourceautomation.android.osaextension.extra.PTWOVALUE"; //$NON-NLS-1$
    public static final String BUNDLE_EXTRA_STRING_REPLACE_KEYS="net.dinglisch.android.tasker.extras.VARIABLE_REPLACE_KEYS";
    /**
     * Type: {@code int}.
     * <p>
     * versionCode of the plug-in that saved the Bundle.
     */
    /*
     * This extra is not strictly required, however it makes backward and forward compatibility significantly
     * easier. For example, suppose a bug is found in how some version of the plug-in stored its Bundle. By
     * having the version, the plug-in can better detect when such bugs occur.
     */
    public static final String BUNDLE_EXTRA_INT_VERSION_CODE =
            "com.yourcompany.yourcondition.extra.INT_VERSION_CODE"; //$NON-NLS-1$

   

    /**
     * Method to verify the content of the bundle are correct.
     * <p>
     * This method will not mutate {@code bundle}.
     *
     * @param bundle bundle to verify. May be null, which will always return false.
     * @return true if the Bundle is valid, false if the bundle is invalid.
     */
    public static boolean isNamedScriptBundleValid(final Bundle bundle)
    {
        if (null == bundle)
        {
            return false;
        }

        /*
         * Make sure the expected extras exist
         */
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_NAMEDSCRIPT))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_STRING_NAMEDSCRIPT)); //$NON-NLS-1$
            }
            return false;
        }
        if (!bundle.containsKey(BUNDLE_EXTRA_INT_VERSION_CODE))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$
            }
            return false;
        }

        /*
         * Make sure the correct number of extras exist. Run this test after checking for specific Bundle
         * extras above so that the error message is more useful. (E.g. the caller will see what extras are
         * missing, rather than just a message that there is the wrong number).
         */
        if (2 != bundle.keySet().size())
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain 2 keys, but currently contains %d keys: %s", bundle.keySet().size(), bundle.keySet())); //$NON-NLS-1$
            }
            return false;
        }

        if (TextUtils.isEmpty(bundle.getString(BUNDLE_EXTRA_STRING_NAMEDSCRIPT)))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle extra %s appears to be null or empty.  It must be a non-empty string", BUNDLE_EXTRA_STRING_NAMEDSCRIPT)); //$NON-NLS-1$
            }
            return false;
        }

        if (bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 0) != bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle extra %s appears to be the wrong type.  It must be an int", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$
            }

            return false;
        }

        return true;
    }    
    
    public static boolean isMethodBundleValid(final Bundle bundle)
    {
        if (null == bundle)
        {
            return false;
        }

        /*
         * Make sure the expected extras exist
         */
        
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_METHODNAME))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_STRING_METHODNAME)); //$NON-NLS-1$
            }
            return false;
        }
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_METHODOBJECT))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_STRING_METHODOBJECT)); //$NON-NLS-1$
            }
            return false;
        }
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE)); //$NON-NLS-1$
            }
            return false;
        }
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_METHODPARAMETER2VALUE))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_STRING_METHODPARAMETER2VALUE)); //$NON-NLS-1$
            }
            return false;
        }        
        if (!bundle.containsKey(BUNDLE_EXTRA_INT_VERSION_CODE))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle must contain extra %s", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$
            }
            return false;
        }


        if (bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 0) != bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1))
        {
            if (CommonUtilities.IS_LOGGABLE)
            {
                Log.e(CommonUtilities.LOG_TAG,
                      String.format("bundle extra %s appears to be the wrong type.  It must be an int", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$
            }

            return false;
        }

        return true;
    }    
    
    /**
     * @param context Application context.
     * @param namedscript The namedscript to be executed in OSA. Cannot be null.
     * @return A plug-in bundle.
     */
    public static Bundle generateNamedScriptBundle(final Context context, final String namedscript)
    {
        final Bundle result = new Bundle();
        result.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, CommonUtilities.getVersionCode(context));
        result.putString(BUNDLE_EXTRA_STRING_NAMEDSCRIPT, namedscript);

        return result;
    }
    
    public static Bundle generateMethodBundle(final Context context, final String methodcontainer, final String methodobject, final String methodname, final String methodp1name, final String methodp2name, final String methodp1value, final String methodp2value)
    {
        final Bundle result = new Bundle();
        result.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, CommonUtilities.getVersionCode(context));
        result.putString(BUNDLE_EXTRA_STRING_METHODCONTAINER, methodcontainer);
        result.putString(BUNDLE_EXTRA_STRING_METHODNAME, methodname);
        result.putString(BUNDLE_EXTRA_STRING_METHODOBJECT, methodobject);
        result.putString(BUNDLE_EXTRA_STRING_METHODPARAMETER1NAME, methodp1name);
        result.putString(BUNDLE_EXTRA_STRING_METHODPARAMETER2NAME, methodp2name);
        result.putString(BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE, methodp1value);
        result.putString(BUNDLE_EXTRA_STRING_METHODPARAMETER2VALUE, methodp2value);
        
        result.putString(BUNDLE_EXTRA_STRING_REPLACE_KEYS, BUNDLE_EXTRA_STRING_METHODPARAMETER1VALUE);
        
        return result;
    }
    
    /**
     * Private constructor prevents instantiation
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginBundleManager()
    {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}