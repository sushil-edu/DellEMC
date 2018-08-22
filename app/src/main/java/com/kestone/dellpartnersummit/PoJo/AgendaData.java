package com.kestone.dellpartnersummit.PoJo;

import org.json.JSONArray;

public class AgendaData {

    String ID,Date,Time,TitleTrack,IsMultitrack,GroupingID,Description,IsRateable,Rating,
    RefAgendaID,Location,Track,Heading;
    JSONArray Speakers;

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getRefAgendaID() {
        return RefAgendaID;
    }

    public String getTrack() {
        return Track;
    }

    public void setTrack(String track) {
        Track = track;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setRefAgendaID(String refAgendaID) {
        RefAgendaID = refAgendaID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public JSONArray getSpeakers() {
        return Speakers;
    }

    public void setSpeakers(JSONArray speakers) {
        Speakers = speakers;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTitleTrack() {
        return TitleTrack;
    }

    public void setTitleTrack(String titleTrack) {
        TitleTrack = titleTrack;
    }

    public String getIsMultitrack() {
        return IsMultitrack;
    }

    public void setIsMultitrack(String isMultitrack) {
        IsMultitrack = isMultitrack;
    }

    public String getGroupingID() {
        return GroupingID;
    }

    public void setGroupingID(String groupingID) {
        GroupingID = groupingID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getIsRateable() {
        return IsRateable;
    }

    public void setIsRateable(String isRateable) {
        IsRateable = isRateable;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
