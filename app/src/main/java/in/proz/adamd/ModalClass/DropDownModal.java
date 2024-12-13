package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class DropDownModal {
    String id,name,type_value;
    @SerializedName("leave_type")
    List<CommonPojo> leaveTypeList;
    @SerializedName("type")
    List<CommonPojo> typeList;
    @SerializedName("alter_type")
    List<CommonPojo> alterTypeList;


    public List<CommonPojo> getLeaveTypeList() {
        return leaveTypeList;
    }

    public List<CommonPojo> getTypeList() {
        return typeList;
    }

    public List<CommonPojo> getAlterTypeList() {
        return alterTypeList;
    }

    public DropDownModal(String id, String name, String type_value) {
        this.id = id;
        this.name = name;
        this.type_value = type_value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType_value() {
        return type_value;
    }

    public void setType_value(String type_value) {
        this.type_value = type_value;
    }
}
