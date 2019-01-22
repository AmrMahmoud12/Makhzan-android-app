package com.makhzan.amr.makhzan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class NotificationActivity extends AppCompatActivity {
     //_________TEXTVIEWS_______________
     TextView Co_NameN,
	ad_discriptionN,
	phone_numberN,
	place_adN;

     //________IMAGE VIEW_______________
     ImageView imageViewN;

     //________BUTTONS______________
     Button showFollowings;
     //_________________FIREBASE_________________
     private ListenerRegistration listenerRegistration;
     DocumentReference firebaseFirestoree;
     private FirebaseFirestore firebaseFirestore;
     //progressbar
     ProgressBar progressBar;
     private String UID;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_notification);
	 //TEXTVIEWS
	 Co_NameN = findViewById(R.id.SingleAd_CoNameN);
	 ad_discriptionN = findViewById(R.id.disrcibe_adN);
	 phone_numberN = findViewById(R.id.phoneN);
	 place_adN = findViewById(R.id.place_adN);
	 imageViewN = findViewById(R.id.Ad_PictureN);
	 //FIREBASE
//	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//	 //assert currentUser != null;
//	 assert currentUser != null;
//	 UID = currentUser.getUid();
//	 firebaseFirestore = FirebaseFirestore.getInstance();
	 String title = getIntent().getStringExtra("title");
//	 String myPlacestxt = getIntent().getStringExtra("myPlacestxt");
//	 String phoneNumbera = getIntent().getStringExtra("phoneNumbera");
//	 String pl = getIntent().getStringExtra("pl");
//
//	 //SET INFO TO ITS VIEWS
//	 Co_NameN.setText(n);
	 ad_discriptionN.setText(title);
//	 phone_numberN.setText(p);
//	 place_adN.setText(pl);
     }
}
