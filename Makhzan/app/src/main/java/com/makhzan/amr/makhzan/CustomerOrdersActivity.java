package com.makhzan.amr.makhzan;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makhzan.amr.makhzan.items.CustomerListItem;
import com.makhzan.amr.makhzan.items.OrderItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrdersActivity extends AppCompatActivity {
     private boolean isScrolling = false;
     private boolean isLastItemReached = false;
     private DocumentSnapshot lastVisible;
     private int limit = 5;
     private RecyclerView CustomerOrdersRV;
     private FirebaseFirestore firebaseFirestore;
     private String user;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_customer_orders);
	// TextView textView=findViewById(R.id.id);
	 String id=getIntent().getStringExtra("uid");
	 //textView.setText(id);

	 //FIREBASE
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 user= FirebaseAuth.getInstance().getCurrentUser().getUid();
	 final CollectionReference Ref = firebaseFirestore.collection("System")
	     .document(id).collection("ProfileOrders");
	 Query firstQuery = Ref.orderBy("orderDate", Query.Direction.DESCENDING).limit(limit);

	 CustomerOrdersRV = findViewById(R.id.CustomerOrdersRV);
	 CustomerOrdersRV.setHasFixedSize(true);
	 final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
	 CustomerOrdersRV.setLayoutManager(linearLayoutManager);


	 firstQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
	      @Override
	      public void onComplete(@NonNull final Task<QuerySnapshot> task) {
		  if (task.isSuccessful()) {
		       final List<OrderItem> list = new ArrayList<>();
		       if ( task.getResult().size() <= 0 ){
			   Toast.makeText(CustomerOrdersActivity.this, "no items", Toast.LENGTH_SHORT).show();
		       }else {

			   for (DocumentSnapshot document : task.getResult()) {

			        OrderItem productModel = document.toObject(OrderItem.class);
			        list.add(productModel);
			   }
			    final CustomerOrdersActivity.CustomerOrdersAdapter Adapter = new CustomerOrdersActivity.CustomerOrdersAdapter(list);
			   CustomerOrdersRV.setAdapter(Adapter);
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
					Query nextQuery = Ref.orderBy("orderDate", Query.Direction.DESCENDING)
					    .startAfter(lastVisible).limit(limit);
					nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					     @Override
					     public void onComplete(@NonNull Task<QuerySnapshot> t) {
						 if (t.isSuccessful()) {
						      for (DocumentSnapshot d : t.getResult()) {
							  OrderItem productModel = d.toObject(OrderItem.class);
							  list.add(productModel);
						      }
						      Adapter.notifyDataSetChanged();

						      for (int i=0;i<task.getResult().size();i++) {
							  lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
						      }
						      Toast.makeText(CustomerOrdersActivity.this, "next page loaded", Toast.LENGTH_SHORT).show();
						      if (t.getResult().size() < limit) {
							  isLastItemReached = true;
						      }
						 }
					     }
					});
				    }
			        }
			   };
			   CustomerOrdersRV.addOnScrollListener(onScrollListener);

		       }

		  }


	      }
	 });
     }



     public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersViewHolder>{
	 private List<OrderItem> list;
CustomerOrdersAdapter(List<OrderItem> list){
     this.list=list;
}
	 @NonNull
	 @Override
	 public CustomerOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_customerorders, parent, false);
	      return new CustomerOrdersViewHolder(view);
}

	 @Override
	 public void onBindViewHolder(@NonNull CustomerOrdersViewHolder holder, int position) {
		holder.setOrderTitle(list.get(position).getOrderTitle());
		holder.setOrderDate(list.get(position).getOrderDate());
		holder.setNumber(list.get(position).getNumber());
	 }

	 @Override
	 public int getItemCount() {
	      return list.size();
	 }
     }

     private class CustomerOrdersViewHolder extends RecyclerView.ViewHolder {
	 private View view;

          public CustomerOrdersViewHolder(View itemView) {
	      super(itemView);
	      this.view=itemView;
	 }
	 public void setNumber(long number) {
               TextView num=view.findViewById(R.id.orderPhone3);
               num.setText(String.valueOf(number));
	 }
	 public void setOrderDate(String orderDate) {
               TextView date=view.findViewById(R.id.orderDate3);
	 date.setText(orderDate);
          }
	 public void setOrderTitle(String orderTitle) {
	      TextView title=view.findViewById(R.id.orderTitle3);
	      title.setText(orderTitle);

          }
     }
}
