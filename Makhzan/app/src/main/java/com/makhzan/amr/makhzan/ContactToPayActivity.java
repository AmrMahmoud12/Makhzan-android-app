package com.makhzan.amr.makhzan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContactToPayActivity extends AppCompatActivity {
TextView contactPhone,payingtxt;
private FirebaseFirestore firebaseFirestore;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_contact_to_pay);

	 contactPhone=findViewById(R.id.contactPhone);
	 payingtxt=findViewById(R.id.payingtxt);
	firebaseFirestore=FirebaseFirestore.getInstance();
	firebaseFirestore.collection("ContactInformation")
	    .document("PaymentTxt")
	    .get()
	    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
	     @Override
	     public void onSuccess(DocumentSnapshot documentSnapshot) {
		String txt=documentSnapshot.getString("txt");
		payingtxt.setText(txt);
	     }
	});
	firebaseFirestore.collection("ContactInformation")
	    .document("PaymentTelephone")
	    .get()
	    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
		@Override
		public void onSuccess(DocumentSnapshot documentSnapshot) {
		String tlp= String.valueOf(documentSnapshot.getLong("number"));
		contactPhone.setText(tlp);
		}
	    });
     }
}
