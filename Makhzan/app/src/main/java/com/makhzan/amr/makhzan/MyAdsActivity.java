package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.makhzan.amr.makhzan.items.MyAdCard;

public class MyAdsActivity extends AppCompatActivity {
     private static final String N = "EDIT";
     //___________FIREBASE RECYCLERVIEW______________
     FirestoreRecyclerAdapter<MyAdCard, MyAdsViewHolder> Adapter;
     FirebaseFirestore firebaseFirestore;
     private RecyclerView myAdsRV;
     String x;

     //_____________ImageButoons______
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_show_ads);


	 FirebaseUser currentUSER = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUSER != null;
	 x = currentUSER.getUid();

	 myAdsRV = findViewById(R.id.Show_myAdsRV);
	 myAdsRV.setHasFixedSize(true);
	 myAdsRV.setLayoutManager(new LinearLayoutManager(this));
//	 myAdsRV.addItemDecoration(new DividerItemDecoration(this,
//	     DividerItemDecoration.HORIZONTAL));
	 // DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
	 // itemDecorator.setDrawable(ContextCompat.getDrawable(this,));
	 firebaseFirestore = FirebaseFirestore.getInstance();

//________//_______EDIT AD CLICK LISTENER_________


	 Toast.makeText(MyAdsActivity.this, "قائمة الاعلانات", Toast.LENGTH_SHORT).show();
	 Query query = firebaseFirestore.collection("Users")
	     .document(x).collection("MyAds")
	     .orderBy("creationDate", Query.Direction.DESCENDING);

	 FirestoreRecyclerOptions<MyAdCard> options = new FirestoreRecyclerOptions
	     .Builder<MyAdCard>()
	     .setQuery(
		query, MyAdCard.class
	     ).build();

	 Adapter = new FirestoreRecyclerAdapter<MyAdCard, MyAdsViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull final MyAdsViewHolder holder, int position, @NonNull MyAdCard model) {
		  holder.MyAdName_txt.setText(model.getMyAdNametxt());
		  holder.MyAddiscription_txt.setText(model.getMyAddiscriptiontxt());
		  holder.MyPlaces_txt.setText(model.getMyPlacestxt());
		  holder.MyAdphonev.setText(String.valueOf(model.getPhoneNumbera()));
		  holder.MyCreationDate.setText(model.getCreationDate());
		  holder.edit_ad.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   //_______GET ID OF THE ADVERTISEMENT_________
			   DocumentSnapshot snapshot = getSnapshots()
			       .getSnapshot(holder.getAdapterPosition());
			   final String AdPosition_myAd = snapshot.getId();
			Intent e=new Intent(MyAdsActivity.this,EditAdActivity.class);
			String ad_Name=holder.MyAdName_txt.getText().toString();
			   String ad_description=holder.MyAddiscription_txt.getText().toString();
				String ad_date=holder.MyCreationDate.getText().toString();
			   e.putExtra("adname",ad_Name);
			   e.putExtra("addescription",ad_description);
			   e.putExtra("currentdate",ad_date);
			   e.putExtra("AdPosition_myAd",AdPosition_myAd);
			   startActivity(e);
		       }
		  });
//
//			   final MyAdCard q = new MyAdCard();
//			//__________FETCH DATA FROM MYAD POSITION AND PUSH IT INTO MAIN ADS COLLECTION_______

	      }

	      @NonNull
	      @Override
	      public MyAdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view = LayoutInflater.from(parent.getContext())
		      .inflate(R.layout.cardview_myads, parent, false);
		  return new MyAdsViewHolder(view);

	      }
	 };


	 myAdsRV.setAdapter(Adapter);


     }

     public class MyAdsViewHolder extends RecyclerView.ViewHolder {

	 public MyAdsViewHolder(View itemView) {
	      super(itemView);
	 }


	 TextView MyAddiscription_txt = itemView.findViewById(R.id.MyAdDiscription);
	 TextView MyPlaces_txt = itemView.findViewById(R.id.MyAdPlaces);
	 TextView MyAdphonev = itemView.findViewById(R.id.MyAdphone);
	 TextView MyCreationDate = itemView.findViewById(R.id.creationDate);
	 TextView MyAdName_txt = itemView.findViewById(R.id.MyAdName);
	 ImageButton edit_ad = itemView.findViewById(R.id.edit_ad);

     }

     @Override
     protected void onStop() {
	 super.onStop();
	 if ( Adapter != null ) {
	      Adapter.stopListening();
	 }
     }

     @Override
     protected void onStart() {
	 super.onStart();

	 Adapter.startListening();

     }
}
