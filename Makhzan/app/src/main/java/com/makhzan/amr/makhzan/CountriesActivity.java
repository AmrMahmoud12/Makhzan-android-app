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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.makhzan.amr.makhzan.items.CountryItem;

public class CountriesActivity extends AppCompatActivity {

     //____FIREBAS_______
     private FirebaseFirestore firebaseFirestore;
     //___________RV________
     private RecyclerView CountriesRV;
     private FirestoreRecyclerAdapter<CountryItem, CountriesViewHolder> adapter;
     private FirebaseFirestore Ref;
     //___________
     private ProgressBar CountriesProgress;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_countries);

	 //______PROGRESS BAR____
	 CountriesProgress=findViewById(R.id.CountriesProgress);
	 //___FIREBASE__________________
	 FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentuser != null;
	 String UID = currentuser.getUid();
	 Ref = FirebaseFirestore.getInstance();
	 CountriesRV = findViewById(R.id.CountriesRV);
	 CountriesRV.setLayoutManager(new LinearLayoutManager(this));
	 CountriesRV.setHasFixedSize(true);

          //______from category names____________
	 final String CategoryPosition = getIntent()
	     .getExtras()
	     .get("CategoryName")
	     .toString();
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 Query query = firebaseFirestore.collection("MainCategories")
	     .document(CategoryPosition)
	     .collection("Countries");

	 FirestoreRecyclerOptions<CountryItem> options = new FirestoreRecyclerOptions.Builder<CountryItem>()
	     .setQuery(query, CountryItem.class)
	     .build();

	 adapter = new FirestoreRecyclerAdapter<CountryItem, CountriesViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull CountriesViewHolder holder, int position, @NonNull CountryItem model) {
		  holder.countryName.setText(model.getCountryName());
		  CountriesProgress.setVisibility(View.GONE);
		  //_______GET ID OF THE ADVERTISEMENT_________
		  DocumentSnapshot snapshot = getSnapshots()
		      .getSnapshot(holder.getAdapterPosition());
		  final String countryName = snapshot.getId();

		  holder.itemView.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
		            CountriesProgress.setVisibility(View.VISIBLE);
			   Intent i=new Intent(CountriesActivity.this,MainAdsActivity.class);
			   i.putExtra("CategoryPosition",CategoryPosition);
			   i.putExtra("countryName",countryName);
			   startActivity(i);
			   CountriesProgress.setVisibility(View.GONE);

		       }
		  });
	      }

	      @NonNull
	      @Override
	      public CountriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_country, parent, false);
		  return new CountriesViewHolder(view);
	      }
	 };
	 CountriesRV.setAdapter(adapter);


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


     private class CountriesViewHolder extends RecyclerView.ViewHolder {
	 public CountriesViewHolder(View itemView) {
	      super(itemView);
	 }

	 TextView countryName = itemView.findViewById(R.id.CountryName);
     }

     @Override
     protected void onRestart() {
	 super.onRestart();
	 CountriesProgress.setVisibility(View.GONE);
     }
}
