package com.musicbase.ui.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MyTouchListener implements View.OnTouchListener {

    private int lastX;
    private int lastY;

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //将点下的点的坐标保存
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //计算出需要移动的距离
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                //将移动距离加上，现在本身距离边框的位置
                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;

                if (left>=((ViewGroup) v.getParent()).getWidth()-v.getWidth()) {
                    left = ((ViewGroup) v.getParent()).getWidth()-v.getWidth();
                }
                if (top>=((ViewGroup) v.getParent()).getHeight()-v.getHeight()) {
                    top = ((ViewGroup) v.getParent()).getHeight()-v.getHeight();
                }
                if(left<=0){
                    left=0;
                }
                if(top<=0) {
                    top=0;
                }
                //获取到layoutParams然后改变属性，在设置回去
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v
                        .getLayoutParams();
                layoutParams.height = v.getHeight();
                layoutParams.width = v.getWidth();
                layoutParams.leftMargin = left;
                layoutParams.topMargin = top;
                v.setLayoutParams(layoutParams);
                //记录最后一次移动的位置
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
        }
        return true;
    }
}

