package in.proz.adamd.ModalClass;

import com.google.gson.annotations.SerializedName;

import in.proz.adamd.Retrofit.CommonPojo;

public class PersonalModal {
    @SerializedName("id")
    String id;
    @SerializedName("emp_no")
    String emp_no;
    @SerializedName("name")
    String name;
    @SerializedName("gender")
    String gender;
    @SerializedName("image")
    String image;
    @SerializedName("dob")
    String dob;
    @SerializedName("bloodgroup")
    String bloodgroup;

    public String getId() {
        return id;
    }

    @SerializedName("address")

    CommonPojo addressPojo;
    @SerializedName("contact")
    String contact;
    @SerializedName("email")
    String email;
    @SerializedName("comp_email")
    String comp_email;
    @SerializedName("aadhar")
    CommonPojo aadharPojo;
    @SerializedName("department")
    String department;
    @SerializedName("designation")
    String designation;

    public String getComp_email() {
        return comp_email;
    }

    public void setComp_email(String comp_email) {
        this.comp_email = comp_email;
    }

    public String getEmp_no() {
        return emp_no;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public String getDob() {
        return dob;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public CommonPojo getAddressPojo() {
        return addressPojo;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public CommonPojo getAadharPojo() {
        return aadharPojo;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }
}
