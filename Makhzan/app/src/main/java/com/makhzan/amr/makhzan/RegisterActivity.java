package com.makhzan.amr.makhzan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.firestore.FirebaseFirestore;



import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    //Buttons
    private Button register;
    private ProgressBar registerProgress;
    //==============
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    //==============
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private  TextInputLayout layoutCoName;
    private TextInputLayout layoutCoAddress;
    //=============
    private EditText companyPhonetxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       //==========create task object==========
        textInputUsername= (TextInputLayout) findViewById(R.id.text_input_username);
        textInputPassword= (TextInputLayout) findViewById(R.id.text_inputpassword);
        textInputEmail= (TextInputLayout) findViewById(R.id.text_input_email);
        layoutCoName=findViewById(R.id.layoutCoName);
        layoutCoAddress=findViewById(R.id.layoutCoAddress);
        //______________________________
        companyPhonetxt=findViewById(R.id.companyPhonetxt);
        //==========firebase===========
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        //===========Button==================
        register=findViewById(R.id.clicktoRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setVisibility(View.INVISIBLE);
                confirmInput();
            }
        });
        registerProgress=findViewById(R.id.registerProgress);
        registerProgress.setVisibility(View.GONE);
    }

    private boolean validateCompanyName() {
        String companyNametxt = layoutCoName.getEditText().getText().toString().trim();

        if ( companyNametxt.isEmpty() ) {
            layoutCoName.setError("املأ البيانات");
            return false;
        } else {
            layoutCoName.setError(null);
            return true;
        }
    }
    private boolean validateCompanyAddress() {
        String companyAddresstxt = layoutCoAddress.getEditText().getText().toString().trim();

        if ( companyAddresstxt.isEmpty() ) {
            layoutCoAddress.setError("املأ البيانات");
            return false;
        } else {
            layoutCoAddress.setError(null);
            return true;
        }
    }
    public boolean validateCompanyNumber() {

        String companyNumber = String.valueOf(companyPhonetxt.getText().toString().trim());
        if ( companyNumber.isEmpty() ) {
            companyPhonetxt.setError("اكتب رقم تليفون صالح");
            return false;
        } else {
            companyPhonetxt.setError(null);
            return true;
        }
    }
    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Field can't be like that");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && !Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }
    //============================method of button==============================================
    public void confirmInput() {
        if (!validateEmail() |
            !validateCompanyAddress()|
            !validateCompanyNumber()|
            !validateCompanyName() |
            !validatePassword()) {
            register.setVisibility(View.VISIBLE);
            return;
        }
        registerProgress.setVisibility(View.VISIBLE);
        String Email =textInputEmail.getEditText().getText().toString();
        String Password=textInputPassword.getEditText().getText().toString();
        String compNametxt = layoutCoName.getEditText().getText().toString().trim();
        String compAddresstxt = layoutCoAddress.getEditText().getText().toString().trim();
//        int ph = Integer.parseInt(companyPhonetxt.getText().toString());
//        final long phone= (long) ph;
        final String phoneNumbera=companyPhonetxt.getText().toString().trim();
        register( Email,  Password,compAddresstxt,compNametxt,phoneNumbera);
    }
    //================method of registering from firebase=========================================
    public void register(final String Email, final String Password, final String compAddresstxt, final String compNametxt, final String phoneNumbera) {
        (firebaseAuth.createUserWithEmailAndPassword(Email, Password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert currentUser != null;
                            String uid = currentUser.getUid();
                            //=======objects to be added to the firestore for the first time=========
                            HashMap<String,Object> usermap=new HashMap<>();
                           usermap.put("companyName",compNametxt);
                           usermap.put("companyAddress",compAddresstxt);
                           usermap.put("CompanyEmail",Email);
                            usermap.put("companyPhone",phoneNumbera);
                            usermap.put("companyPassword",Password);
                            usermap.put("FirebaseId",uid);
                            //===============================put identity in store===================
                            firebaseFirestore
                                    .collection("Users")
                                    .document(uid)
                                    .set(usermap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "registration succeeded",
                                                Toast.LENGTH_SHORT).show();
                                        Intent ri=new Intent(RegisterActivity.this,Login2Activity.class);
                                        startActivity(ri);
                                    }else{
                                        registerProgress.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this,"registration failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }  else{
                            register.setVisibility(View.VISIBLE);

                            registerProgress.setVisibility(View.GONE);
                            String error=task.getException().toString();
                            Toast.makeText(RegisterActivity.this,"حدث خطا أثناء التسجيل, برجاء مراجعة البيانات والتحقق " +
                                    "من جوده الانترنت",
                                    Toast.LENGTH_SHORT).show();
                        }                      // ...
                    }
                });
    }

//=========if we returned to the activity after another activity===================================
    @Override
    protected void onRestart() {
        super.onRestart();
        registerProgress.setVisibility(View.GONE);
    }
    //===============================on back pressed==========================================
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(this,Login2Activity.class);
        startActivity(i);
        finish();
    }
    //========================================================================================
    //if back pressed??>>this is what will happen
    //1-onpause:before activity disappear
    @Override
    protected void onPause() {
        super.onPause();
        registerProgress.setVisibility(View.GONE);
    }
    //2-onstop:when activity disapear and still in memory
    @Override protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    //3-ondestroy:when it is removed from ram
    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerProgress.setVisibility(View.GONE);
    }
  //if home button is pressed?>>onpause then on stop
    //when  return after homebutton >>onrestart-onstart-onresume
}
