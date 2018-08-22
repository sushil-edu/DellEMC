package com.kestone.dellpartnersummit.PoJo;


public class MyScheduledData {

    String FromEmailID,ToEmailID,Location,Name,Designation,Organization,Day,Month,Hours,Minutes,ImageUrl;

    public String getDay() {
        return Day;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getHours() {
        return Hours;
    }

    public void setHours(String hours) {
        Hours = hours;
    }

    public String getMinutes() {
        return Minutes;
    }

    public void setMinutes(String minutes) {
        Minutes = minutes;
    }

    public String getFromEmailID() {
        return FromEmailID;
    }

    public void setFromEmailID(String fromEmailID) {
        FromEmailID = fromEmailID;
    }

    public String getToEmailID() {
        return ToEmailID;
    }

    public void setToEmailID(String toEmailID) {
        ToEmailID = toEmailID;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
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
}
