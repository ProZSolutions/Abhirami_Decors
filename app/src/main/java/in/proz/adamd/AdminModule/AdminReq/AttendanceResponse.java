package in.proz.adamd.AdminModule.AdminReq;

import com.google.gson.annotations.SerializedName;

public class AttendanceResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private AttendanceData data;

    // Getters
    public String getStatus() {
        return status;
    }

    public AttendanceData getData() {
        return data;
    }
}
