package com.github.ybq.android.spinkit;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ybq.
 */
public class SpinKitView extends ProgressBar {

    private Style mStyle;
    private int mColor;
    private Sprite mSprite;

    /*****************************************************************
     * @author williamXia
     * created at 2017/8/29 上午9:37
     * add timer to whatch the loop spinkit
     ******************************************************************/
    private boolean mIsNeedTimerWatch;
    private Timer timer;
    private int mFirstDelay = 4000;

    public OverLoopListener onOverLoopEvent;

    public void setOnOverLoopEvent(OverLoopListener onOverLoopEvent) {
        this.onOverLoopEvent = onOverLoopEvent;
    }

    public interface OverLoopListener {

        public void onOverLoopEvent();
    }

    public OverLoopListener getOnOverLoopEvent() {
        return onOverLoopEvent;
    }

    private class LoopWatch extends TimerTask {

        private SpinKitView kitView;

        public LoopWatch(SpinKitView pareV) {
            kitView = pareV;
        }

        @Override
        public void run() {
            //  Log.d("aaa", "loop watch now.....");
            stopTimer();
            kitView.post(new Runnable() {
                @Override
                public void run() {
                    kitView.setVisibility(INVISIBLE);
                    if (kitView.getOnOverLoopEvent() != null) {
                        kitView.onOverLoopEvent.onOverLoopEvent();
                    }
                }
            });

        }

    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;

        }
    }

    public void StartTimer() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new LoopWatch(this), mFirstDelay, mFirstDelay * 2);//3 seconds

    }

    public boolean ismIsNeedTimerWatch() {
        return mIsNeedTimerWatch;
    }

    public void setmIsNeedTimerWatch(boolean mIsNeedTimerWatch) {
        this.mIsNeedTimerWatch = mIsNeedTimerWatch;
    }


    public SpinKitView(Context context) {
        this(context, null);
    }

    public SpinKitView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SpinKitViewStyle);
    }

    public SpinKitView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.SpinKitView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpinKitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpinKitView, defStyleAttr,
                defStyleRes);
        mStyle = Style.values()[a.getInt(R.styleable.SpinKitView_SpinKit_Style, 0)];
        mColor = a.getColor(R.styleable.SpinKitView_SpinKit_Color, Color.WHITE);
        mIsNeedTimerWatch = a.getBoolean(R.styleable.SpinKitView_SpinKit_isNeedWatch, false);
        mFirstDelay = a.getInteger(R.styleable.SpinKitView_SpinKit_firstDelay, 4000);

        a.recycle();
        init();
        setIndeterminate(true);
    }

    private void init() {
        Sprite sprite = SpriteFactory.create(mStyle);
        setIndeterminateDrawable(sprite);
    }

    @Override
    public void setIndeterminateDrawable(Drawable d) {
        if (!(d instanceof Sprite)) {
            throw new IllegalArgumentException("this d must be instanceof Sprite");
        }
        setIndeterminateDrawable((Sprite) d);
    }

    public void setIndeterminateDrawable(Sprite d) {
        super.setIndeterminateDrawable(d);
        mSprite = d;
        if (mSprite.getColor() == 0) {
            mSprite.setColor(mColor);
        }
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (ismIsNeedTimerWatch() && visibility == VISIBLE) {
            Log.d("aaa", "setvisiblity now...");
            //StartTimer();

        }
    }

    @Override
    public Sprite getIndeterminateDrawable() {
        return mSprite;
    }

    public void setColor(int color) {
        this.mColor = color;
        if (mSprite != null) {
            mSprite.setColor(color);
        }
        invalidate();
    }

    @Override
    public void unscheduleDrawable(Drawable who) {
        super.unscheduleDrawable(who);
        if (who instanceof Sprite) {
            ((Sprite) who).stop();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mSprite != null && getVisibility() == VISIBLE) {
                mSprite.start();
            }
        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        if (screenState == View.SCREEN_STATE_OFF) {
            if (mSprite != null) {
                mSprite.stop();
            }
        }
    }

}
