package in.proz.adamd.AdminModule.AdminModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class BranchMainModal {
    @SerializedName("branch")
    CommonPojo branch;
    @SerializedName("departments")
    List<CommonPojo> departments;

    public CommonPojo getBranch() {
        return branch;
    }

    public List<CommonPojo> getDepartments() {
        return departments;
    }
}
