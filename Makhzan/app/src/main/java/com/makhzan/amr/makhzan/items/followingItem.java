package com.makhzan.amr.makhzan.items;

public class followingItem {
     private String FollowingName;
     private String X;
public followingItem(){

}
public  followingItem(String followingName, String x, String uid){
     FollowingName = followingName;
     X=x;

}
     public followingItem(String followingName) {
	 FollowingName = followingName;
     }

     public String getFollowingName() {
	 return FollowingName;
     }

     public void setFollowingName(String followingName) {
	 FollowingName = followingName;
     }

     public String getX() {
	 return X;
     }

     public void setX(String x) {
	 X = x;
     }



}
