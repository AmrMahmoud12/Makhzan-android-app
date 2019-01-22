package com.makhzan.amr.makhzan;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class CustomersActivity extends AppCompatActivity {
     TextView Compname, Compaddress, followCust, unfollowCust;
     private ProgressBar progressBar;
     private FirebaseFirestore firebaseFirestore;
     private String UID;
     private String myName;
     private String customerName,customerid;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_customers);
	 customerid = getIntent().getStringExtra("customerid");
	 customerName = getIntent().getStringExtra("customerName");

	 Compname = findViewById(R.id.Compname);
	 Compaddress = findViewById(R.id.Compaddress);
	 followCust = findViewById(R.id.followCust);
	 unfollowCust = findViewById(R.id.unfollowCust);
	 progressBar=findViewById(R.id.CustomerProgress);
	progressBar.setVisibility(View.GONE);
	 //FIREBASE
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 //assert currentUser != null;
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 firebaseFirestore = FirebaseFirestore.getInstance();

	 //SET TEXTS INSIDE VIEWS
	Compname.setText(customerName);


	 getUidName();
	 isFollowing();
	 followCust.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  progressBar.setVisibility(View.VISIBLE);
		  final Map<String, Object> map = new HashMap<>();
		  //put id in the database as true
		  map.put(customerid,true );
		  map.put("followingName", customerName);
		  map.put("uid",customerid);

		  firebaseFirestore.collection("System")
		      .document(UID)
		      .collection("followingCustomers")
		      .document(customerid)
		      .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
		       @Override
		       public void onSuccess(Void aVoid) {
			   getUidName();

			   Map<String, Object> map1 = new HashMap<>();
			   map1.put(UID, true);
			   map1.put("followerCompanyName", myName);
			   map1.put("uid",UID);

			   firebaseFirestore.collection("System")
			       .document(customerid)
			       .collection("followersCompanies")
			       .document(UID)
			       .set(map1)
			       .addOnSuccessListener(new OnSuccessListener<Void>() {
				   @Override
				   public void onSuccess(Void aVoid) {
				        Toast.makeText(CustomersActivity.this, "تمت المتابعة", Toast.LENGTH_SHORT).show();
				        setFollowing();
				        String x=customerid+"1";

				        FirebaseMessaging.getInstance().subscribeToTopic(x).addOnSuccessListener(new OnSuccessListener<Void>() {
					    @Override
					    public void onSuccess(Void aVoid) {
						progressBar.setVisibility(View.GONE);

					    }
				        });
				   }
			       });
		       }
		  });
	      }
	 });

	 unfollowCust.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  progressBar.setVisibility(View.VISIBLE);
		  firebaseFirestore
		      .collection("System")
		      .document(UID)
		      .collection("followingCustomers")
		      .document(customerid)
		      .delete()
		      .addOnSuccessListener(new OnSuccessListener<Void>() {
			  @Override
			  public void onSuccess(Void aVoid) {
			       firebaseFirestore
				  .collection("System")
				  .document(customerid)
				  .collection("followersCompanies")
				  .document(UID)
				  .delete()
				  .addOnSuccessListener(new OnSuccessListener<Void>() {
				       @Override
				       public void onSuccess(Void aVoid) {
					   setUnFollowing();
					   String x=customerid+"1";
					   Toast.makeText(CustomersActivity.this, "تم الغاء المتابعة", Toast.LENGTH_SHORT).show();
					   FirebaseMessaging.getInstance().unsubscribeFromTopic(x);

					   progressBar.setVisibility(View.INVISIBLE);

				       }
				  });
			  }
		      });
	      }
	 });
     }


     //IT IS TO CHECK FOLLOWING TO CHANGE BUTTON FROM VIS TO INVIS
     public void isFollowing() {

	 if ( UID.equals(customerid) ) {//ceck if they are the same tom prevent app crash

	      followCust.setVisibility(View.GONE);
	      unfollowCust.setVisibility(View.GONE);

	 } else {

	      setUnFollowing();

	      CollectionReference coRef = firebaseFirestore
		 .collection("System")
		 .document(UID)
		 .collection("followingCustomers");
	      coRef.whereEqualTo(customerid, true).
		 get()
		 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
		      @Override
		      public void onComplete(@NonNull Task<QuerySnapshot> task) {
			  if ( task.isSuccessful() ) {
			       for (QueryDocumentSnapshot document : task.getResult()) {

				   // Toast.makeText(SingleAdActivity.this, "succeeded", Toast.LENGTH_SHORT).show();
				   //Log.d(TAG, document.getId() + " => " + document.getData());
				   setFollowing();

			       }
			  } else {
			       //Toast.makeText(SingleAdActivity.this, "awwds", Toast.LENGTH_SHORT).show();
			       // Log.d(TAG, "Error getting documents: ", task.getException());
			  }
		      }
		 });
	 }

     }

     public void getUidName() {
	 firebaseFirestore.collection("Users").document(UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
	      @Override
	      public void onSuccess(DocumentSnapshot documentSnapshot) {
		  myName = documentSnapshot.getString("companyName");

	      }
	 });
     }


     public void setFollowing() {
	 followCust.setVisibility(View.GONE);
	 unfollowCust.setVisibility(View.VISIBLE);
     }

     public void setUnFollowing() {
	 followCust.setVisibility(View.VISIBLE);
	 unfollowCust.setVisibility(View.GONE);
     }


}
