package com.kestone.dellpartnersummit.PoJo;

public class SpeakerData {

    String ID,Name,Designation,Organization,EmailID,Mobile,PassportNo,RegistrationType,ImageURL,ProfileDesc;

    public String getID() {
        return ID;
    }

    public String getProfileDesc() {
        return ProfileDesc;
    }

    public void setProfileDesc(String profileDesc) {
        ProfileDesc = profileDesc;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getOrganization() {
        return Organization;
    }

    public void setOrganization(String organization) {
        Organization = organization;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassportNo() {
        return PassportNo;
    }

    public void setPassportNo(String passportNo) {
        PassportNo = passportNo;
    }

    public String getRegistrationType() {
        return RegistrationType;
    }

    public void setRegistrationType(String registrationType) {
        RegistrationType = registrationType;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
