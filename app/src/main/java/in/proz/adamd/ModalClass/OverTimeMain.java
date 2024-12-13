package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class OverTimeMain {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> respons;

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getRespons() {
        return respons;
    }
}
