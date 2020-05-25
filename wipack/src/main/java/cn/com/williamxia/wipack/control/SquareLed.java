package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.com.williamxia.wipack.R;


/**
 * Created by williamXia on 2017/6/16.
 */

public class SquareLed extends View {

    private float mWidth, mHeight;
    private Paint mainPaint;
    private int mActiveColor, mUnActiveColor;
    private boolean misActive = false;

    public SquareLed(Context context) {
        this(context, null);
    }

    public SquareLed(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SquareLed(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SquareLed, defStyleAttr, 0);
        int n = array.getIndexCount();
        //
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);

                if(attr== R.styleable.SquareLed_colorActive)
                    mActiveColor = array.getColor(attr, getResources().getColor(R.color.lime));

               else if(attr== R.styleable.SquareLed_colorUnActive)
                    mUnActiveColor = array.getColor(attr, getResources().getColor(R.color.btn_unpress_color));

               else if(attr== R.styleable.SquareLed_isActive)
                    misActive = array.getBoolean(attr, false);




        }
        array.recycle();
    }

    private void init() {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.FILL);
        misActive = false;
        mActiveColor = R.color.lime;
        mUnActiveColor = R.color.btn_unpress_color;

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
        setMeasuredDimension(widthSize, heightSize);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        invalidate();

    }

    public void setMisActive(boolean isActive) {
        misActive = isActive;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        int currentColor = (misActive ? mActiveColor : mUnActiveColor);
        //   mainPaint.setColor(currentColor);
        canvas.drawColor(currentColor);
        //Log.v("squ", "squareLed onDraw now............" + Boolean.toString(misActive) + " color " + currentColor);
    }
}
