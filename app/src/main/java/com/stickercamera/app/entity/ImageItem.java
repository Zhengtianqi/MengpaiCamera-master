package com.stickercamera.app.entity;

import java.util.List;

/**
 * 单张图片实体
 */
public class ImageItem {

    private List<Coordinates> coordinates;

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }
}
