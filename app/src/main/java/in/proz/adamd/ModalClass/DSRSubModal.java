package in.proz.adamd.ModalClass;

import java.io.Serializable;
import java.util.List;

public class DSRSubModal  implements Serializable {
    String id,emp_id,date,planned_work,completed_activity,comments,is_active,
            status,created_at,updated_at,p_name,other_project_name,is_edit,image_url,document_url,is_update;
    List<String> git_url;

    public String getIs_update() {
        return is_update;
    }

    public String getId() {
        return id;
    }

    public String getIs_edit() {
        return is_edit;
    }

    public void setIs_edit(String is_edit) {
        this.is_edit = is_edit;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url = document_url;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getOther_project_name() {
        return other_project_name;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public String getDate() {
        return date;
    }

    public String getPlanned_work() {
        return planned_work;
    }

    public String getCompleted_activity() {
        return completed_activity;
    }

    public String getComments() {
        return comments;
    }

    public String getIs_active() {
        return is_active;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public List<String> getGit_url() {
        return git_url;
    }

/*
    public List<String> getProject_name() {
        return project_name;
    }
*/
}
