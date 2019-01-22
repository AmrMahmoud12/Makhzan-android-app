package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.makhzan.amr.makhzan.items.profileCo;

import java.util.HashMap;
import java.util.Map;

public class edit_ProfileActivity extends AppCompatActivity {

     HashMap<String, Object> paying;
     //______UPLOAD IMAGE________
     private static final int GALLERY_INTENT = 2;
     private StorageReference storageReference;
     private Button change_pic;
     //_____RETRIEVE IMAGE________
     private Button retrieve_pic;
     private ImageView imageView;
     Uri mImageUri;
     private StorageTask storageTask;

     //___________Firebase_______________
     DocumentReference addInfo;
     FirebaseAuth firebaseAuth;

     FirebaseAuth.AuthStateListener mAuthStateListener;
     //______TEXT INPUT VARIABLES___________
     private TextInputLayout text_input_CoName, text_input_address, text_input_PersonalName;
     private EditText nametxt, addresstxt, personalName,phone_Number;
     //_________________________
     private Button saveInputs, cancelInputs;
     private String activitytype_txt;
     //_______________PROGRESS BAR_____________
     ProgressBar ProfileProgress;
     private GoogleSignInClient mGoogleSignInClient;


     //=============
     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_edit_profile);
//_______________define progress bar____
	 ProfileProgress = findViewById(R.id.ProfileProgress);
	 ProfileProgress.setVisibility(View.GONE);

//________________________GET CURRENT FIREBASE USER_______________________________________
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 assert currentUser != null;
	 String x = currentUser.getUid();
	 addInfo = FirebaseFirestore.getInstance().collection("Users").document(x);
	 // Configure Google Sign In//=====================1===================
	 GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
	     .requestIdToken(getString(R.string.default_web_client_id))
	     .requestEmail()
	     .build();
	 mGoogleSignInClient = GoogleSignIn.getClient(edit_ProfileActivity.this, gso);

//______________________TEXTINPUTLAYOUTS___________________________________________________
	 text_input_CoName = (TextInputLayout) findViewById(R.id.text_input_CoName);
	 //text_input_WorkersNum = (TextInputLayout) findViewById(R.id.text_input_WorkersNum);
	 text_input_address = (TextInputLayout) findViewById(R.id.text_input_address);
	 text_input_PersonalName = findViewById(R.id.text_input_PersonaName);

	 personalName = findViewById(R.id.PersonalNameTxt);
	 nametxt = findViewById(R.id.text_input_CoNametxt);
	 addresstxt = findViewById(R.id.addresstxt);
	 phone_Number=findViewById(R.id.companyPhone);
	 //__________________________________________SAVEINPUT____________________________________
	 saveInputs = findViewById(R.id.saveInfo);
	 saveInputs.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  confirmInput();
		  Toast.makeText(edit_ProfileActivity.this, "completed", Toast.LENGTH_SHORT).show();

	      }
	 });
//________________________________________CANCEL iNPUTS__________________________________
	 cancelInputs = findViewById(R.id.cancelInfo);
	 cancelInputs.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		 // alert();
	      }
	 });

//_____________________________________________SPINNER___________________________________
	 ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter
	     .createFromResource(this, R.array.activity, android.R.layout.simple_spinner_item);
	 arrayAdapter

	     .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


     }

     @Override
     public void onBackPressed() {
	 super.onBackPressed();
//	 FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//	 firebaseAuth.signOut();
//	 mGoogleSignInClient.signOut()
//	     .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//		 @Override
//		 public void onComplete(@NonNull Task<Void> task) {
//		     // Main_Progress.setVisibility(View.GONE);
//		      Toast.makeText(edit_ProfileActivity.this, "You are logged out", Toast.LENGTH_LONG).show();
//
//		 }
//	     });
     }

     //_____________________________WRITE COMPANY INFO TO THE DATABASE_________________________
     public void addInfo() {
	 ProfileProgress.setVisibility(View.VISIBLE);
	 saveInputs.setVisibility(View.GONE);
	 cancelInputs.setVisibility(View.GONE);
	 String names = nametxt.getText().toString().trim();
	 String address = addresstxt.getText().toString().trim();
	 String personal_Name = personalName.getText().toString().trim();
	 int a = Integer.parseInt(phone_Number.getText().toString());
	 final long phoneNumber = (long) a;
	 profileCo profileCo = new profileCo(names, address, activitytype_txt, personal_Name, phoneNumber);

	 addInfo.set(profileCo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
	      @Override
	      public void onSuccess(Void aVoid) {

//		  SharedPreferences sharedPref =getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
//		  SharedPreferences.Editor editor = sharedPref.edit();
//		  //editor.putString("userId", result.get("uid"));
//		  editor.putBoolean("profileInfo",true); //this line will do trick
//		  editor.apply();
		  Map<String,Object> map=new HashMap<>();
		  map.put("profileinfo",true);
		  addInfo.set(map,SetOptions.merge());

		  Intent q = new Intent(edit_ProfileActivity.this, MainActivity.class);
		  startActivity(q);
		  finish();
		  ProfileProgress.setVisibility(View.GONE);

	      }
	 }).addOnFailureListener(new OnFailureListener() {
	      @Override
	      public void onFailure(@NonNull Exception e) {
		  Toast.makeText(edit_ProfileActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
		  saveInputs.setVisibility(View.VISIBLE);
		  cancelInputs.setVisibility(View.VISIBLE);
	      }
	 });


     }

//     public void alert() {
//	 AlertDialog.Builder alert = new AlertDialog.Builder(this);
//	 alert.setTitle("هل تريد الغاء اضافه بياناتك؟سيتم اضافة بيانات افتراضيه");
//	 alert.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
//	      @Override
//	      public void onClick(DialogInterface dialog, int which) {
//		  addDefaultInfo();
//	      }
//	 });
//	 alert.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
//	      @Override
//	      public void onClick(DialogInterface dialog, int which) {
//		  dialog.dismiss();
//	      }
//	 });
//	 Dialog dialog = alert.create();
//	 dialog.show();
//
//     }

//     public void addDefaultInfo() {
//	 ProfileProgress.setVisibility(View.VISIBLE);
//	 saveInputs.setVisibility(View.GONE);
//	 cancelInputs.setVisibility(View.GONE);
//	 profileCo profileCo = new profileCo(
//	     "شركة وهميه", "لا يوجد عنوان", "", "0000  ", companyPhone);
//	 addInfo.set(profileCo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//	      @Override
//	      public void onSuccess(Void aVoid) {
//		  ProfileProgress.setVisibility(View.INVISIBLE);
//		  Intent intent = new Intent(edit_ProfileActivity.this, MainActivity.class);
//		  startActivity(intent);
//		  finish();
//
//	      }
//	 }).addOnFailureListener(new OnFailureListener() {
//	      @Override
//	      public void onFailure(@NonNull Exception e) {
//		  Toast.makeText(edit_ProfileActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
//		  ProfileProgress.setVisibility(View.INVISIBLE);
//		  saveInputs.setVisibility(View.VISIBLE);
//		  cancelInputs.setVisibility(View.VISIBLE);
//	      }
//	 });
//     }

     //___________________________________________SAVE BUTTON ___________________________________
     public void confirmInput() {
	 if ( !validateCo_Adress() | !validateCO_Name() | !validatePersonalName()|!validatePhoneNumber() ) {
	      return;//TO END FUNCTION IF OK
	 } else {
	      addInfo();
	 }

     }

     private boolean validateCO_Name() {
	 String nameInput = text_input_CoName.getEditText().getText().toString().trim();

	 if ( nameInput.isEmpty() ) {
	      text_input_CoName.setError("املأ البيانات");
	      return false;
	 } else {
	      text_input_CoName.setError(null);
	      return true;
	 }
     }

     private boolean validatePersonalName() {
	 String personalNameInput = text_input_PersonalName.getEditText().getText().toString().trim();

	 if ( personalNameInput.isEmpty() ) {
	      text_input_PersonalName.setError("اكتب كود خاص بك");
	      return false;
	 } else {
	      text_input_PersonalName.setError(null);
	      return true;
	 }
     }

     private boolean validateCo_Adress() {
	 String adressInput = text_input_address.getEditText().getText().toString().trim();

	 if ( adressInput.isEmpty() ) {
	      text_input_address.setError("املأ البيانات");
	      return false;
	 } else {
	      text_input_address.setError(null);
	      return true;
	 }
     }

     public boolean validatePhoneNumber() {

	 String phoneNum = String.valueOf(phone_Number.getText().toString().trim());
	 if ( phoneNum.isEmpty() ) {
	      phone_Number.setError("اكتب رقم تليفون صالح");
	      return false;
	 } else {
	      phone_Number.setError(null);
	      return true;
	 }
     }

     //__________________________________________________________________________________________________


     //_____________________________load image from your phone_______________________________________________________________

     //________________UPLOAD IMAGE________________________________________________________________


}
//	 spinner = findViewById(R.id.activity_spinner);
//
//	     spinner.setAdapter(arrayAdapter);
//	     spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//@Override
//public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//    if ( parent.getItemAtPosition(position).equals("اختر نشاط") ) {
//    //valActivity();
//    saveInputs.setVisibility(View.INVISIBLE);
//    } else {
//
//    activitytype_txt = parent.getItemAtPosition(position).toString();
//    //confirmInput();
//    saveInputs.setVisibility(View.VISIBLE);
//
//    Toast.makeText(edit_ProfileActivity.this, activitytype_txt, Toast.LENGTH_SHORT).show();
//
//    }
//    }
//
//
//@Override
//public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//    });
////___________________________VALIDATE IF USER SELECT AN ACTIVITY OR NOT___________________
//private void valActivity() {
//    TextView errorText = (TextView) spinner.getSelectedView();
//    errorText.setError("");
//    errorText.setTextColor(Color.RED);//just to highlight that this is an error
//    errorText.setText("اختر نشاط");//changes the selected item text to this
//  }

//     private boolean validateWorkers_Name() {
//	 String workersInput = String.valueOf(text_input_WorkersNum.getEditText().getText().toString().trim());
//
//	 if ( workersInput.isEmpty() ) {
//	      text_input_WorkersNum.setError("املأ البيانات");
//	      return false;
//	 } else {
//	      text_input_WorkersNum.setError(null);
//	      return true;
//	 }
//     }