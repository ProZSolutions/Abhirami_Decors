package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

public class RequestDropDownModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    RequestDropDownInnerModal modal;

    public String getStatus() {
        return status;
    }

    public RequestDropDownInnerModal getModal() {
        return modal;
    }
}
