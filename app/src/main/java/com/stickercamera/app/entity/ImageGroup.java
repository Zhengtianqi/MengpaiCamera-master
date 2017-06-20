package com.stickercamera.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImageGroup {

    private static final long serialVersionUID = 1L;
    public boolean isSelected = false;
    /**
     * 文件夹下所有图片
     */
    public List<ImageBean> imageSets = new ArrayList<ImageBean>();
    /**
     * 文件夹名
     */
    private String folderName = "";

    public ImageGroup() {
        super();
    }

    public ImageGroup(String folderName, List<ImageBean> sets) {
        super();
        this.folderName = folderName;
        this.imageSets = sets;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * 获取第一张图片的路径(作为封面)
     *
     * @return
     */
    public String getFirstImgPath() {
        if (imageSets.size() > 0) {
            return imageSets.get(1).path;
        }
        return "";
    }

    /**
     * 获取图片数量
     *
     * @return
     */
    public int getImageCount() {
        return imageSets.size();
    }

    public List<ImageBean> getImageSets() {
        return imageSets;
    }

    @Override
    public String toString() {
        return "ImageGroup [firstImgPath=" + getFirstImgPath() + ", folderName=" + folderName
                + ", imageCount=" + getImageCount() + "]";
    }

    /**
     * 只要图片所在的文件夹名称(folderName)相同就属于同一个图片组
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageGroup)) {
            return false;
        }
        return folderName.equals(((ImageGroup) o).folderName);
    }
}
