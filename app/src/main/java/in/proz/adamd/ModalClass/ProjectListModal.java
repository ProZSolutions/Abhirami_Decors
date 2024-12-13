package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class ProjectListModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getProjectList;

    public ProjectListModal(String status, List<CommonPojo> getProjectList) {
        this.status = status;
        this.getProjectList = getProjectList;
    }

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getGetProjectList() {
        return getProjectList;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGetProjectList(List<CommonPojo> getProjectList) {
        this.getProjectList = getProjectList;
    }
}
