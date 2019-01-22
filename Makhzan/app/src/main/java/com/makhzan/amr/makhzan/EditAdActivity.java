package com.makhzan.amr.makhzan;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditAdActivity extends AppCompatActivity {
     //___________IMAGE VARIABLES______________
     private static final int IMAGE_REQUEST = 2;
     private ImageView image_edit;
     private Uri mImageUri;
     //__________________________
     private Button
	saveAdBtnEdit,
	CancelBtnEdit,
	removeImageEdit,
	chooseImagesEdit;
     //___________________PLACES CHECKBOX_____________________
     private TextView TV;
     private Button Choose_placeBtnEdit;

     //_______LIST ITEMS_______________
     String[] listItems;
     boolean[] checkedItems;
     ArrayList<Integer> mUserItems = new ArrayList<>();
     //___________________AD NAME LAYOUT_______________
     private TextInputLayout ad_nameEdit;
     private EditText ad_nametxtEdit;

     //_________________DESCRIPTION LAYOUTS_____________
     private TextInputLayout discribeEdit;
     private EditText discribetxtEdit;
     //__________PHONE NUMBER_______________

     private TextInputLayout Ad_phoneNumberEdit;
     private EditText phone_NumberEdit;

     //___________________FIREBASE_______________________________________

     private FirebaseFirestore Ref;
     String UID;
     private DocumentReference MyAds;
     private CollectionReference CategoriesAds;
     StorageReference mStorage;
     StorageReference Storage;

     private StorageTask storageTask;
     String category_NameID;
     String country_ID;
     //_____________ITEMS OF AD__________
     String ad_date;
     String AdID;
     String imageUrl;

     //_________NEW ITEMS MODIFIED___________
     String textDescription;
     String Distribution_Places;
     String adname;
     long phoneNumbera;
     //_PROGRESS BAR____
     ProgressBar EditAdProgress;
     Map<String, Object> map;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_edit_ad);

	 //____________________STRINGS FROM MYADS ACTIVITY__________________
	 String adname = getIntent().getStringExtra("adname");
	 String addescription = getIntent().getStringExtra("addescription");
	 ad_date = getIntent().getStringExtra("currentdate");
	 AdID = getIntent().getStringExtra("AdPosition_myAd");
	 //____________________
	 EditAdProgress = findViewById(R.id.EditAdProgress);
	 EditAdProgress.setVisibility(View.GONE);
	 //_________IMAGE VIEW________
	 image_edit = findViewById(R.id.image_edit);

	 //____________buttos____________
	 saveAdBtnEdit = findViewById(R.id.saveAdBtn1_edit);
	 CancelBtnEdit = findViewById(R.id.cancelAd_edit);
	 chooseImagesEdit = findViewById(R.id.choose_Images_edit);
	 removeImageEdit = findViewById(R.id.remove_Images_edit);
	 Choose_placeBtnEdit = findViewById(R.id.btnOrder1_edit);

	 //___________________LAYOUTS________________
	 ad_nameEdit = findViewById(R.id.AdName1_edit);
	 ad_nametxtEdit = findViewById(R.id.AdNametxt1_edit);

	 discribeEdit = findViewById(R.id.AdDiscription1_edit);
	 discribetxtEdit = findViewById(R.id.AdDiscriptiontxt1_edit);
	 phone_NumberEdit = findViewById(R.id.phoneNumber1_edit);

	 ad_nametxtEdit.setText(adname);
	 discribetxtEdit.setText(addescription);


	 //________________________GET CURRENT FIREBASE USER_______________________________________
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 Ref = FirebaseFirestore.getInstance();
	 mStorage = FirebaseStorage.getInstance().getReference();


	 catchInfo();


	 //____________CHOOSE IMAGE_____________
	 chooseImagesEdit.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  uploadImageEdit();
	      }
	 });
	 //_________REMOVE IMAGE PERMANENTLY__________________
	 removeImageEdit.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  final AlertDialog.Builder builder=new AlertDialog.Builder(EditAdActivity.this);
		  builder.setTitle("هل تريد حذف الصوره نهائيا؟؟");
		  builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int which) {
			   if (imageUrl!=null ){

			        removeOldPic();
			   image_edit.setImageResource(R.drawable.makhzan);

		       }else if ( mImageUri!=null ){
			        image_edit.setImageResource(R.drawable.makhzan);
			   }else {
			        Toast.makeText(EditAdActivity.this, "لايوجدصوره", Toast.LENGTH_SHORT).show();

			   }
		       }

		  }).setNegativeButton("no", new DialogInterface.OnClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int which) {
			   dialog.dismiss();
		       }
		  });
		  AlertDialog dialog=builder.create();
		  dialog.show();
	      }
	 });
	 //________________________CHECK BOX ITEMS______________________
	 listItems = getResources().getStringArray(R.array.shopingItems);
	 checkedItems = new boolean[listItems.length];

	 TV = findViewById(R.id.tvItemSelected1_edit);

	 Choose_placeBtnEdit.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  AlertDialog.Builder mBuilder =
		      new AlertDialog.Builder(EditAdActivity.this);
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

	 saveAdBtnEdit.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  confirmEdit();

	      }
	 });
	 CancelBtnEdit.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  Intent w = new Intent(EditAdActivity.this, MainActivity.class);
		  startActivity(w);
		  finish();
	      }
	 });
     }

     private void confirmEdit() {
	 if ( !validateAdDescription() | !validatePlaces() | !validateAdName() | !validatePhoneNumber() ) {
	      return;//TO END FUNCTION IF OK
	 } else {
	      savePublishAd();


	 }
     }

     private void savePublishAd() {
	 saveAdBtnEdit.setVisibility(View.INVISIBLE);
	 CancelBtnEdit.setVisibility(View.INVISIBLE);
	 Choose_placeBtnEdit.setVisibility(View.GONE);
	 removeImageEdit.setVisibility(View.GONE);
	 chooseImagesEdit.setVisibility(View.GONE);
	 phone_NumberEdit.setVisibility(View.INVISIBLE);
	 EditAdProgress.setVisibility(View.VISIBLE);
	 //GET DISCRIPTION,PLACES,PHONE NUMBER,NAME
	      update();

//	 saveAdBtnEdit.setVisibility(View.VISIBLE);
//	 CancelBtnEdit.setVisibility(View.VISIBLE);
//	 Choose_placeBtnEdit.setVisibility(View.VISIBLE);
//	 removeImageEdit.setVisibility(View.VISIBLE);
//	 chooseImagesEdit.setVisibility(View.VISIBLE);
//	 phone_NumberEdit.setVisibility(View.VISIBLE);
//	 EditAdProgress.setVisibility(View.INVISIBLE);



     }
     //_________________LOAD IMAGE PART FROM OBILE________________
     private void uploadImageEdit() {
	 Intent intent = new Intent();
	 intent.setType("image/*");
	 intent.setAction(Intent.ACTION_GET_CONTENT);
	 startActivityForResult(intent, IMAGE_REQUEST);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	 if ( requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
	     && data != null && data.getData() != null ) {
	      //___IMAGE URL WHICH I WITHOUT STRINGS NO OWNLOAD URL
	      mImageUri = data.getData();//something back
	      Picasso.get().load(mImageUri).fit().into(image_edit);
	 }
     }
//if the user didnt add a new pictur but just remove the olde one
     public void removeOldPic(){
          removePastPicture();
          deleteImageFromMyAds();
          deleteImageFromTotalAds();
	 deleteImageFromPublicAds();
     }
     //____if the user change the picture by adding new one____
     public void update() {
	 if ( imageUrl == null && mImageUri != null ) {
	      addNewPicture();
	      updateInfo();

	 } else if ( imageUrl == null && mImageUri == null ) {
	      updateInfo();

	 } else if ( imageUrl != null && mImageUri != null ) {
	      removeOldPic();
	      addNewPicture();
	      updateInfo();

	 } else if ( imageUrl != null && mImageUri == null ) {
	      updateInfo();
	 }else {
	      updateInfo();
	 }
     }

     private void addNewPicture() {
	 StorageReference newFileRef = mStorage
	     .child("User_Pictures")
	     .child(UID)
	     .child("Ads_pictures")
	     .child(AdID)//advertisement r
	     .child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
	 storageTask = newFileRef
	     .putFile(mImageUri)
	     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
		 @Override
		 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
		      //get download url
		      Uri downloadUri = taskSnapshot.getDownloadUrl();
		      assert downloadUri != null;
		      Toast.makeText(EditAdActivity.this, "there is image ", Toast.LENGTH_SHORT).show();
		      textDescription = discribetxtEdit.getText().toString().trim();
		      Distribution_Places = TV.getText().toString();
		      adname = ad_nametxtEdit.getText().toString().trim();
		      int a = Integer.parseInt(phone_NumberEdit.getText().toString());
		      phoneNumbera = (long) a;

		      //CREATE A NEW MAP BY THE NEW INFO
		      map = new HashMap<>();
		      map.put("myAdNametxt", adname);
		      map.put("myAddiscriptiontxt", textDescription);
		      map.put("myPlacestxt", Distribution_Places);
		      map.put("phoneNumbera", phoneNumbera);
		      map.put("imageUrl", downloadUri.toString());//convert url to string

		      //CHANGE DIRECTORY AND SET MAP: TO MY ADS
		      Ref.collection("Users")
			 .document(UID)
			 .collection("MyAds")
			 .document(AdID)
			 .set(map, SetOptions.merge())//because it contains another infos
			 .addOnSuccessListener(new OnSuccessListener<Void>() {
			      @Override
			      public void onSuccess(Void aVoid) {
				   /*
				   1-get directory of the ad inside the categoryand country
				   2-set map with merg
				   */
				  //SET MAP: TO MAIN ADS
				  Ref.collection("MainCategories").document(category_NameID)
				      .collection("Countries")
				      .document(country_ID)
				      .collection("Ads")
				      .document(AdID).set(map, SetOptions.merge());

				  Ref.collection("System")
				      .document(UID)
				      .collection("ProfileAds")
				      .document(AdID).set(map,SetOptions.merge());

				  Toast.makeText(EditAdActivity.this, "تم التغيير", Toast.LENGTH_SHORT).show();
				  EditAdProgress.setVisibility(View.INVISIBLE);
				  Intent intent = new Intent(EditAdActivity.this, MainActivity.class);
				  startActivity(intent);
				  finish();

			      }
			 }).addOnFailureListener(new OnFailureListener() {
			  @Override
			  public void onFailure(@NonNull Exception e) {

			  }
		      });
		 }
	     });
     }


     private void updateInfo() {
	 textDescription = discribetxtEdit.getText().toString().trim();
	 Distribution_Places = TV.getText().toString();
	 adname = ad_nametxtEdit.getText().toString().trim();
	 int a = Integer.parseInt(phone_NumberEdit.getText().toString());
	 phoneNumbera = (long) a;
	 final Map<String, Object> newmap = new HashMap<>();
	 newmap.put("myAdNametxt", adname);
	 newmap.put("myAddiscriptiontxt", textDescription);
	 newmap.put("myPlacestxt", Distribution_Places);
	 newmap.put("phoneNumbera", phoneNumbera);
	 //CHANGE DIRECTORY AND SET MAP: TO MY ADS
	 Ref.collection("Users")
	     .document(UID)
	     .collection("MyAds")
	     .document(AdID)
	     .set(newmap, SetOptions.merge())//because it contains another infos
	     .addOnSuccessListener(new OnSuccessListener<Void>() {
		 @Override
		 public void onSuccess(Void aVoid) {
				   /*
				   1-get directory of the ad inside the categoryand country
				   2-set map with merg
				   */
		      //SET MAP: TO MAIN ADS
		      Ref.collection("MainCategories")
			 .document(category_NameID)
			 .collection("Countries")
			 .document(country_ID)
			 .collection("Ads")
			 .document(AdID)
			 .set(newmap, SetOptions.merge());

		      Ref.collection("System")
			 .document(UID)
			 .collection("ProfileAds")
			 .document(AdID)
			 .set(newmap,SetOptions.merge());

		      Toast.makeText(EditAdActivity.this, "تم التغيير", Toast.LENGTH_SHORT).show();
		      EditAdProgress.setVisibility(View.INVISIBLE);
		      Intent intent = new Intent(EditAdActivity.this, MainActivity.class);
		      startActivity(intent);
		      finish();
		 }
	     }).addOnFailureListener(new OnFailureListener() {
	      @Override
	      public void onFailure(@NonNull Exception e) {

	      }

	 });
     }

     //___________REMOVE PICTURE FROM STORAGE______
     public void removePastPicture() {
          /*
          get old image url
          delete it
          */
	 Storage = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
	 Storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
	      @Override
	      public void onSuccess(Void aVoid) {
		  Toast.makeText(EditAdActivity.this, "old image is deleted", Toast.LENGTH_SHORT).show();
	      }
	 });
     }

     //get file extention
     private String getFileExtension(Uri uri) {
	 ContentResolver contentResolver = getContentResolver();
	 MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
	 return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

     }
     public void deleteImageFromPublicAds(){
          Ref.collection("System")
	     .document(UID)
	     .collection("ProfileAds")
	     .document(AdID)
	     .update("imageUrl",FieldValue.delete());
     }
     public void deleteImageFromMyAds(){
	 Ref.collection("Users").document(UID)
	     .collection("MyAds")
	     .document(AdID)
	     .update("imageUrl", FieldValue.delete());
     }
     public void deleteImageFromTotalAds(){
	 Ref.collection("MainCategories")
	     .document(category_NameID)
	     .collection("Countries")
	     .document(country_ID)
	     .collection("Ads")
	     .document(AdID).update("imageUrl",FieldValue.delete());

     }
     //catching occurs at the beginig of the app to show results
     public void catchInfo() {

          /*
	1-get reference
	2-put image inside image_edit
	*/
	 Ref.collection("Users")
	     .document(UID)
	     .collection("MyAds")
	     .document(AdID)
	     .get()
	     .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
		 @Override
		 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
		      if ( task.isSuccessful() ) {
			  DocumentSnapshot documentSnapshot = task.getResult();
			  if ( documentSnapshot.exists() ) {
			       category_NameID = documentSnapshot.getString("category_NameID");
			       country_ID = documentSnapshot.getString("country_ID");
			       imageUrl = documentSnapshot.getString("imageUrl");
				if ( imageUrl!=null ) {
				     //___________SET IMAGE WITH PICASSO__________
				     Picasso.get()
					.load(imageUrl)
					.fit()
					.into(image_edit);
				}else {
				     Toast.makeText(EditAdActivity.this, "no pictures", Toast.LENGTH_SHORT).show();

				}
			  } else {
			       Toast.makeText(EditAdActivity.this, "no pictures", Toast.LENGTH_SHORT).show();
			  }
		      } else {
			  Toast.makeText(EditAdActivity.this, "task is not successful", Toast.LENGTH_SHORT).show();

		      }
		 }
	     });
     }

     private boolean validatePhoneNumber() {

	 String phoneNum = String.valueOf(phone_NumberEdit.getText().toString().trim());

	 if ( phoneNum.isEmpty() ) {

	      phone_NumberEdit.setError("اكتب رقم التليفون");

	      return false;
	 } else {

	      phone_NumberEdit.setError(null);

	      return true;
	 }
     }


     private boolean validateAdName() {
	 String addNameTxt = ad_nametxtEdit.getText().toString().trim();
	 if ( addNameTxt.isEmpty() ) {
	      ad_nametxtEdit.setError("اكتب عنوان الاعلان");
	      return false;
	 } else {
	      ad_nametxtEdit.setError(null);
	      return true;
	 }
     }


     private boolean validatePlaces() {
	 String placeTxt = TV.getText().toString();
	 if ( placeTxt.isEmpty() ) {
	      TV.setError("اختر مكان التوزيع");
	      return false;
	 } else {
	      TV.setError(null);
	      return true;
	 }
     }

     private boolean validateAdDescription() {
	 String descriptionInput = discribeEdit.getEditText().getText().toString().trim();
	 if ( descriptionInput.isEmpty() ) {
	      discribeEdit.setError("ماهى تفاصيل الاعلان");
	      return false;
	 } else {
	      discribeEdit.setError(null);
	      return true;
	 }
     }


}
