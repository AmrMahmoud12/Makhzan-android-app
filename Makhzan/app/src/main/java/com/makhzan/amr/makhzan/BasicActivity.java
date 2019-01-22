package com.makhzan.amr.makhzan;

import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class BasicActivity extends AppCompatActivity {
     private Toolbar toolbars;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_basic);
	 toolbars = findViewById(R.id.action);
	 setSupportActionBar(toolbars);
//String CategoryPosition=getIntent().getStringExtra("CategoryPosition");
//String countryName=getIntent().getStringExtra("countryName");
//
//	 TabLayout tabLayout = findViewById(R.id.tablayout);
//	 TabItem ads = findViewById(R.id.adsPage);
//	 TabItem orders = findViewById(R.id.ordersPage);
//	 ViewPager viewPager = findViewById(R.id.viewpager);
//
//	 PagerAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
//	 viewPager.setAdapter(pagerAdapter);
//
//	 viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

     }


}
