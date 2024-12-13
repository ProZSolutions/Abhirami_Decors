package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class WSREmpList {
    @SerializedName("status")
    String status;
    @SerializedName("to_data")
    List<CommonPojo> to_data;
    @SerializedName("cc_data")
    List<CommonPojo> cc_data;

    public List<CommonPojo> getCc_data() {
        return cc_data;
    }

    public void setCc_data(List<CommonPojo> cc_data) {
        this.cc_data = cc_data;
    }

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getTo_data() {
        return to_data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTo_data(List<CommonPojo> to_data) {
        this.to_data = to_data;
    }
}
