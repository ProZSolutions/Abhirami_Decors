package in.proz.adamd.AdminModule.AdminReq;

import com.google.gson.annotations.SerializedName;

public  class DetailCount {

    @SerializedName("count")
    private int count;

    // Getter
    public int getCount() {
        return count;
    }
}