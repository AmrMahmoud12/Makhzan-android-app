package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.makhzan.amr.makhzan.items.MyAdCard;
import com.makhzan.amr.makhzan.items.Upload;

public class AdsOfFollowingActivity extends AppCompatActivity {
//FIREBASE
//FIREBASE
private FirebaseFirestore firebaseFirestore;
     private FirestoreRecyclerAdapter<MyAdCard, FollowingAdsActivityViewHolder> adapter;
     //RECYCLERVIEW
     private RecyclerView adsFollowingRV;
     private String UID;
     String PositionC;
     //PROGRESSBAR
     private ProgressBar AdsFollowingProgress;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_ads_of_following);


	  PositionC = getIntent().getStringExtra("CompanyPosition");
	 AdsFollowingProgress=findViewById(R.id.AdsFollowingProgress);
	 adsFollowingRV=findViewById(R.id.adsFollowingRV);
	 adsFollowingRV.setHasFixedSize(true);
	 adsFollowingRV.setLayoutManager(new LinearLayoutManager(this));


  	firebaseFirestore=FirebaseFirestore.getInstance();
  	 CollectionReference ref=firebaseFirestore.collection("System")
	     .document(PositionC)
	     .collection("ProfileAds");

	 Query query=ref.orderBy("creationDate", Query.Direction.DESCENDING);

	 FirestoreRecyclerOptions<MyAdCard> options=new FirestoreRecyclerOptions.Builder<MyAdCard>()
	     .setQuery(query,MyAdCard.class)
	     .build();
	 adapter=new FirestoreRecyclerAdapter<MyAdCard, FollowingAdsActivityViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull final FollowingAdsActivityViewHolder holder, final int position, @NonNull MyAdCard model) {
		  holder.publicAd_discription.setText(model.getMyAddiscriptiontxt());
		  holder.publicAd_Places.setText(model.getMyPlacestxt());
		  holder.publicAd_phone.setText(String.valueOf(model.getPhoneNumbera()));
		  holder.date_of_publish.setText(model.getCreationDate());
		  holder.adName.setText(model.getMyAdNametxt());
		  holder.c_name.setText(model.getCompanyName());
		  AdsFollowingProgress.setVisibility(View.GONE);
		  holder.itemView.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   //GET INFO TO SEND TO SINGLE AD ACTIVITY
			   String n = holder.c_name.getText().toString();
			   String d = holder.publicAd_discription.getText().toString();
			   String p = holder.publicAd_phone.getText().toString();
			   String pl = holder.publicAd_Places.getText().toString();


			   //_______GET ID OF THE ADVERTISEMENT_________
			   DocumentSnapshot snapshot = getSnapshots()
			       .getSnapshot(holder.getAdapterPosition());
			   String ss = snapshot.getId();
			   Intent i = new Intent(AdsOfFollowingActivity.this,ShowAdFromProfileActivity.class);
			   i.putExtra("documentId", ss);
			   i.putExtra("n", n);
			   i.putExtra("d", d);
			   i.putExtra("p", p);
			   i.putExtra("pl", pl);
			   i.putExtra("Publisher",PositionC);

			   Upload upload = new Upload();
			   i.putExtra("imageUrl", upload.getImageUrl());
			   startActivity(i);
		       }
		  });
	      }

	      @NonNull
	      @Override
	      public FollowingAdsActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_categoryads,parent,false);
		  return new FollowingAdsActivityViewHolder(view);
	      }
	 };
	 adsFollowingRV.setAdapter(adapter);

     }

     private class FollowingAdsActivityViewHolder extends RecyclerView.ViewHolder {
	 public FollowingAdsActivityViewHolder(View itemView) {
	      super(itemView);
	 }


	 TextView publicAd_discription = itemView.findViewById(R.id.publicAd_Discription);
	 TextView publicAd_Places = itemView.findViewById(R.id.publicAdPlaces);
	 TextView publicAd_phone = itemView.findViewById(R.id.publicAdphone);
	 TextView date_of_publish = itemView.findViewById(R.id.publishDate);
	 TextView adName = itemView.findViewById(R.id.AdName);
	 TextView c_name = itemView.findViewById(R.id.company_name);
     }

     @Override
     protected void onStart() {
	 super.onStart();
	 adapter.startListening();
     }

     @Override
     protected void onStop() {
	 super.onStop();
	 if ( adapter != null )
	      adapter.stopListening();
     }

     @Override
     protected void onRestart() {
	 super.onRestart();
	 AdsFollowingProgress.setVisibility(View.GONE);

     }
}
