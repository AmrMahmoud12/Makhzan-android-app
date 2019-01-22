package com.makhzan.amr.makhzan;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import com.makhzan.amr.makhzan.items.MyAdCard;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateSpecialAdFragment extends DialogFragment {

     public static final String TAG = "MySpecialDialog";
     private static final int RESULT_LOAD_IMAGE = 2;
     private static final int PICK_IMAGE = 2;
     private Button
	saveAdBtn,
	CancelBtn,
	removeImage,
	chooseImages;
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

//______________progress bar_____
     ProgressBar mProgressBar;
     public CreateSpecialAdFragment() {
	 // Required empty public constructor
     }

     @SuppressLint("ClickableViewAccessibility")

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
			   Bundle savedInstanceState) {
	 // Inflate the layout for this fragment
	 // Inflate the layout for this fragment
	 final View view = inflater.inflate(R.layout.fragment_create_special_ad, container, false);
	 //____________buttos____________
	 saveAdBtn = view.findViewById(R.id.saveAdBtn1S);
	 CancelBtn = view.findViewById(R.id.cancelAdS);
	 chooseImages = view.findViewById(R.id.choose_ImagesS);
	 removeImage = view.findViewById(R.id.remove_ImagesS);
	 Choose_placeBtn = view.findViewById(R.id.btnOrder1S);

	 //___________________LAYOUTS________________
	 ad_name = view.findViewById(R.id.AdName1S);
	 ad_nametxt = view.findViewById(R.id.AdNametxt1S);

	 discribe = view.findViewById(R.id.AdDiscription1S);
	 discribetxt = view.findViewById(R.id.AdDiscriptiontxt1S);
	 phone_Number = view.findViewById(R.id.phoneNumber1S);
	 //_______progress bar____
	 mProgressBar = view.findViewById(R.id.CreatSpecialAdProgress);
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



	 //_____________CHOOSE IMAGES BUTTON_______________
	 mImageView = view.findViewById(R.id.image);

	 chooseImages.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  uploadImage();

	      }
	 });
	 removeImage.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	   	mImageView.setImageResource(R.drawable.makhzan);
	         }
	 });


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
	 TV = view.findViewById(R.id.tvItemSelected1S);
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
		  getDialog().dismiss();
		  /*
		  object:
		  no capitals, acurate strings with no spaces

		  */

	      }
	 });


	 return view;


     }


     private void uploadImage() {
	 Intent intent = new Intent();
	 intent.setType("image/*");
	 intent.setAction(Intent.ACTION_GET_CONTENT);
	 startActivityForResult(intent, PICK_IMAGE);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	 if ( requestCode == PICK_IMAGE && resultCode == RESULT_OK
	     && data != null && data.getData() != null ) {
	      mImageUri = data.getData();//something back
	      Picasso.get().load(mImageUri).fit().into(mImageView);
	 }
     }

     public void addImagetoFirestorage() {
	 if ( mImageUri != null ) {

	      StorageReference fileRef =
		 mStorage
		     .child("User_Pictures")
		     .child(UID).child("Ads_pictures")
		     .child(MyAds.getId())
		     .child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
	      storageTask = fileRef.putFile(mImageUri)
		 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
		      @Override
		      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
			  Toast.makeText(getContext(), "Uploading", Toast.LENGTH_SHORT).show();
			  downloadUri = taskSnapshot.getDownloadUrl();
			  assert downloadUri != null;
			  //upload a picture to the database by url
			  final Map<String,Object> map=new HashMap<>();
			  map.put("imageUrl",downloadUri.toString());
			  Ref.collection("MainCategories")
			      .document(categoyPo)
			      .collection("Countries")
			      .document(CountryP)
			      .collection("Ads")
			      .document(myadstring).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
			       @Override
			       public void onSuccess(Void aVoid) {

				   //_____AD IMAGE URL TO MY ADS
				   Ref.collection("Users")
				       .document(UID)
				       .collection("MyAds")
				       .document(myadstring).set(map,SetOptions.merge());

				   mProgressBar.setVisibility(View.GONE);

				   getDialog().dismiss();
			       }
			  }).addOnFailureListener(new OnFailureListener() {
			       @Override
			       public void onFailure(@NonNull Exception e) {
				   Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();


			       }
			  });


		      }

		 })
		 .addOnFailureListener(new OnFailureListener() {
		      @Override
		      public void onFailure(@NonNull Exception e) {
			  mProgressBar.setVisibility(View.GONE);
			  saveAdBtn.setVisibility(View.VISIBLE);
			  CancelBtn.setVisibility(View.VISIBLE);
			  Choose_placeBtn.setVisibility(View.VISIBLE);
			  removeImage.setVisibility(View.VISIBLE);
			  chooseImages.setVisibility(View.VISIBLE);
			  phone_Number.setVisibility(View.VISIBLE);
			  Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

		      }
		 });


	 } else {
	      Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
	      mProgressBar.setVisibility(View.GONE);
	      getDialog().dismiss();
	 }
     }

     private String getFileExtension(Uri uri) {
	 ContentResolver contentResolver = getContext().getContentResolver();
	 MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
	 return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

     }


     private void confirmInput() {
	 if ( !validateAdDescription() | !validatePlaces() | !validateAdName() | !validatePhoneNumber() ) {

	      return;//TO END FUNCTION IF OK
	 } else {

	      savePublishAd();



	 }
     }

     private void savePublishAd() {
	 saveAdBtn.setVisibility(View.INVISIBLE);
	 CancelBtn.setVisibility(View.INVISIBLE);
	 Choose_placeBtn.setVisibility(View.GONE);
	 removeImage.setVisibility(View.GONE);
	 chooseImages.setVisibility(View.GONE);
	 phone_Number.setVisibility(View.INVISIBLE);
	 mProgressBar.setVisibility(View.VISIBLE);
	 //GET DATE TO BE ADDED TO THE DATABASE
	 calendar = Calendar.getInstance();
	 simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
	 Date = simpleDateFormat.format(calendar.getTime());
	 //GET DISCRIPTION,PLACES,PHONE NUMBER,NAME
	 final String textDescription = discribetxt.getText().toString().trim();
	 final String Distribution_Places = TV.getText().toString();
	 final String adname = ad_nametxt.getText().toString().trim();
//	 int a = Integer.parseInt(phone_Number.getText().toString());
//	 final long phoneNumbera = (long) a;
	final String phoneNumbera=phone_Number.getText().toString();
	/*
	get object from firestore to check payment status if paid it will make it as special add if not
	add as normal add
	-positioning it of the top is done by adding the end date
	*/

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
			       final String z = String.valueOf(jsonObject.getString("date"));
			       final String trustedCompanyTxt = String.valueOf(jsonObject.getString("txt"));
			       Toast.makeText(getContext(), z, Toast.LENGTH_SHORT).show();

			         Ref.collection("Users")
				  .document(UID).get()
				    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
						    z, categoyPo, CountryP, myadstring, UID, "", companyName, "", trustedCompanyTxt);


						MyAds.set(myAdCard)
						    .addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void aVoid) {
							     Toast.makeText(getContext(), "proceeding", Toast.LENGTH_SHORT).show();

							     CategoriesAds
								.document(MyAds.getId())
								.set(myAdCard)
								.addOnSuccessListener(new OnSuccessListener<Void>() {
								     @Override
								     public void onSuccess(Void aVoid) {
									 //Toast.makeText(getContext(), categoyPo + CountryP, Toast.LENGTH_SHORT).show();
						/*
						ADD IMAGES TO THE FIRSTORAGE
						IT IS ADDED BY ITS ID OF THE AD
						*/
									 addImagetoFirestorage();
								     }
								}).addOnFailureListener(new OnFailureListener() {
								 @Override
								 public void onFailure(@NonNull Exception e) {
								      Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
								      saveAdBtn.setVisibility(View.VISIBLE);
								      CancelBtn.setVisibility(View.VISIBLE);
								      Choose_placeBtn.setVisibility(View.VISIBLE);
								      removeImage.setVisibility(View.VISIBLE);
								      chooseImages.setVisibility(View.VISIBLE);
								      phone_Number.setVisibility(View.VISIBLE);

								 }
							     });

							}
						    }).addOnFailureListener(new OnFailureListener() {
						     @Override
						     public void onFailure(@NonNull Exception e) {
							 Toast.makeText(getContext(), "حدث حطأ ما ", Toast.LENGTH_SHORT).show();
							 saveAdBtn.setVisibility(View.VISIBLE);
							 CancelBtn.setVisibility(View.VISIBLE);
							 Choose_placeBtn.setVisibility(View.VISIBLE);
							 removeImage.setVisibility(View.VISIBLE);
							 chooseImages.setVisibility(View.VISIBLE);
							 phone_Number.setVisibility(View.VISIBLE);

						     }
						});

					    } else {
						Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
						saveAdBtn.setVisibility(View.VISIBLE);
						CancelBtn.setVisibility(View.VISIBLE);
						Choose_placeBtn.setVisibility(View.VISIBLE);
						removeImage.setVisibility(View.VISIBLE);
						chooseImages.setVisibility(View.VISIBLE);
						phone_Number.setVisibility(View.VISIBLE);
						mProgressBar.setVisibility(View.GONE);
					    }

				        } else {
					    Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
					    saveAdBtn.setVisibility(View.VISIBLE);
					    CancelBtn.setVisibility(View.VISIBLE);
					    Choose_placeBtn.setVisibility(View.VISIBLE);
					    removeImage.setVisibility(View.VISIBLE);
					    chooseImages.setVisibility(View.VISIBLE);
					    phone_Number.setVisibility(View.VISIBLE);
					    mProgressBar.setVisibility(View.GONE);


				        }

				   }
			       });
			  } else {

			  }

		      } catch (JSONException e) {
			  e.printStackTrace();
		      }
		 }else {
		      Toast.makeText(getContext(), "no paying", Toast.LENGTH_SHORT).show();
		 }

		 }
	});
//	 Ref.collection("Users").document(UID)
//	     .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//	      @Override
//	      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//		  if ( task.isSuccessful() ){
//
//		       DocumentSnapshot documentSnapshot=task.getResult();
//
//		       if ( documentSnapshot.exists() ) {
//			   if ( documentSnapshot.contains("paying") ){
//			        String paying = documentSnapshot.getData().get("paying").toString();
//
//			   JSONObject jsonObject = null;
//			   try {
//			        jsonObject = new JSONObject(paying);
//			   } catch (JSONException e) {
//			        e.printStackTrace();
//			   }
//			   try {
//			        boolean namee = Boolean.parseBoolean(jsonObject.getString("paid"));
//
//			        if ( namee ) {
//				    final String z = String.valueOf(jsonObject.getString("date"));
//				    final String trustedCompanyTxt = String.valueOf(jsonObject.getString("txt"));
//				    Toast.makeText(getContext(), z, Toast.LENGTH_SHORT).show();
//				    Ref.collection("Users")
//				        .document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//					@Override
//					public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//					     if ( task.isSuccessful() ) {
//						 DocumentSnapshot document = task.getResult();
//						 if ( document.exists() ) {
//						      String companyName = document.getString("companyName");
//						      final MyAdCard myAdCard = new MyAdCard(
//							 adname,
//							 textDescription,
//							 Distribution_Places,
//							 phoneNumbera,
//							 z, categoyPo, CountryP, myadstring, companyName, "", trustedCompanyTxt);
//
//
//						      MyAds.set(myAdCard)
//							 .addOnSuccessListener(new OnSuccessListener<Void>() {
//							      @Override
//							      public void onSuccess(Void aVoid) {
//								  Toast.makeText(getContext(), "proceeding", Toast.LENGTH_SHORT).show();
//
//								  CategoriesAds
//								      .document(MyAds.getId())
//								      .set(myAdCard)
//								      .addOnSuccessListener(new OnSuccessListener<Void>() {
//									  @Override
//									  public void onSuccess(Void aVoid) {
//									       //Toast.makeText(getContext(), categoyPo + CountryP, Toast.LENGTH_SHORT).show();
//						/*
//						ADD IMAGES TO THE FIRSTORAGE
//						IT IS ADDED BY ITS ID OF THE AD
//						*/
//									       addImagetoFirestorage();
//									  }
//								      }).addOnFailureListener(new OnFailureListener() {
//								       @Override
//								       public void onFailure(@NonNull Exception e) {
//									   Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
//									   saveAdBtn.setVisibility(View.VISIBLE);
//									   CancelBtn.setVisibility(View.VISIBLE);
//									   Choose_placeBtn.setVisibility(View.VISIBLE);
//									   removeImage.setVisibility(View.VISIBLE);
//									   chooseImages.setVisibility(View.VISIBLE);
//									   phone_Number.setVisibility(View.VISIBLE);
//
//								       }
//								  });
//
//							      }
//							 }).addOnFailureListener(new OnFailureListener() {
//							  @Override
//							  public void onFailure(@NonNull Exception e) {
//							       Toast.makeText(getContext(), "حدث حطأ ما ", Toast.LENGTH_SHORT).show();
//							       saveAdBtn.setVisibility(View.VISIBLE);
//							       CancelBtn.setVisibility(View.VISIBLE);
//							       Choose_placeBtn.setVisibility(View.VISIBLE);
//							       removeImage.setVisibility(View.VISIBLE);
//							       chooseImages.setVisibility(View.VISIBLE);
//							       phone_Number.setVisibility(View.VISIBLE);
//
//							  }
//						      });
//
//						 } else {
//						      Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
//						      saveAdBtn.setVisibility(View.VISIBLE);
//						      CancelBtn.setVisibility(View.VISIBLE);
//						      Choose_placeBtn.setVisibility(View.VISIBLE);
//						      removeImage.setVisibility(View.VISIBLE);
//						      chooseImages.setVisibility(View.VISIBLE);
//						      phone_Number.setVisibility(View.VISIBLE);
//						      mProgressBar.setVisibility(View.GONE);
//						 }
//
//					     } else {
//						 Toast.makeText(getContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
//						 saveAdBtn.setVisibility(View.VISIBLE);
//						 CancelBtn.setVisibility(View.VISIBLE);
//						 Choose_placeBtn.setVisibility(View.VISIBLE);
//						 removeImage.setVisibility(View.VISIBLE);
//						 chooseImages.setVisibility(View.VISIBLE);
//						 phone_Number.setVisibility(View.VISIBLE);
//						 mProgressBar.setVisibility(View.GONE);
//
//
//					     }
//
//					}
//				    });
//			        } else {
//
//			        }
//
//			   } catch (JSONException e) {
//			        e.printStackTrace();
//			   }
//		       }else {
//			        Toast.makeText(getContext(), "no", Toast.LENGTH_SHORT).show();
//		       }
//			   } else {
//
//			   }
//
//		  }else {
//
//		  }
//	      }
//	 });


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

}
