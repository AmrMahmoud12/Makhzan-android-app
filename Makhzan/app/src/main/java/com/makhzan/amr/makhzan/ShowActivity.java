package com.makhzan.amr.makhzan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

public class ShowActivity extends AppCompatActivity {
     private TextView textView;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_show);

	 textView = findViewById(R.id.textView);
		 textView.setText(SharedPrefManager.getmInstance(this).getToken());



     }
}
