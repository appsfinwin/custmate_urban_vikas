package com.finwin.urbanvikas.custmate.home.balance_enquiry.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Balance {

@SerializedName("data")
@Expose
private Data data;

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

}