package com.makhzan.amr.makhzan.items;

public class mainCategroies {
     private String Category_Name;
     private String imageUrl;
     private String id;
public mainCategroies(){

}
     public mainCategroies(String category_name, String imageUrl, String id) {
	 Category_Name = category_name;
	 this.imageUrl = imageUrl;
	 this.id = id;
     }

     public String getCategory_Name() {
	 return Category_Name;
     }

     public void setCategory_Name(String category_Name) {
	 Category_Name = category_Name;
     }

     public String getImageUrl() {
	 return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
	 this.imageUrl = imageUrl;
     }

     public String getId() {
	 return id;
     }

     public void setId(String id) {
	 this.id = id;
     }
}
