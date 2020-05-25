/**
 * Copyright (c) 2013-2014, Rinc Liu (http://rincliu.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class HScrollViewX extends HorizontalScrollView{
    private static final long DELAY = 100;

    private int currentScroll;

    private Runnable scrollCheckTask;

    /**
     * @param context
     */
    public HScrollViewX(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public HScrollViewX(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HScrollViewX(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        scrollCheckTask = new Runnable() {
            @Override
            public void run() {
                int newScroll = getScrollX();
                if (currentScroll == newScroll) {
                    if (onScrollListener != null) {
                        onScrollListener.onScrollStopped();
                    }
                } else {
                    if (onScrollListener != null) {
                        onScrollListener.onScrolling();
                    }
                    currentScroll = getScrollX();
                    postDelayed(scrollCheckTask, DELAY);
                }
            }
        };
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentScroll = getScrollX();
                    postDelayed(scrollCheckTask, DELAY);
                }
                return false;
            }
        });
    }


    private ScrollListener onScrollListener;

    /**
     * @param ScrollListener
     */
    public void setScrollListener(ScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(x, y, oldX, oldY);
        }
    }

    /**
     * @param child
     * @return
     */
    public boolean isChildVisible(View child) {
        if (child == null) {
            return false;
        }
        Rect scrollBounds = new Rect();
        getHitRect(scrollBounds);
        return child.getLocalVisibleRect(scrollBounds);
    }

    /**
     * @return
     */
    public boolean isAtLeft() {
        return getScrollX() <= 0;
    }

    public boolean isInLeftRange(int cx) {
        return getScrollX() <= cx;
    }

    /**
     * @return
     */
    public boolean isAtRight() {

        return getChildAt(getChildCount() - 1).getRight() + getPaddingRight() == getWidth() + getScrollX();
    }
}
