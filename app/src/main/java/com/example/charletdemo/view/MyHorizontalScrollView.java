package com.example.charletdemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {

    private Paint mPaint;   //中线画笔
    private boolean isBanEvent; //是否禁止Event事件

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    //分发事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isBanEvent) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
        //dispatchTouchEvent 返回值为false 意味着事件停止往子View分发,并往父控件回溯
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制竖直中线
        canvas.drawLine(getWidth() / 2 + getScrollX(), 0, getWidth() / 2 + getScrollX(), canvas.getHeight(), mPaint);
    }

    /**
     * 执行滚动动画
     *
     * @param x
     * @param y
     */
    public void scrollToPosition(int x, int y) {
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(this, "scrollX", x);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(this, "scrollY", y);
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(200L);
        animators.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isBanEvent = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isBanEvent = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isBanEvent = false;
            }
        });
        animators.playTogether(xTranslate, yTranslate);
        animators.start();
    }
}
