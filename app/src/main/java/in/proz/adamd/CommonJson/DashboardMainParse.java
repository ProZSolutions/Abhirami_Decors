package in.proz.adamd.CommonJson;

import com.google.gson.annotations.SerializedName;

public class DashboardMainParse {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    DashboardParseContent dashboardParseContent;

    public String getStatus() {
        return status;
    }

    public DashboardParseContent getDashboardParseContent() {
        return dashboardParseContent;
    }
}
