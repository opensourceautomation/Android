package com.opensourceautomation.android.osaextension;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.opensourceautomation.android.osaextension.R;
import com.opensourceautomation.android.osaextension.utilities.Devicelog;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class WizardActivity extends FragmentActivity  {
	
	private static Devicelog mydevicelog;
	
	private Context myContext;
	
	wizFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
        
    private Button mNextButton;
    private Button mPrevButton;
    
    private Boolean refreshqued=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard);
       
        myContext=this;
        
        mydevicelog= new Devicelog(this);
        
        mAdapter = new wizFragmentAdapter(this,getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
        LinePageIndicator indicator = (LinePageIndicator)findViewById(R.id.indicator);
        mIndicator = indicator;
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setStrokeWidth(4 * density);
        indicator.setLineWidth(30 * density);
        
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	mIndicator.setCurrentItem(position);
                updateBottomBar();
                
                if (refreshqued){
                	refresh();
                	refreshqued=false;
                }
                
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if (mPager.getCurrentItem()<mPager.getChildCount()){
            		mPager.setCurrentItem(mPager.getCurrentItem() + 1);	
            	} else {
            		finish();

//                	Intent myIntent = new Intent(myContext,MessagesActivity.class); 
//                	myContext.startActivity(myIntent); 
            	}
                
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        updateBottomBar();
        
    }
    

    public void querefresh(){
    	refreshqued=true;
    }
    
	public void refresh() {
    	mydevicelog.log( "calling notifyDataSetChanged on mAdapter",Log.DEBUG);
    	mAdapter.notifyDataSetChanged();
    }
    
    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        
        if (position+1 == mAdapter.getCount()){
        	mNextButton.setText(R.string.txt_finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        }else {
            mNextButton.setText(R.string.txt_next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }
    
       

class wizFragmentAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

	private FragmentManager mFragmentManager;
    private int mCount = 4;
    
    public wizFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        
        mFragmentManager = fm;
        
    	myContext=context;
    	mydevicelog= new Devicelog(myContext);

    }

    @Override
    public Fragment getItem(int position) {
    	
    	Fragment myfrag=null;
    	
    	mydevicelog.log( "Fragment getItem() position:"+ Integer.toString(position),Log.DEBUG);
    	
    	    	
    	switch (position) {
    	
    	case 0:
    		myfrag= Wizard_Server.newInstance(myContext);
    		break;
    		
    	case 1:
    		myfrag= Wizard_Register.newInstance(myContext);
    		break;
    		
    	case 2:
    		myfrag= Wizard_Object.newInstance(myContext);
    		break;
    		
    	case 3:
    		myfrag= Wizard_Test.newInstance(myContext);
    		break;
    		    		
    	}
    	
        return myfrag;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return "pagetitle";
    }

    @Override
    public int getIconResId(int index) {
      return index;
    }

    @Override
    public int getItemPosition(Object object) {
    	
    	mydevicelog.log( "in getItemPosition",Log.DEBUG);
    	
    	//if (object instanceof wizJustification){
    		//mydevicelog.log( "in getItemPosition - instance of wizJustification",Log.DEBUG);
    		return POSITION_NONE;
    	//}
    	
    	//return POSITION_UNCHANGED;
    }
    
    public void setCount(int count) {
        if (count > 0 && count <= 50) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
   

    
}
    
}