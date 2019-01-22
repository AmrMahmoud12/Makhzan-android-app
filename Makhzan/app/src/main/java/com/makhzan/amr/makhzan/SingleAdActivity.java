package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kingfisher.easyviewindicator.RecyclerViewIndicator;
import com.makhzan.amr.makhzan.items.Ad_Images_Item;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SingleAdActivity extends AppCompatActivity {

     private RecyclerView MainAdImagesRV;
     private FirestoreRecyclerAdapter<Ad_Images_Item, Ad_Images_ItemViewHolder> adapter;

     //BUTTONS
     Button clickToShowProfile;
     //___________
     String x,
	a,
	z,
	n,
	p,
	d,
	pl,
	imageUrl,
	CountryName,
	PublisherId,
	UID,
	myName;
     //_________TEXTVIEWS_______________
     TextView Co_Name,
	ad_discription,
	phone_number,
	place_ad,
	followComp,
	unFollowComp;
     ImageView imageView;

     //_________________FIREBASE_________________
     private ListenerRegistration listenerRegistration;
     DocumentReference firebaseFirestoree;
     FirebaseFirestore firebaseFirestore;
     //progressbar
     ProgressBar progressBar;

     RecyclerViewIndicator recyclerViewIndicator;
     private int count;
     InkPageIndicator mIndicator;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_single_ad);
	 progressBar = findViewById(R.id.FProgress);
	 MainAdImagesRV = findViewById(R.id.MainAdImagesRV);
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 //progressBar.setVisibility(View.INVISIBLE);
	 followComp = findViewById(R.id.followComp);
	 unFollowComp = findViewById(R.id.unFollowComp);
	 Co_Name = findViewById(R.id.SingleAd_CoName);
	 ad_discription = findViewById(R.id.disrcibe_ad);
	 phone_number = findViewById(R.id.phone);
	 place_ad = findViewById(R.id.place_ad);
	 imageView = findViewById(R.id.Ad_Picture);
	 clickToShowProfile = findViewById(R.id.ShowProfilePage);
	 //GET INFO FROM MAIN ADS ACTIVITY
	 x = getIntent().getStringExtra("single_ad_position");
	 n = getIntent().getStringExtra("n");
	 a = getIntent().getStringExtra("CategoryName");
	 z = getIntent().getStringExtra("documentId");
	 p = getIntent().getStringExtra("p");
	 d = getIntent().getStringExtra("d");
	 pl = getIntent().getStringExtra("pl");
	 imageUrl = getIntent().getStringExtra("imageUrl");
	 CountryName = getIntent().getStringExtra("CountryName");
	 //MainAdImagesRV.setLayoutManager(new GridLayoutManager(MainAdImagesRV.getContext(),2));
	 MainAdImagesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
	 MainAdImagesRV.setHasFixedSize(true);
	recyclerViewIndicator=findViewById(R.id.circleIndicator);
	//GET COUNT OF DOCUMENTS FROM FIREBASE
	 recyclerViewIndicator.setRecyclerView(MainAdImagesRV);

	// mIndicator.setViewPager(mPager);
	 firebaseFirestore.collection("MainCategories")
	     .document(a)
	     .collection("Countries")
	     .document(CountryName)
	     .collection("Ads")
	     .document(z)
	     .collection("photos")
	     .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

	 MainAdImagesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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




	 //FIREBASE
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 //assert currentUser != null;
	 assert currentUser != null;
	 UID = currentUser.getUid();

	 Query query = firebaseFirestore.collection("MainCategories")
	     .document(a)
	     .collection("Countries")
	     .document(CountryName)
	     .collection("Ads")
	     .document(z)
	     .collection("photos");

	 FirestoreRecyclerOptions<Ad_Images_Item> options = new FirestoreRecyclerOptions.Builder<Ad_Images_Item>()
	     .setQuery(query, Ad_Images_Item.class)
	     .build();


	 adapter = new FirestoreRecyclerAdapter<Ad_Images_Item, Ad_Images_ItemViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull final Ad_Images_ItemViewHolder holder, int position, @NonNull final Ad_Images_Item model) {
		  Picasso.get().load(model.getImageUrl()).placeholder(android.R.drawable.progress_indeterminate_horizontal)
		      .into(holder.image, new Callback() {
		       @Override
		       public void onSuccess() {

		       }

		       @Override
		       public void onError(Exception e) {
		       }
		  });
		  if ( model.getImageUrl()==null &&model.getImageUrl().isEmpty()){

		  }
		  //click to enlarge image
		  holder.image.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			 //  Toast.makeText(SingleAdActivity.this, "sss", Toast.LENGTH_SHORT).show();
			   Intent intent = new Intent(SingleAdActivity.this, ImageActivity.class);

			   intent.putExtra("image_url", model.getImageUrl());
			   startActivity(intent);
		       }
		  });
	      }

	      @NonNull
	      @Override
	      public Ad_Images_ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_adphotos, parent, false);
		  return new Ad_Images_ItemViewHolder(view);
	      }
	 };
	 MainAdImagesRV.setAdapter(adapter);


	 //SET INFO TO ITS VIEWS
	 Co_Name.setText(n);

	 //SET INFO TO ITS VIEWS
	 Co_Name.setText(n);
	 ad_discription.setText(d);
	 phone_number.setText(p);
	 place_ad.setText(pl);

	 firebaseFirestoree = FirebaseFirestore.getInstance()
	     .collection("MainCategories")
	     .document(a)
	     .collection("Countries")
	     .document(CountryName)
	     .collection("Ads")
	     .document(z);
	 firebaseFirestoree.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
		  if ( task.isSuccessful() ) {
		       DocumentSnapshot documentSnapshot = task.getResult();
		       if ( documentSnapshot.exists() ) {
			   //___get publisher of ad id
			   PublisherId = documentSnapshot.getString("puplisherId");
			   isFollowing();
			   progressBar.setVisibility(View.GONE);

		       } else {
		       }
		  } else {

		  }

	      }
	 });

	 getUidName();


	 //____when follow button is clicked
	 followComp.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  progressBar.setVisibility(View.VISIBLE);
		  final Map<String, Object> map = new HashMap<>();
		  //put id in the database as true
		  map.put(PublisherId, true);
		  map.put("followingName", n);
		  map.put("notifications", true);

		  firebaseFirestore.collection("System")
		      .document(UID)
		      .collection("following")
		      .document(PublisherId)
		      .set(map)
		      .addOnSuccessListener(new OnSuccessListener<Void>() {
			  @Override
			  public void onSuccess(Void aVoid) {
			       getUidName();

			       Map<String, Object> map1 = new HashMap<>();
			       map1.put(UID, true);
			       map1.put("followerName", myName);
			       firebaseFirestore.collection("System")
				  .document(PublisherId)
				  .collection("followers")
				  .document(UID)
				  .set(map1)
				  .addOnSuccessListener(new OnSuccessListener<Void>() {
				       @Override
				       public void onSuccess(Void aVoid) {
					   Toast.makeText(SingleAdActivity.this, "تمت المتابعة", Toast.LENGTH_SHORT).show();

					   setFollowing();
					   FirebaseMessaging.getInstance().subscribeToTopic(PublisherId).addOnSuccessListener(new OnSuccessListener<Void>() {
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
	 unFollowComp.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  progressBar.setVisibility(View.VISIBLE);
		  firebaseFirestore
		      .collection("System")
		      .document(UID)
		      .collection("following")
		      .document(PublisherId)
		      .delete()
		      .addOnSuccessListener(new OnSuccessListener<Void>() {
			  @Override
			  public void onSuccess(Void aVoid) {
			       firebaseFirestore
				  .collection("System")
				  .document(PublisherId)
				  .collection("followers")
				  .document(UID)
				  .delete()
				  .addOnSuccessListener(new OnSuccessListener<Void>() {
				       @Override
				       public void onSuccess(Void aVoid) {
					   setUnFollowing();
					   Toast.makeText(SingleAdActivity.this, "تم الغاء المتابعة", Toast.LENGTH_SHORT).show();
					   FirebaseMessaging.getInstance().unsubscribeFromTopic(PublisherId)
					       .addOnSuccessListener(new OnSuccessListener<Void>() {
						   @Override
						   public void onSuccess(Void aVoid) {
						        progressBar.setVisibility(View.INVISIBLE);

						   }
					       });


				       }
				  });
			  }
		      });
	      }
	 });
	 clickToShowProfile.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {

		  Intent intent = new Intent(SingleAdActivity.this, ShowProfileActivity.class);
		  intent.putExtra("PublisherId", PublisherId);
		  intent.putExtra("companyname", n);
		  startActivity(intent);
	      }
	 });
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

     public void getUidName() {
	 firebaseFirestore.collection("Users")
	     .document(UID)
	     .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
	      @Override
	      public void onSuccess(DocumentSnapshot documentSnapshot) {
		  myName = documentSnapshot.getString("companyName");

	      }
	 });
     }

     //IT IS TO CHECK FOLLOWING TO CHANGE BUTTON FROM VIS TO INVIS
     public void isFollowing() {

	 if ( UID.equals(PublisherId) ) {//ceck if they are the same tom prevent app crash

	      followComp.setVisibility(View.GONE);
	      unFollowComp.setVisibility(View.GONE);

	 } else {

	      setUnFollowing();

	      CollectionReference coRef = firebaseFirestore
		 .collection("System")
		 .document(UID)
		 .collection("following");
	      coRef.whereEqualTo(PublisherId, true).
		 get()
		 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
		      @Override
		      public void onComplete(@NonNull Task<QuerySnapshot> task) {
			  if ( task.isSuccessful() ) {
			       for (QueryDocumentSnapshot document : task.getResult()) {

				   setFollowing();

			       }
			  } else {

			  }
		      }
		 });
	 }

     }

     public void setFollowing() {
	 followComp.setVisibility(View.GONE);
	 unFollowComp.setVisibility(View.VISIBLE);
     }

     public void setUnFollowing() {
	 followComp.setVisibility(View.VISIBLE);
	 unFollowComp.setVisibility(View.GONE);
     }


     @Override
     protected void onRestart() {
	 super.onRestart();
	 progressBar.setVisibility(View.GONE);
     }

     private class Ad_Images_ItemViewHolder extends RecyclerView.ViewHolder {
	 public ImageView image;
	 View mView;
	 public Ad_Images_ItemViewHolder(View itemView) {
	      super(itemView);
	      mView = itemView;
	      image = mView.findViewById(R.id.imageId);

	 }

     }

}
