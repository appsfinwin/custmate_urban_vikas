package com.finwin.urbanvikas.custmate.home.home_fragment.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Masters {

@SerializedName("status")
@Expose
private String status;
@SerializedName("data")
@Expose
private List<MastersData> data = null;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public List<MastersData> getData() {
return data;
}

public void setData(List<MastersData> data) {
this.data = data;
}

    @SerializedName("msg")
    @Expose
    private String msg;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}