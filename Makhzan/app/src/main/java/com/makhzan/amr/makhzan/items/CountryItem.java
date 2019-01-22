package com.makhzan.amr.makhzan.items;

public class CountryItem {
     private String CountryName;
public CountryItem(){

}
     public CountryItem(String countryName) {
	 CountryName = countryName;
     }

     public String getCountryName() {
	 return CountryName;
     }

     public void setCountryName(String countryName) {
	 CountryName = countryName;
     }
}
