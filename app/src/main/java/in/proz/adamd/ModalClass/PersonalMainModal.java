package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

public class PersonalMainModal {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    PersonalModal personalModal;

    public String getStatus() {
        return status;
    }

    public PersonalModal getPersonalModal() {
        return personalModal;
    }
}
