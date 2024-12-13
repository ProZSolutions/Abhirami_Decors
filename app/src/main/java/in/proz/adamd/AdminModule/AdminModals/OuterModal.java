package in.proz.adamd.AdminModule.AdminModals;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class OuterModal {
    @SerializedName("total")
    String total;

    public String getTotal() {
        return total;
    }

    @SerializedName("data")
    List<CommonPojo> commonPojoList;

    public List<CommonPojo> getCommonPojoList() {
        return commonPojoList;
    }
}
