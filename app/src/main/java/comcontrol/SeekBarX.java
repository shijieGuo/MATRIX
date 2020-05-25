package comcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.com.williamxia.matrixa8dante.R;


/**
 * Created by zhihuafeng on 11/11/2017.
 */

public class SeekBarX extends View {
    private float x, y;
    private float mRadius;
    private float progress;
    private float sLeft, sTop, sRight, sBottom;
    private float sWidth, sHeight; //实际的量程高度
    private float bgWidth, bgHeight;
    private float thumbHeight;
    private int maginTop = 10, maginBottom = 10;
    private Paint paint = new Paint();

    protected OnSlideChangeListener onSlideChangeListener;

    private boolean dragable = true;
    private boolean isAct;
    private Bitmap thumb_background;
    private Bitmap image_background;
    private Bitmap image_bg_press;

    private float ScaleCount = 100;//总刻度数
    private float ScaleGap;//百分比步进，越精准，拖动越流畅
    private int MaxValue, MinValue = 0;
    private int iTag;
    private int CIndex = 0;

    private String channelName = "";

    public SeekBarX(Context context) {
        this(context, null);
    }

    public SeekBarX(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SeekBarX, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SeekBarX_XImage_background:
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;*/
                    image_background = BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr, 0));
                    break;
                case R.styleable.SeekBarX_XThumb_background:
                    BitmapFactory.Options options1 = new BitmapFactory.Options();
                    options1.inSampleSize = 2;
                    options1.inPreferredConfig = Bitmap.Config.RGB_565;
                    thumb_background = BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr, 0), options1);
                    break;
                case R.styleable.SeekBarX_XDragable:
                    dragable = a.getBoolean(attr, true);
                    break;
                case R.styleable.SeekBarX_XTag:
                    iTag = a.getInteger(attr, 0);
                    break;
                case R.styleable.SeekBarX_XMax_value:
                    MaxValue = a.getInteger(attr, 100);
                    break;
                case R.styleable.SeekBarX_XImg_bg_press:
                    image_bg_press = BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr, 0));
                    break;
            }
        }
        a.recycle();
        ScaleCount = MaxValue - MinValue;
    }

    /**
     * 图片资源释放
     */
    public void releaseBitmapEx() {
        if (image_background != null) image_background.recycle();
        image_background = null;
        if (image_bg_press != null) image_bg_press.recycle();
        image_bg_press = null;
        if (thumb_background != null) thumb_background.recycle();
        thumb_background = null;
    }

    /**
     * @param dragable 设置是否可以拖动
     */
    public void setDragable(boolean dragable) {
        this.dragable = dragable;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.MaxValue = maxValue;
        invalidate();
    }

    //true高亮
    public void setAct(boolean act) {
        this.setActivated(act);
        this.invalidate();
    }

    public int getiTag() {
        return iTag;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
        invalidate();
    }

    public String getCHName() {
        return channelName;
    }

    public void setCHName(String chName) {
        this.channelName = chName;
        invalidate();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            bgWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            bgHeight = heightSize;
        }
        thumbHeight = (float) (bgHeight * 0.20);
        sWidth = (float) (bgWidth * 0.3);
        sLeft = bgWidth / 3 - 2;
        sRight = sLeft + sWidth;
        mRadius = thumbHeight / 2;

        sTop = mRadius - 10;
        sBottom = bgHeight - maginBottom - 10;
        sHeight = sBottom - sTop;


        setMeasuredDimension((int) (bgWidth + 0.5),
                (int) (bgHeight + 0.5));


    }

    private void caculateMeasure() {
        ScaleCount = MaxValue - MinValue;
        thumbHeight = (float) (bgHeight * 0.20);
        sWidth = (float) (bgWidth * 0.3);
        sLeft = bgWidth / 3 - 2;
        sRight = sLeft + sWidth;
        mRadius = thumbHeight / 2;
        sTop = mRadius - 10;
        sBottom = bgHeight - maginBottom - 10;
        sHeight = sBottom - sTop;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        caculateMeasure();
        DecimalFormat df = new DecimalFormat("0.000");
        ScaleGap = Float.parseFloat(df.format(1 / ScaleCount));
        x = (float) bgWidth / 2;
        // y = (float) (1 - 0.01 * progress) * sHeight;
        y = (float) (1 - ScaleGap * progress) * sHeight;
        // Log.i("progress----->",""+progress);
        drawBackground(canvas);
        //drawText(canvas);
        drawThumb(canvas);
    }

    private void drawText(Canvas canvas) {
        Rect textRect = new Rect(0, 0, (int) bgWidth, maginTop);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(channelName, textRect.centerX(), baseLineY + 10, textPaint);
    }

    private void drawThumb(Canvas canvas) {

        Paint mpaint = new Paint();
        mpaint.setColor(Color.RED);
        caculateMeasure();
        canvas.drawRect(sLeft, sTop, sRight, sBottom, mpaint);


        /*y = y < mRadius ? mRadius : y;
        y = y > sHeight - mRadius ? sHeight - mRadius : y;*/
        y = y < sTop ? sTop : y;
        y = y > sBottom - mRadius ? sBottom - mRadius : y;
        //Log.i("Y----->",""+y);
        //int outH = (int) (thumb_background.getHeight() * getMeasuredWidth() / thumb_background.getWidth());
        Rect rect = new Rect((int) sLeft - 2, (int) (y+mRadius), (int) sRight, (int) (y - mRadius));
        canvas.drawBitmap(thumb_background, null, rect, null);
    }

    private void drawBackground(Canvas canvas) {
        Rect rectBg = new Rect(0, 0, (int) (bgWidth + 0.5), (int) (bgHeight + 0.5));
        if (this.isActivated())
            canvas.drawBitmap(image_bg_press, null, rectBg, null);
        else
            canvas.drawBitmap(image_background, null, rectBg, null);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!dragable) {
            return false;
        }
        float touchY = event.getY();
        float touchX = event.getX();
        if (isDragThumb(touchY, touchX)) {
            this.getParent().requestDisallowInterceptTouchEvent(true);
            y = touchY;
            this.setActivated(true);
            progress = (sHeight - y) / sHeight * ScaleCount;
            if (progress < 0) {
                progress = 0;
            }
        } else {
            this.getParent().requestDisallowInterceptTouchEvent(false);
        }
        //Log.i("progress------->",""+progress);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setActivated(true);
                onSlideProgress(MotionEvent.ACTION_DOWN, progress);
                break;
            case MotionEvent.ACTION_UP:
                this.setActivated(false);
                onSlideProgress(MotionEvent.ACTION_UP, progress);
                break;
            case MotionEvent.ACTION_MOVE:
                onSlideProgress(MotionEvent.ACTION_MOVE, progress);
                break;
        }
        return true;
    }

    private boolean isDragThumb(float y, float x) {
        float thumbTop = this.y - thumbHeight / 2;
        float thumbBottom = thumbTop + thumbHeight;
        float thumbLeft = sLeft;
        float thumbRight = sLeft + sWidth;
        if (x >= thumbLeft && x <= thumbRight) {
            if (y >= thumbTop - 100 && y <= (thumbBottom + 100)) {
                return true;
            } else
                return false;
        }
        return false;

    }

    public void onSlideProgress(int event, float progress) {
        if (progress < MinValue) {
            progress = MinValue;
        }
        if (progress > MaxValue) {
            progress = MaxValue;
        }
        switch (event) {
            case MotionEvent.ACTION_UP:
                if (onSlideChangeListener != null) {
                    onSlideChangeListener.onSlideStopTouch(this, progress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onSlideChangeListener != null) {
                    onSlideChangeListener.OnSlideChangeListener(this, progress);
                }
                setProgress(progress);
                this.invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                this.invalidate();
                break;
        }

    }


    public interface OnSlideChangeListener {
        void OnSlideChangeListener(View view, float progress);

        void onSlideStopTouch(View view, float progress);
    }

    public void setOnSlideChangeListener(SeekBarX.OnSlideChangeListener onStateChangeListener) {
        this.onSlideChangeListener = onStateChangeListener;
    }


}
