package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class ClaimModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getClaimList;

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getGetClaimList() {
        return getClaimList;
    }
}
