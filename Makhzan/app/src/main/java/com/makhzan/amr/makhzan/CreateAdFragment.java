package com.makhzan.amr.makhzan;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.makhzan.amr.makhzan.items.Adphotos_item;
import com.makhzan.amr.makhzan.items.MyAdCard;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAdFragment extends DialogFragment {
     private RecyclerView mUploadList;
     private List<String> fileNameList;
     private List<String> fileDoneList;
     UploadListAdapter uploadListAdapter;
     int i; //that for looping
     private Uri fileUri;//for the picture multiple
     private Uri singleUri;//for single picture
     List<Uri> uri;
     ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

     int totalItemSelected;
     Uri newUri;
     public static final String TAG = "MyCustomeDialog";
     private static final int RESULT_LOAD_IMAGE = 1;
     private static final int PICK_IMAGE_REQUEST = 1;
     private Button
	saveAdBtn,
	CancelBtn;

     private ImageButton chooseImages;
     //_________IMAGErl
     Uri downloadUri;

     //___________________AD NAME LAYOUT_______________
     private TextInputLayout ad_name;
     private EditText ad_nametxt;

     //_________________DESCRIPTION LAYOUTS_____________
     private TextInputLayout discribe;
     private EditText discribetxt;

     //__________PHONE NUMBER_______________
     private TextInputLayout Ad_phoneNumber;
     private EditText phone_Number;

     //___________________FIREBASE_______________________________________

     private FirebaseFirestore Ref;
     String UID;
     private DocumentReference MyAds;
     private CollectionReference CategoriesAds;
     StorageReference mStorage;
     private StorageTask storageTask;

     //___________________PLACES CHECKBOX_____________________
     private TextView TV;
     private Button Choose_placeBtn;

     //_______LIST ITEMS_______________
     String[] listItems;
     boolean[] checkedItems;
     ArrayList<Integer> mUserItems = new ArrayList<>();

     //__________GET CURRENT TIME AND DATE____________
     Calendar calendar;
     SimpleDateFormat simpleDateFormat;

     //____postion
     String categoyPo;
     String myadstring;
     String CountryP;
     boolean paying_status;
     String EndDate;

     String Date;

     //______adding pictures
     private Uri mImageUri;
     ImageView mImageView;
     //___________RECYCLERVIEWS______
//     RecyclerView rv_Images;
//     String fileName;
//     List<String> fileNameList;
//     List<Uri> fileImageList;
//     UploadListAdapter uploadListAdapter;
//     Uri fileUri;
//     Uri s;

     //Ad Items

     //______________progress bar_____
     ProgressBar mProgressBar;

     public CreateAdFragment() {
	 // Required empty public constructor
     }

     @SuppressLint("ClickableViewAccessibility")

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
			   Bundle savedInstanceState) {
	 // Inflate the layout for this fragment
	 final View view = inflater.inflate(R.layout.fragment_create_ad, container, false);

	 //___________recycle pictures_________________
	 mUploadList = view.findViewById(R.id.rv);
	 fileNameList = new ArrayList<>();
	 fileDoneList = new ArrayList<>();
	 uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);
	 mUploadList.setLayoutManager(new LinearLayoutManager(getContext()));
	 mUploadList.setAdapter(uploadListAdapter);
	 //____________buttos____________
	 saveAdBtn = view.findViewById(R.id.saveAdBtn1);
	 CancelBtn = view.findViewById(R.id.cancelAd);
	 chooseImages = view.findViewById(R.id.choose_Images);
	 Choose_placeBtn = view.findViewById(R.id.btnOrder1);

	 //___________________LAYOUTS________________
	 ad_name = view.findViewById(R.id.AdName1);
	 ad_nametxt = view.findViewById(R.id.AdNametxt1);

	 discribe = view.findViewById(R.id.AdDiscription1);
	 discribetxt = view.findViewById(R.id.AdDiscriptiontxt1);
	 phone_Number = view.findViewById(R.id.phoneNumber1);
	 //_______progress bar____
	 mProgressBar = view.findViewById(R.id.CreatAdProgress);
	 mProgressBar.setVisibility(View.GONE);


	 Ref = FirebaseFirestore.getInstance();
	 //________________________GET CURRENT FIREBASE USER_______________________________________
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 MyAds = Ref.collection("Users")
	     .document(UID)
	     .collection("MyAds").document();
	 myadstring = MyAds.getId();

	 mStorage = FirebaseStorage.getInstance().getReference();

//	 //__________RECYCLERVIEW OF IMAGES ADDED___________
//	 fileImageList = new ArrayList<Uri>();
//	 fileNameList = new ArrayList<>();
//	 uploadListAdapter = new UploadListAdapter(fileNameList, fileImageList);
//	 rv_Images = view.findViewById(R.id.rv_Images);
//	 rv_Images.setHasFixedSize(true);
//	 rv_Images.setLayoutManager(new LinearLayoutManager(getContext()));
//	 rv_Images.setAdapter(uploadListAdapter);

	 //_____________CHOOSE IMAGES BUTTON_______________
	 mImageView = view.findViewById(R.id.image);

	 chooseImages.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  if ( mArrayUri != null ) {
		       mArrayUri.clear();
		       fileDoneList.clear();
		       fileNameList.clear();
		  }

		  //  uploadImage();
		  Intent intent = new Intent();
		  intent.setType("image/*");
		  intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		  intent.setAction(Intent.ACTION_GET_CONTENT);
		  startActivityForResult(Intent.createChooser(intent, "Select picture"), RESULT_LOAD_IMAGE);

	      }
	 });
//	 removeImage.setOnClickListener(new View.OnClickListener() {
//	      @Override
//	      public void onClick(View v) {
//		  //mImageView.setImageDrawable(null);
//		  mImageView.setImageResource(R.drawable.makhzan);
//
//	      }
//	 });

//	 final Button removeImages = view.findViewById(R.id.remove_Images);
//	 removeImages.setOnClickListener(new View.OnClickListener() {
//	      @Override
//	      public void onClick(View v) {
//		  removeimages();
//	      }
//	 });
	 //_____________STRINGS FROM ACTIVITY ADS__________
	 assert getArguments() != null;
	 categoyPo = getArguments().getString("CategoryPositionToFragment");
	 CountryP = getArguments().getString("CountryP");

	 assert categoyPo != null;
	 CategoriesAds = Ref.collection("MainCategories").document(categoyPo)
	     .collection("Countries").document(CountryP).collection("Ads");


	 //________________________CHECK BOX ITEMS______________________
	 listItems = getResources().getStringArray(R.array.shopingItems);
	 checkedItems = new boolean[listItems.length];

	 //_________CHECK BOX ITEMS___________________
	 TV = view.findViewById(R.id.tvItemSelected1);
	 Choose_placeBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  AlertDialog.Builder mBuilder =
		      new AlertDialog.Builder(getActivity());
		  mBuilder.setTitle("المدن التى تستطيع التوزيع اليها");
		  mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int position, boolean isChecked) {
			   if ( isChecked ) {
			        // If the user checked the item, add it to the selected items
			        mUserItems.add(position);
			   } else if ( mUserItems.contains(position) ) {
			        // Else, if the item is already in the array, remove it
			        mUserItems.remove(Integer.valueOf(position));
			   }
		       }
		  });
		  mBuilder.setCancelable(false);
		  mBuilder.setPositiveButton("أوك", new DialogInterface.OnClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int position) {
			   StringBuilder item = new StringBuilder();
			   for (int i = 0; i < mUserItems.size(); i++) {
			        item.append(listItems[mUserItems.get(i)]);
			        if ( i != mUserItems.size() - 1 ) {
				    item.append(",");//put coma if it is not the last item
			        }//not the last item
			   }
			   TV.setText(item);
		       }
		  });
		  mBuilder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int which) {
			   dialog.dismiss();
		       }
		  });
		  mBuilder.setNeutralButton("ازالة العلامات", new DialogInterface.OnClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int which) {
			   for (int i = 0; i < checkedItems.length; i++) {
			        checkedItems[i] = false;
			        mUserItems.clear();
			        TV.setText("");
			   }
		       }
		  });
		  AlertDialog mDialog = mBuilder.create();
		  mDialog.show();
	      }
	 });

	 saveAdBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  confirmInput();

	      }
	 });
	 CancelBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  //storeMultipleImages(mArrayUri);
		  getDialog().dismiss();
		  // getDialog().dismiss();
		  /*
		  object:
		  no capitals, acurate strings with no spaces

		  */

	      }
	 });
//	 discribetxt.setOnTouchListener(new View.OnTouchListener() {
//	      @Override
//	      public boolean onTouch(View v, MotionEvent event) {
//		  if (view.getId() == R.id.AdDiscription1) {
//		       view.getParent().requestDisallowInterceptTouchEvent(true);
//		       switch (event.getAction()&MotionEvent.ACTION_MASK){
//			   case MotionEvent.ACTION_UP:
//			        view.getParent().requestDisallowInterceptTouchEvent(false);
//			        break;
//		       }
//		  }
//		  return false;	      }
//	 });

	 return view;

     }


     public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

	 public List<String> fileNameList;
	 public List<String> fileDoneList;

	 public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList) {
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

     public void storeImage(Uri fileUri) {
	 StorageReference filepath = mStorage
	     .child("User_Pictures")
	     .child(UID)
	     .child("Ads_pictures")
	     .child(MyAds.getId())
	     .child(System.currentTimeMillis() + "." + getFileExtension(fileUri));

	 filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

		  if ( task.isSuccessful() ) {

		       if (getDialog() != null && getDialog().isShowing()) {
			   getDialog().dismiss();
		       }

		  } else {

		  }

	      }
	 });

     }

     public void storeMultipleImages(List<Uri> imageUris) {
	 for (Uri uri : imageUris) {
	      storeImage(uri);
	 }
     }

     public String getFileName(Uri uri) {
	 String result = null;
	 if ( uri.getScheme().equals("content") ) {
	      Cursor cursor = getContext().getContentResolver()
		 .query(uri, null, null, null, null);
	      try {
		  if ( cursor != null && cursor.moveToFirst() ) {
		       result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
		  }
	      } finally {
		  assert cursor != null;
		  cursor.close();
	      }
	 }
	 if ( result == null ) {
	      result = uri.getPath();
	      int cut = result.lastIndexOf('/');
	      if ( cut != -1 ) {
		  result = result.substring(cut + 1);
	      }
	 }
	 return result;

     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	 if ( requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK ) {
	      if ( data.getClipData() != null ) {
		  Toast.makeText(getContext(), "selcet multiple", Toast.LENGTH_SHORT).show();
		  totalItemSelected = data.getClipData().getItemCount();
		  for (i = 0; i < totalItemSelected; i++) {
		       fileUri = data.getClipData().getItemAt(i).getUri();
		       String fileName = getFileName(fileUri);
		       fileNameList.add(fileName);
		       fileDoneList.add("Uploading");
		       mArrayUri.add(fileUri);//add uir to array used into upload multipl imagesor sin
		       uploadListAdapter.notifyDataSetChanged();


		  }
	      } else if ( data.getData() != null ) {
		  Toast.makeText(getContext(), "selcet single", Toast.LENGTH_SHORT).show();

		  singleUri = data.getData();
		  String fileName = getFileName(singleUri);
		  fileNameList.add(fileName);
		  fileDoneList.add("Uploading");
		  mArrayUri.add(singleUri);
		  uploadListAdapter.notifyDataSetChanged();

	      }

	 }
     }
//
//     private void uploadImage() {
//	 Intent intent = new Intent();
//	 intent.setType("image/*");
//	 intent.setAction(Intent.ACTION_GET_CONTENT);
//	 startActivityForResult(intent, PICK_IMAGE_REQUEST);
//     }

     //     @Override
//     public void onActivityResult(int requestCode, int resultCode, Intent data) {
//	 super.onActivityResult(requestCode, resultCode, data);
//	 if ( requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//	     && data != null && data.getData() != null ) {
//	      mImageUri = data.getData();//something back
//	      Picasso.get().load(mImageUri).fit().into(mImageView);
//	 }
//     }
     public void addInformation() {
	 saveAdBtn.setVisibility(View.INVISIBLE);
	 CancelBtn.setVisibility(View.INVISIBLE);
	 Choose_placeBtn.setVisibility(View.GONE);
	 chooseImages.setVisibility(View.GONE);
	 phone_Number.setVisibility(View.INVISIBLE);
	 mProgressBar.setVisibility(View.VISIBLE);

	 //GET DATE TO BE ADDED TO THE DATABASE
	 calendar = Calendar.getInstance();
	 simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
	 Date = simpleDateFormat.format(calendar.getTime());


	 Ref.collection("Users")
	     .document(UID)
	     .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
	      @Override
	      public void onSuccess(DocumentSnapshot documentSnapshot) {
		  if ( documentSnapshot.contains("paying") ) {
		       String paying = documentSnapshot.getData().get("paying").toString();
		       JSONObject jsonObject = null;
		       try {
			   jsonObject = new JSONObject(paying);
		       } catch (JSONException e) {
			   e.printStackTrace();
		       }
		       try {
			   assert jsonObject != null;
			   boolean isTrue = Boolean.parseBoolean(jsonObject.getString("paid"));
			   if ( isTrue ) {
			        Toast.makeText(getContext(), "انت الان على النظام المجانى", Toast.LENGTH_SHORT).show();
			        //publish your add
			        publishAd();
			   }
		       } catch (JSONException e) {
			   e.printStackTrace();
		       }

		  }
	      }
	 });
     }

     public void publishAd() {
	 //GET DISCRIPTION,PLACES,PHONE NUMBER,NAME
	 final String textDescription = discribetxt.getText().toString().trim();
	 final String Distribution_Places = TV.getText().toString();
	 final String adname = ad_nametxt.getText().toString().trim();
	 //int a = Integer.parseInt(phone_Number.getText().toString());
	 //final long phoneNumbera = (long) a;
	final String phoneNumbera=phone_Number.getText().toString().trim();
	 //this is to capture company name
	 Ref.collection("Users")
	     .document(UID)
	     .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
		  if ( task.isSuccessful() ) {
		       DocumentSnapshot document = task.getResult();
		       if ( document.exists() ) {
			   String companyName = document.getString("companyName");
			   final MyAdCard myAdCard = new MyAdCard(
			       adname,
			       textDescription,
			       Distribution_Places,
			       phoneNumbera,
			       Date,
			       categoyPo,
			       CountryP,
			       myadstring,
			       UID,
			       companyName,
			       "",
			       ""
			   );
			   MyAds.set(myAdCard).addOnSuccessListener(new OnSuccessListener<Void>() {
			        @Override
			        public void onSuccess(Void aVoid) {
				    //store image

				    Toast.makeText(getContext(), "تم التحميل الى صفحتك", Toast.LENGTH_SHORT).show();
				    CategoriesAds
				        .document(MyAds.getId())
				        .set(myAdCard).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
					     if (mArrayUri != null && !mArrayUri.isEmpty()) {

						 storeMultipleImages(mArrayUri);

						 if (getDialog() != null && getDialog().isShowing()) {
						      getDialog().dismiss();
						 }
					     }else {

						 if (getDialog() != null && getDialog().isShowing()) {
						      getDialog().dismiss();
						      mProgressBar.setVisibility(View.GONE);

						 }

					     }
					     //addImagetoFirestorage();
					}
				    }).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {

					     Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
					     onFailureActions();
					}
				    });
			        }
			   });
		       } else {
			   onFailureActions();
		       }

		  } else {
		       Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
		       onFailureActions();
		  }
	      }
	 });

     }

     public void onFailureActions() {
	 saveAdBtn.setVisibility(View.VISIBLE);
	 CancelBtn.setVisibility(View.VISIBLE);
	 Choose_placeBtn.setVisibility(View.VISIBLE);
	 chooseImages.setVisibility(View.VISIBLE);
	 phone_Number.setVisibility(View.VISIBLE);
	 mProgressBar.setVisibility(View.GONE);

     }



     private String getFileExtension(Uri uri) {
	 ContentResolver contentResolver = getContext().getContentResolver();
	 MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
	 return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

     }
     /////////////////////////////////////////////////////////////
     ////////////////////////////////////////////////////////
//     private void removeimages() {
//
//	 StorageReference removeStorage = mStorage.child(UID).child("adspics").child(MyAds.getId());
//	 removeStorage.child(fileName);
//	 StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(fileUri));
//	 reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//	      @Override
//	      public void onSuccess(Void aVoid) {
//		  Toast.makeText(getContext(), "suc", Toast.LENGTH_SHORT).show();
//	      }
//	 });
//     }

//     private void uploadImages() {
//	 Intent intent = new Intent();
//	 intent.setType("image/*");
//	 intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//	 intent.setAction(Intent.ACTION_GET_CONTENT);
//	 startActivityForResult(Intent.createChooser(intent, "select a picture"), RESULT_LOAD_IMAGE);
//     }
//
//     @Override
//     public void onActivityResult(int requestCode, int resultCode, Intent data) {
//	 super.onActivityResult(requestCode, resultCode, data);
//	 if ( requestCode == RESULT_LOAD_IMAGE || resultCode == RESULT_OK ) {
//	      if ( data.getClipData() != null ) {
//		  int totalcount = data.getClipData().getItemCount();
//		  for (int i = 0; i < totalcount; i++) {
//		       fileUri = data.getClipData().getItemAt(i).getUri();
//		       fileName = getFileName(fileUri);
//		       fileNameList.add(fileName);
//		       fileImageList.add(fileUri);
//		       uploadListAdapter.notifyDataSetChanged();
////		       StorageReference store = mStorage.child(UID).child("adspics").child(MyAds.getId());
////		       store.child(fileName).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////			   @Override
////			   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////			        Toast.makeText(getContext(), "succeess", Toast.LENGTH_SHORT).show();
////
////			   }
////		       });
//		  }
//	      }
////	      }else {
////	           fileNameList.remove(fileName);
////	           fileImageList.remove(fileUri);
////	      }
//
//	 }
//     }
//
//     public String getFileName(Uri uri) {
//	 String result = null;
//	 if ( uri.getScheme().equals("content") ) {
//	      Cursor cursor = getContext().getContentResolver()
//		 .query(uri, null, null, null, null);
//	      try {
//		  if ( cursor != null && cursor.moveToFirst() ) {
//		       result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//		  }
//	      } finally {
//		  assert cursor != null;
//		  cursor.close();
//	      }
//	 }
//	 if ( result == null ) {
//	      result = uri.getPath();
//	      int cut = result.lastIndexOf('/');
//	      if ( cut != -1 ) {
//		  result = result.substring(cut + 1);
//	      }
//	 }
//	 return result;
//
//     }


     private void confirmInput() {
	 if ( !validateAdDescription() | !validatePlaces() | !validateAdName() | !validatePhoneNumber() ) {

	      return;//TO END FUNCTION IF OK
	 } else {

	      addInformation();


	 }
     }




     //________________________VALIDATION OF ENTRIES________________________
     private boolean validateAdDescription() {
	 String descriptionInput = discribe.getEditText().getText().toString().trim();

	 if ( descriptionInput.isEmpty() ) {
	      discribe.setError("ماهى تفاصيل الاعلان");
	      return false;
	 } else {
	      discribe.setError(null);
	      return true;
	 }
     }

     public boolean validatePlaces() {
	 String placeTxt = TV.getText().toString();
	 if ( placeTxt.isEmpty() ) {
	      TV.setError("اختر مكان التوزيع");

	      return false;
	 } else {
	      TV.setError(null);
	      return true;
	 }
     }

     public boolean validateAdName() {
	 String addNameTxt = ad_nametxt.getText().toString().trim();
	 if ( addNameTxt.isEmpty() ) {
	      ad_nametxt.setError("اكتب عنوان الاعلان");

	      return false;
	 } else {
	      ad_nametxt.setError(null);
	      return true;
	 }
     }

     public boolean validatePhoneNumber() {

	 String phoneNum = String.valueOf(phone_Number.getText().toString().trim());
	 if ( phoneNum.isEmpty() ) {
	      phone_Number.setError("اكتب رقم التليفون");
	      return false;
	 } else {
	      phone_Number.setError(null);
	      return true;
	 }
     }

     //
//     //______________________________________RECYCLERVIEW_____________________
//     public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
//	 public List<String> fileNameList;
//	 public List<Uri> fileImageList;
//
//	 public UploadListAdapter(List<String> fileNameList, List<Uri> fileImageList) {
//	      this.fileNameList = fileNameList;
//	      this.fileImageList = fileImageList;
//	 }
//
//
//	 @NonNull
//	 @Override
//	 public UploadListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//	      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_createad, parent, false);
//	      return new ViewHolder(view);
//	 }
//
//	 @Override
//	 public void onBindViewHolder(@NonNull UploadListAdapter.ViewHolder holder, int position) {
//
//	      String filename = fileNameList.get(position);
//	      holder.fileNameView.setText(filename);
//	      s = fileImageList.get(position);
////holder.upload_image.getCropToPadding();
//
//	 }
//
//	 @Override
//	 public int getItemCount() {
//	      return fileNameList.size();
//	 }
//
//	 public class ViewHolder extends RecyclerView.ViewHolder {
//	      View mView;
//	      public TextView fileNameView;
//	      public ImageView upload_image;
//
//	      public ViewHolder(View itemView) {
//		  super(itemView);
//		  mView = itemView;
//		  fileNameView = mView.findViewById(R.id.image_name);
//		  upload_image = mView.findViewById(R.id.uploaded_Image);
//		  Picasso.get().load(s).into(upload_image);
//	      }
//	 }
//     }
     @Override
     public void onResume() {
	 // Get existing layout params for the window
	 ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
	 // Assign window properties to fill the parent
	 params.width = WindowManager.LayoutParams.MATCH_PARENT;
	 params.height = WindowManager.LayoutParams.MATCH_PARENT;
	 getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	 // Call super onResume after sizing
	 super.onResume();
     }
}
