package in.proz.adamd.Retrofit;

 import java.util.List;

 import in.proz.adamd.AdminModule.AdminApprovalModal.LeaveTopModal;
 import in.proz.adamd.AdminModule.AdminModals.InnerModal;
 import in.proz.adamd.AttendanceUploadModal.UploadGeoModal;
 import in.proz.adamd.AttendanceUploadModal.UploadTimeModal;
 import in.proz.adamd.Face.FaceModal;
 import in.proz.adamd.FaceAuth.SimilarityClassifier;
 import in.proz.adamd.Meeting.MeetingModal;
 import in.proz.adamd.ModalClass.AttendanceMain;
 import in.proz.adamd.ModalClass.CalendarModal;
 import in.proz.adamd.ModalClass.ClaimModal;
 import in.proz.adamd.ModalClass.ConstructionModal;
 import in.proz.adamd.ModalClass.DModal;
 import in.proz.adamd.ModalClass.DSRMainModal;
 import in.proz.adamd.ModalClass.EmpDropDown;
 import in.proz.adamd.ModalClass.LeaveBalanceModal;
 import in.proz.adamd.ModalClass.LeaveModal;
 import in.proz.adamd.ModalClass.MeetingEmpModal;
 import in.proz.adamd.ModalClass.OnDutyMain;
 import in.proz.adamd.ModalClass.OverTimeMain;
 import in.proz.adamd.ModalClass.PersonalMainModal;
 import in.proz.adamd.ModalClass.ProjectListModal;
 import in.proz.adamd.ModalClass.PunchModal;
 import in.proz.adamd.ModalClass.RequestDropDownModal;
 import in.proz.adamd.ModalClass.ShiftModal;
 import in.proz.adamd.ModalClass.SubjectDropDown;
 import in.proz.adamd.ModalClass.TicketDropDownModal;
 import in.proz.adamd.ModalClass.TicketModal;
 import in.proz.adamd.ModalClass.TodayModal;
 import in.proz.adamd.ModalClass.WSREmpList;
 import okhttp3.MultipartBody;
 import retrofit2.Call;
 import retrofit2.http.Body;
 import retrofit2.http.GET;
 import retrofit2.http.Multipart;
 import retrofit2.http.POST;
 import retrofit2.http.Part;
 import retrofit2.http.Query;


public interface ApiInterface {

    ///check attendance punch in or not


    ////offline to online sync work

    @POST("faceauth-store")
    Call<CommonPojo> registerFace(@Body SimilarityClassifier.Recognition recognitionData);
    @POST("faceauth-list")
    Call<FaceModal> getFaceList(@Query("id") String deviceid);

    @GET("employee-block")
    Call<CommonPojo> bloUnBlockReset(@Query("emp_no") String emp_no,@Query("type") String type,@Query("block_reason") String block_reason);
    @GET("employee-index")
    Call<InnerModal> getEmployeeIndex(@Query("branch") String branch,
                                   @Query("department[]") List<String> department,@Query("type") String type,
                                   @Query("search") String search,@Query("page") String page);
    @GET("employee-index")
    Call<InnerModal> getEmployeeIndexAll(@Query("branch") String branch,
                                      @Query("department")  String department,@Query("type") String type,
                                      @Query("search") String search,@Query("page") String page);

    @GET("attendance-details")
    Call<InnerModal> getCommonList(@Query("date") String date,@Query("branch") String branch,
                                   @Query("department[]") List<String> department,@Query("type") String type,
                                   @Query("search") String search,@Query("page") String page);
    @GET("attendance-details")
    Call<InnerModal> getCommonListString(@Query("date") String date,@Query("branch") String branch,
                                   @Query("department")  String department,@Query("type") String type,
                                   @Query("search") String search,@Query("page") String page);
    @GET("leave-list")
    Call<LeaveTopModal> getLeaveApprovals(@Query("branch") String branch,@Query("department")  String departmentId,
                                          @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("leave-list")
    Call<LeaveTopModal>     getLeaveApprovalsArr(@Query("branch") String branch,@Query("department[]")  List<String> departmentId,
                                          @Query("status") String status,@Query("date") String date,@Query("page") String page
    ,@Query("request_date") String request_date);
    @GET("weekoff-list")
    Call<LeaveTopModal> getWeekOffList(@Query("branch") String branch,@Query("department")  String departmentId,
                                          @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("weekoff-list")
    Call<LeaveTopModal> getWeekOffListArr(@Query("branch") String branch,@Query("department[]")  List<String> departmentId,
                                       @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("loan-list")
    Call<LeaveTopModal> getLoanList(@Query("branch") String branch,@Query("department")  String departmentId,
                                       @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("loan-list")
    Call<LeaveTopModal> getLoanListArr(@Query("branch") String branch,@Query("department[]")  List<String> departmentId,
                                    @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);

     @GET("claim-list")
    Call<LeaveTopModal> getClaimList(@Query("branch") String branch,@Query("department")  String departmentId,
                                    @Query("status") String status,@Query("date") String date,@Query("page") String page
             ,@Query("request_date") String request_date);
    @GET("claim-list")
    Call<LeaveTopModal> getClaimListArr(@Query("branch") String branch,@Query("department[]")  List<String> departmentId,
                                     @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);

    @GET("overtime-list")
    Call<LeaveTopModal> getOverTimeApprovsls(@Query("branch") String branch,@Query("department")  String departmentId,
                                          @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("overtime-list")
    Call<LeaveTopModal> getOverTimeApprovslsArr(@Query("branch") String branch,@Query("department[]")  List<String> departmentId,
                                             @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("onduty-list")
    Call<LeaveTopModal> getOnDutyList(@Query("branch") String branch,@Query("department")  String departmentId,
                                             @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @GET("onduty-list")
    Call<LeaveTopModal> getOnDutyListArr(@Query("branch") String branch,@Query("department[]")  List<String> departmentId,
                                      @Query("status") String status,@Query("date") String date,@Query("page") String page
            ,@Query("request_date") String request_date);
    @POST("leave-approved")
    Call<CommonPojo> getApprove(@Query("leave_id[]") List<String> idList);
    @POST("leave-declined")
    Call<CommonPojo> getDeclined(@Query("leave_id[]") List<String> idList);

    @POST("overtime-approved")
    Call<CommonPojo> overtimeApprove(@Query("overtime_id[]") List<String> overtimeID);
    @POST("overtime-declined")
    Call<CommonPojo> overtimeDecline(@Query("overtime_id[]") List<String> overtimeID);

    @POST("onduty-approved")
    Call<CommonPojo> ondutyApproved(@Query("onduty_id[]") List<String> overtimeID);
    @POST("onduty-declined")
    Call<CommonPojo> ondutyDeclined(@Query("onduty_id[]") List<String> overtimeID);
    @POST("loan-approved")
    Call<CommonPojo> loanAproved(@Query("loan_id[]") List<String> overtimeID);
    @POST("loan-declined")
    Call<CommonPojo> loanDeclined(@Query("loan_id[]") List<String> overtimeID);
    @POST("weekoff-approved")
    Call<CommonPojo> weekoffApproved(@Query("weekoff_id[]") List<String> overtimeID);
    @POST("weekoff-declined")
    Call<CommonPojo> weekoffDeclined(@Query("weekoff_id[]") List<String> overtimeID);
    @POST("claim-approved")
    Call<CommonPojo> claimAproved(@Query("claim_id[]") List<String> overtimeID,@Query("payment_date") String payment_date,
                                  @Query("pay_description") String pay_description);
    @POST("claim-declined")
    Call<CommonPojo> claimDeclined(@Query("claim_id[]") List<String> overtimeID,@Query("decline_reason") String decline_reason);


    @POST("bulk-goe-location")
    Call<CommonPojo> uploadGeoTag(@Body UploadGeoModal geoModals);
    @POST("offline-punchin")
    Call<CommonPojo> uploadCheckInTime(@Body UploadTimeModal timeModal);
    @POST("offline-punchout")
    Call<CommonPojo> uploadCheckOutTime(@Body  UploadTimeModal timeModal);

    ///offline to online sync work


    @POST("goe-location")
    Call<CommonPojo> LiveTrackingGeoLocation(@Query("ma_uuid") String ma_uuid,@Query("latitude") String latitude,
                                             @Query("longitude") String longitude,@Query("in_time") String in_time);
    @POST("check-atten")
    Call<PunchModal>    getAttendancePunchInStatus();


    ///meeting module
        @GET("meeting-list")
    Call<MeetingModal> getMeetingList();
    @POST("meeting-create")
    Call<CommonPojo> meetingCreate(@Query("meeting_type") String meeting_type,@Query("other_name") String other_name,
                                   @Query("meeting_name") String meeting_name,@Query("date") String date,
                                   @Query("from_time") String from_time,@Query("to_time") String to_time,
                                   @Query("invite_mail") String invite_mail,@Query("dropdown_group[]") List<String> dropdown_group,
                                   @Query("location") String location,@Query("link_for") String link_for,
                                   @Query("lead") String lead,@Query("address") String address);
    @POST("meeting-update")
    Call<CommonPojo> meetingUpdate(@Query("meeting_id") String meeting_id,@Query("meeting_type") String meeting_type,@Query("other_name") String other_name,
                                   @Query("meeting_name") String meeting_name,@Query("date") String date,
                                   @Query("from_time") String from_time,@Query("to_time") String to_time,
                                   @Query("invite_mail") String invite_mail,@Query("dropdown_group[]") List<String> dropdown_group,
                                   @Query("location") String location,@Query("link_for") String link_for,
                                   @Query("lead") String lead,@Query("address") String address);
    @POST("meeting-cancel")
    Call<CommonPojo> meetingCancel(@Query("meeting_id") String meeting_id,@Query("reason") String reason);



    ////meeting module

    /*@GET("dsr-sentto")
    Call<WSREmpList> getSendTo();*/

    @POST("dsr-start")
    Call<CommonPojo> DSRInsertInAttendance(@Query("emp_id") String emp_id,@Query("date") String date,
                                           @Query("planned_work") String planned_work,@Query("project_name[]") List<String> project_name,
                                           @Query("other_project_name") String other_project_name);
    @GET("dsr-sentto")
    Call<WSREmpList> getDSRSendTo();
        @POST("employee-dropdown")
        Call<MeetingEmpModal> getEmpDetails();
    //meeting    module


    ////payroll 2.0



    @POST("empovertime-dropdown")
    Call<EmpDropDown> getDropDown();
    @POST("change-password")
    Call<CommonPojo> callChangePAssword(@Query("current_password") String pass_old,@Query("new_password") String pass_new);
    @POST("login")
    Call<CommonPojo> callLoginApi(@Query("username") String username, @Query("password") String password,
                                  @Query("device_id") String device_id,@Query("version_no") String version_no);
    @POST("check-device-id")
    Call<CommonPojo> callFingerAuth( @Query("device_id") String device_id,@Query("version_no") String version_no);

    @POST("atten-punchout")
    Call<CommonPojo> callPunchOut(@Query("sync_id") String sync_id,@Query("latitude") double latitude,
                                  @Query("longitude") double longitude,@Query("pout_work_location") String pout_work_location
            ,@Query("branch_id") String branch);
    @POST("atten-punchin")
    Call<CommonPojo> callPunchIn(@Query("latitude") double latitude,@Query("longitude") double longitude,
                                 @Query("pin_work_location") String work_type,@Query("branch_id") String branch);
    @POST("leave-balance")
    Call<LeaveBalanceModal> getLeaveBalance();
    @POST("leave-request-dropdown")
    Call<RequestDropDownModal> getRequestDropDown();
    @POST("profile-update")
    Call<CommonPojo> updateProfile(@Query("dob") String type,@Query("bloodgroup") String leave_type,
                                   @Query("address") String session,@Query("pincode") String reason);
    @Multipart
    @POST("leave-request")
    Call<CommonPojo> insertLeaveRequest(@Query("type") String type,@Query("leave_type") String leave_type,
                                        @Query("session") String session,@Query("reason") String reason, @Part MultipartBody.Part file,@Query("from_date") String from,
                                        @Query("to_date") String to,@Query("from_time") String from_time,@Query("to_time") String to_time,@Query("alt_type") String alt_type);

    @POST("leave-request")
    Call<CommonPojo> insertLeaveRequestWithoutMultipart(@Query("type") String type,@Query("leave_type") String leave_type,
                                        @Query("session") String session,@Query("reason") String reason,@Query("from_date") String from,
                                        @Query("to_date") String to,@Query("from_time") String from_time,@Query("to_time") String to_time,@Query("alt_type") String alt_type);
    @POST("leave-cancel")
    Call<CommonPojo> cancelLeaveRequest(@Query("leave_id") String leave_id);
    @Multipart
    @POST("loan-request")
    Call<CommonPojo> insertLoanRequest(@Query("loan_type") String loan_type,@Query("date") String date,@Query("amount") String amount,
                                       @Query("reason") String reason,@Part MultipartBody.Part file,
                                       @Query("duration") String duration);
     @POST("loan-request")
    Call<CommonPojo> insertLoanRequestWithoutImage(@Query("loan_type") String loan_type,@Query("date") String date,@Query("amount") String amount,
                                       @Query("reason") String reason,
                                       @Query("duration") String duration);
    @Multipart
    @POST("update-loan-request")
    Call<CommonPojo> updateLoanRequest(@Query("loan_type") String loan_type,@Query("date") String date,@Query("amount") String amount,
                                       @Query("reason") String reason,@Part MultipartBody.Part file,
                                       @Query("duration") String duration,@Query("loan_id") String loan_id);
    @POST("update-loan-request")
    Call<CommonPojo> updateLoanRequestWithoutImage(@Query("loan_type") String loan_type,@Query("date") String date,@Query("amount") String amount,
                                                   @Query("reason") String reason,
                                                   @Query("duration") String duration,@Query("loan_id") String loan_id);

    @POST("loan-cancel")
    Call<CommonPojo> cancelLoanRequest(@Query("loan_id") String loan_id);
    @Multipart
    @POST("request-claim")
    Call<CommonPojo> insertClaimRequest(@Query("date") String date,@Query("amount") String amount,@Query("reason") String reason,
                                        @Part MultipartBody.Part file);
    @Multipart
    @POST("update-request-claim")
    Call<CommonPojo> updateClaimRequest(@Query("date") String date,@Query("amount") String amount,@Query("reason") String reason,
                                        @Part MultipartBody.Part file,@Query("claim_id") String claim_id);
     @POST("update-request-claim")
    Call<CommonPojo> updateClaimWithoutImage(@Query("date") String date,@Query("amount") String amount,@Query("reason") String reason,
                                        @Query("claim_id") String claim_id);
    @Multipart
    @POST("advance-claim")
    Call<CommonPojo> advanceClaimRequest(@Query("date") String date,@Query("amount") String amount,@Query("reason") String reason,
                                        @Part MultipartBody.Part file);
    @Multipart
    @POST("update-advance-claim")
    Call<CommonPojo> updateadvanceClaimRequest(@Query("date") String date,@Query("amount") String amount,@Query("reason") String reason,
                                         @Part MultipartBody.Part file,@Query("claim_id") String claim_id);
     @POST("update-advance-claim")
    Call<CommonPojo> updateAdvancedWithoutImage(@Query("date") String date,@Query("amount") String amount,@Query("reason") String reason,
                                              @Query("claim_id") String claim_id);
    @POST("request-alldelete")
    Call<CommonPojo> deleteCommonRequest(@Query("request_id") String request_id);


    @POST("atten-list")
    Call<TodayModal> getDailySummary();
    @POST("atten-index")
    Call<AttendanceMain> getAttenList(@Query("date") String date);
    @GET("leave-index")
    Call<LeaveModal> getLeaveList();
    @POST("leave-cancel")
    Call<CommonPojo> cancelLeave(@Query("leave_id") String leave_id);
    @POST("claim-cancel")
    Call<CommonPojo> cancelClaim(@Query("claim_id") String leave_id);
    @GET("claim-index")
    Call<ClaimModal> getClaimList();
    @GET("advance-claim-index")
    Call<ClaimModal> getAdvancedClaimList();
    @GET("loan-index")
    Call<ClaimModal> getLoanList();
    @POST("admin-wsrlist")
    Call<ClaimModal> getAdminWSRLIST(@Query("c_week") String c_week,@Query("empid") String empid);
     @GET("employee-profile")
    Call<PersonalMainModal> getUserProfile();
    @GET("employee-shift")
    Call<ShiftModal> getShiftList();
    @POST("dsr-empindex")
    Call<LeaveModal> getDSRLIST(@Query("from_date") String year,@Query("to_date") String month);
    @GET("holiday-index")
    Call<CalendarModal> getHolidayCalendarList();
    @GET("get-projectlist")
    Call<ProjectListModal> getProjectList();
    @POST("dsr-store")
    Call<CommonPojo> insertDSRList(@Query("date") String date,@Query("planned_work") String planned_work,
                                   @Query("completed_activity") String completed_activity,@Query("comment") String comments,
     @Query("uuid") String uuid,@Query("other_project_name") String pro_name);
    @POST("dsr-update")
    Call<CommonPojo> updateDSRList(@Query("date") String date,@Query("planned_work") String planned_work,
                                   @Query("completed_activity") String completed_activity,@Query("comment") String comments,

                                   @Query("uuid") String uuid,@Query("dsr_id") String dsr_id,@Query("other_project_name") String pro_name);
    @POST("dsr-empindex")
    Call<DSRMainModal> getDSRList(@Query("from_date") String from_Date,@Query("to_date") String str_to_date);
    @POST("tickets-dropdown")
    Call<TicketDropDownModal> getTicketDropDownModal();
    @Multipart
     @POST("tickets-create")
    Call<CommonPojo> insertTicket(@Query("ticket_type_id") String ticket_type_id,
                                  @Query("priority") String priority,@Query("details") String details,@Part MultipartBody.Part file);

    @POST("tickets-update")
    Call<CommonPojo> updateWithoutTicket(@Query("ticket_id") String ticket_id,@Query("ticket_type_id") String ticket_type_id,
                                  @Query("priority") String priority,@Query("details") String details);
    @Multipart
    @POST("tickets-update")
    Call<CommonPojo> updateTicket(@Query("ticket_id") String ticket_id,@Query("ticket_type_id") String ticket_type_id,
                                  @Query("priority") String priority,@Query("details") String details,@Part MultipartBody.Part file);
    @GET("tickets-list")
    Call<TicketModal> getTicketList();
    @GET("assets-list")
    Call<TicketModal> getAssetList();

    @GET("assets-dropdown")
    Call<SubjectDropDown> getMainCatDropDown();

    @POST("assets-create")
    Call<CommonPojo> insertAssetCreate(@Query("cat_id") String cat_id,@Query("sub_cat_id") String sub_cat_id,@Query("asset_id") String asset_id,
                                       @Query("configuration") String configuration,@Query("quantity") String quantity,@Query("details") String details);


    @POST("assets-update")
    Call<CommonPojo> updateAssetCreate(@Query("ast_id") String id,@Query("cat_id") String cat_id,@Query("sub_cat_id") String sub_cat_id,@Query("asset_id") String asset_id,
                                       @Query("configuration") String configuration,@Query("quantity") String quantity,@Query("details") String details);


    @POST("assets-cancel")
    Call<CommonPojo> assetCancel(@Query("ast_id") String ass_id);
    @POST("tickets-cancel")
    Call<CommonPojo> ticketCancel(@Query("ticket_id") String ticket_id);
    @POST("dsr-generatepdf")
    Call<CommonPojo> generatePDF(@Query("block_factor") String block_factor,@Query("next_week_plan") String next_week_plan,
                                 @Query("others") String others,@Query("send_to") String send_to,@Query("send_cc[]") List<String> send_cc);
    @POST("onduty-dropdown")
    Call<DModal> getODDropDown();
    @GET("get-app")
    Call<CommonPojo> getAppDownloadURL();
    @GET("emponduty-list")
    Call<OnDutyMain> getOnDutyList(@Query("page") String pageNo);
    @GET("empovertime-list")
    Call<OverTimeMain> getOverTimeList(@Query("page") String pageNo);
    @POST("onduty-create")
    Call<CommonPojo> insertOnDuty(@Query("date") String date,@Query("leave_type") String leave_type,@Query("reason") String reason);
    @POST("onduty-create")
    Call<CommonPojo> insertOnDutyDialog(@Query("date") String date,@Query("leave_type") String leave_type,
                                        @Query("reason") String reason,@Query("emp_id") String emp_id);
    @POST("empovertime-create")
    Call<CommonPojo> insertOverTime(@Query("date") String date,@Query("leave_type") String leave_type,@Query("reason") String reason,
                                    @Query("from_time") String from_time,@Query("to_time") String to_time);
    @POST("empovertime-update")
    Call<CommonPojo> updateOverTime(@Query("duty_id") String onduty_id,@Query("date") String date,@Query("leave_type") String leave_type,@Query("reason") String reason,
                                    @Query("from_time") String from_time,@Query("to_time") String to_time);
    @POST("onduty-cancel")
    Call<CommonPojo> cancelOnDuty(@Query("onduty_id") String onduty_id);
    @POST("empovertime-cancel")
    Call<CommonPojo> cancelOverTime(@Query("onduty_id") String onduty_id);
    @Multipart
    @POST("employee-profile-update")
    Call<CommonPojo> updateProfile(@Part MultipartBody.Part file,@Query("profile_id") String profile);
    @GET("version")
    Call<ConstructionModal> getVersionDetails(@Query("version_no") String version_no);


}