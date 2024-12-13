package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class DModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getDDropDownModal;

    public List<CommonPojo> getGetDDropDownModal() {
        return getDDropDownModal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
