package in.proz.adamd.ModalClass;

import java.io.Serializable;

public class NotesModal  implements Serializable {
    String date,title,desc,id;

    public NotesModal(String date, String title, String desc,String id ) {
        this.date = date;
        this.title = title;
        this.desc = desc;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
