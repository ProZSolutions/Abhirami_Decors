package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttendanceListSubModal {
    @SerializedName("date")
    String date;
    @SerializedName("week_date")
    String week_date;
    @SerializedName("intime")
    List<String> intimeList;
    @SerializedName("is_late")
    List<String> is_late;
    @SerializedName("out_time")
    List<String> outtimeList;
    @SerializedName("work_hours")
    List<String> workhoursList;
    @SerializedName("week_end")
    String week_end;
    @SerializedName("holiday")
    String holiday;
    @SerializedName("leave")
    String leave;
    @SerializedName("pin_work_location")
    List<String> pin_work_locationList;
    @SerializedName("pout_work_location")
    List<String> pout_work_locationList;

    public List<String> getPin_work_locationList() {
        return pin_work_locationList;
    }

    public AttendanceListSubModal(String date, String week_date, List<String> intimeList, List<String> outtimeList, List<String> workhoursList, String week_end, String holiday,
                                  String leave, List<String> pin_work_locationList, List<String> pout_work_locationList,List<String> is_late) {
        this.date = date;
        this.week_date = week_date;
        this.intimeList = intimeList;
        this.outtimeList = outtimeList;
        this.workhoursList = workhoursList;
        this.is_late = is_late;
        this.week_end = week_end;
        this.holiday = holiday;
        this.leave = leave;
        this.pin_work_locationList = pin_work_locationList;
        this.pout_work_locationList = pout_work_locationList;
    }

    public List<String> getIs_late() {
        return is_late;
    }

    public void setPin_work_locationList(List<String> pin_work_locationList) {
        this.pin_work_locationList = pin_work_locationList;
    }

    public List<String> getPout_work_locationList() {
        return pout_work_locationList;
    }

    public void setPout_work_locationList(List<String> pout_work_locationList) {
        this.pout_work_locationList = pout_work_locationList;
    }

    public String getDate() {
        return date;
    }

    public String getWeek_date() {
        return week_date;
    }

    public List<String> getIntimeList() {
        return intimeList;
    }

    public List<String> getOuttimeList() {
        return outtimeList;
    }

    public List<String> getWorkhoursList() {
        return workhoursList;
    }

    public String getWeek_end() {
        return week_end;
    }

    public String getHoliday() {
        return holiday;
    }

    public String getLeave() {
        return leave;
    }
}
