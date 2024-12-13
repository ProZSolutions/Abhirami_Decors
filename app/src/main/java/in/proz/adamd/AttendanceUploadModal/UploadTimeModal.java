package in.proz.adamd.AttendanceUploadModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadTimeModal {
    @SerializedName("data")
    List<INTimeOutTimeModal> inTimeOutTimeModalList;

    public List<INTimeOutTimeModal> getInTimeOutTimeModalList() {
        return inTimeOutTimeModalList;
    }

    public void setInTimeOutTimeModalList(List<INTimeOutTimeModal> inTimeOutTimeModalList) {
        this.inTimeOutTimeModalList = inTimeOutTimeModalList;
    }
}
