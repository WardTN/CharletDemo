package com.example.charletdemo.view;

import android.view.MotionEvent;
import android.widget.OverScroller;

public interface GestureActionListener {

    //第一根手指按下
    void down(MotionEvent event);

    //移动单指多指都走这个
    void move(MotionEvent event);

    //最后一根手指抬起
    void up(MotionEvent event);

    //第二根手指按下
    void pointerDown(MotionEvent event);

    //第二根手指移动
    void pointerMove(MotionEvent event);

    //第二根手指抬起
    void pointerUp(MotionEvent event);

    //在双指缩放时
    void onScale(ScaleDetector detector);

    //在单指点击时
    void onClick(MotionEvent event);

    //在单指长按时
    void onLongClick(float x, float y);

    //在事件被父类拦截时
    void onCancel();

    //在拖动时
    void onDrag(MotionEvent event, float dx, float dy);

    //在滚动完成成
    void onFinishRoll();

    //在滚动时
    void onRoll(OverScroller scroller);

    //在滚动被打断时
    void onRollCancel();

    public class ScaleDetector{
        public float scale, scaleX, scaleY;
        public float absScale, absScaleX, absScaleY;
        public float centerX, centerY;
    }
}