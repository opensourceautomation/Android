package com.opensourceautomation.android.osaextension.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Devicelog {

	private final Context myContext;

	private int loglevel; // level at which we will write to logcat

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public Devicelog(Context context) {
		this.myContext = context;
	}

	public void log(String message, int level) {

		Boolean verboselogging = false;
		Boolean syslogenable = false;

		if (myContext != null) {
			SharedPreferences preferences;
			// preferences =
			// PreferenceManager.getDefaultSharedPreferences(myContext);
			preferences = myContext.getSharedPreferences(
					myContext.getPackageName() + "_preferences",
					Context.MODE_MULTI_PROCESS);

			// get logging prefs
			verboselogging = preferences.getBoolean("verboselogging", false);
			syslogenable = preferences.getBoolean("acra.syslog.enable", false);
		}

		if (verboselogging || 1==1) {
			loglevel = 3;
		} else {
			loglevel = 5;
		}

		if (message == null)
			message = "";

		if (level >= loglevel) {
			if (level <= 2) {
				Log.v("osaextension", message);
			}
			if (level == 3) {
				Log.d("osaextension", message);
			}
			if (level == 4) {
				Log.i("osaextension", message);
			}
			if (level == 5) {
				Log.w("osaextension", message);
			}
			if (level >= 6) {
				Log.e("osaextension", message);
			}

			if (syslogenable) {
				appendLog(message, true);
			} else {
				appendLog("log reporting turned off", false);
			}
		}

	}

	private void appendLog(String text, Boolean append) {
		if (myContext == null) {
			return;
		}

		int mylimit = 1000000;
		// File logFile = new File("applog.log");
		File logFile = new File(myContext.getFilesDir() + "/applog.log");

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("osaextension", e.getMessage());
			}
		}
		try {
			Boolean clearedfile = false;

			String now = new Date().toString() + ": ";
			// String nextline=System.getProperty("line.separator");
			// text=text+nextline;

			if (logFile.length() > mylimit) { // truncate if it reaches the
												// limit (not the best approach
												// to wipe everything at this
												// point but it will work for
												// now)
				new RandomAccessFile(logFile, "rw").setLength(0);
				clearedfile = true;
			}

			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					append));

			if (clearedfile) {
				buf.append(now);
				buf.append("cleared log file since it reached the limit");
				buf.newLine();
			}

			buf.append(now);
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			Log.e("osaextension", e.getMessage());
		}
	}

}