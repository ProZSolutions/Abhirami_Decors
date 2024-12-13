package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CalendarModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CalendarSubClass> calendarlist;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CalendarSubClass> getCalendarlist() {
        return calendarlist;
    }

    public void setCalendarlist(List<CalendarSubClass> calendarlist) {
        this.calendarlist = calendarlist;
    }
}