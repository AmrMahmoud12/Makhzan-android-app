package com.makhzan.amr.makhzan;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makhzan.amr.makhzan.items.MyAdCard;
import com.makhzan.amr.makhzan.items.Upload;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainAdsActivity extends AppCompatActivity {
     //_____RV_________________
     RecyclerView categoryAdsRV;
     FirestoreRecyclerAdapter<MyAdCard, AllCardViewHolder> dapter;
     FirestoreRecyclerOptions<MyAdCard> options;
     private DocumentSnapshot lastVisible;
     private boolean isScrolling;
     private boolean isLastItemReached;
     private long limit = 5;

     //________________FIREBASE__________________
     DocumentReference firebaseFirestore;
     private FirebaseFirestore Ref;
     private String UID;
     //__________________progress bar_________
     ProgressBar MainAdsProgress;
     private String CategoryPosition;
     private String CountryName;
     //bUTTONS
     Button button;
     private Query next;

     @Override
     protected void onCreate(final Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_main_ads);

	 //_________GET CATOGORY POSITION FROM MAINACTIVITY_______________
	 CategoryPosition = getIntent()
	     .getExtras()
	     .get("CategoryPosition")
	     .toString();

	 CountryName = getIntent()
	     .getExtras()
	     .get("countryName")
	     .toString();
	 //BUTTONS
	 button = findViewById(R.id.required_Orders);
	 button.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  Intent intent = new Intent(MainAdsActivity.this, OrdersActivity.class);
		  intent.putExtra("CategoryPosition", CategoryPosition);
		  intent.putExtra("CountryName", CountryName);
		  startActivity(intent);
	      }
	 });
	 //______________progressbar________________
	 MainAdsProgress = findViewById(R.id.MainAdsProgress);
final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
	 //_______________RV_________________
	 categoryAdsRV = findViewById(R.id.CategoryAdsRV);
	 categoryAdsRV.setHasFixedSize(true);
	 categoryAdsRV.setLayoutManager(linearLayoutManager);


	 //___FIREBASE__________________
	 FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentuser != null;
	 UID = currentuser.getUid();
	 Ref = FirebaseFirestore.getInstance();

//	 Button publish_SpecialAdBtn = findViewById(R.id.publish_SpecialAdBtn);
//	 publish_SpecialAdBtn.setOnClickListener(new View.OnClickListener() {
//	      @Override
//	      public void onClick(View v) {
//		  Intent intent = new Intent(MainAdsActivity.this, ContactToPayActivity.class);
//		  startActivity(intent);
//
//
//	      }
//	 });
	 //CREATE AD
	 Button createAd = findViewById(R.id.publishAdBtn);
	 createAd.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  Ref.collection("Users")
		      .document(UID)
		      .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
		       @Override
		       public void onSuccess(DocumentSnapshot documentSnapshot) {
			   if ( documentSnapshot.contains("paying") ){
			        String paying = documentSnapshot.getData().get("paying").toString();

			        JSONObject jsonObject = null;
			        try {
				    jsonObject = new JSONObject(paying);
			        } catch (JSONException e) {
				    e.printStackTrace();
			        }
			        try {
				    assert jsonObject != null;
				    boolean namee = Boolean.parseBoolean(jsonObject.getString("paid"));

				    if ( namee ) {
					openCreatAdFragment();

				    }else if (namee==false){
					final AlertDialog.Builder builder = new AlertDialog.Builder(MainAdsActivity.this);
					builder.setCancelable(true);
					builder.setTitle("هل تريد انشاء اعلان؟!");
					builder.setMessage("هذه الخدمه مجانيه لأول 100 عميل ...تواصل مع خدمة العملاء لتفعيلها");
					builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
					     @Override
					     public void onClick(DialogInterface dialog, int which) {
						 Intent intent = new Intent(Intent.ACTION_DIAL);
						 intent.setData(Uri.parse("tel:01011392903"));
						 startActivity(intent);

					     }
					});
					builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
					     @Override
					     public void onClick(DialogInterface dialog, int which) {
						 dialog.dismiss();
					     }
					});
					Dialog dialog=builder.create();
					dialog.show();

				    }else {
					Toast.makeText(MainAdsActivity.this, "لا شىء حدث...تواصل مع خدمة العملاء", Toast.LENGTH_SHORT).show();

				    }
			        }catch (JSONException e) {
				    e.printStackTrace();
			        }
			   }
		       }
		      });
	      }
	 });
	 //CREATE AD
//	 Button createSpecialAd = findViewById(R.id.publishSpecialAdBtn);
//	 createSpecialAd.setOnClickListener(new View.OnClickListener() {
//	      @Override
//	      public void onClick(View v) {
//		  openCreateScialAdFragment();
//	      }
//	 });
//	 createSpecialAd.setVisibility(View.GONE);

	 //__________________________QUERY RECYCLER VIEW TO SHOW ADS________________________
	 assert CategoryPosition != null;
	 firebaseFirestore = FirebaseFirestore
	     .getInstance().collection("MainCategories")
	     .document(CategoryPosition).collection("Countries").document(CountryName);
	 final CollectionReference colref=firebaseFirestore.collection("Ads");
	 final Query query = colref
	     .orderBy("creationDate", Query.Direction.DESCENDING).limit(5);

	 query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<QuerySnapshot> task) {
		  if ( task.isSuccessful() ) {
		       final List<MyAdCard> list = new ArrayList<>();

		  if ( task.getResult().size()<=0 ){
		       Toast.makeText(MainAdsActivity.this, "أضف بضاعتك الأن", Toast.LENGTH_SHORT).show();
		       MainAdsProgress.setVisibility(View.GONE);
		   }else {
		       for (DocumentSnapshot document : task.getResult()) {
			   MyAdCard myAdCard = document.toObject(MyAdCard.class);
			   list.add(myAdCard);
		       }
		       final MainAdsAdapter mainAdsAdapter = new MainAdsAdapter(list);
		       categoryAdsRV.setAdapter(mainAdsAdapter);
		       lastVisible=task.getResult().getDocuments().get(task.getResult().size()-1);
		       RecyclerView.OnScrollListener onScrollListener=new RecyclerView.OnScrollListener() {
			   @Override
			   public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			        super.onScrollStateChanged(recyclerView, newState);
			        if ( newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ){
				    isScrolling=true;
			        }
			   }

			   @Override
			   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			        super.onScrolled(recyclerView, dx, dy);
			        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
			        int visibleItemCount = linearLayoutManager.getChildCount();
			        int totalItemCount = linearLayoutManager.getItemCount();
			        if ( isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached ) {
				    isScrolling=false;

				    Query nextQuery=colref
				        .orderBy("creationDate", Query.Direction.DESCENDING)
				        .startAfter(lastVisible)
				        .limit(limit);
				    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
					     for (DocumentSnapshot document : task.getResult()) {
						 MyAdCard myAdCard = document.toObject(MyAdCard.class);
						 list.add(myAdCard);
					     }
					     mainAdsAdapter.notifyDataSetChanged();
					     for (int i=0;i<task.getResult().size();i++) {
						 lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
					     }
					    // Toast.makeText(MainAdsActivity.this, "باقى الأنشطه", Toast.LENGTH_SHORT).show();
					     if ( task.getResult().size() < limit ) {
						 isLastItemReached = true;

					     }
					}
				    });


			        }

			   }
		       };
		       categoryAdsRV.addOnScrollListener(onScrollListener);
		  }
		  }
	      }
	 });
//	 options = new FirestoreRecyclerOptions
//	     .Builder<MyAdCard>()
//	     .setQuery(query, MyAdCard.class)
//	     .build();
//
//	 dapter = new FirestoreRecyclerAdapter<MyAdCard, AllCardViewHolder>(options) {
//	      @Override
//	      protected void onBindViewHolder(@NonNull final AllCardViewHolder holder, final int position, @NonNull MyAdCard model) {
//		  holder.publicAd_discription.setText(model.getMyAddiscriptiontxt());
//		  holder.publicAd_Places.setText(model.getMyPlacestxt());
//		  holder.publicAd_phone.setText(String.valueOf(model.getPhoneNumbera()));
//		  holder.date_of_publish.setText(model.getCreationDate());
//		  holder.adName.setText(model.getMyAdNametxt());
//		  holder.c_name.setText(model.getCompanyName());
//		  holder.TrustedCompanyTxt.setText(model.getTrustedCompanyTxt());
//		  if ( holder.TrustedCompanyTxt.getText().toString().isEmpty() ) {
//		       holder.TrustedCompanyTxt.setBackgroundColor(getResources().getColor(R.color.itemcolor));
//		  } else {
//		       holder.TrustedCompanyTxt.setBackgroundColor(getResources().getColor(R.color.trustedbc));
//		       holder.TrustedCompanyTxt.setTextColor(getResources().getColor(R.color.names));
//		       //ceck pament
//		  }
//		  MainAdsProgress.setVisibility(View.GONE);
//
//		  holder.itemView.setOnClickListener(new View.OnClickListener() {
//		       @Override
//		       public void onClick(View v) {
//
//			   //GET INFO TO SEND TO SINGLE AD ACTIVITY
//			   String n = holder.c_name.getText().toString();
//			   String d = holder.publicAd_discription.getText().toString();
//			   String p = holder.publicAd_phone.getText().toString();
//			   String pl = holder.publicAd_Places.getText().toString();
//
//
//			   //_______GET ID OF THE ADVERTISEMENT_________
//			   DocumentSnapshot snapshot = getSnapshots()
//			       .getSnapshot(holder.getAdapterPosition());
//			   String ss = snapshot.getId();
//			   Intent i = new Intent(MainAdsActivity.this, SingleAdActivity.class);
//			   i.putExtra("single_ad_position", String.valueOf(holder.getAdapterPosition()));
//			   i.putExtra("CategoryName", CategoryPosition);
//			   i.putExtra("documentId", ss);
//			   i.putExtra("CountryName", CountryName);
//			   i.putExtra("n", n);
//			   i.putExtra("d", d);
//			   i.putExtra("p", p);
//			   i.putExtra("pl", pl);
//			   Upload upload = new Upload();
//			   i.putExtra("imageUrl", upload.getImageUrl());
//			   startActivity(i);
//		       }
//		  });
//	      }
//
//	      @NonNull
//	      @Override
//	      public AllCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//		  View view = LayoutInflater.from(parent.getContext())
//		      .inflate(R.layout.cardview_categoryads, parent, false);
//		  return new AllCardViewHolder(view);
//
//	      }
//	 };
//	 categoryAdsRV.setAdapter(dapter);
     }

     public void openCreatAdFragment() {

	 MainAdsProgress.setVisibility(View.GONE);
	 Bundle bundle = new Bundle();

	 bundle.putString("CategoryPositionToFragment", CategoryPosition);
	 bundle.putString("CountryP", CountryName);

	 CreateAdFragment profile = new CreateAdFragment();
	 profile.setArguments(bundle);
	 profile.setCancelable(false);
	 profile.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
	 profile.show(getSupportFragmentManager(), "MyCustomeDialog");

     }


     public void openCreateScialAdFragment() {

	 MainAdsProgress.setVisibility(View.GONE);
	 Bundle bundle = new Bundle();

	 bundle.putString("CategoryPositionToFragment", CategoryPosition);
	 bundle.putString("CountryP", CountryName);

	 CreateSpecialAdFragment special = new CreateSpecialAdFragment();
	 special.setArguments(bundle);
	 special.setCancelable(false);
	 special.show(getSupportFragmentManager(), "MySpecialDialog");
     }

     private class MainAdsAdapter extends RecyclerView.Adapter<MainAdsViewHolder> {
	 private List<MyAdCard> mainAdsList;

	 MainAdsAdapter(List<MyAdCard> mainAdsList) {
	      this.mainAdsList = mainAdsList;
	 }

	 @NonNull
	 @Override
	 public MainAdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	      View view = LayoutInflater.from(parent.getContext())
		 .inflate(R.layout.cardview_categoryads, parent, false);
	      return new MainAdsViewHolder(view);
	 }

	 @Override
	 public void onBindViewHolder(@NonNull final MainAdsViewHolder holder, final int position) {
	      holder.setMyAdNametxt(mainAdsList.get(position).getMyAdNametxt());
	      holder.setCompanyName(mainAdsList.get(position).getCompanyName());
	      holder.setCreationDate(mainAdsList.get(position).getCreationDate());
	      holder.setMyAddiscriptiontxt(mainAdsList.get(position).getMyAddiscriptiontxt());
	      holder.setMyPlacestxt(mainAdsList.get(position).getMyPlacestxt());
	      holder.setPhoneNumbera(mainAdsList.get(position).getPhoneNumbera());
	      MainAdsProgress.setVisibility(View.GONE);
	      holder.itemView.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View v) {
		       MainAdsProgress.setVisibility(View.VISIBLE);
		       DocumentReference ref=firebaseFirestore
			  .collection("Ads").document(String.valueOf(holder.getAdapterPosition()));
		       ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			   @Override
			   public void onSuccess(DocumentSnapshot documentSnapshot) {
			        String adId=mainAdsList.get(position).getAd_ID();
			    //    Toast.makeText(MainAdsActivity.this, adId, Toast.LENGTH_SHORT).show();
			        //GET INFO TO SEND TO SINGLE AD ACTIVITY
			        String n = holder.c_name.getText().toString();
			        String d = holder.publicAd_discription.getText().toString();
			        String p = holder.publicAd_phone.getText().toString();
			        String pl = holder.publicAd_Places.getText().toString();
			        Intent i = new Intent(MainAdsActivity.this, SingleAdActivity.class);
			        i.putExtra("single_ad_position", String.valueOf(holder.getAdapterPosition()));
			        i.putExtra("CategoryName", CategoryPosition);
			        i.putExtra("documentId", adId);
			        i.putExtra("CountryName", CountryName);
			        i.putExtra("n", n);
			        i.putExtra("d", d);
			        i.putExtra("p", p);
			        i.putExtra("pl", pl);
			        MainAdsProgress.setVisibility(View.GONE);
			        startActivity(i);
			   }
		       });

		  }
	      });

	 }

	 @Override
	 public int getItemCount() {
	      return mainAdsList.size();
	 }
     }

     private class MainAdsViewHolder extends RecyclerView.ViewHolder {

	 private TextView adName, publicAd_discription, publicAd_Places, publicAd_phone, date_of_publish, c_name;

	 public MainAdsViewHolder(View itemView) {
	      super(itemView);
	 }

	 public void setMyAdNametxt(String myAdNametxt) {
	      adName = itemView.findViewById(R.id.AdName);
	      adName.setText(myAdNametxt);
	 }

	 public void setMyAddiscriptiontxt(String myAddiscriptiontxt) {
	      publicAd_discription = itemView.findViewById(R.id.publicAd_Discription);
	      publicAd_discription.setText(myAddiscriptiontxt);

	 }

	 public void setMyPlacestxt(String myPlacestxt) {
	      publicAd_Places = itemView.findViewById(R.id.publicAdPlaces);
	      publicAd_Places.setText(myPlacestxt);
	 }

	 public void setPhoneNumbera(String phoneNumbera) {
	      publicAd_phone = itemView.findViewById(R.id.publicAdphone);
	      publicAd_phone.setText(String.valueOf(phoneNumbera));
	 }

	 public void setCreationDate(String creationDate) {
	      date_of_publish = itemView.findViewById(R.id.publishDate);
	      date_of_publish.setText(creationDate);

	 }

	 public void setCompanyName(String companyName) {
	      c_name = itemView.findViewById(R.id.company_name);
	      c_name.setText(companyName);
	 }
     }


     private class AllCardViewHolder extends RecyclerView.ViewHolder {
	 public AllCardViewHolder(View itemView) {
	      super(itemView);
	 }

	 TextView publicAd_discription = itemView.findViewById(R.id.publicAd_Discription);
	 TextView publicAd_Places = itemView.findViewById(R.id.publicAdPlaces);
	 TextView publicAd_phone = itemView.findViewById(R.id.publicAdphone);
	 TextView date_of_publish = itemView.findViewById(R.id.publishDate);
	 TextView adName = itemView.findViewById(R.id.AdName);
	 TextView c_name = itemView.findViewById(R.id.company_name);
	 TextView TrustedCompanyTxt = itemView.findViewById(R.id.TrustedCompanyTxt);
     }

     @Override
     protected void onStart() {
	 super.onStart();
//	 dapter.startListening();

     }

     @Override
     protected void onStop() {
	 super.onStop();
	 // if ( dapter != null ) dapter.stopListening();


     }


}
