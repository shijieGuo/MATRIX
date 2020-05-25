package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.com.williamxia.wipack.R;


/**
 * Created by williamXia on 2017/8/8.
 */

public class CFourStatusLed extends View {

    public static final int State_Off = 0;
    public static final int State_Start = 1;
    public static final int State_Sig = 2;
    public static final int State_Clip = 3;

    private int LedState;
    private int iTag;
    private int typeValue;


    private int color_stateOff;
    private int color_stateStart;
    private int color_stateSig;
    private int color_stateClip;

    public int getColor_stateOff() {
        return color_stateOff;
    }

    public int getColor_stateStart() {
        return color_stateStart;
    }

    public int getColor_stateSig() {
        return color_stateSig;
    }

    public int getColor_stateClip() {
        return color_stateClip;
    }

    public int getLedState() {
        return LedState;
    }

    public void setColor_stateClip(int color_stateClip) {
        this.color_stateClip = color_stateClip;
        invalidate();
    }

    public void setColor_stateOff(int color_stateOff) {
        this.color_stateOff = color_stateOff;
        invalidate();
    }

    public void setColor_stateStart(int color_stateStart) {
        this.color_stateStart = color_stateStart;
        invalidate();
    }

    public void setColor_stateSig(int color_stateSig) {
        this.color_stateSig = color_stateSig;
        invalidate();
    }

    public int getiTag() {
        return iTag;
    }

    public int getTypeValue() {
        return typeValue;
    }
    //---


    public void setLedState(int ledState) {
        LedState = ledState;
        invalidate();
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }
    //---

    public CFourStatusLed(Context context) {
        this(context, null);
    }

    public CFourStatusLed(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CFourStatusLed(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initital();
        TypedArray resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CFourStatusLed, defStyleAttr, 0);
        int n = resAry.getIndexCount();
        //
        for (int i = 0; i < n; i++) {
            int index = resAry.getIndex(i);//value

                if(index== R.styleable.CFourStatusLed_LedState)
                    LedState = resAry.getInteger(index, State_Off);

                else if(index==  R.styleable.CFourStatusLed_ledTag)
                    iTag = resAry.getInteger(index, 0);

                else if(index==  R.styleable.CFourStatusLed_ledTypeValue)
                    typeValue = resAry.getInteger(index, 0);

                else  if(index==  R.styleable.CFourStatusLed_ledColor_clip)
                    color_stateClip=resAry.getColor(index, Color.GRAY);

                else if(index==  R.styleable.CFourStatusLed_ledColor_sig)
                    color_stateSig=resAry.getColor(index, Color.GRAY);

                else if(index==  R.styleable.CFourStatusLed_ledColor_start)
                    color_stateStart=resAry.getColor(index, Color.GRAY);
                else if(index==  R.styleable.CFourStatusLed_ledColor_off)
                    color_stateOff=resAry.getColor(index, Color.GRAY);



        }
        resAry.recycle();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private float mWidth, mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
            // middleHeight = mHeight / 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    private void initital() {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.FILL);
    }


    private Paint mainPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // super.onDraw(canvas);
        int currentColor = 0;
        switch (LedState) {
            case State_Off:
                currentColor = color_stateOff;
                break;
            case State_Start:
                currentColor = color_stateStart;
                break;
            case State_Sig:
                currentColor = color_stateSig;
                break;
            case State_Clip:
                currentColor = color_stateClip;
                break;
        }
        mainPaint.setColor(currentColor);
        canvas.drawColor(currentColor);
    }


}
