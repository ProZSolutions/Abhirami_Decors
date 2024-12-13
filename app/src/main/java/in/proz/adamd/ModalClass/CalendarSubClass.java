package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

public class CalendarSubClass {
    @SerializedName("id")
    String id;
    @SerializedName("title")
    String title;
    @SerializedName("allDay")
    String allday;
    @SerializedName("start")
    String start;
    @SerializedName("end")
    String end;

    public CalendarSubClass(String id, String title, String allday, String start, String end) {
        this.id = id;
        this.title = title;
        this.allday = allday;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAllday() {
        return allday;
    }

    public void setAllday(String allday) {
        this.allday = allday;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
