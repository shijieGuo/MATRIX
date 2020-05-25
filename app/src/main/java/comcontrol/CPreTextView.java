package comcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.WindowManager;

import cn.com.williamxia.matrixa8dante.R;


public class CPreTextView extends AppCompatTextView {
    private static final String TAG = CPreTextView.class.getSimpleName();
    private static int baseScreenHeight = 1600;
    private float mTextSizePercent = 1f;
    private int iTag=0;

    public int getiTag() {
        return iTag;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public CPreTextView(Context context) {
        super(context);
        setDefaultPercent(context);
        setTextSize(getTextSize());
    }

    public CPreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        setDefaultPercent(context);
       // LogUtil.i(TAG, "PercentTextView() getTextSize() == "+getTextSize());
        setTextSize(getTextSize());
    }

    public CPreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        setDefaultPercent(context);
        setTextSize(getTextSize());
    }

    @Override
    public void setTextSize(int unit, float size) {
        size = (int)(size * mTextSizePercent);
       // LogUtil.i(TAG, "setTextSize() == "+size);
        super.setTextSize(unit, size);
    }

    @Override
    public void setPaintFlags(int flags) {
        super.setPaintFlags(flags);
    }

    @Override
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }

    public float getTextSizePercent() {
        return mTextSizePercent;
    }

    public void setTextSizePercent(int unit, float textSizePercent) {
        mTextSizePercent = textSizePercent;
        setTextSize(unit, getTextSize());
    }

    public void setTextSizePercent(float textSizePercent) {
        mTextSizePercent = textSizePercent;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
    }

    /**
     * 得到自定义的属性值
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CPreTextView);
        baseScreenHeight = ta.getInt(R.styleable.CPreTextView_baseScreenHeight, baseScreenHeight);
        ta.recycle();
    }



    /**
     * 获取当前设备屏幕的宽高
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 设置默认的百分比
     *
     * @param context
     */
    private void setDefaultPercent(Context context) {
        float screenHeight = getScreenHeight(context);
        mTextSizePercent = screenHeight / baseScreenHeight;
    }
}