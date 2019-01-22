package com.makhzan.amr.makhzan.items;

import java.util.HashMap;

public class SpecialAdItem {
     private HashMap<String, Object> paying;

     public SpecialAdItem(HashMap<String, Object> paying) {
	 this.paying = paying;
     }

     public HashMap<String, Object> getPaying() {
	 return paying;
     }

     public void setPaying(HashMap<String, Object> paying) {
	 this.paying = paying;
     }
}
