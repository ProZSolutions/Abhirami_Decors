package in.proz.adamd.AdminModule.AdminModal;

import com.google.gson.annotations.SerializedName;

public class DashboardModal {
    @SerializedName("name")
    String name;
    @SerializedName("today_onduty_pencount")
    String today_onduty_pencount;
    @SerializedName("today_leave_pencount")
    String today_leave_pencount;
    @SerializedName("today_overtime_pencount")
    String today_overtime_pencount;

    public String getToday_overtime_pencount() {
        return today_overtime_pencount;
    }

    public String getToday_onduty_pencount() {
        return today_onduty_pencount;
    }

    public String getToday_leave_pencount() {
        return today_leave_pencount;
    }

    @SerializedName("total_employee_details")
    DashboardSubModal total_employee_details;
    @SerializedName("total_present_details")
    DashboardSubModal total_present_details;
    @SerializedName("total_absent_details")
    DashboardSubModal total_absent_details;
    @SerializedName("total_leave_details")
    DashboardSubModal total_leave_details;
    @SerializedName("total_late_details")
    DashboardSubModal total_late_details;
    @SerializedName("total_permission_details")
    DashboardSubModal total_permission_details;
    @SerializedName("bio_login_details")
    DashboardSubModal bio_login_details;
    @SerializedName("mobile_login_details")
    DashboardSubModal mobile_login_details;
    @SerializedName("req_leave")
    String req_leave;
    @SerializedName("req_loan")
    String req_loan;
    @SerializedName("req_claim")
    String req_claim;
    @SerializedName("req_assets")
    String req_assets;
    @SerializedName("req_tickets")
    String req_tickets;
    @SerializedName("req_onduty")
    String req_onduty;
    @SerializedName("from")
    String from;
    @SerializedName("to")
    String to;

    public String getName() {
        return name;
    }

    public DashboardSubModal getTotal_employee_details() {
        return total_employee_details;
    }

    public DashboardSubModal getTotal_present_details() {
        return total_present_details;
    }

    public DashboardSubModal getTotal_absent_details() {
        return total_absent_details;
    }

    public DashboardSubModal getTotal_leave_details() {
        return total_leave_details;
    }

    public DashboardSubModal getTotal_late_details() {
        return total_late_details;
    }

    public DashboardSubModal getTotal_permission_details() {
        return total_permission_details;
    }

    public DashboardSubModal getBio_login_details() {
        return bio_login_details;
    }

    public DashboardSubModal getMobile_login_details() {
        return mobile_login_details;
    }

    public String getReq_leave() {
        return req_leave;
    }

    public String getReq_loan() {
        return req_loan;
    }

    public String getReq_claim() {
        return req_claim;
    }

    public String getReq_assets() {
        return req_assets;
    }

    public String getReq_tickets() {
        return req_tickets;
    }

    public String getReq_onduty() {
        return req_onduty;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
