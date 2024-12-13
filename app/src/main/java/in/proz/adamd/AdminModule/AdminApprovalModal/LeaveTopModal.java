package in.proz.adamd.AdminModule.AdminApprovalModal;

import com.google.gson.annotations.SerializedName;

public class LeaveTopModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    LeaveInnerModal leaveInnerModal;

    public String getStatus() {
        return status;
    }

    public LeaveInnerModal getLeaveInnerModal() {
        return leaveInnerModal;
    }
    @SerializedName("lcount")
    String lcount;

    public String getLcount() {
        return lcount;
    }
}
