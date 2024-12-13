package in.proz.adamd.AttendanceUploadModal;

public class INTimeOutTimeModal {
    String pout_work_location,in_time,out_time,ma_uuid,m_syn_id,updated,pin_work_location,created;
    double latitude,longitude;

    public INTimeOutTimeModal(double latitude, double longitude, String pout_work_location, String in_time, String out_time,
                              String ma_uuid, String m_syn_id, String updated, String pin_work_location, String created) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pout_work_location = pout_work_location;
        this.in_time = in_time;
        this.out_time = out_time;
        this.ma_uuid = ma_uuid;
        this.m_syn_id = m_syn_id;
        this.updated = updated;
        this.pin_work_location = pin_work_location;
        this.created = created;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    public String getPout_work_location() {
        return pout_work_location;
    }

    public void setPout_work_location(String pout_work_location) {
        this.pout_work_location = pout_work_location;
    }

    public String getIn_time() {
        return in_time;
    }

    public void setIn_time(String in_time) {
        this.in_time = in_time;
    }

    public String getOut_time() {
        return out_time;
    }

    public void setOut_time(String out_time) {
        this.out_time = out_time;
    }

    public String getMa_uuid() {
        return ma_uuid;
    }

    public void setMa_uuid(String ma_uuid) {
        this.ma_uuid = ma_uuid;
    }

    public String getM_syn_id() {
        return m_syn_id;
    }

    public void setM_syn_id(String m_syn_id) {
        this.m_syn_id = m_syn_id;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getPin_work_location() {
        return pin_work_location;
    }

    public void setPin_work_location(String pin_work_location) {
        this.pin_work_location = pin_work_location;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
