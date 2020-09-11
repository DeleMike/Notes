package com.mikeinvents.notes.models;


public class Note {
    private int mId;
    private String mTitle;
    private String mDetails;
    private String mDate;
    private boolean mIsSelected;

    public Note(){}

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setDetails(String mDetails) {
        this.mDetails = mDetails;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDetails() {
        return mDetails;
    }

    public String getDate() {
        return mDate;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }


    public boolean isSelected() {
        return mIsSelected;
    }
}
