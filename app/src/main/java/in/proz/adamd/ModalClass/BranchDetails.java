package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class BranchDetails {
    @SerializedName("branch_details")
    List<CommonPojo>  getBranchDetails;

    public List<CommonPojo> getGetBranchDetails() {
        return getBranchDetails;
    }

    public void setGetBranchDetails(List<CommonPojo> getBranchDetails) {
        this.getBranchDetails = getBranchDetails;
    }
}
