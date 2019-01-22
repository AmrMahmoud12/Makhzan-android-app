package com.makhzan.amr.makhzan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.makhzan.amr.makhzan.items.profileCo;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
     Dialog dialog;
     //______TEXT INPUT VARIABLES___________
     TextInputLayout text_input_CoName, text_input_address, text_input_PersonalName;
     EditText nametxt, addresstxt, personalName, companyPhone;
     //_________________________
     Button saveInputs, cancelInputs;
     //==========google sign in=
     private SignInButton signInButton;
     private GoogleApiClient mGoogleApiClient;
     private static final int RC_SIGN_IN = 9001;
     private Button signout;

     //==============
     FirebaseAuth firebaseAuth;
     FirebaseAuth.AuthStateListener mAuthStateListener;
     private FirebaseFirestore firebaseFirestore;
     //___________Firebase_______________
     DocumentReference addInfo;
     //==============
     //__progress Dialog
     ProgressBar Main_Progress;
     private GoogleSignInClient mGoogleSignInClient;

     private Context mContext;
     private String uid;
     private ProgressBar ProfileProgress;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_login);
	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	 //==========================================================================================
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 firebaseAuth = FirebaseAuth.getInstance();
	 //==========================================================================================

	 //________Progress bar___________
	 Main_Progress = findViewById(R.id.MainProgress);
	 Main_Progress.setVisibility(View.INVISIBLE);
	 //======check if the user is logged in so direct him to the main page=======================


	 signInButton = findViewById(R.id.sign_in_button);
	 signout = findViewById(R.id.signOut);
	 // Configure Google Sign In//=====================1===================
	 GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
	     .requestIdToken(getString(R.string.default_web_client_id))
	     .requestEmail()
	     .build();
	 mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);


	 signInButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  Main_Progress.setVisibility(View.VISIBLE);
		  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		  if ( user != null ) {
		       String uid = user.getUid();
		       firebaseFirestore
			  .collection("Users")
			  .document(uid)
			  .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			   @Override
			   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
			        if ( task.getResult().getBoolean("profileinfo") == null ) {
				    Toast.makeText(LoginActivity.this, "null", Toast.LENGTH_SHORT).show();
				    dialogeAppear();

			        } else if
				   ( task.getResult().getBoolean("profileinfo").equals(true) ) {
				    Toast.makeText(LoginActivity.this, "تم الدخول بنجاح", Toast.LENGTH_SHORT).show();
				    // User is signed in
				    Intent i = new Intent(LoginActivity.this, MainActivity.class);
				    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				    startActivity(i);
				    finish();
			        }
			   }
		       });

		  } else {
		       Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		       startActivityForResult(signInIntent, RC_SIGN_IN);
		  }
	      }
	 });
	 signout.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {

		  signOut();
		  FirebaseAuth.getInstance().signOut();
		  //Auth.GoogleSignInApi.signOut(apiClient);

	      }
	 });
     }


     private void signOut() {

	 Main_Progress.setVisibility(View.VISIBLE);
	 mGoogleSignInClient.signOut()
	     .addOnCompleteListener(this, new OnCompleteListener<Void>() {
		 @Override
		 public void onComplete(@NonNull Task<Void> task) {
		      Main_Progress.setVisibility(View.GONE);
		      Toast.makeText(LoginActivity.this, "You are logged out", Toast.LENGTH_LONG).show();

		 }
	     });
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);

	 // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
	 if ( requestCode == RC_SIGN_IN ) {
	      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
	      try {
		  // Google Sign In was successful, authenticate with Firebase
		  GoogleSignInAccount account = task.getResult(ApiException.class);
		  firebaseAuthWithGoogle(account);
	      } catch (ApiException e) {
		  // Google Sign In failed, update UI appropriately
		  // ...
	      }
	 }
     }

     private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
	 AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
	 firebaseAuth.signInWithCredential(credential)
	     .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
		 @Override
		 public void onComplete(@NonNull Task<AuthResult> task) {
		      if ( task.isSuccessful() ) {

			  // Sign in success, update UI with the signed-in user's information
			  FirebaseUser user = firebaseAuth.getCurrentUser();
			  updateUI(user);
			  if ( user != null ) {
			       String uid = user.getUid();
			       firebaseFirestore
				  .collection("Users")
				  .document(uid)
				  .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				   @Override
				   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				        if ( task.getResult().getBoolean("profileinfo") == null ) {
					    Toast.makeText(LoginActivity.this, "null", Toast.LENGTH_SHORT).show();
					    dialogeAppear();
				        } else if
					   ( task.getResult().getBoolean("profileinfo").equals(true) ) {
					    Toast.makeText(LoginActivity.this, "تم الدخول بنجاح", Toast.LENGTH_SHORT).show();
					    // User is signed in
					    Intent i = new Intent(LoginActivity.this, MainActivity.class);
					    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					    startActivity(i);
					    finish();
				        }
				   }
			       });



			  } else {
			       Intent signInIntent = mGoogleSignInClient.getSignInIntent();
			       startActivityForResult(signInIntent, RC_SIGN_IN);
			  }


		      } else {
			  // If sign in fails, display a message to the user.
			  Toast.makeText(LoginActivity.this, "حدث خطأ ما برجاء المحاوله فى وقت لاحق", Toast.LENGTH_LONG).show();
			  updateUI(null);
		      }
		 }
	     });
     }

     private void dialogeAppear() {

	 AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
	 LayoutInflater inflater = getLayoutInflater();
	 View view = inflater.inflate(R.layout.activity_create_profile, null);
	 builder.setView(view);
	 builder.setCancelable(false);
//______________________TEXTINPUTLAYOUTS___________________________________________________
	 text_input_CoName = view.findViewById(R.id.text_input_CoName);
	 //text_input_WorkersNum = (TextInputLayout) findViewById(R.id.text_input_WorkersNum);
	 text_input_address = view.findViewById(R.id.text_input_address);
	 text_input_PersonalName = view.findViewById(R.id.text_input_PersonaName);
	 ProfileProgress = view.findViewById(R.id.ProfileProgress);
	 personalName = view.findViewById(R.id.PersonalNameTxt);
	 nametxt = view.findViewById(R.id.text_input_CoNametxt);
	 addresstxt = view.findViewById(R.id.addresstxt);
	 companyPhone = view.findViewById(R.id.companyPhone);
	 dialog  = builder.create();
	 dialog.show();
	 ProfileProgress.setVisibility(View.GONE);
	 //__________________________________________SAVEINPUT____________________________________
	 saveInputs = view.findViewById(R.id.saveInfo);
	 saveInputs.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  //confirmInput();
		  if ( !validateCo_Adress() | !validateCO_Name() | !validatePersonalName() | !validatePhoneNumber()  /*!validateWorkers_Name()*/ ) {
		       return;//TO END FUNCTION IF OK
		   } else {
		       ProfileProgress.setVisibility(View.VISIBLE);

		       addInfo();
		       ProfileProgress.setVisibility(View.GONE);
		       dialog.dismiss();
		  }

	      }
	 });
//________________________________________CANCEL iNPUTS__________________________________
	 cancelInputs = view.findViewById(R.id.cancelInfo);
	 cancelInputs.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	           dialog.dismiss();
	      }
	 });


     }

     //___________________________________________SAVE BUTTON ___________________________________
     public void confirmInput() {
	 if ( !validateCo_Adress() | !validateCO_Name() | !validatePersonalName() | !validatePhoneNumber()  /*!validateWorkers_Name()*/ ) {
	      return;//TO END FUNCTION IF OK
	 } else {
	      addInfo();
	 }

     }

     //_____________________________WRITE COMPANY INFO TO THE DATABASE_________________________
     public void addInfo() {
	 saveInputs.setVisibility(View.GONE);
	 cancelInputs.setVisibility(View.GONE);
	 String names = nametxt.getText().toString().trim();
	 String address = addresstxt.getText().toString().trim();
	 String personal_Name = personalName.getText().toString().trim();
	 int a = Integer.parseInt(companyPhone.getText().toString());
	 final long phone= (long) a;
	 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	 String uid = user.getUid();

	 profileCo profileCo = new profileCo(names, address, "", personal_Name, phone);
	 final DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);
	 documentReference.set(profileCo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
	      @Override
	      public void onSuccess(Void aVoid) {

//		  SharedPreferences sharedPref =getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
//		  SharedPreferences.Editor editor = sharedPref.edit();
//		  //editor.putString("userId", result.get("uid"));
//		  editor.putBoolean("profileInfo",true); //this line will do trick
//		  editor.apply();
		  Map<String, Object> map = new HashMap<>();
		  map.put("profileinfo", true);
		  documentReference.set(map, SetOptions.merge());

		  Intent q = new Intent(LoginActivity.this, MainActivity.class);
		  startActivity(q);
		  finish();

	      }
	 }).addOnFailureListener(new OnFailureListener() {
	      @Override
	      public void onFailure(@NonNull Exception e) {
		  Toast.makeText(LoginActivity.this, "Try again later!", Toast.LENGTH_SHORT).show();
		  saveInputs.setVisibility(View.VISIBLE);
		  cancelInputs.setVisibility(View.VISIBLE);
	      }
	 });


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

     public boolean validatePhoneNumber() {

	 String phoneNum = String.valueOf(companyPhone.getText().toString().trim());
	 if ( phoneNum.isEmpty() ) {
	      companyPhone.setError("اكتب رقم تليفون صالح");
	      return false;
	 } else {
	      companyPhone.setError(null);
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

     private void updateUI(FirebaseUser user) {
	 GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
	 if ( acct != null ) {
	      Map<String, Object> tokenMap = new HashMap<>();
	      String personGivenName = acct.getGivenName();
	      String s = user.getUid();
	      String personEmail = acct.getEmail();
	      tokenMap.put("name", personGivenName);
	      tokenMap.put("FirebaseId", s);
	      tokenMap.put("CompanyEmail", personEmail);
	      // String token_id= FirebaseInstanceId.getInstance().getToken();
	      // tokenMap.put("token_id",token_id);

//	      tokenMap.put("CompanyAddress", "default");
//	      tokenMap.put("CompanyName", "No name ");
//	      tokenMap.put("personalName","no name");

	      firebaseFirestore.collection("Users").document(s).set(tokenMap, SetOptions.merge());
	 }


     }


     //===================================================================================================
//2-onstop:when activity disapear and still in memory
     @Override
     protected void onStop() {
	 super.onStop();
	 if ( mAuthStateListener != null ) {
	      firebaseAuth.removeAuthStateListener(mAuthStateListener);
	 }
     }

     @Override
     protected void onStart() {
	 super.onStart();
	 if ( mAuthStateListener != null ) {
	      // Check if user is signed in (non-null) and update UI accordingly.
	      FirebaseUser currentUser = firebaseAuth.getCurrentUser();
	      updateUI(currentUser);
	      firebaseAuth.addAuthStateListener(mAuthStateListener);
	 }
     }


     //====================on back pressed===========================================================
     @Override
     public void onBackPressed() {
	 Main_Progress.setVisibility(View.INVISIBLE);
	 Intent startMain = new Intent(Intent.ACTION_MAIN);
	 startMain.addCategory(Intent.CATEGORY_HOME);
	 startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 startActivity(startMain);
     }

     @Override
     protected void onPause() {
	 super.onPause();

     }

     @Override
     protected void onDestroy() {
	 super.onDestroy();


     }
}

/*public boolean isNetworkAvailable() {
final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

if (activeNetworkInfo != null) { // connected to the internet

if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
return true;
} else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
return true;
}
}
return false;

}*/
