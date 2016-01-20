package com.balaji.mynetwork10.event;

import com.balaji.mynetwork10.model.ProfileData;

import java.util.ArrayList;

/**
 * Created by Dream on 14-Dec-15.
 */
public class GetSelectedLocationEvent {
    private ArrayList<ProfileData> profileDataArrayList;

    public GetSelectedLocationEvent(ArrayList<ProfileData> profileDataArrayList) {
        this.profileDataArrayList = profileDataArrayList;
    }

    public ArrayList<ProfileData> getLocationData() {
        return profileDataArrayList;
    }
}
