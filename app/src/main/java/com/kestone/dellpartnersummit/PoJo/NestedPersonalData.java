package com.kestone.dellpartnersummit.PoJo;

/**
 * Created by mac on 11/10/17.
 */

public class NestedPersonalData {

    String SpeakerName,SpeakerDesignation,SpeakerOrganization,SpeakerImageURL,SpeakerType,SpeakerEmailID;

    public String getSpeakerName() {
        return SpeakerName;
    }

    public String getSpeakerEmailID() {
        return SpeakerEmailID;
    }

    public void setSpeakerEmailID(String speakerEmailID) {
        SpeakerEmailID = speakerEmailID;
    }

    public void setSpeakerName(String speakerName) {
        SpeakerName = speakerName;
    }

    public String getSpeakerDesignation() {
        return SpeakerDesignation;
    }

    public void setSpeakerDesignation(String speakerDesignation) {
        SpeakerDesignation = speakerDesignation;
    }

    public String getSpeakerOrganization() {
        return SpeakerOrganization;
    }

    public void setSpeakerOrganization(String speakerOrganization) {
        SpeakerOrganization = speakerOrganization;
    }

    public String getSpeakerImageURL() {
        return SpeakerImageURL;
    }

    public void setSpeakerImageURL(String speakerImageURL) {
        SpeakerImageURL = speakerImageURL;
    }

    public String getSpeakerType() {
        return SpeakerType;
    }

    public void setSpeakerType(String speakerType) {
        SpeakerType = speakerType;
    }
}
