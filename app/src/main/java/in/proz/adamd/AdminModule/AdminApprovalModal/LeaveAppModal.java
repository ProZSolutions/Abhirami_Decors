package in.proz.adamd.AdminModule.AdminApprovalModal;

import com.google.gson.annotations.SerializedName;

public class LeaveAppModal {
        @SerializedName("dep_name")
        String dep_name;
        boolean isChecked;
        @SerializedName("amount")
        String amount;
        @SerializedName("emp_email")
        String email;


    public String getEmail() {
        return email;
    }

    public String getAmount() {
        return amount;
    }

    @SerializedName("claim_id")
        String claim_id;

        @SerializedName("payment_date")
        String payment_date;
        @SerializedName("pay_description")
        String pay_description;
        @SerializedName("decline_reason")
        String decline_reason;

    public String getPayment_date() {
        return payment_date;
    }

    public String getPay_description() {
        return pay_description;
    }

    public String getDecline_reason() {
        return decline_reason;
    }

    public String getClaim_id() {
        return claim_id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    String checkMyStatus;

    public String getCheckMyStatus() {
        return checkMyStatus;
    }

    public void setCheckMyStatus(String checkMyStatus) {
        this.checkMyStatus = checkMyStatus;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @SerializedName("approve_role")
    String approve_role;
    @SerializedName("weekoff_id")
    String weekoff_id;

    @SerializedName("loan_id")
    String loan_id;

    public String getLoan_id() {
        return loan_id;
    }

    public String getWeekoff_id() {
        return weekoff_id;
    }

    public String getApprove_role() {
        return approve_role;
    }

    @SerializedName("dep_id")
    String dep_id;
    @SerializedName("branch_name")
    String branch_name;
    @SerializedName("branch_id")
    String branch_id;
    @SerializedName("emp_name")
    String emp_name;
    @SerializedName("emp_empno")
    String emp_empno;
    @SerializedName("emp_uuid")
    String emp_uuid;
    @SerializedName("approve_name")
    String approve_name;
    @SerializedName("role_name")
    String role_name;
    @SerializedName("emp_id")
    String emp_id;
    @SerializedName("from")
    String from;
    @SerializedName("to")
    String to;
    @SerializedName("overtime_id")
    String overtime_id;
    @SerializedName("onduty_id")
    String onduty_id;

    public String getOnduty_id() {
        return onduty_id;
    }

    public String getOvertime_id() {
        return overtime_id;
    }
    @SerializedName("perm_hrs")
    String perm_hrs;

    public String getPerm_hrs() {
        return perm_hrs;
    }

    @SerializedName("status")
    String status;
    @SerializedName("reason")
    String reason;
    @SerializedName("file")
    String file;
    @SerializedName("type")
    String type;
    @SerializedName("leave_type")
    String leave_type;
    @SerializedName("leave_id")
    String leave_id;
    @SerializedName("payroll_date")
    String payroll_date;
    @SerializedName("type_label")
    String type_label;
    @SerializedName("leave_type_label")
    String leave_type_label;

    public String getDep_name() {
        return dep_name;
    }

    public void setDep_name(String dep_name) {
        this.dep_name = dep_name;
    }

    public String getDep_id() {
        return dep_id;
    }

    public void setDep_id(String dep_id) {
        this.dep_id = dep_id;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_empno() {
        return emp_empno;
    }

    public void setEmp_empno(String emp_empno) {
        this.emp_empno = emp_empno;
    }

    public String getEmp_uuid() {
        return emp_uuid;
    }

    public void setEmp_uuid(String emp_uuid) {
        this.emp_uuid = emp_uuid;
    }

    public String getApprove_name() {
        return approve_name;
    }

    public void setApprove_name(String approve_name) {
        this.approve_name = approve_name;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getLeave_id() {
        return leave_id;
    }

    public void setLeave_id(String leave_id) {
        this.leave_id = leave_id;
    }

    public String getPayroll_date() {
        return payroll_date;
    }

    public void setPayroll_date(String payroll_date) {
        this.payroll_date = payroll_date;
    }

    public String getType_label() {
        return type_label;
    }

    public void setType_label(String type_label) {
        this.type_label = type_label;
    }

    public String getLeave_type_label() {
        return leave_type_label;
    }

    public void setLeave_type_label(String leave_type_label) {
        this.leave_type_label = leave_type_label;
    }
}
