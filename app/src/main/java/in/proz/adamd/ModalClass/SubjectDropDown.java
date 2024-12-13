package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class SubjectDropDown {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getDropDownList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CommonPojo> getGetDropDownList() {
        return getDropDownList;
    }

    public void setGetDropDownList(List<CommonPojo> getDropDownList) {
        this.getDropDownList = getDropDownList;
    }
}
