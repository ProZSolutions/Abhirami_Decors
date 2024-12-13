package in.proz.adamd.AdminModule.AdminApprovalModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LeaveInnerModal {
    @SerializedName("data")
    List<LeaveAppModal> dataList;

    public List<LeaveAppModal> getDataList() {
        return dataList;
    }
}
