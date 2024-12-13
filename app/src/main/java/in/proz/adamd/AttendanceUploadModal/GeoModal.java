package in.proz.adamd.AttendanceUploadModal;

public class GeoModal {
    String ma_uuid,in_time;
    double m_syn_id;
    double latitude,longitude;

    public GeoModal(String ma_uuid, double latitude, double longitude, String in_time, double m_syn_id) {
        this.ma_uuid = ma_uuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.in_time = in_time;
        this.m_syn_id = m_syn_id;
    }

    public void setMa_uuid(String ma_uuid) {
        this.ma_uuid = ma_uuid;
    }



    public void setIn_time(String in_time) {
        this.in_time = in_time;
    }


    public String getMa_uuid() {
        return ma_uuid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIn_time() {
        return in_time;
    }

    public double getM_syn_id() {
        return m_syn_id;
    }

    public void setM_syn_id(long m_syn_id) {
        this.m_syn_id = m_syn_id;
    }
}
