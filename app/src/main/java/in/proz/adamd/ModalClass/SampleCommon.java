package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SampleCommon implements Serializable {
    @SerializedName("punch_status")
    String punch_status;
    @SerializedName("sync_id")
    String sync_id;
    @SerializedName("dsr_flag")
    String dsr_flag;

    public String getDsr_flag() {
        return dsr_flag;
    }

    public String getPunch_status() {
        return punch_status;
    }

    public String getSync_id() {
        return sync_id;
    }
}
