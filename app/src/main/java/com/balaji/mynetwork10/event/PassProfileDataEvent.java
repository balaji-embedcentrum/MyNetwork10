package com.balaji.mynetwork10.event;

import com.balaji.mynetwork10.model.ProfileData;

/**
 * Created by Dream on 14-Dec-15.
 */
public class PassProfileDataEvent {
    private ProfileData profileData;

    public PassProfileDataEvent(ProfileData profileData) {
        this.profileData = profileData;
    }

    public ProfileData getProfileData() {
        return profileData;
    }
}
