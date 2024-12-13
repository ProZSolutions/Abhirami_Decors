package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import in.proz.adamd.Retrofit.CommonPojo;

public class TicketModal implements Serializable {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<CommonPojo> getTicketList;

    public String getStatus() {
        return status;
    }

    public List<CommonPojo> getGetTicketList() {
        return getTicketList;
    }
}
