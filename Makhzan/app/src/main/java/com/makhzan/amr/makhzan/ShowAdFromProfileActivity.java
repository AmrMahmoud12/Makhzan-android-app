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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kingfisher.easyviewindicator.RecyclerViewIndicator;
import com.makhzan.amr.makhzan.items.Ad_Images_Item;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ShowAdFromProfileActivity extends AppCompatActivity {
     //___________
     String
	a,
	z,
	n,
	p,
	d,
	pl,
	imageUrl,
	CountryName,
	UID,
	Publisher;

     //_________TEXTVIEWS_______________
     TextView Co_Name,
	ad_discription,
	phone_number,
	place_ad,
	followComp,
	unFollowComp;
     //________IMAGE VIEW_______________

     //________BUTTONS______________
     Button showFollowings;
     //_________________FIREBASE_________________
     private ListenerRegistration listenerRegistration;
     DocumentReference firebaseFirestoree;
     FirebaseFirestore firebaseFirestore;
     //progressbar
     ProgressBar progressBar;
	////

     private RecyclerView profileAdImagesRV;
     private FirestoreRecyclerAdapter<Ad_Images_Item, profile_Images_ItemViewHolder> adapter;
     RecyclerViewIndicator recyclerViewIndicator;
     private int count;

     public ShowAdFromProfileActivity() {
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_show_ad_from_profile);
	 //BUTTONS
	 showFollowings = findViewById(R.id.showFollowings);

	 //GET INFO FROM MAIN ADS ACTIVITY
	 n = getIntent().getStringExtra("n");
	 z = getIntent().getStringExtra("documentId");
	 p = getIntent().getStringExtra("p");
	 d = getIntent().getStringExtra("d");
	 pl = getIntent().getStringExtra("pl");
	 Publisher = getIntent().getStringExtra("Publisher");
	 imageUrl = getIntent().getStringExtra("imageUrl");
	 profileAdImagesRV = findViewById(R.id.profileAdImagesRV);
	 profileAdImagesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
	 profileAdImagesRV.setHasFixedSize(true);

	 recyclerViewIndicator=findViewById(R.id.circleIndicator);
	 recyclerViewIndicator.setRecyclerView(profileAdImagesRV);



	 showFollowings.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  Intent intent = new Intent(ShowAdFromProfileActivity.this, FollowingListActivity.class);
		  startActivity(intent);
		  finish();
	      }
	 });
	 //TEXTVIEWS
	 Co_Name = findViewById(R.id.SingleAd_CoName2);
	 ad_discription = findViewById(R.id.disrcibe_ad2);
	 phone_number = findViewById(R.id.phone2);
	 place_ad = findViewById(R.id.place_ad2);
	 //FIREBASE
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 //assert currentUser != null;
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 firebaseFirestore = FirebaseFirestore.getInstance();

	 //SET INFO TO ITS VIEWS
	 Co_Name.setText(n);
	 ad_discription.setText(d);
	 phone_number.setText(p);
	 place_ad.setText(pl);



	 Query query=FirebaseFirestore.getInstance()
	     .collection("System")
	     .document(Publisher)
	     .collection("ProfileAds")
	     .document(z)
	     .collection("photos");
	 FirestoreRecyclerOptions<Ad_Images_Item> options=new FirestoreRecyclerOptions.Builder<Ad_Images_Item>()
	     .setQuery(query, Ad_Images_Item.class)
	     .build();

	    query .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<QuerySnapshot> task) {
		  if (task.isSuccessful()) {
		       count = 0;
		       for (DocumentSnapshot document : task.getResult()) {
			   count++;
			   recyclerViewIndicator.setItemCount(count);

		       }

		  } else {
		  }
	      }
	 });

	 recyclerViewIndicator.setCurrentPosition(0);

	 profileAdImagesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
	      @Override
	      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		  super.onScrollStateChanged(recyclerView, newState);
		  switch ( newState ) {
		       case RecyclerView.SCROLL_STATE_IDLE:
			   int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
			   recyclerViewIndicator.setCurrentPosition(position);
			   break;
		  }

	      }
	 });

	 adapter=new FirestoreRecyclerAdapter<Ad_Images_Item, profile_Images_ItemViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull profile_Images_ItemViewHolder holder, int position, @NonNull final Ad_Images_Item model) {
		  Picasso.get().load(model.getImageUrl()).placeholder(android.R.drawable.progress_indeterminate_horizontal)
		      .into(holder.imageView, new Callback() {
			  @Override
			  public void onSuccess() {
			       Toast.makeText(ShowAdFromProfileActivity.this, "succeed", Toast.LENGTH_SHORT).show();
			  }

			  @Override
			  public void onError(Exception e) {
			  }
		      });
		  if ( model.getImageUrl()==null &&model.getImageUrl().isEmpty()){

		  }
		  //click to enlarge image
		  holder.imageView.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   Intent intent = new Intent(ShowAdFromProfileActivity.this, ImageActivity.class);

			   intent.putExtra("image_url", model.getImageUrl());
			   startActivity(intent);
		       }
		  });
	      }

	      @NonNull
	      @Override
	      public profile_Images_ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_adphotos, parent, false);
		  return new profile_Images_ItemViewHolder(view);
	      }
	 };
	 profileAdImagesRV.setAdapter(adapter);


     }
     private class  profile_Images_ItemViewHolder extends RecyclerView.ViewHolder{
	public  ImageView imageView ;
	View mView;
	public profile_Images_ItemViewHolder(View itemView) {
	      super(itemView);
	      mView=itemView;
	      imageView=mView.findViewById(R.id.imageId);
	 }
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
}
