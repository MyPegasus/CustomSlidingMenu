package com.example.mypegasus.customslidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by MyPegasus on 2015/9/25.
 */
public class MainUI extends RelativeLayout {
    private Context context;
    private FrameLayout leftMenu;
    private FrameLayout middleMenu;
    private FrameLayout middleMask;
    private FrameLayout rightMenu;
    private Scroller mScroller;
    public static final int LEFT_ID = 0xaabbcc;
    public static final int MIDDLE_ID = 0xaaccbb;
    public static final int RIGHT_ID = 0xbbccaa;


    public MainUI(Context context) {
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        mScroller = new Scroller(context, new DecelerateInterpolator());
        leftMenu = new FrameLayout(context);
        middleMenu = new FrameLayout(context);
        middleMask = new FrameLayout(context);
        rightMenu = new FrameLayout(context);
        leftMenu.setBackgroundColor(Color.BLUE);
        middleMenu.setBackgroundColor(Color.GREEN);
        middleMask.setBackgroundColor(0x88000000);
        rightMenu.setBackgroundColor(Color.RED);
        leftMenu.setId(LEFT_ID);
        middleMenu.setId(MIDDLE_ID);
        middleMenu.setId(RIGHT_ID);

        ListView listView = new ListView(context);
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String[] {"极客学院", "Hello World!", "极客学院", "Hello World!", "极客学院", "Hello World!", "极客学院", "Hello World!", "极客学院", "Hello World!", "极客学院", "Hello World!", "极客学院", "Hello World!", "极客学院", "Hello World!"}));
        middleMenu.addView(listView);

        addView(middleMenu);
        addView(leftMenu);
        addView(rightMenu);
        addView(middleMask);
        middleMask.setAlpha(0);
    }

    public float onMiddleMask() {
        System.out.println("透明度：" + middleMask.getAlpha());
        return middleMask.getAlpha();
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        onMiddleMask();
        int curX = Math.abs(getScrollX());
        float alpha = curX /(float) leftMenu.getMeasuredWidth();
        middleMask.setAlpha(alpha);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        middleMenu.measure(widthMeasureSpec, heightMeasureSpec);
        middleMask.measure(widthMeasureSpec, heightMeasureSpec);
        int realWidth = MeasureSpec.getSize(widthMeasureSpec);
        int tempWidthMeasure = MeasureSpec.makeMeasureSpec((int) (0.8f * realWidth), MeasureSpec.EXACTLY);
        leftMenu.measure(tempWidthMeasure, heightMeasureSpec);
        rightMenu.measure(tempWidthMeasure, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        middleMenu.layout(l, t, r, b);
        middleMask.layout(l, t, r, b);
        leftMenu.layout(l - leftMenu.getMeasuredWidth(), t, r - middleMenu.getMeasuredWidth(), b);
//        rightMenu.layout(l + middleMenu.getMeasuredWidth(), t, l + middleMenu.getMeasuredWidth() + rightMenu.getMeasuredWidth(), b);
        rightMenu.layout(l + middleMenu.getMeasuredWidth(), t, r + rightMenu.getMeasuredWidth(), b);
    }

    private boolean isTestComplete;
    private boolean isLeftRightEvent;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isTestComplete) {
            getEventType(ev);
            return true;
        }
        if (isLeftRightEvent) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    int curScrollX = getScrollX();
                    int dis_x = (int) (ev.getX() - point.x);
                    int expectX = -dis_x + curScrollX;
//                    System.out.println(String.format("curScrollX = %d, dis_X = %d, expectX = %d", curScrollX, dis_x, expectX));
                    int finalX = 0;
                    if(expectX < 0) {
                        finalX = Math.max(expectX, -leftMenu.getMeasuredWidth());
                    } else {
                        finalX = Math.min(expectX, rightMenu.getMeasuredWidth());
                    }
                    scrollTo(finalX, 0);
                    point.x = (int) ev.getX();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    curScrollX = getScrollX();
                    /**
                     * Scroller 的 startScroll(int startX, int startY, int dx, int dy)
                     * startX, startY 为起始坐标
                     * dx, dy 为偏移量
                     * 可选参数为duration 动画持续时间
                     * */
                    if(Math.abs(curScrollX) >= leftMenu.getMeasuredWidth() >> 1) {
                        if(curScrollX < 0) {
//                            System.out.println(String.format("curScrollX:%d, to:%d", curScrollX, -leftMenu.getMeasuredWidth() - curScrollX));
                            mScroller.startScroll(curScrollX, 0, -leftMenu.getMeasuredWidth() - curScrollX, 200);
                        } else {
                            /*System.out.println("middleMenu.getWidth() = " + middleMenu.getWidth());
                            System.out.println(String.format("Middle width:%d, right width:%d", middleMenu.getMeasuredWidth(), rightMenu.getMeasuredWidth()));
                            System.out.println(String.format("curScrollX:%d, to:%d", curScrollX, rightMenu.getMeasuredWidth() - curScrollX));*/
                            mScroller.startScroll(curScrollX, 0, rightMenu.getMeasuredWidth() - curScrollX, 200);
                        }
                    } else {
                        mScroller.startScroll(curScrollX, 0, -curScrollX, 0);
                    }
                    invalidate();
                    isLeftRightEvent = false;
                    isTestComplete = false;
                    break;
            }
        } else {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                    isLeftRightEvent = false;
                    isTestComplete = false;
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(!mScroller.computeScrollOffset()) return;
        int tempX = mScroller.getCurrX();
        scrollTo(tempX, 0);
    }

    private Point point = new Point();
    private static final int TEST_DIS = 20;

    private void getEventType(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                point.x = (int) ev.getX();
                point.y = (int) ev.getY();
                super.dispatchTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                int dX = Math.abs((int) ev.getX() - point.x);
                int dY = Math.abs((int) ev.getY() - point.y);
                if (dX >= TEST_DIS && dX >= dY) { // 左右滑动
                    isLeftRightEvent = true;
                    isTestComplete = true;
                    point.x = (int) ev.getX();
                    point.y = (int) ev.getY();
                } else if (dY >= TEST_DIS && dY > dX) { // 上下滑动
                    isLeftRightEvent = false;
                    isTestComplete = true;
                    point.x = (int) ev.getX();
                    point.y = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                super.dispatchTouchEvent(ev);
                /*isLeftRightEvent = false;
                isTestComplete = false;*/
                break;
        }
    }
}
