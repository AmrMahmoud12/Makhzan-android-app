package com.makhzan.amr.makhzan;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makhzan.amr.makhzan.items.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
     private static final int RESULTLOADIMAGE = 2;
     private RecyclerView mUploadList;
     private List<String> fileNameList;
     private List<String> fileDoneList;
     UploadLOrderAdapter uploadListAdapter;
     int i; //that for looping
     private Uri fileUri;//for the picture multiple
     private Uri singleUri;//for single picture
     List<Uri> uri;
     ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

     int totalItemSelected;


     //LAYOUT
     TextInputLayout nameLayout;
     EditText nameTxt, numberTxt;
     Button createOrder, cancelOrder;
     ImageButton Order_Images;
     //FIREBASE
     private FirebaseFirestore firebaseFirestore;
     private CollectionReference collectionReference;
     String UID;
     //Adapter
     //RECYCLER
     private RecyclerView orderRV;
     //private FirestoreRecyclerAdapter<OrderItem, OrdersActivity.OrdersViewHolder> adapter;
     //BUTTON
     private Button button;

     private Calendar calendar;
     private SimpleDateFormat simpleDateFormat;
     private String Date;
     private String CategoryPosition;
     private String CountryName;
     //PROGERSS PAR
     ProgressBar orderProgress;
     AlertDialog alertDialog;
     private String customerid;

     private DocumentSnapshot lastVisible;
     private boolean isScrolling;
     private boolean isLastItemReached;
     private long limit = 5;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_orders);
	 //_________GET CATOGORY POSITION FROM MAINACTIVITY_______________
	 CategoryPosition = getIntent()
	     .getExtras()
	     .get("CategoryPosition")
	     .toString();

	 CountryName = getIntent()
	     .getExtras()
	     .get("CountryName")
	     .toString();
	 orderProgress = findViewById(R.id.ordersProgress);
	 button = findViewById(R.id.createOrderBtn);
	 //rv
	 final RecyclerView orderRV = findViewById(R.id.orderRV);
	 //FIREBASE
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

	 orderRV.setLayoutManager(linearLayoutManager);
	 orderRV.setHasFixedSize(true);

	 //___________recycle pictures_________________
	 //mUploadList = findViewById(R.id.OrdersImagesRv);
	 fileNameList = new ArrayList<>();
	 fileDoneList = new ArrayList<>();
	 //uploadListAdapter = new UploadLOrderAdapter(fileNameList, fileDoneList);
	 //mUploadList.setLayoutManager(new LinearLayoutManager(this));
	// mUploadList.setAdapter(uploadListAdapter);
	   firebaseFirestore = FirebaseFirestore.getInstance();
	 collectionReference = firebaseFirestore
	     .collection("MainCategories")
	     .document(CategoryPosition)
	     .collection("Countries")
	     .document(CountryName)
	     .collection("Orders");

	 Query query = collectionReference.orderBy("orderDate", Query.Direction.DESCENDING);
	 query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<QuerySnapshot> task) {
		  if ( task.isSuccessful() ) {
		       final List<OrderItem> list = new ArrayList<>();
		       if ( task.getResult().size() <= 0 ) {
			   Toast.makeText(OrdersActivity.this, "لا يوجد اوردرات الان..ابدأ بالاضافه ", Toast.LENGTH_SHORT).show();
		       } else {
			   for (DocumentSnapshot documentSnapshot : task.getResult()) {
			        OrderItem orderItem = documentSnapshot.toObject(OrderItem.class);
			        list.add(orderItem);
			   }
			   final PublicOrdersAdapter publicOrdersAdapter = new PublicOrdersAdapter(list);
			   orderRV.setAdapter(publicOrdersAdapter);
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
				    if ( isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached ) {
					isScrolling = false;
					Query nextQuery = collectionReference
					    .orderBy("orderDate", Query.Direction.DESCENDING)
					    .limit(5);

					nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					     @Override
					     public void onComplete(@NonNull Task<QuerySnapshot> task) {
						 if ( task.isSuccessful() ) {
						      for (DocumentSnapshot documentSnapshot : task.getResult()) {
							  OrderItem orderItem = documentSnapshot.toObject(OrderItem.class);
							  list.add(orderItem);
						      }
						      publicOrdersAdapter.notifyDataSetChanged();
						      for (int i = 0; i < task.getResult().size(); i++) {
							  lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
						      }
						      // Toast.makeText(OrdersActivity.this, "باقى الأوردرات", Toast.LENGTH_SHORT).show();
						      if ( task.getResult().size() < limit ) {
							  isLastItemReached = true;

						      }
						 }
					     }
					});


				    }
			        }
			   };
			   orderRV.addOnScrollListener(onScrollListener);

		       }
		  }
	      }
	 });

//	 FirestoreRecyclerOptions<OrderItem> options = new FirestoreRecyclerOptions.Builder<OrderItem>()
//	     .setQuery(query, OrderItem.class)
//	     .build();
//	 adapter = new FirestoreRecyclerAdapter<OrderItem, OrdersViewHolder>(options) {
//	      @Override
//	      protected void onBindViewHolder(@NonNull final OrdersViewHolder holder, int position, @NonNull OrderItem model) {
//		  holder.orderTitle.setText(model.getOrderTitle());
//		  holder.orderDate.setText(model.getOrderDate());
//		  holder.orderCompany.setText(model.getCompanyName());
//		  holder.orderPhone.setText(String.valueOf(model.getNumber()));
//
//
//		  holder.itemView.setOnClickListener(new View.OnClickListener() {
//		       @Override
//		       public void onClick(View v) {
//			   //_______GET ID OF THE ADVERTISEMENT_________
//			   DocumentSnapshot snapshot = getSnapshots()
//			       .getSnapshot(holder.getAdapterPosition());
//			   OrderPosition = snapshot.getId();
//			   collectionReference.document(OrderPosition).get()
//			       .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//				   @Override
//				   public void onSuccess(DocumentSnapshot documentSnapshot) {
//				        customerid = documentSnapshot.getString("OrderPublisherId");
//				        Intent intent=new Intent(OrdersActivity.this,CustomersActivity.class);
//					intent.putExtra("customerid",customerid);
//				        intent.putExtra("customerName",holder.orderCompany.getText().toString());
//
//				        Toast.makeText(OrdersActivity.this, customerid, Toast.LENGTH_SHORT).show();
//				        startActivity(intent);
//				   }
//			       });
//
//		       }
//		  });
//
//	      }
//	      @NonNull
//	      @Override
//	      public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//		  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_order, parent, false);
//		  return new OrdersViewHolder(view);
//	      }
//	 };
//	 orderRV.setAdapter(adapter);


	 orderProgress.setVisibility(View.GONE);
	 button.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  alert();
	      }
	 });
     }
     public class UploadLOrderAdapter extends RecyclerView.Adapter<UploadLOrderAdapter.ViewHolder> {

	 public List<String> fileNameList;
	 public List<String> fileDoneList;

	 public UploadLOrderAdapter(List<String> fileNameList, List<String> fileDoneList) {
	      this.fileNameList = fileNameList;
	      this.fileDoneList = fileDoneList;
	 }

	 @NonNull
	 @Override
	 public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
	      return new ViewHolder(v);
	 }

	 @Override
	 public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
	      String fileName = fileNameList.get(position);
	      holder.fileNameView.setText(fileName);

	      String fileDone = fileDoneList.get(position);
	      if ( fileDone.equals("Uploading") ) {
		  holder.fileDoneView.setImageResource(R.drawable.notcompleted);
	      } else {
		  holder.fileDoneView.setImageResource(R.drawable.completed);
	      }
	 }

	 @Override
	 public int getItemCount() {
	      return fileNameList.size();
	 }


	 public class ViewHolder extends RecyclerView.ViewHolder {
	      View mView;
	      public TextView fileNameView;
	      public ImageView fileDoneView;

	      public ViewHolder(View itemView) {
		  super(itemView);
		  mView = itemView;
		  fileNameView = mView.findViewById(R.id.picName);
		  fileDoneView = mView.findViewById(R.id.completedPic);
	      }
	 }
     }


     public void alert() {
	 final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
	 LayoutInflater inflater = this.getLayoutInflater();
	 View dialogView = inflater.inflate(R.layout.create_order_alert, null);
	 dialogBuilder.setView(dialogView);

	 nameLayout = dialogView.findViewById(R.id.orderName);
	 nameTxt = dialogView.findViewById(R.id.orderNameTxt);
	 Order_Images=dialogView.findViewById(R.id.Order_Images);
	 numberTxt = dialogView.findViewById(R.id.orderNumber);
	 createOrder = dialogView.findViewById(R.id.createOrderBtn);
	 cancelOrder = dialogView.findViewById(R.id.cancelOrderBtn);


	 alertDialog = dialogBuilder.create();
	 alertDialog.show();
	 cancelOrder.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  alertDialog.dismiss();
	      }
	 });
	 createOrder.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  checkTextsandContinue();
	      }
	 });
	 Order_Images.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  //  uploadImage();
		  Intent intent = new Intent();
		  intent.setType("image/*");
		  intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		  intent.setAction(Intent.ACTION_GET_CONTENT);
		  startActivityForResult(Intent.createChooser(intent, "Select picture"), RESULTLOADIMAGE);
	      }
	 });
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	 if ( requestCode == RESULTLOADIMAGE && resultCode == RESULT_OK ) {
	      if ( data.getClipData() != null ) {
		  Toast.makeText(this, "selcet multiple", Toast.LENGTH_SHORT).show();
//		  totalItemSelected = data.getClipData().getItemCount();
//		  for (i = 0; i < totalItemSelected; i++) {
//		       fileUri = data.getClipData().getItemAt(i).getUri();
//		       String fileName = getFileName(fileUri);
//		       fileNameList.add(fileName);
//		       fileDoneList.add("Uploading");
//		       mArrayUri.add(fileUri);//add uir to array used into upload multipl imagesor sin
//		       uploadListAdapter.notifyDataSetChanged();


		  }
	      } else if ( data.getData() != null ) {
		  Toast.makeText(this, "selcet single", Toast.LENGTH_SHORT).show();

//		  singleUri = data.getData();
//		  String fileName = getFileName(singleUri);
//		  fileNameList.add(fileName);
//		  fileDoneList.add("Uploading");
//		  mArrayUri.add(singleUri);
//		  uploadListAdapter.notifyDataSetChanged();

	      }

	 }


     public void checkTextsandContinue() {
	 if ( !validateOrderName() | !validateOrderNumber() ) {
	      return;
	 } else {
	      addOrderToDatabase();
	 }


     }

     public void addOrderToDatabase() {
	 //GET DATE TO BE ADDED TO THE DATABASE
	 calendar = Calendar.getInstance();
	 simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
	 Date = simpleDateFormat.format(calendar.getTime());
	 final String txt = nameTxt.getText().toString().trim();
	 final long num = Long.parseLong(numberTxt.getText().toString().trim());


	 firebaseFirestore.collection("Users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
		  if ( task.isSuccessful() ) {
		       DocumentSnapshot documentSnapshot = task.getResult();
		       if ( documentSnapshot.exists() ) {
			   String companyName = documentSnapshot.getString("companyName");
			   DocumentReference ref = collectionReference.document();
			   String x = ref.getId();

			   OrderItem orderItem = new OrderItem(txt, num, Date, companyName, UID, x);
			   ref.set(orderItem);


			   firebaseFirestore.collection("Users").document(UID)
			       .collection("MyOrders")
			       .document(x)
			       .set(orderItem);

			   firebaseFirestore.collection("System")
			       .document(UID)
			       .collection("ProfileOrders")
			       .document(x).set(orderItem);


			   alertDialog.dismiss();

		       } else {
		       }
		  } else {

		  }

	      }
	 });


     }

     private boolean validateOrderName() {
	 String nameValidation = nameLayout.getEditText().getText().toString().trim();

	 if ( nameValidation.isEmpty() ) {
	      nameLayout.setError("اكتب عنوان الأوردر");
	      return false;
	 } else if ( nameValidation.length() >= 15 ) {
	      nameLayout.setError("اكتب 15 حرف فقط ");
	      return false;
	 } else {
	      nameLayout.setError(null);
	      return true;
	 }
     }

     private boolean validateOrderNumber() {
	 String numberValidation = String.valueOf(numberTxt.getText().toString().trim());

	 if ( numberValidation.isEmpty() ) {
	      numberTxt.setError("اكتب رقم التليفون");
	      return false;
	 } else {
	      numberTxt.setError(null);
	      return true;
	 }
     }

     @Override
     public void onStop() {
	 super.onStop();
	 //adapter.stopListening();
     }

     @Override
     public void onStart() {
	 super.onStart();
	 //adapter.startListening();
     }

     //
//     private class OrdersViewHolder extends RecyclerView.ViewHolder {
//	 TextView orderTitle, orderCompany, orderDate, orderPhone, followCustomer;
//
//	 public OrdersViewHolder(View itemView) {
//	      super(itemView);
//	      orderTitle = itemView.findViewById(R.id.orderTitle);
//	      orderCompany = itemView.findViewById(R.id.orderCompany);
//	      orderDate = itemView.findViewById(R.id.orderDate);
//	      orderPhone = itemView.findViewById(R.id.orderPhone);
//	      followCustomer = itemView.findViewById(R.id.followCustomer);
//
//	 }
//
//     }
     private class PublicOrdersAdapter extends RecyclerView.Adapter<PublicOrdersViewHolder> {
	 private List<OrderItem> orderList;

	 PublicOrdersAdapter(List<OrderItem> orderList) {
	      this.orderList = orderList;
	 }

	 @NonNull
	 @Override
	 public PublicOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_order, parent, false);
	      return new PublicOrdersViewHolder(view);
	 }

	 @Override
	 public void onBindViewHolder(@NonNull final PublicOrdersViewHolder holder, final int position) {
	      holder.setCompanyName(orderList.get(position).getCompanyName());
	      holder.setOrderDate(orderList.get(position).getOrderDate());
	      holder.setNumber(orderList.get(position).getNumber());
	      holder.setOrderTitle(orderList.get(position).getOrderTitle());
	      holder.itemView.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View v) {
//		       RelativeLayout layout = new RelativeLayout(OrdersActivity.this);
//		       final ProgressBar progressBar = new ProgressBar(OrdersActivity.this,null,android.R.attr.progressBarStyleLarge);
//		       progressBar.setIndeterminate(true);
//		       progressBar.setVisibility(View.VISIBLE);
//		       RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
//		       params.addRule(RelativeLayout.CENTER_IN_PARENT);
//		       layout.addView(progressBar,params);
//
//		       setContentView(layout);
		       DocumentReference reference = collectionReference
			  .document(String.valueOf(holder.getAdapterPosition()));
		       reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			   @Override
			   public void onSuccess(DocumentSnapshot documentSnapshot) {
			        String orderId = orderList.get(position).getOrderId();
			        String customerid = orderList.get(position).getOrderPublisherId();
			        //Toast.makeText(OrdersActivity.this, orderId, Toast.LENGTH_SHORT).show();
			        Intent intent = new Intent(OrdersActivity.this, CustomersActivity.class);
			        intent.putExtra("orderId", orderId);
			        intent.putExtra("customerid", customerid);
			        intent.putExtra("customerName", holder.order_Company.getText().toString());

			         startActivity(intent);
			   }
		       });


		  }
	      });
	 }

	 @Override
	 public int getItemCount() {
	      return orderList.size();
	 }
     }

     private class PublicOrdersViewHolder extends RecyclerView.ViewHolder {
	 private TextView order_Title, order_Company, order_Date, order_Phone, follow_Customer;

	 public PublicOrdersViewHolder(View itemView) {
	      super(itemView);
	 }

	 public void setOrderTitle(String orderTitle) {
	      order_Title = itemView.findViewById(R.id.orderTitle);
	      order_Title.setText(orderTitle);
	 }

	 public void setNumber(long number) {
	      order_Phone = itemView.findViewById(R.id.orderPhone);
	      order_Phone.setText(String.valueOf(number));
	 }

	 public void setOrderDate(String orderDate) {
	      order_Date = itemView.findViewById(R.id.orderDate);
	      order_Date.setText(orderDate);
	 }

	 public void setCompanyName(String companyName) {
	      order_Company = itemView.findViewById(R.id.orderCompany);
	      order_Company.setText(companyName);

	 }


     }
}
