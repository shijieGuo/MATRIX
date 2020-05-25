package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.com.williamxia.wipack.R;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/8/5.
 * segment totoal-1,forexample if it has 16 segs,so it maxvalue is 15
 */

public class CMeter extends View {

    //exposed items
    private int mValue;
    private int MaxValue; //segment totoal-1,forexample if it has 16 segs,so it maxvalue is 15

    private int leftRightGap;
    private int upDownGap;
    private int unActiveLedColor, activeLedColor;
    private boolean isReverse;

    private int clipColor, sigColor;

    private int clipNum;
    private int sigNum;

    private boolean isSmooth;


    //inner hidden
    private Paint mainPaint;
    private float remainSpace;
    private float step;


    //---
    public boolean isReverse() {
        return isReverse;
    }

    public int getmValue() {
        return mValue;
    }

    public int getMaxValue() {
        return MaxValue;
    }


    public int getLeftRightGap() {
        return leftRightGap;
    }

    public int getUpDownGap() {
        return upDownGap;
    }

    public int getUnActiveLedColor() {
        return unActiveLedColor;
    }

    public int getActiveLedColor() {
        return activeLedColor;
    }

    public void setmValue(int mValue) {
        this.mValue = mValue;
        recalcStep();
    }

    public void setMaxValue(int maxValue) {
        MaxValue = maxValue;
        recalcStep();
    }


    public void setLeftRightGap(int leftRightGap) {
        this.leftRightGap = leftRightGap;
        recalcStep();
    }

    public void setUpDownGap(int upDownGap) {
        this.upDownGap = upDownGap;
        recalcStep();
    }

    public void setUnActiveLedColor(int unActiveLedColor) {
        this.unActiveLedColor = unActiveLedColor;
        invalidate();
    }

    public void setActiveLedColor(int activeLedColor) {
        this.activeLedColor = activeLedColor;
        invalidate();
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
        invalidate();
    }

    public int getClipNum() {
        return clipNum;
    }

    public int getSigNum() {
        return sigNum;
    }

    public void setClipNum(int clipNum) {
        this.clipNum = clipNum;
        invalidate();
    }

    public void setSigNum(int sigNum) {
        this.sigNum = sigNum;
        invalidate();
    }

    public boolean isSmooth() {
        return isSmooth;
    }

    public void setSmooth(boolean smooth) {
        isSmooth = smooth;
        invalidate();
    }

    public int getClipColor() {
        return clipColor;
    }

    public int getSigColor() {
        return sigColor;
    }

    public void setClipColor(int clipColor) {
        this.clipColor = clipColor;
        invalidate();
    }

    public void setSigColor(int sigColor) {
        this.sigColor = sigColor;
        invalidate();
    }

    public CMeter(Context context) {
        this(context, null);
    }

    public CMeter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CMeter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CMeter, defStyleAttr, 0);

        int atrCount = resAry.getIndexCount();
        for (int i = 0; i < atrCount; i++) {
            int index = resAry.getIndex(i);

            if (index == R.styleable.CMeter_meterActiveLedColor)
                activeLedColor = resAry.getColor(index, getResources().getColor(R.color.lime));

            else if (index == R.styleable.CMeter_meterUnactiveLedColor)
                unActiveLedColor = resAry.getColor(index, getResources().getColor(R.color.gray));

            else if (index == R.styleable.CMeter_meterIsReverse)
                isReverse = resAry.getBoolean(index, false);

            else if (index == R.styleable.CMeter_meterUpDownGap)
                upDownGap = resAry.getInteger(index, 0);


            else if (index == R.styleable.CMeter_meterLeftRightGap)
                leftRightGap = resAry.getInteger(index, 0);

            else if (index == R.styleable.CMeter_meterMaxValue)
                MaxValue = resAry.getInteger(index, 0);

            else if (index == R.styleable.CMeter_meterValue) {
                mValue = (resAry.getInteger(index, 0));
                qDebug.qLog("meter value read from resAry is " + mValue);
            } else if (index == R.styleable.CMeter_meterClipNum)
                clipNum = resAry.getInteger(index, 0);

            else if (index == R.styleable.CMeter_meterSigNum)
                sigNum = resAry.getInteger(index, 0);

            else if (index == R.styleable.CMeter_meterIsSmooth)
                isSmooth = resAry.getBoolean(index, false);


            else if (index == R.styleable.CMeter_meterClipColor)
                clipColor = resAry.getColor(index, getResources().getColor(R.color.red));

            else if (index == R.styleable.CMeter_meterSigColor)
                sigColor = resAry.getColor(index, getResources().getColor(R.color.yellow));


        }
        resAry.recycle();
        initalParameters();
    }

    private void initalParameters() {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.FILL);
        mainPaint.setStrokeWidth(10f);
        //----
        recalcStep();

    }


    private float smallRectHeight = 0;

    private void recalcStep() {

        if (mValue > MaxValue) mValue = MaxValue;
        else if (mValue < 0) mValue = 0;


        step = mHeight / (MaxValue);
        smallRectHeight = step - upDownGap;
        remainSpace = mHeight - step * MaxValue;
        invalidate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        recalcStep();
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

    @Override
    protected void onDraw(Canvas canvas) { //value :[0,maxvalue]  forexampe maxvlue is 15,it has 16 segments total
        super.onDraw(canvas);
        canvas.save();
        int normalSeg = MaxValue - clipNum - sigNum;
        if (!isSmooth) {
            drawRects_backGround(canvas);
            if (mValue > normalSeg + sigNum && mValue <= normalSeg + sigNum + clipNum) {
                drawRects_clipPart(canvas);
            } else if (mValue > normalSeg && mValue <= normalSeg + sigNum) {
                drawRects_sigPart(canvas);
            } else {
                drawRects_normalPart(canvas);
            }
        } else {
            drawRects_backGround_smooth(canvas);
            if (mValue > normalSeg + sigNum && mValue <= normalSeg + sigNum + clipNum) {
                drawRects_clipPart_smooth(canvas);
            } else if (mValue > normalSeg && mValue <= normalSeg + sigNum) {
                drawRects_sigPart_smooth(canvas);
            } else {
                drawRects_normalPart_smooth(canvas);
            }

        }
        canvas.restore();

    }

    private void drawRects_backGround_smooth(Canvas canvas) {
        mainPaint.setColor(unActiveLedColor);//draw normal
        RectF mrect = new RectF();
        mrect.left = leftRightGap;
        mrect.right = mWidth - 2 * leftRightGap;
        mrect.top = remainSpace / 2;
        mrect.bottom = mHeight - remainSpace / 2;
        canvas.drawRect(mrect, mainPaint);
    }

    private void drawRects_clipPart_smooth(Canvas canvas) {
        int normalSeg = MaxValue - clipNum - sigNum;
        qDebug.qLog("draw clip part  because mvalue is " + mValue);
        mainPaint.setColor(activeLedColor);//draw normal

        RectF mrect = new RectF();
        mrect.left = leftRightGap;
        mrect.right = mWidth - 2 * leftRightGap;
        if (isReverse) {
            mrect.top = remainSpace / 2;
            mrect.bottom = mrect.top + normalSeg * step;
        } else {
            mrect.top = mHeight - remainSpace / 2 - (normalSeg) * step;
            mrect.bottom = mHeight - remainSpace / 2;
        }

        canvas.drawRect(mrect, mainPaint);

        mainPaint.setColor(sigColor);//draw sigcolor part
        if (isReverse) {
            mrect.top = mrect.bottom;
            mrect.bottom = mrect.top + sigNum * step;
        } else {
            mrect.bottom = mrect.top;
            mrect.top = mrect.top - sigNum * step;

        }
        canvas.drawRect(mrect, mainPaint);

        mainPaint.setColor(clipColor);//draw clip color part

        if (isReverse) {
            mrect.top = mrect.bottom;
            mrect.bottom = mrect.top + (mValue - sigNum - normalSeg) * step;
        } else {
            mrect.bottom = mrect.top;
            mrect.top = mrect.top - (mValue - sigNum - normalSeg) * step;

        }
        canvas.drawRect(mrect, mainPaint);

    }


    private void drawRects_sigPart_smooth(Canvas canvas) {
        int normalSeg = MaxValue - clipNum - sigNum;
        qDebug.qLog("draw sig part  because mvalue is " + mValue);

        mainPaint.setColor(activeLedColor);
        RectF mrect = new RectF();
        mrect.left = leftRightGap;
        mrect.right = mWidth - 2 * leftRightGap;
        if (isReverse) {
            mrect.top = remainSpace / 2;
            mrect.bottom = mrect.top + normalSeg * step;
        } else {
            mrect.top = mHeight - remainSpace / 2 - (normalSeg) * step;
            mrect.bottom = mHeight - remainSpace / 2;
        }
        canvas.drawRect(mrect, mainPaint);

        mainPaint.setColor(sigColor);
        float bottom = mrect.bottom;

        if (isReverse) {
            mrect.top = mrect.bottom;
            mrect.bottom = mrect.top + (mValue - normalSeg) * step;
        } else {
            mrect.bottom = mrect.top;
            mrect.top = mrect.top - (mValue - normalSeg) * step;

        }
        canvas.drawRect(mrect, mainPaint);

    }


    private void drawRects_normalPart_smooth(Canvas canvas) {
        // qDebug.qLog("draw normal part  because mvalue is " + mValue);
        mainPaint.setColor(activeLedColor);

        RectF mrect = new RectF();
        mrect.left = leftRightGap;
        mrect.right = mWidth - 2 * leftRightGap;
        if (isReverse) {
            mrect.top = remainSpace / 2;
            mrect.bottom = mrect.top + mValue * step;
        } else {
            mrect.top = mHeight - remainSpace / 2 - mValue * step;
            mrect.bottom = mHeight - remainSpace / 2;
        }
        canvas.drawRect(mrect, mainPaint);

    }


    /*****************************************************************
     * @author williamXia
     * created at 2017/8/7 下午1:41
     ******************************************************************/

    private void drawRects_backGround(Canvas canvas) {
        mainPaint.setColor(unActiveLedColor);//draw normal
        for (int i = 0; i < MaxValue; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            mrect.top = remainSpace / 2 + i * step;
            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }
    }

    private void drawRects_clipPart(Canvas canvas) {
        int normalSeg = MaxValue - clipNum - sigNum;
        qDebug.qLog("draw clip part  because mvalue is " + mValue);
        mainPaint.setColor(activeLedColor);//draw normal
        for (int i = 1; i <= normalSeg; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            if (isReverse)
                mrect.top = remainSpace / 2 + (i - 1) * step;
            else
                mrect.top = mHeight - remainSpace / 2 - (i) * step;

            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }

        mainPaint.setColor(sigColor);//draw sigcolor part

        for (int i = normalSeg + 1; i <= normalSeg + sigNum; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            if (isReverse)
                mrect.top = remainSpace / 2 + (i - 1) * step;
            else
                mrect.top = mHeight - remainSpace / 2 - (i) * step;
            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }

        mainPaint.setColor(clipColor);//draw clip color part
        for (int i = normalSeg + sigNum + 1; i <= mValue; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            if (isReverse)
                mrect.top = remainSpace / 2 + (i - 1) * step;
            else
                mrect.top = mHeight - remainSpace / 2 - (i) * step;
            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }
    }


    private void drawRects_sigPart(Canvas canvas) {
        int normalSeg = MaxValue - clipNum - sigNum;
        qDebug.qLog("draw sig part  because mvalue is " + mValue);

        mainPaint.setColor(activeLedColor);
        for (int i = 1; i <= normalSeg; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            if (isReverse)
                mrect.top = remainSpace / 2 + (i - 1) * step;
            else
                mrect.top = mHeight - remainSpace / 2 - (i) * step;
            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }

        mainPaint.setColor(sigColor);
        for (int i = normalSeg + 1; i <= mValue; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            if (isReverse)
                mrect.top = remainSpace / 2 + (i - 1) * step;
            else
                mrect.top = mHeight - remainSpace / 2 - (i) * step;
            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }
    }


    private void drawRects_normalPart(Canvas canvas) {
        // qDebug.qLog("draw normal part  because mvalue is " + mValue);
        mainPaint.setColor(activeLedColor);
        for (int i = 1; i <= mValue; i++) {
            RectF mrect = new RectF();
            mrect.left = leftRightGap;
            mrect.right = mWidth - 2 * leftRightGap;
            if (isReverse)
                mrect.top = remainSpace / 2 + (i - 1) * step;
            else
                mrect.top = mHeight - remainSpace / 2 - (i) * step;
            mrect.bottom = mrect.top + smallRectHeight;
            canvas.drawRect(mrect, mainPaint);
        }
    }

}
