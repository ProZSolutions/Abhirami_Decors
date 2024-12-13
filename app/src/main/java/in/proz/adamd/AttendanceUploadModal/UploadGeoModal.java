package in.proz.adamd.AttendanceUploadModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadGeoModal {
    @SerializedName("data")
    List<GeoModal> data;



    public List<GeoModal> getData() {
        return data;
    }

    public void setData(List<GeoModal> data) {
        this.data = data;
    }
}
