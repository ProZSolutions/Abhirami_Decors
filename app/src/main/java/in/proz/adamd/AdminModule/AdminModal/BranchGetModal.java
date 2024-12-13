package in.proz.adamd.AdminModule.AdminModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BranchGetModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<BranchMainModal> getBranchDetails;

    public String getStatus() {
        return status;
    }

    public List<BranchMainModal> getGetBranchDetails() {
        return getBranchDetails;
    }
}
