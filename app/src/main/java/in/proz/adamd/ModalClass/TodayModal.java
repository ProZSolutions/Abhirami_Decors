package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import in.proz.adamd.Retrofit.CommonPojo;

public class TodayModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    CommonPojo commonPojo;

    public String getStatus() {
        return status;
    }

    public CommonPojo getCommonPojo() {
        return commonPojo;
    }
}
