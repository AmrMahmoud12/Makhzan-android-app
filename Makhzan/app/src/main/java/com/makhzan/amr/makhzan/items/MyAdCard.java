package com.makhzan.amr.makhzan.items;

public class MyAdCard {
     //____FOR MY ADD________
     private String MyAdNametxt;
     private String MyAddiscriptiontxt;
     private String MyPlacestxt;
     private String PhoneNumbera;
     private String CreationDate;
     private String Category_NameID;
     private String Country_ID;
     private String Ad_ID;
     private String PuplisherId;


     public String getMainImage() {
	 return MainImage;
     }

     public void setMainImage(String mainImage) {
	 MainImage = mainImage;
     }

     private String MainImage;

     //_____FOR PUBLIC ADD______
     private String CompanyName;
     //_______MyAdActivity__________
     private String AdActivity;
     //_______TrustedCompanyTxt_____
     private String TrustedCompanyTxt;

     public MyAdCard() {

     }

     public MyAdCard(
	String myAdNametxt,
	String myAddiscriptiontxt,
	String myPlacestxt,
	String phoneNumbera,
	String creationDate,
	String category_nameID,
	String country_id,
	String ad_id,
	String puplisherId,
	String companyName,
	String adActivity,
	String trustedCompanyTxt

	) {
	 MyAdNametxt = myAdNametxt;
	 MyAddiscriptiontxt = myAddiscriptiontxt;
	 MyPlacestxt = myPlacestxt;
	 PhoneNumbera = phoneNumbera;
	 CreationDate = creationDate;
	 Category_NameID = category_nameID;
	 Country_ID = country_id;
	 Ad_ID = ad_id;
	 PuplisherId = puplisherId;
	 CompanyName = companyName;
	 AdActivity = adActivity;
	 TrustedCompanyTxt = trustedCompanyTxt;
     }

     public MyAdCard(String myAdNametxt,
		   String myAddiscriptiontxt,
		   String myPlacestxt,
		   String phoneNumbera,
		   String creationDate,
		   String category_nameID,
		   String country_id,
		   String ad_id,
		   String puplisherId,
		   String mainImage,
		   String companyName,
		   String adActivity,
		   String trustedCompanyTxt) {
	 MyAdNametxt = myAdNametxt;
	 MyAddiscriptiontxt = myAddiscriptiontxt;
	 MyPlacestxt = myPlacestxt;
	 PhoneNumbera = phoneNumbera;
	 CreationDate = creationDate;
	 Category_NameID = category_nameID;
	 Country_ID = country_id;
	 Ad_ID = ad_id;
	 PuplisherId = puplisherId;
	MainImage = mainImage;
	 CompanyName = companyName;
	 AdActivity = adActivity;
	 TrustedCompanyTxt = trustedCompanyTxt;
     }

     public String getMyAdNametxt() {
	 return MyAdNametxt;
     }

     public void setMyAdNametxt(String myAdNametxt) {
	 MyAdNametxt = myAdNametxt;
     }

     public String getMyAddiscriptiontxt() {
	 return MyAddiscriptiontxt;
     }

     public void setMyAddiscriptiontxt(String myAddiscriptiontxt) {
	 MyAddiscriptiontxt = myAddiscriptiontxt;
     }

     public String getMyPlacestxt() {
	 return MyPlacestxt;
     }

     public void setMyPlacestxt(String myPlacestxt) {
	 MyPlacestxt = myPlacestxt;
     }


     public String getPhoneNumbera() {
	 return PhoneNumbera;
     }

     public void setPhoneNumbera(String phoneNumbera) {
	 PhoneNumbera = phoneNumbera;
     }

     public String getCreationDate() {
	 return CreationDate;
     }

     public void setCreationDate(String creationDate) {
	 this.CreationDate = creationDate;
     }


     public String getCompanyName() {
	 return CompanyName;
     }

     public void setCompanyName(String companyName) {
	 CompanyName = companyName;
     }

     public String getAdActivity() {
	 return AdActivity;
     }

     public void setAdActivity(String adActivity) {
	 AdActivity = adActivity;
     }

     public String getCategory_NameID() {
	 return Category_NameID;
     }

     public void setCategory_NameID(String category_NameID) {
	 Category_NameID = category_NameID;
     }

     public String getCountry_ID() {
	 return Country_ID;
     }

     public void setCountry_ID(String country_ID) {
	 Country_ID = country_ID;
     }

     public String getAd_ID() {
	 return Ad_ID;
     }

     public void setAd_ID(String ad_ID) {
	 Ad_ID = ad_ID;
     }

     public String getTrustedCompanyTxt() {
	 return TrustedCompanyTxt;
     }

     public void setTrustedCompanyTxt(String trustedCompanyTxt) {
	 TrustedCompanyTxt = trustedCompanyTxt;
     }

     public String getPuplisherId() {
	 return PuplisherId;
     }

     public void setPuplisherId(String puplisherId) {
	 PuplisherId = puplisherId;
     }


}
/*
package com.makhzan.amr.makhzan.items;

import android.net.Uri;

public class MyAdCard {
     //____FOR MY ADD________
     private String MyAdNametxt;
     private String MyAddiscriptiontxt;
     private String MyPlacestxt;
     private long PhoneNumbera;
     private String CreationDate;
     private String Category_NameID;
     private String Country_ID;
     private String Ad_ID;
     private String PuplisherId;
     private String imageUrl;

     //_____FOR PUBLIC ADD______
     private String CompanyName;
     //_______MyAdActivity__________
     private String AdActivity;
     //_______TrustedCompanyTxt_____
     private String TrustedCompanyTxt;
     public MyAdCard() {

     }

     public MyAdCard(
         String myAdNametxt,
	String myAddiscriptiontxt, String myPlacestxt
	, long phoneNumbera,
		   String creationDate,
		   String category_nameID,
		   String country_id,
		   String ad_id,
		   String puplisherId,
		   String companyName,
		   String adActivity,
		   String trustedCompanyTxt) {
	 MyAdNametxt = myAdNametxt;
	 MyAddiscriptiontxt = myAddiscriptiontxt;
	 MyPlacestxt = myPlacestxt;
	 PhoneNumbera = phoneNumbera;
	 CreationDate = creationDate;
	 Category_NameID = category_nameID;
	 Country_ID = country_id;
	 Ad_ID = ad_id;
	 PuplisherId = puplisherId;
	 CompanyName = companyName;
	 AdActivity = adActivity;
	 TrustedCompanyTxt = trustedCompanyTxt;
     }
     public MyAdCard(String myAdNametxt, String myAddiscriptiontxt, String myPlacestxt
	, long phoneNumbera, String creationDate, String category_nameID, String country_id, String ad_id, String puplisherId, String imageUrl, String companyName, String adActivity, String trustedCompanyTxt) {
	 MyAdNametxt = myAdNametxt;
	 MyAddiscriptiontxt = myAddiscriptiontxt;
	 MyPlacestxt = myPlacestxt;
	 PhoneNumbera = phoneNumbera;
	 CreationDate = creationDate;
	 Category_NameID = category_nameID;
	 Country_ID = country_id;
	 Ad_ID = ad_id;
	 PuplisherId = puplisherId;
	 this.imageUrl = imageUrl;
	 CompanyName = companyName;
	 AdActivity = adActivity;
	 TrustedCompanyTxt = trustedCompanyTxt;
     }

     public String getMyAdNametxt() {
	 return MyAdNametxt;
     }

     public void setMyAdNametxt(String myAdNametxt) {
	 MyAdNametxt = myAdNametxt;
     }

     public String getMyAddiscriptiontxt() {
	 return MyAddiscriptiontxt;
     }

     public void setMyAddiscriptiontxt(String myAddiscriptiontxt) {
	 MyAddiscriptiontxt = myAddiscriptiontxt;
     }

     public String getMyPlacestxt() {
	 return MyPlacestxt;
     }

     public void setMyPlacestxt(String myPlacestxt) {
	 MyPlacestxt = myPlacestxt;
     }


     public long getPhoneNumbera() {
	 return PhoneNumbera;
     }

     public void setPhoneNumbera(long phoneNumbera) {
	 PhoneNumbera = phoneNumbera;
     }

     public String getCreationDate() {
	 return CreationDate;
     }

     public void setCreationDate(String creationDate) {
	 this.CreationDate = creationDate;
     }


     public String getCompanyName() {
	 return CompanyName;
     }

     public void setCompanyName(String companyName) {
	 CompanyName = companyName;
     }

     public String getAdActivity() {
	 return AdActivity;
     }

     public void setAdActivity(String adActivity) {
	 AdActivity = adActivity;
     }

     public String getCategory_NameID() {
	 return Category_NameID;
     }

     public void setCategory_NameID(String category_NameID) {
	 Category_NameID = category_NameID;
     }

     public String getCountry_ID() {
	 return Country_ID;
     }

     public void setCountry_ID(String country_ID) {
	 Country_ID = country_ID;
     }

     public String getAd_ID() {
	 return Ad_ID;
     }

     public void setAd_ID(String ad_ID) {
	 Ad_ID = ad_ID;
     }

     public String getTrustedCompanyTxt() {
	 return TrustedCompanyTxt;
     }

     public void setTrustedCompanyTxt(String trustedCompanyTxt) {
	 TrustedCompanyTxt = trustedCompanyTxt;
     }

     public String getPuplisherId() {
	 return PuplisherId;
     }

     public void setPuplisherId(String puplisherId) {
	 PuplisherId = puplisherId;
     }

     public String getImageUrl() {
	 return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
	 this.imageUrl = imageUrl;
     }
}
*/