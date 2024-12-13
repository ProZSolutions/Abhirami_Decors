package in.proz.adamd.OnDuty;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class Response {
    @SerializedName("data")
    List<CommonPojo> getOnDutyList;

    public List<CommonPojo> getGetOnDutyList() {
        return getOnDutyList;
    }
}
