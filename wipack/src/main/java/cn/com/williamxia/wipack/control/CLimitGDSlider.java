package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import cn.com.williamxia.wipack.R;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/6/23.
 */

public class CLimitGDSlider extends View {

    public static final int GDSLIDER_Exp = 21;
    public static final int GDSLIDER_Dyn = 22;

    public static final float[] dynExpRatioTable =
            {

                    1.0f,     //0
                    1.2f,     //1
                    1.3f,     //2
                    1.5f,     //3
                    1.7f,     //4
                    2.0f,     //5
                    2.2f,     //6
                    2.3f,     //6
                    2.5f,    //7
                    3.0f,     //9
                    3.5f,     //10
                    4.0f,     //11
                    4.5f,     //12
                    5.0f,     //13
                    5.5f,     //14
                    5.6f,     //15
                    5.9f,     //16
                    6.0f,     //17
                    6.2f,     //18
                    6.5f,     //19
                    7.0f,     //20
                    7.2f,     //21
                    7.4f,     //22
                    7.8f,     //23
                    8.0f,     //19

            };

    public static final float[] dynRatioTable =
            {
                    1.0f,     //0
                    1.2f,     //1
                    1.3f,     //2
                    1.5f,     //3
                    1.7f,     //4
                    2.0f,     //5
                    2.2f,     //6
                    2.3f,     //6
                    2.5f,    //7
                    3.0f,     //9
                    3.5f,     //10
                    4.0f,     //11
                    4.5f,     //12
                    5.0f,     //13
                    5.5f,     //14
                    6.0f,     //15
                    6.5f,     //16
                    7.0f,     //17
                    7.5f,     //18
                    8.0f,     //19
                    8.5f,     //20
                    9.0f,     //21
                    9.5f,     //22
                    10.0f,    //23
                    20.0f,    //24
            };
/* part parameter define above...................*/


    private PointF[] dPoints; //float type

    private int dp_gdRatioMax;
    private int dp_gdRatioPos;
    private int dp_gdThresholdPos;
    private int dp_gdThresholdMax;
    private int dp_gdGainMax;
    private int dp_gdGainPos;

    private int dp_limitType;


    public int getDp_limitType() {
        return dp_limitType;
    }

    public void setDp_limitType(int dp_limitType) {
        this.dp_limitType = dp_limitType;
        invalidate();
    }

    private int lineColor;


    private int defaultWidth;
    private int defaultHeight;

    public int getDp_gdRatioMax() {
        return dp_gdRatioMax;
    }

    public int getDp_gdRatioPos() {
        return dp_gdRatioPos;
    }

    public int getDp_gdThresholdPos() {
        return dp_gdThresholdPos;
    }

    public int getDp_gdThresholdMax() {
        return dp_gdThresholdMax;
    }

    public int getDp_gdGainMax() {
        return dp_gdGainMax;
    }

    public int getDp_gdGainPos() {
        return dp_gdGainPos;
    }

    public void setDp_gdRatioMax(int dp_gdRatioMax) {
        this.dp_gdRatioMax = dp_gdRatioMax;
        invalidate();
    }

    public void setDp_gdRatioPos(int dp_gdRatioPos) {
        this.dp_gdRatioPos = dp_gdRatioPos;
        invalidate();
    }

    public void setDp_gdThresholdPos(int dp_gdThresholdPos) {
        this.dp_gdThresholdPos = dp_gdThresholdPos;
        invalidate();
    }

    public void setDP_Ratio_Threshold(int ratio, int threshold) {
        this.dp_gdRatioPos = ratio;
        this.dp_gdThresholdPos = threshold;
        invalidate();
    }

    public void setDp_gdThresholdMax(int dp_gdThresholdMax) {
        this.dp_gdThresholdMax = dp_gdThresholdMax;
        invalidate();
    }

    public void setDp_gdGainMax(int dp_gdGainMax) {
        this.dp_gdGainMax = dp_gdGainMax;
        invalidate();
    }

    public void setDp_gdGainPos(int dp_gdGainPos) {
        this.dp_gdGainPos = dp_gdGainPos;
        invalidate();
    }


    public void setLineColor(int mcolor) {
        lineColor = mcolor;
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    //
    private int mWidth;
    private int mHeight;
    private Paint mainPaint;

    public CLimitGDSlider(Context context) {
        this(context, null);


    }

    public static final int DefaultGDRatioMax = 24;
    public static final int DefaultThresMax = 50;
    public static final int DefaultGainMax = 24;


    public CLimitGDSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CLimitGDSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
        TypedArray resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CLimitGDSlider, defStyleAttr, 0);

        int n = resAry.getIndexCount();
        qDebug.qLog("begin read attr... typearray count items num is : " + n);

        //
        for (int i = 0; i < n; i++) {
            int index = resAry.getIndex(i);//value

            if (index == R.styleable.CLimitGDSlider_GainMax)
                dp_gdGainMax = resAry.getInt(index, DefaultGainMax);

            else if (index == R.styleable.CLimitGDSlider_RatioMax)
                dp_gdRatioMax = resAry.getInt(index, DefaultGDRatioMax);

            else if (index == R.styleable.CLimitGDSlider_ThresholdMax)
                dp_gdThresholdMax = resAry.getInt(index, DefaultThresMax);


            else if (index == R.styleable.CLimitGDSlider_GLimiteType)
                dp_limitType = resAry.getInteger(index, GDSLIDER_Exp);

            else if (index == R.styleable.CLimitGDSlider_ThresholdPos)
                dp_gdThresholdPos = resAry.getInt(index, 0);


            else if (index == R.styleable.CLimitGDSlider_RatioPos)
                dp_gdRatioPos = resAry.getInt(index, 0);

            else if (index == R.styleable.CLimitGDSlider_GainPos)
                dp_gdGainPos = resAry.getInt(index, 0);

            else if (index == R.styleable.CLimitGDSlider_lineColor)
                lineColor = resAry.getColor(index, Color.YELLOW);


        }

        resAry.recycle();
        qDebug.qLog("gdliser type read is   " + dp_limitType + "  thres max is : " + dp_gdThresholdMax);

        invalidate();

        // qDebug.qLog("read..............attrib over.....defaultWidth " + defaultWidth + "  default height " + defaultHeight + "  ................");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int wd = mWidth;//getWidth()
        int ht = mHeight;//getHeight();
        canvas.drawColor(Color.TRANSPARENT);
        // qDebug.qLog("climit gdslider type is : " + dp_limitType);
        float K = 0;
        float K1 = 0;
        switch (dp_limitType) {
            case GDSLIDER_Exp: {
                qDebug.qLog("gdslider exp  gogog..........................");
                K = (float) wd / ht;
                K1 = (float) 1.0 / dynExpRatioTable[dp_gdRatioPos];
                float vrStep = ht / dp_gdThresholdMax;

                float m = (dp_gdThresholdMax - dp_gdThresholdPos) * vrStep;
                float dt = K1 * (ht - m);
                dPoints[0].x = wd - dt - m / K;
                dPoints[0].y = ht;
                dPoints[1].x = dPoints[0].x + dt;
                dPoints[1].y = m;

                if (dp_gdThresholdPos == 0) {
                    dPoints[0].x = dPoints[1].x = 0;
                    dPoints[0].y = dPoints[1].y = ht;
                } else if (dp_gdThresholdPos == dp_gdThresholdMax) {
                    dPoints[1].x = wd;
                    dPoints[1].y = 0;
                }

                if (dp_gdRatioPos == dp_gdRatioMax) {
                    dPoints[0].x = dPoints[1].x;
                }
                dPoints[2].x = wd;
                dPoints[2].y = 0;

            }
            break;
            case GDSLIDER_Dyn: {

                K = (float) ht / wd;
                K1 = (float) 1 / dynRatioTable[dp_gdRatioPos];
                float vrStep = ht / dp_gdThresholdMax;
                float h = (dp_gdThresholdMax - dp_gdThresholdPos) * vrStep;

                dPoints[0].x = 0f;
                dPoints[0].y = ht;

                if (dp_gdThresholdPos == 0 && dp_gdRatioPos == 0) {
                    dPoints[1].x = 0f;
                    dPoints[1].y = ht;
                    dPoints[2].x = wd;
                    dPoints[2].y = 0f;
                } else if (dp_gdRatioPos == 0 && dp_gdThresholdPos > 0 && dp_gdThresholdPos < dp_gdThresholdMax) {

                    dPoints[1].x = wd - h / K;
                    dPoints[1].y = h;

                    dPoints[2].x = wd;
                    dPoints[2].y = 0f;
                } else if (dp_gdRatioPos == 0 && (dp_gdThresholdPos == dp_gdThresholdMax)) {

                    dPoints[1].x = wd;
                    dPoints[1].y = 0f;
                    dPoints[2].x = wd;
                    dPoints[2].y = 0f;
                } else if (dp_gdRatioPos > 0 && dp_gdRatioPos < dp_gdRatioMax && dp_gdThresholdPos > 0 && dp_gdThresholdPos < dp_gdThresholdMax) {

                    dPoints[1].x = (ht - h) / K;
                    dPoints[1].y = h;
                    dPoints[2].x = wd;
                    dPoints[2].y = h - K1 * (wd - dPoints[1].x);

                } else if (dp_gdRatioPos > 0 && dp_gdRatioPos < dp_gdRatioMax && dp_gdThresholdPos == dp_gdThresholdMax) {

                    dPoints[1].x = wd;
                    dPoints[1].y = 0f;
                    dPoints[2].x = wd;
                    dPoints[2].y = 0f;

                } else if (dp_gdThresholdPos == 0 && dp_gdRatioPos > 0 && dp_gdRatioPos < dp_gdRatioMax)//thres=0 ratio!=0
                {

                    dPoints[1].x = 0f;
                    dPoints[1].y = ht;
                    dPoints[2].x = wd;
                    dPoints[2].y = ht - K1 * wd;

                } else if (dp_gdRatioPos == dp_gdRatioMax && dp_gdThresholdPos == dp_gdThresholdMax) //ratio max,thresMax
                {
                    dPoints[1].x = wd;
                    dPoints[1].y = 0f;
                    dPoints[2].x = wd;
                    dPoints[2].y = 0f;
                } else if (dp_gdRatioPos == dp_gdRatioMax && dp_gdThresholdPos == 0) //ratio max
                {
                    dPoints[1].x = wd;
                    dPoints[1].y = ht;

                    dPoints[2].x = wd;
                    dPoints[2].y = ht;

                } else if (dp_gdRatioPos == dp_gdRatioMax && dp_gdThresholdPos < dp_gdThresholdMax && dp_gdThresholdPos > 0) {

                    dPoints[1].x = (ht - h) / K;
                    dPoints[1].y = h;
                    dPoints[2].x = wd;
                    dPoints[2].y = h - 2;

                }


            }
            break;
            default:
                break;

        }
        //qDebug.qLog("read gdslider type is : " + dp_GSSliderType);
        if (dPoints[0].x < 0) dPoints[0].x = 0;

        mainPaint.setColor(lineColor);
        canvas.drawLine(dPoints[0].x, dPoints[0].y, dPoints[1].x, dPoints[1].y, mainPaint);
        canvas.drawLine(dPoints[1].x, dPoints[1].y, dPoints[2].x, dPoints[2].y, mainPaint);
        StringBuilder builder = new StringBuilder();
        builder.append("mwidh " + mWidth + "  mheight: " + mHeight);
        builder.append("\n");
        builder.append("point 0 x:" + dPoints[0].x + "   y: " + dPoints[0].y);
        builder.append("\n");
        builder.append("point 1 x:" + dPoints[1].x + "    y: " + dPoints[1].y);
        builder.append("\n");
        builder.append("point 2 x:" + dPoints[2].x + "    y: " + dPoints[2].y);
        // qDebug.qLog(builder.toString());
        canvas.restore();

    }


    private void init() {
        if (dPoints == null)
            dPoints = new PointF[3]; //float type
        for (int i = 0; i < 3; i++) {
            dPoints[i] = new PointF();
        }
        if (mainPaint == null)
            mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setColor(lineColor);
        mainPaint.setStrokeWidth(5);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = defaultWidth;
            if (widthMode == MeasureSpec.AT_MOST)
                mWidth = Math.min(mWidth, widthSize);
        }
        //-------
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = defaultHeight;
            if (heightMode == MeasureSpec.AT_MOST)
                mHeight = Math.min(mHeight, heightSize);
        }
        setMeasuredDimension(mWidth, mHeight);
        // qDebug.qLog("on measure width is : " + mWidth + "  mheight :" + mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    public void setGDLimitData(LimitData mitData) {
        dp_gdRatioPos = mitData.limit_ratio;
        dp_gdThresholdPos = mitData.limit_threshold;
        invalidate();
    }

    public void resetControl() {
        dp_gdRatioPos = dp_gdGainPos = dp_gdThresholdPos = 0;
        invalidate();
    }


    public static class LimitData {
        public byte limit_threshold;
        public byte limit_ratio;
        public int limit_attack;
        public int limit_release;
        public byte limit_bypas;
        public byte limit_gain;

        //
        public LimitData() {
            limit_threshold = 0;
            limit_ratio = 0;
            limit_attack = 0;
            limit_release = 0;
            limit_bypas = 0;
            limit_gain = 0;
        }


        public void dataCopy(LimitData srcData) {
            if (srcData != null) {
                limit_threshold = srcData.limit_threshold;
                limit_ratio = srcData.limit_ratio;
                limit_attack = srcData.limit_attack;
                limit_release = srcData.limit_release;
                limit_bypas = srcData.limit_bypas;
                limit_gain = srcData.limit_gain;
            }

        }

    }


}
