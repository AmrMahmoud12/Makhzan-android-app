package com.makhzan.amr.makhzan;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.makhzan.amr.makhzan.Adapter.CardImagesAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.internal.operators.parallel.ParallelFromPublisher;

public class CreateShoppingCartActivity extends AppCompatActivity {
     private static final int RESULT_LOAD_IMAGE = 4;
     private RecyclerView mUploadList;
     private List<String> fileNameList;
     private List<String> fileDoneList;
     CardImagesAdapter cardImagesAdapter;
     private Uri fileUri;//for the picture multiple
     private Uri singleUri;//for single picture
     int totalItemSelected;
     ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
     int i; //that for looping


     //Layouts
     private TextInputLayout cartNamelayout;
     private TextInputLayout cartPricelayout;
     //Buttons
     private Button B;
     //textviews
     private TextView size_pieces_TxtView;
     private EditText s_num, m_num, l_num, xl_num, xxl_num;
	private ImageButton clickAddImages;

     //___________________FIREBASE_______________________________________

     private FirebaseFirestore Ref;
     String UID;
     private DocumentReference MyCarts;
     private CollectionReference CategoriesAds;
     StorageReference mStorage;
     private StorageTask storageTask;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_create_shopping_cart);
	 //________________________GET CURRENT FIREBASE USER_______________________________________
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUser != null;
	 UID = currentUser.getUid();
	 MyCarts = Ref.collection("Users")
	     .document(UID)
	     .collection("MyCarts").document();
	String myadstring = MyCarts.getId();

	 mStorage = FirebaseStorage.getInstance().getReference();
	 //___________recycle pictures_________________
	 mUploadList = findViewById(R.id.shopCartImagedRV);
	 fileNameList = new ArrayList<>();
	 fileDoneList = new ArrayList<>();
	 cardImagesAdapter = new CardImagesAdapter(fileNameList, fileDoneList);
	 mUploadList.setLayoutManager(new LinearLayoutManager(CreateShoppingCartActivity.this));
	 mUploadList.setAdapter(cardImagesAdapter);
	 //1-adding back button to toolbar : 1A,2A
	 showBackButton();
	 viewItems();
	 B.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  confirmInput();
	      }
	 });
	 clickAddImages.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {//
		  //if user want to change his picture immediately
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
     }
     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	 if ( requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK ) {
	      if ( data.getClipData() != null ) {
		  Toast.makeText(CreateShoppingCartActivity.this, "selcet multiple", Toast.LENGTH_SHORT).show();
		  totalItemSelected = data.getClipData().getItemCount();
		  for (i = 0; i < totalItemSelected; i++) {
		       fileUri = data.getClipData().getItemAt(i).getUri();
		       String fileName = getFileName(fileUri);
		       fileNameList.add(fileName);
		       fileDoneList.add("Uploading");
		       mArrayUri.add(fileUri);//add uir to array used into upload multipl imagesor sin
		       cardImagesAdapter.notifyDataSetChanged();


		  }
	      } else if ( data.getData() != null ) {
		  Toast.makeText(CreateShoppingCartActivity.this, "selcet single", Toast.LENGTH_SHORT).show();

		  singleUri = data.getData();
		  String fileName = getFileName(singleUri);
		  fileNameList.add(fileName);
		  fileDoneList.add("Uploading");
		  mArrayUri.add(singleUri);
		  cardImagesAdapter.notifyDataSetChanged();

	      }

	 }
     }

     public String getFileName(Uri uri) {
	 String result = null;
	 if ( uri.getScheme().equals("content") ) {
	      Cursor cursor = this.getContentResolver()
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
     private void viewItems() {
	 cartNamelayout = findViewById(R.id.shoppingCartNamelayout);
	 cartPricelayout = findViewById(R.id.shoppingCartpricelayout);
	 s_num = findViewById(R.id.s_num);
	 m_num = findViewById(R.id.m_num);
	 l_num = findViewById(R.id.l_num);
	 xl_num = findViewById(R.id.xl_num);
	 xxl_num = findViewById(R.id.xxl_num);
	 B = findViewById(R.id.submitCardBtn);
     clickAddImages=findViewById(R.id.choose_Images);
     }

     private boolean validateCartName() {
	 String nameInput = cartNamelayout.getEditText().getText().toString().trim();

	 if ( nameInput.isEmpty() ) {
	      cartNamelayout.setError("Enter item name");
	      return false;
	 } else {
	      cartNamelayout.setError(null);
	      return true;
	 }
     }

     private boolean validateCartPrice() {
	 String priceInput = cartPricelayout.getEditText().getText().toString().trim();

	 if ( priceInput.isEmpty() ) {
	      cartPricelayout.setError("Enter item price");
	      return false;
	 } else {
	      cartPricelayout.setError(null);
	      return true;
	 }
     }

     public boolean validateSmall() {

	 String sNum = String.valueOf(s_num.getText().toString().trim());
	 if ( sNum.isEmpty() ) {

	      s_num.setError("Enter 0 if no Items");
	      return false;
	 } else {
	      s_num.setError(null);
	      return true;
	 }


     }

     public Boolean validateMedium() {
	 String mNum = String.valueOf(m_num.getText().toString().trim());
	 if ( mNum.isEmpty() ) {

	      m_num.setError("Enter 0 if no Items");
	      return false;
	 } else {
	      m_num.setError(null);
	      return true;
	 }

     }

     public Boolean validateLarge() {
	 String lNum = String.valueOf(l_num.getText().toString().trim());
	 if ( lNum.isEmpty() ) {

	      l_num.setError("Enter 0 if no Items");
	      return false;
	 } else {
	      l_num.setError(null);
	      return true;
	 }

     }

     public Boolean validatexLarge() {
	 String xlNum = String.valueOf(xl_num.getText().toString().trim());
	 if ( xlNum.isEmpty() ) {

	      xl_num.setError("Enter 0 if no Items");
	      return false;
	 } else {
	      xl_num.setError(null);
	      return true;
	 }

     }

     public Boolean validateXxlarge() {
	 String xxlNum = String.valueOf(xxl_num.getText().toString().trim());
	 if ( xxlNum.isEmpty() ) {

	      xxl_num.setError("Enter 0 if no Items");
	      return false;
	 } else {
	      xxl_num.setError(null);
	      return true;
	 }

     }

     public void confirmInput() {
	 if ( !validateCartName() |
	     !validateCartPrice() |
	     !validateSmall() |
	     !validateMedium() |
	     !validateLarge() |
	     !validatexLarge() | !validateXxlarge() ) {
	      Toast.makeText(this, "hhhh", Toast.LENGTH_SHORT).show();
	      return;

	 }else {
	      addInfo();
	 }
	 //continue information

     }

     private void addInfo() {

     }
     public void storeMultipleImages(List<Uri> imageUris) {
	 for (Uri uri : imageUris) {
	      storeImage(uri);
	 }
     }
     public void storeImage(Uri fileUri) {
	 StorageReference filepath = mStorage
	     .child("User_Pictures")
	     .child(UID)
	     .child("Carts_pictures")
	     .child(MyCarts.getId())
	     .child(System.currentTimeMillis() + "." + getFileExtension(fileUri));

	 filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

		  if ( task.isSuccessful() ) {


		  } else {

		  }

	      }
	 });

     }
     private String getFileExtension(Uri uri) {
	 ContentResolver contentResolver = this.getContentResolver();
	 MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
	 return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

     }
     //1A
     public void showBackButton() {
	 Toolbar toolbar = findViewById(R.id.include_create_shoppigcart);//note: id of include not toolbar
	 setSupportActionBar(toolbar);
	 if ( getSupportActionBar() != null ) {
	      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	      getSupportActionBar().setDisplayShowHomeEnabled(true);
	 }
     }

     //2A
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
	 if ( item.getItemId() == android.R.id.home ) {
	      finish();
	 }
	 return super.onOptionsItemSelected(item);
     }
}
