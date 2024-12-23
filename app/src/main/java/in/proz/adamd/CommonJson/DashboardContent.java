package in.proz.adamd.CommonJson;

public class DashboardContent {
    String id,tag,title;
    boolean visible;
    int image;

    public DashboardContent(String id, String tag, String title, boolean visible,int image) {
        this.id = id;
        this.image=image;
        this.tag = tag;
        this.title = title;
        this.visible = visible;
    }

    public int getImage() {
        return image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVisible() {
        return visible;
    }
}
