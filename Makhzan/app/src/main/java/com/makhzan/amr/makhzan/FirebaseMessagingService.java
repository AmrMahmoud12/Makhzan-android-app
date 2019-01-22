package com.makhzan.amr.makhzan;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
     public static final String TOKEN_BROADCAST = "mytokenbroadcast";
     @Override
     public void onMessageReceived(RemoteMessage remoteMessage) {
	 //____ID _____
	 int id = (int) System.currentTimeMillis();
	 //_____NOTIFICATION ID'S FROM FCF_____
	 String messageTitle = remoteMessage.getNotification().getTitle();
	 String messageBody = remoteMessage.getNotification().getBody();
	 String click_action = remoteMessage.getNotification().getClickAction();
	 String companyposition = remoteMessage.getData().get("id");

	  String title=remoteMessage.getData().get("title");
	 String myPlacestxt=remoteMessage.getData().get("myPlacestxt");
	 String phoneNumbera=remoteMessage.getData().get("phoneNumbera");
	     //String dataFrom=remoteMessage.getData().get("fromuserid");
	 NotificationCompat.Builder builder =
	     new NotificationCompat
		.Builder(this, getString(R.string.default_notification_channel_id))
		.setSmallIcon(R.drawable.m)
		.setContentTitle(messageTitle)
		.setContentText(messageBody)
	     .setBadgeIconType(R.drawable.m)
		.setAutoCancel(true)
		;
//	 //_____REDIRECTING PAGE WHEN NOTIFICATION CLICKS_____
	 Intent resultIntent = new Intent(click_action);
	 resultIntent.putExtra("CompanyPosition", companyposition);
	 resultIntent.putExtra("title", title);
	 resultIntent.putExtra("myPlacestxt", myPlacestxt);
	 resultIntent.putExtra("phoneNumbera", phoneNumbera);

	 // resultIntent.putExtra("fromuserid",dataFrom);
	 PendingIntent pendingIntent =
	     PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	 builder.setContentIntent(pendingIntent);
	 //____FOR OREO AND HIGHER VERSIONS_____
	 if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
	      int importance = NotificationManager.IMPORTANCE_HIGH;
	      String channelID = BuildConfig.APPLICATION_ID;
	      NotificationChannel channel = new NotificationChannel
		 (getString(R.string.default_notification_channel_id), BuildConfig.APPLICATION_ID, importance);
	      channel.setDescription(channelID);
	      NotificationManager notificationManager = getSystemService(NotificationManager.class);
	      //assert notificationManager != null;
	      notificationManager.createNotificationChannel(channel);
	 }
	 NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	 assert notificationManager != null;
	 notificationManager.notify(id, builder.build());
     }
     @Override
     public void onNewToken(String s) {
	 super.onNewToken(s);
	 FirebaseInstanceId.getInstance().getInstanceId()
	     .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
	      @Override
	      public void onSuccess(InstanceIdResult instanceIdResult)
	      {
		  String newToken = instanceIdResult.getToken();
		  storeToken(newToken);
	      }
	 });
     }
     private void storeToken(String token) {
	 SharedPrefManager.getmInstance(getApplicationContext()).storeToken(token); }}
