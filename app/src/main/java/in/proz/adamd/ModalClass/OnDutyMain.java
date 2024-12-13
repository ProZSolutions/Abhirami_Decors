package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class OnDutyMain {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getOnDutyList;
    /*@SerializedName("data")
    Response respons;*/

    public String getStatus() {
        return status;
    }

/*
    public Response getRespons() {
        return respons;
    }
*/

    public List<CommonPojo> getGetOnDutyList() {
        return getOnDutyList;
    }
}
