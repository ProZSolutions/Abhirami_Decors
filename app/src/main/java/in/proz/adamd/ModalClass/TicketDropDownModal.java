package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TicketDropDownModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<InnerTicketModal> getTicketList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<InnerTicketModal> getGetTicketList() {
        return getTicketList;
    }

    public void setGetTicketList(List<InnerTicketModal> getTicketList) {
        this.getTicketList = getTicketList;
    }
}
