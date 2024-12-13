package in.proz.adamd.DSR;

import com.google.gson.annotations.SerializedName;

public class WSRModal {
    @SerializedName("empname")
    String empname;
    @SerializedName("empid")
    String empid;

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }
}
