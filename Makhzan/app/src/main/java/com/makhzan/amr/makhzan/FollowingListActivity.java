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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makhzan.amr.makhzan.items.followingItem;

import java.util.HashMap;
import java.util.Map;

public class FollowingListActivity extends AppCompatActivity {
     //FIREBASE
     private FirebaseFirestore firebaseFirestore;
     private FirestoreRecyclerAdapter<followingItem, followingItemViewHolder> adapter;
     CollectionReference mItemsCollection;
     //RECYCLERVIEW
     private RecyclerView followingRV;
     private String UID;

     //PROGRESSBAR
     private ProgressBar FollowingProgress;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_following_list);
	 //PROGRESSBAR
	 FollowingProgress = findViewById(R.id.FollowingProgress);
	 FollowingProgress.setVisibility(View.GONE);

	 //RECYCLER VIEW ITEMS
	 followingRV = findViewById(R.id.followingRV);
	 followingRV.setHasFixedSize(true);
	 followingRV.setLayoutManager(new LinearLayoutManager(this));

	 //___________FIREBASE______________
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 //assert currentUser != null;
	 UID = currentUser.getUid();

	 mItemsCollection = firebaseFirestore
	     .collection("System")
	     .document(UID)
	     .collection("following");

	 Query baseQuery = mItemsCollection
	     .orderBy("followingName", Query.Direction.ASCENDING);

	 FirestoreRecyclerOptions<followingItem> options = new FirestoreRecyclerOptions.Builder<followingItem>()
	     .setQuery(baseQuery, followingItem.class)
	     .build();

	 adapter = new FirestoreRecyclerAdapter<followingItem, followingItemViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull final followingItemViewHolder holder, final int position, @NonNull followingItem model) {
		  holder.name.setText(model.getFollowingName());
		  //_______GET ID OF THE ADVERTISEMENT_________
		  DocumentSnapshot snapshot = getSnapshots()
		      .getSnapshot(holder.getAdapterPosition());
		  String docId = snapshot.getId();
		  mItemsCollection.whereEqualTo("companiesnotifications", false)
		      .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
		       @Override
		       public void onComplete(@NonNull Task<QuerySnapshot> task) {
			   if ( task.isSuccessful() ) {
			        for (QueryDocumentSnapshot document : task.getResult()) {

				    holder.disable_adsNot.setVisibility(View.GONE);
				    holder.enable_adsNot.setVisibility(View.VISIBLE);                             //Log.d(TAG, document.getId() + " => " + document.getData());

			        }
			   } else {
			   }
		       }
		  });
		  FollowingProgress.setVisibility(View.GONE);

		  //FollowingProgress.setVisibility(View.GONE);
		  holder.cancelFollow.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   FollowingProgress.setVisibility(View.VISIBLE);

			   //_______GET ID OF THE ADVERTISEMENT_________
			   DocumentSnapshot snapshot = getSnapshots()
			       .getSnapshot(holder.getAdapterPosition());
			   final String Position = snapshot.getId();
			   FirebaseMessaging.getInstance().unsubscribeFromTopic(Position);

			   removeFollowing(Position);
			   removeFollowerFromTheDeletedCompany(Position);
			   FollowingProgress.setVisibility(View.GONE);

		       }
		  });
		  holder.dailyAdsBtn.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   FollowingProgress.setVisibility(View.VISIBLE);

			   //_______GET ID OF THE ADVERTISEMENT_________
			   DocumentSnapshot snapshot = getSnapshots()
			       .getSnapshot(holder.getAdapterPosition());
			   String Position = snapshot.getId();
			   Intent y = new Intent(FollowingListActivity.this, AdsOfFollowingActivity.class);
			   y.putExtra("CompanyPosition", Position);
			   FollowingProgress.setVisibility(View.GONE);

			   startActivity(y);
			   finish();
		       }
		  });
		  holder.disable_adsNot.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   FollowingProgress.setVisibility(View.VISIBLE);

			   //_______GET ID OF THE ADVERTISEMENT_________
			   DocumentSnapshot snapshot = getSnapshots()
			       .getSnapshot(holder.getAdapterPosition());
			   final String Position = snapshot.getId();
			   Map<String, Object> map = new HashMap<>();
			   map.put("companiesnotifications", false);
			   mItemsCollection.document(Position)
			       .set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
			        @Override
			        public void onSuccess(Void aVoid) {
				    FirebaseMessaging.getInstance().unsubscribeFromTopic(Position);
				    holder.enable_adsNot.setVisibility(View.VISIBLE);
				    holder.disable_adsNot.setVisibility(View.GONE);
				    FollowingProgress.setVisibility(View.GONE);

			        }
			   });

		       }
		  });
		  holder.enable_adsNot.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
			   FollowingProgress.setVisibility(View.VISIBLE);

			   //_______GET ID OF THE ADVERTISEMENT_________
			   DocumentSnapshot snapshot = getSnapshots()
			       .getSnapshot(holder.getAdapterPosition());
			   final String Position = snapshot.getId();
			   Map<String, Object> map = new HashMap<>();
			   map.put("companiesnotifications", true);
			   mItemsCollection.document(Position)
			       .set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
			        @Override
			        public void onSuccess(Void aVoid) {
				    FirebaseMessaging.getInstance().subscribeToTopic(Position);
				    holder.enable_adsNot.setVisibility(View.GONE);
				    holder.disable_adsNot.setVisibility(View.VISIBLE);
				    FollowingProgress.setVisibility(View.GONE);

			        }
			   });

		       }
		  });

	      }

	      @NonNull
	      @Override
	      public followingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_following, parent, false);
		  return new followingItemViewHolder(view);
	      }
	 };
	 followingRV.setAdapter(adapter);

     }

     public void stVisibility(int position) {
	 followingRV
	     .findViewHolderForAdapterPosition(position)
	     .itemView
	     .findViewById(R.id.disable_adsNot)
	     .setVisibility(View.GONE);
	 followingRV
	     .findViewHolderForAdapterPosition(position)
	     .itemView
	     .findViewById(R.id.enable_adsNot)
	     .setVisibility(View.GONE);
     }

     public class followingItemViewHolder extends RecyclerView.ViewHolder {
	 TextView disable_adsNot, enable_adsNot, name, cancelFollow, dailyAdsBtn;

	 public followingItemViewHolder(View itemView) {
	      super(itemView);
	      name = itemView.findViewById(R.id.followingName);
	      cancelFollow = itemView.findViewById(R.id.unfollowBtn);
	      dailyAdsBtn = itemView.findViewById(R.id.dailyAdsBtn);
	      disable_adsNot = itemView.findViewById(R.id.disable_adsNot);
	      enable_adsNot = itemView.findViewById(R.id.enable_adsNot);

	 }



     }

     public void removeFollowing(String Position) {
	 mItemsCollection.document(Position)
	     .delete()
	     .addOnSuccessListener(new OnSuccessListener<Void>() {
		 @Override
		 public void onSuccess(Void aVoid) {
		      Toast.makeText(FollowingListActivity.this, "deleted", Toast.LENGTH_SHORT).show();

		 }
	     });
     }

     public void removeFollowerFromTheDeletedCompany(String Position) {
	 firebaseFirestore.collection("System")
	     .document(Position)
	     .collection("followers")
	     .document(UID)
	     .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
	      @Override
	      public void onSuccess(Void aVoid) {
		  Toast.makeText(FollowingListActivity.this, "deleted", Toast.LENGTH_SHORT).show();

	      }
	 });
     }

     public void isNotified(final int position) {


     }

     public void setNotification(TextView enable, TextView disable) {

	 enable.setVisibility(View.VISIBLE);
	 disable.setVisibility(View.GONE);
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
	 FollowingProgress.setVisibility(View.GONE);
     }
}
