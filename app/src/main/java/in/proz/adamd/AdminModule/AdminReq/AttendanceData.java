package in.proz.adamd.AdminModule.AdminReq;

import com.google.gson.annotations.SerializedName;

public  class AttendanceData {

    @SerializedName("name")
    private String name;

    @SerializedName("total_employee_details")
    private DetailCount totalEmployeeDetails;

    @SerializedName("total_present_details")
    private DetailCount totalPresentDetails;

    @SerializedName("total_absent_details")
    private DetailCount totalAbsentDetails;

    @SerializedName("total_leave_details")
    private DetailCount totalLeaveDetails;

    @SerializedName("total_late_details")
    private DetailCount totalLateDetails;

    @SerializedName("total_permission_details")
    private DetailCount totalPermissionDetails;

    @SerializedName("bio_login_details")
    private DetailCount bioLoginDetails;

    @SerializedName("mobile_login_details")
    private DetailCount mobileLoginDetails;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("req_leave")
    private int reqLeave;

    @SerializedName("req_loan")
    private int reqLoan;

    @SerializedName("req_claim")
    private int reqClaim;

    @SerializedName("req_assets")
    private int reqAssets;

    @SerializedName("req_tickets")
    private int reqTickets;

    @SerializedName("req_onduty")
    private int reqOnduty;

    // Getters
    public String getName() {
        return name;
    }

    public DetailCount getTotalEmployeeDetails() {
        return totalEmployeeDetails;
    }

    public DetailCount getTotalPresentDetails() {
        return totalPresentDetails;
    }

    public DetailCount getTotalAbsentDetails() {
        return totalAbsentDetails;
    }

    public DetailCount getTotalLeaveDetails() {
        return totalLeaveDetails;
    }

    public DetailCount getTotalLateDetails() {
        return totalLateDetails;
    }

    public DetailCount getTotalPermissionDetails() {
        return totalPermissionDetails;
    }

    public DetailCount getBioLoginDetails() {
        return bioLoginDetails;
    }

    public DetailCount getMobileLoginDetails() {
        return mobileLoginDetails;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getReqLeave() {
        return reqLeave;
    }

    public int getReqLoan() {
        return reqLoan;
    }

    public int getReqClaim() {
        return reqClaim;
    }

    public int getReqAssets() {
        return reqAssets;
    }

    public int getReqTickets() {
        return reqTickets;
    }

    public int getReqOnduty() {
        return reqOnduty;
    }
}