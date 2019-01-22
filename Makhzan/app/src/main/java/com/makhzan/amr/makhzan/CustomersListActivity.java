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
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makhzan.amr.makhzan.items.CustomerListItem;

import java.util.ArrayList;
import java.util.List;

public class CustomersListActivity extends AppCompatActivity {
     private boolean isScrolling = false;
     private boolean isLastItemReached = false;
     private DocumentSnapshot lastVisible;
     private int limit = 5;
     private RecyclerView customersListRV;
     private FirebaseFirestore firebaseFirestore;
     private String user;
	private ProgressBar Progress;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_customerslist);
	 Progress=findViewById(R.id.Progress);
	 //FIREBASE
	  firebaseFirestore = FirebaseFirestore.getInstance();
	  user= FirebaseAuth.getInstance().getCurrentUser().getUid();

	 final CollectionReference productsRef = firebaseFirestore.collection("System")
	     .document(user).collection("followingCustomers");
	 Query firstQuery = productsRef.orderBy("followingName", Query.Direction.ASCENDING).limit(limit);
	 customersListRV = findViewById(R.id.customersListRV);
	 customersListRV.setHasFixedSize(true);
	 final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
	 customersListRV.setLayoutManager(linearLayoutManager);

	 firstQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
	      @Override
	      public void onComplete(@NonNull final Task<QuerySnapshot> task) {
		  if (task.isSuccessful()) {
		       final List<CustomerListItem> list = new ArrayList<>();
				if ( task.getResult().size() <= 0 ){
				     Toast.makeText(CustomersListActivity.this, "no items", Toast.LENGTH_SHORT).show();
					Progress.setVisibility(View.GONE);
				}else {

				     for (DocumentSnapshot document : task.getResult()) {

					 CustomerListItem productModel = document.toObject(CustomerListItem.class);
					 list.add(productModel);
				   			  }
				     final ProductAdapter productAdapter = new ProductAdapter(list);
				     customersListRV.setAdapter(productAdapter);
				     Progress.setVisibility(View.GONE);

				     lastVisible = task.getResult().getDocuments().get(task.getResult().size()-1);

				     RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
					 @Override
					 public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					      super.onScrollStateChanged(recyclerView, newState);
					      if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
						  isScrolling = true;
					      }
					 }

					 @Override
					 public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					      super.onScrolled(recyclerView, dx, dy);

					      int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
					      int visibleItemCount = linearLayoutManager.getChildCount();
					      int totalItemCount = linearLayoutManager.getItemCount();

					      if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
						  isScrolling = false;
						  Query nextQuery = productsRef.orderBy("customerName", Query.Direction.ASCENDING)
						      .startAfter(lastVisible).limit(limit);
						  nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						       @Override
						       public void onComplete(@NonNull Task<QuerySnapshot> t) {
							   if (t.isSuccessful()) {
							        for (DocumentSnapshot d : t.getResult()) {
								    CustomerListItem productModel = d.toObject(CustomerListItem.class);
								    list.add(productModel);
							        }
							        productAdapter.notifyDataSetChanged();

							        for (int i=0;i<task.getResult().size();i++) {
								    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
							        }						 Toast.makeText(CustomersListActivity.this, "next page loaded", Toast.LENGTH_SHORT).show();
							        if (t.getResult().size() < limit) {
								    isLastItemReached = true;
							        }
							   }
						       }
						  });
					      }
					 }
				     };
				     customersListRV.addOnScrollListener(onScrollListener);

				}

		       }


	      }
	 });


     }


     private class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
	 private List<CustomerListItem> list;

	 ProductAdapter(List<CustomerListItem> list) {
	      this.list = list;

	 }

	 @NonNull
	 @Override
	 public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_customer_item, parent, false);
	      return new ProductViewHolder(view);

	 }

	 @Override
	 public void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position) {
	      final String productname = list.get(position).getFollowingName();
	      holder.setCustomerName(productname);

	      holder.view.setOnClickListener(new View.OnClickListener() {
		     @Override
		     public void onClick(View v) {
		          Progress.setVisibility(View.VISIBLE);
			 final String SS = String.valueOf(holder.getAdapterPosition());

			 DocumentReference refr = firebaseFirestore
			     .collection("System").document(user).collection("followingCustomers")
			     .document(String.valueOf(SS));
			 refr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			      @Override
			      public void onSuccess(DocumentSnapshot documentSnapshot) {
				  // GET NAME FROM CATEGORY_NAME WHICH IS THE SAME OF
				  //THE ID OF DOCUMENT
				  String uid = list.get(position).getUid();
				Progress.setVisibility(View.GONE);
				  //String uid = documentSnapshot.getString("uid");
				  Intent i = new Intent(CustomersListActivity.this, CustomerOrdersActivity.class);
				 i.putExtra("uid",uid);
				  startActivity(i);
				 // Toast.makeText(CustomersListActivity.this, uid, Toast.LENGTH_SHORT).show();

			      }
			 });
		     }
		});
		holder.deleteCustomer.setOnClickListener(new View.OnClickListener() {
		     @Override
		     public void onClick(View v) {
			 final String SS = String.valueOf(holder.getAdapterPosition());
			 DocumentReference refr = firebaseFirestore
			     .collection("System").document(user).collection("followingCustomers")
			     .document(String.valueOf(SS));
			 refr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			      @Override
			      public void onSuccess(DocumentSnapshot documentSnapshot) {
				  // GET NAME FROM CATEGORY_NAME WHICH IS THE SAME OF
				  //THE ID OF DOCUMENT
				  String uid = list.get(position).getUid();
				  FirebaseMessaging.getInstance().unsubscribeFromTopic(uid);
				    Progress.setVisibility(View.GONE);
				   // Toast.makeText(CustomersListActivity.this, uid, Toast.LENGTH_SHORT).show();

			      }
			 });
		     }
		});

	 }

	 @Override
	 public int getItemCount() {
	      return list.size();
	 }
     }

     private class ProductViewHolder extends RecyclerView.ViewHolder {
	 public View view;

	 public ProductViewHolder(View itemView) {
	      super(itemView);
	      view = itemView;

	 }
	 TextView  deleteCustomer=itemView.findViewById(R.id.deleteCustomer);

	 void setCustomerName(String customerName){
	      TextView textView=view.findViewById(R.id.CustomerName);
	      textView.setText(customerName);
	 }
     }

     @Override
     protected void onStart() {
	 super.onStart();

     }

     //     private class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
//	 private List<ProductModel> list;
//
//	 ProductAdapter(List<ProductModel> list) {
//	      this.list = list;
//	 }
//
//	 @NonNull
//	 @Override
//	 public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//	      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
//	      return new ProductViewHolder(view);
//	 }
//
//	 @Override
//	 public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
//	      String productName = list.get(position).getProductName();
//	      productViewHolder.setProductName(productName);
//	 }
//
//	 @Override
//	 public int getItemCount() {
//	      return list.size();
//	 }
//     }
}
