package com.makhzan.amr.makhzan;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MyShopActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_my_shop);
	 final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.myshopCoordinator);

	 FloatingActionButton floatingActionButton =
	     (FloatingActionButton) findViewById(R.id.fab);

	 floatingActionButton.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View view) {
//		  Snackbar snackbar = Snackbar.make(coordinatorLayout, "Simple Snackbar", Snackbar.LENGTH_LONG);
//		  snackbar.show();
		  Intent i=new Intent(MyShopActivity.this,CreateShoppingCartActivity.class);
		  startActivity(i);
	      }
	 });
     }
}
