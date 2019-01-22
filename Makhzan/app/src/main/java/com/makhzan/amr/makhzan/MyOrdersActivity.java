package com.makhzan.amr.makhzan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.makhzan.amr.makhzan.Adapter.MyOrdersAdapter;
import com.makhzan.amr.makhzan.items.OrderItem;

public class MyOrdersActivity extends AppCompatActivity {
	//FIREBASE
     private String UID;
	FirebaseFirestore firebaseFirestore;
     private MyOrdersAdapter myOrdersAdapter;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_my_orders);


	 //FIREBASE
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 firebaseFirestore=FirebaseFirestore.getInstance();
	 CollectionReference collectionReference=firebaseFirestore
	     .collection("Users")
	     .document(UID)
	     .collection("MyOrders");
	 //rv
	 RecyclerView MyOrdersRV = findViewById(R.id.MyOrdersRV);
	 MyOrdersRV.setLayoutManager(new LinearLayoutManager((this)));
	 MyOrdersRV.setHasFixedSize(true);

	 Query query = collectionReference.orderBy("orderDate", Query.Direction.DESCENDING);
	 FirestoreRecyclerOptions<OrderItem> options = new FirestoreRecyclerOptions.Builder<OrderItem>()
	     .setQuery(query, OrderItem.class)
	     .build();
	 myOrdersAdapter = new MyOrdersAdapter(options);


	 MyOrdersRV.setAdapter(myOrdersAdapter);
     }

     @Override
     protected void onStop() {
	 super.onStop();
	 myOrdersAdapter.stopListening();
     }

     @Override
     public void onStart() {
	 super.onStart();
	 myOrdersAdapter.startListening();
     }
}
