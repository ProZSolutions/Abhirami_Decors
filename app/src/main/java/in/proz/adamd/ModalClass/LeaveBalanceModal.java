package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import in.proz.adamd.Retrofit.CommonPojo;

public class LeaveBalanceModal {
    @SerializedName("status")
    String status ;
    @SerializedName("data")
    CommonPojo data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CommonPojo getData() {
        return data;
    }

    public void setData(CommonPojo data) {
        this.data = data;
    }
}
