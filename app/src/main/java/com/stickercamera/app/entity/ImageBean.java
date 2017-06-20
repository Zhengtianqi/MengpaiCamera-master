package com.stickercamera.app.entity;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class ImageBean implements Serializable {

    private static final long serialVersionUID = 5700379542385619938L;

    private long pictureId = -1;

    private int id;
    public String parentName;
    public long size;
    public String displayName;

    public String path;
    public boolean isChecked = false;
    private String uploadTaskId;
    private String pictureUrl;
    private int photoYear;
    private int photoMonth;
    private int monthlyNum;

    private Long photoId;
    private Date photoTakenDate;
    private String memo;
    private Boolean monthlyCover;

    private long photoDate;

    public ImageBean() {
        super();
    }

    public ImageBean(String parentName, long size, String displayName, String path, boolean isChecked) {
        super();
        this.parentName = parentName;
        this.size = size;
        this.displayName = displayName;
        this.path = path;
        this.isChecked = isChecked;
    }

    public long getPictureId() {
        return pictureId;
    }

    public void setPictureId(long pictureId) {
        this.pictureId = pictureId;
    }

    public Date getPhotoTakenDate() {
        return photoTakenDate;
    }

    public void setPhotoTakenDate(Date photoTakenDate) {
        this.photoTakenDate = photoTakenDate;
    }


    public long getPhotoDate() {
        return photoDate;
    }

    public void setPhotoDate(long photoDate) {
        this.photoDate = photoDate;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getPhotoYear() {
        return photoYear;
    }

    public void setPhotoYear(int photoYear) {
        this.photoYear = photoYear;
    }

    public int getPhotoMonth() {
        return photoMonth;
    }

    public void setPhotoMonth(int photoMonth) {
        this.photoMonth = photoMonth;
    }

    public int getMonthlyNum() {
        return monthlyNum;
    }

    public void setMonthlyNum(int monthlyNum) {
        this.monthlyNum = monthlyNum;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }


    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Boolean getMonthlyCover() {
        return monthlyCover;
    }

    public void setMonthlyCover(Boolean monthlyCover) {
        this.monthlyCover = monthlyCover;
    }


    public String getUploadTaskId() {
        return uploadTaskId;
    }

    public void setUploadTaskId(String uploadTaskId) {
        this.uploadTaskId = uploadTaskId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
