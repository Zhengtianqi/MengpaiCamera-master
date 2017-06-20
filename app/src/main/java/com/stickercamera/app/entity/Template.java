package com.stickercamera.app.entity;

/**
 * 模板实体
 */
public class Template {

    private int path;
    private boolean isSelected;

    public Template(int path, boolean isSelected) {
        this.path = path;
        this.isSelected = isSelected;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
