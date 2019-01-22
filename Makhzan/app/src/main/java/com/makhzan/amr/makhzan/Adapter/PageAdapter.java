package com.makhzan.amr.makhzan.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.makhzan.amr.makhzan.OneFragment;
import com.makhzan.amr.makhzan.ThreeFragment;
import com.makhzan.amr.makhzan.TwoFragment;

public class PageAdapter extends FragmentPagerAdapter {
     private static final int NUM_OF_SLIDER = 2;
     private String tabTitles[] = new String[]{ "Shopping","Retails"};

     public PageAdapter(FragmentManager fm) {
	 super(fm);
     }

     @Nullable
     @Override
     public CharSequence getPageTitle(int position) {
	 return tabTitles[position];
     }


     @Override
     public Fragment getItem(int position) {
	 switch ( position ) {
	      case 0:
		  return new TwoFragment();
	      case 1:
		  return new OneFragment();
	      default:
		  return null;
	 }

     }

     @Override
     public int getCount() {
	 return NUM_OF_SLIDER;
     }
}
