package in.proz.adamd.Retrofit;

import java.util.List;

import in.proz.adamd.AdminModule.AdminModal.BranchGetModal;
import in.proz.adamd.AdminModule.AdminModal.DashboardInnerModal;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AdminApiInterface {
    @GET("drop-down")
    Call<BranchGetModal> getSortingDropDown();
    @GET("admin-dashboard")
    Call<DashboardInnerModal> getAdminDashboardDetails(@Query("date") String date, @Query("branch") String branch
            , @Query("department[]") List<String> department);
    @GET("admin-dashboard")
    Call<DashboardInnerModal> getAdminDashboardDetails(@Query("date") String date, @Query("branch") String branch
            , @Query("department") String department);
}
