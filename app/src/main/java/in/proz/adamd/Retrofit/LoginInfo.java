package in.proz.adamd.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginInfo {
    String Employee_No,Name;
    @SerializedName("Role_Name")
    List<String> Role_Name;
    @SerializedName("Role")
    List<String> Role;

    public String getEmployee_No() {
        return Employee_No;
    }

    public void setEmployee_No(String employee_No) {
        Employee_No = employee_No;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<String> getRole_Name() {
        return Role_Name;
    }

    public void setRole_Name(List<String> role_Name) {
        Role_Name = role_Name;
    }

    public List<String> getRole() {
        return Role;
    }

    public void setRole(List<String> role) {
        Role = role;
    }
}
