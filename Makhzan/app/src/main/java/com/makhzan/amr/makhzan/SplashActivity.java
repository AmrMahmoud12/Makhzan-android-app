package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
     private FirebaseAuth mAuth;
     private FirebaseAuth.AuthStateListener mAuthStateListener;
     FirebaseUser user;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_splash);
	 mAuth = FirebaseAuth.getInstance();
	 user = FirebaseAuth.getInstance().getCurrentUser();
	 Handler handler = new Handler();
	 handler.postDelayed(new Runnable() {
	      @Override
	      public void run() {
		  if (user != null) {
		       Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		       startActivity(intent);
		       finish();
		  } else {
		       Intent intent = new Intent(SplashActivity.this, Login2Activity.class);
		       startActivity(intent);
		       finish();
		       // No user is signed in
		  }
	      }
	 }, 2000);

     }

     @Override
     public void onStart() {
	 super.onStart();
	 // Check if user is signed in (non-null) and update UI accordingly.

     }
}
