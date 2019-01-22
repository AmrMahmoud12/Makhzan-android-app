package com.makhzan.amr.makhzan;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.makhzan.amr.makhzan.Adapter.PageAdapter;
import com.makhzan.amr.makhzan.items.mainCategroies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

     private DrawerLayout mdrawerLayout;
     private ActionBarDrawerToggle mToggle;
     private Toolbar toolbar;


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
    // private FirestoreRecyclerAdapter<mainCategroies, AllCategoriesViewHolder> Adapter;
     //String categortName;
     //__________progress dialog__________
     private ProgressBar MainActivityProgress;
     private DocumentSnapshot lastVisible;
     private boolean isScrolling;
     private boolean isLastItemReached;
     private int limit = 5;
     //____________ViewFlipper____________________
     ViewFlipper imgBanner;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_main);
	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//fix orientation
	 TabLayout tabLayout=findViewById(R.id.tabItems);
	 ViewPager fragmentPager=findViewById(R.id.fragmentsPager);
	 fragmentPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
	 tabLayout.setupWithViewPager(fragmentPager);
	 for (int i = 0; i < tabLayout.getTabCount(); i++) {
	      tabLayout.getTabAt(0).setIcon(R.drawable.shopping);
	      tabLayout.getTabAt(1).setIcon(R.drawable.retails);
	 }

//
	 //ViewFlipper and banner slider_____________
	/* imgBanner = findViewById(R.id.imgBanner);

	 int slider[] = {
	     R.drawable.circle,
	     R.drawable.adnameicon,
	     R.drawable.ad_icon};
	 for (int slide : slider) {
	      bannerFlipper(slide);
	 }*/
	 //____________________________________________
	 //TOOLBAR
	 toolbar = findViewById(R.id.new_action);
	 setSupportActionBar(toolbar);
//
//	 //______PROGRESS BAR__________
//	 MainActivityProgress = findViewById(R.id.MainActivityProgress);

	 //___FIREBASE__________________
	 FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentuser != null;
	 UID = currentuser.getUid();
	 firebaseFirestore = FirebaseFirestore.getInstance();

	 //________RV________
	 /*MainCategoriesRV = findViewById(R.id.MainCategoriesRV);
	 MainCategoriesRV.setHasFixedSize(true);
	 final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
	 MainCategoriesRV.setLayoutManager(linearLayoutManager);*/

	 //final RecyclerView.LayoutManager linearLayoutManager=new GridLayoutManager(this,2);
	 //MainCategoriesRV.setLayoutManager(linearLayoutManager);
	 //add token to the database
	 /*
	 when you log in token is generated stored in shared preferences

	 */
	 Map<String, Object> map = new HashMap<>();
	 map.put("token_id", SharedPrefManager.getmInstance(this).getToken());
	 firebaseFirestore.collection("Users").document(UID).set(map, SetOptions.merge());

	 // Configure Google Sign In//=====================1===================
	 GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
	     .requestIdToken(getString(R.string.default_web_client_id))
	     .requestEmail()
	     .build();
	 mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
	 //QUERY RECYCLER VIEW
	/* final CollectionReference ref = firebaseFirestore
	     .collection("MainCategories");

	 Query firstQuery = ref
	     .orderBy("Category_Name", Query.Direction.ASCENDING).limit(5);

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

		       lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
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
			        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
			        int visibleItemCount = linearLayoutManager.getChildCount();
			        int totalItemCount = linearLayoutManager.getItemCount();
			        if ( isScrolling && (firstVisibleItemPosition+visibleItemCount == totalItemCount) && !isLastItemReached ) {
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
						 lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
					     }

					     Toast.makeText(MainActivity.this, "please wait,... ", Toast.LENGTH_SHORT).show();
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
	 });*/


//___________________NAVIGATION _______________
	 mdrawerLayout = findViewById(R.id.drawerLayout);
	 mToggle = new ActionBarDrawerToggle(this, mdrawerLayout, R.string.open, R.string.close);
	 mdrawerLayout.addDrawerListener(mToggle);
	 mToggle.syncState();

	 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	 navigationView = findViewById(R.id.nav_view);
	 View headerView = navigationView.getHeaderView(0);
	 CompanyEmail = headerView.findViewById(R.id.CompanyEmail);
	 companyName = headerView.findViewById(R.id.CompanyName);
	 //companyName.setError("please enter a name");
	 //________________________

	 navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
	      @Override
	      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		  switch ( item.getItemId() ) {
		       case R.id.invite:

			   Intent sendIntent = new Intent();
			   sendIntent.setAction(Intent.ACTION_SEND);
			   sendIntent.putExtra(Intent.EXTRA_TEXT, "\nLet me recommend you this application\n" + "https://play.google.com/store/apps/details?id=com.makhzan.amr.makhzan");
			   sendIntent.setType("text/plain");
			   startActivity(Intent.createChooser(sendIntent, "Share via"));
			   break;
		       case R.id.new_account:
			   Intent i = new Intent(MainActivity.this, edit_ProfileActivity.class);
			   startActivity(i);

			   break;
		       case R.id.MyShop:
			   Intent t = new Intent(MainActivity.this, MyShopActivity.class);
			   startActivity(t);

			   break;
		       case R.id.new_settings:
//			   Snackbar snackbar = Snackbar
//			       .make(mdrawerLayout, "Coming soon", Snackbar.LENGTH_LONG)
//
//			       .setAction("Close", new View.OnClickListener() {
//				   @Override
//				   public void onClick(View v) {
//
//				   }
//			       });
//			   snackbar.setActionTextColor(Color.RED);
//			   snackbar.show();
			   Intent F = new Intent(MainActivity.this, SettingActivity.class);
			   startActivity(F);
			   break;

		       case R.id.FollowinglList:
			   Intent z = new Intent(MainActivity.this, FollowingListActivity.class);
			   startActivity(z);
			   break;

		       case R.id.CustomersList:
			   Intent c = new Intent(MainActivity.this, CustomersListActivity.class);
			   startActivity(c);
			   break;

		       case R.id.ShowAds:
			   Intent s = new Intent(MainActivity.this, MyAdsActivity.class);
			   startActivity(s);
			   break;

		       case R.id.contact:
			   final Intent intent = new Intent(Intent.ACTION_DIAL);
			   intent.setData(Uri.parse("tel:01011392903"));
			   startActivity(intent);


			   break;

		       case R.id.ShowOrders:
			   final Intent d = new Intent(MainActivity.this, MyOrdersActivity.class);
			   startActivity(d);
			   break;
		       case R.id.new_logout:
			   final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			   builder.setCancelable(true);
			   builder.setTitle("هل تريد تسجيل الخروج؟!");
			   // builder.setMessage("اشعاراتك ستظل تعمل..اذا أردت الغاءها عليك بحذف الشركات والموردين!..");
			   builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
				    //token methods
			   /*

			   get token id
			   when log out delete token
			   and when user log in it will be generated again


			   */
				    Map<String, Object> updates = new HashMap<>();
				    updates.put("companiesnotifications", false);
				    updates.put("customersnotifications", false);
				    updates.put("token_id", FieldValue.delete());
				    firebaseFirestore.collection("Users").document(UID)
				        .update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {

					     FirebaseAuth.getInstance().signOut();
					     Intent inten = new Intent(MainActivity.this, Login2Activity.class);
					     startActivity(inten);
					     finish();

					}
				    });

			        }
			   });
			   builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
			        }
			   });
			   Dialog dialog = builder.create();
			   dialog.show();

//
			   break;
		  }
		  mdrawerLayout.closeDrawer(GravityCompat.START);

		  return true;
	      }

	 });


     }

/*     public void bannerFlipper(int image) {
	 ImageView imageView = new ImageView(this);
	 imageView.setImageResource(image);
	 imgBanner.addView(imageView);
	 imgBanner.setFlipInterval(4000);
	 imgBanner.setAutoStart(true);
	 imgBanner.setInAnimation(this, R.anim.fade_in);
	 imgBanner.setOutAnimation(this, R.anim.fade_out);


     }*/

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {

	 if ( mToggle.onOptionsItemSelected(item) ) {
	      return true;
	 }
	 return super.onOptionsItemSelected(item);
     }

     @Override
     protected void onStart() {
	 super.onStart();
	 //_______LISTEN FOR NAME AND PERSONAL EMAIL__________
	 listenerRegistration = firebaseFirestore
	     .collection("Users")
	     .document(UID).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
		 @Override
		 public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
		      if ( e != null ) {
			  Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
			  return;
		      }
		      if ( documentSnapshot.exists() ) {
			  String coName = documentSnapshot.getString("companyName");
			  String coEmail = documentSnapshot.getString("personEmail");
			  x = documentSnapshot.getBoolean("x");


			  companyName.setText(coName);
			  CompanyEmail.setText(coEmail);
		      }

		 }

	     });

//_______________LISTEN FOR ADAPTER OF RECYCLER VIEW____________
	 //Adapter.startListening();
     }

     @Override
     protected void onStop() {
	 super.onStop();
//	 listenerRegistration.remove();
//	 if ( Adapter != null )
//	      Adapter.stopListening();
     }

     //ADAPTER
 /*    private class CategoriesAdapter extends RecyclerView.Adapter<AllCategoriesViewHolder> {
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

			        Toast.makeText(MainActivity.this, CategoryName, Toast.LENGTH_SHORT).show();
			        Intent i = new Intent(MainActivity.this, CountriesActivity.class);
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

     private class AllCategoriesViewHolder extends RecyclerView.ViewHolder {
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
     }*/

     //====================on back pressed===========================================================
     @Override
     public void onBackPressed() {
	 Intent startMain = new Intent(Intent.ACTION_MAIN);
	 startMain.addCategory(Intent.CATEGORY_HOME);
	 startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 startActivity(startMain);
     }
}
