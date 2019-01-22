package com.makhzan.amr.makhzan.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.makhzan.amr.makhzan.R;
import com.makhzan.amr.makhzan.items.OrderItem;


public class MyOrdersAdapter extends FirestoreRecyclerAdapter<OrderItem, MyOrdersAdapter.MyOrdersHolder> {

     /**
      * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
      * FirestoreRecyclerOptions} for configuration options.
      *
      * @param options
      */
     public MyOrdersAdapter(@NonNull FirestoreRecyclerOptions<OrderItem> options) {
	 super(options);
     }

     @Override
     protected void onBindViewHolder(@NonNull MyOrdersHolder holder, int position, @NonNull OrderItem model) {
		holder.orderTitle2.setText(model.getOrderTitle());
		holder.orderPhone2.setText(String.valueOf(model.getNumber()));
		holder.orderDate2.setText(model.getOrderDate());

     }

     @NonNull
     @Override
     public MyOrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	 View view = LayoutInflater
	     .from(parent.getContext())
	     .inflate(R.layout.cardview_myorders, parent, false);
	 return new MyOrdersHolder(view);
     }

      class MyOrdersHolder extends RecyclerView.ViewHolder {

	 TextView orderTitle2,
	     	 orderDate2,
	     	 orderPhone2;
	 ImageView edit_order,delete_order;

	 public MyOrdersHolder(View itemView) {
	      super(itemView);
	      orderTitle2 = itemView.findViewById(R.id.orderTitle2);
	      orderDate2 = itemView.findViewById(R.id.orderDate2);
	      orderPhone2 = itemView.findViewById(R.id.orderPhone2);

	 }

     }
}
