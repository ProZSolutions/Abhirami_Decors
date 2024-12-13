package in.proz.adamd.Meeting;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class MeetingModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getData;

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getGetData() {
        return getData;
    }
}
