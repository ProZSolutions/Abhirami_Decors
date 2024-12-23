package in.proz.adamd.CommonJson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardTagContent {
    @SerializedName("title")
    String tag_name;
    @SerializedName("list")
    List<DashboardContent> dashboardContentList;

    public DashboardTagContent(String tag_name, List<DashboardContent> dashboardContentList) {
        this.tag_name = tag_name;
        this.dashboardContentList = dashboardContentList;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public List<DashboardContent> getDashboardContentList() {
        return dashboardContentList;
    }

    public void setDashboardContentList(List<DashboardContent> dashboardContentList) {
        this.dashboardContentList = dashboardContentList;
    }
}
