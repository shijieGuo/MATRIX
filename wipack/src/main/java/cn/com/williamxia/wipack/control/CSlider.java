package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.com.williamxia.wipack.R;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/8/2.
 */

public class CSlider extends View {


    private static final float def_tailGap = 2.0f;
    private static final float def_bottomGap = 3.0f;
    private static final float def_headGap = 2.0f;
    private static final float def_dockTop = 3.0f;
    private static final float def_thumbOffset = 0f;
    private static final int def_sliderMax = 100;
    private static final int def_sliderWH = 30;
    private final int KDebug = 1;

    private final float def_leftlowrate = 0.376f;
    private final float def_thumblongrate = 0.15f;
    private int type;

    float gapSpace = 0;
    //-for exposed
    private int iTag;
    private int iThumb;//thumb image for slider
    private int MaxValue;
    private int MinValue;
    private int value;
    private boolean isLevel;//is horizontal
    //
    private float thumbOffset;
    private float _thumbOffsetRatio;
    private float dockTopGap;
    private float dockBotomGap;
    private float headerGap;
    private float tailGap;


    private float _dockTopGapRatio = 0;
    private float _dockBotomGapRatio = 0;
    private float _headerGapRatio = 0;
    private float _tailGapRatio = 0;

    private float _thumbWidhtRaito = 0;

    private boolean isLighted;

    public boolean isLighted() {
        return isLighted;
    }

    public void setLighted(boolean lighted) {
        isLighted = lighted;
        reDraw();
    }

    //-------tempoaray value
    private int tempValue;
    private int lastValue;
    private int shift;
    private float step;
    private boolean isTouched;
    private int image_background;
    private int image_bg_press;
    private Rect sliderRect;
    private RectF thumbRect;
    private float mWidth, mHeight;
    private float _thumbWidth;
    private float _thumbHeight;
    private float curX, curY;
    private float lastX, lastY;
    private boolean dragable=true;
    private SliderChangeListenner changeListenner;
    private int eindex;
    private int typeValue;
    private int trackColor;//for state:which need draw background
    //endregion
    private float leftSideX = 0.376f;
    private float _thumbHeightRaito = 0.15f;

    public void setLeftSideX(float leftSideX) {
        this.leftSideX = leftSideX;
        initProperty();
    }

    public void set_thumbHeightRaito(float _thumbHeightRaito) {
        this._thumbHeightRaito = _thumbHeightRaito;
        initProperty();
    }

    private int parentResID;
    private boolean isNeedDrawBackground;


    public float getLeftSideX() {
        return leftSideX;
    }

    public float get_thumbHeightRaito() {
        return _thumbHeightRaito;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTrackColor() {
        return trackColor;
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;

        this.startColor = trackColor;
        this.middleColor = trackColor;
        this.endColor = trackColor;
        colorArray[0] = startColor;
        colorArray[1] = middleColor;
        colorArray[2] = endColor;
        colorArray2[0] = startColor;
        colorArray2[1] = middleColor;
        colorArray2[2] = endColor;
        invalidate();

    }

    public boolean isNeedDrawBackground() {
        return isNeedDrawBackground;

    }

    public void setNeedDrawBackground(boolean needDrawBackground) {
        isNeedDrawBackground = needDrawBackground;
        invalidate();
    }

    //region slider construct function
    public CSlider(Context context) {
        this(context, null);
    }

    public boolean isDragable() {
        return dragable;
    }

    public void setDragable(boolean dragable) {
        this.dragable = dragable;
    }

    public CSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    Paint linePt;

    public CSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linePt = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePt.setColor(Color.BLACK);
        linePt.setStrokeWidth(5);
        MinValue = 0;
        TypedArray resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CSlider, defStyleAttr, 0);
        int atrCount = resAry.getIndexCount();
        for (int i = 0; i < atrCount; i++) {
            int index = resAry.getIndex(i);

            if (index == R.styleable.CSlider_dockBottom)
                _dockBotomGapRatio = resAry.getFloat(index, def_bottomGap);

            else if (index == R.styleable.CSlider_dockTop)
                _dockTopGapRatio = resAry.getFloat(index, def_dockTop);

            else if (index == R.styleable.CSlider_headGap)
                _headerGapRatio = resAry.getFloat(index, def_headGap);

            else if (index == R.styleable.CSlider_tailGap)
                _tailGapRatio = resAry.getFloat(index, def_tailGap);

            else if (index == R.styleable.CSlider_thumbImage)
                iThumb = resAry.getResourceId(R.styleable.CSlider_thumbImage, 0);
            else if (index == R.styleable.CSlider_cslider_bkNormalImge)
                this.image_background = resAry.getResourceId(R.styleable.CSlider_cslider_bkNormalImge, 0);
            else if (index == R.styleable.CSlider_cslider_bkPressImge)
                this.image_bg_press = resAry.getResourceId(R.styleable.CSlider_cslider_bkPressImge, 0);
            else if (index == R.styleable.CSlider_value)
                this.value = resAry.getInteger(index, 0);
            else if (index == R.styleable.CSlider_dragable)
                this.dragable = resAry.getBoolean(index, true);
            else if (index == R.styleable.CSlider_isHorizontal)
                isLevel = resAry.getBoolean(index, false);//default is vertical
            else if (index == R.styleable.CSlider_thumbOffset)
                _thumbOffsetRatio = resAry.getFloat(index, def_thumbOffset);

            else if (index == R.styleable.CSlider_MaxValue)
                MaxValue = resAry.getInteger(index, def_sliderMax);

            else if (index == R.styleable.CSlider_MinValue)
                MinValue = resAry.getInteger(index, 0);

            else if (index == R.styleable.CSlider_isNeedDrawBack)
                isNeedDrawBackground = resAry.getBoolean(index, false);
            else if (index == R.styleable.CSlider_trackColor)
                trackColor = resAry.getColor(R.styleable.CSlider_trackColor, Color.BLACK);
            else if (index == R.styleable.CSlider_thumbWidthRatio)
                _thumbWidhtRaito = resAry.getFloat(R.styleable.CSlider_thumbWidthRatio, def_leftlowrate);
            else if (index == R.styleable.CSlider_thumbHeightRaito)
                _thumbHeightRaito = resAry.getFloat(R.styleable.CSlider_thumbHeightRaito, def_thumblongrate);

        }

        resAry.recycle();
        parentResID = 0;
        typeValue = 0;
        eindex = 0;
        setTrackColor(trackColor);
        initProperty();

    }

    //fix java.lang.outofmemmory excetpion  20180619
    public Bitmap gBitMapFromRes(int fid) {

        BitmapFactory.Options options2 = new BitmapFactory.Options();
        // options2.outWidth = getMeasuredWidth();
        options2.inSampleSize = 2;
        options2.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(getResources(),
                fid, options2);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }

    public int getImage_bg_press() {
        return image_bg_press;
    }

    public void setImage_bg_press(int image_bg_press) {
        this.image_bg_press = image_bg_press;
        reDraw();
    }

    public int getImage_background() {
        return image_background;
    }

    public void setImage_background(int image_background) {
        this.image_background = image_background;
        reDraw();
    }

    //region all exposed getter function below
    public int getiThumb() {
        return iThumb;
    }

    public void setiThumb(int iThumb) {
        this.iThumb = iThumb;
        initProperty();
    }

    public int getMaxValue() {
        return MaxValue;
    }

    public void setMaxValue(int maxValue) {
        MaxValue = maxValue;
        initProperty();
    }

    public int getMinValue() {
        return MinValue;
    }

    public void setMinValue(int minValue) {
        MinValue = minValue;
        initProperty();
    }

    public int getValue() {
        return value;
    }

    public void setValue_Max(int fmaxv, int mvalue) {
        this.MaxValue = fmaxv;
        setValue(mvalue);
    }

    public void setValue(int value) {

        int tmp = value;
        if (value > MaxValue) tmp = MaxValue;
        else if (value < MinValue) tmp = MinValue;

        RectF tmpRect = thumbRect;
        if (isLevel) {
            this.value = tmp;
            tmpRect.left = (sliderRect.left + this.value * step);
            tmpRect.right = tmpRect.left + _thumbWidth;

        } else {
            this.value = MaxValue - tmp;
            tmpRect.top = (sliderRect.top + this.value * step);
            tmpRect.bottom = tmpRect.top + _thumbHeight;

        }
        thumbRect = tmpRect;
        reDraw();
    }

    //endregion

    public boolean isLevel() {
        return isLevel;
    }

    public void setLevel(boolean level) {
        isLevel = level;
        invalidate();
    }

    public float getThumbOffset() {
        return thumbOffset;
    }

    public void setThumbOffset(float thumbOffset) {
        this.thumbOffset = thumbOffset;
    }

    public float getDockTopGap() {
        return dockTopGap;
    }

    public void setDockTopGap(float dockTopGap) {
        this.dockTopGap = dockTopGap;
        initProperty();
    }

    public float getDockBotomGap() {
        return dockBotomGap;
    }

    public void setDockBotomGap(float dockBotomGap) {
        this.dockBotomGap = dockBotomGap;
        initProperty();
    }

    public float getHeaderGap() {
        return headerGap;
    }

    public void setHeaderGap(float headerGap) {
        this.headerGap = headerGap;
        initProperty();
    }

    public float getTailGap() {
        return tailGap;
    }

    //region exposed setter function below
    public void setTailGap(float tailGap) {
        this.tailGap = tailGap;
        initProperty();
    }

    public int getiTag() {
        return iTag;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public void setChangeListenner(SliderChangeListenner changeListenner) {
        this.changeListenner = changeListenner;
    }

    public int getEindex() {
        return eindex;
    }

    public void setEindex(int eindex) {
        this.eindex = eindex;
    }

    public int getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }
    //endregion

    public int getParentResID() {
        return parentResID;
    }

    public void setParentResID(int parentResID) {
        this.parentResID = parentResID;
    }

    //region override function
    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        if (KDebug < 1) {
            Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
            pt.setStyle(Paint.Style.FILL);
            pt.setColor(Color.RED);
            canvas.drawRect(sliderRect, pt);
        }
        //  canvas.drawLine(sx, sy, ex, sy, linePt);

        if (iThumb != 0) {
            recalculateRect();
            canvas.drawBitmap(gBitMapFromRes(iThumb), null, thumbRect, null);
        }

    }

    public void releaseBitmapEx() {
//        if (image_background != null) image_background.recycle();
//        image_background = null;
//        if (image_bg_press != null) image_bg_press.recycle();
//        image_bg_press = null;
//        if (iThumb != null) iThumb.recycle();
//        iThumb = null;
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
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
            // middleHeight = mHeight / 2;
        }
        setMeasuredDimension((int) (widthSize + 0.5), (int) (heightSize + 0.5));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initProperty();

    }
    //endregion

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       if (!dragable) {
            return false;
        }
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                shift = 0;
                isTouched = true;
                lastX = curX = event.getX();//for horizontal
                lastY = curY = event.getY();//for vertical
                isTouched = true;
                lastValue = value;
                qDebug.qLog("onTouch Down last y : " + lastY + "  lastvalue is : " + value);
                //increase listenner
                if (changeListenner != null) {
                    changeListenner.onSliderTouchDown(this);
                }
                reDraw();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouched) {
                    curX = event.getX();
                    curY = event.getY();
                    float delta;
                    if (isLevel)
                        delta = (curX - lastX);
                    else
                        delta = (curY - lastY);
                    if (step <= 0) step = 0.1f;
                    shift = (int) (delta / step);
                    calValue();
                    //qDebug.qLog("onTouch move cury: " + curY + "  delta " + delta + "  shift " + shift + " step: " + step + "  tmpvalue " + tempValue);

                    //expose the tempvalue
                    if (changeListenner != null) {
                        changeListenner.onSliderChange(this, tempValue);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                shift = 0;
                reDraw();
                //increase listenner

                break;


        }
        return true;
    }

    public void reDraw() {
        this.invalidate(sliderRect);
    }

    public void calValue() {

        if (isLevel)
            tempValue = lastValue + shift;
        else
            tempValue = (MaxValue - (lastValue + shift));

        if (tempValue > MaxValue) tempValue = MaxValue;
        else if (tempValue < MinValue) tempValue = MinValue;
    }

    public float f2d(float x) {
        return (float) ((int) (x * 100) * 0.01);
    }

    private void initProperty() {

        recalculateRect();
        invalidate();
    }


    private void recalculateRect() {

        float SliderPan = 0f;
        if (sliderRect == null)
            sliderRect = new Rect();
        if (thumbRect == null)
            thumbRect = new RectF();


        _thumbWidth = (int) (mWidth * _thumbWidhtRaito);
        _thumbHeight = (int) (mHeight * _thumbHeightRaito);

        dockTopGap = mHeight * _dockTopGapRatio;
        dockBotomGap = mHeight * _dockBotomGapRatio;
        headerGap = mHeight * _headerGapRatio;
        tailGap = _tailGapRatio * mHeight;


        if (isLevel) //hor
        {
            thumbOffset = _thumbOffsetRatio * mHeight;
            leftSideX = ((1 - _thumbWidhtRaito) / 2) * mHeight + thumbOffset; //作为top start值
            SliderPan = mWidth - dockTopGap - dockBotomGap - headerGap - tailGap - _thumbWidth;

        } else //vticual
        {

            thumbOffset = _thumbOffsetRatio * mWidth;
            leftSideX = ((1 - _thumbWidhtRaito) / 2) * mWidth + thumbOffset;    //作为left start值
            SliderPan = mHeight - dockTopGap - dockBotomGap - headerGap - tailGap - _thumbHeight;
        }

        step = (float) SliderPan / MaxValue;
        step = f2d(step);
        gapSpace = SliderPan - step * MaxValue;

        RectF tmpRect = new RectF();
        if (!isLevel) {
            tmpRect.set((int) (leftSideX), dockTopGap + headerGap + gapSpace / 2, (int) (leftSideX + _thumbWidth),
                    mHeight - dockBotomGap - tailGap + gapSpace / 2);
        } else {
            tmpRect.set(dockTopGap + headerGap + gapSpace / 2, leftSideX, mWidth - dockBotomGap - tailGap + gapSpace / 2,
                    (int) (mHeight - _thumbHeight / 2));
        }
        //caculate sliderRect
        // sliderRect = tmpRect;  // can assin for draw

        sliderRect.top = (int) (tmpRect.top - 0.5);
        sliderRect.right = (int) tmpRect.right;
        sliderRect.left = (int) tmpRect.left;
        sliderRect.bottom = (int) (tmpRect.bottom + 0.5);

        if (!isLevel) {

            thumbRect.left = leftSideX;
            thumbRect.top = tmpRect.top + this.value * step;
            thumbRect.right = (int) (leftSideX + _thumbWidth);
            thumbRect.bottom = thumbRect.top + _thumbHeight;

        } else {

            thumbRect.left = tmpRect.left + this.value * step;
            thumbRect.top = tmpRect.top;
            thumbRect.right = thumbRect.left + _thumbWidth;
            thumbRect.bottom = thumbRect.top + _thumbHeight;

        }
    }


    private void drawBackground(Canvas canvas) {
        Rect rectBg = new Rect(0, 0, (int) (mWidth + 0.5), (int) (mHeight + 0.5));
        if (!isNeedDrawBackground) {
            if (isLighted) {
                if (image_bg_press != 0)
                    canvas.drawBitmap(gBitMapFromRes(image_bg_press), null, rectBg, null);
            } else {
                if (image_background != 0)
                    canvas.drawBitmap(gBitMapFromRes(image_background), null, rectBg, null);
            }

        } else {
            drawBackgroundGreen(canvas);

        }


    }

    private LinearGradient linearGradient;
    private Paint paint = new Paint();
    private int startColor = Color.BLACK;
    private int middleColor = Color.BLACK;
    private int endColor = Color.BLACK;
    private int colorArray[] = {startColor, middleColor, endColor};
    private int colorArray2[] = {Color.BLACK, Color.BLACK, Color.BLACK};

    private void drawBackgroundGreen(Canvas canvas) {

        int sWidth = sliderRect.width();
        int sHeight = sliderRect.height();
        float sLeft, sTop, sRight, sBottom, y = 0;
        RectF rectBlackBg = null;
        if (!isLevel) {

            sLeft = thumbRect.left + thumbRect.width() * 0.3f;
            sTop = sliderRect.top;
            sRight = thumbRect.left + thumbRect.width() * 0.6f;
            sBottom = sliderRect.bottom;

        } else {
            sLeft = sliderRect.left;
            sTop = sliderRect.top + thumbRect.width() * 0.3f;
            sRight = sliderRect.right;
            sBottom = sTop + thumbRect.height() * 0.3f;

        }
        rectBlackBg = new RectF(sLeft, sTop, sRight, sBottom);
        linearGradient = new LinearGradient(sLeft, sTop, sRight, sBottom, colorArray2, null, Shader.TileMode.MIRROR);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(linearGradient);
        canvas.drawRoundRect(rectBlackBg, sWidth / 2, sWidth / 2, paint);
        /*
         *  使用LinearGradient线性来填充矩形图
         *  drawRoundRect:第2，第3个参数的圆角弧度
         *
         * */

    }


    public static interface SliderChangeListenner {

        public void onSliderTouchDown(Object sender);

        public void onSliderChange(Object sender, int pvalue);


    }


}
