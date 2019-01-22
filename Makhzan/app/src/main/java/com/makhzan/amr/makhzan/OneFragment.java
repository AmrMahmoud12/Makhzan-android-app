package com.makhzan.amr.makhzan;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.makhzan.amr.makhzan.items.mainCategroies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment {
     //______FIREBASE___________
     private String UID;
     Boolean x;
     private ListenerRegistration listenerRegistration;
     private FirebaseFirestore firebaseFirestore;
     private GoogleSignInClient mGoogleSignInClient;


     //______NAVIGATION ______
     NavigationView navigationView;
     private TextView CompanyEmail, companyName;

     //________________RV_____
     private RecyclerView MainCategoriesRV;
     private FirestoreRecyclerAdapter<mainCategroies, AllCategoriesViewHolder> Adapter;
     //String categortName;
     //__________progress dialog__________
     private ProgressBar MainActivityProgress;
     private DocumentSnapshot lastVisible;
     private boolean isScrolling;
     private boolean isLastItemReached;
     private int limit = 20;
     //____________ViewFlipper____________________
     ViewFlipper imgBanner;


     public OneFragment() {
	 // Required empty public constructor
     }


     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
			   Bundle savedInstanceState) {
	 // Inflate the layout for this fragment
	 View view=inflater.inflate(R.layout.fragment_one, container, false);


	 //___FIREBASE__________________
	 FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentuser != null;
	 UID = currentuser.getUid();
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 //________RV________
	 MainCategoriesRV = view.findViewById(R.id.MainCategoriesRV);
	 MainCategoriesRV.setHasFixedSize(true);
	// final GridLayoutManager linearLayoutManager=new GridLayoutManager(MainCategoriesRV.getContext(),2);
	// final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
	// MainCategoriesRV.setLayoutManager(new GridLayoutManager(MainCategoriesRV.getContext(),2));
	 MainCategoriesRV.setLayoutManager(new GridLayoutManager(MainCategoriesRV.getContext(),2));
	  //ViewFlipper and banner slider_____________
	 imgBanner = view.findViewById(R.id.imgBanner);

	 //______PROGRESS BAR__________
	 MainActivityProgress = view.findViewById(R.id.MainActivityProgress);

	 int slider[] = {
	     R.drawable.circle,
	     R.drawable.adnameicon,
	     R.drawable.ad_icon};
	 for (int slide : slider) {
	      bannerFlipper(slide);
	 }
	 final CollectionReference ref = firebaseFirestore
	     .collection("MainCategories");

	 Query firstQuery = ref
	     .orderBy("Category_Name", Query.Direction.ASCENDING).limit(20);

	 firstQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<QuerySnapshot> task) {
		  if ( task.isSuccessful() ) {
		       final List<mainCategroies> list = new ArrayList<>();
		       for (DocumentSnapshot document : task.getResult()) {
			   mainCategroies maincategroies = document.toObject(mainCategroies.class);
			   // assert maincategroies != null;
			   // maincategroies.setId(document.getId());

			   list.add(maincategroies);
		       }
		       final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(list);
		       MainCategoriesRV.setAdapter(categoriesAdapter);

		       lastVisible = task.getResult().getDocuments().get(task.getResult().size() -2);

		       RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
			   @Override
			   public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			        super.onScrollStateChanged(recyclerView, newState);
			        if ( newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ) {
				    isScrolling = true;
			        }
			   }

			   @Override
			   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			        super.onScrolled(recyclerView, dx, dy);
			        GridLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),2);
			        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
			        int visibleItemCount = linearLayoutManager.getChildCount();
			        int totalItemCount = linearLayoutManager.getItemCount();
			        if ( isScrolling && (firstVisibleItemPosition+visibleItemCount>= totalItemCount) && !isLastItemReached ) {
				    isScrolling = false;
				    Query nextQuery = ref.orderBy("Category_Name", Query.Direction.ASCENDING)
				        .startAfter(lastVisible)
				        .limit(limit);
				    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
					     for (DocumentSnapshot d : task.getResult()) {
						 mainCategroies maincategroies = d.toObject(mainCategroies.class);
						 list.add(maincategroies);
					     }
					     categoriesAdapter.notifyDataSetChanged();
					     for (int i = 0; i < task.getResult().size(); i++) {
						 lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 2);
					     }

					     Toast.makeText(getContext(), "please wait,... ", Toast.LENGTH_SHORT).show();
					     if ( task.getResult().size() < limit ) {
						 isLastItemReached = true;

					     }
					}
				    });

			        }

			   }
		       };

		       MainCategoriesRV.addOnScrollListener(onScrollListener);

		  }
	      }
	 });

	 //QUERY RECYCLER VIEW
	/* Query ref = firebaseFirestore
	     .collection("MainCategories");
	 FirestoreRecyclerOptions<mainCategroies> options=new FirestoreRecyclerOptions.Builder<mainCategroies>()
	     .setQuery(ref,mainCategroies.class)
	     .build();

	 Adapter=new FirestoreRecyclerAdapter<mainCategroies, AllCategoriesViewHolder>(options) {
	      @Override
	      protected void onBindViewHolder(@NonNull AllCategoriesViewHolder holder, int position, @NonNull mainCategroies model) {
		  holder.setCategory_Name(model.getCategory_Name());
		  MainActivityProgress.setVisibility(View.GONE);
		holder.setImageUrl(model.getImageUrl());
	      }

	      @NonNull
	      @Override
	      public AllCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_maincategory, parent, false);
		  return new AllCategoriesViewHolder(view);
	      }
	 };
	 MainCategoriesRV.setAdapter(Adapter);*/

	 return view;
     }
   public void bannerFlipper(int image) {
	 ImageView imageView = new ImageView(getActivity());
	 imageView.setImageResource(image);
	 imgBanner.addView(imageView);
	 imgBanner.setFlipInterval(4000);
	 imgBanner.setAutoStart(true);
	 imgBanner.setInAnimation(getActivity(), R.anim.fade_in);
	 imgBanner.setOutAnimation(getActivity(), R.anim.fade_out);


     }
     class CategoriesAdapter extends RecyclerView.Adapter<AllCategoriesViewHolder> {
	 private List<mainCategroies> categorylist;

	 CategoriesAdapter(List<mainCategroies> categorylist) {
	      this.categorylist = categorylist;

	 }

	 @NonNull
	 @Override
	 public AllCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_maincategory, parent, false);
	      return new AllCategoriesViewHolder(view);
	 }

	 @Override
	 public void onBindViewHolder(@NonNull final AllCategoriesViewHolder holder, final int position) {
	      final String categoryName = categorylist.get(position).getCategory_Name();
	      holder.setCategory_Name(categoryName);
	      String d = categorylist.get(position).getImageUrl();
	      holder.setImageUrl(d);

	      final String SS = String.valueOf(holder.getAdapterPosition());

	      MainActivityProgress.setVisibility(View.GONE);
	      holder.view.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View v) {

		       MainActivityProgress.setVisibility(View.VISIBLE);


		       DocumentReference refr = firebaseFirestore
			  .collection("MainCategories")
			  .document(String.valueOf(SS));

		       refr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			   @Override
			   public void onSuccess(DocumentSnapshot documentSnapshot) {
			        // GET NAME FROM CATEGORY_NAME WHICH IS THE SAME OF
			        //THE ID OF DOCUMENT
			        MainActivityProgress.setVisibility(View.GONE);
			        String CategoryName = categorylist.get(position).getCategory_Name();

			        Toast.makeText(getContext(), CategoryName, Toast.LENGTH_SHORT).show();
			        Intent i = new Intent(getContext(), CountriesActivity.class);
			        i.putExtra("CategoryName", CategoryName);
			        startActivity(i);

			   }
		       });


		  }
	      });

	 }

	 @Override
	 public int getItemCount() {
	      return categorylist.size();
	 }
     }
     class AllCategoriesViewHolder extends RecyclerView.ViewHolder {
	 private View view;


	 public AllCategoriesViewHolder(final View itemView) {
	      super(itemView);
	      view = itemView;

	 }

	 void setCategory_Name(String category_Name) {
	      TextView names = view.findViewById(R.id.CategoryName);
	      names.setText(category_Name);

	 }


	 void setImageUrl(String imageUrl) {
	      ImageView imageView = view.findViewById(R.id.MainCat_CardViewImage);
	      Picasso.get()
		 .load(imageUrl)
		 .fit()
		 .into(imageView);
	 }
     }
     /*
     private class AllCategoriesViewHolder extends RecyclerView.ViewHolder {
	 public View view;

	 public AllCategoriesViewHolder(View itemView) {
	      super(itemView);
	      view = itemView;

	 }
	 void setCategory_Name(String category_Name) {
	      TextView names = view.findViewById(R.id.CategoryName);
	      names.setText(category_Name);

	 }


	 void setImageUrl(String imageUrl) {
	      ImageView imageView = view.findViewById(R.id.MainCat_CardViewImage);
	      Picasso.get()
		 .load(imageUrl)
		 .fit()
	      .placeholder(android.R.drawable.progress_indeterminate_horizontal)
		 .into(imageView);
	 }
     }*/


}
