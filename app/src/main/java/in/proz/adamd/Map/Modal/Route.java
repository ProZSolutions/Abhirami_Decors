package in.proz.adamd.Map.Modal;

import com.google.gson.annotations.SerializedName;

public class Route {
    @SerializedName("overview_polyline")
    private Polyline overviewPolyline;

    public Polyline getOverviewPolyline() {
        return overviewPolyline;
    }
}
