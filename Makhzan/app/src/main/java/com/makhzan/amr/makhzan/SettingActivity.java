package com.makhzan.amr.makhzan;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
     //FIREBASE
     FirebaseFirestore firebaseFirestore;
     private String uid;
     private Switch companySwitch,custumersSwitch;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_setting);
	 //FIREBASE
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	 //assert currentUser != null;
	 assert currentUser != null;
	 uid = currentUser.getUid();
	 firebaseFirestore = FirebaseFirestore.getInstance();
	 companySwitch = findViewById(R.id.switchCompanies);
	  custumersSwitch = findViewById(R.id.switchCustomers);
	 getNotify();
	 companySwitch.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
		  if ( companySwitch.isChecked() ) {
		       Map<String, Object> map = new HashMap<>();
		       map.put("companiesnotifications", true);
		       firebaseFirestore.collection("Users").document(uid).update(map)
			  .addOnSuccessListener(new OnSuccessListener<Void>() {
			   @Override
			   public void onSuccess(Void aVoid) {
			        Toast.makeText(SettingActivity.this, "تم تفعيل الاشعارات", Toast.LENGTH_SHORT).show();

			   }
		       });
		  } else {
		       Map<String, Object> map = new HashMap<>();
		       map.put("companiesnotifications", false);
		       firebaseFirestore.collection("Users").document(uid).update(map)
			  .addOnSuccessListener(new OnSuccessListener<Void>() {
			   @Override
			   public void onSuccess(Void aVoid) {
			        Toast.makeText(SettingActivity.this, "تم الغاء الاشعارات", Toast.LENGTH_SHORT).show();

			   }
		       });
		  }
	      }
	 });
custumersSwitch.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
	 if ( custumersSwitch.isChecked() ) {
	      Map<String, Object> map = new HashMap<>();
	      map.put("customersnotifications", true);
	      firebaseFirestore.collection("Users").document(uid).set(map,SetOptions.merge())
		 .addOnSuccessListener(new OnSuccessListener<Void>() {
		      @Override
		      public void onSuccess(Void aVoid) {
			  Toast.makeText(SettingActivity.this, "تم تفعيل الاشعارات", Toast.LENGTH_SHORT).show();

		      }
		 });
	 } else {
	      Map<String, Object> map = new HashMap<>();
	      map.put("customersnotifications", false);
	      firebaseFirestore.collection("Users").document(uid).set(map,SetOptions.merge())
		 .addOnSuccessListener(new OnSuccessListener<Void>() {
		      @Override
		      public void onSuccess(Void aVoid) {
			  Toast.makeText(SettingActivity.this, "تم الغاء الاشعارات", Toast.LENGTH_SHORT).show();

		      }
		 });
	 }
     }
});
     }

     public void getNotify() {

	 firebaseFirestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
	      @Override
	      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
		  if ( task.isSuccessful() ){
		       DocumentSnapshot documentSnapshot=task.getResult();
		       if ( documentSnapshot.exists() ){
			   boolean notifyCompanies = Boolean.parseBoolean(documentSnapshot.get("companiesnotifications").toString());
			   boolean notifyCustomers = Boolean.parseBoolean(documentSnapshot.get("customersnotifications").toString());

			   if ( notifyCompanies ) {
				companySwitch.setChecked(true);
			   } else {
			        companySwitch.setChecked(false);
			   }

			   if ( notifyCustomers ) {
			        custumersSwitch.setChecked(true);

			   } else {
			        custumersSwitch.setChecked(false);
			   }
		       }else {}
		  }else {

		  }
	      }
	 });
     }

}
