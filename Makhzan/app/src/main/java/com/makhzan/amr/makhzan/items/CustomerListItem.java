package com.makhzan.amr.makhzan.items;

public class CustomerListItem {
     private String followingName;
     private String uid;


     public CustomerListItem(){

     }
     public CustomerListItem(String followingName, String uid) {
	 this.followingName = followingName;
	 this.uid = uid;
     }

     public String getFollowingName() {
	 return followingName;
     }

     public void setFollowingName(String followingName) {
	 this.followingName = followingName;
     }

     public String getUid() {
	 return uid;
     }

     public void setUid(String uid) {
	 this.uid = uid;
     }
}
