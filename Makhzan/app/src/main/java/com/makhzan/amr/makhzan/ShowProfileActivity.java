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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.makhzan.amr.makhzan.items.MyAdCard;
import com.makhzan.amr.makhzan.items.Upload;

public class ShowProfileActivity extends AppCompatActivity {
     //_____FOLLOW AND UNFOLLOW TXTVIEW______
     private TextView
	follow,
	unfollow,
	comp_name,
	comp_address;
     //     //_________FIREBASE____________________
//     private FirebaseFirestore firebaseFirestore;
     //________
     String
	UID,
	PublisherId,
	CompanyName,
	myName;
     //___progress bar
     ProgressBar ShowProfileActivityProgress;


     //FIREBASE
     private FirebaseFirestore firebaseFirestore;
     private FirestoreRecyclerAdapter<MyAdCard, ShowProfileActivityViewHolder> adapter;
     CollectionReference mItemsCollection;
     //RECYCLERVIEW
     private RecyclerView profileAds;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_show_profile);
	 ShowProfileActivityProgress=findViewById(R.id.ShowProfileActivityProgress);
	 //INTENTS FROM SINGLE AD ACTIVIIY
	 PublisherId = getIntent().getStringExtra("PublisherId");
	 CompanyName = getIntent().getStringExtra("companyname");
	 //RECYCLER VIEW ITEMS
	 profileAds = findViewById(R.id.profileAds);
	 profileAds.setHasFixedSize(true);
	 profileAds.setLayoutManager(new LinearLayoutManager(this));

	 //___________FIREBASE______________
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 //assert currentUser != null;
	 UID = currentUser.getUid();

	 mItemsCollection = firebaseFirestore
	     .collection("System")
	     .document(PublisherId)
	     .collection("ProfileAds");

	 Query baseQuery = mItemsCollection
	     .orderBy("creationDate", Query.Direction.DESCENDING);

	 FirestoreRecyclerOptions<MyAdCard> options = new FirestoreRecyclerOptions.Builder<MyAdCard>()
	     .setQuery(baseQuery, MyAdCard.class)
	     .build();

	 adapter = new FirestoreRecyclerAdapter<MyAdCard, ShowProfileActivityViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull final ShowProfileActivityViewHolder holder, int position, @NonNull MyAdCard model) {
		  holder.publicAd_discription.setText(model.getMyAddiscriptiontxt());
		  holder.publicAd_Places.setText(model.getMyPlacestxt());
		  holder.publicAd_phone.setText(String.valueOf(model.getPhoneNumbera()));
		  holder.date_of_publish.setText(model.getCreationDate());
		  holder.adName.setText(model.getMyAdNametxt());
		  holder.c_name.setText(model.getCompanyName());
		  ShowProfileActivityProgress.setVisibility(View.GONE);
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
			   String documentId = snapshot.getId();
			   Intent i = new Intent(ShowProfileActivity.this, ShowAdFromProfileActivity.class);
			   i.putExtra("documentId", documentId);
			   i.putExtra("n", n);
			   i.putExtra("d", d);
			   i.putExtra("p", p);
			   i.putExtra("pl", pl);
			   i.putExtra("Publisher",PublisherId);

			   startActivity(i);
		       }
		  });
	      }

	      @NonNull
	      @Override
	      public ShowProfileActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_categoryads, parent, false);
		  return new ShowProfileActivityViewHolder(view);

	      }
	 };
	 profileAds.setAdapter(adapter);
     }

     public class ShowProfileActivityViewHolder extends RecyclerView.ViewHolder {


	 public ShowProfileActivityViewHolder(View itemView) {
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
	 ShowProfileActivityProgress.setVisibility(View.GONE);
     }

}
//	 //___________FIREBASE______________
//	 firebaseFirestore = FirebaseFirestore.getInstance();
//	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//	 //assert currentUser != null;
//	 UID = currentUser.getUid();
//	 //INTENTS FROM SINGLE AD ACTIVIIY
//	 PublisherId = getIntent().getStringExtra("PublisherId");
//	 CompanyName = getIntent().getStringExtra("companyname");
//	 //TEXT VIEWA
//	 comp_name = findViewById(R.id.Comp_name);
//	 comp_address = findViewById(R.id.Comp_address);
//	 follow = findViewById(R.id.follow);
//	 unfollow = findViewById(R.id.unfollow);
//	 //PROGRESS BAR
//	 progressBar = findViewById(R.id.FollowProgress);
//	 progressBar.setVisibility(View.INVISIBLE);
//	 //____________SET COMPANY INFORMATION________
//	 comp_name.setText(CompanyName);
//
//	 isFollowing();
//	 getUidName();
//
//	 //____when follow button is clicked
//	 follow.setOnClickListener(new View.OnClickListener() {
//	      @Override
//	      public void onClick(View v) {
//		  progressBar.setVisibility(View.VISIBLE);
//		  final Map<String, Object> map = new HashMap<>();
//		  //put id in the database as true
//		  map.put(PublisherId, true);
//		  map.put("followingName", CompanyName);
//
//		  firebaseFirestore.collection("System")
//		      .document(UID)
//		      .collection("following")
//		      .document(PublisherId)
//		      .set(map)
//		      .addOnSuccessListener(new OnSuccessListener<Void>() {
//			  @Override
//			  public void onSuccess(Void aVoid) {
//			       Map<String, Object> map1 = new HashMap<>();
//			       map1.put(UID, true);
//			       map1.put("followerName", myName);
//			       firebaseFirestore.collection("System")
//				  .document(PublisherId)
//				  .collection("followers")
//				  .document(UID)
//				  .set(map1)
//				  .addOnSuccessListener(new OnSuccessListener<Void>() {
//				       @Override
//				       public void onSuccess(Void aVoid) {
//					   Toast.makeText(ShowProfileActivity.this, "تمت المتابعة", Toast.LENGTH_SHORT).show();
//					   setFollowing();
//					   progressBar.setVisibility(View.GONE);
//				       }
//				  });
//			  }
//		      });
//
//	      }
//	 });
//
//	 unfollow.setOnClickListener(new View.OnClickListener() {
//	      @Override
//	      public void onClick(View v) {
//		  progressBar.setVisibility(View.VISIBLE);
//		  firebaseFirestore
//		      .collection("System")
//		      .document(UID)
//		      .collection("following")
//		      .document(PublisherId)
//		      .delete()
//		      .addOnSuccessListener(new OnSuccessListener<Void>() {
//			  @Override
//			  public void onSuccess(Void aVoid) {
//			       firebaseFirestore
//				  .collection("System")
//				  .document(PublisherId)
//				  .collection("followers")
//				  .document(UID)
//				  .delete()
//				  .addOnSuccessListener(new OnSuccessListener<Void>() {
//				       @Override
//				       public void onSuccess(Void aVoid) {
//					   setUnFollowing();
//					   Toast.makeText(ShowProfileActivity.this, "تم الغاء المتابعة", Toast.LENGTH_SHORT).show();
//					   progressBar.setVisibility(View.INVISIBLE);
//
//				       }
//				  });
//			  }
//		      });
//	      }
//	 });
//
//	 //to check if the current user is following the publisher id or not
//     }
//
//     public void getUidName() {
//	 firebaseFirestore.collection("Users").document(UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//	      @Override
//	      public void onSuccess(DocumentSnapshot documentSnapshot) {
//		  myName = documentSnapshot.getString("companyName");
//
//	      }
//	 });
//     }
//
//     //IT IS TO CHECK FOLLOWING TO CHANGE BUTTON FROM VIS TO INVIS
//     public void isFollowing() {
//
//	 if ( UID.equals(PublisherId) ) {//ceck if they are the same tom prevent app crash
//
//	      follow.setVisibility(View.GONE);
//	      unfollow.setVisibility(View.GONE);
//
//	 } else {
//
//	      setUnFollowing();
//
//	      CollectionReference coRef = firebaseFirestore
//		 .collection("System")
//		 .document(UID)
//		 .collection("following");
//	      coRef.whereEqualTo(PublisherId, true).
//		 get()
//		 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//		      @Override
//		      public void onComplete(@NonNull Task<QuerySnapshot> task) {
//			  if ( task.isSuccessful() ) {
//			       for (QueryDocumentSnapshot document : task.getResult()) {
//
//				   Toast.makeText(ShowProfileActivity.this, "succeeded", Toast.LENGTH_SHORT).show();
//				   //Log.d(TAG, document.getId() + " => " + document.getData());
//				   setFollowing();
//
//			       }
//			  } else {
//			       Toast.makeText(ShowProfileActivity.this, "awwds", Toast.LENGTH_SHORT).show();
//			       // Log.d(TAG, "Error getting documents: ", task.getException());
//			  }
//		      }
//		 });
//	 }
//
//     }
//
//     public void setFollowing() {
//	 follow.setVisibility(View.GONE);
//	 unfollow.setVisibility(View.VISIBLE);
//     }
//
//     public void setUnFollowing() {
//	 follow.setVisibility(View.VISIBLE);
//	 unfollow.setVisibility(View.GONE);
//     }
//
//}
