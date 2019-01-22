package com.makhzan.amr.makhzan;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makhzan.amr.makhzan.Adapter.PageAdapter;
import com.squareup.picasso.Picasso;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.List;

import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.event.OnSlideClickListener;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class SignOutScreenActivity extends AppCompatActivity {
     private int[] navIcons = {
	R.drawable.adnameicon,
	R.drawable.ad_icon,

     };
     private int[] navLabels = {
         R.string.shop,
	R.string.gomla
     };
     // another resouces array for active state for the icon
     private int[] navIconsActive = {
	R.drawable.ad_icon,
	R.drawable.adnameicon
     };
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_sign_out_screen);
	 TabLayout tabLayout=findViewById(R.id.tabItems);
	 ViewPager fragmentPager=findViewById(R.id.fragmentsPager);
	 fragmentPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
	 tabLayout.setupWithViewPager(fragmentPager);
	 for (int i = 0; i < tabLayout.getTabCount(); i++) {
	      tabLayout.getTabAt(0).setIcon(R.drawable.shopping);
	      tabLayout.getTabAt(1).setIcon(R.drawable.retails);

//
//	      // inflate the Parent LinearLayout Container for the tab
//	      // from the layout nav_tab.xml file that we created 'R.layout.nav_tab
//	      LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tabslayout_title, null);
//	      // get child TextView and ImageView from this layout for the icon and label
//	      TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
//	      ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);
//	      // set the label text by getting the actual string value by its id
//	      // by getting the actual resource value `getResources().getString(string_id)`
//	      tab_label.setText(getResources().getString(navLabels[i]));
//
//	      // set the home to be active at first
//	      if(i == 0) {
//		  tab_label.setTextColor(getResources().getColor(R.color.Adnamecolor));
//		  tab_icon.setImageResource(navIconsActive[i]);
//	      } else {
//		  tab_icon.setImageResource(navIcons[i]);
//	      }
//	      // finally publish this custom view to navigation tab
//	      tabLayout.getTabAt(i).setCustomView(tab);
	 }
	 }


}
//	 PicassoImageLoadingService picassoImageLoadingService=new PicassoImageLoadingService(this);
//	 Slider.init(picassoImageLoadingService);
//
//	 Slider slider = findViewById(R.id.banner_slider1);
//
//	  slider.setAdapter(new MainSliderAdapter());
//	 slider.setOnSlideClickListener(new OnSlideClickListener() {
//	      @Override
//	      public void onSlideClick(int position) {
//
//
//
//	      }
//	 });
//
//     }
//     class MainSliderAdapter extends SliderAdapter {
//
//	 @Override
//	 public int getItemCount() {
//	      return 3;
//	 }
//
//	 @Override
//	 public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
//
//	       switch (position) {
//		  case 0:
//		       viewHolder.bindImageSlide("https://assets.materialup.com/uploads/dcc07ea4-845a-463b-b5f0-4696574da5ed/preview.jpg");
//		       break;
//		  case 1:
//		       viewHolder.bindImageSlide("https://assets.materialup.com/uploads/20ded50d-cc85-4e72-9ce3-452671cf7a6d/preview.jpg");
//		       break;
//		  case 2:
//		       viewHolder.bindImageSlide("https://assets.materialup.com/uploads/76d63bbc-54a1-450a-a462-d90056be881b/preview.png");
//		       break;
//	      }
//	 }
//     }
//     public class PicassoImageLoadingService implements ImageLoadingService {
//	 public Context context;
//
//	 public PicassoImageLoadingService(Context context) {
//	      this.context = context;
//	 }
//
//	 @Override
//	 public void loadImage(String url, ImageView imageView) {
//	      Picasso.get().load(url).into(imageView);
//	 }
//
//	 @Override
//	 public void loadImage(int resource, ImageView imageView) {
//	      Picasso.get().load(resource).into(imageView);
//	 }
//
//	 @Override
//	 public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
//	      Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
//	 }

