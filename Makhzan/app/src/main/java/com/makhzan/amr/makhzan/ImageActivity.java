package com.makhzan.amr.makhzan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
     ImageView myImage;
     String url = "";
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_image);

	 url = getIntent().getStringExtra("image_url");

	 myImage = findViewById(R.id.myImage);
	 Picasso.get().load(url)
	     .placeholder(R.drawable.ic_launcher_background)
	     .error(R.drawable.ic_launcher_background)
	     .into(myImage);
     }
}
