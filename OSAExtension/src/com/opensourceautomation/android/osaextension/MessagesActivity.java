package com.opensourceautomation.android.osaextension;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;
import com.viewpagerindicator.TabPageIndicator;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MessagesActivity extends FragmentActivity {
    private static final String[] CONTENT = new String[] { "Log", "Alert Setup"};
    
    
	private static Devicelog mydevicelog;
	private Context myContext;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagesactivity);

        myContext=this;
        mydevicelog= new Devicelog(this);
        
        FragmentPagerAdapter adapter = new MainAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
	

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.menu_settings:
            	openSettings();
                return true;
                
            case R.id.menu_webui:
            	openWebUI();
                return true;                
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    private void openSettings(){
    	
    	Intent myIntent = new Intent(this,Settings.class); 
    	
    	this.startActivity(myIntent); 
    	    	
    }
    
    private void openWebUI(){
    	
    	Intent myIntent = new Intent(this,MainActivity.class); 
    	
    	this.startActivity(myIntent); 
    	    	
    }
    
    
    class MainAdapter extends FragmentPagerAdapter {
        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment myfrag=null;
        	
        	mydevicelog.log( "Fragment getItem() position:"+ Integer.toString(position),Log.DEBUG);
        	
        	    	
        	switch (position) {
        	
        	case 0:
        		myfrag= Messages_Log.newInstance(myContext);
        		break;
        		
        	case 1:
        		myfrag= Messages_NotificationSetup.newInstance(myContext);
        		break;

        	default:
        		myfrag= Messages_Placeholder.newInstance(myContext);
        		break;	
        	}
        	
            return myfrag;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
          return CONTENT.length;
        }
    }
}
