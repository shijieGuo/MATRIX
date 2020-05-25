package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import cn.com.williamxia.wipack.R;
import cn.com.williamxia.wipack.utils.qDebug;

/**
 * Created by williamXia on 2017/9/9.
 */

public class PrecentProgressBar extends ProgressBar {

    private String text;
    private Paint mPaint;
    private int txtColor;
    private float txtSize;

    public void setTxtSize(float txtSize) {
        this.txtSize = txtSize;
        mPaint.setTextSize(txtSize);
        invalidate();
    }

    public float getTxtSize() {
        return txtSize;
    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
        mPaint.setColor(txtColor);
        invalidate();
    }

    public int getTxtColor() {
        return txtColor;
    }

    public PrecentProgressBar(Context context) {
        this(context, null);
    }

    public PrecentProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrecentProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initText();
        TypedArray resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PrecentProgressBar, defStyleAttr, 0);
        int n = resAry.getIndexCount();
        for (int i = 0; i < n; i++) {
            int index = resAry.getIndex(i);
            if (index == R.styleable.PrecentProgressBar_preProgresBar_txtColor)
                txtColor = resAry.getColor(index, Color.WHITE);
            else if (index == R.styleable.PrecentProgressBar_preProgresBar_txtSize)
                txtSize = resAry.getFloat(index, 15);
        }
        qDebug.qLog("prebar... read txtSize is "+txtSize);
        invalidate();

    }


    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        setText(progress);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.setTextSize(txtSize);
        mPaint.setColor(txtColor);
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);

        int x = getWidth() / 2 - rect.centerX();
        int y = getHeight() / 2 - rect.centerY();

        canvas.drawText(this.text, x, y, this.mPaint);
    }


    private void initText() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(20);
        txtColor = Color.WHITE;
        mPaint.setColor(txtColor);
    }

    private void setText() {
        setText(this.getProgress());
    }

    private void setText(int progres) {
        int i = (progres * 100) / this.getMax();
        this.text = String.format("%d%%", i);
    }


}
