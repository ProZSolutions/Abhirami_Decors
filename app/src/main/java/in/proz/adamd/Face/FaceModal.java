package in.proz.adamd.Face;

import com.google.gson.annotations.SerializedName;

import in.proz.adamd.Retrofit.CommonPojo;

public class FaceModal {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCommonPojo(CommonPojo commonPojo) {
        this.commonPojo = commonPojo;
    }
}
