package com.balaji.mynetwork10.model;

import com.google.gson.Gson;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

/**
 * Created by Dream on 03-Jan-16.
 */
public class ProfileData {
    private String objectId;
    private String userId;
    private int profileCode;
    private String profileName;
    private String profileAddr1;
    private String profileAddr2;
    private String profileCity;
    private int profileZip;
    private String profileState;
    private String profileCountry;
    private ParseGeoPoint ProfileGeopoint;
    private ParseFile profileImage;
    private int profileViews;
    private String profileCategory;

    public ParseGeoPoint getProfileGeopoint() {
        return ProfileGeopoint;
    }

    public void setProfileGeopoint(ParseGeoPoint profileGeopoint) {
        ProfileGeopoint = profileGeopoint;
    }

    public int getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(int profileViews) {
        this.profileViews = profileViews;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(int profileCode) {
        this.profileCode = profileCode;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileAddr1() {
        return profileAddr1;
    }

    public void setProfileAddr1(String profileAddr1) {
        this.profileAddr1 = profileAddr1;
    }

    public String getProfileAddr2() {
        return profileAddr2;
    }

    public void setProfileAddr2(String profileAddr2) {
        this.profileAddr2 = profileAddr2;
    }

    public String getProfileCity() {
        return profileCity;
    }

    public void setProfileCity(String profileCity) {
        this.profileCity = profileCity;
    }

    public int getProfileZip() {
        return profileZip;
    }

    public void setProfileZip(int profileZip) {
        this.profileZip = profileZip;
    }

    public String getProfileState() {
        return profileState;
    }

    public void setProfileState(String profileState) {
        this.profileState = profileState;
    }

    public String getProfileCountry() {
        return profileCountry;
    }

    public void setProfileCountry(String profileCountry) {
        this.profileCountry = profileCountry;
    }

    public ParseFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ParseFile profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileCategory() {
        return profileCategory;
    }

    public void setProfileCategory(String profileCategory) {
        this.profileCategory = profileCategory;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
