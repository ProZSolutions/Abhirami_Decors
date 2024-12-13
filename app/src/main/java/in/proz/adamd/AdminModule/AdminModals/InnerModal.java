package in.proz.adamd.AdminModule.AdminModals;

import com.google.gson.annotations.SerializedName;

public class InnerModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    OuterModal outerModal;

    public String getStatus() {
        return status;
    }

    public OuterModal getOuterModal() {
        return outerModal;
    }
}
