package com.example.charletdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;

import com.example.charletdemo.util.DeviceInfo;

public abstract class BaseScrollerView extends View implements GestureActionListener {

    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private static final float MAX_SCALE = 5.0f;    //最大缩放值
    private VelocityTracker mVelocityTracker;       //监听滑动速率
    private ScrollerRunnable mRunnable;             //滑动线程
    protected float mOffsetX, mOffsetY;             //缓冲
    protected int MAX_OFFSETX = (int) (10 * DeviceInfo.sAutoScaleX);
    protected int mMaxValX, mMaxValY;   // 右边界 下边界
    protected int mMinValX, mMinValY;   //左边界  上边界
    public float mDownX, mDownY;
    protected float mScale = 1;
    public boolean mIsEnabledEvent = true;
    private float mOnePixelScale;

    private boolean mEnabledDoubleTouch;    //是否支持双击

    private Runnable mLongClickRunnable;


    private boolean mIsDrag, mIsRoll, mIsDoubleHand, mIsLongClick;

    protected float mMoveXOffsetCount, mMoveYOffsetCount;

    private float mPointerSpacing, mPointerSpacingX, mPointerSpacingY;
    protected ScaleDetector mDetector = new ScaleDetector();
    protected PointF mDownCenter = new PointF();

    protected final int CLICK_OFFSET = (int) (10 * DeviceInfo.sAutoScaleX);
    protected final int CLICK_RADIUS = (int) (60 * DeviceInfo.sAutoScaleX);


    public BaseScrollerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BaseScrollerView(Context context, @Nullable AttributeSet attrs, int MAX_OFFSETX) {
        super(context, attrs);
        mVelocityTracker = VelocityTracker.obtain();
        if (MAX_OFFSETX >= 0) {
            this.MAX_OFFSETX = MAX_OFFSETX;
        }
    }


    public void setEnabledDoubleTouch(boolean enabled) {
        mEnabledDoubleTouch = enabled;
    }


    //在控件大小发生改变时调用。所以这里初始化会被调用一次
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mOnePixelScale = (float) (MAX_SCALE / Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2)));
        }
    }

    public void setInitScale(float scale) {
        mScale = scale;
        mDetector.absScale = scale;
    }

    public void setMaxValX(int maxValX) {
        mMaxValX = maxValX == 0 ? 0 : maxValX + MAX_OFFSETX;
    }

    public void setMinVal(int minValX, int minValY) {
        mMinValX = minValX;
        mMinValY = minValY;
    }


    public void setMaxVal(int maxValX, int maxValY) {
        mMaxValX = maxValX < 0 ? 0 : maxValX;
        mMaxValY = maxValY < 0 ? 0 : maxValY;

        mOffsetX = -(mOffsetX) < mMinValX ? -mMinValX : -(mOffsetX) > mMaxValX ? -mMaxValX : mOffsetX;
        mOffsetY = -(mOffsetY) < mMinValY ? -mMinValY : -(mOffsetY) > mMaxValY ? -mMaxValY : mOffsetY;
    }

    class ScrollerRunnable implements Runnable {

        private OverScroller mScroller;

        public ScrollerRunnable() {
            mScroller = new OverScroller(getContext());
        }

        public void cancel() {
            mScroller.forceFinished(true);
        }

        public boolean isFinished() {
            return mScroller.isFinished();
        }

        public void fling(int velocityX, int velocityY) {

            /**
             * int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY
             * X 轴滚动起点坐标  Y轴滚动起点坐标  X轴初速度  Y轴初速度  minX控制左边界  maxX 控制右边界  上下边界
             */
            mScroller.fling((int) -mOffsetX, (int) -mOffsetY, velocityX, velocityY, mMinValX, mMaxValX, mMinValY, mMaxValY);
        }

        @Override
        public void run() {

            if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
                mOffsetX = -mScroller.getCurrX();
                mOffsetY = -mScroller.getCurrY();

                onRoll(mScroller);
                postDelayed(this, 15);
                postInvalidateOnAnimation();
            } else {
                onFinishRoll();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mIsEnabledEvent) return super.onTouchEvent(event);

        int action = event.getAction();
        if (mEnabledDoubleTouch) {
            action &= 0xff;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                down(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1)
                    move(event);
                else if (event.getPointerCount() == 2) {
                    pointerMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                up(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                removeLongClick();
                onCancel();
                break;
        }
        invalidate();
        return true;
    }

    public void down(MotionEvent event) {
        mIsDoubleHand = false;
        mMoveXOffsetCount = 0;
        mMoveYOffsetCount = 0;
        mVelocityTracker.clear();
        mVelocityTracker.addMovement(event);
        if (mRunnable != null) {
            if (!mRunnable.isFinished()) {
                mRunnable.cancel();
                onRollCancel();
            }
            mRunnable = null;
        }
        mDownX = event.getX();
        mDownY = event.getY();

        //down时给一个延迟, 如果在这个时间内没有过多的移动并且没有弹起则为长按
        removeLongClick();
        mLongClickRunnable = new Runnable() {
            @Override
            public void run() {
                BaseScrollerView.this.onLongClick(mDownX, mDownY);
                mIsLongClick = true;
                BaseScrollerView.this.invalidate();
            }
        };
        mHandler.postDelayed(mLongClickRunnable, 500);
    }

    public void move(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        float dx = event.getX() - mDownX;
        float dy = event.getY() - mDownY;


        mMoveXOffsetCount += Math.abs(dx);
        mMoveYOffsetCount += Math.abs(dy);
        mDownX = event.getX();
        mDownY = event.getY();

        //手势预判
        if (mMoveYOffsetCount < CLICK_OFFSET) {
            return;
        }
        //执行到这,  说明手已经移动超过一定距离, 这个时候可以判断出用户滑动的方向了
        getParent().requestDisallowInterceptTouchEvent(true);
        mIsDrag = true;
        removeLongClick();
        onDrag(event, dx, dy);

        dx = dx / mScale;
        dy = dy / mScale;

        mOffsetX = -(mOffsetX + dx) < mMinValX ? -mMinValX : -(mOffsetX + dx) > mMaxValX ? -mMaxValX : mOffsetX + dx;
        mOffsetY = -(mOffsetY + dy) < mMinValY ? -mMinValY : -(mOffsetY + dy) > mMaxValY ? -mMaxValY : mOffsetY + dy;
    }

    public void up(MotionEvent event) {
        if (mIsDoubleHand) return;
        mHandler.removeCallbacks(mLongClickRunnable);
        mIsDrag = false;
        //如果没有已经执行过长按并且手指没有更多的移动则执行点击
        if (checkClick() && !mIsLongClick) {
            onClick(event);
            return;
        }
        mIsRoll = true;
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);
        int xV = (int) (mVelocityTracker.getXVelocity() / mScale);
        int yV = (int) (mVelocityTracker.getYVelocity() / mScale);
        mRunnable = new ScrollerRunnable();
        mRunnable.fling(-xV, -yV);
        post(mRunnable);
    }

    @Override
    public void pointerDown(MotionEvent event) {
        removeLongClick();
        //双指按下
        mIsDoubleHand = true;
        mDownCenter = getCenter(event);
        mPointerSpacing = pointerSpacing(event);
        mPointerSpacingX = pointerSpacingX(event);
        mPointerSpacingY = pointerSpacingY(event);
        mDownX = event.getX(0);
        mDownY = event.getY(0);
    }

    @Override
    public void pointerMove(MotionEvent event) {
        float spacing = pointerSpacing(event);
        //双指移动
        mDetector.scale = checkScale((spacing - mPointerSpacing) * mOnePixelScale);
        mDetector.scaleX = checkScale((pointerSpacingX(event) - mPointerSpacingX) * mOnePixelScale);
        mDetector.scaleY = checkScale((pointerSpacingY(event) - mPointerSpacingY) * mOnePixelScale);

        mDetector.absScale += mDetector.scale;
        mDetector.absScaleX += mDetector.scaleX;
        mDetector.absScaleY += mDetector.scaleY;
        mDetector.centerX = getCenter(event).x;
        mDetector.centerY = getCenter(event).y;

        onScale(mDetector);

        mPointerSpacing = pointerSpacing(event);
        mPointerSpacingX = pointerSpacingX(event);
        mPointerSpacingY = pointerSpacingY(event);
    }

    @Override
    public void pointerUp(MotionEvent event) {
        //双指抬起
        int index = event.getActionIndex();
        mDownX = event.getX(index == 0 ? 1 : 0);
        mDownY = event.getY(index == 0 ? 1 : 0);
    }

    @Override
    public void onScale(ScaleDetector detector) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScale, mScale, getWidth() / 2, getHeight() / 2);
        onDrawContent(canvas);
        canvas.restore();
    }


    protected void removeLongClick() {
        mIsLongClick = false;
        mHandler.removeCallbacks(mLongClickRunnable);
    }


    public float checkScale(float scale) {
        if (Float.isNaN(scale)) {
            return 0;
        }
        if (scale < -0.05f) return -0.05f;
        if (scale > 0.05f) return 0.05f;
        return scale;
    }

    /**
     * 计算两指之间的线长
     *
     * @param event
     * @return
     */
    public float pointerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.abs(Math.sqrt(x * x + y * y));
    }


    public float pointerSpacingX(MotionEvent event) {
        return Math.abs(event.getX(0) - event.getX(1));
    }

    public float pointerSpacingY(MotionEvent event) {
        return Math.abs(event.getY(0) - event.getY(1));
    }

    /**
     * 获取两指之间的中心点
     *
     * @param event
     * @return
     */
    public PointF getCenter(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    protected boolean checkClick() {
        return mMoveXOffsetCount < CLICK_OFFSET && mMoveYOffsetCount < CLICK_OFFSET;
    }

    public void onClick(MotionEvent event) {
    }

    public void onLongClick(float x, float y) {
    }

    public void onDrag(MotionEvent event, float dx, float dy) {
    }

    public void onFinishRoll() {
        mIsRoll = false;
    }

    protected abstract void onDrawContent(Canvas canvas);

    public void onRoll(OverScroller scroller) {
    }

    public void onRollCancel() {
    }

    public boolean isDrag() {
        return mIsDrag;
    }

    public boolean isRoll() {
        return mIsRoll;
    }

    public void setEnabledEvent(boolean enabled) {
        mIsEnabledEvent = enabled;
    }
}