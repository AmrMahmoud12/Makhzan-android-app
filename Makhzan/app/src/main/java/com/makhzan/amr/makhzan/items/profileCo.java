package com.makhzan.amr.makhzan.items;

public class profileCo {
     private String CompanyName;
     private String CompanyAddress;
     private String ActivityType;
     private String personalName;
     private long CompanyPhone;



     public profileCo(){

     }
     public profileCo(String companyName, String companyAddress, String activityType, String personalName, long companyPhone) {
	 CompanyName = companyName;
	 CompanyAddress = companyAddress;
	 ActivityType = activityType;
	 this.personalName = personalName;
	 CompanyPhone = companyPhone;
     }

     public String getCompanyAddress() {
	 return CompanyAddress;
     }

     public void setCompanyAddress(String companyAddress) {
	 CompanyAddress = companyAddress;
     }

     public String getActivityType() {
	 return ActivityType;
     }

     public void setActivityType(String activityType) {
	 ActivityType = activityType;
     }

     public String getCompanyName() {
	 return CompanyName;
     }

     public void setCompanyName(String companyName) {
	 CompanyName = companyName;
     }

     public String getPersonalName() {
	 return personalName;
     }

     public void setPersonalName(String personalName) {
	 this.personalName = personalName;
     }


     public long getCompanyPhone() {
	 return CompanyPhone;
     }

     public void setCompanyPhone(long companyPhone) {
	 CompanyPhone = companyPhone;
     }
}
