package in.proz.adamd.Face;

public class SQLFaceModal {
    String extra,title,updated_at;

    public SQLFaceModal(String extra, String title, String updated_at) {
        this.extra = extra;
        this.title = title;
        this.updated_at = updated_at;
    }

    public String getExtra() {
        return extra;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
