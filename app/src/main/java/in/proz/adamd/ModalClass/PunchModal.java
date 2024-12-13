package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

public class PunchModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    SampleCommon commonPojo;

    public String getStatus() {
        return status;
    }

    public SampleCommon getCommonPojo() {
        return commonPojo;
    }
}
