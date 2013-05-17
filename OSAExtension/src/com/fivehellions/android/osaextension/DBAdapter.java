package com.fivehellions.android.osaextension;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper {

	private static final int DB_VERSION=2;
	private static String DB_PATH = "";
	private static final String DB_NAME = "osaextension.sqlite";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	private static DBAdapter mDBConnection;

	private static int openconnections=0;
	
	private Devicelog mydevicelog;
	
	/**
	 * Constructor 
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	private DBAdapter(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
		
		String mycontextstring="null";
		String datadir="null";
		
		if (myContext != null){
			mycontextstring=myContext.toString();
			if (myContext.getApplicationInfo() != null) {
				datadir=myContext.getApplicationInfo().dataDir + "/databases/";
			}
			
			mydevicelog= new Devicelog(myContext);
			mydevicelog.log("in DBAdapter - myContext="+mycontextstring+", dataDir="+datadir, Log.DEBUG);
			
		} else {
			Log.w("osaextension","in DBAdapter and myContext is null");
		}
		
		DB_PATH = datadir;
		
	}
	
	/**
	 * getting Instance
	 * @param context
	 * @return DBAdapter
	 */
	public static synchronized DBAdapter getDBAdapterInstance(Context context) {
		if (mDBConnection == null) {
			mDBConnection = new DBAdapter(context);
		}
		
		return mDBConnection;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own database.
	 **/
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		SQLiteDatabase db_Read = null;
		
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling following method 
			// 1) an empty database will be created into the default system path of your application 
			// 2) than we overwrite that database with our database.
			db_Read = this.getReadableDatabase(); 
			db_Read.close();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			mydevicelog.log("*****database does not exist so we will create it",Log.VERBOSE);
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		    // Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		    // Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		    // Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		    // transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		    // Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
 
	/**
	 * Open the database
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		//String myPath = DB_PATH + DB_NAME;
		//myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		
		mydevicelog.log("openconnections before open:"+openconnections,Log.DEBUG);
		
		openconnections++;
		
		mydevicelog.log("openconnections after open:"+openconnections,Log.DEBUG);
		
		myDataBase = this.getWritableDatabase(); 

	}

	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized void close() {
		
		
		mydevicelog.log("openconnections before close:"+openconnections,Log.DEBUG);
		
		openconnections--;
		
		mydevicelog.log("openconnections after close:"+openconnections,Log.DEBUG);
		
		if (openconnections<=0){
			openconnections=0;
			if (myDataBase != null){
				myDataBase.close();
				mydevicelog.log("database closed:"+openconnections,Log.DEBUG);
			}
			super.close();
		}

	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables again - upgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if (oldVersion<=1){
			db.execSQL("alter table notification_log add column vibrate INTEGER NOT NULL DEFAULT 0;");
			db.execSQL("alter table notification_actions add column vibrate INTEGER NOT NULL DEFAULT 0;");
		}

	}

	public void beginTransaction() {
		myDataBase.beginTransaction();		
	}
	
	public void endTransaction() {
		myDataBase.endTransaction();	
	}
	
	public void setTransactionSuccessful() {
		myDataBase.setTransactionSuccessful();	
	}
	
	// ----------------------- CRUD Functions ------------------------------
	
	/**
	 * This function used to select the records from DB.
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return A Cursor object, which is positioned before the first entry.
	 */
	public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
			String whereClase, String whereArgs[], String groupBy,
			String having, String orderBy) {
		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
				groupBy, having, orderBy);
	}
	
	/**
	 * select records from db and return in list
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ArrayList<ArrayList<String>>
	 */
	public ArrayList<ArrayList<String>> selectRecordsFromDBList(String tableName, String[] tableColumns,
			String whereClase, String whereArgs[], String groupBy,
			String having, String orderBy) {		
		
		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
	      ArrayList<String> list = new ArrayList<String>();
	      Cursor cursor = myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
					groupBy, having, orderBy);        
	      if (cursor.moveToFirst()) {
	         do {
	        	 list = new ArrayList<String>();
	        	 for(int i=0; i<cursor.getColumnCount(); i++){	        		 
	        		 list.add( cursor.getString(i) );
	        	 }	 
	        	 retList.add(list);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return retList;

	}	

	/**
	 * This function used to insert the Record in DB. 
	 * @param tableName
	 * @param nullColumnHack
	 * @param initialValues
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertRecordsInDB(String tableName, String nullColumnHack,
			ContentValues initialValues) {
		return myDataBase.insert(tableName, nullColumnHack, initialValues);
	}

	
	
	/**
	 * This function used to update the Record in DB.
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return true / false on updating one or more records
	 */
	public boolean updateRecordInDB(String tableName,
			ContentValues initialValues, String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs) > 0;				
	}
	
	/**
	 * This function used to update the Record in DB.
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	public int updateRecordsInDB(String tableName,
			ContentValues initialValues, String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause, whereArgs);		
	}

	
	/**
	 * This function used to replace the Record in DB.
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return true / false on updating one or more records
	 */
	public boolean replaceRecordInDB(String tableName, String nullColumnHack,
			ContentValues initialValues) {
		return myDataBase.replace(tableName, nullColumnHack, initialValues) > 0;				
	}
	
	/**
	 * This function used to replace the Record in DB.
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	public long replaceRecordsInDB(String tableName, String nullColumnHack,
			ContentValues initialValues) {
		return myDataBase.replace(tableName, nullColumnHack, initialValues);
	}	
	
	
	/**
	 * This function used to delete the Record in DB.
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are deleted.
	 */
	public int deleteRecordInDB(String tableName, String whereClause,
			String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	// --------------------- Select Raw Query Functions ---------------------
	
	/**
	 * apply raw Query
	 * @param query
	 * @param selectionArgs
	 * @return Cursor
	 */
	public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);		
	}
	
	/**
	 * apply raw query and return result in list
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList<ArrayList<String>>
	 */
	public ArrayList<ArrayList<String>> selectRecordsFromDBList(String query, String[] selectionArgs) {	      
	      ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
	      ArrayList<String> list = new ArrayList<String>();
	      Cursor cursor = myDataBase.rawQuery(query, selectionArgs);	        
	      if (cursor.moveToFirst()) {
	         do {
	        	 list = new ArrayList<String>();
	        	 for(int i=0; i<cursor.getColumnCount(); i++){	        		 
	        		 list.add( cursor.getString(i) );
	        	 }	 
	        	 retList.add(list);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return retList;
	   }

	/**
	 * apply raw Query
	 * @param query
	 * @param selectionArgs
	 * @return rowsUpdated
	 */
	public int runRawQuery(String query, String[] selectionArgs) {
		Cursor cursor =  myDataBase.rawQuery(query, selectionArgs);
		
		int rowsUpdated;
		rowsUpdated=cursor.getCount();
		
	      if (cursor != null && !cursor.isClosed()) {
		         cursor.close();
	      }
		
		return rowsUpdated;
	}
	
	
	/**
	 * gets last generated ID
	 * @return ID
	 * */
	public int getLastId(){
		
		try {
        	// get _id of last inserted record
        	String query="SELECT last_insert_rowid();";
        	ArrayList<ArrayList<String>> stringList = selectRecordsFromDBList(query, null);
        	
        	if (stringList.isEmpty()) {
        		return -1;
        	}
        	else
            {
            	ArrayList<String> list = stringList.get(0);
            	String myNewId = list.get(0);
            	return Integer.parseInt(myNewId);
            }
		}catch (Exception e) {
			mydevicelog.log(e.getMessage(),Log.ERROR);
			return -1;
		}		
		
	}
	
}
