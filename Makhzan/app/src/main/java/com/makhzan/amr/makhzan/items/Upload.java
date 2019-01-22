package com.makhzan.amr.makhzan.items;

public class Upload {
     private String imageUrl;
     public Upload(){
	 //empty constructor needed

     }
     public Upload(String imageUrl) {
	 this.imageUrl = imageUrl;
     }


     public String getImageUrl() {
	 return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
	 this.imageUrl = imageUrl;
     }
}
