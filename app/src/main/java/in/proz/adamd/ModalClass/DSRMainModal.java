package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DSRMainModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<DSRSubModal> dsrSubModals;
    @SerializedName("wsr")
    String wsr;

    public String getWsr() {
        return wsr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DSRSubModal> getDsrSubModals() {
        return dsrSubModals;
    }

    public void setDsrSubModals(List<DSRSubModal> dsrSubModals) {
        this.dsrSubModals = dsrSubModals;
    }
}
