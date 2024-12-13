package in.proz.adamd.Map.Modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }
}
