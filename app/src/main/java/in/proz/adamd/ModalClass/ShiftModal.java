package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class ShiftModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> commonPojoList;

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getCommonPojoList() {
        return commonPojoList;
    }
}
