package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

public class InnerTicketModal {
    @SerializedName("ticket_type_id")
    String ticket_type_id;
    @SerializedName("ticket_type")
    String ticket_type;

    public String getTicket_type_id() {
        return ticket_type_id;
    }

    public String getTicket_type() {
        return ticket_type;
    }
}
