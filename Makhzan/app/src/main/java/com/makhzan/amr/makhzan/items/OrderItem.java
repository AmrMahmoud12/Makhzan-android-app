package com.makhzan.amr.makhzan.items;

public class OrderItem {
     private String orderTitle;
     private long number ;
     private String orderDate;
     private String companyName;
     private String OrderPublisherId;
     private String orderId;
public OrderItem(){

}
     public OrderItem(String orderTitle, long number, String orderDate, String companyName, String orderPublisherId, String orderId) {
	 this.orderTitle = orderTitle;
	 this.number = number;
	 this.orderDate = orderDate;
	 this.companyName = companyName;
	 OrderPublisherId = orderPublisherId;
	 this.orderId = orderId;
     }

     public String getOrderTitle() {
	 return orderTitle;
     }

     public void setOrderTitle(String orderTitle) {
	 this.orderTitle = orderTitle;
     }

     public long getNumber() {
	 return number;
     }

     public void setNumber(long number) {
	 this.number = number;
     }

     public String getOrderDate() {
	 return orderDate;
     }

     public void setOrderDate(String orderDate) {
	 this.orderDate = orderDate;
     }

     public String getCompanyName() {
	 return companyName;
     }

     public void setCompanyName(String companyName) {
	 this.companyName = companyName;
     }

     public String getOrderPublisherId() {
	 return OrderPublisherId;
     }

     public void setOrderPublisherId(String orderPublisherId) {
	 OrderPublisherId = orderPublisherId;
     }

     public String getOrderId() {
	 return orderId;
     }

     public void setOrderId(String orderId) {
	 this.orderId = orderId;
     }
}
