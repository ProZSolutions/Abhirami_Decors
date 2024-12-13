package in.proz.adamd.AdminModule.AdminModal;

import com.google.gson.annotations.SerializedName;

public class DashboardInnerModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    DashboardModal dashboardModal;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

  public DashboardModal getDashboardModal() {
        return dashboardModal;
    }

    public void setDashboardModal(DashboardModal dashboardModal) {
        this.dashboardModal = dashboardModal;
    }
}
