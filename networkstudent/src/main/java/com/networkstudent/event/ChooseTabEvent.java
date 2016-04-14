package com.networkstudent.event;

/**
 * Created by Dream on 14-Dec-15.
 */
public class ChooseTabEvent {
    private String tabHeading;

    public ChooseTabEvent(String tabHeading) {
        this.tabHeading = tabHeading;
    }

    public String getTabHeading() {
        return tabHeading;
    }
}
