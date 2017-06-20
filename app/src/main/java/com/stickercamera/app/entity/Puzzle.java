package com.stickercamera.app.entity;

import java.util.List;

/**.
 * 所有拼图类型实体
 */
public class Puzzle {

    private List<Type> style;

    public List<Type> getStyle() {
        return style;
    }

    public void setStyle(List<Type> style) {
        this.style = style;
    }
}
