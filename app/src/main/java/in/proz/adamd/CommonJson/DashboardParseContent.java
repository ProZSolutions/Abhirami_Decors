package in.proz.adamd.CommonJson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DashboardParseContent {
    //main title
    @SerializedName("admin_header_card")
    DashboardTagContent admin_header_card;
    @SerializedName("employee_card")
    DashboardTagContent employee_card;
    @SerializedName("admin_essentials")
    DashboardTagContent admin_essentials;
    @SerializedName("admin_middle_card")
    DashboardTagContent admin_middle_card;
    @SerializedName("radius_distance")
    DashboardTagContent radius_distance;

    public DashboardTagContent getRadius_distance() {
        return radius_distance;
    }

    public DashboardTagContent getAdmin_header_card() {
        return admin_header_card;
    }

    public DashboardTagContent getEmployee_card() {
        return employee_card;
    }

    public DashboardTagContent getAdmin_essentials() {
        return admin_essentials;
    }

    public DashboardTagContent getAdmin_middle_card() {
        return admin_middle_card;
    }


    /*public  DashboardTagContent MainTitle( ){
        List<DashboardContent> dashboardContentList = new ArrayList<>();
        dashboardContentList.add(0,new DashboardContent("0","summary","Summary",true));
        dashboardContentList.add(1,new DashboardContent("1","approval","Approvals",true));
        DashboardTagContent dashboardTagContent=null;
             dashboardTagContent = new DashboardTagContent("Admin Main Card",dashboardContentList);


        return dashboardTagContent;
     }
    //employee content
    public DashboardTagContent EmployeeList( ){
        List<DashboardContent> dashboardContentList = new ArrayList<>();
        dashboardContentList.add(0,new DashboardContent("0","attendance","Attendance",true));
        dashboardContentList.add(1,new DashboardContent("1","leave","Permission",true));
        dashboardContentList.add(2,new DashboardContent("2","dsr","Work Update",true));
        dashboardContentList.add(3,new DashboardContent("3","overtime","Overtime",true));
        dashboardContentList.add(4,new DashboardContent("4","meeting","My Meetings",true));
        dashboardContentList.add(5,new DashboardContent("5","loan","Loan",true));
        dashboardContentList.add(6,new DashboardContent("6","reimbursement","Reimbursement",true));
        dashboardContentList.add(7,new DashboardContent("7","calendar","My Calendar",true));
        DashboardTagContent dashboardTagContent=null;
             dashboardTagContent = new DashboardTagContent("My Service & Request",dashboardContentList);
         return  dashboardTagContent;
    }
    //admin middle management
    public DashboardTagContent adminMiddleContent(){
        List<DashboardContent> dashboardContentList = new ArrayList<>();
        dashboardContentList.add(0,new DashboardContent("0","attendance","Attendance",true));
        dashboardContentList.add(1,new DashboardContent("1","leave","Permission",true));
        dashboardContentList.add(2,new DashboardContent("2","dsr","Work Update",true));
        DashboardTagContent dashboardTagContent=null;
             dashboardTagContent = new DashboardTagContent("Employee Attendance Management",dashboardContentList);

        return  dashboardTagContent;
    }
    //admin Essentials
    public DashboardTagContent adminEssentials( ){
        List<DashboardContent> dashboardContentList = new ArrayList<>();
        dashboardContentList.add(0,new DashboardContent("0","meeting","My Meetings",true));
        dashboardContentList.add(1,new DashboardContent("1","attendance","Attendance",true));
        dashboardContentList.add(2,new DashboardContent("2","calendar","My Calendar",false));
        dashboardContentList.add(3,new DashboardContent("3","request","My Request",true));
        DashboardTagContent dashboardTagContent=null;
        dashboardTagContent = new DashboardTagContent("My Essentials",dashboardContentList);


        return  dashboardTagContent;
    }*/

}
