package in.proz.adamd.ModalClass;

public class LatLngBranch {
    String branchid,latitude,longitude,branchdistance;

    public LatLngBranch(String branchid, String latitude, String longitude,
                        String branchdistance) {
        this.branchid = branchid;
        this.latitude = latitude;
        this.branchdistance =branchdistance;
        this.longitude = longitude;
    }

    public String getBranchid() {
        return branchid;
    }

    public String getBranchdistance() {
        return branchdistance;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public void setBranchdistance(String branchdistance) {
        this.branchdistance = branchdistance;
    }

    public String getId() {
        return branchid;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.branchid = branchid;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
