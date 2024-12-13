package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class RequestDropDownInnerModal {
    @SerializedName("leave_type")
    List<CommonPojo> leaveTypeList;
  /*  @SerializedName("type")
    List<CommonPojo> typeList;
    @SerializedName("alter_type")
    List<CommonPojo> alterTypeList;*/

    public List<CommonPojo> getLeaveTypeList() {
        return leaveTypeList;
    }
/*
    public List<CommonPojo> getTypeList() {
        return typeList;
    }

    public List<CommonPojo> getAlterTypeList() {
        return alterTypeList;
    }*/
}
