package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttendanceMain {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<AttendanceListSubModal> attendanceListSubModalList ;

    public String getStatus() {
        return status;
    }

    public List<AttendanceListSubModal> getAttendanceListSubModalList() {
        return attendanceListSubModalList;
    }
}
