package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login2Activity extends AppCompatActivity {
     private EditText loginEmailtxt, loginPasswordtxt;
     private Button loginBtn, registerBtn;
     FirebaseAuth firebaseAuth;
     FirebaseAuth.AuthStateListener mAuthStateListener;
     private ProgressBar loginProgress;
     private TextInputLayout textInputEmail, textInputPassword;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_login2);
	 firebaseAuth = FirebaseAuth.getInstance();
	 textInputEmail = (TextInputLayout) findViewById(R.id.text_login_email);
	 textInputPassword = (TextInputLayout) findViewById(R.id.text_login_Password);

	 loginEmailtxt = findViewById(R.id.loginEmailtxt);
	 loginPasswordtxt = findViewById(R.id.loginPasswordtxt);

	 loginProgress = findViewById(R.id.loginProgress);
	 loginProgress.setVisibility(View.GONE);

	 SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
	 String email = prefs.getString("email", "");
	 String pwd = prefs.getString("password", "");
	 loginEmailtxt.setText(email);
	 loginPasswordtxt.setText(pwd);
	 registerBtn = findViewById(R.id.registerBtn);
	 registerBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  Intent m = new Intent(Login2Activity.this, RegisterActivity.class);
		  startActivity(m);
	      }
	 });
	 loginBtn = findViewById(R.id.loginBtn);
	 loginBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	           loginBtn.setVisibility(View.INVISIBLE);
	           registerBtn.setVisibility(View.INVISIBLE);
		  confirmInput();

	      }
	 });

     }

     private boolean validateEmail() {
	 String emailInput = textInputEmail.getEditText().getText().toString().trim();

	 if ( emailInput.isEmpty() ) {
	      textInputEmail.setError("Field can't be empty");
	      return false;
	 } else {
	      textInputEmail.setError(null);
	      return true;
	 }
     }

     private boolean validatePassword() {
	 String passwordInput = textInputPassword.getEditText().getText().toString().trim();

	 if ( passwordInput.isEmpty() ) {
	      textInputPassword.setError("Field can't be empty");
	      return false;
	 } else {
	      textInputPassword.setError(null);
	      return true;
	 }
     }

     public void confirmInput() {
	 if ( !validateEmail() | !validatePassword() ) {
	     loginBtn.setVisibility(View.VISIBLE);
	      registerBtn.setVisibility(View.VISIBLE);

	      return;
	 }

	 String Email = textInputEmail.getEditText().getText().toString();
	 String Password = textInputPassword.getEditText().getText().toString();

	 login(Email, Password);
     }

     public void login(final String email, final String password) {
	 loginProgress.setVisibility(View.VISIBLE);
	 //email = loginEmail.getText().toString().trim();
	 //password = loginPassword.getText().toString().trim();
	 firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
	      @Override
	      public void onComplete(@NonNull Task<AuthResult> task) {
		  if ( task.isSuccessful() ) {
		       SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
		       SharedPreferences.Editor editor = prefs.edit();
		       editor.putString("email", email);
		       editor.putString("password", password);
		       editor.apply();
		       loginProgress.setVisibility(View.GONE);
		       Intent i = new Intent(Login2Activity.this, MainActivity.class);
		       startActivity(i);
		  } else {
		       loginBtn.setVisibility(View.VISIBLE);
		       registerBtn.setVisibility(View.VISIBLE);

		       loginProgress.setVisibility(View.GONE);
		       Toast.makeText(Login2Activity.this, "error occurs, please try again", Toast.LENGTH_SHORT).show();


		  }
	      }
	 });
     }

     @Override
     protected void onStart() {
	 super.onStart();
	 if ( mAuthStateListener != null ) {
	      // Check if user is signed in (non-null) and update UI accordingly.
	      FirebaseUser currentUser = firebaseAuth.getCurrentUser();
	      firebaseAuth.addAuthStateListener(mAuthStateListener);
	 }
     }
}
