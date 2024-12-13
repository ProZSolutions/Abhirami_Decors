package in.proz.adamd.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import in.proz.adamd.ModalClass.PunchInModal;

public class CommonPojo   implements Serializable {
    String status,data,department,punch_in,cc_name,attachmentPath,document;
    String status_label,from_time,to_time,empno,name;
    String branch_lat,branch_long,branch_id;
    @SerializedName("updated")
    String updated_at;

    public String getUpdated_at() {
        return updated_at;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public String getBranch_lat() {
        return branch_lat;
    }

    public String getBranch_long() {
        return branch_long;
    }

    String extra;
    Float distance;

    public String getEmbeedings() {
        return extra;
    }

    public Float getDistance() {
        return distance;
    }

    String bearer_token,is_edit,files,ma_uuid,desg_name,is_blocked,active;
    String token_type,issue_date,issued_details,user_name;
    String filename,payment_date,pay_description;
    String role,empname,empid,contact,comp_email;
    String priority;

    public String getDocument() {
        return document;
    }

    String device_id,other_type,meeting_name;
    int image_setter;

    public String getCc_name() {
        return cc_name;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public String getEmpno() {
        return empno;
    }

    public String getStatus_label() {
        return status_label;
    }
String approve_name;

    public String getApprove_name() {
        return approve_name;
    }

    public String getFrom_time() {
        return from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setDetails(String name, int image_setter){
        this.image_setter = image_setter;
        this.name =name;
    }
    public int getImage_setter() {
        return image_setter;
    }

    public String getDepartment() {
        return department;
    }

    public String getIs_blocked() {
        return is_blocked;
    }

    public String getActive() {
        return active;
    }

    public String getPunch_in() {
        return punch_in;
    }

    public String getContact() {
        return contact;
    }


    // admin module
    String total_employee,total_present_count,total_absent_count,total_leave_count,total_late_count,
            total_permission_count,bio_login,mobile_login,req_leave,req_loan,req_claim,req_assets,
            req_tickets,req_onduty,latitude,longitude;

    public String getExtra() {
        return extra;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @SerializedName("emp_details")
    LoginInfo loginInfo;
    String half_day,id,location,address,incharge,halfdaylessmin,absentlessmin,halflatemin,
            halfearlymin,uuid,pincode,district, state, country, latlong,branch;

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIncharge() {
        return incharge;
    }

    public void setIncharge(String incharge) {
        this.incharge = incharge;
    }

    public String getHalfdaylessmin() {
        return halfdaylessmin;
    }

    public void setHalfdaylessmin(String halfdaylessmin) {
        this.halfdaylessmin = halfdaylessmin;
    }

    public String getAbsentlessmin() {
        return absentlessmin;
    }

    public void setAbsentlessmin(String absentlessmin) {
        this.absentlessmin = absentlessmin;
    }

    public String getHalflatemin() {
        return halflatemin;
    }

    public void setHalflatemin(String halflatemin) {
        this.halflatemin = halflatemin;
    }

    public String getTotal_employee() {
        return total_employee;
    }

    public String getTotal_present_count() {
        return total_present_count;
    }

    public String getTotal_absent_count() {
        return total_absent_count;
    }

    public String getTotal_leave_count() {
        return total_leave_count;
    }

    public String getTotal_late_count() {
        return total_late_count;
    }

    public String getTotal_permission_count() {
        return total_permission_count;
    }

    public String getBio_login() {
        return bio_login;
    }

    public String getMobile_login() {
        return mobile_login;
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

    public String getHalfearlymin() {
        return halfearlymin;
    }

    public void setHalfearlymin(String halfearlymin) {
        this.halfearlymin = halfearlymin;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public void setMa_uuid(String ma_uuid) {
        this.ma_uuid = ma_uuid;
    }

    public String getDesg_name() {
        return desg_name;
    }
    @SerializedName("dept_name")
    String dept_name;

    public String getDept_name() {
        return dept_name;
    }

    public void setDesg_name(String desg_name) {
        this.desg_name = desg_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setOther_type(String other_type) {
        this.other_type = other_type;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public void setEmp_no(String emp_no) {
        this.emp_no = emp_no;
    }

    public void setComp_email(String comp_email) {
        this.comp_email = comp_email;
    }

    public void setIs_variable(String is_variable) {
        this.is_variable = is_variable;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public void setParticipate(String participate) {
        this.participate = participate;
    }

    public void setInvite_mail(String invite_mail) {
        this.invite_mail = invite_mail;
    }

    public void setLoaction_type(String loaction_type) {
        this.loaction_type = loaction_type;
    }

    public void setMeeting_link(String meeting_link) {
        this.meeting_link = meeting_link;
    }

    public void setLocation_details(String location_details) {
        this.location_details = location_details;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public void setF_time(String f_time) {
        this.f_time = f_time;
    }

    public void setT_time(String t_time) {
        this.t_time = t_time;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public void setTicket_name(String ticket_name) {
        this.ticket_name = ticket_name;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
    }

    public void setStatus_by(String status_by) {
        this.status_by = status_by;
    }

    public void setPunch_status(String punch_status) {
        this.punch_status = punch_status;
    }

    public void setSync_id(String sync_id) {
        this.sync_id = sync_id;
    }

    public String getUser_name() {
        return user_name;
    }

    String project_id,is_cancel;
    String project_name,image;
    String emp_no,is_variable;

    public String getIs_variable() {
        return is_variable;
    }

    public String getMa_uuid() {
        return ma_uuid;
    }

    public void setIs_cancel(String is_cancel) {
        this.is_cancel = is_cancel;
    }

    public String getIs_cancel() {
        return is_cancel;
    }

    public String getEmpname() {
        return empname;
    }

    public String getEmpid() {
        return empid;
    }

    public String getEmp_no() {
        return emp_no;
    }

    public String getComp_email() {
        return comp_email;
    }

    public void setApp_under_construction(String app_under_construction) {
        this.app_under_construction = app_under_construction;
    }
    @SerializedName("branchDetails")
            List<CommonPojo> getBranchDetails;

    public List<CommonPojo> getGetBranchDetails() {
        return getBranchDetails;
    }

    String app_version,app_under_construction;

    public String getApp_version() {
        return app_version;
    }

    public String getApp_under_construction() {
        return app_under_construction;
    }

    public String getFiles() {
        return files;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getPay_description() {
        return pay_description;
    }

    public void setPay_description(String pay_description) {
        this.pay_description = pay_description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIs_edit() {
        return is_edit;
    }

    public void setIs_edit(String is_edit) {
        this.is_edit = is_edit;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public void setIssued_details(String issued_details) {
        this.issued_details = issued_details;
    }






    public String getIssue_date() {
        return issue_date;
    }

    public String getIssued_details() {
        return issued_details;
    }

    public String getStatus_by() {
        return status_by;
    }

    List<String> git_url;
    String emp_id;
    String planned_work;
    String cat_id;
    String category_name,subcategory_id;
    String sub_cat_id;

    String category_id;
    String subcategory_name;
    String asset_id;
    String asset_name;
    String completed_activity;
    String comments;
    String ticket_type_id,ticket_type,lead,participate,invite_mail,loaction_type,meeting_link
            ,location_details,from_date,to_date,f_time,t_time,attachments;
    String type_name,ticket_name,configuration,details,quantity,approved_by;

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getType_name() {
        return type_name;
    }


    public String getOther_type() {
        return other_type;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public String getLead() {
        return lead;
    }

    public String getParticipate() {
        return participate;
    }

    public String getInvite_mail() {
        return invite_mail;
    }

    public String getLoaction_type() {
        return loaction_type;
    }

    public String getMeeting_link() {
        return meeting_link;
    }

    public String getLocation_details() {
        return location_details;
    }

    public String getFrom_date() {
        return from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public String getF_time() {
        return f_time;
    }

    public String getT_time() {
        return t_time;
    }

    public String getAttachments() {
        return attachments;
    }

    public String getTicket_name() {
        return ticket_name;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getDetails() {
        return details;
    }

    public String getFilename() {
        return filename;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public String getTicket_type_id() {
        return ticket_type_id;
    }

    public void setTicket_type_id(String ticket_type_id) {
        this.ticket_type_id = ticket_type_id;
    }

    public String getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getAsset_name() {
        return asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getPlanned_work() {
        return planned_work;
    }

    public void setPlanned_work(String planned_work) {
        this.planned_work = planned_work;
    }

    public String getCompleted_activity() {
        return completed_activity;
    }

    public void setCompleted_activity(String completed_activity) {
        this.completed_activity = completed_activity;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public List<String> getGit_url() {
        return git_url;
    }

    public void setGit_url(List<String> git_url) {
        this.git_url = git_url;
    }

    public String getHalf_day() {
        return half_day;
    }

    public void setHalf_day(String half_day) {
        this.half_day = half_day;
    }

    public void setCasual_bal_leave(String casual_bal_leave) {
        this.casual_bal_leave = casual_bal_leave;
    }

    public void setSick_bal_leave(String sick_bal_leave) {
        this.sick_bal_leave = sick_bal_leave;
    }

    public void setMedical_bal_leave(String medical_bal_leave) {
        this.medical_bal_leave = medical_bal_leave;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setAadharno(String aadharno) {
        this.aadharno = aadharno;
    }

    public void setShift_name(String shift_name) {
        this.shift_name = shift_name;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setHis_isflexi(String his_isflexi) {
        this.his_isflexi = his_isflexi;
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

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setInTimeList(List<String> inTimeList) {
        this.inTimeList = inTimeList;
    }

    public void setOutTimeList(List<String> outTimeList) {
        this.outTimeList = outTimeList;
    }

    public void setWorkHours(List<String> workHours) {
        this.workHours = workHours;
    }

    public void setTotal_leave(String total_leave) {
        this.total_leave = total_leave;
    }

    public void setTotal_bal_leave(String total_bal_leave) {
        this.total_bal_leave = total_bal_leave;
    }

    public void setCasual_leave(String casual_leave) {
        this.casual_leave = casual_leave;
    }

    public void setSick_leave(String sick_leave) {
        this.sick_leave = sick_leave;
    }

    public void setMedical_leave(String medical_leave) {
        this.medical_leave = medical_leave;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setError(String error) {
        this.error = error;
    }

    @SerializedName("date")
    String date;
    @SerializedName("casual_bal_leave")
    String casual_bal_leave;
    @SerializedName("sick_bal_leave")
    String sick_bal_leave;
    @SerializedName("medical_bal_leave")
    String medical_bal_leave;

    public String getPriority() {
        return priority;
    }

    @SerializedName("amount")
    String amount;
    @SerializedName("status_by")
    String status_by;
    @SerializedName("reason")
    String reason;

    @SerializedName("aadharno")
    String aadharno;
    @SerializedName("shift_name")
    String shift_name;
    @SerializedName("start_time")
    String start_time;
    @SerializedName("end_time")
    String end_time;
    @SerializedName("his_isflexi")
    String his_isflexi;
    @SerializedName("start")
    String start;
    @SerializedName("end")
    String end;

    public String getShift_name() {
        return shift_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getHis_isflexi() {
        return his_isflexi;
    }

    public String getAddress() {
        return address;
    }

    public String getPincode() {
        return pincode;
    }

    public String getAadharno() {
        return aadharno;
    }

    @SerializedName("type")
    String type;
    @SerializedName("leave_type")
    String leave_type;
    @SerializedName("approved")
    String approved;
    @SerializedName("file")
    String file;
    @SerializedName("status_code")
    String status_code;

    public void setType(String type) {
        this.type = type;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
    @SerializedName("title")
    String title;

    public String getTitle() {
        return title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("in_time")
    List<String> inTimeList;
    @SerializedName("out_time")
    List<String> outTimeList;
    @SerializedName("work_hours")
    List<String> workHours;
    @SerializedName("total_leave")
    String total_leave;
    @SerializedName("total_bal_leave")
    String total_bal_leave;

    @SerializedName("casual_leave")
    String casual_leave ;
    @SerializedName("sick_leave")
    String sick_leave;
    @SerializedName("medical_leave")
    String medical_leave;


    @SerializedName("from")
    String from;
    @SerializedName("to")
    String to;

    public String getType() {
        return type;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public String getApproved() {
        return approved;
    }

    public String getFile() {
        return file;
    }

    public String getStatus_code() {
        return status_code;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getId() {
        return id;
    }

    String onduty_id;

    public String getOnduty_id() {
        return onduty_id;
    }

    public String getName() {
        return name;
    }

    public String getCasual_leave() {
        return casual_leave;
    }
    @SerializedName("total_days")
    String total_days;

    public String getTotal_days() {
        return total_days;
    }

    public void setTotal_days(String total_days) {
        this.total_days = total_days;
    }

    public String getSick_leave() {
        return sick_leave;
    }

    public String getMedical_leave() {
        return medical_leave;
    }

    public String getTotal_leave() {
        return total_leave;
    }

    public String getTotal_bal_leave() {
        return total_bal_leave;
    }

    public String getCasual_bal_leave() {
        return casual_bal_leave;
    }

    public String getSick_bal_leave() {
        return sick_bal_leave;
    }

    public String getMedical_bal_leave() {
        return medical_bal_leave;
    }

   /* @SerializedName("data")
    DashboardModal dashboardModal;*/
    @SerializedName("error")
    String error;
    @SerializedName("punchin_data")
    PunchInModal punchInModal;
    @SerializedName("punch_status")
    String punch_status;
    @SerializedName("sync_id")
    String sync_id;

    public String getPunch_status() {
        return punch_status;
    }

    public String getSync_id() {
        return sync_id;
    }

    public PunchInModal getPunchInModal() {
        return punchInModal;
    }


    public void setPunchInModal(PunchInModal punchInModal) {
        this.punchInModal = punchInModal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBearer_token() {
        return bearer_token;
    }

    public void setBearer_token(String bearer_token) {
        this.bearer_token = bearer_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public List<String> getInTimeList() {
        return inTimeList;
    }

    public List<String> getOutTimeList() {
        return outTimeList;
    }

    public List<String> getWorkHours() {
        return workHours;
    }
}
