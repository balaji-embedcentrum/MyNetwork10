package com.networkstudent.event;

/**
 * Created by Dream on 14-Dec-15.
 */
public class CloseProductDetailsScreenEvent {
    private boolean needToClose;

    public CloseProductDetailsScreenEvent(boolean needToClose) {
        this.needToClose = needToClose;
    }

    public boolean getCloseProductDetailsScreen() {
        return needToClose;
    }
}
