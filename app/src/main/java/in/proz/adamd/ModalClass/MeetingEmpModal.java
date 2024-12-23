package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class MeetingEmpModal implements Serializable {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getData;
    @SerializedName("employee")
    List<CommonPojo> employeeList;

    public List<CommonPojo> getEmployeeList() {
        return employeeList;
    }

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getGetData() {
        return getData;
    }
}
