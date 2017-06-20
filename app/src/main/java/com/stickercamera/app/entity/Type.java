package com.stickercamera.app.entity;

import java.util.List;

/**
 * 相同图片张数的不同排列实体
 */
public class Type {

    private List<ImageItem> pic;

    public List<ImageItem> getPic() {
        return pic;
    }

    public void setPic(List<ImageItem> pic) {
        this.pic = pic;
    }
}
