package com.example.charletdemo.bean;

import android.graphics.Rect;
import android.graphics.RectF;

public class JRectF extends RectF {

    public RectF drawRect;

    public float drawLeft;
    public float drawTop;
    public float drawRight;
    public float drawBottom;

    public JRectF() {
    }

    public JRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }

    public JRectF(RectF r) {
        super(r);
    }

    public JRectF(Rect r) {
        super(r);
    }


}